package com.jvmbasic.ir.expr;

import com.jvmbasic.ir.*;
import java.util.List;

/**
 * IR node for function calls (static method calls to user-defined functions).
 */
public record IRCall(String name, List<IRExpression> arguments, IRType returnType, int line, int column) implements IRExpression {

    @Override
    public IRType getType() { return returnType; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitCall(this); }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return name + "(" + String.join(", ", arguments.stream().map(Object::toString).toList()) + ")";
    }
}
