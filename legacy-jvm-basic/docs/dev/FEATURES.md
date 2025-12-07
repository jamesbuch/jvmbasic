# JVM BASIC Complete Feature List

**Version**: Phase 12 (System Namespace)
**Status**: Production-Ready Language
**Last Updated**: December 2025

---

## Language Features

### Data Types (9 total)

**Scalar Types:**
- **Integer** - 32-bit signed integers
- **Single/Float** - 32-bit IEEE 754 floating point
- **Double** - 64-bit IEEE 754 floating point
- **String** - Java String objects (immutable)
- **Boolean** - Boolean (True/False)
- **BigInt** - Arbitrary precision integers (java.math.BigInteger)
- **Decimal** - Arbitrary precision decimals (java.math.BigDecimal)

**Array Types:**
- **IntArray** - Integer arrays
- **FloatArray** - Float arrays
- **DoubleArray** - Double arrays
- **StringArray** - String arrays
- **BoolArray** - Boolean arrays

**User-Defined Types:**
- **TYPE...END TYPE** - Struct definitions
- **CLASS...END CLASS** - Object-oriented classes

**Type Features:**
- Automatic Int→Float promotion in expressions
- Type checking at compile time
- Type inference for arrays (from init value)
- Modern VB-style declarations: `Dim x As Integer = 10`

---

### Operators

**Arithmetic:**
- `+` Addition (also string concatenation)
- `-` Subtraction
- `*` Multiplication
- `/` Division
- `Mod` Modulo/Remainder

**Comparison:**
- `<` Less than
- `>` Greater than
- `<=` Less than or equal
- `>=` Greater than or equal
- `=` Equal to
- `<>` Not equal to

**Logical:**
- `And` Logical AND
- `Or` Logical OR
- `Not` Logical NOT
- `Xor` Logical XOR

**Bitwise:**
- `&` Bitwise AND
- `|` Bitwise OR
- `^` Bitwise XOR
- `<<` Shift left
- `>>` Shift right

---

### Control Flow Statements

**IF Statement:**
```basic
If condition Then
    <statements>
ElseIf condition Then
    <statements>
Else
    <statements>
End If
```

**FOR Loop:**
```basic
For i = 1 To 10 Step 2
    <statements>
Next i
```

**WHILE Loop:**
```basic
While condition
    <statements>
Wend
```

**DO...WHILE/UNTIL Loop:**
```basic
Do
    <statements>
While condition

Do
    <statements>
Until condition
```

**Loop Control:**
- `Exit For` - Break out of For loop
- `Exit While` - Break out of While loop
- `Continue` - Skip to next iteration

---

### Functions and Subroutines

**Functions (with return value):**
```basic
Function Add(a As Integer, b As Integer) As Integer
    Return a + b
End Function
```

**Subroutines (no return value):**
```basic
Sub PrintMessage(msg As String)
    Console.WriteLine(msg)
End Sub
```

**Recursion:** Fully supported

---

### Object-Oriented Programming (Phase 7)

**Class Definition:**
```basic
Class ClassName
    Private field1 As Integer
    Public field2 As String

    Public Sub New(param As Integer)
        Me.field1 = param
    End Sub

    Public Function GetValue() As Integer
        Return Me.field1
    End Function
End Class
```

**Object Creation:**
```basic
Dim obj As New ClassName(42)
obj.field2 = "Hello"
Dim val As Integer = obj.GetValue()
```

**Features:**
- PUBLIC/PRIVATE access modifiers
- Constructor (Sub New)
- Instance methods
- ME keyword (self-reference)
- Multiple classes per program

---

### User-Defined Types (Phase 6)

**Struct Definition:**
```basic
Type Person
    name As String
    age As Integer
End Type

Dim p As Person
p.name = "Alice"
p.age = 30
```

---

## Standard Library (23 Namespaces)

### Console Namespace
| Function | Description |
|----------|-------------|
| `Console.WriteLine(text)` | Print with newline |
| `Console.Write(text)` | Print without newline |
| `Console.ReadLine()` | Read line of input |
| `Console.ReadKey()` | Read single character |

### Math Namespace
| Function | Description |
|----------|-------------|
| `Math.Sin(x)`, `Math.Cos(x)`, `Math.Tan(x)` | Trigonometric |
| `Math.Asin(x)`, `Math.Acos(x)`, `Math.Atan(x)` | Inverse trig |
| `Math.Sqrt(x)`, `Math.Pow(x, y)` | Power functions |
| `Math.Exp(x)`, `Math.Log(x)`, `Math.Log10(x)` | Exponential |
| `Math.Abs(x)`, `Math.Floor(x)`, `Math.Ceil(x)` | Rounding |
| `Math.Min(a, b)`, `Math.Max(a, b)` | Min/Max |
| `Math.PI()`, `Math.E()` | Constants |

