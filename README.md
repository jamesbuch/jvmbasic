# JVM BASIC

A modern BASIC compiler targeting the JVM (Java Virtual Machine).

## Two Implementations

### JVM BASIC 2.0 (Active Development)

A clean-room rewrite using ANTLR4 for parsing and ASM for bytecode generation. Written in Java.

**Location:** `src/java/`

**Implemented Features:**
| Feature | Status | Example |
|---------|--------|---------|
| Variables | ✅ | `var x as Integer = 10` |
| All numeric types | ✅ | Integer, Long, Float, Double |
| Strings | ✅ | `var s as String = "Hello"` |
| Booleans | ✅ | `var b as Boolean = true` |
| Arithmetic | ✅ | `+`, `-`, `*`, `/`, `mod` |
| Comparisons | ✅ | `<`, `>`, `<=`, `>=`, `=`, `<>` |
| Logical ops | ✅ | `and`, `or`, `not` |
| If/ElseIf/Else | ✅ | Full conditional branching |
| For loops | ✅ | `for i = 1 to 10 step 2` |
| For Each loops | ✅ | `for each x in array` |
| While loops | ✅ | `while x < 10 ... end while` |
| Do loops | ✅ | All variants (while/until) |
| Arrays | ✅ | `new Integer[5]`, `arr[0]` |
| Functions | ✅ | With parameters and return values |
| Console I/O | ✅ | `Console.WriteLine`, `ReadLine` |
| String interpolation | ✅ | `$"Hello {name}!"` |
| Exit/Continue | ✅ | `exit for`, `continue while`, etc. |
| Select Case | ✅ | Multi-value cases, Case Else |
| Math namespace | ✅ | `Math.Sqrt()`, `Math.Sin()`, etc. |
| Str namespace | ✅ | `Str.ToUpper()`, `Str.Length()`, etc. |
| Regex namespace | ✅ | `Regex.IsMatch()`, `Regex.Replace()`, etc. |
| File namespace | ✅ | `File.ReadAllText()`, `File.WriteAllText()`, etc. |

**Coming Soon:**
- More standard library (Http, Json, Db)
- Classes and OOP

**Documentation:**
- [User Guide](src/java/docs/USER_GUIDE.md)
- [Developer Guide](src/java/docs/DEVELOPER_GUIDE.md)
- [Advanced Features Strategy](src/java/docs/ADVANCED_FEATURES_STRATEGY.md)

**Quick Start:**
```bash
cd src/java
./gradlew build

# Compile a program
java -jar build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar examples/hello.jvmb

# Run it
java hello

# Run test suite
./test-examples.sh
```

---

### Legacy JVM BASIC (C++ Implementation)

The original C++ implementation with 23 namespaces, 280+ functions, OOP support, database connectivity, and more.

**Status:** Feature-complete but no longer in active development. Maintained for reference.

**Location:** Root directory (`.cpp` files)

**Documentation:** [docs/legacy-jvmbasic/](docs/legacy-jvmbasic/)

**Quick Start:**
```bash
# Build compiler and runtime
./rebuild.sh

# Compile a BASIC program
./jvmbasic -o MyProgram < program.bas

# Run with all libraries
java -cp '.:basicrt:lib/*' MyProgram
```

**Features:**
- 23 namespaces (Console, File, Http, Json, Db, Crypto, Thread, etc.)
- 280+ built-in functions
- OOP: Classes, interfaces, constructors, encapsulation
- Database: PostgreSQL, MariaDB with parameterized queries
- Web: HTTP client, JSON/XML parsing
- Crypto: SHA-256/512, AES, Base64
- Arbitrary precision: BigInt, Decimal

---

## Project Structure

```
jvmbasic/
├── src/java/                  # JVM BASIC 2.0 (ANTLR4 + ASM)
│   ├── com/jvmbasic/
│   │   ├── grammar/          # ANTLR4 grammar files
│   │   ├── ir/               # Intermediate representation
│   │   └── visitor/          # Code generation
│   ├── docs/                 # JVM BASIC 2.0 documentation
│   └── examples/             # Example programs (.jvmb files)
│
├── *.cpp, *.h                # Legacy C++ compiler
├── BasicRuntime.java         # Legacy Java runtime
├── basicrt/                  # Compiled runtime classes
├── lib/                      # Library JARs (JDBC, Gson, etc.)
├── examples/                 # Legacy examples (.bas files)
├── tests/                    # Legacy test suite
│
└── docs/
    ├── CLAUDE.md             # AI assistant context
    ├── jvmbasic-2.0/         # Planning docs for 2.0
    └── legacy-jvmbasic/      # Legacy documentation
```

---

## License

Public domain / MIT - choose what fits your needs.
