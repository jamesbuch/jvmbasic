REM Test all bitwise operators - Phase 9 complete

REM Test bitwise AND
a = 5      REM 0101
b = 3      REM 0011
c = a & b  REM 0001 = 1
Console.WriteLine("5 & 3 = " + c)

REM Test bitwise OR
d = a | b  REM 0111 = 7
Console.WriteLine("5 | 3 = " + d)

REM Test bitwise XOR
e = a ^ b  REM 0110 = 6
Console.WriteLine("5 ^ 3 = " + e)

REM Test shift operators
f = a << 2  REM 20
g = a >> 1  REM 2
Console.WriteLine("5 << 2 = " + f)
Console.WriteLine("5 >> 1 = " + g)

REM Test combinations
flags = (1 | 2 | 4)  REM 7
Console.WriteLine("1 | 2 | 4 = " + flags)

mask = 255 & 15  REM 15
Console.WriteLine("255 & 15 = " + mask)

Console.WriteLine("All bitwise operators test complete!")

