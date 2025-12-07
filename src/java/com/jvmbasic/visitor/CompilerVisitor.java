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

    // ========================================================================
    // Block Scoping Support
    // ========================================================================

    // Scope stack for tracking variable visibility and slot allocation
    private final java.util.Deque<ScopeFrame> scopeStack = new java.util.ArrayDeque<>();

    // Record for tracking variables within a scope
    private record ScopeFrame(
        String name,
        int startSlot,                                    // Slot level when entering scope
        java.util.Map<String, LocalVar> variables         // Variables declared in this scope
    ) {
        ScopeFrame(String name, int startSlot) {
            this(name, startSlot, new java.util.LinkedHashMap<>());
        }
    }

    // Enter a new scope (block, loop, etc.)
    private void enterScopeFrame(String name) {
        scopeStack.push(new ScopeFrame(name, localVarSlot));
    }

    // Exit scope and optionally reclaim slots
    private void exitScopeFrame(boolean reclaimSlots) {
        if (!scopeStack.isEmpty()) {
            ScopeFrame frame = scopeStack.pop();
            if (reclaimSlots) {
                // Reclaim slots used by this scope
                localVarSlot = frame.startSlot();
            }
        }
    }

    // Add a variable to the current scope
    private void addScopedVariable(String name, String type, int slot) {
        if (!scopeStack.isEmpty()) {
            scopeStack.peek().variables().put(name, new LocalVar(name, type, slot));
        }
    }

    // Look up a variable in the scope chain (innermost first)
    private LocalVar lookupScopedVariable(String name) {
        for (ScopeFrame frame : scopeStack) {
            LocalVar var = frame.variables().get(name);
            if (var != null) {
                return var;
            }
        }
        return null;
    }

    // Check if variable is in current scope (not parent scopes)
    private boolean isInCurrentScope(String name) {
        if (!scopeStack.isEmpty()) {
            return scopeStack.peek().variables().containsKey(name);
        }
        return false;
    }

    // Track dynamically-created locals (FOR loop variables, etc.) during codegen
    // This complements the symbol table which only has declared variables
    // DEPRECATED: Use scopeStack instead for new code
    private final java.util.Map<String, LocalVar> dynamicLocals = new java.util.LinkedHashMap<>();

    // Legacy alias for backwards compatibility
    private java.util.Map<String, LocalVar> mainLocals = dynamicLocals;

    // OOP support: generated class files for user-defined classes
    private final java.util.Map<String, byte[]> generatedClasses = new java.util.LinkedHashMap<>();
    private String currentClass = null;  // Name of class being compiled (null = main class)

    private record LocalVar(String name, String type, int slot) {}

    // Loop context for exit/continue statements
    private final java.util.Deque<LoopContext> loopStack = new java.util.ArrayDeque<>();

    private record LoopContext(String type, Label continueLabel, Label breakLabel) {}

    private void pushLoop(String type, Label continueLabel, Label breakLabel) {
        loopStack.push(new LoopContext(type, continueLabel, breakLabel));
    }

    private void popLoop() {
        loopStack.pop();
    }

    private LoopContext findLoop(String type) {
        if (type == null) {
            // No specific type, return the innermost loop
            return loopStack.isEmpty() ? null : loopStack.peek();
        }
        for (LoopContext ctx : loopStack) {
            if (ctx.type.equalsIgnoreCase(type)) {
                return ctx;
            }
        }
        return null;
    }

    public CompilerVisitor(String className, SymbolTable symbols) {
        this.className = className;
        this.symbols = symbols;
    }

    public byte[] getBytecode() {
        return cw.toByteArray();
    }

    /**
     * Returns a map of generated class files for user-defined classes.
     * Key is class name, value is bytecode.
     */
    public java.util.Map<String, byte[]> getGeneratedClasses() {
        return generatedClasses;
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
        dynamicLocals.clear();  // Clear dynamic locals for main method
        scopeStack.clear();     // Clear scope stack
        enterScopeFrame("main"); // Enter main function scope

        // Visit all top-level statements from topLevelElement
        for (JvmBasicParser.TopLevelElementContext elem : ctx.topLevelElement()) {
            if (elem.statement() != null) {
                visit(elem.statement());
            }
        }

        exitScopeFrame(false);  // Exit main scope (no slot reclaim needed)
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
        dynamicLocals.clear();  // Clear dynamic locals for new function scope
        scopeStack.clear();     // Clear scope stack
        enterScopeFrame(name);  // Enter function scope

        // Add parameters to scope
        List<ParameterSymbol> params = func.getParameters();
        for (int i = 0; i < params.size(); i++) {
            addScopedVariable(params.get(i).name, params.get(i).type, i);
        }

        // Visit function body
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }

        // Add default return if needed
        String returnType = func.returnType;
        if (!endsWithReturn(ctx)) {
            generateDefaultReturn(returnType);
        }

        exitScopeFrame(false);  // Exit function scope
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        currentMethod = null;
        dynamicLocals.clear();  // Clean up after function
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
        dynamicLocals.clear();  // Clear dynamic locals for new sub scope
        scopeStack.clear();     // Clear scope stack
        enterScopeFrame(name);  // Enter sub scope

        // Add parameters to scope
        List<ParameterSymbol> params = sub.getParameters();
        for (int i = 0; i < params.size(); i++) {
            addScopedVariable(params.get(i).name, params.get(i).type, i);
        }

        // Visit sub body
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }

        exitScopeFrame(false);  // Exit sub scope
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        currentMethod = null;
        dynamicLocals.clear();  // Clean up after sub
        return null;
    }

    // ========================================================================
    // Class Declarations (OOP support)
    // ========================================================================

    @Override
    public Object visitClassDeclaration(JvmBasicParser.ClassDeclarationContext ctx) {
        String classNameStr = ctx.IDENTIFIER().getText();
        ClassSymbol classSym = symbols.getClass(classNameStr);
        if (classSym == null) {
            throw new RuntimeException("Unknown class: " + classNameStr);
        }

        // Save current class writer state
        ClassWriter savedCw = cw;
        MethodVisitor savedMv = mv;
        String savedCurrentClass = currentClass;

        // Create new class writer for this class
        currentClass = classNameStr;
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        // Determine base class
        String baseClass = classSym.getBaseClass();
        String baseClassInternal = "java/lang/Object";
        if (baseClass != null && !baseClass.equals("Object")) {
            baseClassInternal = baseClass.replace(".", "/");
        }

        cw.visit(V21, ACC_PUBLIC | ACC_SUPER, classNameStr, null, baseClassInternal, null);

        // Generate fields
        for (FieldSymbol field : classSym.getFields()) {
            int access = fieldAccessToOpcodes(field.getAccessModifier());
            if (field.isStatic()) {
                access |= ACC_STATIC;
            }
            cw.visitField(access, field.name, typeToDescriptor(field.type), null, null).visitEnd();
        }

        // Generate constructor(s)
        boolean hasConstructor = false;
        for (JvmBasicParser.ClassMemberContext member : ctx.classMember()) {
            if (member.constructorDeclaration() != null) {
                generateConstructor(member.constructorDeclaration(), classNameStr, baseClassInternal, classSym);
                hasConstructor = true;
            }
        }

        // If no constructor, generate default constructor
        if (!hasConstructor) {
            generateDefaultClassConstructor(baseClassInternal);
        }

        // Generate methods
        for (JvmBasicParser.ClassMemberContext member : ctx.classMember()) {
            if (member.methodDeclaration() != null) {
                generateMethod(member.methodDeclaration(), classNameStr, classSym);
            }
        }

        cw.visitEnd();

        // Store generated class bytecode
        generatedClasses.put(classNameStr, cw.toByteArray());

        // Restore class writer state
        cw = savedCw;
        mv = savedMv;
        currentClass = savedCurrentClass;

        return null;
    }

    private int fieldAccessToOpcodes(String accessModifier) {
        return switch (accessModifier.toLowerCase()) {
            case "public" -> ACC_PUBLIC;
            case "private" -> ACC_PRIVATE;
            case "protected" -> ACC_PROTECTED;
            default -> 0;  // Package-private
        };
    }

    private void generateDefaultClassConstructor(String baseClassInternal) {
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, baseClassInternal, "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void generateConstructor(JvmBasicParser.ConstructorDeclarationContext ctx,
                                     String classNameStr, String baseClassInternal, ClassSymbol classSym) {
        // Get constructor symbol (stored as method named "New")
        FunctionSymbol constructor = classSym.getMethod("New");

        // Build descriptor
        StringBuilder descBuilder = new StringBuilder("(");
        if (constructor != null) {
            for (ParameterSymbol param : constructor.getParameters()) {
                descBuilder.append(typeToDescriptor(param.type));
            }
        }
        descBuilder.append(")V");
        String descriptor = descBuilder.toString();

        mv = cw.visitMethod(ACC_PUBLIC, "<init>", descriptor, null, null);
        mv.visitCode();

        // Call super constructor
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, baseClassInternal, "<init>", "()V", false);

        currentMethod = "New";
        // Slot 0 is 'this', parameters start at 1
        localVarSlot = 1 + (constructor != null ? constructor.getParameters().size() : 0);

        // Visit constructor body
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }

        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        currentMethod = null;
    }

    private void generateMethod(JvmBasicParser.MethodDeclarationContext ctx,
                                String classNameStr, ClassSymbol classSym) {
        String methodName = ctx.IDENTIFIER().getText();
        FunctionSymbol methodSym = classSym.getMethod(methodName);
        if (methodSym == null) {
            throw new RuntimeException("Unknown method: " + methodName + " in class " + classNameStr);
        }

        // Determine if static (SHARED keyword)
        boolean isStatic = ctx.SHARED() != null;

        // Build descriptor
        String descriptor = buildMethodDescriptor(methodSym);

        // Determine access
        int access = ACC_PUBLIC;  // Default to public
        if (ctx.accessModifier() != null) {
            access = fieldAccessToOpcodes(ctx.accessModifier().getText());
        }
        if (isStatic) {
            access |= ACC_STATIC;
        }

        mv = cw.visitMethod(access, methodName, descriptor, null, null);
        mv.visitCode();

        currentMethod = methodName;
        // Slot 0 is 'this' for instance methods, parameters start at 1 (or 0 for static)
        localVarSlot = isStatic ? methodSym.getParameters().size() : 1 + methodSym.getParameters().size();

        // Visit method body
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }

        // Add default return if needed
        String returnType = methodSym.returnType;
        if (!methodEndsWithReturn(ctx)) {
            generateDefaultReturn(returnType);
        }

        mv.visitMaxs(0, 0);
        mv.visitEnd();

        currentMethod = null;
    }

    private boolean methodEndsWithReturn(JvmBasicParser.MethodDeclarationContext ctx) {
        var statements = ctx.statement();
        if (statements.isEmpty()) return false;
        var lastStatement = statements.get(statements.size() - 1);
        return lastStatement.returnStatement() != null;
    }

    // ========================================================================
    // Statements
    // ========================================================================

    @Override
    public Object visitVarStatement(JvmBasicParser.VarStatementContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        String type = ctx.typeName().getText();
        int slot = allocateSlot(type);

        // Add to current scope for proper block scoping
        addScopedVariable(name, type, slot);

        // Store slot in symbol table for later reference (legacy support)
        if (currentMethod != null) {
            FunctionSymbol func = symbols.getFunction(currentMethod);
            if (func != null) {
                VariableSymbol var = func.getLocal(name);
                if (var != null) {
                    var.setSlot(slot);
                }
            } else if ("main".equals(currentMethod)) {
                // Track main method locals separately (legacy)
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
        // Check if LHS is an array index assignment
        if (ctx.lvalue() instanceof JvmBasicParser.IndexLValueContext indexLValue) {
            // For array assignment: arr[index] = value
            // We need to: push arr ref, push index, push value, then XASTORE

            // Get the array variable from the inner lvalue
            JvmBasicParser.LvalueContext innerLValue = indexLValue.lvalue();
            if (innerLValue instanceof JvmBasicParser.SimpleLValueContext simpleLValue) {
                String arrayName = simpleLValue.IDENTIFIER().getText();
                loadVariable(arrayName);  // Push array ref

                visit(indexLValue.expression());  // Push index
                visit(ctx.expression());  // Push value

                // Determine element type
                String elementType = getArrayElementTypeFromVariable(arrayName);
                emitArrayStore(elementType);
            }
            return null;
        }

        // Check if LHS is member access (e.g., this.name = value)
        if (ctx.lvalue() instanceof JvmBasicParser.MemberLValueContext memberLValue) {
            // For field assignment: obj.field = value
            // We need to: push obj ref, push value, then PUTFIELD
            String fieldName = memberLValue.IDENTIFIER().getText();

            // Get object reference from inner lvalue
            JvmBasicParser.LvalueContext innerLValue = memberLValue.lvalue();

            // Check if it's this.field (using ThisLValue grammar rule)
            if (innerLValue instanceof JvmBasicParser.ThisLValueContext && currentClass != null) {
                // this.field = value
                mv.visitVarInsn(ALOAD, 0);  // Push 'this'
                visit(ctx.expression());    // Push value

                // Get field type
                ClassSymbol classSym = symbols.getClass(currentClass);
                if (classSym != null) {
                    FieldSymbol field = classSym.getField(fieldName);
                    if (field != null) {
                        mv.visitFieldInsn(PUTFIELD, currentClass, fieldName, typeToDescriptor(field.type));
                        return null;
                    }
                }
                throw new RuntimeException("Unknown field: " + fieldName + " in class " + currentClass);
            } else if (innerLValue instanceof JvmBasicParser.SimpleLValueContext simpleLValue) {
                // obj.field = value (where obj is a variable)
                String objName = simpleLValue.IDENTIFIER().getText();
                loadVariable(objName);      // Push object ref
                String objType = lastExprType;
                visit(ctx.expression());    // Push value

                ClassSymbol classSym = symbols.getClass(objType);
                if (classSym != null) {
                    FieldSymbol field = classSym.getField(fieldName);
                    if (field != null) {
                        mv.visitFieldInsn(PUTFIELD, objType, fieldName, typeToDescriptor(field.type));
                        return null;
                    }
                }
                throw new RuntimeException("Unknown field: " + fieldName + " in class " + objType);
            }
            return null;
        }

        // Regular assignment: evaluate RHS expression, then store
        visit(ctx.expression());
        visitLValueStore(ctx.lvalue());

        return null;
    }

    private String getArrayElementTypeFromVariable(String varName) {
        // Check in main locals
        if ("main".equals(currentMethod)) {
            LocalVar local = mainLocals.get(varName);
            if (local != null && local.type().endsWith("[]")) {
                return local.type().substring(0, local.type().length() - 2);
            }
        } else if (currentMethod != null) {
            FunctionSymbol func = symbols.getFunction(currentMethod);
            if (func != null) {
                VariableSymbol local = func.getLocal(varName);
                if (local != null && local.type.endsWith("[]")) {
                    return local.type.substring(0, local.type.length() - 2);
                }
            }
        }
        return "Integer";  // Default
    }

    @Override
    public Object visitReturnStatement(JvmBasicParser.ReturnStatementContext ctx) {
        if (ctx.expression() != null) {
            visit(ctx.expression());
            // Determine return type and use appropriate return instruction
            String returnType = "Void";
            if (currentClass != null) {
                // Look up method in current class
                ClassSymbol classSym = symbols.getClass(currentClass);
                if (classSym != null) {
                    FunctionSymbol method = classSym.getMethod(currentMethod);
                    if (method != null) {
                        returnType = method.returnType;
                    }
                }
            } else {
                // Look up in global functions
                FunctionSymbol func = symbols.getFunction(currentMethod);
                if (func != null) {
                    returnType = func.returnType;
                }
            }
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

        // Then block - enter scope
        enterScopeFrame("if_then");
        for (JvmBasicParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }
        exitScopeFrame(true);  // Reclaim slots
        mv.visitJumpInsn(GOTO, endLabel);

        // Else-if clauses
        mv.visitLabel(elseLabel);
        for (JvmBasicParser.ElseIfClauseContext elseIf : ctx.elseIfClause()) {
            Label nextElse = new Label();
            visit(elseIf.expression());
            mv.visitJumpInsn(IFEQ, nextElse);

            // Enter elseif scope
            enterScopeFrame("elseif");
            for (JvmBasicParser.StatementContext stmt : elseIf.statement()) {
                visit(stmt);
            }
            exitScopeFrame(true);  // Reclaim slots
            mv.visitJumpInsn(GOTO, endLabel);
            mv.visitLabel(nextElse);
        }

        // Else clause
        if (ctx.elseClause() != null) {
            enterScopeFrame("else");
            for (JvmBasicParser.StatementContext stmt : ctx.elseClause().statement()) {
                visit(stmt);
            }
            exitScopeFrame(true);  // Reclaim slots
        }

        mv.visitLabel(endLabel);
        return null;
    }

    @Override
    public Object visitWhileStatement(JvmBasicParser.WhileStatementContext ctx) {
        Label startLabel = new Label();
        Label endLabel = new Label();

        // Enter WHILE scope
        enterScopeFrame("while");

        // For while, continue should re-check condition
        pushLoop("while", startLabel, endLabel);

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

        popLoop();

        // Exit scope and reclaim slots
        exitScopeFrame(true);

        return null;
    }

    @Override
    public Object visitDoStatement(JvmBasicParser.DoStatementContext ctx) {
        // DO [WHILE|UNTIL expr] ... LOOP [WHILE|UNTIL expr]
        Label startLabel = new Label();
        Label endLabel = new Label();

        // Enter DO scope
        enterScopeFrame("do");

        // For do loop, continue should go to start (re-check condition)
        pushLoop("do", startLabel, endLabel);

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

        popLoop();

        // Exit scope and reclaim slots
        exitScopeFrame(true);

        return null;
    }

    @Override
    public Object visitForStatement(JvmBasicParser.ForStatementContext ctx) {
        // FOR i = start TO end [STEP step]
        //   statements
        // NEXT i

        String varName = ctx.IDENTIFIER(0).getText();

        // Enter FOR loop scope (will reclaim slots on exit)
        enterScopeFrame("for_" + varName);

        // Evaluate start value and store in loop variable
        visit(ctx.expression(0));  // start expression
        int slot = localVarSlot++;
        mv.visitVarInsn(ISTORE, slot);

        // Track the loop variable in scope
        addScopedVariable(varName, "Integer", slot);
        dynamicLocals.put(varName, new LocalVar(varName, "Integer", slot));  // For backward compat

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
        Label continueLabel = new Label();  // continue jumps here (increment)
        Label endLabel = new Label();       // exit/break jumps here

        // Register loop for exit/continue statements
        pushLoop("for", continueLabel, endLabel);

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

        // Continue label - continue jumps here to increment
        mv.visitLabel(continueLabel);

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

        // Pop loop context
        popLoop();

        // Exit scope and reclaim slots
        dynamicLocals.remove(varName);  // Clean up backward compat
        exitScopeFrame(true);  // Reclaim slots

        return null;
    }

    @Override
    public Object visitForEachStatement(JvmBasicParser.ForEachStatementContext ctx) {
        // FOR EACH item IN collection
        //   statements
        // NEXT
        //
        // For arrays, this compiles to:
        //   arr = <collection>
        //   i = 0
        //   while (i < arr.length)
        //     item = arr[i]
        //     <statements>
        //     i++

        String itemName = ctx.IDENTIFIER(0).getText();

        // Enter FOR EACH scope
        enterScopeFrame("foreach_" + itemName);

        // Evaluate and store the collection (array)
        visit(ctx.expression());
        String arrayType = lastExprType;  // e.g., "Integer[]"
        String elementType = getArrayElementType(arrayType);

        int arraySlot = localVarSlot++;
        mv.visitVarInsn(ASTORE, arraySlot);  // Store array reference

        // Initialize index counter i = 0
        int indexSlot = localVarSlot++;
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, indexSlot);

        // Allocate slot for loop variable
        int itemSlot = allocateSlot(elementType);
        // Track the loop variable in scope
        addScopedVariable(itemName, elementType, itemSlot);
        dynamicLocals.put(itemName, new LocalVar(itemName, elementType, itemSlot));  // For backward compat

        Label startLabel = new Label();
        Label continueLabel = new Label();  // continue jumps here (increment)
        Label endLabel = new Label();       // exit jumps here

        // For Each is treated as a FOR loop
        pushLoop("for", continueLabel, endLabel);

        mv.visitLabel(startLabel);

        // Condition: i < arr.length
        mv.visitVarInsn(ILOAD, indexSlot);
        mv.visitVarInsn(ALOAD, arraySlot);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPGE, endLabel);  // if i >= length, exit

        // Load current element: item = arr[i]
        mv.visitVarInsn(ALOAD, arraySlot);
        mv.visitVarInsn(ILOAD, indexSlot);
        emitArrayLoad(elementType);
        storeLocal(itemSlot, elementType);

        // Execute loop body
        for (var stmt : ctx.statement()) {
            visit(stmt);
        }

        // Continue label - continue jumps here to increment
        mv.visitLabel(continueLabel);

        // Increment index: i++
        mv.visitIincInsn(indexSlot, 1);

        mv.visitJumpInsn(GOTO, startLabel);
        mv.visitLabel(endLabel);

        popLoop();

        // Exit scope and reclaim slots
        dynamicLocals.remove(itemName);  // Clean up backward compat
        exitScopeFrame(true);  // Reclaim slots

        return null;
    }

    @Override
    public Object visitSelectStatement(JvmBasicParser.SelectStatementContext ctx) {
        // SELECT CASE expression
        //   CASE value1, value2
        //     statements
        //   CASE value3
        //     statements
        //   CASE ELSE
        //     statements
        // END SELECT
        //
        // Strategy: Evaluate the select expression, store it in a temp variable,
        // then generate a chain of if-elseif-else comparisons.

        Label endLabel = new Label();

        // Evaluate the select expression and store in a temp variable
        visit(ctx.expression());
        String selectType = lastExprType;
        int selectSlot = localVarSlot++;
        storeLocal(selectSlot, selectType);

        // Track the select context for EXIT SELECT
        pushLoop("select", endLabel, endLabel);  // both continue and break go to end

        // Process each CASE clause
        for (int i = 0; i < ctx.caseClause().size(); i++) {
            JvmBasicParser.CaseClauseContext caseCtx = ctx.caseClause(i);
            Label nextCaseLabel = new Label();

            // Each case can have multiple values: CASE 1, 2, 3
            JvmBasicParser.ExpressionListContext exprList = caseCtx.expressionList();
            int numValues = exprList.expression().size();

            if (numValues == 1) {
                // Single value case - simple comparison
                loadLocal(selectSlot, selectType);
                visit(exprList.expression(0));
                emitEqualityComparison(selectType, nextCaseLabel);
            } else {
                // Multiple values - any match should execute the case
                // OR logic: if val1 || val2 || val3 ...
                Label matchLabel = new Label();
                for (int j = 0; j < numValues; j++) {
                    loadLocal(selectSlot, selectType);
                    visit(exprList.expression(j));
                    emitEqualityJumpIfTrue(selectType, matchLabel);
                }
                // None matched, jump to next case
                mv.visitJumpInsn(GOTO, nextCaseLabel);
                mv.visitLabel(matchLabel);
            }

            // Execute case body
            for (JvmBasicParser.StatementContext stmt : caseCtx.statement()) {
                visit(stmt);
            }

            // Jump to end after executing case body
            mv.visitJumpInsn(GOTO, endLabel);

            mv.visitLabel(nextCaseLabel);
        }

        // Handle CASE ELSE if present
        if (ctx.caseElseClause() != null) {
            for (JvmBasicParser.StatementContext stmt : ctx.caseElseClause().statement()) {
                visit(stmt);
            }
        }

        mv.visitLabel(endLabel);
        popLoop();

        return null;
    }

    // Emit comparison that jumps to falseLabel if NOT equal
    private void emitEqualityComparison(String type, Label falseLabel) {
        if ("String".equalsIgnoreCase(type)) {
            // Use String.equals() for strings
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals",
                              "(Ljava/lang/Object;)Z", false);
            mv.visitJumpInsn(IFEQ, falseLabel);  // if result == 0 (false), jump
        } else if ("Integer".equalsIgnoreCase(type) || "Int".equalsIgnoreCase(type)) {
            mv.visitJumpInsn(IF_ICMPNE, falseLabel);  // if not equal, jump
        } else if ("Long".equalsIgnoreCase(type)) {
            mv.visitInsn(LCMP);
            mv.visitJumpInsn(IFNE, falseLabel);  // if not equal, jump
        } else if ("Float".equalsIgnoreCase(type)) {
            mv.visitInsn(FCMPL);
            mv.visitJumpInsn(IFNE, falseLabel);
        } else if ("Double".equalsIgnoreCase(type)) {
            mv.visitInsn(DCMPL);
            mv.visitJumpInsn(IFNE, falseLabel);
        } else {
            // Reference types - use equals()
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals",
                              "(Ljava/lang/Object;)Z", false);
            mv.visitJumpInsn(IFEQ, falseLabel);
        }
    }

    // Emit comparison that jumps to trueLabel if EQUAL
    private void emitEqualityJumpIfTrue(String type, Label trueLabel) {
        if ("String".equalsIgnoreCase(type)) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals",
                              "(Ljava/lang/Object;)Z", false);
            mv.visitJumpInsn(IFNE, trueLabel);  // if result != 0 (true), jump
        } else if ("Integer".equalsIgnoreCase(type) || "Int".equalsIgnoreCase(type)) {
            mv.visitJumpInsn(IF_ICMPEQ, trueLabel);  // if equal, jump
        } else if ("Long".equalsIgnoreCase(type)) {
            mv.visitInsn(LCMP);
            mv.visitJumpInsn(IFEQ, trueLabel);  // if equal, jump
        } else if ("Float".equalsIgnoreCase(type)) {
            mv.visitInsn(FCMPL);
            mv.visitJumpInsn(IFEQ, trueLabel);
        } else if ("Double".equalsIgnoreCase(type)) {
            mv.visitInsn(DCMPL);
            mv.visitJumpInsn(IFEQ, trueLabel);
        } else {
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals",
                              "(Ljava/lang/Object;)Z", false);
            mv.visitJumpInsn(IFNE, trueLabel);
        }
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
    public Object visitThisExpr(JvmBasicParser.ThisExprContext ctx) {
        // 'this' reference - load slot 0 which holds the object reference
        if (currentClass == null) {
            throw new RuntimeException("'this' can only be used inside a class method");
        }
        mv.visitVarInsn(ALOAD, 0);
        lastExprType = currentClass;
        return null;
    }

    // ========================================================================
    // Object Creation
    // ========================================================================

    @Override
    public Object visitNewObjectExpr(JvmBasicParser.NewObjectExprContext ctx) {
        // new ClassName(args...)
        String typeName = ctx.typeName().getText();

        // Check if this is a user-defined class
        ClassSymbol classSym = symbols.getClass(typeName);
        if (classSym != null) {
            // User-defined class: NEW, DUP, push args, INVOKESPECIAL <init>
            mv.visitTypeInsn(NEW, typeName);
            mv.visitInsn(DUP);

            // Build constructor descriptor and push arguments
            FunctionSymbol constructor = classSym.getMethod("New");
            StringBuilder descBuilder = new StringBuilder("(");
            if (ctx.argumentList() != null) {
                for (JvmBasicParser.ArgumentContext arg : ctx.argumentList().argument()) {
                    visit(arg.expression());
                }
            }
            if (constructor != null) {
                for (ParameterSymbol param : constructor.getParameters()) {
                    descBuilder.append(typeToDescriptor(param.type));
                }
            }
            descBuilder.append(")V");

            mv.visitMethodInsn(INVOKESPECIAL, typeName, "<init>", descBuilder.toString(), false);
            lastExprType = typeName;
            return null;
        }

        // Standard library / external class instantiation
        String internalName = typeName.replace(".", "/");
        mv.visitTypeInsn(NEW, internalName);
        mv.visitInsn(DUP);

        // For now, only support default constructor for external types
        if (ctx.argumentList() == null || ctx.argumentList().argument().isEmpty()) {
            mv.visitMethodInsn(INVOKESPECIAL, internalName, "<init>", "()V", false);
        } else {
            throw new RuntimeException("Constructor with arguments not yet supported for external type: " + typeName);
        }

        lastExprType = typeName;
        return null;
    }

    // ========================================================================
    // Array Operations
    // ========================================================================

    @Override
    public Object visitNewArrayExpr(JvmBasicParser.NewArrayExprContext ctx) {
        // new Integer[size]
        String elementType = ctx.typeName().getText();
        visit(ctx.expression());  // Push size onto stack

        // Determine array type and emit NEWARRAY or ANEWARRAY
        String desc = primitiveArrayType(elementType);
        if (desc != null) {
            // Primitive array: NEWARRAY
            mv.visitIntInsn(NEWARRAY, desc.charAt(0) == 'I' ? T_INT :
                           desc.charAt(0) == 'J' ? T_LONG :
                           desc.charAt(0) == 'F' ? T_FLOAT :
                           desc.charAt(0) == 'D' ? T_DOUBLE :
                           desc.charAt(0) == 'Z' ? T_BOOLEAN :
                           desc.charAt(0) == 'B' ? T_BYTE :
                           desc.charAt(0) == 'C' ? T_CHAR :
                           desc.charAt(0) == 'S' ? T_SHORT : T_INT);
        } else {
            // Reference array: ANEWARRAY
            mv.visitTypeInsn(ANEWARRAY, elementType.equals("String") ?
                            "java/lang/String" : elementType);
        }

        lastExprType = elementType + "[]";
        return null;
    }

    // Return primitive type descriptor, or null for reference types
    private String primitiveArrayType(String type) {
        return switch (type.toLowerCase()) {
            case "integer", "int" -> "I";
            case "long" -> "J";
            case "float" -> "F";
            case "double" -> "D";
            case "boolean" -> "Z";
            case "byte" -> "B";
            case "char" -> "C";
            case "short" -> "S";
            default -> null;
        };
    }

    @Override
    public Object visitIndexAccess(JvmBasicParser.IndexAccessContext ctx) {
        // arr[index] - load array element
        // The array reference is already on the stack from visiting the primary
        // Save the array type before visiting the index expression (which may change lastExprType)
        String arrayType = lastExprType;

        visit(ctx.expression());  // Push index

        // Determine element type from saved array type
        String elementType = getArrayElementType(arrayType);

        // Emit appropriate XALOAD instruction
        emitArrayLoad(elementType);
        lastExprType = elementType;
        return null;
    }

    private String getArrayElementType(String arrayType) {
        if (arrayType != null && arrayType.endsWith("[]")) {
            return arrayType.substring(0, arrayType.length() - 2);
        }
        return "Object";  // Default
    }

    private void emitArrayLoad(String elementType) {
        switch (elementType.toLowerCase()) {
            case "integer", "int" -> mv.visitInsn(IALOAD);
            case "long" -> mv.visitInsn(LALOAD);
            case "float" -> mv.visitInsn(FALOAD);
            case "double" -> mv.visitInsn(DALOAD);
            case "boolean", "byte" -> mv.visitInsn(BALOAD);
            case "char" -> mv.visitInsn(CALOAD);
            case "short" -> mv.visitInsn(SALOAD);
            default -> mv.visitInsn(AALOAD);  // Reference type
        }
    }

    private void emitArrayStore(String elementType) {
        switch (elementType.toLowerCase()) {
            case "integer", "int" -> mv.visitInsn(IASTORE);
            case "long" -> mv.visitInsn(LASTORE);
            case "float" -> mv.visitInsn(FASTORE);
            case "double" -> mv.visitInsn(DASTORE);
            case "boolean", "byte" -> mv.visitInsn(BASTORE);
            case "char" -> mv.visitInsn(CASTORE);
            case "short" -> mv.visitInsn(SASTORE);
            default -> mv.visitInsn(AASTORE);  // Reference type
        }
    }

    @Override
    public Object visitIdentifierExpr(JvmBasicParser.IdentifierExprContext ctx) {
        String name = ctx.IDENTIFIER().getText();

        // IMPORTANT: Check for variables FIRST before namespace matching
        // This allows variables named 'json', 'file', etc. to shadow namespace names
        LocalVar scopedVar = lookupScopedVariable(name);
        if (scopedVar != null) {
            loadLocal(scopedVar.slot(), scopedVar.type());
            return null;
        }
        // Also check dynamic locals (for main method variables)
        LocalVar dynamicVar = dynamicLocals.get(name);
        if (dynamicVar != null) {
            loadLocal(dynamicVar.slot(), dynamicVar.type());
            return null;
        }

        // Check for built-in namespaces (only if not a variable)
        if ("Console".equalsIgnoreCase(name)) {
            // Console is a pseudo-namespace, don't load anything
            // The method call handler will deal with it
            pendingNamespace = "Console";
            return null;
        }
        if ("Math".equalsIgnoreCase(name)) {
            // Math namespace - maps to com.jvmbasic.runtime.BasicMath
            pendingNamespace = "Math";
            return null;
        }
        if ("Str".equalsIgnoreCase(name)) {
            // Str namespace - maps to com.jvmbasic.runtime.BasicStr
            pendingNamespace = "Str";
            return null;
        }
        if ("Regex".equalsIgnoreCase(name)) {
            // Regex namespace - maps to com.jvmbasic.runtime.BasicRegex
            pendingNamespace = "Regex";
            return null;
        }
        if ("File".equalsIgnoreCase(name)) {
            // File namespace - maps to com.jvmbasic.runtime.BasicFile
            pendingNamespace = "File";
            return null;
        }
        if ("Http".equalsIgnoreCase(name)) {
            // Http namespace - maps to com.jvmbasic.runtime.BasicHttp
            pendingNamespace = "Http";
            return null;
        }
        if ("Json".equalsIgnoreCase(name)) {
            // Json namespace - maps to com.jvmbasic.runtime.BasicJson
            pendingNamespace = "Json";
            return null;
        }
        if ("Db".equalsIgnoreCase(name)) {
            // Db namespace - maps to com.jvmbasic.runtime.BasicDb
            pendingNamespace = "Db";
            return null;
        }
        if ("BigInt".equalsIgnoreCase(name)) {
            // BigInt namespace - maps to java.math.BigInteger
            pendingNamespace = "BigInt";
            return null;
        }
        if ("Decimal".equalsIgnoreCase(name)) {
            // Decimal namespace - maps to java.math.BigDecimal
            pendingNamespace = "Decimal";
            return null;
        }
        // Check if this is a function name (will be handled by FunctionCall postfixOp)
        if (symbols.getFunction(name) != null) {
            pendingFunctionName = name;
            return null;
        }
        // Load variable value (fallback for variables not found in scope checks above)
        loadVariable(name);
        return null;
    }

    @Override
    public Object visitBigIntNamespaceExpr(JvmBasicParser.BigIntNamespaceExprContext ctx) {
        // BigInteger/BigInt used as namespace for method calls like BigInteger.FromString()
        pendingNamespace = "BigInt";
        return null;
    }

    @Override
    public Object visitDecimalNamespaceExpr(JvmBasicParser.DecimalNamespaceExprContext ctx) {
        // Decimal used as namespace for method calls like Decimal.FromString()
        pendingNamespace = "Decimal";
        return null;
    }

    // Track namespace for method calls
    private String pendingNamespace = null;
    // Track function name for function calls
    private String pendingFunctionName = null;

    @Override
    public Object visitMethodCall(JvmBasicParser.MethodCallContext ctx) {
        // This handles Namespace.Method() calls like Console.WriteLine()
        // and instance method calls like alice.Greet()
        String methodName = ctx.memberName().getText();

        // Handle instance method calls on user-defined classes
        // The object is already on stack from visiting primaryExpr, and lastObjectType has its type
        if (lastObjectType != null) {
            ClassSymbol classSym = symbols.getClass(lastObjectType);
            if (classSym != null) {
                FunctionSymbol methodSym = classSym.getMethod(methodName);
                if (methodSym != null) {
                    String objectType = lastObjectType;
                    lastObjectType = null;

                    // Push arguments onto stack (object is already on stack)
                    if (ctx.argumentList() != null) {
                        for (JvmBasicParser.ArgumentContext arg : ctx.argumentList().argument()) {
                            visit(arg.expression());
                        }
                    }

                    // Build method descriptor and call INVOKEVIRTUAL
                    String descriptor = buildMethodDescriptor(methodSym);
                    mv.visitMethodInsn(INVOKEVIRTUAL, objectType, methodName, descriptor, false);
                    lastExprType = methodSym.returnType;
                    return null;
                }
            }
        }

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
            } else if ("ReadLine".equalsIgnoreCase(methodName)) {
                // Console.ReadLine() -> new Scanner(System.in).nextLine()
                mv.visitTypeInsn(NEW, "java/util/Scanner");
                mv.visitInsn(DUP);
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
                mv.visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);
                lastExprType = "String";
                return null;
            }
        }

        // Handle Math namespace - calls com.jvmbasic.runtime.BasicMath
        if ("Math".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            return handleMathCall(methodName, ctx.argumentList());
        }

        // Handle Str namespace - calls com.jvmbasic.runtime.BasicStr
        if ("Str".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            return handleStrCall(methodName, ctx.argumentList());
        }

        if ("Regex".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            return handleRegexCall(methodName, ctx.argumentList());
        }

        if ("File".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            return handleFileCall(methodName, ctx.argumentList());
        }

        // Handle Http namespace - calls com.jvmbasic.runtime.BasicHttp
        if ("Http".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            return handleHttpCall(methodName, ctx.argumentList());
        }

        // Handle Json namespace - calls com.jvmbasic.runtime.BasicJson
        if ("Json".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            return handleJsonCall(methodName, ctx.argumentList());
        }

        // Handle Db namespace - calls com.jvmbasic.runtime.BasicDb
        if ("Db".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            return handleDbCall(methodName, ctx.argumentList());
        }

        // Handle BigInt namespace - java.math.BigInteger factory and utility methods
        if ("BigInt".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            return handleBigIntCall(methodName, ctx.argumentList());
        }

        // Handle Decimal namespace - java.math.BigDecimal factory and utility methods
        if ("Decimal".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            return handleDecimalCall(methodName, ctx.argumentList());
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

    // Handle Math.* calls - maps to com.jvmbasic.runtime.BasicMath static methods
    private Object handleMathCall(String methodName, JvmBasicParser.ArgumentListContext argList) {
        String runtimeClass = "com/jvmbasic/runtime/BasicMath";
        int argCount = argList != null ? argList.argument().size() : 0;

        // Visit arguments and coerce to double (Math functions expect doubles)
        if (argList != null) {
            for (JvmBasicParser.ArgumentContext arg : argList.argument()) {
                visit(arg.expression());
                // Coerce to double if needed
                coerceToDouble();
            }
        }

        // Map method names to their descriptors
        // Most math functions take and return doubles
        switch (methodName) {
            // Constants (no args)
            case "Pi" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Pi", "()D", false);
                lastExprType = "Double";
            }
            case "E" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "E", "()D", false);
                lastExprType = "Double";
            }

            // Single double argument functions
            case "Sqrt", "Cbrt", "Exp", "Log", "Log10", "Log2",
                 "Sin", "Cos", "Tan", "Asin", "Acos", "Atan",
                 "Sinh", "Cosh", "Tanh", "Floor", "Ceil",
                 "ToRadians", "ToDegrees", "Truncate" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(D)D", false);
                lastExprType = "Double";
            }

            // Abs - works with int, long, float, double
            case "Abs" -> {
                // For simplicity, assume double
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Abs", "(D)D", false);
                lastExprType = "Double";
            }

            // Sign - returns int
            case "Sign" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Sign", "(D)I", false);
                lastExprType = "Integer";
            }

            // Pow(base, exp)
            case "Pow" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Pow", "(DD)D", false);
                lastExprType = "Double";
            }

            // Atan2(y, x)
            case "Atan2" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Atan2", "(DD)D", false);
                lastExprType = "Double";
            }

            // Hypot(x, y)
            case "Hypot" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Hypot", "(DD)D", false);
                lastExprType = "Double";
            }

            // Lerp(a, b, t)
            case "Lerp" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Lerp", "(DDD)D", false);
                lastExprType = "Double";
            }

            // Round - returns long
            case "Round" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Round", "(D)J", false);
                lastExprType = "Long";
            }

            // Min/Max with 2 double args
            case "Min", "Max" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(DD)D", false);
                lastExprType = "Double";
            }

            // Clamp(value, min, max)
            case "Clamp" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Clamp", "(DDD)D", false);
                lastExprType = "Double";
            }

            // Random functions
            case "Random" -> {
                if (argCount == 0) {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Random", "()D", false);
                    lastExprType = "Double";
                } else if (argCount == 1) {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Random", "(I)I", false);
                    lastExprType = "Integer";
                } else {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Random", "(II)I", false);
                    lastExprType = "Integer";
                }
            }

            case "Randomize" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Randomize", "(J)V", false);
                lastExprType = "void";
            }

            // Boolean checks
            case "IsNaN", "IsInfinite", "IsFinite" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(D)Z", false);
                lastExprType = "Boolean";
            }

            // Epsilon comparison
            case "ApproxEqual" -> {
                if (argCount == 2) {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ApproxEqual", "(DD)Z", false);
                } else if (argCount == 3) {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ApproxEqual", "(DDD)Z", false);
                } else {
                    throw new RuntimeException("Math.ApproxEqual requires 2 or 3 arguments");
                }
                lastExprType = "Boolean";
            }

            case "ApproxEqualRelative" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ApproxEqualRelative", "(DDD)Z", false);
                lastExprType = "Boolean";
            }

            // Machine epsilon constants
            case "FloatEpsilon" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "FloatEpsilon", "()F", false);
                lastExprType = "Float";
            }

            case "DoubleEpsilon" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "DoubleEpsilon", "()D", false);
                lastExprType = "Double";
            }

            default -> throw new RuntimeException("Unknown Math function: " + methodName);
        }

        return null;
    }

    // Handle Str.* calls - maps to com.jvmbasic.runtime.BasicStr static methods
    private Object handleStrCall(String methodName, JvmBasicParser.ArgumentListContext argList) {
        String runtimeClass = "com/jvmbasic/runtime/BasicStr";
        int argCount = argList != null ? argList.argument().size() : 0;

        // Visit arguments first
        if (argList != null) {
            for (JvmBasicParser.ArgumentContext arg : argList.argument()) {
                visit(arg.expression());
            }
        }

        switch (methodName) {
            // String properties - takes String, returns int/boolean
            case "Length" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Length", "(Ljava/lang/String;)I", false);
                lastExprType = "Integer";
            }
            case "IsEmpty", "IsBlank" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            // Case conversion - String -> String
            case "ToUpper", "ToLower", "Capitalize", "Title", "Trim", "TrimLeft", "TrimRight", "Reverse" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Substring operations - String, int -> String
            case "Substring" -> {
                if (argCount == 2) {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Substring", "(Ljava/lang/String;I)Ljava/lang/String;", false);
                } else {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Substring", "(Ljava/lang/String;II)Ljava/lang/String;", false);
                }
                lastExprType = "String";
            }

            case "Left", "Right" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;I)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            case "Mid" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Mid", "(Ljava/lang/String;II)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Search - returns int
            case "IndexOf" -> {
                if (argCount == 2) {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "IndexOf", "(Ljava/lang/String;Ljava/lang/String;)I", false);
                } else {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "IndexOf", "(Ljava/lang/String;Ljava/lang/String;I)I", false);
                }
                lastExprType = "Integer";
            }

            case "LastIndexOf" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "LastIndexOf", "(Ljava/lang/String;Ljava/lang/String;)I", false);
                lastExprType = "Integer";
            }

            // Search - returns boolean
            case "Contains", "StartsWith", "EndsWith" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            // Replace - String, String, String -> String
            case "Replace", "ReplaceFirst", "ReplaceAll" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Repeat - String, int -> String
            case "Repeat" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Repeat", "(Ljava/lang/String;I)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Padding - String, int -> String
            case "PadLeft", "PadRight", "Center" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;I)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Character operations
            case "CharAt" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "CharAt", "(Ljava/lang/String;I)C", false);
                lastExprType = "Char";
            }

            case "Asc" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Asc", "(Ljava/lang/String;)I", false);
                lastExprType = "Integer";
            }

            case "Chr" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Chr", "(I)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Comparison
            case "Compare" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Compare", "(Ljava/lang/String;Ljava/lang/String;)I", false);
                lastExprType = "Integer";
            }

            case "CompareIgnoreCase" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "CompareIgnoreCase", "(Ljava/lang/String;Ljava/lang/String;)I", false);
                lastExprType = "Integer";
            }

            case "Equals", "EqualsIgnoreCase" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            // Regex
            case "Matches" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Matches", "(Ljava/lang/String;Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            // Split - returns String[]
            case "Split", "SplitRegex" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;", false);
                lastExprType = "String[]";
            }

            // Join - String[], String -> String
            case "Join" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Join", "([Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Conversion to types
            case "ToInt" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ToInt", "(Ljava/lang/String;)I", false);
                lastExprType = "Integer";
            }

            case "ToLong" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ToLong", "(Ljava/lang/String;)J", false);
                lastExprType = "Long";
            }

            case "ToDouble" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ToDouble", "(Ljava/lang/String;)D", false);
                lastExprType = "Double";
            }

            case "ToBoolean" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ToBoolean", "(Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            // Conversion from types
            case "FromInt" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "FromInt", "(I)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            case "FromLong" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "FromLong", "(J)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            case "FromDouble" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "FromDouble", "(D)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            case "FromBoolean" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "FromBoolean", "(Z)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Additional string operations
            case "CountOccurrences" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "CountOccurrences", "(Ljava/lang/String;Ljava/lang/String;)I", false);
                lastExprType = "Integer";
            }

            case "PadZero" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "PadZero", "(Ljava/lang/String;I)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            case "SwapCase" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SwapCase", "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Character type checks - returns boolean
            case "IsAlpha", "IsDigit", "IsAlphanumeric", "IsWhitespace", "IsUpperCase", "IsLowerCase", "IsNumeric" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            // Split variants - returns String[]
            case "SplitFirst", "SplitLast" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;", false);
                lastExprType = "String[]";
            }

            case "SplitLines", "Words" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)[Ljava/lang/String;", false);
                lastExprType = "String[]";
            }

            // Prefix/Suffix operations - String, String -> String
            case "RemovePrefix", "RemoveSuffix", "EnsurePrefix", "EnsureSuffix" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Word operations - String -> String
            case "CapitalizeFirst", "LowercaseFirst", "Shuffle" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            case "WordCount" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "WordCount", "(Ljava/lang/String;)I", false);
                lastExprType = "Integer";
            }

            // Text wrapping
            case "Wrap" -> {
                if (argCount == 2) {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Wrap", "(Ljava/lang/String;I)Ljava/lang/String;", false);
                } else {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Wrap", "(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;", false);
                }
                lastExprType = "String";
            }

            case "InsertEvery" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "InsertEvery", "(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // HTML/Web utilities - String -> String
            case "NewlinesToBreaks", "EscapeHtml", "UnescapeHtml", "EscapeJson", "Slugify" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Whitespace operations - String -> String
            case "RemoveWhitespace", "CollapseSpaces", "NormalizeWhitespace" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            case "ExpandTabs" -> {
                if (argCount == 1) {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ExpandTabs", "(Ljava/lang/String;)Ljava/lang/String;", false);
                } else {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ExpandTabs", "(Ljava/lang/String;I)Ljava/lang/String;", false);
                }
                lastExprType = "String";
            }

            // Truncation
            case "Truncate" -> {
                if (argCount == 2) {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Truncate", "(Ljava/lang/String;I)Ljava/lang/String;", false);
                } else {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Truncate", "(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;", false);
                }
                lastExprType = "String";
            }

            // Extraction/Filtering - String -> String
            case "OnlyDigits", "OnlyLetters", "OnlyAlphanumeric" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Case conversion utilities - String -> String
            case "ToSnakeCase", "ToKebabCase", "ToCamelCase", "ToPascalCase" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            default -> throw new RuntimeException("Unknown Str function: " + methodName);
        }

        return null;
    }

    private Object handleRegexCall(String methodName, JvmBasicParser.ArgumentListContext argList) {
        String runtimeClass = "com/jvmbasic/runtime/BasicRegex";
        int argCount = argList != null ? argList.argument().size() : 0;

        // Visit arguments first
        if (argList != null) {
            for (JvmBasicParser.ArgumentContext arg : argList.argument()) {
                visit(arg.expression());
            }
        }

        switch (methodName) {
            // Pattern matching - returns boolean
            case "IsMatch", "Contains", "StartsWith", "EndsWith" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            // Search - returns String
            case "Find" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Find", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Search - returns int
            case "FindIndex", "Count" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;Ljava/lang/String;)I", false);
                lastExprType = "Integer";
            }

            // Search - returns String[]
            case "FindAll" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "FindAll", "(Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;", false);
                lastExprType = "String[]";
            }

            // Capture groups
            case "Group" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Group", "(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            case "Groups" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Groups", "(Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;", false);
                lastExprType = "String[]";
            }

            // Replace - returns String
            case "ReplaceFirst", "ReplaceAll", "Replace" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            case "Remove" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Remove", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Split - returns String[]
            case "Split" -> {
                if (argCount == 2) {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Split", "(Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;", false);
                } else {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Split", "(Ljava/lang/String;Ljava/lang/String;I)[Ljava/lang/String;", false);
                }
                lastExprType = "String[]";
            }

            // Utilities
            case "Escape" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Escape", "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            case "IsValidPattern" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "IsValidPattern", "(Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            // Common pattern helpers - returns boolean
            case "IsEmail", "IsUrl", "IsIPv4", "IsDigitsOnly", "IsLettersOnly", "IsAlphanumericOnly" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            // Extract helpers - returns String[]
            case "ExtractNumbers", "ExtractWords" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)[Ljava/lang/String;", false);
                lastExprType = "String[]";
            }

            default -> throw new RuntimeException("Unknown Regex function: " + methodName);
        }

        return null;
    }

    private Object handleFileCall(String methodName, JvmBasicParser.ArgumentListContext argList) {
        String runtimeClass = "com/jvmbasic/runtime/BasicFile";
        int argCount = argList != null ? argList.argument().size() : 0;

        // Visit arguments first
        if (argList != null) {
            for (JvmBasicParser.ArgumentContext arg : argList.argument()) {
                visit(arg.expression());
            }
        }

        switch (methodName) {
            // File reading - returns String
            case "ReadAllText" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ReadAllText", "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // File reading - returns String[]
            case "ReadAllLines" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ReadAllLines", "(Ljava/lang/String;)[Ljava/lang/String;", false);
                lastExprType = "String[]";
            }

            // File reading - returns byte[]
            case "ReadAllBytes" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ReadAllBytes", "(Ljava/lang/String;)[B", false);
                lastExprType = "byte[]";
            }

            // File writing - returns boolean
            case "WriteAllText" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "WriteAllText", "(Ljava/lang/String;Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            case "WriteAllLines" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "WriteAllLines", "(Ljava/lang/String;[Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            case "WriteAllBytes" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "WriteAllBytes", "(Ljava/lang/String;[B)Z", false);
                lastExprType = "Boolean";
            }

            case "AppendAllText" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "AppendAllText", "(Ljava/lang/String;Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            case "AppendAllLines" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "AppendAllLines", "(Ljava/lang/String;[Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            // File operations - returns boolean
            case "Exists", "IsFile", "IsDirectory" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            case "Delete" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Delete", "(Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            case "Copy", "Move" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            case "CreateDirectory" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "CreateDirectory", "(Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            // File info - returns long
            case "Size" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Size", "(Ljava/lang/String;)J", false);
                lastExprType = "Long";
            }

            // File info - returns String
            case "GetFileName", "GetExtension", "GetFileNameWithoutExtension", "GetDirectory", "GetFullPath" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            case "Combine" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Combine", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Directory listing - returns String[]
            case "ListFiles", "ListDirectories", "ListAll" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)[Ljava/lang/String;", false);
                lastExprType = "String[]";
            }

            case "ListFilesWithPattern" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ListFilesWithPattern", "(Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;", false);
                lastExprType = "String[]";
            }

            // Temp files - returns String
            case "CreateTempFile" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "CreateTempFile", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            case "GetTempDirectory", "GetCurrentDirectory", "GetHomeDirectory" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "()Ljava/lang/String;", false);
                lastExprType = "String";
            }

            default -> throw new RuntimeException("Unknown File function: " + methodName);
        }

        return null;
    }

    // Handle Http.* calls - maps to com.jvmbasic.runtime.BasicHttp static methods
    private Object handleHttpCall(String methodName, JvmBasicParser.ArgumentListContext argList) {
        String runtimeClass = "com/jvmbasic/runtime/BasicHttp";
        int argCount = argList != null ? argList.argument().size() : 0;

        // Visit arguments first
        if (argList != null) {
            for (JvmBasicParser.ArgumentContext arg : argList.argument()) {
                visit(arg.expression());
            }
        }

        switch (methodName) {
            // HTTP request methods - return String
            case "Get" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Get", "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "Post" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Post", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "Put" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Put", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "Patch" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Patch", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "Delete" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Delete", "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "Head" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Head", "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Convenience methods - return String
            case "GetJson" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetJson", "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "PostJson" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "PostJson", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Response info - return int
            case "GetStatus" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetStatus", "()I", false);
                lastExprType = "Integer";
            }

            // Response header - return String
            case "GetResponseHeader" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetResponseHeader", "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Status checks - return boolean
            case "IsSuccess", "IsClientError", "IsServerError" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "()Z", false);
                lastExprType = "Boolean";
            }

            // Request configuration - void
            case "SetHeader" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetHeader", "(Ljava/lang/String;Ljava/lang/String;)V", false);
                lastExprType = "void";
            }
            case "ClearHeaders" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ClearHeaders", "()V", false);
                lastExprType = "void";
            }
            case "SetTimeout" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetTimeout", "(I)V", false);
                lastExprType = "void";
            }
            case "SetBasicAuth" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetBasicAuth", "(Ljava/lang/String;Ljava/lang/String;)V", false);
                lastExprType = "void";
            }
            case "SetBearerToken" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetBearerToken", "(Ljava/lang/String;)V", false);
                lastExprType = "void";
            }

            // URL utilities - return String
            case "UrlEncode", "UrlDecode" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // Download - return boolean
            case "Download" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Download", "(Ljava/lang/String;Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            default -> throw new RuntimeException("Unknown Http function: " + methodName);
        }

        return null;
    }

    // Handle Json.* calls - maps to com.jvmbasic.runtime.BasicJson static methods
    private Object handleJsonCall(String methodName, JvmBasicParser.ArgumentListContext argList) {
        String runtimeClass = "com/jvmbasic/runtime/BasicJson";
        int argCount = argList != null ? argList.argument().size() : 0;

        // Visit arguments first
        if (argList != null) {
            for (JvmBasicParser.ArgumentContext arg : argList.argument()) {
                visit(arg.expression());
            }
        }

        switch (methodName) {
            // JSON creation - return String
            case "Create" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Create", "()Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "CreateArray" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "CreateArray", "()Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // JSON access - return String
            case "Get" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Get", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "GetPath" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetPath", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // JSON access - return int
            case "GetInt" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetInt", "(Ljava/lang/String;Ljava/lang/String;)I", false);
                lastExprType = "Integer";
            }

            // JSON access - return double
            case "GetDouble" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetDouble", "(Ljava/lang/String;Ljava/lang/String;)D", false);
                lastExprType = "Double";
            }

            // JSON access - return boolean
            case "GetBool" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetBool", "(Ljava/lang/String;Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }
            case "Has" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Has", "(Ljava/lang/String;Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            // JSON array length - return int
            case "Length" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Length", "(Ljava/lang/String;)I", false);
                lastExprType = "Integer";
            }

            // JSON keys - return String[]
            case "Keys" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Keys", "(Ljava/lang/String;)[Ljava/lang/String;", false);
                lastExprType = "String[]";
            }

            // JSON modification - return String
            case "Set" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Set", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "SetInt" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetInt", "(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "SetDouble" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetDouble", "(Ljava/lang/String;Ljava/lang/String;D)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "SetBool" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetBool", "(Ljava/lang/String;Ljava/lang/String;Z)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "SetJson" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetJson", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "Remove" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Remove", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // JSON array operations - return String
            case "Push" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Push", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "PushJson" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "PushJson", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // JSON formatting - return String
            case "Pretty" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Pretty", "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "Minify" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Minify", "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }

            // JSON validation - return boolean
            case "IsValid", "IsObject", "IsArray" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, methodName, "(Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            default -> throw new RuntimeException("Unknown Json function: " + methodName);
        }

        return null;
    }

    // Handle Db.* calls - maps to com.jvmbasic.runtime.BasicDb static methods
    private Object handleDbCall(String methodName, JvmBasicParser.ArgumentListContext argList) {
        String runtimeClass = "com/jvmbasic/runtime/BasicDb";
        int argCount = argList != null ? argList.argument().size() : 0;

        // Visit arguments first
        if (argList != null) {
            for (JvmBasicParser.ArgumentContext arg : argList.argument()) {
                visit(arg.expression());
            }
        }

        switch (methodName) {
            // Connection management - return boolean
            case "Connect" -> {
                if (argCount == 1) {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Connect", "(Ljava/lang/String;)Z", false);
                } else if (argCount == 3) {
                    mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Connect", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z", false);
                }
                lastExprType = "Boolean";
            }
            case "IsConnected" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "IsConnected", "()Z", false);
                lastExprType = "Boolean";
            }

            // Connection management - void
            case "Close" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Close", "()V", false);
                lastExprType = "void";
            }

            // Simple query execution - return String[][]
            case "Query" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Query", "(Ljava/lang/String;)[[Ljava/lang/String;", false);
                lastExprType = "String[][]";
            }

            // Simple statement execution - return int
            case "Execute" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Execute", "(Ljava/lang/String;)I", false);
                lastExprType = "Integer";
            }

            // Execute any SQL - return boolean
            case "ExecuteAny" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ExecuteAny", "(Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            // Parameterized queries - return boolean
            case "Prepare" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Prepare", "(Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }
            case "SetString" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetString", "(ILjava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }
            case "SetInt" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetInt", "(II)Z", false);
                lastExprType = "Boolean";
            }
            case "SetLong" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetLong", "(IJ)Z", false);
                lastExprType = "Boolean";
            }
            case "SetFloat" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetFloat", "(IF)Z", false);
                lastExprType = "Boolean";
            }
            case "SetDouble" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetDouble", "(ID)Z", false);
                lastExprType = "Boolean";
            }
            case "SetNull" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "SetNull", "(I)Z", false);
                lastExprType = "Boolean";
            }
            case "ClearParameters" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ClearParameters", "()Z", false);
                lastExprType = "Boolean";
            }

            // Parameterized query execution - return String[][]
            case "ExecuteQuery" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ExecuteQuery", "()[[Ljava/lang/String;", false);
                lastExprType = "String[][]";
            }

            // Parameterized update execution - return int
            case "ExecuteUpdate" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ExecuteUpdate", "()I", false);
                lastExprType = "Integer";
            }

            // Close prepared statement - void
            case "CloseStmt" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "CloseStmt", "()V", false);
                lastExprType = "void";
            }

            // Transaction management - return boolean
            case "BeginTransaction" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "BeginTransaction", "()Z", false);
                lastExprType = "Boolean";
            }
            case "Commit" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Commit", "()Z", false);
                lastExprType = "Boolean";
            }
            case "Rollback" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Rollback", "()Z", false);
                lastExprType = "Boolean";
            }

            // Utility functions
            case "GetLastInsertId" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetLastInsertId", "()J", false);
                lastExprType = "Long";
            }
            case "Escape" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "Escape", "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "GetTables" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetTables", "()[Ljava/lang/String;", false);
                lastExprType = "String[]";
            }
            case "GetColumns" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetColumns", "(Ljava/lang/String;)[Ljava/lang/String;", false);
                lastExprType = "String[]";
            }

            // Cursor-based query iteration (avoids NEXT keyword conflict)
            case "QueryCursor" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "QueryCursor", "(Ljava/lang/String;)V", false);
                lastExprType = "void";
            }
            case "NextRow" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "NextRow", "()Z", false);
                lastExprType = "Boolean";
            }
            case "ExecuteQueryCursor" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "ExecuteQueryCursor", "()V", false);
                lastExprType = "void";
            }
            case "CloseResults" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "CloseResults", "()V", false);
                lastExprType = "void";
            }

            // Row value getters (for cursor iteration)
            case "GetString" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetString", "(Ljava/lang/String;)Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "GetInt" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetInt", "(Ljava/lang/String;)I", false);
                lastExprType = "Integer";
            }
            case "GetLong" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetLong", "(Ljava/lang/String;)J", false);
                lastExprType = "Long";
            }
            case "GetFloat" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetFloat", "(Ljava/lang/String;)F", false);
                lastExprType = "Float";
            }
            case "GetDouble" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetDouble", "(Ljava/lang/String;)D", false);
                lastExprType = "Double";
            }
            case "GetBool" -> {
                mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "GetBool", "(Ljava/lang/String;)Z", false);
                lastExprType = "Boolean";
            }

            default -> throw new RuntimeException("Unknown Db function: " + methodName);
        }

        return null;
    }

    // Handle BigInt.* calls - maps to java.math.BigInteger factory and instance methods
    private Object handleBigIntCall(String methodName, JvmBasicParser.ArgumentListContext argList) {
        int argCount = argList != null ? argList.argument().size() : 0;

        switch (methodName) {
            // Factory methods - create BigInteger from various sources
            case "FromString" -> {
                // BigInt.FromString("123456789012345678901234567890")
                if (argCount != 1) throw new RuntimeException("BigInt.FromString requires 1 argument");
                mv.visitTypeInsn(NEW, "java/math/BigInteger");
                mv.visitInsn(DUP);
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKESPECIAL, "java/math/BigInteger", "<init>", "(Ljava/lang/String;)V", false);
                lastExprType = "BigInteger";
            }
            case "FromLong" -> {
                // BigInt.FromLong(9999999999L)
                if (argCount != 1) throw new RuntimeException("BigInt.FromLong requires 1 argument");
                visit(argList.argument(0).expression());
                // Coerce to long if needed
                if ("Integer".equals(lastExprType)) {
                    mv.visitInsn(I2L);
                }
                mv.visitMethodInsn(INVOKESTATIC, "java/math/BigInteger", "valueOf", "(J)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "FromInt" -> {
                // BigInt.FromInt(42)
                if (argCount != 1) throw new RuntimeException("BigInt.FromInt requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitInsn(I2L);
                mv.visitMethodInsn(INVOKESTATIC, "java/math/BigInteger", "valueOf", "(J)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }

            // Constants
            case "Zero" -> {
                mv.visitFieldInsn(GETSTATIC, "java/math/BigInteger", "ZERO", "Ljava/math/BigInteger;");
                lastExprType = "BigInteger";
            }
            case "One" -> {
                mv.visitFieldInsn(GETSTATIC, "java/math/BigInteger", "ONE", "Ljava/math/BigInteger;");
                lastExprType = "BigInteger";
            }
            case "Ten" -> {
                mv.visitFieldInsn(GETSTATIC, "java/math/BigInteger", "TEN", "Ljava/math/BigInteger;");
                lastExprType = "BigInteger";
            }

            // Arithmetic operations (take two BigIntegers, return BigInteger)
            case "Add" -> {
                // BigInt.Add(a, b) -> a.add(b)
                if (argCount != 2) throw new RuntimeException("BigInt.Add requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "add", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "Subtract" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.Subtract requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "subtract", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "Multiply" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.Multiply requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "multiply", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "Divide" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.Divide requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "divide", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "Mod", "Remainder" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.Mod requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "mod", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "Pow" -> {
                // BigInt.Pow(base, exponent) - exponent is int
                if (argCount != 2) throw new RuntimeException("BigInt.Pow requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                // Second arg should be int for pow
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "pow", "(I)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "Gcd" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.Gcd requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "gcd", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "Max" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.Max requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "max", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "Min" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.Min requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "min", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }

            // Unary operations (take one BigInteger, return BigInteger)
            case "Abs" -> {
                if (argCount != 1) throw new RuntimeException("BigInt.Abs requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "abs", "()Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "Negate" -> {
                if (argCount != 1) throw new RuntimeException("BigInt.Negate requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "negate", "()Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "Sqrt" -> {
                if (argCount != 1) throw new RuntimeException("BigInt.Sqrt requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "sqrt", "()Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }

            // Comparison (returns int: -1, 0, or 1)
            case "Compare" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.Compare requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "compareTo", "(Ljava/math/BigInteger;)I", false);
                lastExprType = "Integer";
            }
            case "Equals" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.Equals requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "equals", "(Ljava/lang/Object;)Z", false);
                lastExprType = "Boolean";
            }

            // Sign (returns int: -1, 0, or 1)
            case "Signum" -> {
                if (argCount != 1) throw new RuntimeException("BigInt.Signum requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "signum", "()I", false);
                lastExprType = "Integer";
            }

            // Bit operations
            case "BitLength" -> {
                if (argCount != 1) throw new RuntimeException("BigInt.BitLength requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "bitLength", "()I", false);
                lastExprType = "Integer";
            }
            case "BitCount" -> {
                if (argCount != 1) throw new RuntimeException("BigInt.BitCount requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "bitCount", "()I", false);
                lastExprType = "Integer";
            }
            case "And" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.And requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "and", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "Or" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.Or requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "or", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "Xor" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.Xor requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "xor", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "Not" -> {
                if (argCount != 1) throw new RuntimeException("BigInt.Not requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "not", "()Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "ShiftLeft" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.ShiftLeft requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "shiftLeft", "(I)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "ShiftRight" -> {
                if (argCount != 2) throw new RuntimeException("BigInt.ShiftRight requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "shiftRight", "(I)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }

            // Conversion methods
            case "ToLong" -> {
                if (argCount != 1) throw new RuntimeException("BigInt.ToLong requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "longValue", "()J", false);
                lastExprType = "Long";
            }
            case "ToInt" -> {
                if (argCount != 1) throw new RuntimeException("BigInt.ToInt requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "intValue", "()I", false);
                lastExprType = "Integer";
            }
            case "ToDouble" -> {
                if (argCount != 1) throw new RuntimeException("BigInt.ToDouble requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "doubleValue", "()D", false);
                lastExprType = "Double";
            }
            case "ToString" -> {
                if (argCount == 1) {
                    visit(argList.argument(0).expression());
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "toString", "()Ljava/lang/String;", false);
                } else if (argCount == 2) {
                    // ToString with radix
                    visit(argList.argument(0).expression());
                    visit(argList.argument(1).expression());
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "toString", "(I)Ljava/lang/String;", false);
                } else {
                    throw new RuntimeException("BigInt.ToString requires 1 or 2 arguments");
                }
                lastExprType = "String";
            }
            case "ToDecimal" -> {
                // Convert BigInteger to BigDecimal
                if (argCount != 1) throw new RuntimeException("BigInt.ToDecimal requires 1 argument");
                mv.visitTypeInsn(NEW, "java/math/BigDecimal");
                mv.visitInsn(DUP);
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKESPECIAL, "java/math/BigDecimal", "<init>", "(Ljava/math/BigInteger;)V", false);
                lastExprType = "BigDecimal";
            }

            // Primality testing
            case "IsProbablePrime" -> {
                // IsProbablePrime(n, certainty)
                if (argCount != 2) throw new RuntimeException("BigInt.IsProbablePrime requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "isProbablePrime", "(I)Z", false);
                lastExprType = "Boolean";
            }

            // ModPow and ModInverse
            case "ModPow" -> {
                // ModPow(base, exponent, modulus)
                if (argCount != 3) throw new RuntimeException("BigInt.ModPow requires 3 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                visit(argList.argument(2).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "modPow", "(Ljava/math/BigInteger;Ljava/math/BigInteger;)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }
            case "ModInverse" -> {
                // ModInverse(value, modulus)
                if (argCount != 2) throw new RuntimeException("BigInt.ModInverse requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigInteger", "modInverse", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }

            default -> throw new RuntimeException("Unknown BigInt function: " + methodName);
        }

        return null;
    }

    // Handle Decimal.* calls - maps to java.math.BigDecimal factory and instance methods
    private Object handleDecimalCall(String methodName, JvmBasicParser.ArgumentListContext argList) {
        int argCount = argList != null ? argList.argument().size() : 0;

        switch (methodName) {
            // Factory methods - create BigDecimal from various sources
            case "FromString" -> {
                // Decimal.FromString("123.456789012345678901234567890")
                if (argCount != 1) throw new RuntimeException("Decimal.FromString requires 1 argument");
                mv.visitTypeInsn(NEW, "java/math/BigDecimal");
                mv.visitInsn(DUP);
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKESPECIAL, "java/math/BigDecimal", "<init>", "(Ljava/lang/String;)V", false);
                lastExprType = "BigDecimal";
            }
            case "FromDouble" -> {
                // Decimal.FromDouble(3.14159) - Note: use FromString for exact values
                if (argCount != 1) throw new RuntimeException("Decimal.FromDouble requires 1 argument");
                visit(argList.argument(0).expression());
                if ("Float".equals(lastExprType)) {
                    mv.visitInsn(F2D);
                }
                mv.visitMethodInsn(INVOKESTATIC, "java/math/BigDecimal", "valueOf", "(D)Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }
            case "FromLong" -> {
                if (argCount != 1) throw new RuntimeException("Decimal.FromLong requires 1 argument");
                visit(argList.argument(0).expression());
                if ("Integer".equals(lastExprType)) {
                    mv.visitInsn(I2L);
                }
                mv.visitMethodInsn(INVOKESTATIC, "java/math/BigDecimal", "valueOf", "(J)Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }
            case "FromInt" -> {
                if (argCount != 1) throw new RuntimeException("Decimal.FromInt requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitInsn(I2L);
                mv.visitMethodInsn(INVOKESTATIC, "java/math/BigDecimal", "valueOf", "(J)Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }
            case "FromBigInt" -> {
                // Convert BigInteger to BigDecimal
                if (argCount != 1) throw new RuntimeException("Decimal.FromBigInt requires 1 argument");
                mv.visitTypeInsn(NEW, "java/math/BigDecimal");
                mv.visitInsn(DUP);
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKESPECIAL, "java/math/BigDecimal", "<init>", "(Ljava/math/BigInteger;)V", false);
                lastExprType = "BigDecimal";
            }

            // Constants
            case "Zero" -> {
                mv.visitFieldInsn(GETSTATIC, "java/math/BigDecimal", "ZERO", "Ljava/math/BigDecimal;");
                lastExprType = "BigDecimal";
            }
            case "One" -> {
                mv.visitFieldInsn(GETSTATIC, "java/math/BigDecimal", "ONE", "Ljava/math/BigDecimal;");
                lastExprType = "BigDecimal";
            }
            case "Ten" -> {
                mv.visitFieldInsn(GETSTATIC, "java/math/BigDecimal", "TEN", "Ljava/math/BigDecimal;");
                lastExprType = "BigDecimal";
            }

            // Arithmetic operations (take two BigDecimals, return BigDecimal)
            case "Add" -> {
                if (argCount != 2) throw new RuntimeException("Decimal.Add requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "add", "(Ljava/math/BigDecimal;)Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }
            case "Subtract" -> {
                if (argCount != 2) throw new RuntimeException("Decimal.Subtract requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "subtract", "(Ljava/math/BigDecimal;)Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }
            case "Multiply" -> {
                if (argCount != 2) throw new RuntimeException("Decimal.Multiply requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "multiply", "(Ljava/math/BigDecimal;)Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }
            case "Divide" -> {
                // Divide with default rounding (HALF_UP, scale 10)
                if (argCount == 2) {
                    visit(argList.argument(0).expression());
                    visit(argList.argument(1).expression());
                    mv.visitIntInsn(BIPUSH, 10); // scale = 10
                    mv.visitFieldInsn(GETSTATIC, "java/math/RoundingMode", "HALF_UP", "Ljava/math/RoundingMode;");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "divide", "(Ljava/math/BigDecimal;ILjava/math/RoundingMode;)Ljava/math/BigDecimal;", false);
                } else if (argCount == 4) {
                    // Divide(a, b, scale, roundingMode)
                    visit(argList.argument(0).expression());
                    visit(argList.argument(1).expression());
                    visit(argList.argument(2).expression()); // scale as int
                    visit(argList.argument(3).expression()); // rounding mode as int
                    // Convert int to RoundingMode enum
                    mv.visitMethodInsn(INVOKESTATIC, "java/math/RoundingMode", "valueOf", "(I)Ljava/math/RoundingMode;", false);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "divide", "(Ljava/math/BigDecimal;ILjava/math/RoundingMode;)Ljava/math/BigDecimal;", false);
                } else {
                    throw new RuntimeException("Decimal.Divide requires 2 or 4 arguments");
                }
                lastExprType = "BigDecimal";
            }
            case "Remainder" -> {
                if (argCount != 2) throw new RuntimeException("Decimal.Remainder requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "remainder", "(Ljava/math/BigDecimal;)Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }
            case "Pow" -> {
                // Decimal.Pow(base, exponent) - exponent is int
                if (argCount != 2) throw new RuntimeException("Decimal.Pow requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "pow", "(I)Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }
            case "Max" -> {
                if (argCount != 2) throw new RuntimeException("Decimal.Max requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "max", "(Ljava/math/BigDecimal;)Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }
            case "Min" -> {
                if (argCount != 2) throw new RuntimeException("Decimal.Min requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "min", "(Ljava/math/BigDecimal;)Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }

            // Unary operations
            case "Abs" -> {
                if (argCount != 1) throw new RuntimeException("Decimal.Abs requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "abs", "()Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }
            case "Negate" -> {
                if (argCount != 1) throw new RuntimeException("Decimal.Negate requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "negate", "()Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }
            case "Sqrt" -> {
                // sqrt with default MathContext (DECIMAL128)
                if (argCount != 1) throw new RuntimeException("Decimal.Sqrt requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitFieldInsn(GETSTATIC, "java/math/MathContext", "DECIMAL128", "Ljava/math/MathContext;");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "sqrt", "(Ljava/math/MathContext;)Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }

            // Comparison (returns int: -1, 0, or 1)
            case "Compare" -> {
                if (argCount != 2) throw new RuntimeException("Decimal.Compare requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "compareTo", "(Ljava/math/BigDecimal;)I", false);
                lastExprType = "Integer";
            }
            case "Equals" -> {
                if (argCount != 2) throw new RuntimeException("Decimal.Equals requires 2 arguments");
                visit(argList.argument(0).expression());
                visit(argList.argument(1).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "equals", "(Ljava/lang/Object;)Z", false);
                lastExprType = "Boolean";
            }

            // Sign (returns int: -1, 0, or 1)
            case "Signum" -> {
                if (argCount != 1) throw new RuntimeException("Decimal.Signum requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "signum", "()I", false);
                lastExprType = "Integer";
            }

            // Scale and precision
            case "Scale" -> {
                if (argCount != 1) throw new RuntimeException("Decimal.Scale requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "scale", "()I", false);
                lastExprType = "Integer";
            }
            case "Precision" -> {
                if (argCount != 1) throw new RuntimeException("Decimal.Precision requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "precision", "()I", false);
                lastExprType = "Integer";
            }
            case "SetScale" -> {
                // SetScale(value, newScale) or SetScale(value, newScale, roundingMode)
                if (argCount == 2) {
                    visit(argList.argument(0).expression());
                    visit(argList.argument(1).expression());
                    mv.visitFieldInsn(GETSTATIC, "java/math/RoundingMode", "HALF_UP", "Ljava/math/RoundingMode;");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "setScale", "(ILjava/math/RoundingMode;)Ljava/math/BigDecimal;", false);
                } else if (argCount == 3) {
                    visit(argList.argument(0).expression());
                    visit(argList.argument(1).expression());
                    visit(argList.argument(2).expression());
                    mv.visitMethodInsn(INVOKESTATIC, "java/math/RoundingMode", "valueOf", "(I)Ljava/math/RoundingMode;", false);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "setScale", "(ILjava/math/RoundingMode;)Ljava/math/BigDecimal;", false);
                } else {
                    throw new RuntimeException("Decimal.SetScale requires 2 or 3 arguments");
                }
                lastExprType = "BigDecimal";
            }
            case "StripTrailingZeros" -> {
                if (argCount != 1) throw new RuntimeException("Decimal.StripTrailingZeros requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "stripTrailingZeros", "()Ljava/math/BigDecimal;", false);
                lastExprType = "BigDecimal";
            }

            // Rounding modes as constants (return int for use with SetScale/Divide)
            case "ROUND_UP" -> {
                mv.visitInsn(ICONST_0);
                lastExprType = "Integer";
            }
            case "ROUND_DOWN" -> {
                mv.visitInsn(ICONST_1);
                lastExprType = "Integer";
            }
            case "ROUND_CEILING" -> {
                mv.visitInsn(ICONST_2);
                lastExprType = "Integer";
            }
            case "ROUND_FLOOR" -> {
                mv.visitInsn(ICONST_3);
                lastExprType = "Integer";
            }
            case "ROUND_HALF_UP" -> {
                mv.visitInsn(ICONST_4);
                lastExprType = "Integer";
            }
            case "ROUND_HALF_DOWN" -> {
                mv.visitInsn(ICONST_5);
                lastExprType = "Integer";
            }
            case "ROUND_HALF_EVEN" -> {
                mv.visitIntInsn(BIPUSH, 6);
                lastExprType = "Integer";
            }
            case "ROUND_UNNECESSARY" -> {
                mv.visitIntInsn(BIPUSH, 7);
                lastExprType = "Integer";
            }

            // Conversion methods
            case "ToDouble" -> {
                if (argCount != 1) throw new RuntimeException("Decimal.ToDouble requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "doubleValue", "()D", false);
                lastExprType = "Double";
            }
            case "ToLong" -> {
                if (argCount != 1) throw new RuntimeException("Decimal.ToLong requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "longValue", "()J", false);
                lastExprType = "Long";
            }
            case "ToInt" -> {
                if (argCount != 1) throw new RuntimeException("Decimal.ToInt requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "intValue", "()I", false);
                lastExprType = "Integer";
            }
            case "ToString" -> {
                if (argCount != 1) throw new RuntimeException("Decimal.ToString requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "toString", "()Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "ToPlainString" -> {
                // ToPlainString - no scientific notation
                if (argCount != 1) throw new RuntimeException("Decimal.ToPlainString requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "toPlainString", "()Ljava/lang/String;", false);
                lastExprType = "String";
            }
            case "ToBigInt" -> {
                if (argCount != 1) throw new RuntimeException("Decimal.ToBigInt requires 1 argument");
                visit(argList.argument(0).expression());
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/math/BigDecimal", "toBigInteger", "()Ljava/math/BigInteger;", false);
                lastExprType = "BigInteger";
            }

            default -> throw new RuntimeException("Unknown Decimal function: " + methodName);
        }

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
            String rightType = lastExprType;
            // Determine operator (PLUS, MINUS, or AMP for string concat)
            var opToken = ctx.getChild(2 * i - 1); // Operators are at odd positions
            String op = opToken.getText();
            switch (op) {
                case "+" -> {
                    // If either operand is a String, treat + as string concatenation
                    if (isStringType(leftType) || isStringType(rightType)) {
                        emitStringConcat(leftType, rightType);
                    } else {
                        emitAdd(leftType);
                    }
                }
                case "-" -> emitSub(leftType);
                case "&" -> {
                    // String concatenation - convert both operands to String and concatenate
                    emitStringConcat(leftType, rightType);
                }
            }
        }
        return null;
    }

    /**
     * Check if a type is a String type.
     */
    private boolean isStringType(String type) {
        if (type == null) return false;
        String lower = type.toLowerCase();
        return lower.equals("string") || lower.equals("java.lang.string");
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

    /**
     * Emit string concatenation bytecode.
     * Stack: [left, right] -> [result]
     * Both values are converted to String if needed, then concatenated.
     */
    private void emitStringConcat(String leftType, String rightType) {
        // Strategy: Convert both operands to String using String.valueOf if needed,
        // then use String.concat()

        // Stack currently has: [left, right]
        // We need to convert right to String first (it's on top), then swap, convert left, swap back, then concat

        // Actually, it's simpler to use StringBuilder:
        // new StringBuilder().append(left).append(right).toString()

        // But the simplest approach for two operands:
        // Use invokedynamic makeConcatWithConstants (Java 9+) or just call String.valueOf and concat

        // For Java 21 compatibility, use simple String.concat approach:
        // Convert right to String (on top of stack)
        emitToString(rightType);
        // Stack: [left, rightString]

        // Swap
        mv.visitInsn(SWAP);
        // Stack: [rightString, left]

        // Convert left to String
        emitToString(leftType);
        // Stack: [rightString, leftString]

        // Swap back
        mv.visitInsn(SWAP);
        // Stack: [leftString, rightString]

        // Call String.concat
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "concat",
                          "(Ljava/lang/String;)Ljava/lang/String;", false);
        // Stack: [result]

        lastExprType = "String";
    }

    /**
     * Convert value on top of stack to String.
     */
    private void emitToString(String type) {
        if (type == null) {
            type = "Object";
        }
        switch (type.toLowerCase()) {
            case "string", "java.lang.string" -> { /* already a string */ }
            case "int", "integer" ->
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false);
            case "long" ->
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(J)Ljava/lang/String;", false);
            case "float" ->
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(F)Ljava/lang/String;", false);
            case "double" ->
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false);
            case "boolean" ->
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Z)Ljava/lang/String;", false);
            case "char" ->
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(C)Ljava/lang/String;", false);
            default ->
                // Object - call String.valueOf(Object)
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
                                  "(Ljava/lang/Object;)Ljava/lang/String;", false);
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
                String memberName = memberAccess.memberName().getText();

                // Check if next op is a FunctionCall
                if (i + 1 < postfixOps.size() && postfixOps.get(i + 1) instanceof JvmBasicParser.FunctionCallContext funcCall) {
                    // This is a method call: .memberName(args)
                    handleMethodInvocation(memberName, funcCall.argumentList());
                    i++; // Skip the FunctionCall since we handled it
                } else {
                    // Just a member access (field access like this.age or other.age)
                    // Must visit the member access to emit GETFIELD for user-defined class fields
                    visit(memberAccess);
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

        // Handle Math namespace
        if ("Math".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            handleMathCall(methodName, argList);
            return;
        }

        // Handle Str namespace
        if ("Str".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            handleStrCall(methodName, argList);
            return;
        }

        if ("Regex".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            handleRegexCall(methodName, argList);
            return;
        }

        if ("File".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            handleFileCall(methodName, argList);
            return;
        }

        // Handle Http namespace
        if ("Http".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            handleHttpCall(methodName, argList);
            return;
        }

        // Handle Json namespace
        if ("Json".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            handleJsonCall(methodName, argList);
            return;
        }

        // Handle Db namespace
        if ("Db".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            handleDbCall(methodName, argList);
            return;
        }

        // Handle BigInt namespace - java.math.BigInteger factory and utility methods
        if ("BigInt".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            handleBigIntCall(methodName, argList);
            return;
        }

        // Handle Decimal namespace - java.math.BigDecimal factory and utility methods
        if ("Decimal".equalsIgnoreCase(pendingNamespace)) {
            pendingNamespace = null;
            handleDecimalCall(methodName, argList);
            return;
        }

        // Handle instance method calls on user-defined classes
        // The object is already on the stack from visiting the primary expression
        // lastExprType contains the type of the object
        String objectType = lastExprType;
        ClassSymbol classSym = symbols.getClass(objectType);
        if (classSym != null) {
            FunctionSymbol methodSym = classSym.getMethod(methodName);
            if (methodSym != null) {
                // Push arguments onto stack (object is already on stack)
                if (argList != null) {
                    for (JvmBasicParser.ArgumentContext arg : argList.argument()) {
                        visit(arg.expression());
                    }
                }

                // Build method descriptor and call INVOKEVIRTUAL
                String descriptor = buildMethodDescriptor(methodSym);
                mv.visitMethodInsn(INVOKEVIRTUAL, objectType, methodName, descriptor, false);
                lastExprType = methodSym.returnType;
                return;
            }
        }
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
        // Handle field access like alice.name or this.age
        String memberName = ctx.memberName().getText();

        // The object is already on stack from previous expression (visit of primaryExpr)
        String objectType = lastExprType;

        // Check if this is a field access on a user-defined class
        ClassSymbol classSym = symbols.getClass(objectType);
        if (classSym != null) {
            FieldSymbol field = classSym.getField(memberName);
            if (field != null) {
                // Emit GETFIELD to read the field
                mv.visitFieldInsn(GETFIELD, objectType, memberName, typeToDescriptor(field.type));
                lastExprType = field.type;
                lastMemberAccess = null;  // Field access is complete
                return null;
            }
        }

        // Not a field access, record for potential method call
        lastMemberAccess = memberName;
        lastObjectType = objectType;
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
        String leftType = lastExprType;
        for (int i = 1; i < ctx.shiftExpression().size(); i++) {
            visit(ctx.shiftExpression(i));
            String rightType = lastExprType;
            var opToken = ctx.getChild(2 * i - 1);
            String op = opToken.getText();

            // Determine the comparison type (promote to wider type)
            String compType = promoteType(leftType, rightType);

            Label trueLabel = new Label();
            Label endLabel = new Label();

            emitRelationalComparison(compType, op, trueLabel);
            mv.visitInsn(ICONST_0);
            mv.visitJumpInsn(GOTO, endLabel);
            mv.visitLabel(trueLabel);
            mv.visitInsn(ICONST_1);
            mv.visitLabel(endLabel);

            leftType = "Boolean"; // Result of comparison is boolean
        }
        lastExprType = "Boolean";
        return null;
    }

    @Override
    public Object visitEqualityExpression(JvmBasicParser.EqualityExpressionContext ctx) {
        if (ctx.relationalExpression().size() == 1) {
            return visit(ctx.relationalExpression(0));
        }

        visit(ctx.relationalExpression(0));
        String leftType = lastExprType;
        for (int i = 1; i < ctx.relationalExpression().size(); i++) {
            visit(ctx.relationalExpression(i));
            String rightType = lastExprType;
            var opToken = ctx.getChild(2 * i - 1);
            String op = opToken.getText();

            // Determine the comparison type
            String compType = promoteType(leftType, rightType);

            Label trueLabel = new Label();
            Label endLabel = new Label();

            emitEqualityComparison(compType, op, trueLabel);
            mv.visitInsn(ICONST_0);
            mv.visitJumpInsn(GOTO, endLabel);
            mv.visitLabel(trueLabel);
            mv.visitInsn(ICONST_1);
            mv.visitLabel(endLabel);

            leftType = "Boolean"; // Result of comparison is boolean
        }
        lastExprType = "Boolean";
        return null;
    }

    /**
     * Promotes two types to their common wider type for comparison/arithmetic.
     * Type promotion order: int < long < float < double
     */
    private String promoteType(String left, String right) {
        String l = left.toLowerCase();
        String r = right.toLowerCase();

        // Handle string comparisons
        if ("string".equals(l) || "string".equals(r)) {
            return "String";
        }

        // Handle boolean comparisons
        if ("boolean".equals(l) && "boolean".equals(r)) {
            return "Boolean";
        }

        // Numeric type promotion: double > float > long > int
        if ("double".equals(l) || "double".equals(r)) {
            return "Double";
        }
        if ("float".equals(l) || "float".equals(r)) {
            return "Float";
        }
        if ("long".equals(l) || "long".equals(r)) {
            return "Long";
        }
        return "Integer";
    }

    /**
     * Emits bytecode for relational comparison (<, >, <=, >=)
     */
    private void emitRelationalComparison(String type, String op, Label trueLabel) {
        switch (type.toLowerCase()) {
            case "double" -> {
                // DCMPL: pushes -1 if left < right, 0 if equal, 1 if left > right
                // (DCMPL returns -1 for NaN, DCMPG returns 1 for NaN)
                switch (op) {
                    case "<" -> {
                        mv.visitInsn(DCMPG); // Use DCMPG so NaN returns 1 (false for <)
                        mv.visitJumpInsn(IFLT, trueLabel);
                    }
                    case ">" -> {
                        mv.visitInsn(DCMPL); // Use DCMPL so NaN returns -1 (false for >)
                        mv.visitJumpInsn(IFGT, trueLabel);
                    }
                    case "<=" -> {
                        mv.visitInsn(DCMPG);
                        mv.visitJumpInsn(IFLE, trueLabel);
                    }
                    case ">=" -> {
                        mv.visitInsn(DCMPL);
                        mv.visitJumpInsn(IFGE, trueLabel);
                    }
                }
            }
            case "float" -> {
                switch (op) {
                    case "<" -> {
                        mv.visitInsn(FCMPG);
                        mv.visitJumpInsn(IFLT, trueLabel);
                    }
                    case ">" -> {
                        mv.visitInsn(FCMPL);
                        mv.visitJumpInsn(IFGT, trueLabel);
                    }
                    case "<=" -> {
                        mv.visitInsn(FCMPG);
                        mv.visitJumpInsn(IFLE, trueLabel);
                    }
                    case ">=" -> {
                        mv.visitInsn(FCMPL);
                        mv.visitJumpInsn(IFGE, trueLabel);
                    }
                }
            }
            case "long" -> {
                mv.visitInsn(LCMP);
                switch (op) {
                    case "<" -> mv.visitJumpInsn(IFLT, trueLabel);
                    case ">" -> mv.visitJumpInsn(IFGT, trueLabel);
                    case "<=" -> mv.visitJumpInsn(IFLE, trueLabel);
                    case ">=" -> mv.visitJumpInsn(IFGE, trueLabel);
                }
            }
            case "string" -> {
                // String comparison using compareTo
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "compareTo",
                                  "(Ljava/lang/String;)I", false);
                switch (op) {
                    case "<" -> mv.visitJumpInsn(IFLT, trueLabel);
                    case ">" -> mv.visitJumpInsn(IFGT, trueLabel);
                    case "<=" -> mv.visitJumpInsn(IFLE, trueLabel);
                    case ">=" -> mv.visitJumpInsn(IFGE, trueLabel);
                }
            }
            default -> {
                // Integer comparison
                switch (op) {
                    case "<" -> mv.visitJumpInsn(IF_ICMPLT, trueLabel);
                    case ">" -> mv.visitJumpInsn(IF_ICMPGT, trueLabel);
                    case "<=" -> mv.visitJumpInsn(IF_ICMPLE, trueLabel);
                    case ">=" -> mv.visitJumpInsn(IF_ICMPGE, trueLabel);
                }
            }
        }
    }

    /**
     * Emits bytecode for equality comparison (=, <>)
     */
    private void emitEqualityComparison(String type, String op, Label trueLabel) {
        switch (type.toLowerCase()) {
            case "double" -> {
                mv.visitInsn(DCMPL);
                switch (op) {
                    case "=" -> mv.visitJumpInsn(IFEQ, trueLabel);
                    case "<>" -> mv.visitJumpInsn(IFNE, trueLabel);
                }
            }
            case "float" -> {
                mv.visitInsn(FCMPL);
                switch (op) {
                    case "=" -> mv.visitJumpInsn(IFEQ, trueLabel);
                    case "<>" -> mv.visitJumpInsn(IFNE, trueLabel);
                }
            }
            case "long" -> {
                mv.visitInsn(LCMP);
                switch (op) {
                    case "=" -> mv.visitJumpInsn(IFEQ, trueLabel);
                    case "<>" -> mv.visitJumpInsn(IFNE, trueLabel);
                }
            }
            case "string" -> {
                // String equality using equals()
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals",
                                  "(Ljava/lang/Object;)Z", false);
                switch (op) {
                    case "=" -> mv.visitJumpInsn(IFNE, trueLabel); // equals returns true (non-zero)
                    case "<>" -> mv.visitJumpInsn(IFEQ, trueLabel); // equals returns false (zero)
                }
            }
            case "boolean" -> {
                // Boolean equality
                switch (op) {
                    case "=" -> mv.visitJumpInsn(IF_ICMPEQ, trueLabel);
                    case "<>" -> mv.visitJumpInsn(IF_ICMPNE, trueLabel);
                }
            }
            default -> {
                // Integer comparison
                switch (op) {
                    case "=" -> mv.visitJumpInsn(IF_ICMPEQ, trueLabel);
                    case "<>" -> mv.visitJumpInsn(IF_ICMPNE, trueLabel);
                }
            }
        }
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
        // Handle array types first
        if (type.endsWith("[]")) {
            String elementType = type.substring(0, type.length() - 2);
            return "[" + typeToDescriptor(elementType);
        }

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
            case "biginteger" -> "Ljava/math/BigInteger;";
            case "decimal", "bigdecimal" -> "Ljava/math/BigDecimal;";
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

    // Coerce the value on stack to double (for Math functions)
    private void coerceToDouble() {
        switch (lastExprType.toLowerCase()) {
            case "integer", "int" -> {
                mv.visitInsn(I2D);
                lastExprType = "Double";
            }
            case "long" -> {
                mv.visitInsn(L2D);
                lastExprType = "Double";
            }
            case "float" -> {
                mv.visitInsn(F2D);
                lastExprType = "Double";
            }
            // Double already - no conversion needed
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
        // FIRST: Check the scope stack (block-scoped variables)
        LocalVar scopedVar = lookupScopedVariable(name);
        if (scopedVar != null) {
            loadLocal(scopedVar.slot(), scopedVar.type());
            return;
        }

        // Check if we're in a class method
        if (currentClass != null && currentMethod != null) {
            ClassSymbol classSym = symbols.getClass(currentClass);
            if (classSym != null) {
                FunctionSymbol method = classSym.getMethod(currentMethod);
                if (method != null) {
                    // Instance methods: slot 0 is 'this', parameters start at slot 1
                    List<ParameterSymbol> params = method.getParameters();
                    for (int i = 0; i < params.size(); i++) {
                        if (params.get(i).name.equals(name)) {
                            loadLocal(i + 1, params.get(i).type);  // +1 because slot 0 is 'this'
                            return;
                        }
                    }
                    // Check local variables in method
                    VariableSymbol local = method.getLocal(name);
                    if (local != null && local.getSlot() >= 0) {
                        loadLocal(local.getSlot(), local.type);
                        return;
                    }
                }
            }
        }

        // Check local variables and parameters first (for non-class methods)
        if (currentMethod != null && currentClass == null) {
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
                // Check main method locals (legacy support)
                LocalVar local = mainLocals.get(name);
                if (local != null) {
                    loadLocal(local.slot(), local.type());
                    return;
                }
            }
        }

        // Check dynamicLocals (FOR loop variables, etc.) - legacy support
        LocalVar dynamicLocal = dynamicLocals.get(name);
        if (dynamicLocal != null) {
            loadLocal(dynamicLocal.slot(), dynamicLocal.type());
            return;
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

            // FIRST: Check the scope stack (block-scoped variables)
            LocalVar scopedVar = lookupScopedVariable(name);
            if (scopedVar != null) {
                storeLocal(scopedVar.slot(), scopedVar.type());
                return;
            }

            // Find variable slot and store (legacy support)
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
            .replace("\\'", "'")
            .replace("\\\\", "\\");
    }

    // ========================================================================
    // Exit and Continue Statements
    // ========================================================================

    @Override
    public Object visitExitStatement(JvmBasicParser.ExitStatementContext ctx) {
        // EXIT FOR | EXIT WHILE | EXIT DO | EXIT SUB | EXIT FUNCTION | EXIT SELECT
        String loopType = null;

        if (ctx.FOR() != null) {
            loopType = "for";
        } else if (ctx.WHILE() != null) {
            loopType = "while";
        } else if (ctx.DO() != null) {
            loopType = "do";
        } else if (ctx.SELECT() != null) {
            loopType = "select";
        } else if (ctx.SUB() != null || ctx.FUNCTION() != null) {
            // Exit function/sub - emit return
            FunctionSymbol func = symbols.getFunction(currentMethod);
            if (func != null) {
                generateDefaultReturn(func.returnType);
            } else {
                // In main, just return
                mv.visitInsn(RETURN);
            }
            return null;
        }

        LoopContext loop = findLoop(loopType);
        if (loop == null) {
            throw new RuntimeException("Exit " + (loopType != null ? loopType : "") +
                                       " statement not inside a matching loop");
        }

        // Jump to break label
        mv.visitJumpInsn(GOTO, loop.breakLabel);
        return null;
    }

    @Override
    public Object visitContinueStatement(JvmBasicParser.ContinueStatementContext ctx) {
        // CONTINUE [FOR | WHILE | DO]
        String loopType = null;

        if (ctx.FOR() != null) {
            loopType = "for";
        } else if (ctx.WHILE() != null) {
            loopType = "while";
        } else if (ctx.DO() != null) {
            loopType = "do";
        }

        LoopContext loop = findLoop(loopType);
        if (loop == null) {
            throw new RuntimeException("Continue" + (loopType != null ? " " + loopType : "") +
                                       " statement not inside a matching loop");
        }

        // Jump to continue label
        mv.visitJumpInsn(GOTO, loop.continueLabel);
        return null;
    }

    // ========================================================================
    // String Interpolation
    // ========================================================================

    @Override
    public Object visitInterpolatedStringLiteral(JvmBasicParser.InterpolatedStringLiteralContext ctx) {
        // Handle $"Hello {name}!" syntax
        String text = ctx.INTERPOLATED_STRING().getText();

        // Remove $" prefix and " suffix
        String content = text.substring(2, text.length() - 1);

        // Parse interpolations and generate StringBuilder code
        compileInterpolatedString(content);

        lastExprType = "String";
        return null;
    }

    /**
     * Compiles an interpolated string like "Hello {name}, you are {age} years old"
     * Uses StringBuilder for efficient concatenation.
     */
    private void compileInterpolatedString(String content) {
        // Create StringBuilder
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

        // Parse the string and process literal parts and interpolations
        int i = 0;
        StringBuilder literalPart = new StringBuilder();

        while (i < content.length()) {
            char c = content.charAt(i);

            if (c == '{') {
                // Flush any accumulated literal text first
                if (literalPart.length() > 0) {
                    String lit = processEscapes(literalPart.toString());
                    mv.visitLdcInsn(lit);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                                      "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                    literalPart.setLength(0);
                }

                // Find the closing brace (handling nested braces and string literals)
                int closeBrace = findMatchingCloseBrace(content, i);
                if (closeBrace == -1) {
                    throw new RuntimeException("Unclosed interpolation in string: missing '}'");
                }

                String exprText = content.substring(i + 1, closeBrace).trim();
                if (exprText.isEmpty()) {
                    throw new RuntimeException("Empty interpolation expression");
                }

                // Compile the expression (parse and evaluate)
                compileInterpolationExpression(exprText);

                // Convert result to String and append
                emitToStringForInterpolation();
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                                  "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                i = closeBrace + 1;
            } else if (c == '$' && i + 1 < content.length() && content.charAt(i + 1) == '$') {
                // Escaped $$ -> $
                literalPart.append('$');
                i += 2;
            } else if (c == '\\' && i + 1 < content.length()) {
                // Escape sequence - preserve for processing
                literalPart.append(c);
                literalPart.append(content.charAt(i + 1));
                i += 2;
            } else {
                literalPart.append(c);
                i++;
            }
        }

        // Flush remaining literal text
        if (literalPart.length() > 0) {
            String lit = processEscapes(literalPart.toString());
            mv.visitLdcInsn(lit);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                              "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        }

        // Call toString on StringBuilder
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                          "()Ljava/lang/String;", false);
    }

    /**
     * Process escape sequences in a string
     */
    private String processEscapes(String s) {
        return s
            .replace("\\n", "\n")
            .replace("\\t", "\t")
            .replace("\\r", "\r")
            .replace("\\\"", "\"")
            .replace("\\'", "'")
            .replace("\\\\", "\\");
    }

    /**
     * Find the matching close brace for an interpolation expression.
     * Handles nested braces and string literals that may contain braces.
     *
     * @param content The string content
     * @param openBrace The position of the opening brace
     * @return The position of the matching close brace, or -1 if not found
     */
    private int findMatchingCloseBrace(String content, int openBrace) {
        int depth = 1;
        int i = openBrace + 1;

        while (i < content.length() && depth > 0) {
            char c = content.charAt(i);

            if (c == '"') {
                // Inside a string literal - skip to the end of the string
                i++;
                while (i < content.length()) {
                    char sc = content.charAt(i);
                    if (sc == '\\' && i + 1 < content.length()) {
                        // Skip escape sequence
                        i += 2;
                    } else if (sc == '"') {
                        // End of string literal
                        i++;
                        break;
                    } else {
                        i++;
                    }
                }
            } else if (c == '\\' && i + 1 < content.length()) {
                // Skip escape sequence
                i += 2;
            } else if (c == '{') {
                depth++;
                i++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return i;
                }
                i++;
            } else {
                i++;
            }
        }

        return -1; // No matching brace found
    }

    /**
     * Compiles an expression inside an interpolation.
     *
     * Supported expression forms:
     *   - Simple variable: {name}
     *   - Namespace method call: {Str.ToUpper(name)}, {Math.Sqrt(x)}
     *   - Property/member access: {obj.property}
     *   - Method call on object: {obj.method()}, {obj.method(arg)}
     *
     * Re-parses the expression text using the ANTLR grammar for full compatibility.
     */
    private void compileInterpolationExpression(String exprText) {
        try {
            // Convert escaped quotes \" to regular quotes "
            // Inside an interpolation like $"test {Json.Get(arr, \"1\")}",
            // the user writes \" to include a quote, but when we parse the
            // expression standalone, these should be regular string delimiters
            exprText = exprText.replace("\\\"", "\"");

            // Re-parse the expression using ANTLR
            org.antlr.v4.runtime.CharStream input = org.antlr.v4.runtime.CharStreams.fromString(exprText);
            JvmBasicLexer lexer = new JvmBasicLexer(input);
            lexer.removeErrorListeners(); // Suppress console errors
            org.antlr.v4.runtime.CommonTokenStream tokens = new org.antlr.v4.runtime.CommonTokenStream(lexer);
            JvmBasicParser parser = new JvmBasicParser(tokens);
            parser.removeErrorListeners(); // Suppress console errors

            // Parse as an expression
            JvmBasicParser.ExpressionContext exprCtx = parser.expression();

            // Check for parsing errors
            if (parser.getNumberOfSyntaxErrors() > 0) {
                throw new RuntimeException("Invalid expression in string interpolation: " + exprText);
            }

            // Visit the parsed expression to generate bytecode
            visit(exprCtx);

        } catch (RuntimeException e) {
            // Re-throw runtime exceptions as-is
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse interpolation expression: " + exprText, e);
        }
    }

    /**
     * Emits code to convert the top of stack to a String for interpolation
     */
    private void emitToStringForInterpolation() {
        switch (lastExprType) {
            case "String" -> {
                // Already a string, nothing to do
            }
            case "Integer", "int" -> {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "toString",
                                  "(I)Ljava/lang/String;", false);
            }
            case "Long", "long" -> {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "toString",
                                  "(J)Ljava/lang/String;", false);
            }
            case "Float", "float" -> {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "toString",
                                  "(F)Ljava/lang/String;", false);
            }
            case "Double", "double" -> {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "toString",
                                  "(D)Ljava/lang/String;", false);
            }
            case "Boolean", "boolean" -> {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "toString",
                                  "(Z)Ljava/lang/String;", false);
            }
            default -> {
                // For objects, call String.valueOf which handles null safely
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
                                  "(Ljava/lang/Object;)Ljava/lang/String;", false);
            }
        }
    }
}
