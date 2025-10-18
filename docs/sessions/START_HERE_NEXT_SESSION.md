# ⚡ START HERE - Next Session Quick Guide

**Date**: October 13, 2025  
**Branch**: phase7-oop  
**Focus**: Phase 7 Code Generation

---

## 🎯 Current Status

**Phase 7 Progress**: 75% Complete

✅ **DONE**:
- Modular architecture (7 components)
- All parsing (CLASS, NEW, methods, ME)
- All AST types
- 7 test cases
- Complete documentation

⏳ **TODO**:
- Code generation (nested classes)
- Make tests execute (not just parse)

---

## 🚀 Quick Verification (2 minutes)

```bash
cd /home/james/Downloads/jvmbasic
git branch  # Should show: * phase7-oop

# Build
make clean && make

# Test parsing works (all should ✓)
for t in tests/test_class*.bas; do
    ./jvmbasic --dump-ast < "$t" > /dev/null && echo "✓" || echo "✗"
done

# Test baseline
./test_runner.sh  # Should show: 26/49 passing
```

**Expected**:
- ✅ Clean build
- ✅ 7/7 Phase 7 tests parse
- ✅ 26/49 baseline tests pass

---

## 📖 Read These First (15 minutes)

1. **docs/sessions/START_PHASE7_CODEGEN.md** (5 min)
   - Overview of what's needed
   - Code generation task breakdown

2. **docs/planning/PHASE7_CODEGEN_PLAN.md** (10 min)
   - Detailed implementation strategy
   - JVM bytecode examples
   - Step-by-step guide

---

## 🎯 Your Task: Code Generation

**Goal**: Make Phase 7 tests actually compile and run

**File to Modify**: `codegen.h` (1491 lines)

**What to Add**:
1. Nested class generation
2. Field declarations (public/private)
3. Constructors (<init> methods)
4. Instance methods
5. NEW operator handling
6. Method call handling
7. Field access (getfield/putfield)
8. ME reference (aload_0)

**Estimated Time**: 14-19 hours

---

## 🧪 Testing Workflow

### Start Simple

**Test 1**: Empty class
```bash
./jvmbasic < tests/test_class_basic.bas && java BasicProgram
```

**Test 2**: Constructor
```bash
./jvmbasic < tests/test_class_constructor.bas && java BasicProgram
```

Work through all 7 tests incrementally.

---

## 📊 Example Syntax (All Parses Correctly!)

```basic
' This syntax is fully supported in parser:
CLASS BankAccount
    PRIVATE balance AS FLOAT
    PUBLIC owner AS STRING
    
    PUBLIC SUB New(name AS STRING, initial AS FLOAT)
        owner = name
        balance = initial
    END SUB
    
    PUBLIC FUNCTION GetBalance() AS FLOAT
        RETURN balance
    END FUNCTION
END CLASS

DIM account AS NEW BankAccount("Alice", 1000.0)
CALL account.Deposit(500.0)
PRINT account.owner; " has $"; account.GetBalance()
```

**Try it**:
```bash
./jvmbasic --dump-ast < /tmp/example.bas
```

You'll see perfect AST! Just needs codegen.

---

## 🏗️ Architecture Quick Ref

```
Compiler Pipeline:
  lexer.cpp   → Tokens       ✅ Done
  parser.cpp  → AST          ✅ Done
  semantic.cpp → Validated   ✅ Done (basic)
  codegen.h   → Bytecode     ⏳ Needs nested classes
```

**All infrastructure ready** - just need nested class generation in codegen.h!

---

## 🎓 Key Files

| File | Purpose | Status |
|------|---------|--------|
| codegen.h | Bytecode generation | ⏳ Needs Phase 7 |
| tests/test_class_*.bas | Test cases | ✅ Ready |
| docs/sessions/START_PHASE7_CODEGEN.md | Handoff guide | ✅ Read this |
| docs/planning/PHASE7_CODEGEN_PLAN.md | Implementation plan | ✅ Reference |

---

## 💡 Hints

### Generate Java Reference

Create a Java class and inspect its bytecode:

```java
// Point.java
public class BasicProgram {
    public static class Point {
        public float x;
        public float y;
        
        public Point(float px, float py) {
            this.x = px;
            this.y = py;
        }
    }
}
```

```bash
javac Point.java
javap -v -c -private BasicProgram
# Study the output - this is what we need to generate!
```

### Study Existing Codegen

Current `codegen.h` already generates:
- Functions → static methods ✅
- Structs (TYPE) → Object[] arrays ✅
- Arrays → native arrays ✅

Pattern: Look at how `generateFunction()` works, apply to `generateNestedClass()`

---

## ⏰ Timeline

**Session 1** (Today) - 10 hours:
- ✅ Modular refactoring
- ✅ Complete parsing
- ✅ Documentation

**Session 2** (Next) - 14-19 hours:
- ⏳ Code generation
- ⏳ Testing
- ⏳ Debug

**Total Phase 7**: 24-29 hours

---

## ✅ Success Criteria

Phase 7 complete when:

- [ ] All 7 Phase 7 tests compile
- [ ] All 7 Phase 7 tests execute correctly
- [ ] Baseline still passes (26/49 minimum)
- [ ] Can create objects: `NEW ClassName(args)`
- [ ] Can call methods: `obj.Method()`
- [ ] Can access fields: `obj.field`
- [ ] PRIVATE fields are inaccessible
- [ ] ME reference works in methods

---

## 🚦 You're Ready!

Everything is set up perfectly:
- ✅ Clean modular codebase
- ✅ All parsing works
- ✅ Tests are ready
- ✅ Documentation complete
- ✅ No blockers

**Just need to implement nested class bytecode generation in codegen.h!**

---

**Next Action**: 
1. Read `docs/sessions/START_PHASE7_CODEGEN.md`
2. Read `docs/planning/PHASE7_CODEGEN_PLAN.md`
3. Start implementing `generateNestedClass()` in `codegen.h`

**Good luck!** 🚀



