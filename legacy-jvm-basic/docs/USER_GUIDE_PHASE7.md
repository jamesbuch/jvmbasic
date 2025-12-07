# JVM BASIC User Guide - Phase 7 (Object-Oriented Programming)

**Version**: Phase 7 (In Progress - Parsing Complete)  
**Date**: October 13, 2025

---

## Table of Contents

1. [Object-Oriented Programming](#object-oriented-programming)
2. [Classes](#classes)
3. [Constructors](#constructors)
4. [Methods](#methods)
5. [Access Modifiers](#access-modifiers)
6. [Creating Objects](#creating-objects)
7. [Method Calls](#method-calls)
8. [Field Access](#field-access)
9. [ME Reference](#me-reference)
10. [Modern Syntax](#modern-syntax)

---

## Object-Oriented Programming

Phase 7 introduces Visual Basic .NET-style object-oriented programming with classes, constructors, methods, and encapsulation.

---

## Classes

### Basic CLASS Declaration

```basic
CLASS ClassName
    PUBLIC field1 AS Type
    PRIVATE field2 AS Type
END CLASS
```

### Example: Point Class

```basic
CLASS Point
    PUBLIC x AS FLOAT
    PUBLIC y AS FLOAT
END CLASS
```

**Features**:
- Case-insensitive (Point, POINT, point all equivalent)
- Fields must have access modifiers (PUBLIC/PRIVATE)
- Fields must have type declarations (AS FLOAT, AS STRING, etc.)

---

## Constructors

### Constructor Syntax

```basic
PUBLIC SUB New(param1 AS Type, param2 AS Type)
    field1 = param1
    field2 = param2
END SUB
```

### Example: Point Constructor

```basic
CLASS Point
    PUBLIC x AS FLOAT
    PUBLIC y AS FLOAT
    
    PUBLIC SUB New(px AS FLOAT, py AS FLOAT)
        x = px
        y = py
    END SUB
END CLASS
```

**Rules**:
- Constructor must be named `New` (Visual Basic style)
- Constructor is a SUB (no return type)
- Can have parameters with type declarations
- Can be PUBLIC or PRIVATE
- Initializes fields

---

## Methods

### Instance Methods

```basic
PUBLIC SUB MethodName(params)
    ' Method body
END SUB

PUBLIC FUNCTION MethodName(params) AS ReturnType
    RETURN value
END FUNCTION
```

### Example: Counter Class

```basic
CLASS Counter
    PRIVATE count AS FLOAT
    
    PUBLIC SUB New()
        count = 0.0
    END SUB
    
    PUBLIC SUB Increment()
        count = count + 1.0
    END SUB
    
    PUBLIC FUNCTION GetCount() AS FLOAT
        RETURN count
    END FUNCTION
END CLASS
```

**Method Types**:
- **SUB**: No return value (void method)
- **FUNCTION**: Returns a value with specified type

---

## Access Modifiers

### PUBLIC vs PRIVATE

```basic
CLASS BankAccount
    PRIVATE balance AS FLOAT    ' Only accessible within class
    PUBLIC owner AS STRING       ' Accessible from anywhere
    
    PUBLIC FUNCTION GetBalance() AS FLOAT
        RETURN balance    ' Can access private field inside class
    END FUNCTION
END CLASS
```

**Rules**:
- `PUBLIC`: Accessible from anywhere
- `PRIVATE`: Accessible only within the class
- Default: PUBLIC (if not specified)
- Enforced at compile time

---

## Creating Objects

### DIM AS NEW Syntax

```basic
DIM variableName AS NEW ClassName(arguments)
```

### Examples

```basic
' Create Point with constructor
DIM p AS NEW Point(3.0, 4.0)

' Create BankAccount
DIM account AS NEW BankAccount("Alice", 1000.0)

' Create with no-argument constructor
DIM counter AS NEW Counter()
```

**Note**: `NEW` creates and initializes the object in one statement.

---

## Method Calls

### CALL Statement

```basic
CALL object.MethodName(arguments)
```

### Method Calls in Expressions

```basic
LET result = object.FunctionName(arguments)
PRINT object.GetValue()
```

### Examples

```basic
DIM account AS NEW BankAccount("Alice", 1000.0)

' Call SUB method
CALL account.Deposit(500.0)

' Call FUNCTION method
LET balance = account.GetBalance()
PRINT "Balance: "; balance

' Direct in PRINT
PRINT "Balance: "; account.GetBalance()
```

---

## Field Access

### Reading Fields

```basic
LET value = object.fieldName
PRINT object.fieldName
```

### Writing Fields

```basic
LET object.fieldName = value
```

### Example

```basic
DIM p AS NEW Point(3.0, 4.0)

' Read public fields
PRINT "x = "; p.x
PRINT "y = "; p.y

' Modify public fields
LET p.x = 10.0
LET p.y = 20.0
```

**Note**: Only PUBLIC fields can be accessed outside the class.

---

## ME Reference

### Self Reference

```basic
ME.fieldName          ' Access own field
ME.MethodName(args)   ' Call own method
```

### Example: Parameter Shadowing

```basic
CLASS Person
    PRIVATE age AS FLOAT
    
    PUBLIC SUB SetAge(age AS FLOAT)
        ' Parameter 'age' shadows field 'age'
        ' Use ME to access field
        ME.age = age
    END SUB
    
    PUBLIC FUNCTION GetAge() AS FLOAT
        RETURN ME.age
    END FUNCTION
END CLASS
```

**When to Use ME**:
- Parameter names same as field names
- Explicit clarity
- Calling own methods
- Accessing own fields

---

## Modern Syntax

### Modern VB-Style Features

**1. Apostrophe Comments**:
```basic
' This is a modern comment
CLASS Example
    PUBLIC value AS FLOAT  ' Inline comment
END CLASS
```

**2. Bare Assignment** (No LET required in methods):
```basic
PUBLIC SUB SetValue(v AS FLOAT)
    value = v        ' No LET needed
    count = count + 1
END SUB
```

**3. Two-Word END Statements**:
```basic
END SUB          ' Instead of ENDSUB
END FUNCTION     ' Instead of ENDFUNCTION
END CLASS        ' Instead of ENDCLASS
END IF           ' Instead of ENDIF
```

Both styles are supported!

---

## Complete Example

```basic
' Modern VB-style BASIC with OOP
CLASS BankAccount
    PRIVATE balance AS FLOAT
    PUBLIC owner AS STRING
    
    PUBLIC SUB New(name AS STRING, initialBalance AS FLOAT)
        owner = name
        balance = initialBalance
    END SUB
    
    PUBLIC SUB Deposit(amount AS FLOAT)
        balance = balance + amount
    END SUB
    
    PUBLIC SUB Withdraw(amount AS FLOAT)
        balance = balance - amount
    END SUB
    
    PUBLIC FUNCTION GetBalance() AS FLOAT
        RETURN balance
    END FUNCTION
END CLASS

' Main program
DIM account AS NEW BankAccount("Alice", 1000.0)

PRINT "Account owner: "; account.owner

CALL account.Deposit(500.0)
PRINT "After deposit: $"; account.GetBalance()

CALL account.Withdraw(200.0)
PRINT "After withdrawal: $"; account.GetBalance()
```

**Expected Output** (when codegen complete):
```
Account owner: Alice
After deposit: $1500.0
After withdrawal: $1300.0
```

---

## Syntax Reference

### CLASS Declaration

```basic
CLASS ClassName
    [PUBLIC|PRIVATE] fieldName AS Type
    
    [PUBLIC|PRIVATE] SUB New([params])
        ' Constructor body
    END SUB
    
    [PUBLIC|PRIVATE] SUB MethodName([params])
        ' Method body
    END SUB
    
    [PUBLIC|PRIVATE] FUNCTION FunctionName([params]) AS ReturnType
        RETURN value
    END FUNCTION
END CLASS
```

### Object Creation

```basic
DIM varName AS NEW ClassName(arguments)
```

### Method Call

```basic
CALL object.Method(arguments)
```

### Field Access

```basic
LET value = object.field
LET object.field = value
```

### ME Reference

```basic
ME                   ' Current instance
ME.field            ' Access own field
ME.Method(args)     ' Call own method
```

---

## Supported Types

| Type | Keyword | JVM Type | Example |
|------|---------|----------|---------|
| Integer | INTEGER | int | 42 |
| Float | FLOAT | float | 3.14 |
| String | STRING | String | "Hello" |
| Boolean | BOOL | boolean | TRUE |
| User Class | ClassName | Object | Point |

---

## Comments

Both styles supported:

```basic
REM Old BASIC style comment
' New VB-style comment
```

Inline comments:

```basic
PRINT "Hello"  ' This prints
LET x = 10     REM Both styles work
```

---

## Current Status

**Phase 7 Parsing**: ✅ COMPLETE  
**Phase 7 Codegen**: ⏳ IN PROGRESS

All syntax above can be **parsed** with `--dump-ast` flag:
```bash
./jvmbasic --dump-ast < your_program.bas
```

**Full compilation** coming soon (code generation in progress).

---

## See Also

- `docs/dev/CODE_GUIDE.md` - Compiler architecture
- `tests/TEST_SUITE_PHASE7.md` - Test cases
- `docs/planning/PHASE7_DESIGN.md` - Complete design document

---

**For comprehensive function reference, see the main USER_GUIDE.md (93 built-in functions).**



