FUNCTION square(x As Integer) As Integer
    RETURN x * x
ENDFUNCTION

FUNCTION increment(n As Integer) As Integer
    RETURN n + 1
ENDFUNCTION

Console.WriteLine("Testing single-parameter functions")

x = 5
sq = square(x)
Console.WriteLine("square(5) = " + sq)

num = increment(10)
Console.WriteLine("increment(10) = " + num)

Console.WriteLine("Nested: " + square(increment(3)))

