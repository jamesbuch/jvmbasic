FUNCTION add(a As Float, b As Float) As Float
    RETURN a + b
ENDFUNCTION

SUB greet(name As String)
Console.WriteLine("Hello, " + name)
ENDSUB

Console.WriteLine("Testing type inference")

Dim x As Integer
Dim y As Integer
Dim result As Single
x = 5
y = 3
result = add(x, y)
Console.WriteLine("add(5, 3) = " + result)

CALL greet("World")
CALL greet("Alice")

Dim f As Single
f = add(2.5, 3.5)
Console.WriteLine("add(2.5, 3.5) = " + f)

