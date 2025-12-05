package com.jvmbasic.ir.expr;
import com.jvmbasic.ir.*;

public record IRNullLiteral(IRType targetType, int line, int column) implements IRExpression {
    @Override public IRType getType() { return targetType != null ? targetType : IRType.Reference.OBJECT; }
    @Override public <T> T accept(IRVisitor<T> visitor) { return visitor.visitNullLiteral(this); }
    @Override public int getLine() { return line; }
    @Override public int getColumn() { return column; }
    @Override public String toString() { return "nil"; }
}
