# JVM BASIC 2.0 - TODO and Development Roadmap

This document outlines the current state, pending work, and future development plans for JVM BASIC 2.0.

## Current State Summary

### What's Working

| Category | Features | Status |
|----------|----------|--------|
| **Core Types** | Integer, Long, Float, Double, Boolean, String | ✅ Complete |
| **Variables** | Declarations, assignments, block scoping | ✅ Complete |
| **Operators** | Arithmetic, comparison, logical, string concatenation | ✅ Complete |
| **Control Flow** | If/ElseIf/Else, For, For Each, While, Do, Select Case | ✅ Complete |
| **Functions/Subs** | Parameters, return values, array params | ✅ Complete |
| **String Interpolation** | `$"Hello {name}!"` | ✅ Complete |
| **Exit/Continue** | `exit for`, `continue while`, etc. | ✅ Complete |
| **Arrays** | Declaration, access, iteration | ✅ Complete |
| **Basic OOP** | Classes, constructors, fields, methods | ✅ Complete |
| **Console I/O** | WriteLine, Write, ReadLine | ✅ Complete |
| **Math Namespace** | Sqrt, Sin, Cos, Pow, Random, etc. | ✅ Complete |
| **Str Namespace** | ToUpper, Length, Substring, Replace, etc. | ✅ Complete |
| **Regex Namespace** | IsMatch, Replace, Find, Split, Group, Groups | ✅ Complete |
| **File Namespace** | ReadAllText, WriteAllText, Exists, etc. | ✅ Complete |

### Fully Implemented

| Category | Features | Status |
|----------|----------|--------|
| **Http Namespace** | Get, Post, Put, Delete, headers, URL encoding | ✅ Complete |
| **Json Namespace** | Create, Get, Set, Parse, Pretty, arrays | ✅ Complete |
| **Db Namespace** | Connect, Query, Execute, Prepared statements, transactions | ✅ Complete |
| **Date Namespace** | Now, Today, Format, Parse, Add, Compare, TimeZones | ✅ Complete |
| **Crypto Namespace** | Hashing, HMAC, AES, Argon2, BCrypt, signatures, encoding | ✅ Complete |

### Not Yet Implemented

| Category | Features | Priority |
|----------|----------|----------|
| **Exception Handling** | `try/catch/finally`, `throw`, custom exceptions | High |
| **Testing Support** | `assert`, `Assert.Equal`, test runner | High |
| **OOP: Inheritance** | `extends`, `super`, base class calls | High |
| **OOP: Interfaces** | `interface`, `implements` | High |
| **OOP: Method Overriding** | `override`, virtual dispatch | High |
| **Attributes/Annotations** | PHP 8-style `#[Route("/path")]` for decorators | High |
| **OOP: Static Members** | `static` fields and methods | Medium |
| **OOP: Properties** | `get`/`set` accessors | Medium |
| **Async/Await** | Async functions, await expressions | Medium |
| **Concurrency** | Channels, Mutex, WaitGroup, spawn | Medium |
| **Xml Namespace** | Parse, query, create XML | Medium |
| **Jetty Integration** | Web server with attribute-based routing | Medium |
| **Lambda Expressions** | `x => x * 2` | Low |

---

## Phase 1: Complete Namespace Code Generation ✅ COMPLETE

All runtime libraries are now fully wired into the compiler's code generation.

**Completed:**
- Http namespace: GET, POST, PUT, PATCH, DELETE, headers, URL encoding/decoding
- Json namespace: Create, Get, Set (all types), Push, Has, Length, Pretty, IsValid, IsArray, IsObject
- Db namespace: Connect, Query, Execute, Prepare, SetString/Int/Float/Double/Long/Null, ExecuteQuery/Update, Next, GetString/Int/Float/Double/Long/Bool, Transactions

### 1.1 Http Namespace (CompilerVisitor)

**Files to modify:**
- `src/java/com/jvmbasic/visitor/CompilerVisitor.java`

**Methods to add code generation for:**
```java
// Already have runtime in: com/jvmbasic/runtime/BasicHttp.java
Http.Get(url)              -> "(Ljava/lang/String;)Ljava/lang/String;"
Http.Post(url, body)       -> "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
Http.Put(url, body)        -> "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
Http.Patch(url, body)      -> "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
Http.Delete(url)           -> "(Ljava/lang/String;)Ljava/lang/String;"
Http.GetStatus()           -> "()I"
Http.IsSuccess()           -> "()Z"
Http.SetHeader(name, val)  -> "(Ljava/lang/String;Ljava/lang/String;)V"
Http.ClearHeaders()        -> "()V"
Http.SetTimeout(seconds)   -> "(I)V"
Http.GetJson(url)          -> "(Ljava/lang/String;)Ljava/lang/String;"
Http.PostJson(url, json)   -> "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
Http.UrlEncode(value)      -> "(Ljava/lang/String;)Ljava/lang/String;"
Http.UrlDecode(value)      -> "(Ljava/lang/String;)Ljava/lang/String;"
Http.Download(url, path)   -> "(Ljava/lang/String;Ljava/lang/String;)Z"
Http.SetBasicAuth(u, p)    -> "(Ljava/lang/String;Ljava/lang/String;)V"
Http.SetBearerToken(token) -> "(Ljava/lang/String;)V"
```

### 1.2 Json Namespace (CompilerVisitor)

