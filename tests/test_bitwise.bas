REM Test bitwise operators
REM Phase 9: Shift operators

a = 5      REM 0101 in binary
b = 2

REM Test shift left
c = a << 1  REM 5 << 1 = 10 (1010)
Console.WriteLine("5 << 1 = " + c)

REM Test shift right
d = a >> 1  REM 5 >> 1 = 2 (0010)
Console.WriteLine("5 >> 1 = " + d)

REM Test combined
e = (a << 2) + (b << 1)
Console.WriteLine("(5 << 2) + (2 << 1) = " + e)

Console.WriteLine("Bitwise shift operators work!")

