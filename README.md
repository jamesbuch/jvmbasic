## jvmbasic — A tiny BASIC-like compiler that emits JVM bytecode

This project is a minimal compiler for a small BASIC-like language written in modern C++. It reads source from stdin and writes a valid Java `.class` file (`BasicProg.class`) that can be disassembled with `javap` and executed on any JVM.

### Features
- **Statements**: `PRINT <expr>;`, `LET <ID> = <expr>;`
- **Expressions**: integers, floats, strings, variable references, parentheses, and `+ - * / % (MOD)` with precedence
- **Types**: `Int`, `Float`, `String` with simple numeric promotion (Int→Float)
- **Output**: Uses `java/lang/System.out.println` overloads for `int`, `float`, and `String`

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

### Language reference (current subset)
- **Program**: a sequence of statements, each terminated by `;`
- **Statements**:
  - `PRINT <expr>;`
  - `LET <ID> = <expr>;`
- **Expressions**:
  - Literals: integer (`1`, `42`), float (`3.14`, `.5`), string (`"Hello"`)
  - Variables: `<ID>` defined by a previous `LET`
  - Parentheses: `(<expr>)`
  - Binary ops: `*` and `/` bind tighter than `+` and `-`
  - Type rules: numeric ops must be numeric; `Int` is promoted to `Float` when mixed

### How it works (high level)
- The single file `jvmbasic.cpp` contains:
  - A small **lexer** that recognizes numbers, strings, identifiers, keywords (`PRINT`, `LET`) and symbols
  - A **recursive-descent parser** that builds a typed AST for expressions and statements
  - A very small **classfile generator** that constructs a constant pool, emits JVM bytecode for `main`, and writes a valid `.class`
- The compiler currently generates one class `BasicProg` with a single `public static void main(String[] args)` method. Local variables are allocated in JVM local slots starting at index 1 (index 0 is the implicit `args`).

### Example
```basic
PRINT 1 + 2;
LET A = 3.5;
PRINT A * 2;
LET B = "Hello";
PRINT B;
PRINT 7 % 3;
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
- No boolean type, comparisons, or control flow yet
- No user-defined functions/procedures
- No function calls or library/builtin calls beyond `println`
- Minimal constant pool management (no deduplication)

### Documentation
- Walkthrough of the code: see [`docs/walkthrough.md`](docs/walkthrough.md)
- How to extend the language and compiler: see [`docs/extending.md`](docs/extending.md)
- Docs index: [`docs/index.md`](docs/index.md)

### License
Public domain or MIT—choose what fits your needs. If you contribute, include a license header of your choice.


