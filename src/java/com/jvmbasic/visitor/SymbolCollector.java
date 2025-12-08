package com.jvmbasic.visitor;

import com.jvmbasic.grammar.*;
import org.antlr.v4.runtime.tree.*;

import java.util.*;

/**
 * Symbol Collector - First pass listener for gathering symbol information
 *
 * This listener performs a pre-pass to collect:
 * - Function/Sub declarations (names, parameters, return types)
 * - Class declarations (fields, methods)
 * - Global variables
 * - Constants
 * - Block-scoped local variables
 *
 * The collected symbol table is then passed to the CompilerVisitor
 * for the second pass code generation.
 *
 * Usage:
 *   SymbolCollector collector = new SymbolCollector();
 *   ParseTreeWalker.DEFAULT.walk(collector, tree);
 *   SymbolTable symbols = collector.getSymbols();
 */
public class SymbolCollector extends JvmBasicParserBaseListener {

    private final SymbolTable symbols = new SymbolTable();
    private String currentClass = null;
    private String currentFunction = null;

    // Scope stack for tracking block-level scoping
    private final Deque<Scope> scopeStack = new ArrayDeque<>();
    private int scopeIdCounter = 0;

    public SymbolTable getSymbols() {
        return symbols;
    }

    // ========================================================================
    // Scope Management
    // ========================================================================

    private void enterScope(String name, ScopeType type, int line) {
        Scope parent = scopeStack.peek();
        Scope scope = new Scope(scopeIdCounter++, name, type, parent, line);
        scopeStack.push(scope);

        // Link scope to function if we're inside one
        if (currentFunction != null && currentClass == null) {
            FunctionSymbol func = symbols.getFunction(currentFunction);
            if (func != null) {
                func.addScope(scope);
            }
        } else if (currentFunction != null && currentClass != null) {
            ClassSymbol cls = symbols.getClass(currentClass);
            if (cls != null) {
                FunctionSymbol method = cls.getMethod(currentFunction);
                if (method != null) {
                    method.addScope(scope);
                }
            }
        }
    }

    private void exitScope() {
        if (!scopeStack.isEmpty()) {
            scopeStack.pop();
        }
    }

    private Scope currentScope() {
        return scopeStack.peek();
    }

    /**
     * Look up a variable in the current scope chain (current scope + all parents)
     */
    private VariableSymbol lookupVariable(String name) {
        Scope scope = currentScope();
        while (scope != null) {
            VariableSymbol var = scope.getVariable(name);
            if (var != null) {
                return var;
            }
            scope = scope.parent;
        }
        return null;
    }

    /**
     * Check if a variable is declared in the current scope (not parent scopes)
     */
    private boolean isDeclaredInCurrentScope(String name) {
        Scope scope = currentScope();
        return scope != null && scope.hasVariable(name);
    }

    // ========================================================================
    // Class Declarations
    // ========================================================================

    @Override
    public void enterClassDeclaration(JvmBasicParser.ClassDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();

        ClassSymbol classSymbol = new ClassSymbol(name, line);

        // Check for abstract modifier
        if (ctx.ABSTRACT() != null) {
            classSymbol.setAbstract(true);
        }

        // Check for extends
        if (ctx.typeName() != null) {
            classSymbol.setBaseClass(ctx.typeName().getText());
        }

        // Check for implements
        if (ctx.typeNameList() != null) {
            for (JvmBasicParser.TypeNameContext typeCtx : ctx.typeNameList().typeName()) {
                classSymbol.addInterface(typeCtx.getText());
            }
        }

        symbols.addClass(classSymbol);
        currentClass = name;
        enterScope(name, ScopeType.CLASS, line);
    }

    @Override
    public void exitClassDeclaration(JvmBasicParser.ClassDeclarationContext ctx) {
        exitScope();
        currentClass = null;
    }

    // ========================================================================
    // Function/Sub Declarations
    // ========================================================================

