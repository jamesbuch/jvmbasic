# Phase 7: OOP + Syntax Modernization Plan

**Date**: October 13, 2025  
**Branch**: phase7-oop  
**Status**: Planning → Implementation  

---

## Overview

Phase 7 combines two major improvements:
1. **Object-Oriented Programming** - CLASS, methods, constructors, encapsulation
2. **Syntax Modernization** - Visual Basic .NET-style explicit typing, deprecation of legacy syntax

This plan outlines the implementation strategy for both improvements simultaneously.

---

## 🎯 Syntax Modernization Goals

### 1. Explicit Type Declarations (Required)

**NEW Syntax** (VB.NET style):
```basic
DIM count AS INTEGER
DIM price AS FLOAT
DIM name AS STRING
DIM isActive AS BOOL
```

**DEPRECATE**:
```basic
LET count = 10           ' No type specified - REMOVE
LET price = 99.99        ' Type inference - REMOVE
LET name                 ' Uninitialized - REMOVE
```

**Strategy**: Require `DIM variable AS Type` for all variable declarations. No type inference.

### 2. Comment Syntax (Both Supported)

**KEEP Both Styles**:
```basic
REM Traditional BASIC comment
' Modern VB-style comment
```

Both already work or will be added in Phase 7A.

### 3. Print Statement Modernization

**Current Status**: Research needed on what to deprecate.

**Questions for user**:
- Keep `PRINT x; y` (semicolon for no space)?
- Keep `PRINT x, y` (comma for tab spacing)?
- Move to `PRINT x + " " + y` (string concatenation only)?

**Recommendation**: Keep semicolon syntax for Phase 7, revisit in Phase 8.

### 4. Strong Typing Everywhere

**Classes**:
```basic
CLASS Person
    PUBLIC name AS STRING
    PUBLIC age AS INTEGER
    
    PUBLIC SUB New(n AS STRING, a AS INTEGER)
        name = n
        age = a
    END SUB
    
    PUBLIC FUNCTION GetAge() AS INTEGER
        RETURN age
    END FUNCTION
END CLASS
```

**Functions** (already done in Phase 5, enhance in Phase 7):
```basic
FUNCTION Add(x AS INTEGER, y AS INTEGER) AS INTEGER
    RETURN x + y
END FUNCTION
```

**Variables**:
```basic
DIM result AS INTEGER
result = Add(10, 20)
```

---

## 🏗️ Type System Changes

### Remove Type Inference from Semantic Phase

**Current Behavior** (Phase 1-6):
- Parser guesses types based on literal values
- Semantic phase infers function return types
- `LET x = 10` creates a FLOAT variable (implicit)

**New Behavior** (Phase 7+):
- Parser requires explicit type declarations
- Semantic phase only **checks** types (no inference)
- `DIM x AS INTEGER` is required
- Error if type not specified

### Implementation Strategy

**Step 1**: Add INTEGER type (currently only FLOAT for numbers)
- Lexer: Recognize INTEGER keyword
- AST: Distinguish `Type::Int` from `Type::Float`
- Codegen: Use `int`/`istore`/`iload` instead of `float`/`fstore`/`fload`

**Step 2**: Make DIM...AS mandatory
- Parse `DIM name AS Type` (already exists for structs)
- Extend to all primitive types
- Error on `LET` without prior `DIM`

**Step 3**: Remove LET for declarations
- `LET` becomes assignment-only (requires prior DIM)
- `DIM x AS INTEGER` declares
- `LET x = 10` assigns (only if x already declared)

**Step 4**: Update all test files
- Convert `LET x = 10` → `DIM x AS INTEGER` + `x = 10`
- Add explicit types everywhere

---

## 📐 New Type System

### Primitive Types

| Type | BASIC Keyword | JVM Type | Default Value |
|------|---------------|----------|---------------|
| Integer | `INTEGER` | `int` (I) | 0 |
| Float | `FLOAT` | `float` (F) | 0.0 |
| String | `STRING` | `java/lang/String` | "" |
| Boolean | `BOOL` | `int` (0/1) | false (0) |

### Literals

**Integer Literals**:
```basic
DIM count AS INTEGER
count = 42
count = -100
```

**Float Literals** (require decimal point):
```basic
DIM price AS FLOAT
price = 99.99
price = 3.14
```

**Type Suffixes** (Phase 8 - Future):
```basic
LET big = 1000000L      ' Long integer
LET precise = 99.99D    ' Double/Decimal
LET rate = 3.14F        ' Float (explicit)
```

### Type Checking Rules

