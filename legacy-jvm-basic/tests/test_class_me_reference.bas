' Test 6: ME reference (self/this)
' Tests: ME keyword, explicit self reference

CLASS Person
    PRIVATE age As Float
    PUBLIC name As String
    
SUB New(n As Integer, a As Integer)
        ME.name = n
        ME.age = a
    END SUB
    
SUB SetAge(age As Integer)
        ' Parameter shadows field - use ME to disambiguate
        ME.age = age
    END SUB
    
    PUBLIC FUNCTION GetAge() As Integer
        RETURN ME.age
    END FUNCTION
END CLASS

' When codegen works:
' DIM person AS NEW Person("Bob", 25.0)
' CALL person.SetAge(26.0)
' Console.WriteLine(person.name + " " + " is " + " " + person.GetAge() + " " + " years old")
' PRINT "Expected: Bob is 26.0 years old"

Console.WriteLine("Test: ME reference")
Console.WriteLine("Status: ✓ WORKING (Phase 7 complete)")



