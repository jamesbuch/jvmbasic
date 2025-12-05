package com.jvmbasic.ir.stmt;

import com.jvmbasic.ir.*;
import com.jvmbasic.ir.expr.*;
import java.util.List;
import java.util.ArrayList;

/**
 * IR node for while loops.
 */
public class IRWhile implements IRStatement {
    private final IRExpression condition;
    private final List<IRStatement> body;
    private final int line;
    private final int column;

    public IRWhile(IRExpression condition, int line, int column) {
        this.condition = condition;
        this.body = new ArrayList<>();
        this.line = line;
        this.column = column;
    }

    public IRExpression getCondition() { return condition; }
    public List<IRStatement> getBody() { return body; }
    public void addStatement(IRStatement stmt) { body.add(stmt); }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitWhile(this); }
    @Override
    public int getLine() { return line; }
    @Override
    public int getColumn() { return column; }
    @Override
    public String toString() { return "while (" + condition + ") { ... }"; }
}
