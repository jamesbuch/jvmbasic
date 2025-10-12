# JVM BASIC - Debugging Guide

**Purpose**: Techniques and tools for debugging the compiler and generated code  
**Audience**: Developers troubleshooting issues

---

## Debugging Philosophy

**Rule #1**: Use the right tool for each phase:
- **Lexer issues** → Token dumps
- **Parser issues** → AST dumps
- **Type issues** → Semantic analyzer output
- **Runtime issues** → Bytecode inspection + JVM errors

---

## Phase 1: Lexer Debugging

### Problem: Unexpected token errors

**Symptom**:
```
Error: Line 5: Expected THEN but got ID
```

**Solution**: Dump tokens

**Method 1**: Add debug output
```cpp
// In lexer.cpp, nextToken()
Token t = /* ... */;
cerr << "TOKEN: " << (int)t.type << " [" << t.val << "] line " << t.line << "\n";
return t;
```

**Method 2**: Test lexer in isolation
```bash
# Create minimal test
echo 'IF x > 5 THEN' | ./jvmbasic-new
```

### Common Lexer Issues

**Issue**: Keywords not recognized
```basic
Print "hello"  # Works
print "hello"  # Should work but doesn't
```

**Fix**: Check uppercase conversion:
```cpp
string upper = s;
for (auto& c : upper) c = toupper(c);
```

**Issue**: Numbers with decimal points
```basic
PRINT 3.14  # Works
PRINT .5    # Doesn't work (expected)
```

**Note**: By design, numbers must start with digit.

---

## Phase 2: Parser Debugging

### Problem: Parse errors or crashes

**Symptom**:
```
Segmentation fault
```
or
```
Error: Unexpected EOF
```

**Solution 1**: Use AST Dump

```bash
./jvmbasic-new --dump-ast < program.bas
```

**Output**:
```
FunctionDecl: factorial
  Params:
    n: Float
  Body:
    If
      Condition: (n <= 1.0)
```

**What to Check**:
- Are all statements present?
- Are expressions structured correctly?
- Are types assigned?

**Solution 2**: Add Parser Traces

```cpp
unique_ptr<Stmt> Parser::parseIfStmt() {
    cerr << "DEBUG: Parsing IF at line " << current.line << "\n";
    // ... parsing logic ...
    cerr << "DEBUG: IF parsed successfully\n";
}
```

### Common Parser Issues

**Issue**: Null pointer dereference
```cpp
// BAD
auto expr = parseExpr();
if (expr->type == Type::Int) // CRASH if parseExpr returned nullptr
```

**Fix**: Always check for nullptr:
```cpp
auto expr = parseExpr();
if (!expr) error("Expected expression");
if (expr->type == Type::Int) // Safe
```

**Issue**: Infinite recursion
```cpp
// BAD - direct left recursion
Expr* parseExpr() {
    auto left = parseExpr();  // Stack overflow!
    // ...
}
```

**Fix**: Use precedence climbing or eliminate left recursion.

**Issue**: Wrong precedence
```basic
PRINT 2 + 3 * 4  # Should be 14, not 20
```

**Check**: Operator precedence in parser:
```cpp
// Higher precedence = tighter binding
// parseMultiplicative() before parseAdditive()
```

---

## Phase 3: Type System Debugging

### Problem: Type mismatches

**Symptom**:
```
Error: Line 10: Type mismatch: expected Float but got Int
```

**Solution**: Trace type inference

```cpp
// In semantic.cpp
Type inferType(const Expr& expr) {
    cerr << "Inferring type for expr at line " << expr.line << "\n";
    Type t = /* ... inference logic ... */;
    cerr << "  Result: " << typeToString(t) << "\n";
    return t;
}
```

### Debugging Multi-Pass Inference

For array parameters, we use **multi-pass** type inference:

**Pass 1**: Collect call sites
```cpp
cerr << "Pass 1: Collecting call sites\n";
collectCallSites();
cerr << "  Found " << callSites.size() << " call sites\n";
```

