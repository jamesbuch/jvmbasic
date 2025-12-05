# Bytecode Generation with ASM

## Overview

We use the ASM library (https://asm.ow2.io/) for bytecode generation. ASM provides:
- Complete JVM bytecode API
- Built-in verification
- Stack map frame computation
- Optimization utilities

## ASM Setup

```xml
<!-- Maven dependency -->
<dependency>
    <groupId>org.ow2.asm</groupId>
    <artifactId>asm</artifactId>
    <version>9.6</version>
</dependency>
<dependency>
    <groupId>org.ow2.asm</groupId>
    <artifactId>asm-util</artifactId>
    <version>9.6</version>
</dependency>
```

## Code Generator

```java
package com.jvmbasic.codegen;

import org.objectweb.asm.*;
import static org.objectweb.asm.Opcodes.*;

public class BytecodeGenerator {
    private final ClassWriter cw;
    private MethodVisitor mv;
    private final String className;

    public BytecodeGenerator(String className) {
        this.className = className;
        this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }

    public byte[] generate(ClassDecl classDecl) {
        // Class header
        cw.visit(
            V17,                                    // Java version
            ACC_PUBLIC | ACC_SUPER,                 // Access flags
            className,                              // Internal name
            null,                                   // Signature (for generics)
            getSuperClass(classDecl),               // Super class
            getInterfaces(classDecl)                // Interfaces
        );

        // Source file for debugging
        cw.visitSource(classDecl.location().file(), null);

        // Generate members
        for (ClassMember member : classDecl.members()) {
            switch (member) {
                case FieldDecl field -> generateField(field);
                case ConstructorDecl ctor -> generateConstructor(ctor);
                case MethodDecl method -> generateMethod(method);
                case PropertyDecl prop -> generateProperty(prop);
            }
        }

        // Default constructor if none defined
        if (!hasConstructor(classDecl)) {
            generateDefaultConstructor();
        }

        cw.visitEnd();
        return cw.toByteArray();
    }

    private void generateField(FieldDecl field) {
        int access = mapAccessModifier(field.access());
        if (field.isShared()) access |= ACC_STATIC;

        FieldVisitor fv = cw.visitField(
            access,
            field.name(),
            getDescriptor(field.type()),
            getSignature(field.type()),  // For generics
            null                          // Initial value (for constants)
        );
        fv.visitEnd();
    }

    private void generateConstructor(ConstructorDecl ctor) {
        String descriptor = buildMethodDescriptor(ctor.parameters(), PrimitiveType.VOID);
        mv = cw.visitMethod(
            mapAccessModifier(ctor.access()),
            "<init>",
            descriptor,
            null,
            null
        );
        mv.visitCode();

        // Call super constructor
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        // Generate body
        LocalVariableTable locals = new LocalVariableTable();
        locals.allocate("Me", className);  // Slot 0 = this
        for (Parameter param : ctor.parameters()) {
            locals.allocate(param.name(), param.type());
        }

        for (Statement stmt : ctor.body()) {
            generateStatement(stmt, locals);
        }

        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);  // Computed by ASM
        mv.visitEnd();
    }

    private void generateMethod(MethodDecl method) {
        int access = mapAccessModifier(method.access());
        if (method.isShared()) access |= ACC_STATIC;

        String descriptor = buildMethodDescriptor(method.parameters(), method.returnType().orElse(null));

        mv = cw.visitMethod(
            access,
            method.name(),
            descriptor,
            getGenericSignature(method),
            null
        );
        mv.visitCode();

        LocalVariableTable locals = new LocalVariableTable();
        if (!method.isShared()) {
            locals.allocate("Me", className);  // Slot 0 = this
        }
        for (Parameter param : method.parameters()) {
            locals.allocate(param.name(), param.type());
        }

        for (Statement stmt : method.body()) {
            generateStatement(stmt, locals);
        }

        // Ensure method ends with return
        if (method.returnType().isEmpty() || method.returnType().get() == PrimitiveType.VOID) {
            mv.visitInsn(RETURN);
        }

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    // ========================================================================
    // Statement Generation
    // ========================================================================

    private void generateStatement(Statement stmt, LocalVariableTable locals) {
        switch (stmt) {
            case DimStmt dim -> {
                int slot = locals.allocate(dim.name(), dim.type());
                if (dim.initializer().isPresent()) {
                    generateExpression(dim.initializer().get(), locals);
                    storeLocal(slot, dim.type());
                }
            }

            case AssignStmt assign -> {
                if (assign.target() instanceof IdentifierExpr id) {
                    generateExpression(assign.value(), locals);
                    int slot = locals.getSlot(id.name());
                    storeLocal(slot, assign.value().type());
                } else if (assign.target() instanceof MemberAccessExpr mem) {
                    generateExpression(mem.object(), locals);
                    generateExpression(assign.value(), locals);
                    mv.visitFieldInsn(
                        PUTFIELD,
                        getInternalName(mem.object().type()),
                        mem.memberName(),
                        getDescriptor(mem.type())
                    );
                } else if (assign.target() instanceof IndexExpr idx) {
                    generateExpression(idx.array(), locals);
                    generateExpression(idx.index(), locals);
                    generateExpression(assign.value(), locals);
                    arrayStore(getElementType(idx.array().type()));
                }
            }

            case IfStmt ifStmt -> {
                Label elseLabel = new Label();
                Label endLabel = new Label();

                generateExpression(ifStmt.condition(), locals);
                mv.visitJumpInsn(IFEQ, elseLabel);

                // Then branch
                for (Statement s : ifStmt.thenBranch()) {
                    generateStatement(s, locals);
                }
                mv.visitJumpInsn(GOTO, endLabel);

                // ElseIf branches
                for (ElseIfClause clause : ifStmt.elseIfClauses()) {
                    Label nextElse = new Label();
                    mv.visitLabel(elseLabel);
                    elseLabel = nextElse;

                    generateExpression(clause.condition(), locals);
                    mv.visitJumpInsn(IFEQ, nextElse);

                    for (Statement s : clause.body()) {
                        generateStatement(s, locals);
                    }
                    mv.visitJumpInsn(GOTO, endLabel);
                }

                // Else branch
                mv.visitLabel(elseLabel);
                if (ifStmt.elseBranch().isPresent()) {
                    for (Statement s : ifStmt.elseBranch().get()) {
                        generateStatement(s, locals);
                    }
                }

                mv.visitLabel(endLabel);
            }

            case WhileStmt whileStmt -> {
                Label condLabel = new Label();
                Label endLabel = new Label();

                mv.visitLabel(condLabel);
                generateExpression(whileStmt.condition(), locals);
                mv.visitJumpInsn(IFEQ, endLabel);

                for (Statement s : whileStmt.body()) {
                    generateStatement(s, locals);
                }
                mv.visitJumpInsn(GOTO, condLabel);

                mv.visitLabel(endLabel);
            }

            case ForStmt forStmt -> {
                Label condLabel = new Label();
                Label endLabel = new Label();
                Label incLabel = new Label();

                // Initialize
                int slot = locals.allocate(forStmt.variable(), PrimitiveType.INTEGER);
                generateExpression(forStmt.start(), locals);
                mv.visitVarInsn(ISTORE, slot);

                // Condition
                mv.visitLabel(condLabel);
                mv.visitVarInsn(ILOAD, slot);
                generateExpression(forStmt.end(), locals);
                mv.visitJumpInsn(IF_ICMPGT, endLabel);

                // Body
                for (Statement s : forStmt.body()) {
                    generateStatement(s, locals);
                }

                // Increment
                mv.visitLabel(incLabel);
                mv.visitVarInsn(ILOAD, slot);
                if (forStmt.step().isPresent()) {
                    generateExpression(forStmt.step().get(), locals);
                } else {
                    mv.visitInsn(ICONST_1);
                }
                mv.visitInsn(IADD);
                mv.visitVarInsn(ISTORE, slot);
                mv.visitJumpInsn(GOTO, condLabel);

                mv.visitLabel(endLabel);
            }

            case ReturnStmt ret -> {
                if (ret.value().isPresent()) {
                    generateExpression(ret.value().get(), locals);
                    returnValue(ret.value().get().type());
                } else {
                    mv.visitInsn(RETURN);
                }
            }

            case ExpressionStmt expr -> {
                generateExpression(expr.expression(), locals);
                // Pop result if not void
                Type exprType = expr.expression().type();
                if (exprType != PrimitiveType.VOID) {
                    if (exprType == PrimitiveType.LONG || exprType == PrimitiveType.DOUBLE) {
                        mv.visitInsn(POP2);
                    } else {
                        mv.visitInsn(POP);
                    }
                }
            }

            case TryStmt tryStmt -> {
                Label tryStart = new Label();
                Label tryEnd = new Label();
                Label afterCatch = new Label();

                mv.visitLabel(tryStart);
                for (Statement s : tryStmt.tryBody()) {
                    generateStatement(s, locals);
                }
                mv.visitLabel(tryEnd);
                mv.visitJumpInsn(GOTO, afterCatch);

                // Catch clauses
                for (CatchClause clause : tryStmt.catchClauses()) {
                    Label catchLabel = new Label();
                    mv.visitLabel(catchLabel);

                    int exSlot = locals.allocate(clause.variable(), clause.exceptionType());
                    mv.visitVarInsn(ASTORE, exSlot);

                    for (Statement s : clause.body()) {
                        generateStatement(s, locals);
                    }
                    mv.visitJumpInsn(GOTO, afterCatch);

                    mv.visitTryCatchBlock(
                        tryStart,
                        tryEnd,
                        catchLabel,
                        getInternalName(clause.exceptionType())
                    );
                }

                mv.visitLabel(afterCatch);
            }
        }
    }

    // ========================================================================
    // Expression Generation
    // ========================================================================

    private void generateExpression(Expression expr, LocalVariableTable locals) {
        switch (expr) {
            case LiteralExpr lit -> {
                switch (lit.value()) {
                    case Integer i -> {
                        if (i >= -1 && i <= 5) mv.visitInsn(ICONST_0 + i);
                        else if (i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE) mv.visitIntInsn(BIPUSH, i);
                        else if (i >= Short.MIN_VALUE && i <= Short.MAX_VALUE) mv.visitIntInsn(SIPUSH, i);
                        else mv.visitLdcInsn(i);
                    }
                    case Long l -> mv.visitLdcInsn(l);
                    case Float f -> mv.visitLdcInsn(f);
                    case Double d -> mv.visitLdcInsn(d);
                    case String s -> mv.visitLdcInsn(s);
                    case Boolean b -> mv.visitInsn(b ? ICONST_1 : ICONST_0);
                    case null -> mv.visitInsn(ACONST_NULL);
                }
            }

            case IdentifierExpr id -> {
                int slot = locals.getSlot(id.name());
                loadLocal(slot, id.type());
            }

            case MeExpr me -> mv.visitVarInsn(ALOAD, 0);

            case BinaryExpr bin -> {
                generateExpression(bin.left(), locals);
                generateExpression(bin.right(), locals);
                generateBinaryOp(bin.operator(), bin.left().type());
            }

            case UnaryExpr un -> {
                generateExpression(un.operand(), locals);
                generateUnaryOp(un.operator(), un.operand().type());
            }

            case CallExpr call -> {
                // Push arguments
                for (Argument arg : call.arguments()) {
                    generateExpression(arg.value(), locals);
                }
                // Invoke
                if (call.callee() instanceof IdentifierExpr id) {
                    mv.visitMethodInsn(
                        INVOKESTATIC,
                        className,
                        id.name(),
                        buildMethodDescriptor(call),
                        false
                    );
                }
            }

            case MethodCallExpr mc -> {
                generateExpression(mc.object(), locals);
                for (Argument arg : mc.arguments()) {
                    generateExpression(arg.value(), locals);
                }
                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    getInternalName(mc.object().type()),
                    mc.methodName(),
                    buildMethodDescriptor(mc),
                    false
                );
            }

            case NewExpr newExpr -> {
                String internalName = getInternalName(newExpr.type());
                mv.visitTypeInsn(NEW, internalName);
                mv.visitInsn(DUP);
                for (Argument arg : newExpr.arguments()) {
                    generateExpression(arg.value(), locals);
                }
                mv.visitMethodInsn(
                    INVOKESPECIAL,
                    internalName,
                    "<init>",
                    buildConstructorDescriptor(newExpr),
                    false
                );
            }

            case MemberAccessExpr mem -> {
                generateExpression(mem.object(), locals);
                mv.visitFieldInsn(
                    GETFIELD,
                    getInternalName(mem.object().type()),
                    mem.memberName(),
                    getDescriptor(mem.type())
                );
            }

            case IndexExpr idx -> {
                generateExpression(idx.array(), locals);
                generateExpression(idx.index(), locals);
                arrayLoad(getElementType(idx.array().type()));
            }
        }
    }

    private void generateBinaryOp(BinaryOp op, Type type) {
        switch (op) {
            case ADD -> {
                if (type == PrimitiveType.INTEGER) mv.visitInsn(IADD);
                else if (type == PrimitiveType.LONG) mv.visitInsn(LADD);
                else if (type == PrimitiveType.FLOAT) mv.visitInsn(FADD);
                else if (type == PrimitiveType.DOUBLE) mv.visitInsn(DADD);
            }
            case SUBTRACT -> {
                if (type == PrimitiveType.INTEGER) mv.visitInsn(ISUB);
                else if (type == PrimitiveType.LONG) mv.visitInsn(LSUB);
                else if (type == PrimitiveType.FLOAT) mv.visitInsn(FSUB);
                else if (type == PrimitiveType.DOUBLE) mv.visitInsn(DSUB);
            }
            // ... more operators

            case EQUAL -> {
                Label trueLabel = new Label();
                Label endLabel = new Label();
                if (isReferenceType(type)) {
                    mv.visitJumpInsn(IF_ACMPEQ, trueLabel);
                } else {
                    mv.visitJumpInsn(IF_ICMPEQ, trueLabel);
                }
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, endLabel);
                mv.visitLabel(trueLabel);
                mv.visitInsn(ICONST_1);
                mv.visitLabel(endLabel);
            }

            case CONCAT -> {
                // String concatenation via StringBuilder
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                mv.visitInsn(SWAP);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitInsn(SWAP);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                    "()Ljava/lang/String;", false);
            }
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private void loadLocal(int slot, Type type) {
        if (type == PrimitiveType.INTEGER || type == PrimitiveType.BOOLEAN ||
            type == PrimitiveType.BYTE || type == PrimitiveType.CHAR) {
            mv.visitVarInsn(ILOAD, slot);
        } else if (type == PrimitiveType.LONG) {
            mv.visitVarInsn(LLOAD, slot);
        } else if (type == PrimitiveType.FLOAT) {
            mv.visitVarInsn(FLOAD, slot);
        } else if (type == PrimitiveType.DOUBLE) {
            mv.visitVarInsn(DLOAD, slot);
        } else {
            mv.visitVarInsn(ALOAD, slot);
        }
    }

    private void storeLocal(int slot, Type type) {
        if (type == PrimitiveType.INTEGER || type == PrimitiveType.BOOLEAN ||
            type == PrimitiveType.BYTE || type == PrimitiveType.CHAR) {
            mv.visitVarInsn(ISTORE, slot);
        } else if (type == PrimitiveType.LONG) {
            mv.visitVarInsn(LSTORE, slot);
        } else if (type == PrimitiveType.FLOAT) {
            mv.visitVarInsn(FSTORE, slot);
        } else if (type == PrimitiveType.DOUBLE) {
            mv.visitVarInsn(DSTORE, slot);
        } else {
            mv.visitVarInsn(ASTORE, slot);
        }
    }

    private String getDescriptor(Type type) {
        return switch (type) {
            case PrimitiveType p -> switch (p) {
                case INTEGER -> "I";
                case LONG -> "J";
                case FLOAT -> "F";
                case DOUBLE -> "D";
                case BOOLEAN -> "Z";
                case BYTE -> "B";
                case CHAR -> "C";
                case VOID -> "V";
                case STRING -> "Ljava/lang/String;";
            };
            case NamedType n -> "L" + n.name().replace('.', '/') + ";";
            case ArrayType a -> "[" + getDescriptor(a.elementType());
            default -> "Ljava/lang/Object;";
        };
    }
}
```

## Local Variable Table

```java
public class LocalVariableTable {
    private final Map<String, Integer> slots = new HashMap<>();
    private int nextSlot = 0;

    public int allocate(String name, Type type) {
        int slot = nextSlot;
        slots.put(name, slot);
        // Long and Double take 2 slots
        if (type == PrimitiveType.LONG || type == PrimitiveType.DOUBLE) {
            nextSlot += 2;
        } else {
            nextSlot += 1;
        }
        return slot;
    }

    public int getSlot(String name) {
        return slots.get(name);
    }

    public int getMaxLocals() {
        return nextSlot;
    }
}
```