    @Override
    public void enterFunctionDeclaration(JvmBasicParser.FunctionDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        String returnType = ctx.typeName().getText();
        int line = ctx.getStart().getLine();

        FunctionSymbol func = new FunctionSymbol(name, returnType, line);
        collectParameters(ctx.parameterList(), func);

        if (currentClass != null) {
            symbols.getClass(currentClass).addMethod(func);
        } else {
            symbols.addFunction(func);
        }
        currentFunction = name;
        enterScope(name, ScopeType.FUNCTION, line);

        // Add parameters as variables in the function scope
        for (ParameterSymbol param : func.getParameters()) {
            VariableSymbol paramVar = new VariableSymbol(param.name, param.type, line);
            currentScope().addVariable(paramVar);
        }
    }

    @Override
    public void exitFunctionDeclaration(JvmBasicParser.FunctionDeclarationContext ctx) {
        exitScope();
        currentFunction = null;
    }

    @Override
    public void enterSubDeclaration(JvmBasicParser.SubDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();

        FunctionSymbol sub = new FunctionSymbol(name, "Void", line);
        sub.setSub(true);
        collectParameters(ctx.parameterList(), sub);

        if (currentClass != null) {
            symbols.getClass(currentClass).addMethod(sub);
        } else {
            symbols.addFunction(sub);
        }
        currentFunction = name;
        enterScope(name, ScopeType.FUNCTION, line);

        // Add parameters as variables in the function scope
        for (ParameterSymbol param : sub.getParameters()) {
            VariableSymbol paramVar = new VariableSymbol(param.name, param.type, line);
            currentScope().addVariable(paramVar);
        }
    }

    @Override
    public void exitSubDeclaration(JvmBasicParser.SubDeclarationContext ctx) {
        exitScope();
        currentFunction = null;
    }

    // ========================================================================
    // Constructor Declarations (class constructors: SUB NEW)
    // ========================================================================

    @Override
    public void enterConstructorDeclaration(JvmBasicParser.ConstructorDeclarationContext ctx) {
        if (currentClass == null) return;  // Should only be in a class

        int line = ctx.getStart().getLine();

        // Constructor is stored as method named "New" (VB-style) or "<init>" (JVM-style)
        FunctionSymbol ctor = new FunctionSymbol("New", "Void", line);
        ctor.setSub(true);
        collectParameters(ctx.parameterList(), ctor);

        symbols.getClass(currentClass).addMethod(ctor);
        currentFunction = "New";
        enterScope("New", ScopeType.FUNCTION, line);

        // Add parameters as variables in the constructor scope
        for (ParameterSymbol param : ctor.getParameters()) {
            VariableSymbol paramVar = new VariableSymbol(param.name, param.type, line);
            currentScope().addVariable(paramVar);
        }
    }

    @Override
    public void exitConstructorDeclaration(JvmBasicParser.ConstructorDeclarationContext ctx) {
        exitScope();
        currentFunction = null;
    }

    // ========================================================================
    // Method Declarations (class methods: FUNCTION/SUB IDENTIFIER)
    // ========================================================================

    @Override
    public void enterMethodDeclaration(JvmBasicParser.MethodDeclarationContext ctx) {
        if (currentClass == null) return;  // Should only be in a class

        String name = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();

        // Get return type - default to Void for SUB
        String returnType = "Void";
        if (ctx.typeName() != null) {
            returnType = ctx.typeName().getText();
        }

        FunctionSymbol method = new FunctionSymbol(name, returnType, line);
        method.setSub(ctx.SUB() != null && ctx.FUNCTION() == null);
        collectParameters(ctx.parameterList(), method);

        symbols.getClass(currentClass).addMethod(method);
        currentFunction = name;
        enterScope(name, ScopeType.FUNCTION, line);

        // Add parameters as variables in the method scope
        for (ParameterSymbol param : method.getParameters()) {
            VariableSymbol paramVar = new VariableSymbol(param.name, param.type, line);
            currentScope().addVariable(paramVar);
        }
    }

    @Override
    public void exitMethodDeclaration(JvmBasicParser.MethodDeclarationContext ctx) {
        exitScope();
        currentFunction = null;
    }

    // ========================================================================
    // Abstract Method Declarations (only inside abstract classes)
    // ========================================================================

