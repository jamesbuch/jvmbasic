package com.jvmbasic.ir.stmt;

import com.jvmbasic.ir.*;
import com.jvmbasic.ir.expr.*;

/**
 * IR node for return statements.
 */
public class IRReturn implements IRStatement {
    private final IRExpression value;  // may be null for void returns
    private final int line;
    private final int column;

    public IRReturn(IRExpression value, int line, int column) {
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public IRExpression getValue() { return value; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitReturn(this); }
    @Override
    public int getLine() { return line; }
    @Override
    public int getColumn() { return column; }
    @Override
    public String toString() { return "return" + (value != null ? " " + value : ""); }
}
