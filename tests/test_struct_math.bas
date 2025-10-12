REM Test struct with math operations
TYPE Vector2D
    x AS FLOAT
    y AS FLOAT
ENDTYPE

DIM v1 AS Vector2D
DIM v2 AS Vector2D

LET v1.x = 3.0
LET v1.y = 4.0

LET v2.x = 5.0
LET v2.y = 12.0

REM Calculate magnitudes
LET mag1 = SQRT(v1.x * v1.x + v1.y * v1.y)
LET mag2 = SQRT(v2.x * v2.x + v2.y * v2.y)

PRINT "Vector 1: ("; v1.x; ", "; v1.y; "), magnitude: "; mag1
PRINT "Vector 2: ("; v2.x; ", "; v2.y; "), magnitude: "; mag2