    @Override
    public void enterAbstractMethodDeclaration(JvmBasicParser.AbstractMethodDeclarationContext ctx) {
        if (currentClass == null) return;  // Should only be in a class

        String name = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();

        // Get return type - default to Void for SUB
        String returnType = "Void";
        if (ctx.typeName() != null) {
            returnType = ctx.typeName().getText();
        }

        FunctionSymbol method = new FunctionSymbol(name, returnType, line);
        method.setSub(ctx.SUB() != null && ctx.FUNCTION() == null);
        method.setAbstract(true);  // Mark as abstract
        collectParameters(ctx.parameterList(), method);

        symbols.getClass(currentClass).addMethod(method);
        // No scope for abstract methods - they have no body
    }

    @Override
    public void exitAbstractMethodDeclaration(JvmBasicParser.AbstractMethodDeclarationContext ctx) {
        // Nothing to do - abstract methods have no body/scope
    }

    private void collectParameters(JvmBasicParser.ParameterListContext ctx, FunctionSymbol func) {
        if (ctx == null) return;

        for (JvmBasicParser.ParameterContext param : ctx.parameter()) {
            String paramName = param.IDENTIFIER().getText();
            String paramType = param.typeName().getText();
            boolean byRef = param.BYREF() != null;
            func.addParameter(new ParameterSymbol(paramName, paramType, byRef));
        }
    }

    // ========================================================================
    // Field Declarations (class members)
    // ========================================================================

    @Override
    public void enterFieldDeclaration(JvmBasicParser.FieldDeclarationContext ctx) {
        if (currentClass == null) return;

        String name = ctx.IDENTIFIER().getText();
        String type = ctx.typeName().getText();
        boolean isShared = ctx.SHARED() != null;
        int line = ctx.getStart().getLine();

        FieldSymbol field = new FieldSymbol(name, type, line);
        field.setStatic(isShared);

        if (ctx.accessModifier() != null) {
            field.setAccessModifier(ctx.accessModifier().getText());
        }

        symbols.getClass(currentClass).addField(field);
    }

    // ========================================================================
    // Variable Declarations (local and global)
    // ========================================================================

    @Override
    public void enterVarStatement(JvmBasicParser.VarStatementContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        String type = ctx.typeName().getText();
        int line = ctx.getStart().getLine();

        VariableSymbol var = new VariableSymbol(name, type, line);

        if (currentFunction != null) {
            // Local variable - add to current scope
            Scope scope = currentScope();
            if (scope != null) {
                // Check for duplicate in current scope
                if (isDeclaredInCurrentScope(name)) {
                    throw new SemanticException("Variable '" + name + "' is already declared in this scope", line);
                }
                scope.addVariable(var);

                // Also track in function's flat local list for backwards compatibility
                if (currentClass != null) {
                    symbols.getClass(currentClass).getMethod(currentFunction).addLocal(var);
                } else {
                    symbols.getFunction(currentFunction).addLocal(var);
                }
            }
        } else if (currentClass != null) {
            // Instance field declared with var (treat as field)
            FieldSymbol field = new FieldSymbol(name, type, line);
            symbols.getClass(currentClass).addField(field);
        } else {
            // Global variable
            symbols.addGlobal(var);
        }
    }

    // ========================================================================
    // Block Scopes - FOR loops
    // ========================================================================

    @Override
    public void enterForStatement(JvmBasicParser.ForStatementContext ctx) {
        int line = ctx.getStart().getLine();
        String varName = ctx.IDENTIFIER(0).getText();
        enterScope("for_" + varName, ScopeType.FOR, line);

        // The FOR loop variable is implicitly declared in this scope
        VariableSymbol loopVar = new VariableSymbol(varName, "Integer", line);
        loopVar.setImplicit(true);  // Mark as implicitly declared
        currentScope().addVariable(loopVar);
    }

    @Override
    public void exitForStatement(JvmBasicParser.ForStatementContext ctx) {
        exitScope();
    }

    @Override
    public void enterForEachStatement(JvmBasicParser.ForEachStatementContext ctx) {
        int line = ctx.getStart().getLine();
        String varName = ctx.IDENTIFIER(0).getText();
        enterScope("foreach_" + varName, ScopeType.FOREACH, line);

        // The FOR EACH loop variable is implicitly declared in this scope
        // Type will be inferred from the collection during codegen
        VariableSymbol loopVar = new VariableSymbol(varName, "Object", line);
        loopVar.setImplicit(true);
        currentScope().addVariable(loopVar);
    }

