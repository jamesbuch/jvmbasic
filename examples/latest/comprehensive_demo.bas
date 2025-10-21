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
    Print "Math Operations:"
    Print "  Sqrt(16) = "; Math.Sqrt(16.0)
    Print "  Sin(PI/2) = "; Math.Sin(Math.PI() / 2.0)
    Print "  2^8 = "; Math.Pow(2.0, 8.0)
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

Print "Variables:"
Print "  count = "; count
Print "  price = "; price
Print "  name = "; name
Print ""

' Array operations
Print "Array operations:"
Dim numbers(5) = 0.0
numbers(0) = 10.0
numbers(1) = 20.0
numbers(2) = 30.0
numbers(3) = 40.0
numbers(4) = 50.0
Call ARRAYSORT(numbers)
Print "  Array sorted"
Print ""

' Function calls
Print "Functions:"
Print "  Factorial(5) = "; Factorial(5.0)
Print ""

' Math namespace
Call ShowMathOperations()
Print ""

Print "========================================"
Print "  Demo Complete!"
Print "========================================"