**Methods to add code generation for:**
```java
// Already have runtime in: com/jvmbasic/runtime/BasicJson.java
Json.Create()              -> "()Ljava/lang/String;"
Json.CreateArray()         -> "()Ljava/lang/String;"
Json.Get(json, key)        -> "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
Json.GetInt(json, key)     -> "(Ljava/lang/String;Ljava/lang/String;)I"
Json.GetDouble(json, key)  -> "(Ljava/lang/String;Ljava/lang/String;)D"
Json.GetBool(json, key)    -> "(Ljava/lang/String;Ljava/lang/String;)Z"
Json.Set(json, key, val)   -> "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
Json.SetInt(json, key, v)  -> "(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;"
Json.SetBool(json, key, v) -> "(Ljava/lang/String;Ljava/lang/String;Z)Ljava/lang/String;"
Json.SetJson(json, key, j) -> "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
Json.Has(json, key)        -> "(Ljava/lang/String;Ljava/lang/String;)Z"
Json.Length(json)          -> "(Ljava/lang/String;)I"
Json.Keys(json)            -> "(Ljava/lang/String;)[Ljava/lang/String;"
Json.Push(json, value)     -> "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
Json.Pretty(json)          -> "(Ljava/lang/String;)Ljava/lang/String;"
Json.Minify(json)          -> "(Ljava/lang/String;)Ljava/lang/String;"
Json.IsValid(json)         -> "(Ljava/lang/String;)Z"
Json.IsObject(json)        -> "(Ljava/lang/String;)Z"
Json.IsArray(json)         -> "(Ljava/lang/String;)Z"
```

### 1.3 Db Namespace (CompilerVisitor)

**Methods to add code generation for:**
```java
// Already have runtime in: com/jvmbasic/runtime/BasicDb.java
Db.Connect(url)            -> "(Ljava/lang/String;)Z"
Db.Connect(url, user, pw)  -> "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z"
Db.Close()                 -> "()V"
Db.IsConnected()           -> "()Z"
Db.Query(sql)              -> "(Ljava/lang/String;)[[Ljava/lang/String;"
Db.Execute(sql)            -> "(Ljava/lang/String;)I"
Db.Prepare(sql)            -> "(Ljava/lang/String;)Z"
Db.SetString(index, value) -> "(ILjava/lang/String;)V"
Db.SetInt(index, value)    -> "(II)V"
Db.SetDouble(index, value) -> "(ID)V"
Db.SetLong(index, value)   -> "(IJ)V"
Db.SetNull(index)          -> "(I)V"
Db.ExecuteQuery()          -> "()[[Ljava/lang/String;"
Db.ExecuteUpdate()         -> "()I"
Db.ClearParameters()       -> "()V"
Db.CloseStmt()             -> "()V"
Db.BeginTransaction()      -> "()Z"
Db.Commit()                -> "()Z"
Db.Rollback()              -> "()Z"
```

---

## Phase 2: New Types - BigInteger and Decimal

### 2.1 BigInteger Type

Arbitrary precision integers using `java.math.BigInteger`. All standard operators work naturally.

**Grammar changes (JvmBasicLexer.g4, JvmBasicParser.g4):**
```antlr
BIGINTEGER : B I G I N T E G E R ;

primitiveType
    : INTEGER | LONG | FLOAT | DOUBLE | BOOLEAN | BYTE | CHAR | BIGINTEGER | DECIMAL
    ;
```

**Type representation:**
- JVM type: `Ljava/math/BigInteger;`
- Stored as reference type in local variables

**Operator mapping (codegen):**
Instead of bytecode instructions, generate method calls:

| Operator | Code Generation |
|----------|-----------------|
| `a + b` | `a.add(b)` → INVOKEVIRTUAL BigInteger.add |
| `a - b` | `a.subtract(b)` → INVOKEVIRTUAL BigInteger.subtract |
| `a * b` | `a.multiply(b)` → INVOKEVIRTUAL BigInteger.multiply |
| `a / b` | `a.divide(b)` → INVOKEVIRTUAL BigInteger.divide |
| `a mod b` | `a.mod(b)` → INVOKEVIRTUAL BigInteger.mod |
| `a ^ b` | `a.pow(b.intValue())` → INVOKEVIRTUAL BigInteger.pow |
| `a = b` | `a.equals(b)` → INVOKEVIRTUAL BigInteger.equals |
| `a <> b` | `!a.equals(b)` |
| `a < b` | `a.compareTo(b) < 0` → INVOKEVIRTUAL BigInteger.compareTo |
| `a <= b` | `a.compareTo(b) <= 0` |
| `a > b` | `a.compareTo(b) > 0` |
| `a >= b` | `a.compareTo(b) >= 0` |
| `-a` | `a.negate()` → INVOKEVIRTUAL BigInteger.negate |
| `a and b` | `a.and(b)` → bitwise AND |
| `a or b` | `a.or(b)` → bitwise OR |
| `a xor b` | `a.xor(b)` → bitwise XOR |
| `not a` | `a.not()` → bitwise NOT |

**Literal parsing:**
```basic
var huge as BigInteger = 12345678901234567890123456789
var hex as BigInteger = 0xFFFFFFFFFFFFFFFFFFFFFFFF
```

**Conversion:**
```basic
var big as BigInteger = BigInteger.FromLong(12345L)
var big2 as BigInteger = BigInteger.FromString("999999999999999999999")
var longVal as Long = big.ToLong()
var strVal as String = big.ToString()
```

**Example usage:**
```basic
var factorial as BigInteger = 1
for i = 1 to 100
    factorial = factorial * i
next i
Console.WriteLine($"100! = {factorial}")
```

### 2.2 Decimal Type

Arbitrary precision decimals using `java.math.BigDecimal`. Essential for financial calculations.

**Type representation:**
- JVM type: `Ljava/math/BigDecimal;`
- Stored as reference type in local variables

**Operator mapping (codegen):**

| Operator | Code Generation |
|----------|-----------------|
| `a + b` | `a.add(b)` → INVOKEVIRTUAL BigDecimal.add |
| `a - b` | `a.subtract(b)` → INVOKEVIRTUAL BigDecimal.subtract |
| `a * b` | `a.multiply(b)` → INVOKEVIRTUAL BigDecimal.multiply |
| `a / b` | `a.divide(b, MathContext.DECIMAL128)` |
| `a mod b` | `a.remainder(b)` → INVOKEVIRTUAL BigDecimal.remainder |
| `a = b` | `a.compareTo(b) == 0` (not equals() for Decimal!) |
| `a <> b` | `a.compareTo(b) != 0` |
| `a < b` | `a.compareTo(b) < 0` |
| `a <= b` | `a.compareTo(b) <= 0` |
| `a > b` | `a.compareTo(b) > 0` |
| `a >= b` | `a.compareTo(b) >= 0` |
| `-a` | `a.negate()` → INVOKEVIRTUAL BigDecimal.negate |

