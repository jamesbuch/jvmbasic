# Phase 6: User-Defined Types - Design Document

**Date**: October 12, 2025  
**Status**: In Progress  
**Target**: Implement TYPE...ENDTYPE for struct-like records

---

## Syntax Design

### Type Definition:
```basic
TYPE TypeName
    field1 AS Type1
    field2 AS Type2
    field3 AS Type3
ENDTYPE
```

### Variable Declaration:
```basic
DIM varName AS TypeName
```

### Member Access:
```basic
LET varName.field1 = value
PRINT varName.field2
```

### Example:
```basic
TYPE Point
    x AS FLOAT
    y AS FLOAT
ENDTYPE

TYPE Rectangle
    topLeft AS Point
    width AS FLOAT
    height AS FLOAT
ENDTYPE

DIM rect AS Rectangle
LET rect.topLeft.x = 10.0
LET rect.topLeft.y = 20.0
LET rect.width = 100.0
LET rect.height = 50.0

PRINT "Rectangle at (", rect.topLeft.x, ",", rect.topLeft.y, ")"
PRINT "Size:", rect.width, "x", rect.height
```

---

## Implementation Strategy

### Option A: JVM Classes (Clean but Complex)
**Pros**:
- Type-safe at JVM level
- Natural mapping to OOP
- Future-proof for Phase 7

**Cons**:
- Requires dynamic class generation
- Complex ClassFile construction
- Harder to debug

### Option B: Object Arrays (Simple) ⭐ **CHOSEN**
**Pros**:
- Simple to implement
- Fast to develop
- Easy to understand
- Type-safe at BASIC level

**Cons**:
- Slightly less efficient
- No JVM-level type safety

**Implementation**:
```java
// Each TYPE instance is Object[]
// rect → [Point_object, 100.0f, 50.0f]
// Point_object → [10.0f, 20.0f]
```

### Decision: Start with Option B, migrate to A in Phase 7

---

## AST Changes

### New Node Types:

```cpp
// Type definition
struct TypeDef {
    string name;
    vector<pair<string, Type>> fields;  // field name → type
};

// New statement type
enum class StmtKind {
    // ... existing ...
    TypeDef,  // NEW
};

// New expression type for member access
struct MemberAccess {
    unique_ptr<Expr> object;
    string member;
};
```

---

## Type System Changes

### Extend Type enum:
```cpp
enum class Type {
    Int, Float, String, Bool,
    IntArray, FloatArray, StringArray, BoolArray,
    UserDefined,  // NEW - points to TypeDef
};

// Global registry
map<string, TypeDef> userTypes;
```

### Type Resolution:
```cpp
Type resolveType(const string& typeName) {
    if (typeName == "INT") return Type::Int;
    if (typeName == "FLOAT") return Type::Float;
    // ...
    if (userTypes.count(typeName)) return Type::UserDefined;
    error("Unknown type: " + typeName);
}
```

---

## Parser Changes

### 1. Parse TYPE...ENDTYPE

```cpp
unique_ptr<Stmt> Parser::parseTypeDecl() {
    consume(TokenType::TYPE);
    string typeName = current.value;
    consume(TokenType::Identifier);
    
    vector<pair<string, Type>> fields;
    
    while (current.type != TokenType::ENDTYPE) {
        string fieldName = current.value;
        consume(TokenType::Identifier);
        consume(TokenType::AS);
        Type fieldType = parseTypeSpecifier();
        fields.push_back({fieldName, fieldType});
    }
    
    consume(TokenType::ENDTYPE);
    
    // Register type
    userTypes[typeName] = TypeDef{typeName, fields};
    
    return make_unique<Stmt>(StmtKind::TypeDef, 
                             TypeDefStmt{typeName, fields});
}
```

### 2. Parse DIM with TYPE

```cpp
// DIM varName AS TypeName
if (nextToken == "AS") {
    consume("AS");
    string typeName = current.value;
    consume(TokenType::Identifier);
    
    Type varType;
    if (userTypes.count(typeName)) {
        varType = Type::UserDefined;
        // Store type name for later lookup
    }
}
```

### 3. Parse Member Access (dot operator)

