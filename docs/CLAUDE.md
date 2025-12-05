# Claude Context for JVM BASIC 2.0

This document provides context for Claude when working on the JVM BASIC 2.0 compiler.

## Current Focus: JVM BASIC 2.0 (Java/ANTLR)

We are actively developing **JVM BASIC 2.0**, a complete rewrite of the BASIC compiler in Java using ANTLR4 and ASM for bytecode generation. **Ignore the legacy C++ compiler** (the `jvmbasic` binary in the root directory and related C++ source files).

## Project Structure

```
/home/james/development/jvmbasic/
├── src/java/                    # JVM BASIC 2.0 compiler (ACTIVE)
│   ├── com/jvmbasic/
│   │   ├── grammar/             # ANTLR grammar files
│   │   │   ├── JvmBasicLexer.g4
│   │   │   └── JvmBasicParser.g4
│   │   ├── ir/                  # Tree-based IR (debugging only)
│   │   ├── sir/                 # Stack-based IR (debugging only)
│   │   ├── visitor/             # ANTLR visitors for code generation
│   │   │   ├── CompilerVisitor.java  # BYTECODE GENERATION (ASM)
│   │   │   ├── SymbolCollector.java  # Pass 1: symbol table
│   │   │   └── DebugListener.java    # Debug output
│   │   └── Main.java            # Entry point
│   ├── examples/                # Example .jvmb programs
│   ├── build.gradle.kts         # Gradle build file
│   └── gradlew                  # Gradle wrapper
│
├── docs/
│   ├── CLAUDE.md                # THIS FILE - Claude context
│   ├── jvmbasic-2.0/            # Current compiler docs
│   │   ├── CODEGEN.md           # Code generation guide (ASM examples)
│   │   ├── DEVELOPER_GUIDE.md   # Architecture, adding features
│   │   ├── USER_GUIDE.md        # Language reference
│   │   └── IR_TO_BYTECODE.md    # IR to bytecode mapping (future)
│   └── legacy-jvmbasic/         # Old C++ compiler docs (IGNORE)
│
├── basicrt/                     # Runtime library (BasicRuntime.class)
├── lib/                         # Dependencies (ANTLR, ASM JARs)
│
└── [C++ files]                  # LEGACY - ignore these
```

## Building and Running

```bash
# ALWAYS work from src/java directory:
cd /home/james/development/jvmbasic/src/java

# Build the compiler
./gradlew build

# Compile a BASIC program
java -jar build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar examples/demo.jvmb

# Run the compiled class
java demo

# Debug: show parse tree
java -jar build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar -tree -parse-only examples/demo.jvmb

# Debug: show IR (for visualization only)
java -jar build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar -ir -sir -parse-only examples/demo.jvmb
```

## Architecture: How Code Generation Works

### IMPORTANT: We use VISITOR-based codegen, NOT IR-based

```
Source Code (.jvmb)
       │
       ▼
   ANTLR Lexer/Parser
       │
       ▼
   Parse Tree (CST)
       │
       ├─────────────────────────────────────────┐
       │                                         │
       ▼                                         ▼
 CompilerVisitor ◄── ACTUAL CODEGEN        IR (debug only)
 (uses ASM)                                      │
       │                                    NOT USED
       ▼                                    for codegen
   .class file
```

The `CompilerVisitor` walks the ANTLR parse tree and emits JVM bytecode using ASM.
The IR (Tree IR and Stack IR) are for **debugging/visualization only** - they are not used in code generation.

### Key Files for Code Generation

| File | Purpose |
|------|---------|
| `CompilerVisitor.java` | Generates bytecode from parse tree |
| `SymbolCollector.java` | Pass 1: collects variable/function declarations |
| `Main.java` | Orchestrates parsing and compilation |

### See Also

For detailed code generation examples with ASM bytecode, see:
**`docs/jvmbasic-2.0/CODEGEN.md`**

## What's Working Now (MVP)

| Feature | Example | Status |
|---------|---------|--------|
| Integer variables | `var x as Integer = 10` | ✅ |
| Arithmetic | `x + y`, `x * y` | ✅ |
| Console.WriteLine | Strings and integers | ✅ |
| If/Then/Else | With comparisons | ✅ |
| While loops | With variable update | ✅ |
| Comparisons | `<`, `>`, `<=`, `>=`, `=`, `<>` | ✅ |

### Demo Program That Works

```basic
' demo.jvmb - this compiles and runs!
var x as Integer = 10
var y as Integer = 25
var sum as Integer = x + y

Console.WriteLine("Sum:")
Console.WriteLine(sum)

if sum > 30 then
    Console.WriteLine("Greater than 30")
end if

var i as Integer = 0
while i < 3
    Console.WriteLine(i)
    i = i + 1
end while
```

## What's NOT Yet Implemented

| Feature | Priority |
|---------|----------|
| For loops | High |
| User functions with parameters | High |
| String interpolation | Medium |
| Long, Float, Double types | Medium |
| Arrays | Medium |
| Classes | Low |

## Future: IR-Based Code Generation

The Stack IR is designed to eventually be used for code generation:

```
Stack IR:                    JVM Bytecode:
%0 = ICONST 10         →     BIPUSH 10
%1 = ICONST 25         →     BIPUSH 25
%2 = IADD %0, %1       →     IADD
ISTORE local_0, %2     →     ISTORE 1
```

Benefits of IR-based codegen (future work):
- Optimization passes (constant folding, dead code)
- Better register allocation
- Multiple backends

## Important Rules

1. **DO NOT** modify the C++ compiler (jvmbasic binary, *.cpp files)
2. **ALWAYS** use `./gradlew build` from `src/java/` directory
3. **DO NOT** use IR for code generation yet - use CompilerVisitor
4. **UPDATE** `CODEGEN.md` when adding new code generation features
