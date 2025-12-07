# JVM BASIC 2.0 Compiler Architecture

This document provides a deep dive into the JVM BASIC 2.0 compiler architecture, compilation phases, and design decisions.

## Table of Contents

1. [Overview](#overview)
2. [Compilation Pipeline](#compilation-pipeline)
3. [ANTLR Listener vs Visitor Pattern](#antlr-listener-vs-visitor-pattern)
4. [Semantic Analysis](#semantic-analysis)
5. [Symbol Table and Scoping](#symbol-table-and-scoping)
6. [Type System](#type-system)
7. [Code Generation](#code-generation)
8. [Command Line Interface](#command-line-interface)
9. [Future: Concurrency Support](#future-concurrency-support)

---

## Overview

The JVM BASIC 2.0 compiler transforms BASIC source code into JVM bytecode through a multi-phase pipeline. Key technologies:

- **ANTLR 4.13.2** - Parser generator for lexer and parser
- **ASM 9.9** - JVM bytecode manipulation library
- **Java 21** - Target JVM version with modern features
- **Gradle 9.2.1** - Build system

### Design Principles

1. **Two-pass compilation** - Symbol collection before code generation
2. **Direct bytecode generation** - No intermediate class file manipulation
3. **Block-level scoping** - Modern variable scoping like Java/C#
4. **Static namespace methods** - Standard library via static method calls
5. **JVM interoperability** - Generated code runs on any JVM

---

## Compilation Pipeline

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Source Code (.jvmb)                          │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      PHASE 1: Lexical Analysis                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  JvmBasicLexer (ANTLR4)                                     │    │
│  │  - Tokenizes source into token stream                       │    │
│  │  - Handles case-insensitive keywords (VAR, var, Var)        │    │
│  │  - Supports multiple number formats (hex, binary, octal)    │    │
│  │  - Processes string escape sequences                        │    │
│  │  - Handles line continuation with _                         │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
                                   │ Token Stream
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      PHASE 2: Parsing                                │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  JvmBasicParser (ANTLR4)                                    │    │
│  │  - Builds Concrete Syntax Tree (CST/Parse Tree)             │    │
│  │  - Validates grammatical structure                          │    │
│  │  - Reports syntax errors with line/column info              │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
                                   │ Parse Tree
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                   PHASE 3: Symbol Collection (Pass 1)                │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  SymbolCollector (ANTLR Listener Pattern)                   │    │
│  │  - Automatic tree traversal via ParseTreeWalker             │    │
│  │  - Collects function/sub signatures before use              │    │
│  │  - Gathers class declarations (fields, methods)             │    │
│  │  - Tracks variable declarations per scope                   │    │
│  │  - Detects duplicate symbol definitions                     │    │
│  │  - Builds scope hierarchy                                   │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
                                   │ SymbolTable
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                   PHASE 4: IR Generation (Optional)                  │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  IRBuilder (ANTLR Visitor Pattern)                          │    │
│  │  - Transforms parse tree to typed IR AST                    │    │
│  │  - Resolves type information on all nodes                   │    │
│  │  - Simplifies structure (removes parse tree noise)          │    │
│  │  - Used for debugging (-ir flag) and future optimization    │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
                                   │ IR Tree (optional)
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                   PHASE 5: Semantic Analysis (Optional)              │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  SemanticAnalyzer                                           │    │
│  │  - Type checking (assignment compatibility)                 │    │
│  │  - Reference validation (variables, functions exist)        │    │
│  │  - Parameter validation (count, types)                      │    │
│  │  - Return type consistency                                  │    │
│  │  - Enabled with -semantic flag                              │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                   PHASE 6: Code Generation (Pass 2)                  │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  CompilerVisitor (ANTLR Visitor Pattern)                    │    │
│  │  - Generates JVM bytecode using ASM library                 │    │
│  │  - Manages local variable slots with scope awareness        │    │
│  │  - Handles method invocations (static/virtual)              │    │
│  │  - Produces .class files directly                           │    │
│  │  - Generates main class + user-defined classes              │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        .class File(s)                                │
│  - Main class with static main(String[] args) method               │
│  - User-defined class files                                         │
│  - Ready for java command execution                                 │
└─────────────────────────────────────────────────────────────────────┘
```

---

## ANTLR Listener vs Visitor Pattern

ANTLR4 provides two mechanisms for traversing parse trees. Understanding when to use each is crucial for compiler design.

### Listener Pattern

**How it works:** ANTLR automatically walks the entire parse tree, calling `enterX()` when entering a node and `exitX()` when leaving.

```java
public class SymbolCollector extends JvmBasicParserBaseListener {
    private SymbolTable symbols = new SymbolTable();

    @Override
    public void enterFunctionDeclaration(FunctionDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        String returnType = ctx.typeName() != null ? ctx.typeName().getText() : "void";
        symbols.addFunction(name, returnType, getParameters(ctx));
    }

    @Override
    public void enterForStatement(ForStatementContext ctx) {
        symbols.enterScope("for");  // Track block scope
    }

    @Override
    public void exitForStatement(ForStatementContext ctx) {
        symbols.exitScope();  // Leave block scope
    }
}

// Usage
ParseTreeWalker walker = new ParseTreeWalker();
SymbolCollector collector = new SymbolCollector();
walker.walk(collector, parseTree);
```

**Characteristics:**
- Automatic traversal - ANTLR walks the tree for you
- No return values - Methods return void; accumulate data in fields
- Event-driven - Like SAX parsing for XML
- Depth-first - Children visited between enter and exit
- Best for: Symbol collection, validation, analysis passes

### Visitor Pattern

**How it works:** You explicitly control traversal by calling `visit()` on children.

```java
public class CompilerVisitor extends JvmBasicParserBaseVisitor<Object> {
    @Override
    public Object visitBinaryExpression(BinaryExpressionContext ctx) {
        visit(ctx.left);    // Generate code for left operand
        visit(ctx.right);   // Generate code for right operand

        String op = ctx.operator.getText();
        if (op.equals("+")) {
            mv.visitInsn(IADD);  // Emit ADD instruction
        }
        return null;
    }

    @Override
    public Object visitIfStatement(IfStatementContext ctx) {
        Label elseLabel = new Label();
        Label endLabel = new Label();

        visit(ctx.condition);  // Generate condition code
        mv.visitJumpInsn(IFEQ, elseLabel);  // Jump if false

        visit(ctx.thenBlock);  // Generate then block
        mv.visitJumpInsn(GOTO, endLabel);

        mv.visitLabel(elseLabel);
        if (ctx.elseBlock != null) {
            visit(ctx.elseBlock);  // Generate else block
        }

        mv.visitLabel(endLabel);
        return null;
    }
}
```

**Characteristics:**
- Manual traversal - You decide what to visit and when
- Return values - Methods can return computed results
- Flexible control - Skip subtrees, visit multiple times, custom order
- Best for: IR building, code generation, evaluation

### When to Use Each

| Use Case | Pattern | Reason |
|----------|---------|--------|
| Collect function signatures | Listener | Walk everything once, accumulate in fields |
| Collect class declarations | Listener | Need to see all classes before code generation |
| Track scope boundaries | Listener | Enter/exit events naturally match scope enter/exit |
| Detect duplicate definitions | Listener | Check all declarations without controlling order |
| Generate bytecode | Visitor | Need precise control over instruction order |
| Build IR tree | Visitor | Need to return constructed nodes |
| Evaluate expressions | Visitor | Need to return computed values |
| Pretty print | Either | Listener for simple, Visitor for custom formatting |

### Our Two-Pass Approach

```java
// Main.java - Compilation orchestration

// Pass 1: Collect symbols (Listener)
SymbolCollector collector = new SymbolCollector();
ParseTreeWalker.DEFAULT.walk(collector, parseTree);
SymbolTable symbols = collector.getSymbols();

// Pass 2: Generate code (Visitor)
CompilerVisitor codegen = new CompilerVisitor(className, symbols);
codegen.visit(parseTree);
byte[] bytecode = codegen.getBytecode();
```

**Why Two Passes?**

1. **Forward references** - Functions can be called before they're defined
2. **Class visibility** - All classes known before generating `new` expressions
3. **Type resolution** - Variable types available when generating load/store
4. **Cleaner code** - Separation of concerns (collect vs generate)

---

## Semantic Analysis

The `SemanticAnalyzer` performs optional type checking and validation. Enable with `-semantic` flag.

### Analysis Phases

```
IR Tree
   │
   ▼
┌──────────────────┐
│ Symbol Collection │  Gather all declarations from IR
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  Type Inference  │  Resolve types for expressions
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  Type Checking   │  Verify assignment/operation compatibility
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│Reference Checking│  Verify variables/functions exist
└────────┬─────────┘
         │
         ▼
   Validated IR / Errors
```

### Checks Performed

| Check | Description | Example Error |
|-------|-------------|---------------|
| Duplicate definitions | Same name declared twice in scope | `Variable 'x' already defined` |
| Undefined reference | Using variable/function not declared | `Unknown variable: y` |
| Type mismatch | Incompatible types in assignment | `Cannot assign String to Integer` |
| Parameter count | Wrong number of arguments | `Expected 2 arguments, got 3` |
| Return type | Function return doesn't match declared | `Expected Integer, returning String` |
| Namespace validation | Unknown namespace or method | `Unknown Math function: Squareroot` |

### Known Namespaces

The semantic analyzer recognizes these built-in namespaces:

- **Console** - WriteLine, Write, ReadLine
- **Math** - Sqrt, Sin, Cos, Pow, Random, etc.
- **Str** - Length, ToUpper, Substring, Replace, etc.
- **File** - ReadAllText, WriteAllText, Exists, etc.
- **Regex** - IsMatch, Replace, Find, Split, etc.
- **Http** - Get, Post, Put, Delete, etc.
- **Json** - Create, Get, Set, Parse, Pretty, etc.
- **Db** - Connect, Query, Execute, Prepare, etc.

---

## Symbol Table and Scoping

### Scope Hierarchy

```
Global Scope
├── Function "main" Scope
│   ├── For Loop Scope
│   │   └── If Block Scope
│   └── While Loop Scope
├── Function "factorial" Scope
│   └── If Block Scope
└── Class "Person" Scope
    ├── Constructor Scope
    └── Method "greet" Scope
```

### Block-Level Scoping

Variables are scoped to their containing block:

```basic
for i = 1 to 3
    var temp as Integer = i * 10   ' temp scoped to FOR block
    Console.WriteLine(temp)
next i
' temp no longer accessible here

for j = 1 to 3
    var temp as Integer = j * 100  ' Different temp, reuses slot
    Console.WriteLine(temp)
next j
```

### Symbol Table Structure

```java
public class SymbolTable {
    // Scoped symbol tracking
    private Deque<Map<String, Symbol>> scopeStack = new ArrayDeque<>();

    // Global definitions
    private Map<String, FunctionSymbol> functions = new HashMap<>();
    private Map<String, ClassSymbol> classes = new HashMap<>();

    public void enterScope(String scopeName) {
        scopeStack.push(new LinkedHashMap<>());
    }

    public void exitScope() {
        scopeStack.pop();
    }

    public Symbol lookup(String name) {
        // Search from innermost to outermost scope
        for (Map<String, Symbol> scope : scopeStack) {
            Symbol sym = scope.get(name);
            if (sym != null) return sym;
        }
        return null;  // Not found
    }

    public void define(String name, Symbol symbol) {
        scopeStack.peek().put(name, symbol);
    }
}
```

### Slot Allocation and Reclamation

The `CompilerVisitor` manages JVM local variable slots:

```java
record ScopeFrame(String name, int startSlot, Map<String, LocalVar> locals) {}

private Deque<ScopeFrame> scopeStack = new ArrayDeque<>();
private int nextSlot = 1;  // Slot 0 is 'this' or args

public void enterScope(String name) {
    scopeStack.push(new ScopeFrame(name, nextSlot, new LinkedHashMap<>()));
}

public void exitScope() {
    ScopeFrame frame = scopeStack.pop();
    nextSlot = frame.startSlot();  // Reclaim slots
}

public int allocateSlot(String name, String type) {
    int slot = nextSlot;
    nextSlot += (type.equals("Long") || type.equals("Double")) ? 2 : 1;
    scopeStack.peek().locals().put(name, new LocalVar(slot, type));
    return slot;
}
```

---

## Type System

### IRType Hierarchy

```java
public sealed interface IRType {
    // Primitive types
    enum Primitive implements IRType {
        VOID, BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE
    }

    // Reference types (String, Object, user classes)
    record Reference(String className) implements IRType {
        public static final Reference STRING = new Reference("java.lang.String");
        public static final Reference OBJECT = new Reference("java.lang.Object");
    }

    // Array types
    record Array(IRType elementType) implements IRType {}

    // Nullable wrapper (Type?)
    record Nullable(IRType innerType) implements IRType {}

    // Function types (for lambdas)
    record Function(List<IRType> params, IRType returnType) implements IRType {}
}
```

### JVM Type Descriptors

| BASIC Type | JVM Descriptor | JVM Class |
|------------|----------------|-----------|
| Integer | `I` | int |
| Long | `J` | long |
| Float | `F` | float |
| Double | `D` | double |
| Boolean | `Z` | boolean |
| Byte | `B` | byte |
| Char | `C` | char |
| String | `Ljava/lang/String;` | java.lang.String |
| Integer[] | `[I` | int[] |
| String[] | `[Ljava/lang/String;` | String[] |

---

## Code Generation

### Bytecode Generation with ASM

```java
public class CompilerVisitor extends JvmBasicParserBaseVisitor<Object> {
    private ClassWriter cw;      // ASM class writer
    private MethodVisitor mv;    // Current method being generated

    @Override
    public Object visitCompilationUnit(CompilationUnitContext ctx) {
        // Create class
        cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
        cw.visit(V21, ACC_PUBLIC | ACC_SUPER, className, null,
                 "java/lang/Object", null);

        // Generate default constructor
        generateDefaultConstructor();

        // Generate user classes
        for (ClassDeclarationContext classCtx : ctx.classDeclaration()) {
            visit(classCtx);
        }

        // Generate main method with top-level statements
        generateMainMethod(ctx);

        cw.visitEnd();
        return cw.toByteArray();
    }

    @Override
    public Object visitVarStatement(VarStatementContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        String type = getType(ctx.typeName());

        // Allocate slot in current scope
        int slot = allocateSlot(name, type);

        // Generate initializer if present
        if (ctx.expression() != null) {
            visit(ctx.expression());
            storeLocal(slot, type);
        }

        return null;
    }
}
```

### Namespace Method Calls

```java
private void handleMathCall(String methodName, ArgumentListContext args) {
    // Visit arguments to push values on stack
    if (args != null) {
        for (ExpressionContext arg : args.expression()) {
            visit(arg);
        }
    }

    // Generate INVOKESTATIC to runtime class
    String descriptor = getMathDescriptor(methodName);
    mv.visitMethodInsn(INVOKESTATIC,
        "com/jvmbasic/runtime/BasicMath",
        methodName,
        descriptor,
        false);
}
```

---

## Command Line Interface

### Available Options

```bash
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar [options] source.jvmb
```

| Option | Type | Description |
|--------|------|-------------|
| `-o <name>` | Compilation | Set output class name |
| `-d` | Debug | Enable detailed trace output |
| `-parse-only` | Compilation | Parse without generating bytecode |
| `-semantic` | Analysis | Run semantic analysis with type checking |
| `-ast` | Output | Print AST (compact format) |
| `-tree` | Output | Print parse tree (pretty-printed) |
| `-ir` | Output | Print tree-based IR |
| `-sir` | Output | Print stack-based IR (SSA-style) |
| `-tokens` | Output | Print token stream |
| `--output-ast` | File | Write AST to .ast file |
| `--output-tree` | File | Write parse tree to .tree file |
| `--output-ir` | File | Write IR to .ir file |
| `--output-sir` | File | Write Stack IR to .sir file |
| `--output-all` | File | Write all output formats |
| `-help` | Help | Show usage information |

### Option Combinations

```bash
# Development: See all internal representations
java -jar compiler.jar -d -ir -sir program.jvmb

# Syntax check only
java -jar compiler.jar -parse-only program.jvmb

# Type checking without compilation
java -jar compiler.jar -semantic -parse-only program.jvmb

# Generate analysis files for debugging
java -jar compiler.jar --output-all program.jvmb
```

---

## Future: Concurrency Support

JVM BASIC 2.0 is planned to support modern concurrency primitives similar to Go's model.

### Planned Features

#### 1. Async/Await

```basic
' Async function declaration
async function fetchData(url as String) as String
    var response as String = await Http.GetAsync(url)
    return response
end function

' Usage
var data as String = await fetchData("https://api.example.com")
```

**Implementation approach:**
- Compile async functions to return `CompletableFuture<T>`
- `await` compiles to `.thenApply()` or `.get()` depending on context
- Use Java 21's virtual threads for lightweight concurrency

#### 2. Goroutine-style Tasks

```basic
' Spawn a task (similar to Go's goroutines)
spawn processItem(item)

' Spawn with channel
var results as Channel of String = new Channel()
spawn worker(data, results)

' Receive from channel
var result as String = results.Receive()
```

**Implementation approach:**
- `spawn` creates a virtual thread (Java 21)
- Channels implemented as `BlockingQueue<T>`
- Task returns a Future for result collection

#### 3. Channels (Go-style)

```basic
' Create a channel
var ch as Channel of Integer = new Channel()

' Send to channel
ch.Send(42)

' Receive from channel
var value as Integer = ch.Receive()

' Buffered channel
var buffered as Channel of String = new Channel(10)

' Select statement (like Go's select)
select
    case msg = ch1.Receive()
        Console.WriteLine("Received: " & msg)
    case ch2.Send(value)
        Console.WriteLine("Sent value")
    case else
        Console.WriteLine("No activity")
end select
```

**Implementation approach:**
- `Channel<T>` wraps `java.util.concurrent.BlockingQueue<T>`
- Unbuffered = `SynchronousQueue`
- Buffered = `ArrayBlockingQueue`
- `select` compiles to `CompletableFuture.anyOf()`

#### 4. Mutex and Synchronization

```basic
' Mutex for critical sections
var lock as Mutex = new Mutex()

lock.Lock()
try
    ' Critical section
    sharedCounter = sharedCounter + 1
finally
    lock.Unlock()
end try

' With statement for automatic unlock
with lock
    sharedCounter = sharedCounter + 1
end with

' Read-write lock
var rwLock as RWMutex = new RWMutex()
rwLock.RLock()   ' Multiple readers allowed
rwLock.RUnlock()
rwLock.Lock()    ' Exclusive write access
rwLock.Unlock()
```

**Implementation approach:**
- `Mutex` wraps `java.util.concurrent.locks.ReentrantLock`
- `RWMutex` wraps `ReentrantReadWriteLock`
- `with` block compiles to try-finally pattern

#### 5. Wait Groups

```basic
' Wait for multiple tasks to complete
var wg as WaitGroup = new WaitGroup()

for i = 1 to 10
    wg.Add(1)
    spawn doWork(i, wg)
next i

wg.Wait()  ' Block until all tasks complete
Console.WriteLine("All tasks done")

' In the worker:
sub doWork(id as Integer, wg as WaitGroup)
    ' ... do work ...
    wg.Done()
end sub
```

**Implementation approach:**
- `WaitGroup` wraps `java.util.concurrent.CountDownLatch`
- Or use `Phaser` for reusable barriers

### Concurrency Runtime Classes (Planned)

| Class | Purpose | Java Backing |
|-------|---------|--------------|
| `BasicTask` | Task/goroutine management | Virtual threads (Java 21) |
| `BasicChannel<T>` | Go-style channels | BlockingQueue |
| `BasicMutex` | Mutual exclusion | ReentrantLock |
| `BasicRWMutex` | Read-write locks | ReentrantReadWriteLock |
| `BasicWaitGroup` | Task synchronization | CountDownLatch/Phaser |
| `BasicAtomic` | Atomic operations | AtomicInteger, AtomicReference |

### Async HTTP Example

```basic
' Parallel HTTP requests
var urls as String[] = new String[3]
urls[0] = "https://api1.example.com"
urls[1] = "https://api2.example.com"
urls[2] = "https://api3.example.com"

var results as Channel of String = new Channel()
var wg as WaitGroup = new WaitGroup()

for each url in urls
    wg.Add(1)
    spawn fetchUrl(url, results, wg)
next

' Collector task
spawn collector(results, 3)

wg.Wait()
results.Close()

sub fetchUrl(url as String, ch as Channel of String, wg as WaitGroup)
    var response as String = Http.Get(url)
    ch.Send(response)
    wg.Done()
end sub

sub collector(ch as Channel of String, count as Integer)
    for i = 1 to count
        var result as String = ch.Receive()
        Console.WriteLine("Got response: " & Str.Length(result) & " bytes")
    next i
end sub
```

### Grammar Support (Already in Place)

The ANTLR grammar already includes tokens for:
- `ASYNC` - Async function modifier
- `AWAIT` - Await expression
- `SPAWN` - Task creation (planned)
- `CHANNEL` - Channel type (planned)

Current parsing works; code generation and runtime support are pending.
