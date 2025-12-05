# JVM BASIC 2.0 - Advanced Features Strategy

This document outlines the implementation strategy for advanced features in JVM BASIC 2.0.

## Current Status

### Implemented Features
- Variable declarations (`var x as Type = value`)
- Primitive types: Integer, Long, Float, Double, String, Boolean
- Arithmetic and comparison operators
- Control flow: If/ElseIf/Else, For loops (with STEP), While, Do loops (all variants)
- User-defined functions with parameters and return values
- Arrays: creation, element access, For Each loops
- Console I/O: Console.WriteLine, Console.Write, Console.ReadLine

### Architecture
- **Parser**: ANTLR4-generated from `JvmBasic.g4`
- **IR**: `com.jvmbasic.ir` package (IRBuilder, IRProgram, IRFunction, IRStatement, etc.)
- **Codegen**: `CompilerVisitor` using ASM library for bytecode generation

---

## Phase 1: Core Language Completion

### 1.1 String Interpolation
**Priority**: High
**Complexity**: Medium

The grammar already supports `$"Hello {name}"` syntax but codegen is missing.

**Implementation**:
```java
// In CompilerVisitor
@Override
public Object visitInterpolatedString(JvmBasicParser.InterpolatedStringContext ctx) {
    // Use StringBuilder for efficiency
    mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

    for (var part : ctx.interpolationPart()) {
        if (part.stringLiteralPart() != null) {
            mv.visitLdcInsn(part.stringLiteralPart().getText());
        } else {
            visit(part.expression());
            // Convert to string if needed
            emitToString();
        }
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                          "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
    }
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                      "()Ljava/lang/String;", false);
    lastExprType = "String";
    return null;
}
```

### 1.2 Exit/Continue Statements
**Priority**: High
**Complexity**: Low

Add `exit for`, `exit while`, `exit do`, `continue for`, etc.

**Implementation**:
- Track loop context with a stack of `LoopContext` objects
- Each LoopContext stores break and continue labels
- `exit` jumps to break label
- `continue` jumps to continue label (loop increment for `for`, condition check for `while`)

### 1.3 Select Case
**Priority**: Medium
**Complexity**: Medium

**Implementation Options**:
1. **TABLESWITCH/LOOKUPSWITCH**: For Integer cases, use JVM switch opcodes
2. **If-Else Chain**: For String cases or ranges, emit as chained if-else

```java
// For integer cases
mv.visitVarInsn(ILOAD, caseExprSlot);
Label[] caseLabels = new Label[cases.size()];
int[] caseValues = new int[cases.size()];
// ... populate arrays
mv.visitLookupSwitchInsn(defaultLabel, caseValues, caseLabels);
```

---

## Phase 2: Class System

### 2.1 Multi-Class Compilation
**Priority**: High
**Complexity**: High

Currently, JVM BASIC compiles to a single class with a main method. We need:

1. **Class Declaration Parsing**: Already in grammar
2. **Multi-Class Generation**: Generate multiple `.class` files from one source
3. **Class Symbols**: Track class names, fields, methods for resolution

**Design**:
```
JvmBasicCompiler
├── Parse phase → AST
├── Symbol collection phase → ClassSymbolTable
│   ├── class "Point" → fields: x, y; methods: getX(), getY()
│   └── class "Main" → main method
├── Type checking phase → validated AST
└── Code generation phase → multiple ClassWriter instances
```

**Implementation Steps**:
1. First pass: collect all class declarations into a symbol table
2. Second pass: resolve types (field types, method signatures)
3. Third pass: generate bytecode for each class

### 2.2 Field and Method Access
**Priority**: High (depends on 2.1)
**Complexity**: Medium

```java
// Field access: obj.field
mv.visitFieldInsn(GETFIELD, ownerClass, fieldName, fieldDescriptor);

// Method call: obj.method(args)
mv.visitMethodInsn(INVOKEVIRTUAL, ownerClass, methodName, methodDescriptor, false);
```

**Key Decisions**:
- All fields are instance fields (no static fields except for main class)
- Method dispatch: always `INVOKEVIRTUAL` for instance methods
- Constructor: generate `<init>` method, call with `INVOKESPECIAL`

### 2.3 Constructors
**Priority**: High (depends on 2.1)
**Complexity**: Medium

BASIC syntax: `public sub new(args...)`

**Implementation**:
```java
// Generate constructor as <init> method
MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", descriptor, null, null);
mv.visitVarInsn(ALOAD, 0);  // this
mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
// ... field initialization code
mv.visitInsn(RETURN);
```

### 2.4 Inheritance
**Priority**: Medium
**Complexity**: Medium

```basic
public class Employee extends Person
```

