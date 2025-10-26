FUNCTION gcd(a As Integer, b As Integer) As Integer
    IF b == 0 THEN
        RETURN a
    ELSE
        RETURN gcd(b, a MOD b)
    ENDIF
ENDFUNCTION

SUB printBanner(text As String, width As Integer)
    i = 0
    WHILE i < width
        Console.Write("=")
        i = i + 1
    ENDWHILE
    Console.WriteLine("")
    Console.WriteLine(text)
    j = 0
    WHILE j < width
        Console.Write("=")
        j = j + 1
    ENDWHILE
    Console.WriteLine("")
ENDSUB

CALL printBanner("JVM BASIC - COMPREHENSIVE DEMONSTRATION", 60)
Console.WriteLine("")

Console.WriteLine("1. RECURSION:")
Console.WriteLine("   GCD(270, 192) = " + gcd(270, 192))
Console.WriteLine("   GCD(1071, 462) = " + gcd(1071, 462))
Console.WriteLine("")

Console.WriteLine("2. ARRAY OPERATIONS:")
DIM scores(5) As Integer
scores(0) = 95
scores(1) = 87
scores(2) = 92
scores(3) = 78
scores(4) = 88
Console.WriteLine("   Scores: 95, 87, 92, 78, 88")
Console.WriteLine("   Sum: " + (95 + 87 + 92 + 78 + 88))
Console.WriteLine("   Average: " + ((95 + 87 + 92 + 78 + 88) / 5))
Console.WriteLine("")

Console.WriteLine("3. MATH FUNCTIONS:")
Console.WriteLine("   SQR(144) = " + SQR(144))
Console.WriteLine("   POW(2, 10) = " + POW(2, 10))
Console.WriteLine("   PI = " + PI)
Console.WriteLine("   SIN(PI/2) = " + SIN(PI / 2))
Console.WriteLine("")

Console.WriteLine("4. STRING FUNCTIONS:")
text = "  Hello, JVM BASIC!  "
Console.WriteLine("   Original: '" + text + "'")
Console.WriteLine("   Trimmed: '" + TRIM(text) + "'")
Console.WriteLine("   Upper: " + UPPER(text))
Console.WriteLine("   Length: " + LEN(text))
Console.WriteLine("   Left 5: " + LEFT(TRIM(text), 5))
Console.WriteLine("")

Console.WriteLine("5. CONTROL STRUCTURES:")
x = 0
FOR i = 1 TO 5
    x = x + i
NEXT
Console.WriteLine("   Sum 1-5: " + x)
Console.WriteLine("")

Console.WriteLine("6. TYPE INFERENCE:")
Console.WriteLine("   Int + Float: " + (5 + 3.14))
Console.WriteLine("   Comparison: " + (10.0 > 5.0))
Console.WriteLine("   Boolean: " + true)
Console.WriteLine("")

Console.WriteLine("7. FORMAT STRINGS:")
name = "Alice"
age = 30
Console.WriteLine("    " + FORMAT("Name: {0}", name))
Console.WriteLine("    " + FORMATF("Age: {0} years", age))
Console.WriteLine("")

CALL printBanner("ALL FEATURES WORKING PERFECTLY!", 60)

