' Test all bitwise operators - Phase 9 complete

' Test bitwise AND
a = 5      ' 0101
b = 3      ' 0011
c = a & b  ' 0001 = 1
Console.WriteLine("5 & 3 = " + c)

' Test bitwise OR
d = a | b  ' 0111 = 7
Console.WriteLine("5 | 3 = " + d)

' Test bitwise XOR
e = a ^ b  ' 0110 = 6
Console.WriteLine("5 ^ 3 = " + e)

' Test shift operators
f = a << 2  ' 20
g = a >> 1  ' 2
Console.WriteLine("5 << 2 = " + f)
Console.WriteLine("5 >> 1 = " + g)

' Test combinations
flags = (1 | 2 | 4)  ' 7
Console.WriteLine("1 | 2 | 4 = " + flags)

mask = 255 & 15  ' 15
Console.WriteLine("255 & 15 = " + mask)

Console.WriteLine("All bitwise operators test complete!")

