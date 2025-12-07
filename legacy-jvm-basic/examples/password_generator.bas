' Password Generator
' Generates random passwords of various lengths

Function generatePassword(length As Integer) As String
    Dim password As String
    Dim count As Integer
    Dim rand As Integer
    password = ""
    count = 0
    While count < length
        rand = RNDINT(0, 2)
        If rand == 0 Then
            password = password + CHR(RNDINT(97, 122))
        ElseIf rand == 1 Then
            password = password + CHR(RNDINT(65, 90))
        Else
            password = password + CHR(RNDINT(48, 57))
        EndIf
        count = count + 1
    EndWhile
    Return password
EndFunction

Function generateSecurePassword(length As Integer) As String
    Dim password As String
    Dim count As Integer
    Dim rand As Integer
    password = ""
    count = 0
    While count < length
        rand = RNDINT(0, 3)
        If rand == 0 Then
            password = password + CHR(RNDINT(97, 122))
        ElseIf rand == 1 Then
            password = password + CHR(RNDINT(65, 90))
        ElseIf rand == 2 Then
            password = password + CHR(RNDINT(48, 57))
        Else
            password = password + "!"
        EndIf
        count = count + 1
    EndWhile
    Return password
EndFunction

Console.WriteLine("================================================")
Console.WriteLine("  PASSWORD GENERATOR")
Console.WriteLine("================================================")
Console.WriteLine("")

Console.WriteLine("Generating passwords...")
Console.WriteLine("")

Dim password1 As String
Dim password2 As String
Dim password3 As String
Dim password4 As String

password1 = generatePassword(8)
password2 = generatePassword(12)
password3 = generateSecurePassword(10)
password4 = generateSecurePassword(16)

Console.WriteLine("8-character password: " + password1)
Console.WriteLine("12-character password: " + password2)
Console.WriteLine("10-character secure password: " + password3)
Console.WriteLine("16-character secure password: " + password4)
Console.WriteLine("")

Console.WriteLine("Password strength analysis:")
Console.WriteLine("Length 8: " + LEN(password1) + " characters")
Console.WriteLine("Length 12: " + LEN(password2) + " characters")
Console.WriteLine("Length 10: " + LEN(password3) + " characters")
Console.WriteLine("Length 16: " + LEN(password4) + " characters")
Console.WriteLine("")

Console.WriteLine("================================================")
Console.WriteLine("  Password generation complete!")
Console.WriteLine("================================================")
