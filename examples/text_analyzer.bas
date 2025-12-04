' Text File Analyzer - Demonstrates Phase 8 String & File I/O Functions
' Analyzes a text file and provides statistics

Console.WriteLine("=== Text File Analyzer ===")
Console.WriteLine("Showcases: String functions, Character I/O, File operations")
Console.WriteLine("")

' Variable declarations
Dim handle As Integer
Dim dummy As Integer
Dim size As Single
Dim totalChars As Integer
Dim letters As Integer
Dim digits As Integer
Dim spaces As Integer
Dim punctuation As Integer
Dim ch As Integer
Dim totalLines As Integer
Dim totalWords As Integer
Dim longestLine As Integer
Dim line As String
Dim words As Integer
Dim sample As String
Dim deleted As Boolean

' Create sample file for analysis
handle = OPENOUTPUT("sample_text.txt")
IF handle >= 0 THEN
    dummy = WRITELINE(handle, "The quick brown fox jumps over the lazy dog.")
    dummy = WRITELINE(handle, "Hello World! This is a test file.")
    dummy = WRITELINE(handle, "JVM BASIC has powerful string functions!")
    dummy = WRITELINE(handle, "Date and time support is built-in.")
    dummy = WRITELINE(handle, "Character-by-character I/O is now possible.")
    dummy = CLOSEFILE(handle)
    Console.WriteLine("Created sample_text.txt")
ENDIF

' Get file information
size = FILESIZE("sample_text.txt")
Console.WriteLine("File size: " + size + " bytes")
Console.WriteLine("")

' Analyze file character by character
Console.WriteLine("Character Analysis:")
handle = OPENINPUT("sample_text.txt")
IF handle >= 0 THEN
    totalChars = 0
    letters = 0
    digits = 0
    spaces = 0
    punctuation = 0

    ch = READCHAR(handle)
    WHILE ch >= 0
        totalChars = totalChars + 1

        IF ch >= 65 AND ch <= 90 THEN
            letters = letters + 1
        ELSEIF ch >= 97 AND ch <= 122 THEN
            letters = letters + 1
        ELSEIF ch >= 48 AND ch <= 57 THEN
            digits = digits + 1
        ELSEIF ch == 32 THEN
            spaces = spaces + 1
        ELSE
            punctuation = punctuation + 1
        ENDIF

        ch = READCHAR(handle)
    ENDWHILE
    dummy = CLOSEFILE(handle)

    Console.WriteLine("Total characters: " + totalChars)
    Console.WriteLine("Letters: " + letters)
    Console.WriteLine("Digits: " + digits)
    Console.WriteLine("Spaces: " + spaces)
    Console.WriteLine("Punctuation: " + punctuation)
ENDIF

Console.WriteLine("")

' Analyze file line by line
Console.WriteLine("Line Analysis:")
handle = OPENINPUT("sample_text.txt")
IF handle >= 0 THEN
    totalLines = 0
    totalWords = 0
    longestLine = 0

    line = READLINE(handle)
    WHILE LEN(line) > 0
        totalLines = totalLines + 1

        IF LEN(line) > longestLine THEN
            longestLine = LEN(line)
        ENDIF

        ' Count words in line (simplified)
        words = 5
        totalWords = totalWords + words

        Console.WriteLine("Line " + totalLines + ": " + LEN(line) + " chars, " + words + " words")
        line = READLINE(handle)
    ENDWHILE
    dummy = CLOSEFILE(handle)

    Console.WriteLine("")
    Console.WriteLine("Summary:")
    Console.WriteLine("Total lines: " + totalLines)
    Console.WriteLine("Total words: " + totalWords)
    Console.WriteLine("Longest line: " + longestLine + " characters")
    Console.WriteLine("Average words per line: " + (totalWords / totalLines))
ENDIF

Console.WriteLine("")

' String function demonstrations
sample = "  Hello, JVM BASIC!  "
Console.WriteLine("String Function Demonstrations:")
Console.WriteLine("Original: '" + sample + "'")
Console.WriteLine("Trimmed: '" + TRIM(sample) + "'")
Console.WriteLine("Uppercase: '" + UPPER(sample) + "'")
Console.WriteLine("Lowercase: '" + LOWER(sample) + "'")
Console.WriteLine("Length: " + LEN(sample))
Console.WriteLine("Left 5: '" + LEFT(sample, 5) + "'")
Console.WriteLine("Right 5: '" + RIGHT(sample, 5) + "'")

Console.WriteLine("")
Console.WriteLine("=== Text Analysis Complete ===")

' Clean up
deleted = DELETEFILE("sample_text.txt")
IF deleted THEN
    Console.WriteLine("Sample file cleaned up")
ENDIF
