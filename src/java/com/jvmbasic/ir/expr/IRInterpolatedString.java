package com.jvmbasic.ir.expr;
import com.jvmbasic.ir.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * IR node for interpolated string literals like $"Hello {name}".
 * Parts contains alternating literal strings and expressions.
 */
public record IRInterpolatedString(List<IRExpression> parts, int line, int column) implements IRExpression {
    @Override public IRType getType() { return IRType.Reference.STRING; }
    @Override public <T> T accept(IRVisitor<T> visitor) { return visitor.visitInterpolatedString(this); }
    @Override public int getLine() { return line; }
    @Override public int getColumn() { return column; }
    @Override public String toString() {
        return "$\"" + parts.stream()
            .map(p -> p instanceof IRStringLiteral s ? s.value() : "{" + p + "}")
            .collect(Collectors.joining()) + "\"";
    }
}
