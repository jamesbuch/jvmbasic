package com.jvmbasic.ir.expr;
import com.jvmbasic.ir.*;

public record IRFloatLiteral(float value, int line, int column) implements IRExpression {
    @Override public IRType getType() { return IRType.Primitive.FLOAT; }
    @Override public <T> T accept(IRVisitor<T> visitor) { return visitor.visitFloatLiteral(this); }
    @Override public int getLine() { return line; }
    @Override public int getColumn() { return column; }
    @Override public String toString() { return value + "F"; }
}
