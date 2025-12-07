Console.WriteLine("Multiplication table:")
Dim i As Integer
Dim j As Integer
Dim product As Integer
FOR i = 1 TO 3
    FOR j = 1 TO 3
        product = i * j
        Console.WriteLine("Product: " + product)
    NEXT j
NEXT i

Console.WriteLine("Nested WHILE:")
Dim x As Integer
Dim y As Integer
x = 0
WHILE x < 2
    y = 0
    WHILE y < 2
Console.WriteLine("x=" + x + " y=" + y)
        y = y + 1
    ENDWHILE
    x = x + 1
ENDWHILE

Console.WriteLine("Done")

