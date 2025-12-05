package com.jvmbasic.visitor;

import com.jvmbasic.grammar.*;
import com.jvmbasic.visitor.SymbolCollector.*;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Compiler Visitor - Second pass for bytecode generation
 *
 * This visitor traverses the parse tree and generates JVM bytecode using ASM.
 * It uses the symbol table collected in the first pass (SymbolCollector).
 *
 * Two-pass compilation:
 *   Pass 1 (Listener): SymbolCollector gathers declarations, types, scopes
 *   Pass 2 (Visitor):  CompilerVisitor generates bytecode with full type info
 *
 * Usage:
 *   CompilerVisitor visitor = new CompilerVisitor(className, symbolTable);
 *   visitor.visit(tree);
 *   byte[] bytecode = visitor.getBytecode();
 */
public class CompilerVisitor extends JvmBasicParserBaseVisitor<Object> {

    private final String className;
    private final SymbolTable symbols;

    // ASM class writer
    private ClassWriter cw;
    private MethodVisitor mv;
    private GeneratorAdapter ga;

    // Current compilation state
    private String currentMethod = null;
    private int localVarSlot = 0;

    public CompilerVisitor(String className, SymbolTable symbols) {
        this.className = className;
        this.symbols = symbols;
    }

    public byte[] getBytecode() {
        return cw.toByteArray();
    }

    // ========================================================================
    // Compilation Unit
    // ========================================================================

    @Override
    public Object visitCompilationUnit(JvmBasicParser.CompilationUnitContext ctx) {
        // Initialize class writer
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(V21, ACC_PUBLIC | ACC_SUPER, className, null, "java/lang/Object", null);

        // Generate default constructor
        generateDefaultConstructor();

        // Visit all declarations from topLevelElement
        for (JvmBasicParser.TopLevelElementContext elem : ctx.topLevelElement()) {
            if (elem.declaration() != null) {
                visit(elem.declaration());
            }
        }

        // Generate main method with top-level statements from topLevelElement
        boolean hasStatements = ctx.topLevelElement().stream()
            .anyMatch(elem -> elem.statement() != null);
        if (hasStatements) {
            generateMainMethod(ctx);
        }

        cw.visitEnd();
        return null;
    }

    private void generateDefaultConstructor() {
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void generateMainMethod(JvmBasicParser.CompilationUnitContext ctx) {
        mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();

        currentMethod = "main";
        localVarSlot = 1; // slot 0 is args

        // Visit all top-level statements from topLevelElement
        for (JvmBasicParser.TopLevelElementContext elem : ctx.topLevelElement()) {
            if (elem.statement() != null) {
                visit(elem.statement());
            }
        }

        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0); // Computed by ClassWriter
        mv.visitEnd();

        currentMethod = null;
    }

    // ========================================================================
    // Function/Sub Declarations
    // ========================================================================

    @Override
    public Object visitFunctionDeclaration(JvmBasicParser.FunctionDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        FunctionSymbol func = symbols.getFunction(name);

        String descriptor = buildMethodDescriptor(func);
        int access = ACC_PUBLIC | ACC_STATIC;

        mv = cw.visitMethod(access, name, descriptor, null, null);
        mv.visitCode();

        currentMethod = name;
        localVarSlot = func.getParameters().size();

        // Visit function body
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }

        // Add default return if needed
        String returnType = func.returnType;
        if (!endsWithReturn(ctx)) {
            generateDefaultReturn(returnType);
        }

        mv.visitMaxs(0, 0);
        mv.visitEnd();