**Strict Assignment**:
```basic
DIM x AS INTEGER
x = 10        ' OK
x = 10.5      ' ERROR: Cannot assign FLOAT to INTEGER
```

**Explicit Conversion** (Phase 8):
```basic
DIM x AS INTEGER
DIM y AS FLOAT
y = 10.5
x = INT(y)    ' Explicit conversion required
```

**For Phase 7**: Allow implicit conversion FLOAT → INTEGER (truncate), INTEGER → FLOAT (widen).

---

## 🔧 Implementation Phases

### Phase 7A: Core OOP + Type System (30 hours)

**Week 1: Lexer & Parser (10 hours)**
1. Add tokens: `CLASS`, `PUBLIC`, `PRIVATE`, `NEW`, `ME`, `INTEGER`
2. Add `'` apostrophe comment support
3. Parse `CLASS...END CLASS` with fields and methods
4. Parse `DIM x AS INTEGER/FLOAT/STRING/BOOL`
5. Make `DIM...AS` mandatory for new code
6. Keep `LET` for assignment only (no declaration)

**Week 2: AST & Semantic (10 hours)**
7. Add `ClassDecl`, `MethodDecl` to AST
8. Add `NewExpr`, `MethodCallExpr`, `MeExpr` to AST
9. Update semantic analyzer:
   - Check types (no inference)
   - Validate `DIM...AS` declarations
   - Enforce type compatibility
10. Add `Type::Int` and distinguish from `Type::Float`

**Week 3: Code Generation (10 hours)**
11. Generate nested classes for `CLASS` declarations
12. Generate constructors (`<init>` methods)
13. Generate instance methods
14. Handle `NEW` operator (new + dup + invokespecial)
15. Handle method calls (aload + invokevirtual)
16. Generate INTEGER bytecode (iload/istore/iadd/etc.)
17. Generate FLOAT bytecode (fload/fstore/fadd/etc.)

### Phase 7B: Testing & Migration (10 hours)

**Week 4: Test Suite**
18. Create new test cases for classes
19. Create new test cases for explicit typing
20. Convert existing tests to new syntax
21. Update documentation
22. Update USER_GUIDE.md

---

## 📝 Syntax Examples

### Example 1: Basic Class with Explicit Types

```basic
' Modern VB.NET style BASIC with OOP
CLASS BankAccount
    PRIVATE balance AS FLOAT
    PUBLIC owner AS STRING
    
    PUBLIC SUB New(name AS STRING, initialBalance AS FLOAT)
        owner = name
        balance = initialBalance
    END SUB
    
    PUBLIC SUB Deposit(amount AS FLOAT)
        balance = balance + amount
    END SUB
    
    PUBLIC FUNCTION GetBalance() AS FLOAT
        RETURN balance
    END FUNCTION
END CLASS

' Main program
DIM account AS NEW BankAccount("Alice", 1000.0)
CALL account.Deposit(500.0)

DIM currentBalance AS FLOAT
currentBalance = account.GetBalance()

PRINT "Owner: "; account.owner
PRINT "Balance: $"; currentBalance
```

### Example 2: Explicit Variable Declarations

```basic
' All variables explicitly typed
DIM count AS INTEGER
DIM total AS FLOAT
DIM name AS STRING
DIM isValid AS BOOL

count = 0
total = 0.0
name = "John"
isValid = TRUE

FOR count = 1 TO 10
    total = total + count
NEXT

PRINT "Sum of 1 to 10: "; total
```

### Example 3: Functions with Explicit Types

```basic
FUNCTION Factorial(n AS INTEGER) AS INTEGER
    DIM result AS INTEGER
    DIM i AS INTEGER
    
    IF n <= 1 THEN
        RETURN 1
    ENDIF
    
    result = 1
    FOR i = 2 TO n
        result = result * i
    NEXT
    
    RETURN result
END FUNCTION

DIM num AS INTEGER
DIM fact AS INTEGER

num = 5
fact = Factorial(num)
PRINT "Factorial of "; num; " is "; fact
```

---

## 🔄 Migration Strategy

### Backward Compatibility

**Phase 7A**: Support BOTH old and new syntax (transition period)
- `LET x = 10` still works (infers FLOAT)
- `DIM x AS INTEGER` is new preferred way
- Emit warnings for old syntax

**Phase 7B**: Make new syntax default
- `LET x = 10` emits WARNING
- `DIM x AS INTEGER` required for new code
- Update all examples and tests

**Phase 8**: Deprecate old syntax completely
- `LET x = 10` is ERROR
- Must use `DIM x AS Type`

### Migration Process

