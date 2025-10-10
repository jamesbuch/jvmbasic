PRINT "======================================"
PRINT "    LOOPS SHOWCASE"
PRINT "======================================"

PRINT "1. Simple FOR loop - Print 1 to 5"
FOR i = 1 TO 5
    PRINT i
NEXT i

PRINT "2. FOR with STEP - Even numbers"
FOR i = 0 TO 10 STEP 2
    PRINT i
NEXT i

PRINT "3. FOR with arrays - Fill array"
DIM scores(5) = 0
FOR i = 0 TO 4
    LET scores(i) = (i + 1) * 10
NEXT i

PRINT "Scores:"
FOR i = 0 TO 4
    PRINT "Score", i, "=", scores(i)
NEXT i

PRINT "4. Nested FOR loops - Multiplication table"
FOR i = 1 TO 3
    FOR j = 1 TO 3
        PRINT i; " * "; j; " = "; i * j
    NEXT j
NEXT i

PRINT "5. WHILE loop - Count up"
LET x = 0
WHILE x < 5
    PRINT "x =", x
    LET x = x + 1
ENDWHILE

PRINT "6. DO-WHILE - Executes at least once"
LET y = 0
DO
    PRINT "y =", y
    LET y = y + 1
WHILE y < 3

PRINT "7. DO-UNTIL variant"
LET z = 0
DO
    PRINT "z =", z
    LET z = z + 1
UNTIL z >= 3

PRINT "8. Loops with functions"
DIM nums(5) = 0
FOR i = 0 TO 4
    LET nums(i) = INT(RND() * 100)
NEXT i

PRINT "Random numbers:"
FOR i = 0 TO 4
    PRINT nums(i)
NEXT i

PRINT "9. Loop with math in expression"
LET sum = 0.0
FOR i = 1 TO 5
    LET sum = sum + POW(i, 2)
NEXT i
PRINT "Sum of squares 1-5:", INT(sum)

PRINT "======================================"
PRINT "All loop types working perfectly!"
PRINT "======================================"

