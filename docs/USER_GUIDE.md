# JVM BASIC 2.0 User Guide

A modern BASIC compiler targeting the Java Virtual Machine.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Basic Syntax](#basic-syntax)
3. [Data Types](#data-types)
4. [Variables](#variables)
5. [Operators](#operators)
6. [Control Flow](#control-flow)
7. [Functions and Subroutines](#functions-and-subroutines)
8. [Classes and OOP](#classes-and-oop)
9. [Exception Handling](#exception-handling)
10. [Arrays](#arrays)
11. [Standard Library](#standard-library)
12. [Command Line Usage](#command-line-usage)

---

## Getting Started

### Building the Compiler

```bash
cd src/java
./gradlew build
```

This creates `build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar`.

### Hello World

Create a file `hello.jvmb`:

```basic
// hello.jvmb - Your first JVM BASIC program
var message as String = "Hello, World!"
Console.WriteLine(message)
```

Compile and run:

```bash
java -jar build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar hello.jvmb
java hello
```

---

## Basic Syntax

### File Extension

JVM BASIC 2.0 source files use the `.jvmb` extension.

### Comments

```basic
// Single-line comment (C-style)
' Single-line comment (BASIC-style)
/* Multi-line
   comment */
```

### Case Insensitivity

Keywords are case-insensitive:

```basic
VAR x AS Integer = 10
var x as Integer = 10   // Same as above
Var X As Integer = 10   // Also the same
```

### No Required Newlines

Statements can be on the same line (though not recommended for readability):

```basic
var x as Integer = 10
var y as Integer = 20
```

---

## Data Types

### Primitive Types

| Type      | Description          | JVM Type    |
|-----------|---------------------|-------------|
| `Integer` | 32-bit signed integer | `int`      |
| `Long`    | 64-bit signed integer | `long`     |
| `Float`   | 32-bit floating point | `float`    |
| `Double`  | 64-bit floating point | `double`   |
| `Boolean` | true/false           | `boolean`   |
| `Byte`    | 8-bit signed integer  | `byte`     |
| `Char`    | 16-bit Unicode char   | `char`     |

### Reference Types

| Type      | Description          | JVM Type           |
|-----------|---------------------|-------------------|
| `String`  | Text string          | `java.lang.String` |
| `Object`  | Base object type     | `java.lang.Object` |

### Literals

```basic
// Integer literals
var decimal as Integer = 255
var hex as Integer = 0xFF
var binary as Integer = 0b11111111
var octal as Integer = 0o377

// Long literals (suffix L)
var longVal as Long = 9223372036854775807L

// Float literals (suffix F)
var floatVal as Float = 3.14159F

// Double literals
var doubleVal as Double = 2.718281828459045

// Boolean literals
var t as Boolean = true
var f as Boolean = false

// String literals with escapes
var str as String = "Hello\nWorld"
var quoted as String = "She said \"Hi\""

// Character literals
var ch as Char = 'A'

// Null values
var nothing as Object = nil
var empty as String = nothing
```

---

## Variables

### Declaration with var

```basic
// With type and initializer
var name as String = "Alice"

// With type, no initializer (defaults to zero/null)
var count as Integer

// Type can be qualified
var list as java.util.ArrayList
```

### Arrays

```basic
// Declare and create array
var numbers as Integer[] = new Integer[5]

// Access elements
numbers[0] = 10
numbers[1] = 20

// String array
var names as String[] = new String[3]
names[0] = "Alice"
```

### Nullable Types

```basic
// Nullable type with ?
var maybeNull as String? = nil

// Check for null
if maybeNull <> nil then
    Console.WriteLine(maybeNull)
end if
```

---

## Operators

### Arithmetic Operators

| Operator | Description      | Example     |
|----------|-----------------|-------------|
| `+`      | Addition        | `a + b`     |
| `-`      | Subtraction     | `a - b`     |
| `*`      | Multiplication  | `a * b`     |
| `/`      | Division        | `a / b`     |
| `\`      | Integer division| `a \ b`     |
| `mod`    | Modulo          | `a mod b`   |
| `^`      | Power           | `a ^ 2`     |

### Comparison Operators

| Operator | Description      |
|----------|-----------------|
| `=`      | Equal           |
| `<>`     | Not equal       |
| `<`      | Less than       |
| `>`      | Greater than    |
| `<=`     | Less or equal   |
| `>=`     | Greater or equal|

### Logical Operators

| Operator | Description      |
|----------|-----------------|
| `and`    | Logical AND     |
| `or`     | Logical OR      |
| `xor`    | Exclusive OR    |
| `not`    | Logical NOT     |

### String Operators

| Operator | Description      | Example                    |
|----------|-----------------|----------------------------|
| `+`      | String concatenation (modern) | `"Hello, " + name` |
| `&`      | String concatenation (legacy) | `"Hello, " & name` |

### String Interpolation

JVM BASIC 2.0 supports string interpolation with `$"..."`:

```basic
var name as String = "World"
var count as Integer = 42
Console.WriteLine($"Hello, {name}!")
Console.WriteLine($"The answer is {count}")
Console.WriteLine($"1 + 1 = {1 + 1}")
```

### Bitwise Operators

| Operator | Description      |
|----------|-----------------|
| `\|`     | Bitwise OR      |
| `~`      | Bitwise NOT     |
| `<<`     | Left shift      |
| `>>`     | Right shift     |

### Compound Assignment

```basic
var x as Integer = 10
x += 5   // x = x + 5
x -= 3   // x = x - 3
x *= 2   // x = x * 2
x /= 4   // x = x / 4
```

### Ternary Operator

```basic
var status as String = (age >= 18) ? "Adult" : "Minor"
```

---

## Control Flow

### If Statement

```basic
if score >= 90 then
    Console.WriteLine("A")
elseif score >= 80 then
    Console.WriteLine("B")
elseif score >= 70 then
    Console.WriteLine("C")
else
    Console.WriteLine("F")
end if
```

### For Loop

```basic
// Count up
for i = 1 to 10
    Console.WriteLine(i.ToString())
next i

// Count up with step
for i = 0 to 100 step 10
    Console.WriteLine(i.ToString())
next i

// Count down
for i = 10 to 1 step -1
    Console.WriteLine(i.ToString())
next i
```

### For Each Loop

```basic
var names as String[] = new String[3]
names[0] = "Alice"
names[1] = "Bob"
names[2] = "Charlie"

for each name in names
    Console.WriteLine(name)
next
```

### While Loop

```basic
var count as Integer = 0
while count < 5
    Console.WriteLine(count.ToString())
    count += 1
end while
```

### Do Loop

```basic
// Do While
var x as Integer = 0
do while x < 5
    Console.WriteLine(x.ToString())
    x += 1
loop

// Do Until
var y as Integer = 0
do until y >= 5
    Console.WriteLine(y.ToString())
    y += 1
loop
```

### Select Case

```basic
var day as Integer = 3
select case day
    case 1
        Console.WriteLine("Monday")
    case 2
        Console.WriteLine("Tuesday")
    case 3
        Console.WriteLine("Wednesday")
    case else
        Console.WriteLine("Other day")
end select
```

### Exit and Continue

```basic
// Exit loop early
for i = 1 to 100
    if i = 50 then
        exit for
    end if
next i

// Skip to next iteration
for i = 1 to 10
    if i mod 2 = 0 then
        continue for
    end if
    Console.WriteLine(i.ToString())  // Only odd numbers
next i
```

---

## Functions and Subroutines

### Functions (return a value)

```basic
function add(a as Integer, b as Integer) as Integer
    return a + b
end function

// Call the function
var sum as Integer = add(5, 3)
```

### Subroutines (no return value)

```basic
sub printGreeting(name as String)
    Console.WriteLine("Hello, " & name & "!")
end sub

// Call the subroutine
printGreeting("World")
```

### Default Parameters

```basic
function greet(name as String = "Guest") as String
    return "Hello, " & name & "!"
end function

Console.WriteLine(greet())        // "Hello, Guest!"
Console.WriteLine(greet("Alice")) // "Hello, Alice!"
```

### ByRef Parameters

```basic
sub swap(byref a as Integer, byref b as Integer)
    var temp as Integer = a
    a = b
    b = temp
end sub

var x as Integer = 10
var y as Integer = 20
swap(x, y)
// Now x = 20, y = 10
```

### Functions Can Appear Anywhere

Functions and subroutines can be defined before or after the main code:

```basic
// Main code
Console.WriteLine(factorial(5).ToString())

// Function defined after use
function factorial(n as Integer) as Integer
    if n <= 1 then
        return 1
    end if
    return n * factorial(n - 1)
end function
```

---

## Classes and OOP

### Class Definition

```basic
public class Point
    private var _x as Integer
    private var _y as Integer

    public sub new(x as Integer, y as Integer)
        _x = x
        _y = y
    end sub

    public function getX() as Integer
        return _x
    end function

    public function getY() as Integer
        return _y
    end function
end class
```

### Properties

```basic
public class Person
    private var _name as String

    public property Name as String
        get
            return _name
        end get
        set(value as String)
            _name = value
        end set
    end property
end class
```

### Inheritance

```basic
public class Employee extends Person
    private var _salary as Double

    public sub new(name as String, salary as Double)
        Super.new(name)  // Call parent constructor
        _salary = salary
    end sub

    public override function greet() as String
        return Super.greet() & " - Employee"
    end function
end class
```

### Interfaces

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

### Object Creation

```basic
var p as Point = new Point(10, 20)
Console.WriteLine(p.getX().ToString())
```

---

## Exception Handling

### Try/Catch/Finally

```basic
try
    var result as Integer = 10 / 0
catch ex as Exception
    Console.WriteLine("Error: " & ex.getMessage())
finally
    Console.WriteLine("Cleanup code here")
end try
```

### Throwing Exceptions

```basic
function divide(a as Integer, b as Integer) as Integer
    if b = 0 then
        throw new Exception("Division by zero!")
    end if
    return a / b
end function
```

### Multiple Catch Blocks

```basic
try
    // Risky code
catch ex as ArithmeticException
    Console.WriteLine("Math error")
catch ex as NullPointerException
    Console.WriteLine("Null error")
catch ex as Exception
    Console.WriteLine("Other error")
end try
```

---

## Arrays

### Creating Arrays

```basic
// Create with size
var numbers as Integer[] = new Integer[10]

// Access elements
numbers[0] = 100
var first as Integer = numbers[0]

// Array length
Console.WriteLine(numbers.length.ToString())
```

### Iterating Arrays

```basic
// Using for loop
for i = 0 to 4
    Console.WriteLine(numbers[i].ToString())
next i

// Using for each
for each num in numbers
    Console.WriteLine(num.ToString())
next
```

---

## Standard Library

### Console I/O

```basic
// Output
Console.WriteLine("Hello, World!")
Console.Write("No newline")

// Input (returns String)
var name as String = Console.ReadLine()
```

### String Methods

```basic
var s as String = "Hello, World!"
var len as Integer = s.length()
var upper as String = s.toUpperCase()
var lower as String = s.toLowerCase()
var sub as String = s.substring(0, 5)
var index as Integer = s.indexOf("World")
var parts as String[] = s.split(",")
```

### Math Functions

```basic
var sq as Double = Math.Sqrt(16.0)
var pow as Double = Math.Pow(2.0, 10.0)
var abs as Integer = Math.Abs(-42)
var max as Integer = Math.Max(10, 20)
var min as Integer = Math.Min(10, 20)
var rand as Double = Math.Random()
```

### Type Conversion

```basic
// String to Integer
var num as Integer = Integer.Parse("123")

// Integer to String
var str as String = num.ToString()
```

---

## Command Line Usage

### Basic Compilation

```bash
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar source.jvmb
```

### Command Line Options

| Option        | Description                              |
|--------------|------------------------------------------|
| `-o <name>`   | Output class name                        |
| `-d`          | Enable debug output                      |
| `-ast`        | Print AST (compact)                      |
| `-tree`       | Print parse tree (pretty-printed)        |
| `-ir`         | Print intermediate representation        |
| `-tokens`     | Print token stream                       |
| `-parse-only` | Parse without code generation            |
| `-help`       | Show help                                |

### Examples

```bash
# Compile with custom output name
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar -o MyProgram source.jvmb

# Parse only (no .class file generated)
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar -parse-only source.jvmb

# Show intermediate representation
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar -ir -parse-only source.jvmb

# Debug mode
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar -d source.jvmb
```

---

## Web Development

JVM BASIC 2.0 includes built-in web server support using Jetty 11, enabling you to build modern web applications.

### Creating a Web Server

```basic
' Create a server on port 8080
var server as Integer = WebServer.Create(8080)

' Register routes
var r1 as Integer = WebServer.AddRoute(server, "GET", "/", "MyApp", "HandleHome")
var r2 as Integer = WebServer.AddRoute(server, "GET", "/api/data", "MyApp", "HandleApi")
var r3 as Integer = WebServer.AddRoute(server, "POST", "/api/submit", "MyApp", "HandleSubmit")

' Start the server
var started as Integer = WebServer.Start(server)
Console.WriteLine("Server running on http://localhost:8080")

' Keep server running
WebServer.Join(server)
```

### Request Handling

```basic
sub HandleHome()
    Response.SetContentType("text/html")
    Response.Write("<html><body><h1>Hello!</h1></body></html>")
end sub

sub HandleApi()
    Response.SetContentType("application/json")
    var name as String = Request.GetParameter("name")
    Response.Write("{\"greeting\": \"Hello, " + name + "!\"}")
end sub

sub HandleSubmit()
    var body as String = Request.GetBody()
    Response.SetStatus(201)
    Response.Write("Created!")
end sub
```

### Request Methods

| Method | Description |
|--------|-------------|
| `Request.GetParameter(name)` | Get query string parameter |
| `Request.GetBody()` | Get request body |
| `Request.GetMethod()` | Get HTTP method (GET, POST, etc.) |
| `Request.GetPath()` | Get request path |
| `Request.GetPathParam(name)` | Get path parameter (e.g., `/users/{id}`) |
| `Request.GetHeader(name)` | Get request header |
| `Request.GetCookie(name)` | Get cookie value |
| `Request.HasCookie(name)` | Check if cookie exists |

### Response Methods

| Method | Description |
|--------|-------------|
| `Response.Write(content)` | Write response body |
| `Response.SetContentType(type)` | Set Content-Type header |
| `Response.SetStatus(code)` | Set HTTP status code |
| `Response.SetHeader(name, value)` | Set response header |
| `Response.SetCookie(name, value)` | Set a cookie |
| `Response.SetCookieEx(name, value, maxAge, path, httpOnly, secure)` | Set cookie with options |
| `Response.DeleteCookie(name)` | Delete a cookie |
| `Response.Redirect(url)` | Redirect to URL |

### File Uploads

```basic
sub HandleUpload()
    if Request.IsMultipart() then
        Request.ParseMultipart()
        if Request.HasUpload("file") then
            var filename as String = Request.GetUploadFilename("file")
            var size as Long = Request.GetUploadSize("file")
            Request.SaveUpload("file", "/uploads/" + filename)
            Response.Write("Uploaded: " + filename)
        end if
    end if
end sub
```

### Path Parameters

```basic
' Route with path parameter
var r as Integer = WebServer.AddRoute(server, "GET", "/users/{id}", "MyApp", "HandleUser")

sub HandleUser()
    var userId as String = Request.GetPathParam("id")
    Response.Write("User ID: " + userId)
end sub
```

### Static Files

```basic
' Serve static files from /public directory
WebServer.ServeStatic(server, "/static", "/public")
```

### Caching with Redis

```basic
' Set a value with 1 hour expiration
Redis.Connect("localhost", 6379)
Redis.Set("key", "value")
Redis.SetEx("temp", "value", 3600)

' Get a value
var value as String = Redis.Get("key")

' Check existence
if Redis.Exists("key") then
    Console.WriteLine("Key exists")
end if

' Delete
Redis.Delete("key")
Redis.Close()
```

### Caching with Memcached

```basic
Memcached.Connect("localhost", 11211)
Memcached.Set("key", "value", 3600)
var value as String = Memcached.Get("key")
Memcached.Delete("key")
Memcached.Close()
```

### Running a Web Application

```bash
# Compile
java -jar build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar myapp.jvmb -o MyApp

# Run (requires lib/ directory with Jetty JARs)
java -cp '.:lib/*' MyApp
```

---

## Example Programs

See the `examples/` directory for complete example programs:

### Basic Examples

- `hello.jvmb` - Hello World
- `types.jvmb` - All data types
- `control_flow.jvmb` - Control flow statements
- `functions.jvmb` - Functions and subroutines
- `classes.jvmb` - OOP features
- `arrays.jvmb` - Array operations
- `exceptions.jvmb` - Exception handling
- `expressions.jvmb` - All operators

### Web Examples

- `jetty_hello.jvmb` - Simple web server
- `jetty_api.jvmb` - REST API example
- `task_app.jvmb` - Full task manager with Alpine.js, HTMX, and TailwindCSS

### Database Examples

- `db_mariadb_demo.jvmb` - MariaDB/MySQL integration
- `db_postgresql_demo.jvmb` - PostgreSQL integration

Each example has a corresponding `.ir` file showing the intermediate representation.
