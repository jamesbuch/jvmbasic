FUNCTION square(x As Integer) As Integer
    RETURN x * x
ENDFUNCTION

FUNCTION add(a As Integer, b As Integer) As Integer
    RETURN a + b
ENDFUNCTION

SUB printResult(msg As String, value As Integer)
    Console.WriteLine(msg + " " + value)
ENDSUB

Console.WriteLine("Testing multiple functions")

Dim x As Integer = 5
Dim sq As Integer = square(x)
CALL printResult("Square of 5:", sq)

Dim sum As Integer = add(10, 20)
CALL printResult("10 + 20 =", sum)

Console.WriteLine("Square of sum: " + square(sum))

