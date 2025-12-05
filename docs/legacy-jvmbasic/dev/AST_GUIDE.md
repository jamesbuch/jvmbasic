# JVM BASIC - Abstract Syntax Tree (AST) Guide

**Purpose**: Understanding the AST structure and how to work with it  
**Audience**: Developers extending the compiler

---

## Overview

The Abstract Syntax Tree (AST) is the internal representation of a BASIC program after parsing. It's a tree structure where each node represents a language construct (statement, expression, etc.).

**Location**: `ast.h` and `ast.cpp`

---

## Core Structures

### Type System

```cpp
enum class Type {
    Int,          // 32-bit integer
    Float,        // 32-bit float
    String,       // String reference
    Bool,         // Boolean
    IntArray,     // Integer array
    FloatArray,   // Float array
    StringArray,  // String array
    BoolArray,    // Boolean array
};
```

**Key Point**: Arrays are distinct types from their element types.

---

## Statements (Stmt)

### Statement Kinds

```cpp
enum class StmtKind {
    Print,      // PRINT statement
    Let,        // LET assignment
    Input,      // INPUT statement
    Dim,        // DIM array declaration
    If,         // IF-THEN-ELSE
    For,        // FOR loop
    While,      // WHILE loop
    DoWhile,    // DO-WHILE loop
    Call,       // SUB call
    Return,     // RETURN from function
    FunctionDecl,  // Function definition
    SubDecl,    // SUB definition
};
```

### Statement Structure

```cpp
struct Stmt {
    StmtKind kind;
    int line;
    
    // Union-like variant data
    PrintStmt print;
    LetStmt let;
    InputStmt input;
    DimStmt dim;
    IfStmt ifStmt;
    ForStmt forStmt;
    WhileStmt whileStmt;
    DoWhileStmt doWhileStmt;
    CallStmt call;
    ReturnStmt returnStmt;
    FunctionDecl functionDecl;
    SubDecl subDecl;
};
```

**Note**: Only one variant is active based on `kind`.

### Key Statement Types

#### Print Statement
```cpp
struct PrintStmt {
    vector<Expr> exprs;      // Expressions to print
    vector<bool> semicolons; // Separator flags
};
```

#### Let Statement (Assignment)
```cpp
struct LetStmt {
    string var;              // Variable name
    unique_ptr<Expr> index;  // Array index (if array)
    unique_ptr<Expr> value;  // Right-hand side
};
```

#### If Statement
```cpp
struct IfStmt {
    unique_ptr<Expr> condition;
    vector<unique_ptr<Stmt>> thenBlock;
    vector<unique_ptr<Stmt>> elseBlock;
    vector<pair<unique_ptr<Expr>, vector<unique_ptr<Stmt>>>> elseifs;
};
```

#### Function Declaration
```cpp
struct FunctionDecl {
    string name;
    vector<Param> params;    // Parameters with types
    vector<unique_ptr<Stmt>> body;
    Type returnType;
};

struct Param {
    string name;
    Type type;
};
```

---

## Expressions (Expr)

### Expression Kinds

```cpp
enum class ExprKind {
    Number,     // Numeric literal
    String,     // String literal
    Bool,       // Boolean literal
    Var,        // Variable reference
    Binary,     // Binary operation
    Unary,      // Unary operation
    Call,       // Function call
    ArrayAccess // Array element access
};
```

### Expression Structure

```cpp
struct Expr {
    ExprKind kind;
    Type type;      // Inferred/resolved type
    int line;
    
    // Variant data
    double num;
    string str;
    bool boolean;
    string var;
    BinaryExpr binary;
    UnaryExpr unary;
    CallExpr call;
    ArrayAccessExpr arrayAccess;
};
```

### Key Expression Types

#### Binary Expression
```cpp
enum class BinOp {
    Add, Sub, Mul, Div, Mod,
    Lt, Gt, Le, Ge, Eq, Ne
};

struct BinaryExpr {
    BinOp op;
    unique_ptr<Expr> left;
    unique_ptr<Expr> right;
};
```

