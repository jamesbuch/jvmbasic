REM Test bitwise operators
REM Phase 9: Shift operators

Dim a As Integer = 5      REM 0101 in binary
Dim b As Integer = 2

REM Test shift left
Dim c As Integer = a << 1  REM 5 << 1 = 10 (1010)
Print "5 << 1 = "; c

REM Test shift right
Dim d As Integer = a >> 1  REM 5 >> 1 = 2 (0010)
Print "5 >> 1 = "; d

REM Test combined
Dim e As Integer = (a << 2) + (b << 1)
Print "(5 << 2) + (2 << 1) = "; e

Print "Bitwise shift operators work!"

