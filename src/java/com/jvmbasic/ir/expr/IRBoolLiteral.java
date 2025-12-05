package com.jvmbasic.ir.expr;
import com.jvmbasic.ir.*;

public record IRBoolLiteral(boolean value, int line, int column) implements IRExpression {
    @Override public IRType getType() { return IRType.Primitive.BOOLEAN; }
    @Override public <T> T accept(IRVisitor<T> visitor) { return visitor.visitBoolLiteral(this); }
    @Override public int getLine() { return line; }
    @Override public int getColumn() { return column; }
    @Override public String toString() { return String.valueOf(value); }
}
