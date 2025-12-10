# JVM BASIC 2.0 Language Features

Comprehensive reference of all implemented language features.

## Data Types

### Primitive Types

| Type | Size | JVM Type | Range/Notes |
|------|------|----------|-------------|
| `Integer` | 32-bit | `int` | -2,147,483,648 to 2,147,483,647 |
| `Long` | 64-bit | `long` | -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807 |
| `Float` | 32-bit | `float` | IEEE 754 single precision |
| `Double` | 64-bit | `double` | IEEE 754 double precision |
| `Boolean` | 1-bit | `boolean` | `true` or `false` |
| `Byte` | 8-bit | `byte` | -128 to 127 |
| `Char` | 16-bit | `char` | Unicode character |
| `BigInteger` | Arbitrary | `java.math.BigInteger` | Unlimited precision integers |
| `Decimal` | Arbitrary | `java.math.BigDecimal` | Unlimited precision decimals |

### Reference Types

| Type | JVM Type | Notes |
|------|----------|-------|
| `String` | `java.lang.String` | Immutable text |
| `Object` | `java.lang.Object` | Base object type |
| `Type[]` | Array of Type | Single-dimensional arrays |
| User classes | Generated classes | User-defined types |

### Literals

```basic
' Integer literals
var dec as Integer = 255
var hex as Integer = 0xFF
var bin as Integer = 0b11111111
var oct as Integer = 0o377

' Long literals (L suffix)
var bigNum as Long = 9223372036854775807L

' Float literals (F suffix)
var pi as Float = 3.14159F

' Double literals
var precise as Double = 2.718281828459045

' Boolean literals
var yes as Boolean = true
var no as Boolean = false

' String literals with escapes
var text as String = "Hello\nWorld"
var quoted as String = "She said \"Hi\""

' Character literals
var ch as Char = 'A'

' Null
var empty as Object = nil
```

---

## Variables

### Declaration

```basic
' With type and initializer
var name as String = "Alice"

' With type only (default value)
var count as Integer        ' Defaults to 0
var text as String          ' Defaults to null

' Nullable types
var maybeNull as String? = nil
```

### Assignment

```basic
var x as Integer = 10
x = 20                      ' Simple assignment
x += 5                      ' Compound: x = x + 5
x -= 3                      ' Compound: x = x - 3
x *= 2                      ' Compound: x = x * 2
x /= 4                      ' Compound: x = x / 4
```

### Block Scoping

Variables are scoped to their containing block:

```basic
for i = 1 to 3
    var temp as Integer = i * 10   ' Scoped to FOR block
    Console.WriteLine(temp)
next i
' temp is NOT accessible here

for j = 1 to 3
    var temp as Integer = j * 100  ' New temp, reuses slot
    Console.WriteLine(temp)
next j
```

---

## Operators

### Arithmetic

| Operator | Description | Example |
|----------|-------------|---------|
| `+` | Addition | `a + b` |
| `-` | Subtraction | `a - b` |
| `*` | Multiplication | `a * b` |
| `/` | Division | `a / b` |
| `\` | Integer division | `a \ b` |
| `mod` | Modulo | `a mod b` |
| `^` | Power | `a ^ 2` |

### Comparison

| Operator | Description |
|----------|-------------|
| `=` | Equal |
| `<>` | Not equal |
| `<` | Less than |
| `>` | Greater than |
| `<=` | Less or equal |
| `>=` | Greater or equal |

### Logical

| Operator | Description |
|----------|-------------|
| `and` | Logical AND |
| `or` | Logical OR |
| `xor` | Exclusive OR |
| `not` | Logical NOT |

### Bitwise

| Operator | Description |
|----------|-------------|
| `\|` | Bitwise OR |
| `~` | Bitwise NOT |
| `<<` | Left shift |
| `>>` | Right shift |

### String

| Operator | Description |
|----------|-------------|
| `+` | Concatenation (modern) |
| `&` | Concatenation (legacy) |

### String Interpolation

```basic
var name as String = "Alice"
var age as Integer = 30
Console.WriteLine($"Hello, {name}! Age: {age}")
Console.WriteLine($"2 + 2 = {2 + 2}")
```

### Ternary

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
' Count up
for i = 1 to 10
    Console.WriteLine(i)
next i

' With step
for i = 0 to 100 step 10
    Console.WriteLine(i)
next i

' Count down
for i = 10 to 1 step -1
    Console.WriteLine(i)
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
    Console.WriteLine(count)
    count += 1
end while
```

### Do Loop

