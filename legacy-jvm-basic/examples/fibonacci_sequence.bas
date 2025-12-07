' Fibonacci Sequence - Recursive and Iterative Implementations

Function fibonacci(n As Integer) As Integer
    If n <= 1 Then
        Return n
    Else
        Return fibonacci(n - 1) + fibonacci(n - 2)
    EndIf
EndFunction

Function fibonacciIterative(n As Integer) As Integer
    Dim a As Integer
    Dim b As Integer
    Dim count As Integer
    Dim temp As Integer
    a = 0
    b = 1
    count = 0

    While count < n
        temp = a + b
        a = b
        b = temp
        count = count + 1
    EndWhile
    Return a
EndFunction

Sub printFibSequence(n As Integer)
    Dim i As Integer
    Console.WriteLine("Fibonacci sequence (first " + n + " terms):")
    i = 0
    While i < n
        Console.WriteLine("  fib(" + i + ") = " + fibonacciIterative(i))
        i = i + 1
    EndWhile
EndSub

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

Call printFibSequence(12)

Console.WriteLine("")
Console.WriteLine("=========================================")
Console.WriteLine("  Fibonacci Complete!")
Console.WriteLine("=========================================")
