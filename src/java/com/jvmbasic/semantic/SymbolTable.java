package com.jvmbasic.semantic;

import com.jvmbasic.ir.IRType;
import com.jvmbasic.ir.decl.IRClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Symbol table for a scope in the program.
 * Supports nested scopes with parent references.
 */
public class SymbolTable {

    private final String name;
    private final SymbolTable parent;
    private final Map<String, Symbol> symbols = new HashMap<>();

    public SymbolTable(String name) {
        this(name, null);
    }

    public SymbolTable(String name, SymbolTable parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public SymbolTable getParent() {
        return parent;
    }

    /**
     * Define a variable in this scope.
     */
    public void defineVariable(String name, IRType type, boolean initialized) {
        Symbol sym = new Symbol(name, Symbol.Kind.VARIABLE, type);
        sym.setInitialized(initialized);
        symbols.put(name, sym);
    }

    /**
     * Define a function in this scope.
     */
    public void defineFunction(String name, IRType returnType, List<IRType> paramTypes) {
        Symbol sym = new Symbol(name, Symbol.Kind.FUNCTION, returnType);
        sym.setParameterTypes(paramTypes);
        sym.setInitialized(true);
        symbols.put(name, sym);
    }

    /**
     * Define a class in this scope.
     */
    public void defineClass(String name, IRClass classDecl) {
        Symbol sym = new Symbol(name, Symbol.Kind.CLASS, new IRType.Reference(name));
        sym.setClassDecl(classDecl);
        sym.setInitialized(true);
        symbols.put(name, sym);
    }

    /**
     * Check if a symbol exists in this scope only (not parent).
     */
    public boolean hasLocalSymbol(String name) {
        return symbols.containsKey(name);
    }

    /**
     * Check if a symbol exists in this scope or any parent scope.
     */
    public boolean hasSymbol(String name) {
        if (symbols.containsKey(name)) {
            return true;
        }
        if (parent != null) {
            return parent.hasSymbol(name);
        }
        return false;
    }

    /**
     * Get a symbol from this scope only.
     */
    public Symbol getSymbol(String name) {
        return symbols.get(name);
    }

    /**
     * Lookup a symbol in this scope and parent scopes.
     */
    public Symbol lookup(String name) {
        Symbol sym = symbols.get(name);
        if (sym != null) {
            return sym;
        }
        if (parent != null) {
            return parent.lookup(name);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SymbolTable[").append(name).append("] {\n");
        for (Symbol sym : symbols.values()) {
            sb.append("  ").append(sym).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
