# Phase 7: Object-Oriented Programming - Design Document

**Date**: October 12, 2025  
**Status**: Planning  
**Dependencies**: Phase 6 (User-Defined Types) ✅ Complete

---

## Overview

Phase 7 transforms JVM BASIC from a structured language with user-defined types into a true object-oriented language with classes, methods, constructors, encapsulation, and inheritance.

---

## Syntax Design (Visual Basic .NET Style)

### Class Definition

```basic
CLASS ClassName
    REM Instance variables (fields)
    PRIVATE balance AS FLOAT
    PUBLIC name AS STRING
    
    REM Constructor
    PUBLIC SUB New(initName AS STRING, initBalance AS FLOAT)
        LET name = initName
        LET balance = initBalance
    END SUB
    
    REM Methods
    PUBLIC SUB Deposit(amount AS FLOAT)
        LET balance = balance + amount
    END SUB
    
    PUBLIC FUNCTION GetBalance() AS FLOAT
        RETURN balance
    END FUNCTION
    
END CLASS
```

### Object Creation

```basic
REM Create instance
DIM account AS NEW BankAccount("Alice", 1000.0)

REM Access public members
PRINT account.name

REM Call methods
CALL account.Deposit(500.0)
LET bal = account.GetBalance()
PRINT "Balance: "; bal
```

---

## Comment Syntax Modernization

### Both Styles Supported

```basic
REM Traditional BASIC comment
' Modern VB-style comment

CLASS Person
    ' Instance variables
    PUBLIC name AS STRING  REM Can mix styles
    
    ' Constructor
    PUBLIC SUB New()
        ' Initialize to defaults
        LET name = ""
    END SUB
END CLASS
```

### Implementation
- `REM` already implemented (Phase 5)
- Add `'` (apostrophe) as alternative comment token
- Both skip to end of line
- No preference - user can use either

---

## Explicit Type Syntax (Phase 8)

### Type Suffixes (Future)
```basic
LET count = 1000L      REM Long integer
LET price = 99.99D     REM Decimal/Double
LET rate = 3.14F       REM Float
LET code = 42          REM Integer (default)
```

### Function Return Types (Future)
```basic
FUNCTION Add(a AS INTEGER, b AS INTEGER) AS INTEGER
    RETURN a + b
END FUNCTION
```

**Note**: Defer to Phase 8 to keep Phase 7 focused on OOP.

---

## Constructor Design

### Default Constructor
Every class has an implicit default constructor:

```basic
CLASS Point
    PUBLIC x AS FLOAT
    PUBLIC y AS FLOAT
END CLASS

REM Compiler generates implicit:
REM PUBLIC SUB New()
REM     LET x = 0.0
REM     LET y = 0.0
REM END SUB
```

Default initialization rules:
- `INT` → `0`
- `FLOAT` → `0.0`
- `STRING` → `""` (empty string)
- `BOOL` → `false`
- User-defined types → `null` or default constructor call
- Arrays → `null` (not allocated)

### Explicit Constructor

```basic
CLASS Person
    PUBLIC name AS STRING
    PUBLIC age AS FLOAT
    
    PUBLIC SUB New(n AS STRING, a AS FLOAT)
        LET name = n
        LET age = a
    END SUB
END CLASS

DIM p AS NEW Person("Bob", 25.0)
```

### Constructor Rules
- Named `New` (Visual Basic style)
- Must be PUBLIC or PRIVATE
- Can be overloaded (multiple constructors with different parameters)
- No return value
- Runs automatically on object creation

---

## Destructor/Cleanup Design

### Manual Cleanup (Phase 7)
```basic
CLASS FileHandler
    PRIVATE handle AS INT
    
    PUBLIC SUB New(filename AS STRING)
        LET handle = OPENINPUT(filename)
    END SUB
    
    PUBLIC SUB Delete()
        IF handle >= 0 THEN
            CALL CLOSEFILE(handle)
        ENDIF
    END SUB
END CLASS

DIM handler AS NEW FileHandler("data.txt")
REM ... use handler ...
CALL handler.Delete()  REM Manual cleanup
```

