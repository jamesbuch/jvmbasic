# JVM BASIC Feature Wishlist
**Last Updated**: After Phase 4 (Loops) completion  
**Current Version**: Includes arrays, functions, loops, and 40+ built-in functions

---

## IMMEDIATE PRIORITY (Phase 5)

### User-Defined Functions and Subroutines

**Functions (with return value)**:
```basic
FUNCTION add(a, b)
    RETURN a + b
ENDFUNCTION

LET result = add(5, 3)
PRINT result
```

**Subroutines/Procedures (void)**:
```basic
SUB greet(name)
    PRINT "Hello,", name
ENDSUB

CALL greet("Alice")
# or just:
greet("Alice")
```

**Implementation needs**:
- Multiple methods in generated class (not just main)
- Method descriptors with parameter types
- Return type inference or annotation
- invokestatic for calls within same class
- ireturn, freturn, areturn, return instructions
- Local variable scoping per function

**Array procedure calls** (for SORT, REVERSE, FILL):
```basic
SUB SORT(arr)
    # In-place sorting
ENDSUB

DIM nums(10) = 0
# ... fill nums ...
CALL SORT(nums)
```

---

## HIGH PRIORITY

### 1. Procedure Statements (for void functions)

Enable calling void functions as statements:
```basic
CALL SORT(myArray)
CALL REVERSE(names)
CALL FILL(scores, 0)
PRINT(x)    # PRINT as a procedure call
```

Required for:
- SORT, REVERSE, FILL array functions
- Future I/O operations
- Side-effect functions

---

### 2. User-Defined Types / Structures

**Simple RECORD/TYPE**:
```basic
TYPE Student
    name AS STRING
    age AS INT
    gpa AS FLOAT
ENDTYPE

DIM students(10) AS Student

LET students(0).name = "Alice"
LET students(0).age = 20
LET students(0).gpa = 3.8

PRINT students(0).name, students(0).gpa
```

**Implementation challenges**:
- Type definitions stored globally
- Field access syntax (dot notation)
- Struct types in JVM (create classes or use arrays)
- Memory layout for fields
- Initialization

**Alternative simpler approach**:
Parallel arrays:
```basic
DIM studentNames(10) = ""
DIM studentAges(10) = 0
DIM studentGPAs(10) = 0.0
```

---

### 3. Multi-Dimensional Arrays

```basic
DIM matrix(10, 10) = 0
LET matrix(5, 3) = 42
PRINT matrix(5, 3)

DIM board(8, 8) = ""   # Chess board
```

**Implementation**:
- Arrays of arrays in JVM
- Parse multi-index: `arr(i, j, k)`
- Nested newarray/anewarray
- Flattened access or true multi-dim

---

### 4. String Concatenation Operator

```basic
LET fullName = firstName + " " + lastName
LET greeting = "Hello, " + name + "!"
```

**Currently**: Must use separate variables or PRINT
**Implementation**: Overload `+` for String + String

---

## MEDIUM PRIORITY

### 5. Loop Control Statements

```basic
FOR i = 1 TO 100
    IF i == 50 THEN
        EXIT FOR     # Break out of loop
    ENDIF
    
    IF i % 2 == 0 THEN
        CONTINUE     # Skip to next iteration
    ENDIF
    
    PRINT i
NEXT i
```

**Implementation**:
- Track loop context (stack of loop labels)
- EXIT FOR/WHILE → goto end label
- CONTINUE → goto start label
- Requires loop context awareness

---

### 6. GOTO and Labels

```basic
10 PRINT "Start"
20 LET x = 5
30 IF x > 0 THEN GOTO 50
40 PRINT "Negative"
50 PRINT "Done"
```

Or modern syntax:
```basic
LABEL start
PRINT "Loop"
GOTO start
```

**Implementation**:
- Named labels map
- GOTO statement
- Line numbers (classic BASIC compat)

---

### 7. GOSUB and RETURN (Classic BASIC)

```basic
10 PRINT "Main"
20 GOSUB 100
30 PRINT "Back"
40 END

100 PRINT "Subroutine"
110 RETURN
```

**Implementation**:
- Return address stack
- JSR-like mechanism in JVM
- Or compile to actual methods

---

### 8. DATA and READ Statements

```basic
DATA 10, 20, 30, "Alice", "Bob"
READ x, y, z
READ name1, name2

PRINT x, y, z
PRINT name1, name2

RESTORE   # Reset DATA pointer
```

**Implementation**:
- Parse DATA into constant array
- READ fetches next values
- RESTORE resets index
- Type inference or conversion

---

### 9. File I/O

```basic
OPEN "data.txt" FOR INPUT AS #1
WHILE NOT EOF(1)
    INPUT #1, line
    PRINT line
WEND
CLOSE #1

OPEN "output.txt" FOR OUTPUT AS #2
PRINT #2, "Hello, file!"
CLOSE #2
```

**Implementation**:
- File handles (channel numbers)
- java.io.BufferedReader/Writer
- Open/Close tracking
- Input from file vs stdin

---

### 10. Error Handling

```basic
ON ERROR GOTO errorHandler

LET x = 10 / 0

LABEL errorHandler
PRINT "Error occurred!"
END
```

Or modern:
```basic
TRY
    LET x = 10 / 0
CATCH
    PRINT "Division by zero"
ENDTRY
```

**Implementation**:
- Exception table in class file
- Try-catch blocks
- Error handlers

---

## LOW PRIORITY / NICE TO HAVE

### 11. String Interpolation

```basic
LET name = "Alice"
LET age = 25
PRINT "Name: {name}, Age: {age}"
# or
PRINT $"Name: {name}, Age: {age}"
```

---

### 12. SELECT CASE (Switch)

