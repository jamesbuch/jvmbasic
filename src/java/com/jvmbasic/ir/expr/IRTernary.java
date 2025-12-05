package com.jvmbasic.ir.expr;

import com.jvmbasic.ir.*;

/**
 * IR node for ternary conditional expressions (condition ? thenExpr : elseExpr).
 */
public record IRTernary(
    IRExpression condition,
    IRExpression thenExpr,
    IRExpression elseExpr,
    IRType type,
    int line,
    int column
) implements IRExpression {

    @Override
    public IRType getType() { return type; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitTernary(this); }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return "(" + condition + " ? " + thenExpr + " : " + elseExpr + ")";
    }
}
