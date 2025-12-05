# JVM BASIC 2.0 Compiler

A modern BASIC dialect targeting the JVM, featuring clean syntax, object-oriented programming, and seamless Java interoperability.

## Overview

JVM BASIC 2.0 is a complete rewrite of the original compiler using ANTLR4 for parsing and ASM for bytecode generation. It provides a modern BASIC experience with:

- **Modern syntax**: `var` declarations, nullable types (`Type?`), lambdas
- **Object-oriented**: Classes, interfaces, inheritance, properties
- **No legacy cruft**: No PRINT, INPUT, WEND, CALL, REM - all I/O via namespaced calls
- **Clean formatting**: Newlines not required (but recommended for readability)
- **Full Unicode support**: UTF-8 identifiers throughout

## Quick Start

### Build the Compiler

```bash
./gradlew build
```

### Parse and Inspect

```bash
# Show parse tree
java -jar build/libs/jvmbasic.jar -tree -parse-only examples/hello.jvmb

# Compile to bytecode
java -jar build/libs/jvmbasic.jar -o HelloWorld examples/hello.jvmb
java HelloWorld
```

## Language Syntax

### Variables

```basic
// Modern 'var' syntax (no Dim)
var message as String = "Hello, World!"
var count as Integer = 5
var ratio as Double = 3.14159
var flag as Boolean = true

// Nullable types
var maybeName as String? = Nil
```

### Control Flow

```basic
// If statements
if condition then
    // statements
elseif other then
    // statements
else
    // statements
end if

// For loops
for i = 1 to 10
    Console.WriteLine(i.ToString())
next i

// For-each loops
for each item in collection
    Console.WriteLine(item.ToString())
next item

// While loops
while condition
    // statements
end while

// Do loops
do while condition
    // statements
loop

do
    // statements
loop until condition

// Select/Case
select case value
case 1
    // statements
case 2, 3
    // statements
case else
    // default
end select
```

### Functions and Subs

```basic
// Function with return value
function factorial(n as Integer) as Integer
    if n <= 1 then
        return 1
    end if
    return n * factorial(n - 1)
end function

// Sub (void procedure)
sub printMessage(msg as String)
    Console.WriteLine(msg)
end sub

// Parameters with defaults
function greet(name as String, greeting as String = "Hello") as String
    return greeting & ", " & name & "!"
end function

// ByRef parameters
sub increment(byref value as Integer)
    value += 1
end sub
```

### Classes and OOP

```basic
// Class declaration
public class Person
    // Fields
    private var _name as String
    private var _age as Integer

    // Constructor
    public sub new(name as String, age as Integer)
        _name = name
        _age = age
    end sub

    // Property with getter/setter
    public property Name as String
        get
            return _name
        end get
        set(value as String)
            _name = value
        end set
    end property

    // Method
    public function greet() as String
        return "Hello, I'm " & _name
    end function
end class

// Inheritance
public class Employee extends Person
    private var _title as String

    public sub new(name as String, age as Integer, title as String)
        MyBase.new(name, age)
        _title = title
    end sub

    public override function greet() as String
        return MyBase.greet() & ", " & _title
    end function
end class

// Interface
public interface ISerializable
    function serialize() as String
    sub deserialize(data as String)
end interface

// Implementing interface
public class Data implements ISerializable
    // implementation
end class
```

### Exception Handling

```basic
try
    // risky code
    var result as Integer = dangerousOperation()
catch ex as IOException
    Console.WriteLine("IO Error: " & ex.Message)
catch ex as Exception
    Console.WriteLine("Error: " & ex.Message)
finally
    // cleanup
end try

// Throwing exceptions
throw new InvalidArgumentException("Value must be positive")
```

### Expressions

```basic
// Arithmetic
var result as Integer = (a + b) * c / d - e ^ 2

// String concatenation
var fullName as String = firstName & " " & lastName

// Comparison
if x >= 0 and x <= 100 then
    // in range
end if

// Bitwise
var flags as Integer = a | b & c

// Ternary
var status as String = isActive ? "Active" : "Inactive"

// Lambda
var square as Function(Integer) as Integer = lambda (x) => x * x
```

### I/O (via namespaced calls)

```basic
// Console I/O
Console.WriteLine("Hello, World!")
Console.Write("Enter name: ")
var name as String = Console.ReadLine()

// File I/O
var content as String = File.ReadAllText("data.txt")
File.WriteAllText("output.txt", content)
```

### Null Handling

```basic
// Null literals (aliases)
var nothing as Object = Nil
var empty as String? = Nothing

// Null check
if value = Nil then
    Console.WriteLine("Value is null")
end if
```

## Type System

### Primitive Types

| Type | Description | JVM Mapping |
|------|-------------|-------------|
| Integer | 32-bit signed integer | int |
| Long | 64-bit signed integer | long |
| Float | 32-bit floating point | float |
| Double | 64-bit floating point | double |
| Boolean | true/false | boolean |
| Byte | 8-bit unsigned integer | byte |
| Char | Unicode character | char |
| String | Unicode string | java.lang.String |
| Decimal | Arbitrary precision decimal | java.math.BigDecimal |
| BigInt | Arbitrary precision integer | java.math.BigInteger |
| Object | Base object type | java.lang.Object |

### Arrays

```basic
// Array declaration
var numbers as Integer[] = new Integer[10]

// Array access
numbers[0] = 42
var first as Integer = numbers[0]

// Multi-dimensional (future)
var matrix as Integer[][] = new Integer[3][3]
```

### Generics

```basic
// Generic class
public class Box<T>
    private var _value as T

    public property Value as T
        get
            return _value
        end get
        set(value as T)
            _value = value
        end set
    end property
end class

// Generic function
function identity<T>(value as T) as T
    return value
end function
```

## Project Structure

```
src/java/
  com/jvmbasic/
    grammar/           # ANTLR4 grammar files
      JvmBasicLexer.g4
      JvmBasicParser.g4
    ir/               # Intermediate Representation
      IRNode.java
      IRType.java
      IRVisitor.java
      decl/           # Declarations (function, class, etc.)
      stmt/           # Statements
      expr/           # Expressions
    listener/         # Parse tree listeners
    visitor/          # Parse tree visitors (code gen)
    Main.java
  examples/           # Example programs
  build.gradle.kts    # Gradle build configuration
```

## Building from Source

### Prerequisites

- Java 21+
- Gradle 9.2.1+ (or use included wrapper)

### Dependencies

- ANTLR 4.13.2 (parsing)
- ASM 9.9 (bytecode generation)
- JUnit 6.0.1 (testing)

### Build Commands

```bash
# Generate ANTLR sources + compile
./gradlew build

# Just generate ANTLR sources
./gradlew generateGrammarSource

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

## Compiler Flags

| Flag | Description |
|------|-------------|
| `-o <name>` | Output class name |
| `-tree` | Show parse tree |
| `-parse-only` | Parse without compiling |
| `-ir` | Show intermediate representation |

## License

See main project LICENSE file.

## Contributing

Contributions welcome! Please follow the existing code style and include tests for new features.
