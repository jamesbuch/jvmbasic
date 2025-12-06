package com.jvmbasic.sir;

import com.jvmbasic.ir.IRType;

import java.util.*;

/**
 * A function in Stack IR form.
 *
 * Contains:
 * - Function metadata (name, parameters, return type)
 * - List of basic blocks forming the control flow graph
 * - Local variable table
 */
public class SIRFunction {
    private final String name;
    private final List<Parameter> parameters;
    private final IRType returnType;
    private final boolean isStatic;
    private final List<SIRBasicBlock> blocks;
    private final Map<String, SIRBasicBlock> blockMap;
    private final Map<String, LocalVariable> locals;
    private SIRBasicBlock entryBlock;
    private int nextRegister = 0;
    private int nextSlot = 0;

    public record Parameter(String name, IRType type, int slot) {}
    public record LocalVariable(String name, IRType type, int slot) {}

    public SIRFunction(String name, List<Parameter> parameters, IRType returnType, boolean isStatic) {
        this.name = name;
        this.parameters = new ArrayList<>(parameters);
        this.returnType = returnType;
        this.isStatic = isStatic;
        this.blocks = new ArrayList<>();
        this.blockMap = new LinkedHashMap<>();
        this.locals = new LinkedHashMap<>();

        // Reserve slot 0 for 'this' in instance methods
        if (!isStatic) {
            nextSlot = 1;
        }

        // Allocate slots for parameters
        for (Parameter param : parameters) {
            locals.put(param.name(), new LocalVariable(param.name(), param.type(), param.slot()));
            // Update nextSlot based on parameter width
            int width = (param.type().equals(IRType.Primitive.LONG) ||
                        param.type().equals(IRType.Primitive.DOUBLE)) ? 2 : 1;
            if (param.slot() + width > nextSlot) {
                nextSlot = param.slot() + width;
            }
        }
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public IRType getReturnType() {
        return returnType;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public List<SIRBasicBlock> getBlocks() {
        return blocks;
    }

    public SIRBasicBlock getEntryBlock() {
        return entryBlock;
    }

    public Map<String, LocalVariable> getLocals() {
        return locals;
    }

    /**
     * Allocate a new virtual register.
     */
    public int allocRegister() {
        return nextRegister++;
    }

    /**
     * Get the next register number (for reference).
     */
    public int getNextRegister() {
        return nextRegister;
    }

    /**
     * Allocate a local variable slot.
     */
    public int allocSlot(String name, IRType type) {
        int slot = nextSlot;
        locals.put(name, new LocalVariable(name, type, slot));
        // Long and Double take 2 slots
        nextSlot += (type.equals(IRType.Primitive.LONG) ||
                    type.equals(IRType.Primitive.DOUBLE)) ? 2 : 1;
        return slot;
    }

    /**
     * Get the slot for a local variable.
     */
    public int getSlot(String name) {
        LocalVariable local = locals.get(name);
        return local != null ? local.slot() : -1;
    }

    /**
     * Get the type for a local variable.
     */
    public IRType getLocalType(String name) {
        LocalVariable local = locals.get(name);
        return local != null ? local.type() : null;
    }

    /**
     * Get total number of local slots used.
     */
    public int getMaxLocals() {
        return nextSlot;
    }

    /**
     * Create a new basic block with the given label.
     */
    public SIRBasicBlock createBlock(String label) {
        SIRBasicBlock block = new SIRBasicBlock(label);
        blocks.add(block);
        blockMap.put(label, block);
        if (entryBlock == null) {
            entryBlock = block;
        }
        return block;
    }

    /**
     * Get a block by label.
     */
    public SIRBasicBlock getBlock(String label) {
        return blockMap.get(label);
    }

    /**
     * Generate a unique label.
     */
    private int labelCounter = 0;
    public String newLabel(String prefix) {
        return prefix + "_" + (labelCounter++);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Function signature
        sb.append(isStatic ? "static " : "");
        sb.append("function ").append(name).append("(");
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) sb.append(", ");
            Parameter p = parameters.get(i);
            sb.append(p.name()).append(": ").append(p.type());
        }
        sb.append(")");
        if (returnType != null && !returnType.equals(IRType.Primitive.VOID)) {
            sb.append(" -> ").append(returnType);
        }
        sb.append("\n");

        // Local variables
        if (!locals.isEmpty()) {
            sb.append("  ; locals: ");
            boolean first = true;
            for (LocalVariable local : locals.values()) {
                if (!first) sb.append(", ");
                sb.append(local.name()).append("@").append(local.slot());
                first = false;
            }
            sb.append("\n");
        }

        // Basic blocks
        for (SIRBasicBlock block : blocks) {
            sb.append("  ").append(block.getLabel()).append(":\n");
            for (SIRInstruction inst : block.getInstructions()) {
                sb.append("    ").append(inst).append("\n");
            }
        }

        sb.append("end function\n");
        return sb.toString();
    }
}
