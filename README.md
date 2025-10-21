# JVM BASIC — A Modern BASIC Compiler for the JVM

A modern, professional BASIC compiler with Visual Basic-style syntax that generates JVM bytecode. Supports full object-oriented programming, modern type system (Decimal, BigInt), web capabilities (JSON, HTTP), database connectivity, and 255 built-in functions with namespace syntax.

**Current Version**: Phase 9 Complete (Modern VB Syntax + Enterprise Libraries) ✅  
**Test Coverage**: 100% (81/81 tests passing + 3 stdin tests skipped)  
**Function Count**: 255 functions across 7 namespaces  
**Libraries**: 16 professional JARs (22MB) - Gson, JDBC, Apache Commons, ANTLR4, Bouncy Castle, Jetty

## Features

### Phase 9: Modern Syntax & Enterprise Libraries ✨ NEW!
- **Modern VB-Style Syntax**: `Dim x As Integer = 10`, `Function Add(a As Integer) As Integer`
- **Fully Case-Insensitive**: All keywords work in any case (Dim/DIM/dim, If/IF/if, Function/FUNCTION/function, etc.)
- **Expression Statements**: Call functions without dummy variables: `Console.WriteLine("text")`
- **Expanded Type System**: Integer, Single, Double, Long, Boolean, String, Decimal, BigInt
- **Namespace/OO Syntax**: Console.WriteLine(), Math.Sin(), File.ReadAllText()
- **Professional Libraries** (16 JARs - 22MB):
  - **JSON**: Google Gson 2.10.1 for robust JSON parsing/generation
  - **Databases**: PostgreSQL JDBC 42.7.1 + MariaDB JDBC 3.3.2
  - **HTTP**: Modern `java.net.http.HttpClient` (Java 11+)
  - **XML**: javax.xml DOM + XPath support
  - **Utilities**: Apache Commons (IO, Lang3, Text, Math3, Codec), Google Guava
  - **Crypto**: Bouncy Castle 1.77 (future)
  - **Web Server**: Jetty 11.0.19 (future)
  - **Parsers**: ANTLR4 4.13.1 (self-hosting goal!)
- **Complete Operators**: 
  - Bitwise: `&` (AND), `|` (OR), `^` (XOR), `<<` (SHL), `>>` (SHR)
  - Logical: `AND`, `OR`, `NOT`, `XOR`
  - Arithmetic: `+`, `-`, `*`, `/`, `%`
  - Comparison: `<`, `>`, `<=`, `>=`, `=`, `<>`
- **255 Functions** across 7 namespaces (Console, Math, File, Http, Json, Xml, Db)

### Object-Oriented Programming (Phase 7)
- `CLASS...END CLASS` with PUBLIC/PRIVATE fields and methods
- Constructors, instance methods, `NEW` operator, `ME` keyword
- Full encapsulation and inheritance foundation

### User-Defined Types (Phase 6)
- `TYPE...ENDTYPE` structures with dot notation member access

### Core Language
- **Functions & Subroutines**: Modern syntax with typed parameters
- **Arrays**: Typed arrays with modern declarations
- **Control Flow**: IF, FOR, WHILE, DO...WHILE/UNTIL with modern syntax
- **I/O**: Console namespace + traditional PRINT/INPUT
- **Comments**: `REM` and `'` (apostrophe)
- **Type System**: Int, Float, String, Bool, Decimal, BigInt, UserDefined
- **Expressions**: Full arithmetic, comparison, logical, bitwise operations

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

# Compile runtime with libraries
javac -cp "lib/*" BasicRuntime.java
cp BasicRuntime.class basicrt/
```

### Compile and Run
```bash
./jvmbasic < program.bas
java -cp ".:lib/*:basicrt" BasicProgram

# Or use build script
./buildrun.sh program.bas
```

### Hello World
```basic
' Modern syntax
Console.WriteLine("Hello, World!")

' Classic syntax (also works)
Print "Hello, World!"
```

## Language Examples

### Object-Oriented Programming
```basic
' Define a class with constructor
CLASS BankAccount
    PRIVATE balance AS SINGLE
    PUBLIC owner AS STRING
    
    PUBLIC SUB New(name AS STRING, initial AS SINGLE)
        owner = name
        balance = initial
    END SUB
END CLASS

' Create and use objects
Dim account As New BankAccount("Alice", 1000.0)
account.balance = 1500.0
Print account.owner; " has $"; account.balance
```

### User-Defined Types (Structs)
```basic
TYPE Person
    name AS STRING
    age AS SINGLE
ENDTYPE

