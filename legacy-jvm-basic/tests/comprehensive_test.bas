Console.WriteLine("Testing all features...")

Dim intVal As Integer
Dim floatVal As Single
Dim strVal As String
Dim boolVal As Boolean
Dim sum As Integer
Dim prod As Single
Dim cmp1 As Boolean
Dim cmp2 As Boolean
Dim cmp3 As Boolean
Dim cmp4 As Boolean

intVal = 42
floatVal = 3.14
strVal = "Hello"
boolVal = TrUe

Console.WriteLine(intVal)
Console.WriteLine(floatVal)
Console.WriteLine(strVal)
Console.WriteLine(boolVal)

sum = intVal + 8
prod = floatVal * 2
Console.WriteLine(sum)
Console.WriteLine(prod)

cmp1 = intVal > 40
cmp2 = floatVal < 3.2
cmp3 = strVal == "Hello"
cmp4 = boolVal <> false

Console.WriteLine(cmp1)
Console.WriteLine(cmp2)
Console.WriteLine(cmp3)
Console.WriteLine(cmp4)

IF intVal >= 42 THEN
    Console.WriteLine("Test 1 passed")
ENDIF

IF floatVal <= 3.0 THEN
    Console.WriteLine("Test 2 failed")
ELSE
    Console.WriteLine("Test 2 passed")
ENDIF

IF intVal == 0 THEN
    Console.WriteLine("Test 3 failed")
ELSEIF intVal < 50 THEN
    Console.WriteLine("Test 3 passed")
ELSE
    Console.WriteLine("Test 3 failed")
ENDIF

IF boolVal THEN
    IF strVal == "Hello" THEN
        Console.WriteLine("Nested IF works")
    ENDIF
ENDIF

Console.WriteLine("All tests completed!")

