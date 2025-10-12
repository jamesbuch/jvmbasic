REM Test nested structs
TYPE Point
    x AS FLOAT
    y AS FLOAT
ENDTYPE

TYPE Rectangle
    width AS FLOAT
    height AS FLOAT
ENDTYPE

DIM p AS Point
LET p.x = 5.0
LET p.y = 10.0

DIM r AS Rectangle
LET r.width = 100.0
LET r.height = 50.0

PRINT "Point: ("; p.x; ", "; p.y; ")"
PRINT "Rectangle: "; r.width; "x"; r.height

