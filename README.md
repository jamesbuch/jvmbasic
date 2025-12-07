# JVM BASIC 2.0

A modern BASIC compiler targeting the Java Virtual Machine, written in Java using ANTLR4 and ASM.

## Features

| Category | Features | Status |
|----------|----------|--------|
| **Core Types** | Integer, Long, Float, Double, Boolean, Byte, Char, String, BigInteger, Decimal | ✅ |
| **Variables** | Declarations, assignments, block-level scoping | ✅ |
| **Operators** | Arithmetic, comparison, logical, bitwise, string `+` and `&` | ✅ |
| **Control Flow** | If/ElseIf/Else, For, For Each, While, Do, Select Case | ✅ |
| **Functions** | Parameters, return values, default values, ByRef | ✅ |
| **Subroutines** | Procedures without return values | ✅ |
| **String Interpolation** | `$"Hello {name}!"` | ✅ |
| **Exit/Continue** | `exit for`, `continue while`, etc. | ✅ |
| **Try/Catch/Finally** | Exception handling with `throw` | ✅ |
| **Assert** | `assert condition, "message"` | ✅ |
| **Arrays** | Declaration, access, iteration | ✅ |
| **OOP Classes** | Classes, constructors, fields, instance methods | ✅ |
| **Inheritance** | `extends`, `super.Method()`, `Super.New()` | ✅ |
| **Access Modifiers** | public, private, protected | ✅ |
| **Console I/O** | WriteLine, Write, ReadLine | ✅ |
| **Math Namespace** | Sqrt, Sin, Cos, Pow, Random, Abs, etc. | ✅ |
| **Str Namespace** | ToUpper, Length, Substring, Replace, Split, etc. | ✅ |
| **Regex Namespace** | IsMatch, Replace, Find, Split, Groups, etc. | ✅ |
| **File Namespace** | ReadAllText, WriteAllText, Exists, Delete, etc. | ✅ |
| **Http Namespace** | GET, POST, PUT, DELETE, headers, URL encoding | ✅ |
| **Json Namespace** | Parse, Create, Get, Set, Pretty, arrays | ✅ |
| **Db Namespace** | Connect, Query, Prepared statements, transactions | ✅ |
| **Date Namespace** | Now, Today, Format, Parse, Add, Compare, TimeZones | ✅ |
| **Crypto Namespace** | SHA, MD5, HMAC, AES, RSA, BCrypt, Argon2, Base64, Hex | ✅ |
| **BigInt Namespace** | Arbitrary precision integers, arithmetic, conversions | ✅ |
| **Decimal Namespace** | Arbitrary precision decimals, arithmetic, rounding | ✅ |

**Coming Soon:**

- Interfaces (`implements` keyword)
- Async/Await and concurrency primitives
- Jetty web server integration with attributes

## Quick Start

```bash
# Build the compiler
cd src/java && ./gradlew build && cd ../..

# Compile a BASIC program
java -jar src/java/build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar examples/hello.jvmb

# Run the compiled class
java hello

# Run the test suite
./test-runner.sh
```

## Running Compiled Programs

Programs that use runtime namespaces (Math, Str, File, Http, Json, Db, Date, Crypto, etc.) require the runtime libraries on the classpath:

```bash
# From the jvmbasic directory
java -cp ".:src/java/build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar:lib/*" your_program

# Or from the examples directory
cd examples
java -cp ".:../src/java/build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar:../lib/*" hello
```

On Windows, use semicolons instead of colons:

```bash
java -cp ".;src/java/build/libs/jvmbasic-compiler-2.0.0-SNAPSHOT.jar;lib/*" your_program
```

Simple programs that only use Console.WriteLine don't need the full classpath:

```bash
java hello
```

## Example Programs

### Hello World
```basic
var message as String = "Hello, World!"
Console.WriteLine(message)
```

### String Interpolation
```basic
var name as String = "Alice"
var age as Integer = 30
Console.WriteLine($"Hello, {name}! You are {age} years old.")
```

### Functions
```basic
function factorial(n as Integer) as Integer
    if n <= 1 then
        return 1
    end if
    return n * factorial(n - 1)
end function

Console.WriteLine($"5! = {factorial(5)}")
```

