package com.jvmbasic.sir;

import com.jvmbasic.ir.IRType;

/**
 * Stack-based IR instruction.
 *
 * SIR (Stack IR) is a low-level, linear representation that maps closely to JVM bytecode.
 * Each instruction operates on a virtual operand stack and uses SSA-style virtual registers.
 *
 * Format: %result = OPCODE operands...
 *
 * Virtual registers (%0, %1, %2, ...) represent values on the conceptual stack.
 * This makes the data flow explicit and enables optimization passes.
 */
public sealed interface SIRInstruction permits
    // Constants
    SIRInstruction.IConst,
    SIRInstruction.LConst,
    SIRInstruction.FConst,
    SIRInstruction.DConst,
    SIRInstruction.SConst,
    SIRInstruction.NullConst,

    // Local variable operations
    SIRInstruction.Load,
    SIRInstruction.Store,

    // Arithmetic (integer)
    SIRInstruction.IAdd,
    SIRInstruction.ISub,
    SIRInstruction.IMul,
    SIRInstruction.IDiv,
    SIRInstruction.IMod,
    SIRInstruction.INeg,

    // Arithmetic (long)
    SIRInstruction.LAdd,
    SIRInstruction.LSub,
    SIRInstruction.LMul,
    SIRInstruction.LDiv,
    SIRInstruction.LMod,
    SIRInstruction.LNeg,

    // Arithmetic (float)
    SIRInstruction.FAdd,
    SIRInstruction.FSub,
    SIRInstruction.FMul,
    SIRInstruction.FDiv,
    SIRInstruction.FNeg,

    // Arithmetic (double)
    SIRInstruction.DAdd,
    SIRInstruction.DSub,
    SIRInstruction.DMul,
    SIRInstruction.DDiv,
    SIRInstruction.DNeg,

    // Power (uses Math.pow)
    SIRInstruction.Power,

    // Bitwise
    SIRInstruction.IAnd,
    SIRInstruction.IOr,
    SIRInstruction.IXor,
    SIRInstruction.INot,
    SIRInstruction.IShl,
    SIRInstruction.IShr,

    // Comparisons
    SIRInstruction.ICmp,
    SIRInstruction.LCmp,
    SIRInstruction.FCmp,
    SIRInstruction.DCmp,
    SIRInstruction.ACmp,

    // Type conversions
    SIRInstruction.I2L,
    SIRInstruction.I2F,
    SIRInstruction.I2D,
    SIRInstruction.L2I,
    SIRInstruction.L2F,
    SIRInstruction.L2D,
    SIRInstruction.F2I,
    SIRInstruction.F2L,
    SIRInstruction.F2D,
    SIRInstruction.D2I,
    SIRInstruction.D2L,
    SIRInstruction.D2F,

    // Object operations
    SIRInstruction.New,
    SIRInstruction.GetField,
    SIRInstruction.PutField,
    SIRInstruction.GetStatic,
    SIRInstruction.PutStatic,
    SIRInstruction.InvokeVirtual,
    SIRInstruction.InvokeStatic,
    SIRInstruction.InvokeInterface,
    SIRInstruction.InvokeSpecial,

    // Array operations
    SIRInstruction.NewArray,
    SIRInstruction.ArrayLoad,
    SIRInstruction.ArrayStore,
    SIRInstruction.ArrayLength,

    // Control flow
    SIRInstruction.Label,
    SIRInstruction.Goto,
    SIRInstruction.IfTrue,
    SIRInstruction.IfFalse,
    SIRInstruction.IfICmpEq,
    SIRInstruction.IfICmpNe,
    SIRInstruction.IfICmpLt,
    SIRInstruction.IfICmpGe,
    SIRInstruction.IfICmpGt,
    SIRInstruction.IfICmpLe,
    SIRInstruction.IfACmpEq,
    SIRInstruction.IfACmpNe,
    SIRInstruction.IfNull,
    SIRInstruction.IfNonNull,
    SIRInstruction.Return,
    SIRInstruction.Throw,

    // Stack manipulation
    SIRInstruction.Dup,
    SIRInstruction.Pop,

    // String operations
    SIRInstruction.Concat,
    SIRInstruction.ToString,

    // Type checking
    SIRInstruction.CheckCast,
    SIRInstruction.InstanceOf,

    // SSA Phi function
    SIRInstruction.Phi,

    // Comments (for readability)
    SIRInstruction.Comment {

    /**
     * Get the result register (if any). Returns -1 if instruction doesn't produce a value.
     */
    default int result() { return -1; }

    /**
     * Get the type of the result (if any).
     */
    default IRType resultType() { return null; }

    // ========================================================================
    // Constants - push values onto the stack
    // ========================================================================

    /** %r = ICONST value */
    record IConst(int r, int value) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = ICONST %d", r, value);
        }
    }

    /** %r = LCONST value */
    record LConst(int r, long value) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.LONG; }
        @Override public String toString() {
            return String.format("%%%-3d = LCONST %dL", r, value);
        }
    }

    /** %r = FCONST value */
    record FConst(int r, float value) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.FLOAT; }
        @Override public String toString() {
            return String.format("%%%-3d = FCONST %sf", r, value);
        }
    }

    /** %r = DCONST value */
    record DConst(int r, double value) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.DOUBLE; }
        @Override public String toString() {
            return String.format("%%%-3d = DCONST %s", r, value);
        }
    }

    /** %r = SCONST "value" */
    record SConst(int r, String value) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Reference.STRING; }
        @Override public String toString() {
            String escaped = value.replace("\\", "\\\\")
                                  .replace("\"", "\\\"")
                                  .replace("\n", "\\n")
                                  .replace("\t", "\\t");
            return String.format("%%%-3d = SCONST \"%s\"", r, escaped);
        }
    }

    /** %r = NULL */
    record NullConst(int r) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Reference.OBJECT; }
        @Override public String toString() {
            return String.format("%%%-3d = NULL", r);
        }
    }

    // ========================================================================
    // Local variable operations
    // ========================================================================

    /** %r = LOAD slot [name] */
    record Load(int r, int slot, String name, IRType type) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return type; }
        @Override public String toString() {
            return String.format("%%%-3d = LOAD %d ; %s", r, slot, name);
        }
    }

    /** STORE slot, %value [name] */
    record Store(int slot, int value, String name, IRType type) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        STORE %d, %%%d ; %s", slot, value, name);
        }
    }

    // ========================================================================
    // Integer arithmetic
    // ========================================================================

    /** %r = IADD %left, %right */
    record IAdd(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = IADD %%%d, %%%d", r, left, right);
        }
    }

    record ISub(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = ISUB %%%d, %%%d", r, left, right);
        }
    }

    record IMul(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = IMUL %%%d, %%%d", r, left, right);
        }
    }

    record IDiv(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = IDIV %%%d, %%%d", r, left, right);
        }
    }

    record IMod(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = IMOD %%%d, %%%d", r, left, right);
        }
    }

    record INeg(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = INEG %%%d", r, operand);
        }
    }

    // ========================================================================
    // Long arithmetic
    // ========================================================================

    record LAdd(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.LONG; }
        @Override public String toString() {
            return String.format("%%%-3d = LADD %%%d, %%%d", r, left, right);
        }
    }

    record LSub(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.LONG; }
        @Override public String toString() {
            return String.format("%%%-3d = LSUB %%%d, %%%d", r, left, right);
        }
    }

    record LMul(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.LONG; }
        @Override public String toString() {
            return String.format("%%%-3d = LMUL %%%d, %%%d", r, left, right);
        }
    }

    record LDiv(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.LONG; }
        @Override public String toString() {
            return String.format("%%%-3d = LDIV %%%d, %%%d", r, left, right);
        }
    }

    record LMod(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.LONG; }
        @Override public String toString() {
            return String.format("%%%-3d = LMOD %%%d, %%%d", r, left, right);
        }
    }

    record LNeg(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.LONG; }
        @Override public String toString() {
            return String.format("%%%-3d = LNEG %%%d", r, operand);
        }
    }

    // ========================================================================
    // Float arithmetic
    // ========================================================================

    record FAdd(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.FLOAT; }
        @Override public String toString() {
            return String.format("%%%-3d = FADD %%%d, %%%d", r, left, right);
        }
    }

    record FSub(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.FLOAT; }
        @Override public String toString() {
            return String.format("%%%-3d = FSUB %%%d, %%%d", r, left, right);
        }
    }

    record FMul(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.FLOAT; }
        @Override public String toString() {
            return String.format("%%%-3d = FMUL %%%d, %%%d", r, left, right);
        }
    }

    record FDiv(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.FLOAT; }
        @Override public String toString() {
            return String.format("%%%-3d = FDIV %%%d, %%%d", r, left, right);
        }
    }

    record FNeg(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.FLOAT; }
        @Override public String toString() {
            return String.format("%%%-3d = FNEG %%%d", r, operand);
        }
    }

    // ========================================================================
    // Double arithmetic
    // ========================================================================

    record DAdd(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.DOUBLE; }
        @Override public String toString() {
            return String.format("%%%-3d = DADD %%%d, %%%d", r, left, right);
        }
    }

    record DSub(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.DOUBLE; }
        @Override public String toString() {
            return String.format("%%%-3d = DSUB %%%d, %%%d", r, left, right);
        }
    }

    record DMul(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.DOUBLE; }
        @Override public String toString() {
            return String.format("%%%-3d = DMUL %%%d, %%%d", r, left, right);
        }
    }

    record DDiv(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.DOUBLE; }
        @Override public String toString() {
            return String.format("%%%-3d = DDIV %%%d, %%%d", r, left, right);
        }
    }

    record DNeg(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.DOUBLE; }
        @Override public String toString() {
            return String.format("%%%-3d = DNEG %%%d", r, operand);
        }
    }

    // ========================================================================
    // Power (uses Math.pow, result is always double)
    // ========================================================================

    record Power(int r, int base, int exponent) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.DOUBLE; }
        @Override public String toString() {
            return String.format("%%%-3d = POWER %%%d, %%%d", r, base, exponent);
        }
    }

    // ========================================================================
    // Bitwise operations
    // ========================================================================

    record IAnd(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = IAND %%%d, %%%d", r, left, right);
        }
    }

    record IOr(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = IOR %%%d, %%%d", r, left, right);
        }
    }

    record IXor(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = IXOR %%%d, %%%d", r, left, right);
        }
    }

    record INot(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = INOT %%%d", r, operand);
        }
    }

    record IShl(int r, int value, int shift) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = ISHL %%%d, %%%d", r, value, shift);
        }
    }

    record IShr(int r, int value, int shift) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = ISHR %%%d, %%%d", r, value, shift);
        }
    }

    // ========================================================================
    // Comparisons - produce boolean (int 0/1) result
    // ========================================================================

    enum CmpOp { EQ, NE, LT, LE, GT, GE }

    record ICmp(int r, CmpOp op, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.BOOLEAN; }
        @Override public String toString() {
            return String.format("%%%-3d = ICMP_%s %%%d, %%%d", r, op, left, right);
        }
    }

    record LCmp(int r, CmpOp op, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.BOOLEAN; }
        @Override public String toString() {
            return String.format("%%%-3d = LCMP_%s %%%d, %%%d", r, op, left, right);
        }
    }

    record FCmp(int r, CmpOp op, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.BOOLEAN; }
        @Override public String toString() {
            return String.format("%%%-3d = FCMP_%s %%%d, %%%d", r, op, left, right);
        }
    }

    record DCmp(int r, CmpOp op, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.BOOLEAN; }
        @Override public String toString() {
            return String.format("%%%-3d = DCMP_%s %%%d, %%%d", r, op, left, right);
        }
    }

    record ACmp(int r, CmpOp op, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.BOOLEAN; }
        @Override public String toString() {
            return String.format("%%%-3d = ACMP_%s %%%d, %%%d", r, op, left, right);
        }
    }

    // ========================================================================
    // Type conversions
    // ========================================================================

    record I2L(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.LONG; }
        @Override public String toString() { return String.format("%%%-3d = I2L %%%d", r, operand); }
    }

    record I2F(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.FLOAT; }
        @Override public String toString() { return String.format("%%%-3d = I2F %%%d", r, operand); }
    }

    record I2D(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.DOUBLE; }
        @Override public String toString() { return String.format("%%%-3d = I2D %%%d", r, operand); }
    }

    record L2I(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() { return String.format("%%%-3d = L2I %%%d", r, operand); }
    }

    record L2F(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.FLOAT; }
        @Override public String toString() { return String.format("%%%-3d = L2F %%%d", r, operand); }
    }

    record L2D(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.DOUBLE; }
        @Override public String toString() { return String.format("%%%-3d = L2D %%%d", r, operand); }
    }

    record F2I(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() { return String.format("%%%-3d = F2I %%%d", r, operand); }
    }

    record F2L(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.LONG; }
        @Override public String toString() { return String.format("%%%-3d = F2L %%%d", r, operand); }
    }

    record F2D(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.DOUBLE; }
        @Override public String toString() { return String.format("%%%-3d = F2D %%%d", r, operand); }
    }

    record D2I(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() { return String.format("%%%-3d = D2I %%%d", r, operand); }
    }

    record D2L(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.LONG; }
        @Override public String toString() { return String.format("%%%-3d = D2L %%%d", r, operand); }
    }

    record D2F(int r, int operand) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.FLOAT; }
        @Override public String toString() { return String.format("%%%-3d = D2F %%%d", r, operand); }
    }

    // ========================================================================
    // Object operations
    // ========================================================================

    /** %r = NEW className */
    record New(int r, String className) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return new IRType.Reference(className); }
        @Override public String toString() {
            return String.format("%%%-3d = NEW %s", r, className);
        }
    }

    /** %r = GETFIELD %object, owner.name : type */
    record GetField(int r, int object, String owner, String name, IRType type) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return type; }
        @Override public String toString() {
            return String.format("%%%-3d = GETFIELD %%%d, %s.%s", r, object, owner, name);
        }
    }

    /** PUTFIELD %object, owner.name, %value */
    record PutField(int object, String owner, String name, int value, IRType type) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        PUTFIELD %%%d, %s.%s, %%%d", object, owner, name, value);
        }
    }

    /** %r = GETSTATIC owner.name : type */
    record GetStatic(int r, String owner, String name, IRType type) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return type; }
        @Override public String toString() {
            return String.format("%%%-3d = GETSTATIC %s.%s", r, owner, name);
        }
    }

    /** PUTSTATIC owner.name, %value */
    record PutStatic(String owner, String name, int value, IRType type) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        PUTSTATIC %s.%s, %%%d", owner, name, value);
        }
    }

    /** %r = INVOKEVIRTUAL %object, owner.name(args) : returnType */
    record InvokeVirtual(int r, int object, String owner, String name,
                         int[] args, IRType returnType) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return returnType; }
        @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            if (returnType != null && !returnType.equals(IRType.Primitive.VOID)) {
                sb.append(String.format("%%%-3d = ", r));
            } else {
                sb.append("        ");
            }
            sb.append("INVOKEVIRTUAL %").append(object).append(", ");
            sb.append(owner).append(".").append(name).append("(");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append("%").append(args[i]);
            }
            sb.append(")");
            return sb.toString();
        }
    }

    /** %r = INVOKESTATIC owner.name(args) : returnType */
    record InvokeStatic(int r, String owner, String name,
                        int[] args, IRType returnType) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return returnType; }
        @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            if (returnType != null && !returnType.equals(IRType.Primitive.VOID)) {
                sb.append(String.format("%%%-3d = ", r));
            } else {
                sb.append("        ");
            }
            sb.append("INVOKESTATIC ").append(owner).append(".").append(name).append("(");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append("%").append(args[i]);
            }
            sb.append(")");
            return sb.toString();
        }
    }

    /** %r = INVOKEINTERFACE %object, owner.name(args) : returnType */
    record InvokeInterface(int r, int object, String owner, String name,
                           int[] args, IRType returnType) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return returnType; }
        @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            if (returnType != null && !returnType.equals(IRType.Primitive.VOID)) {
                sb.append(String.format("%%%-3d = ", r));
            } else {
                sb.append("        ");
            }
            sb.append("INVOKEINTERFACE %").append(object).append(", ");
            sb.append(owner).append(".").append(name).append("(");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append("%").append(args[i]);
            }
            sb.append(")");
            return sb.toString();
        }
    }

    /** INVOKESPECIAL %object, owner.name(args) - for constructors and super calls */
    record InvokeSpecial(int object, String owner, String name,
                         int[] args) implements SIRInstruction {
        @Override public String toString() {
            StringBuilder sb = new StringBuilder("        INVOKESPECIAL %");
            sb.append(object).append(", ");
            sb.append(owner).append(".").append(name).append("(");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append("%").append(args[i]);
            }
            sb.append(")");
            return sb.toString();
        }
    }

    // ========================================================================
    // Array operations
    // ========================================================================

    /** %r = NEWARRAY elementType, %size */
    record NewArray(int r, IRType elementType, int size) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return new IRType.Array(elementType); }
        @Override public String toString() {
            return String.format("%%%-3d = NEWARRAY %s, %%%d", r, elementType, size);
        }
    }

    /** %r = ARRAYLOAD %array, %index */
    record ArrayLoad(int r, int array, int index, IRType elementType) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return elementType; }
        @Override public String toString() {
            return String.format("%%%-3d = ARRAYLOAD %%%d, %%%d", r, array, index);
        }
    }

    /** ARRAYSTORE %array, %index, %value */
    record ArrayStore(int array, int index, int value) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        ARRAYSTORE %%%d, %%%d, %%%d", array, index, value);
        }
    }

    /** %r = ARRAYLENGTH %array */
    record ArrayLength(int r, int array) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.INT; }
        @Override public String toString() {
            return String.format("%%%-3d = ARRAYLENGTH %%%d", r, array);
        }
    }

    // ========================================================================
    // Control flow
    // ========================================================================

    /** LABEL name: */
    record Label(String name) implements SIRInstruction {
        @Override public String toString() {
            return name + ":";
        }
    }

    /** GOTO label */
    record Goto(String label) implements SIRInstruction {
        @Override public String toString() {
            return "        GOTO " + label;
        }
    }

    /** IFTRUE %cond, label */
    record IfTrue(int cond, String label) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        IFTRUE %%%d, %s", cond, label);
        }
    }

    /** IFFALSE %cond, label */
    record IfFalse(int cond, String label) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        IFFALSE %%%d, %s", cond, label);
        }
    }

    record IfICmpEq(int left, int right, String label) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        IF_ICMPEQ %%%d, %%%d, %s", left, right, label);
        }
    }

    record IfICmpNe(int left, int right, String label) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        IF_ICMPNE %%%d, %%%d, %s", left, right, label);
        }
    }

    record IfICmpLt(int left, int right, String label) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        IF_ICMPLT %%%d, %%%d, %s", left, right, label);
        }
    }

    record IfICmpGe(int left, int right, String label) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        IF_ICMPGE %%%d, %%%d, %s", left, right, label);
        }
    }

    record IfICmpGt(int left, int right, String label) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        IF_ICMPGT %%%d, %%%d, %s", left, right, label);
        }
    }

    record IfICmpLe(int left, int right, String label) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        IF_ICMPLE %%%d, %%%d, %s", left, right, label);
        }
    }

    record IfACmpEq(int left, int right, String label) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        IF_ACMPEQ %%%d, %%%d, %s", left, right, label);
        }
    }

    record IfACmpNe(int left, int right, String label) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        IF_ACMPNE %%%d, %%%d, %s", left, right, label);
        }
    }

    record IfNull(int ref, String label) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        IFNULL %%%d, %s", ref, label);
        }
    }

    record IfNonNull(int ref, String label) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        IFNONNULL %%%d, %s", ref, label);
        }
    }

    /** RETURN [%value] */
    record Return(int value, IRType type) implements SIRInstruction {
        @Override public String toString() {
            if (type == null || type.equals(IRType.Primitive.VOID)) {
                return "        RETURN";
            }
            return String.format("        RETURN %%%d", value);
        }
    }

    /** THROW %exception */
    record Throw(int exception) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        THROW %%%d", exception);
        }
    }

    // ========================================================================
    // Stack manipulation
    // ========================================================================

    /** %r = DUP %value */
    record Dup(int r, int value) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public String toString() {
            return String.format("%%%-3d = DUP %%%d", r, value);
        }
    }

    /** POP %value */
    record Pop(int value) implements SIRInstruction {
        @Override public String toString() {
            return String.format("        POP %%%d", value);
        }
    }

    // ========================================================================
    // String operations
    // ========================================================================

    /** %r = CONCAT %left, %right */
    record Concat(int r, int left, int right) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Reference.STRING; }
        @Override public String toString() {
            return String.format("%%%-3d = CONCAT %%%d, %%%d", r, left, right);
        }
    }

    /** %r = TOSTRING %value */
    record ToString(int r, int value, IRType fromType) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Reference.STRING; }
        @Override public String toString() {
            return String.format("%%%-3d = TOSTRING %%%d", r, value);
        }
    }

    // ========================================================================
    // Type checking
    // ========================================================================

    /** %r = CHECKCAST %value, targetType */
    record CheckCast(int r, int value, IRType targetType) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return targetType; }
        @Override public String toString() {
            return String.format("%%%-3d = CHECKCAST %%%d, %s", r, value, targetType);
        }
    }

    /** %r = INSTANCEOF %value, targetType */
    record InstanceOf(int r, int value, IRType targetType) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return IRType.Primitive.BOOLEAN; }
        @Override public String toString() {
            return String.format("%%%-3d = INSTANCEOF %%%d, %s", r, value, targetType);
        }
    }

    // ========================================================================
    // SSA Phi function
    // ========================================================================

    /** %r = PHI [%v1, label1], [%v2, label2], ... */
    record Phi(int r, java.util.Map<String, Integer> sources, IRType type) implements SIRInstruction {
        @Override public int result() { return r; }
        @Override public IRType resultType() { return type; }
        @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%%%-3d = PHI ", r));
            boolean first = true;
            for (var entry : sources.entrySet()) {
                if (!first) sb.append(", ");
                sb.append("[%").append(entry.getValue()).append(", ").append(entry.getKey()).append("]");
                first = false;
            }
            return sb.toString();
        }
    }

    // ========================================================================
    // Comments (for readability)
    // ========================================================================

    record Comment(String text) implements SIRInstruction {
        @Override public String toString() {
            return "        ; " + text;
        }
    }
}
