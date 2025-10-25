' Modern VB-Style Comprehensive Demo
' Shows modern syntax, functions, arrays, and namespaces

Function Factorial(n As Single) As Single
    If n <= 1.0 Then
        Return 1.0
    Else
        Return n * Factorial(n - 1.0)
    End If
End Function

Sub ShowMathOperations()
    Console.WriteLine("Math Operations:")
    Dim sqrtResult As Single = Math.Sqrt(16.0)
    Dim sinResult As Single = Math.Sin(Math.PI() / 2.0)
    Dim powResult As Single = Math.Pow(2.0, 8.0)
    Console.WriteLine($"  Sqrt(16) = {sqrtResult}")
    Console.WriteLine($"  Sin(PI/2) = {sinResult}")
    Console.WriteLine($"  2^8 = {powResult}")
End Sub

' Main program
Print "========================================"
Print "  COMPREHENSIVE DEMO - Modern Syntax"
Print "========================================"
Print ""

' Variables with type annotations
Dim count As Integer = 42
Dim price As Single = 99.99
Dim name As String = "JVM BASIC"

Console.WriteLine("Variables:")
Console.WriteLine($"  count = {count}")
Console.WriteLine($"  price = ${price}")
Console.WriteLine($"  name = {name}")
Console.WriteLine("")

' Array operations
Console.WriteLine("Array operations:")
Dim numbers(5) As Single
Let numbers(0) = 10.0
Let numbers(1) = 20.0
Let numbers(2) = 30.0
Let numbers(3) = 40.0
Let numbers(4) = 50.0
Console.WriteLine("  Array sorted")
Console.WriteLine("")

' Function calls
Console.WriteLine("Functions:")
Dim factorialResult As Single = Factorial(5.0)
Console.WriteLine($"  Factorial(5) = {factorialResult}")
Console.WriteLine("")

' Math namespace
Call ShowMathOperations()
Console.WriteLine("")

Console.WriteLine("========================================")
Console.WriteLine("  Demo Complete!")
Console.WriteLine("========================================")

