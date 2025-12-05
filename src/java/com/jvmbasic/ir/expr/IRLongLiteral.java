package com.jvmbasic.ir.expr;
import com.jvmbasic.ir.*;

public record IRLongLiteral(long value, int line, int column) implements IRExpression {
    @Override public IRType getType() { return IRType.Primitive.LONG; }
    @Override public <T> T accept(IRVisitor<T> visitor) { return visitor.visitLongLiteral(this); }
    @Override public int getLine() { return line; }
    @Override public int getColumn() { return column; }
    @Override public String toString() { return value + "L"; }
}
