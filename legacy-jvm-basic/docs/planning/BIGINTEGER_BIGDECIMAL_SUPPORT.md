# BigInteger and BigDecimal Support for JVM BASIC

## Current State

The types `BigInt` and `Decimal` are partially implemented:
- ✅ Type enum entries in ast.h
- ✅ Token types in lexer.h
- ✅ Parser recognition of `Dim x As BigInt/Decimal`
- ✅ Basic variable declaration
- ❌ Arithmetic operations (add, subtract, multiply, divide)
- ❌ Comparison operations
- ❌ Conversion functions
- ❌ Math namespace functions

## Implementation Plan

### Phase 1: BasicRuntime Functions

Add these functions to `BasicRuntime.java`:

```java
// ===== BigInteger Support =====

// Creation
public static java.math.BigInteger bigint_FromString(String s);
public static java.math.BigInteger bigint_FromInt(int n);
public static java.math.BigInteger bigint_FromLong(long n);

// Arithmetic
public static java.math.BigInteger bigint_Add(java.math.BigInteger a, java.math.BigInteger b);
public static java.math.BigInteger bigint_Subtract(java.math.BigInteger a, java.math.BigInteger b);
public static java.math.BigInteger bigint_Multiply(java.math.BigInteger a, java.math.BigInteger b);
public static java.math.BigInteger bigint_Divide(java.math.BigInteger a, java.math.BigInteger b);
public static java.math.BigInteger bigint_Mod(java.math.BigInteger a, java.math.BigInteger b);
public static java.math.BigInteger bigint_Pow(java.math.BigInteger base, int exp);
public static java.math.BigInteger bigint_ModPow(java.math.BigInteger base, java.math.BigInteger exp, java.math.BigInteger mod);

// Comparison
public static int bigint_CompareTo(java.math.BigInteger a, java.math.BigInteger b);
public static boolean bigint_Equals(java.math.BigInteger a, java.math.BigInteger b);

// Unary operations
public static java.math.BigInteger bigint_Abs(java.math.BigInteger a);
public static java.math.BigInteger bigint_Negate(java.math.BigInteger a);
public static int bigint_Signum(java.math.BigInteger a);

// Conversion
public static String bigint_ToString(java.math.BigInteger a);
public static int bigint_ToInt(java.math.BigInteger a);
public static long bigint_ToLong(java.math.BigInteger a);

// Utility
public static java.math.BigInteger bigint_Gcd(java.math.BigInteger a, java.math.BigInteger b);
public static java.math.BigInteger bigint_Max(java.math.BigInteger a, java.math.BigInteger b);
public static java.math.BigInteger bigint_Min(java.math.BigInteger a, java.math.BigInteger b);
public static int bigint_BitLength(java.math.BigInteger a);
public static boolean bigint_IsProbablePrime(java.math.BigInteger a, int certainty);

// Constants
public static java.math.BigInteger bigint_Zero();
public static java.math.BigInteger bigint_One();
public static java.math.BigInteger bigint_Ten();


// ===== BigDecimal Support =====

// Creation
public static java.math.BigDecimal decimal_FromString(String s);
public static java.math.BigDecimal decimal_FromDouble(double d);
public static java.math.BigDecimal decimal_FromBigInt(java.math.BigInteger n);

// Arithmetic
public static java.math.BigDecimal decimal_Add(java.math.BigDecimal a, java.math.BigDecimal b);
public static java.math.BigDecimal decimal_Subtract(java.math.BigDecimal a, java.math.BigDecimal b);
public static java.math.BigDecimal decimal_Multiply(java.math.BigDecimal a, java.math.BigDecimal b);
public static java.math.BigDecimal decimal_Divide(java.math.BigDecimal a, java.math.BigDecimal b, int scale, int roundingMode);
public static java.math.BigDecimal decimal_Remainder(java.math.BigDecimal a, java.math.BigDecimal b);
public static java.math.BigDecimal decimal_Pow(java.math.BigDecimal base, int exp);

// Comparison
public static int decimal_CompareTo(java.math.BigDecimal a, java.math.BigDecimal b);
public static boolean decimal_Equals(java.math.BigDecimal a, java.math.BigDecimal b);

// Unary operations
public static java.math.BigDecimal decimal_Abs(java.math.BigDecimal a);
public static java.math.BigDecimal decimal_Negate(java.math.BigDecimal a);
public static int decimal_Signum(java.math.BigDecimal a);

// Scale and precision
public static int decimal_Scale(java.math.BigDecimal a);
public static int decimal_Precision(java.math.BigDecimal a);
public static java.math.BigDecimal decimal_SetScale(java.math.BigDecimal a, int scale, int roundingMode);

// Rounding
public static java.math.BigDecimal decimal_Round(java.math.BigDecimal a, int precision, int roundingMode);

// Conversion
public static String decimal_ToString(java.math.BigDecimal a);
public static double decimal_ToDouble(java.math.BigDecimal a);
public static java.math.BigInteger decimal_ToBigInt(java.math.BigDecimal a);

// Utility
public static java.math.BigDecimal decimal_Max(java.math.BigDecimal a, java.math.BigDecimal b);
public static java.math.BigDecimal decimal_Min(java.math.BigDecimal a, java.math.BigDecimal b);

// Constants
public static java.math.BigDecimal decimal_Zero();
public static java.math.BigDecimal decimal_One();
public static java.math.BigDecimal decimal_Ten();
```

### Phase 2: Math Namespace Extensions

Add to `Math` namespace in BASIC:

