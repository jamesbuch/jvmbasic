# JVM BASIC Code Structure Guide
**A detailed walkthrough for understanding and modifying the compiler**

---

## File Overview

### Source Files
- **jvmbasic.cpp** (~1,600 lines) - The complete compiler
- **BasicRuntime.java** (~470 lines) - Standard library helper class

### Documentation
- **README.md** - User-facing documentation
- **CODE_GUIDE.md** - This file (developer guide)
- **DEVELOPMENT_PLAN.md** - Feature roadmap
- **WISHLIST.md** - Future features
- **SESSION_SUMMARY.md** - Development history
- **LOOPS_PLAN.md**, **ARRAY_PLAN.md**, **STDLIB_PLAN.md** - Specific feature plans
- **walkthrough.md**, **extending.md**, **index.md** - Original documentation

### Test Files (31 .bas files)
- Comprehensive test coverage for every feature

---

## Architecture Overview

### The Pipeline

```
Source Code (stdin)
    ↓
┌─────────┐
│ Lexer   │ → Tokens (NUMBER, STRING, ID, keywords, symbols)
└─────────┘
    ↓
┌─────────┐
│ Parser  │ → Typed AST (Abstract Syntax Tree)
└─────────┘
    ↓
┌──────────┐
│ClassFile │ → JVM Bytecode
└──────────┘
    ↓
BasicProgram.class (output)
```

### Key Data Structures

```cpp
// Types
enum class Type { 
    Int, Float, String, Bool,              // Scalar types
    IntArray, FloatArray, StringArray, BoolArray  // Array types
};

// Operators
enum class Op { 
    Add, Sub, Mul, Div, Mod,               // Arithmetic
    Lt, Gt, Le, Ge, Eq, Ne                 // Comparisons
};

// Expressions (what produces a value)
enum class ExprKind { 
    Num,      // Numeric literal
    Str,      // String literal
    Var,      // Variable or array access
    Bin,      // Binary operation (a + b)
    BoolLit,  // Boolean literal (true/false)
    Cmp,      // Comparison (a < b)
    Call      // Function call
};

// Statements (what does something)
enum class StmtKind { 
    Print,    // Output
    Let,      // Assignment
    Input,    // Read input
    Dim,      // Array declaration
    If,       // Conditional
    For,      // FOR loop
    While,    // WHILE loop
    DoWhile   // DO-WHILE loop
};
```

---

## The Lexer (Lines ~210-330)

### What it does
Converts characters into tokens. Think of it as breaking "LET x = 5" into:
```
[LET] [ID:"x"] [ASSIGN] [NUMBER:5]
```

### Key Methods

**`nextToken()`** - Returns the next token from input
- Numbers: Recognizes `42`, `3.14`, `.5` (allows decimal on either side)
- Strings: Quoted with `"..."`, no escape sequences
- Identifiers: Letters followed by alphanumeric (`x`, `myVar`, `score1`)
- Keywords: Uppercase comparison (`PRINT`, `LET`, `FOR`, etc.)
- Symbols: `+`, `-`, `*`, `/`, `=`, `<`, `>`, etc.

### How to Add a New Token

1. Add to `TokenType` enum (line ~12)
2. Add recognition in `nextToken()`:
   - For symbols: Add in the `else if (!eof)` section
   - For keywords: Add in the `isalpha(ch)` section with uppercase comparison

**Example - Adding `^` for exponentiation**:
```cpp
// In TokenType enum:
enum class TokenType { ..., POW, ... };

// In nextToken():
else if (ch == '^') { read(); return {TokenType::POW}; }
```

---

## The Parser (Lines ~340-760)

### What it does
Builds an Abstract Syntax Tree (AST) from tokens. Checks types and enforces rules.

### Expression Parsing (Precedence Climbing)

The parser uses **recursive descent** with different methods for each precedence level:

