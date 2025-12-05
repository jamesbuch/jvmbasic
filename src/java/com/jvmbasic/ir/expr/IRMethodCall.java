package com.jvmbasic.ir.expr;

import com.jvmbasic.ir.*;
import java.util.List;

/**
 * IR node for method calls on objects (including static method calls like Console.WriteLine).
 */
public record IRMethodCall(
    IRExpression receiver,  // null for static calls
    String className,       // for static calls like "Console"
    String methodName,
    List<IRExpression> arguments,
    IRType returnType,
    boolean isStatic,
    int line,
    int column
) implements IRExpression {

    @Override
    public IRType getType() { return returnType; }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitMethodCall(this); }

    @Override
    public int getLine() { return line; }

    @Override
    public int getColumn() { return column; }

    @Override
    public String toString() {
        String target = receiver != null ? receiver.toString() : className;
        return target + "." + methodName + "(" +
               String.join(", ", arguments.stream().map(Object::toString).toList()) + ")";
    }
}