**Pass 2**: Infer types
```cpp
cerr << "Pass 2: Inferring parameter types\n";
inferParameterTypes();
cerr << "  Function foo: param types = ";
// Print inferred types
```

**Pass 3**: Fix AST
```cpp
cerr << "Pass 3: Fixing AST with inferred types\n";
fixParameterTypesInAST();
```

**Pass 4**: Re-infer
```cpp
cerr << "Pass 4: Re-inferring with fixed types\n";
```

### Type Debugging Helpers

```cpp
string typeToString(Type t) {
    switch (t) {
        case Type::Int: return "Int";
        case Type::Float: return "Float";
        case Type::String: return "String";
        case Type::IntArray: return "IntArray";
        // ...
    }
}

void dumpInferredTypes() {
    for (const auto& [name, sig] : userFunctions) {
        cerr << "Function " << name << ": (";
        for (auto t : sig.first) {
            cerr << typeToString(t) << ", ";
        }
        cerr << ") -> " << typeToString(sig.second) << "\n";
    }
}
```

---

## Phase 4: Code Generation Debugging

### Problem: JVM VerifyError

**Symptom**:
```
java.lang.VerifyError: Expecting to find integer on stack
```

**Solution 1**: Disassemble bytecode

```bash
./jvmbasic < program.bas
javap -v -c BasicProgram > bytecode.txt
less bytecode.txt
```

**What to Look For**:
- Stack depth mismatches
- Type mismatches (int vs float vs reference)
- Invalid jumps
- Missing stack map frames (if Java 7+)

**Solution 2**: Compare with javac

Write equivalent Java code and compare:
```bash
javac Test.java
javap -v -c Test > java_bytecode.txt
diff java_bytecode.txt our_bytecode.txt
```

### Common Bytecode Issues

**Issue**: Wrong load/store instruction
```java
// Arrays are references → use aload
aload_1   // Load array reference

// NOT
iload_1   // Wrong! Arrays aren't ints
```

**Issue**: Stack depth mismatch
```java
// BAD
iload_1    // Stack: [int]
iload_2    // Stack: [int, int]
fadd       // WRONG! fadd expects floats
```

**Fix**: Use correct instruction for type:
```java
iload_1
iload_2
iadd       // Correct for ints
```

**Issue**: Array index not int
```java
// BAD
fload_1    // float
faload     // ERROR: array index must be int
```

**Fix**: Convert first:
```java
fload_1
f2i        // Convert float → int
faload     // Now OK
```

---

## Phase 5: Runtime Debugging

### Problem: Runtime exceptions

**Symptom**:
```
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException
```

**Solution**: Add runtime checks

```basic
FUNCTION processArray(arr, size)
    IF size < 0.0 OR size > 1000.0 THEN
        PRINT "Invalid size:", size
        RETURN -1.0
    ENDIF
    
    LET i = 0.0
    WHILE i < size
        IF i < 0.0 THEN
            PRINT "BUG: negative index"
            RETURN -1.0
        ENDIF
        PRINT arr(i)
        LET i = i + 1.0
    ENDWHILE
ENDFUNCTION
```

### Problem: Infinite loops

**Solution**: Add loop counters

```basic
WHILE condition
    LET loopCount = loopCount + 1.0
    IF loopCount > 10000.0 THEN
        PRINT "WARNING: Loop exceeded 10000 iterations"
        ' Break or return
    ENDIF
    ' ... loop body ...
ENDWHILE
```

---

## Debugging Tools Summary

### 1. AST Dump
```bash
./jvmbasic-new --dump-ast < program.bas
```
**Use for**: Parser issues, structure verification

### 2. Bytecode Disassembly
```bash
javap -v -c BasicProgram
```
**Use for**: Code generation issues, JVM errors