### Automatic Cleanup (Future - Phase 9+)
**Challenge**: JVM doesn't have deterministic destructors.

**Options**:
1. **Finalizer Pattern**: Override `finalize()` (deprecated in modern Java)
2. **Try-Finally Pattern**: Compiler inserts cleanup at scope end
3. **Reference Counting**: Track last reference, insert `Delete()` call
4. **Defer to User**: Manual cleanup via `Delete()` method

**Decision for Phase 7**: Manual cleanup only. Automatic cleanup deferred to Phase 9+ after more research.

---

## Encapsulation

### Access Modifiers

```basic
CLASS Account
    PRIVATE balance AS FLOAT      REM Not accessible outside class
    PUBLIC owner AS STRING         REM Accessible everywhere
    
    PUBLIC FUNCTION GetBalance() AS FLOAT
        RETURN balance             REM Can access private fields
    END FUNCTION
    
    PRIVATE SUB ValidateBalance()
        REM Private helper method
    END SUB
END CLASS
```

### Rules
- `PUBLIC` - Accessible from anywhere
- `PRIVATE` - Accessible only within class
- Default: `PUBLIC` (if not specified)
- Checked at compile time (semantic analysis)

---

## Method Types

### Instance Methods
```basic
PUBLIC SUB Greet()
    PRINT "Hello from "; name
END SUB
```

### Functions (with return)
```basic
PUBLIC FUNCTION GetAge() AS FLOAT
    RETURN age
END FUNCTION
```

### Static Methods (Future - Phase 8)
```basic
PUBLIC STATIC FUNCTION PI() AS FLOAT
    RETURN 3.14159
END FUNCTION

REM Called without instance
LET pi = Math.PI()
```

---

## Field Access and "Me" Reference

### Explicit Self Reference
```basic
CLASS Counter
    PRIVATE count AS FLOAT
    
    PUBLIC SUB Increment()
        LET Me.count = Me.count + 1.0  REM Explicit
        LET count = count + 1.0         REM Implicit (preferred)
    END SUB
END CLASS
```

### Implementation
- `Me` keyword refers to current instance
- Field access without `Me` searches: local vars → class fields
- `Me.field` explicitly accesses class field
- In constructor/methods, bare field names resolve to instance fields

---

## Implementation Strategy

### Phase 7A: Basic Classes (First Priority)
1. CLASS...END CLASS parsing
2. PUBLIC/PRIVATE modifiers
3. Field declarations in classes
4. Simple constructor (PUBLIC SUB New)
5. Instance methods (SUB and FUNCTION)
6. Object creation (DIM x AS NEW ClassName)
7. Method calls (obj.Method())

### Phase 7B: Advanced Features (Second Priority)
8. Me reference
9. Constructor overloading
10. Method overloading
11. Field access resolution
12. Manual cleanup (Delete method)

### Phase 7C: Deferred to Phase 8+
- Inheritance (INHERITS keyword)
- Method overriding (OVERRIDES keyword)
- Static methods/fields
- Protected access modifier
- Abstract classes/methods
- Interfaces

---

## JVM Implementation

### Class Generation Options

**Option A: Generate Nested Classes**
- Each BASIC CLASS becomes a nested static class in BasicProgram
- Simple to implement
- All in one .class file

**Option B: Generate Separate .class Files**
- Each BASIC CLASS becomes a separate .class file
- More complex (need to write multiple files)
- More "Java-like"

**Decision**: Option A for Phase 7 (simpler), migrate to B if needed later.

### Example Bytecode

```basic
CLASS Point
    PUBLIC x AS FLOAT
    PUBLIC y AS FLOAT
    
    PUBLIC SUB New(px AS FLOAT, py AS FLOAT)
        LET x = px
        LET y = py
    END SUB
END CLASS
```

