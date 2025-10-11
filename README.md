## jvmbasic — A tiny BASIC-like compiler that emits JVM bytecode

This project is a minimal compiler for a small BASIC-like language written in modern C++. It reads source from stdin and writes a valid Java `.class` file (`BasicProg.class`) that can be disassembled with `javap` and executed on any JVM.

### Features
- **Statements**: `PRINT`, `LET`, `INPUT`, `DIM`, `IF...THEN...ELSE...ENDIF`, `FOR...TO...STEP...NEXT`, `WHILE...ENDWHILE`, `DO...WHILE/UNTIL` (no semicolons required!)
- **Expressions**: integers, floats, strings, booleans, variable references, parentheses, and `+ - * / % (MOD)` with precedence
- **Comparisons**: `< > <= >= == <>` for numeric, string, and boolean values
- **Types**: `Int`, `Float`, `String`, `Bool` with simple numeric promotion (Int→Float)
- **Arrays**: One-dimensional typed arrays with `DIM arr(size) = initValue`
- **Control Flow**: IF/THEN/ELSEIF/ELSE/ENDIF with cascading conditions, nested support
- **Loops**: FOR...TO...STEP...NEXT, WHILE...ENDWHILE, DO...WHILE/UNTIL with full nesting
- **Output**: Multi-argument PRINT with traditional BASIC `,` (space) and `;` (no space) separators
- **Input**: INPUT statement with automatic type conversion based on variable type
- **Built-in Functions**: 40+ math and string functions (see below)
- **I/O**: Uses `java/lang/System.out` for output, `java.util.Scanner` for input, `basicrt.BasicRuntime` for functions

### Quick start
1) Build (macOS or Linux, C++20):

```bash
clang++ -std=c++20 -O2 /Volumes/externalssd/Development/jvm-basic/jvmbasic.cpp -o /Volumes/externalssd/Development/jvm-basic/jvmbasic
# or
g++ -std=gnu++020 -O2 /Volumes/externalssd/Development/jvm-basic/jvmbasic.cpp -o /Volumes/externalssd/Development/jvm-basic/jvmbasic
```

2) Run the example program:

```bash
./jvmbasic < input.bas
javap -c BasicProg
java BasicProg
```

You should see the output:

```text
3
7.0
Hello
```

### Built-in Functions

**Math Functions:**
- `ABS(x)` - Absolute value
- `SQR(x)`, `SQRT(x)` - Square root
- `INT(x)` - Integer part (floor)
- `SGN(x)` - Sign (-1, 0, 1)
- `ROUND(x)`, `CEIL(x)`, `FLOOR(x)` - Rounding
- `POW(x, y)` - x raised to power y
- `MIN(a, b)`, `MAX(a, b)` - Minimum/maximum
- `SIN(x)`, `COS(x)`, `TAN(x)` - Trigonometry (radians)
- `ASIN(x)`, `ACOS(x)`, `ATAN(x)`, `ATAN2(y,x)` - Inverse trig
- `EXP(x)`, `LOG(x)`, `LOG10(x)` - Exponential and logarithms
- `RND()` - Random 0.0-1.0
- `RNDI(x)` - Random integer to x
- `RNDINT(x,y)` - Ranged random integer x to y inclusive
- `PI()`, `E()` - Mathematical constants

**String Functions:**
- `LEN(s)` - String length
- `LEFT(s, n)`, `RIGHT(s, n)` - Extract characters
- `MID(s, start, len)`, `SUBSTR(s, start, len)` - Substring
- `UPPER(s)`, `UCASE(s)` - Convert to uppercase
- `LOWER(s)`, `LCASE(s)` - Convert to lowercase
- `TRIM(s)`, `LTRIM(s)`, `RTRIM(s)` - Remove whitespace
- `REVERSE(s)` - Reverse string
- `ASC(s)` - ASCII code of first character
- `CHR(n)` - Character from ASCII code
- `INSTR(haystack, needle)` - Find substring position
- `CONTAINS(haystack, needle)` - Check if contains substring
- `SPACE(n)` - String of n spaces
- `STRING(n, c)` - Repeat character n times
- `VAL(s)` - Convert string to number
- `ISNUM(s)`, `ISINT(s)` - Type checking