    @Override
    public void exitForEachStatement(JvmBasicParser.ForEachStatementContext ctx) {
        exitScope();
    }

    // ========================================================================
    // Block Scopes - WHILE loops
    // ========================================================================

    @Override
    public void enterWhileStatement(JvmBasicParser.WhileStatementContext ctx) {
        int line = ctx.getStart().getLine();
        enterScope("while", ScopeType.WHILE, line);
    }

    @Override
    public void exitWhileStatement(JvmBasicParser.WhileStatementContext ctx) {
        exitScope();
    }

    // ========================================================================
    // Block Scopes - DO loops
    // ========================================================================

    @Override
    public void enterDoStatement(JvmBasicParser.DoStatementContext ctx) {
        int line = ctx.getStart().getLine();
        enterScope("do", ScopeType.DO, line);
    }

    @Override
    public void exitDoStatement(JvmBasicParser.DoStatementContext ctx) {
        exitScope();
    }

    // ========================================================================
    // Block Scopes - IF statements
    // ========================================================================

    @Override
    public void enterIfStatement(JvmBasicParser.IfStatementContext ctx) {
        int line = ctx.getStart().getLine();
        enterScope("if", ScopeType.IF, line);
    }

    @Override
    public void exitIfStatement(JvmBasicParser.IfStatementContext ctx) {
        exitScope();
    }

    @Override
    public void enterElseIfClause(JvmBasicParser.ElseIfClauseContext ctx) {
        int line = ctx.getStart().getLine();
        // Exit previous IF/ELSEIF scope and enter new one
        exitScope();
        enterScope("elseif", ScopeType.IF, line);
    }

    @Override
    public void enterElseClause(JvmBasicParser.ElseClauseContext ctx) {
        int line = ctx.getStart().getLine();
        // Exit previous IF/ELSEIF scope and enter new one
        exitScope();
        enterScope("else", ScopeType.IF, line);
    }

    // ========================================================================
    // Block Scopes - SELECT CASE statements
    // ========================================================================

    @Override
    public void enterSelectStatement(JvmBasicParser.SelectStatementContext ctx) {
        int line = ctx.getStart().getLine();
        enterScope("select", ScopeType.SELECT, line);
    }

    @Override
    public void exitSelectStatement(JvmBasicParser.SelectStatementContext ctx) {
        exitScope();
    }

    @Override
    public void enterCaseClause(JvmBasicParser.CaseClauseContext ctx) {
        int line = ctx.getStart().getLine();
        enterScope("case", ScopeType.CASE, line);
    }

    @Override
    public void exitCaseClause(JvmBasicParser.CaseClauseContext ctx) {
        exitScope();
    }

    @Override
    public void enterCaseElseClause(JvmBasicParser.CaseElseClauseContext ctx) {
        int line = ctx.getStart().getLine();
        enterScope("case_else", ScopeType.CASE, line);
    }

    @Override
    public void exitCaseElseClause(JvmBasicParser.CaseElseClauseContext ctx) {
        exitScope();
    }

    // ========================================================================
    // Block Scopes - TRY/CATCH/FINALLY statements
    // ========================================================================

    @Override
    public void enterTryStatement(JvmBasicParser.TryStatementContext ctx) {
        int line = ctx.getStart().getLine();
        enterScope("try", ScopeType.TRY, line);
    }

    @Override
    public void exitTryStatement(JvmBasicParser.TryStatementContext ctx) {
        exitScope();
    }

    @Override
    public void enterCatchClause(JvmBasicParser.CatchClauseContext ctx) {
        int line = ctx.getStart().getLine();
        // Exit try scope, enter catch scope
        exitScope();
        enterScope("catch", ScopeType.CATCH, line);

        // Add exception variable to catch scope
        String exName = ctx.IDENTIFIER().getText();
        String exType = ctx.typeName() != null ? ctx.typeName().getText() : "Exception";
        VariableSymbol exVar = new VariableSymbol(exName, exType, line);
        exVar.setImplicit(true);
        currentScope().addVariable(exVar);
    }

