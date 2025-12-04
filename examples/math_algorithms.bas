' Mathematical Algorithms Demonstration
' Shows GCD, LCM, Factorial, Fibonacci, Prime checking

FUNCTION gcd(a As Integer, b As Integer) As Integer
    Dim temp As Integer
    WHILE b > 0
        temp = b
        b = a MOD b
        a = temp
    ENDWHILE
    RETURN a
ENDFUNCTION

FUNCTION lcm(a As Integer, b As Integer) As Integer
    RETURN (a * b) / gcd(a, b)
ENDFUNCTION

FUNCTION factorial(n As Integer) As Integer
    IF n <= 1 THEN
        RETURN 1
    ELSE
        RETURN n * factorial(n - 1)
    ENDIF
ENDFUNCTION

FUNCTION fibonacci(n As Integer) As Integer
    IF n <= 1 THEN
        RETURN n
    ELSE
        RETURN fibonacci(n - 1) + fibonacci(n - 2)
    ENDIF
ENDFUNCTION

FUNCTION fibonacciIterative(n As Integer) As Integer
    Dim a As Integer
    Dim b As Integer
    Dim i As Integer
    Dim temp As Integer
    IF n <= 1 THEN
        RETURN n
    ENDIF

    a = 0
    b = 1
    i = 2
    WHILE i <= n
        temp = a + b
        a = b
        b = temp
        i = i + 1
    ENDWHILE
    RETURN b
ENDFUNCTION

FUNCTION isPrime(n As Integer) As Integer
    Dim i As Integer
    IF n < 2 THEN
        RETURN 0
    ENDIF

    i = 2
    WHILE i * i <= n
        IF n MOD i == 0 THEN
            RETURN 0
        ENDIF
        i = i + 1
    ENDWHILE
    RETURN 1
ENDFUNCTION

Console.WriteLine("================================================")
Console.WriteLine("  MATHEMATICAL ALGORITHMS DEMONSTRATION")
Console.WriteLine("================================================")
Console.WriteLine("")

Console.WriteLine("1. Greatest Common Divisor (GCD):")
Console.WriteLine("   GCD(48, 18) = " + gcd(48, 18))
Console.WriteLine("   GCD(1071, 462) = " + gcd(1071, 462))
Console.WriteLine("")

Console.WriteLine("2. Least Common Multiple (LCM):")
Console.WriteLine("   LCM(12, 18) = " + lcm(12, 18))
Console.WriteLine("   LCM(15, 25) = " + lcm(15, 25))
Console.WriteLine("")

Console.WriteLine("3. Factorial:")
Console.WriteLine("   5! = " + factorial(5))
Console.WriteLine("   7! = " + factorial(7))
Console.WriteLine("")

Console.WriteLine("4. Fibonacci Sequence:")
Console.WriteLine("   fib(10) = " + fibonacci(10))
Console.WriteLine("   fib(20) = " + fibonacciIterative(20))
Console.WriteLine("")

Console.WriteLine("5. Prime Number Check:")
Console.WriteLine("   Is 17 prime? " + isPrime(17))
Console.WriteLine("   Is 25 prime? " + isPrime(25))
Console.WriteLine("   Is 29 prime? " + isPrime(29))
Console.WriteLine("")

Console.WriteLine("6. Built-in Math Functions:")
Console.WriteLine("   SQR(144) = " + SQR(144))
Console.WriteLine("   POW(2, 10) = " + POW(2, 10))
Console.WriteLine("   ABS(-15) = " + ABS(-15))
Console.WriteLine("   MIN(5, 3) = " + MIN(5, 3))
Console.WriteLine("   MAX(5, 3) = " + MAX(5, 3))
Console.WriteLine("")

Console.WriteLine("================================================")
Console.WriteLine("  All mathematical algorithms working!")
Console.WriteLine("================================================")
