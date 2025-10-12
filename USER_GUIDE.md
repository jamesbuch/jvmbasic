# JVM BASIC - User Guide

**Version**: Phase 5 Complete  
**Date**: October 12, 2025

---

## Quick Start

### Installation
```bash
git clone git@github.com:jamesbuch/jvmbasic.git
cd jvmbasic
make
```

### Hello World
```basic
PRINT "Hello, World!"
```

```bash
./jvmbasic hello.bas
java BasicProgram
```

---

## Command Line Usage

### Basic Usage
```bash
jvmbasic program.bas              # Compile program.bas
jvmbasic < program.bas            # Read from stdin
jvmbasic program.bas -o Out.class # Custom output file
jvmbasic --help                   # Show help
```

### Options
- `-o <file>` - Output class file (default: BasicProgram.class)
- `-h, --help` - Show help message

### Running Programs
```bash
java BasicProgram                 # Run compiled program
java BasicProgram < input.txt     # With stdin input
```

---

## Language Features

### Variables & Types
```basic
LET x = 42                  REM Integer
LET y = 3.14                REM Float
LET name = "Alice"          REM String  
LET active = true           REM Boolean
```

### Arrays
```basic
DIM numbers(10) = 0.0       REM Float array
DIM names(5) = ""           REM String array

LET numbers(0) = 100.0
LET names(0) = "Alice"
```

### Control Structures
```basic
REM IF statement
IF x > 5 THEN
    PRINT "Greater than 5"
ELSEIF x == 5 THEN
    PRINT "Equal to 5"
ELSE
    PRINT "Less than 5"
ENDIF

REM FOR loop
FOR i = 1.0 TO 10.0
    PRINT i
NEXT i

REM FOR with STEP
FOR i = 0.0 TO 100.0 STEP 10.0
    PRINT i
NEXT i

REM WHILE loop
WHILE x > 0.0
    LET x = x - 1.0
ENDWHILE

REM DO-WHILE loop
DO
    PRINT x
    LET x = x + 1.0
WHILE x < 10.0
```

### Functions
```basic
FUNCTION factorial(n)
    IF n <= 1.0 THEN
        RETURN 1.0
    ELSE
        RETURN n * factorial(n - 1.0)
    ENDIF
ENDFUNCTION

LET result = factorial(10.0)
PRINT "10! =", result
```

### SUB Procedures
```basic
SUB greet(name)
    PRINT "Hello,", name, "!"
ENDSUB

CALL greet("Alice")
```

### Comments
```basic
REM This is a comment
PRINT "Code"    REM Comments can be at end of line
```

---

## Built-in Functions (93 Total)

### Math Functions

#### Basic Math
```basic
ABS(x)          REM Absolute value
SQR(x)          REM Square root (SQRT also works)
POW(x, y)       REM x to the power y
MIN(x, y)       REM Minimum
MAX(x, y)       REM Maximum
INT(x)          REM Convert to integer
FLOOR(x)        REM Floor
CEIL(x)         REM Ceiling
ROUND(x)        REM Round to nearest
```

**Examples**:
```basic
PRINT ABS(-5.0)              REM 5.0
PRINT SQR(16.0)              REM 4.0
PRINT POW(2.0, 8.0)          REM 256.0
PRINT MIN(10.0, 20.0)        REM 10.0
PRINT MAX(10.0, 20.0)        REM 20.0
PRINT INT(3.7)               REM 3
PRINT FLOOR(3.7)             REM 3.0
PRINT CEIL(3.2)              REM 4.0
PRINT ROUND(3.5)             REM 4
```

#### Trigonometry
```basic
SIN(x)          REM Sine (radians)
COS(x)          REM Cosine
TAN(x)          REM Tangent
ASIN(x)         REM Arc sine
ACOS(x)         REM Arc cosine
ATAN(x)         REM Arc tangent
ATAN2(y, x)     REM Arc tangent of y/x
```

**Examples**:
```basic
PRINT SIN(PI / 2.0)          REM 1.0
PRINT COS(0.0)               REM 1.0
PRINT TAN(PI / 4.0)          REM ~1.0
```

