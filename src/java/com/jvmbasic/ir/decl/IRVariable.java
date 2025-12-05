package com.jvmbasic.ir.decl;

import com.jvmbasic.ir.*;

/**
 * IR node for variable information.
 */
public class IRVariable implements IRNode {
    private final String name;
    private final IRType type;
    private final boolean isConst;
    private final int line;
    private final int column;
    private int slot = -1;  // JVM local variable slot

    public IRVariable(String name, IRType type, boolean isConst, int line, int column) {
        this.name = name;
        this.type = type;
        this.isConst = isConst;
        this.line = line;
        this.column = column;
    }

    public String getName() { return name; }
    public IRType getType() { return type; }
    public boolean isConst() { return isConst; }
    public int getSlot() { return slot; }
    public void setSlot(int slot) { this.slot = slot; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) {
        return visitor.visitVariable(this);
    }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return (isConst ? "const " : "var ") + name + ": " + type;
    }
}
