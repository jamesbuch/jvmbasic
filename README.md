# JVM BASIC — A Modern BASIC Compiler for the JVM

A feature-complete BASIC compiler written in modern C++ that generates JVM bytecode. Supports full object-oriented programming, user-defined types (structs), functions, arrays, recursion, file I/O, regular expressions, and 93 built-in functions.

**Current Version**: Phase 7 Complete (Object-Oriented Programming) ✅  
**Test Coverage**: 100% (56/56 tests passing)

## Features

### Language Features
- **Object-Oriented Programming** (Phase 7 NEW! ✨): 
  - `CLASS...END CLASS` declarations with PUBLIC/PRIVATE fields
  - Constructors with `PUBLIC SUB New(params)`
  - Instance methods (SUB/FUNCTION)
  - `NEW` operator for object instantiation
  - `ME` keyword for self-reference
  - Field access and method calls with dot notation
- **User-Defined Types** (Phase 6): `TYPE...ENDTYPE` with member access via dot notation
- **Functions & Subroutines**: `FUNCTION...ENDFUNCTION`, `SUB...ENDSUB` with full recursion support
- **Arrays**: Typed arrays with `DIM arr(size) = value`, arrays as function parameters
- **Control Flow**: `IF...THEN...ELSEIF...ELSE...ENDIF`, `FOR...TO...STEP...NEXT`, `WHILE...ENDWHILE`, `DO...WHILE/UNTIL`
- **I/O**: `PRINT` with BASIC-style `,` and `;` separators, `INPUT` statement, file I/O functions
- **Comments**: `REM` and `'` (apostrophe) comments
- **Type System**: `Int`, `Float`, `String`, `Bool` with automatic Int→Float promotion
- **Expressions**: Full arithmetic, comparison, boolean, and string operations
- **Built-in Functions**: 93 functions for math, strings, arrays, file I/O, and regex

### Compiler Features
- **Pure C++20**: Single-file or modular architecture
- **Direct Bytecode**: Generates valid `.class` files for JVM (Java 5 target)
- **Type Inference**: Multi-pass type inference for function parameters and arrays
- **Error Reporting**: Line numbers and descriptive error messages
- **Zero Dependencies**: No external libraries for compilation

## Quick Start

### Build
```bash
make
# or manually:
g++ -std=gnu++20 -O2 jvmbasic.cpp builtin_functions.o -o jvmbasic
```

### Compile and Run
```bash
./jvmbasic < program.bas
java BasicProgram
```

### Hello World
```basic
PRINT "Hello, World!"
```

## Language Examples

### Object-Oriented Programming (NEW!)
```basic
' Define a class with constructor
CLASS BankAccount
    PRIVATE balance AS FLOAT
    PUBLIC owner AS STRING
    
    PUBLIC SUB New(name AS STRING, initial AS FLOAT)
        owner = name
        balance = initial
    END SUB
END CLASS

' Create and use objects
DIM account AS NEW BankAccount("Alice", 1000.0)
LET account.balance = 1500.0
PRINT account.owner; " has $"; account.balance
```

### User-Defined Types (Structs)
```basic
TYPE Person
    name AS STRING
    age AS FLOAT
ENDTYPE

DIM p AS Person
LET p.name = "Alice"
LET p.age = 30.0
PRINT "Person: "; p.name; ", age "; p.age
```

### Functions with Recursion
```basic
FUNCTION factorial(n)
    IF n <= 1.0 THEN
        RETURN 1.0
    ELSE
        RETURN n * factorial(n - 1.0)
    ENDIF
ENDFUNCTION

PRINT "5! = "; factorial(5.0)  REM Output: 120.0
```

### Arrays and Array Functions
```basic
DIM numbers(10) = 0.0
FOR i = 0.0 TO 9.0
    LET numbers(INT(i)) = RNDINT(1, 100)
NEXT i

CALL ARRAYSORT(numbers)
PRINT "Min: "; ARRAYMIN(numbers)
PRINT "Max: "; ARRAYMAX(numbers)
PRINT "Average: "; ARRAYAVG(numbers)
```

### File I/O
```basic
LET out = OPENOUTPUT("data.txt")
IF out >= 0.0 THEN
    CALL WRITELINE(out, "Hello from JVM BASIC!")
    LET dummy = CLOSEFILE(out)
ENDIF

LET in = OPENINPUT("data.txt")
IF in >= 0.0 THEN
    LET line = READLINE(in)
    PRINT line
    LET dummy = CLOSEFILE(in)
ENDIF
```

### Regular Expressions
```basic
LET email = "user@example.com"
IF REGEXMATCH(email, "\\w+@\\w+\\.\\w+") THEN
    LET user = REGEXGROUP(email, "(\\w+)@(\\w+\\.\\w+)", 1)
    LET domain = REGEXGROUP(email, "(\\w+)@(\\w+\\.\\w+)", 2)
    PRINT "User: "; user
    PRINT "Domain: "; domain
ENDIF
```