#### Unary Expression
```cpp
enum class UnaryOp {
    Neg,   // Arithmetic negation (-)
    Not    // Logical not
};

struct UnaryExpr {
    UnaryOp op;
    unique_ptr<Expr> operand;
};
```

#### Function Call
```cpp
struct CallExpr {
    string func;
    vector<unique_ptr<Expr>> args;
};
```

---

## AST Construction

### In the Parser

The parser constructs AST nodes as it recognizes language constructs:

```cpp
unique_ptr<Stmt> Parser::parseLetStmt() {
    consume(TokenType::LET);
    string varName = current.val;
    consume(TokenType::ID);
    
    // Check for array index
    unique_ptr<Expr> index = nullptr;
    if (current.type == TokenType::LPAREN) {
        consume(TokenType::LPAREN);
        index = parseExpr();
        consume(TokenType::RPAREN);
    }
    
    consume(TokenType::ASSIGN);
    auto value = parseExpr();
    
    return make_unique<Stmt>(
        StmtKind::Let,
        LetStmt{varName, move(index), move(value)}
    );
}
```

**Key Points**:
- Use `make_unique` for owned pointers
- Use `move()` for transferring ownership
- Capture line numbers for error reporting

---

## AST Traversal

### Pattern: Visitor

The semantic analyzer and code generator traverse the AST:

```cpp
void processStmt(const Stmt& stmt) {
    switch (stmt.kind) {
        case StmtKind::Print:
            processPrint(stmt.print);
            break;
        case StmtKind::Let:
            processLet(stmt.let);
            break;
        // ... other cases ...
    }
}
```

### Recursive Processing

```cpp
void processExpr(const Expr& expr) {
    switch (expr.kind) {
        case ExprKind::Binary:
            processExpr(*expr.binary.left);
            processExpr(*expr.binary.right);
            // Process operation
            break;
        case ExprKind::Call:
            for (const auto& arg : expr.call.args) {
                processExpr(*arg);
            }
            break;
    }
}
```

---

## AST Dumping (Pretty Print)

### Location
`ast_printer.h` and `ast_printer.cpp`

### Usage
```cpp
#include "ast_printer.h"

ASTPrinter printer;
for (const auto& stmt : program) {
    printer.printStmt(*stmt);
}
```

### Command Line
```bash
./jvmbasic-new --dump-ast < program.bas
```

### Output Format
```
FunctionDecl: factorial
  Params:
    n: Float
  Body:
    If
      Condition: (n <= 1.0)
      Then:
        Return: 1.0
      Else:
        Return: (n * factorial(n - 1.0))
  ReturnType: Float
```

### Implementation

The AST printer uses recursive traversal with indentation:

```cpp
void ASTPrinter::printStmt(const Stmt& stmt, int indent) {
    string prefix(indent, ' ');
    
    switch (stmt.kind) {
        case StmtKind::If:
            cout << prefix << "If\n";
            cout << prefix << "  Condition: ";
            printExpr(*stmt.ifStmt.condition);
            cout << "\n";
            
            cout << prefix << "  Then:\n";
            for (const auto& s : stmt.ifStmt.thenBlock) {
                printStmt(*s, indent + 4);
            }
            break;
    }
}
```

---

## Extending the AST

### Adding a New Statement Type

**Step 1**: Add to `StmtKind` enum:
```cpp
enum class StmtKind {
    // ... existing ...
    MyNewStmt,  // NEW
};
```

**Step 2**: Define statement structure:
```cpp
struct MyNewStmt {
    string data;
    unique_ptr<Expr> expr;
};
```

**Step 3**: Add to `Stmt` union:
```cpp
struct Stmt {
    // ... existing ...
    MyNewStmt myNewStmt;
};
```

