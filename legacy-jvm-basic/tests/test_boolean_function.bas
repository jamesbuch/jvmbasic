' Test Boolean return type

FUNCTION IsPositive(x As Float) As Boolean
    IF x > 0 THEN
        RETURN true
    ELSE
        RETURN false
    ENDIF
ENDFUNCTION

DIM result As Boolean
DIM result2 As Boolean
result = IsPositive(5.0)
result2 = IsPositive(-3.0)

Console.WriteLine("Is 5 positive? " + result)
Console.WriteLine("Is -3 positive? " + result2)
Console.WriteLine("Boolean function test complete")

