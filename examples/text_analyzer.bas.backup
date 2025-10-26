REM Text File Analyzer - Demonstrates Phase 8 String & File I/O Functions
REM Analyzes a text file and provides statistics

PRINT "=== Text File Analyzer ==="
PRINT "Showcases: String functions, Character I/O, File operations"
PRINT ""

REM Create sample file for analysis
LET handle = OPENOUTPUT("sample_text.txt")
IF handle >= 0 THEN
    LET dummy = WRITELINE(handle, "The quick brown fox jumps over the lazy dog.")
    LET dummy = WRITELINE(handle, "Hello World! This is a test file.")
    LET dummy = WRITELINE(handle, "JVM BASIC has powerful string functions!")
    LET dummy = WRITELINE(handle, "Date and time support is built-in.")
    LET dummy = WRITELINE(handle, "Character-by-character I/O is now possible.")
    LET dummy = CLOSEFILE(handle)
    PRINT "Created sample_text.txt"
ENDIF

REM Get file information
LET size = FILESIZE("sample_text.txt")
PRINT "File size: "; size; " bytes"
PRINT ""

REM Analyze file character by character
PRINT "Character Analysis:"
LET handle = OPENINPUT("sample_text.txt")
IF handle >= 0 THEN
    LET totalChars = 0
    LET letters = 0
    LET digits = 0
    LET spaces = 0
    LET punctuation = 0
    
    LET ch = READCHAR(handle)
    WHILE ch >= 0
        LET totalChars = totalChars + 1
        
        REM Check if letter (A-Z, a-z)
        IF ch >= 65 THEN
            IF ch <= 90 THEN
                LET letters = letters + 1
            ENDIF
        ENDIF
        IF ch >= 97 THEN
            IF ch <= 122 THEN
                LET letters = letters + 1
            ENDIF
        ENDIF
        
        REM Check if digit (0-9)
        IF ch >= 48 THEN
            IF ch <= 57 THEN
                LET digits = digits + 1
            ENDIF
        ENDIF
        
        REM Count spaces and punctuation  
        REM (ASCII: space=32, comma=44, period=46, exclaim=33, question=63)
        IF ch < 33 THEN
            LET spaces = spaces + 1
        ELSE
            IF ch > 32 THEN
                IF ch < 48 THEN
                    LET punctuation = punctuation + 1
                ENDIF
            ENDIF
        ENDIF
        
        LET ch = READCHAR(handle)
    ENDWHILE
    LET dummy = CLOSEFILE(handle)
    
    PRINT "Total characters: "; totalChars
    PRINT "Letters: "; letters
    PRINT "Digits: "; digits
    PRINT "Spaces: "; spaces
    PRINT "Punctuation: "; punctuation
ENDIF

PRINT ""
PRINT "Line Analysis:"

REM Analyze lines
LET handle = OPENINPUT("sample_text.txt")
IF handle >= 0 THEN
    LET lineCount = 0
    LET longestLine = 0
    LET totalLength = 0
    
    LET line = READLINE(handle)
    WHILE LEN(line) > 0
        LET lineCount = lineCount + 1
        LET lineLen = LEN(line)
        LET totalLength = totalLength + lineLen
        
        IF lineLen > longestLine THEN
            LET longestLine = lineLen
        ENDIF
        
        PRINT "Line "; lineCount; ": "; lineLen; " chars - ";
        IF lineLen > 40 THEN
            PRINT LEFT(line, 37); "..."
        ELSE
            PRINT line
        ENDIF
        
        LET line = READLINE(handle)
    ENDWHILE
    LET dummy = CLOSEFILE(handle)
    
    PRINT ""
    PRINT "Total lines: "; lineCount
    PRINT "Longest line: "; longestLine; " characters"
    IF lineCount > 0 THEN
        LET avgLen = totalLength / lineCount
        PRINT "Average line length: "; avgLen; " characters"
    ENDIF
ENDIF

PRINT ""
PRINT "String Function Demonstrations:"

REM Test string functions
LET sample = "The quick brown fox"
PRINT "Original: "; sample
PRINT "UPPER: "; UPPER(sample)
PRINT "LOWER: "; LOWER(sample)
PRINT "REVERSE: "; REVERSE(sample)
PRINT "REPEAT('-', 40): "; REPEAT("-", 40)

LET replaced = REPLACE(sample, "quick", "FAST")
PRINT "REPLACE 'quick' with 'FAST': "; replaced

IF STARTSWITH(sample, "The") THEN
    PRINT "Starts with 'The': YES"
ENDIF

IF ENDSWITH(sample, "fox") THEN
    PRINT "Ends with 'fox': YES"
ENDIF

LET idx = INDEXOF(sample, "brown")
PRINT "Index of 'brown': "; idx

PRINT ""
PRINT "=== Analysis Complete ==="
PRINT "Generated at: "; DATETIME()

