package com.jvmbasic.ir;

import com.jvmbasic.ir.decl.*;
import com.jvmbasic.ir.stmt.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Root node of the IR tree - represents a compilation unit.
 */
public class IRCompilationUnit implements IRNode {
    private final String name;
    private final List<String> imports;
    private final List<IRFunction> functions;
    private final List<IRClass> classes;
    private final List<IRStatement> statements;  // Top-level statements (for main)

    public IRCompilationUnit(String name) {
        this.name = name;
        this.imports = new ArrayList<>();
        this.functions = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.statements = new ArrayList<>();
    }

    public String getName() { return name; }
    public List<String> getImports() { return imports; }
    public List<IRFunction> getFunctions() { return functions; }
    public List<IRClass> getClasses() { return classes; }
    public List<IRStatement> getStatements() { return statements; }

    public void addImport(String imp) { imports.add(imp); }
    public void addFunction(IRFunction func) { functions.add(func); }
    public void addClass(IRClass cls) { classes.add(cls); }
    public void addStatement(IRStatement stmt) { statements.add(stmt); }

    @Override
    public <T> T accept(IRVisitor<T> visitor) {
        return visitor.visitCompilationUnit(this);
    }

    @Override
    public int getLine() { return 1; }

    @Override
    public int getColumn() { return 0; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CompilationUnit(").append(name).append(")\n");

        if (!imports.isEmpty()) {
            sb.append("  Imports:\n");
            for (String imp : imports) {
                sb.append("    import ").append(imp).append("\n");
            }
        }

        if (!functions.isEmpty()) {
            sb.append("  Functions:\n");
            for (IRFunction func : functions) {
                sb.append("    ").append(func).append("\n");
            }
        }

        if (!classes.isEmpty()) {
            sb.append("  Classes:\n");
            for (IRClass cls : classes) {
                sb.append("    ").append(cls).append("\n");
            }
        }

        if (!statements.isEmpty()) {
            sb.append("  Statements:\n");
            for (IRStatement stmt : statements) {
                sb.append("    ").append(stmt).append("\n");
            }
        }

        return sb.toString();
    }
}
