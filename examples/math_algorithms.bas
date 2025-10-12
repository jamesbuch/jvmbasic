FUNCTION gcd(a, b)
    WHILE b > 0.0
        LET temp = b
        LET b = a MOD b
        LET a = temp
    ENDWHILE
    RETURN a
ENDFUNCTION

FUNCTION lcm(a, b)
    RETURN (a * b) / gcd(a, b)
ENDFUNCTION

FUNCTION factorial(n)
    IF n <= 1.0 THEN
        RETURN 1.0
    ELSE
        RETURN n * factorial(n - 1.0)
    ENDIF
ENDFUNCTION

FUNCTION fibonacci(n)
    IF n <= 1.0 THEN
        RETURN n
    ELSE
        RETURN fibonacci(n - 1.0) + fibonacci(n - 2.0)
    ENDIF
ENDFUNCTION

FUNCTION fibonacciIterative(n)
    LET a = 0.0
    LET b = 1.0
    LET count = 0.0
    
    WHILE count < n
        LET temp = a + b
        LET a = b
        LET b = temp
        LET count = count + 1.0
    ENDWHILE
    RETURN a
ENDFUNCTION

FUNCTION isPrime(num)
    IF num <= 1.0 THEN
        RETURN 0.0
    ENDIF
    IF num == 2.0 THEN
        RETURN 1.0
    ENDIF
    IF num MOD 2.0 == 0.0 THEN
        RETURN 0.0
    ENDIF
    
    LET divisor = 3.0
    WHILE divisor * divisor <= num
        IF num MOD divisor == 0.0 THEN
            RETURN 0.0
        ENDIF
        LET divisor = divisor + 2.0
    ENDWHILE
    RETURN 1.0
ENDFUNCTION

FUNCTION power(base, exponent)
    LET result = 1.0
    LET count = 0.0
    WHILE count < exponent
        LET result = result * base
        LET count = count + 1.0
    ENDWHILE
    RETURN result
ENDFUNCTION

FUNCTION isPerfectSquare(n)
    LET root = SQR(n)
    LET intRoot = INT(root)
    LET test = intRoot * intRoot
    IF test == n THEN
        RETURN 1.0
    ENDIF
    RETURN 0.0
ENDFUNCTION

PRINT "========================================================"
PRINT "  MATHEMATICAL ALGORITHMS DEMONSTRATION"
PRINT "========================================================"
PRINT ""

PRINT "1. GREATEST COMMON DIVISOR (GCD)"
PRINT "   GCD(48, 18) =", gcd(48.0, 18.0)
PRINT "   GCD(100, 75) =", gcd(100.0, 75.0)
PRINT "   GCD(17, 19) =", gcd(17.0, 19.0)
PRINT ""

PRINT "2. LEAST COMMON MULTIPLE (LCM)"
PRINT "   LCM(12, 18) =", lcm(12.0, 18.0)
PRINT "   LCM(21, 6) =", lcm(21.0, 6.0)
PRINT ""

PRINT "3. FACTORIAL"
PRINT "   5! =", factorial(5.0)
PRINT "   10! =", factorial(10.0)
PRINT "   12! =", factorial(12.0)
PRINT ""

PRINT "4. FIBONACCI (Recursive)"
PRINT "   fib(10) =", fibonacci(10.0)
PRINT "   fib(15) =", fibonacci(15.0)
PRINT "   fib(20) =", fibonacci(20.0)
PRINT ""

PRINT "5. FIBONACCI (Iterative - faster!)"
PRINT "   fib(25) =", fibonacciIterative(25.0)
PRINT "   fib(30) =", fibonacciIterative(30.0)
PRINT "   fib(35) =", fibonacciIterative(35.0)
PRINT ""

PRINT "6. PRIME TESTING"
PRINT "   2 is prime:", isPrime(2.0)
PRINT "   17 is prime:", isPrime(17.0)
PRINT "   100 is prime:", isPrime(100.0)
PRINT "   97 is prime:", isPrime(97.0)
PRINT "   89 is prime:", isPrime(89.0)
PRINT ""

PRINT "7. PRIME NUMBERS UP TO 50:"
LET num = 2.0
WHILE num <= 50.0
    IF isPrime(num) == 1.0 THEN
        PRINT "  ", num
    ENDIF
    LET num = num + 1.0
ENDWHILE
PRINT ""
PRINT ""

PRINT "8. POWERS"
PRINT "   2^8 =", power(2.0, 8.0)
PRINT "   3^4 =", power(3.0, 4.0)
PRINT "   5^3 =", power(5.0, 3.0)
PRINT ""

PRINT "9. PERFECT SQUARES"
PRINT "   16 is perfect square:", isPerfectSquare(16.0)
PRINT "   25 is perfect square:", isPerfectSquare(25.0)
PRINT "   26 is perfect square:", isPerfectSquare(26.0)
PRINT "   100 is perfect square:", isPerfectSquare(100.0)
PRINT ""

PRINT "========================================================"
PRINT "  All mathematical tests complete!"
PRINT "========================================================"

