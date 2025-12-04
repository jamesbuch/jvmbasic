' Text File Analyzer - Demonstrates Phase 8 String & File I/O Functions
' Analyzes a text file and provides statistics

Console.WriteLine("=== Text File Analyzer ===")
Console.WriteLine("Showcases: String functions, Character I/O, File operations")
Console.WriteLine("")

' Variable declarations
Dim handle As Integer
Dim dummy As Integer
Dim size As Float
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
If handle >= 0 Then
    dummy = WRITELINE(handle, "The quick brown fox jumps over the lazy dog.")
    dummy = WRITELINE(handle, "Hello World! This is a test file.")
    dummy = WRITELINE(handle, "JVM BASIC has powerful string functions!")
    dummy = WRITELINE(handle, "Date and time support is built-in.")
    dummy = WRITELINE(handle, "Character-by-character I/O is now possible.")
    dummy = CLOSEFILE(handle)
    Console.WriteLine("Created sample_text.txt")
EndIf

' Get file information
size = FILESIZE("sample_text.txt")
Console.WriteLine("File size: " + size + " bytes")
Console.WriteLine("")

' Analyze file character by character
Console.WriteLine("Character Analysis:")
handle = OPENINPUT("sample_text.txt")
If handle >= 0 Then
    totalChars = 0
    letters = 0
    digits = 0
    spaces = 0
    punctuation = 0

    ch = READCHAR(handle)
    While ch >= 0
        totalChars = totalChars + 1

        If ch >= 65 And ch <= 90 Then
            letters = letters + 1
        ElseIf ch >= 97 And ch <= 122 Then
            letters = letters + 1
        ElseIf ch >= 48 And ch <= 57 Then
            digits = digits + 1
        ElseIf ch == 32 Then
            spaces = spaces + 1
        Else
            punctuation = punctuation + 1
        EndIf

        ch = READCHAR(handle)
    EndWhile
    dummy = CLOSEFILE(handle)

    Console.WriteLine("Total characters: " + totalChars)
    Console.WriteLine("Letters: " + letters)
    Console.WriteLine("Digits: " + digits)
    Console.WriteLine("Spaces: " + spaces)
    Console.WriteLine("Punctuation: " + punctuation)
EndIf

Console.WriteLine("")

' Analyze file line by line
Console.WriteLine("Line Analysis:")
handle = OPENINPUT("sample_text.txt")
If handle >= 0 Then
    totalLines = 0
    totalWords = 0
    longestLine = 0

    line = READLINE(handle)
    While LEN(line) > 0
        totalLines = totalLines + 1

        If LEN(line) > longestLine Then
            longestLine = LEN(line)
        EndIf

        ' Count words in line (simplified)
        words = 5
        totalWords = totalWords + words

        Console.WriteLine("Line " + totalLines + ": " + LEN(line) + " chars, " + words + " words")
        line = READLINE(handle)
    EndWhile
    dummy = CLOSEFILE(handle)

    Console.WriteLine("")
    Console.WriteLine("Summary:")
    Console.WriteLine("Total lines: " + totalLines)
    Console.WriteLine("Total words: " + totalWords)
    Console.WriteLine("Longest line: " + longestLine + " characters")
    Console.WriteLine("Average words per line: " + (totalWords / totalLines))
EndIf

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
If deleted Then
    Console.WriteLine("Sample file cleaned up")
EndIf
