# Modular Compiler Refactor - Assessment

**Date**: October 13, 2025  
**Branch**: phase7-oop  
**Priority**: BLOCKING Phase 7 - Must complete first

---

## Current State Analysis

### ✅ What's Working (Modular)

| Component | File | Status | Notes |
|-----------|------|--------|-------|
| **AST** | ast.h, ast.cpp | ✅ Complete | Phase 6 support with TYPE |
| **Lexer** | lexer.h, lexer.cpp | ✅ Complete | Has TYPE, ENDTYPE, AS, REM, DOT |
| **Built-in Functions** | builtin_functions.h/cpp | ✅ Complete | 93 functions extracted |
| **Code Generation** | codegen.h | ✅ Complete | Extracted, header-only |
| **AST Printer** | ast_printer.h/cpp | ✅ Complete | Updated for Phase 6 |
| **Semantic Analyzer** | semantic.h/cpp | ⚠️ Partial | Needs Phase 6 TYPE validation |

### ❌ What's Missing (Modular)

| Component | File | Status | What's Needed |
|-----------|------|--------|---------------|
| **Parser** | parser.cpp | ❌ Phase 5 only | Missing TYPE parsing, member access |
| **Main** | main.cpp | ❌ No codegen | Needs codegen.h integration |
| **Semantic** | semantic.cpp | ⚠️ Incomplete | Needs TYPE/struct validation |

### 🗑️ What's Obsolete

| File | Purpose | Status |
|------|---------|--------|
| jvmbasic.cpp | Monolithic compiler | DELETE after migration |
| jvmbasic (binary) | Built from jvmbasic.cpp | Will be replaced |
| jvmbasic-new (binary) | Stub modular version | DELETE |

---

## Detailed Component Analysis

### 1. Lexer (lexer.cpp) ✅

**Status**: Complete for Phase 6

**Has**:
- All basic tokens (numbers, strings, operators)
- All control flow keywords (IF, FOR, WHILE, DO)
- All declaration keywords (DIM, FUNCTION, SUB, TYPE, ENDTYPE, AS)
- REM comments (lines 106-112)
- DOT operator for member access (line 166)
- Boolean literals (TRUE, FALSE)