#### Exponentials & Logarithms
```basic
EXP(x)          REM e^x
LOG(x)          REM Natural log
LOG10(x)        REM Base-10 log
```

**Examples**:
```basic
PRINT EXP(1.0)               REM 2.71828... (e)
PRINT LOG(E)                 REM 1.0
PRINT LOG10(100.0)           REM 2.0
```

#### Constants
```basic
PI              REM 3.14159...
E               REM 2.71828...
```

**Examples**:
```basic
PRINT PI                     REM 3.1415927
PRINT E                      REM 2.7182817
LET circumference = 2.0 * PI * radius
```

#### Random Numbers
```basic
RND             REM Random 0.0-1.0
RNDI(n)         REM Random integer 0 to n-1
RNDINT(min, max) REM Random integer min to max
```

**Examples**:
```basic
PRINT RND                    REM 0.734521...
PRINT RNDI(10)               REM Random 0-9
PRINT RNDINT(1, 6)           REM Dice roll 1-6
```

### String Functions

#### Basic String Operations
```basic
LEN(s)          REM Length
UPPER(s)        REM Uppercase (UCASE also works)
LOWER(s)        REM Lowercase (LCASE also works)
TRIM(s)         REM Trim whitespace
LTRIM(s)        REM Trim left
RTRIM(s)        REM Trim right
REVERSE(s)      REM Reverse string
```

**Examples**:
```basic
LET name = "  Alice  "
PRINT LEN(name)              REM 9
PRINT TRIM(name)             REM "Alice"
PRINT UPPER("hello")         REM "HELLO"
PRINT LOWER("WORLD")         REM "world"
PRINT REVERSE("abc")         REM "cba"
```

#### Substring Operations
```basic
LEFT(s, n)      REM Left n characters
RIGHT(s, n)     REM Right n characters
MID(s, start, len) REM Substring (SUBSTR also works)
```

**Examples**:
```basic
LET text = "Hello World"
PRINT LEFT(text, 5)          REM "Hello"
PRINT RIGHT(text, 5)         REM "World"
PRINT MID(text, 6, 5)        REM "World"
```

#### Search & Compare
```basic
INSTR(haystack, needle)   REM Find position
CONTAINS(s, substr)       REM Check if contains
```

**Examples**:
```basic
PRINT INSTR("Hello World", "World")  REM 6
PRINT CONTAINS("abc", "b")           REM true
```

#### Character Functions
```basic
ASC(s)          REM ASCII code of first char
CHR(n)          REM Character from ASCII code
```

**Examples**:
```basic
PRINT ASC("A")               REM 65
PRINT CHR(65)                REM "A"
```

#### String Building
```basic
SPACE(n)        REM n spaces
STRING(n, c)    REM Repeat character n times
```

**Examples**:
```basic
PRINT SPACE(5) + "text"      REM "     text"
PRINT STRING(3, "*")         REM "***"
```

#### Type Conversion
```basic
VAL(s)          REM String to number
STR(n)          REM Number to string (varies by type)
```

**Examples**:
```basic
LET num = VAL("42.5")        REM 42.5
LET text = STR(100.0)        REM "100.0"
```

#### Type Checking
```basic
ISNUM(s)        REM Check if numeric
ISINT(s)        REM Check if integer
```

**Examples**:
```basic
PRINT ISNUM("123")           REM true
PRINT ISNUM("abc")           REM false
PRINT ISINT("42")            REM true
PRINT ISINT("42.5")          REM false
```

### Regular Expressions

```basic
REGEXMATCH(pattern, text)           REM Test if matches
REGEXFIND(pattern, text)            REM Find first match
REGEXREPLACE(pattern, text, repl)   REM Replace all
REGEXGROUP(pattern, text, num)      REM Get capture group
```

**Examples**:
```basic
LET email = "user@example.com"
LET username = REGEXGROUP("(.+)@", email, 1)  REM "user"
LET domain = REGEXGROUP("@(.+)", email, 1)    REM "example.com"

LET phone = "555-1234"
LET clean = REGEXREPLACE("[^0-9]", phone, "")  REM "5551234"
```

### String Formatting

