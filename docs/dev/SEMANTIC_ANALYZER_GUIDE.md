# Semantic Analyzer Guide

**Component**: `semantic.cpp` / `semantic.h`  
**Phase**: All phases (1-9)  
**Purpose**: Type inference, type checking, and semantic validation

---

## Overview

The Semantic Analyzer is a **multi-pass type inference engine** that:
1. Analyzes expressions and statements for type correctness
2. Infers types for variables, function parameters, and return values
3. Validates semantic rules (variable existence, type compatibility, etc.)
4. Annotates the AST with resolved types for code generation

**Key Feature**: The analyzer supports **bidirectional type inference**, allowing untyped code like classic BASIC while also supporting modern type annotations.

---

## Architecture

### Class: `SemanticAnalyzer`

```cpp
class SemanticAnalyzer {
private:
    map<string, FunctionDecl*> functions;      // User-defined functions
    map<string, SubDecl*> subs;                // User-defined subroutines
    map<string, TypeDefDecl*> userTypes;       // TYPE declarations
    map<string, ClassDecl*> userClasses;       // CLASS declarations
    vector<string> semanticErrors;             // Accumulated errors
    
    // Analysis methods
    void analyzeExpr(Expr& expr, SymbolTable& symbols);
    void analyzeStmt(Stmt& stmt, SymbolTable& symbols);
    void inferFunctionTypes(FunctionDecl& fd);
    void inferReturnType(FunctionDecl& fd);
    
public:
    void analyze(Program& prog);
    bool hasErrors() const;
    void printErrors() const;
};
```

### Symbol Table

```cpp
struct SymbolTable {
    map<string, Type> variables;           // Variable name -> type
    map<string, string> variableTypeNames; // For user-defined types
    SymbolTable* parent;                   // For nested scopes
};
```

---

## Analysis Phases

### Phase 1: Declaration Collection
**What**: Collects all functions, subs, types, and classes  
**Why**: Enables forward references and mutual recursion

```cpp
// Collect all declarations first
for (auto& decl : prog.declarations) {
    if (decl->kind == DeclKind::Function) {
        FunctionDecl& fd = get<FunctionDecl>(decl->data);
        functions[fd.name] = &fd;
    }
    // ... similar for SUB, TYPE, CLASS
}
```

### Phase 2: Type Inference (Multiple Passes)
**What**: Infers types for untyped parameters and arrays  
**Why**: Supports classic BASIC (no type annotations)

```cpp
// Infer function parameter types
for (auto& [name, func] : functions) {
    inferFunctionTypes(*func);  // Analyze function body to infer param types
    inferReturnType(*func);     // Infer return type from RETURN statements
}
```

**Algorithm**: Iterative inference with dependency resolution
- Pass 1: Analyze function bodies, infer from usage
- Pass 2: Propagate inferred types to callers
- Pass 3+: Continue until all types stabilize

### Phase 3: Full Analysis
**What**: Validates all expressions and statements  
**Why**: Catches type errors, undefined variables, invalid operations

```cpp
// Analyze each declaration
for (auto& decl : prog.declarations) {
    analyzeDecl(*decl);
}

// Analyze main program
for (auto& stmt : prog.statements) {
    analyzeStmt(*stmt, globalSymbols);
}
```

---

## Type Inference Rules

### Variables
**Rule**: Type determined from first assignment or DIM statement

```basic
Dim x As Integer = 10      ' Type: Integer (explicit)
Let y = 5.0                ' Type: Float (inferred from literal)
Let z = "hello"            ' Type: String (inferred from literal)
Let w = x + 1              ' Type: Integer (inferred from expression)
```

### Function Parameters
**Rule**: Type inferred from how parameter is used in function body

```basic
Function Add(a, b)         ' Types inferred from usage
    Return a + b           ' If called with Float args, becomes (Float,Float)->Float
End Function
```

**Inference Process**:
1. Analyze RETURN expressions
2. Analyze parameter usage (operators, function calls)
3. Propagate constraints
4. Resolve to most specific compatible type

### Return Types
**Rule**: Inferred from RETURN statements

```basic
Function GetName()
    Return "Alice"         ' Inferred as String
End Function
```