        currentMethod = null;
        return null;
    }

    @Override
    public Object visitSubDeclaration(JvmBasicParser.SubDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        FunctionSymbol sub = symbols.getFunction(name);

        String descriptor = buildMethodDescriptor(sub);
        int access = ACC_PUBLIC | ACC_STATIC;

        mv = cw.visitMethod(access, name, descriptor, null, null);
        mv.visitCode();

        currentMethod = name;
        localVarSlot = sub.getParameters().size();

        // Visit sub body
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }

        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        currentMethod = null;
        return null;
    }

    // ========================================================================
    // Statements
    // ========================================================================

    @Override
    public Object visitVarStatement(JvmBasicParser.VarStatementContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        String type = ctx.typeName().getText();
        int slot = localVarSlot++;

        // Store slot in symbol table for later reference
        if (currentMethod != null) {
            FunctionSymbol func = symbols.getFunction(currentMethod);
            if (func != null) {
                VariableSymbol var = func.getLocal(name);
                if (var != null) {
                    var.setSlot(slot);
                }
            }
        }

        // Generate initialization if present
        if (ctx.expression() != null) {
            visit(ctx.expression());
            storeLocal(slot, type);
        } else {
            // Initialize with default value
            generateDefaultValue(type);
            storeLocal(slot, type);
        }

        return null;
    }

    @Override
    public Object visitAssignmentStatement(JvmBasicParser.AssignmentStatementContext ctx) {
        // Evaluate RHS expression
        visit(ctx.expression());

        // Store to LHS
        visitLValueStore(ctx.lvalue());

        return null;
    }

    @Override
    public Object visitReturnStatement(JvmBasicParser.ReturnStatementContext ctx) {
        if (ctx.expression() != null) {
            visit(ctx.expression());
            // Determine return type and use appropriate return instruction
            FunctionSymbol func = symbols.getFunction(currentMethod);
            String returnType = func != null ? func.returnType : "Void";
            generateReturn(returnType);
        } else {
            mv.visitInsn(RETURN);
        }
        return null;
    }

    @Override
    public Object visitIfStatement(JvmBasicParser.IfStatementContext ctx) {
        Label elseLabel = new Label();
        Label endLabel = new Label();

        // Evaluate condition
        visit(ctx.expression());
        mv.visitJumpInsn(IFEQ, elseLabel);

        // Then block
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }
        mv.visitJumpInsn(GOTO, endLabel);

        // Else-if clauses
        mv.visitLabel(elseLabel);
        for (JvmBasicParser.ElseIfClauseContext elseIf : ctx.elseIfClause()) {
            Label nextElse = new Label();
            visit(elseIf.expression());
            mv.visitJumpInsn(IFEQ, nextElse);

            for (JvmBasicParser.StatementContext stmt : elseIf.statement()) {
                visit(stmt);
            }
            mv.visitJumpInsn(GOTO, endLabel);
            mv.visitLabel(nextElse);
        }

        // Else clause
        if (ctx.elseClause() != null) {
            for (JvmBasicParser.StatementContext stmt : ctx.elseClause().statement()) {
                visit(stmt);
            }
        }

        mv.visitLabel(endLabel);
        return null;
    }

    @Override
    public Object visitWhileStatement(JvmBasicParser.WhileStatementContext ctx) {
        Label startLabel = new Label();
        Label endLabel = new Label();

        mv.visitLabel(startLabel);

        // Evaluate condition
        visit(ctx.expression());
        mv.visitJumpInsn(IFEQ, endLabel);

        // Loop body
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }

        mv.visitJumpInsn(GOTO, startLabel);
        mv.visitLabel(endLabel);

        return null;
    }

    @Override
    public Object visitForStatement(JvmBasicParser.ForStatementContext ctx) {
        // TODO: Implement for loop
        return null;
    }

    @Override
    public Object visitExpressionStatement(JvmBasicParser.ExpressionStatementContext ctx) {
        visit(ctx.expression());
        // Pop result if expression leaves value on stack
        // (method calls that return void don't leave anything)
        return null;
    }

    // ========================================================================
    // Expressions
    // ========================================================================

    @Override
    public Object visitIntLiteral(JvmBasicParser.IntLiteralContext ctx) {
        int value = Integer.parseInt(ctx.INTEGER_LITERAL().getText());
        mv.visitLdcInsn(value);
        return null;
    }

    @Override
    public Object visitStringLiteral(JvmBasicParser.StringLiteralContext ctx) {
        String text = ctx.STRING_LITERAL().getText();
        // Remove quotes and process escapes
        String value = processStringLiteral(text);
        mv.visitLdcInsn(value);
        return null;
    }

    @Override
    public Object visitTrueLiteral(JvmBasicParser.TrueLiteralContext ctx) {
        mv.visitInsn(ICONST_1);
        return null;
    }

    @Override
    public Object visitFalseLiteral(JvmBasicParser.FalseLiteralContext ctx) {
        mv.visitInsn(ICONST_0);
        return null;
    }

    @Override
    public Object visitNilLiteral(JvmBasicParser.NilLiteralContext ctx) {
        mv.visitInsn(ACONST_NULL);
        return null;
    }

    @Override
    public Object visitNothingLiteral(JvmBasicParser.NothingLiteralContext ctx) {
        mv.visitInsn(ACONST_NULL);
        return null;
    }

    @Override
    public Object visitIdentifierExpr(JvmBasicParser.IdentifierExprContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        // Load variable value
        loadVariable(name);
        return null;
    }

    @Override
    public Object visitMethodCall(JvmBasicParser.MethodCallContext ctx) {
        // This handles Namespace.Method() calls like Console.WriteLine()
        // The object/namespace is already on stack from primary expression
        String methodName = ctx.IDENTIFIER().getText();

        // Visit arguments
        if (ctx.argumentList() != null) {
            for (JvmBasicParser.ArgumentContext arg : ctx.argumentList().argument()) {
                visit(arg.expression());
            }
        }

        // TODO: Determine actual method to call based on namespace/type
        // For now, this is a placeholder
        return null;
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private String buildMethodDescriptor(FunctionSymbol func) {
        StringBuilder sb = new StringBuilder("(");
        for (ParameterSymbol param : func.getParameters()) {
            sb.append(typeToDescriptor(param.type));
        }
        sb.append(")");
        sb.append(typeToDescriptor(func.returnType));
        return sb.toString();
    }

    private String typeToDescriptor(String type) {
        return switch (type.toLowerCase()) {
            case "integer", "int" -> "I";
            case "long" -> "J";
            case "float" -> "F";
            case "double" -> "D";
            case "boolean", "bool" -> "Z";
            case "byte" -> "B";
            case "char" -> "C";
            case "string" -> "Ljava/lang/String;";
            case "void" -> "V";
            case "object" -> "Ljava/lang/Object;";
            default -> "L" + type.replace(".", "/") + ";";
        };
    }

    private void storeLocal(int slot, String type) {
        switch (type.toLowerCase()) {
            case "integer", "int", "boolean", "bool", "byte", "char" -> mv.visitVarInsn(ISTORE, slot);
            case "long" -> mv.visitVarInsn(LSTORE, slot);
            case "float" -> mv.visitVarInsn(FSTORE, slot);
            case "double" -> mv.visitVarInsn(DSTORE, slot);
            default -> mv.visitVarInsn(ASTORE, slot);
        }
    }

    private void loadLocal(int slot, String type) {
        switch (type.toLowerCase()) {
            case "integer", "int", "boolean", "bool", "byte", "char" -> mv.visitVarInsn(ILOAD, slot);
            case "long" -> mv.visitVarInsn(LLOAD, slot);
            case "float" -> mv.visitVarInsn(FLOAD, slot);
            case "double" -> mv.visitVarInsn(DLOAD, slot);
            default -> mv.visitVarInsn(ALOAD, slot);
        }
    }

    private void generateDefaultValue(String type) {
        switch (type.toLowerCase()) {
            case "integer", "int", "boolean", "bool", "byte", "char" -> mv.visitInsn(ICONST_0);
            case "long" -> mv.visitInsn(LCONST_0);
            case "float" -> mv.visitInsn(FCONST_0);
            case "double" -> mv.visitInsn(DCONST_0);
            case "string" -> mv.visitLdcInsn("");
            default -> mv.visitInsn(ACONST_NULL);
        }
    }

    private void generateReturn(String type) {
        switch (type.toLowerCase()) {
            case "integer", "int", "boolean", "bool", "byte", "char" -> mv.visitInsn(IRETURN);
            case "long" -> mv.visitInsn(LRETURN);
            case "float" -> mv.visitInsn(FRETURN);
            case "double" -> mv.visitInsn(DRETURN);
            case "void" -> mv.visitInsn(RETURN);
            default -> mv.visitInsn(ARETURN);
        }
    }

    private void generateDefaultReturn(String type) {
        generateDefaultValue(type);
        generateReturn(type);
    }

    private void loadVariable(String name) {
        // Check local variables first
        if (currentMethod != null) {
            FunctionSymbol func = symbols.getFunction(currentMethod);
            if (func != null) {
                VariableSymbol local = func.getLocal(name);
                if (local != null && local.getSlot() >= 0) {
                    loadLocal(local.getSlot(), local.type);
                    return;
                }
            }
        }

        // Check globals
        VariableSymbol global = symbols.getGlobal(name);
        if (global != null) {
            mv.visitFieldInsn(GETSTATIC, className, name, typeToDescriptor(global.type));
            return;
        }

        // Unknown variable - will be caught by semantic analysis
        throw new RuntimeException("Unknown variable: " + name);
    }

    private void visitLValueStore(JvmBasicParser.LvalueContext lvalue) {
        // TODO: Handle member access and array indexing
        if (lvalue instanceof JvmBasicParser.SimpleLValueContext simple) {
            String name = simple.IDENTIFIER().getText();
            // Find variable slot and store
            if (currentMethod != null) {
                FunctionSymbol func = symbols.getFunction(currentMethod);
                if (func != null) {
                    VariableSymbol local = func.getLocal(name);
                    if (local != null && local.getSlot() >= 0) {
                        storeLocal(local.getSlot(), local.type);
                        return;
                    }
                }
            }
        }
    }

    private boolean endsWithReturn(JvmBasicParser.FunctionDeclarationContext ctx) {
        var statements = ctx.statement();
        if (statements.isEmpty()) return false;
        var lastStatement = statements.get(statements.size() - 1);
        // StatementContext has children that are specific statement types
        return lastStatement.returnStatement() != null;
    }

    private String processStringLiteral(String text) {
        // Remove surrounding quotes
        String content = text.substring(1, text.length() - 1);
        // Process escape sequences
        return content
            .replace("\\n", "\n")
            .replace("\\t", "\t")
            .replace("\\r", "\r")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\");
    }
}