```basic
FORMAT(template, arg)       REM Replace {0} with string
FORMATF(template, float)    REM Replace {0} with float
FORMATI(template, int)      REM Replace {0} with int
```

**Examples**:
```basic
PRINT FORMAT("Hello, {0}!", "World")         REM "Hello, World!"
PRINT FORMATF("Pi = {0}", PI)                REM "Pi = 3.1415927"
PRINT FORMATI("Count: {0}", 42)              REM "Count: 42"
```

### File I/O

```basic
OPENINPUT(filename)         REM Open for reading, returns handle
OPENOUTPUT(filename)        REM Open for writing, returns handle
READLINE(handle)            REM Read line from file
CLOSEFILE(handle)           REM Close file
FILEEXISTS(filename)        REM Check if file exists
DELETEFILE(filename)        REM Delete file
```

**Examples**:
```basic
LET handle = OPENOUTPUT("/tmp/output.txt")
IF handle >= 0.0 THEN
    REM File opened successfully
    LET result = CLOSEFILE(handle)
ENDIF

LET exists = FILEEXISTS("/tmp/data.txt")
PRINT "File exists:", exists
```

### Array Functions

```basic
MINARRAY(arr)   REM Find minimum (IntArray)
MAXARRAY(arr)   REM Find maximum (IntArray)
SUMARRAY(arr)   REM Sum all elements (IntArray)
UBOUND(arr)     REM Last index (array.length - 1)
```

**Examples**:
```basic
DIM data(5) = 0
LET data(0) = 10
LET data(1) = 5
LET data(2) = 20

PRINT MINARRAY(data)         REM 5
PRINT MAXARRAY(data)         REM 20
PRINT SUMARRAY(data)         REM 35
PRINT UBOUND(data)           REM 4 (size-1)
```

---

## Complete Example Programs

### Factorial (Recursive)
```basic
FUNCTION factorial(n)
    IF n <= 1.0 THEN
        RETURN 1.0
    ELSE
        RETURN n * factorial(n - 1.0)
    ENDIF
ENDFUNCTION

PRINT "Enter a number:"
INPUT n
PRINT n, "! =", factorial(n)
```

### Prime Number Checker
```basic
FUNCTION isPrime(num)
    IF num <= 1.0 THEN
        RETURN false
    ENDIF
    IF num == 2.0 THEN
        RETURN true
    ENDIF
    
    LET divisor = 2.0
    WHILE divisor * divisor <= num
        IF num MOD divisor == 0.0 THEN
            RETURN false
        ENDIF
        LET divisor = divisor + 1.0
    ENDWHILE
    
    RETURN true
ENDFUNCTION

PRINT "Enter a number:"
INPUT num
PRINT num, "is prime:", isPrime(num)
```

### Statistical Analysis
```basic
FUNCTION mean(arr, size)
    LET sum = 0.0
    LET i = 0.0
    WHILE i < size
        LET sum = sum + arr(i)
        LET i = i + 1.0
    ENDWHILE
    RETURN sum / size
ENDFUNCTION

DIM data(5) = 0.0
LET data(0) = 10.0
LET data(1) = 20.0
LET data(2) = 30.0
LET data(3) = 40.0
LET data(4) = 50.0

PRINT "Mean:", mean(data, 5.0)
```

---

## Tips & Tricks

### Type Inference
```basic
REM JVM BASIC infers types automatically
LET x = 42          REM Int
LET y = 42.0        REM Float
LET z = x + y       REM Float (promoted)
```

### Array Parameters
```basic
FUNCTION sumArray(arr, size)
    LET total = 0.0
    LET i = 0.0
    WHILE i < size
        LET total = total + arr(i)
        LET i = i + 1.0
    ENDWHILE
    RETURN total
ENDFUNCTION

REM Arrays passed by reference - can modify!
SUB doubleArray(arr, size)
    LET i = 0.0
    WHILE i < size
        LET arr(i) = arr(i) * 2.0
        LET i = i + 1.0
    ENDWHILE
ENDSUB
```

### Recursion
```basic
REM Recursion works - functions can call themselves
FUNCTION fibonacci(n)
    IF n <= 1.0 THEN
        RETURN n
    ELSE
        RETURN fibonacci(n - 1.0) + fibonacci(n - 2.0)
    ENDIF
ENDFUNCTION
```

