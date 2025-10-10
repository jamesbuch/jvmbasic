PRINT "=================================="
PRINT "  JVM BASIC Feature Showcase"
PRINT "=================================="

PRINT "1. Variables and Types"
LET x = 42
LET pi = 3.14159
LET name = "Alice"
LET flag = true
PRINT "Int:", x, "Float:", pi
PRINT "String:", name, "Bool:", flag

PRINT "2. Arithmetic"
LET sum = x + 8
LET product = pi * 2
PRINT "42 + 8 =", sum
PRINT "pi * 2 =", product

PRINT "3. Arrays"
DIM nums(3) = 0
LET nums(0) = 10
LET nums(1) = 20
LET nums(2) = 30
PRINT "Array:", nums(0), nums(1), nums(2)

PRINT "4. String Functions"
PRINT "UPPER(hello):", UPPER("hello")
PRINT "LEN(Alice):", LEN(name)
PRINT "LEFT(Alice, 3):", LEFT(name, 3)

PRINT "5. Math Functions"
PRINT "SQR(16):", SQR(16)
PRINT "ABS(-7):", ABS(0 - 7)
PRINT "POW(2, 10):", POW(2, 10)
PRINT "PI:", PI
PRINT "SIN(0):", SIN(0)

PRINT "6. Nested Functions"
LET a = 3
LET b = 4
LET c = SQR(POW(a, 2) + POW(b, 2))
PRINT "Pythagorean:", a, ",", b, "->", c

PRINT "7. Comparisons"
IF x > 40 THEN
    PRINT "x is greater than 40"
ENDIF

IF name == "Alice" THEN
    PRINT "Hello, Alice!"
ENDIF

PRINT "8. Control Flow"
LET score = 85
IF score >= 90 THEN
    PRINT "Grade: A"
ELSEIF score >= 80 THEN
    PRINT "Grade: B"
ELSEIF score >= 70 THEN
    PRINT "Grade: C"
ELSE
    PRINT "Grade: F"
ENDIF

PRINT "9. Complex Expressions"
LET result = INT(SQR(nums(0)) + ABS(0 - nums(1)) + POW(2, 3))
PRINT "Complex expression result:", result

PRINT "10. String Arrays"
DIM greetings(3) = ""
LET greetings(0) = UPPER("hello")
LET greetings(1) = LOWER("WORLD")
LET greetings(2) = LEFT("Goodbye", 4)
PRINT greetings(0), greetings(1), greetings(2)

PRINT "=================================="
PRINT "All features working perfectly!"
PRINT "=================================="

