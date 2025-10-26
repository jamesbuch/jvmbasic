FUNCTION circleArea(radius As Integer) As Integer
    RETURN PI * radius * radius
ENDFUNCTION

FUNCTION celsiusToFahrenheit(c As Integer) As Integer
    RETURN c * 9.0 / 5.0 + 32.0
ENDFUNCTION

FUNCTION max3(a As Integer, b As Integer, c As Integer) As Integer
    temp = a
    IF b > temp THEN
        temp = b
    ENDIF
    IF c > temp THEN
        temp = c
    ENDIF
    RETURN temp
ENDFUNCTION

FUNCTION hypotenuse(a As Integer, b As Integer) As Integer
    RETURN SQR(a * a + b * b)
ENDFUNCTION

FUNCTION triangleArea(base As Integer, height As Integer) As Integer
    RETURN base * height / 2.0
ENDFUNCTION

FUNCTION pythagoreanTriple(a As Integer, b As Integer) As Integer
    c = hypotenuse(a, b)
    area = triangleArea(a, b)
    Console.WriteLine("  Triangle sides: " + a + "," + b + "," + c)
    Console.WriteLine("  Triangle area: " + area)
    RETURN c
ENDFUNCTION

SUB printBanner(title As String, width As Integer)
    i = 0
    WHILE i < width
        Console.Write("=")
        i = i + 1
    ENDWHILE
    Console.WriteLine("")
    Console.WriteLine(title)
    j = 0
    WHILE j < width
        Console.Write("=")
        j = j + 1
    ENDWHILE
    Console.WriteLine("")
ENDSUB

Console.WriteLine("============================================")
Console.WriteLine("   JVM BASIC COMPREHENSIVE TEST SUITE")
Console.WriteLine("============================================")
Console.WriteLine("")

Console.WriteLine("--- 1. VARIABLES AND TYPES ---")
x = 42
y = 3.14
name = "JVM BASIC"
active = true
Console.WriteLine("Integer: " + x)
Console.WriteLine("Float: " + y)
Console.WriteLine("String: " + name)
Console.WriteLine("Boolean: " + active)
Console.WriteLine("")

Console.WriteLine("--- 2. ARITHMETIC OPERATIONS ---")
a = 10.0
b = 3.0
Console.WriteLine("a = " + a + ", b = " + b)
Console.WriteLine("a + b = " + (a + b))
Console.WriteLine("a - b = " + (a - b))
Console.WriteLine("a * b = " + (a * b))
Console.WriteLine("a / b = " + (a / b))
Console.WriteLine("a MOD b = " + (a MOD b))
Console.WriteLine("Unary: -a = " + (-a))
Console.WriteLine("")

Console.WriteLine("--- 3. COMPARISON AND LOGIC ---")
Console.WriteLine("10 < 20: " + (10.0 < 20.0))
Console.WriteLine("15 > 10: " + (15.0 > 10.0))
Console.WriteLine("5 == 5: " + (5.0 == 5.0))
Console.WriteLine("7 <> 3: " + (7.0 <> 3.0))
Console.WriteLine("5 <= 5: " + (5.0 <= 5.0))
Console.WriteLine("8 >= 7: " + (8.0 >= 7.0))
Console.WriteLine("")

Console.WriteLine("--- 4. CONTROL STRUCTURES ---")
Console.WriteLine("IF/ELSEIF/ELSE test:")
score = 85.0
IF score >= 90.0 THEN
    Console.WriteLine("  Grade: A")
ELSEIF score >= 80.0 THEN
    Console.WriteLine("  Grade: B")
ELSEIF score >= 70.0 THEN
    Console.WriteLine("  Grade: C")
ELSE
    Console.WriteLine("  Grade: F")
ENDIF
Console.WriteLine("")

Console.WriteLine("FOR loop (1 to 5):")
FOR i = 1 TO 5
    Console.WriteLine("  i = " + i)
NEXT
Console.WriteLine("")

Console.WriteLine("WHILE loop (countdown from 3):")
count = 3
WHILE count > 0
    Console.WriteLine("  count = " + count)
    count = count - 1
ENDWHILE
Console.WriteLine("")

Console.WriteLine("--- 5. ARRAYS ---")
DIM numbers(5) As Integer
numbers(0) = 10
numbers(1) = 20
numbers(2) = 30
numbers(3) = 40
numbers(4) = 50
Console.WriteLine("Array elements:")
FOR j = 0 TO 4
    Console.WriteLine("  numbers(" + j + ") = " + numbers(j))
NEXT
Console.WriteLine("")

Console.WriteLine("--- 6. BUILT-IN MATH FUNCTIONS ---")
Console.WriteLine("ABS(-15) = " + ABS(-15.0))
Console.WriteLine("SQR(16) = " + SQR(16.0))
Console.WriteLine("POW(2, 8) = " + POW(2.0, 8.0))
Console.WriteLine("MIN(5, 3) = " + MIN(5.0, 3.0))
Console.WriteLine("MAX(5, 3) = " + MAX(5.0, 3.0))
Console.WriteLine("SIN(0) = " + SIN(0.0))
Console.WriteLine("COS(0) = " + COS(0.0))
Console.WriteLine("PI = " + PI)
Console.WriteLine("E = " + E)
Console.WriteLine("")

Console.WriteLine("--- 7. STRING FUNCTIONS ---")
text = "Hello World"
Console.WriteLine("Original: " + text)
Console.WriteLine("LEN: " + LEN(text))
Console.WriteLine("UPPER: " + UPPER(text))
Console.WriteLine("LOWER: " + LOWER(text))
Console.WriteLine("LEFT(s, 5): " + LEFT(text, 5))
Console.WriteLine("RIGHT(s, 5): " + RIGHT(text, 5))
Console.WriteLine("")

Console.WriteLine("--- 8. USER-DEFINED FUNCTIONS ---")
Console.WriteLine("Circle area (radius=5): " + circleArea(5))
Console.WriteLine("0°C in Fahrenheit: " + celsiusToFahrenheit(0))
Console.WriteLine("100°C in Fahrenheit: " + celsiusToFahrenheit(100))
Console.WriteLine("max(15, 42, 28) = " + max3(15, 42, 28))
Console.WriteLine("")

Console.WriteLine("--- 9. SUB PROCEDURES ---")
CALL printBanner("SUCCESS", 20)
Console.WriteLine("")

Console.WriteLine("--- 10. NESTED FUNCTIONS ---")
Console.WriteLine("Pythagorean triple (3, 4):")
h = pythagoreanTriple(3, 4)
Console.WriteLine("")

Console.WriteLine("============================================")
Console.WriteLine("   ALL COMPREHENSIVE TESTS COMPLETE!")
Console.WriteLine("============================================")

