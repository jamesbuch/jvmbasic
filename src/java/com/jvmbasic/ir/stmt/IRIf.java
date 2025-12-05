package com.jvmbasic.ir.stmt;

import com.jvmbasic.ir.*;
import com.jvmbasic.ir.expr.*;

import java.util.List;
import java.util.ArrayList;

/**
 * IR node for if statements.
 */
public class IRIf implements IRStatement {
    private final IRExpression condition;
    private final List<IRStatement> thenBranch;
    private final List<ElseIf> elseIfs;
    private final List<IRStatement> elseBranch;  // may be empty
    private final int line;
    private final int column;

    public record ElseIf(IRExpression condition, List<IRStatement> body) {}

    public IRIf(IRExpression condition, int line, int column) {
        this.condition = condition;
        this.thenBranch = new ArrayList<>();
        this.elseIfs = new ArrayList<>();
        this.elseBranch = new ArrayList<>();
        this.line = line;
        this.column = column;
    }

    public IRExpression getCondition() { return condition; }
    public List<IRStatement> getThenBranch() { return thenBranch; }
    public List<ElseIf> getElseIfs() { return elseIfs; }
    public List<IRStatement> getElseBranch() { return elseBranch; }

    public void addThen(IRStatement stmt) { thenBranch.add(stmt); }
    public void addElseIf(IRExpression cond, List<IRStatement> body) { elseIfs.add(new ElseIf(cond, body)); }
    public void addElse(IRStatement stmt) { elseBranch.add(stmt); }

    @Override
    public <T> T accept(IRVisitor<T> visitor) {
        return visitor.visitIf(this);
    }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return "if (" + condition + ") { ... }";
    }
}
