# JVM BASIC

A modern BASIC compiler targeting the JVM (Java Virtual Machine).

## Two Implementations

### JVM BASIC 2.0 (Active Development)

A clean-room rewrite using ANTLR4 for parsing and ASM for bytecode generation. Written in Java.

**Location:** `src/java/`

**Features:**
- Modern VB-style syntax: `var x as Integer = 10`
- Console I/O via namespaced calls: `Console.WriteLine("Hello")`
- All primitive types: Integer, Long, Float, Double, String, Boolean
- Control flow: If/ElseIf/Else, For loops (with STEP), While, Do loops (all variants)
- User-defined functions with parameters and return values
- ANTLR4 grammar with ASM bytecode generation

**Documentation:**
- [User Guide](src/java/docs/USER_GUIDE.md)
- [Developer Guide](src/java/docs/DEVELOPER_GUIDE.md)
- [IR to Bytecode Design](src/java/docs/IR_TO_BYTECODE.md)

**Quick Start:**
```bash
cd src/java
./gradlew build

# Compile a program
java -jar build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar examples/hello.jvmb

# Run it
java hello
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