    @Override
    public void enterFinallyClause(JvmBasicParser.FinallyClauseContext ctx) {
        int line = ctx.getStart().getLine();
        // Exit previous scope, enter finally scope
        exitScope();
        enterScope("finally", ScopeType.FINALLY, line);
    }

    // ========================================================================
    // Constant Declarations
    // ========================================================================

    @Override
    public void enterConstDeclaration(JvmBasicParser.ConstDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        String type = ctx.typeName().getText();
        int line = ctx.getStart().getLine();

        ConstantSymbol constant = new ConstantSymbol(name, type, line);
        symbols.addConstant(constant);
    }

    // ========================================================================
    // Scope Type Enum
    // ========================================================================

    public enum ScopeType {
        GLOBAL,
        CLASS,
        FUNCTION,
        FOR,
        FOREACH,
        WHILE,
        DO,
        IF,
        SELECT,
        CASE,
        TRY,
        CATCH,
        FINALLY,
        BLOCK  // Generic block
    }

    // ========================================================================
    // Scope Class
    // ========================================================================

    public static class Scope {
        public final int id;
        public final String name;
        public final ScopeType type;
        public final Scope parent;
        public final int line;
        private final Map<String, VariableSymbol> variables = new LinkedHashMap<>();
        private int startSlot = -1;  // First slot used in this scope
        private int endSlot = -1;    // Last slot used in this scope

        public Scope(int id, String name, ScopeType type, Scope parent, int line) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.parent = parent;
            this.line = line;
        }

        public void addVariable(VariableSymbol var) {
            variables.put(var.name, var);
            var.setScopeId(id);
        }

        public VariableSymbol getVariable(String name) {
            return variables.get(name);
        }

        public boolean hasVariable(String name) {
            return variables.containsKey(name);
        }

        public Collection<VariableSymbol> getVariables() {
            return variables.values();
        }

        public void setSlotRange(int start, int end) {
            this.startSlot = start;
            this.endSlot = end;
        }

        public int getStartSlot() { return startSlot; }
        public int getEndSlot() { return endSlot; }

        public int getDepth() {
            int depth = 0;
            Scope s = parent;
            while (s != null) {
                depth++;
                s = s.parent;
            }
            return depth;
        }

