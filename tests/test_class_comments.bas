' Test 7: Modern VB-style comments
' Tests: Apostrophe comments in classes

CLASS Example
    ' This is a VB-style comment
    PUBLIC value AS FLOAT
    
    ' Constructor with comment
    PUBLIC SUB New(v AS FLOAT)
        value = v  ' Inline comment
    END SUB
    
    PUBLIC FUNCTION Double() AS FLOAT
        ' Return double the value
        RETURN value * 2.0
    END FUNCTION
END CLASS

REM Old-style comment still works
PRINT "Test: Both comment styles"
PRINT "Status: Comments work! ✓"


