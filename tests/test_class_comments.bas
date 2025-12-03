' Test 7: Modern VB-style comments
' Tests: Apostrophe comments in classes

CLASS Example
    ' This is a VB-style comment
    PUBLIC value As Float
    
    ' Constructor with comment
    SUB New(v As Float)
        value = v  ' Inline comment
    END SUB
    
    PUBLIC FUNCTION GetDouble() As Float
        ' Return double the value
        RETURN value * 2.0
    END FUNCTION
END CLASS

' Old-style comment still works
Console.WriteLine("Test: Both comment styles")
Console.WriteLine("Status: Comments work! ✓")