### OOP Classes
```basic
class Person
    public var name as String
    public var age as Integer

    public sub New(n as String, a as Integer)
        this.name = n
        this.age = a
    end sub

    public function Greet() as String
        return $"Hello, I am {this.name}"
    end function
end class

var alice as Person = new Person("Alice", 30)
Console.WriteLine(alice.Greet())
Console.WriteLine($"Age: {alice.age}")
```

### HTTP Client
```basic
' Make a GET request
var response as String = Http.Get("https://api.example.com/data")
Console.WriteLine("Status: " & Http.GetStatus())

if Http.IsSuccess() then
    Console.WriteLine("Response: " & response)
end if

' URL encoding
var encoded as String = Http.UrlEncode("Hello World!")
Console.WriteLine(encoded)  ' Hello+World%21
```

### JSON Processing
```basic
' Create JSON object
var json as String = Json.Create()
json = Json.Set(json, "name", "Alice")
json = Json.Set(json, "age", "30")
json = Json.SetBool(json, "active", true)

' Read values
var name as String = Json.Get(json, "name")
var age as Integer = Json.GetInt(json, "age")

' Pretty print
Console.WriteLine(Json.Pretty(json))

' Work with arrays
var arr as String = Json.CreateArray()
arr = Json.Push(arr, "apple")
arr = Json.Push(arr, "banana")
Console.WriteLine(arr)  ' ["apple","banana"]
```

### Database Access
```basic
' Connect to database
var connected as Boolean = Db.Connect("jdbc:mysql://localhost:3306/mydb", "user", "password")

if connected then
    ' Parameterized query (SQL injection safe)
    Db.Prepare("SELECT * FROM users WHERE age > ?")
    Db.SetInt(1, 18)
    Db.ExecuteQuery()

    while Db.Next()
        Console.WriteLine(Db.GetString("name"))
    end while

    Db.CloseResults()
    Db.Close()
end if
```

### Control Flow
```basic
for i = 1 to 10
    if i mod 2 = 0 then
        Console.WriteLine($"{i} is even")
    else
        Console.WriteLine($"{i} is odd")
    end if
next i
```

### Arrays
```basic
var numbers as Integer[] = new Integer[5]
for i = 0 to 4
    numbers[i] = (i + 1) * 10
next i

for each n in numbers
    Console.WriteLine(n)
next
```

## Project Structure

```
jvmbasic/
├── src/java/                      # JVM BASIC 2.0 compiler
│   ├── com/jvmbasic/
│   │   ├── grammar/               # ANTLR4 grammar files
│   │   ├── ir/                    # Intermediate representation
│   │   ├── runtime/               # Runtime library classes
│   │   │   ├── BasicMath.java     # Math namespace
│   │   │   ├── BasicStr.java      # Str namespace
│   │   │   ├── BasicFile.java     # File namespace
│   │   │   ├── BasicRegex.java    # Regex namespace
│   │   │   ├── BasicHttp.java     # Http namespace
│   │   │   ├── BasicJson.java     # Json namespace
│   │   │   ├── BasicDb.java       # Db namespace
│   │   │   ├── BasicDate.java     # Date namespace
│   │   │   └── BasicCrypto.java   # Crypto namespace
│   │   └── visitor/               # Code generation
│   └── build.gradle.kts
│
├── examples/                      # Example programs (.jvmb)
├── tests/                         # Test programs (.jvmb)
├── docs/                          # Documentation
│   ├── CLAUDE.md                  # AI assistant context
│   ├── TODO.md                    # Development roadmap
│   ├── USER_GUIDE.md              # Language reference
│   └── LANGUAGE_FEATURES.md       # Detailed feature docs
│
├── lib/                           # Library JARs
│   ├── gson-2.10.1.jar
│   ├── postgresql-42.7.1.jar
│   ├── mariadb-java-client-3.3.2.jar
│   ├── bcprov-jdk18on-1.77.jar    # BouncyCastle (crypto)
│   └── ...
│
├── test-runner.sh                 # Test runner script
├── build-examples.sh              # Example builder script
│
└── legacy-jvm-basic/              # Legacy C++ implementation (archived)
```

## Command Line Options

```bash
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar [options] source.jvmb
```

### Compilation Options

