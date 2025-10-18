' Test 2: CLASS with constructor
' Tests: Constructor declaration (SUB New), constructor parameters

CLASS Point
    PUBLIC x AS FLOAT
    PUBLIC y AS FLOAT
    
    PUBLIC SUB New(px AS FLOAT, py AS FLOAT)
        x = px
        y = py
    END SUB
END CLASS

' Constructor parsing works, but full method body generation is Phase 8
' DIM p AS NEW Point(3.0, 4.0)
' PRINT "Point: ("; p.x; ", "; p.y; ")"

PRINT "Test: CLASS with constructor"
PRINT "Status: ✓ PARSING WORKS - Full methods in Phase 8"


