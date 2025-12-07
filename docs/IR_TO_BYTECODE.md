# IR to JVM Bytecode Transformation Plan

This document outlines the plan for transforming the tree-based IR to JVM bytecode using the ASM library.

## Current Architecture

```
Source (.jvmb) → Lexer → Parser → Parse Tree → IRBuilder → IR Tree → [Codegen] → Bytecode
```

### Current IR Structure

The existing IR is a **tree-based representation** with:

- **IRCompilationUnit**: Root node containing imports, functions, classes, and top-level statements
- **Declarations**: IRFunction, IRClass, IRVariable, IRParameter
- **Statements**: IRVarDecl, IRAssignment, IRIf, IRWhile, IRFor, IRForEach, IRReturn, IRExprStmt, IRBlock, IRTry, IRThrow
- **Expressions**: Literals (Int, Long, Float, Double, String, Bool, Null), IRIdentifier, IRBinaryOp, IRUnaryOp, IRCall, IRMethodCall, IRMemberAccess, IRArrayAccess, IRNewObject, IRNewArray, IRCast, IRTernary, IRInterpolatedString
- **Types**: IRType with Primitive, Reference, Array, Nullable, Function variants

## Code Generation Strategy

### Option 1: Direct IR-to-Bytecode (Recommended for MVP)

Generate bytecode directly from the tree IR using a visitor. This is simpler and sufficient for initial implementation.

```java
public class BytecodeGenerator implements IRVisitor<Void> {
    private final ClassWriter cw;
    private MethodVisitor mv;
    private LocalVariableTable locals;

    @Override
    public Void visitCompilationUnit(IRCompilationUnit unit) {
        // Generate class with main method for top-level statements
        // Generate each declared class
        // Generate each function as static method
    }
}
```

### Option 2: IR → Stack IR → Bytecode (Future Enhancement)

For more sophisticated optimizations, introduce an intermediate stack-based IR:

```
Tree IR → Stack IR (basic blocks, SSA) → Optimizations → Bytecode
```

This is described in `docs/planning/refactor/05-IR.md` but is not required for MVP.

## Implementation Plan

### Phase 1: Core Infrastructure

1. **Create BytecodeGenerator class**
   - Implement IRVisitor<Void>
   - Initialize ClassWriter with COMPUTE_FRAMES | COMPUTE_MAXS
   - Track current MethodVisitor and local variable table

2. **LocalVariableTable class**
   - Map variable names to JVM local slots
   - Handle slot widths (longs/doubles take 2 slots)
   - Track max locals for method

3. **Type mapping utilities**
   - IRType → JVM descriptor (e.g., "I", "Ljava/lang/String;")
   - IRType → JVM internal name (e.g., "java/lang/String")
   - IRType.Function → method descriptor

### Phase 2: Expressions

Generate code that pushes results onto operand stack:

| IR Expression | JVM Bytecode |
|---------------|--------------|
| IRIntLiteral | `ICONST_n`, `BIPUSH`, `SIPUSH`, or `LDC` |
| IRLongLiteral | `LDC` (or `LCONST_0`/`LCONST_1`) |
| IRFloatLiteral | `LDC` (or `FCONST_0`/`FCONST_1`/`FCONST_2`) |
| IRDoubleLiteral | `LDC` (or `DCONST_0`/`DCONST_1`) |
| IRStringLiteral | `LDC` |
| IRBoolLiteral | `ICONST_0` or `ICONST_1` |
| IRNullLiteral | `ACONST_NULL` |
| IRIdentifier | `xLOAD slot` (ILOAD, LLOAD, FLOAD, DLOAD, ALOAD) |
| IRBinaryOp | Generate left, generate right, emit op instruction |
| IRUnaryOp | Generate operand, emit op instruction |
| IRCall | Push args, `INVOKESTATIC` |
| IRMethodCall | Push object, push args, `INVOKEVIRTUAL`/`INVOKEINTERFACE` |
| IRMemberAccess | Push object, `GETFIELD` |
| IRArrayAccess | Push array, push index, `xALOAD` |
| IRNewObject | `NEW`, `DUP`, push args, `INVOKESPECIAL <init>` |
| IRNewArray | Push size, `NEWARRAY`/`ANEWARRAY` |
| IRCast | Generate expression, emit conversion instruction |
| IRTernary | Generate with branching (IFEQ/IFNE + GOTO) |
| IRInterpolatedString | StringBuilder pattern |