**Array Functions:**
- `MINARRAY(arr)` - Find minimum value in array
- `MAXARRAY(arr)` - Find maximum value in array
- `SUMARRAY(arr)` - Sum all array elements
- `UBOUND(arr)` - Get upper bound (size - 1) of array

### Language reference (current subset)
- **Program**: a sequence of statements (no semicolons required!)
- **Statements**:
  - `PRINT [<expr> [{, | ;}  <expr>]* [, | ;]?]` - Print expressions with optional separators
    - Comma (`,`): adds space between values
    - Semicolon (`;`): no space between values
    - Trailing `,` or `;`: suppresses final newline
  - `LET <ID> = <expr>` - Variable assignment
  - `LET <ID>(<index>) = <expr>` - Array element assignment
  - `INPUT <ID>` - Read input from stdin (variable must be defined first)
    - Converts input automatically: Int→`Integer.parseInt()`, Float→`Float.parseFloat()`, Bool→check "true", String→direct
  - `DIM <ID>(<size>) = <initValue>` - Declare and initialize array
    - Size must be integer expression
    - Init value determines array type (Int, Float, String, or Bool)
    - All elements initialized to the given value
  - `IF <expr> THEN <stmt>* [ELSEIF <expr> THEN <stmt>*]* [ELSE <stmt>*] ENDIF` - Conditional execution
  - `FOR <ID> = <start> TO <end> [STEP <step>] ... NEXT [<ID>]` - FOR loop
    - Default step is 1
    - Works with Int and Float
    - Loop variable automatically defined
  - `WHILE <expr> ... ENDWHILE` (or `WEND`) - WHILE loop
    - Condition checked before each iteration
  - `DO ... WHILE <expr>` - DO-WHILE loop (executes at least once)
  - `DO ... UNTIL <expr>` - DO-UNTIL loop (executes at least once)
- **Expressions**:
  - Literals: integer (`1`, `42`), float (`3.14`, `.5`), string (`"Hello"`), boolean (`true`, `false` - case insensitive)
  - Variables: `<ID>` defined by a previous `LET`
  - Arrays: `<ID>(<index>)` - array element access
  - Function calls: `FUNC(args)` - 40+ built-in functions
  - Parentheses: `(<expr>)`
  - Binary ops: `*` and `/` bind tighter than `+` and `-`
  - Comparisons: `< > <= >= == <>` (relational ops bind looser than arithmetic)
  - Type rules: numeric ops must be numeric; `Int` is promoted to `Float` when mixed
  - Comparison rules: numeric comparisons use epsilon for floats; string comparisons use `.equals()`
  - Function calls support nested expressions and auto Int→Float promotion

### How it works (high level)
- The single file `jvmbasic.cpp` contains:
  - A small **lexer** that recognizes numbers, strings, identifiers, keywords (`PRINT`, `LET`) and symbols
  - A **recursive-descent parser** that builds a typed AST for expressions and statements
  - A very small **classfile generator** that constructs a constant pool, emits JVM bytecode for `main`, and writes a valid `.class`
- The compiler currently generates one class `BasicProg` with a single `public static void main(String[] args)` method. Local variables are allocated in JVM local slots starting at index 1 (index 0 is the implicit `args`).

