# JVM BASIC Modular Architecture Guide

**Date**: October 13, 2025  
**Version**: Phase 7 (Parsing Complete)

---

## Overview

JVM BASIC is built with a clean, modular compiler architecture following traditional compiler design:

**Compilation Pipeline**:
```
Source Code (.bas)
    ↓
[1] LEXER (lexer.cpp) → Tokens
    ↓
[2] PARSER (parser.cpp) → AST
    ↓
[3] SEMANTIC ANALYZER (semantic.cpp) → Validated AST
    ↓
[4] CODE GENERATOR (codegen.h) → JVM Bytecode (.class)
```

---

## Component Architecture

### 1. Lexer (lexer.cpp/h)

**Purpose**: Convert source text into tokens  
**Input**: Character stream  
**Output**: Stream of tokens

**Responsibilities**:
- Tokenize keywords (PRINT, IF, CLASS, etc.)
- Recognize literals (numbers, strings, booleans)
- Handle operators (+, -, *, /, ==, <, etc.)
- Skip whitespace and comments (REM, ')
- Track line numbers for error reporting

**Key Types**:
```cpp
enum class TokenType { ... };
struct Token {
    TokenType type;
    string val;
    double num;
    int line;
};
```

**Entry Point**:
```cpp
Lexer lexer(cin);
Token tok = lexer.nextToken();
```

---

### 2. Parser (parser.cpp/h)

**Purpose**: Build Abstract Syntax Tree from tokens  
**Input**: Token stream from lexer  
**Output**: Program (AST)

**Responsibilities**:
- Recognize language grammar
- Build AST nodes (Expr, Stmt, Decl)
- Track user-defined types and classes
- NO type checking (pure structural parsing)

**Key Types**:
```cpp
class Parser {
    Program parse();
    const map<string, TypeDefDecl>& getUserTypes();
    const set<string>& getUserClassNames();
    const map<string, Type>& getKnownTypes();
};
```

**Entry Point**:
```cpp
Parser parser(lexer);
Program program = parser.parse();
```

---

### 3. AST (ast.h/cpp)

**Purpose**: Data structures representing the program  
**Input**: N/A (data structures)  
**Output**: N/A

**Key Structures**:

**Expressions**:
```cpp
enum class ExprKind {
    Num, Str, Var, Bin, Cmp, Call, Unary,
    MemberAccess,  // Phase 6
    NewExpr, MethodCall, Me  // Phase 7
};

struct Expr {
    ExprKind kind;
    Type type;
    variant<...> data;
};
```

**Statements**:
```cpp
enum class StmtKind {
    Print, Let, Input, Dim, If, For, While, DoWhile, Return,
    CallStmt, MethodCallStmt  // Phase 7
};

struct Stmt {
    StmtKind kind;
    variant<...> data;
};
```

**Declarations**:
```cpp
enum class DeclKind {
    Function, Sub, TypeDef,  // Phases 5-6
    Class  // Phase 7
};

struct Decl {
    DeclKind kind;
    variant<FunctionDecl, SubDecl, TypeDefDecl, ClassDecl> data;
};
```

---

### 4. Semantic Analyzer (semantic.cpp/h)

**Purpose**: Type checking and validation  
**Input**: AST from parser  
**Output**: Validated AST with inferred types

**Responsibilities**:
- Infer expression types
- Check type compatibility
- Validate variable declarations
- Build symbol tables with scoping
- Check function/method signatures

**Current Status**:
- ✅ Phase 5 complete (functions, arrays)
- ⚠️ Phase 6 partial (TYPE not fully supported)
- ⚠️ Phase 7 basic (doesn't fail on CLASS)

**Key Types**:
```cpp
class SemanticAnalyzer {
    bool analyze(Program& prog);
    bool hasErrors();
    vector<string> getErrors();
};
```

---

### 5. Code Generator (codegen.h)

**Purpose**: Generate JVM bytecode  
**Input**: Validated AST  
**Output**: .class file (JVM bytecode)

**Responsibilities**:
- Generate JVM class file structure
- Generate constant pool
- Generate method bytecode
- Generate field declarations
- Handle all expressions and statements
- Emit valid JVM instructions

**Current Status**:
- ✅ Phases 1-6 complete
- ⏳ Phase 7 in progress (nested classes needed)

**Key Types**:
```cpp
class ClassFile {
    void buildConstantPool();
    void initStructs(...);
    void generate(declarations, statements, knownTypes);
    void write(ostream& out);
};
```

---

### 6. AST Printer (ast_printer.cpp/h)

**Purpose**: Visualize AST for debugging  
**Input**: AST  
**Output**: Human-readable AST dump

**Usage**:
```bash
./jvmbasic --dump-ast < program.bas
```

**Example Output**:
```
=== AST Dump ===

--- Declarations ---
CLASS POINT
  PUBLIC x AS Float
  PUBLIC SUB New(px:Float)
    LET x = [Float] px
  END SUB
END CLASS

--- Main Program ---
DIM p AS [Unknown] NEW POINT([Int] 3)
```

---

### 7. Main Driver (main.cpp)

**Purpose**: Coordinate all phases  
**Input**: Command-line arguments  
**Output**: Compiled .class file

**Flow**:
```cpp
1. Parse command-line arguments
2. Create Lexer
3. Create Parser and parse
4. Run Semantic Analysis
5. Generate bytecode (if not --dump-ast or --check-only)
6. Write .class file
```

**Command-Line Options**:
- `--dump-ast`: Print AST and exit
- `--check-only`: Parse and validate only (no codegen)
- `-o filename.class`: Specify output file

---

## Build System

### Makefile Structure

```makefile
OBJECTS = ast.o lexer.o parser.o semantic.o ast_printer.o builtin_functions.o

jvmbasic: $(OBJECTS) main.o
	$(CXX) $(CXXFLAGS) $(OBJECTS) main.o -o jvmbasic
```

**Targets**:
- `make` - Build compiler
- `make clean` - Remove build artifacts
- `make test` - Run simple test
- `make dump-ast` - Test AST dumping
- `make check` - Test syntax checking

---

## Data Flow

### Parsing Phase

```
Source: "CLASS Point\n  PUBLIC x AS FLOAT\nEND CLASS"
    ↓ Lexer
Tokens: [CLASS] [ID:"Point"] [PUBLIC] [ID:"x"] [AS] [ID:"FLOAT"] [ENDCLASS]
    ↓ Parser
AST: Decl {
    kind: DeclKind::Class,
    data: ClassDecl {
        name: "POINT",
        fields: [Field{name:"x", type:Float, isPublic:true}],
        methods: []
    }
}
```

### Type Checking Phase

```
AST
    ↓ Semantic Analyzer
Validated AST (types inferred, symbols resolved)
```

### Code Generation Phase

```
Validated AST
    ↓ Code Generator
JVM Bytecode (.class file)
```

---

## File Organization

```
jvmbasic/
├── lexer.h, lexer.cpp       # Tokenization
├── parser.h, parser.cpp     # AST construction
├── semantic.h, semantic.cpp # Type checking
├── ast.h, ast.cpp           # AST data structures
├── ast_printer.h, .cpp      # AST visualization
├── builtin_functions.h, .cpp # 93 built-in functions
├── codegen.h                # Bytecode generation (header-only)
├── main.cpp                 # Driver
├── Makefile                 # Build system
├── tests/                   # Test suite
├── docs/                    # Documentation
└── examples/                # Example programs
```

---

## Adding New Language Features

### Step 1: Add Tokens (Lexer)

**File**: `lexer.h`, `lexer.cpp`

```cpp
// 1. Add token type to enum
enum class TokenType {
    ...,
    MYNEWKEYWORD
};

// 2. Add keyword recognition in nextToken()
if (upper == "MYNEWKEYWORD") 
    return {TokenType::MYNEWKEYWORD, s, 0.0, tokenLine};
```

### Step 2: Extend AST (ast.h)

```cpp
// 1. Add to appropriate enum
enum class ExprKind {
    ...,
    MyNewExpr
};

// 2. Create structure
struct MyNewExpr {
    string data;
    ExprPtr child;
};

// 3. Add to variant
variant<..., MyNewExpr> data;

// 4. Add constructor
Expr(ExprKind k, Type t, MyNewExpr m) : kind(k), type(t), data(std::move(m)) {}
```

### Step 3: Implement Parsing (parser.cpp)

```cpp
// Add parsing method
ExprPtr Parser::parseMyNewFeature() {
    expect(TokenType::MYNEWKEYWORD);
    string data = expect(TokenType::ID).val;
    auto child = parseExpr();
    
    return make_unique<Expr>(ExprKind::MyNewExpr, Type::Float,
                           MyNewExpr{data, move(child)});
}

// Call from appropriate place (parsePrimary, parseStmt, parseDecl)
if (tok.type == TokenType::MYNEWKEYWORD) {
    return parseMyNewFeature();
}
```

### Step 4: Update AST Printer (ast_printer.cpp)

```cpp
case ExprKind::MyNewExpr: {
    const MyNewExpr& mne = get<MyNewExpr>(expr.data);
    out << "MYNEW(" << mne.data << ", ";
    printExpr(*mne.child);
    out << ")";
    break;
}
```

### Step 5: Update Semantic Analyzer (semantic.cpp)

```cpp
// In inferExprType()
case ExprKind::MyNewExpr: {
    const MyNewExpr& mne = get<MyNewExpr>(expr.data);
    return inferExprType(*mne.child, symbols);
}

// In analyzeExpr()
case ExprKind::MyNewExpr: {
    MyNewExpr& mne = get<MyNewExpr>(expr.data);
    analyzeExpr(*mne.child, symbols);
    break;
}
```

### Step 6: Generate Code (codegen.h)

```cpp
// In load() or genStmt()
case ExprKind::MyNewExpr: {
    const MyNewExpr& mne = get<MyNewExpr>(e.data);
    // Generate JVM bytecode
    load(*mne.child, varIdx);
    // ... more bytecode ...
    break;
}
```

### Step 7: Test

```basic
' tests/test_mynew.bas
LET result = MYNEWKEYWORD(data)
PRINT result
```

```bash
./jvmbasic --dump-ast < tests/test_mynew.bas  # Test parsing
./jvmbasic < tests/test_mynew.bas && java BasicProgram  # Test execution
```

---

## Debugging Tools

### AST Dumping

```bash
./jvmbasic --dump-ast < program.bas
```

Shows complete AST structure with types.

### Syntax-Only Checking

```bash
./jvmbasic --check-only < program.bas
```

Parses and type-checks without generating code.

### Bytecode Inspection

```bash
./jvmbasic < program.bas
javap -v -c -private BasicProgram
```

Shows generated JVM bytecode.

### GDB Debugging

```bash
gdb ./jvmbasic
(gdb) run < program.bas
(gdb) bt  # Backtrace on crash
```

---

## Common Patterns

### Recursive Descent Parsing

```cpp
ExprPtr Parser::parseAdd() {
    auto left = parseMul();  // Higher precedence
    while (tok.type == TokenType::PLUS || tok.type == TokenType::MINUS) {
        Op op = (tok.type == TokenType::PLUS) ? Op::Add : Op::Sub;
        next();
        auto right = parseMul();
        left = make_unique<Expr>(ExprKind::Bin, Type::Float,
                               BinOp{op, move(left), move(right)});
    }
    return left;
}
```

### Symbol Table Scoping

```cpp
// Create new scope
SymbolTable childScope(&parentScope);

// Define in scope
childScope.define("x", Type::Int);

// Lookup with parent fallback
Type t = childScope.getType("x");  // Checks parent if not found
```

### Move Semantics

```cpp
// Correct - use std::move for unique_ptr
vector<StmtPtr> body;
body.push_back(move(stmt));

// Correct - move in return
return make_unique<Expr>(...);

// Wrong - copy attempt
body.push_back(stmt);  // Error! Can't copy unique_ptr
```

---

## Phase Implementation Checklist

When adding a new phase (like Phase 7 OOP):

### Planning
- [ ] Design document (syntax, semantics, examples)
- [ ] Implementation plan (steps, timeline)
- [ ] Test cases designed

### Implementation
- [ ] Add tokens to lexer
- [ ] Extend AST with new types
- [ ] Implement parsing
- [ ] Update AST printer
- [ ] Update semantic analyzer
- [ ] Generate bytecode
- [ ] Create tests
- [ ] Update documentation

### Verification
- [ ] All new tests pass
- [ ] No regression (existing tests still pass)
- [ ] AST dumps correctly
- [ ] Bytecode valid (javap -v)
- [ ] Code runs correctly

---

## Testing Strategy

### Unit Testing (Per Component)

```bash
# Test lexer
echo 'CLASS Point\n  PUBLIC x\nEND CLASS' | ./jvmbasic --dump-ast

# Test parser  
./jvmbasic --dump-ast < tests/test_class_basic.bas

# Test semantic
./jvmbasic --check-only < tests/test_class_basic.bas

# Test codegen
./jvmbasic < tests/test_class_basic.bas && java BasicProgram
```

### Integration Testing

```bash
# Run full test suite
./test_runner.sh

# Run specific category
for test in tests/test_class*.bas; do
    ./jvmbasic < "$test" && java BasicProgram
done
```

---

## Code Style Guidelines

### Naming Conventions

- **Classes**: PascalCase (`ClassFile`, `SemanticAnalyzer`)
- **Functions**: camelCase (`parseExpr`, `nextToken`)
- **Variables**: camelCase (`className`, `varIdx`)
- **Constants**: UPPER_CASE (`MAX_STACK`, `ACC_PUBLIC`)
- **Types**: PascalCase (`ExprKind`, `Type`)

### File Organization

- **Headers**: Declarations only (class, struct, enum)
- **Implementation**: Definitions (.cpp files)
- **Header-only**: Acceptable for templates (codegen.h)

### Error Handling

```cpp
void error(const string& msg) {
    throw runtime_error("Line " + to_string(line) + ": " + msg);
}
```

Always include line numbers in error messages.

---

## Performance Considerations

### Unique Pointers

```cpp
using ExprPtr = unique_ptr<Expr>;
```

- Use move semantics to transfer ownership
- No copying (deleted copy constructor)
- Automatic memory management

### Constant Pool

- No deduplication (simplicity over optimization)
- Each literal/string/class reference adds entry
- Acceptable for compiler size

### Bytecode Generation

- Direct bytecode emission (no intermediate representation)
- Single-pass code generation
- Stack-based JVM instructions

---

## Future Enhancements

### Phase 8+
- Separate .class files for each CLASS
- Static methods and fields
- Inheritance (INHERITS keyword)
- Method overriding (OVERRIDES keyword)
- Interfaces
- Generic types

### Build System
- CMake migration
- Cross-platform builds
- Install target

### Tools
- REPL (read-eval-print loop)
- Debugger integration
- IDE support (LSP server)

---

## Contributing

When extending the compiler:

1. **Read existing code** - Understand the pattern
2. **Follow conventions** - Match existing style
3. **Add tests** - Test new features
4. **Update docs** - Keep docs synchronized
5. **No regressions** - Verify existing tests still pass

---

## Common Issues

### Issue: unique_ptr Copy Error

```
error: use of deleted function 'unique_ptr::unique_ptr(const unique_ptr&)'
```

**Solution**: Use `std::move()`:
```cpp
vector.push_back(move(ptr));
return make_unique<Expr>(...);
```

### Issue: Variant Access

```
error: 'get' is not a member of 'std'
```

**Solution**: Use `std::get` or add `using std::get`:
```cpp
const MyType& mt = std::get<MyType>(expr.data);
```

### Issue: Forward Declaration

```
error: incomplete type
```

**Solution**: Include proper headers or use forward declarations.

---

## Resources

- **JVM Specification**: https://docs.oracle.com/javase/specs/jvms/se8/html/
- **C++ Reference**: https://en.cppreference.com/
- **Compiler Design**: Dragon Book (Aho, Sethi, Ullman)

---

**See Also**:
- `CODE_GUIDE.md` - Detailed code walkthrough
- `AST_GUIDE.md` - AST structure reference
- `LEXER_GUIDE.md` - Lexer implementation details


