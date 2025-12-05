package com.jvmbasic.ir.expr;

import com.jvmbasic.ir.*;

/**
 * IR node for member access (obj.field).
 */
public record IRMemberAccess(IRExpression object, String memberName, IRType type, int line, int column) implements IRExpression {

    @Override
    public IRType getType() { return type; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitMemberAccess(this); }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() { return object + "." + memberName; }
}
