package com.jvmbasic.semantic;

/**
 * Represents a semantic error found during analysis.
 */
public class SemanticError {

    private final int line;
    private final int column;
    private final String message;

    public SemanticError(int line, int column, String message) {
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
        return "Error at " + line + ":" + column + ": " + message;
    }
}
