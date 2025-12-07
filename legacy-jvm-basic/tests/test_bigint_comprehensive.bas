' Test BigInt comprehensive operations
' Tests all BigInteger runtime functions

Console.WriteLine("=== BigInt Operations Test ===")
Console.WriteLine("")

' Test creation from string
Dim a As BigInt
Dim b As BigInt
a = BigInt.FromString("12345678901234567890")
b = BigInt.FromString("98765432109876543210")

Console.WriteLine("a = 12345678901234567890")
Console.WriteLine("b = 98765432109876543210")
Console.WriteLine("")

' Test arithmetic operations
Console.WriteLine("--- Arithmetic Operations ---")
Dim sum As BigInt
Dim diff As BigInt
Dim prod As BigInt
Dim quot As BigInt
Dim remainder As BigInt

sum = BigInt.Add(a, b)
Console.WriteLine("a + b = " + BigInt.ToString(sum))

diff = BigInt.Subtract(b, a)
Console.WriteLine("b - a = " + BigInt.ToString(diff))

prod = BigInt.Multiply(a, b)
Console.WriteLine("a * b = " + BigInt.ToString(prod))

quot = BigInt.Divide(b, a)
Console.WriteLine("b / a = " + BigInt.ToString(quot))

remainder = BigInt.Remainder(b, a)
Console.WriteLine("b mod a = " + BigInt.ToString(remainder))
Console.WriteLine("")

' Test power
Console.WriteLine("--- Power Operations ---")
Dim base As BigInt
Dim power As BigInt
base = BigInt.FromString("2")
power = BigInt.Pow(base, 100)
Console.WriteLine("2^100 = " + BigInt.ToString(power))
Console.WriteLine("")

' Test comparison
Console.WriteLine("--- Comparison Operations ---")
Dim cmp As Integer
cmp = BigInt.CompareTo(a, b)
Console.WriteLine("CompareTo(a, b) = " + cmp)

cmp = BigInt.CompareTo(b, a)
Console.WriteLine("CompareTo(b, a) = " + cmp)

Dim eq As Boolean
eq = BigInt.Equals(a, a)
Console.WriteLine("Equals(a, a) = " + eq)

eq = BigInt.Equals(a, b)
Console.WriteLine("Equals(a, b) = " + eq)
Console.WriteLine("")

' Test unary operations
Console.WriteLine("--- Unary Operations ---")
Dim neg As BigInt
neg = BigInt.FromString("-12345")
Dim absVal As BigInt
absVal = BigInt.Abs(neg)
Console.WriteLine("Abs(-12345) = " + BigInt.ToString(absVal))

Dim negated As BigInt
negated = BigInt.Negate(a)
Console.WriteLine("Negate(a) = " + BigInt.ToString(negated))

Dim sign As Integer
sign = BigInt.Signum(neg)
Console.WriteLine("Signum(-12345) = " + sign)

sign = BigInt.Signum(a)
Console.WriteLine("Signum(a) = " + sign)
Console.WriteLine("")

' Test utility functions
Console.WriteLine("--- Utility Functions ---")
Dim x As BigInt
Dim y As BigInt
x = BigInt.FromString("48")
y = BigInt.FromString("18")
Dim gcdVal As BigInt
gcdVal = BigInt.Gcd(x, y)
Console.WriteLine("GCD(48, 18) = " + BigInt.ToString(gcdVal))

Dim maxVal As BigInt
maxVal = BigInt.Max(a, b)
Console.WriteLine("Max(a, b) = " + BigInt.ToString(maxVal))

Dim minVal As BigInt
minVal = BigInt.Min(a, b)
Console.WriteLine("Min(a, b) = " + BigInt.ToString(minVal))

Dim bits As Integer
bits = BigInt.BitLength(a)
Console.WriteLine("BitLength(a) = " + bits)
Console.WriteLine("")

' Test primality
Console.WriteLine("--- Primality Test ---")
Dim prime As BigInt
prime = BigInt.FromString("104729")
Dim isPrime As Boolean
isPrime = BigInt.IsProbablePrime(prime, 10)
Console.WriteLine("IsProbablePrime(104729) = " + isPrime)

Dim notPrime As BigInt
notPrime = BigInt.FromString("104730")
isPrime = BigInt.IsProbablePrime(notPrime, 10)
Console.WriteLine("IsProbablePrime(104730) = " + isPrime)
Console.WriteLine("")

' Test constants
Console.WriteLine("--- Constants ---")
Console.WriteLine("Zero = " + BigInt.ToString(BigInt.Zero()))
Console.WriteLine("One = " + BigInt.ToString(BigInt.One()))
Console.WriteLine("Ten = " + BigInt.ToString(BigInt.Ten()))
Console.WriteLine("")

' Test conversion
Console.WriteLine("--- Conversion ---")
Dim small As BigInt
small = BigInt.FromInt(42)
Console.WriteLine("FromInt(42) = " + BigInt.ToString(small))

Dim asInt As Integer
asInt = BigInt.ToInt(small)
Console.WriteLine("ToInt(small) = " + asInt)
Console.WriteLine("")

Console.WriteLine("=== BigInt Test Complete ===")