## Complete Feature List

### Statements
- `PRINT [expr [, | ;] ...] [, | ;]` - Output with optional separators
- `LET var = expr` - Variable assignment
- `LET var(index) = expr` - Array element assignment
- `LET var.member = expr` - Struct member assignment (Phase 6)
- `INPUT var` - Read input (auto type conversion)
- `DIM var(size) = init` - Array declaration
- `DIM var AS TypeName` - Struct declaration (Phase 6)
- `IF cond THEN ... [ELSEIF cond THEN ...] [ELSE ...] ENDIF`
- `FOR var = start TO end [STEP step] ... NEXT var`
- `WHILE cond ... ENDWHILE` (or `WEND`)
- `DO ... WHILE cond` - Post-test loop
- `DO ... UNTIL cond` - Post-test until loop
- `RETURN [expr]` - Return from function
- `CALL name(args)` - Call subroutine
- `REM comment` - Comment line

### Declarations
- `FUNCTION name(params) ... RETURN expr ... ENDFUNCTION`
- `SUB name(params) ... ENDSUB`
- `TYPE name ... field AS type ... ENDTYPE` (Phase 6)

### Built-in Functions (93 Total)

See `docs/USER_GUIDE.md` for complete documentation with examples.

**Math** (25): `ABS`, `SQRT`, `POW`, `SIN`, `COS`, `TAN`, `ASIN`, `ACOS`, `ATAN`, `ATAN2`, `LOG`, `LOG10`, `EXP`, `FLOOR`, `CEIL`, `ROUND`, `MIN`, `MAX`, `PI`, `E`, `SIGN`, `HYPOT`, `INT`, `FLOAT`, `BOOL`

**Random** (3): `RND`, `RNDINT`, `RANDOMSEED`

**String** (20): `LEN`, `LEFT`, `RIGHT`, `MID`, `UPPER`, `LOWER`, `TRIM`, `REPLACE`, `INDEXOF`, `SUBSTRING`, `STARTSWITH`, `ENDSWITH`, `CONTAINS`, `CHR`, `ASC`, `SPLIT`, `JOIN`, `FORMAT`, `STR`, `VAL`

**Array** (13): `ARRAYLEN`, `ARRAYGET`, `ARRAYSET`, `ARRAYSORT`, `ARRAYSORTSTR`, `ARRAYSUM`, `ARRAYMIN`, `ARRAYMAX`, `ARRAYAVG`, `ARRAYREVERSE`, `ARRAYFIND`, `ARRAYCOPY`, `ARRAYFILL`

**File I/O** (8): `OPENINPUT`, `OPENOUTPUT`, `READLINE`, `WRITELINE`, `WRITETEXT`, `CLOSEFILE`, `FILEEXISTS`, `DELETEFILE`

**Regular Expressions** (4): `REGEXMATCH`, `REGEXFIND`, `REGEXREPLACE`, `REGEXGROUP`

**I/O** (2): `INPUT`, `PRINT`

## Architecture

### Compiler Pipeline
1. **Lexer** - Tokenization with keywords, operators, literals
2. **Parser** - Recursive descent parser building typed AST
3. **Type Inference** - Multi-pass inference for parameters and arrays
4. **Code Generation** - JVM bytecode emission to `.class` file

### Files
- `jvmbasic.cpp` - Main compiler (1,376 lines) with embedded lexer/parser
- `codegen.h` - JVM bytecode generator (1,400+ lines)
- `ast.h` - Abstract Syntax Tree definitions
- `builtin_functions.cpp/h` - Built-in function registry
- `BasicRuntime.java` - Runtime library (93 function implementations)

### Code Generation
- **Target**: JVM bytecode (Java 5 / version 49)
- **Class Name**: `BasicProgram` (default)
- **Method**: Single `public static void main(String[])`
- **Locals**: Efficiently allocated local variable slots
- **Structs**: Implemented as `Object[]` arrays with boxing/unboxing

## Testing

### Test Suite
- **56 tests total**: 54 regular tests + 2 INPUT tests
- **100% passing** ✅ (56/56)
  - Phase 7 OOP tests: 7/7
  - Phase 6 Struct tests: 4/4
  - Array tests: 12/12
  - Function tests: 15/15
  - Other tests: 16/16
  - INPUT tests: 2/2
- Automated test runners: `test_runner.sh`, `run_input_tests.sh`

### Run Tests
```bash
./test_runner.sh              # All 54 regular tests
./run_input_tests.sh          # 2 INPUT tests with data files
./dump_test_artifacts.sh      # Generate AST and bytecode dumps
```

## Documentation

### User Documentation
- `docs/USER_GUIDE.md` - Complete guide with all 93 built-in functions and examples (1,351 lines)
- `README.md` - This file (quick start and overview)

