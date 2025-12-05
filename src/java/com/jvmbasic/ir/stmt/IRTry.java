package com.jvmbasic.ir.stmt;

import com.jvmbasic.ir.*;
import java.util.List;
import java.util.ArrayList;

/**
 * IR node for try-catch-finally statements.
 */
public class IRTry implements IRStatement {
    private final List<IRStatement> tryBlock;
    private final List<CatchClause> catchClauses;
    private final List<IRStatement> finallyBlock;  // may be empty
    private final int line;
    private final int column;

    public record CatchClause(String variable, IRType exceptionType, List<IRStatement> body) {}

    public IRTry(int line, int column) {
        this.tryBlock = new ArrayList<>();
        this.catchClauses = new ArrayList<>();
        this.finallyBlock = new ArrayList<>();
        this.line = line;
        this.column = column;
    }

    public List<IRStatement> getTryBlock() { return tryBlock; }
    public List<CatchClause> getCatchClauses() { return catchClauses; }
    public List<IRStatement> getFinallyBlock() { return finallyBlock; }

    public void addTry(IRStatement stmt) { tryBlock.add(stmt); }
    public void addTryStatement(IRStatement stmt) { tryBlock.add(stmt); }
    public void addCatch(String var, IRType type, List<IRStatement> body) { catchClauses.add(new CatchClause(var, type, body)); }
    public void addFinally(IRStatement stmt) { finallyBlock.add(stmt); }
    public void addFinallyStatement(IRStatement stmt) { finallyBlock.add(stmt); }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitTry(this); }
    @Override
    public int getLine() { return line; }
    @Override
    public int getColumn() { return column; }
    @Override
    public String toString() { return "try { ... }"; }
}
