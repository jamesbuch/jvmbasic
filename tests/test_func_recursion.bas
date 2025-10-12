FUNCTION factorial(n)
    IF n <= 1.0 THEN
        RETURN 1.0
    ELSE
        RETURN n * factorial(n - 1.0)
    ENDIF
ENDFUNCTION

FUNCTION fib(n)
    IF n <= 1.0 THEN
        RETURN n
    ELSE
        RETURN fib(n - 1.0) + fib(n - 2.0)
    ENDIF
ENDFUNCTION

FUNCTION gcd(a, b)
    IF b == 0.0 THEN
        RETURN a
    ELSE
        RETURN gcd(b, a MOD b)
    ENDIF
ENDFUNCTION

FUNCTION power(base, exponent)
    IF exponent == 0.0 THEN
        RETURN 1.0
    ELSE
        IF exponent == 1.0 THEN
            RETURN base
        ELSE
            RETURN base * power(base, exponent - 1.0)
        ENDIF
    ENDIF
ENDFUNCTION

PRINT "=== Recursion Test Suite ==="
PRINT ""

PRINT "1. Factorial Tests:"
PRINT "   3! =", factorial(3.0)
PRINT "   5! =", factorial(5.0)
PRINT "   7! =", factorial(7.0)
PRINT ""

PRINT "2. Fibonacci Tests:"
PRINT "   fib(5) =", fib(5.0)
PRINT "   fib(8) =", fib(8.0)
PRINT "   fib(10) =", fib(10.0)
PRINT ""

PRINT "3. GCD Tests (Euclidean):"
PRINT "   gcd(48, 18) =", gcd(48.0, 18.0)
PRINT "   gcd(100, 35) =", gcd(100.0, 35.0)
PRINT "   gcd(270, 192) =", gcd(270.0, 192.0)
PRINT ""

PRINT "4. Power Tests:"
PRINT "   2^5 =", power(2.0, 5.0)
PRINT "   3^4 =", power(3.0, 4.0)
PRINT "   5^3 =", power(5.0, 3.0)
PRINT ""

PRINT "=== All Recursion Tests Complete ==="