**Literal parsing:**
```basic
var price as Decimal = 19.99
var precise as Decimal = 3.14159265358979323846264338327950288
```

**Conversion and formatting:**
```basic
var dec as Decimal = Decimal.FromDouble(123.456)
var dec2 as Decimal = Decimal.FromString("999.999999999999999")
var doubleVal as Double = dec.ToDouble()
var strVal as String = dec.ToString()
var formatted as String = dec.Format(2)  ' "123.46" (2 decimal places)
```

**Rounding modes:**
```basic
dec = dec.Round(2)                    ' Default: HALF_UP
dec = dec.Round(2, "HALF_EVEN")       ' Banker's rounding
dec = dec.Round(2, "CEILING")         ' Round up
dec = dec.Round(2, "FLOOR")           ' Round down
```

**Example usage:**
```basic
' Financial calculation with exact precision
var principal as Decimal = 10000.00
var rate as Decimal = 0.05
var years as Integer = 30

var amount as Decimal = principal
for year = 1 to years
    amount = amount * (1 + rate)
next year

Console.WriteLine($"After {years} years: ${amount.Format(2)}")
```

---

## Phase 3: Date/Time Namespace

**Create:** `src/java/com/jvmbasic/runtime/BasicDate.java`

Uses Java 8+ `java.time` API (LocalDateTime, ZonedDateTime, Instant, etc.)

### 3.1 Current Time

```java
Date.Now()                 -> String   // ISO 8601 datetime (local): "2025-01-15T14:30:00"
Date.NowUtc()              -> String   // ISO 8601 datetime (UTC): "2025-01-15T06:30:00Z"
Date.Today()               -> String   // Current date only: "2025-01-15"
Date.Time()                -> String   // Current time only: "14:30:00"
Date.Timestamp()           -> Long     // Unix timestamp (milliseconds)
Date.TimestampSeconds()    -> Long     // Unix timestamp (seconds)
```

### 3.2 Formatting and Parsing

```java
Date.Format(date, pattern) -> String   // Format with pattern
Date.Parse(str, pattern)   -> String   // Parse date string
Date.ToIso(date)           -> String   // Convert to ISO 8601
Date.FromTimestamp(ts)     -> String   // From Unix timestamp (ms)
```

**Pattern examples:**
- `"yyyy-MM-dd"` → "2025-01-15"
- `"MM/dd/yyyy"` → "01/15/2025"
- `"HH:mm:ss"` → "14:30:00"
- `"yyyy-MM-dd HH:mm:ss"` → "2025-01-15 14:30:00"
- `"EEE, MMM d, yyyy"` → "Wed, Jan 15, 2025"

### 3.3 Components

```java
Date.Year(date)            -> Integer
Date.Month(date)           -> Integer  // 1-12
Date.Day(date)             -> Integer  // 1-31
Date.Hour(date)            -> Integer  // 0-23
Date.Minute(date)          -> Integer  // 0-59
Date.Second(date)          -> Integer  // 0-59
Date.Millisecond(date)     -> Integer  // 0-999
Date.DayOfWeek(date)       -> Integer  // 1=Monday...7=Sunday
Date.DayOfYear(date)       -> Integer  // 1-366
Date.WeekOfYear(date)      -> Integer  // 1-53
Date.Quarter(date)         -> Integer  // 1-4
Date.IsLeapYear(year)      -> Boolean
Date.DaysInMonth(year, month) -> Integer
```

### 3.4 Arithmetic

```java
Date.AddDays(date, n)      -> String
Date.AddMonths(date, n)    -> String
Date.AddYears(date, n)     -> String
Date.AddHours(date, n)     -> String
Date.AddMinutes(date, n)   -> String
Date.AddSeconds(date, n)   -> String
Date.AddWeeks(date, n)     -> String

Date.DaysBetween(d1, d2)   -> Long
Date.MonthsBetween(d1, d2) -> Long
Date.YearsBetween(d1, d2)  -> Long
Date.SecondsBetween(d1, d2) -> Long
```

### 3.5 Comparison

```java
Date.IsBefore(d1, d2)      -> Boolean
Date.IsAfter(d1, d2)       -> Boolean
Date.IsEqual(d1, d2)       -> Boolean
Date.IsToday(date)         -> Boolean
Date.IsPast(date)          -> Boolean
Date.IsFuture(date)        -> Boolean
Date.IsWeekend(date)       -> Boolean
Date.IsWeekday(date)       -> Boolean
```

### 3.6 Time Zones

```java
Date.ToUtc(date)           -> String   // Convert to UTC
Date.ToLocal(date)         -> String   // Convert to local
Date.ToZone(date, zone)    -> String   // Convert to specific zone
Date.GetTimeZone()         -> String   // Get current zone ID
Date.SetTimeZone(zone)     -> void     // Set default zone for session
```

---

## Phase 4: Comprehensive Crypto Namespace

**Create:** `src/java/com/jvmbasic/runtime/BasicCrypto.java`

Uses Java's built-in crypto and Bouncy Castle (`lib/bcprov-jdk18on-1.77.jar`) for advanced algorithms.

### 4.1 Hash Functions (Message Digests)

**Legacy (for compatibility):**
```java
Crypto.Md5(data)           -> String   // MD5 hash as hex (128-bit) - NOT secure!
Crypto.Sha1(data)          -> String   // SHA-1 hash as hex (160-bit) - NOT secure!
```

**SHA-2 Family (secure):**
```java
Crypto.Sha224(data)        -> String   // SHA-224 hash as hex
Crypto.Sha256(data)        -> String   // SHA-256 hash as hex (256-bit)
Crypto.Sha384(data)        -> String   // SHA-384 hash as hex
Crypto.Sha512(data)        -> String   // SHA-512 hash as hex (512-bit)
Crypto.Sha512_224(data)    -> String   // SHA-512/224
Crypto.Sha512_256(data)    -> String   // SHA-512/256
```

