PRINT "=========================================="
PRINT "   JVM BASIC - ULTIMATE DEMO"
PRINT "   Showcasing ALL Features"
PRINT "=========================================="

PRINT "Creating and filling array with random scores..."
DIM scores(10) = 0
FOR i = 0 TO 9
    LET scores(i) = INT(RND() * 100)
NEXT i

PRINT "Scores:"
FOR i = 0 TO 9
    PRINT "Student", i, ":", scores(i)
NEXT i

PRINT "Computing statistics..."
LET minScore = MINARRAY(scores)
LET maxScore = MAXARRAY(scores)
LET totalScore = SUMARRAY(scores)
LET studentCount = UBOUND(scores) + 1
LET avgScore = totalScore / studentCount

PRINT "Statistics:"
PRINT "  Min:", minScore
PRINT "  Max:", maxScore
PRINT "  Total:", totalScore
PRINT "  Average:", avgScore

PRINT "Grade distribution:"
LET countA = 0
LET countB = 0
LET countC = 0
LET countF = 0

FOR i = 0 TO 9
    IF scores(i) >= 90 THEN
        LET countA = countA + 1
    ELSEIF scores(i) >= 80 THEN
        LET countB = countB + 1
    ELSEIF scores(i) >= 70 THEN
        LET countC = countC + 1
    ELSE
        LET countF = countF + 1
    ENDIF
NEXT i

PRINT "  A grades:", countA
PRINT "  B grades:", countB
PRINT "  C grades:", countC
PRINT "  F grades:", countF

PRINT "Math demonstration:"
LET angle = PI() / 4
PRINT "  sin(45°) =", SIN(angle)
PRINT "  cos(45°) =", COS(angle)
PRINT "  tan(45°) =", TAN(angle)

PRINT "String manipulation:"
DIM names(3) = ""
LET names(0) = "alice"
LET names(1) = "bob"
LET names(2) = "charlie"

PRINT "  Original:", names(0), names(1), names(2)

FOR i = 0 TO 2
    LET names(i) = UPPER(names(i))
NEXT i

PRINT "  Uppercase:", names(0), names(1), names(2)

PRINT "Nested loops - Multiplication drill:"
FOR i = 1 TO 3
    FOR j = 1 TO 3
        LET product = i * j
        PRINT "  "; i; " x "; j; " = "; product
    NEXT j
NEXT i

PRINT "Conditional logic with functions:"
LET testScore = avgScore
IF testScore >= 90 THEN
    PRINT "  Class performance: EXCELLENT"
ELSEIF testScore >= 70 THEN
    PRINT "  Class performance: GOOD"
ELSE
    PRINT "  Class performance: NEEDS IMPROVEMENT"
ENDIF

PRINT "Complex calculations:"
LET a = 3.0
LET b = 4.0
LET hypotenuse = SQR(POW(a, 2) + POW(b, 2))
PRINT "  Triangle (3,4) hypotenuse:", hypotenuse

PRINT "String functions showcase:"
LET msg = "  Hello, JVM BASIC!  "
PRINT "  Original: ["; msg; "]"
PRINT "  Trimmed: ["; TRIM(msg); "]"
PRINT "  Length:", LEN(msg)
PRINT "  First 5:", LEFT(msg, 5)
PRINT "  Last 5:", RIGHT(msg, 5)

PRINT "=========================================="
PRINT "   DEMO COMPLETE!"
PRINT "   All features working perfectly!"
PRINT "=========================================="
PRINT "JVM BASIC is a fully functional programming language!"

