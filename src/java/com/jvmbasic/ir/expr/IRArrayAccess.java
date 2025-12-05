package com.jvmbasic.ir.expr;

import com.jvmbasic.ir.*;

/**
 * IR node for array access (arr[index]).
 */
public record IRArrayAccess(IRExpression array, IRExpression index, IRType elementType, int line, int column) implements IRExpression {

    @Override
    public IRType getType() { return elementType; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitArrayAccess(this); }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() { return array + "[" + index + "]"; }
}
