package com.jvmbasic.visitor;

import com.jvmbasic.grammar.*;
import com.jvmbasic.visitor.SymbolCollector.*;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

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

    // Track main method locals when not using a FunctionSymbol
    private final java.util.Map<String, LocalVar> mainLocals = new java.util.LinkedHashMap<>();

    private record LocalVar(String name, String type, int slot) {}

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
        int slot = allocateSlot(type);

        // Store slot in symbol table for later reference
        if (currentMethod != null) {
            FunctionSymbol func = symbols.getFunction(currentMethod);
            if (func != null) {
                VariableSymbol var = func.getLocal(name);
                if (var != null) {
                    var.setSlot(slot);
                }
            } else if ("main".equals(currentMethod)) {
                // Track main method locals separately
                mainLocals.put(name, new LocalVar(name, type, slot));
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

    // Allocate a local variable slot, accounting for wide types (long, double)
    private int allocateSlot(String type) {
        int slot = localVarSlot;
        localVarSlot += isWideType(type) ? 2 : 1;
        return slot;
    }

    // Long and Double take 2 slots in the JVM
    private boolean isWideType(String type) {
        String t = type.toLowerCase();
        return t.equals("long") || t.equals("double");
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
    public Object visitDoStatement(JvmBasicParser.DoStatementContext ctx) {
        // DO [WHILE|UNTIL expr] ... LOOP [WHILE|UNTIL expr]
        Label startLabel = new Label();
        Label endLabel = new Label();

        // Check for pre-condition (DO WHILE/UNTIL)
        boolean hasPreWhile = false;
        boolean hasPreUntil = false;
        boolean hasPostWhile = false;
        boolean hasPostUntil = false;

        // Count occurrences of WHILE and UNTIL tokens to determine structure
        // expression(0) is for pre-condition if it exists before statements
        // The grammar structure means we need to check tokens carefully
        var children = ctx.children;
        int exprIndex = 0;

        // Check tokens after DO
        for (int i = 0; i < children.size(); i++) {
            String text = children.get(i).getText();
            if (text.equalsIgnoreCase("WHILE") && i < 3) {
                hasPreWhile = true;
            } else if (text.equalsIgnoreCase("UNTIL") && i < 3) {
                hasPreUntil = true;
            } else if (text.equalsIgnoreCase("LOOP")) {
                // Check tokens after LOOP
                if (i + 1 < children.size()) {
                    String nextText = children.get(i + 1).getText();
                    if (nextText.equalsIgnoreCase("WHILE")) {
                        hasPostWhile = true;
                    } else if (nextText.equalsIgnoreCase("UNTIL")) {
                        hasPostUntil = true;
                    }
                }
                break;
            }
        }

        mv.visitLabel(startLabel);

        // Pre-condition check
        if ((hasPreWhile || hasPreUntil) && !ctx.expression().isEmpty()) {
            visit(ctx.expression(0));
            if (hasPreUntil) {
                // UNTIL: exit when true (IFNE = jump if not equal to 0)
                mv.visitJumpInsn(IFNE, endLabel);
            } else {
                // WHILE: exit when false (IFEQ = jump if equal to 0)
                mv.visitJumpInsn(IFEQ, endLabel);
            }
            exprIndex = 1;
        }

        // Loop body
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }

        // Post-condition check
        if (hasPostWhile || hasPostUntil) {
            int postExprIndex = (hasPreWhile || hasPreUntil) ? 1 : 0;
            if (postExprIndex < ctx.expression().size()) {
                visit(ctx.expression(postExprIndex));
                if (hasPostUntil) {
                    // UNTIL: continue when false (IFEQ = jump if equal to 0)
                    mv.visitJumpInsn(IFEQ, startLabel);
                } else {
                    // WHILE: continue when true (IFNE = jump if not equal to 0)
                    mv.visitJumpInsn(IFNE, startLabel);
                }
            } else {
                mv.visitJumpInsn(GOTO, startLabel);
            }
        } else {
            // No post-condition, just loop back
            mv.visitJumpInsn(GOTO, startLabel);
        }

        mv.visitLabel(endLabel);
        return null;
    }

    @Override
    public Object visitForStatement(JvmBasicParser.ForStatementContext ctx) {
        // FOR i = start TO end [STEP step]
        //   statements
        // NEXT i

        String varName = ctx.IDENTIFIER(0).getText();

        // Evaluate start value and store in loop variable
        visit(ctx.expression(0));  // start expression
        int slot = localVarSlot++;
        mv.visitVarInsn(ISTORE, slot);

        // Track the loop variable in mainLocals
        if ("main".equals(currentMethod)) {
            mainLocals.put(varName, new LocalVar(varName, "Integer", slot));
        }

        // We need to store the end value in a temp variable since we check it each iteration
        visit(ctx.expression(1));  // end expression
        int endSlot = localVarSlot++;
        mv.visitVarInsn(ISTORE, endSlot);

        // Handle optional STEP (default is 1)
        int stepSlot = -1;
        boolean hasStep = ctx.expression().size() > 2;
        if (hasStep) {
            visit(ctx.expression(2));  // step expression
            stepSlot = localVarSlot++;
            mv.visitVarInsn(ISTORE, stepSlot);
        }

        Label startLabel = new Label();
        Label endLabel = new Label();

        mv.visitLabel(startLabel);

        // Condition: i <= end (for positive step) or i >= end (for negative step)
        // For simplicity in MVP, assume positive step: i <= end
        mv.visitVarInsn(ILOAD, slot);      // Load i
        mv.visitVarInsn(ILOAD, endSlot);   // Load end
        mv.visitJumpInsn(IF_ICMPGT, endLabel);  // if i > end, exit loop

        // Execute loop body
        for (var stmt : ctx.statement()) {
            visit(stmt);
        }

        // Increment: i = i + step (or i + 1 if no step)
        mv.visitVarInsn(ILOAD, slot);
        if (hasStep) {
            mv.visitVarInsn(ILOAD, stepSlot);
        } else {
            mv.visitInsn(ICONST_1);
        }
        mv.visitInsn(IADD);
        mv.visitVarInsn(ISTORE, slot);

        mv.visitJumpInsn(GOTO, startLabel);
        mv.visitLabel(endLabel);

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
        lastExprType = "Integer";
        return null;
    }

    @Override
    public Object visitLongLiteral(JvmBasicParser.LongLiteralContext ctx) {
        String text = ctx.LONG_LITERAL().getText();
        // Remove the L suffix if present
        if (text.toUpperCase().endsWith("L")) {
            text = text.substring(0, text.length() - 1);
        }
        long value = Long.parseLong(text);
        mv.visitLdcInsn(value);
        lastExprType = "Long";
        return null;
    }

    @Override
    public Object visitFloatLiteral(JvmBasicParser.FloatLiteralContext ctx) {
        String text = ctx.FLOAT_LITERAL().getText();
        // Remove the F suffix if present
        if (text.toUpperCase().endsWith("F")) {
            text = text.substring(0, text.length() - 1);
        }
        float value = Float.parseFloat(text);
        mv.visitLdcInsn(value);
        lastExprType = "Float";
        return null;
    }

    @Override
    public Object visitDoubleLiteral(JvmBasicParser.DoubleLiteralContext ctx) {
        String text = ctx.DOUBLE_LITERAL().getText();
        // Remove the D suffix if present
        if (text.toUpperCase().endsWith("D")) {
            text = text.substring(0, text.length() - 1);
        }
        double value = Double.parseDouble(text);
        mv.visitLdcInsn(value);
        lastExprType = "Double";
        return null;
    }

    @Override
    public Object visitStringLiteral(JvmBasicParser.StringLiteralContext ctx) {
        String text = ctx.STRING_LITERAL().getText();
        // Remove quotes and process escapes
        String value = processStringLiteral(text);
        mv.visitLdcInsn(value);
        lastExprType = "String";
        return null;
    }

    @Override
    public Object visitTrueLiteral(JvmBasicParser.TrueLiteralContext ctx) {
        mv.visitInsn(ICONST_1);
        lastExprType = "Boolean";
        return null;
    }

    @Override
    public Object visitFalseLiteral(JvmBasicParser.FalseLiteralContext ctx) {
        mv.visitInsn(ICONST_0);
        lastExprType = "Boolean";
        return null;
    }

    @Override
    public Object visitNilLiteral(JvmBasicParser.NilLiteralContext ctx) {
        mv.visitInsn(ACONST_NULL);
        lastExprType = "Object";
        return null;
    }

    @Override
    public Object visitNothingLiteral(JvmBasicParser.NothingLiteralContext ctx) {
        mv.visitInsn(ACONST_NULL);
        lastExprType = "Object";
        return null;
    }

    @Override
    public Object visitIdentifierExpr(JvmBasicParser.IdentifierExprContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        // Check for special namespaces like Console
        if ("Console".equalsIgnoreCase(name)) {
            // Console is a pseudo-namespace, don't load anything
            // The method call handler will deal with it
            pendingNamespace = "Console";
            return null;
        }
        // Check if this is a function name (will be handled by FunctionCall postfixOp)
        if (symbols.getFunction(name) != null) {
            pendingFunctionName = name;
            return null;
        }
        // Load variable value
        loadVariable(name);
        return null;
    }

    // Track namespace for method calls
    private String pendingNamespace = null;
    // Track function name for function calls
    private String pendingFunctionName = null;

    @Override
    public Object visitMethodCall(JvmBasicParser.MethodCallContext ctx) {
        // This handles Namespace.Method() calls like Console.WriteLine()
        String methodName = ctx.IDENTIFIER().getText();

        // Handle Console namespace
        if ("Console".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;

            if ("WriteLine".equalsIgnoreCase(methodName)) {
                // Get System.out
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

                // Visit argument (if any)
                if (ctx.argumentList() != null && !ctx.argumentList().argument().isEmpty()) {
                    visit(ctx.argumentList().argument(0).expression());
                    // Determine argument type - for now assume String or int
                    // In a full implementation, we'd track expression types
                    // For simplicity, always use Object version and convert to String
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                } else {
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "()V", false);
                }
                return null;
            } else if ("Write".equalsIgnoreCase(methodName)) {
                // Get System.out
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

                // Visit argument
                if (ctx.argumentList() != null && !ctx.argumentList().argument().isEmpty()) {
                    visit(ctx.argumentList().argument(0).expression());
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V", false);
                }
                return null;
            }
        }

        // Visit arguments for non-Console method calls
        if (ctx.argumentList() != null) {
            for (JvmBasicParser.ArgumentContext arg : ctx.argumentList().argument()) {
                visit(arg.expression());
            }
        }

        // TODO: Handle other method calls (instance methods, etc.)
        return null;
    }

    // ========================================================================
    // Binary Expressions
    // ========================================================================

    @Override
    public Object visitAdditiveExpression(JvmBasicParser.AdditiveExpressionContext ctx) {
        // First operand
        visit(ctx.multiplicativeExpression(0));

        // Process additional operands
        for (int i = 1; i < ctx.multiplicativeExpression().size(); i++) {
            String leftType = lastExprType;
            visit(ctx.multiplicativeExpression(i));
            // Determine operator (PLUS, MINUS, or AMP for string concat)
            var opToken = ctx.getChild(2 * i - 1); // Operators are at odd positions
            String op = opToken.getText();
            switch (op) {
                case "+" -> emitAdd(leftType);
                case "-" -> emitSub(leftType);
                case "&" -> {
                    // String concatenation - convert to String and call concat
                    // For now just emit IADD as placeholder
                    mv.visitInsn(IADD);
                }
            }
        }
        return null;
    }

    @Override
    public Object visitMultiplicativeExpression(JvmBasicParser.MultiplicativeExpressionContext ctx) {
        // First operand
        visit(ctx.powerExpression(0));

        // Process additional operands
        for (int i = 1; i < ctx.powerExpression().size(); i++) {
            String leftType = lastExprType;
            visit(ctx.powerExpression(i));
            var opToken = ctx.getChild(2 * i - 1);
            String op = opToken.getText();
            switch (op) {
                case "*" -> emitMul(leftType);
                case "/" -> emitDiv(leftType);
                case "\\" -> mv.visitInsn(IDIV); // Integer division
                case "mod", "Mod", "MOD" -> emitRem(leftType);
            }
        }
        return null;
    }

    // Type-aware arithmetic operations
    private void emitAdd(String type) {
        switch (type.toLowerCase()) {
            case "long" -> mv.visitInsn(LADD);
            case "float" -> mv.visitInsn(FADD);
            case "double" -> mv.visitInsn(DADD);
            default -> mv.visitInsn(IADD);
        }
    }

    private void emitSub(String type) {
        switch (type.toLowerCase()) {
            case "long" -> mv.visitInsn(LSUB);
            case "float" -> mv.visitInsn(FSUB);
            case "double" -> mv.visitInsn(DSUB);
            default -> mv.visitInsn(ISUB);
        }
    }

    private void emitMul(String type) {
        switch (type.toLowerCase()) {
            case "long" -> mv.visitInsn(LMUL);
            case "float" -> mv.visitInsn(FMUL);
            case "double" -> mv.visitInsn(DMUL);
            default -> mv.visitInsn(IMUL);
        }
    }

    private void emitDiv(String type) {
        switch (type.toLowerCase()) {
            case "long" -> mv.visitInsn(LDIV);
            case "float" -> mv.visitInsn(FDIV);
            case "double" -> mv.visitInsn(DDIV);
            default -> mv.visitInsn(IDIV);
        }
    }

    private void emitRem(String type) {
        switch (type.toLowerCase()) {
            case "long" -> mv.visitInsn(LREM);
            case "float" -> mv.visitInsn(FREM);
            case "double" -> mv.visitInsn(DREM);
            default -> mv.visitInsn(IREM);
        }
    }

    @Override
    public Object visitPowerExpr(JvmBasicParser.PowerExprContext ctx) {
        // For now, implement simple integer power using Math.pow
        visit(ctx.powerExpression(0));
        mv.visitInsn(I2D);
        visit(ctx.powerExpression(1));
        mv.visitInsn(I2D);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
        mv.visitInsn(D2I);
        return null;
    }

    @Override
    public Object visitPowerBase(JvmBasicParser.PowerBaseContext ctx) {
        return visit(ctx.unaryExpression());
    }

    @Override
    public Object visitUnaryOpExpr(JvmBasicParser.UnaryOpExprContext ctx) {
        visit(ctx.unaryExpression());
        String op = ctx.getChild(0).getText();
        switch (op) {
            case "-" -> mv.visitInsn(INEG);
            case "+" -> { /* no-op */ }
            case "not", "Not", "NOT" -> {
                // Boolean NOT: flip 0 to 1, non-zero to 0
                Label falseLabel = new Label();
                Label endLabel = new Label();
                mv.visitJumpInsn(IFEQ, falseLabel);
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, endLabel);
                mv.visitLabel(falseLabel);
                mv.visitInsn(ICONST_1);
                mv.visitLabel(endLabel);
            }
            case "~" -> {
                // Bitwise NOT
                mv.visitInsn(ICONST_M1);
                mv.visitInsn(IXOR);
            }
        }
        return null;
    }

    @Override
    public Object visitPostfixExprAlt(JvmBasicParser.PostfixExprAltContext ctx) {
        return visit(ctx.postfixExpression());
    }

    @Override
    public Object visitPostfixExpression(JvmBasicParser.PostfixExpressionContext ctx) {
        // Visit primary expression
        visit(ctx.primaryExpression());

        // Process postfix operations - need to combine MemberAccess+FunctionCall as method call
        var postfixOps = ctx.postfixOp();
        for (int i = 0; i < postfixOps.size(); i++) {
            var postfix = postfixOps.get(i);

            // Check for MemberAccess followed by FunctionCall pattern
            if (postfix instanceof JvmBasicParser.MemberAccessContext memberAccess) {
                String memberName = memberAccess.IDENTIFIER().getText();

                // Check if next op is a FunctionCall
                if (i + 1 < postfixOps.size() && postfixOps.get(i + 1) instanceof JvmBasicParser.FunctionCallContext funcCall) {
                    // This is a method call: .memberName(args)
                    handleMethodInvocation(memberName, funcCall.argumentList());
                    i++; // Skip the FunctionCall since we handled it
                } else {
                    // Just a member access
                    lastMemberAccess = memberName;
                }
            } else if (postfix instanceof JvmBasicParser.FunctionCallContext funcCall) {
                // Check if this is a user-defined function call
                if (pendingFunctionName != null) {
                    handleUserFunctionCall(pendingFunctionName, funcCall.argumentList());
                    pendingFunctionName = null;
                } else {
                    visit(postfix);
                }
            } else {
                visit(postfix);
            }
        }
        return null;
    }

    private void handleMethodInvocation(String methodName, JvmBasicParser.ArgumentListContext argList) {
        // Handle Console namespace
        if ("Console".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;

            if ("WriteLine".equalsIgnoreCase(methodName)) {
                // Get System.out
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

                // Visit argument (if any)
                if (argList != null && !argList.argument().isEmpty()) {
                    visit(argList.argument(0).expression());
                    // Use the appropriate println overload based on the expression type
                    emitPrintln();
                } else {
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "()V", false);
                }
                return;
            } else if ("Write".equalsIgnoreCase(methodName)) {
                // Get System.out
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

                // Visit argument
                if (argList != null && !argList.argument().isEmpty()) {
                    visit(argList.argument(0).expression());
                    // Use the appropriate print overload based on the expression type
                    emitPrint();
                }
                return;
            }
        }

        // TODO: Handle other method calls
    }

    // Handle user-defined function calls
    private void handleUserFunctionCall(String funcName, JvmBasicParser.ArgumentListContext argList) {
        FunctionSymbol func = symbols.getFunction(funcName);
        if (func == null) {
            throw new RuntimeException("Unknown function: " + funcName);
        }

        // Push arguments onto stack
        if (argList != null) {
            for (JvmBasicParser.ArgumentContext arg : argList.argument()) {
                visit(arg.expression());
            }
        }

        // Generate INVOKESTATIC for static function
        String descriptor = buildMethodDescriptor(func);
        mv.visitMethodInsn(INVOKESTATIC, className, funcName, descriptor, false);

        // Set lastExprType based on return type
        lastExprType = func.returnType;
    }

    // Emit the correct println call based on the type on the stack
    private void emitPrintln() {
        String descriptor = switch (lastExprType) {
            case "Integer", "int" -> "(I)V";
            case "Long", "long" -> "(J)V";
            case "Float", "float" -> "(F)V";
            case "Double", "double" -> "(D)V";
            case "Boolean", "boolean" -> "(Z)V";
            case "String" -> "(Ljava/lang/String;)V";
            default -> "(Ljava/lang/Object;)V";
        };
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", descriptor, false);
    }

    // Emit the correct print call based on the type on the stack
    private void emitPrint() {
        String descriptor = switch (lastExprType) {
            case "Integer", "int" -> "(I)V";
            case "Long", "long" -> "(J)V";
            case "Float", "float" -> "(F)V";
            case "Double", "double" -> "(D)V";
            case "Boolean", "boolean" -> "(Z)V";
            case "String" -> "(Ljava/lang/String;)V";
            default -> "(Ljava/lang/Object;)V";
        };
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", descriptor, false);
    }

    @Override
    public Object visitMemberAccess(JvmBasicParser.MemberAccessContext ctx) {
        // Handle Console.WriteLine etc.
        String memberName = ctx.IDENTIFIER().getText();
        // The object is already on stack from previous expression
        // For now, just record member access info
        lastMemberAccess = memberName;
        return null;
    }

    // Track last member access for method call resolution
    private String lastMemberAccess = null;
    private String lastObjectType = null;

    // Track the type of the last expression evaluated (for println overload selection)
    private String lastExprType = "Object";

    @Override
    public Object visitParenExpr(JvmBasicParser.ParenExprContext ctx) {
        return visit(ctx.expression());
    }

    // ========================================================================
    // Comparison Expressions
    // ========================================================================

    @Override
    public Object visitRelationalExpression(JvmBasicParser.RelationalExpressionContext ctx) {
        if (ctx.shiftExpression().size() == 1) {
            return visit(ctx.shiftExpression(0));
        }

        visit(ctx.shiftExpression(0));
        for (int i = 1; i < ctx.shiftExpression().size(); i++) {
            visit(ctx.shiftExpression(i));
            var opToken = ctx.getChild(2 * i - 1);
            String op = opToken.getText();

            Label trueLabel = new Label();
            Label endLabel = new Label();

            switch (op) {
                case "<" -> mv.visitJumpInsn(IF_ICMPLT, trueLabel);
                case ">" -> mv.visitJumpInsn(IF_ICMPGT, trueLabel);
                case "<=" -> mv.visitJumpInsn(IF_ICMPLE, trueLabel);
                case ">=" -> mv.visitJumpInsn(IF_ICMPGE, trueLabel);
            }
            mv.visitInsn(ICONST_0);
            mv.visitJumpInsn(GOTO, endLabel);
            mv.visitLabel(trueLabel);
            mv.visitInsn(ICONST_1);
            mv.visitLabel(endLabel);
        }
        return null;
    }

    @Override
    public Object visitEqualityExpression(JvmBasicParser.EqualityExpressionContext ctx) {
        if (ctx.relationalExpression().size() == 1) {
            return visit(ctx.relationalExpression(0));
        }

        visit(ctx.relationalExpression(0));
        for (int i = 1; i < ctx.relationalExpression().size(); i++) {
            visit(ctx.relationalExpression(i));
            var opToken = ctx.getChild(2 * i - 1);
            String op = opToken.getText();

            Label trueLabel = new Label();
            Label endLabel = new Label();

            switch (op) {
                case "=" -> mv.visitJumpInsn(IF_ICMPEQ, trueLabel);
                case "<>" -> mv.visitJumpInsn(IF_ICMPNE, trueLabel);
            }
            mv.visitInsn(ICONST_0);
            mv.visitJumpInsn(GOTO, endLabel);
            mv.visitLabel(trueLabel);
            mv.visitInsn(ICONST_1);
            mv.visitLabel(endLabel);
        }
        return null;
    }

    @Override
    public Object visitShiftExpression(JvmBasicParser.ShiftExpressionContext ctx) {
        visit(ctx.additiveExpression(0));
        for (int i = 1; i < ctx.additiveExpression().size(); i++) {
            visit(ctx.additiveExpression(i));
            var opToken = ctx.getChild(2 * i - 1);
            String op = opToken.getText();
            switch (op) {
                case "<<" -> mv.visitInsn(ISHL);
                case ">>" -> mv.visitInsn(ISHR);
            }
        }
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
        // Track the type for println overload selection
        lastExprType = type;
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
        // Check local variables and parameters first
        if (currentMethod != null) {
            FunctionSymbol func = symbols.getFunction(currentMethod);
            if (func != null) {
                // Check parameters first (they occupy slots 0, 1, 2...)
                List<ParameterSymbol> params = func.getParameters();
                for (int i = 0; i < params.size(); i++) {
                    if (params.get(i).name.equals(name)) {
                        loadLocal(i, params.get(i).type);
                        return;
                    }
                }
                // Check local variables
                VariableSymbol local = func.getLocal(name);
                if (local != null && local.getSlot() >= 0) {
                    loadLocal(local.getSlot(), local.type);
                    return;
                }
            } else if ("main".equals(currentMethod)) {
                // Check main method locals
                LocalVar local = mainLocals.get(name);
                if (local != null) {
                    loadLocal(local.slot(), local.type());
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
                } else if ("main".equals(currentMethod)) {
                    // Check main locals
                    LocalVar local = mainLocals.get(name);
                    if (local != null) {
                        storeLocal(local.slot(), local.type());
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