→ Generates inner class in BasicProgram:
```java
public static class Point {
    public float x;
    public float y;
    
    public Point(float px, float py) {
        this.x = px;
        this.y = py;
    }
}
```

→ Usage:
```java
Point p = new Point(10.0f, 20.0f);
```

---

## AST Extensions

### New Nodes

```cpp
// Class declaration
struct ClassDecl {
    string name;
    vector<Field> fields;           // Instance variables
    vector<MethodDecl> methods;     // Methods and constructors
    bool isPublic;                  // Public vs internal
};

// Method declaration (instance method)
struct MethodDecl {
    string name;
    bool isPublic;                  // PUBLIC vs PRIVATE
    bool isConstructor;             // SUB New
    vector<Param> params;
    Type returnType;                // Float for SUB (void), actual for FUNCTION
    vector<StmtPtr> body;
};

// Member access for methods
struct MethodCallExpr {
    ExprPtr object;
    string methodName;
    vector<ExprPtr> args;
};

// New keyword for object creation
struct NewExpr {
    string className;
    vector<ExprPtr> args;           // Constructor arguments
};

// Me reference
struct MeExpr {
    // No data needed - just a marker
};
```

### Updated Enums

```cpp
enum class ExprKind {
    // ... existing ...
    MemberAccess,
    MethodCall,    // NEW: obj.method()
    NewExpr,       // NEW: NEW ClassName()
    Me             // NEW: Me reference
};

enum class DeclKind {
    // ... existing ...
    TypeDef,
    Class          // NEW
};
```

---

## Parser Changes

### 1. Parse CLASS...END CLASS

```cpp
DeclPtr parseClassDecl() {
    expect(TokenType::CLASS);
    string className = expect(TokenType::ID).val;
    
    vector<Field> fields;
    vector<MethodDecl> methods;
    
    while (tok.type != TokenType::END) {
        if (tok.type == TokenType::PUBLIC || tok.type == TokenType::PRIVATE) {
            bool isPublic = (tok.type == TokenType::PUBLIC);
            next();
            
            if (tok.type == TokenType::SUB || tok.type == TokenType::FUNCTION) {
                // Parse method
                methods.push_back(parseMethodDecl(isPublic));
            } else {
                // Parse field: PUBLIC name AS Type
                string fieldName = expect(TokenType::ID).val;
                expect(TokenType::AS);
                Type fieldType = resolveTypeName(expect(TokenType::ID).val);
                fields.push_back(Field{fieldName, fieldType, isPublic});
            }
        } else {
            error("Expected PUBLIC or PRIVATE in class");
        }
    }
    
    expect(TokenType::END);
    expect(TokenType::CLASS);
    
    return make_unique<Decl>(DeclKind::Class, ClassDecl{className, fields, methods});
}
```

### 2. Parse NEW Expression

```cpp
// In parsePrimary(), handle NEW keyword
if (tok.type == TokenType::NEW) {
    next();
    string className = expect(TokenType::ID).val;
    expect(TokenType::LPAREN);
    
    vector<ExprPtr> args;
    if (tok.type != TokenType::RPAREN) {
        args.push_back(parseExpr());
        while (tok.type == TokenType::COMMA) {
            next();
            args.push_back(parseExpr());
        }
    }
    expect(TokenType::RPAREN);
    
    return make_unique<Expr>(ExprKind::NewExpr, Type::UserDefined, 
                           NewExpr{className, move(args)});
}
```

### 3. Distinguish Method Call from Property Access

```cpp
// After parsing obj.member
if (tok.type == TokenType::LPAREN) {
    // Method call: obj.method(args)
    vector<ExprPtr> args = parseArguments();
    return make_unique<Expr>(ExprKind::MethodCall, ..., 
                           MethodCallExpr{move(obj), member, move(args)});
} else {
    // Property access: obj.field
    return make_unique<Expr>(ExprKind::MemberAccess, ...,
                           MemberAccessExpr{move(obj), member});
}
```