| Option | Description |
|--------|-------------|
| `-o <name>` | Set output class name (default: derived from source filename) |
| `-d` | Enable debug output with detailed trace information |
| `-parse-only` | Parse and analyze without generating bytecode |
| `-semantic` | Run semantic analysis with type checking |

### Output Options

| Option | Description |
|--------|-------------|
| `-ast` | Print AST structure (compact, single-line format) |
| `-tree` | Print parse tree (pretty-printed, indented) |
| `-ir` | Print intermediate representation (tree-based IR) |
| `-sir` | Print stack IR (SSA-style, for code generation) |
| `-tokens` | Print token stream from lexer |
| `--output-ast` | Write AST to `<source>.ast` file |
| `--output-tree` | Write parse tree to `<source>.tree` file |
| `--output-ir` | Write IR to `<source>.ir` file |
| `--output-sir` | Write Stack IR to `<source>.sir` file |
| `--output-all` | Write all output files (.ast, .tree, .ir, .sir) |

### Help

| Option | Description |
|--------|-------------|
| `-help`, `--help` | Show help message |

### Examples

```bash
# Basic compilation
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar myprogram.jvmb
java myprogram

# Custom output name
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar -o MyApp myprogram.jvmb
java MyApp

# Parse only (syntax check)
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar -parse-only myprogram.jvmb

# Show parse tree for debugging
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar -tree myprogram.jvmb

# Show IR and compile
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar -ir myprogram.jvmb

# Run semantic analysis
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar -semantic myprogram.jvmb

# Full debug output with all IR formats
java -jar jvmbasic-compiler-2.0.0-SNAPSHOT.jar -d -ir -sir myprogram.jvmb
```

## Standard Library

### Console
```basic
Console.WriteLine("Hello")      ' Print with newline
Console.Write("Hello")          ' Print without newline
var input as String = Console.ReadLine()
```

### Math
```basic
Math.Sqrt(16.0)       ' = 4.0
Math.Pow(2.0, 10.0)   ' = 1024.0
Math.Sin(Math.Pi() / 2) ' = 1.0
Math.Random()         ' Random 0.0-1.0
Math.Random(1, 100)   ' Random integer 1-100
Math.Abs(-42)         ' = 42
Math.Max(10, 20)      ' = 20
Math.Min(10, 20)      ' = 10
Math.Round(3.7)       ' = 4
Math.Floor(3.7)       ' = 3.0
Math.Ceil(3.2)        ' = 4.0
Math.Log(100.0)       ' Natural log
Math.Log10(100.0)     ' = 2.0
```

### Str (String Operations)
```basic
Str.Length("Hello")           ' = 5
Str.ToUpper("hello")          ' = "HELLO"
Str.ToLower("HELLO")          ' = "hello"
Str.Substring("Hello", 0, 2)  ' = "He"
Str.IndexOf("Hello", "l")     ' = 2
Str.Replace("Hello", "l", "L") ' = "HeLLo"
Str.Split("a,b,c", ",")       ' = ["a", "b", "c"]
Str.Trim("  hello  ")         ' = "hello"
Str.StartsWith("Hello", "He") ' = true
Str.EndsWith("Hello", "lo")   ' = true
Str.Repeat("ab", 3)           ' = "ababab"
Str.Reverse("Hello")          ' = "olleH"
Str.PadLeft("42", 5, "0")     ' = "00042"
```

### File
```basic
var content as String = File.ReadAllText("file.txt")
File.WriteAllText("file.txt", "Hello")
File.AppendAllText("file.txt", " World")
var exists as Boolean = File.Exists("file.txt")
File.Delete("file.txt")
File.Copy("src.txt", "dst.txt")
File.Move("old.txt", "new.txt")
var lines as String[] = File.ReadAllLines("file.txt")
File.CreateDirectory("mydir")
var files as String[] = File.GetFiles("mydir")
```

### Regex
```basic
Regex.IsMatch("hello", "h.*o")        ' = true
Regex.Replace("hello", "[aeiou]", "X") ' = "hXllX"
Regex.Find("test@email.com", "\\w+@\\w+") ' = "test@email"
var parts as String[] = Regex.Split("a1b2c3", "\\d")
var groups as String[] = Regex.Groups("test@email.com", "(\\w+)@(\\w+)")
```

