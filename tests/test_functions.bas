PRINT "=== Math Functions ==="
LET neg = 0 - 5
PRINT "ABS of negative:", ABS(neg)
PRINT "SQR(16):", SQR(16)
PRINT "INT(3.7):", INT(3.7)
PRINT "POW(2, 3):", POW(2, 3)

PRINT "SIN(0):", SIN(0)
PRINT "COS(0):", COS(0)

PRINT "PI:", PI
PRINT "E:", E

PRINT "MIN(10, 5):", MIN(10, 5)
PRINT "MAX(10, 5):", MAX(10, 5)

PRINT "=== String Functions ==="
LET s = "Hello World"
PRINT "LEN:", LEN(s)
PRINT "UPPER:", UPPER(s)
PRINT "LOWER:", LOWER(s)
PRINT "LEFT(5):", LEFT(s, 5)
PRINT "RIGHT(5):", RIGHT(s, 5)
PRINT "MID(3, 5):", MID(s, 3, 5)

LET name = "  Alice  "
PRINT "Before trim: ["; name; "]"
PRINT "After trim: ["; TRIM(name); "]"

PRINT "CHR(65):", CHR(65)
PRINT "ASC(A):", ASC("A")

PRINT "=== Functions in Expressions ==="
LET a = 3
LET b = 4
LET hyp = SQR(POW(a, 2) + POW(b, 2))
PRINT "Hypotenuse of 3,4:", hyp

PRINT "=== Done ==="

