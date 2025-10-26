REM Test modern VB-style function syntax
REM Phase 9: Typed parameters and return types

FUNCTION Add(a As Integer, b As Integer) As Integer
    RETURN a + b
ENDFUNCTION

FUNCTION Multiply(x As Float, y As Float) As Float
    RETURN x * y
ENDFUNCTION

FUNCTION Greet(name As String) As String
    RETURN "Hello, " + name + "!"
ENDFUNCTION

SUB PrintSum(a As Integer, b As Integer)
    DIM result As Integer
    result = Add(a, b)
    Console.WriteLine("Sum: " + result)
ENDSUB

REM Test the functions
DIM x As Integer
DIM y As Integer
DIM sum As Integer
x = 10
y = 20
sum = Add(x, y)
Console.WriteLine("Add(10, 20) = " + sum)

f1 = 3.5
f2 = 2.0
product = Multiply(f1, f2)
Console.WriteLine("Multiply(3.5, 2.0) = " + product)

DIM greeting As String
greeting = Greet("World")
Console.WriteLine(greeting)

CALL PrintSum(15, 25)

