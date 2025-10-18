REM Test Phase 8: Advanced String Functions
PRINT "=== Phase 8 String Functions Test ==="

REM Test REPLACE
LET s = "Hello World"
LET s2 = REPLACE(s, "World", "JVM BASIC")
PRINT "REPLACE: "; s2
IF EQUALS(s2, "Hello JVM BASIC") THEN
    REM OK
ELSE
    PRINT "ERROR: REPLACE failed"
ENDIF

REM Test REPLACEALL
LET s = "foo bar foo baz foo"
LET s2 = REPLACEALL(s, "foo", "TEST")
PRINT "REPLACEALL: "; s2
IF EQUALS(s2, "TEST bar TEST baz TEST") THEN
    REM OK
ELSE
    PRINT "ERROR: REPLACEALL failed"
ENDIF

REM Test STARTSWITH and ENDSWITH
LET s = "Hello World"
IF STARTSWITH(s, "Hello") THEN
    PRINT "STARTSWITH: OK"
ELSE
    PRINT "ERROR: STARTSWITH failed"
ENDIF

IF ENDSWITH(s, "World") THEN
    PRINT "ENDSWITH: OK"
ELSE
    PRINT "ERROR: ENDSWITH failed"
ENDIF

REM Test INDEXOF and LASTINDEXOF
LET s = "foo bar foo baz"
LET idx = INDEXOF(s, "foo")
PRINT "INDEXOF: "; idx
IF idx <> 0 THEN
    PRINT "ERROR: INDEXOF should be 0"
ENDIF

LET idx = LASTINDEXOF(s, "foo")
PRINT "LASTINDEXOF: "; idx
IF idx <> 8 THEN
    PRINT "ERROR: LASTINDEXOF should be 8"
ENDIF

REM Test CONCAT
LET s = CONCAT("Hello", " World")
PRINT "CONCAT: "; s

LET s = CONCAT3("A", "B", "C")
PRINT "CONCAT3: "; s

REM Test REPEAT
LET s = REPEAT("*", 5)
PRINT "REPEAT: "; s
IF EQUALS(s, "*****") THEN
    REM OK
ELSE
    PRINT "ERROR: REPEAT failed"
ENDIF

REM Test PADLEFT and PADRIGHT
LET s = PADLEFT("42", 5)
PRINT "PADLEFT: ["; s; "]"
IF LEN(s) <> 5 THEN
    PRINT "ERROR: PADLEFT length should be 5"
ENDIF

LET s = PADRIGHT("42", 5)
PRINT "PADRIGHT: ["; s; "]"
IF LEN(s) <> 5 THEN
    PRINT "ERROR: PADRIGHT length should be 5"
ENDIF

REM Test SUBSTRING
LET s = "Hello World"
LET s2 = SUBSTRING(s, 6)
PRINT "SUBSTRING: "; s2
IF EQUALS(s2, "World") THEN
    REM OK
ELSE
    PRINT "ERROR: SUBSTRING failed"
ENDIF

LET s2 = SUBSTRINGLEN(s, 0, 5)
PRINT "SUBSTRINGLEN: "; s2
IF EQUALS(s2, "Hello") THEN
    REM OK
ELSE
    PRINT "ERROR: SUBSTRINGLEN failed"
ENDIF

REM Test STRCMP
LET cmp = STRCMP("abc", "xyz")
PRINT "STRCMP abc vs xyz: "; cmp
IF cmp >= 0 THEN
    PRINT "ERROR: STRCMP should be negative"
ENDIF

REM Test EQUALS and EQUALSIGNORECASE
IF EQUALS("Test", "Test") THEN
    PRINT "EQUALS: OK"
ELSE
    PRINT "ERROR: EQUALS failed"
ENDIF

IF EQUALSIGNORECASE("Test", "test") THEN
    PRINT "EQUALSIGNORECASE: OK"
ELSE
    PRINT "ERROR: EQUALSIGNORECASE failed"
ENDIF

REM Test CHAR and CHARCODE
LET s = "Hello"
LET c = CHAR(s, 0)
PRINT "CHAR at 0: "; c
IF EQUALS(c, "H") THEN
    REM OK
ELSE
    PRINT "ERROR: CHAR failed"
ENDIF

LET code = CHARCODE(s, 0)
PRINT "CHARCODE at 0: "; code
IF code <> 72 THEN
    PRINT "ERROR: CHARCODE should be 72 (H)"
ENDIF

PRINT "=== All String Tests Complete ==="

