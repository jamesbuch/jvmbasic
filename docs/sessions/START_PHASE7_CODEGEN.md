# Phase 7 Code Generation - Start Here

**Date**: October 13, 2025  
**Branch**: phase7-oop  
**Status**: Parsing Complete ✅ → Code Generation Next

---

## 🎯 Quick Status

**What's Done**:
- ✅ Modular compiler architecture (7 clean components)
- ✅ All Phase 7 parsing (CLASS, NEW, methods, ME)
- ✅ All Phase 7 AST types
- ✅ AST printer updated
- ✅ Semantic analyzer updated
- ✅ 7 comprehensive test cases created
- ✅ Documentation complete

**What's Needed**:
- ⏳ Generate nested static classes in JVM bytecode
- ⏳ Generate constructors (<init> methods)
- ⏳ Generate instance methods
- ⏳ Handle NEW operator
- ⏳ Handle method calls (invokevirtual)
- ⏳ Handle field access (getfield/putfield)

---

## 🏗️ Architecture Overview

### Modular Components

```
Source (.bas)
    ↓
[lexer.cpp] → Tokens
    ↓
[parser.cpp] → AST  ← YOU ARE HERE
    ↓
[semantic.cpp] → Validated AST
    ↓
[codegen.h] → Bytecode (.class)  ← NEXT STEP
```

**All parsing works!** Test with:
```bash
./jvmbasic --dump-ast < tests/test_class_basic.bas
```

---

## 📊 Test Status

**Current Baseline**: 26/49 tests passing  
**Phase 7 Tests**: 7/7 parse correctly (codegen not implemented)

```bash
# Verify parsing works
for test in tests/test_class*.bas; do
    ./jvmbasic --dump-ast < "$test" > /dev/null && echo "✓" || echo "✗"
done
```

**Expected**: 7/7 ✓

---

## 🎯 Phase 7 Codegen Task

### Goal

Generate JVM bytecode for CLASS declarations using nested static classes.

### Example

**Input** (`test_class_constructor.bas`):
```basic
CLASS Point
    PUBLIC x AS FLOAT
    PUBLIC y AS FLOAT
    
    PUBLIC SUB New(px AS FLOAT, py AS FLOAT)
        x = px
        y = py
    END SUB
END CLASS

DIM p AS NEW Point(3.0, 4.0)
PRINT "Point: ("; p.x; ", "; p.y; ")"
```

**Expected Output**:
```
Point: (3.0, 4.0)
```

**JVM Bytecode** (Java equivalent):
```java
public class BasicProgram {
    public static class Point {
        public float x;
        public float y;
        
        public Point(float px, float py) {
            this.x = px;
            this.y = py;
        }
    }
    
    public static void main(String[] args) {
        Point p = new Point(3.0f, 4.0f);
        System.out.println("Point: (" + p.x + ", " + p.y + ")");
    }
}
```

---

## 🔧 Implementation Approach

### Option A: Single .class File (RECOMMENDED)

Generate everything in `BasicProgram.class`:
- Main class with main() method
- Nested static classes for each CLASS declaration
- InnerClasses attribute

**Pros**:
- Simpler (one output file)
- All in codegen.h
- Easier to debug

**Cons**:
- More complex class file structure
- Need InnerClasses attribute

### Option B: Separate .class Files

Generate multiple files:
- `BasicProgram.class` (main)
- `BasicProgram$Point.class` (nested)
- `BasicProgram$BankAccount.class` (nested)

**Pros**:
- Standard JVM approach
- Each class independent

**Cons**:
- Multiple file management
- More complex implementation

**Recommendation**: Start with Option A, migrate to B if needed.

---

## 📝 Current AST Structure

### ClassDecl

```cpp
struct ClassDecl {
    string name;               // "POINT", "BANKACCOUNT" (uppercase)
    vector<Field> fields;       // PUBLIC x AS FLOAT, etc.
    vector<MethodDecl> methods; // Constructors and methods
};
```

### MethodDecl