**Implementation**:
- Change `cw.visit()` to use parent class instead of `java/lang/Object`
- Constructor must call parent constructor
- Track inheritance for method resolution

### 2.5 Interfaces
**Priority**: Low
**Complexity**: High

```basic
public interface IShape
    function area() as Double
end interface

public class Circle implements IShape
```

**Implementation**:
- Generate interface as Java interface (ACC_INTERFACE | ACC_ABSTRACT)
- Implementing class uses `INVOKEINTERFACE` for interface method calls

---

## Phase 3: Modules and Imports

### 3.1 Module System
**Priority**: Medium
**Complexity**: Medium

**Design Options**:

**Option A: File-Based Modules**
Each `.jvmb` file is a module. The filename is the module name.
```basic
' In math_utils.jvmb
module MathUtils

public function square(n as Integer) as Integer
    return n * n
end function
```

**Option B: Explicit Module Declaration**
```basic
module MyApp.Utils
' ... code
end module
```

**Recommendation**: Option A (simpler, consistent with Java's one-class-per-file convention)

### 3.2 Import Statements
**Priority**: Medium
**Complexity**: Medium

```basic
import MathUtils
import MyApp.Data.Customer

' Or import specific items
from MathUtils import square, cube
```

**Implementation**:
1. Parse import statements, build import table
2. When resolving identifiers, check import table
3. Generate fully-qualified class names in bytecode

### 3.3 Package/Namespace Mapping
**Priority**: Medium
**Complexity**: Low

Map BASIC modules to Java packages:
```
module MyApp.Utils → package myapp.utils → class file at myapp/utils/Utils.class
```

---

## Phase 4: Standard Library

### 4.1 Namespace Architecture

Use namespaces similar to the legacy C++ implementation:

| Namespace    | Description                | JVM Mapping                    |
|-------------|---------------------------|--------------------------------|
| Console     | Console I/O               | System.out, System.in          |
| File        | File operations           | java.io.*, java.nio.*          |
| Math        | Mathematical functions    | java.lang.Math                 |
| String      | String utilities          | java.lang.String methods       |
| Http        | HTTP client               | java.net.http.HttpClient       |
| Json        | JSON parsing              | com.google.gson.Gson           |
| Db          | Database access           | java.sql.*, JDBC drivers       |
| Thread      | Threading                 | java.lang.Thread               |
| Crypto      | Cryptography              | javax.crypto.*, BC provider    |

### 4.2 Leveraging Available JARs

The `lib/` directory provides:

| JAR                    | Use Case                              |
|-----------------------|---------------------------------------|
| gson-2.10.1.jar       | Json namespace                        |
| jetty-*.jar           | Http.Server namespace (web server)    |
| postgresql-*.jar      | Db namespace (PostgreSQL)             |
| mariadb-*.jar         | Db namespace (MariaDB/MySQL)          |
| bcprov-*.jar          | Crypto namespace (BouncyCastle)       |
| commons-io-*.jar      | File namespace utilities              |
| commons-text-*.jar    | String namespace utilities            |
| commons-math3-*.jar   | Math namespace (advanced)             |
| guava-*.jar           | Collections, utilities                |

### 4.3 Implementation Strategy

**Option A: Runtime Library (Recommended)**

Create a `BasicRuntime.java` helper class that wraps Java APIs with BASIC-friendly interfaces:

```java
public class BasicRuntime {
    // Json namespace
    public static String jsonParse(String json, String path) { ... }
    public static String jsonStringify(Object obj) { ... }

    // Http namespace
    public static String httpGet(String url) { ... }
    public static String httpPost(String url, String body) { ... }

    // Db namespace
    public static Object dbConnect(String url, String user, String pass) { ... }
    public static Object dbQuery(Object conn, String sql) { ... }
}
```

**Codegen**: Emit calls to `BasicRuntime` static methods.

```java
// Json.Parse(str) compiles to:
mv.visitVarInsn(ALOAD, strSlot);
mv.visitMethodInsn(INVOKESTATIC, "BasicRuntime", "jsonParse",
                  "(Ljava/lang/String;)Ljava/lang/String;", false);
```

**Option B: Direct Java Interop**

Compile directly to Java library calls without a runtime wrapper:

```java
// Json.Parse(str) → new Gson().fromJson(str, JsonElement.class)
mv.visitTypeInsn(NEW, "com/google/gson/Gson");
mv.visitInsn(DUP);
mv.visitMethodInsn(INVOKESPECIAL, "com/google/gson/Gson", "<init>", "()V", false);
mv.visitVarInsn(ALOAD, strSlot);
mv.visitLdcInsn(Type.getType("Lcom/google/gson/JsonElement;"));
mv.visitMethodInsn(INVOKEVIRTUAL, "com/google/gson/Gson", "fromJson",
                  "(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;", false);
```

**Recommendation**: Use Option A initially (simpler development), migrate to Option B for commonly used functions (better performance).

---

## Phase 5: Generics

### 5.1 Syntax Design

```basic
' Generic class
public class List<T>
    private var items as T[]

    public function add(item as T)
        ' ...
    end function

    public function get(index as Integer) as T
        return items[index]
    end function
end class

' Usage
var numbers as List<Integer> = new List<Integer>()
numbers.add(42)
```

### 5.2 JVM Generics (Type Erasure)

Java generics use type erasure - generic type parameters are erased at runtime. The JVM stores:
- Raw types (List, not List<Integer>)
- Cast instructions where needed
- Signature attributes for reflection

### 5.3 Implementation Approach

**Phase 5a: Generic Consumption**
Support using Java generic classes:
```basic
var list as java.util.ArrayList = new java.util.ArrayList()
list.add("Hello")
var item as String = list.get(0)  // Requires cast
```

**Phase 5b: Generic Declaration**
Support declaring generic BASIC classes:
```basic
public class Pair<K, V>
    ' ...
end class
```

**Implementation**:
1. Store generic parameters in symbol table
2. When generating bytecode, use Object for all generic parameters
3. Insert CHECKCAST where type safety is needed
4. Generate Signature attributes for Java interop

---

## Phase 6: IR-Based Codegen (Future)

### 6.1 Current Architecture
```
Source → ANTLR Parser → Parse Tree → CompilerVisitor → Bytecode
                                            ↑
                                    (direct ASM calls)
```

### 6.2 Target Architecture
```
Source → ANTLR Parser → Parse Tree → IRBuilder → IR → BytecodeEmitter → Bytecode
                                          ↓
                              (optimization passes)
```

### 6.3 Benefits
- **Optimization**: Dead code elimination, constant folding, inlining
- **Debugging**: IR is human-readable, easier to debug
- **Portability**: IR could target other backends (JavaScript, WASM)
- **Testing**: IR can be validated independently

### 6.4 Migration Strategy
1. Complete current CompilerVisitor implementation
2. Ensure comprehensive test coverage
3. Build parallel IR-based emitter
4. Compare outputs for correctness
5. Gradually migrate

---

## Implementation Priority

### Immediate (Next Session)
1. String interpolation
2. Exit/Continue statements
3. More tests for edge cases

### Short-term
1. Select Case
2. Multi-class compilation basics
3. BasicRuntime helper class

### Medium-term
1. Full class system (inheritance, interfaces)
2. Module/import system
3. Standard library namespaces (Console, Math, String, File)

### Long-term
1. Database namespace (Db)
2. HTTP namespace (Http, Json)
3. Generics (consumption, then declaration)
4. IR-based codegen

---

## File Organization

```
src/java/
├── com/jvmbasic/
│   ├── grammar/           # ANTLR grammar
│   ├── ir/                # Intermediate representation
│   ├── visitor/           # CompilerVisitor (bytecode gen)
│   ├── runtime/           # BasicRuntime.java (stdlib wrapper)
│   └── symbols/           # Symbol tables (classes, methods, fields)
├── basicrt/               # Compiled runtime classes
├── docs/
│   ├── USER_GUIDE.md
│   ├── DEVELOPER_GUIDE.md
│   └── ADVANCED_FEATURES_STRATEGY.md (this file)
└── examples/
```

---

## Testing Strategy

### Unit Tests
- Test each language construct in isolation
- Example: `array_test.jvmb`, `foreach_test.jvmb`

### Integration Tests
- Test interactions between features
- Example: classes with arrays, functions with loops

### Compatibility Tests
- Ensure generated bytecode runs on standard JVM
- Test with different Java versions (11, 17, 21)

### Regression Tests
- Before each commit, run all examples
- Automate with shell script or Gradle task

---

## Appendix: Available Libraries Summary

| Library        | Version | Purpose              |
|---------------|---------|---------------------|
| ASM           | 9.9     | Bytecode generation |
| ANTLR4        | 4.13.x  | Parser generation   |
| Gson          | 2.10.1  | JSON parsing        |
| Jetty         | 11.0.19 | HTTP server         |
| PostgreSQL    | 42.7.1  | Database driver     |
| MariaDB       | 3.3.2   | Database driver     |
| BouncyCastle  | 1.77    | Cryptography        |
| Commons IO    | 2.15.1  | File utilities      |
| Commons Text  | 1.11.0  | String utilities    |
| Commons Math3 | 3.6.1   | Math utilities      |
| Guava         | 33.0.0  | General utilities   |
