package com.jvmbasic.ir;

/**
 * Base interface for all IR nodes.
 *
 * The IR (Intermediate Representation) is a simplified AST that:
 * - Has already resolved types
 * - Has simplified expression trees
 * - Is ready for code generation
 */
public interface IRNode {
    /**
     * Accept a visitor for traversal
     */
    <T> T accept(IRVisitor<T> visitor);

    /**
     * Get the source line number for error reporting
     */
    int getLine();

    /**
     * Get the source column for error reporting
     */
    int getColumn();
}