```cpp
Expr* parseMemberAccess(Expr* object) {
    consume(TokenType::Dot);
    string member = current.value;
    consume(TokenType::Identifier);
    
    // Create member access expression
    return new MemberAccessExpr{object, member};
}
```

---

## Code Generation

### Object Array Layout:
```
TYPE Point { x, y }
→ Object[2] = {Float x, Float y}

TYPE Rectangle { topLeft, width, height }
→ Object[3] = {Object[] topLeft, Float width, Float height}
```

### Initialization:
```basic
DIM rect AS Rectangle
```
→
```java
// Allocate array with default values
Object[] rect = new Object[3];
rect[0] = new Object[2];  // topLeft (Point)
rect[1] = 0.0f;           // width
rect[2] = 0.0f;           // height
```

### Member Access:
```basic
LET rect.width = 100.0
```
→
```java
// Field index lookup: width = index 1
rect[1] = 100.0f;
```

### JVM Bytecode:
```
// rect.width = 100.0
aload_1        // Load rect
ldc 100.0      // Load constant
fconst_0       // Index 1 (but as array store)
fastore        // Store in array
```

**Wait, this is wrong!** Object arrays use `aastore` for reference types.

Better approach:
```
aload_1        // Load rect (Object[])
iconst_1       // Index 1 (width)
ldc 100.0      // Value
invokestatic Float.valueOf
aastore        // Store Object
```

Or use direct field storage in generated classes...

**Revised Decision**: Use simple approach with boxed types in Object[]

---

## Example Implementation Timeline

### Step 1: Lexer (1 hour)
- Add TYPE, ENDTYPE tokens
- Add AS token (might already exist)
- Add DOT token

### Step 2: AST (2 hours)
- TypeDef struct
- TypeDefStmt
- MemberAccessExpr
- Update Stmt and Expr variants

### Step 3: Parser (4 hours)
- parseTypeDecl()
- Parse DIM with user types
- Parse member access (dot operator)
- Update parsePrimary() for dots

### Step 4: Type System (3 hours)
- Extend Type enum
- Type registry (map<string, TypeDef>)
- Field lookup
- Type checking for member access

### Step 5: Code Generation (6 hours)
- Generate Object[] allocation
- Initialize nested types
- Member access bytecode
- Field index lookup

### Step 6: Testing (4 hours)
- Basic struct test
- Nested struct test
- Struct in functions
- Array of structs

**Total**: ~20 hours

---

## Test Cases

### Test 1: Basic Struct
```basic
TYPE Person
    name AS STRING
    age AS INT
ENDTYPE

DIM p AS Person
LET p.name = "Alice"
LET p.age = 30
PRINT p.name, "is", p.age, "years old"
```

### Test 2: Nested Structs
```basic
TYPE Point
    x AS FLOAT
    y AS FLOAT
ENDTYPE

TYPE Circle
    center AS Point
    radius AS FLOAT
ENDTYPE

DIM c AS Circle
LET c.center.x = 10.0
LET c.center.y = 20.0
LET c.radius = 5.0
```

### Test 3: Struct Parameters
```basic
FUNCTION distance(p AS Point) AS FLOAT
    RETURN SQR(p.x * p.x + p.y * p.y)
ENDFUNCTION

DIM origin AS Point
LET origin.x = 0.0
LET origin.y = 0.0
PRINT distance(origin)
```

---

## Challenges & Solutions

### Challenge 1: Dot Operator Conflicts with Floats
**Problem**: `3.14` vs `obj.field`
**Solution**: Lexer lookahead - if digit follows dot, it's float

### Challenge 2: Type Resolution Order
**Problem**: Forward references to user types
**Solution**: Two-pass - first collect all TYPE definitions

### Challenge 3: Nested Member Access
**Problem**: `rect.topLeft.x` - multiple dots
**Solution**: Recursive parsing of member access

### Challenge 4: Boxing/Unboxing
**Problem**: Object[] requires boxing primitives
**Solution**: Auto-box on store, auto-unbox on load

---

## Next Steps

1. ✅ Design complete (this document)
2. → Add tokens to lexer
3. → Extend AST structures
4. → Implement parser
5. → Extend type system
6. → Generate bytecode
7. → Test thoroughly

---

**Let's begin with Step 1: Lexer changes!**

