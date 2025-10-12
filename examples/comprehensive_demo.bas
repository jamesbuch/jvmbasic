FUNCTION gcd(a, b)
    IF b == 0.0 THEN
        RETURN a
    ELSE
        RETURN gcd(b, a MOD b)
    ENDIF
ENDFUNCTION

FUNCTION sumArray(arr, size)
    LET total = 0.0
    LET i = 0.0
    WHILE i < size
        LET total = total + arr(i)
        LET i = i + 1.0
    ENDWHILE
    RETURN total
ENDFUNCTION

FUNCTION average(arr, size)
    RETURN sumArray(arr, size) / size
ENDFUNCTION

SUB printBanner(text, width)
    LET i = 0.0
    WHILE i < width
        PRINT "=";
        LET i = i + 1.0
    ENDWHILE
    PRINT ""
    PRINT text
    LET j = 0.0
    WHILE j < width
        PRINT "=";
        LET j = j + 1.0
    ENDWHILE
    PRINT ""
ENDSUB

CALL printBanner("JVM BASIC - COMPREHENSIVE DEMONSTRATION", 60.0)
PRINT ""

PRINT "1. RECURSION:"
PRINT "   GCD(270, 192) =", gcd(270.0, 192.0)
PRINT "   GCD(1071, 462) =", gcd(1071.0, 462.0)
PRINT ""

PRINT "2. ARRAY PARAMETERS (with nested calls):"
DIM scores(5) = 0.0
LET scores(0) = 95.0
LET scores(1) = 87.0
LET scores(2) = 92.0
LET scores(3) = 78.0
LET scores(4) = 88.0
PRINT "   Scores: 95, 87, 92, 78, 88"
PRINT "   Sum:", sumArray(scores, 5.0)
PRINT "   Average:", average(scores, 5.0)
PRINT ""

PRINT "3. MATH FUNCTIONS:"
PRINT "   SQR(144) =", SQR(144.0)
PRINT "   POW(2, 10) =", POW(2.0, 10.0)
PRINT "   PI =", PI
PRINT "   SIN(PI/2) =", SIN(PI / 2.0)
PRINT ""

PRINT "4. STRING FUNCTIONS:"
LET text = "  Hello, JVM BASIC!  "
PRINT "   Original: '", text, "'"
PRINT "   Trimmed: '", TRIM(text), "'"
PRINT "   Upper:", UPPER(text)
PRINT "   Length:", LEN(text)
PRINT "   Left 5:", LEFT(TRIM(text), 5)
PRINT ""

PRINT "5. CONTROL STRUCTURES:"
LET x = 0.0
FOR i = 1.0 TO 5.0
    LET x = x + i
NEXT
PRINT "   Sum 1-5:", x
PRINT ""

PRINT "6. TYPE INFERENCE:"
PRINT "   Int + Float:", 5 + 3.14
PRINT "   Comparison:", 10.0 > 5.0
PRINT "   Boolean:", true
PRINT ""

PRINT "7. FORMAT STRINGS:"
LET name = "Alice"
LET age = 30.0
PRINT "   ", FORMAT("Name: {0}", name)
PRINT "   ", FORMATF("Age: {0} years", age)
PRINT ""

CALL printBanner("ALL FEATURES WORKING PERFECTLY!", 60.0)