```
parseExpr()       →  parseEq()         (==, <>)
                     ↓
                     parseRel()        (<, >, <=, >=)
                     ↓
                     parseAdd()        (+, -)
                     ↓
                     parseMul()        (*, /, %)
                     ↓
                     parsePrimary()    (numbers, strings, vars, functions, parens)
```

This ensures proper precedence: `2 + 3 * 4` parses as `2 + (3 * 4)`.

### Statement Parsing

**`parseStmt()`** - Dispatches based on keyword:
- Sees `PRINT` → calls print parsing logic
- Sees `LET` → calls assignment logic
- Sees `IF` → calls conditional logic
- etc.

### Type System

**`knownTypes`** map tracks variable types:
```cpp
map<string, Type> knownTypes;

// After parsing: LET x = 5
knownTypes["x"] = Type::Int;

// Later: LET x = "hello"  → ERROR (type mismatch)
```

### Adding New Statements

**Example - Adding REPEAT n TIMES**:

```cpp
// 1. Add to StmtKind
enum class StmtKind { ..., Repeat };

// 2. Create AST struct
struct RepeatStmt {
    ExprPtr count;
    vector<StmtPtr> body;
};

// 3. Add to Stmt variant
variant<..., RepeatStmt> data;

// 4. Add parsing in parseStmt():
else if (tok.type == TokenType::REPEAT) {
    next();
    auto count = parseExpr();
    expect(TokenType::TIMES);
    vector<StmtPtr> body;
    while (tok.type != TokenType::ENDREPEAT) {
        body.push_back(parseStmt());
    }
    expect(TokenType::ENDREPEAT);
    return make_unique<Stmt>(StmtKind::Repeat, RepeatStmt{move(count), move(body)});
}
```

---

## The ClassFile Generator (Lines ~760-1700)

### What it does
Emits JVM bytecode into a valid `.class` file.

### Constant Pool

Stores all constants used by the program:
- UTF8 strings (class names, method names, field names, descriptors)
- Class references
- Method references
- Field references
- String literals
- Float/Int constants

**Adding to constant pool**:
```cpp
u2 utf_idx = cp.addUtf8("Hello");        // Add UTF8
u2 str_idx = cp.addString(utf_idx);      // Add String constant
u2 class_idx = cp.addClass(utf_idx);     // Add Class
```

### Bytecode Emission

**Basic pattern**: Load values, perform operation, store result

**Example - Compiling `LET x = 5 + 3`**:
```cpp
iconst(5);      // Push 5
iconst(3);      // Push 3
iadd();         // Add (stack now has 8)
istore(varIdx["x"]);  // Store to local variable x
```

### Key Bytecode Methods

**Constants**:
- `iconst(i)` - Push int constant
- `fconst(f)` - Push float constant
- `ldc(idx)` - Load constant from pool (strings, large numbers)

**Loads (get value from local variable)**:
- `iload(idx)` - Load int from local
- `fload(idx)` - Load float from local
- `aload(idx)` - Load reference (String, array) from local

**Stores (put value into local variable)**:
- `istore(idx)` - Store int to local
- `fstore(idx)` - Store float to local
- `astore(idx)` - Store reference to local

**Arithmetic**:
- `iadd()`, `isub()`, `imul()`, `idiv()`, `irem()` - Integer ops
- `fadd()`, `fsub()`, `fmul()`, `fdiv()`, `frem()` - Float ops

**Arrays**:
- `newarray_int()` - Create int array
- `anewarray(classIdx)` - Create reference array (String[])
- `iaload()`, `iastore()` - Load/store int array element
- `faload()`, `fastore()` - Load/store float array element
- `aaload()`, `aastore()` - Load/store reference array element

**Branching**:
- `if_icmplt(L)` - Jump to L if top < second (integers)
- `if_icmpgt(L)`, `if_icmple(L)`, `if_icmpge(L)`, `if_icmpeq(L)`, `if_icmpne(L)`
- `ifeq(L)` - Jump to L if top == 0
- `ifne(L)` - Jump to L if top != 0
- `goto_(L)` - Unconditional jump

