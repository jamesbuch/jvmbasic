FUNCTION add(a As Integer, b As Integer) As Integer
    RETURN a + b
ENDFUNCTION

Console.WriteLine("Testing simple function")
Dim result As Integer
result = add(5, 3)
Console.WriteLine("5 + 3 = " + result)

