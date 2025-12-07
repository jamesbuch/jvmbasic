# Claude Context for JVM BASIC 2.0

This document provides context for Claude when working on the JVM BASIC 2.0 compiler.

## ⚠️ CRITICAL: Avoid Confusion with Legacy JVM Basic 1.0

There are TWO implementations in this repository:

| Aspect | JVM BASIC 2.0 (ACTIVE) | Legacy JVM Basic 1.0 (IGNORE) |
|--------|------------------------|-------------------------------|
| Location | `src/java/` | Root directory (*.cpp, *.h) |
| Language | Java | C++ |
| Build | `./gradlew build` | `make` or `./rebuild.sh` |
| Examples | `src/java/examples/*.jvmb` | `examples/*.bas` |
| Binary | JAR file | `./jvmbasic` executable |
| Web server | NOT YET IMPLEMENTED | TaskApp (Jetty on port 8080) |

**DO NOT:**
- Confuse legacy `.bas` files with new `.jvmb` files
- Try to run legacy TaskApp (uses port 8080) - kill with `fuser -k 8080/tcp`
- Modify C++ files unless explicitly asked
- The legacy files will be moved to `legacy-jvm-basic/` directory soon

## Current Focus: JVM BASIC 2.0 (Java/ANTLR)

We are actively developing **JVM BASIC 2.0**, a complete rewrite of the BASIC compiler in Java using ANTLR4 and ASM for bytecode generation.

## Project Structure

```
/home/james/development/jvmbasic/
├── src/java/                    # JVM BASIC 2.0 compiler (ACTIVE)
│   ├── com/jvmbasic/
│   │   ├── grammar/             # ANTLR grammar files
│   │   │   ├── JvmBasicLexer.g4
│   │   │   └── JvmBasicParser.g4
│   │   ├── ir/                  # Tree-based IR (debugging/future optimization)
│   │   ├── sir/                 # Stack-based IR / SSA-style IR (future codegen)
│   │   ├── visitor/             # ANTLR visitors for code generation
│   │   │   ├── CompilerVisitor.java  # BYTECODE GENERATION (ASM)
│   │   │   ├── SymbolCollector.java  # Pass 1: symbol table
│   │   │   └── DebugListener.java    # Debug output
│   │   └── Main.java            # Entry point
│   ├── examples/                # Example .jvmb programs
│   ├── build.gradle.kts         # Gradle build file
│   ├── test-examples.sh         # TEST SCRIPT - RUN BEFORE COMMITS
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
└── [C++ files]                  # LEGACY - will be moved to legacy-jvm-basic/
```

## Building and Running

```bash
# ALWAYS work from src/java directory:
cd /home/james/development/jvmbasic/src/java

# Build the compiler
./gradlew build

# Compile a BASIC program
java -jar build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar examples/demo.jvmb

# Run the compiled class (may need runtime for Str functions)
java -cp .:../../basicrt demo

# Debug: show parse tree
java -jar build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar -tree -parse-only examples/demo.jvmb

# Debug: show IR and sIR (SSA-style IR)
java -jar build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar -ir -sir -parse-only examples/demo.jvmb
```

## Architecture: Compilation Pipeline

```
Source Code (.jvmb)
       │
       ▼
   ANTLR Lexer/Parser
       │
       ▼
   Parse Tree (CST)
       │
       ├──────────────────────────────────────────────────────┐
       │                                                      │
       ▼                                                      ▼
 SymbolCollector (Pass 1)                              IR Generation
 - Collects variable declarations                      - Tree IR (visualization)
 - Collects function/sub signatures                    - sIR (SSA-style, future codegen)
 - Collects class definitions                               │
       │                                               NOT YET USED
       ▼                                               for codegen
 Semantic Analysis                                          │
 - Type checking                                       [FUTURE: Optimization]
 - Symbol resolution                                        │
       │                                               [FUTURE: sIR-based codegen]
       ▼
 CompilerVisitor (Pass 2) ◄── CURRENT BYTECODE GENERATION
 - Walks parse tree
 - Emits JVM bytecode via ASM
       │
       ▼
   .class file(s)
```

