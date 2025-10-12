REM Test multi-argument PRINT with comma and semicolon
LET x = 42
LET y = 3.14
LET name = "Alice"

PRINT "Test 1: Basic values without semicolons"
PRINT x
PRINT y
PRINT name

PRINT "Test 2: Comma separator (with spaces)"
PRINT "x is", x, "and y is", y

PRINT "Test 3: Semicolon separator (no spaces)"
PRINT "x="; x; " y="; y

PRINT "Test 4: Mixed separators"
PRINT "Value:", x, "Pi:"; y

PRINT "Test 5: Trailing comma (no newline)"
PRINT "Loading",
PRINT "done"

PRINT "Test 6: Trailing semicolon (no newline)"
PRINT "Count: ";
PRINT x

PRINT "Test 7: Boolean values"
LET flag = true
PRINT "Flag is", flag

PRINT "Test 8: Multiple types"
PRINT x, y, name, flag