### Arrays
**Rule**: Element type inferred from initialization value

```basic
Dim numbers(10) = 0.0      ' FloatArray (init value is Float)
Dim names(5) = ""          ' StringArray (init value is String)
```

---

## Expression Type Rules

### Binary Operations

| Left Type | Operator | Right Type | Result Type |
|-----------|----------|------------|-------------|
| Int | +, -, *, / | Int | Int (if both are Int)|
| Int | +, -, *, / | Float | Float (widening) |
| Float | +, -, *, / | Any numeric | Float |
| String | + | String | String (concatenation) |
| Int | <<, >>, &, \|, ^ | Int | Int (bitwise) |
| Bool | AND, OR, XOR | Bool | Bool (logical) |

### Type Widening
**Rule**: Integer widens to Float in mixed expressions

```basic
Dim x As Integer = 5
Dim y As Single = 3.14
Dim z = x + y              ' Result: Float (5.0 + 3.14)
```

### Comparison Operations
**Rule**: All comparisons return Boolean

```basic
Dim result = (5 > 3)       ' Type: Bool
Dim check = ("a" == "b")   ' Type: Bool (string comparison)
```

---

## Statement Validation

### Variable Assignment
**Checks**:
- Variable must be declared (DIM or first LET)
- Type compatibility between LHS and RHS
- Array bounds (if applicable)

```basic
Dim x As Integer = 10
x = 5.0                    ' OK: Float widens to Int storage slot
x = "hello"                ' ERROR: Cannot assign String to Integer variable
```

### Function Calls
**Checks**:
- Function exists (built-in or user-defined)
- Argument count matches parameters
- Argument types compatible with parameters

```basic
Function Add(a As Integer, b As Integer) As Integer
    Return a + b
End Function

Dim x = Add(5, 10)         ' OK
Dim y = Add(5)             ' ERROR: Wrong number of arguments
```

### Member Access
**Checks**:
- Object/struct variable exists
- Member exists in type definition
- Types match for assignment

```basic
Type Person
    name As String
    age As Single
End Type

Dim p As Person
p.name = "Alice"           ' OK
p.height = 5.9             ' ERROR: No such field 'height'
```

---

## Special Cases

### Phase 6: User-Defined Types (Structs)
**Analysis**:
- Track TYPE declarations
- Validate field access
- Ensure struct variables are properly initialized

### Phase 7: Object-Oriented Programming
**Analysis**:
- Track CLASS declarations with fields and methods
- Validate constructors (SUB New)
- Check PUBLIC/PRIVATE access modifiers
- Validate ME keyword usage (only inside methods)

### Phase 8: Collections & Logical Operators
**Analysis**:
- Validate collection IDs (IntList, StringList, Map, etc.)
- Type check logical expressions (AND, OR, XOR, NOT)
- Ensure boolean operands for logical operators

### Phase 9: Modern Syntax & Namespaces
**Analysis**:
- Validate namespace calls (Console, Math, File, Http, Json, Xml, Db)
- Check method names exist in namespace
- Type check modern syntax (Dim x As Integer)
- Validate expression statements
- Type check bitwise operators (&, |, ^, <<, >>)

---

## Error Reporting

### Error Types
1. **Undefined variable**: Variable used before declaration
2. **Unknown function**: Function call to undefined function
3. **Type mismatch**: Incompatible types in operation
4. **Wrong argument count**: Function call with incorrect number of args
5. **Unknown field**: Member access on non-existent field
6. **Invalid operation**: Operator applied to incompatible types