**Conversion**:
- `i2f()` - Convert int to float

**Method Calls**:
- `invokevirtual(idx)` - Call instance method (println, equals)
- `invokestatic(idx)` - Call static method (Math functions, BasicRuntime)
- `invokespecial(idx)` - Call constructor

**Object Creation**:
- `new_(idx)` - Create object
- `dup()` - Duplicate top of stack

### The `load()` Method - Core of Code Generation

This is the heart of expression evaluation. It emits bytecode to push an expression's value onto the stack:

```cpp
void load(const Expr& e, map<string, u1>& varIdx) {
    if (e.kind == ExprKind::Num) {
        // Emit iconst or fconst
    } else if (e.kind == ExprKind::Var) {
        // Emit iload/fload/aload
        // If array access: aload array, load index, iaload/faload/aaload
    } else if (e.kind == ExprKind::Bin) {
        // Recursively load left operand
        // Recursively load right operand
        // Emit operation (iadd, fadd, etc.)
    } else if (e.kind == ExprKind::Call) {
        // Load arguments
        // Emit invokestatic
    }
    // etc.
}
```

### The `genStmt()` Method - Statement Generation

Emits bytecode for statements:

```cpp
void genStmt(const Stmt& s, ...) {
    if (s.kind == StmtKind::Print) {
        // getstatic System.out
        // load expression
        // invokevirtual println
    } else if (s.kind == StmtKind::Let) {
        // load expression
        // store to variable
    } else if (s.kind == StmtKind::If) {
        // load condition
        // ifeq to else/endif
        // generate then-body
        // goto end
        // mark else label
        // generate else-body
        // mark end label
    }
    // etc.
}
```

---

## Label Management (Lines ~880-920)

Labels are used for jumps (if/else, loops, etc.).

### The Label Structure

```cpp
struct Label {
    int pos = -1;              // Position in bytecode (-1 = not set yet)
    vector<int> patchSites;    // Locations that need to jump here
};
```

### How It Works

1. **Create label**: `Label myLabel;`
2. **Jump to label**: `goto_(myLabel);` or `ifeq(myLabel);`
   - If label position unknown, adds to patchSites
3. **Mark label position**: `mark(myLabel);`
   - Sets position
   - Patches all pending jump sites

### Example - IF statement

```cpp
Label elseLabel, endLabel;

load(condition);
ifeq(elseLabel);           // Jump to else if false
// then-body
goto_(endLabel);           // Skip else
mark(elseLabel);           // Mark else position
// else-body  
mark(endLabel);            // Mark end position
```

### Backpatching

When we emit `goto_(label)` before knowing where the label is:
1. Emit jump instruction with placeholder (0x00, 0x00)
2. Record current position in label's patchSites
3. When `mark(label)` is called, compute offset and patch bytes

---

## Type System

### Type Promotion Rules

**Int → Float automatic**:
```basic
LET x = 5        # Int
LET y = 3.14     # Float
LET z = x + y    # Result is Float, x auto-promoted
```

**No other automatic conversions**:
```basic
LET s = "hello" + 5    # ERROR
LET b = true + 1       # ERROR (booleans are not numeric)
```

### Array Types

Arrays have separate types:
- `Type::IntArray` - Array of integers
- `Type::FloatArray` - Array of floats
- `Type::StringArray` - Array of strings
- `Type::BoolArray` - Array of booleans

**Array vs Element**:
```cpp
// Variable nums is IntArray
knownTypes["nums"] = Type::IntArray;

// Expression nums(0) has type Int
ExprKind::Var with index → type is Int (not IntArray)

// Expression nums (no index) has type IntArray (for passing to functions)
ExprKind::Var without index → type is IntArray
```

---

## Function Call System

### Built-in Function Registry

```cpp
static map<string, FunctionSig> builtinFunctions = {
    {"ABS", {{Type::Float}, Type::Float, "abs_f", "(F)F"}},
    //      ^params        ^return      ^method   ^descriptor
};
```

