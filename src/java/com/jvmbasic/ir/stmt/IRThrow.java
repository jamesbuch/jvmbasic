package com.jvmbasic.ir.stmt;

import com.jvmbasic.ir.*;
import com.jvmbasic.ir.expr.*;

/**
 * IR node for throw statements.
 */
public class IRThrow implements IRStatement {
    private final IRExpression exception;
    private final int line;
    private final int column;

    public IRThrow(IRExpression exception, int line, int column) {
        this.exception = exception;
        this.line = line;
        this.column = column;
    }

    public IRExpression getException() { return exception; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitThrow(this); }
    @Override
    public int getLine() { return line; }
    @Override
    public int getColumn() { return column; }
    @Override
    public String toString() { return "throw " + exception; }
}
