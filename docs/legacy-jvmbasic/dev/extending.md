## Extending the Language and Compiler

This guide shows how to add features to the BASIC-like language and how to emit JVM bytecode for them. Each section sketches grammar, AST extensions, type rules, and codegen notes with small code pointers.

The single source file is `jvmbasic.cpp`. The core areas to touch are:

- Lexer: `Lexer::nextToken`
- Parser: `StmtKind`, `ExprKind`, new AST structs, and parse methods
- Codegen: `ClassFile` helpers, `ClassFile::load`, and `genStmt`

Keep the implementation incremental and test with `javap -c`.

### Adding more statements

#### 1) Assignment without `LET`
- Grammar: `<ID> = <expr>;`
- Parser: add a branch in `parseStmt` that recognizes `ID ASSIGN expr ;` and checks the variable is defined. Optionally allow define-on-first-assignment; if you do, record `knownTypes[name] = expr.type` when first seen.
- Codegen: identical to `LET`, reusing `istore/fstore/astore` with existing slot.

#### 2) PRINT of multiple expressions
### Arithmetic: MOD operator

Already implemented: `%` symbol and `MOD` keyword map to integer/float remainder.

- Parsing: `%` is accepted at the same precedence as `*` and `/`; the keyword `MOD` is also recognized. Expressions like `A % 3` or `A MOD 3` parse as `Bin{ Mod, left, right }`.
- Types: numeric only; strings are rejected. Mixed `Int/Float` promotes to `Float`.
- Bytecode: uses `irem` for `Int` and `frem` for `Float`.

- Grammar: `PRINT <expr> { "," <expr> } ;`
- Parser: parse a list of expressions
- Codegen: for each expression, load and call the correct `println` (or use `print` for all but the last, if you add that method ref).

### Comparisons and booleans

Add a `Bool` type and comparison operators.

- Tokens: `== != < <= > >=` (add to `TokenType` and `Lexer`)
- Types: introduce `Type::Bool`
- AST: add `ExprKind::Cmp` or reuse `Bin` with more ops
- Parser: create `parseRel` and `parseEq` layers above `parseAdd`
- Codegen:
  - For `int`/`float` comparisons use `if_icmp*` or `fcmpl`/`fcmpg` plus conditional branches
  - Materialize booleans on the stack as `iconst_0`/`iconst_1`
  - Add `println(boolean)` or convert to `String` (`Boolean.toString`) if you prefer

Minimal pattern for integer compare `a < b` producing `0/1`:
1. `iload a; iload b; if_icmpge L_false; iconst_1; goto L_end; L_false: iconst_0; L_end:`

You will need simple label management: accumulate unresolved jump offsets and backpatch when writing bytecode or store placeholders in `code` and fix later.

### Boolean literals and normalization

- Lexer: recognize identifiers `true`/`false` case-insensitively by lowercasing input prior to keyword comparison. Keep original variable names case-sensitive if desired, but booleans normalize.
- Parser: `LET X = true;` infers `Type::Bool` for `X`. Reassignments must match `Bool`.
- Codegen: represent booleans as `int` 0/1 in locals and on the stack. Add `println(boolean)` by reusing `println(int)` or wiring the explicit boolean overload if desired.

### Control flow

#### IF / ELSE
- Grammar:
  ```
  IF <bool-expr> THEN <stmt>*
  { ELSE IF <bool-expr> THEN <stmt>* }*
  [ ELSE <stmt>* ]
  ENDIF
  ```
- Parser: add new keywords/tokens (`IF`, `THEN`, `ELSE`, `ENDIF`) and a `StmtKind::If` with two statement lists.
- Codegen:
  - Evaluate condition → expect `Int`/`Bool` with 0=false, non-zero=true
  - For the cascade: for each condition `Ci`, emit `ifeq L_next_i`; then-block; `goto L_end`; place `L_next_i`. After all, emit optional else-block. Finally place `L_end`.

#### WHILE
- Grammar:
  ```
  WHILE <expr_bool> DO <stmt>* ENDWHILE
  ```
- Parser: `StmtKind::While` with a body list
- Codegen: `L_top: <cond>; ifeq L_end; <body>; goto L_top; L_end:`

#### FOR
- Grammar (simple numeric loop):
  ```
  FOR <ID> = <start> TO <end> [STEP <step>]
    <stmt>*
  NEXT
  ```
- Parser: `StmtKind::For { var, start, end, step?, body }`
- Codegen:
  - Initialize var local slot
  - `L_top: iload var; <end>; if_icmpgt L_end; <body>; iload var; <step||1>; iadd; istore var; goto L_top; L_end:`
  - Support `Float` variant mirroring `i*` ops with `f*` ops and comparisons

