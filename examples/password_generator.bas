FUNCTION generatePassword(length As Integer) As String
    password = ""
    count = 0
    WHILE count < length
        rand = RNDINT(0, 2)
        IF rand == 0 THEN
            password = password + CHR(RNDINT(97, 122))
        ELSEIF rand == 1 THEN
            password = password + CHR(RNDINT(65, 90))
        ELSE
            password = password + CHR(RNDINT(48, 57))
        ENDIF
        count = count + 1
    ENDWHILE
    RETURN password
ENDFUNCTION

FUNCTION generateSecurePassword(length As Integer) As String
    password = ""
    count = 0
    WHILE count < length
        rand = RNDINT(0, 3)
        IF rand == 0 THEN
            password = password + CHR(RNDINT(97, 122))
        ELSEIF rand == 1 THEN
            password = password + CHR(RNDINT(65, 90))
        ELSEIF rand == 2 THEN
            password = password + CHR(RNDINT(48, 57))
        ELSE
            password = password + "!"
        ENDIF
        count = count + 1
    ENDWHILE
    RETURN password
ENDFUNCTION

Console.WriteLine("================================================")
Console.WriteLine("  PASSWORD GENERATOR")
Console.WriteLine("================================================")
Console.WriteLine("")

Console.WriteLine("Generating passwords...")
Console.WriteLine("")

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