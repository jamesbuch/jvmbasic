# Migration Plan

## Overview

This document describes the migration path from the current C++ implementation to the new ANTLR4-based Java implementation.

## Current Implementation Issues

### Lexer Problems
1. Hand-written tokenizer with ad-hoc rules
2. Inconsistent keyword recognition
3. Poor error recovery
4. No proper Unicode support
5. Line continuation handling is fragile

### Parser Problems
1. Not a proper recursive descent parser
2. Cannot handle nested expressions properly
3. Missing operators (= in conditions, <>, etc.)
4. Return inside If blocks fails
5. Statement nesting is limited
6. Precedence is handled incorrectly in some cases

### Semantic Analysis Problems
1. Single-pass analysis can't handle forward references cleanly
2. Type inference is incomplete
3. No proper flow analysis
4. Symbol table design is ad-hoc

### Code Generator Problems
1. Direct AST to bytecode without IR
2. Manual bytecode emission is error-prone
3. Stack management bugs cause VerifyError
4. No optimization passes
5. String concatenation handling is inconsistent

## Migration Strategy

### Phase 1: Parallel Development

Create the new implementation alongside the existing one:

```
jvmbasic/
├── src/
│   ├── cpp/           # Current C++ implementation
│   │   ├── lexer.cpp
│   │   ├── parser.cpp
│   │   ├── semantic.cpp
│   │   └── codegen.h
│   │
│   └── java/          # New Java implementation
│       └── com/jvmbasic/
│           ├── grammar/      # ANTLR4 grammar files
│           ├── ast/          # AST definitions
│           ├── semantic/     # Semantic analysis
│           ├── ir/           # Intermediate representation
│           ├── codegen/      # Bytecode generation
│           └── runtime/      # Runtime library
```

### Phase 2: Feature Parity Testing

1. Create comprehensive test suite from existing examples
2. Run both implementations on same inputs
3. Compare outputs for correctness
4. Track feature coverage

```java
@Test
void testArithmetic() {
    String source = """
        Dim x As Integer = 10
        Dim y As Integer = 20
        Console.WriteLine(x + y)
        """;

    CompileResult oldResult = oldCompiler.compile(source);
    CompileResult newResult = newCompiler.compile(source);

    assertEquals(oldResult.output(), newResult.output());
}
```

### Phase 3: Syntax Conversion

Convert existing BASIC files to new syntax if needed:

| Old Syntax | New Syntax | Notes |
|------------|------------|-------|
| `If x Then` (single line) | Multi-line only | Cleaner parsing |
| `Dim x = 10` | `Dim x As Integer = 10` | Explicit types |
| `GoTo label` | Remove | No GOTO in modern BASIC |
| Line numbers | Remove | Not supported |
| `Chr(34)` | `\"` | Proper escapes |

### Phase 4: Gradual Rollout

1. New compiler as opt-in with `--new-compiler` flag
2. Collect feedback and bug reports
3. Fix issues in new implementation
4. Make new compiler the default
5. Deprecate old compiler
6. Remove old compiler

## Build System Migration

### From Makefile to Gradle

Current Makefile:
```makefile
jvmbasic: ast.o lexer.o parser.o semantic.o main.o
    $(CXX) $(CXXFLAGS) $^ -o $@
```

New Gradle:
```kotlin
// build.gradle.kts
plugins {
    java
    antlr
}

dependencies {
    antlr("org.antlr:antlr4:4.13.1")
    implementation("org.ow2.asm:asm:9.6")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

tasks.generateGrammarSource {
    arguments = listOf("-visitor", "-no-listener")
}
```

## API Compatibility

### Compiler CLI

Old:
```bash
./jvmbasic -o ClassName < input.bas
```

New:
```bash
java -jar jvmbasic.jar -o ClassName input.bas
```

Or with shim script:
```bash
#!/bin/bash
java -jar /path/to/jvmbasic.jar "$@"
```

### Runtime Library

The runtime library (BasicRuntime.java) will be refactored into proper packages but maintain backward compatibility for compiled code:

```java
// Old: basicrt/BasicRuntime.class
package basicrt;
public class BasicRuntime { ... }

// New: Facade that delegates to new packages
package basicrt;
public class BasicRuntime {
    public static int console_WriteLine(String s) {
        return com.jvmbasic.runtime.io.Console.writeLine(s);
    }
    // ... other methods for compatibility
}
```

## Feature Additions

New features enabled by the refactor:

### 1. Proper Equality Operators
```basic
If x = y Then   ' Now works correctly
If x <> y Then  ' Not equals
```

### 2. Complex Expressions
```basic
If (a > b) And (c < d) Then  ' Parenthesized expressions
If Not flag Then             ' Logical not
```

### 3. Return Anywhere
```basic
Sub HandleRequest()
    If Not IsValid() Then
        Return  ' Now works inside If
    End If
    ' Continue processing
End Sub
```

### 4. Str() for All Types
```basic
Dim i As Integer = 42
Console.WriteLine(Str(i))  ' Works for Integer

Dim l As Long = 123456789L
Console.WriteLine(Str(l))  ' Works for Long

Dim d As Double = 3.14159
Console.WriteLine(Str(d))  ' Works for Double
```

### 5. Proper String Interpolation
```basic
Dim name As String = "World"
Dim count As Integer = 42
Console.WriteLine($"Hello, {name}! Count: {count}")
Console.WriteLine($"Method: {Request.GetMethod()}")  ' Namespace calls work
```

### 6. Lambda Expressions
```basic
Dim add = Lambda (a, b) => a + b
Console.WriteLine(add(2, 3))  ' 5

Dim numbers = {1, 2, 3, 4, 5}
Dim squares = numbers.Select(Lambda (x) => x * x)
```

### 7. Try/Catch
```basic
Try
    Dim conn = Db.Connect(url, user, pass)
    ' Use connection
Catch ex As SQLException
    Console.WriteLine("Database error: " + ex.Message)
Finally
    Db.Close(conn)
End Try
```

## Timeline

| Week | Phase | Deliverables |
|------|-------|--------------|
| 1 | Setup | Gradle project, ANTLR4 grammar basics |
| 2 | Parser | Complete grammar, AST builder |
| 3 | Types | Type system, symbol tables |
| 4 | Semantic | All semantic passes |
| 5 | IR | IR design, IR builder |
| 6 | Codegen | ASM-based code generator |
| 7 | Runtime | Runtime library port |
| 8 | Testing | Full test suite, migration tools |

## Risks and Mitigations

| Risk | Mitigation |
|------|------------|
| Syntax differences break existing code | Provide conversion tool |
| Performance regression | Benchmark both implementations |
| Missing features | Comprehensive test coverage |
| Build system complexity | Docker-based builds |

## Success Criteria

1. All existing examples compile and run correctly
2. No VerifyError from generated bytecode
3. All new features (=, <>, etc.) work
4. Performance is comparable or better
5. Error messages are clear and helpful
6. Code is maintainable and well-documented
