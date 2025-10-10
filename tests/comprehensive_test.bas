PRINT "Testing all features...";

LET intVal = 42;
LET floatVal = 3.14;
LET strVal = "Hello";
LET boolVal = TrUe;

PRINT intVal;
PRINT floatVal;
PRINT strVal;
PRINT boolVal;

LET sum = intVal + 8;
LET prod = floatVal * 2;
PRINT sum;
PRINT prod;

LET cmp1 = intVal > 40;
LET cmp2 = floatVal < 3.2;
LET cmp3 = strVal == "Hello";
LET cmp4 = boolVal <> false;

PRINT cmp1;
PRINT cmp2;
PRINT cmp3;
PRINT cmp4;

IF intVal >= 42 THEN
    PRINT "Test 1 passed";
ENDIF

IF floatVal <= 3.0 THEN
    PRINT "Test 2 failed";
ELSE
    PRINT "Test 2 passed";
ENDIF

IF intVal == 0 THEN
    PRINT "Test 3 failed";
ELSEIF intVal < 50 THEN
    PRINT "Test 3 passed";
ELSE
    PRINT "Test 3 failed";
ENDIF

IF boolVal THEN
    IF strVal == "Hello" THEN
        PRINT "Nested IF works";
    ENDIF
ENDIF

PRINT "All tests completed!";