### Current Status of IR Pipeline
- **IR Generation**: Outputs tree-based IR for debugging (`-ir` flag)
- **sIR Generation**: Outputs SSA-style stack IR for debugging (`-sir` flag)
- **Lowering**: sIR is generated but NOT YET used for code generation
- **Future**: Move code generation entirely to sIR for optimization passes

### Key Files for Code Generation

| File | Purpose |
|------|---------|
| `CompilerVisitor.java` | Generates bytecode from parse tree |
| `SymbolCollector.java` | Pass 1: collects variable/function declarations |
| `Main.java` | Orchestrates parsing and compilation |

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
| Functions | With parameters and return values | ✅ |
| Subroutines | `sub greet(name as String)` | ✅ |
| String interpolation | `$"Hello {name}!"` | ✅ |
| Exit/Continue | `exit for`, `continue while` | ✅ |
| Select Case | Multi-value cases, Case Else | ✅ |
| Math namespace | `Math.Sqrt()`, `Math.Sin()`, etc. | ✅ |
| Str namespace | `Str.ToUpper()`, `Str.Length()`, etc. | ✅ |
| **OOP Classes** | Class, constructor, instance methods | ✅ |
| **OOP Fields** | `this.fieldName` access and assignment | ✅ |

## OOP Support (Recently Added)

Classes with constructors, fields, and methods are now supported:

```basic
class Counter
    public var count as Integer

    public sub New(initial as Integer)
        this.count = initial
    end sub

    public sub Increment()
        this.count = this.count + 1
    end sub

    public function GetValue() as Integer
        return this.count
    end function
end class

var c as Counter = new Counter(10)
c.Increment()
Console.WriteLine(c.GetValue())  ' Outputs: 11
```

### Known OOP Limitations
- Local variables in class methods must be declared before use in loops
- Some complex control flow in methods may cause bytecode verification issues

## Known Compiler Limitations (To Fix)

**All critical limitations have been fixed!** The following issues were resolved:

| Issue | Status | Details |
|-------|--------|---------|
| String `+` operator | ✅ Fixed | Now correctly detects string context and uses string concatenation |
| Reserved word variables | ✅ Working | Lexer correctly tokenizes keywords before identifiers; parser gives clear errors |
| FOR in functions | ✅ Fixed | Dynamic locals tracking now works for all methods, not just main |
| Array params in subs | ✅ Fixed | Array type descriptors now correctly generate `[I` instead of `LInteger[];` |
| **Block Scoping** | ✅ Fixed | Full block-level scoping with slot reuse |

### Block Scoping System
JVM BASIC 2.0 now implements **proper block-level scoping** like modern languages:

- **Scope boundaries**: FOR, FOR EACH, WHILE, DO, IF/ELSEIF/ELSE, SELECT CASE blocks
- **Variable visibility**: Variables declared in a block are only visible within that block
- **Slot reuse**: When a block exits, its variable slots are reclaimed for reuse
- **Duplicate detection**: SymbolCollector detects duplicate variable declarations within the same scope
- **Nested scopes**: Inner blocks can shadow outer variables (each gets its own slot)

Example:
```vb
for i = 1 to 3
    var temp as Integer = i * 10  ' temp is scoped to this FOR block
    Console.WriteLine(temp)
next i
' temp is no longer accessible here

for j = 1 to 3
    var temp as Integer = j * 100  ' New temp variable, reuses slots
    Console.WriteLine(temp)
next j
```

### Testing
New test files were added to verify these fixes:
- `string_plus_test.jvmb` - Tests string `+` operator with various types
- `for_in_function_test.jvmb` - Tests FOR loops inside functions
- `array_param_test.jvmb` - Tests array parameters in functions and subs
- `scope_test.jvmb` - Comprehensive block scoping tests with slot reuse verification

## What's NOT Yet Implemented

| Feature | Priority | Notes |
|---------|----------|-------|
| Inheritance | High | `extends`, `super` |
| Interfaces | Medium | `implements` |
| Http namespace | High | REST client |
| Json namespace | High | JSON parsing |
| Db namespace | High | Database access |
| Crypto namespace | Medium | SHA, AES, Base64 |
| Xml namespace | Medium | XML parsing |
| Jetty integration | Medium | Web server |
| Guava utilities | Low | Collections, I/O |
| Apache Commons | Low | Additional utilities |

