' crypto_bigint.bas - Cryptographic calculations with BigInteger
' Demonstrates RSA-like key operations using arbitrary precision integers

Console.WriteLine("=== BigInteger Cryptography Demo ===")
Console.WriteLine("")

' RSA-like prime generation and operations
Console.WriteLine("--- Prime Number Operations ---")

' Small primes for demonstration (real RSA uses 1024+ bit primes)
Dim p As BigInt = BigInt.FromString("104729")  ' 10000th prime
Dim q As BigInt = BigInt.FromString("104723")  ' Another prime

Console.WriteLine("Prime p: " + BigInt.ToString(p))
Console.WriteLine("Prime q: " + BigInt.ToString(q))

' Test primality
Dim isPrimeP As Boolean = BigInt.IsProbablePrime(p, 100)
Dim isPrimeQ As Boolean = BigInt.IsProbablePrime(q, 100)
Console.WriteLine("IsPrime(p): " + isPrimeP)
Console.WriteLine("IsPrime(q): " + isPrimeQ)
Console.WriteLine("")

' Calculate n = p * q (modulus)
Console.WriteLine("--- RSA Modulus Calculation ---")
Dim n As BigInt = BigInt.Multiply(p, q)
Console.WriteLine("n = p * q = " + BigInt.ToString(n))
Console.WriteLine("Bit length of n: " + BigInt.BitLength(n))
Console.WriteLine("")

' Calculate phi(n) = (p-1) * (q-1)
Console.WriteLine("--- Euler's Totient ---")
Dim one As BigInt = BigInt.One()
Dim pMinus1 As BigInt = BigInt.Subtract(p, one)
Dim qMinus1 As BigInt = BigInt.Subtract(q, one)
Dim phi As BigInt = BigInt.Multiply(pMinus1, qMinus1)
Console.WriteLine("phi(n) = (p-1)(q-1) = " + BigInt.ToString(phi))
Console.WriteLine("")

' Common public exponent
Console.WriteLine("--- Public Exponent ---")
Dim pubExp As BigInt = BigInt.FromString("65537")  ' Common RSA public exponent
Console.WriteLine("e = " + BigInt.ToString(pubExp))

' Verify e and phi are coprime (GCD = 1)
Dim gcd As BigInt = BigInt.Gcd(pubExp, phi)
Console.WriteLine("GCD(e, phi) = " + BigInt.ToString(gcd) + " (should be 1)")
Console.WriteLine("")

' Demonstrate modular exponentiation (core of RSA)
Console.WriteLine("--- Modular Exponentiation Demo ---")
Dim message As BigInt = BigInt.FromString("42")
Console.WriteLine("Original message: " + BigInt.ToString(message))

' Encrypt: cipher = message^e mod n
Dim cipher As BigInt = BigInt.ModPow(message, pubExp, n)
Console.WriteLine("Encrypted (m^e mod n): " + BigInt.ToString(cipher))
Console.WriteLine("")

' Power of two calculations
Console.WriteLine("--- Powers of Two ---")
Dim two As BigInt = BigInt.FromString("2")
Dim pow64 As BigInt = BigInt.Pow(two, 64)
Dim pow128 As BigInt = BigInt.Pow(two, 128)
Dim pow256 As BigInt = BigInt.Pow(two, 256)

Console.WriteLine("2^64  = " + BigInt.ToString(pow64))
Console.WriteLine("2^128 = " + BigInt.ToString(pow128))
Console.WriteLine("2^256 = " + BigInt.ToString(pow256))
Console.WriteLine("")

' Very large number operations
Console.WriteLine("--- Large Number Arithmetic ---")
Dim large1 As BigInt = BigInt.FromString("123456789012345678901234567890")
Dim large2 As BigInt = BigInt.FromString("987654321098765432109876543210")

Console.WriteLine("A = " + BigInt.ToString(large1))
Console.WriteLine("B = " + BigInt.ToString(large2))
Console.WriteLine("")

Dim sum As BigInt = BigInt.Add(large1, large2)
Console.WriteLine("A + B = " + BigInt.ToString(sum))

Dim product As BigInt = BigInt.Multiply(large1, large2)
Console.WriteLine("A * B = " + BigInt.ToString(product))

Dim gcdLarge As BigInt = BigInt.Gcd(large1, large2)
Console.WriteLine("GCD(A, B) = " + BigInt.ToString(gcdLarge))
Console.WriteLine("")

' Factorial calculation
Console.WriteLine("--- Factorial Demo ---")
Dim fact As BigInt = BigInt.One()
Dim i As Integer
For i = 1 To 50
    fact = BigInt.Multiply(fact, BigInt.FromInt(i))
Next
Console.WriteLine("50! = " + BigInt.ToString(fact))
Console.WriteLine("Digits in 50!: " + Len(BigInt.ToString(fact)))
Console.WriteLine("")

Console.WriteLine("=== Demo Complete ===")