**SHA-3 Family (latest standard):**
```java
Crypto.Sha3_224(data)      -> String   // SHA3-224 hash as hex
Crypto.Sha3_256(data)      -> String   // SHA3-256 hash as hex
Crypto.Sha3_384(data)      -> String   // SHA3-384 hash as hex
Crypto.Sha3_512(data)      -> String   // SHA3-512 hash as hex
Crypto.Shake128(data, len) -> String   // SHAKE128 with variable length
Crypto.Shake256(data, len) -> String   // SHAKE256 with variable length
```

**Other Hash Functions:**
```java
Crypto.Blake2b(data)       -> String   // BLAKE2b-256 (fast)
Crypto.Blake2s(data)       -> String   // BLAKE2s-256 (optimized for 32-bit)
Crypto.Ripemd160(data)     -> String   // RIPEMD-160 (Bitcoin uses this)
```

**File Hashing:**
```java
Crypto.HashFile(path, algorithm) -> String  // Hash a file
// algorithm: "MD5", "SHA-1", "SHA-256", "SHA-512", "SHA3-256", etc.
```

### 4.2 HMAC (Keyed-Hash Message Authentication)

```java
Crypto.HmacMd5(data, key)      -> String   // HMAC-MD5
Crypto.HmacSha1(data, key)     -> String   // HMAC-SHA1
Crypto.HmacSha256(data, key)   -> String   // HMAC-SHA256
Crypto.HmacSha384(data, key)   -> String   // HMAC-SHA384
Crypto.HmacSha512(data, key)   -> String   // HMAC-SHA512
Crypto.HmacSha3_256(data, key) -> String   // HMAC-SHA3-256
```

### 4.3 Symmetric Encryption (AES)

```java
// Key generation
Crypto.GenerateAesKey()        -> String   // Generate 256-bit AES key (hex)
Crypto.GenerateAesKey(bits)    -> String   // Generate key (128, 192, or 256 bits)

// AES-GCM (authenticated encryption - recommended)
Crypto.AesGcmEncrypt(data, key)       -> String   // Encrypt with random IV
Crypto.AesGcmDecrypt(ciphertext, key) -> String   // Decrypt
Crypto.AesGcmEncrypt(data, key, aad)  -> String   // With additional auth data

// AES-CBC (legacy compatibility)
Crypto.AesCbcEncrypt(data, key, iv)   -> String   // Encrypt
Crypto.AesCbcDecrypt(ciphertext, key, iv) -> String // Decrypt

// AES-CTR (stream cipher mode)
Crypto.AesCtrEncrypt(data, key, nonce) -> String
Crypto.AesCtrDecrypt(ciphertext, key, nonce) -> String
```

### 4.4 Password-Based Key Derivation

```java
// PBKDF2 (Password-Based Key Derivation Function 2)
Crypto.Pbkdf2(password, salt, iterations, keyLength) -> String
Crypto.Pbkdf2Sha256(password, salt, iterations, keyLength) -> String

// Argon2 (modern, memory-hard - recommended for passwords)
Crypto.Argon2Hash(password)    -> String   // Default settings
Crypto.Argon2Verify(password, hash) -> Boolean

// Bcrypt (widely used)
Crypto.BcryptHash(password)    -> String   // Default work factor
Crypto.BcryptHash(password, rounds) -> String
Crypto.BcryptVerify(password, hash) -> Boolean

// Scrypt
Crypto.ScryptHash(password, salt, n, r, p, keyLength) -> String
```

### 4.5 Digital Signatures

```java
// RSA Signatures
Crypto.GenerateRsaKeyPair()    -> String[]  // [privateKey, publicKey]
Crypto.GenerateRsaKeyPair(bits) -> String[] // Key size (2048, 4096)
Crypto.RsaSign(data, privateKey) -> String  // Sign with SHA-256
Crypto.RsaVerify(data, signature, publicKey) -> Boolean

// ECDSA Signatures (Elliptic Curve)
Crypto.GenerateEcKeyPair()     -> String[]  // Default: P-256
Crypto.GenerateEcKeyPair(curve) -> String[] // "P-256", "P-384", "P-521"
Crypto.EcdsaSign(data, privateKey) -> String
Crypto.EcdsaVerify(data, signature, publicKey) -> Boolean

// Ed25519 (modern, fast)
Crypto.GenerateEd25519KeyPair() -> String[] // [privateKey, publicKey]
Crypto.Ed25519Sign(data, privateKey) -> String
Crypto.Ed25519Verify(data, signature, publicKey) -> Boolean
```

### 4.6 Encoding/Decoding

```java
// Base64
Crypto.Base64Encode(data)      -> String   // Standard Base64
Crypto.Base64Decode(encoded)   -> String   // Decode to string
Crypto.Base64UrlEncode(data)   -> String   // URL-safe Base64
Crypto.Base64UrlDecode(encoded) -> String

// Hexadecimal
Crypto.HexEncode(data)         -> String   // Bytes to hex string
Crypto.HexDecode(hex)          -> String   // Hex string to bytes (as string)

// Base32
Crypto.Base32Encode(data)      -> String
Crypto.Base32Decode(encoded)   -> String
```

### 4.7 Random Number Generation

```java
Crypto.RandomBytes(length)     -> String   // Cryptographically secure random bytes (hex)
Crypto.RandomBase64(length)    -> String   // Random bytes as Base64
Crypto.RandomInt(min, max)     -> Integer  // Secure random integer
Crypto.Uuid()                  -> String   // UUID v4
Crypto.UuidV7()                -> String   // UUID v7 (time-ordered)
```

### 4.8 Utility Functions

```java
Crypto.ConstantTimeEquals(a, b) -> Boolean  // Timing-safe comparison
Crypto.SecureWipe(data)         -> void     // Securely clear sensitive data
Crypto.GetAlgorithms()          -> String[] // List available algorithms
```

---

## Phase 5: XML Namespace

**Create:** `src/java/com/jvmbasic/runtime/BasicXml.java`

### 5.1 Parsing

```java
Xml.Parse(xmlString)       -> String   // Validate and normalize
Xml.IsValid(xmlString)     -> Boolean
Xml.LoadFile(path)         -> String   // Load from file
Xml.SaveFile(xml, path)    -> Boolean  // Save to file
```

