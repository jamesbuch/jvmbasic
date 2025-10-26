REM Simple struct test
TYPE Point
    x As Float
    y As Float
ENDTYPE

DIM p As Point
p.x = 10.0
p.y = 20.0
Console.WriteLine("Point: (" + p.x + ", " + p.y + ")")

