' Modern VB-Style Fibonacci Sequence
' True case-insensitive modern syntax

Function Fibonacci(n As Single) As Single
    If n <= 1.0 Then
        Return n
    Else
        Return Fibonacci(n - 1.0) + Fibonacci(n - 2.0)
    End If
End Function

Function FibonacciIterative(n As Single) As Single
    Dim a As Single = 0.0
    Dim b As Single = 1.0
    Dim count As Single = 0.0
    
    While count < n
        Dim temp As Single = a + b
        a = b
        b = temp
        count = count + 1.0
    End While
    Return a
End Function

Sub PrintFibSequence(n As Single)
    Console.WriteLine($"Fibonacci sequence (first {n} terms):")
    Dim i As Single = 0.0
    While i < n
        Dim fibValue As Single = FibonacciIterative(i)
        Console.WriteLine($"  fib({i}) = {fibValue}")
        i = i + 1.0
    End While
End Sub

' Main program
Console.WriteLine("=========================================")
Console.WriteLine("  FIBONACCI - Recursive & Iterative")
Console.WriteLine("=========================================")
Console.WriteLine("")

Console.WriteLine("Recursive fibonacci:")
Dim fib5 As Single = Fibonacci(5.0)
Dim fib10 As Single = Fibonacci(10.0)
Dim fib15 As Single = Fibonacci(15.0)
Console.WriteLine($"  fib(5) = {fib5}")
Console.WriteLine($"  fib(10) = {fib10}")
Console.WriteLine($"  fib(15) = {fib15}")
Console.WriteLine("")

Console.WriteLine("Iterative fibonacci:")
Dim fib20 As Single = FibonacciIterative(20.0)
Dim fib25 As Single = FibonacciIterative(25.0)
Dim fib30 As Single = FibonacciIterative(30.0)
Console.WriteLine($"  fib(20) = {fib20}")
Console.WriteLine($"  fib(25) = {fib25}")
Console.WriteLine($"  fib(30) = {fib30}")
Console.WriteLine("")

Call PrintFibSequence(12.0)

Console.WriteLine("")
Console.WriteLine("=========================================")
Console.WriteLine("  Fibonacci Complete!")
Console.WriteLine("=========================================")
