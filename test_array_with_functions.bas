DIM nums(5) = 0

LET nums(0) = INT(ABS(0 - 10))
LET nums(1) = INT(3.7)
LET nums(2) = ROUND(2.5)
LET nums(3) = INT(POW(2, 3))

PRINT "Array with function results:"
PRINT nums(0), nums(1), nums(2), nums(3)

DIM names(3) = ""
LET names(0) = UPPER("alice")
LET names(1) = LOWER("BOB")
LET names(2) = LEFT("Charlie", 4)

PRINT "String array:"
PRINT names(0), names(1), names(2)

LET total = nums(0) + nums(1) + nums(2)
PRINT "Sum:", total

IF LEN(names(0)) > 3 THEN
    PRINT names(0), "is long"
ENDIF