---

## Code Generation Strategy

### Inner Class Approach

Generate each BASIC CLASS as a static inner class:

```java
// BasicProgram.class
public class BasicProgram {
    // Nested class for each BASIC CLASS
    public static class Point {
        public float x;
        public float y;
        
        public Point(float px, float py) {
            this.x = px;
            this.y = py;
        }
    }
    
    public static class BankAccount {
        private float balance;
        
        public BankAccount() {
            this.balance = 0.0f;
        }
        
        public void deposit(float amount) {
            this.balance += amount;
        }
        
        public float getBalance() {
            return this.balance;
        }
    }
    
    public static void main(String[] args) {
        // BASIC main program
        Point p = new Point(10.0f, 20.0f);
        BankAccount acc = new BankAccount();
        acc.deposit(1000.0f);
    }
}
```

### InnerClasses Attribute

Need to add `InnerClasses` attribute to ClassFile for nested classes:
```
InnerClasses {
    inner_class: Point
    outer_class: BasicProgram
    flags: ACC_PUBLIC | ACC_STATIC
}
```

---

## Default Initialization Values

### Primitive Types
- `INT` → `0`
- `FLOAT` → `0.0`
- `BOOL` → `false` (0 in bytecode)

### Reference Types
- `STRING` → `""` (empty string, not null)
- User-defined types → `null` initially
- Arrays → `null` (not allocated)

### Why Empty String vs Null?

