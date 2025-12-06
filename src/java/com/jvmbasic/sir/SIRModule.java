package com.jvmbasic.sir;

import java.util.ArrayList;
import java.util.List;

/**
 * A compilation unit in Stack IR form.
 *
 * Contains all functions and classes lowered to stack-based IR.
 */
public class SIRModule {
    private final String name;
    private final List<SIRFunction> functions;
    private final List<SIRClass> classes;
    private SIRFunction mainFunction;  // Entry point (generated from top-level statements)

    public SIRModule(String name) {
        this.name = name;
        this.functions = new ArrayList<>();
        this.classes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<SIRFunction> getFunctions() {
        return functions;
    }

    public List<SIRClass> getClasses() {
        return classes;
    }

    public SIRFunction getMainFunction() {
        return mainFunction;
    }

    public void addFunction(SIRFunction func) {
        functions.add(func);
    }

    public void addClass(SIRClass cls) {
        classes.add(cls);
    }

    public void setMainFunction(SIRFunction main) {
        this.mainFunction = main;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("; SIR Module: ").append(name).append("\n");
        sb.append("; ").append("=".repeat(60)).append("\n\n");

        // Classes
        for (SIRClass cls : classes) {
            sb.append(cls).append("\n");
        }

        // Functions
        for (SIRFunction func : functions) {
            sb.append(func).append("\n");
        }

        // Main function
        if (mainFunction != null) {
            sb.append("; Entry point\n");
            sb.append(mainFunction).append("\n");
        }

        return sb.toString();
    }
}
