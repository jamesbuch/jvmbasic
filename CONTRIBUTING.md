# Contributing to JVM BASIC

Welcome! This guide will help you understand and extend the JVM BASIC compiler.

---

## Project Structure

```
jvmbasic/
├── Source Code
│   ├── lexer.h, lexer.cpp       # Tokenization
│   ├── parser.h, parser.cpp     # AST construction
│   ├── semantic.h, semantic.cpp # Type checking  
│   ├── ast.h, ast.cpp           # AST data structures
│   ├── ast_printer.h, .cpp      # AST visualization
│   ├── builtin_functions.h, .cpp # 93 built-in functions
│   ├── codegen.h                # JVM bytecode generation
│   └── main.cpp                 # Compiler driver
│
├── Build System
│   ├── Makefile                 # Build configuration
│   └── g++-15-wrapper           # Compiler wrapper
│
├── Tests
│   ├── tests/*.bas              # Test programs (54 files)
│   ├── test_runner.sh           # Core test runner
│   └── run_input_tests.sh       # INPUT test runner
│
├── Documentation
│   ├── README.md                # Project overview
│   ├── docs/USER_GUIDE.md       # User documentation
│   ├── docs/USER_GUIDE_PHASE7.md # OOP guide
│   ├── docs/dev/                # Developer guides
│   └── docs/planning/           # Design documents
│
└── Examples
    └── examples/*.bas           # Example programs
```

---

## Building and Testing

### Quick Start

```bash
# Build
make clean && make

# Run tests
./test_runner.sh

# Test specific file
./jvmbasic < tests/test_functions.bas && java BasicProgram
```

### Development Workflow

```bash
# 1. Edit source files (lexer.cpp, parser.cpp, etc.)

# 2. Build
make

# 3. Test parsing
./jvmbasic --dump-ast < your_test.bas

# 4. Test compilation
./jvmbasic < your_test.bas && java BasicProgram

# 5. Inspect bytecode (if needed)
javap -v -c BasicProgram
```

---

## Compiler Architecture

### Phase 1: Lexical Analysis (Lexer)

**File**: `lexer.cpp`

**Converts**:
```
"CLASS Point\n  PUBLIC x\nEND CLASS"
```
**Into**:
```
[CLASS] [ID:"Point"] [PUBLIC] [ID:"x"] [ENDCLASS]
```

**To Add Keywords**:
1. Add to `TokenType` enum in `lexer.h`
2. Add recognition in `nextToken()` in `lexer.cpp`

### Phase 2: Parsing (Parser)

**File**: `parser.cpp`

**Converts**: Tokens → AST

**To Add New Syntax**:
1. Extend AST in `ast.h`
2. Add parsing method in `parser.cpp`
3. Update `parse()` to call new method

**Example**: Adding a new statement

```cpp
// 1. Add to StmtKind enum in ast.h
enum class StmtKind { ..., MyNewStmt };

// 2. Create structure in ast.h
struct MyNewStmtNode { string data; };

// 3. Add to variant and constructor
variant<..., MyNewStmtNode> data;
Stmt(StmtKind k, MyNewStmtNode m) : kind(k), data(std::move(m)) {}

// 4. Parse in parseStmt() in parser.cpp
if (tok.type == TokenType::MYNEW) {
    next();
    string data = expect(TokenType::ID).val;
    return make_unique<Stmt>(StmtKind::MyNewStmt, MyNewStmtNode{data});
}
```

### Phase 3: Semantic Analysis (Semantic Analyzer)

**File**: `semantic.cpp`

**Validates**: Type correctness, symbol resolution

**To Add Type Checking**:
1. Handle in `inferExprType()` - return the type
2. Handle in `analyzeExpr()` - validate children
3. Handle in `analyzeStmt()` - validate statement

### Phase 4: Code Generation (Codegen)

**File**: `codegen.h`

**Generates**: JVM bytecode

**To Add Bytecode**:
1. Handle in `load()` for expressions
2. Handle in `genStmt()` for statements
3. Emit JVM instructions using helper methods

---

## AST Types Reference

### Expressions (ExprKind)

| Kind | Purpose | Example |
|------|---------|---------|
| Num | Number literal | 42, 3.14 |
| Str | String literal | "Hello" |
| Var | Variable reference | x, arr(i) |
| Bin | Binary operation | a + b |
| Cmp | Comparison | x < 10 |
| Call | Function call | SQRT(x) |
| Unary | Unary operation | -x |
| MemberAccess | Struct/class field | obj.field |
| NewExpr | Object creation | NEW Point(3, 4) |
| MethodCall | Method invocation | obj.method() |
| Me | Self reference | ME |

### Statements (StmtKind)

| Kind | Purpose | Example |
|------|---------|---------|
| Print | Output | PRINT x |
| Let | Assignment | LET x = 10 |
| Input | User input | INPUT x |
| Dim | Declaration | DIM arr(10) = 0 |
| If | Conditional | IF x > 0 THEN |
| For | For loop | FOR i = 1 TO 10 |
| While | While loop | WHILE x < 10 |
| DoWhile | Do-while loop | DO ... WHILE x |
| Return | Return value | RETURN x |
| CallStmt | Call SUB | CALL MySub() |
| MethodCallStmt | Call method | CALL obj.Method() |

### Declarations (DeclKind)

| Kind | Purpose | Example |
|------|---------|---------|
| Function | Function declaration | FUNCTION Add(x, y) |
| Sub | Subroutine declaration | SUB PrintMsg() |
| TypeDef | User-defined type | TYPE Point ... ENDTYPE |
| Class | Class declaration | CLASS Point ... END CLASS |

