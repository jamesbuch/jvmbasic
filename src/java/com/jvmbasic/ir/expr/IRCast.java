package com.jvmbasic.ir.expr;

import com.jvmbasic.ir.*;

/**
 * IR node for type casts.
 */
public record IRCast(IRExpression expression, IRType targetType, int line, int column) implements IRExpression {

    @Override
    public IRType getType() { return targetType; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitCast(this); }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() { return "(" + targetType + ")" + expression; }
}
