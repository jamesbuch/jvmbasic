FUNCTION fibonacci(n As Integer) As Integer
    IF n <= 1 THEN
        RETURN n
    ELSE
        RETURN fibonacci(n - 1) + fibonacci(n - 2)
    ENDIF
ENDFUNCTION

FUNCTION fibonacciIterative(n As Integer) As Integer
    a = 0
    b = 1
    count = 0
    
    WHILE count < n
        temp = a + b
        a = b
        b = temp
        count = count + 1
    ENDWHILE
    RETURN a
ENDFUNCTION

SUB printFibSequence(n As Integer)
    Console.WriteLine("Fibonacci sequence (first " + n + " terms):")
    i = 0
    WHILE i < n
        Console.WriteLine("  fib(" + i + ") = " + fibonacciIterative(i))
        i = i + 1
    ENDWHILE
ENDSUB

Console.WriteLine("=========================================")
Console.WriteLine("  FIBONACCI - Recursive & Iterative")
Console.WriteLine("=========================================")
Console.WriteLine("")

Console.WriteLine("Recursive fibonacci:")
Console.WriteLine("  fib(5) = " + fibonacci(5))
Console.WriteLine("  fib(10) = " + fibonacci(10))
Console.WriteLine("  fib(15) = " + fibonacci(15))
Console.WriteLine("")

Console.WriteLine("Iterative fibonacci:")
Console.WriteLine("  fib(20) = " + fibonacciIterative(20))
Console.WriteLine("  fib(25) = " + fibonacciIterative(25))
Console.WriteLine("  fib(30) = " + fibonacciIterative(30))
Console.WriteLine("")

CALL printFibSequence(12)

Console.WriteLine("")
Console.WriteLine("=========================================")
Console.WriteLine("  Fibonacci Complete!")
Console.WriteLine("=========================================")