### File Namespace
| Function | Description |
|----------|-------------|
| `File.ReadAllText(path)` | Read entire file |
| `File.WriteAllText(path, content)` | Write to file |
| `File.Exists(path)` | Check if file exists |
| `File.Delete(path)` | Delete file |
| `File.Copy(src, dest)` | Copy file |
| `File.Move(src, dest)` | Move/rename file |
| `File.Size(path)` | Get file size |
| `File.OpenReader(path)` | Open for reading |
| `File.ReadLine(handle)` | Read next line |

### Http Namespace
| Function | Description |
|----------|-------------|
| `Http.Get(url)` | HTTP GET request |
| `Http.Post(url, data)` | HTTP POST request |
| `Http.UrlEncode(text)` | URL encode |
| `Http.UrlDecode(text)` | URL decode |

### Json Namespace
| Function | Description |
|----------|-------------|
| `Json.Parse(jsonStr)` | Parse JSON string |
| `Json.GetString(obj, key)` | Get string value |
| `Json.GetInt(obj, key)` | Get integer value |
| `Json.NewObject()` | Create new JSON object |
| `Json.Put(obj, key, value)` | Set string value |
| `Json.PutInt(obj, key, value)` | Set integer value |
| `Json.ToString(obj)` | Convert to JSON string |

### Db Namespace
| Function | Description |
|----------|-------------|
| `Db.Connect(url, user, pass)` | Connect to database |
| `Db.Query(conn, sql)` | Execute SELECT query |
| `Db.Execute(conn, sql)` | Execute INSERT/UPDATE/DELETE |
| `Db.NextRow(result)` | Move to next row |
| `Db.GetString(result, col)` | Get string column |
| `Db.GetInt(result, col)` | Get integer column |
| `Db.CloseResult(result)` | Close result set |
| `Db.Close(conn)` | Close connection |
| `Db.BeginTransaction(conn)` | Start transaction |
| `Db.Commit(conn)` | Commit transaction |
| `Db.Rollback(conn)` | Rollback transaction |

### Crypto Namespace
| Function | Description |
|----------|-------------|
| `Crypto.Sha256(text)` | SHA-256 hash |
| `Crypto.Sha512(text)` | SHA-512 hash |
| `Crypto.Md5(text)` | MD5 hash |
| `Crypto.AesEncrypt(text, key)` | AES encryption |
| `Crypto.AesDecrypt(text, key)` | AES decryption |
| `Crypto.Base64Encode(text)` | Base64 encode |
| `Crypto.Base64Decode(text)` | Base64 decode |

### Thread Namespace
| Function | Description |
|----------|-------------|
| `Thread.Sleep(millis)` | Sleep for milliseconds |
| `Thread.CurrentId()` | Get current thread ID |
| `Thread.Lock(name)` | Acquire named lock |
| `Thread.Unlock(name)` | Release named lock |
| `Thread.AtomicAdd(name, delta)` | Atomic increment |

### System Namespace (Phase 12)
| Function | Description |
|----------|-------------|
| `System.exit(code)` | Exit program with code |
| `System.getenv(name)` | Get environment variable |
| `System.currentTimeMillis()` | Get time in milliseconds |
| `System.nanoTime()` | Get time in nanoseconds |
| `System.gc()` | Request garbage collection |

### Additional Namespaces
- **Xml** - XML parsing with XPath
- **Path** - Path manipulation
- **Dir** - Directory operations
- **Args** - Command-line arguments
- **Regex** - Regular expressions
- **Array** - Array operations
- **Str** - String formatting
- **IntList/StringList** - Dynamic lists
- **Map** - Key-value storage
- **Stack** - LIFO collection
- **Queue** - FIFO collection
- **BigInt** - Arbitrary precision integers
- **Decimal** - Arbitrary precision decimals

---

## Development History

### Phase 12 (Current)
- System namespace for program control
- System.exit(), System.getenv(), System.nanoTime()

### Phase 11
- BigInt and Decimal namespaces
- Crypto namespace (SHA, AES, Base64)
- Thread namespace

### Phase 10
- String interpolation (`$"Hello {name}!"`)
- Type system fixes

### Phase 9
- 23 namespaces with modern syntax
- HTTP, JSON, XML, Database support
- 16 professional library JARs

### Phase 8
- 199 built-in functions
- Collections (Map, Stack, Queue)
- Logical operators

### Phase 7
- Object-oriented programming
- Classes with constructors and methods

### Phase 6
- User-defined types (structs)

### Phase 5 and Earlier
- Functions with recursion
- Arrays and control flow
- Modular compiler architecture

---

## Included Libraries

- **Google Gson 2.10.1** - JSON
- **PostgreSQL JDBC 42.7.1** - PostgreSQL
- **MariaDB JDBC 3.3.2** - MariaDB/MySQL
- **Apache Commons** (IO, Lang3, Text, Math3, Codec)
- **Bouncy Castle 1.77** - Cryptography
- **Jetty 11.0.19** - Web server
- **ANTLR4 4.13.1** - Parser generation
