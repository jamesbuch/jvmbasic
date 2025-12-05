package com.jvmbasic.ir.expr;

import com.jvmbasic.ir.*;
import java.util.List;

/**
 * IR node for object creation (new ClassName(args)).
 */
public record IRNewObject(IRType type, List<IRExpression> arguments, int line, int column) implements IRExpression {

    @Override
    public IRType getType() { return type; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitNewObject(this); }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return "new " + type + "(" + String.join(", ", arguments.stream().map(Object::toString).toList()) + ")";
    }
}
