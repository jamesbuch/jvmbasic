REM Test Advanced String Functions from Phase 8
PRINT "=== Advanced String Functions Test ==="

REM Test CONCAT variations
LET s1 = CONCAT("Hello", " World")
PRINT "CONCAT: "; s1

LET s2 = CONCAT3("A", "B", "C")
PRINT "CONCAT3: "; s2

REM Test REPEAT
LET stars = REPEAT("*", 20)
PRINT "REPEAT: "; stars

REM Test padding
LET num1 = "7"
LET num2 = "42"
LET num3 = "100"
PRINT "PADLEFT numbers:"
PRINT "  "; PADLEFT(num1, 5)
PRINT "  "; PADLEFT(num2, 5)
PRINT "  "; PADLEFT(num3, 5)

PRINT "PADRIGHT numbers:"
PRINT "  "; PADRIGHT(num1, 5); " <"
PRINT "  "; PADRIGHT(num2, 5); " <"
PRINT "  "; PADRIGHT(num3, 5); " <"

REM Test SUBSTRING variations
LET text = "The Quick Brown Fox"
PRINT "Original: "; text
PRINT "SUBSTRING(6): "; SUBSTRING(text, 6)
PRINT "SUBSTRINGLEN(4, 5): "; SUBSTRINGLEN(text, 4, 5)

REM Test string comparison functions
LET cmp1 = STRCMP("apple", "banana")
LET cmp2 = STRCMP("zebra", "apple")
LET cmp3 = STRCMP("test", "test")
PRINT "STRCMP results:"
PRINT "  apple vs banana: "; cmp1; " (negative)"
PRINT "  zebra vs apple: "; cmp2; " (positive)"
PRINT "  test vs test: "; cmp3; " (zero)"

REM Test case-insensitive comparison
LET cmp4 = STRICMP("Hello", "HELLO")
PRINT "STRICMP Hello vs HELLO: "; cmp4; " (should be 0)"

REM Test EQUALS
IF EQUALS("test", "test") THEN
    PRINT "EQUALS test=test: TRUE"
ENDIF

IF EQUALSIGNORECASE("Test", "TEST") THEN
    PRINT "EQUALSIGNORECASE Test=TEST: TRUE"
ENDIF

REM Test CHAR and CHARCODE
LET word = "BASIC"
PRINT "Word: "; word
PRINT "CHAR(0): "; CHAR(word, 0)
PRINT "CHAR(1): "; CHAR(word, 1)
PRINT "CHARCODE(0): "; CHARCODE(word, 0)
PRINT "CHARCODE(1): "; CHARCODE(word, 1)

PRINT "=== All Tests Complete ==="

