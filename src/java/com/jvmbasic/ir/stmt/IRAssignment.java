package com.jvmbasic.ir.stmt;

import com.jvmbasic.ir.*;
import com.jvmbasic.ir.expr.*;

/**
 * IR node for assignment statements.
 */
public class IRAssignment implements IRStatement {
    private final IRExpression target;  // Variable, member access, or array access
    private final IRExpression value;
    private final Operator op;
    private final int line;
    private final int column;

    public enum Operator {
        ASSIGN,     // =
        ADD,        // +=
        SUB,        // -=
        MUL,        // *=
        DIV         // /=
    }

    public IRAssignment(IRExpression target, IRExpression value, Operator op, int line, int column) {
        this.target = target;
        this.value = value;
        this.op = op;
        this.line = line;
        this.column = column;
    }

    public IRExpression getTarget() { return target; }
    public IRExpression getValue() { return value; }
    public Operator getOp() { return op; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) {
        return visitor.visitAssignment(this);
    }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() {
        String opStr = switch (op) {
            case ASSIGN -> "=";
            case ADD -> "+=";
            case SUB -> "-=";
            case MUL -> "*=";
            case DIV -> "/=";
        };
        return target + " " + opStr + " " + value;
    }
}
