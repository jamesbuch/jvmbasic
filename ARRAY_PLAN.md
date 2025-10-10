# Array Implementation Plan

## Overview
Add support for one-dimensional arrays in JVM BASIC with type-safe array operations.

## Syntax Design

### Array Declaration
```basic
DIM numbers(10)          # Declare integer array of size 10
DIM names(5) AS STRING   # Declare string array of size 5
DIM flags(3) AS BOOL     # Declare boolean array of size 3
DIM temps(8) AS FLOAT    # Declare float array of size 8
```

**Alternative simpler syntax** (if we want to avoid AS keyword):
```basic
DIM numbers(10)          # Type inferred from first assignment
LET numbers(0) = 42      # Implicitly sets array type to Int
```

### Array Access
```basic
LET numbers(0) = 42      # Set array element
LET x = numbers(0)       # Get array element
PRINT numbers(5)         # Print array element
```

### Array in Expressions
```basic
LET sum = numbers(0) + numbers(1)
IF numbers(5) > 10 THEN
    PRINT "Large value"
ENDIF
```

## Technical Implementation

### 1. Type System Changes

**Add array types to Type enum:**
```cpp
enum class Type { 
    Int, Float, String, Bool,
    IntArray, FloatArray, StringArray, BoolArray 
};
```

### 2. AST Changes

**Add DIM statement:**
```cpp
struct DimStmt { 
    string name;
    ExprPtr size;  // Array size expression (must evaluate to Int)
    Type elemType; // Element type
};
```

**Modify VarRef to support array indexing:**
```cpp
struct VarRef { 
    string name;
    ExprPtr index; // nullptr for scalar, non-null for array access
};
```

**Or add separate ArrayAccess expression:**
```cpp
struct ArrayAccess {
    string name;
    ExprPtr index;
};
```

### 3. Parser Changes

**Add DIM keyword:**
- TokenType::DIM
- Parse: `DIM <ID> '(' <size-expr> ')' [AS <type>]`

**Parse array indexing:**
- After ID in parsePrimary, check for `(`
- Distinguish from function calls (future feature)
- Parse index expression: `ID '(' <expr> ')'`

**Parse array assignment:**
- In LET statement, after ID check for `(`
- `LET arr(index) = value`

### 4. JVM Array Instructions

**Array creation:**
- `newarray <type>` - for primitive arrays (int, float, boolean)
- `anewarray <class>` - for object arrays (String)

**Array access (load):**
- `iaload` - load int from array
- `faload` - load float from array
- `aaload` - load reference from array
- `baload` - load byte/boolean from array

**Array store:**
- `iastore` - store int to array
- `fastore` - store float to array
- `aastore` - store reference to array
- `bastore` - store byte/boolean to array

**Array length:**
- `arraylength` - get array length (for bounds checking, future feature)

### 5. Constant Pool Changes

No new constant pool entries needed for basic arrays!
- String arrays need String class reference (already have)
- Primitive arrays use newarray with type code

### 6. Code Generation

**DIM statement:**
```bytecode
# DIM numbers(10)
iconst_10              # push size
newarray T_INT         # create int[] array
astore <var_slot>      # store array reference
```

**Array element store:**
```bytecode
# LET numbers(3) = 42
aload <var_slot>       # load array reference
iconst_3               # push index
iconst_42              # push value
iastore                # store value at index
```

**Array element load:**
```bytecode
# LET x = numbers(3)
aload <var_slot>       # load array reference
iconst_3               # push index
iaload                 # load value at index
istore <x_slot>        # store to variable
```

### 7. Type Checking

- Arrays stored as reference types (use aload/astore for array reference itself)
- Element access type-checked at compile time
- `numbers(3) = "hello"` → compile error if numbers is IntArray
- Mixed type assignments rejected

### 8. Implementation Steps

1. **Add array types** to Type enum ✅
2. **Add DIM token and AS keyword** (optional)
3. **Add DimStmt to AST**
4. **Modify VarRef** to support indexing OR add ArrayAccess
5. **Update parser**:
   - Parse DIM statements
   - Parse array indexing in expressions
   - Parse array assignments in LET
6. **Add array bytecode helpers**:
   - `newarray_int()`, `newarray_float()`, etc.
   - `iaload()`, `iastore()`, etc.
7. **Update genStmt** for DIM
8. **Update load()** for array access
9. **Update LET genStmt** for array assignment
10. **Test thoroughly**

### 9. Testing Strategy

**Test 1: Integer arrays**
```basic
DIM numbers(5)
LET numbers(0) = 10
LET numbers(1) = 20
PRINT numbers(0), numbers(1)
```

**Test 2: Array in expressions**
```basic
DIM arr(3)
LET arr(0) = 5
LET arr(1) = 10
LET sum = arr(0) + arr(1)
PRINT "Sum:", sum
```

**Test 3: String arrays**
```basic
DIM names(3) AS STRING
LET names(0) = "Alice"
LET names(1) = "Bob"
PRINT names(0), names(1)
```

**Test 4: Arrays with INPUT**
```basic
DIM nums(3)
LET i = 0
INPUT nums(i)
LET i = 1
INPUT nums(i)
PRINT nums(0), nums(1)
```

**Test 5: Arrays in IF statements**
```basic
DIM scores(5)
LET scores(0) = 85
IF scores(0) >= 80 THEN
    PRINT "Pass"
ENDIF
```

### 10. Future Enhancements

- **Bounds checking**: Runtime checks for array index validity
- **Multi-dimensional arrays**: `DIM matrix(10, 10)`
- **Array literals**: `LET arr = [1, 2, 3, 4, 5]`
- **Array functions**: `LEN(arr)`, `SORT(arr)`, etc.
- **FOR EACH loops**: `FOR EACH item IN arr`

### 11. Limitations (Initial Implementation)

- **Fixed size**: Arrays size must be constant or computed at declaration
- **No resizing**: Array size fixed after DIM
- **No multi-dimensional**: Only 1D arrays initially
- **No array literals**: Must use DIM and individual assignments
- **No bounds checking**: Runtime errors if accessing out of bounds (JVM will throw)

---

## Decision: Syntax Choice

**Option A: Explicit typing with AS**
```basic
DIM numbers(10)           # Error: type not specified
DIM numbers(10) AS INT    # Explicit
```

**Option B: Implicit typing from first use**
```basic
DIM numbers(10)           # Creates array of unknown type
LET numbers(0) = 42       # Now it's IntArray
```

**Option C: Type inference from initialization**
```basic
DIM numbers(10) = 0       # Initialize all to 0, infer Int
DIM names(3) = ""         # Initialize all to "", infer String
```

**Recommended: Option C** - Most BASIC-like, clear intent, no AS keyword needed.

---

## Next Steps

1. Implement Option C syntax
2. Add tests as we go
3. Commit incrementally
4. Merge to main when stable