### 5.2 XPath Queries

```java
Xml.Get(xml, xpath)        -> String   // Get single value
Xml.GetAll(xml, xpath)     -> String[] // Get all matching values
Xml.GetAttribute(xml, xpath, attr) -> String
Xml.GetAttributes(xml, xpath) -> String[] // All attributes as key=value pairs
Xml.Count(xml, xpath)      -> Integer  // Count matching nodes
Xml.Exists(xml, xpath)     -> Boolean  // Check if path exists
```

### 5.3 Creation and Modification

```java
Xml.Create(rootElement)    -> String   // Create document
Xml.AddElement(xml, parent, name) -> String
Xml.AddElement(xml, parent, name, text) -> String
Xml.SetAttribute(xml, xpath, attr, value) -> String
Xml.SetText(xml, xpath, text) -> String
Xml.Remove(xml, xpath)     -> String   // Remove matching nodes
Xml.Clone(xml, xpath)      -> String   // Clone a subtree
```

### 5.4 Conversion and Formatting

```java
Xml.ToJson(xml)            -> String   // Convert to JSON
Xml.FromJson(json)         -> String   // Convert JSON to XML
Xml.Pretty(xml)            -> String   // Format with indentation
Xml.Minify(xml)            -> String   // Remove whitespace
Xml.Escape(text)           -> String   // Escape special characters
Xml.Unescape(text)         -> String   // Unescape
```

---

## Phase 6: Complete OOP Implementation

This phase completes the object-oriented programming support for JVM BASIC 2.0.

### 6.1 Inheritance (High Priority)

**Status:** Grammar exists, needs compiler implementation

**Grammar (already in JvmBasicParser.g4):**

```antlr
classDeclaration
    : accessModifier? CLASS IDENTIFIER (EXTENDS typeName)? classBody END CLASS
    ;
```

**Compiler changes needed:**

1. **SymbolCollector changes:**
   - Track base class name for each class
   - Validate base class exists before subclass
   - Build inheritance hierarchy for type checking

2. **CompilerVisitor changes:**
   - Generate `extends` clause in class bytecode: `cw.visit(..., superClassName, ...)`
   - Handle `super.` prefix for parent method calls
   - Generate `INVOKESPECIAL` for super constructor calls
   - Handle field inheritance (parent fields accessible in child)

3. **SemanticAnalyzer changes:**
   - Type compatibility checks (subclass assignable to parent type)
   - Method resolution with inheritance chain
   - Field visibility in inheritance hierarchy

**Example syntax (using `super.` not `MyBase.`):**

```basic
class Person
    public var name as String
    public var age as Integer

    public sub New(name as String, age as Integer)
        this.name = name
        this.age = age
    end sub

    public function ToString() as String
        return $"{this.name}, age {this.age}"
    end function
end class

class Employee extends Person
    private var salary as Double
    private var department as String

    public sub New(name as String, age as Integer, salary as Double, dept as String)
        super.New(name, age)  ' Call parent constructor
        this.salary = salary
        this.department = dept
    end sub

    public override function ToString() as String
        return super.ToString() + $", {this.department}, ${this.salary}"
    end function

    public function GetSalary() as Double
        return this.salary
    end function
end class

' Usage
var emp as Employee = new Employee("Alice", 30, 75000.0, "Engineering")
Console.WriteLine(emp.ToString())
Console.WriteLine(emp.name)  ' Inherited field access
```

### 6.2 Interfaces (High Priority)

**Status:** Grammar exists, needs compiler implementation

**Grammar changes:**

```antlr
interfaceDeclaration
    : accessModifier? INTERFACE IDENTIFIER interfaceBody END INTERFACE
    ;

interfaceBody
    : (functionSignature | subSignature)*
    ;

classDeclaration
    : accessModifier? CLASS IDENTIFIER
      (EXTENDS typeName)?
      (IMPLEMENTS typeNameList)?
      classBody END CLASS
    ;
```

**Compiler changes needed:**

1. Generate interface as JVM interface (ACC_INTERFACE | ACC_ABSTRACT)
2. Generate abstract method signatures in interface
3. Add `implements` clause to class bytecode
4. Validate all interface methods are implemented
5. Support multiple interface implementation

**Example syntax:**

```basic
interface IShape
    function Area() as Double
    function Perimeter() as Double
    function GetName() as String
end interface

interface IDrawable
    sub Draw()
end interface

class Circle implements IShape, IDrawable
    private var radius as Double
    private var x as Integer
    private var y as Integer

    public sub New(radius as Double, x as Integer, y as Integer)
        this.radius = radius
        this.x = x
        this.y = y
    end sub

    public function Area() as Double
        return 3.14159 * this.radius * this.radius
    end function

    public function Perimeter() as Double
        return 2.0 * 3.14159 * this.radius
    end function

    public function GetName() as String
        return "Circle"
    end function

    public sub Draw()
        Console.WriteLine($"Drawing circle at ({this.x}, {this.y}) with radius {this.radius}")
    end sub
end class

' Polymorphic usage
var shape as IShape = new Circle(5.0, 10, 20)
Console.WriteLine($"Area: {shape.Area()}")
```

### 6.3 Method Overriding (High Priority)

**Status:** Grammar has `override` keyword, needs compiler implementation

**Compiler changes needed:**

1. Validate `override` methods exist in parent class/interface
2. Check method signature matches parent exactly
3. Generate proper virtual dispatch (methods are virtual by default in JVM)
4. Support `final` to prevent further overriding

**Example syntax:**

```basic
class Animal
    public function Speak() as String
        return "..."
    end function
end class

class Dog extends Animal
    public override function Speak() as String
        return "Woof!"
    end function
end class

class Cat extends Animal
    public override function Speak() as String
        return "Meow!"
    end function
end class

' Polymorphism in action
var animals() as Animal = new Animal[3]
animals[0] = new Dog()
animals[1] = new Cat()
animals[2] = new Animal()

for each animal in animals
    Console.WriteLine(animal.Speak())
next
```

### 6.4 Static Members (Medium Priority)

**Status:** Grammar has `static` keyword, needs compiler implementation