### Error Collection
**Strategy**: Collect all errors and report at end (don't stop at first error)

```cpp
void SemanticAnalyzer::error(const string& msg) {
    semanticErrors.push_back(msg);
}

bool SemanticAnalyzer::hasErrors() const {
    return !semanticErrors.empty();
}
```

---

## Current Capabilities

### ✅ Fully Implemented
- Type inference for variables, functions, arrays
- Type checking for all expressions
- Validation for all statements
- User-defined types (TYPE and CLASS)
- Namespace call validation
- Logical and bitwise operator checking
- Expression statement validation (Phase 9)

### 🔄 Partial Implementation
- Generic types (future: List(Of T))
- Method overloading (single signature per name)
- Operator overloading (Decimal, BigInt)

### ❌ Not Implemented (Wishlist)
- **Static Analysis Mode** (--analyze flag)
  - Dead code detection
  - Unused variable warnings
  - Potential null reference warnings
  - Complexity metrics
- **Flow Analysis**
  - Definite assignment checking
  - Unreachable code detection
  - Infinite loop detection
- **Advanced Type Inference**
  - Generic type parameters
  - Variance annotations
  - Type constraints

---

## Usage Examples

### Basic Analysis
```bash
./jvmbasic < program.bas           # Compile (includes semantic analysis)
./jvmbasic --check-only < prog.bas # Parse and analyze only, no codegen
```

### AST Dump (with types)
```bash
./jvmbasic --dump-ast < program.bas
# Shows: [Int] expr + [Float] expr -> type annotations from semantic analysis
```

### Error Messages
```bash
$ ./jvmbasic < bad_program.bas
Semantic errors:
  Unknown function: MissingFunc
  Type mismatch in binary operation: String vs Float
  Variable 'undeclared' used before definition
```

---

## Implementation Details

### How Type Inference Works

**Example**: Infer parameter types from usage
```basic
Function Multiply(a, b)
    Return a * b
End Function

Let result = Multiply(5.0, 3.0)
```

**Analysis Process**:
1. **Parse**: Creates FunctionDecl with parameters `a` and `b` (type unknown)
2. **Inference**:
   - Analyze `a * b` expression
   - See multiplication operator - could be Int*Int or Float*Float
   - Check call site: `Multiply(5.0, 3.0)` - both Float literals
   - Infer: `a:Float`, `b:Float`, return:Float
3. **Validation**: Check all uses of Multiply for type consistency

### Symbol Table Scoping

**Hierarchy**:
```
Global Scope
├── Functions (each has own scope)
│   ├── Parameters
│   └── Local variables
├── Subs (each has own scope)
└── Classes
    └── Methods (each has own scope)
        ├── Parameters
        ├── Local variables
        └── Fields (via ME)
```

**Lookup Order**:
1. Current scope (function/method locals)
2. Parent scope (global variables)
3. Builtin functions

### Type Compatibility Matrix

| Source → Target | Int | Float | String | Bool |
|-----------------|-----|-------|--------|------|
| **Int** | ✅ | ✅ | ❌ | ❌ |
| **Float** | ⚠️ | ✅ | ❌ | ❌ |
| **String** | ❌ | ❌ | ✅ | ❌ |
| **Bool** | ✅ | ✅ | ❌ | ✅ |

✅ = Allowed  
⚠️ = Allowed with warning (precision loss)  
❌ = Type error

---

## Future: Static Analyzer Mode

### Proposed `--analyze` Flag

**Purpose**: Run as linter/static analyzer without code generation

**Features**:
```bash
./jvmbasic --analyze < program.bas

# Output:
Analysis Report for program.bas:
✓ No type errors
⚠ Warnings:
  Line 42: Variable 'temp' declared but never used
  Line 58: Function 'Helper' defined but never called
  Line 103: Infinite loop detected (no exit condition)
ℹ Info:
  Functions: 12
  Variables: 45
  Cyclomatic Complexity: 18 (consider refactoring)
```

**Implementation Plan** (Phase 10):
1. Add `--analyze` flag to main.cpp
2. Extend SemanticAnalyzer with additional checks:
   - Unused variable detection
   - Dead code detection
   - Complexity metrics
3. Output analysis report instead of bytecode
4. Integration with IDEs (LSP protocol)

### Advanced Checks (Wishlist)

**Dead Code Detection**:
```basic
Function Test()
    Return 1
    Print "This is dead code"  ' WARNING: Unreachable
End Function
```

**Unused Variables**:
```basic
Dim unusedVar As Integer = 42  ' WARNING: Variable declared but never used
```

**Type Safety Warnings**:
```basic
Dim x = 5 / 2                  ' WARNING: Integer division, result truncated
```

**Null Safety** (when references added):
```basic
Dim obj As Object = Nothing
obj.Method()                   ' WARNING: Possible null reference
```

---

## Code Structure

### Main Entry Point
```cpp
void SemanticAnalyzer::analyze(Program& prog) {
    // 1. Collect declarations
    collectDeclarations(prog);
    
    // 2. Infer types (multiple passes)
    inferTypes();
    
    // 3. Validate everything
    validateProgram(prog);
    
    // 4. Report errors
    if (hasErrors()) {
        printErrors();
        throw runtime_error("Semantic analysis failed");
    }
}
```

### Expression Analysis
```cpp
void SemanticAnalyzer::analyzeExpr(Expr& expr, SymbolTable& symbols) {
    switch (expr.kind) {
        case ExprKind::Num:
            expr.type = Type::Float;
            break;
        
        case ExprKind::Var:
            VarRef& vr = get<VarRef>(expr.data);
            if (!symbols.has(vr.name)) {
                error("Undefined variable: " + vr.name);
            }
            expr.type = symbols.get(vr.name);
            break;
        
        case ExprKind::Bin:
            BinOp& bo = get<BinOp>(expr.data);
            analyzeExpr(*bo.left, symbols);
            analyzeExpr(*bo.right, symbols);
            expr.type = inferBinOpType(bo.left->type, bo.right->type, bo.op);
            break;
        
        // ... other cases
    }
}
```

### Statement Analysis
```cpp
void SemanticAnalyzer::analyzeStmt(Stmt& stmt, SymbolTable& symbols) {
    switch (stmt.kind) {
        case StmtKind::Let:
            // Analyze RHS expression
            analyzeExpr(*ls.expr, symbols);
            // Register/validate variable type
            symbols.set(ls.var, ls.expr->type);
            break;
        
        case StmtKind::If:
            // Condition must be boolean
            analyzeExpr(*ifs.cond, symbols);
            if (ifs.cond->type != Type::Bool) {
                error("IF condition must be boolean");
            }
            break;
        
        // ... other cases
    }
}
```

---

## Phase-Specific Features

### Phase 6: User-Defined Types
**Analysis**:
- Validate TYPE declarations
- Check field access: `struct.field`
- Ensure all fields are initialized

**Code**:
```cpp
// Member access: struct.field
if (vr.name.find('.') != string::npos) {
    // Split into var.field
    auto [varName, fieldName] = splitMemberAccess(vr.name);
    Type structType = symbols.get(varName);
    expr.type = getFieldType(structType, fieldName);
}
```

### Phase 7: Object-Oriented
**Analysis**:
- Validate CLASS declarations
- Check constructor signatures (SUB New)
- Validate method calls: `object.method(args)`
- Enforce PUBLIC/PRIVATE access
- Validate ME keyword (only in instance methods)

**Code**:
```cpp
// Method call: object.method(args)
case ExprKind::MethodCall:
    analyzeExpr(*mce.object, symbols);
    string className = mce.object->typeName;
    ClassDecl* cls = userClasses[className];
    MethodDecl* method = findMethod(cls, mce.methodName);
    validateArguments(method->params, mce.args);
    expr.type = method->returnType;
    break;
```

### Phase 8: Logical Operators
**Analysis**:
- Validate AND, OR, XOR, NOT operands are boolean
- Ensure proper short-circuit semantics (if implemented)

**Code**:
```cpp
case ExprKind::Logical:
    analyzeExpr(*le.left, symbols);
    analyzeExpr(*le.right, symbols);
    if (le.left->type != Type::Bool || le.right->type != Type::Bool) {
        error("Logical operators require boolean operands");
    }
    expr.type = Type::Bool;
    break;
```

### Phase 9: Modern Syntax & Namespaces
**Analysis**:
- Validate namespace calls (Console.WriteLine, Math.Sin, etc.)
- Check method exists in namespace
- Type check arguments against namespace method signature
- Expression statements (validate and mark as discarded)
- Bitwise operators (&, |, ^) - ensure integer operands

**Code**:
```cpp
case ExprKind::NamespaceCall:
    // Lookup namespace method in builtin registry
    string methodKey = nce.namespaceName + "_" + nce.methodName;
    if (!hasNamespaceMethod(methodKey)) {
        error("Unknown namespace method: " + methodKey);
    }
    // Type check arguments
    validateNamespaceArgs(nce);
    expr.type = getNamespaceReturnType(nce);
    break;

case StmtKind::ExprStmt:
    // Validate expression, result will be discarded
    analyzeExpr(*es.expr, symbols);
    break;
```

---

## Error Examples

### Type Mismatch
```basic
Dim x As Integer = 10
x = "hello"

# Error: Type mismatch in assignment: cannot assign String to Integer variable
```

### Undefined Variable
```basic
Print undefinedVar

# Error: Variable 'undefinedVar' used before definition
```

### Wrong Argument Count
```basic
Function Add(a As Integer, b As Integer) As Integer
    Return a + b
End Function

Dim x = Add(5)

# Error: Function 'Add' expects 2 arguments, got 1
```

### Unknown Function
```basic
Dim x = MissingFunc(42)

# Error: Unknown function: MissingFunc
```

---

## Integration with Compiler Pipeline

```
Input Program
     ↓
Lexer (tokenization)
     ↓
Parser (AST construction - types unknown)
     ↓
Semantic Analyzer (type inference + validation) ← YOU ARE HERE
     ↓
Code Generator (bytecode emission)
     ↓
JVM Class File
```

**Input**: AST with incomplete type information  
**Output**: AST with all types resolved and validated  
**Side Effect**: Error list (if validation fails)

---

## Testing

### Test Coverage
- ✅ Variable type inference
- ✅ Function parameter inference
- ✅ Array type inference
- ✅ User-defined type validation
- ✅ Class/OOP validation
- ✅ Namespace call validation
- ✅ Expression statement validation
- ✅ Bitwise operator validation

### Test Files
- `tests/test_type_inference.bas` - Variable and function type inference
- `tests/test_struct_*.bas` - User-defined types
- `tests/test_class_*.bas` - OOP features
- `tests/test_namespace_syntax.bas` - Namespace calls
- `tests/test_bitwise_complete.bas` - Bitwise operators

---

## Performance Notes

**Complexity**: O(n * p) where n = AST nodes, p = inference passes (typically 2-3)  
**Memory**: O(n) for symbol tables and type maps  
**Optimizations**:
- Single-pass analysis for explicitly typed code
- Caching of inferred types
- Early termination when types stabilize

---

## Extending the Analyzer

### Adding New Type Rules
1. Update `inferBinOpType()` with new operator/type combinations
2. Add validation in `analyzeExpr()` for new expression kinds
3. Test with edge cases

### Adding New Semantic Checks
```cpp
void SemanticAnalyzer::checkUnusedVariables(SymbolTable& symbols) {
    for (auto& [name, type] : symbols.variables) {
        if (!symbols.isUsed(name)) {
            warn("Variable '" + name + "' declared but never used");
        }
    }
}
```

---

## Wishlist: Enhanced Semantic Analysis (Phase 10+)

### Static Analyzer Mode
- **Unused Code Detection**: Functions, variables, imports never used
- **Complexity Metrics**: Cyclomatic complexity, nesting depth
- **Code Smells**: Long functions, too many parameters, deep nesting
- **Security Warnings**: SQL injection risks, file path traversal

### Flow Analysis
- **Definite Assignment**: Ensure variables initialized before use
- **Null Safety**: Track nullable vs non-nullable references
- **Resource Leaks**: File handles, database connections not closed

### IDE Integration
- **Language Server Protocol** (LSP)
- **Real-time type inference**
- **Code completion** based on inferred types
- **Inline error markers**

---

## Summary

The Semantic Analyzer is a **powerful multi-pass type inference and validation engine** that enables:
1. **Classic BASIC**: Untyped code with automatic type inference
2. **Modern VB**: Explicit type annotations with validation
3. **Hybrid**: Mix of typed and untyped code

**Quality**: Comprehensive error checking catches issues before code generation  
**Flexibility**: Supports 9 phases of language evolution  
**Future**: Foundation for static analysis and IDE tooling

**Status**: ✅ Production-ready for phases 1-9  
**Next**: Enhanced static analysis mode (Phase 10)

