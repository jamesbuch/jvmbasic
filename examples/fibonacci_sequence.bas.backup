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

SUB printFibSequence(n)
    PRINT "Fibonacci sequence (first", n, "terms):"
    LET i = 0.0
    WHILE i < n
        PRINT "  fib(", i, ") =", fibonacciIterative(i)
        LET i = i + 1.0
    ENDWHILE
ENDSUB

PRINT "========================================="
PRINT "  FIBONACCI - Recursive & Iterative"
PRINT "========================================="
PRINT ""

PRINT "Recursive fibonacci:"
PRINT "  fib(5) =", fibonacci(5.0)
PRINT "  fib(10) =", fibonacci(10.0)
PRINT "  fib(15) =", fibonacci(15.0)
PRINT ""

PRINT "Iterative fibonacci:"
PRINT "  fib(20) =", fibonacciIterative(20.0)
PRINT "  fib(25) =", fibonacciIterative(25.0)
PRINT "  fib(30) =", fibonacciIterative(30.0)
PRINT ""

CALL printFibSequence(12.0)

PRINT ""
PRINT "========================================="
PRINT "  Fibonacci Complete!"
PRINT "========================================="