**Compiler changes needed:**

1. Generate static fields with ACC_STATIC flag
2. Generate static methods with ACC_STATIC flag
3. Use GETSTATIC/PUTSTATIC for static field access
4. Use INVOKESTATIC for static method calls
5. Static initializer block support (<clinit>)

**Example syntax:**

```basic
class Counter
    private static var count as Integer = 0

    public static function GetCount() as Integer
        return Counter.count
    end function

    public static sub Increment()
        Counter.count = Counter.count + 1
    end sub

    public static sub Reset()
        Counter.count = 0
    end sub
end class

' Usage - no instance needed
Counter.Increment()
Counter.Increment()
Console.WriteLine($"Count: {Counter.GetCount()}")  ' Output: Count: 2
```

### 6.5 Properties (Medium Priority)

**Status:** Grammar may need updates, needs compiler implementation

**Compiler changes needed:**

1. Generate getter/setter methods (get_PropertyName, set_PropertyName)
2. Property access translates to method calls
3. Support read-only properties (get only)
4. Support write-only properties (set only)
5. Support auto-implemented properties

**Example syntax:**

```basic
class Person
    private var _name as String
    private var _age as Integer

    ' Full property with backing field
    public property Name as String
        get
            return this._name
        end get
        set(value as String)
            if Str.Length(value) > 0 then
                this._name = value
            end if
        end set
    end property

    ' Read-only property
    public property Age as Integer
        get
            return this._age
        end get
    end property

    ' Auto-implemented property (future)
    ' public property Email as String

    public sub New(name as String, age as Integer)
        this._name = name
        this._age = age
    end sub
end class

var p as Person = new Person("Alice", 25)
Console.WriteLine(p.Name)  ' Calls getter
p.Name = "Bob"             ' Calls setter
```

### 6.6 Abstract Classes (Low Priority)

**Example syntax:**

```basic
abstract class Shape
    public abstract function Area() as Double
    public abstract function Perimeter() as Double

    public function Describe() as String
        return $"Shape with area {this.Area()}"
    end function
end class

class Rectangle extends Shape
    private var width as Double
    private var height as Double

    public sub New(w as Double, h as Double)
        this.width = w
        this.height = h
    end sub

    public override function Area() as Double
        return this.width * this.height
    end function

    public override function Perimeter() as Double
        return 2.0 * (this.width + this.height)
    end function
end class
```

### OOP Implementation Order

1. **Phase 6.1: Inheritance** - Foundation for all other OOP features
2. **Phase 6.3: Method Overriding** - Depends on inheritance
3. **Phase 6.2: Interfaces** - Can be done in parallel with overriding
4. **Phase 6.4: Static Members** - Independent, can be done anytime
5. **Phase 6.5: Properties** - Nice to have, syntactic sugar
6. **Phase 6.6: Abstract Classes** - Combines inheritance + interfaces concepts

---

## Phase 7: Example Programs Updates

### 7.1 Convert to Modern Syntax

Many examples use old-style string concatenation:
```basic
' OLD:
Console.WriteLine("Name: " & name & ", Age: " & age)

' NEW:
Console.WriteLine($"Name: {name}, Age: {age}")
```

**Files to update:**
- `examples/class_test.jvmb` - Uses `&` concatenation
- `examples/methods.jvmb` - Uses `+` concatenation in some places
- `examples/http_test.jvmb` - Uses `&` concatenation
- `examples/json_test.jvmb` - Uses `&` concatenation

### 7.2 New Real-World Examples Needed

| Program | Description | Namespaces Used |
|---------|-------------|-----------------|
| `password_generator.jvmb` | Secure password generator | Crypto, Str |
| `file_backup.jvmb` | Timestamped file backup | File, Date, Str |
| `log_analyzer.jvmb` | Parse and analyze log files | File, Regex, Str |
| `rest_client.jvmb` | REST API client example | Http, Json |
| `weather_api.jvmb` | Fetch weather from API | Http, Json |
| `config_manager.jvmb` | JSON configuration files | File, Json |
| `db_crud.jvmb` | Database CRUD operations | Db |
| `contact_manager.jvmb` | Contact management (OOP) | Classes, File, Json |
| `simple_calculator.jvmb` | Expression calculator (OOP) | Classes, Math |
| `text_statistics.jvmb` | Word/char/line counting | File, Str, Regex |
| `financial_calc.jvmb` | Compound interest with Decimal | Decimal, Math |
| `factorial_big.jvmb` | Large factorial with BigInteger | BigInteger |
| `hash_password.jvmb` | Password hashing demo | Crypto |
| `sign_verify.jvmb` | Digital signature demo | Crypto |

---

## Phase 8: Jetty Web Server Integration

Once Http, Json, and Db are working, add web server support:

```basic
' Simple web server example
import Jetty

Jetty.Start(8080)

Jetty.Get("/", handleRoot)
Jetty.Get("/api/users", handleGetUsers)
Jetty.Post("/api/users", handleCreateUser)

sub handleRoot(req as Request, res as Response)
    res.SetContentType("text/html")
    res.Write("<h1>Welcome to JVM BASIC!</h1>")
end sub

sub handleGetUsers(req as Request, res as Response)
    var users as String = Db.Query("SELECT * FROM users")
    res.SetContentType("application/json")
    res.Write(Json.FromArray(users))
end sub
```

---

## Available Libraries (lib/ directory)

The following JARs are available for use:

