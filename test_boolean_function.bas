Rem Test Boolean return type

Function IsPositive(x As Single) As Boolean
    If x > 0.0 Then
        Return true
    Else
        Return false
    End If
End Function

Dim result As Boolean = IsPositive(5.0)
Print "Is 5 positive? "; result

Dim result2 As Boolean = IsPositive(-3.0)
Print "Is -3 positive? "; result2

Print "Boolean function test complete"

