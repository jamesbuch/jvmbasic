package com.jvmbasic.ir.stmt;

import com.jvmbasic.ir.*;
import com.jvmbasic.ir.expr.*;

/**
 * IR node for variable declarations.
 */
public class IRVarDecl implements IRStatement {
    private final String name;
    private final IRType type;
    private final IRExpression initializer;  // may be null
    private final int line;
    private final int column;
    private int slot = -1;  // JVM local variable slot

    public IRVarDecl(String name, IRType type, IRExpression initializer, int line, int column) {
        this.name = name;
        this.type = type;
        this.initializer = initializer;
        this.line = line;
        this.column = column;
    }

    public String getName() { return name; }
    public IRType getType() { return type; }
    public IRExpression getInitializer() { return initializer; }
    public int getSlot() { return slot; }
    public void setSlot(int slot) { this.slot = slot; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) {
        return visitor.visitVarDecl(this);
    }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return "var " + name + ": " + type +
               (initializer != null ? " = " + initializer : "");
    }
}
