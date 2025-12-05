package com.jvmbasic.ir.stmt;

import com.jvmbasic.ir.*;
import com.jvmbasic.ir.expr.*;
import java.util.List;
import java.util.ArrayList;

/**
 * IR node for for-each loops (for each x in collection).
 */
public class IRForEach implements IRStatement {
    private final String variable;
    private final IRType elementType;
    private final IRExpression collection;
    private final List<IRStatement> body;
    private final int line;
    private final int column;

    public IRForEach(String variable, IRType elementType, IRExpression collection, int line, int column) {
        this.variable = variable;
        this.elementType = elementType;
        this.collection = collection;
        this.body = new ArrayList<>();
        this.line = line;
        this.column = column;
    }

    public String getVariable() { return variable; }
    public IRType getElementType() { return elementType; }
    public IRExpression getCollection() { return collection; }
    public List<IRStatement> getBody() { return body; }
    public void addStatement(IRStatement stmt) { body.add(stmt); }

    @Override
    public <T> T accept(IRVisitor<T> visitor) { return visitor.visitForEach(this); }
    @Override
    public int getLine() { return line; }
    @Override
    public int getColumn() { return column; }
    @Override
    public String toString() { return "for each " + variable + " in " + collection + " { ... }"; }
}