### Http
```basic
' Simple GET request
var response as String = Http.Get("https://api.example.com/data")
var status as Integer = Http.GetStatus()
var success as Boolean = Http.IsSuccess()

' POST with body
var result as String = Http.Post("https://api.example.com/users", "{\"name\":\"Alice\"}")

' Set headers
Http.SetHeader("Authorization", "Bearer token123")
Http.SetHeader("Content-Type", "application/json")

' Other HTTP methods
Http.Put(url, body)
Http.Patch(url, body)
Http.Delete(url)

' URL utilities
var encoded as String = Http.UrlEncode("Hello World!")
var decoded as String = Http.UrlDecode("Hello%20World%21")

' Download file
Http.Download("https://example.com/file.zip", "/tmp/file.zip")
```

### Json
```basic
' Create and modify JSON
var json as String = Json.Create()
json = Json.Set(json, "name", "Alice")
json = Json.Set(json, "age", "30")
json = Json.SetBool(json, "active", true)
json = Json.SetInt(json, "score", 100)

' Read values
var name as String = Json.Get(json, "name")
var age as Integer = Json.GetInt(json, "age")
var active as Boolean = Json.GetBool(json, "active")

' Check properties
var hasName as Boolean = Json.Has(json, "name")
var isValid as Boolean = Json.IsValid(json)
var isObject as Boolean = Json.IsObject(json)

' Arrays
var arr as String = Json.CreateArray()
arr = Json.Push(arr, "apple")
arr = Json.Push(arr, "banana")
var length as Integer = Json.Length(arr)
var isArray as Boolean = Json.IsArray(arr)

' Nested JSON
var nested as String = Json.Create()
nested = Json.SetJson(nested, "user", json)
nested = Json.SetJson(nested, "fruits", arr)

' Pretty print
Console.WriteLine(Json.Pretty(nested))
```

### Db (Database)
```basic
' Connect to database
var ok as Boolean = Db.Connect("jdbc:mysql://localhost:3306/mydb", "user", "pass")

' Simple query
Db.Query("SELECT * FROM users")
while Db.Next()
    Console.WriteLine(Db.GetString("name") & " - " & Db.GetInt("age"))
end while
Db.CloseResults()

' Parameterized queries (SQL injection safe)
Db.Prepare("INSERT INTO users (name, age) VALUES (?, ?)")
Db.SetString(1, "Alice")
Db.SetInt(2, 30)
var rows as Integer = Db.ExecuteUpdate()

' Transactions
Db.BeginTransaction()
Db.Execute("UPDATE accounts SET balance = balance - 100 WHERE id = 1")
Db.Execute("UPDATE accounts SET balance = balance + 100 WHERE id = 2")
Db.Commit()  ' or Db.Rollback() on error

' Close connection
Db.Close()
```

### Arbitrary Precision Numbers
```basic
' BigInteger - arbitrary precision integers
var huge as BigInteger = BigInt.FromString("123456789012345678901234567890")
var small as BigInteger = BigInt.FromInt(42)
var sum as BigInteger = BigInt.Add(huge, small)
var power as BigInteger = BigInt.Pow(BigInt.FromInt(2), 100)
Console.WriteLine("2^100 = " + power)

' Decimal - arbitrary precision decimals
var pi as Decimal = Decimal.FromString("3.14159265358979323846")
var precise as Decimal = Decimal.Divide(Decimal.FromInt(1), Decimal.FromInt(3))
var scaled as Decimal = Decimal.SetScale(precise, 10)  ' 10 decimal places
Console.WriteLine("1/3 = " + scaled)

' Conversions
var asLong as Long = BigInt.ToLong(small)
var asDouble as Double = Decimal.ToDouble(pi)
```

