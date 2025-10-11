# Array Parameter Implementation Plan

## Problem

Currently:
```basic
FUNCTION sumArray(arr, size)
    LET total = total + arr(i)  # Error: arr is not an array
ENDFUNCTION

DIM nums(5) = 0.0
PRINT sumArray(nums, 5.0)  # Passes FloatArray type
```

**Issue**: Parameters registered as Float in knownTypes, not as array types

## Root Cause

In parseDecl() line 481:
```cpp
for (const auto& param : params) {
    knownTypes[param.name] = Type::Float;  // WRONG for arrays!
}
```

After type inference (line 858), param.type is correctly set to FloatArray, but knownTypes still has Float.

## Solution

After inferParameterTypes() completes, update knownTypes in function/sub scope to match inferred types:

```cpp
// In parseDecl(), AFTER type inference:
// Replace line 481 with:
for (const auto& param : params) {
    // Will be updated after inference
    knownTypes[param.name] = Type::Float;  // Temporary
}

// Then in inferParameterTypes(), after setting param.type:
// Add a post-processing step to update function/sub scopes
```

Better approach: **Re-parse function bodies** after type inference with correct types!

## Implementation Steps

1. **Store function bodies unparsed** (as token streams)
2. **Parse parameters only** in first pass
3. **Infer types** from call sites  
4. **Re-parse bodies** with correct parameter types in knownTypes
5. **Update codegen** to handle array parameters in descriptors

**Effort**: 4-6 hours  
**Complexity**: Medium (requires two-pass parsing)

## Simpler Alternative

**One-pass fix**: After type inference, patch knownTypes in parsed AST:

1. inferParameterTypes() runs (infers arr → FloatArray)
2. Walk through function/sub bodies in AST
3. Update VarRef nodes where name matches array parameter
4. Problem: AST is already built with wrong types

**Issue**: AST already has Type::Float for arr, changing it post-hoc is messy

## Recommended Approach

**Best**: Delay body parsing until after type inference

Current flow:
```
parseDecl() {
  parse params
  register params as Float
  parse body  ← uses wrong types!
  return Decl
}
parse()
inferParameterTypes()  ← too late!
```

New flow:
```
parseDecl() {
  parse params
  save body as tokens (don't parse yet)
  return Decl with unparsed body
}
parse()
inferParameterTypes()
parseDecl Bodies() {  ← NEW
  for each decl:
    register params with INFERRED types
    parse body now
}
```

## Quick Fix (For Now)

Update knownTypes lookup in function scope to check parameter types:

In parsePrimary() around line 365, when checking array access:
```cpp
// Check if it's a function parameter with inferred array type
// (knownTypes might be wrong during initial parse)
```

Actually simpler: Just update knownTypes after inference!

## Implementation (4 hours)

1. Modify inferParameterTypes() to return parameter type mappings
2. Add updateFunctionScopes() that re-walks the AST
3. Update VarRef type information
4. Update codegen to handle array parameters

## Start Now?

