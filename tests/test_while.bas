PRINT "Test WHILE loop"
LET x = 0
WHILE x < 5
    PRINT x
    LET x = x + 1
ENDWHILE

PRINT "Test DO-WHILE"
LET y = 0
DO
    PRINT "y =", y
    LET y = y + 1
WHILE y < 3

PRINT "Test DO-UNTIL"
LET z = 0
DO
    PRINT "z =", z
    LET z = z + 1
UNTIL z >= 3

PRINT "Done"