```cpp
struct MethodDecl {
    string name;                // "New", "Deposit", "GetBalance"
    bool isPublic;              // true for PUBLIC, false for PRIVATE
    bool isConstructor;         // true if name == "New"
    vector<Param> params;       // (name AS STRING, amount AS FLOAT)
    Type returnType;            // Float for SUB, actual type for FUNCTION
    vector<StmtPtr> body;       // Method statements
};
```

### NewExpr

```cpp
struct NewExpr {
    string className;           // "POINT" (uppercase)
    vector<ExprPtr> args;       // Constructor arguments
};
```

### MethodCallExpr

```cpp
struct MethodCallExpr {
    ExprPtr object;             // The object instance
    string methodName;          // "Deposit", "GetBalance"
    vector<ExprPtr> args;       // Method arguments
};
```

---

## 🛠️ Code Generation Steps

### Step 1: Detect CLASS Declarations

In `ClassFile::generate()`:

```cpp
for (const auto& decl : declarations) {
    if (decl->kind == DeclKind::Class) {
        const ClassDecl& cd = get<ClassDecl>(decl->data);
        generateNestedClass(cd);  // NEW METHOD
    }
}
```

### Step 2: Generate Nested Class

**File**: `codegen.h` (new method)

```cpp
void generateNestedClass(const ClassDecl& cd) {
    // 1. Add class to constant pool
    u2 class_idx = cp.addClass(cp.addUtf8("BasicProgram$" + cd.name));
    
    // 2. Generate fields
    for (const Field& field : cd.fields) {
        // Add field_info structure
    }
    
    // 3. Generate constructor
    for (const MethodDecl& method : cd.methods) {
        if (method.isConstructor) {
            generateConstructor(cd, method);
        } else {
            generateInstanceMethod(cd, method);
        }
    }
    
    // 4. Add to InnerClasses attribute
}
```

### Step 3: Generate Constructor Bytecode

```cpp
void generateConstructor(const ClassDecl& cd, const MethodDecl& md) {
    // Method name: <init>
    // Descriptor: (parameters)V
    // Code:
    //   aload_0
    //   invokespecial java/lang/Object/<init>
    //   <field initialization>
    //   return
}
```

### Step 4: Handle NEW Operator

In `load()` method:

```cpp
case ExprKind::NewExpr: {
    const NewExpr& ne = get<NewExpr>(e.data);
    
    // new ClassName
    u2 class_idx = cp.addClass(cp.addUtf8("BasicProgram$" + ne.className));
    code.push_back(0xBB);  // new
    code.push_back(class_idx >> 8);
    code.push_back(class_idx & 0xFF);
    
    // dup
    code.push_back(0x59);
    
    // Load constructor arguments
    for (const auto& arg : ne.args) {
        load(*arg, varIdx);
    }
    
    // invokespecial <init>
    // ... methodref to constructor ...
    
    break;
}
```

### Step 5: Handle Method Calls

```cpp
case ExprKind::MethodCall: {
    const MethodCallExpr& mce = get<MethodCallExpr>(e.data);
    
    // Load object
    load(*mce.object, varIdx);
    
    // Load arguments
    for (const auto& arg : mce.args) {
        load(*arg, varIdx);
    }
    
    // invokevirtual ClassName/methodName
    // ... methodref ...
    
    break;
}
```

---

## 🧪 Testing Plan

### Test 1: Empty Class

```basic
CLASS Point
    PUBLIC x AS FLOAT
END CLASS
PRINT "OK"
```

**Verify**: Compiles, runs, prints "OK"

### Test 2: Constructor

```basic
CLASS Point
    PUBLIC x AS FLOAT
    PUBLIC SUB New(px AS FLOAT)
        x = px
    END SUB
END CLASS
DIM p AS NEW Point(5.0)
PRINT "OK"
```

**Verify**: Object created, no crashes

### Test 3: Field Access

```basic
DIM p AS NEW Point(3.0, 4.0)
PRINT p.x
```

**Verify**: Prints "3.0"

### Test 4: Methods