Dim p As Person
p.name = "Alice"
p.age = 30.0
Print "Person: "; p.name; ", age "; p.age
```

### Functions with Recursion
```basic
FUNCTION Factorial(n AS SINGLE) AS SINGLE
    If n <= 1.0 Then
        Return 1.0
    Else
        Return n * Factorial(n - 1.0)
    EndIf
ENDFUNCTION

Print "5! = "; Factorial(5.0)  ' Output: 120.0
```

### Arrays and Array Functions
```basic
Dim numbers(10) = 0.0
For i = 0.0 To 9.0
    numbers(Int(i)) = RNDINT(1, 100)
Next i

Call ARRAYSORT(numbers)
Print "Min: "; ARRAYMIN(numbers)
Print "Max: "; ARRAYMAX(numbers)
Print "Average: "; ARRAYAVG(numbers)
```

### File I/O
```basic
' Modern namespace syntax
Dim dummy As Integer
Let dummy = File.WriteAllText("data.txt", "Hello from JVM BASIC!")
Dim content As String = File.ReadAllText("data.txt")
Let dummy = Console.WriteLine(content)

' Classic syntax (also works)
Dim out = OPENOUTPUT("data.txt")
If out >= 0.0 Then
    Call WRITELINE(out, "Hello from JVM BASIC!")
    Let dummy = CLOSEFILE(out)
EndIf
```

### Regular Expressions
```basic
Dim email = "user@example.com"
If REGEXMATCH(email, "\\w+@\\w+\\.\\w+") Then
    Dim user = REGEXGROUP(email, "(\\w+)@(\\w+\\.\\w+)", 1)
    Dim domain = REGEXGROUP(email, "(\\w+)@(\\w+\\.\\w+)", 2)
    Print "User: "; user
    Print "Domain: "; domain
EndIf
```

## Complete Feature List

### Statements (Case-Insensitive)
- `Print [expr [, | ;] ...] [, | ;]` - Output with optional separators
- `Console.WriteLine(expr)` / `Console.Write(expr)` - Modern I/O
- `Dim var = expr` - Variable assignment (modern) or `Let var = expr` (classic)
- `var(index) = expr` - Array element assignment
- `var.member = expr` - Struct/object member assignment
- `Input var` - Read input (auto type conversion)
- `Dim var(size) = init` - Array declaration
- `Dim var As TypeName` - Typed variable declaration
- `Dim var As New ClassName(args)` - Object instantiation
- `If cond Then ... [ElseIf cond Then ...] [Else ...] EndIf`
- `For var = start To end [Step step] ... Next var`
- `While cond ... EndWhile` (or `Wend`)
- `Do ... While cond` - Post-test loop
- `Do ... Until cond` - Post-test until loop
- `Return [expr]` - Return from function
- `Call name(args)` - Call subroutine
- `Rem comment` - Comment line
- `' comment` - Apostrophe comment

### Declarations (Case-Insensitive)
- `Function name(params [As Type]) [As ReturnType] ... Return expr ... End Function`
- `Sub name(params [As Type]) ... End Sub`
- `Type name ... field As type ... EndType` - User-defined types
- `Class name ... [Public/Private] fields/methods ... End Class` - Object-oriented

### Built-in Functions (255 Total - All Case-Insensitive)

See `docs/USER_GUIDE.md` for complete documentation with examples.

**Console Namespace** (4): `Console.WriteLine()`, `Console.Write()`, `Console.ReadLine()`, `Console.ReadKey()`

**Math Namespace** (20): `Math.Sin()`, `Math.Cos()`, `Math.Tan()`, `Math.Sqrt()`, `Math.Abs()`, `Math.Min()`, `Math.Max()`, `Math.Pow()`, `Math.Log()`, `Math.Exp()`, `Math.Floor()`, `Math.Ceil()`, `Math.Round()`, `Math.PI()`, `Math.E()`, etc.

**File Namespace** (8): `File.ReadAllText()`, `File.WriteAllText()`, `File.Exists()`, `File.Delete()`, `File.Copy()`, `File.Move()`, `File.Size()`, `File.IsDirectory()`

**Http Namespace** (4): `Http.Get()`, `Http.Post()`, `Http.UrlEncode()`, `Http.UrlDecode()`

**Json Namespace** (8): `Json.Parse()`, `Json.NewObject()`, `Json.Put()`, `Json.PutInt()`, `Json.GetString()`, `Json.GetInt()`, `Json.GetFloat()`, `Json.ToString()`

**Xml Namespace** (2): `Xml.Parse()`, `Xml.GetText()`

