# JVM BASIC User Guide

## Table of Contents
1. [Getting Started](#getting-started)
2. [Command-Line Options](#command-line-options)
3. [Language Basics](#language-basics)
4. [Data Types](#data-types)
5. [Variables and Arrays](#variables-and-arrays)
6. [Control Flow](#control-flow)
7. [Functions and Subroutines](#functions-and-subroutines)
8. [Built-in Functions](#built-in-functions)
9. [File I/O](#file-io)
10. [Regular Expressions](#regular-expressions)
11. [Examples](#examples)

---

## Getting Started

### Compiling Programs

```bash
# Compile from file
jvmbasic program.bas

# Compile with custom output name
jvmbasic program.bas -o MyProgram.class

# Compile from stdin
jvmbasic < program.bas
```

### Running Programs

```bash
# Run default compiled program
java BasicProgram

# Run custom-named program
java MyProgram
```

### Your First Program

```basic
REM hello.bas - A simple greeting program
PRINT "Hello, World!"
```

Compile and run:
```bash
jvmbasic hello.bas
java BasicProgram
```

---

## Command-Line Options

### `-o <file>`
Specify the output class file name. The class name is automatically derived from the filename.

```bash
jvmbasic program.bas -o MyProgram.class
java MyProgram
```

### `-h` or `--help`
Display help information including usage, options, examples, and features.

```bash
jvmbasic --help
```

---

## Language Basics

### Comments
Use `REM` for single-line comments:

```basic
REM This is a comment
PRINT "Hello"  REM Comments can appear at end of line
```

### Case Insensitivity
JVM BASIC is case-insensitive for keywords and identifiers:

```basic
print "Hello"
PRINT "World"
PrInT "!"
```

### Line Structure
One statement per line. No line numbers or explicit line continuations needed.

---

## Data Types

JVM BASIC supports four basic data types:

### Float (Numeric)
Default numeric type for all numbers.

```basic
LET pi = 3.14159
LET count = 42.0
LET negative = -10.5
```

### Integer
Whole numbers (used internally by some functions).

```basic
LET x = INT(3.7)  REM x = 3
LET y = INT(-2.3) REM y = -2
```

### String
Text enclosed in double quotes.

```basic
LET name = "Alice"
LET message = "Hello, World!"
LET empty = ""
```

### Boolean
True or false values.

```basic
LET flag = true
LET done = false
IF flag THEN
    PRINT "Flag is set"
ENDIF
```

---

## Variables and Arrays

### Variables
Variables are declared with `LET`:

```basic
LET x = 10.0
LET name = "Bob"
LET active = true
```

### Arrays
Arrays are declared with `DIM` and indexed from 0:

```basic
REM Declare array of 10 elements (indices 0-9)
DIM numbers(10)

REM Initialize elements
LET numbers(0) = 1.0
LET numbers(1) = 2.0

REM Access elements
PRINT numbers(0)
```

#### Multi-dimensional Arrays
```basic
REM 2D array: 3 rows, 4 columns
DIM matrix(3, 4)

LET matrix(0, 0) = 1.0
LET matrix(2, 3) = 99.0

PRINT matrix(0, 0)
```

---

## Control Flow

### IF Statements

```basic
IF condition THEN
    REM statements
ENDIF

IF x > 0.0 THEN
    PRINT "Positive"
ELSE
    PRINT "Not positive"
ENDIF
```

### FOR Loops

```basic
REM Loop from 1 to 10
FOR i = 1.0 TO 10.0
    PRINT i
NEXT i

REM Loop with step
FOR i = 0.0 TO 100.0 STEP 10.0
    PRINT i
NEXT i

REM Countdown
FOR i = 10.0 TO 1.0 STEP -1.0
    PRINT i
NEXT i
```

### WHILE Loops

```basic
LET count = 0.0
WHILE count < 5.0
    PRINT count
    LET count = count + 1.0
ENDWHILE
```

---

## Functions and Subroutines

### Functions
Functions return a value using `RETURN`:

```basic
FUNCTION add(a, b)
    RETURN a + b
ENDFUNCTION

LET result = add(5.0, 3.0)
PRINT result  REM Output: 8.0
```

### Subroutines
Subroutines are called with `CALL` and don't return values:

```basic
SUB greet(name)
    PRINT "Hello, "; name; "!"
ENDSUB

CALL greet("Alice")
```

### Recursion
Functions can call themselves:

```basic
FUNCTION factorial(n)
    IF n <= 1.0 THEN
        RETURN 1.0
    ELSE
        RETURN n * factorial(n - 1.0)
    ENDIF
ENDFUNCTION

PRINT factorial(5.0)  REM Output: 120.0
```

### Array Parameters
Arrays can be passed to functions:

```basic
SUB fillArray(arr, size, value)
    FOR i = 0.0 TO size - 1.0
        LET arr(INT(i)) = value
    NEXT i
ENDSUB

DIM myArray(10)
CALL fillArray(myArray, 10.0, 42.0)
```

---

## Built-in Functions

### Mathematical Functions

#### `ABS(x)` - Absolute Value
```basic
PRINT ABS(-5.5)   REM Output: 5.5
PRINT ABS(3.2)    REM Output: 3.2
```

#### `SQRT(x)` - Square Root
```basic
PRINT SQRT(16.0)  REM Output: 4.0
PRINT SQRT(2.0)   REM Output: 1.414...
```

#### `POW(base, exponent)` - Power
```basic
PRINT POW(2.0, 3.0)   REM Output: 8.0
PRINT POW(10.0, 2.0)  REM Output: 100.0
```

#### `SIN(x)`, `COS(x)`, `TAN(x)` - Trigonometric
Angle in radians:
```basic
PRINT SIN(0.0)           REM Output: 0.0
PRINT COS(0.0)           REM Output: 1.0
PRINT TAN(PI() / 4.0)    REM Output: 1.0
```

#### `ASIN(x)`, `ACOS(x)`, `ATAN(x)` - Inverse Trigonometric
Returns result in radians:
```basic
PRINT ASIN(1.0)     REM Output: π/2
PRINT ACOS(0.0)     REM Output: π/2
PRINT ATAN(1.0)     REM Output: π/4
```

#### `ATAN2(y, x)` - Two-argument Arc Tangent
```basic
PRINT ATAN2(1.0, 1.0)   REM Output: π/4
```

#### `LOG(x)` - Natural Logarithm
```basic
PRINT LOG(E())     REM Output: 1.0
PRINT LOG(1.0)     REM Output: 0.0
```

#### `LOG10(x)` - Base-10 Logarithm
```basic
PRINT LOG10(100.0)   REM Output: 2.0
PRINT LOG10(1000.0)  REM Output: 3.0
```

#### `EXP(x)` - Exponential (e^x)
```basic
PRINT EXP(1.0)    REM Output: 2.718... (e)
PRINT EXP(0.0)    REM Output: 1.0
```

#### `FLOOR(x)` - Round Down
```basic
PRINT FLOOR(3.7)   REM Output: 3.0
PRINT FLOOR(-2.3)  REM Output: -3.0
```

#### `CEIL(x)` - Round Up
```basic
PRINT CEIL(3.2)    REM Output: 4.0
PRINT CEIL(-2.7)   REM Output: -2.0
```

#### `ROUND(x)` - Round to Nearest
```basic
PRINT ROUND(3.5)   REM Output: 4.0
PRINT ROUND(3.4)   REM Output: 3.0
```

#### `MIN(a, b)` - Minimum
```basic
PRINT MIN(5.0, 3.0)   REM Output: 3.0
```

#### `MAX(a, b)` - Maximum
```basic
PRINT MAX(5.0, 3.0)   REM Output: 5.0
```

#### `PI()` - Pi Constant
```basic
PRINT PI()   REM Output: 3.14159...
```

#### `E()` - Euler's Number
```basic
PRINT E()    REM Output: 2.71828...
```

#### `SIGN(x)` - Sign of Number
```basic
PRINT SIGN(5.0)    REM Output: 1.0
PRINT SIGN(-3.0)   REM Output: -1.0
PRINT SIGN(0.0)    REM Output: 0.0
```

#### `HYPOT(x, y)` - Hypotenuse
```basic
PRINT HYPOT(3.0, 4.0)   REM Output: 5.0
```

### Random Number Functions

#### `RND()` - Random Float [0.0, 1.0)
```basic
LET r = RND()
PRINT r   REM Random value like 0.123456
```

#### `RNDINT(min, max)` - Random Integer [min, max]
```basic
LET dice = RNDINT(1, 6)
PRINT dice   REM Random integer 1-6
```

#### `RANDOMSEED(seed)` - Set Random Seed
```basic
CALL RANDOMSEED(42)  REM Reproducible random sequence
```

### Type Conversion Functions

#### `INT(x)` - Convert to Integer
```basic
PRINT INT(3.7)      REM Output: 3
PRINT INT(-2.3)     REM Output: -2
```

#### `FLOAT(x)` - Convert to Float
```basic
LET i = INT(5.5)
LET f = FLOAT(i)    REM Convert back to float
```

#### `STR(x)` - Convert to String
```basic
LET s = STR(42.0)
PRINT s              REM Output: "42.0"
```

#### `VAL(s)` - Convert String to Number
```basic
LET x = VAL("123.45")
PRINT x + 1.0        REM Output: 124.45
```

#### `BOOL(x)` - Convert to Boolean
```basic
PRINT BOOL(1.0)      REM Output: true
PRINT BOOL(0.0)      REM Output: false
```

### String Functions

#### `LEN(s)` - String Length
```basic
PRINT LEN("Hello")   REM Output: 5
```

#### `LEFT(s, n)` - Left Substring
```basic
PRINT LEFT("Hello", 2)   REM Output: "He"
```

#### `RIGHT(s, n)` - Right Substring
```basic
PRINT RIGHT("Hello", 2)   REM Output: "lo"
```

#### `MID(s, start, length)` - Middle Substring
```basic
PRINT MID("Hello", 1, 3)   REM Output: "ell"
```

#### `UPPER(s)` - Convert to Uppercase
```basic
PRINT UPPER("hello")   REM Output: "HELLO"
```

#### `LOWER(s)` - Convert to Lowercase
```basic
PRINT LOWER("HELLO")   REM Output: "hello"
```

#### `TRIM(s)` - Remove Whitespace
```basic
PRINT TRIM("  hello  ")   REM Output: "hello"
```

#### `REPLACE(s, old, new)` - Replace Substring
```basic
LET s = REPLACE("Hello World", "World", "JVM")
PRINT s   REM Output: "Hello JVM"
```

#### `INDEXOF(s, substring)` - Find Position
```basic
PRINT INDEXOF("Hello", "ll")   REM Output: 2
PRINT INDEXOF("Hello", "x")    REM Output: -1 (not found)
```

#### `SUBSTRING(s, start, end)` - Extract Range
```basic
PRINT SUBSTRING("Hello", 1, 4)   REM Output: "ell"
```

#### `STARTSWITH(s, prefix)` - Check Prefix
```basic
PRINT STARTSWITH("Hello", "He")   REM Output: true
PRINT STARTSWITH("Hello", "he")   REM Output: false
```

#### `ENDSWITH(s, suffix)` - Check Suffix
```basic
PRINT ENDSWITH("Hello", "lo")   REM Output: true
```

#### `CONTAINS(s, substring)` - Check Contains
```basic
PRINT CONTAINS("Hello", "ell")   REM Output: true
```

#### `CHR(code)` - Character from ASCII Code
```basic
PRINT CHR(65)   REM Output: "A"
PRINT CHR(97)   REM Output: "a"
```

#### `ASC(s)` - ASCII Code of First Character
```basic
PRINT ASC("A")     REM Output: 65
PRINT ASC("Hello") REM Output: 72 (for 'H')
```

#### `SPLIT(s, delimiter)` - Split String to Array
```basic
LET parts = SPLIT("apple,banana,cherry", ",")
PRINT ARRAYLEN(parts)   REM Output: 3
PRINT ARRAYGET(parts, 0) REM Output: "apple"
```

#### `JOIN(arr, delimiter)` - Join Array to String
```basic
DIM fruits(3)
LET fruits(0) = "apple"
LET fruits(1) = "banana"
LET fruits(2) = "cherry"
LET result = JOIN(fruits, ", ")
PRINT result   REM Output: "apple, banana, cherry"
```

#### `FORMAT(template, value)` - Format String
```basic
LET s = FORMAT("Value: %s", "test")
PRINT s   REM Output: "Value: test"

LET s = FORMAT("Number: %s", STR(42.0))
PRINT s   REM Output: "Number: 42.0"
```

### Array Functions

#### `ARRAYLEN(arr)` - Array Length
```basic
DIM numbers(5)
PRINT ARRAYLEN(numbers)   REM Output: 5
```

#### `ARRAYGET(arr, index)` - Get Element (string arrays)
```basic
LET parts = SPLIT("a,b,c", ",")
PRINT ARRAYGET(parts, 1)   REM Output: "b"
```

#### `ARRAYSET(arr, index, value)` - Set Element (string arrays)
```basic
LET parts = SPLIT("a,b,c", ",")
LET dummy = ARRAYSET(parts, 1, "X")
PRINT ARRAYGET(parts, 1)   REM Output: "X"
```

#### `ARRAYSORT(arr)` - Sort Array (modifies in-place)
```basic
DIM nums(5)
LET nums(0) = 5.0
LET nums(1) = 2.0
LET nums(2) = 8.0
LET nums(3) = 1.0
LET nums(4) = 9.0

CALL ARRAYSORT(nums)
REM Array is now: 1.0, 2.0, 5.0, 8.0, 9.0
```

#### `ARRAYSORTSTR(arr)` - Sort String Array
```basic
LET words = SPLIT("dog,cat,apple,zebra", ",")
CALL ARRAYSORTSTR(words)
REM Array is now: "apple", "cat", "dog", "zebra"
```

#### `ARRAYSUM(arr)` - Sum of Array Elements
```basic
DIM nums(3)
LET nums(0) = 10.0
LET nums(1) = 20.0
LET nums(2) = 30.0
PRINT ARRAYSUM(nums)   REM Output: 60.0
```

#### `ARRAYMIN(arr)` - Minimum Element
```basic
DIM nums(3)
LET nums(0) = 10.0
LET nums(1) = 5.0
LET nums(2) = 15.0
PRINT ARRAYMIN(nums)   REM Output: 5.0
```

#### `ARRAYMAX(arr)` - Maximum Element
```basic
DIM nums(3)
LET nums(0) = 10.0
LET nums(1) = 5.0
LET nums(2) = 15.0
PRINT ARRAYMAX(nums)   REM Output: 15.0
```

#### `ARRAYAVG(arr)` - Average of Elements
```basic
DIM nums(3)
LET nums(0) = 10.0
LET nums(1) = 20.0
LET nums(2) = 30.0
PRINT ARRAYAVG(nums)   REM Output: 20.0
```

#### `ARRAYREVERSE(arr)` - Reverse Array
```basic
DIM nums(3)
LET nums(0) = 1.0
LET nums(1) = 2.0
LET nums(2) = 3.0
CALL ARRAYREVERSE(nums)
REM Array is now: 3.0, 2.0, 1.0
```

#### `ARRAYFIND(arr, value)` - Find Index of Value
```basic
DIM nums(3)
LET nums(0) = 10.0
LET nums(1) = 20.0
LET nums(2) = 30.0
PRINT ARRAYFIND(nums, 20.0)   REM Output: 1
PRINT ARRAYFIND(nums, 99.0)   REM Output: -1 (not found)
```

#### `ARRAYCOPY(src, dest)` - Copy Array Contents
```basic
DIM src(3)
DIM dest(3)
LET src(0) = 1.0
LET src(1) = 2.0
LET src(2) = 3.0
CALL ARRAYCOPY(src, dest)
REM dest now contains: 1.0, 2.0, 3.0
```

#### `ARRAYFILL(arr, value)` - Fill with Value
```basic
DIM nums(5)
CALL ARRAYFILL(nums, 42.0)
REM All elements are now 42.0
```

### Input/Output Functions

#### `INPUT(prompt)` - Read User Input
```basic
LET name = INPUT("Enter your name: ")
PRINT "Hello, "; name

LET age = VAL(INPUT("Enter your age: "))
PRINT "You are "; age; " years old"
```

#### `PRINT` - Display Output
```basic
REM Basic printing
PRINT "Hello, World!"

REM Multiple values with comma (adds space)
PRINT "Value:", 42

REM Multiple values with semicolon (no space)
PRINT "x="; 10; " y="; 20

REM Trailing comma/semicolon (no newline)
PRINT "Loading...";
PRINT "done"
```

---

## File I/O

### Opening Files

#### `OPENINPUT(filename)` - Open for Reading
```basic
LET handle = OPENINPUT("data.txt")
IF handle >= 0.0 THEN
    PRINT "File opened successfully"
ELSE
    PRINT "Error opening file"
ENDIF
```

#### `OPENOUTPUT(filename)` - Open for Writing
```basic
LET handle = OPENOUTPUT("output.txt")
IF handle >= 0.0 THEN
    PRINT "File created successfully"
ENDIF
```

### Reading Files

#### `READLINE(handle)` - Read Single Line
```basic
LET handle = OPENINPUT("data.txt")
IF handle >= 0.0 THEN
    LET line = READLINE(handle)
    WHILE LEN(line) > 0.0
        PRINT line
        LET line = READLINE(handle)
    ENDWHILE
    LET dummy = CLOSEFILE(handle)
ENDIF
```

### Writing Files

#### `WRITELINE(handle, text)` - Write Line with Newline
```basic
LET handle = OPENOUTPUT("log.txt")
IF handle >= 0.0 THEN
    CALL WRITELINE(handle, "First line")
    CALL WRITELINE(handle, "Second line")
    LET dummy = CLOSEFILE(handle)
ENDIF
```

#### `WRITETEXT(handle, text)` - Write Text Without Newline
```basic
LET handle = OPENOUTPUT("output.txt")
IF handle >= 0.0 THEN
    CALL WRITETEXT(handle, "Hello ")
    CALL WRITETEXT(handle, "World")
    LET dummy = CLOSEFILE(handle)
    REM File contains: "Hello World"
ENDIF
```

### Closing Files

#### `CLOSEFILE(handle)` - Close File
```basic
LET handle = OPENINPUT("data.txt")
REM ... do file operations ...
LET dummy = CLOSEFILE(handle)
```

### File Management

#### `FILEEXISTS(filename)` - Check if File Exists
```basic
IF FILEEXISTS("config.txt") THEN
    PRINT "Config file found"
ELSE
    PRINT "Config file not found"
ENDIF
```

#### `DELETEFILE(filename)` - Delete File
```basic
IF FILEEXISTS("temp.txt") THEN
    IF DELETEFILE("temp.txt") THEN
        PRINT "File deleted"
    ELSE
        PRINT "Error deleting file"
    ENDIF
ENDIF
```

### Complete File I/O Example

```basic
REM Write to file
LET out = OPENOUTPUT("numbers.txt")
IF out >= 0.0 THEN
    FOR i = 1.0 TO 10.0
        CALL WRITELINE(out, STR(i))
    NEXT i
    LET dummy = CLOSEFILE(out)
    PRINT "File written"
ENDIF

REM Read from file
LET in = OPENINPUT("numbers.txt")
IF in >= 0.0 THEN
    LET line = READLINE(in)
    WHILE LEN(line) > 0.0
        PRINT "Read: "; line
        LET line = READLINE(in)
    ENDWHILE
    LET dummy = CLOSEFILE(in)
ENDIF
```

---

## Regular Expressions

### Pattern Matching

#### `REGEXMATCH(text, pattern)` - Test if Pattern Matches
```basic
IF REGEXMATCH("hello123", "[0-9]+") THEN
    PRINT "Contains numbers"
ENDIF

IF REGEXMATCH("test@example.com", "\\w+@\\w+\\.\\w+") THEN
    PRINT "Valid email format"
ENDIF
```

#### `REGEXFIND(text, pattern)` - Find First Match
```basic
LET email = "Contact: john@example.com for info"
LET match = REGEXFIND(email, "\\w+@\\w+\\.\\w+")
PRINT match   REM Output: "john@example.com"

REM Returns empty string if not found
LET result = REGEXFIND("no email here", "\\w+@\\w+")
IF LEN(result) = 0.0 THEN
    PRINT "No match found"
ENDIF
```

### Pattern Replacement

#### `REGEXREPLACE(text, pattern, replacement)` - Replace Matches
```basic
REM Replace all digits with X
LET text = "Code: 12345"
LET result = REGEXREPLACE(text, "[0-9]", "X")
PRINT result   REM Output: "Code: XXXXX"

REM Replace whitespace with underscores
LET text = "Hello World Test"
LET result = REGEXREPLACE(text, "\\s+", "_")
PRINT result   REM Output: "Hello_World_Test"
```

### Capture Groups

#### `REGEXGROUP(text, pattern, groupIndex)` - Extract Capture Group
```basic
REM Extract date components
LET date = "Date: 2025-10-12"
LET year = REGEXGROUP(date, "(\\d{4})-(\\d{2})-(\\d{2})", 1)
LET month = REGEXGROUP(date, "(\\d{4})-(\\d{2})-(\\d{2})", 2)
LET day = REGEXGROUP(date, "(\\d{4})-(\\d{2})-(\\d{2})", 3)

PRINT "Year: "; year     REM Output: "2025"
PRINT "Month: "; month   REM Output: "10"
PRINT "Day: "; day       REM Output: "12"

REM Extract email parts
LET email = "john.doe@example.com"
LET user = REGEXGROUP(email, "(\\w+)@(\\w+\\.\\w+)", 1)
LET domain = REGEXGROUP(email, "(\\w+)@(\\w+\\.\\w+)", 2)
PRINT "User: "; user       REM Output: "john"
PRINT "Domain: "; domain   REM Output: "example.com"
```

### Common Regex Patterns

```basic
REM Validate phone number
IF REGEXMATCH(phone, "\\d{3}-\\d{3}-\\d{4}") THEN
    PRINT "Valid phone: ###-###-####"
ENDIF

REM Validate ZIP code
IF REGEXMATCH(zip, "\\d{5}") THEN
    PRINT "Valid 5-digit ZIP"
ENDIF

REM Extract all numbers
LET text = "Order 123 costs $45.99"
LET number1 = REGEXFIND(text, "\\d+")
PRINT number1   REM Output: "123"

REM Check for alphanumeric
IF REGEXMATCH(username, "^[a-zA-Z0-9]+$") THEN
    PRINT "Valid username"
ENDIF

REM URL validation (simple)
IF REGEXMATCH(url, "https?://\\w+\\.\\w+") THEN
    PRINT "Looks like a URL"
ENDIF
```

---

## Examples

### Example 1: Temperature Converter

```basic
REM Temperature converter
PRINT "Temperature Converter"
PRINT "===================="
PRINT ""

LET celsius = VAL(INPUT("Enter temperature in Celsius: "))
LET fahrenheit = (celsius * 9.0 / 5.0) + 32.0
LET kelvin = celsius + 273.15

PRINT ""
PRINT "Results:"
PRINT "Celsius:    "; celsius
PRINT "Fahrenheit: "; fahrenheit
PRINT "Kelvin:     "; kelvin
```

### Example 2: Fibonacci Sequence

```basic
REM Generate Fibonacci sequence
FUNCTION fib(n)
    IF n <= 1.0 THEN
        RETURN n
    ELSE
        RETURN fib(n - 1.0) + fib(n - 2.0)
    ENDIF
ENDFUNCTION

PRINT "Fibonacci Sequence (first 10 numbers):"
FOR i = 0.0 TO 9.0
    PRINT "fib("; i; ") = "; fib(i)
NEXT i
```

### Example 3: Simple Calculator

```basic
REM Simple calculator
PRINT "Simple Calculator"
PRINT "================="

LET a = VAL(INPUT("Enter first number: "))
LET op = INPUT("Enter operator (+, -, *, /): ")
LET b = VAL(INPUT("Enter second number: "))

LET result = 0.0

IF op = "+" THEN
    LET result = a + b
ENDIF
IF op = "-" THEN
    LET result = a - b
ENDIF
IF op = "*" THEN
    LET result = a * b
ENDIF
IF op = "/" THEN
    IF b <> 0.0 THEN
        LET result = a / b
    ELSE
        PRINT "Error: Division by zero"
        STOP
    ENDIF
ENDIF

PRINT ""
PRINT a; " "; op; " "; b; " = "; result
```

### Example 4: Array Statistics

```basic
REM Calculate statistics for an array
SUB printStats(arr, size)
    PRINT "Array Statistics:"
    PRINT "  Count:   "; size
    PRINT "  Sum:     "; ARRAYSUM(arr)
    PRINT "  Min:     "; ARRAYMIN(arr)
    PRINT "  Max:     "; ARRAYMAX(arr)
    PRINT "  Average: "; ARRAYAVG(arr)
ENDSUB

DIM numbers(10)
FOR i = 0.0 TO 9.0
    LET numbers(INT(i)) = RNDINT(1, 100)
NEXT i

PRINT "Random numbers:"
FOR i = 0.0 TO 9.0
    PRINT numbers(INT(i)); " ";
NEXT i
PRINT ""
PRINT ""

CALL printStats(numbers, 10.0)
```

### Example 5: Text File Processing

```basic
REM Count words in a file
FUNCTION countWords(filename)
    LET count = 0.0
    LET handle = OPENINPUT(filename)
    
    IF handle >= 0.0 THEN
        LET line = READLINE(handle)
        WHILE LEN(line) > 0.0
            REM Split line into words
            LET words = SPLIT(line, " ")
            LET count = count + FLOAT(ARRAYLEN(words))
            LET line = READLINE(handle)
        ENDWHILE
        LET dummy = CLOSEFILE(handle)
    ENDIF
    
    RETURN count
ENDFUNCTION

LET filename = INPUT("Enter filename: ")
IF FILEEXISTS(filename) THEN
    LET words = countWords(filename)
    PRINT "File contains "; words; " words"
ELSE
    PRINT "File not found: "; filename
ENDIF
```

### Example 6: Password Generator

```basic
REM Generate random password
FUNCTION generatePassword(length)
    LET password = ""
    LET chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
    LET charLen = LEN(chars)
    
    FOR i = 1.0 TO length
        LET idx = RNDINT(0, INT(charLen) - 1)
        LET char = MID(chars, idx, 1)
        LET password = password + char
    NEXT i
    
    RETURN password
ENDFUNCTION

LET len = VAL(INPUT("Enter password length: "))
LET password = generatePassword(len)
PRINT "Generated password: "; password
```

### Example 7: Email Validator

```basic
REM Email validator using regex
FUNCTION isValidEmail(email)
    REM Check basic email pattern
    IF NOT REGEXMATCH(email, "^[\\w.]+@[\\w.]+\\.[a-zA-Z]{2,}$") THEN
        RETURN false
    ENDIF
    
    REM Extract parts
    LET user = REGEXGROUP(email, "([\\w.]+)@([\\w.]+)", 1)
    LET domain = REGEXGROUP(email, "([\\w.]+)@([\\w.]+)", 2)
    
    REM Check user part
    IF LEN(user) < 1.0 THEN
        RETURN false
    ENDIF
    
    REM Check domain part
    IF LEN(domain) < 3.0 THEN
        RETURN false
    ENDIF
    
    RETURN true
ENDFUNCTION

LET email = INPUT("Enter email address: ")
IF isValidEmail(email) THEN
    PRINT "Valid email address"
ELSE
    PRINT "Invalid email address"
ENDIF
```

### Example 8: Bubble Sort Implementation

```basic
REM Bubble sort algorithm
SUB bubbleSort(arr, size)
    FOR i = 0.0 TO size - 2.0
        FOR j = 0.0 TO size - i - 2.0
            LET idx1 = INT(j)
            LET idx2 = INT(j + 1.0)
            IF arr(idx1) > arr(idx2) THEN
                REM Swap elements
                LET temp = arr(idx1)
                LET arr(idx1) = arr(idx2)
                LET arr(idx2) = temp
            ENDIF
        NEXT j
    NEXT i
ENDSUB

REM Create and fill array
DIM numbers(8)
LET numbers(0) = 64.0
LET numbers(1) = 34.0
LET numbers(2) = 25.0
LET numbers(3) = 12.0
LET numbers(4) = 22.0
LET numbers(5) = 11.0
LET numbers(6) = 90.0
LET numbers(7) = 88.0

PRINT "Unsorted array:"
FOR i = 0.0 TO 7.0
    PRINT numbers(INT(i)); " ";
NEXT i
PRINT ""

CALL bubbleSort(numbers, 8.0)

PRINT "Sorted array:"
FOR i = 0.0 TO 7.0
    PRINT numbers(INT(i)); " ";
NEXT i
PRINT ""
```

---

## Best Practices

### 1. Use Meaningful Variable Names
```basic
REM Good
LET customerName = "Alice"
LET totalPrice = 99.95

REM Avoid
LET cn = "Alice"
LET tp = 99.95
```

### 2. Add Comments
```basic
REM Calculate compound interest
REM A = P(1 + r/n)^(nt)
LET principal = 1000.0
LET rate = 0.05
LET years = 10.0
LET amount = principal * POW(1.0 + rate, years)
```

### 3. Use Functions for Reusable Code
```basic
FUNCTION isLeapYear(year)
    IF INT(year) MOD 4 = 0 THEN
        IF INT(year) MOD 100 = 0 THEN
            IF INT(year) MOD 400 = 0 THEN
                RETURN true
            ELSE
                RETURN false
            ENDIF
        ELSE
            RETURN true
        ENDIF
    ELSE
        RETURN false
    ENDIF
ENDFUNCTION
```

### 4. Validate Input
```basic
LET age = VAL(INPUT("Enter age: "))
IF age < 0.0 OR age > 150.0 THEN
    PRINT "Invalid age"
    STOP
ENDIF
```

### 5. Close Files After Use
```basic
LET handle = OPENINPUT("data.txt")
IF handle >= 0.0 THEN
    REM ... process file ...
    LET dummy = CLOSEFILE(handle)
ENDIF
```

### 6. Use Type Conversion Explicitly
```basic
REM When indexing arrays, explicitly convert to INT
FOR i = 0.0 TO 9.0
    LET arr(INT(i)) = i * 2.0
NEXT i
```

---

## Troubleshooting

### Common Errors

#### "Variable not declared"
Make sure to declare arrays with `DIM` before use:
```basic
DIM myArray(10)  REM Declare first
LET myArray(0) = 42.0
```

#### "Type mismatch"
Ensure proper type conversions:
```basic
LET s = INPUT("Enter number: ")
LET x = VAL(s)  REM Convert string to number
```

#### "Index out of bounds"
Check array indices:
```basic
DIM arr(10)  REM Valid indices: 0-9
LET arr(5) = 42.0  REM OK
LET arr(10) = 42.0  REM Error! Index out of bounds
```

#### "Division by zero"
Always check before dividing:
```basic
IF denominator <> 0.0 THEN
    LET result = numerator / denominator
ELSE
    PRINT "Error: Division by zero"
ENDIF
```

---

## Appendix: Complete Built-in Function Reference

| Category | Function | Description |
|----------|----------|-------------|
| **Math** | `ABS(x)` | Absolute value |
| | `SQRT(x)` | Square root |
| | `POW(x, y)` | Power (x^y) |
| | `SIN(x)` | Sine (radians) |
| | `COS(x)` | Cosine (radians) |
| | `TAN(x)` | Tangent (radians) |
| | `ASIN(x)` | Arc sine |
| | `ACOS(x)` | Arc cosine |
| | `ATAN(x)` | Arc tangent |
| | `ATAN2(y, x)` | Two-argument arc tangent |
| | `LOG(x)` | Natural logarithm |
| | `LOG10(x)` | Base-10 logarithm |
| | `EXP(x)` | Exponential (e^x) |
| | `FLOOR(x)` | Round down |
| | `CEIL(x)` | Round up |
| | `ROUND(x)` | Round to nearest |
| | `MIN(x, y)` | Minimum |
| | `MAX(x, y)` | Maximum |
| | `PI()` | Pi constant |
| | `E()` | Euler's number |
| | `SIGN(x)` | Sign of number |
| | `HYPOT(x, y)` | Hypotenuse |
| **Random** | `RND()` | Random float [0,1) |
| | `RNDINT(min, max)` | Random integer |
| | `RANDOMSEED(seed)` | Set random seed |
| **Type** | `INT(x)` | Convert to integer |
| | `FLOAT(x)` | Convert to float |
| | `STR(x)` | Convert to string |
| | `VAL(s)` | String to number |
| | `BOOL(x)` | Convert to boolean |
| **String** | `LEN(s)` | String length |
| | `LEFT(s, n)` | Left substring |
| | `RIGHT(s, n)` | Right substring |
| | `MID(s, start, len)` | Middle substring |
| | `UPPER(s)` | To uppercase |
| | `LOWER(s)` | To lowercase |
| | `TRIM(s)` | Remove whitespace |
| | `REPLACE(s, old, new)` | Replace substring |
| | `INDEXOF(s, sub)` | Find position |
| | `SUBSTRING(s, start, end)` | Extract range |
| | `STARTSWITH(s, prefix)` | Check prefix |
| | `ENDSWITH(s, suffix)` | Check suffix |
| | `CONTAINS(s, sub)` | Check contains |
| | `CHR(code)` | ASCII to char |
| | `ASC(s)` | Char to ASCII |
| | `SPLIT(s, delim)` | Split to array |
| | `JOIN(arr, delim)` | Join to string |
| | `FORMAT(template, val)` | Format string |
| **Array** | `ARRAYLEN(arr)` | Array length |
| | `ARRAYGET(arr, idx)` | Get element |
| | `ARRAYSET(arr, idx, val)` | Set element |
| | `ARRAYSORT(arr)` | Sort numeric array |
| | `ARRAYSORTSTR(arr)` | Sort string array |
| | `ARRAYSUM(arr)` | Sum elements |
| | `ARRAYMIN(arr)` | Minimum element |
| | `ARRAYMAX(arr)` | Maximum element |
| | `ARRAYAVG(arr)` | Average |
| | `ARRAYREVERSE(arr)` | Reverse array |
| | `ARRAYFIND(arr, val)` | Find value |
| | `ARRAYCOPY(src, dest)` | Copy array |
| | `ARRAYFILL(arr, val)` | Fill with value |
| **File I/O** | `OPENINPUT(file)` | Open for reading |
| | `OPENOUTPUT(file)` | Open for writing |
| | `READLINE(handle)` | Read line |
| | `WRITELINE(handle, text)` | Write line |
| | `WRITETEXT(handle, text)` | Write text |
| | `CLOSEFILE(handle)` | Close file |
| | `FILEEXISTS(file)` | Check if exists |
| | `DELETEFILE(file)` | Delete file |
| **Regex** | `REGEXMATCH(text, pat)` | Test pattern |
| | `REGEXFIND(text, pat)` | Find match |
| | `REGEXREPLACE(text, pat, rep)` | Replace |
| | `REGEXGROUP(text, pat, idx)` | Extract group |
| **I/O** | `INPUT(prompt)` | Read user input |
| | `PRINT` | Display output |

---

## License and Support

JVM BASIC is an open-source project. For issues, examples, and updates, visit the documentation in the `docs/` folder.

**Total Built-in Functions: 93**

---

*End of User Guide*

