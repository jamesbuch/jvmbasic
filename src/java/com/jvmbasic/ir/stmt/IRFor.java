package com.jvmbasic.ir.stmt;

import com.jvmbasic.ir.*;
import com.jvmbasic.ir.expr.*;
import java.util.List;
import java.util.ArrayList;

/**
 * IR node for for loops (for i = start to end step s).
 */
public class IRFor implements IRStatement {
    private final String variable;
    private final IRExpression start;
    private final IRExpression end;
    private final IRExpression step;  // may be null (default 1)
    private final List<IRStatement> body;
    private final int line;
    private final int column;

    public IRFor(String variable, IRExpression start, IRExpression end, IRExpression step, int line, int column) {
        this.variable = variable;
        this.start = start;
        this.end = end;
        this.step = step;
        this.body = new ArrayList<>();
        this.line = line;
        this.column = column;
    }

    public String getVariable() { return variable; }
    public IRExpression getStart() { return start; }
    public IRExpression getEnd() { return end; }
    public IRExpression getStep() { return step; }
    public List<IRStatement> getBody() { return body; }
    public void addStatement(IRStatement stmt) { body.add(stmt); }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitFor(this); }
    @Override
    public int getLine() { return line; }
    @Override
    public int getColumn() { return column; }
    @Override
    public String toString() { return "for " + variable + " = " + start + " to " + end + " { ... }"; }
}
