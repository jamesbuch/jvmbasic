# Phase 7 Code Generation - Implementation Plan

**Status**: Planning → Implementation  
**Goal**: Generate JVM bytecode for CLASS declarations

---

## JVM Bytecode Structure for Nested Classes

### Example: Simple Class

**BASIC Code**:
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
PRINT p.x
```

**Generated JVM Bytecode** (conceptual Java equivalent):
```java
public class BasicProgram {
    // Nested static class for Point
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
        System.out.println(p.x);
    }
}
```

---

## Implementation Strategy

### Option A: Single ClassFile with InnerClasses (RECOMMENDED)
Generate everything in one BasicProgram.class with:
- InnerClasses attribute listing nested classes
- Nested class bytecode embedded
- All in one .class file

**Pros**:
- Simpler (one file output)
- Easier debugging
- No file management

**Cons**:
- More complex class file structure
- Single file size limits (unlikely to hit)

### Option B: Separate .class Files
Generate:
- BasicProgram.class (main program)
- BasicProgram$Point.class (nested class)
- BasicProgram$BankAccount.class (nested class)

**Pros**:
- Standard JVM approach
- Each class is independent

**Cons**:
- Multiple file management
- Harder to implement
- Need to track output files

**Decision**: Start with Option A (single file), migrate to B if needed.

---

## Code Generation Steps

### Step 1: Generate Nested Class Structure

For each CLASS declaration, generate a nested static class:

**Bytecode Requirements**:
1. Add class to constant pool
2. Generate class structure in memory
3. Write nested class as separate method/section
4. Add InnerClasses attribute to main class

**JVM Class File Format** (nested class):
```
ClassFile {
    magic
    minor_version, major_version
    constant_pool
    access_flags = ACC_PUBLIC | ACC_STATIC
    this_class = "BasicProgram$Point"
    super_class = "java/lang/Object"
    interfaces_count = 0
    fields_count = 2  (x, y)
    fields[]
    methods_count = 1  (constructor)
    methods[]
    attributes_count = 0
}
```

**Challenge**: Current codegen.h generates a single ClassFile. We need to either:
- Generate multiple ClassFile objects
- Extend ClassFile to support nested classes
- Write nested class bytes directly

### Step 2: Generate Field Declarations

For each field in the CLASS:
```basic
PUBLIC x AS FLOAT      // → public float x;
PRIVATE balance AS FLOAT   // → private float balance;
```

**Bytecode**:
```
field {
    access_flags = ACC_PUBLIC or ACC_PRIVATE
    name_index = cp["x"]
    descriptor_index = cp["F"]  // F for float, Ljava/lang/String; for String
    attributes_count = 0
}
```

### Step 3: Generate Constructor (<init>)

**BASIC**:
```basic
PUBLIC SUB New(px AS FLOAT)
    x = px
END SUB
```

**JVM Bytecode**:
```
method {
    access_flags = ACC_PUBLIC
    name_index = cp["<init>"]
    descriptor_index = cp["(F)V"]  // (float) → void
    code {
        aload_0        // load 'this'
        invokespecial java/lang/Object/<init>  // super()
        aload_0        // load 'this'
        fload_1        // load parameter px
        putfield Point.x  // this.x = px
        return
    }
}
```

**Default Constructor** (if no New method):
```java
public Point() {
    super();
    this.x = 0.0f;
    this.y = 0.0f;
}
```

### Step 4: Generate Instance Methods

**BASIC**:
```basic
PUBLIC FUNCTION GetX() AS FLOAT
    RETURN x
END FUNCTION
```

**JVM Bytecode**:
```
method {
    access_flags = ACC_PUBLIC
    name_index = cp["GetX"]
    descriptor_index = cp["()F"]  // () → float
    code {
        aload_0        // load 'this'
        getfield Point.x
        freturn
    }
}
```

### Step 5: Generate NEW Expression

**BASIC**:
```basic
DIM p AS NEW Point(3.0, 4.0)
```

**JVM Bytecode**:
```
new BasicProgram$Point     // Create object
dup                         // Duplicate reference
fconst_3                    // Push 3.0f
ldc 4.0f                    // Push 4.0f
invokespecial Point/<init>  // Call constructor
astore_1                    // Store in local variable 1 (p)
```

### Step 6: Generate Method Calls

**BASIC**:
```basic
CALL obj.Method(arg)
```

**JVM Bytecode**:
```
aload_1                     // Load obj
fload_2                     // Load arg
invokevirtual Point/Method  // Call method
```

### Step 7: Generate Field Access

**BASIC**:
```basic
LET value = obj.field
```

**JVM Bytecode**:
```
aload_1                     // Load obj
getfield Point/field        // Get field
fstore_2                    // Store in value
```

**BASIC**:
```basic
LET obj.field = value
```

**JVM Bytecode**:
```
aload_1                     // Load obj
fload_2                     // Load value
putfield Point/field        // Set field
```

### Step 8: Generate ME Reference

**BASIC** (inside method):
```basic
LET ME.x = value
```

**JVM Bytecode**:
```
aload_0                     // Load 'this'
fload_1                     // Load value
putfield Point/x            // this.x = value
```

---

## Implementation Plan

### Phase 1: Basic Class Generation (4-5 hours)
1. Generate nested class structure
2. Generate field declarations
3. Generate default constructor
4. Test with empty class

### Phase 2: Constructor Implementation (3-4 hours)
5. Generate explicit constructors (SUB New)
6. Handle constructor parameters
7. Generate field initialization code
8. Test constructor with parameters

### Phase 3: Method Generation (3-4 hours)
9. Generate instance methods (SUB/FUNCTION)
10. Handle method parameters
11. Handle RETURN statements
12. Generate method bytecode

### Phase 4: NEW and Method Calls (2-3 hours)
13. Generate NEW expressions
14. Generate method call bytecode (invokevirtual)
15. Generate field access (getfield/putfield)
16. Handle ME reference (aload_0)

### Phase 5: Testing (2-3 hours)
17. Create comprehensive test suite
18. Debug bytecode issues
19. Verify all OOP features work

**Total**: 14-19 hours

---

## Next Step

Start with Phase 1: Generate a simple class with fields and default constructor.

**Test Case**:
```basic
CLASS Point
    PUBLIC x AS FLOAT
    PUBLIC y AS FLOAT
END CLASS

PRINT "Class generated!"
```

Expected bytecode: Nested static class Point with two public float fields.