```basic
' Do While (condition at start)
var x as Integer = 0
do while x < 5
    Console.WriteLine(x)
    x += 1
loop

' Do Until (condition at start)
var y as Integer = 0
do until y >= 5
    Console.WriteLine(y)
    y += 1
loop

' Loop While (condition at end)
var z as Integer = 0
do
    Console.WriteLine(z)
    z += 1
loop while z < 5
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
    case 4, 5
        Console.WriteLine("Thursday or Friday")
    case else
        Console.WriteLine("Weekend")
end select
```

### Exit and Continue

```basic
' Exit loop early
for i = 1 to 100
    if i = 50 then
        exit for
    end if
next i

' Skip to next iteration
for i = 1 to 10
    if i mod 2 = 0 then
        continue for
    end if
    Console.WriteLine(i)  ' Only odd numbers
next i

' Exit types: exit for, exit while, exit do, exit sub, exit function, exit select
' Continue types: continue for, continue while, continue do
```

---

## Functions and Subroutines

### Functions

```basic
function add(a as Integer, b as Integer) as Integer
    return a + b
end function

var sum as Integer = add(5, 3)
```

### Subroutines

```basic
sub printGreeting(name as String)
    Console.WriteLine("Hello, " & name & "!")
end sub

printGreeting("World")
```

### Default Parameters

```basic
function greet(name as String = "Guest") as String
    return "Hello, " & name & "!"
end function

Console.WriteLine(greet())        ' "Hello, Guest!"
Console.WriteLine(greet("Alice")) ' "Hello, Alice!"
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
' Now x = 20, y = 10
```

### Forward References

Functions can be called before they're defined:

```basic
Console.WriteLine(factorial(5))

function factorial(n as Integer) as Integer
    if n <= 1 then
        return 1
    end if
    return n * factorial(n - 1)
end function
```

---

## Arrays

### Declaration and Creation

```basic
var numbers as Integer[] = new Integer[5]
var names as String[] = new String[10]
```

### Element Access

```basic
numbers[0] = 100
numbers[1] = 200
var first as Integer = numbers[0]
```

### Iteration

```basic
' Using for loop
for i = 0 to 4
    Console.WriteLine(numbers[i])
next i

' Using for each
for each num in numbers
    Console.WriteLine(num)
next
```

### Array Parameters

```basic
function sumArray(arr as Integer[]) as Integer
    var total as Integer = 0
    for each n in arr
        total += n
    next
    return total
end function
```

---

## Classes and OOP

### Class Definition

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
```

### Object Creation

```basic
var alice as Person = new Person("Alice", 30)
Console.WriteLine(alice.Greet())
Console.WriteLine(alice.age)
```

### Access Modifiers

| Modifier | Visibility |
|----------|------------|
| `public` | Accessible from anywhere |
| `private` | Accessible only within class |
| `protected` | Accessible within class and subclasses |

### Inheritance

```basic
class Employee extends Person
    private var salary as Double

    public sub New(name as String, age as Integer, sal as Double)
        Super.New(name, age)
        this.salary = sal
    end sub

    public override function Greet() as String
        return Super.Greet() & " - Employee"
    end function
end class
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
    Console.WriteLine("Cleanup code")
end try
```

### Multiple Catch Blocks

```basic
try
    ' Risky code
catch ex as ArithmeticException
    Console.WriteLine("Math error")
catch ex as NullPointerException
    Console.WriteLine("Null error")
catch ex as Exception
    Console.WriteLine("Other error")
end try
```

---

## Standard Library Namespaces

### Console

```basic
Console.WriteLine("Hello")      ' Print with newline
Console.Write("Hello")          ' Print without newline
var input as String = Console.ReadLine()
```

### Math

```basic
Math.Sqrt(16.0)              ' 4.0
Math.Pow(2.0, 10.0)          ' 1024.0
Math.Sin(Math.Pi() / 2)      ' 1.0
Math.Cos(0.0)                ' 1.0
Math.Abs(-42)                ' 42
Math.Max(10, 20)             ' 20
Math.Min(10, 20)             ' 10
Math.Floor(3.7)              ' 3.0
Math.Ceil(3.2)               ' 4.0
Math.Round(3.5)              ' 4
Math.Random()                ' 0.0 to 1.0
Math.Random(1, 100)          ' Integer 1 to 100
Math.Log(x)                  ' Natural log
Math.Log10(x)                ' Log base 10
```

### Str

```basic
' Length, case conversion
Str.Length("Hello")          ' 5
Str.ToUpper("hello")         ' "HELLO"
Str.ToLower("HELLO")         ' "hello"

' Substring extraction (start index, length)
Str.Substring("Hello", 0, 2) ' "He" - 2 chars from position 0
Str.Substring("Hello", 1, 3) ' "ell" - 3 chars from position 1
Str.Substring("Hello", 2)    ' "llo" - from position 2 to end
Str.Left("Hello", 3)         ' "Hel" - first 3 characters
Str.Right("Hello", 3)        ' "llo" - last 3 characters

