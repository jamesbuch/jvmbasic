package com.jvmbasic.ir.expr;

import com.jvmbasic.ir.*;

/**
 * IR node for array creation (new Type[size]).
 */
public record IRNewArray(IRType elementType, IRExpression size, int line, int column) implements IRExpression {

    @Override
    public IRType getType() { return new IRType.Array(elementType); }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitNewArray(this); }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() { return "new " + elementType + "[" + size + "]"; }
}
