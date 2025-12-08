package com.jvmbasic.ir.decl;

import com.jvmbasic.ir.*;
import com.jvmbasic.ir.stmt.*;

import java.util.List;
import java.util.ArrayList;

/**
 * IR node for function/sub/method declarations.
 */
public class IRFunction implements IRNode {
    /** Access modifier for class methods */
    public enum Access { PUBLIC, PRIVATE, PROTECTED, PACKAGE }

    private final String name;
    private final List<IRParameter> parameters;
    private final IRType returnType;
    private final List<IRStatement> body;
    private final boolean isStatic;
    private final boolean isOverride;
    private final boolean isConstructor;
    private final boolean isAbstract;
    private final Access access;
    private final int line;
    private final int column;

    public IRFunction(String name, IRType returnType, int line, int column) {
        this(name, returnType, true, false, false, false, Access.PACKAGE, line, column);
    }

    public IRFunction(String name, IRType returnType, boolean isStatic, boolean isOverride,
                      boolean isConstructor, Access access, int line, int column) {
        this(name, returnType, isStatic, isOverride, isConstructor, false, access, line, column);
    }

    public IRFunction(String name, IRType returnType, boolean isStatic, boolean isOverride,
                      boolean isConstructor, boolean isAbstract, Access access, int line, int column) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
        this.body = new ArrayList<>();
        this.isStatic = isStatic;
        this.isOverride = isOverride;
        this.isConstructor = isConstructor;
        this.isAbstract = isAbstract;
        this.access = access;
        this.line = line;
        this.column = column;
    }

    public String getName() { return name; }
    public List<IRParameter> getParameters() { return parameters; }
    public IRType getReturnType() { return returnType; }
    public List<IRStatement> getBody() { return body; }
    public boolean isStatic() { return isStatic; }
    public boolean isOverride() { return isOverride; }
    public boolean isConstructor() { return isConstructor; }
    public boolean isAbstract() { return isAbstract; }
    public Access getAccess() { return access; }

    public void addParameter(IRParameter param) { parameters.add(param); }
    public void addStatement(IRStatement stmt) { body.add(stmt); }

    public String getDescriptor() {
        StringBuilder sb = new StringBuilder("(");
        for (IRParameter param : parameters) {
            sb.append(param.getType().descriptor());
        }
        sb.append(")").append(returnType.descriptor());
        return sb.toString();
    }

    @Override
    public <T> T accept(IRVisitor<T> visitor) {
        return visitor.visitFunction(this);
    }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Function ").append(name).append("(");
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(parameters.get(i));
        }
        sb.append(") -> ").append(returnType);
        return sb.toString();
    }
}
