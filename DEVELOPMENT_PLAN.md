# JVM BASIC Development Plan

## Current Status (development-1 branch)
✅ Boolean type with true/false literals  
✅ Comparison operators: `< > <= >= == <>`  
✅ IF/THEN/ELSEIF/ELSE/ENDIF control flow  
✅ Integer, Float, String, Boolean types  
✅ Basic arithmetic and operators  
✅ Simple PRINT and LET statements  

---

## Phase 1: Enhanced I/O (Next)

### 1.1 Multi-argument PRINT
**Goal**: Support printing multiple values in one statement  
**Syntax**: `PRINT <expr>, <expr>, <expr>, ...;`

**Example**:
```basic
LET x = 42;
PRINT "The value is", x, "and pi is", 3.14;
```

**Implementation**:
- Modify `PrintStmt` to hold `vector<ExprPtr>` instead of single `ExprPtr`
- Update parser to accept comma-separated expressions
- Generate bytecode to print each expression in sequence
- Optional: Add space or no space between values (decide on convention)

**Bytecode approach**:
- For each expression: `getstatic System.out`, load expr, `invokevirtual println`
- OR: Use `print` (no newline) for all but last, then `println` for final

---

### 1.2 INPUT Statement
**Goal**: Read user input and assign to variables  
**Syntax**: `INPUT <variable>;`

**Example**:
```basic
LET name = "";
LET age = 0;
PRINT "Enter your name:";
INPUT name;
PRINT "Enter your age:";
INPUT age;
PRINT "Hello", name, "you are", age, "years old";
```

**Implementation Details**:

1. **Add Scanner to constant pool**:
   - `java/util/Scanner` class
   - Constructor: `(Ljava/io/InputStream;)V`
   - Method: `nextLine()Ljava/lang/String;`

2. **Create Scanner instance** (in method prologue):
   ```bytecode
   new java/util/Scanner
   dup
   getstatic System.in
   invokespecial Scanner.<init>
   astore <scanner_local>
   ```

3. **INPUT statement codegen**:
   - Load scanner
   - Call `nextLine()` → returns String
   - Parse based on variable type:
     - **String**: store directly
     - **Int**: call `Integer.parseInt(String)`
     - **Float**: call `Float.parseFloat(String)`
     - **Bool**: check if "true" or "false" (case-insensitive)
   - Store to variable

4. **Type inference/coercion**:
   - Try parsing in order: Int → Float → Bool → String
   - Use try/catch or regex pre-check
   - Simple approach: just convert based on known variable type

**Tokens**: Add `INPUT` keyword  
**AST**: Add `InputStmt { string var; }`

---

## Phase 2: Arrays

### 2.1 Array Declaration and Access
**Syntax**: 
```basic
LET arr[10] = 0;          # Declare int array size 10, init to 0
LET arr[3] = 42;          # Set element
LET x = arr[3];           # Get element
```

**Implementation**:
- Track array types: `Type::IntArray`, `Type::FloatArray`, etc.
- Use JVM `newarray`, `anewarray` instructions
- Use `iaload`, `iastore`, `faload`, `fastore`, `aaload`, `astore` for access
- Store arrays as reference types in locals (use `astore`/`aload`)

**Challenges**:
- Parse array indexing: `arr[expr]`
- Distinguish declaration from assignment
- Handle multi-dimensional arrays (future)

---

## Phase 3: Standard Library Functions

### 3.1 Math Functions
**Goal**: Support common math operations

**Functions to add**:
- `ABS(x)` - absolute value
- `SQR(x)` or `SQRT(x)` - square root
- `SIN(x)`, `COS(x)`, `TAN(x)` - trig functions
- `INT(x)` - integer part (floor)
- `RND()` - random number 0.0-1.0
- `POW(x, y)` - x raised to power y

**Implementation**:
- Create a `BasicRuntime` helper class with static methods
- Example: `public static float sqrtf(float x) { return (float)Math.sqrt(x); }`
- Add to constant pool and call with `invokestatic`
- Parse function calls: `<ID> '(' <args> ')'`