### Function calls (builtins and Java interop)

Support calls as expressions and statements.

#### Calls as expressions
- Grammar: `<ID> '(' <arg-list?> ')'`
- Parser: after seeing an `ID`, if next token is `(`, parse arguments; emit an `ExprKind::Call { name, args }`
- Type info: either hardcode a map of builtins (e.g., `SIN(Float)->Float`) or allow Java interop by specifying Java method descriptors.
- Codegen:
  - For builtins implemented in Java: add constant-pool `Class`, `NameAndType`, and `MethodRef` entries, then `invokestatic` or `invokevirtual` depending on target
  - Examples:
    - `Math.sin(double)` requires `double`—either add `Type::Double` or call `Float`→`double` widening via `f2d` and handle double ops, or pick float-friendly functions (`StrictMath` with `float` is not available; consider custom helper class)
  - Simpler path: implement a `Runtime` helper class with `static float sinf(float)`, etc., then reference it from the constant pool and `invokestatic`

#### Calls as statements
- `CALL <ID>(args);` shorthand for discarding the return value: emit the call and, if it returns a value, pop with `pop`

### User-defined functions/procedures

Add multi-method class emission: one method per user function plus `main`.

- Grammar:
  ```
  FUNCTION <ID>('('<params?>')'?)
    <stmt>*
    [RETURN <expr>;]
  ENDFUNCTION
  ```
  And calls as above.
- Parser:
  - Maintain a table of functions with their parameter names/types and return type (inferred or annotated)
  - Parse top-level function declarations before the main statement list, or require all functions first
- Codegen:
  - For each function, start a fresh code buffer, locals mapping, and compute its `max_locals`; write as a separate method with descriptor based on parameters/return type
  - For `RETURN`, evaluate expression and emit the corresponding `ireturn`, `freturn`, or `areturn`; if no return, emit `return`
  - For calls, `invokestatic` on the same class
- Types:
  - You can infer return type from the `RETURN` expression or require an annotation; start with requiring all returns to match the first seen type

### Adding new types

To add `Double`, `Long`, or `Boolean`:
- Extend `Type` enum and update parsing/type rules
- Add constant emission helpers (`dconst`, `lconst`), load/store pairs, arithmetic, and conversion ops (e.g., `i2l`, `f2d`)
- Update `println` support in the constant pool for new overloads

### Labels and branching utility

For control flow you need label handling. Minimal approach:
- Reserve `u2` placeholders for jump offsets (e.g., for `ifeq`, `if_icmp*`, `goto`)
- Keep a list of fixups with positions and target labels
- After emitting a block and knowing target positions, compute relative offsets and backpatch bytes in `code`

#### Minimal label API sketch in `ClassFile`

Add:
- `struct Label { int pos = -1; vector<int> patchSites; };`
- `int position() const { return (int)code.size(); }`
- `void mark(Label& L) { L.pos = position(); for (int site : L.patchSites) patchJump(site, L.pos); L.patchSites.clear(); }`
- `int emitGotoPlaceholder() { emit(0xA7); emit(0x00); emit(0x00); return position() - 2; } // returns offset location`
- `void jump(Label& L) { int site = emitGotoPlaceholder(); if (L.pos < 0) L.patchSites.push_back(site); else patchJump(site, L.pos); }`
- Similar helpers for conditional branches that emit opcodes like `ifeq`/`ifne` and return patch sites
- `void patchJump(int site, int target) { int16_t rel = (int16_t)(target - site); code[site] = (rel >> 8) & 0xFF; code[site+1] = rel & 0xFF; }`

This keeps label logic simple enough for IF/ELSE IF/ELSE, WHILE, and GOTO.

### GOTO and labels in the language

- Grammar:
  ```
  LABEL <ID>
  GOTO <ID>;
  ```
- Parser:
  - Maintain a map `labelName -> Label` and a list of forward references `gotoSites[labelName] -> vector<site>`
  - `LABEL X` marks the current code position (when generating) or is stored in the AST to be resolved during codegen
- Codegen:
  - On `LABEL X`, call `mark(labels["X"])`
  - On `GOTO X`, call `jump(labels["X"])`
  - After method emission, validate that all labels were resolved (`pos != -1`)

### Testing tips
- Use tiny programs and inspect bytecode with `javap -c -v`
- For branches, verify jump targets by counting instruction lengths and ensuring fallthroughs match the intended flow
- Add one feature at a time; keep the constant pool minimal but correct

### Small roadmap
1. Add `IF/ELSE` with integer truthiness
2. Add `WHILE` loops
3. Add comparison operators and a `Bool` type
4. Add simple function calls via a `Runtime` helper class with `static` methods
5. Add user-defined functions compiled as additional methods in `BasicProg`


