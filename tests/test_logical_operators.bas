' Test Logical Operators (AND, OR, NOT, XOR)
Console.WriteLine("=== Logical Operators Test ===")

' Test AND
IF 1 == 1 AND 2 == 2 THEN
    Console.WriteLine("AND test 1: PASS")
ELSE
    Console.WriteLine("ERROR: AND test 1 failed")
ENDIF

IF 1 == 1 AND 2 == 999 THEN
    Console.WriteLine("ERROR: AND test 2 should be false")
ELSE
    Console.WriteLine("AND test 2: PASS (correctly false)")
ENDIF

' Test OR
IF 1 == 1 OR 2 == 999 THEN
    Console.WriteLine("OR test 1: PASS")
ELSE
    Console.WriteLine("ERROR: OR test 1 failed")
ENDIF

IF 1 == 999 OR 2 == 999 THEN
    Console.WriteLine("ERROR: OR test 2 should be false")
ELSE
    Console.WriteLine("OR test 2: PASS (correctly false)")
ENDIF

' Test NOT
IF NOT 1 == 999 THEN
    Console.WriteLine("NOT test 1: PASS")
ELSE
    Console.WriteLine("ERROR: NOT test 1 failed")
ENDIF

' Test complex expression
x = 10
y = 20
z = 30

IF x < y AND y < z THEN
    Console.WriteLine("Complex AND: PASS (x < y < z)")
ELSE
    Console.WriteLine("ERROR: Complex AND failed")
ENDIF

IF x > 100 OR y == 20 THEN
    Console.WriteLine("Complex OR: PASS (y=20 is true)")
ELSE
    Console.WriteLine("ERROR: Complex OR failed")
ENDIF

' Test NOT with comparisons
IF NOT x > 100 THEN
    Console.WriteLine("NOT with comparison: PASS")
ELSE
    Console.WriteLine("ERROR: NOT with comparison failed")
ENDIF

' Test XOR
a = 1
b = 0
IF a == 1 XOR b == 1 THEN
Console.WriteLine("XOR test 1: PASS (one true, one false)")
ELSE
    Console.WriteLine("ERROR: XOR test 1 failed")
ENDIF

IF a == 1 XOR a == 1 THEN
    Console.WriteLine("ERROR: XOR test 2 should be false (both true)")
ELSE
    Console.WriteLine("XOR test 2: PASS (both true = false)")
ENDIF

Console.WriteLine("=== All Logical Operator Tests Complete ===")

