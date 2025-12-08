# Stack IR (sIR) and SSA Form Analysis

This document analyzes the Stack IR (sIR) intermediate representation in JVM BASIC 2.0, including its structure, the lowering process from Tree IR, and the status of SSA (Static Single Assignment) form implementation.

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [sIR Structure](#sir-structure)
4. [Lowering Process](#lowering-process)
5. [Virtual Registers](#virtual-registers)
6. [Basic Blocks and Control Flow](#basic-blocks-and-control-flow)
7. [SSA Form Status](#ssa-form-status)
8. [Instruction Categories](#instruction-categories)
9. [Code Generation Path](#code-generation-path)
10. [Future Improvements](#future-improvements)

---

## Overview

JVM BASIC 2.0 has two IR representations:

1. **Tree IR**: High-level, human-readable representation for debugging
2. **Stack IR (sIR)**: Low-level, linear representation that maps closely to JVM bytecode

The sIR uses SSA-style virtual registers (%0, %1, %2, ...) to make data flow explicit.

### Current Status

| Feature | Status | Notes |
|---------|--------|-------|
| Tree IR → sIR Lowering | ✅ Complete | IRLowering.java |
| Virtual Registers | ✅ Complete | %0, %1, %2, ... |
| Basic Blocks | ✅ Complete | SIRBasicBlock |
| Control Flow Graph | ✅ Complete | Predecessor/successor tracking |
| Phi Functions | ⚠️ Defined Only | Never emitted by lowering |
| sIR → Bytecode | ❌ Not Used | Direct CST → bytecode instead |

---

## Architecture

```
Source (.jvmb)
     │
     ▼
 ANTLR Parser
     │
     ▼
 Parse Tree (CST)
     │
     ├─────────────────────────────────┐
     │                                 │
     ▼                                 ▼
 CompilerVisitor                   IRBuilder
 (Direct bytecode)                     │
     │                                 ▼
     ▼                           Tree IR (-ir flag)
 .class file                           │
 (Current)                             ▼
                                 IRLowering
                                       │
                                       ▼
                                Stack IR (-sir flag)
                                       │
                                       ▼ (Future)
                              Bytecode Generator
```

**Current Implementation**: The compiler uses `CompilerVisitor` to generate bytecode directly from the parse tree. The IR representations (Tree IR and sIR) are currently for **debugging and visualization only**.

**Future Path**: Generate bytecode from sIR for better optimization opportunities.

---

## sIR Structure

### Core Components

```
SIRModule
├── name: String
├── mainFunction: SIRFunction
├── functions: List<SIRFunction>
└── classes: List<SIRClass>

SIRClass
├── name: String
├── superClass: String
├── interfaces: List<String>
├── fields: List<Field>
└── methods: List<SIRFunction>

SIRFunction
├── name: String
├── parameters: List<Parameter>
├── returnType: IRType
├── isStatic: boolean
├── blocks: List<SIRBasicBlock>
├── locals: Map<String, LocalVar>
└── nextRegister: int

SIRBasicBlock
├── label: String
├── instructions: List<SIRInstruction>
├── predecessors: List<SIRBasicBlock>
└── successors: List<SIRBasicBlock>
```

### File Locations

| File | Purpose |
|------|---------|
| `sir/SIRModule.java` | Module container |
| `sir/SIRClass.java` | Class representation |
| `sir/SIRFunction.java` | Function/method with blocks |
| `sir/SIRBasicBlock.java` | Basic block in CFG |
| `sir/SIRInstruction.java` | All instruction types |
| `sir/IRLowering.java` | Tree IR → sIR conversion |

---

## Lowering Process

The `IRLowering` class converts Tree IR to Stack IR:

### Entry Point

```java
public SIRModule lower(IRCompilationUnit unit) {
    module = new SIRModule(unit.getName());

    // Lower declared functions
    for (IRFunction func : unit.getFunctions()) {
        SIRFunction sirFunc = lowerFunction(func);
        module.addFunction(sirFunc);
    }

    // Lower classes
    for (IRClass cls : unit.getClasses()) {
        SIRClass sirClass = lowerClass(cls);
        module.addClass(sirClass);
    }

    // Lower top-level statements into main function
    if (!unit.getStatements().isEmpty()) {
        SIRFunction main = createMainFunction(unit.getStatements());
        module.setMainFunction(main);
    }

    return module;
}
```

### Statement Lowering

Each IR statement type has a corresponding lowering method:

| Tree IR | sIR Output |
|---------|------------|
| `IRVarDecl` | `STORE slot, %value` |
| `IRAssignment` | `STORE`/`PUTFIELD`/`ARRAYSTORE` |
| `IRIf` | `IFFALSE %cond, label` + blocks |
| `IRWhile` | `GOTO cond` + condition block + body |
| `IRFor` | Init + condition + body + step blocks |
| `IRReturn` | `RETURN [%value]` |
| `IRExprStmt` | Expression + optional `POP` |

### Expression Lowering

Each expression is lowered to produce a virtual register containing the result:

```java
private int lowerExpression(IRExpression expr) {
    if (expr instanceof IRIntLiteral lit) {
        int r = currentFunction.allocRegister();
        emit(new SIRInstruction.IConst(r, lit.value()));
        return r;
    }
    // ... other expression types
}
```

---

## Virtual Registers

The sIR uses virtual registers to represent values:

### Format

```
%0 = ICONST 10
%1 = ICONST 25
%2 = IADD %0, %1
     STORE 1, %2 ; x
```

### Register Allocation

Registers are allocated sequentially within each function:

```java
// In SIRFunction
private int nextRegister = 0;

public int allocRegister() {
    return nextRegister++;
}
```

### Difference from SSA

True SSA form requires:
1. Each variable is assigned exactly once
2. Phi functions at control flow merge points

The current sIR implementation uses virtual registers but does **not** enforce SSA properties:
- Variables can be reassigned (via `STORE`)
- Phi functions are defined but never emitted

---

## Basic Blocks and Control Flow

### Basic Block Properties

A basic block is a sequence of instructions with:
- Single entry point (the first instruction)
- Single exit point (the last instruction)
- No branches in the middle

```java
public boolean isTerminated() {
    SIRInstruction last = instructions.getLast();
    return last instanceof SIRInstruction.Goto ||
           last instanceof SIRInstruction.Return ||
           last instanceof SIRInstruction.Throw ||
           last instanceof SIRInstruction.IfTrue ||
           // ... other branch instructions
}
```

### Control Flow Example

For an if statement:

```basic
if x > 5 then
    Console.WriteLine("Greater")
else
    Console.WriteLine("Not greater")
end if
```

Generates blocks:

```
entry:
    %0  = LOAD 1 ; x
    %1  = ICONST 5
    %2  = ICMP_GT %0, %1
        IFFALSE %2, else_0

then_0:
    ...
        GOTO endif_0

else_0:
    ...
        GOTO endif_0

endif_0:
    ; continues here
```

---

## SSA Form Status

### Phi Instruction Definition

The Phi instruction is defined in `SIRInstruction.java`:

```java
/** %r = PHI [%v1, label1], [%v2, label2], ... */
record Phi(int r, java.util.Map<String, Integer> sources, IRType type)
    implements SIRInstruction {
    @Override public int result() { return r; }
    @Override public IRType resultType() { return type; }
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%%%-3d = PHI ", r));
        boolean first = true;
        for (var entry : sources.entrySet()) {
            if (!first) sb.append(", ");
            sb.append("[%").append(entry.getValue())
              .append(", ").append(entry.getKey()).append("]");
            first = false;
        }
        return sb.toString();
    }
}
```

### Current Usage: None

The `IRLowering` class **never emits Phi instructions**. Instead, it uses local variable slots for values that need to survive across control flow:

```java
private int lowerTernary(IRTernary ternary) {
    // Uses a temporary slot instead of Phi
    int trueResultSlot = currentFunction.allocSlot("$ternary_result", ternary.getType());

    // True branch stores to slot
    emit(new SIRInstruction.Store(trueResultSlot, trueReg, "$ternary_result", ...));

    // False branch stores to same slot
    emit(new SIRInstruction.Store(trueResultSlot, falseReg, "$ternary_result", ...));

    // Load from slot after merge
    emit(new SIRInstruction.Load(r, trueResultSlot, "$ternary_result", ...));
    return r;
}
```

### Why Phi Functions Are Not Used

1. **Simplicity**: The current approach using slots is simpler to implement
2. **JVM Target**: The JVM uses local variables, not registers, so Phi conversion isn't strictly necessary
3. **ASM COMPUTE_FRAMES**: ASM's frame computation handles stack/local consistency automatically

### When Phi Would Be Needed

True SSA form with Phi functions would be valuable for:
- Advanced optimizations (constant propagation, dead code elimination)
- Register allocation algorithms
- Targeting non-JVM backends (LLVM, native code)

---

## Instruction Categories

### Constants

| Instruction | Description | Example |
|-------------|-------------|---------|
| `IConst` | Integer constant | `%0 = ICONST 42` |
| `LConst` | Long constant | `%0 = LCONST 100L` |
| `FConst` | Float constant | `%0 = FCONST 3.14f` |
| `DConst` | Double constant | `%0 = DCONST 2.718` |
| `SConst` | String constant | `%0 = SCONST "hello"` |
| `NullConst` | Null reference | `%0 = NULL` |

### Local Variables

| Instruction | Description | Example |
|-------------|-------------|---------|
| `Load` | Load from slot | `%0 = LOAD 1 ; x` |
| `Store` | Store to slot | `STORE 1, %0 ; x` |

### Arithmetic (Integer)

| Instruction | Description | Example |
|-------------|-------------|---------|
| `IAdd` | Addition | `%2 = IADD %0, %1` |
| `ISub` | Subtraction | `%2 = ISUB %0, %1` |
| `IMul` | Multiplication | `%2 = IMUL %0, %1` |
| `IDiv` | Division | `%2 = IDIV %0, %1` |
| `IMod` | Modulo | `%2 = IMOD %0, %1` |
| `INeg` | Negation | `%1 = INEG %0` |

Similar instructions exist for Long (`L*`), Float (`F*`), and Double (`D*`).

### Comparisons

| Instruction | Description | Example |
|-------------|-------------|---------|
| `ICmp` | Integer compare | `%2 = ICMP_LT %0, %1` |
| `LCmp` | Long compare | `%2 = LCMP_EQ %0, %1` |
| `FCmp` | Float compare | `%2 = FCMP_GT %0, %1` |
| `DCmp` | Double compare | `%2 = DCMP_LE %0, %1` |
| `ACmp` | Reference compare | `%2 = ACMP_EQ %0, %1` |

### Control Flow

| Instruction | Description | Example |
|-------------|-------------|---------|
| `Label` | Block label | `then_0:` |
| `Goto` | Unconditional jump | `GOTO endif_0` |
| `IfTrue` | Branch if true | `IFTRUE %0, label` |
| `IfFalse` | Branch if false | `IFFALSE %0, label` |
| `IfICmpEq` | Branch if equal | `IF_ICMPEQ %0, %1, label` |
| `Return` | Return from method | `RETURN %0` |
| `Throw` | Throw exception | `THROW %0` |

### Object Operations

| Instruction | Description | Example |
|-------------|-------------|---------|
| `New` | Allocate object | `%0 = NEW Person` |
| `GetField` | Read field | `%1 = GETFIELD %0, Person.name` |
| `PutField` | Write field | `PUTFIELD %0, Person.name, %1` |
| `InvokeVirtual` | Virtual call | `INVOKEVIRTUAL %0, Person.greet()` |
| `InvokeStatic` | Static call | `INVOKESTATIC Math.abs(%0)` |
| `InvokeSpecial` | Constructor/super | `INVOKESPECIAL %0, Person.<init>()` |

### Array Operations

| Instruction | Description | Example |
|-------------|-------------|---------|
| `NewArray` | Create array | `%0 = NEWARRAY Integer, %1` |
| `ArrayLoad` | Read element | `%2 = ARRAYLOAD %0, %1` |
| `ArrayStore` | Write element | `ARRAYSTORE %0, %1, %2` |
| `ArrayLength` | Get length | `%1 = ARRAYLENGTH %0` |

---

## Code Generation Path

### Current Path (Used)

```
Parse Tree → CompilerVisitor → ASM → .class file
```

The `CompilerVisitor` generates bytecode directly from the parse tree.

### Future Path (Planned)

```
Parse Tree → IRBuilder → Tree IR → IRLowering → sIR → BytecodeGenerator → .class file
```

Benefits of sIR-based generation:
1. Optimization passes on sIR
2. Better separation of concerns
3. Easier to add new backends

---

## Future Improvements

### 1. True SSA Form

Implement SSA construction with Phi functions:

```
entry:
    %0 = ICONST 0
        GOTO cond

cond:
    %1 = PHI [%0, entry], [%3, body]
    %2 = ICMP_LT %1, %10
        IFFALSE %2, end

body:
    %3 = IADD %1, %4
        GOTO cond

end:
    ; %1 contains final value
```

### 2. sIR-Based Code Generation

Create `SIRCodeGenerator` to emit bytecode from sIR:

```java
public class SIRCodeGenerator {
    public byte[] generate(SIRModule module) {
        // Generate main class
        // Generate each declared class
        // Map sIR instructions to JVM bytecode
    }
}
```

### 3. Optimization Passes

Implement passes that operate on sIR:

- **Constant Folding**: `IADD %0, %1` where both are constants
- **Dead Code Elimination**: Remove unused instructions
- **Common Subexpression Elimination**: Reuse computed values
- **Copy Propagation**: Eliminate unnecessary copies

### 4. Register Allocation

For sIR → bytecode, map virtual registers to JVM locals:

```java
public class RegisterAllocator {
    public Map<Integer, Integer> allocate(SIRFunction func) {
        // Linear scan or graph coloring
        // Map %0, %1, ... to local slots
    }
}
```

---

## References

- [SSA Form (Wikipedia)](https://en.wikipedia.org/wiki/Static_single_assignment_form)
- [LLVM IR Reference](https://llvm.org/docs/LangRef.html)
- [ASM Bytecode Framework](https://asm.ow2.io/)
- [JVM Specification - Instruction Set](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-6.html)