        @Override
        public String toString() {
            return "Scope[" + id + ", " + type + ", " + name + ", depth=" + getDepth() + "]";
        }
    }

    // ========================================================================
    // Semantic Exception
    // ========================================================================

    public static class SemanticException extends RuntimeException {
        public final int line;

        public SemanticException(String message, int line) {
            super("Line " + line + ": " + message);
            this.line = line;
        }
    }

    // ========================================================================
    // Symbol Classes
    // ========================================================================

    public static class SymbolTable {
        private final Map<String, ClassSymbol> classes = new LinkedHashMap<>();
        private final Map<String, FunctionSymbol> functions = new LinkedHashMap<>();
        private final Map<String, VariableSymbol> globals = new LinkedHashMap<>();
        private final Map<String, ConstantSymbol> constants = new LinkedHashMap<>();

        public void addClass(ClassSymbol c) { classes.put(c.name, c); }
        public void addFunction(FunctionSymbol f) { functions.put(f.name, f); }
        public void addGlobal(VariableSymbol v) { globals.put(v.name, v); }
        public void addConstant(ConstantSymbol c) { constants.put(c.name, c); }

        public ClassSymbol getClass(String name) { return classes.get(name); }
        public FunctionSymbol getFunction(String name) { return functions.get(name); }
        public VariableSymbol getGlobal(String name) { return globals.get(name); }
        public ConstantSymbol getConstant(String name) { return constants.get(name); }

        public Collection<ClassSymbol> getClasses() { return classes.values(); }
        public Collection<FunctionSymbol> getFunctions() { return functions.values(); }
        public Collection<VariableSymbol> getGlobals() { return globals.values(); }
        public Collection<ConstantSymbol> getConstants() { return constants.values(); }

        public boolean hasClass(String name) { return classes.containsKey(name); }
        public boolean hasFunction(String name) { return functions.containsKey(name); }
    }

    public static class ClassSymbol {
        public final String name;
        public final int line;
        private String baseClass = "Object";
        private boolean isAbstract = false;
        private final List<String> interfaces = new ArrayList<>();
        private final Map<String, FieldSymbol> fields = new LinkedHashMap<>();
        private final Map<String, FunctionSymbol> methods = new LinkedHashMap<>();

        public ClassSymbol(String name, int line) {
            this.name = name;
            this.line = line;
        }

        public void setBaseClass(String base) { this.baseClass = base; }
        public String getBaseClass() { return baseClass; }
        public void setAbstract(boolean isAbstract) { this.isAbstract = isAbstract; }
        public boolean isAbstract() { return isAbstract; }
        public void addInterface(String iface) { interfaces.add(iface); }
        public List<String> getInterfaces() { return interfaces; }

        public void addField(FieldSymbol f) { fields.put(f.name, f); }
        public void addMethod(FunctionSymbol m) { methods.put(m.name, m); }
        public FieldSymbol getField(String name) { return fields.get(name); }
        public FunctionSymbol getMethod(String name) { return methods.get(name); }
        public Collection<FieldSymbol> getFields() { return fields.values(); }
        public Collection<FunctionSymbol> getMethods() { return methods.values(); }
    }

    public static class FunctionSymbol {
        public final String name;
        public final String returnType;
        public final int line;
        private boolean isSub = false;
        private final List<ParameterSymbol> parameters = new ArrayList<>();
        private final Map<String, VariableSymbol> locals = new LinkedHashMap<>();
        private final List<Scope> scopes = new ArrayList<>();  // All scopes within this function

        public FunctionSymbol(String name, String returnType, int line) {
            this.name = name;
            this.returnType = returnType;
            this.line = line;
        }

        public void setSub(boolean sub) { this.isSub = sub; }
        public boolean isSub() { return isSub; }

        private boolean isAbstract = false;
        public void setAbstract(boolean isAbstract) { this.isAbstract = isAbstract; }
        public boolean isAbstract() { return isAbstract; }

        public void addParameter(ParameterSymbol p) { parameters.add(p); }
        public List<ParameterSymbol> getParameters() { return parameters; }

        public void addLocal(VariableSymbol v) { locals.put(v.name, v); }
        public VariableSymbol getLocal(String name) { return locals.get(name); }
        public Collection<VariableSymbol> getLocals() { return locals.values(); }

        public void addScope(Scope s) { scopes.add(s); }
        public List<Scope> getScopes() { return scopes; }
    }

    public static class ParameterSymbol {
        public final String name;
        public final String type;
        public final boolean byRef;

        public ParameterSymbol(String name, String type, boolean byRef) {
            this.name = name;
            this.type = type;
            this.byRef = byRef;
        }
    }

    public static class FieldSymbol {
        public final String name;
        public final String type;
        public final int line;
        private boolean isStatic = false;
        private String accessModifier = "Public";

        public FieldSymbol(String name, String type, int line) {
            this.name = name;
            this.type = type;
            this.line = line;
        }

        public void setStatic(boolean s) { this.isStatic = s; }
        public boolean isStatic() { return isStatic; }
        public void setAccessModifier(String mod) { this.accessModifier = mod; }
        public String getAccessModifier() { return accessModifier; }
    }

    public static class VariableSymbol {
        public final String name;
        public final String type;
        public final int line;
        private int slot = -1;  // JVM local variable slot
        private int scopeId = -1;  // ID of the scope where this variable was declared
        private boolean implicit = false;  // True for loop variables, catch variables, etc.

        public VariableSymbol(String name, String type, int line) {
            this.name = name;
            this.type = type;
            this.line = line;
        }

        public void setSlot(int slot) { this.slot = slot; }
        public int getSlot() { return slot; }

        public void setScopeId(int scopeId) { this.scopeId = scopeId; }
        public int getScopeId() { return scopeId; }

        public void setImplicit(boolean implicit) { this.implicit = implicit; }
        public boolean isImplicit() { return implicit; }
    }

    public static class ConstantSymbol {
        public final String name;
        public final String type;
        public final int line;

        public ConstantSymbol(String name, String type, int line) {
            this.name = name;
            this.type = type;
            this.line = line;
        }
    }
}
