# JVM BASIC 2.0 Object-Oriented Programming Analysis

This document provides a comprehensive analysis of the OOP features in JVM BASIC 2.0, including their implementation status, grammar rules, bytecode generation, and areas for improvement.

## Table of Contents

1. [Overview](#overview)
2. [Class Declaration](#class-declaration)
3. [Inheritance (extends)](#inheritance-extends)
4. [Interfaces (implements)](#interfaces-implements)
5. [Constructors](#constructors)
6. [Access Modifiers](#access-modifiers)
7. [this and super Keywords](#this-and-super-keywords)
8. [Methods and Fields](#methods-and-fields)
9. [Properties](#properties)
10. [Enums](#enums)
11. [Annotations](#annotations)
12. [Summary](#summary)

---

## Overview

JVM BASIC 2.0 has comprehensive OOP support at the grammar level, with most features fully implemented in the compiler. The language follows a VB.NET-like syntax while targeting JVM bytecode.

### Implementation Status Summary

| Feature | Grammar | Symbol Collection | Code Generation | Status |
|---------|---------|-------------------|-----------------|--------|
| Class Declaration | ✅ | ✅ | ✅ | **Complete** |
| Single Inheritance | ✅ | ✅ | ✅ | **Complete** |
| Interfaces | ✅ | ✅ | ⚠️ Partial | **In Progress** |
| Constructors | ✅ | ✅ | ✅ | **Complete** |
| Access Modifiers | ✅ | ✅ | ✅ | **Complete** |
| this keyword | ✅ | N/A | ✅ | **Complete** |
| super keyword | ✅ | N/A | ✅ | **Complete** |
| Fields | ✅ | ✅ | ✅ | **Complete** |
| Methods | ✅ | ✅ | ✅ | **Complete** |
| Static Members | ✅ | ✅ | ✅ | **Complete** |
| Properties | ✅ | ✅ | ⚠️ Partial | **In Progress** |
| Enums | ✅ | ✅ | ⚠️ Partial | **In Progress** |
| Annotations | ❌ | ❌ | ❌ | **Not Started** |

---

## Class Declaration

### Grammar (JvmBasicParser.g4, lines 64-71)

```antlr
classDeclaration
    : accessModifier? ABSTRACT? CLASS IDENTIFIER
      typeParameters?
      (EXTENDS typeName)?
      (IMPLEMENTS typeNameList)?
      classMember*
      END CLASS
    ;
```

### Features
- Optional access modifier (public, private, protected)
- Optional `ABSTRACT` modifier for abstract classes
- Optional type parameters for generics
- Single inheritance via `EXTENDS`
- Multiple interface implementation via `IMPLEMENTS`
- Class members: fields, properties, methods, constructors

### Bytecode Generation (CompilerVisitor.java, lines 319-385)

```java
// Class creation with inheritance
String baseClass = classSym.getBaseClass();
String baseClassInternal = "java/lang/Object";
if (baseClass != null && !baseClass.equals("Object")) {
    baseClassInternal = baseClass.replace(".", "/");
}
cw.visit(V21, ACC_PUBLIC | ACC_SUPER, classNameStr, null, baseClassInternal, null);
```

### Example

```basic
public class BankAccount
    private var _balance as Double
    private var _owner as String

    public sub new(owner as String, initialBalance as Double)
        _owner = owner
        _balance = initialBalance
    end sub

    public function getBalance() as Double
        return _balance
    end function

    public sub deposit(amount as Double)
        if amount > 0 then
            _balance = _balance + amount
        end if
    end sub
end class
```

---

## Inheritance (extends)

### Grammar

```antlr
(EXTENDS typeName)?        ' In classDeclaration (line 67)
EXTENDS : E X T E N D S ;  ' Lexer token (line 89)
```

### Implementation Details

1. **Symbol Collection**: Base class stored in `ClassSymbol.baseClass`
2. **Code Generation**:
   - Base class passed to `ClassWriter.visit()` as 5th parameter
   - Converts BASIC type names to JVM internal format (`Person` → `Person`)
   - Defaults to `java/lang/Object` when no extends clause

### Bytecode Pattern

```
class Child extends Parent:
  - ClassWriter.visit(..., "Parent", null)
  - Constructor calls: INVOKESPECIAL Parent.<init>
  - Super method calls: INVOKESPECIAL Parent.methodName
```

### Example

```basic
public class Employee extends Person
    private var _salary as Double

    public sub new(name as String, salary as Double)
        super.new(name)  ' Call parent constructor
        _salary = salary
    end sub

    public override function greet() as String
        return super.greet() & " - Employee"
    end function
end class
```

---

## Interfaces (implements)

### Grammar

```antlr
interfaceDeclaration
    : accessModifier? INTERFACE IDENTIFIER typeParameters?
      (EXTENDS typeNameList)?       ' Interface inheritance
      interfaceMember*
      END INTERFACE
    ;

interfaceMember
    : (FUNCTION | SUB) IDENTIFIER parameterList? (AS typeName)?
    | PROPERTY IDENTIFIER AS typeName
    ;
```

### Implementation Status

- **Grammar**: Complete
- **Symbol Collection**: Interfaces stored in `ClassSymbol.interfaces`
- **Code Generation**: Partial - interfaces array not passed to `ClassWriter.visit()`

### Current Limitation

The `ClassWriter.visit()` call doesn't include implemented interfaces:

```java
// Current (incomplete):
cw.visit(V21, ACC_PUBLIC | ACC_SUPER, classNameStr, null, baseClassInternal, null);

// Should be:
String[] interfaces = classSym.getInterfaces().toArray(new String[0]);
cw.visit(V21, ACC_PUBLIC | ACC_SUPER, classNameStr, null, baseClassInternal, interfaces);
```

### Example

```basic
public interface IShape
    function area() as Double
    function perimeter() as Double
end interface

public class Circle implements IShape
    private var _radius as Double

    public function area() as Double
        return 3.14159 * _radius * _radius
    end function

    public function perimeter() as Double
        return 2 * 3.14159 * _radius
    end function
end class
```

---

## Constructors

### Grammar (lines 109-113)

```antlr
constructorDeclaration
    : accessModifier? SUB NEW parameterList?
      statement*
      END SUB
    ;
```

### Implementation Details

1. **VB-Style Syntax**: `SUB NEW` instead of class name
2. **Default Constructor**: Auto-generated if none provided
3. **Super Call Detection**: Checks if first statement is `super.new()`
4. **Auto Super Call**: Inserts super constructor call if not explicit

### Bytecode Generation (lines 406-451)

```java
private void generateConstructor(...) {
    // Check for explicit super.new() call
    boolean hasSuperCall = isSuperConstructorCall(statements.get(0));

    // Auto-generate super call if needed
    if (!hasSuperCall) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, baseClassInternal, "<init>", "()V", false);
    }

    // Process constructor body
    for (StatementContext stmt : statements) {
        visit(stmt);
    }
}
```

---

## Access Modifiers

### Grammar (lines 73-77)

```antlr
accessModifier
    : PUBLIC
    | PRIVATE
    | PROTECTED
    ;
```

### Bytecode Mapping

| BASIC | JVM Opcode | Visibility |
|-------|------------|------------|
| `PUBLIC` | `ACC_PUBLIC` (0x0001) | Everywhere |
| `PRIVATE` | `ACC_PRIVATE` (0x0002) | Same class only |
| `PROTECTED` | `ACC_PROTECTED` (0x0004) | Same class + subclasses |
| (none) | 0 | Package-private |

### Implementation

```java
private int fieldAccessToOpcodes(String access) {
    return switch (access.toLowerCase()) {
        case "public" -> ACC_PUBLIC;
        case "private" -> ACC_PRIVATE;
        case "protected" -> ACC_PROTECTED;
        default -> 0;  // Package-private
    };
}
```

---

## this and super Keywords

### this Keyword

**Grammar** (line 537):
```antlr
| THIS                     # ThisExpr
```

**Implementation** (CompilerVisitor.java, lines 1472-1480):
```java
public Object visitThisExpr(JvmBasicParser.ThisExprContext ctx) {
    if (currentClass == null) {
        throw new RuntimeException("'this' can only be used inside a class method");
    }
    mv.visitVarInsn(ALOAD, 0);  // Load 'this' from slot 0
    lastExprType = currentClass;
    return null;
}
```

### super Keyword

**Grammar** (line 539):
```antlr
| SUPER                    # SuperExpr
```

**Implementation** (CompilerVisitor.java, lines 1483-1503):
```java
public Object visitSuperExpr(JvmBasicParser.SuperExprContext ctx) {
    if (currentClass == null) {
        throw new RuntimeException("'super' can only be used inside a class method");
    }
    ClassSymbol classSym = symbols.getClass(currentClass);
    if (classSym == null || classSym.getBaseClass() == null) {
        throw new RuntimeException("'super' requires a class that extends another class");
    }
    mv.visitVarInsn(ALOAD, 0);
    isSuperCall = true;  // Flag for INVOKESPECIAL dispatch
    lastExprType = classSym.getBaseClass();
    return null;
}
```

### Method Dispatch

| Call Type | Opcode | Description |
|-----------|--------|-------------|
| `this.method()` | `INVOKEVIRTUAL` | Polymorphic dispatch |
| `super.method()` | `INVOKESPECIAL` | Non-virtual, parent class |
| `obj.method()` | `INVOKEVIRTUAL` | Polymorphic dispatch |
| `ClassName.method()` | `INVOKESTATIC` | Static method |

### Deprecated Aliases

For backward compatibility, VB.NET aliases are supported:
- `ME` → `THIS`
- `MYBASE` → `SUPER`

---

## Methods and Fields

### Field Declaration

```antlr
fieldDeclaration
    : accessModifier? SHARED? VAR IDENTIFIER AS typeName (EQ expression)?
    ;
```

**Features:**
- Access modifiers (public, private, protected)
- Static fields via `SHARED` keyword
- Type declaration required
- Optional initializer

### Method Declaration

```antlr
methodDeclaration
    : accessModifier? SHARED? OVERRIDE? (FUNCTION | SUB) IDENTIFIER
      typeParameters?
      parameterList?
      (AS typeName)?
      statement*
      END (FUNCTION | SUB)
    ;
```

**Features:**
- Access modifiers
- Static methods via `SHARED`
- Method override via `OVERRIDE`
- Both `FUNCTION` (returns value) and `SUB` (void) supported
- Generic type parameters
- Return type for functions

### Bytecode Generation

```java
// Instance method
mv = cw.visitMethod(ACC_PUBLIC, "deposit", "(D)V", null, null);
mv.visitCode();
mv.visitVarInsn(ALOAD, 0);      // Load 'this'
// ... method body ...
mv.visitInsn(RETURN);
mv.visitMaxs(0, 0);
mv.visitEnd();

// Static method
mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "createAccount", "(Ljava/lang/String;)LBankAccount;", null, null);
```

---

## Properties

### Grammar (lines 98-107)

```antlr
propertyDeclaration
    : accessModifier? PROPERTY IDENTIFIER AS typeName
      propertyAccessor*
      END PROPERTY
    ;

propertyAccessor
    : GET statement* END GET
    | SET LPAREN IDENTIFIER AS typeName RPAREN statement* END SET
    ;
```

### Example

```basic
public property Name as String
    get
        return _name
    end get
    set(value as String)
        _name = value
    end set
end property
```

### Implementation Status

Properties are parsed but code generation for getter/setter methods may need verification.

---

## Enums

### Grammar (lines 138-146)

```antlr
enumDeclaration
    : accessModifier? ENUM IDENTIFIER
      enumMember (COMMA? enumMember)*
      END ENUM
    ;

enumMember
    : IDENTIFIER (EQ INTEGER_LITERAL)?
    ;
```

### Example

```basic
public enum DayOfWeek
    Sunday = 0,
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday
end enum
```

### Implementation Status

Enum parsing is complete. Bytecode generation for Java enum classes may need implementation.

---

## Annotations

### Current Status: NOT IMPLEMENTED

JVM BASIC 2.0 does not currently support annotations in the grammar or compiler.

### Proposed Syntax (PHP 8 style)

```basic
#[Route("/api/users")]
#[Controller]
public class UserController

    #[Get]
    #[Authorize("admin")]
    public function getUsers() as String[]
        ' ...
    end function

    #[Post]
    #[ValidateInput]
    public function createUser(user as User) as User
        ' ...
    end function
end class
```

### Implementation Requirements

See [ANNOTATIONS_PROPOSAL.md](ANNOTATIONS_PROPOSAL.md) for detailed implementation plan.

---

## Summary

### Fully Implemented
- Class declarations with inheritance
- Constructors (including super constructor calls)
- Access modifiers (public, private, protected)
- this and super keywords
- Instance and static methods
- Instance and static fields
- Method override

### Partially Implemented
- Interfaces (parsed but not wired to bytecode)
- Properties (parsed, code gen needs verification)
- Enums (parsed, bytecode gen needs verification)

### Not Implemented
- Annotations (#[] style or @style)
- Abstract methods
- Interface default methods

### Recommended Next Steps

1. Complete interface implementation in `ClassWriter.visit()`
2. Verify property getter/setter bytecode generation
3. Implement Java enum bytecode generation
4. Design and implement annotation system
