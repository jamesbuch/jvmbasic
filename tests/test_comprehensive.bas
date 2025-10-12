FUNCTION circleArea(radius)
    RETURN PI * radius * radius
ENDFUNCTION

FUNCTION celsiusToFahrenheit(c)
    RETURN c * 9.0 / 5.0 + 32.0
ENDFUNCTION

FUNCTION max3(a, b, c)
    LET temp = a
    IF b > temp THEN
        LET temp = b
    ENDIF
    IF c > temp THEN
        LET temp = c
    ENDIF
    RETURN temp
ENDFUNCTION

FUNCTION hypotenuse(a, b)
    RETURN SQR(a * a + b * b)
ENDFUNCTION

FUNCTION triangleArea(base, height)
    RETURN base * height / 2.0
ENDFUNCTION

FUNCTION pythagoreanTriple(a, b)
    LET c = hypotenuse(a, b)
    LET area = triangleArea(a, b)
    PRINT "  Triangle sides:", a, ",", b, ",", c
    PRINT "  Triangle area:", area
    RETURN c
ENDFUNCTION

SUB printBanner(title, width)
    LET i = 0.0
    WHILE i < width
        PRINT "=";
        LET i = i + 1.0
    ENDWHILE
    PRINT ""
    PRINT title
    LET j = 0.0
    WHILE j < width
        PRINT "=";
        LET j = j + 1.0
    ENDWHILE
    PRINT ""
ENDSUB

PRINT "============================================"
PRINT "   JVM BASIC COMPREHENSIVE TEST SUITE"
PRINT "============================================"
PRINT ""

PRINT "--- 1. VARIABLES AND TYPES ---"
LET x = 42
LET y = 3.14
LET name = "JVM BASIC"
LET active = true
PRINT "Integer:", x
PRINT "Float:", y
PRINT "String:", name
PRINT "Boolean:", active
PRINT ""

PRINT "--- 2. ARITHMETIC OPERATIONS ---"
LET a = 10.0
LET b = 3.0
PRINT "a =", a, ", b =", b
PRINT "a + b =", a + b
PRINT "a - b =", a - b
PRINT "a * b =", a * b
PRINT "a / b =", a / b
PRINT "a MOD b =", a MOD b
PRINT "Unary: -a =", -a
PRINT ""

PRINT "--- 3. COMPARISON AND LOGIC ---"
PRINT "10 < 20:", 10.0 < 20.0
PRINT "15 > 10:", 15.0 > 10.0
PRINT "5 == 5:", 5.0 == 5.0
PRINT "7 <> 3:", 7.0 <> 3.0
PRINT "5 <= 5:", 5.0 <= 5.0
PRINT "8 >= 7:", 8.0 >= 7.0
PRINT ""

PRINT "--- 4. CONTROL STRUCTURES ---"
PRINT "IF/ELSEIF/ELSE test:"
LET score = 85.0
IF score >= 90.0 THEN
    PRINT "  Grade: A"
ELSEIF score >= 80.0 THEN
    PRINT "  Grade: B"
ELSEIF score >= 70.0 THEN
    PRINT "  Grade: C"
ELSE
    PRINT "  Grade: F"
ENDIF
PRINT ""

PRINT "FOR loop (1 to 5):"
FOR i = 1.0 TO 5.0
    PRINT "  i =", i
NEXT
PRINT ""

PRINT "WHILE loop (countdown from 3):"
LET count = 3.0
WHILE count > 0.0
    PRINT "  count =", count
    LET count = count - 1.0
ENDWHILE
PRINT ""

PRINT "--- 5. ARRAYS ---"
DIM numbers(5) = 0.0
LET numbers(0) = 10.0
LET numbers(1) = 20.0
LET numbers(2) = 30.0
LET numbers(3) = 40.0
LET numbers(4) = 50.0
PRINT "Array elements:"
FOR j = 0.0 TO 4.0
    PRINT "  numbers(", j, ") =", numbers(j)
NEXT
PRINT ""

PRINT "--- 6. BUILT-IN MATH FUNCTIONS ---"
PRINT "ABS(-15) =", ABS(-15.0)
PRINT "SQR(16) =", SQR(16.0)
PRINT "POW(2, 8) =", POW(2.0, 8.0)
PRINT "MIN(5, 3) =", MIN(5.0, 3.0)
PRINT "MAX(5, 3) =", MAX(5.0, 3.0)
PRINT "SIN(0) =", SIN(0.0)
PRINT "COS(0) =", COS(0.0)
PRINT "PI =", PI
PRINT "E =", E
PRINT ""

PRINT "--- 7. STRING FUNCTIONS ---"
LET text = "Hello World"
PRINT "Original:", text
PRINT "LEN:", LEN(text)
PRINT "UPPER:", UPPER(text)
PRINT "LOWER:", LOWER(text)
PRINT "LEFT(5):", LEFT(text, 5)
PRINT "RIGHT(5):", RIGHT(text, 5)
PRINT ""

PRINT "--- 8. USER-DEFINED FUNCTIONS ---"
PRINT "Circle area (radius=5):", circleArea(5.0)
PRINT "0°C in Fahrenheit:", celsiusToFahrenheit(0.0)
PRINT "100°C in Fahrenheit:", celsiusToFahrenheit(100.0)
PRINT "max(15, 42, 28) =", max3(15.0, 42.0, 28.0)
PRINT ""

PRINT "--- 9. SUB PROCEDURES ---"
CALL printBanner("SUCCESS", 20.0)
PRINT ""

PRINT "--- 10. NESTED FUNCTIONS ---"
PRINT "Pythagorean triple (3, 4):"
LET h = pythagoreanTriple(3.0, 4.0)
PRINT ""

PRINT "============================================"
PRINT "   ALL COMPREHENSIVE TESTS COMPLETE!"
PRINT "============================================"

