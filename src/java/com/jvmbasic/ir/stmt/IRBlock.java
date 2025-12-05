package com.jvmbasic.ir.stmt;

import com.jvmbasic.ir.*;
import java.util.List;
import java.util.ArrayList;

/**
 * IR node for a block of statements.
 */
public class IRBlock implements IRStatement {
    private final List<IRStatement> statements;
    private final int line;
    private final int column;

    public IRBlock(int line, int column) {
        this.statements = new ArrayList<>();
        this.line = line;
        this.column = column;
    }

    public List<IRStatement> getStatements() { return statements; }
    public void addStatement(IRStatement stmt) { statements.add(stmt); }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitBlock(this); }
    @Override
    public int getLine() { return line; }
    @Override
    public int getColumn() { return column; }
    @Override
    public String toString() { return "{ " + statements.size() + " statements }"; }
}
