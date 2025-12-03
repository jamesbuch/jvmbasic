' Test Phase 8: Advanced String Functions
Console.WriteLine("=== Phase 8 String Functions Test ===")

' Test REPLACE
s = "Hello World"
s2 = REPLACE(s, "World", "JVM BASIC")
Console.WriteLine("REPLACE: " + s2)
IF EQUALS(s2, "Hello JVM BASIC") THEN
    ' OK
ELSE
    Console.WriteLine("ERROR: REPLACE failed")
ENDIF

' Test REPLACEALL
s = "foo bar foo baz foo"
s2 = REPLACEALL(s, "foo", "TEST")
Console.WriteLine("REPLACEALL: " + s2)
IF EQUALS(s2, "TEST bar TEST baz TEST") THEN
    ' OK
ELSE
    Console.WriteLine("ERROR: REPLACEALL failed")
ENDIF

' Test STARTSWITH and ENDSWITH
s = "Hello World"
IF STARTSWITH(s, "Hello") THEN
    Console.WriteLine("STARTSWITH: OK")
ELSE
    Console.WriteLine("ERROR: STARTSWITH failed")
ENDIF

IF ENDSWITH(s, "World") THEN
    Console.WriteLine("ENDSWITH: OK")
ELSE
    Console.WriteLine("ERROR: ENDSWITH failed")
ENDIF

' Test INDEXOF and LASTINDEXOF
s = "foo bar foo baz"
idx = INDEXOF(s, "foo")
Console.WriteLine("INDEXOF: " + idx)
IF idx <> 0 THEN
    Console.WriteLine("ERROR: INDEXOF should be 0")
ENDIF

idx = LASTINDEXOF(s, "foo")
Console.WriteLine("LASTINDEXOF: " + idx)
IF idx <> 8 THEN
    Console.WriteLine("ERROR: LASTINDEXOF should be 8")
ENDIF

' Test CONCAT
s = CONCAT("Hello", " World")
Console.WriteLine("CONCAT: " + s)

s = CONCAT3("A", "B", "C")
Console.WriteLine("CONCAT3: " + s)

' Test REPEAT
s = REPEAT("*", 5)
Console.WriteLine("REPEAT: " + s)
IF EQUALS(s, "*****") THEN
    ' OK
ELSE
    Console.WriteLine("ERROR: REPEAT failed")
ENDIF

' Test PADLEFT and PADRIGHT
s = PADLEFT("42", 5)
Console.WriteLine("PADLEFT: [" + s + "]")
IF LEN(s) <> 5 THEN
    Console.WriteLine("ERROR: PADLEFT length should be 5")
ENDIF

s = PADRIGHT("42", 5)
Console.WriteLine("PADRIGHT: [" + s + "]")
IF LEN(s) <> 5 THEN
    Console.WriteLine("ERROR: PADRIGHT length should be 5")
ENDIF

' Test SUBSTRING
s = "Hello World"
s2 = SUBSTRING(s, 6)
Console.WriteLine("SUBSTRING: " + s2)
IF EQUALS(s2, "World") THEN
    ' OK
ELSE
    Console.WriteLine("ERROR: SUBSTRING failed")
ENDIF

s2 = SUBSTRINGLEN(s, 0, 5)
Console.WriteLine("SUBSTRINGLEN: " + s2)
IF EQUALS(s2, "Hello") THEN
    ' OK
ELSE
    Console.WriteLine("ERROR: SUBSTRINGLEN failed")
ENDIF

' Test STRCMP
cmp = STRCMP("abc", "xyz")
Console.WriteLine("STRCMP abc vs xyz: " + cmp)
IF cmp >= 0 THEN
    Console.WriteLine("ERROR: STRCMP should be negative")
ENDIF

' Test EQUALS and EQUALSIGNORECASE
IF EQUALS("Test", "Test") THEN
    Console.WriteLine("EQUALS: OK")
ELSE
    Console.WriteLine("ERROR: EQUALS failed")
ENDIF

IF EQUALSIGNORECASE("Test", "test") THEN
    Console.WriteLine("EQUALSIGNORECASE: OK")
ELSE
    Console.WriteLine("ERROR: EQUALSIGNORECASE failed")
ENDIF

' Test CHAR and CHARCODE
s = "Hello"
c = CHAR(s, 0)
Console.WriteLine("CHAR at 0: " + c)
IF EQUALS(c, "H") THEN
    ' OK
ELSE
    Console.WriteLine("ERROR: CHAR failed")
ENDIF

code = CHARCODE(s, 0)
Console.WriteLine("CHARCODE at 0: " + code)
IF code <> 72 THEN
    Console.WriteLine("ERROR: CHARCODE should be 72 (H)")
ENDIF

Console.WriteLine("=== All String Tests Complete ===")

