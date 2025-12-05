package com.jvmbasic.ir.expr;

import com.jvmbasic.ir.IRNode;
import com.jvmbasic.ir.IRType;

/**
 * Base interface for all IR expression nodes.
 */
public interface IRExpression extends IRNode {
    /**
     * Get the resolved type of this expression
     */
    IRType getType();
}
