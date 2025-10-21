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

### Build the Compiler

**Using Make:**
```bash
# Clean build
make clean && make

# This compiles all C++ modules and links them into ./jvmbasic
```

**Manual Build:**
```bash
# Compile individual modules
g++ -std=gnu++20 -O2 -c ast.cpp
g++ -std=gnu++20 -O2 -c lexer.cpp
g++ -std=gnu++20 -O2 -c parser.cpp
g++ -std=gnu++20 -O2 -c semantic.cpp
g++ -std=gnu++20 -O2 -c ast_printer.cpp
g++ -std=gnu++20 -O2 -c builtin_functions.cpp
g++ -std=gnu++20 -O2 -c main.cpp

# Link all modules
g++ -std=gnu++20 -O2 *.o -o jvmbasic
```

**Compile Runtime with Libraries:**
```bash
# Compile with all 16 professional libraries
javac -cp "lib/*" BasicRuntime.java
cp BasicRuntime.class basicrt/

# Or use the provided script
./compile_runtime.sh
```

### Compile a BASIC Program

**Basic Compilation:**
```bash
./jvmbasic < program.bas
# Generates: BasicProgram.class
```

**Custom Class Name:**
```bash
./jvmbasic -o MyProgram < program.bas
# Generates: MyProgram.class
```

**Debug with AST Dump:**
```bash
./jvmbasic --dump-ast < program.bas
# Prints the Abstract Syntax Tree (pretty-printed)
```

**Check Syntax Only (No Code Generation):**
```bash
./jvmbasic --check-only < program.bas
# Parse and type-check only, useful for syntax validation
```

**All Command-Line Options:**
```bash
./jvmbasic --help

Options:
  -o <name>       Output class name (default: BasicProgram)
  --dump-ast      Print AST and exit (for debugging)
  --check-only    Parse and type-check, don't generate code
  --help          Show this help message
```

### Run a Compiled Program

**With Libraries (Recommended):**
```bash
java -cp ".:lib/*:basicrt" BasicProgram
```

**Without Libraries (Core Features Only):**
```bash
java -cp ".:basicrt" BasicProgram
```

**Windows:**
```cmd
java -cp ".;lib/*;basicrt" BasicProgram
```

**Custom Class Name:**
```bash
java -cp ".:lib/*:basicrt" MyProgram
```

### Complete Workflow

**Using the Build Script (Easiest):**
```bash
./buildrun.sh program.bas
# Automatically compiles runtime, compiles program, and runs it
```

**Manual Step-by-Step:**
```bash
# 1. Compile runtime (once)
javac -cp "lib/*" BasicRuntime.java
cp BasicRuntime.class basicrt/

# 2. Compile your BASIC program
./jvmbasic < program.bas

# 3. Run it
java -cp ".:lib/*:basicrt" BasicProgram
```

### Hello World

**Modern Syntax (Recommended):**
```basic
' hello_world.bas - Modern VB-style syntax
Console.WriteLine("Hello, World!")
Console.WriteLine("Welcome to JVM BASIC!")
```

**With Variables:**
```basic
Dim name As String = "Alice"
Console.WriteLine("Hello, " + name + "!")
```

**Classic Syntax (Still Works):**
```basic
Print "Hello, World!"
```

## Language Examples

### Object-Oriented Programming
```basic
' Define a class with constructor and methods
Class BankAccount
    Private balance As Single
    Public owner As String
    
    Public Sub New(name As String, initial As Single)
        Me.owner = name
        Me.balance = initial
    End Sub
    
    Public Sub Deposit(amount As Single)
        If amount > 0.0 Then
            Me.balance = Me.balance + amount
            Console.WriteLine("Deposited: " + Str(amount))
        End If
    End Sub
    
    Public Function GetBalance() As Single
        Return Me.balance
    End Function
End Class

' Create and use objects
Dim account As New BankAccount("Alice", 1000.0)
account.Deposit(500.0)
Console.WriteLine(account.owner + " has $" + Str(account.GetBalance()))
```

### User-Defined Types (Structs)
```basic
Type Person
    name As String
    age As Single
End Type

Dim person As Person
person.name = "Alice"
person.age = 30.0
Console.WriteLine("Person: " + person.name + ", age " + Str(person.age))
```

### Functions with Recursion
```basic
Function Factorial(n As Single) As Single
    If n <= 1.0 Then
        Return 1.0
    Else
        Return n * Factorial(n - 1.0)
    End If
End Function

Console.WriteLine("5! = " + Str(Factorial(5.0)))  ' Output: 120.0
```

### Arrays and Array Functions
```basic
Dim numbers(10) As Single = 0.0
For i = 0.0 To 9.0
    numbers(Int(i)) = RndInt(1, 100)
Next i

ArraySort(numbers)
Console.WriteLine("Min: " + Str(ArrayMin(numbers)))
Console.WriteLine("Max: " + Str(ArrayMax(numbers)))
Console.WriteLine("Average: " + Str(ArrayAvg(numbers)))
```

### File I/O (Phase 9)
```basic
' Modern namespace syntax with expression statements
File.WriteAllText("data.txt", "Hello from JVM BASIC!")
Dim content As String = File.ReadAllText("data.txt")
Console.WriteLine(content)

' Check if file exists
If File.Exists("data.txt") Then
    Console.WriteLine("File exists!")
    Console.WriteLine("File size: " + Str(File.Size("data.txt")) + " bytes")
End If

' File operations
File.Copy("data.txt", "backup.txt")
File.Move("backup.txt", "backup2.txt")
File.Delete("backup2.txt")
```

### Regular Expressions
```basic
Dim email As String = "user@example.com"
If RegexMatch(email, "\\w+@\\w+\\.\\w+") Then
    Dim user As String = RegexGroup(email, "(\\w+)@(\\w+\\.\\w+)", 1)
    Dim domain As String = RegexGroup(email, "(\\w+)@(\\w+\\.\\w+)", 2)
    Console.WriteLine("User: " + user)
    Console.WriteLine("Domain: " + domain)
End If
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