```basic
CLASS Counter
    PRIVATE count AS FLOAT
    PUBLIC SUB Increment()
        count = count + 1.0
    END SUB
    PUBLIC FUNCTION GetCount() AS FLOAT
        RETURN count
    END FUNCTION
END CLASS
DIM c AS NEW Counter()
CALL c.Increment()
PRINT c.GetCount()
```

**Verify**: Prints "1.0"

---

## 📁 Files to Modify

### Primary
- **codegen.h** (1491 lines) - Add nested class generation

### Testing
- Run `tests/test_class_*.bas` files
- Verify with `javap -v BasicProgram` to inspect bytecode

### Documentation
- Update when codegen works
- Document bytecode structure
- Add usage examples

---

## 🎓 Resources

### JVM Bytecode References

**Nested Classes**:
```
InnerClasses {
    #1 = class BasicProgram$Point
    outer_class_info_index = #2 (BasicProgram)
    inner_class_access_flags = ACC_PUBLIC | ACC_STATIC
}
```

**Constructor**:
```
Method: <init>
Descriptor: (FF)V
Code:
  0: aload_0
  1: invokespecial java/lang/Object/<init>
  4: aload_0
  5: fload_1
  6: putfield Point.x
  9: aload_0
  10: fload_2
  11: putfield Point.y
  14: return
```

### Helpful Commands

```bash
# Generate Java class for reference
javac Point.java
javap -v -c Point

# Compare with our output
./jvmbasic < test.bas
javap -v -c BasicProgram
```

---

## ⏱️ Time Estimate

| Task | Time | Difficulty |
|------|------|-----------|
| Research nested classes | 2h | Medium |
| Generate class structure | 3h | Hard |
| Generate constructors | 3h | Hard |
| Generate methods | 4h | Hard |
| Handle NEW/method calls | 3h | Medium |
| Debug and test | 4h | Medium |
| **Total** | **19h** | **Hard** |

---

## ✅ Success Criteria

Phase 7 complete when:

- [ ] All 7 Phase 7 tests compile
- [ ] All 7 Phase 7 tests run correctly
- [ ] Baseline tests still pass (26/49 minimum)
- [ ] Can create objects with NEW
- [ ] Can call methods
- [ ] Can access public fields
- [ ] Private fields are inaccessible
- [ ] ME reference works
- [ ] No bytecode verification errors

---

## 🚀 To Start Code Generation

1. Read `docs/planning/PHASE7_CODEGEN_PLAN.md` (detailed plan)
2. Review JVM spec for nested classes
3. Study current `codegen.h` structure
4. Create helper: `generateNestedClass(ClassDecl&)`
5. Test incrementally with each test case

---

## 📞 Quick Commands

```bash
# Build
make clean && make

# Test parsing (all should pass)
for t in tests/test_class*.bas; do ./jvmbasic --dump-ast < "$t" > /dev/null && echo "✓"; done

# Test one file
./jvmbasic < tests/test_class_basic.bas && java BasicProgram

# Inspect bytecode
javap -v -c -private BasicProgram

# Debug
gdb ./jvmbasic
```

---

## 💡 Key Insights

1. **Parsing is Complete**: All Phase 7 syntax works
2. **AST is Perfect**: Visualizes correctly with --dump-ast
3. **Tests are Ready**: 7 tests waiting for codegen
4. **Clean Architecture**: Easy to extend codegen.h
5. **No Regressions**: 26/49 tests still pass

---

## 🎉 Session Achievements

Today we:
1. ✅ Refactored to modular architecture
2. ✅ Implemented all Phase 7 parsing
3. ✅ Created comprehensive test suite
4. ✅ Updated all documentation
5. ✅ Maintained baseline (26/49 tests)

**Remaining**: Code generation (14-19 hours estimated)

---

**You're ready to implement nested class bytecode generation!** 🚀

**Start with**: Simple class with fields only (test_class_basic.bas)  
**Then add**: Constructors, methods, NEW, method calls, field access  
**Finally**: ME reference and complete test suite


