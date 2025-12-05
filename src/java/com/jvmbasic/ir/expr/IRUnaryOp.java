package com.jvmbasic.ir.expr;

import com.jvmbasic.ir.*;

/**
 * IR node for unary operations.
 */
public record IRUnaryOp(Operator op, IRExpression operand, IRType type, int line, int column) implements IRExpression {

    public enum Operator {
        PLUS,      // +x (identity)
        MINUS,     // -x (negation)
        NEG,       // -x (alias for MINUS)
        NOT,       // Not x (logical)
        BIT_NOT    // ~x (bitwise)
    }

    @Override
    public IRType getType() { return type; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitUnaryOp(this); }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() { return "(" + op + " " + operand + ")"; }
}
