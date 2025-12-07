FUNCTION factorial(n As Integer) As Integer
    IF n <= 1 THEN
        RETURN 1
    ELSE
        RETURN n * factorial(n - 1)
    ENDIF
ENDFUNCTION

FUNCTION fib(n As Integer) As Integer
    IF n <= 1 THEN
        RETURN n
    ELSE
        RETURN fib(n - 1) + fib(n - 2)
    ENDIF
ENDFUNCTION

FUNCTION gcd(a As Integer, b As Integer) As Integer
    IF b == 0 THEN
        RETURN a
    ELSE
        RETURN gcd(b, a MOD b)
    ENDIF
ENDFUNCTION

FUNCTION power(base As Integer, exponent As Integer) As Integer
    IF exponent == 0 THEN
        RETURN 1
    ELSE
        IF exponent == 1 THEN
            RETURN base
        ELSE
            RETURN base * power(base, exponent - 1)
        ENDIF
    ENDIF
ENDFUNCTION

Console.WriteLine("=== Recursion Test Suite ===")
Console.WriteLine("")

Console.WriteLine("1. Factorial Tests:")
Console.WriteLine("   3! = " + factorial(3))
Console.WriteLine("   5! = " + factorial(5))
Console.WriteLine("   7! = " + factorial(7))
Console.WriteLine("")

Console.WriteLine("2. Fibonacci Tests:")
Console.WriteLine("   fib(5) = " + fib(5))
Console.WriteLine("   fib(8) = " + fib(8))
Console.WriteLine("   fib(10) = " + fib(10))
Console.WriteLine("")

Console.WriteLine("3. GCD Tests (Euclidean):")
Console.WriteLine("   gcd(48, 18) = " + gcd(48, 18))
Console.WriteLine("   gcd(100, 35) = " + gcd(100, 35))
Console.WriteLine("   gcd(270, 192) = " + gcd(270, 192))
Console.WriteLine("")

Console.WriteLine("4. Power Tests:")
Console.WriteLine("   2^5 = " + power(2, 5))
Console.WriteLine("   3^4 = " + power(3, 4))
Console.WriteLine("   5^3 = " + power(5, 3))
Console.WriteLine("")

Console.WriteLine("=== All Recursion Tests Complete ===")

