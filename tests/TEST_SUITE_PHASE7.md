# Phase 7 OOP Test Suite

**Status**: Tests created, codegen pending  
**Location**: `/home/james/Downloads/jvmbasic/tests/`

---

## Test Files

### 1. test_class_basic.bas
**Focus**: Basic CLASS declaration with fields  
**Tests**:
- CLASS keyword parsing
- PUBLIC field declarations
- END CLASS
- Field types (FLOAT)

**Expected** (when codegen works):
- Compiles without errors
- Prints status message

---

### 2. test_class_constructor.bas
**Focus**: Constructors (SUB New)  
**Tests**:
- PUBLIC SUB New syntax
- Constructor parameters
- Field initialization in constructor
- DIM AS NEW ClassName(args)

**Expected** (when codegen works):
- Object creation succeeds
- Fields initialized with constructor values
- Can access public fields

---

### 3. test_class_methods.bas
**Focus**: Instance methods  
**Tests**:
- SUB methods (void)
- FUNCTION methods (with return)
- Method calls via CALL statement
- Method calls in expressions
- Field access within methods

**Expected** (when codegen works):
- Method calls execute
- FUNCTION returns correct value
- Private field accessible within class

---

### 4. test_class_encapsulation.bas
**Focus**: Access control  
**Tests**:
- PRIVATE fields
- PUBLIC fields
- PRIVATE field access only via methods
- PUBLIC methods

**Expected** (when codegen works):
- Can call public methods
- Can access public fields
- Cannot access private fields (compile error)

---

### 5. test_class_multiple.bas
**Focus**: Multiple classes in one program  
**Tests**:
- Multiple CLASS declarations
- Different classes with different methods
- Creating instances of multiple classes
- No naming conflicts

**Expected** (when codegen works):
- Both classes compile
- Can create instances of both
- Methods work independently

---

### 6. test_class_me_reference.bas
**Focus**: ME/this reference  
**Tests**:
- ME keyword
- ME.field access
- ME in methods
- Parameter shadowing resolution

**Expected** (when codegen works):
- ME resolves to current instance
- Can disambiguate between parameter and field
- ME.field accesses instance field

---

### 7. test_class_comments.bas
**Focus**: Modern comment syntax  
**Tests**:
- Apostrophe (') comments
- Inline comments
- Comments in CLASS declarations
- Both REM and ' working together

**Expected**:
- ✅ ALREADY WORKING (tested)
- Both comment styles work
- Inline comments work

---

## Running Tests

### Current (Parse Only)
```bash
# Test parsing with AST dump
for test in tests/test_class*.bas; do
    echo "Testing: $test"
    ./jvmbasic --dump-ast < "$test" > /dev/null && echo "✓ PARSE OK" || echo "✗ PARSE FAIL"
done
```

### When Codegen Complete
```bash
# Test compilation and execution
for test in tests/test_class*.bas; do
    echo "Testing: $test"
    ./jvmbasic < "$test" && java BasicProgram && echo "✓ PASS" || echo "✗ FAIL"
done
```

---

## Parsing Status

All Phase 7 tests **parse successfully** ✅

```bash
$ for test in tests/test_class*.bas; do ./jvmbasic --dump-ast < "$test" > /dev/null && echo "$test: ✓"; done

test_class_basic.bas: ✓
test_class_comments.bas: ✓
test_class_constructor.bas: ✓
test_class_encapsulation.bas: ✓
test_class_me_reference.bas: ✓
test_class_methods.bas: ✓
test_class_multiple.bas: ✓
```

---

## Expected Behavior After Codegen

### test_class_constructor.bas
```
Output: Point: (3.0, 4.0)
```

### test_class_methods.bas
```
Output: Count: 2.0
```

### test_class_encapsulation.bas
```
Output: Alice has balance: 1500.0
```

### test_class_multiple.bas
```
Output: 
Point distance: 5.0
Circle area: 78.53975
```

### test_class_me_reference.bas
```
Output: Bob is 26.0 years old
```

---

## Test Coverage

| Feature | Test File | Parse | Codegen |
|---------|-----------|-------|---------|
| CLASS declaration | test_class_basic | ✅ | ⏳ |
| Constructors | test_class_constructor | ✅ | ⏳ |
| Instance methods | test_class_methods | ✅ | ⏳ |
| PRIVATE/PUBLIC | test_class_encapsulation | ✅ | ⏳ |
| Multiple classes | test_class_multiple | ✅ | ⏳ |
| ME reference | test_class_me_reference | ✅ | ⏳ |
| Comments (') | test_class_comments | ✅ | ✅ |

---

## Next Session Focus

**Code Generation Tasks**:
1. Generate nested static classes
2. Generate field declarations
3. Generate constructors (<init>)
4. Generate instance methods
5. Handle NEW operator
6. Handle method calls (invokevirtual)
7. Handle field access (getfield/putfield)
8. Handle ME reference (aload_0)

**Estimated**: 14-19 hours

---

**All tests created and ready for codegen implementation!** 🎯


