package com.jvmbasic.ir.expr;

import com.jvmbasic.ir.*;

/**
 * IR node for binary operations.
 */
public record IRBinaryOp(Operator op, IRExpression left, IRExpression right, IRType type, int line, int column) implements IRExpression {

    public enum Operator {
        // Arithmetic
        ADD, SUB, MUL, DIV, MOD, POWER, INTDIV,
        // Comparison
        EQ, NE, LT, LE, GT, GE,
        // Logical
        AND, OR, XOR,
        // Bitwise
        BIT_AND, BIT_OR, BIT_XOR, SHL, SHR,
        // String
        CONCAT
    }

    @Override
    public IRType getType() { return type; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitBinaryOp(this); }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() { return "(" + left + " " + op + " " + right + ")"; }
}
