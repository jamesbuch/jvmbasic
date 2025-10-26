FUNCTION factorial(n As Integer) As Integer
    DIM result As Integer = 1
    DIM i As Integer = 1
    WHILE i <= n
        result = result * i
        i = i + 1
    ENDWHILE
    RETURN result
ENDFUNCTION

FUNCTION sumrange(start As Integer, finish As Integer) As Integer
    DIM total As Integer = 0
    FOR i = start TO finish
        total = total + i
    NEXT i
    RETURN total
ENDFUNCTION

Console.WriteLine("Testing functions with loops")

fact5 = factorial(5)
Console.WriteLine("factorial(5) = " + fact5)

sum = sumrange(1, 10)
Console.WriteLine("sum(1 to 10) = " + sum)

