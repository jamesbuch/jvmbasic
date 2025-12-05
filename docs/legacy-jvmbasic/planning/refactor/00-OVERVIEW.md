# JVM BASIC Refactor: ANTLR4-Based Parser and Code Generator

## Executive Summary

This document outlines a comprehensive refactor of JVM BASIC to replace the current hand-written lexer and parser with an ANTLR4-based implementation. The goal is to create a robust, maintainable, and extensible compiler for a modern Object BASIC language that compiles to JVM bytecode.

## Current State Issues

### Parser Problems
1. **Ad-hoc lexer** - Token recognition is fragile and inconsistent
2. **Non-recursive descent** - Cannot handle nested expressions properly
3. **Missing operators** - No `=` equality in conditionals, no `<>` not-equals
4. **Statement limitations** - `Return` inside `If` blocks fails
5. **String concatenation** - AST flattening not applied consistently
6. **Type conversion** - `Str()` only accepts Float, not Int/Double/Long

### Code Generator Problems
1. **Brittle bytecode generation** - Many edge cases cause VerifyError
2. **Inconsistent type handling** - Sub calls weren't in userFunctions map
3. **Stack management issues** - Extra `pop` instructions for void returns
4. **No optimization passes** - Direct AST-to-bytecode without IR

## Proposed Architecture

```
Source Code (.bas)
       │
       ▼
┌─────────────────┐
│  ANTLR4 Lexer   │  Generated from ObjectBasic.g4
└────────┬────────┘
         │ Token Stream
         ▼
┌─────────────────┐
│  ANTLR4 Parser  │  Generated from ObjectBasic.g4
└────────┬────────┘
         │ Parse Tree
         ▼
┌─────────────────┐
│  AST Builder    │  Visitor pattern converts parse tree to AST
└────────┬────────┘
         │ Clean AST
         ▼
┌─────────────────┐
│ Semantic Pass 1 │  Symbol table, type inference
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Semantic Pass 2 │  Type checking, validation
└────────┬────────┘
         │ Typed AST
         ▼
┌─────────────────┐
│  IR Generator   │  Convert to intermediate representation
└────────┬────────┘
         │ IR (SSA form)
         ▼
┌─────────────────┐
│  Optimizer      │  Constant folding, dead code, inlining
└────────┬────────┘
         │ Optimized IR
         ▼
┌─────────────────┐
│ Bytecode Gen    │  Generate JVM bytecode using ASM library
└────────┬────────┘
         │
         ▼
    .class files
```

## Language Design: Modern Object BASIC

### Core Principles
1. **Strong static typing** - All variables must be declared with types
2. **Object-oriented** - Classes, interfaces, inheritance, polymorphism
3. **No legacy BASIC** - No line numbers, no GOTO, no implicit types
4. **JVM-native** - Direct mapping to Java types and interop

### Key Features
- Classes with constructors, methods, properties
- Interfaces and abstract classes
- Generics (List<T>, Map<K,V>)
- Lambda expressions
- Pattern matching
- Null safety (Optional types)
- Exception handling (Try/Catch/Finally)
- Async/Await for concurrency

## Implementation Plan

See the following documents for detailed specifications:

1. [01-GRAMMAR.md](01-GRAMMAR.md) - ANTLR4 grammar specification
2. [02-AST.md](02-AST.md) - AST node definitions
3. [03-TYPE-SYSTEM.md](03-TYPE-SYSTEM.md) - Type system and inference
4. [04-SEMANTIC-ANALYSIS.md](04-SEMANTIC-ANALYSIS.md) - Semantic passes
5. [05-IR.md](05-IR.md) - Intermediate representation
6. [06-CODEGEN.md](06-CODEGEN.md) - Bytecode generation with ASM
7. [07-RUNTIME.md](07-RUNTIME.md) - Runtime library design
8. [08-MIGRATION.md](08-MIGRATION.md) - Migration path from current impl

## Technology Stack

- **ANTLR4** - Parser generator (Java target, generates lexer + parser)
- **ASM** - JVM bytecode manipulation library (replaces hand-written bytecode)
- **Java 17+** - Implementation language (replacing C++)
- **Gradle** - Build system
- **JUnit 5** - Testing framework

## Benefits

1. **Correctness** - ANTLR4 guarantees correct parsing for the grammar
2. **Maintainability** - Grammar is declarative, easy to modify
3. **Error messages** - ANTLR4 provides excellent error recovery
4. **Performance** - Generated parsers are highly optimized
5. **Tooling** - IDE support, syntax highlighting, debugging
6. **Extensibility** - Add language features by modifying grammar

## Timeline Estimate

Phase 1: Grammar and Parser (Week 1-2)
Phase 2: AST and Type System (Week 2-3)
Phase 3: Semantic Analysis (Week 3-4)
Phase 4: Code Generation with ASM (Week 4-6)
Phase 5: Runtime Library (Week 6-7)
Phase 6: Testing and Migration (Week 7-8)

## Decision Points

1. **Java vs C++ for implementation** - Java recommended for ASM library
2. **Grammar style** - LALR vs LL(*) - ANTLR4 uses ALL(*)
3. **IR design** - SSA form vs stack-based
4. **Null safety** - Kotlin-style vs Optional<T>