### Date (Date/Time Operations)
```basic
' Current date/time
var now as String = Date.Now()              ' Full timestamp
var today as String = Date.Today()          ' Date only
var time as String = Date.Time()            ' Time only

' Formatting
var formatted as String = Date.Format(now, "yyyy-MM-dd HH:mm:ss")
var custom as String = Date.Format(now, "MMMM d, yyyy")

' Parsing
var parsed as String = Date.Parse("2024-12-25", "yyyy-MM-dd")

' Date arithmetic
var nextWeek as String = Date.AddDays(today, 7)
var nextMonth as String = Date.AddMonths(today, 1)
var nextYear as String = Date.AddYears(today, 1)

' Components
var year as Integer = Date.Year(now)
var month as Integer = Date.Month(now)
var day as Integer = Date.Day(now)
var dayOfWeek as String = Date.DayOfWeek(now)

' Comparison
var isBefore as Boolean = Date.IsBefore(date1, date2)
var isAfter as Boolean = Date.IsAfter(date1, date2)
var daysBetween as Long = Date.DaysBetween(date1, date2)

' Time zones
var utc as String = Date.ToUtc(now)
var local as String = Date.ToLocal(utc)
var inZone as String = Date.ToTimeZone(now, "America/New_York")
```

### Crypto (Cryptography)

```basic
' Hashing
var sha256 as String = Crypto.SHA256("message")
var sha512 as String = Crypto.SHA512("message")
var md5 as String = Crypto.MD5("message")

' HMAC
var hmac as String = Crypto.HMAC("message", "secret", "SHA256")

' Password hashing (secure)
var bcrypt as String = Crypto.BCrypt("password")
var argon2 as String = Crypto.Argon2("password")
var validBcrypt as Boolean = Crypto.VerifyBCrypt("password", bcrypt)
var validArgon2 as Boolean = Crypto.VerifyArgon2("password", argon2)

' AES encryption
var encrypted as String = Crypto.AESEncrypt("plaintext", "key")
var decrypted as String = Crypto.AESDecrypt(encrypted, "key")

' RSA key generation and encryption
var keyPair as String = Crypto.RSAGenerateKeyPair(2048)
var pubKey as String = Crypto.RSAGetPublicKey(keyPair)
var privKey as String = Crypto.RSAGetPrivateKey(keyPair)
var rsaEncrypted as String = Crypto.RSAEncrypt("message", pubKey)
var rsaDecrypted as String = Crypto.RSADecrypt(rsaEncrypted, privKey)

' Digital signatures
var signature as String = Crypto.RSASign("message", privKey)
var isValid as Boolean = Crypto.RSAVerify("message", signature, pubKey)

' Encoding
var base64 as String = Crypto.Base64Encode("Hello")
var decoded as String = Crypto.Base64Decode(base64)
var hex as String = Crypto.HexEncode("Hello")
var fromHex as String = Crypto.HexDecode(hex)

' Random
var randomBytes as String = Crypto.RandomBytes(32)  ' Base64 encoded
var uuid as String = Crypto.UUID()
```

## Documentation

- [User Guide](docs/USER_GUIDE.md) - Language reference
- [Developer Guide](docs/DEVELOPER_GUIDE.md) - Compiler internals
- [Architecture](docs/ARCHITECTURE.md) - Compiler pipeline and design
- [TODO](docs/TODO.md) - Development roadmap

## Compiler Architecture

The JVM BASIC 2.0 compiler uses a multi-phase pipeline:

```
Source Code (.jvmb)
       │
       ▼
┌─────────────────┐
│  ANTLR Lexer    │  Tokenizes source into token stream
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  ANTLR Parser   │  Builds concrete syntax tree (CST)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Symbol Collector│  Pass 1: Gathers function/class/variable declarations
│   (Listener)    │  Uses ANTLR listener pattern for automatic tree walk
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   IR Builder    │  Builds typed intermediate representation
│   (Visitor)     │  Uses ANTLR visitor pattern for controlled traversal
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Semantic      │  Optional: Type checking, reference validation
│   Analyzer      │  Enabled with -semantic flag
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Compiler Visitor│  Pass 2: Generates JVM bytecode using ASM
│   (Visitor)     │  Produces .class files directly
└────────┬────────┘
         │
         ▼
    .class file(s)
```

### Listener vs Visitor

| Pattern | Used For | Key Trait |
|---------|----------|-----------|
| **Listener** | Symbol collection, validation | Automatic tree walk, no return values |
| **Visitor** | IR building, code generation | Controlled traversal, return values |

## Legacy Implementation

The original C++ implementation is archived in `legacy-jvm-basic/`. It includes 23 namespaces and 280+ functions but is no longer in active development.

## License

MIT License - See LICENSE file for details.
