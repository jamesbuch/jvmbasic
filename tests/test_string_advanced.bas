REM Test Advanced String Functions from Phase 8
Console.WriteLine("=== Advanced String Functions Test ===")

REM Test CONCAT variations
s1 = CONCAT("Hello", " World")
Console.WriteLine("CONCAT: " + s1)

s2 = CONCAT3("A", "B", "C")
Console.WriteLine("CONCAT3: " + s2)

REM Test REPEAT
stars = REPEAT("*", 20)
Console.WriteLine("REPEAT: " + stars)

REM Test padding
num1 = "7"
num2 = "42"
num3 = "100"
Console.WriteLine("PADLEFT numbers:")
Console.WriteLine("  " + PADLEFT(num1, 5))
Console.WriteLine("  " + PADLEFT(num2, 5))
Console.WriteLine("  " + PADLEFT(num3, 5))

Console.WriteLine("PADRIGHT numbers:")
Console.WriteLine("  " + PADRIGHT(num1, 5) + " <")
Console.WriteLine("  " + PADRIGHT(num2, 5) + " <")
Console.WriteLine("  " + PADRIGHT(num3, 5) + " <")

REM Test SUBSTRING variations
text = "The Quick Brown Fox"
Console.WriteLine("Original: " + text)
Console.WriteLine("SUBSTRING(s, 6): " + SUBSTRING(text, 6))
Console.WriteLine("SUBSTRINGLEN(4, 5): " + SUBSTRINGLEN(text, 4, 5))

REM Test string comparison functions
cmp1 = STRCMP("apple", "banana")
cmp2 = STRCMP("zebra", "apple")
cmp3 = STRCMP("test", "test")
Console.WriteLine("STRCMP results:")
Console.WriteLine("  apple vs banana: " + cmp1 + " (negative)")
Console.WriteLine("  zebra vs apple: " + cmp2 + " (positive)")
Console.WriteLine("  test vs test: " + cmp3 + " (zero)")

REM Test case-insensitive comparison
cmp4 = STRICMP("Hello", "HELLO")
Console.WriteLine("STRICMP Hello vs HELLO: " + cmp4 + " (should be 0)")

REM Test EQUALS
IF EQUALS("test", "test") THEN
    Console.WriteLine("EQUALS test=test: TRUE")
ENDIF

IF EQUALSIGNORECASE("Test", "TEST") THEN
    Console.WriteLine("EQUALSIGNORECASE Test=TEST: TRUE")
ENDIF

REM Test CHAR and CHARCODE
word = "BASIC"
Console.WriteLine("Word: " + word)
Console.WriteLine("CHAR(s, 0): " + CHAR(word, 0))
Console.WriteLine("CHAR(s, 1): " + CHAR(word, 1))
Console.WriteLine("CHARCODE(s, 0): " + CHARCODE(word, 0))
Console.WriteLine("CHARCODE(s, 1): " + CHARCODE(word, 1))

Console.WriteLine("=== All Tests Complete ===")

