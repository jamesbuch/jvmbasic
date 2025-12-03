FUNCTION add(a As Integer, b As Integer) As Integer
    RETURN a + b
ENDFUNCTION

FUNCTION multiply(x As Integer, y As Integer) As Integer
    RETURN x * y
ENDFUNCTION

FUNCTION average(a As Integer, b As Integer, c As Integer) As Integer
    RETURN (a + b + c) / 3
ENDFUNCTION

Console.WriteLine("Testing multi-parameter functions")

Dim sum As Integer
sum = add(10, 20)
Console.WriteLine("add(10, 20) = " + sum)

Dim product As Integer
product = multiply(6, 7)
Console.WriteLine("multiply(6, 7) = " + product)

Dim avg As Integer
avg = average(10, 20, 30)
Console.WriteLine("average(10, 20, 30) = " + avg)