### Example
```basic
PRINT 1 + 2
LET A = 3.5
PRINT A * 2
LET B = "Hello"
PRINT B
PRINT 7 % 3

LET flag = true
LET x = 10
LET y = 5

PRINT "Multi-argument PRINT:", x, y, flag

IF x > y THEN
    PRINT "x is greater"
ELSEIF x == y THEN
    PRINT "x equals y"
ELSE
    PRINT "x is less"
ENDIF

PRINT "Values:"; x; ","; y
PRINT "Loading...";
PRINT "done!"

LET name = ""
LET age = 0
PRINT "Enter your name:"
INPUT name
PRINT "Enter your age:"
INPUT age
PRINT "Hello", name, "you are", age, "years old"

DIM scores(3) = 0
LET scores(0) = 85
LET scores(1) = 92
LET scores(2) = 78

LET avg = (scores(0) + scores(1) + scores(2)) / 3
PRINT "Average score:", INT(avg)

LET greeting = UPPER("hello")
PRINT greeting, "has", LEN(greeting), "letters"

LET hypotenuse = SQR(POW(3, 2) + POW(4, 2))
PRINT "3-4-5 triangle hypotenuse:", hypotenuse

FOR i = 1 TO 10
    PRINT i
NEXT i

DIM numbers(10) = 0
FOR i = 0 TO 9
    LET numbers(i) = INT(RND() * 100)
NEXT i

LET total = SUMARRAY(numbers)
PRINT "Sum of random numbers:", total

LET x = 0
WHILE x < 5
    PRINT "Counting:", x
    LET x = x + 1
ENDWHILE
```

Disassembled `main` (abridged):

```text
getstatic java/lang/System.out : Ljava/io/PrintStream;
iconst_1
iconst_2
iadd
invokevirtual java/io/PrintStream.println (I)V
ldc 3.5f
fstore_1
getstatic java/lang/System.out : Ljava/io/PrintStream;
fload_1
iconst_2
i2f
fmul
invokevirtual java/io/PrintStream.println (F)V
ldc "Hello"
astore_2
getstatic java/lang/System.out : Ljava/io/PrintStream;
aload_2
invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
return
```

### Building blocks in the source
- `Lexer`: converts characters to tokens (`NUMBER`, `STRING`, `ID`, symbols) and recognizes `PRINT`/`LET`
- `Parser`: builds AST nodes for expressions (`Num`, `Str`, `Var`, `Bin`) and statements (`Print`, `Let`), checks types, and performs simple numeric promotion
- `ClassFile`: constant-pool builder and bytecode emitter for arithmetic, loads/stores, literals, and `println`

### Limitations (by design for simplicity)
- No user-defined functions/procedures (planned for Phase 5)
- No GOTO, GOSUB, or line numbers (classic BASIC compat - future)
- One-dimensional arrays only (multi-dimensional planned)
- No loop control statements (EXIT FOR, CONTINUE - future)
- No array procedure calls (SORT, REVERSE - needs procedure support)
- No file I/O (OPEN, CLOSE - planned)
- No user-defined types/structures (future)
- Minimal constant pool management (no deduplication)
- Targets Java 6 bytecode (avoids StackMapTable complexity)
- Requires `basicrt/BasicRuntime.class` in classpath for built-in functions

### Documentation

**For Users** (Learning the language):
- `README.md` - This file (complete language reference)
- `docs/user/showcase.bas` - Feature demonstration
- `docs/user/ultimate_demo.bas` - Comprehensive real-world example
- `docs/user/loops_showcase.bas` - All loop types demonstrated
- `docs/user/demo.bas` - Original demo

**For Developers** (Understanding and extending):
- `docs/dev/CODE_GUIDE.md` - ⭐ **START HERE!** Complete developer guide (1,000+ lines)
- `docs/dev/FEATURES.md` - Complete feature specification
- `docs/dev/FINAL_SUMMARY.md` - What was accomplished
- `docs/dev/SESSION_SUMMARY.md` - Development history
- `docs/dev/DEVELOPMENT_PLAN.md` - Feature roadmap
- `docs/dev/walkthrough.md` - Original code walkthrough
- `docs/dev/extending.md` - How to extend the language

**Planning and Ideas**:
- `docs/ideas/WISHLIST.md` - ⭐ Future features (25+ ideas, prioritized)
- `docs/ideas/LOOPS_PLAN.md` - Loop design document
- `docs/ideas/ARRAY_PLAN.md` - Array design document
- `docs/ideas/STDLIB_PLAN.md` - Standard library design

**Continuing Development**:
- `CONTINUATION.md` - ⭐ **Read this to continue development independently**

**Tests**: `tests/*.bas` - 31 comprehensive test files covering all features

### License
Public domain or MIT—choose what fits your needs. If you contribute, include a license header of your choice.


