' Modern VB-Style Secure Password Generator

Function GeneratePassword(length As Single) As String
    Dim charset(62) = ""
    Dim i As Single = 0.0
    
    ' Lowercase letters
    While i < 26.0
        charset(Int(i)) = Chr(97 + i)
        i = i + 1.0
    End While
    
    ' Uppercase letters
    i = 0.0
    While i < 26.0
        charset(Int(26 + i)) = Chr(65 + i)
        i = i + 1.0
    End While
    
    ' Digits
    i = 0.0
    While i < 10.0
        charset(Int(52 + i)) = Chr(48 + i)
        i = i + 1.0
    End While
    
    Dim password As String = ""
    Dim count As Single = 0.0
    While count < length
        password = password + charset(RndInt(0, 61))
        count = count + 1.0
    End While
    
    Return password
End Function

Function HasLowercase(pwd As String) As Boolean
    Dim i As Single = 0.0
    While i < Len(pwd)
        Dim c As Single = Asc(Mid(pwd, i, 1))
        If c >= 97 And c <= 122 Then
            Return true
        End If
        i = i + 1.0
    End While
    Return false
End Function

Function HasUppercase(pwd As String) As Boolean
    Dim i As Single = 0.0
    While i < Len(pwd)
        Dim c As Single = Asc(Mid(pwd, i, 1))
        If c >= 65 And c <= 90 Then
            Return true
        End If
        i = i + 1.0
    End While
    Return false
End Function

Function HasDigit(pwd As String) As Boolean
    Dim i As Single = 0.0
    While i < Len(pwd)
        Dim c As Single = Asc(Mid(pwd, i, 1))
        If c >= 48 And c <= 57 Then
            Return true
        End If
        i = i + 1.0
    End While
    Return false
End Function

Function IsStrongPassword(pwd As String) As Boolean
    If HasLowercase(pwd) And HasUppercase(pwd) And HasDigit(pwd) Then
        Return true
    End If
    Return false
End Function

' Main program
Console.WriteLine("================================================")
Console.WriteLine("  SECURE PASSWORD GENERATOR")
Console.WriteLine("================================================")
Console.WriteLine("")

Dim attempts As Single = 0.0
Dim pwd As String = ""

While attempts < 100.0
    pwd = GeneratePassword(12.0)
    attempts = attempts + 1.0
    If IsStrongPassword(pwd) Then
        Console.WriteLine($"Generated strong password (attempt {attempts}):")
        Console.WriteLine($"  {pwd}")
        Console.WriteLine("")
        
        Dim pwdLen As Single = Len(pwd)
        Dim hasLower As Boolean = HasLowercase(pwd)
        Dim hasUpper As Boolean = HasUppercase(pwd)
        Dim hasDigits As Boolean = HasDigit(pwd)
        
        Console.WriteLine($"Length: {pwdLen}")
        Console.WriteLine($"Has lowercase: {hasLower}")
        Console.WriteLine($"Has uppercase: {hasUpper}")
        Console.WriteLine($"Has digits: {hasDigits}")
        attempts = 1000.0  ' Exit loop
    End If
End While

Console.WriteLine("")
Console.WriteLine("Generating 5 random passwords:")
Dim i As Single = 1.0
While i <= 5.0
    Dim randomPwd As String = GeneratePassword(12.0)
    Console.WriteLine($"  Password {i}: {randomPwd}")
    i = i + 1.0
End While

Console.WriteLine("")
Console.WriteLine("================================================")