- **Params**: Vector of parameter types
- **Return**: Return type
- **Method**: Java method name in BasicRuntime
- **Descriptor**: JVM method descriptor

### Parsing Function Calls

In `parsePrimary()`:
1. See an ID
2. Check if it's in `builtinFunctions`
3. If yes, parse arguments
4. Return `CallExpr` with function name and args
5. Type = function's return type

### Generating Function Calls

In `load()` for `ExprKind::Call`:
1. Load each argument (recursively call `load()`)
2. Promote Int→Float if needed
3. Get method reference from constant pool (cached)
4. Emit `invokestatic basicrt/BasicRuntime.methodName`

---

## How Features Are Implemented

### Example 1: IF Statement

**Parser**:
```cpp
// IF condition THEN ... ELSE ... ENDIF
auto cond = parseExpr();
expect(THEN);
parse then-body statements
if ELSE: parse else-body
expect(ENDIF)
return IfStmt{cond, thenBody, elseBody}
```

**Codegen**:
```cpp
load(condition);          // Evaluate, push 0 or 1
Label elseL, endL;
ifeq(elseL);             // If 0, jump to else
genStmt(thenBody);       // Generate then code
goto_(endL);             // Skip else
mark(elseL);             // Place else label
genStmt(elseBody);       // Generate else code
mark(endL);              // Place end label
```

### Example 2: FOR Loop

**Parser**:
```cpp
// FOR i = 1 TO 10 STEP 2
var name = "i"
start = parseExpr()      // 1
TO
end = parseExpr()        // 10
STEP
step = parseExpr()       // 2
parse body
NEXT
return ForStmt{var, start, end, step, body}
```

**Codegen**:
```cpp
load(start);
istore(varIdx[var]);     // i = start

Label loopStart, loopEnd;
mark(loopStart);
iload(varIdx[var]);
load(end);
if_icmpgt(loopEnd);      // If i > end, exit

genStmt(body);           // Loop body

iload(varIdx[var]);
load(step);
iadd();
istore(varIdx[var]);     // i += step

goto_(loopStart);
mark(loopEnd);
```

### Example 3: Array Access

**Parser** - `nums(5)`:
```cpp
name = "nums"
see LPAREN
index = parseExpr()      // 5
RPAREN
varType = element type   // Int (from IntArray)
return VarRef{name, index} with type Int
```

**Codegen** - Load `nums(5)`:
```cpp
aload(varIdx["nums"]);   // Load array reference
load(index);             // Load 5
iaload();                // Load element (pushes int)
```

**Codegen** - Store `LET nums(5) = 42`:
```cpp
aload(varIdx["nums"]);   // Load array reference
load(index);             // Load 5
load(value);             // Load 42
iastore();               // Store element
```

---

## Local Variables

JVM methods have numbered local variable slots:
- Slot 0: `args` parameter (String[] for main)
- Slot 1: Scanner (for INPUT)
- Slot 2+: User variables in order of definition

```cpp
map<string, u1> varIdx;   // Maps variable name → slot number
u1 nextLocal = 1;         // Next available slot

// When defining variable:
if (!varIdx.count(varName)) {
    varIdx[varName] = nextLocal++;
    max_locals = max(max_locals, nextLocal);
}
```

---

## The Stack

JVM is stack-based. All operations work on the stack:

```
Expression: 5 + (3 * 2)

Bytecode:
  iconst 5       Stack: [5]
  iconst 3       Stack: [5, 3]
  iconst 2       Stack: [5, 3, 2]
  imul           Stack: [5, 6]        (3*2=6)
  iadd           Stack: [11]          (5+6=11)
```

`max_stack` tracks maximum stack depth (currently fixed at 10).

---

## How to Add a New Feature

### Step-by-Step Guide

#### 1. Add Tokens (if needed)
```cpp
enum class TokenType { ..., MYNEWTOKEN };

// In Lexer:
if (upper == "MYNEW") return {TokenType::MYNEWTOKEN};
```