| Library | Version | Purpose | Status |
|---------|---------|---------|--------|
| `antlr-4.13.2-complete.jar` | 4.13.2 | ANTLR parser generator | ✅ In use |
| `asm-9.9.jar` | 9.9 | Bytecode generation | ✅ In use |
| `bcel-6.11.0.jar` | 6.11.0 | Alternative bytecode lib | Available |
| `bcprov-jdk18on-1.77.jar` | 1.77 | Bouncy Castle crypto | ✅ In use |
| `bcpkix-jdk18on-1.77.jar` | 1.77 | Bouncy Castle PKI | ✅ In use |
| `gson-2.10.1.jar` | 2.10.1 | JSON parsing | Available |
| `postgresql-42.7.1.jar` | 42.7.1 | PostgreSQL JDBC | ✅ In use |
| `mariadb-java-client-3.3.2.jar` | 3.3.2 | MariaDB/MySQL JDBC | ✅ In use |
| `jetty-server-11.0.19.jar` | 11.0.19 | Jetty HTTP server | Pending |
| `jetty-servlet-11.0.19.jar` | 11.0.19 | Jetty servlet support | Pending |
| `jetty-http-11.0.19.jar` | 11.0.19 | Jetty HTTP utilities | Pending |
| `jetty-io-11.0.19.jar` | 11.0.19 | Jetty I/O | Pending |
| `jetty-security-11.0.19.jar` | 11.0.19 | Jetty security | Pending |
| `jetty-util-11.0.19.jar` | 11.0.19 | Jetty utilities | Pending |
| `jakarta.servlet-api-5.0.0.jar` | 5.0.0 | Servlet API | Pending |
| `slf4j-api-2.0.9.jar` | 2.0.9 | Logging facade | Pending |
| `slf4j-simple-2.0.9.jar` | 2.0.9 | Simple logging | Pending |
| `guava-33.0.0-jre.jar` | 33.0.0 | Google Guava utilities | Available |
| `commons-io-2.15.1.jar` | 2.15.1 | Apache Commons I/O | Available |
| `commons-lang3-3.14.0.jar` | 3.14.0 | Apache Commons Lang | Available |
| `commons-codec-1.16.0.jar` | 1.16.0 | Apache Commons Codec | Available |
| `commons-text-1.11.0.jar` | 1.11.0 | Apache Commons Text | Available |
| `commons-math3-3.6.1.jar` | 3.6.1 | Apache Commons Math | Available |

### Libraries to Add

| Library | Version | Purpose | Priority |
|---------|---------|---------|----------|
| `picocli-4.7.6.jar` | 4.7.6 | CLI argument parsing | High |
| `jackson-core` | 2.17.x | JSON (faster than hand-rolled) | Medium |
| `logback-classic` | 1.5.x | Production logging | Low |

---

## Testing Checklist

Before each commit:
```bash
./test-examples.sh
```

Current test coverage (23 tests):
- Core features: hello, demo, arrays, functions, control flow
- Types: float_long_test, double_test
- Loops: simple_for, simple_while, do_loop_test, foreach_test
- Conditionals: simple_if, select_case_test, exit_continue_test
- Strings: str_test, interpolation_test, string_plus_test, regex_test
- OOP: class_test (basic), oop_shapes, oop_linked_list
- Scoping: scope_test, for_in_function_test, array_param_test
- I/O: file_test, comparison_test
- Algorithms: algo_fibonacci, calculator

**Tests to add:**
- `http_test.jvmb` - needs working Http namespace codegen
- `json_test.jvmb` - needs working Json namespace codegen
- `db_test.jvmb` - needs working Db namespace codegen
- `biginteger_test.jvmb` - when BigInteger is implemented
- `decimal_test.jvmb` - when Decimal is implemented
- `date_test.jvmb` - ✅ Date namespace implemented and tested
- `crypto_test.jvmb` - ✅ Crypto namespace implemented and tested
- `inheritance_test.jvmb` - when inheritance is implemented
- `interface_test.jvmb` - when interfaces are implemented

---

## Revised Priority Order

### Completed

1. ✅ **Complete namespace code generation** (Http, Json, Db) - DONE
2. ✅ **Add Date namespace** - DONE (60+ methods)
3. ✅ **Add comprehensive Crypto namespace** - DONE (BouncyCastle integration)
4. ✅ **Add BigInteger and Decimal types** - DONE (with full operator support)

### Next Up

5. **Exception Handling** - `try/catch/finally`, `throw`
6. **Testing Support** - `assert`, Assert namespace
7. **Complete OOP: Inheritance** - `extends`, `super.` calls (grammar exists)
8. **Complete OOP: Method Overriding** - `override` keyword, virtual dispatch
9. **Complete OOP: Interfaces** - `interface`, `implements` (grammar exists)
10. **Complete OOP: Static Members** - `static` fields and methods
11. **Attributes/Annotations** - PHP 8-style decorators for Jetty integration
12. **Add real-world example programs** - Showcase language capabilities
13. **Add Xml namespace** (data interchange)
14. **Jetty web server integration** - With attribute-based routing
15. **Add CLI namespace** (picocli-based argument parsing)
16. **Complete OOP: Properties** - `get`/`set` accessors
17. **Complete OOP: Abstract Classes** - `abstract` keyword

---

## Phase 9: Exception Handling

### 9.1 Try/Catch/Finally

**Grammar additions:**

```antlr
tryStatement
    : TRY statementBlock
      (CATCH (IDENTIFIER (AS typeName)?)? statementBlock)*
      (FINALLY statementBlock)?
      END TRY
    ;

throwStatement
    : THROW expression
    ;
```

**Example syntax:**

```basic
try
    var result as String = File.ReadAllText("config.json")
    var config as String = Json.Parse(result)
catch ex as IOException
    Console.WriteLine($"File error: {ex.Message}")
catch ex as JsonException
    Console.WriteLine($"Parse error: {ex.Message}")
catch ex
    Console.WriteLine($"Unexpected error: {ex.Message}")
finally
    Console.WriteLine("Cleanup complete")
end try
```

**Compiler implementation:**

1. Generate JVM exception table entries
2. Use `ATHROW` for throw statements
3. Handle exception type hierarchy
4. Generate proper finally block (JSR/RET or duplication)

### 9.2 Custom Exceptions

```basic
class ValidationException extends Exception
    private var fieldName as String

    public sub New(field as String, message as String)
        super.New(message)
        this.fieldName = field
    end sub

    public function GetField() as String
        return this.fieldName
    end function
end class

' Usage
throw new ValidationException("email", "Invalid email format")
```

---

## Phase 10: Testing Support

### 10.1 Assert Statement

**Grammar:**

```antlr
assertStatement
    : ASSERT expression (COMMA STRING_LITERAL)?
    ;
```

**Examples:**

