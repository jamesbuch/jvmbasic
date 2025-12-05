package com.jvmbasic.ir.expr;

import com.jvmbasic.ir.*;

/**
 * IR node for identifier references (variable names).
 */
public record IRIdentifier(String name, IRType type, int slot, int line, int column) implements IRExpression {
    @Override
    public IRType getType() { return type; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitIdentifier(this); }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() { return name; }
}