' Search and replace
Str.IndexOf("Hello", "l")    ' 2 - first occurrence
Str.LastIndexOf("Hello", "l") ' 3 - last occurrence
Str.Replace("Hello", "l", "L") ' "HeLLo"
Str.Split("a,b,c", ",")      ' ["a", "b", "c"]

' Trimming and padding
Str.Trim("  hello  ")        ' "hello"
Str.TrimStart("  hello")     ' "hello"
Str.TrimEnd("hello  ")       ' "hello"
Str.PadLeft("42", 5, "0")    ' "00042"
Str.PadRight("Hi", 5, "!")   ' "Hi!!!"

' Testing
Str.StartsWith("Hello", "He") ' true
Str.EndsWith("Hello", "lo")  ' true
Str.Contains("Hello", "ell") ' true
Str.IsEmpty("")              ' true
Str.IsNullOrEmpty(nil)       ' true

' Transform
Str.Repeat("ab", 3)          ' "ababab"
Str.Reverse("Hello")         ' "olleH"
Str.Join(", ", arr)          ' Join array with separator
Str.Chr(65)                  ' "A" - character from code
Str.Asc("A")                 ' 65 - code from character
```

### File

```basic
var text as String = File.ReadAllText("file.txt")
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
Regex.IsMatch("hello", "h.*o")           ' true
Regex.Replace("hello", "[aeiou]", "X")   ' "hXllX"
Regex.Find("test@email.com", "\\w+@\\w+") ' "test@email"
Regex.Split("a1b2c3", "\\d")             ' ["a", "b", "c"]
Regex.Groups("test@email.com", "(\\w+)@(\\w+)") ' ["test@email.com", "test", "email"]
```

### Http

```basic
var response as String = Http.Get("https://api.example.com")
var status as Integer = Http.GetStatus()
var success as Boolean = Http.IsSuccess()

Http.SetHeader("Authorization", "Bearer token")
var result as String = Http.Post(url, body)
Http.Put(url, body)
Http.Patch(url, body)
Http.Delete(url)

var encoded as String = Http.UrlEncode("Hello World!")
var decoded as String = Http.UrlDecode("Hello%20World")
Http.Download(url, filepath)
```

### Json

```basic
var json as String = Json.Create()
json = Json.Set(json, "name", "Alice")
json = Json.SetInt(json, "age", 30)
json = Json.SetBool(json, "active", true)

var name as String = Json.Get(json, "name")
var age as Integer = Json.GetInt(json, "age")
var active as Boolean = Json.GetBool(json, "active")

var hasKey as Boolean = Json.Has(json, "name")
var valid as Boolean = Json.IsValid(json)
var isObj as Boolean = Json.IsObject(json)

var arr as String = Json.CreateArray()
arr = Json.Push(arr, "apple")
arr = Json.Push(arr, "banana")
var len as Integer = Json.Length(arr)
var isArr as Boolean = Json.IsArray(arr)

Console.WriteLine(Json.Pretty(json))
```

### Db

```basic
var ok as Boolean = Db.Connect("jdbc:mysql://localhost/mydb", "user", "pass")

Db.Query("SELECT * FROM users")
while Db.Next()
    Console.WriteLine(Db.GetString("name"))
end while
Db.CloseResults()

' Parameterized queries (SQL injection safe)
Db.Prepare("INSERT INTO users (name, age) VALUES (?, ?)")
Db.SetString(1, "Alice")
Db.SetInt(2, 30)
var rows as Integer = Db.ExecuteUpdate()
Db.CloseStmt()

' Transactions
Db.BeginTransaction()
Db.Execute("UPDATE accounts SET balance = balance - 100 WHERE id = 1")
Db.Execute("UPDATE accounts SET balance = balance + 100 WHERE id = 2")
Db.Commit()  ' or Db.Rollback()

Db.Close()
```

### BigInt (Arbitrary Precision Integers)

```basic
' Factory methods
var a as BigInteger = BigInt.FromString("123456789012345678901234567890")
var b as BigInteger = BigInt.FromLong(9999999999L)
var c as BigInteger = BigInt.FromInt(42)
var zero as BigInteger = BigInt.Zero()
var one as BigInteger = BigInt.One()
var ten as BigInteger = BigInt.Ten()

' Arithmetic
var sum as BigInteger = BigInt.Add(a, b)
var diff as BigInteger = BigInt.Subtract(a, b)
var prod as BigInteger = BigInt.Multiply(a, b)
var quot as BigInteger = BigInt.Divide(a, b)
var rem as BigInteger = BigInt.Mod(a, b)
var power as BigInteger = BigInt.Pow(a, 10)
var gcd as BigInteger = BigInt.Gcd(a, b)