**For Existing Code**:
1. Add `DIM` declarations at top of program/function
2. Convert `LET x = value` → `DIM x AS Type` + `x = value`
3. Specify types explicitly

**Automated Migration Script** (Optional - Phase 7C):
```bash
# Convert old syntax to new
./migrate_to_phase7.sh old_program.bas > new_program.bas
```

---

## 📊 Implementation Checklist

### Lexer Changes
- [ ] Add `CLASS` token
- [ ] Add `PUBLIC` token
- [ ] Add `PRIVATE` token
- [ ] Add `NEW` token
- [ ] Add `ME` token
- [ ] Add `INTEGER` token
- [ ] Add `'` apostrophe comment (skip to EOL)
- [ ] Update `END CLASS` parsing (END + CLASS)

### Parser Changes
- [ ] Parse `CLASS...END CLASS` declarations
- [ ] Parse `PUBLIC/PRIVATE` access modifiers
- [ ] Parse field declarations in classes
- [ ] Parse method declarations (SUB/FUNCTION in class)
- [ ] Parse `PUBLIC SUB New(...)` constructors
- [ ] Parse `DIM x AS INTEGER/FLOAT/STRING/BOOL`
- [ ] Parse `NEW ClassName(args)` expressions
- [ ] Parse `obj.method(args)` method calls
- [ ] Parse `ME` keyword (self reference)
- [ ] Distinguish `obj.field` vs `obj.method()`

### AST Changes
- [ ] Add `ClassDecl` struct (name, fields, methods)
- [ ] Add `MethodDecl` struct (name, params, return type, body)
- [ ] Add `NewExpr` struct (class name, args)
- [ ] Add `MethodCallExpr` struct (object, method name, args)
- [ ] Add `MeExpr` struct (self reference)
- [ ] Add `Type::Int` enum value
- [ ] Update `Field` struct with `isPublic` flag
- [ ] Extend `DeclKind` enum with `Class`
- [ ] Extend `ExprKind` enum with `NewExpr`, `MethodCall`, `Me`

### Semantic Analysis Changes
- [ ] Build symbol table for each class
- [ ] Check duplicate field/method names
- [ ] Validate constructor signatures
- [ ] Check access modifier violations (private fields)
- [ ] Validate `ME` usage (only in methods)
- [ ] Type checking (no inference)
- [ ] Require `DIM...AS` for declarations
- [ ] Check INTEGER vs FLOAT compatibility

### Code Generation Changes
- [ ] Generate nested static class for each `CLASS`
- [ ] Generate field declarations (public/private)
- [ ] Generate default constructor with field initialization
- [ ] Generate explicit constructors (`<init>` methods)
- [ ] Generate instance methods
- [ ] Generate `NEW` bytecode (new + dup + invokespecial)
- [ ] Generate method calls (aload + invokevirtual)
- [ ] Generate field access (aload + getfield/putfield)
- [ ] Generate `ME` reference (aload_0)
- [ ] Generate INTEGER operations (iadd, isub, imul, idiv, irem)
- [ ] Generate INTEGER load/store (iload, istore)
- [ ] Generate INTEGER constants (iconst, bipush, sipush, ldc)
- [ ] Add InnerClasses attribute to ClassFile

### Testing
- [ ] test_class_basic.bas - Simple class with fields
- [ ] test_class_constructor.bas - Constructor with parameters
- [ ] test_class_methods.bas - Instance methods (SUB/FUNCTION)
- [ ] test_class_encapsulation.bas - Private fields
- [ ] test_class_multiple.bas - Multiple classes in one program
- [ ] test_integer_type.bas - INTEGER variables and operations
- [ ] test_explicit_types.bas - DIM...AS declarations
- [ ] Convert existing tests to new syntax

### Documentation
- [ ] Update USER_GUIDE.md with CLASS syntax
- [ ] Update USER_GUIDE.md with INTEGER type
- [ ] Update USER_GUIDE.md with DIM...AS requirement
- [ ] Add OOP examples to examples/
- [ ] Update README.md
- [ ] Create MIGRATION_GUIDE.md

---

## 🎓 Design Decisions

### 1. INTEGER vs FLOAT

**Why Both?**
- INTEGER: Whole numbers, loop counters, array indices
- FLOAT: Decimal numbers, calculations, measurements
- Explicit distinction improves clarity and performance

**JVM Mapping**:
- `INTEGER` → `int` (32-bit signed)
- `FLOAT` → `float` (32-bit IEEE 754)

**Future** (Phase 8+):
- `LONG` → `long` (64-bit signed)
- `DOUBLE` → `double` (64-bit IEEE 754)

