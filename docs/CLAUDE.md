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

## What's Working Now

| Feature | Example | Status |
|---------|---------|--------|
| Integer variables | `var x as Integer = 10` | ✅ |
| Long variables | `var x as Long = 9999999999L` | ✅ |
| Float variables | `var x as Float = 3.14F` | ✅ |
| Double variables | `var x as Double = 3.14159` | ✅ |
| String variables | `var s as String = "Hello"` | ✅ |
| Boolean variables | `var b as Boolean = true` | ✅ |
| Arithmetic | `+`, `-`, `*`, `/`, `mod` | ✅ |
| Comparisons | `<`, `>`, `<=`, `>=`, `=`, `<>` | ✅ |
| Logical operators | `and`, `or`, `not` | ✅ |
| Console.WriteLine | All types | ✅ |
| Console.Write | All types | ✅ |
| Console.ReadLine | Input as String | ✅ |
| If/Then/Else/ElseIf | All variants | ✅ |
| For loops | `for i = 1 to 10 step 2` | ✅ |
| For Each loops | `for each x in array` | ✅ |
| While loops | `while x < 10 ... end while` | ✅ |
| Do loops | All variants (while/until, pre/post) | ✅ |
| Arrays | `new Integer[5]`, `arr[0] = 10` | ✅ |
| Functions | `function add(a as Integer, b as Integer) as Integer` | ✅ |
| Subroutines | `sub greet(name as String)` | ✅ |

## What's NOT Yet Implemented

| Feature | Priority |
|---------|----------|
| String interpolation | High |
| Exit/Continue statements | High |
| Select Case | High |
| Standard library (File, Math, etc.) | High |
| Classes | Medium |
| Modules/Imports | Medium |
| Generics | Low |

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

## Testing Requirements

### MANDATORY: Run Tests Before Every Commit

Before committing any changes to the JVM BASIC 2.0 compiler, you MUST:

1. Run the test suite:
   ```bash
   cd /home/james/development/jvmbasic/src/java
   ./test-examples.sh
   ```

2. All tests must pass before committing. If any test fails:
   - Fix the issue before committing
   - Never commit code that breaks existing tests
   - The main branch must always have passing tests

### Test Suite

The test script `src/java/test-examples.sh` tests these example programs:
- `hello.jvmb` - Basic hello world
- `demo.jvmb` - Comprehensive demo of all features
- `array_test.jvmb` - Array creation and access
- `foreach_test.jvmb` - For Each loops
- `function_test.jvmb` - User-defined functions
- `do_loop_test.jvmb` - All Do loop variants
- `simple_for.jvmb` - For loops with STEP
- `simple_while.jvmb` - While loops
- `simple_if.jvmb` - If/Then/Else
- `float_long_test.jvmb` - Float and Long types
- `double_test.jvmb` - Double type

### Adding New Tests

When adding a new feature:
1. Create a test example in `examples/` (e.g., `new_feature_test.jvmb`)
2. Add it to the TESTS array in `test-examples.sh`
3. Run `./test-examples.sh` to verify it works
4. Commit both the feature and the test

## Important Rules

1. **DO NOT** modify the C++ compiler (jvmbasic binary, *.cpp files)
2. **ALWAYS** use `./gradlew build` from `src/java/` directory
3. **DO NOT** use IR for code generation yet - use CompilerVisitor
4. **UPDATE** `CODEGEN.md` when adding new code generation features
5. **ALWAYS** run `./test-examples.sh` before committing
6. **NEVER** push code that breaks tests to main branch
