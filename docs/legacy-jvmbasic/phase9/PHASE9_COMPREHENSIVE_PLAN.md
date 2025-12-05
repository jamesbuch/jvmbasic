# Phase 9 Comprehensive Implementation Plan

**Goal**: Transform JVM BASIC into a modern, OO-capable language with web/database support

---

## ✅ Already Complete
- Modern type keywords (Integer, Single, Double, Long, Boolean, String)
- Modern variable declarations (Dim x As Integer = 10)
- Modern function syntax (Function Name() As Integer)
- Case-insensitive keywords (If/IF, Dim/DIM all work)
- Console I/O functions (ConsoleWriteLine, etc.)

---

## 🎯 This Session Goals

### 1. Bitwise Operators (Phase 9.5)
**Priority**: HIGH  
**Complexity**: LOW

```basic
Dim a As Integer = 5      ' 0101
Dim b As Integer = 3      ' 0011

' Bitwise operations:
Dim c = a And b           ' Bitwise AND = 1 (0001)
Dim d = a Or b            ' Bitwise OR = 7 (0111)
Dim e = a Xor b           ' Bitwise XOR = 6 (0110)
Dim f = Not a             ' Bitwise NOT (complement)
Dim g = a << 2            ' Left shift = 20
Dim h = a >> 1            ' Right shift = 2
```

**Implementation**:
- Parser: Add bitwise expression parsing (context-sensitive: integer operands = bitwise, boolean = logical)
- Semantic: Type checking for integer operands
- Codegen: JVM bytecode (iand, ior, ixor, ishl, ishr)

---

### 2. Decimal Type (Phase 9.16)
**Priority**: HIGH  
**Complexity**: MEDIUM

```basic
Dim price As Decimal = 19.99
Dim tax As Decimal = 0.08
Dim total As Decimal = price * (1 + tax)  ' Precise calculation
```

**Implementation**:
- Type system: Add Type::Decimal
- Lexer: Support decimal literals with 'D' suffix (123.45D)
- Parser: Accept DECIMAL type keyword
- Semantic: Type checking
- Codegen: Use java.math.BigDecimal
- Runtime: Arithmetic operators (add, sub, mul, div, etc.)

---

### 3. BigInt Type (Phase 9.17)
**Priority**: HIGH  
**Complexity**: MEDIUM

```basic
Dim factorial As BigInt = 1
For i = 1 To 100
    factorial = factorial * i
Next i
```

**Implementation**:
- Type system: Add Type::BigInt
- Lexer: Support bigint literals with 'L' or 'N' suffix
- Parser: Accept BIGINT type keyword
- Semantic: Type checking
- Codegen: Use java.math.BigInteger
- Runtime: Arithmetic operators

---

### 4. OO-Style Namespaces and Methods

#### 4a. Math Namespace (Phase 9.18)
**Priority**: MEDIUM

```basic
Dim angle = Math.PI / 4
Dim result = Math.Sin(angle)
Dim power = Math.Pow(2, 10)
Dim root = Math.Sqrt(144)
```

**Implementation**: Create namespace access in parser

---

#### 4b. Console Namespace (Phase 9.4 Extended)
**Priority**: HIGH

```basic
Console.WriteLine("Hello")
Console.Write("Prompt: ")
Dim input = Console.ReadLine()
Dim key = Console.ReadKey()
```

**Implementation**: Parse `Console.Method()` as namespace call

---

#### 4c. String Methods (Phase 9.6)
**Priority**: HIGH  
**Complexity**: HIGH

```basic
Dim text As String = "Hello World"
Dim len = text.Length()
Dim upper = text.ToUpper()
Dim lower = text.ToLower()
Dim sub = text.Substring(0, 5)
Dim idx = text.IndexOf("World")
Dim replaced = text.Replace("World", "JVM")
```

**Implementation**:
- Parser: Detect identifier followed by dot and method call
- Semantic: Resolve method based on variable type
- Codegen: Call appropriate runtime method
- Runtime: Wrapper methods for String operations

---

#### 4d. File Namespace (Phase 9.19)
**Priority**: MEDIUM

```basic
Dim content = File.ReadAllText("data.txt")
File.WriteAllText("output.txt", content)
Dim exists = File.Exists("file.txt")
Dim lines = File.ReadAllLines("data.txt")
```

---

#### 4e. Http Namespace (Phase 9.8)
**Priority**: HIGH

```basic
Dim response = Http.Get("https://api.example.com/data")
Dim json = Json.Parse(response)
Dim status = Http.GetStatusCode()
```

---

#### 4f. Json Namespace (Phase 9.7)
**Priority**: HIGH

```basic
Dim json = Json.Parse(jsonString)
Dim name = Json.GetString(json, "name")
Dim age = Json.GetInt(json, "age")

Dim obj = Json.NewObject()
Json.Put(obj, "name", "John")
Dim output = Json.ToString(obj)
```

---

#### 4g. Xml Namespace (Phase 9.9)
**Priority**: MEDIUM

```basic
Dim xml = Xml.Parse(xmlString)
Dim value = Xml.Get(xml, "/root/item")
Dim nodes = Xml.GetAll(xml, "//item")
```

---

#### 4h. Db Namespace (Phase 9.10)
**Priority**: MEDIUM

```basic
Dim conn = Db.Connect("jdbc:postgresql://localhost/mydb", "user", "pass")
Dim result = Db.Query(conn, "SELECT * FROM users")
While Db.Next(result)
    Dim name = Db.GetString(result, "name")
    Console.WriteLine(name)
Wend
Db.Close(conn)
```

---

## 🎮 Implementation Strategy

### Session Workflow

1. **Bitwise Operators** (30 min)
   - Add to expression parser
   - Generate bytecode
   - Test

2. **Decimal Type** (60 min)
   - Add type to system
   - Implement arithmetic
   - Test

3. **BigInt Type** (60 min)
   - Similar to Decimal
   - Test

4. **Namespace/Method Parsing** (90 min)
   - Add namespace detection to parser
   - Handle `Namespace.Method()` syntax
   - Handle `variable.Method()` syntax

5. **Implement Namespaces** (120 min)
   - Math namespace
   - Console namespace (enhance existing)
   - String methods
   - File namespace
   - Http namespace
   - Json namespace
   - Xml namespace
   - Db namespace

6. **Testing** (60 min)
   - Create comprehensive tests
   - Run full test suite
   - Fix any issues

**Total Estimated Time**: ~7 hours of focused work

---

## 📊 Expected Outcome

By end of session:
- **203 → ~280 functions** (+77 new functions)
- **Bitwise operators**: Full support
- **Decimal/BigInt**: Complete type system
- **8 namespaces**: Math, Console, String methods, File, Http, Json, Xml, Db
- **OO syntax**: Working for all major operations
- **All tests**: Passing

---

## 🚀 Let's Begin!

Starting with bitwise operators, then Decimal/BigInt, then namespaces...

