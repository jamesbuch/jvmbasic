## Code Walkthrough

This walkthrough explains the major pieces of `jvmbasic.cpp`, how the source text is turned into an AST, and how JVM bytecode is emitted into a valid `.class` file. The code is intentionally compact; this guide annotates the main ideas and where to add features.

### High-level pipeline
1. `Lexer` tokenizes the input stream
2. `Parser` builds a typed AST and checks simple typing rules
3. `ClassFile` constructs the constant pool and appends JVM bytecode for `main`
4. `BasicCompiler` wires the steps and writes `BasicProg.class`

### Types and AST
Relevant enums and structs:

- `Type { Int, Float, String }`
- `ExprKind { Num, Str, Var, Bin }`, wrapped by `Expr`
- `StmtKind { Print, Let }`, wrapped by `Stmt`

Expressions carry both a kind and a resolved `Type`. Numeric literals are parsed as `Int` or `Float` based on a decimal point; `String` literals are quoted. Binary ops record the operator and two operand expressions.

### Lexer
The lexer (`Lexer`) reads characters, skipping whitespace, and produces tokens (`TokenType`): numbers, strings, identifiers, symbols (`+ - * / = ; ( )`), and keywords (`PRINT`, `LET`). Numbers accept either side of the decimal (e.g., `.5`, `3.`, `3.14`); strings are double-quoted without escapes (minimal for clarity).

### Parser
The parser implements a small recursive-descent grammar with precedence:

- `parsePrimary`: numbers, strings, identifiers, parenthesized expressions
- `parseMul`: `*` and `/`
- `parseAdd`: `+` and `-`
- `parseExpr`: aliases to `parseAdd`
- `parseStmt`: `PRINT <expr>;` and `LET <ID> = <expr>;`

The parser maintains `knownTypes` to track variable types and enforce that identifier references are defined. For binary expressions, it rejects string operands and promotes `Int`→`Float` if any operand is a `Float`. A `LET` records the resulting type and prevents conflicting reassignments.

### ClassFile and JVM emission
`ClassFile` is a minimal classfile writer:

- Builds a constant pool with the necessary UTF8, Class, NameAndType, FieldRef, and MethodRef entries for:
  - class `BasicProg`, super `java/lang/Object`
  - `java/lang/System.out : java/io/PrintStream`
  - `println(int)`, `println(float)`, `println(String)`
- Provides helpers to emit bytecode: `ldc`, loads/stores for `int/float/ref`, arithmetic `iadd/fadd/...`, conversion `i2f`, and finally `_return`
- Tracks `max_stack` (fixed to 10) and `max_locals` (grown as variables are assigned)

### Expression codegen
`ClassFile::load(const Expr&, map<string,u1>& varIdx)` is the core codegen for expressions:

- `Num`: emits small constants using `iconst`/`fconst` (with `ldc` fallback)
- `Str`: emits UTF8 and `String` constant pool entries, then `ldc`
- `Var`: emits `iload`/`fload`/`aload` depending on recorded type
- `Bin`: recursively loads operands, inserts `i2f` conversions when the result type is `Float` and an operand is `Int`, then emits the appropriate `i*` or `f*` arithmetic

### Statement codegen
`genStmt` handles the two statements:

- `PRINT e;` → `getstatic System.out`, `load(e)`, and the matching `println` overload based on `e.type`
- `LET x = e;` → allocate a new local slot if needed, `load(e)`, and `istore/fstore/astore`

Local variable slots start at 1 since slot 0 is the implicit `String[] args` parameter of `main`.

### Building and writing the class
`generate(program)` visits all statements, then inserts a final `return`. `write(out)` serializes the classfile structure: header, constant pool, access flags, this/super, empty interfaces/fields, the single `main` method with a `Code` attribute that wraps the emitted bytecode.

### Error handling
The compiler throws `runtime_error` for lexing and parsing errors such as invalid characters, unterminated strings, undefined variables, or type mismatches. The `main` function catches exceptions and reports an error.

### Where to extend next
- Add new tokens/keywords in `Lexer::nextToken`
- Extend `StmtKind`/`ExprKind` and add parsing in `Parser`
- Add bytecode helpers in `ClassFile` for new operations and control flow (branches, comparisons)
- Update type rules and conversions in `Parser` and `ClassFile::load`

For concrete recipes (conditionals, loops, builtins, user functions), see [`extending.md`](extending.md).


