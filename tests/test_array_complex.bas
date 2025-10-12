DIM scores(5) = 0

LET scores(0) = 85
LET scores(1) = 92
LET scores(2) = 78
LET scores(3) = 95
LET scores(4) = 88

PRINT "Test Scores:"
LET i = 0
PRINT "Score", i, "=", scores(i)
LET i = 1
PRINT "Score", i, "=", scores(i)

LET total = scores(0) + scores(1) + scores(2)
PRINT "Total of first 3 scores:", total

IF scores(3) > 90 THEN
    PRINT "Student 3 got an A!"
ENDIF

DIM flags(3) = false
LET flags(0) = true
LET flags(1) = false

PRINT "Flags:"
PRINT flags(0), flags(1)