```basic
assert x > 0                          ' Throws if false
assert x > 0, "x must be positive"    ' With custom message
assert Str.Length(name) > 0, "Name required"
```

**Compiler implementation:**
- Generate conditional throw of AssertionError
- Include source location in error message
- Can be disabled with compiler flag (`-noassert`)

### 10.2 Assert Namespace

For more expressive test assertions:

```basic
' Equality
Assert.Equal(expected, actual)
Assert.NotEqual(a, b)
Assert.Same(obj1, obj2)              ' Reference equality
Assert.NotSame(obj1, obj2)

' Null checks
Assert.Null(value)
Assert.NotNull(value)

' Boolean
Assert.True(condition)
Assert.False(condition)

' String
Assert.Contains("ello", "Hello")
Assert.StartsWith("He", "Hello")
Assert.EndsWith("lo", "Hello")
Assert.Matches("h.*o", "hello")

' Collections/Arrays
Assert.Empty(arr)
Assert.NotEmpty(arr)
Assert.Count(3, arr)
Assert.Contains(item, arr)

' Numeric
Assert.Greater(10, 5)
Assert.GreaterOrEqual(10, 10)
Assert.Less(5, 10)
Assert.LessOrEqual(5, 5)
Assert.InRange(5, 1, 10)

' Exceptions
Assert.Throws("ValidationException", sub()
    throw new ValidationException("test")
end sub)

' Custom message on any assertion
Assert.Equal(expected, actual, "Values should match")
```

### 10.3 Test Runner Integration

Test file structure:

```basic
' tests/user_test.jvmb

#[TestClass]
class UserTest

    #[SetUp]
    sub Before()
        ' Run before each test
    end sub

    #[TearDown]
    sub After()
        ' Run after each test
    end sub

    #[Test]
    sub TestUserCreation()
        var user as User = new User("Alice", "alice@example.com")
        Assert.Equal("Alice", user.GetName())
        Assert.NotNull(user.GetEmail())
    end sub

    #[Test]
    #[ExpectedException("ValidationException")]
    sub TestInvalidEmail()
        var user as User = new User("Bob", "invalid-email")
    end sub

    #[Test]
    #[Skip("Not implemented yet")]
    sub TestPasswordReset()
        ' This test will be skipped
    end sub
end class
```

---

## Phase 11: Attributes (PHP 8-style Decorators)

### 11.1 Syntax Design

Using `#[...]` syntax similar to PHP 8 attributes:

```antlr
attribute
    : HASH LBRACKET attributeName (LPAREN attributeArgs? RPAREN)? RBRACKET
    ;

attributeName
    : IDENTIFIER (DOT IDENTIFIER)*
    ;

attributeArgs
    : attributeArg (COMMA attributeArg)*
    ;

attributeArg
    : (IDENTIFIER ASSIGN)? expression
    ;
```

### 11.2 Built-in Attributes

**Routing (for Jetty integration):**

```basic
#[Route("/users")]
#[Route("/users/{id}", method = "GET")]
#[Get("/users")]
#[Post("/users")]
#[Put("/users/{id}")]
#[Delete("/users/{id}")]
```

**Validation:**

```basic
#[Required]
#[MinLength(3)]
#[MaxLength(100)]
#[Range(1, 100)]
#[Email]
#[Regex("^[A-Z].*")]
```

**Serialization:**

```basic
#[JsonProperty("user_name")]
#[JsonIgnore]
#[XmlElement("UserName")]
```

**Testing:**

```basic
#[Test]
#[TestClass]
#[SetUp]
#[TearDown]
#[Skip("reason")]
#[ExpectedException("ExceptionType")]
#[Timeout(5000)]
```

**Documentation:**

```basic
#[Deprecated("Use NewMethod instead")]
#[Description("Brief description")]
```

### 11.3 Web Controller Example

```basic
#[Controller]
#[Route("/api")]
class UserController extends AbstractController

    private var userService as UserService

    public sub New(userService as UserService)
        this.userService = userService
    end sub

    #[Get("/users")]
    #[Produces("application/json")]
    public function GetAllUsers() as String
        var users as String[] = this.userService.FindAll()
        return Json.FromArray(users)
    end function

    #[Get("/users/{id}")]
    public function GetUser(#[PathParam] id as Integer) as String
        var user as User = this.userService.FindById(id)
        if user = nil then
            Response.Status(404)
            return Json.Set("{}", "error", "User not found")
        end if
        return user.ToJson()
    end function

    #[Post("/users")]
    #[Consumes("application/json")]
    public function CreateUser(#[Body] userData as String) as String
        var name as String = Json.Get(userData, "name")
        var email as String = Json.Get(userData, "email")

        var user as User = this.userService.Create(name, email)
        Response.Status(201)
        return user.ToJson()
    end function

    #[Put("/users/{id}")]
    public function UpdateUser(#[PathParam] id as Integer, #[Body] userData as String) as String
        var user as User = this.userService.Update(id, userData)
        return user.ToJson()
    end function

    #[Delete("/users/{id}")]
    public function DeleteUser(#[PathParam] id as Integer) as String
        this.userService.Delete(id)
        Response.Status(204)
        return ""
    end function
end class
```

### 11.4 Compiler Implementation

1. **Lexer changes:**
   - Add `HASH` token for `#`
   - Recognize `#[...]` as attribute start

2. **Parser changes:**
   - Parse attributes before class/method/field declarations
   - Store attributes in AST nodes

3. **Symbol collection:**
   - Attach attributes to class/method/field symbols
   - Validate attribute usage (e.g., `#[Test]` only on methods)

4. **Code generation:**
   - Generate Java annotations in bytecode
   - Create runtime-visible annotations for reflection
   - Custom annotation classes for JVM BASIC-specific attributes

5. **Runtime support:**
   - Reflection API to read attributes
   - Attribute processor for web framework integration

---

## Notes

### Regex Groups - Already Working

The Regex namespace already supports capture groups:
- `Regex.Group(input, pattern, groupNum)` - Extract specific group
- `Regex.Groups(input, pattern)` - Extract all groups as array

Both runtime and codegen are complete for these functions.
