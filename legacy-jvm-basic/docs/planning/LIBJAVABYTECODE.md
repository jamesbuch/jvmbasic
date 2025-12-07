# libjavabytecode - Standalone JVM Bytecode Generator Library

**Vision**: High-performance C++ library for generating JVM bytecode from any compiler  
**Status**: Planning  
**Priority**: Medium (Post-Phase 10)  

---

## 🎯 Vision

Create a **standalone, reusable C++ library** that any compiler can use as a backend to generate JVM `.class` files.

**Key Principle**: Compiler-agnostic bytecode generation

```
┌─────────────┐
│   Compiler  │ (Python, JavaScript, Rust, etc.)
│   Frontend  │
└──────┬──────┘
       │ AST/IR
       ▼
┌─────────────────────┐
│  libjavabytecode    │ ← Standalone C++ library
│  (Backend)          │
└──────┬──────────────┘
       │ JVM Bytecode
       ▼
┌─────────────┐
│ .class file │ (Runnable on any JVM)
└─────────────┘
```

---

## 🎨 Design Goals

### 1. Compiler-Agnostic
- Accept standard IR (Intermediate Representation)
- No dependency on source language
- Works with any frontend

### 2. High Performance
- Fast bytecode generation
- Minimal memory overhead
- Efficient constant pool management

### 3. Easy to Use
- Simple C++ API
- Clear documentation
- Example integrations

### 4. Complete JVM Support
- JVM 8, 11, 17, 21+ support
- All bytecode instructions
- Stack maps, annotations, debug info

### 5. Reusable
- Header-only or static library
- No external dependencies
- MIT or Apache 2.0 license

---

## 📋 Core API Design

### High-Level API

```cpp
#include <libjavabytecode/ClassBuilder.h>

using namespace libjavabytecode;

// Create a class
ClassBuilder builder("MyClass", "java/lang/Object");
builder.setVersion(52, 0);  // Java 8

// Add fields
builder.addField("count", "I", ACC_PRIVATE);  // private int count;

// Add methods
MethodBuilder method = builder.addMethod("main", "([Ljava/lang/String;)V", ACC_PUBLIC | ACC_STATIC);

// Generate bytecode
method.getstatic("java/lang/System", "out", "Ljava/io/PrintStream;");
method.ldc("Hello, World!");
method.invokevirtual("java/io/PrintStream", "println", "(Ljava/lang/String;)V");
method.return_void();

// Write to file
builder.writeTo("MyClass.class");
```

### Low-Level API (Advanced)

```cpp
// Direct bytecode emission
CodeBuilder code;
code.emitOpcode(GETSTATIC);
code.emitU16(constantPool.addFieldRef("java/lang/System", "out", "Ljava/io/PrintStream;"));
code.emitOpcode(LDC);
code.emitU8(constantPool.addString("Hello"));
code.emitOpcode(INVOKEVIRTUAL);
code.emitU16(constantPool.addMethodRef("java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
code.emitOpcode(RETURN);

bytes buffer = code.toBytes();
```

---

## 🏗️ Architecture

### Core Components

```
libjavabytecode/
├── include/
│   └── libjavabytecode/
│       ├── ClassBuilder.h      // High-level class construction
│       ├── MethodBuilder.h     // Method bytecode generation
│       ├── ConstantPool.h      // Constant pool management
│       ├── Instruction.h       // JVM instruction definitions
│       ├── CodeBuilder.h       // Low-level code generation
│       ├── StackMapBuilder.h   // Stack map frames (Java 7+)
│       ├── Attribute.h         // Class/method/field attributes
│       └── Types.h             // Type descriptors and utilities
├── src/
│   ├── ClassBuilder.cpp
│   ├── MethodBuilder.cpp
│   ├── ConstantPool.cpp
│   ├── CodeBuilder.cpp
│   └── StackMapBuilder.cpp
├── tests/
│   ├── test_basic.cpp
│   ├── test_constantpool.cpp
│   └── test_instructions.cpp
├── examples/
│   ├── hello_world.cpp         // Simple example
│   ├── calculator.cpp          // Expressions
│   └── compiler_backend.cpp    // Full compiler integration
├── docs/
│   ├── API.md                  // API documentation
│   ├── TUTORIAL.md             // Step-by-step guide
│   └── INTEGRATION.md          // Integrating with compilers
├── CMakeLists.txt
└── README.md
```

---

## 📚 API Examples

### Example 1: Hello World

```cpp
#include <libjavabytecode/ClassBuilder.h>

int main() {
    using namespace libjavabytecode;
    
    // Create class
    ClassBuilder cls("HelloWorld");
    cls.setVersion(52, 0);  // Java 8
    
    // Add main method
    auto main = cls.addMethod("main", "([Ljava/lang/String;)V", ACC_PUBLIC | ACC_STATIC);
    
    // System.out.println("Hello, World!");
    main.getstatic("java/lang/System", "out", "Ljava/io/PrintStream;");
    main.ldc("Hello, World!");
    main.invokevirtual("java/io/PrintStream", "println", "(Ljava/lang/String;)V");
    main.return_void();
    
    // Write class file
    cls.writeTo("HelloWorld.class");
    
    return 0;
}
```