**Step 4**: Update parser:
```cpp
unique_ptr<Stmt> Parser::parseMyNewStmt() {
    // Parse tokens
    return make_unique<Stmt>(
        StmtKind::MyNewStmt,
        MyNewStmt{...}
    );
}
```

**Step 5**: Update code generator:
```cpp
void generateStmt(const Stmt& stmt) {
    case StmtKind::MyNewStmt:
        // Generate bytecode
        break;
}
```

**Step 6**: Update AST printer:
```cpp
void ASTPrinter::printStmt(const Stmt& stmt, int indent) {
    case StmtKind::MyNewStmt:
        // Print representation
        break;
}
```

---

## Common AST Patterns

### Pattern 1: Building Expressions
```cpp
// Create: x + 5
auto left = make_unique<Expr>(
    ExprKind::Var, Type::Float, VarExpr{"x"}
);
auto right = make_unique<Expr>(
    ExprKind::Number, Type::Float, 5.0
);
auto expr = make_unique<Expr>(
    ExprKind::Binary, Type::Float,
    BinaryExpr{BinOp::Add, move(left), move(right)}
);
```

### Pattern 2: Traversing with Modification
```cpp
void fixTypes(Stmt& stmt) {
    // Modify in place
    if (stmt.kind == StmtKind::Let) {
        fixTypes(*stmt.let.value);
        // Update type
        stmt.let.value->type = inferType(*stmt.let.value);
    }
}
```

### Pattern 3: Collecting Information
```cpp
set<string> collectVariables(const Expr& expr) {
    set<string> vars;
    
    if (expr.kind == ExprKind::Var) {
        vars.insert(expr.var);
    } else if (expr.kind == ExprKind::Binary) {
        auto left = collectVariables(*expr.binary.left);
        auto right = collectVariables(*expr.binary.right);
        vars.insert(left.begin(), left.end());
        vars.insert(right.begin(), right.end());
    }
    
    return vars;
}
```

---

## Memory Management

### Smart Pointers
- Use `unique_ptr<T>` for owned children
- Use `move()` when transferring ownership
- Raw pointers (`T*`) only for non-owning references

### Example
```cpp
// CORRECT
unique_ptr<Expr> expr = parseExpr();
stmt.value = move(expr);  // Transfer ownership

// WRONG
Expr* expr = parseExpr().get();  // Dangling pointer!
```

---

## Debugging AST Issues

### 1. Use AST Dump
```bash
./jvmbasic-new --dump-ast < program.bas > ast.txt
```

### 2. Check Line Numbers
Every AST node has a `line` field for error reporting:
```cpp
if (error) {
    throw runtime_error("Line " + to_string(expr.line) + 
                       ": Type mismatch");
}
```

### 3. Verify Types
After semantic analysis, all expressions should have types:
```cpp
void verifyTypes(const Expr& expr) {
    assert(expr.type != Type::Unknown);
    // Check children...
}
```

### 4. Pretty Print During Development
```cpp
ASTPrinter printer;
printer.printExpr(expr);  // See what you built
```

---

## Best Practices

### 1. **Immutable After Construction**
Once built, AST nodes should be read-only (except during semantic analysis).

### 2. **Line Numbers Always**
Every node needs a line number for error reporting.

### 3. **Type Inference Separation**
Keep type inference logic in semantic analyzer, not parser.

### 4. **Clear Ownership**
Use smart pointers to make ownership explicit.

### 5. **Consistent Naming**
- Structs: PascalCase (e.g., `BinaryExpr`)
- Fields: camelCase (e.g., `returnType`)
- Enums: PascalCase values (e.g., `StmtKind::Print`)

---

## Summary

The AST is the central data structure of the compiler:
- **Parser** builds it
- **Semantic analyzer** annotates it (types)
- **Code generator** walks it
- **AST printer** visualizes it

Understanding the AST is key to extending JVM BASIC!

---

**Next**: Read PARSER_GUIDE.md to understand how the AST is constructed.

