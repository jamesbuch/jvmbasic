package com.jvmbasic.ir.decl;

import com.jvmbasic.ir.*;

/**
 * IR node for function parameters.
 */
public class IRParameter implements IRNode {
    private final String name;
    private final IRType type;
    private final boolean byRef;
    private final int line;
    private final int column;
    private int slot = -1;  // JVM local variable slot

    public IRParameter(String name, IRType type, boolean byRef, int line, int column) {
        this.name = name;
        this.type = type;
        this.byRef = byRef;
        this.line = line;
        this.column = column;
    }

    public String getName() { return name; }
    public IRType getType() { return type; }
    public boolean isByRef() { return byRef; }
    public int getSlot() { return slot; }
    public void setSlot(int slot) { this.slot = slot; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) {
        return visitor.visitParameter(this);
    }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return (byRef ? "ByRef " : "") + name + ": " + type;
    }
}
