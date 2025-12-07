Console.WriteLine("Test WHILE loop")
Dim x As Integer = 0
WHILE x < 5
    Console.WriteLine(x)
    x = x + 1
ENDWHILE

Console.WriteLine("Test DO-WHILE")
Dim y As Integer = 0
DO
    Console.WriteLine("y = " + y)
    y = y + 1
WHILE y < 3

Console.WriteLine("Test DO-UNTIL")
Dim z As Integer = 0
DO
    Console.WriteLine("z = " + z)
    z = z + 1
UNTIL z >= 3

Console.WriteLine("Done")