## Future: sIR-Based Code Generation

The Stack IR (sIR) is designed to eventually replace visitor-based codegen:

```
Stack IR:                    JVM Bytecode:
%0 = ICONST 10         →     BIPUSH 10
%1 = ICONST 25         →     BIPUSH 25
%2 = IADD %0, %1       →     IADD
ISTORE local_0, %2     →     ISTORE 1
```

Benefits of sIR-based codegen (future work):
- Optimization passes (constant folding, dead code elimination)
- Better register allocation
- Multiple backends (JVM, native, LLVM)

## MANDATORY: Testing and Commit Requirements

### Before EVERY Commit

1. **Run the test suite**:
   ```bash
   cd /home/james/development/jvmbasic/src/java
   ./test-examples.sh
   ```

2. **All tests MUST pass** before committing

3. **All example programs MUST compile and run correctly**

4. **Never push code that breaks tests to main branch**

### Test Suite Examples

The test script `src/java/test-examples.sh` tests these example programs:
- `hello.jvmb` - Basic hello world
- `demo.jvmb` - Comprehensive demo
- `class_test.jvmb` - OOP classes
- `calculator.jvmb` - Math functions demo
- `algo_fibonacci.jvmb` - Fibonacci algorithm
- `oop_shapes.jvmb` - Point and Rectangle classes
- `oop_linked_list.jvmb` - Node class demonstration
- And many more...

### Adding New Tests

When adding a new feature:
1. Create a test example in `src/java/examples/` (e.g., `new_feature_test.jvmb`)
2. Add it to the TESTS array in `test-examples.sh`
3. Run `./test-examples.sh` to verify it works
4. Commit both the feature and the test

## Development Roadmap

### Next Steps (In Order)
1. **More OOP Testing**: Inheritance, complex method interactions
2. **Standard Library Expansion**:
   - Http namespace (REST client)
   - Json namespace (parsing/serialization)
   - Db namespace (database connectivity)
   - Crypto namespace (SHA, AES, Base64)
   - Xml namespace
3. **Jetty Integration**: Web server support
4. **Third-party Libraries**:
   - Guava utilities
   - Apache Commons integration
   - Java SE library wrapping
5. **sIR-Based Codegen**: Move from visitor-based to IR-based generation

## Important Rules

1. **DO NOT** modify the C++ compiler (jvmbasic binary, *.cpp files)
2. **ALWAYS** use `./gradlew build` from `src/java/` directory
3. **DO NOT** use IR for code generation yet - use CompilerVisitor
4. **UPDATE** `CODEGEN.md` when adding new code generation features
5. **ALWAYS** run `./test-examples.sh` before committing
6. **NEVER** push code that breaks tests to main branch
7. **KILL** stale legacy processes: `fuser -k 8080/tcp` if TaskApp is running
8. **USE** `src/java/examples/*.jvmb` NOT `examples/*.bas` (legacy)

## Continuation Notes (for Auto-Compact)

### Session Progress
- Fixed OOP bytecode generation for field access and instance methods
- Added 4 working example programs: calculator, algo_fibonacci, oop_shapes, oop_linked_list
- Identified compiler limitations with certain constructs

### Key Directories for JVM BASIC 2.0
- Source: `src/java/com/jvmbasic/`
- Grammar: `src/java/com/jvmbasic/grammar/`
- Visitors: `src/java/com/jvmbasic/visitor/`
- Examples: `src/java/examples/*.jvmb`
- Tests: `src/java/test-examples.sh`
- Build: `src/java/build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar`

### Build and Test Commands
```bash
cd /home/james/development/jvmbasic/src/java
./gradlew build
./test-examples.sh
java -jar build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar examples/FILE.jvmb
java -cp .:../../basicrt CLASSNAME
```

### Next Tasks
1. Test more OOP functionality (inheritance, interfaces)
2. Expand standard library (Http, Json, Db, Crypto, Xml)
3. Add Jetty web server integration
4. Explore Java SE library integration
5. Move legacy files to `legacy-jvm-basic/` directory
