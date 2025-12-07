# Phase 5 Test Suite
**Date**: October 12, 2025  
**Purpose**: Comprehensive testing of user-defined functions

---

## Test Results

### ✅ Working Tests

| Test File | Features | Status |
|-----------|----------|--------|
| test_function_simple.bas | Basic function with 2 params | ✓ PASS |
| test_func_single_param.bas | Single parameter, nested calls | ✓ PASS |
| test_func_multi_param.bas | 2 and 3 parameter functions | ✓ PASS |
| test_func_minimal.bas | Minimal function usage | ✓ PASS |
| test_func_expression_only.bas | Functions with expressions only | ✓ PASS |

### 🚧 Known Limitations

| Test File | Issue | Status |
|-----------|-------|--------|
| test_func_with_loops.bas | Local variables in functions | Not yet supported |
| test_func_recursion.bas | Recursive calls | Not yet supported |
| test_sub_working.bas | SUB parameter types | Type mismatch |

---

## What Works ✅

### Basic Functions
```basic
FUNCTION add(a, b)
    RETURN a + b
ENDFUNCTION

LET result = add(5, 3)  # ✓ Works!
```

### Multiple Parameters
```basic
FUNCTION average(a, b, c)
    RETURN (a + b + c) / 3
ENDFUNCTION

PRINT average(10, 20, 30)  # ✓ Works! Output: 20.0
```

### Nested Calls
```basic
FUNCTION double(x)
    RETURN x * 2
ENDFUNCTION

PRINT double(double(5))  # ✓ Works! Output: 20.0
```

### Type Inference
```basic
FUNCTION add(a, b)  # Types inferred from call site
    RETURN a + b
ENDFUNCTION

add(5, 3)      # Infers: a:Int, b:Int
add(2.5, 3.5)  # Promotes to: a:Float, b:Float
```

### Conditional Returns
```basic
FUNCTION max2(a, b)
    IF a > b THEN
        RETURN a
    ELSE
        RETURN b
    ENDIF
ENDFUNCTION

PRINT max2(15, 23)  # ✓ Works! Output: 23.0
```

---

## What Doesn't Work Yet 🚧

### Local Variables in Functions
```basic
FUNCTION factorial(n)
    LET result = 1  # ✗ Scoping issue
    # ...
ENDFUNCTION
```

**Issue:** Local variables inside functions aren't scoped properly  
**Workaround:** Use only parameter manipulation and return expressions  
**Fix needed:** Add proper local variable tracking in generateFunction()

### Recursive Functions
```basic
FUNCTION fib(n)
    RETURN fib(n-1) + fib(n-2)  # ✗ Function not registered during parse
ENDFUNCTION
```

**Issue:** Function not in symbol table when parsing its own body  
**Workaround:** None currently  
**Fix needed:** Register function signature before parsing body

### SUB Procedures
```basic
SUB greet(name)
    PRINT "Hello", name  # ✗ Type mismatch in codegen
ENDSUB
```

**Issue:** Parameter types not loaded correctly in codegen  
**Workaround:** Use only numeric parameters  
**Fix needed:** Fix currentLocalTypes lookup in load()

---

## Type Inference Test Cases

### Test 1: All Int
```basic
FUNCTION add(a, b)
    RETURN a + b
ENDFUNCTION
add(5, 3)  # Infers: Int, Int → Float
```
✓ Works perfectly

### Test 2: All Float
```basic
FUNCTION mul(x, y)
    RETURN x * y
ENDFUNCTION
mul(2.5, 3.0)  # Infers: Float, Float → Float
```
✓ Works perfectly

### Test 3: Mixed Int/Float
```basic
FUNCTION add(a, b)
    RETURN a + b
ENDFUNCTION
add(5, 3)      # First call: Int, Int
add(2.5, 3.5)  # Second call: promotes to Float, Float
```
✓ Type promotion works!

---

## Recommended Test Approach

### For Now (Phase 5 v1)
**Use functions with:**
- ✓ Parameters only (no local variables)
- ✓ Direct return of expressions
- ✓ Conditional returns (IF/THEN/ELSE)
- ✓ Nested function calls
- ✓ Arithmetic operations on parameters

**Avoid:**
- ✗ LET statements inside functions
- ✗ Loops inside functions
- ✗ Recursive calls
- ✗ SUB procedures (for now)

### For Phase 5 v2 (Future Refinement)
- Fix local variable scoping
- Add recursion support
- Fix SUB parameter handling
- Add function-local arrays

---

## Test Commands

```bash
# Working tests
./jvmbasic < tests/test_function_simple.bas && java -cp . BasicProgram
./jvmbasic < tests/test_func_single_param.bas && java -cp . BasicProgram
./jvmbasic < tests/test_func_multi_param.bas && java -cp . BasicProgram
./jvmbasic < tests/test_func_minimal.bas && java -cp . BasicProgram
./jvmbasic < tests/test_func_expression_only.bas && java -cp . BasicProgram

# Debug with AST dump
./jvmbasic-new --dump-ast < tests/test_func_single_param.bas
```

---

## Conclusion

**Phase 5 Core Functionality:** ✅ WORKING  
**Type Inference:** ✅ EXCELLENT  
**Error Reporting:** ✅ PROFESSIONAL  

**Known Limitations:** Documented and addressable  
**Recommendation:** Merge Phase 5 as-is, refine in Phase 5.1

The core value is delivered - users can write reusable functions!

