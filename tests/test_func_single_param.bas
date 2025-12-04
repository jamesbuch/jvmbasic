FUNCTION square(x As Integer) As Integer
    RETURN x * x
ENDFUNCTION

FUNCTION increment(n As Integer) As Integer
    RETURN n + 1
ENDFUNCTION

Console.WriteLine("Testing single-parameter functions")

Dim x As Integer
x = 5
Dim sq As Integer
sq = square(x)
Console.WriteLine("square(5) = " + sq)

Dim num As Integer
num = increment(10)
Console.WriteLine("increment(10) = " + num)

Console.WriteLine("Nested: " + square(increment(3)))

