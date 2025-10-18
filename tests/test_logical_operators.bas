REM Test Logical Operators (AND, OR, NOT, XOR)
PRINT "=== Logical Operators Test ==="

REM Test AND
IF 1 == 1 AND 2 == 2 THEN
    PRINT "AND test 1: PASS"
ELSE
    PRINT "ERROR: AND test 1 failed"
ENDIF

IF 1 == 1 AND 2 == 999 THEN
    PRINT "ERROR: AND test 2 should be false"
ELSE
    PRINT "AND test 2: PASS (correctly false)"
ENDIF

REM Test OR
IF 1 == 1 OR 2 == 999 THEN
    PRINT "OR test 1: PASS"
ELSE
    PRINT "ERROR: OR test 1 failed"
ENDIF

IF 1 == 999 OR 2 == 999 THEN
    PRINT "ERROR: OR test 2 should be false"
ELSE
    PRINT "OR test 2: PASS (correctly false)"
ENDIF

REM Test NOT
IF NOT 1 == 999 THEN
    PRINT "NOT test 1: PASS"
ELSE
    PRINT "ERROR: NOT test 1 failed"
ENDIF

REM Test complex expression
LET x = 10
LET y = 20
LET z = 30

IF x < y AND y < z THEN
    PRINT "Complex AND: PASS (x < y < z)"
ELSE
    PRINT "ERROR: Complex AND failed"
ENDIF

IF x > 100 OR y == 20 THEN
    PRINT "Complex OR: PASS (y=20 is true)"
ELSE
    PRINT "ERROR: Complex OR failed"
ENDIF

REM Test NOT with comparisons
IF NOT x > 100 THEN
    PRINT "NOT with comparison: PASS"
ELSE
    PRINT "ERROR: NOT with comparison failed"
ENDIF

REM Test XOR
LET a = 1
LET b = 0
IF a == 1 XOR b == 1 THEN
    PRINT "XOR test 1: PASS (one true, one false)"
ELSE
    PRINT "ERROR: XOR test 1 failed"
ENDIF

IF a == 1 XOR a == 1 THEN
    PRINT "ERROR: XOR test 2 should be false (both true)"
ELSE
    PRINT "XOR test 2: PASS (both true = false)"
ENDIF

PRINT "=== All Logical Operator Tests Complete ==="

