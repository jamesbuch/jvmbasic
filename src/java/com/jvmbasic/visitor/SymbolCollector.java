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

    public SymbolTable getSymbols() {
        return symbols;
    }

    // ========================================================================
    // Class Declarations
    // ========================================================================

    @Override
    public void enterClassDeclaration(JvmBasicParser.ClassDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();

        ClassSymbol classSymbol = new ClassSymbol(name, line);

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
    }

    @Override
    public void exitClassDeclaration(JvmBasicParser.ClassDeclarationContext ctx) {
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
    }

    @Override
    public void exitFunctionDeclaration(JvmBasicParser.FunctionDeclarationContext ctx) {
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
    }

    @Override
    public void exitSubDeclaration(JvmBasicParser.SubDeclarationContext ctx) {
        currentFunction = null;
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
            // Local variable
            if (currentClass != null) {
                symbols.getClass(currentClass).getMethod(currentFunction).addLocal(var);
            } else {
                symbols.getFunction(currentFunction).addLocal(var);
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
        private final List<String> interfaces = new ArrayList<>();
        private final Map<String, FieldSymbol> fields = new LinkedHashMap<>();
        private final Map<String, FunctionSymbol> methods = new LinkedHashMap<>();

        public ClassSymbol(String name, int line) {
            this.name = name;
            this.line = line;
        }

        public void setBaseClass(String base) { this.baseClass = base; }
        public String getBaseClass() { return baseClass; }
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

        public FunctionSymbol(String name, String returnType, int line) {
            this.name = name;
            this.returnType = returnType;
            this.line = line;
        }

        public void setSub(boolean sub) { this.isSub = sub; }
        public boolean isSub() { return isSub; }

        public void addParameter(ParameterSymbol p) { parameters.add(p); }
        public List<ParameterSymbol> getParameters() { return parameters; }

        public void addLocal(VariableSymbol v) { locals.put(v.name, v); }
        public VariableSymbol getLocal(String name) { return locals.get(name); }
        public Collection<VariableSymbol> getLocals() { return locals.values(); }
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

        public VariableSymbol(String name, String type, int line) {
            this.name = name;
            this.type = type;
            this.line = line;
        }

        public void setSlot(int slot) { this.slot = slot; }
        public int getSlot() { return slot; }
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
