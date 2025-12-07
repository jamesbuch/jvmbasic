# JVM BASIC 2.0 Developer Guide

This guide is for developers who want to understand, modify, or extend the JVM BASIC 2.0 compiler.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Project Structure](#project-structure)
3. [Compilation Pipeline](#compilation-pipeline)
4. [ANTLR Grammar](#antlr-grammar)
5. [Intermediate Representation (IR)](#intermediate-representation-ir)
6. [Code Generation](#code-generation)
7. [Adding New Features](#adding-new-features)
8. [Building and Testing](#building-and-testing)

---

## Architecture Overview

The JVM BASIC 2.0 compiler follows a traditional multi-phase compiler architecture:

```
Source Code (.jvmb)
       │
       ▼
┌─────────────────┐
│  ANTLR Lexer    │  (JvmBasicLexer.g4)
└────────┬────────┘
         │ Tokens
         ▼
┌─────────────────┐
│  ANTLR Parser   │  (JvmBasicParser.g4)
└────────┬────────┘
         │ Parse Tree (CST)
         ▼
┌─────────────────┐
│   IR Builder    │  (IRBuilder.java)
└────────┬────────┘
         │ IR Tree (AST)
         ▼
┌─────────────────┐
│  Code Generator │  (CompilerVisitor.java)
└────────┬────────┘
         │ Bytecode
         ▼
    .class file
```

### Key Technologies

- **ANTLR 4.13.2** - Parser generator for lexer and parser
- **ASM 9.9** - Java bytecode manipulation library
- **Gradle 9.2.1** - Build system
- **Java 21** - Target JVM version

---

## Project Structure

```
src/java/
├── com/jvmbasic/
│   ├── grammar/           # ANTLR grammar files
│   │   ├── JvmBasicLexer.g4
│   │   └── JvmBasicParser.g4
│   │
│   ├── ir/                # Intermediate Representation
│   │   ├── IRBuilder.java       # Parse tree → IR
│   │   ├── IRCompilationUnit.java
│   │   ├── IRNode.java
│   │   ├── IRType.java
│   │   ├── IRVisitor.java
│   │   ├── decl/               # Declaration nodes
│   │   │   ├── IRClass.java
│   │   │   ├── IRFunction.java
│   │   │   ├── IRImport.java
│   │   │   └── IRParameter.java
│   │   ├── expr/               # Expression nodes
│   │   │   ├── IRBinaryOp.java
│   │   │   ├── IRCall.java
│   │   │   ├── IRIdentifier.java
│   │   │   ├── IRLiteral.java
│   │   │   └── ...
│   │   └── stmt/               # Statement nodes
│   │       ├── IRAssignment.java
│   │       ├── IRFor.java
│   │       ├── IRIf.java
│   │       └── ...
│   │
│   ├── visitor/           # AST visitors
│   │   ├── CompilerVisitor.java   # Code generation
│   │   ├── SymbolCollector.java   # Symbol table
│   │   └── DebugListener.java     # Debug output
│   │
│   └── Main.java          # Entry point
│
├── examples/              # Example programs
│   ├── hello.jvmb
│   ├── hello.ir           # IR output
│   └── ...
│
├── docs/                  # Documentation
│   ├── USER_GUIDE.md
│   └── DEVELOPER_GUIDE.md
│
└── build.gradle.kts       # Gradle build file
```

---

## Compilation Pipeline

### 1. Lexical Analysis

The lexer (`JvmBasicLexer.g4`) tokenizes source code:

```
Source: var x as Integer = 42
Tokens: VAR IDENTIFIER AS INTEGER EQ INTEGER_LITERAL
```

Key features:
- Case-insensitive keywords
- Unicode identifier support
- Multiple number formats (decimal, hex, binary, octal)
- String escape sequences
- Line continuation with `_`

### 2. Parsing

The parser (`JvmBasicParser.g4`) builds a concrete syntax tree (CST):

```
compilationUnit
├── topLevelElement
│   └── statement
│       └── varStatement
│           ├── VAR
│           ├── IDENTIFIER: "x"
│           ├── AS
│           ├── typeName
│           │   └── primitiveType: INTEGER
│           ├── EQ
│           └── expression
│               └── literal: 42
```

### 3. IR Generation

`IRBuilder.java` transforms the CST into a typed IR:

```
IRCompilationUnit("test")
├── statements:
│   └── IRVarDecl(name="x", type=INT, init=IRLiteral(42))
```

The IR provides:
- Type information on all expressions
- Simplified structure (no parse tree noise)
- Easy traversal with the Visitor pattern

### 4. Code Generation

`CompilerVisitor.java` generates JVM bytecode using ASM:

```java
// For: var x as Integer = 42
mv.visitIntInsn(BIPUSH, 42);     // Push 42
mv.visitVarInsn(ISTORE, slot);   // Store in local variable
```

---

## ANTLR Listener vs Visitor Pattern

ANTLR4 generates two mechanisms for traversing parse trees: **Listeners** and **Visitors**. Understanding when to use each is crucial for compiler design.

### Listener Pattern

**How it works:** ANTLR walks the entire parse tree automatically, calling `enterX()` when entering a node and `exitX()` when leaving. You don't control the traversal.

```java
public class MyListener extends JvmBasicParserBaseListener {
    @Override
    public void enterVarStatement(VarStatementContext ctx) {
        // Called when entering a var statement
        System.out.println("Found variable: " + ctx.IDENTIFIER().getText());
    }

    @Override
    public void exitVarStatement(VarStatementContext ctx) {
        // Called when leaving a var statement
    }
}

// Usage
ParseTreeWalker walker = new ParseTreeWalker();
walker.walk(new MyListener(), parseTree);
```

**Characteristics:**
- **Automatic traversal** - ANTLR walks the tree for you
- **No return values** - Methods return void; use class fields to accumulate data
- **Event-driven** - Like SAX parsing for XML
- **Depth-first** - Children visited between enter and exit
- **Best for:** Symbol collection, validation, analysis passes

### Visitor Pattern

**How it works:** You explicitly control traversal by calling `visit()` on children. You can skip subtrees, visit in different order, or return values.

```java
public class MyVisitor extends JvmBasicParserBaseVisitor<IRExpression> {
    @Override
    public IRExpression visitBinaryExpression(BinaryExpressionContext ctx) {
        IRExpression left = visit(ctx.left);    // Explicitly visit left
        IRExpression right = visit(ctx.right);  // Explicitly visit right
        String op = ctx.operator.getText();
        return new IRBinaryOp(op, left, right); // Return computed value
    }
}

// Usage
IRExpression result = new MyVisitor().visit(parseTree);
```

**Characteristics:**
- **Manual traversal** - You decide what to visit and when
- **Return values** - Methods return computed results
- **Flexible control** - Skip subtrees, visit multiple times, custom order
- **Best for:** IR building, code generation, evaluation

### When to Use Each

| Use Case | Best Pattern | Why |
|----------|--------------|-----|
| Symbol table collection | Listener | Walk everything once, accumulate in fields |
| Type checking | Listener | Verify all nodes, no return values needed |
| IR/AST construction | Visitor | Need return values, build bottom-up |
| Code generation | Visitor | Need control over traversal order |
| Pretty printing | Either | Listener for simple, Visitor for custom |
| Debug tracing | Listener | Log enter/exit events |
| Expression evaluation | Visitor | Return computed values |

### Our Compiler's Approach

JVM BASIC uses **both** patterns strategically:

1. **SymbolCollector (Listener)** - First pass to collect all function/class declarations:
   ```java
   public class SymbolCollector extends JvmBasicParserBaseListener {
       private Map<String, Symbol> symbols = new HashMap<>();

       @Override
       public void enterFunctionDeclaration(FunctionDeclarationContext ctx) {
           symbols.put(ctx.IDENTIFIER().getText(),
               new Symbol(ctx.typeName().getText(), ...));
       }
   }
   ```

2. **IRBuilder (Manual Visitor)** - Transform parse tree to IR:
   ```java
   public IRExpression visitExpression(ExpressionContext ctx) {
       if (ctx instanceof BinaryContext) {
           return new IRBinaryOp(visit(ctx.left), visit(ctx.right), ...);
       }
       // ...
   }
   ```

3. **CompilerVisitor (ANTLR Visitor)** - Generate bytecode from parse tree:
   ```java
   public class CompilerVisitor extends JvmBasicParserBaseVisitor<Object> {
       @Override
       public Object visitVarStatement(VarStatementContext ctx) {
           // Generate ISTORE bytecode
       }
   }
   ```

### Offloading Work to Listener

You can use a Listener pass to pre-compute information that the Visitor needs:

```java
// Pass 1: Listener collects symbols
SymbolCollector collector = new SymbolCollector();
walker.walk(collector, tree);
Map<String, Symbol> symbols = collector.getSymbols();

// Pass 2: Visitor uses pre-collected symbols
CompilerVisitor visitor = new CompilerVisitor(symbols);
visitor.visit(tree);
```

**What to collect in Listener:**
- Function signatures (name → return type, parameters)
- Class hierarchies (extends, implements)
- Variable declarations (scope → type info)
- Import mappings (alias → full qualified name)
- Constant values (name → literal value)

**Benefits:**
- Visitor doesn't need to "look ahead"
- Cleaner visitor code (single responsibility)
- Forward references work naturally
- Better error messages (know what's declared)

### Debug Output from Visitor

Add instrumentation to track visitor execution:

```java
public class DebugVisitor extends JvmBasicParserBaseVisitor<Object> {
    private int depth = 0;
    private boolean debug = true;

    private void log(String msg) {
        if (debug) {
            System.out.println("  ".repeat(depth) + msg);
        }
    }

    @Override
    public Object visitVarStatement(VarStatementContext ctx) {
        log("→ VarStatement: " + ctx.IDENTIFIER().getText());
        depth++;
        Object result = super.visitVarStatement(ctx);
        depth--;
        log("← VarStatement done");
        return result;
    }
}
```

---

## ANTLR Grammar

### Lexer Structure

```antlr
// Keywords (case-insensitive)
VAR     : V A R ;
IF      : I F ;
// ...

// Literals
INTEGER_LITERAL : DECIMAL_DIGITS | '0' [xX] HEX_DIGITS | ... ;
STRING_LITERAL  : '"' STRING_CHAR* '"' ;

// Identifiers (Unicode support)
IDENTIFIER : LETTER (LETTER | UNICODE_DIGIT)* ;

// Whitespace → hidden channel
NEWLINE : ('\r'? '\n' | '\r')+ -> channel(WHITESPACE_CHANNEL) ;
WS      : [ \t]+ -> channel(WHITESPACE_CHANNEL) ;
```

### Parser Structure

```antlr
// Top-level structure
compilationUnit : importDeclaration* topLevelElement* EOF ;
topLevelElement : declaration | statement ;

// Declarations
declaration
    : classDeclaration
    | functionDeclaration
    | subDeclaration
    | ... ;

// Statements
statement
    : varStatement
    | assignmentStatement
    | ifStatement
    | forStatement
    | ... ;

// Expressions (precedence hierarchy)
expression
    : conditionalOrExpression
    | LAMBDA parameterList ARROW expression
    | expression QUESTION expression COLON expression  // ternary
    ;

conditionalOrExpression : conditionalAndExpression (OR conditionalAndExpression)* ;
conditionalAndExpression : bitwiseOrExpression (AND bitwiseOrExpression)* ;
// ... down to primaryExpression
```

---

## Intermediate Representation (IR)

### Type System

```java
public sealed interface IRType {
    // Primitive types
    enum Primitive implements IRType {
        VOID, BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE
    }

    // Reference types
    record Reference(String className) implements IRType {
        public static final Reference STRING = new Reference("java.lang.String");
        public static final Reference OBJECT = new Reference("java.lang.Object");
    }

    // Array types
    record Array(IRType elementType) implements IRType {}

    // Nullable wrapper
    record Nullable(IRType innerType) implements IRType {}
}
```

### Node Hierarchy

```
IRNode (marker interface)
├── IRExpression
│   ├── IRLiteral           // Constants
│   ├── IRIdentifier        // Variables
│   ├── IRBinaryOp          // a + b, a < b, etc.
│   ├── IRUnaryOp           // -x, not x
│   ├── IRCall              // function(args)
│   ├── IRMethodCall        // obj.method(args)
│   ├── IRNewObject         // new Class(args)
│   ├── IRArrayAccess       // arr[i]
│   ├── IRMemberAccess      // obj.field
│   └── IRTernary           // cond ? a : b
│
├── IRStatement
│   ├── IRVarDecl           // var x as Type = init
│   ├── IRAssignment        // x = expr
│   ├── IRIf                // if/elseif/else
│   ├── IRFor               // for i = start to end
│   ├── IRForEach           // for each x in collection
│   ├── IRWhile             // while cond
│   ├── IRDoLoop            // do while/until
│   ├── IRTry               // try/catch/finally
│   ├── IRReturn            // return expr
│   └── IRExpressionStatement // expr as statement
│
└── IRDeclaration
    ├── IRFunction          // function/sub
    ├── IRClass             // class definition
    ├── IRInterface         // interface
    └── IRImport            // import statement
```

### Visitor Pattern

```java
public interface IRVisitor<T> {
    // Expressions
    T visitLiteral(IRLiteral expr);
    T visitIdentifier(IRIdentifier expr);
    T visitBinaryOp(IRBinaryOp expr);
    T visitUnaryOp(IRUnaryOp expr);
    T visitCall(IRCall expr);
    // ...

    // Statements
    T visitVarDecl(IRVarDecl stmt);
    T visitAssignment(IRAssignment stmt);
    T visitIf(IRIf stmt);
    T visitFor(IRFor stmt);
    // ...

    // Declarations
    T visitFunction(IRFunction decl);
    T visitClass(IRClass decl);
    // ...
}
```

---

## Code Generation

### CompilerVisitor Overview

```java
public class CompilerVisitor extends JvmBasicParserBaseVisitor<Object> {
    private ClassWriter cw;        // ASM class writer
    private MethodVisitor mv;      // Current method
    private SymbolTable symbols;   // Variable tracking
    private int localVarSlot;      // Local variable index

    // Entry point
    @Override
    public Object visitCompilationUnit(CompilationUnitContext ctx) {
        cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
        cw.visit(V21, ACC_PUBLIC | ACC_SUPER, className, null, "java/lang/Object", null);

        generateDefaultConstructor();

        // Visit declarations
        for (TopLevelElementContext elem : ctx.topLevelElement()) {
            if (elem.declaration() != null) visit(elem.declaration());
        }

        // Generate main method
        if (hasStatements(ctx)) {
            generateMainMethod(ctx);
        }

        cw.visitEnd();
        return null;
    }
}
```

### Expression Code Generation

```java
@Override
public Object visitBinaryOp(BinaryOpContext ctx) {
    visit(ctx.left);   // Push left operand
    visit(ctx.right);  // Push right operand

    switch (ctx.operator) {
        case PLUS:
            mv.visitInsn(IADD);  // Integer add
            break;
        case MINUS:
            mv.visitInsn(ISUB);
            break;
        case TIMES:
            mv.visitInsn(IMUL);
            break;
        // ...
    }
    return null;
}
```

### Method Calls

```java
@Override
public Object visitMethodCall(MethodCallContext ctx) {
    // Static method call: Console.WriteLine(...)
    if (ctx.isStatic) {
        // Visit arguments
        for (arg : ctx.arguments) {
            visit(arg);
        }
        mv.visitMethodInsn(INVOKESTATIC,
            ctx.className,
            ctx.methodName,
            ctx.descriptor,
            false);
    }
    // Instance method call: obj.method(...)
    else {
        visit(ctx.receiver);  // Push object reference
        for (arg : ctx.arguments) {
            visit(arg);
        }
        mv.visitMethodInsn(INVOKEVIRTUAL, ...);
    }
    return null;
}
```

---

## Adding New Features

### Adding a New Statement

1. **Add grammar rules** in `JvmBasicParser.g4`:

```antlr
// In statement rule
statement
    : ...
    | myNewStatement
    ;

myNewStatement
    : MYNEW expression
    ;
```

2. **Add lexer token** in `JvmBasicLexer.g4` (if needed):

```antlr
MYNEW : M Y N E W ;
```

3. **Add IR node** in `ir/stmt/`:

```java
public record IRMyNew(IRExpression expr, int line, int column)
    implements IRStatement {

    @Override
    public <T> T accept(IRVisitor<T> visitor) {
        return visitor.visitMyNew(this);
    }
}
```

4. **Update IRVisitor**:

```java
T visitMyNew(IRMyNew stmt);
```

5. **Update IRBuilder**:

```java
public IRMyNew visitMyNewStatement(JvmBasicParser.MyNewStatementContext ctx) {
    IRExpression expr = visitExpression(ctx.expression());
    Token token = ctx.MYNEW().getSymbol();
    return new IRMyNew(expr, token.getLine(), token.getCharPositionInLine());
}
```

6. **Update CompilerVisitor**:

```java
@Override
public Object visitMyNewStatement(MyNewStatementContext ctx) {
    visit(ctx.expression());
    // Generate bytecode for the new statement
    return null;
}
```

7. **Regenerate grammar**:

```bash
./gradlew generateGrammarSource
./gradlew build
```

### Adding a New Expression

Similar process, but add to `ir/expr/` and the expression rules in the grammar.

### Adding a New Type

1. Update `IRType.java` if needed
2. Update type resolution in `IRBuilder.visitTypeName()`
3. Update code generation for the type

---

## Building and Testing

### Build Commands

```bash
# Full build
./gradlew build

# Regenerate grammar only
./gradlew generateGrammarSource

# Clean build
./gradlew clean build

# Create distribution
./gradlew distZip
```

### Testing a Change

```bash
# From project root:
JAR=src/java/build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar

# Compile and test with IR output
java -jar $JAR -ir -parse-only examples/hello.jvmb

# Full compile and run
java -jar $JAR examples/hello.jvmb
java hello

# Run full test suite
./test-examples.sh
```

### Debug Output

```bash
# Show parse tree
java -jar $JAR -tree examples/hello.jvmb

# Show token stream
java -jar $JAR -tokens examples/hello.jvmb

# Full debug
java -jar $JAR -d examples/hello.jvmb
```

---

## Key Design Decisions

### Why Separate Lexer/Parser Grammars?

- Cleaner code organization
- Easier to maintain
- Standard ANTLR pattern

### Why an IR Layer?

- Separates parsing from code generation
- Enables optimizations in the future
- Makes debugging easier
- Supports multiple backends (potential)

### Why ASM for Bytecode?

- Industry standard
- Well-documented
- Active development
- Direct bytecode control

### Case Insensitivity

Achieved through fragment rules in the lexer:

```antlr
fragment A : [Aa] ;
fragment B : [Bb] ;
// ...
VAR : V A R ;  // Matches VAR, var, Var, etc.
```

### Unicode Support

ANTLR provides `\p{L}` and `\p{Nd}` for Unicode letters and digits:

```antlr
fragment UNICODE_LETTER : [\p{L}] ;
fragment UNICODE_DIGIT  : [\p{Nd}] ;
```

---

## Future Work

- [ ] Full class code generation
- [ ] Interface implementation
- [ ] Generic types support
- [ ] Lambda expressions
- [ ] Async/await support
- [ ] Optimization passes
- [ ] Better error messages
- [ ] Language Server Protocol (LSP) support