### 3. JVM Verbose Output
```bash
java -verbose:class -verbose:gc BasicProgram
```
**Use for**: Class loading issues, memory problems

### 4. Compare with javac
```bash
javac equivalent.java
javap -v -c equivalent
# Compare with our output
```
**Use for**: Understanding correct bytecode

### 5. GDB (for compiler crashes)
```bash
gdb ./jvmbasic-new
run < crashing_program.bas
bt  # Backtrace
```
**Use for**: Segmentation faults, compiler crashes

---

## Systematic Debugging Process

### Step 1: Identify the Phase
- **Lexer**: Token/syntax errors
- **Parser**: Structure errors, crashes
- **Semantic**: Type errors
- **Codegen**: VerifyError, bytecode issues
- **Runtime**: Exceptions during execution

### Step 2: Isolate the Problem
Create minimal test case:
```basic
' Start with full program
' Remove lines until bug disappears
' Last removed line is the culprit
```

### Step 3: Add Instrumentation
```cpp
cerr << "DEBUG: At point X, variable = " << var << "\n";
```

### Step 4: Compare with Working Code
- Compare bytecode with javac
- Compare AST with known-good program
- Check against test suite

### Step 5: Fix and Verify
```bash
make clean && make
./test_runner.sh  # All tests should pass
```

---

## Common Error Patterns

### Pattern 1: Type Conversion Missing
```
VerifyError: Type mismatch
```
**Look for**: Missing `i2f`, `f2i`, etc.

### Pattern 2: Null Pointer
```
Segmentation fault in Parser::parseExpr
```
**Look for**: Dereferencing null `unique_ptr`

### Pattern 3: Stack Mismatch
```
VerifyError: Inconsistent stack height
```
**Look for**: Different paths leaving different stack depths

### Pattern 4: Wrong Descriptor
```
NoSuchMethodError
```
**Look for**: Method descriptor doesn't match actual signature

---

## Prevention Strategies

### 1. Write Tests First
```bash
# Before implementing feature
cat > tests/test_new_feature.bas << 'EOF'
PRINT "Testing new feature"
' ... test code ...
EOF
```

### 2. Incremental Development
- Add one feature at a time
- Test after each change
- Commit when tests pass

### 3. Use Assertions
```cpp
assert(expr.type != Type::Unknown && 
       "Expression must have type after inference");
```

### 4. Defensive Programming
```cpp
// Check preconditions
if (!userFunctions.count(name)) {
    error("Unknown function: " + name);
}
```

---

## Debug Build Flags

```makefile
# In Makefile for debug builds
DEBUG_FLAGS = -g -O0 -DDEBUG -fsanitize=address

jvmbasic-debug: *.cpp
	$(CXX) $(DEBUG_FLAGS) -o jvmbasic-debug *.cpp
```

**Benefits**:
- `-g`: Debug symbols
- `-O0`: No optimization (easier debugging)
- `-DDEBUG`: Enable debug code
- `-fsanitize=address`: Detect memory errors

---

## Quick Reference

| Problem | Tool | Command |
|---------|------|---------|
| Token errors | Token dump | Add `cerr` in lexer |
| Parse errors | AST dump | `--dump-ast` |
| Type errors | Type trace | Add `cerr` in semantic |
| VerifyError | Disassemble | `javap -v -c` |
| Crash | GDB | `gdb jvmbasic-new` |
| Wrong output | Compare javac | `javac` + `javap` |

---

## Summary

**Debugging is systematic**:
1. Identify phase (lexer/parser/semantic/codegen/runtime)
2. Use appropriate tool
3. Isolate minimal test case
4. Add instrumentation
5. Fix and verify

**Tools are your friends**:
- AST dump shows structure
- Bytecode disassembly shows generated code
- GDB finds crashes
- Comparison with javac reveals correct approach

**Prevention is better**:
- Write tests first
- Develop incrementally
- Use assertions
- Check preconditions

---

**Remember**: The best debugger is a clear head and systematic approach!

