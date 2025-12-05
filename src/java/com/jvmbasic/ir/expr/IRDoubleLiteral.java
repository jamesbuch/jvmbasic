package com.jvmbasic.ir.expr;
import com.jvmbasic.ir.*;

public record IRDoubleLiteral(double value, int line, int column) implements IRExpression {
    @Override public IRType getType() { return IRType.Primitive.DOUBLE; }
    @Override public <T> T accept(IRVisitor<T> visitor) { return visitor.visitDoubleLiteral(this); }
    @Override public int getLine() { return line; }
    @Override public int getColumn() { return column; }
    @Override public String toString() { return String.valueOf(value); }
}
