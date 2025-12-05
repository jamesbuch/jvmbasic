package com.jvmbasic.ir.stmt;

import com.jvmbasic.ir.*;
import com.jvmbasic.ir.expr.*;

/**
 * IR node for expression statements (e.g., method calls).
 */
public class IRExprStmt implements IRStatement {
    private final IRExpression expression;
    private final int line;
    private final int column;

    public IRExprStmt(IRExpression expression, int line, int column) {
        this.expression = expression;
        this.line = line;
        this.column = column;
    }

    public IRExpression getExpression() { return expression; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitExprStmt(this); }
    @Override
    public int getLine() { return line; }
    @Override
    public int getColumn() { return column; }
    @Override
    public String toString() { return expression.toString(); }
}