#### 2. Add AST Structure
```cpp
struct MyNewStmt { 
    ExprPtr value;
    string name;
};

enum class StmtKind { ..., MyNew };
variant<..., MyNewStmt> data;
Stmt(StmtKind k, MyNewStmt m) : kind(k), data(std::move(m)) {}
```

#### 3. Add Parser Logic
```cpp
else if (tok.type == TokenType::MYNEWTOKEN) {
    next();
    // Parse your syntax
    auto value = parseExpr();
    string name = expect(TokenType::ID).val;
    return make_unique<Stmt>(StmtKind::MyNew, MyNewStmt{move(value), name});
}
```

#### 4. Add Code Generation
```cpp
else if (s.kind == StmtKind::MyNew) {
    const MyNewStmt& ms = get<MyNewStmt>(s.data);
    // Emit bytecode
    load(*ms.value, varIdx);
    // ... do something with it
}
```

#### 5. Test!
```basic
MYNEW 42 x
PRINT x
```

---

## Common Patterns

### Pattern 1: Binary Operation

```cpp
// Parse
auto left = parseXXX();
while (tok.type == OP) {
    next();
    auto right = parseXXX();
    left = BinOp{op, left, right};
}
return left;

// Codegen
load(left);
load(right);
iadd(); // or fadd, imul, etc.
```

### Pattern 2: Control Flow

```cpp
// Parse
auto condition = parseExpr();
vector<StmtPtr> body;
while (not at END) {
    body.push_back(parseStmt());
}

// Codegen
load(condition);
Label endLabel;
ifeq(endLabel);      // Exit if false
genStmt(body);
mark(endLabel);
```

### Pattern 3: Loops

```cpp
// Codegen
Label startLabel, endLabel;
mark(startLabel);
load(condition);
ifeq(endLabel);
genStmt(body);
goto_(startLabel);
mark(endLabel);
```

---

## Debugging Tips

### 1. Examine Generated Bytecode

```bash
./jvmbasic < program.bas
javap -c -v BasicProgram > bytecode.txt
```

Look for:
- Stack depth issues
- Wrong instruction opcodes
- Incorrect jump offsets
- Missing constant pool entries

### 2. Add Debug Prints

```cpp
void load(const Expr& e, ...) {
    cerr << "Loading expression kind: " << (int)e.kind << endl;
    // ... rest of code
}
```

### 3. Test Incrementally

Don't add 5 features at once. Add one, test it, commit, then add next.

### 4. Use Small Test Programs

```basic
LET x = 5
PRINT x
```

Much easier to debug than 100-line programs.

---

## JVM Bytecode Reference (Opcodes Used)

### Constants
- `0x02` - iconst_m1
- `0x03-0x08` - iconst_0 through iconst_5
- `0x0B-0x0D` - fconst_0, fconst_1, fconst_2
- `0x10` - bipush (byte immediate)
- `0x11` - sipush (short immediate)
- `0x12` - ldc (load from constant pool)
- `0x13` - ldc_w (wide index)

### Loads
- `0x15` - iload
- `0x17` - fload
- `0x19` - aload
- `0x1A-0x1D` - iload_0 through iload_3
- `0x22-0x25` - fload_0 through fload_3
- `0x2A-0x2D` - aload_0 through aload_3
- `0x2E` - iaload
- `0x30` - faload
- `0x32` - aaload
- `0x33` - baload

### Stores
- `0x36` - istore
- `0x38` - fstore
- `0x3A` - astore
- `0x3B-0x3E` - istore_0 through istore_3
- `0x43-0x46` - fstore_0 through fstore_3
- `0x4B-0x4E` - astore_0 through astore_3
- `0x4F` - iastore
- `0x51` - fastore
- `0x53` - aastore
- `0x54` - bastore

### Arithmetic
- `0x60` - iadd
- `0x62` - fadd
- `0x64` - isub
- `0x66` - fsub
- `0x68` - imul
- `0x6A` - fmul
- `0x6C` - idiv
- `0x6E` - fdiv
- `0x70` - irem
- `0x72` - frem

