' Test bitwise operators
' Phase 9: Shift operators

Dim a As Integer
Dim b As Integer
a = 5      ' 0101 in binary
b = 2

' Test shift left
Dim c As Integer
c = a << 1  ' 5 << 1 = 10 (1010)
Console.WriteLine("5 << 1 = " + c)

' Test shift right
Dim d As Integer
d = a >> 1  ' 5 >> 1 = 2 (0010)
Console.WriteLine("5 >> 1 = " + d)

' Test combined
Dim e As Integer
e = (a << 2) + (b << 1)
Console.WriteLine("(5 << 2) + (2 << 1) = " + e)

Console.WriteLine("Bitwise shift operators work!")

