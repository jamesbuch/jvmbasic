package com.jvmbasic.sir;

import java.util.ArrayList;
import java.util.List;

/**
 * A basic block in the SIR control flow graph.
 *
 * A basic block is a sequence of instructions with:
 * - Single entry point (the first instruction)
 * - Single exit point (the last instruction)
 * - No branches in the middle
 *
 * Control flow is only at the boundaries (jumps to other blocks).
 */
public class SIRBasicBlock {
    private final String label;
    private final List<SIRInstruction> instructions;
    private final List<SIRBasicBlock> predecessors;
    private final List<SIRBasicBlock> successors;

    public SIRBasicBlock(String label) {
        this.label = label;
        this.instructions = new ArrayList<>();
        this.predecessors = new ArrayList<>();
        this.successors = new ArrayList<>();
    }

    public String getLabel() {
        return label;
    }

    public List<SIRInstruction> getInstructions() {
        return instructions;
    }

    public List<SIRBasicBlock> getPredecessors() {
        return predecessors;
    }

    public List<SIRBasicBlock> getSuccessors() {
        return successors;
    }

    public void addInstruction(SIRInstruction inst) {
        instructions.add(inst);
    }

    public void addSuccessor(SIRBasicBlock block) {
        if (!successors.contains(block)) {
            successors.add(block);
            block.predecessors.add(this);
        }
    }

    /**
     * Check if this block ends with a terminator instruction.
     */
    public boolean isTerminated() {
        if (instructions.isEmpty()) {
            return false;
        }
        SIRInstruction last = instructions.getLast();
        return last instanceof SIRInstruction.Goto ||
               last instanceof SIRInstruction.Return ||
               last instanceof SIRInstruction.Throw ||
               last instanceof SIRInstruction.IfTrue ||
               last instanceof SIRInstruction.IfFalse ||
               last instanceof SIRInstruction.IfICmpEq ||
               last instanceof SIRInstruction.IfICmpNe ||
               last instanceof SIRInstruction.IfICmpLt ||
               last instanceof SIRInstruction.IfICmpGe ||
               last instanceof SIRInstruction.IfICmpGt ||
               last instanceof SIRInstruction.IfICmpLe ||
               last instanceof SIRInstruction.IfACmpEq ||
               last instanceof SIRInstruction.IfACmpNe ||
               last instanceof SIRInstruction.IfNull ||
               last instanceof SIRInstruction.IfNonNull;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(label).append(":\n");
        for (SIRInstruction inst : instructions) {
            sb.append("    ").append(inst).append("\n");
        }
        return sb.toString();
    }
}