### Comparisons
- `0x95` - fcmpl
- `0x96` - fcmpg
- `0x99` - ifeq
- `0x9A` - ifne
- `0x9B` - iflt
- `0x9C` - ifge
- `0x9D` - ifgt
- `0x9E` - ifle
- `0x9F` - if_icmpeq
- `0xA0` - if_icmpne
- `0xA1` - if_icmplt
- `0xA2` - if_icmpge
- `0xA3` - if_icmpgt
- `0xA4` - if_icmple
- `0xA7` - goto

### Objects and Arrays
- `0x59` - dup
- `0xBB` - new
- `0xBC` - newarray
- `0xBD` - anewarray
- `0xB2` - getstatic
- `0xB6` - invokevirtual
- `0xB7` - invokespecial
- `0xB8` - invokestatic

### Returns
- `0xB1` - return (void)

### Conversions
- `0x86` - i2f

---

## Type Descriptors (JVM)

Used in method signatures:

- `I` - int
- `F` - float
- `Z` - boolean
- `Ljava/lang/String;` - String
- `[I` - int array
- `[F` - float array
- `[Ljava/lang/String;` - String array
- `V` - void (return type only)

**Method descriptor format**: `(parameters)returnType`

Examples:
- `(I)V` - void method(int)
- `(FF)F` - float method(float, float)
- `(Ljava/lang/String;)I` - int method(String)
- `([I)I` - int method(int[])

---

## Error Handling Strategy

Currently: `throw runtime_error("message")`

Caught in `main()` and printed to stderr.

**To improve errors**:
1. Add line number tracking in Lexer
2. Store line info in tokens
3. Include in error messages: "Line 42: Undefined variable: x"

---

## Performance Notes

### Current Approach: Simplicity Over Speed

- No constant pool deduplication (could add hash map)
- No optimization passes
- Fixed max_stack (could calculate actual max)
- Runtime array initialization (could optimize for constant init values)

### Easy Optimizations to Add

1. **Constant folding**: `5 + 3` → `8` at compile time
2. **Dead code elimination**: Remove unreachable code
3. **Peephole optimization**: Remove useless sequences
4. **Strength reduction**: `x * 2` → `x + x`

---

## Build System

### Compilation
```bash
./g++-15-wrapper -std=gnu++20 -O2 jvmbasic.cpp -o jvmbasic
```

Requires C++20 for:
- `variant<...>`
- `unique_ptr`
- Designated initializers (could remove if targeting C++17)

### Runtime
```bash
javac -d . BasicRuntime.java     # Compile helper class
./jvmbasic < program.bas         # Compile BASIC program
java -cp . BasicProgram          # Run (needs BasicRuntime in classpath)
```

---

## Extending the Standard Library

### Adding a New Function

**1. Add to BasicRuntime.java**:
```java
public static float myFunc(float x) {
    return x * x + 1;
}
```

**2. Add to function registry**:
```cpp
{"MYFUNC", {{Type::Float}, Type::Float, "myFunc", "(F)F"}},
```

**3. Recompile both**:
```bash
javac -d . BasicRuntime.java
./g++-15-wrapper -std=gnu++20 -O2 jvmbasic.cpp -o jvmbasic
```

**4. Use in BASIC**:
```basic
PRINT MYFUNC(5)    # Prints 26.0
```

That's it! No changes to parser or codegen needed - the function call system handles it automatically.

---

## Key Files to Understand

### Start with these in order:

1. **Token and Type enums** (lines 10-17)
   - Understand what tokens exist
   - Understand the type system

2. **AST structures** (lines 24-110)
   - How expressions are represented
   - How statements are represented

3. **Lexer.nextToken()** (lines ~250-330)
   - Character → Token conversion
   - Relatively simple, no complex state