**Db Namespace** (6): `Db.Connect()`, `Db.Query()`, `Db.Next()`, `Db.GetString()`, `Db.GetInt()`, `Db.Close()`

**Classic Functions**: 199 additional functions including Math, String, Array, File I/O, Regex, Collections, Date/Time, and more

## Architecture

### Compiler Pipeline
1. **Lexer** - Tokenization with keywords, operators, literals
2. **Parser** - Recursive descent parser building typed AST
3. **Type Inference** - Multi-pass inference for parameters and arrays
4. **Code Generation** - JVM bytecode emission to `.class` file

### Files
- **Compiler** (8,782 lines C++):
  - `main.cpp` - Entry point and CLI
  - `lexer.cpp/.h` - Tokenization
  - `parser.cpp/.h` - Recursive descent parser
  - `ast.cpp/.h` - Abstract Syntax Tree definitions
  - `semantic.cpp/.h` - Type inference and analysis
  - `codegen.h` - JVM bytecode generator
  - `ast_printer.cpp/.h` - Pretty-printing for debugging
  - `builtin_functions.cpp/.h` - Built-in function registry
- **Runtime** (2,108 lines Java):
  - `BasicRuntime.java` - All 255 functions + namespace implementations

### Code Generation
- **Target**: JVM bytecode (Java 5 / version 49)
- **Class Name**: `BasicProgram` (default)
- **Method**: Single `public static void main(String[])`
- **Locals**: Efficiently allocated local variable slots
- **Structs**: Implemented as `Object[]` arrays with boxing/unboxing

## Testing

### Test Suite
- **81 tests passing** ✅ (100% automated success rate)
  - **Phase 9 tests (9)**: XML parsing, JSON (Gson), PostgreSQL, MariaDB, all types, bitwise ops, namespaces
  - **Phase 8 tests (56)**: Built-in functions, string operations, math, file I/O, collections
  - **Phase 7 OOP tests (7)**: Classes, inheritance, methods, constructors
  - **Phase 6 tests (4)**: User-defined types (structs)
  - **Core tests (5+)**: Variables, arrays, control flow, operators, functions
  - **Stdin tests (3)**: Skipped in automation (test_input.bas, test_input_simple.bas, input.bas)
- Automated test runner: `./test_runner.sh`

### Run Tests
```bash
./test_runner.sh              # All 81 automated tests
./dump_test_artifacts.sh      # Generate AST and bytecode dumps for debugging

# Test results show:
# Passed:  81
# Failed:  0
# Skipped: 3 (require stdin)
# Total:   84
```

## Documentation

### User Documentation (`docs/`)
- `docs/USER_GUIDE.md` - Complete user guide with all 255 functions and examples
- `docs/user/` - User example programs and showcases
- `README.md` - This file (quick start and overview)

### Developer Documentation (`docs/dev/`)
- `docs/dev/CODE_GUIDE.md` - Complete developer guide
- `docs/dev/AST_GUIDE.md` - AST structure and extension guide
- `docs/dev/LEXER_GUIDE.md` - Lexer internals and extension
- `docs/dev/DEBUGGING_GUIDE.md` - Debugging techniques
- `docs/dev/MODULAR_ARCHITECTURE.md` - Modular compiler architecture
- `docs/dev/PHASE7_IMPLEMENTATION_GUIDE.md` - OOP implementation details

### Planning & Phase Documentation
- `docs/planning/` - Design documents for each phase
- `docs/phase9/` - Phase 9 (Modern Syntax) completion reports and progress
- `docs/sessions/` - Historical session summaries

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

### Phase 8 (Complete) ✅
- ✅ 199 built-in functions (+106 from Phase 7)
- ✅ Logical operators (AND, OR, NOT, XOR)
- ✅ Collections (IntList, StringList, Map, Stack, Queue)
- ✅ Date/Time functions (21 functions)
- ✅ Enhanced string and file I/O

### Phase 9 (Complete) ✅
- ✅ Modern VB-style syntax (Dim x As Integer = 10)
- ✅ Fully case-insensitive keywords
- ✅ Modern function syntax (Function Add() As Integer)
- ✅ Namespace/OO syntax (Console.WriteLine, Math.Sin)
- ✅ Web capabilities (Http.Get, Http.Post)
- ✅ JSON support (Json.Parse, Json.ToString)
- ✅ File namespace (File.ReadAllText, File.WriteAllText)
- ✅ Database support (Db.Connect, Db.Query)
- ✅ Bitwise operators (<< >>)
- ✅ Decimal and BigInt types
- ✅ 255 total functions (+56 from Phase 8)