### Phase 3: Statements

| IR Statement | JVM Bytecode Pattern |
|--------------|---------------------|
| IRVarDecl | Generate initializer (if any), `xSTORE slot` |
| IRAssignment | Generate value, store to target (variable, field, array) |
| IRIf | Generate condition, `IFEQ elseLabel`, then block, `GOTO endLabel`, else block |
| IRWhile | `condLabel:` condition, `IFEQ endLabel`, body, `GOTO condLabel` |
| IRFor | Init, `condLabel:` condition, `IFEQ endLabel`, body, step, `GOTO condLabel` |
| IRForEach | Get iterator, loop with hasNext()/next() |
| IRReturn | Generate value (if any), `xRETURN` |
| IRExprStmt | Generate expression, `POP`/`POP2` if not void |
| IRBlock | Generate each statement |
| IRTry | TryCatchBlock setup, try body, `GOTO afterCatch`, catch handlers |
| IRThrow | Generate exception, `ATHROW` |

### Phase 4: Declarations

1. **IRFunction** → Static method in generated class
2. **IRClass** → Full class file with fields, methods, constructor
3. **Top-level statements** → `public static void main(String[] args)`

### Phase 5: Special Cases

1. **Console.WriteLine / Console.ReadLine**
   - Map to System.out.println / BufferedReader.readLine
   - Handle type-specific overloads

2. **String concatenation**
   - Use StringBuilder for multiple concatenations
   - Optimize for simple cases (invokedynamic for Java 9+)

3. **Interpolated strings**
   - Generate StringBuilder sequence
   - Convert non-String parts with appropriate toString() calls

4. **Properties**
   - Generate getter/setter methods
   - Replace property access with method calls

5. **Super constructor calls**
   - Handle `Super.new()` → `INVOKESPECIAL super.<init>`

## File Structure

```
com/jvmbasic/
  codegen/
    BytecodeGenerator.java      # Main IR visitor for bytecode generation
    LocalVariableTable.java     # Local variable slot management
    TypeMapper.java             # IRType → JVM type mapping
    MethodBuilder.java          # Helper for building methods
    ClassBuilder.java           # Helper for building classes
```

## Testing Strategy

1. **Unit tests per IR node type**
   - Verify correct bytecode for each expression type
   - Verify correct bytecode for each statement type

2. **Integration tests**
   - Compile example .jvmb files to bytecode
   - Execute generated classes and verify output
   - Compare with expected results

3. **Verification**
   - Use ASM's CheckClassAdapter for bytecode validation
   - Use javap to inspect generated classes

## Dependencies

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.ow2.asm:asm:9.9")
    implementation("org.ow2.asm:asm-util:9.9")  // For CheckClassAdapter
}
```

## Example: Generating Hello World

Input IR:
```
IRCompilationUnit("hello.jvmb")
  Statements:
    IRExprStmt(
      IRMethodCall(
        target: IRMemberAccess(
          target: IRIdentifier("Console"),
          member: "WriteLine"
        ),
        args: [IRStringLiteral("Hello, World!")]
      )
    )
```

Generated bytecode (conceptual):
```java
public class hello {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

ASM code to generate:
```java
ClassWriter cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
cw.visit(V17, ACC_PUBLIC | ACC_SUPER, "hello", null, "java/lang/Object", null);

// main method
MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "main",
    "([Ljava/lang/String;)V", null, null);
mv.visitCode();
mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
mv.visitLdcInsn("Hello, World!");
mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
    "(Ljava/lang/String;)V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(0, 0);
mv.visitEnd();

cw.visitEnd();
byte[] bytecode = cw.toByteArray();
```

## Next Steps

1. Add ASM dependency to build.gradle.kts
2. Create BytecodeGenerator skeleton with basic structure
3. Implement literal expressions (simplest case)
4. Implement variable declaration and identifier access
5. Implement Console.WriteLine special case
6. Test with hello.jvmb example
7. Incrementally add support for more IR nodes
