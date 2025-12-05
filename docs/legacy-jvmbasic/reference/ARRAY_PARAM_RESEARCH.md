# Array Parameter Research - Java Bytecode Analysis

## Test Program

```java
public static float sumArray(float[] arr, int size) {
    float total = 0.0f;
    for (int i = 0; i < size; i++) {
        total = total + arr[i];
    }
    return total;
}
```

## Bytecode Analysis

### Method Signature
```
Descriptor: ([FI)F
Means: (float[], int) -> float
```

**Key**: `[F` for array parameter, NOT just `F`!

### Parameter Slots
```
args_size=2
  slot 0: float[] arr  (reference type)
  slot 1: int size     (primitive)
  slot 2: float total  (local variable)
  slot 3: int i        (local variable)
```

### Loading Array Parameter
```
10: aload_0      // Load arr from slot 0 (REFERENCE!)
11: iload_3      // Load index i
12: faload       // Load float element arr[i]
```

**Critical**: Arrays are REFERENCE types, use `aload` not `fload`!

### Calling the Method
```
// In main():
34: invokestatic  #18  // Method sumArray:([FI)F
```

---

## Implementation Plan for JVM BASIC

### Changes Needed

#### 1. Fix generateFunction() Parameter Loading

**Current (WRONG for arrays)**:
```cpp
map<string, Type> localTypes;
for (const auto& param : fd.params) {
    localTypes[param.name] = fd.returnType;  // All params as return type
}
```

**New (CORRECT)**:
```cpp
map<string, Type> localTypes;
for (const auto& param : fd.params) {
    localTypes[param.name] = param.type;  // Use actual type
}

// BUT ALSO need to update load() to handle array params:
// If loading array parameter (FloatArray), use aload (reference)
// If loading scalar parameter (Float), use fload (value)
```

#### 2. Fix Method Descriptor Generation

**Current (WRONG)**:
```cpp
for (const auto& param : fd.params) {
    if (fd.returnType == Type::Float) descriptor += "F";
}
```

**New (CORRECT)**:
```cpp
for (const auto& param : fd.params) {
    Type ptype = param.type;
    if (ptype == Type::FloatArray) descriptor += "[F";
    else if (ptype == Type::IntArray) descriptor += "[I";
    else if (ptype == Type::StringArray) descriptor += "[Ljava/lang/String;";
    else if (ptype == Type::BoolArray) descriptor += "[Z";
    else if (ptype == Type::Float) descriptor += "F";
    else if (ptype == Type::Int || ptype == Type::Bool) descriptor += "I";
    else if (ptype == Type::String) descriptor += "Ljava/lang/String;";
}
```

#### 3. Fix load() to Handle Array vs Scalar Parameters

**In load() for ExprKind::Var**:
```cpp
if (actualType == Type::FloatArray || actualType == Type::IntArray ||
    actualType == Type::StringArray || actualType == Type::BoolArray) {
    // Array reference parameter - use aload
    aload(idx);
} else if (actualType == Type::Float) {
    fload(idx);
} else if (actualType == Type::Int || actualType == Type::Bool) {
    iload(idx);
} else {
    aload(idx);  // String
}
```

#### 4. Fix Function Call Sites

**When calling functions with array args**:
```cpp
// In load() for ExprKind::Call
for (const auto& arg : ce.args) {
    load(*arg, varIdx);
    // Don't convert arrays!
    if (arg->type != FloatArray && arg->type != IntArray && ...) {
        // Only convert scalars
    }
}
```

---

## Test Case Walkthrough

### BASIC Code:
```basic
FUNCTION sumArray(arr, size)
    LET total = 0.0
    LET i = 0.0
    WHILE i < size
        LET total = total + arr(i)
        LET i = i + 1.0
    ENDWHILE
    RETURN total
ENDFUNCTION

DIM nums(5) = 0.0
LET nums(0) = 10.0
PRINT sumArray(nums, 5.0)
```

### Expected Flow:

**1. Parsing**:
- Parse `sumArray(arr, size)` parameters
- Register params as `Float` temporarily
- Parse body (arr(i) fails if we're strict)

**2. Type Inference**:
- See call: `sumArray(nums, 5.0)`
- nums is FloatArray
- Infer: arr → FloatArray, size → Float

**3. Codegen**:
- Generate method: `sumArray([FF)F`
- Parameter slots:
  - 0: float[] arr (use aload_0)
  - 1: float size (use fload_1)
- Body:
  - Load arr: `aload_0`
  - Load index: `fload`, `f2i` convert
  - Access: `faload`

---

## Issues to Solve

### Issue 1: Parsing arr(i) When arr is Typed as Float

**Solution**: Be lenient during parsing
```cpp
if (it->second != array type && it->second != FloatArray/IntArray/...) {
    // Could be a parameter, allow it
    // Will be fixed by type inference
}
```

### Issue 2: Return Type Simplification Breaks

**Current trick**: All params use return type in signature  
**Problem**: Can't use Float for FloatArray parameter!

**Solution**: Use ACTUAL parameter types, fix Int→Float promotion at call sites:

```cpp
// Caller promotes arguments to match signature
if (paramType == Float && argType == Int) {
    load(arg);
    i2f();  // Convert
}
```

---

## Implementation Checklist

- [ ] Fix generateFunction() to use param.type for localTypes
- [ ] Fix descriptor generation to use param.type
- [ ] Fix load() to use aload for array reference parameters
- [ ] Fix function calls to match exact parameter types
- [ ] Add conversion logic for Int→Float at call sites
- [ ] Test with sumArray example
- [ ] Verify all 10 standard tests still pass

**Estimated Time**: 4-6 hours

---

## Next Step

Start implementing the fixes one by one, testing after each change.

