package com.jvmbasic.ir.expr;
import com.jvmbasic.ir.*;

public record IRIntLiteral(int value, int line, int column) implements IRExpression {
    @Override public IRType getType() { return IRType.Primitive.INT; }
    @Override public <T> T accept(IRVisitor<T> visitor) { return visitor.visitIntLiteral(this); }
    @Override public int getLine() { return line; }
    @Override public int getColumn() { return column; }
    @Override public String toString() { return String.valueOf(value); }
}
