FUNCTION add(a As Integer, b As Integer) As Integer
    RETURN a + b
ENDFUNCTION

FUNCTION times2(x As Integer) As Integer
    RETURN x * 2
ENDFUNCTION

Console.WriteLine(add(3, 4))
Console.WriteLine(times2(10))
Console.WriteLine(add(times2(5), 3))