### Example 2: Calculator (with local variables)

```cpp
// public static int add(int a, int b) { return a + b; }
auto add = cls.addMethod("add", "(II)I", ACC_PUBLIC | ACC_STATIC);

add.iload(0);   // Load first parameter (a)
add.iload(1);   // Load second parameter (b)
add.iadd();     // Add them
add.ireturn();  // Return result

// Usage from other method:
other.iconst(5);
other.iconst(10);
other.invokestatic("Calculator", "add", "(II)I");
// Result (15) is on stack
```

### Example 3: Class with Fields

```cpp
// public class Counter {
//     private int count = 0;
//     
//     public void increment() {
//         count++;
//     }
//     
//     public int getCount() {
//         return count;
//     }
// }

ClassBuilder cls("Counter");

// Add field
cls.addField("count", "I", ACC_PRIVATE);

// Constructor
auto ctor = cls.addMethod("<init>", "()V", ACC_PUBLIC);
ctor.aload(0);  // Load 'this'
ctor.invokespecial("java/lang/Object", "<init>", "()V");
ctor.aload(0);
ctor.iconst(0);
ctor.putfield("Counter", "count", "I");
ctor.return_void();

// increment() method
auto inc = cls.addMethod("increment", "()V", ACC_PUBLIC);
inc.aload(0);  // Load 'this'
inc.aload(0);
inc.getfield("Counter", "count", "I");
inc.iconst(1);
inc.iadd();
inc.putfield("Counter", "count", "I");
inc.return_void();

// getCount() method
auto get = cls.addMethod("getCount", "()I", ACC_PUBLIC);
get.aload(0);
get.getfield("Counter", "count", "I");
get.ireturn();
```

---

## 🔧 Instruction Set Support

### Arithmetic
```cpp
method.iadd();   // int add
method.fadd();   // float add
method.isub();   // int subtract
method.imul();   // int multiply
method.idiv();   // int divide
method.irem();   // int remainder
method.ineg();   // int negate
```

### Stack Manipulation
```cpp
method.pop();    // Pop top value
method.dup();    // Duplicate top value
method.swap();   // Swap top two values
```

### Local Variables
```cpp
method.iload(index);   // Load int from local
method.istore(index);  // Store int to local
method.aload(index);   // Load reference from local
method.astore(index);  // Store reference to local
```

### Control Flow
```cpp
Label endIf = method.newLabel();
method.ifle(endIf);    // if (value <= 0) goto endIf
// ... true branch ...
method.mark(endIf);
```

### Method Calls
```cpp
method.invokevirtual(className, methodName, descriptor);
method.invokestatic(className, methodName, descriptor);
method.invokespecial(className, methodName, descriptor);
method.invokeinterface(interfaceName, methodName, descriptor);
```

---

## 🎯 IR (Intermediate Representation)

### Standard IR Format (JSON)

```json
{
  "class": "MyProgram",
  "superclass": "java/lang/Object",
  "version": "52.0",
  "methods": [
    {
      "name": "main",
      "descriptor": "([Ljava/lang/String;)V",
      "access": "public static",
      "code": [
        {"op": "getstatic", "owner": "java/lang/System", "name": "out", "desc": "Ljava/io/PrintStream;"},
        {"op": "ldc", "value": "Hello"},
        {"op": "invokevirtual", "owner": "java/io/PrintStream", "name": "println", "desc": "(Ljava/lang/String;)V"},
        {"op": "return"}
      ]
    }
  ]
}
```

### Or Use Direct C++ API

```cpp
// From abstract syntax tree
void compileExpression(ExprNode* node, MethodBuilder& method) {
    if (node->type == BINARY_OP) {
        compileExpression(node->left, method);
        compileExpression(node->right, method);
        
        if (node->op == ADD) {
            method.iadd();
        } else if (node->op == SUB) {
            method.isub();
        }
        // ... etc
    } else if (node->type == LITERAL) {
        if (node->valueType == INT) {
            method.iconst(node->intValue);
        }
    }
}
```

---

## 🔬 Advanced Features

### 1. Stack Map Generation (Automatic)
```cpp
// Library automatically generates stack maps for Java 7+
method.setAutoStackMaps(true);

// Or manually specify
method.addStackMapFrame(StackMapFrame::SAME);
```

### 2. Debug Information
```cpp
method.addLineNumber(instructionIndex, sourceLineNumber);
method.addLocalVariable("count", "I", startPC, endPC, slot);
```

### 3. Annotations
```cpp
cls.addAnnotation("Deprecated");
method.addParameterAnnotation(0, "NotNull");
```

### 4. Generics (Signatures)
```cpp
// Generic: List<String>
method.setSignature("()Ljava/util/List<Ljava/lang/String;>;");
```

---

## 🧪 Integration with JVM BASIC