### 3.2 String Functions
- `LEN(s)` - string length
- `MID(s, start, len)` or `SUBSTR(s, start, len)` - substring
- `LEFT(s, n)` - first n characters
- `RIGHT(s, n)` - last n characters
- `UPPER(s)`, `LOWER(s)` - case conversion
- `STR(x)` - convert number to string
- `VAL(s)` - convert string to number

**Implementation**:
- Use Java String methods: `length()`, `substring()`, `toUpperCase()`, etc.
- Emit `invokevirtual` for instance methods
- Add conversion methods to BasicRuntime

---

## Phase 4: Loops

### 4.1 FOR Loop
**Syntax**:
```basic
FOR i = 1 TO 10
    PRINT i;
NEXT i
```

Or with STEP:
```basic
FOR i = 0 TO 100 STEP 5
    PRINT i;
NEXT i
```

**Implementation**:
- Parse loop variable, start, end, optional step
- Generate labels: `L_top`, `L_end`
- Codegen:
  ```
  <init loop var>
  L_top:
  <load var>, <load end>, if_icmpgt L_end
  <body>
  <load var>, <load step>, iadd, store var
  goto L_top
  L_end:
  ```

### 4.2 WHILE Loop
**Syntax**:
```basic
WHILE condition
    <statements>
ENDWHILE
```

**Implementation**:
```
L_top:
<load condition>
ifeq L_end
<body>
goto L_top
L_end:
```

### 4.3 DO WHILE Loop
**Syntax**:
```basic
DO
    <statements>
WHILE condition
```

**Implementation**:
```
L_top:
<body>
<load condition>
ifne L_top
```

---

## Phase 5: User-Defined Functions and Subroutines

### 5.1 Functions (with return value)
**Syntax**:
```basic
FUNCTION add(a, b)
    RETURN a + b;
ENDFUNCTION

LET sum = add(5, 3);
```

**Implementation**:
- Parse function definitions before main statements
- Generate separate methods in the class
- Track parameter types (infer from usage)
- Use `invokestatic` for calls
- Use `ireturn`, `freturn`, `areturn` for returns

### 5.2 Subroutines (no return value)
**Syntax**:
```basic
SUB greet(name)
    PRINT "Hello,", name;
ENDSUB

CALL greet("Alice");
```

**Implementation**:
- Similar to functions but returns void
- Use `return` instruction
- Optional: `CALL` keyword or just function name as statement

---

## Implementation Order

**Immediate Next Steps (development-1 branch)**:

1. ✅ Merge boolean-expr-branch → main
2. ✅ Create development-1 branch
3. **Implement multi-argument PRINT**
   - Parse comma-separated expressions
   - Generate appropriate println/print calls
4. **Implement INPUT statement**
   - Add Scanner to constant pool
   - Parse INPUT statements
   - Generate type-appropriate parsing code
5. **Test and commit**
6. **Merge to main**

**Subsequent Branches**:
- `development-2`: Arrays
- `development-3`: Standard library functions
- `development-4`: Loops (FOR, WHILE, DO-WHILE)
- `development-5`: User functions and subroutines

---

## Design Principles

1. **Keep it simple**: Prioritize readability over optimization
2. **Test incrementally**: Add tests for each feature
3. **Document as we go**: Update README and extending.md
4. **Maintain Java 6 bytecode**: Avoid StackMapTable complexity
5. **Case-insensitive keywords**: Follow BASIC tradition
6. **Type safety**: Maintain type checking at compile time where possible

---

## Future Considerations

- **Error handling**: Better error messages with line numbers
- **Multiple source files**: INCLUDE or IMPORT statements
- **Debugger support**: LineNumberTable attribute
- **Optimization**: Constant folding, dead code elimination
- **Standard library**: File I/O, more string/math functions
- **Data structures**: Records/structs, multi-dimensional arrays

---

**Current Focus**: Multi-argument PRINT and INPUT statements

