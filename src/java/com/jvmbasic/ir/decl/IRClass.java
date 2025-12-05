package com.jvmbasic.ir.decl;

import com.jvmbasic.ir.*;

import java.util.List;
import java.util.ArrayList;

/**
 * IR node for class declarations.
 */
public class IRClass implements IRNode {
    private final String name;
    private final String superClass;
    private final List<String> interfaces;
    private final List<IRVariable> fields;
    private final List<IRFunction> methods;
    private final boolean isAbstract;
    private final int line;
    private final int column;

    public IRClass(String name, String superClass, boolean isAbstract, int line, int column) {
        this.name = name;
        this.superClass = superClass != null ? superClass : "java.lang.Object";
        this.isAbstract = isAbstract;
        this.interfaces = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.line = line;
        this.column = column;
    }

    public String getName() { return name; }
    public String getSuperClass() { return superClass; }
    public List<String> getInterfaces() { return interfaces; }
    public List<IRVariable> getFields() { return fields; }
    public List<IRFunction> getMethods() { return methods; }
    public boolean isAbstract() { return isAbstract; }

    public void addInterface(String iface) { interfaces.add(iface); }
    public void addField(IRVariable field) { fields.add(field); }
    public void addMethod(IRFunction method) { methods.add(method); }

    @Override
    public <T> T accept(IRVisitor<T> visitor) {
        return visitor.visitClass(this);
    }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return (isAbstract ? "abstract " : "") + "class " + name +
               " extends " + superClass +
               (interfaces.isEmpty() ? "" : " implements " + String.join(", ", interfaces));
    }
}