**Missing for Phase 7**:
- CLASS, ENDCLASS keywords
- PUBLIC, PRIVATE keywords
- NEW, ME keywords  
- INTEGER keyword
- Apostrophe (') comment syntax

**Conclusion**: Lexer is Phase 6 ready, just needs Phase 7 tokens added.

---

### 2. Parser (parser.cpp) ❌

**Status**: Only Phase 5 support

**Has**:
- Expression parsing (arithmetic, comparison, boolean)
- Statement parsing (PRINT, LET, INPUT, DIM arrays)
- Control flow (IF/THEN/ELSE, FOR, WHILE, DO/UNTIL)
- Functions and SUBs with parameters
- Arrays
- Built-in function calls

**Missing**:
- TYPE...ENDTYPE parsing ❌
- DIM x AS TypeName ❌
- Member access (x.field) ❌
- Assignment to member (x.field = value) ❌
- Struct field type tracking ❌

**Lines to Extract from jvmbasic.cpp**:
- `parseTypeDecl()` - Parse TYPE declarations (~50 lines)
- Member access in `parsePrimary()` (~30 lines)
- DIM AS struct handling (~20 lines)
- Update `parseStmt()` for member assignment (~15 lines)

**Conclusion**: Need to port Phase 6 features from jvmbasic.cpp

---

### 3. Semantic Analyzer (semantic.cpp) ⚠️

**Status**: Has structure but incomplete

**Has**:
- Symbol table with scoping
- Type checking for expressions
- Array type validation
- Function signature checking

**Missing**:
- User-defined TYPE validation ❌
- Struct field type tracking ❌
- Member access type checking ❌

**Conclusion**: Need to add TYPE support

---

### 4. Main (main.cpp) ❌

**Status**: Parses and checks but doesn't generate code

**Has**:
- Command-line argument parsing (--dump-ast, --check-only)
- Lexer initialization
- Parser invocation
- Semantic analysis invocation
- AST printer invocation

**Missing**:
- **Code generation** (lines 69-72 say "TODO")

**What to Add**:
```cpp
// PHASE 3: Code Generation
#include "codegen.h"

// ... after semantic analysis ...

// Generate bytecode
generateBytecode(program, "BasicProgram");
cout << "Generated BasicProgram.class\n";
```

**Conclusion**: Need to integrate codegen.h

---

### 5. Code Generation (codegen.h) ✅

**Status**: Complete and extracted

**Has**:
- JVM bytecode generation
- All Phase 6 features (structs, member access)
- Class file format writing
- 93 built-in functions
- Arrays, loops, functions, subs

**Issue**: Currently included by jvmbasic.cpp, needs to be included by main.cpp instead

**Conclusion**: Ready to use, just needs integration

---

## Migration Strategy

### Phase 1: Parser Update (3-4 hours)

**Extract from jvmbasic.cpp lines 800-1200 approximately**:

1. Add `parseTypeDecl()` to parser.cpp
2. Update `parsePrimary()` to handle member access (DOT operator)
3. Update `parseDimStmt()` to handle `DIM x AS TypeName`
4. Update `parseStmt()` for member assignment
5. Add type name resolution map
6. Test: `./jvmbasic-new --dump-ast < tests/test_struct_basic.bas`

### Phase 2: Semantic Update (2-3 hours)

1. Add struct type validation to semantic.cpp
2. Add field name validation
3. Add member access type checking
4. Test: `./jvmbasic-new --check-only < tests/test_struct_basic.bas`

### Phase 3: Code Generation Integration (2-3 hours)

1. Add codegen.h include to main.cpp
2. Add command-line option `-o` for output file
3. Call `generateBytecode(program, className)`
4. Handle output file creation
5. Test: `./jvmbasic-new < tests/test_struct_basic.bas && java BasicProgram`

### Phase 4: Testing (3-4 hours)

1. Run all 49 tests through modular compiler
2. Compare output with monolithic version
3. Fix any discrepancies
4. Verify identical behavior

### Phase 5: Cleanup (1 hour)

1. Update Makefile to build only `jvmbasic` from modules
2. Delete `jvmbasic.cpp` (monolithic)
3. Delete `jvmbasic-new` target
4. Update documentation
5. Commit clean modular version

**Total Time**: 11-15 hours

---

## Implementation Plan

### Step 1: Extract Parser Functions ⚠️ CRITICAL

**Source**: jvmbasic.cpp lines ~750-1200

**Target**: parser.cpp

**Functions to Extract**:

```cpp
DeclPtr parseTypeDecl() {
    // Parse TYPE...ENDTYPE
}

void updateParsePrimary() {
    // Add DOT operator handling for x.field
}

void updateParseDimStmt() {
    // Add AS TypeName support
}

void updateParseStmt() {
    // Add x.field = value assignment
}
```

**Data Structures Needed**:
- `map<string, TypeDefDecl> userTypes` - Track TYPE definitions
- `map<string, map<string, int>> structFields` - Field indices

### Step 2: Integrate Code Generation

**Source**: codegen.h (already extracted)

**Target**: main.cpp

**Changes**:

```cpp
// Add after line 68
if (!checkOnly) {
    // PHASE 3: Code Generation
    string outputFile = "BasicProgram.class";  // TODO: from -o flag
    string className = "BasicProgram";
    
    generateBytecode(program, className);
    
    cout << "Generated " << outputFile << "\n";
    return 0;
}
```

### Step 3: Update Makefile

**Current**:
```makefile
# Two targets: jvmbasic (monolithic) and jvmbasic-new (stub)
```

**New**:
```makefile
# Single target: jvmbasic (fully modular)
jvmbasic: $(OBJECTS) main.o
	$(CXX) $(CXXFLAGS) $(OBJECTS) main.o -o jvmbasic
```

### Step 4: Test Verification

**Test all 49 tests**:
```bash
# Core tests
./test_runner.sh

# INPUT tests  
./run_input_tests.sh

# Struct tests
for f in tests/test_struct*.bas; do
    ./jvmbasic < "$f" && java BasicProgram
done
```

**Expected**: 49/49 passing

---

## Risk Assessment

### Low Risk ✅

- Lexer is already complete
- AST is complete
- codegen.h is extracted and working
- Built-in functions are extracted

### Medium Risk ⚠️

- Parser extraction is straightforward but tedious
- Semantic analyzer may need updates
- Testing may reveal edge cases

### High Risk ❌

- None - we have working monolithic version to reference
- Can always fall back if needed

---

## Success Criteria

- [ ] Single `jvmbasic` binary built from modules
- [ ] All 49 tests passing
- [ ] No `jvmbasic.cpp` monolithic file
- [ ] No `jvmbasic-new` stub
- [ ] Clean modular architecture:
  - lexer.cpp/h
  - parser.cpp/h
  - semantic.cpp/h
  - ast.cpp/h
  - ast_printer.cpp/h
  - builtin_functions.cpp/h
  - codegen.h
  - main.cpp
- [ ] Makefile builds single target
- [ ] Documentation updated

---

## Next Actions

1. ✅ Assessment complete
2. → Extract `parseTypeDecl()` from jvmbasic.cpp
3. → Extract member access parsing
4. → Integrate codegen into main.cpp
5. → Test all 49 tests
6. → Clean up old files
7. → Update docs

**Estimated completion**: 11-15 hours

---

**Once complete, we'll have a clean, maintainable foundation for Phase 7!** 🎯