### Current jvmbasic Implementation
```cpp
// codegen.h (current implementation)
ClassFile cf;
cf.major_version = 49;
cf.minor_version = 0;

// ... direct bytecode emission ...
```

### With libjavabytecode
```cpp
#include <libjavabytecode/ClassBuilder.h>

void generateCode(Program& program, const string& className) {
    libjavabytecode::ClassBuilder cls(className);
    cls.setVersion(49, 0);
    
    // Generate main method
    auto main = cls.addMethod("main", "([Ljava/lang/String;)V", ACC_PUBLIC | ACC_STATIC);
    
    // Compile statements
    for (auto& stmt : program.statements) {
        compileStatement(stmt, main);
    }
    
    cls.writeTo(className + ".class");
}
```

---

## 📦 Distribution

### As Header-Only Library
```cpp
// Single header include
#include <libjavabytecode.hpp>

// No linking required
```

### As Static Library
```bash
# Build
cmake -B build
cmake --build build

# Link
g++ mycompiler.cpp -llibjavabytecode -Lbuild/lib -Ibuild/include
```

### As Shared Library
```bash
# Dynamic linking
g++ mycompiler.cpp -ljbc -Lbuild/lib -Ibuild/include
```

---

## 🎓 Use Cases

### 1. Language Compilers
- Python → JVM bytecode
- JavaScript → JVM bytecode
- Lisp → JVM bytecode
- Any language targeting the JVM

### 2. Code Generators
- ORM frameworks generating entity classes
- Protocol buffer compilers
- Parser generators (like ANTLR)

### 3. Dynamic Code Generation
- JIT compilers
- Runtime code synthesis
- Hot code replacement

### 4. Educational Tools
- Teaching compiler construction
- JVM internals exploration
- Bytecode visualization

---

## 🚀 Development Roadmap

### Phase 1: Core Library (3 months)
- Basic class/method/field generation
- Constant pool management
- Core instructions (load, store, arithmetic, control flow)
- Method calls (invokevirtual, invokestatic)

### Phase 2: Advanced Features (2 months)
- Stack map generation
- Exception handling
- Interfaces and abstract classes
- Inner classes

### Phase 3: Optimization (1 month)
- Dead code elimination
- Constant folding in constant pool
- Stack usage optimization
- Peephole optimization

### Phase 4: Documentation & Examples (1 month)
- API documentation
- Tutorial series
- Example compilers
- Integration guides

### Phase 5: Testing & Validation (1 month)
- Unit tests
- Integration tests
- Bytecode verification
- Performance benchmarks

**Total**: ~8 months for complete, production-ready library

---

## 🎯 Success Criteria

libjavabytecode is complete when:
- ✅ Can generate all JVM bytecode instructions
- ✅ Handles constant pool efficiently
- ✅ Automatic stack map generation for Java 7+
- ✅ Easy-to-use high-level API
- ✅ Low-level API for advanced users
- ✅ Comprehensive documentation
- ✅ 90%+ test coverage
- ✅ Used by at least one real compiler project
- ✅ Open-sourced on GitHub

---

## 💡 Why This Matters for JVM BASIC

### Short-term Benefits
1. **Cleaner Code**: Replace complex bytecode generation code with clean API calls
2. **Better Tested**: Leverage library's test suite
3. **Maintainable**: API changes are versioned and documented

### Long-term Benefits
1. **Ecosystem Growth**: Other compilers can target JVM using our library
2. **Collaboration**: Community contributions improve the library
3. **Recognition**: JVM BASIC becomes known for high-quality bytecode generation
4. **Educational**: Perfect for teaching compiler backends

### Reuse in jvmbasic
```cpp
// Current: ~2000 lines of bytecode generation in codegen.h
// Future: ~200 lines using libjavabytecode
```

---

## 📚 Related Projects

### Existing Libraries
- **ASM (Java)**: Most popular, but Java-based
- **Javassist (Java)**: Higher-level, but slower
- **ByteBuddy (Java)**: Modern, but Java-only
- **BCEL (Java)**: Old, but still used

### Why C++?
- **Performance**: Faster than Java-based tools
- **Integration**: Easy to use from other C++ compilers
- **No JVM**: Generate bytecode without running a JVM
- **Control**: Full control over memory and performance

**libjavabytecode would be the first high-quality, modern C++ library for JVM bytecode generation!**

---

## 🎯 Next Steps

1. **Phase 10**: Focus on JVM BASIC features (I/O, crypto, modules)
2. **Phase 11**: Extract bytecode generation from codegen.h
3. **Phase 12**: Design libjavabytecode API
4. **Phase 13**: Implement core library
5. **Phase 14**: Integrate into jvmbasic
6. **Phase 15**: Open-source and document
7. **Phase 16**: Self-hosting (jvmbasic in JVM BASIC)

---

**Vision**: Make JVM bytecode generation accessible to every compiler writer  
**Goal**: The best C++ library for JVM bytecode generation  
**Impact**: Enable new languages and tools on the JVM platform  

**This would be a significant contribution to the compiler community!** 🚀

