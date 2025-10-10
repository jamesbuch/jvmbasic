PRINT "Test 1: Basic FOR loop"
FOR i = 1 TO 5
    PRINT i
NEXT i

PRINT "Test 2: FOR with STEP"
FOR i = 0 TO 10 STEP 2
    PRINT i
NEXT i

PRINT "Test 3: Countdown"
FOR i = 5 TO 1 STEP -1
    PRINT i
NEXT i

PRINT "Test 4: FOR with arrays"
DIM nums(5) = 0
FOR i = 0 TO 4
    LET nums(i) = i * 10
NEXT i

PRINT "Array contents:"
FOR i = 0 TO 4
    PRINT nums(i)
NEXT i

