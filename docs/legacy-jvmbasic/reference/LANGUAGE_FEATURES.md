# JVM BASIC - Complete Language Feature Reference

**Version**: Phase 5 Complete  
**Status**: Production Ready  
**Date**: October 12, 2025

---

## 📋 Core Language Features

### Data Types ✅

**Primitive Types**:
- `Integer` / `Int` - 32-bit integer (JVM `int`)
- `Single` / `Float` - 32-bit floating point (JVM `float`)
- `Double` - 64-bit floating point (JVM `double`)
- `String` - Text strings
- `Boolean` / `Bool` - Boolean (true/false)

**Array Types**:
- `IntArray` - Integer arrays
- `FloatArray` - Float arrays
- `DoubleArray` - Double arrays
- `StringArray` - String arrays
- `BoolArray` - Boolean arrays

**Note:** `Single` and `Float` are synonymous (both map to Java's 32-bit `float`). Use `Double` for higher precision (64-bit).

**Type Inference**: Automatic at call sites and assignments

---

### Variables ✅

```basic
LET x = 42                    # Int
LET y = 3.14                  # Float
LET name = "Alice"            # String
LET active = true             # Bool
LET result = x < y            # Bool from comparison
```

**Arrays**:
```basic
DIM numbers(10) = 0.0         # Float array
DIM names(5) = ""             # String array
DIM flags(3) = false          # Bool array

LET numbers(0) = 100.0
LET names(0) = "Alice"
```

**Scope**:
- Global variables (in main program)
- Local variables (in functions/subs)
- Function parameters

---

### Operators ✅

**Arithmetic**:
- `+` Addition
- `-` Subtraction (binary and unary)
- `*` Multiplication
- `/` Division
- `MOD` Modulo

**Comparison** (return Bool):
- `<` Less than
- `>` Greater than
- `<=` Less than or equal
- `>=` Greater than or equal
- `==` Equal to
- `<>` Not equal to

**Type Promotion**:
- Int → Float (automatic)
- Epsilon comparison for floats

---

### Control Structures ✅

**IF Statement**:
```basic
IF condition THEN
    statements
ELSEIF condition THEN
    statements
ELSE
    statements
ENDIF
```

**FOR Loop**:
```basic
FOR i = start TO end
    statements
NEXT

FOR i = start TO end STEP increment
    statements
NEXT
```

**WHILE Loop**:
```basic
WHILE condition
    statements
ENDWHILE
```

**DO-WHILE Loop**:
```basic
DO
    statements
WHILE condition    # or UNTIL condition
```

---

### User-Defined Functions ✅

**Syntax**:
```basic
FUNCTION name(param1, param2, ...)
    statements
    RETURN expression
ENDFUNCTION
```

**Features**:
- Automatic type inference from call sites
- Local variables inside functions
- Recursive calls supported
- **Array parameters** (NEW!)
- Nested function calls
- Multiple return points

**Examples**:
```basic
FUNCTION factorial(n)
    IF n <= 1.0 THEN
        RETURN 1.0
    ELSE
        RETURN n * factorial(n - 1.0)
    ENDIF
ENDFUNCTION

FUNCTION sumArray(arr, size)
    LET total = 0.0
    LET i = 0.0
    WHILE i < size
        LET total = total + arr(i)
        LET i = i + 1.0
    ENDWHILE
    RETURN total
ENDFUNCTION
```

---

### SUB Procedures ✅

**Syntax**:
```basic
SUB name(param1, param2, ...)
    statements
ENDSUB

CALL name(arg1, arg2, ...)
```

**Features**:
- No return value
- Same parameter support as functions
- Array parameters supported
- Local variables

---

### Built-in Functions ✅

**Math Functions**:
```basic
ABS(x)        # Absolute value
SQR(x)        # Square root (SQRT alias)
POW(x, y)     # x to the power y
MIN(x, y)     # Minimum
MAX(x, y)     # Maximum
INT(x)        # Convert to integer
SIN(x)        # Sine
COS(x)        # Cosine
TAN(x)        # Tangent
EXP(x)        # e^x
LOG(x)        # Natural logarithm
LOG10(x)      # Base-10 logarithm
FLOOR(x)      # Floor
CEIL(x)       # Ceiling
ROUND(x)      # Round to nearest
```

**String Functions**:
```basic
LEN(s)        # Length
UPPER(s)      # Uppercase (UCASE alias)
LOWER(s)      # Lowercase (LCASE alias)
TRIM(s)       # Trim whitespace
LTRIM(s)      # Trim left
RTRIM(s)      # Trim right
LEFT(s, n)    # Left n characters
RIGHT(s, n)   # Right n characters
MID(s, start, len)  # Substring (SUBSTR alias)
INSTR(haystack, needle)  # Find substring position
CONTAINS(s, substr)  # Check if contains
REVERSE(s)    # Reverse string
SPACE(n)      # n spaces
STRING(n, s)  # Repeat s n times
ASC(s)        # ASCII code of first char
CHR(n)        # Character from ASCII
VAL(s)        # Parse string to number
STR(n)        # Number to string
ISNUM(s)      # Check if numeric
ISINT(s)      # Check if integer
```

**Random**:
```basic
RND           # Random 0.0-1.0
RANDOMIZE     # Seed random
```

**Constants**:
```basic
PI            # 3.14159...
E             # 2.71828...
```

---

### I/O ✅

**Output**:
```basic
PRINT expression1, expression2, ...   # With spaces
PRINT expression1; expression2        # Without spaces
PRINT                                 # Empty line
```

**Input**:
```basic
INPUT variable      # Read from stdin
```

---

## 🚀 Advanced Features

### Recursion ✅
- Forward declarations automatic
- Factorial, Fibonacci, GCD all work
- No depth limit (JVM stack-based)

### Type System ✅
- Call-site type inference
- Automatic promotion (Int→Float)
- Array type inference
- Multi-pass convergence

### Error Reporting ✅
- Line numbers in all errors
- Clear error messages
- Type mismatch details
- "Expected X but got Y" format

---

## 📝 Planned Features (Phase 6+)

### File I/O (Next)
```basic
OPEN "file.txt" FOR INPUT AS #1
OPEN "output.txt" FOR OUTPUT AS #2
READ #1, variable
WRITE #2, data
CLOSE #1
```

### Regular Expressions (Next)
```basic
IF REGEX("pattern", text) THEN
    LET groups = REGEXGROUPS("([a-z]+)", text)
    PRINT groups(1)  # First capture group
ENDIF
```

### User-Defined Types (Phase 6)
```basic
TYPE Person
    name AS STRING
    age AS INT
    email AS STRING
ENDTYPE

DIM person AS Person
LET person.name = "Alice"
```

### Object-Oriented Features (Phase 7)
```basic
CLASS Account
    PRIVATE balance AS FLOAT
    
    METHOD deposit(amount)
        LET balance = balance + amount
    ENDMETHOD
    
    METHOD getBalance()
        RETURN balance
    ENDMETHOD
ENDCLASS
```

### Collections (Phase 8)
```basic
DIM list AS List(Float)
CALL list.add(42.0)
PRINT list.get(0)

DIM map AS Map(String, Int)
CALL map.put("answer", 42)
```

### Networking (Phase 9)
```basic
LET sock = SOCKET("localhost", 8080)
SEND sock, "GET / HTTP/1.0\r\n\r\n"
LET response = RECEIVE(sock, 1024)
CLOSE sock
```

---

## 🎯 What Makes This a Serious Language

### Already Have ✅
- Complete type system
- Functions with recursion
- Arrays and array parameters
- Professional error reporting
- Clean modular architecture
- JVM bytecode (runs anywhere)
- Fast execution (JIT compiled)

### Need to Add ⏳
1. **File I/O** (critical for real programs)
2. **Regular expressions** (text processing)
3. **Better string manipulation** (split, join, format)
4. **Exception handling** (TRY/CATCH)
5. **Modules/imports** (code organization)
6. **Standard library** (data structures)

---

## 🚀 Priority Order

**Immediate** (2-4 hours):
1. File I/O functions
2. String: SPLIT, JOIN, FORMAT
3. Regex: MATCH, GROUPS, REPLACE

**Short-term** (8-12 hours):
4. Exception handling
5. More built-in functions
6. Module system

**Medium-term** (20-30 hours):
7. User-defined types (structs)
8. Basic OOP (classes, methods)
9. Collections (List, Map, Set)

**Long-term** (40+ hours):
10. Networking
11. Full generics
12. Advanced OOP features

---

## 📚 Current Capabilities

**Can Build**:
- Algorithms (sorting, searching)
- Math computations
- Text processing
- Data analysis
- Recursive problems
- Array manipulation

**Cannot Yet Build** (needs Phase 6):
- File processors
- Web scrapers (needs networking)
- Database tools (needs I/O)
- Complex data structures (needs types/collections)

---

**Next**: Implement File I/O and Regex to make it truly practical!

