PRINT "=== Boolean Demo ===";
LET isTrue = true;
LET isFalse = FALSE;
PRINT isTrue;
PRINT isFalse;

PRINT "=== Comparisons ===";
LET x = 10;
LET y = 20;
PRINT x < y;
PRINT x > y;
PRINT x <= y;
PRINT x >= y;
PRINT x == y;
PRINT x <> y;

PRINT "=== Float Comparisons ===";
LET pi = 3.14;
LET e = 2.71;
PRINT pi > e;

PRINT "=== String Comparisons ===";
LET name1 = "Alice";
LET name2 = "Alice";
LET name3 = "Bob";
PRINT name1 == name2;
PRINT name1 <> name3;

PRINT "=== IF/THEN/ELSE ===";
IF x < y THEN
    PRINT "x is less than y";
ENDIF

IF x > y THEN
    PRINT "x is greater";
ELSE
    PRINT "x is not greater";
ENDIF

PRINT "=== ELSEIF Chains ===";
LET score = 85;
IF score >= 90 THEN
    PRINT "Grade A";
ELSEIF score >= 80 THEN
    PRINT "Grade B";
ELSEIF score >= 70 THEN
    PRINT "Grade C";
ELSE
    PRINT "Grade F";
ENDIF

PRINT "=== Done ===";

