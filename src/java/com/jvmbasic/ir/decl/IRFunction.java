package com.jvmbasic.ir.decl;

import com.jvmbasic.ir.*;
import com.jvmbasic.ir.stmt.*;

import java.util.List;
import java.util.ArrayList;

/**
 * IR node for function/sub declarations.
 */
public class IRFunction implements IRNode {
    private final String name;
    private final List<IRParameter> parameters;
    private final IRType returnType;
    private final List<IRStatement> body;
    private final boolean isStatic;
    private final int line;
    private final int column;

    public IRFunction(String name, IRType returnType, int line, int column) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
        this.body = new ArrayList<>();
        this.isStatic = true;  // Module-level functions are static
        this.line = line;
        this.column = column;
    }

    public String getName() { return name; }
    public List<IRParameter> getParameters() { return parameters; }
    public IRType getReturnType() { return returnType; }
    public List<IRStatement> getBody() { return body; }
    public boolean isStatic() { return isStatic; }

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
