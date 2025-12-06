package com.jvmbasic.semantic;

import com.jvmbasic.ir.IRType;

import java.util.List;

/**
 * Represents a symbol in the symbol table.
 */
public class Symbol {

    public enum Kind {
        VARIABLE,
        FUNCTION,
        CLASS,
        PARAMETER
    }

    private final String name;
    private final Kind kind;
    private final IRType type;
    private boolean initialized;

    // For functions
    private List<IRType> parameterTypes;

    // For classes
    private Object classDecl;  // IRClass

    public Symbol(String name, Kind kind, IRType type) {
        this.name = name;
        this.kind = kind;
        this.type = type;
        this.initialized = false;
    }

    public String getName() {
        return name;
    }

    public Kind getKind() {
        return kind;
    }

    public IRType getType() {
        return type;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public List<IRType> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<IRType> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object getClassDecl() {
        return classDecl;
    }

    public void setClassDecl(Object classDecl) {
        this.classDecl = classDecl;
    }

    @Override
    public String toString() {
        return kind + " " + name + ": " + type;
    }
}
