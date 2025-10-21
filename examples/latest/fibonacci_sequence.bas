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
    Print "Fibonacci sequence (first "; n; " terms):"
    Dim i As Single = 0.0
    While i < n
        Print "  fib("; i; ") = "; FibonacciIterative(i)
        i = i + 1.0
    End While
End Sub

' Main program
Print "========================================="
Print "  FIBONACCI - Recursive & Iterative"
Print "========================================="
Print ""

Print "Recursive fibonacci:"
Print "  fib(5) = "; Fibonacci(5.0)
Print "  fib(10) = "; Fibonacci(10.0)
Print "  fib(15) = "; Fibonacci(15.0)
Print ""

Print "Iterative fibonacci:"
Print "  fib(20) = "; FibonacciIterative(20.0)
Print "  fib(25) = "; FibonacciIterative(25.0)
Print "  fib(30) = "; FibonacciIterative(30.0)
Print ""

Call PrintFibSequence(12.0)

Print ""
Print "========================================="
Print "  Fibonacci Complete!"
Print "========================================="
