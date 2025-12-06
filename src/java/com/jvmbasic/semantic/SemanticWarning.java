package com.jvmbasic.semantic;

/**
 * Represents a semantic warning found during analysis.
 */
public class SemanticWarning {

    private final int line;
    private final int column;
    private final String message;

    public SemanticWarning(int line, int column, String message) {
        this.line = line;
        this.column = column;
        this.message = message;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Warning at " + line + ":" + column + ": " + message;
    }
}