---

## Testing Guidelines

### Creating Tests

**Location**: `tests/test_*.bas`

**Naming**:
- `test_feature_case.bas` (e.g., `test_class_constructor.bas`)
- Descriptive names
- One feature per test (focused testing)

**Structure**:
```basic
REM test_feature.bas - Description
REM Tests: Specific features being tested

' Test code here
PRINT "Expected output"

' Expected output in comments when helpful
```

### Test Organization

- **Core tests**: General language features
- **Phase-specific tests**: `test_class_*` for Phase 7
- **Edge cases**: `test_array_complex`, `test_func_recursion`

### Running Tests

```bash
# All tests
./test_runner.sh

# Specific phase
for t in tests/test_class*.bas; do
    ./jvmbasic < "$t" && java BasicProgram
done

# Single test
./jvmbasic < tests/test_class_basic.bas && java BasicProgram
```

---

## Documentation Standards

### Code Comments

```cpp
// Brief description of what this does
void myFunction() {
    // Implementation details
}
```

### Function Headers

```cpp
// Parse a CLASS declaration
// Expects: CLASS keyword already consumed
// Returns: DeclPtr with ClassDecl
// Throws: runtime_error on syntax error
DeclPtr parseClassDecl() {
    ...
}
```

### Documentation Files

- **User Guide**: Language features and syntax
- **Developer Guide**: Architecture and contribution
- **Design Docs**: Phase planning and decisions
- **Session Docs**: Handoff between sessions

---

## Development Best Practices

### 1. Incremental Development

- Implement one feature at a time
- Test after each change
- Don't break existing tests

### 2. Use Debugging Tools

```bash
# AST dump
./jvmbasic --dump-ast < test.bas

# Syntax check only
./jvmbasic --check-only < test.bas

# Bytecode inspection
javap -v -c BasicProgram

# Debug with GDB
gdb --args ./jvmbasic < test.bas
```

### 3. Follow Patterns

Look at existing code:
- How is IF parsed? Follow that pattern for new control flow
- How is FUNCTION parsed? Follow that for new declarations
- How is Bin generated? Follow that for new expressions

### 4. Test-Driven Development

1. Write test case first
2. Implement feature
3. Verify test passes
4. Check no regressions

---

## Common Tasks

### Adding a New Keyword

**Example**: Add REPEAT keyword

```cpp
// 1. lexer.h - Add token type
enum class TokenType { ..., REPEAT };

// 2. lexer.cpp - Add recognition
if (upper == "REPEAT") return {TokenType::REPEAT, s, 0.0, tokenLine};

// 3. Done! Now use in parser
```

### Adding a New Expression Type

**Example**: Add string interpolation

```cpp
// 1. ast.h - Add kind and structure
enum class ExprKind { ..., StringInterpolation };
struct StringInterpExpr { string format; vector<ExprPtr> args; };

// 2. ast.h - Add to variant and constructor
variant<..., StringInterpExpr> data;
Expr(ExprKind k, Type t, StringInterpExpr si) : ...;

// 3. parser.cpp - Parse it
ExprPtr parseStringInterp() { ... }

// 4. ast_printer.cpp - Print it
case ExprKind::StringInterpolation: { ... }

// 5. semantic.cpp - Check it
case ExprKind::StringInterpolation: { ... }

// 6. codegen.h - Generate bytecode
case ExprKind::StringInterpolation: { ... }
```

### Adding a New Built-in Function

**File**: `builtin_functions.cpp`

```cpp
// Add to builtinFunctions map
builtinFunctions["MYFUNCTION"] = FunctionSig{
    {Type::Float},       // Parameter types
    Type::String,        // Return type
    "myFunction",        // Java method name
    "(F)Ljava/lang/String;"  // JVM descriptor
};
```

**File**: `BasicRuntime.java`

```java
public static String myFunction(float x) {
    return "Result: " + x;
}
```

---

## Troubleshooting

### Compilation Errors

**Error**: `no matching function for call`  
**Solution**: Check variant constructors in ast.h

**Error**: `use of deleted function` (unique_ptr)  
**Solution**: Use `std::move()`

**Error**: `incomplete type`  
**Solution**: Include proper header or forward declare

### Runtime Errors

**Error**: VerifyError (wrong type on stack)  
**Solution**: Check bytecode generation in codegen.h

**Error**: SegmentationFault in parser  
**Solution**: Use gdb to find nullptr dereference

---

## Getting Help

### Documentation

- **Language**: `docs/USER_GUIDE.md`, `docs/USER_GUIDE_PHASE7.md`
- **Architecture**: `docs/dev/MODULAR_ARCHITECTURE.md`
- **Code Walkthrough**: `docs/dev/CODE_GUIDE.md`
- **AST**: `docs/dev/AST_GUIDE.md`

### Examples

- Look at `tests/` for working examples
- Check `examples/` for demo programs
- Review existing parsing code for patterns

---

## Version Control

### Branch Strategy

- `main` - Stable releases
- `phase7-oop` - Current development
- Feature branches as needed

### Commit Messages

```
Add CLASS parsing support

- Implement parseClassDecl() in parser.cpp
- Add ClassDecl to AST
- Update AST printer
- Tests: 7/7 Phase 7 tests parse correctly
```

Be descriptive and include test status.

---

## Questions?

Check existing documentation:
- `README.md` - Project overview
- `docs/dev/` - Developer guides
- `docs/planning/` - Design decisions
- Previous chat logs in `previous-chats/`

---

**Happy Hacking!** 🚀