### Developer Documentation
- `docs/dev/CODE_GUIDE.md` - Complete developer guide
- `docs/dev/AST_GUIDE.md` - AST structure and extension guide
- `docs/dev/LEXER_GUIDE.md` - Lexer internals and extension
- `docs/dev/DEBUGGING_GUIDE.md` - Debugging techniques
- `docs/dev/MODULAR_ARCHITECTURE.md` - Modular compiler architecture
- `docs/dev/PHASE7_IMPLEMENTATION_GUIDE.md` - OOP implementation details
- `PHASE7_COMPLETE.md` - Phase 7 completion report

### Planning Documents
- `docs/planning/PHASE7_DESIGN.md` - OOP design specification
- `docs/planning/PHASE7_CODEGEN_PLAN.md` - Code generation plan
- `docs/planning/PHASE6_DESIGN.md` - User-defined types design
- `docs/planning/SERIOUS_LANGUAGE_ANALYSIS.md` - Language evolution plan

## Development History

### Phase 5 (Complete)
- ✅ Functions with recursion
- ✅ Array parameters
- ✅ Multi-pass type inference
- ✅ 93 built-in functions
- ✅ File I/O
- ✅ Regular expressions
- ✅ REM comments
- ✅ Modular architecture

### Phase 6 (Complete)
- ✅ TYPE...ENDTYPE declarations
- ✅ User-defined types (structs)
- ✅ DIM var AS TypeName syntax
- ✅ Member access via dot operator
- ✅ JVM bytecode generation with Object[]
- ✅ Boxing/unboxing for primitives
- ✅ Comprehensive testing

### Phase 7 (Complete) ✅
- ✅ Object-oriented programming
- ✅ CLASS...END CLASS declarations
- ✅ Constructors (SUB New with parameters)
- ✅ Instance methods (SUB/FUNCTION)
- ✅ NEW operator for instantiation
- ✅ Field access and assignment (getfield/putfield)
- ✅ Encapsulation (PUBLIC/PRIVATE fields)
- ✅ ME keyword (self-reference)
- ✅ Apostrophe comments (`'`)
- ✅ Multiple classes per program

### Phase 8 (Future)
- Inheritance (INHERITS keyword)
- Method overriding
- Interfaces
- Static members (SHARED keyword)
- Properties with GET/SET
- Method overloading

## Examples

### Complete Programs

See `examples/` directory for complete working programs:
- `examples/math_algorithms.bas` - GCD, factorial, Fibonacci, primes
- `examples/sorting_algorithms.bas` - Bubble sort, selection sort
- `examples/password_generator.bas` - Random password generation
- `examples/lotto_improved.bas` - Lottery number generator

### Test Programs

See `tests/` directory for 56 test programs covering:
- **OOP** (`test_class_*.bas`, 7 tests) - Classes, constructors, methods
- **Structs** (`test_struct_*.bas`, 4 tests) - User-defined types
- **Functions** (`test_func_*.bas`, 15 tests) - Recursion, parameters, return values
- **Arrays** (`test_array_*.bas`, 12 tests) - All array types, parameters, functions
- **Control Flow** (`test_if.bas`, `test_for.bas`, `test_while.bas`)
- **I/O** (`test_input*.bas`, `test_print*.bas`)
- **Advanced** (`test_algorithms.bas`, `test_comprehensive.bas`) - Complete feature demos

## Building and Running

### Requirements
- C++20 compiler (g++ 10+, clang 12+)
- JDK/JRE 8+ (for running compiled programs)
- Make (optional, for build system)

### Build Options
```bash
# Full build with Makefile
make

# Clean build
make clean && make

# Manual build (single file)
g++ -std=gnu++20 -O2 jvmbasic.cpp builtin_functions.o -o jvmbasic

# Build runtime library
javac BasicRuntime.java
```

### Usage
```bash
# Compile from file
./jvmbasic < program.bas

# Compile from stdin
cat program.bas | ./jvmbasic

# Run compiled program
java BasicProgram

# With runtime library
java -cp .:basicrt BasicProgram
```

## Current Limitations

These are intentional design decisions for simplicity:

- **One class file**: Generates single `BasicProgram.class`
- **No module system**: Single file programs only (for now)
- **Simple type system**: No user-defined generic types yet
- **Basic inheritance**: Classes planned for Phase 7
- **File I/O**: Text files only (binary I/O future)
- **Arrays**: One-dimensional (multi-dimensional planned)

## Contributing

This is an educational project demonstrating compiler construction. Feel free to:
- Study the code
- Extend with new features
- Use as a learning resource
- Fork for your own projects

## Repository

**GitHub**: `git@github.com:jamesbuch/jvmbasic.git`  
**Branch**: `main` (Phase 6 complete)  
**Development**: `phase7-oop` (next phase)

## License

Public domain / MIT - choose what fits your needs.

---

**JVM BASIC** - A modern BASIC compiler for the JVM, bridging classic BASIC syntax with modern language features. Phase 6 complete with user-defined types! 🚀