**Use Empty String** (Recommended):
- Safer - no NullPointerExceptions
- More BASIC-like (traditional BASIC didn't have null)
- Consistent with existing behavior
- Methods like `LEN("")` return 0 (safe)

**Implementation**:
```java
// Field initialization
this.name = "";         // String
this.count = 0;         // Int
this.value = 0.0f;      // Float
this.active = false;    // Bool
```

---

## RAII Pattern and Resource Management

### Constructor Initialization

```basic
CLASS FileLogger
    PRIVATE handle AS INT
    PRIVATE filename AS STRING
    
    PUBLIC SUB New(fname AS STRING)
        LET filename = fname
        LET handle = OPENOUTPUT(fname)
        IF handle < 0 THEN
            PRINT "Error opening file"
        ENDIF
    END SUB
    
    PUBLIC SUB Log(message AS STRING)
        IF handle >= 0 THEN
            CALL WRITELINE(handle, message)
        ENDIF
    END SUB
    
    PUBLIC SUB Delete()
        IF handle >= 0 THEN
            CALL CLOSEFILE(handle)
            LET handle = -1
        ENDIF
    END SUB
END CLASS
```

### Automatic Cleanup (Future - Phase 9)

**Approach 1**: Try-Finally in main()
```java
FileLogger logger = new FileLogger("log.txt");
try {
    logger.log("Hello");
} finally {
    logger.delete();  // Automatic
}
```

**Approach 2**: Scope-based Analysis
- Semantic analysis tracks variable scope
- Insert `Delete()` call at end of scope
- Requires sophisticated lifetime analysis

**Approach 3**: Reference Counting
- Track all references to object
- When last reference goes out of scope, call `Delete()`
- Complex for JVM (garbage collected)

**Decision**: Manual cleanup for Phase 7, research automatic cleanup for Phase 9.

---

## Member Access Resolution

### Name Resolution Order

In methods/constructors, name lookup follows this order:
1. **Local variables** (method parameters, LET variables)
2. **Instance fields** (class members)
3. **Global functions** (user-defined and built-in)

### Explicit vs Implicit

```basic
CLASS Example
    PUBLIC value AS FLOAT
    
    PUBLIC SUB SetValue(value AS FLOAT)
        REM Ambiguous: parameter vs field
        LET Me.value = value       REM Explicit: field = parameter
        LET value = value          REM Error: ambiguous
    END SUB
    
    PUBLIC SUB SetValueClear(newValue AS FLOAT)
        LET value = newValue       REM Clear: parameter ≠ field name
    END SUB
END CLASS
```

### Implementation
- `Me.field` always refers to instance field
- Bare `field` checks locals first, then instance fields
- Compiler warning for shadowing (optional Phase 8)

---

## Method Overloading (Future)

### Same Name, Different Parameters

```basic
CLASS Calculator
    PUBLIC FUNCTION Add(a AS FLOAT, b AS FLOAT) AS FLOAT
        RETURN a + b
    END FUNCTION
    
    PUBLIC FUNCTION Add(a AS INT, b AS INT) AS INT
        RETURN a + b
    END FUNCTION
END CLASS
```

**Implementation**: JVM supports overloading via descriptors.

**Decision**: Phase 7B or Phase 8 (after explicit types).

---

## Inheritance (Phase 7C or Phase 8)

### VB.NET Style

```basic
CLASS Vehicle
    PUBLIC speed AS FLOAT
    
    PUBLIC SUB Move()
        PRINT "Moving at "; speed
    END SUB
END CLASS

CLASS Car
    INHERITS Vehicle
    PUBLIC wheels AS INT
    
    PUBLIC OVERRIDES SUB Move()
        PRINT "Driving at "; speed; " with "; wheels; " wheels"
    END SUB
END CLASS
```

### Implementation Challenges
- JVM inheritance with `extends`
- Virtual method dispatch
- Constructor chaining (`super()`)
- Access to parent methods

**Decision**: Defer to Phase 7C or Phase 8. Start with composition, not inheritance.

---

## Implementation Timeline

### Phase 7A: Core Classes (15-20 hours)
1. Add CLASS, END CLASS, PUBLIC, PRIVATE, NEW, Me tokens
2. Add ' comment support
3. Extend AST with ClassDecl, MethodDecl
4. Parse CLASS declarations
5. Parse NEW expressions
6. Generate nested classes in BasicProgram
7. Generate constructors
8. Generate instance methods
9. Generate field access via `this`
10. Test basic class usage

### Phase 7B: Advanced Features (10-15 hours)
11. Constructor overloading
12. Method overloading
13. Better name resolution
14. Me reference
15. Private field enforcement
16. Delete() method pattern

### Phase 7C: Inheritance (15-20 hours)
17. INHERITS keyword
18. OVERRIDES keyword
19. Constructor chaining
20. Virtual dispatch
21. Protected modifier

**Total Phase 7A+7B**: 25-35 hours  
**Total Phase 7 (all)**: 40-55 hours

---

## Test Cases

### Test 1: Basic Class
```basic
CLASS Point
    PUBLIC x AS FLOAT
    PUBLIC y AS FLOAT
    
    PUBLIC SUB New(px AS FLOAT, py AS FLOAT)
        LET x = px
        LET y = py
    END SUB
    
    PUBLIC FUNCTION Distance() AS FLOAT
        RETURN SQRT(x * x + y * y)
    END FUNCTION
END CLASS

DIM p AS NEW Point(3.0, 4.0)
PRINT "Point: ("; p.x; ", "; p.y; ")"
PRINT "Distance: "; p.Distance()
```
**Expected**: `Point: (3.0, 4.0)` and `Distance: 5.0`

### Test 2: Encapsulation
```basic
CLASS BankAccount
    PRIVATE balance AS FLOAT
    PUBLIC owner AS STRING
    
    PUBLIC SUB New(name AS STRING, initial AS FLOAT)
        LET owner = name
        LET balance = initial
    END SUB
    
    PUBLIC SUB Deposit(amount AS FLOAT)
        IF amount > 0.0 THEN
            LET balance = balance + amount
        ENDIF
    END SUB
    
    PUBLIC FUNCTION GetBalance() AS FLOAT
        RETURN balance
    END FUNCTION
END CLASS

DIM account AS NEW BankAccount("Alice", 1000.0)
CALL account.Deposit(500.0)
PRINT account.owner; " has balance: "; account.GetBalance()
REM PRINT account.balance  REM Should error: private field
```

### Test 3: Resource Management
```basic
CLASS FileWriter
    PRIVATE handle AS INT
    
    PUBLIC SUB New(filename AS STRING)
        LET handle = OPENOUTPUT(filename)
    END SUB
    
    PUBLIC SUB Write(text AS STRING)
        IF handle >= 0 THEN
            CALL WRITELINE(handle, text)
        ENDIF
    END SUB
    
    PUBLIC SUB Delete()
        IF handle >= 0 THEN
            CALL CLOSEFILE(handle)
            LET handle = -1
        ENDIF
    END SUB
END CLASS

DIM writer AS NEW FileWriter("output.txt")
CALL writer.Write("Hello")
CALL writer.Write("World")
CALL writer.Delete()
```

---

## Semantic Analysis Requirements

### Checks Needed
1. **Duplicate field/method names** in class
2. **Private access violations** outside class
3. **Constructor validation** (must be named New)
4. **Method signature conflicts** (overloading rules)
5. **Me reference validity** (only in methods)
6. **Field initialization** (all fields get defaults)
7. **Class name conflicts** with existing types

### Implementation
- Add semantic analysis pass after parsing
- Build symbol tables for each class
- Check access modifiers
- Validate method calls

---

## Migration from Phase 6 Structs

### Compatibility

Phase 6 TYPE declarations should still work:
```basic
TYPE Point
    x AS FLOAT
    y AS FLOAT
ENDTYPE

DIM p AS Point
LET p.x = 10.0
```

**vs** Phase 7 CLASS:
```basic
CLASS Point
    PUBLIC x AS FLOAT
    PUBLIC y AS FLOAT
END CLASS

DIM p AS NEW Point()
LET p.x = 10.0
```

### Differences
- TYPE: No methods, no constructor, simple struct
- CLASS: Methods, constructors, encapsulation
- Both: Member access via dot notation
- TYPE uses Object[], CLASS uses generated JVM class

---

## Phase 7A Step-by-Step Implementation

### Step 1: Lexer (2 hours)
- Add tokens: `CLASS`, `PUBLIC`, `PRIVATE`, `NEW`, `ME`
- Add `'` apostrophe comment support
- Update `END CLASS` parsing (END followed by CLASS)

### Step 2: AST (3 hours)
- Add ClassDecl, MethodDecl structs
- Add NewExpr, MethodCallExpr, MeExpr
- Extend DeclKind and ExprKind enums
- Update ast_printer.cpp

### Step 3: Parser (8 hours)
- parseClassDecl() for CLASS...END CLASS
- parseMethodDecl() for methods
- Parse NEW expressions
- Parse Me keyword
- Parse method calls (obj.method())
- Update DIM to support AS NEW

### Step 4: Code Generation (12 hours)
- Generate nested class structure
- Generate constructors with field initialization
- Generate instance methods
- Generate NEW bytecode (new + dup + invokespecial <init>)
- Generate method calls (invokevirtual)
- Handle `this` for field access in methods

### Step 5: Testing (5 hours)
- Basic class test
- Constructor test
- Method call test
- Encapsulation test
- Multiple classes test

**Total Phase 7A**: ~30 hours

---

## Next Steps

1. ✅ Document read (this file)
2. → Add tokens (CLASS, PUBLIC, PRIVATE, NEW, ME, ')
3. → Extend AST
4. → Implement parser
5. → Generate bytecode
6. → Test thoroughly
7. → Update documentation

---

## Notes from User

- Start favoring modern BASIC syntax in Phase 8
- Explicit return types (`AS INTEGER`) in Phase 8
- Type literals (`1000D`, `42L`) in Phase 8
- RAII pattern with constructors
- Manual cleanup with Delete() for Phase 7
- Automatic cleanup research for Phase 9
- Default initialization to sensible values
- Prefer empty string over null for STRING

---

**Ready to begin Phase 7 implementation!** 🚀