### Phase 10 (Future)
- Inheritance (Inherits keyword)
- Method overriding
- Interfaces
- Static members (Shared keyword)
- Module system and imports
- String instance methods
- Enhanced collections with generics

## Examples

### Complete Programs

**17 examples in `examples/latest/`** - All using modern VB-style syntax:

**Algorithms:**
- `fibonacci_sequence.bas` - Recursive Fibonacci implementation
- `math_algorithms.bas` - GCD, LCM, factorial algorithms
- `sorting_algorithms.bas` - Bubble and selection sort
- `prime_numbers.bas` - Prime number finder with optimization
- `password_generator.bas` - Secure random password generation

**Data & Statistics:**
- `statistics.bas` - Mean, median, mode, standard deviation
- `text_analyzer.bas` - Word frequency, text statistics
- `log_processor.bas` - Log file parsing and analysis

**Object-Oriented:**
- `oop_bank_account.bas` - Banking system with classes and methods
- `oop_geometry.bas` - Shape hierarchy with inheritance
- `oop_contact_manager.bas` - Contact management with classes

**File & Web:**
- `file_backup_utility.bas` - File backup and restoration utility
- `modern_web_app.bas` - Web API integration with JSON

**Games & Utilities:**
- `lotto.bas` - Lottery number generator
- `lotto_improved.bas` - Advanced lottery with statistics

**Demonstrations:**
- `comprehensive_demo.bas` - All Phase 9 features in one program
- `modern_syntax_demo.bas` - Modern VB syntax showcase

### Test Programs

**81 tests in `tests/` directory** - All automated:

**Phase 9 Tests (9):**
- `test_xml_namespace.bas` - XML parsing with javax.xml and XPath
- `test_json_real.bas` - JSON with Google Gson library
- `test_postgres.bas` - PostgreSQL database connectivity
- `test_mariadb.bas` - MariaDB database connectivity
- `test_db_namespace.bas` - Database wrapper functions
- `test_all_types.bas` - All 8 type keywords
- `test_bitwise_complete.bas` - All 5 bitwise operators
- `test_console_readkey.bas` - Console namespace
- `test_all_namespaces.bas` - Integration test for all 7 namespaces

**Phase 8 Tests (56):** Built-in functions, string ops, math, file I/O  
**Phase 7 OOP Tests (7):** Classes, inheritance, constructors  
**Phase 6 Tests (4):** User-defined types (structs)  
**Core Tests:** Variables, arrays, control flow, operators, functions
- **Control Flow** (`test_if.bas`, `test_for.bas`, `test_while.bas`)
- **I/O** (`test_input*.bas`, `test_print*.bas`)
- **Advanced** (`test_algorithms.bas`, `test_comprehensive.bas`) - Complete feature demos

## Building and Running

### Requirements
- C++20 compiler (g++ 10+, clang 12+)
- JDK/JRE 11+ (for running compiled programs)
- Make (optional, for build system)

**Integrated Professional Libraries** (16 JARs - 22MB in `lib/`):
- **Google Gson 2.10.1** - JSON parsing/generation
- **PostgreSQL JDBC 42.7.1** - PostgreSQL database driver
- **MariaDB JDBC 3.3.2** - MariaDB/MySQL database driver
- **Apache Commons IO 2.15.1** - File I/O utilities
- **Apache Commons Lang3 3.14.0** - String/Array utilities
- **Apache Commons Text 1.11.0** - Advanced text processing
- **Apache Commons Math3 3.6.1** - Mathematical algorithms
- **Apache Commons Codec 1.16.0** - Encoding/decoding utilities
- **Google Guava 33.0.0** - Collections and utilities
- **Bouncy Castle Provider 1.77** - Comprehensive cryptography
- **Bouncy Castle PKIX 1.77** - Certificate validation
- **Jetty Server 11.0.19** - Embedded HTTP server
- **Jetty Servlet 11.0.19** - Servlet support
- **Jetty Util 11.0.19** - Web utilities
- **ANTLR4 Complete 4.13.1** - Parser generation (self-hosting goal!)
- **ANTLR4 Runtime 4.13.1** - Parser runtime

📖 See `lib/README.md` and `PROFESSIONAL_CAPABILITIES.md` for full details.

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
**Branch**: `phase9-modern-syntax` (Phase 9 complete)  
**Development**: Ready for Phase 10

## License

Public domain / MIT - choose what fits your needs.

---

**JVM BASIC** - A modern BASIC compiler for the JVM with Visual Basic-style syntax, web capabilities (HTTP, JSON), database support, and 255 built-in functions. Phase 9 complete! 🚀