' Unary operations
var abs as BigInteger = BigInt.Abs(a)
var neg as BigInteger = BigInt.Negate(a)
var sqrt as BigInteger = BigInt.Sqrt(a)

' Comparison
var cmp as Integer = BigInt.Compare(a, b)  ' -1, 0, or 1
var eq as Boolean = BigInt.Equals(a, b)
var max as BigInteger = BigInt.Max(a, b)
var min as BigInteger = BigInt.Min(a, b)
var sign as Integer = BigInt.Signum(a)     ' -1, 0, or 1

' Bitwise operations
var andResult as BigInteger = BigInt.And(a, b)
var orResult as BigInteger = BigInt.Or(a, b)
var xorResult as BigInteger = BigInt.Xor(a, b)
var notResult as BigInteger = BigInt.Not(a)
var shl as BigInteger = BigInt.ShiftLeft(a, 10)
var shr as BigInteger = BigInt.ShiftRight(a, 10)

' Properties
var bits as Integer = BigInt.BitLength(a)
var bitCount as Integer = BigInt.BitCount(a)
var isPrime as Boolean = BigInt.IsProbablePrime(a, 10)

' Conversions
var asLong as Long = BigInt.ToLong(a)
var asInt as Integer = BigInt.ToInt(a)
var asDouble as Double = BigInt.ToDouble(a)
var asStr as String = BigInt.ToString(a)
var asHex as String = BigInt.ToString(a, 16)
var asDec as Decimal = BigInt.ToDecimal(a)

' Advanced
var modPow as BigInteger = BigInt.ModPow(a, b, c)
var modInv as BigInteger = BigInt.ModInverse(a, b)
```

### Decimal (Arbitrary Precision Decimals)

```basic
' Factory methods
var d1 as Decimal = Decimal.FromString("3.14159265358979323846")
var d2 as Decimal = Decimal.FromDouble(3.14159)
var d3 as Decimal = Decimal.FromLong(9999999999L)
var d4 as Decimal = Decimal.FromInt(100)
var d5 as Decimal = Decimal.FromBigInt(bigIntValue)
var zero as Decimal = Decimal.Zero()
var one as Decimal = Decimal.One()
var ten as Decimal = Decimal.Ten()

' Arithmetic
var sum as Decimal = Decimal.Add(d1, d2)
var diff as Decimal = Decimal.Subtract(d1, d2)
var prod as Decimal = Decimal.Multiply(d1, d2)
var quot as Decimal = Decimal.Divide(d1, d2)
var rem as Decimal = Decimal.Remainder(d1, d2)
var power as Decimal = Decimal.Pow(d1, 2)

' Unary operations
var abs as Decimal = Decimal.Abs(d1)
var neg as Decimal = Decimal.Negate(d1)
var sqrt as Decimal = Decimal.Sqrt(d1)

' Comparison
var cmp as Integer = Decimal.Compare(d1, d2)  ' -1, 0, or 1
var eq as Boolean = Decimal.Equals(d1, d2)
var max as Decimal = Decimal.Max(d1, d2)
var min as Decimal = Decimal.Min(d1, d2)
var sign as Integer = Decimal.Signum(d1)      ' -1, 0, or 1

' Scale and precision
var scale as Integer = Decimal.Scale(d1)
var prec as Integer = Decimal.Precision(d1)
var scaled as Decimal = Decimal.SetScale(d1, 2)
var stripped as Decimal = Decimal.StripTrailingZeros(d1)

' Rounding modes (pass as second arg to SetScale)
' ROUND_UP, ROUND_DOWN, ROUND_CEILING, ROUND_FLOOR
' ROUND_HALF_UP, ROUND_HALF_DOWN, ROUND_HALF_EVEN, ROUND_UNNECESSARY

' Conversions
var asDouble as Double = Decimal.ToDouble(d1)
var asLong as Long = Decimal.ToLong(d1)
var asInt as Integer = Decimal.ToInt(d1)
var asStr as String = Decimal.ToString(d1)
var asPlain as String = Decimal.ToPlainString(d1)  ' No scientific notation
var asBigInt as BigInteger = Decimal.ToBigInt(d1)
```

---

## Features Not Yet Implemented

| Feature | Status | Notes |
|---------|--------|-------|
| Interfaces | Planned | `interface`, `implements` |
| Properties | Planned | `get`/`set` accessors |
| Static members | Planned | `static` keyword |
| Async/Await | Planned | Concurrency support |
| Channels | Planned | Go-style communication |
| Lambda expressions | Planned | `x => x * 2` |
| Generic types | Planned | Type parameters |
| Enums | Planned | Enumeration types |