### 2. Nested Classes vs Separate Files

**Phase 7A**: Nested static classes
```java
public class BasicProgram {
    public static class BankAccount {
        // ...
    }
    
    public static void main(String[] args) {
        BankAccount acc = new BankAccount();
    }
}
```

**Why?**
- Simpler implementation (one .class file)
- All code in single output
- Easier to debug

**Future** (Phase 8+): Separate .class files if needed.

### 3. Default Field Initialization

**All fields initialized in constructor**:
```java
public Point() {
    this.x = 0;        // INTEGER → 0
    this.y = 0.0f;     // FLOAT → 0.0
    this.name = "";    // STRING → ""
    this.active = 0;   // BOOL → false (0)
}
```

**Why empty string?**
- Safer than null (no NullPointerException)
- Traditional BASIC didn't have null
- Methods like `LEN("")` work correctly

### 4. Type Checking vs Type Inference

**Phase 7 Philosophy**: Explicit is better than implicit.

**OLD** (Phase 1-6):
```basic
LET count = 10        ' Compiler infers FLOAT (wrong!)
LET name = "Bob"      ' Compiler infers STRING (ok)
```

**NEW** (Phase 7+):
```basic
DIM count AS INTEGER  ' Explicit type required
count = 10            ' Type checked

DIM name AS STRING
name = "Bob"
```

**Benefits**:
- Clearer code
- Fewer runtime errors
- Better performance (no unnecessary conversions)
- Easier to reason about

---

## 📂 File Changes Summary

### Files to Modify

| File | Lines | Changes |
|------|-------|---------|
| jvmbasic.cpp | ~1,400 | Add CLASS parsing, INTEGER type, DIM...AS |
| ast.h | ~250 | Add ClassDecl, MethodDecl, NewExpr, Type::Int |
| codegen.h | ~1,500 | Generate classes, INTEGER bytecode |
| ast_printer.cpp | ~300 | Print CLASS declarations |
| builtin_functions.h | ~200 | Update type signatures if needed |

### New Files to Create

- `tests/test_class_basic.bas`
- `tests/test_class_constructor.bas`
- `tests/test_class_methods.bas`
- `tests/test_class_encapsulation.bas`
- `tests/test_class_multiple.bas`
- `tests/test_integer_type.bas`
- `tests/test_explicit_types.bas`
- `docs/MIGRATION_GUIDE.md`
- `examples/oop_bank_account.bas`
- `examples/oop_point_class.bas`

---

## 🚀 Next Steps

### Immediate Actions

1. **Review this plan** with user
2. **Confirm syntax decisions**:
   - Require `DIM...AS` for all variables?
   - Keep semicolon in PRINT statements?
   - INTEGER vs INT (keyword name)?
3. **Start implementation**:
   - Begin with lexer tokens
   - Add INTEGER type support
   - Implement CLASS parsing
   - Generate bytecode

### Questions for User

1. **PRINT syntax**: Keep `PRINT x; y` or deprecate?
2. **Type keyword**: `INTEGER` or `INT` (shorter)?
3. **Backward compatibility**: Support old syntax in Phase 7A?
4. **Migration timing**: When to make new syntax mandatory?

---

## 📈 Timeline Estimate

| Phase | Task | Time |
|-------|------|------|
| 7A.1 | Lexer tokens (CLASS, PUBLIC, etc.) | 2 hours |
| 7A.2 | Add INTEGER type support | 3 hours |
| 7A.3 | Parse CLASS declarations | 5 hours |
| 7A.4 | Parse DIM...AS for primitives | 3 hours |
| 7A.5 | AST extensions | 3 hours |
| 7A.6 | Generate INTEGER bytecode | 4 hours |
| 7A.7 | Generate CLASS bytecode | 8 hours |
| 7A.8 | Testing and debugging | 5 hours |
| 7A.9 | Update documentation | 3 hours |
| **Total Phase 7A** | | **36 hours** |

---

## ✅ Success Criteria

Phase 7A is complete when:

- [ ] All existing tests pass
- [ ] Can define classes with fields and methods
- [ ] Can create objects with NEW
- [ ] Can call methods on objects
- [ ] INTEGER and FLOAT types work correctly
- [ ] DIM...AS declarations work for all types
- [ ] Private/Public access modifiers enforced
- [ ] 5+ new OOP test cases pass
- [ ] Documentation updated
- [ ] Zero regressions

---

**Ready to begin implementation!** 🎯

Next: Start with lexer tokens and INTEGER type support.


