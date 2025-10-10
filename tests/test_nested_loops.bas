PRINT "Multiplication table:"
FOR i = 1 TO 3
    FOR j = 1 TO 3
        LET product = i * j
        PRINT i; " * "; j; " = "; product
    NEXT j
NEXT i

PRINT "Nested WHILE:"
LET x = 0
WHILE x < 2
    LET y = 0
    WHILE y < 2
        PRINT "x="; x; " y="; y
        LET y = y + 1
    ENDWHILE
    LET x = x + 1
ENDWHILE

PRINT "Done"