```basic
' BigInteger functions
Math.BigInt.Add(a, b)
Math.BigInt.Subtract(a, b)
Math.BigInt.Multiply(a, b)
Math.BigInt.Divide(a, b)
Math.BigInt.Mod(a, b)
Math.BigInt.Pow(base, exp)
Math.BigInt.Abs(a)
Math.BigInt.Gcd(a, b)
Math.BigInt.IsPrime(a)

' BigDecimal functions
Math.Decimal.Add(a, b)
Math.Decimal.Subtract(a, b)
Math.Decimal.Multiply(a, b)
Math.Decimal.Divide(a, b, scale)
Math.Decimal.Round(a, precision)
Math.Decimal.Abs(a)
Math.Decimal.SetScale(a, scale)
```

### Phase 3: Operator Overloading (Inline)

For expressions like `a + b` where a or b is BigInt/Decimal:

1. Detect in semantic analyzer when BinOp involves BigInt/Decimal
2. In codegen, emit calls to runtime functions instead of JVM arithmetic

Example transformation:
```basic
Dim a As BigInt = BigInt("12345678901234567890")
Dim b As BigInt = BigInt("98765432109876543210")
Dim c As BigInt = a + b  ' Becomes: bigint_Add(a, b)
```

### Phase 4: Literal Support

Support for BigInt/Decimal literals:
```basic
Dim x As BigInt = 12345678901234567890BI    ' BigInt literal suffix
Dim y As Decimal = 123.456789012345678901D  ' Decimal literal suffix
' Or function-style:
Dim x As BigInt = BigInt("12345678901234567890")
Dim y As Decimal = Decimal("123.456789012345678901")
```

## BASIC API Examples

### BigInteger

```basic
' Create BigInteger values
Dim a As BigInt = BigInt("12345678901234567890")
Dim b As BigInt = BigInt("98765432109876543210")

' Arithmetic
Dim sum As BigInt = Math.BigInt.Add(a, b)
Dim diff As BigInt = Math.BigInt.Subtract(b, a)
Dim prod As BigInt = Math.BigInt.Multiply(a, b)
Dim quot As BigInt = Math.BigInt.Divide(b, a)
Dim rem As BigInt = Math.BigInt.Mod(b, a)

' Power and modular arithmetic
Dim power As BigInt = Math.BigInt.Pow(a, 10)
Dim modpow As BigInt = Math.BigInt.ModPow(a, b, BigInt("1000000007"))

' Utility
Dim gcd As BigInt = Math.BigInt.Gcd(a, b)
Dim isPrime As Boolean = Math.BigInt.IsPrime(BigInt("104729"))  ' 10000th prime

' Output
Console.WriteLine("Sum: " + Math.BigInt.ToString(sum))
Console.WriteLine("Is prime: " + Str(isPrime))
```

### BigDecimal

```basic
' Create BigDecimal values (for financial calculations)
Dim price As Decimal = Decimal("19.99")
Dim quantity As Decimal = Decimal("3")
Dim taxRate As Decimal = Decimal("0.0825")

' Arithmetic with precise control
Dim subtotal As Decimal = Math.Decimal.Multiply(price, quantity)
Dim tax As Decimal = Math.Decimal.Multiply(subtotal, taxRate)
Dim total As Decimal = Math.Decimal.Add(subtotal, tax)

' Round to 2 decimal places
total = Math.Decimal.Round(total, 2)

' Output
Console.WriteLine("Subtotal: $" + Math.Decimal.ToString(subtotal))
Console.WriteLine("Tax: $" + Math.Decimal.ToString(tax))
Console.WriteLine("Total: $" + Math.Decimal.ToString(total))
```

## Codegen Updates

### Type Descriptors

```cpp
// For method signatures
"Ljava/math/BigInteger;"  // BigInt type
"Ljava/math/BigDecimal;"  // Decimal type
```

### DIM Statement

```cpp
// Dim x As BigInt
// Load null initially (will be set by assignment)
aconst_null();
astore(idx);

// Dim x As BigInt = BigInt("123")
// Call runtime constructor
ldc("123");
invokestatic("basicrt/BasicRuntime", "bigint_FromString", "(Ljava/lang/String;)Ljava/math/BigInteger;");
astore(idx);
```

### Arithmetic Operations

```cpp
// For BigInt: a + b
// Stack: [BigInteger a, BigInteger b]
invokestatic("basicrt/BasicRuntime", "bigint_Add",
             "(Ljava/math/BigInteger;Ljava/math/BigInteger;)Ljava/math/BigInteger;");
// Stack: [BigInteger result]
```

## Rounding Modes

Expose Java's RoundingMode enum as constants:

```basic
' Rounding mode constants
Const ROUND_UP = 0
Const ROUND_DOWN = 1
Const ROUND_CEILING = 2
Const ROUND_FLOOR = 3
Const ROUND_HALF_UP = 4
Const ROUND_HALF_DOWN = 5
Const ROUND_HALF_EVEN = 6  ' Banker's rounding
Const ROUND_UNNECESSARY = 7
```

## Implementation Order

1. **BasicRuntime.java**: Add all bigint_* and decimal_* functions
2. **builtin_functions.cpp**: Register Math.BigInt.* and Math.Decimal.* namespaced functions
3. **semantic.cpp**: Update type checking for BigInt/Decimal operations
4. **codegen.h**: Update code generation for:
   - BigInt/Decimal variable storage (aload/astore)
   - Constructor calls for literals
   - Method calls for arithmetic
5. **Tests**: Create comprehensive test suite

## References

- [Java BigInteger API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigInteger.html)
- [Java BigDecimal API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigDecimal.html)
- [Baeldung: BigDecimal and BigInteger](https://www.baeldung.com/java-bigdecimal-biginteger)