```basic
SELECT CASE grade
    CASE "A"
        PRINT "Excellent"
    CASE "B", "C"
        PRINT "Good"
    CASE "D"
        PRINT "Passing"
    CASE ELSE
        PRINT "Failed"
ENDSELECT
```

**Implementation**:
- tableswitch or lookupswitch
- Multiple case values
- Range support (CASE 1 TO 10)

---

### 13. Inline Array Literals

```basic
LET nums = [1, 2, 3, 4, 5]
DIM names = ["Alice", "Bob", "Charlie"]
```

**Implementation**:
- Parse array literal syntax
- Infer size and type
- Generate initialization code

---

### 14. Range-Based FOR (FOR EACH)

```basic
DIM names(5) = ""
# ... fill names ...

FOR EACH name IN names
    PRINT name
NEXT
```

---

### 15. Constants

```basic
CONST PI = 3.14159
CONST MAX_STUDENTS = 100

LET circumference = 2 * PI * radius
```

**Implementation**:
- Parse CONST declarations
- Store in separate map
- Prevent reassignment
- Inline at compile time (constant folding)

---

### 16. Type Annotations

```basic
LET age AS INT = 25
DIM scores(10) AS FLOAT = 0.0

FUNCTION calculate(x AS FLOAT, y AS FLOAT) AS FLOAT
    RETURN x * y
ENDFUNCTION
```

---

### 17. Namespace/Module System

```basic
IMPORT "math_lib.bas"
IMPORT "string_utils.bas"

LET result = MathLib.complexFunc(x)
```

---

### 18. Debugger Support

- Add LineNumberTable attribute
- Source file attribute
- Local variable table
- Enable debugging with standard Java debuggers

---

### 19. Optimization Passes

- Constant folding
- Dead code elimination
- Common subexpression elimination
- Loop invariant code motion

---

### 20. Enhanced Type System

```basic
TYPE Color = INT   # Type alias
TYPE Result = (INT, STRING)   # Tuple
TYPE Status = "OK" | "ERROR" | "PENDING"   # Enum
```

---

## ADVANCED / FUTURE

### 21. Object-Oriented Features

```basic
CLASS Animal
    PRIVATE name AS STRING
    
    SUB init(n AS STRING)
        name = n
    ENDSUB
    
    FUNCTION getName() AS STRING
        RETURN name
    ENDFUNCTION
ENDCLASS

LET dog = NEW Animal("Fido")
PRINT dog.getName()
```

---

### 22. Lambda Functions / Closures

```basic
LET double = LAMBDA(x) x * 2
PRINT double(5)   # 10

LET nums = [1, 2, 3]
LET doubled = MAP(nums, LAMBDA(x) x * 2)
```

---

### 23. List Comprehensions

```basic
LET evens = [x FOR x IN range(10) IF x % 2 == 0]
```

---

### 24. Pattern Matching

```basic
MATCH value
    CASE 0:
        PRINT "Zero"
    CASE 1..10:
        PRINT "Small"
    CASE _:
        PRINT "Other"
ENDMATCH
```

---

### 25. Async/Concurrent

```basic
ASYNC SUB fetchData()
    # ...
ENDSUB

AWAIT fetchData()
```

---

## IMPLEMENTATION ROADMAP

### Phase 5: User Functions (NEXT)
- [ ] FUNCTION...RETURN...ENDFUNCTION
- [ ] SUB...ENDSUB
- [ ] CALL statements
- [ ] Parameter passing
- [ ] Return values
- [ ] Procedure calls for SORT, REVERSE, FILL

### Phase 6: Enhanced Arrays
- [ ] SORT procedure
- [ ] REVERSE procedure  
- [ ] FILL procedure
- [ ] Multi-dimensional arrays (2D, 3D)
- [ ] More array algorithms

### Phase 7: Classic BASIC Compat
- [ ] Line numbers
- [ ] GOTO and labels
- [ ] GOSUB/RETURN
- [ ] DATA/READ/RESTORE
- [ ] REM comments (already implicit via grammar)

### Phase 8: Advanced Control Flow
- [ ] EXIT FOR / EXIT WHILE
- [ ] CONTINUE
- [ ] SELECT CASE

### Phase 9: File I/O
- [ ] OPEN/CLOSE
- [ ] INPUT #channel
- [ ] PRINT #channel
- [ ] EOF() function

### Phase 10: Data Structures
- [ ] RECORD/TYPE user-defined types
- [ ] Nested types
- [ ] Type aliases

---

## NOTES FOR FUTURE DEVELOPMENT

**Keep it Simple**: The beauty of this compiler is its simplicity. Each feature should be carefully considered for complexity vs. value.

**Java Interop**: Consider allowing direct Java class usage:
```basic
LET list = JAVA("java.util.ArrayList", "new")
CALL list.add("Hello")
```

**Standard Library Expansion**: Before adding language features, expand BasicRuntime with more utilities.

**Performance**: Currently favoring simplicity over optimization. Future: add optimization passes.

**Compatibility**: Consider adding mode flags:
```bash
./jvmbasic --classic < program.bas    # Classic BASIC with line numbers
./jvmbasic --modern < program.bas     # Modern syntax (current)
```

---

## CURRENT STATUS

✅ **Complete**:
- Booleans, comparisons, control flow
- Arrays (1D, all types)
- 40+ built-in functions
- Loops (FOR, WHILE, DO-WHILE)
- I/O (PRINT, INPUT)
- Array utilities (MIN, MAX, SUM, UBOUND)

🚧 **In Progress**:
- Phase 5 planning (user functions)

📋 **Planned**: Everything above!

---

**The compiler is already incredibly powerful and usable!**
Further features are enhancements, not requirements.

