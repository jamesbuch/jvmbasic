REM Simple struct test
TYPE Point
    x AS FLOAT
    y AS FLOAT
ENDTYPE

DIM p AS Point
LET p.x = 10.0
LET p.y = 20.0
PRINT "Point: "; p.x; ", "; p.y