4. **Parser.parsePrimary()** (lines ~360-465)
   - How the simplest expressions are parsed
   - Good entry point to understand recursion

5. **Parser.parseStmt()** (lines ~530-760)
   - How each statement type is parsed
   - See the pattern repeated for each

6. **ClassFile.load()** (lines ~1250-1320)
   - How expressions turn into bytecode
   - Mirrors parser structure

7. **ClassFile.genStmt()** (lines ~1360-1630)
   - How statements turn into bytecode
   - See loops, conditions, etc.

---

## Modifying vs. Adding

### Modifying Existing Features

**Changing PRINT behavior**:
- Find `StmtKind::Print` in `genStmt()`
- Modify bytecode generation
- No parser changes needed

**Adding new operator** (e.g., `**` for power):
- Add to `Op` enum
- Add to `TokenType` enum
- Add recognition in Lexer
- Add parsing in appropriate layer (probably `parseMul`)
- Add codegen in `load()` for `ExprKind::Bin`

### Adding New Features

**New statement type**: Follow the 5-step guide above

**New expression type**: Add to `ExprKind`, create struct, add parsing in expression layers, add `load()` case

**New built-in function**: Just add to registry and BasicRuntime (easiest!)

---

## Testing Your Changes

### Quick Test Cycle

```bash
./g++-15-wrapper -std=gnu++20 -O2 jvmbasic.cpp -o jvmbasic
echo 'PRINT "test"' | ./jvmbasic && java -cp . BasicProgram
```

### Full Test

```bash
./buildrun.sh    # Compiles BasicRuntime, jvmbasic, runs input.bas
```

### Bytecode Verification

```bash
javap -c -v BasicProgram | less
```

Look at the bytecode to ensure it's correct.

---

## Common Pitfalls

### 1. Forgetting to call `next()`
```cpp
if (tok.type == TokenType::ID) {
    // MUST call next() before parsing more!
    next();
}
```

### 2. Type Mismatches
Check types in parser, not just in codegen. Fail fast with good errors.

### 3. Label Backpatching
Always `mark()` labels eventually, or jumps will point to garbage.

### 4. Stack Balance
Every path through code should have same stack depth. The JVM verifier will catch this.

### 5. Local Variable Indices
Don't reuse indices for different variables. Each variable gets its own slot.

---

## The Beauty of This Design

### Why It's Simple

1. **Single-pass**: Parse and type-check in one go
2. **No IR**: AST directly to bytecode (no intermediate representation)
3. **No optimization**: Correctness over speed
4. **Minimal JVM**: Using ~30 instructions total (JVM has 200+)
5. **Helper class**: Complex functions in Java, not bytecode emission

### Why It's Extensible

1. **Visitor pattern**: Each Stmt/Expr kind handled separately
2. **Function registry**: Add functions without code changes
3. **Type system**: Extensible Type enum
4. **Clean separation**: Lexer, Parser, Codegen are independent

---

## Next Steps for Independent Development

### To add FOR EACH loop:

1. Add `FOREACH`, `IN` tokens
2. Create `ForEachStmt{var, array, body}`
3. Parse: `FOR EACH var IN array ... NEXT`
4. Codegen:
   ```
   counter = 0
   L_start:
   if counter >= array.length goto L_end
   var = array[counter]
   <body>
   counter++
   goto L_start
   L_end:
   ```

### To add GOTO:

1. Add `LABEL` token for label definitions
2. Parse labels into map
3. GOTO emits jump to label
4. Validate all labels exist at end of parsing

### To add string concatenation:

1. Overload `+` operator for strings
2. In `parseAdd()`, allow String + String
3. Use `StringBuilder` or just call `String.concat()`

---

## Conclusion

This compiler is a beautiful example of how much you can accomplish with clean, simple code. The entire compiler is ~1,600 lines and implements a remarkably complete language.

**Core principles**:
- Keep it simple
- Test incrementally
- Document as you go
- Extend through patterns

You now have all the knowledge to modify and extend JVM BASIC independently! 🎉