---

## Error Messages

### Common Errors

**"Undefined variable"**:
```basic
PRINT x  REM Error if x not defined
LET x = 10  REM Define first
PRINT x  REM OK
```

**"Type mismatch"**:
```basic
LET x = 42
LET x = "text"  REM Error: can't change type
```

**"Function arity mismatch"**:
```basic
FUNCTION add(a, b)
    RETURN a + b
ENDFUNCTION

LET x = add(1.0)  REM Error: needs 2 arguments
```

---

## Advanced Features

### Nested Function Calls
```basic
PRINT double(double(5.0))  REM Nested calls work
```

### Array Modification in Functions
```basic
FUNCTION doubleArray(arr, size)
    LET i = 0.0
    WHILE i < size
        LET arr(i) = arr(i) * 2.0
        LET i = i + 1.0
    ENDWHILE
    RETURN 0.0
ENDFUNCTION

DIM data(5) = 0.0
LET data(0) = 1.0
LET dummy = doubleArray(data, 5.0)
PRINT data(0)  REM 2.0 (modified!)
```

### Regular Expressions
```basic
LET email = "user@example.com"
IF REGEXMATCH(".+@.+", email) THEN
    LET user = REGEXGROUP("(.+)@", email, 1)
    PRINT "Username:", user
ENDIF
```

---

## Performance Tips

### Use Iterative Instead of Recursive (When Possible)
```basic
REM Recursive (slower)
FUNCTION fibRec(n)
    IF n <= 1.0 THEN RETURN n
    ELSE RETURN fibRec(n-1.0) + fibRec(n-2.0)
    ENDIF
ENDFUNCTION

REM Iterative (faster!)
FUNCTION fibIter(n)
    LET a = 0.0
    LET b = 1.0
    LET count = 0.0
    WHILE count < n
        LET temp = a + b
        LET a = b
        LET b = temp
        LET count = count + 1.0
    ENDWHILE
    RETURN a
ENDFUNCTION
```

### Use Built-in Functions
```basic
REM Instead of loops, use built-ins when possible
LET min = MINARRAY(data)  REM Faster than manual loop
```

---

## Limitations

### Current Limitations:
1. No user-defined types (coming in Phase 6!)
2. No collections (List, Map, Set - Phase 8)
3. No object-oriented features (Phase 7)
4. No exception handling (Phase 10)
5. Class name always "BasicProgram" (use that to run)

### Workarounds:
- Use parallel arrays instead of structs
- Fixed-size arrays instead of dynamic lists
- Return error codes instead of exceptions

---

## Example Programs

Located in `examples/`:
- `fibonacci_sequence.bas` - Recursive & iterative Fibonacci
- `math_algorithms.bas` - GCD, factorial, primes, powers
- `statistics.bas` - Mean, variance, standard deviation
- `prime_numbers.bas` - Prime testing and generation
- `comprehensive_demo.bas` - All features showcase
- `lotto_improved.bas` - Random number generation with statistics

---

## Getting Help

### Documentation:
- `README.md` - Project overview
- `docs/reference/LANGUAGE_FEATURES.md` - Complete language reference
- `docs/dev/` - Developer guides
- `START_PHASE6_HERE.md` - What's next

### Debugging:
```bash
# Check your program structure
./jvmbasic-new --dump-ast < program.bas

# Examine generated bytecode
javap -c BasicProgram

# Compare with Java
javac Test.java && javap -c Test
```

### Common Issues:
See `docs/dev/DEBUGGING_GUIDE.md`

---

## What's Next

**Phase 6**: User-defined types (TYPE...ENDTYPE)  
**Phase 7**: Object-oriented programming  
**Phase 8**: Collections (List, Map, Set)  
**Phase 9**: Networking  
**Phase 10**: Exception handling  

See `docs/planning/PHASE6_ROADMAP.md` for complete roadmap.

---

**JVM BASIC - A modern BASIC for the JVM!** 🚀

Version: Phase 5 Complete  
Tests: 44/44 passing  
Built-in Functions: 93  
Status: Production-ready for educational use

