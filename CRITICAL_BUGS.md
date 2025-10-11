# Critical Bugs and Limitations

## 🔴 Critical Issues

### 1. **Large Programs Fail with VerifyError**

**Status**: CRITICAL BUG  
**Symptom**: Programs over ~100-150 lines fail at runtime with:
```
java.lang.VerifyError: Expecting a stackmap frame at branch target XXX
```

**Root Cause**: JVM requires stack map frames for methods with:
- More than ~32KB of bytecode
- Complex branch patterns (many IF/WHILE/FOR)
- Forward jumps that skip code

**Current Impact**:
- test_comprehensive.bas (171 lines) - FAILS ❌
- Moderate programs (50-100 lines) - WORK ✓
- Simple programs - WORK ✓

**Fix Required**: Add StackMapTable attribute generation in codegen

---

### 2. **Array Parameters Don't Work**

**Status**: CRITICAL LIMITATION  
**Symptom**: Cannot pass arrays to functions:
```basic
FUNCTION sum(arr, size)  # arr not recognized as array
    ...
ENDFUNCTION
```

**Root Cause**: Function signature doesn't track array types for parameters

**Current Impact**:
- test_algorithms.bas algorithms - FAIL ❌
- Module-level arrays - WORK ✓

**Fix Required**: Enhance type inference to handle array parameters

---

### 3. **Codegen Not Modular**

**Status**: ARCHITECTURAL DEBT  
**Issue**: Code generator still embedded in 2462-line jvmbasic.cpp  
**Impact**: 
- Hard to test codegen separately
- Hard to add new backends
- Violates modular architecture goals

**Fix Required**: Extract ~1200 lines of codegen to codegen.cpp

---

## ⚠️ Known Limitations (Not Bugs)

### 1. **No Comments**
- REM not supported
- Workaround: Descriptive names

### 2. **No Line Numbers**
- Classic BASIC line numbers not supported
- Modern structured approach

### 3. **Float-Centric**
- Integer arithmetic in functions sometimes promoted to Float
- Not a bug, design choice for simplicity

---

## ✅ What Actually Works

- Programs up to ~100 lines: PERFECT
- All control structures: WORKS
- Recursion: WORKS PERFECTLY
- Functions with local vars: WORKS
- SUBs with parameters: WORKS
- All 10 standard tests: PASS

---

## 🎯 Priority Fixes

1. **Extract Codegen** (8-12 hours) - Architectural
2. **Add Stack Map Frames** (16-20 hours) - Enable large programs
3. **Array Parameters** (4-6 hours) - Enable more algorithms

**Total Estimated**: 28-38 hours to true production quality

