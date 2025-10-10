DIM names(3) = ""

LET names(0) = "Alice"
LET names(1) = "Bob"
LET names(2) = "Charlie"

PRINT "Names in array:"
PRINT names(0)
PRINT names(1)
PRINT names(2)

IF names(0) == "Alice" THEN
    PRINT "First name is Alice"
ENDIF

