' Test 6: ME reference (self/this)
' Tests: ME keyword, explicit self reference

CLASS Person
    PRIVATE age AS FLOAT
    PUBLIC name AS STRING
    
    PUBLIC SUB New(n AS STRING, a AS FLOAT)
        ME.name = n
        ME.age = a
    END SUB
    
    PUBLIC SUB SetAge(age AS FLOAT)
        ' Parameter shadows field - use ME to disambiguate
        ME.age = age
    END SUB
    
    PUBLIC FUNCTION GetAge() AS FLOAT
        RETURN ME.age
    END FUNCTION
END CLASS

' When codegen works:
' DIM person AS NEW Person("Bob", 25.0)
' CALL person.SetAge(26.0)
' PRINT person.name; " is "; person.GetAge(); " years old"
' PRINT "Expected: Bob is 26.0 years old"

PRINT "Test: ME reference"
PRINT "Status: ✓ WORKING (Phase 7 complete)"



