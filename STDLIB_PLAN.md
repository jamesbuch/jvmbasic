# Standard Library Functions Plan

## Phase 3A: Math Functions (Priority 1)

### Basic Math
- `ABS(x)` - Absolute value (works for Int and Float)
- `SGN(x)` - Sign of number (-1, 0, 1)
- `INT(x)` - Integer part (floor for positive, ceiling for negative)
- `RND()` - Random number 0.0 to 1.0
- `RND(n)` - Random integer 0 to n-1

### Trigonometry
- `SIN(x)` - Sine (radians)
- `COS(x)` - Cosine (radians)
- `TAN(x)` - Tangent (radians)
- `ASIN(x)` - Arc sine
- `ACOS(x)` - Arc cosine
- `ATAN(x)` - Arc tangent
- `ATAN2(y, x)` - Arc tangent of y/x

### Powers and Roots
- `SQR(x)` or `SQRT(x)` - Square root
- `POW(x, y)` - x raised to power y
- `EXP(x)` - e^x
- `LOG(x)` - Natural logarithm
- `LOG10(x)` - Base-10 logarithm

### Rounding
- `ROUND(x)` - Round to nearest integer
- `CEIL(x)` - Round up
- `FLOOR(x)` - Round down

### Constants (as functions for simplicity)
- `PI()` - Returns 3.14159...
- `E()` - Returns 2.71828...

---

## Phase 3B: String Functions (Priority 1)

### Length and Character Access
- `LEN(s)` - String length
- `ASC(s)` - ASCII value of first character
- `CHR(n)` - Character from ASCII value

### Substring Operations
- `LEFT(s, n)` - First n characters
- `RIGHT(s, n)` - Last n characters
- `MID(s, start, len)` - Substring from start, len characters
- `SUBSTR(s, start, len)` - Alias for MID

### Case Conversion
- `UPPER(s)` or `UCASE(s)` - Convert to uppercase
- `LOWER(s)` or `LCASE(s)` - Convert to lowercase

### String Manipulation
- `TRIM(s)` - Remove leading/trailing whitespace
- `LTRIM(s)` - Remove leading whitespace
- `RTRIM(s)` - Remove trailing whitespace
- `REVERSE(s)` - Reverse string

### String Search
- `INSTR(haystack, needle)` - Find position of substring (0-based or 1-based?)
- `CONTAINS(haystack, needle)` - Returns boolean

### Type Conversion
- `STR(n)` - Convert number to string
- `VAL(s)` - Convert string to number (Int or Float)

### String Building
- `SPACE(n)` - String of n spaces
- `STRING(n, c)` - Repeat character c, n times

---

## Phase 4: Utility Functions (Priority 2)

### Type Checking
- `ISNUM(s)` - Check if string is numeric
- `ISINT(s)` - Check if string is integer
- `TYPEOF(x)` - Return type as string ("Int", "Float", "String", "Bool")

### Array Utilities
- `UBOUND(arr)` - Upper bound of array (size - 1)
- `LBOUND(arr)` - Lower bound of array (always 0 in our impl)
- `MIN(a, b, ...)` - Minimum of values
- `MAX(a, b, ...)` - Maximum of values
- `SUM(arr)` - Sum all array elements
- `AVG(arr)` - Average of array elements

### Date/Time (if we want to add)
- `TIME()` - Current time as string
- `DATE()` - Current date as string
- `TIMESTAMP()` - Unix timestamp

### I/O Utilities
- `INPUT(prompt)` - INPUT with prompt in one statement
- `PRINT_AT(row, col, text)` - Position cursor (terminal dependent)

---

## Implementation Strategy

### 1. Create BasicRuntime Helper Class

```java
package jvmbasic;

public class BasicRuntime {
    // Math functions
    public static float abs_f(float x) { return Math.abs(x); }
    public static int abs_i(int x) { return Math.abs(x); }
    public static float sqrt(float x) { return (float)Math.sqrt(x); }
    public static float sin(float x) { return (float)Math.sin(x); }
    public static float cos(float x) { return (float)Math.cos(x); }
    public static float pow(float x, float y) { return (float)Math.pow(x, y); }
    public static float rnd() { return (float)Math.random(); }
    public static int rnd_i(int n) { return (int)(Math.random() * n); }
    
    // String functions
    public static int len(String s) { return s.length(); }
    public static String left(String s, int n) { 
        return s.substring(0, Math.min(n, s.length())); 
    }
    public static String right(String s, int n) { 
        int len = s.length();
        return s.substring(Math.max(0, len - n)); 
    }
    public static String mid(String s, int start, int len) {
        return s.substring(start, Math.min(start + len, s.length()));
    }
    public static String upper(String s) { return s.toUpperCase(); }
    public static String lower(String s) { return s.toLowerCase(); }
    
    // ... more functions
}
```

### 2. Compilation Step

Create BasicRuntime.java, compile it, and package with BasicProgram.class:
```bash
javac BasicRuntime.java
# Distribute BasicRuntime.class with compiled programs
```

### 3. Compiler Changes

**Add to constant pool:**
- BasicRuntime class reference
- Method references for each function

**Parser changes:**
- Recognize function call syntax: `FUNCTION_NAME(args)`
- Parse argument lists
- Type checking for function arguments
- Return type inference

**Codegen:**
- Load arguments
- `invokestatic jvmbasic/BasicRuntime.functionName`
- Result on stack

### 4. Function Registry

```cpp
struct FunctionSig {
    string name;
    vector<Type> paramTypes;
    Type returnType;
};

map<string, FunctionSig> builtinFunctions = {
    {"ABS", {{Type::Float}, Type::Float}},
    {"SQR", {{Type::Float}, Type::Float}},
    {"SIN", {{Type::Float}, Type::Float}},
    {"LEN", {{Type::String}, Type::Int}},
    // ... etc
};
```

---

## Testing Strategy

### Test 1: Basic Math
```basic
PRINT ABS(-5)
PRINT SQR(16)
PRINT SIN(0)
LET x = POW(2, 3)
PRINT x
```

### Test 2: String Functions
```basic
LET s = "Hello"
PRINT LEN(s)
PRINT LEFT(s, 3)
PRINT RIGHT(s, 3)
PRINT UPPER(s)
```

### Test 3: Combined
```basic
DIM nums(5) = 0
LET nums(0) = ABS(-10)
LET nums(1) = INT(3.7)
PRINT nums(0), nums(1)

LET name = "alice"
PRINT UPPER(name)
```

---

## Priority Order

1. **Immediate (This Session)**:
   - ABS, SQR, INT, RND
   - LEN, LEFT, RIGHT, UPPER, LOWER, MID
   - SIN, COS, TAN (basic trig)
   - POW

2. **Next Session**:
   - More math: ROUND, CEIL, FLOOR, LOG
   - More strings: TRIM, INSTR, CHR, ASC
   - Type conversion: STR, VAL

3. **Later**:
   - Array utilities
   - Date/time functions
   - Advanced features

---

## Notes for Implementation

- **Overloading**: ABS should work for Int and Float - create abs_i and abs_f
- **Type promotion**: INT(3.7) returns Int, but can accept Float
- **Error handling**: What happens with domain errors? (e.g., SQR(-1))
- **Case sensitivity**: Function names should be case-insensitive (like keywords)
- **Naming**: Use traditional BASIC names (SQR not SQRT, UCASE not UPPERCASE)

---

## Documentation Updates Needed

- README: Add "Built-in Functions" section
- extending.md: Document how to add new functions
- Create FUNCTIONS.md: Complete reference of all functions

