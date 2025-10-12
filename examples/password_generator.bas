FUNCTION generatePassword(length)
    DIM charset(62) = ""
    
    LET i = 0.0
    WHILE i < 26.0
        LET charset(i) = CHR(97 + i)
        LET i = i + 1.0
    ENDWHILE
    
    LET i = 0.0
    WHILE i < 26.0
        LET charset(26 + i) = CHR(65 + i)
        LET i = i + 1.0
    ENDWHILE
    
    LET i = 0.0
    WHILE i < 10.0
        LET charset(52 + i) = CHR(48 + i)
        LET i = i + 1.0
    ENDWHILE
    
    LET password = ""
    LET count = 0.0
    WHILE count < length
        LET idx = RNDINT(0, 61)
        LET password = password + charset(idx)
        LET count = count + 1.0
    ENDWHILE
    
    RETURN password
ENDFUNCTION

FUNCTION hasLowercase(pwd)
    LET i = 0.0
    LET pwdLen = LEN(pwd)
    WHILE i < pwdLen
        LET c = ASC(MID(pwd, i, 1))
        IF c >= 97 AND c <= 122 THEN
            RETURN 1.0
        ENDIF
        LET i = i + 1.0
    ENDWHILE
    RETURN 0.0
ENDFUNCTION

FUNCTION hasUppercase(pwd)
    LET i = 0.0
    LET pwdLen = LEN(pwd)
    WHILE i < pwdLen
        LET c = ASC(MID(pwd, i, 1))
        IF c >= 65 AND c <= 90 THEN
            RETURN 1.0
        ENDIF
        LET i = i + 1.0
    ENDWHILE
    RETURN 0.0
ENDFUNCTION

FUNCTION hasDigit(pwd)
    LET i = 0.0
    LET pwdLen = LEN(pwd)
    WHILE i < pwdLen
        LET c = ASC(MID(pwd, i, 1))
        IF c >= 48 AND c <= 57 THEN
            RETURN 1.0
        ENDIF
        LET i = i + 1.0
    ENDWHILE
    RETURN 0.0
ENDFUNCTION

FUNCTION isStrongPassword(pwd)
    IF hasLowercase(pwd) == 1.0 THEN
        IF hasUppercase(pwd) == 1.0 THEN
            IF hasDigit(pwd) == 1.0 THEN
                RETURN 1.0
            ENDIF
        ENDIF
    ENDIF
    RETURN 0.0
ENDFUNCTION

PRINT "================================================"
PRINT "  SECURE PASSWORD GENERATOR"
PRINT "================================================"
PRINT ""

LET attempts = 0.0
LET maxAttempts = 100.0
LET found = 0.0

WHILE found == 0.0 AND attempts < maxAttempts
    LET pwd = generatePassword(12.0)
    LET attempts = attempts + 1.0
    
    IF isStrongPassword(pwd) == 1.0 THEN
        LET found = 1.0
    ENDIF
ENDWHILE

IF found == 1.0 THEN
    PRINT "Generated strong password (attempt", attempts, "):"
    PRINT "  ", pwd
    PRINT ""
    PRINT "Password strength analysis:"
    PRINT "  Length:", LEN(pwd)
    PRINT "  Has lowercase:", hasLowercase(pwd)
    PRINT "  Has uppercase:", hasUppercase(pwd)
    PRINT "  Has digits:", hasDigit(pwd)
ELSE
    PRINT "Failed to generate strong password after", maxAttempts, "attempts"
ENDIF

PRINT ""
PRINT "Generating 5 random passwords (12 characters each):"
LET i = 1.0
WHILE i <= 5.0
    LET pwd = generatePassword(12.0)
    PRINT "  ", i, ":", pwd, "(strong:", isStrongPassword(pwd), ")"
    LET i = i + 1.0
ENDWHILE

PRINT ""
PRINT "================================================"
PRINT "  Password generation complete!"
PRINT "================================================"

