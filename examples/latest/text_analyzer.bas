' Modern VB-Style Text Analyzer
' Demonstrates modern syntax with string operations

FUNCTION CountWords(text AS STRING) AS SINGLE
    Dim count As Single = 1.0
    Dim i AS SINGLE = 0.0
    While i < LEN(text)
        If MID(text, i, 1) == " " Then
            count = count + 1.0
        EndIf
        i = i + 1.0
    EndWhile
    Return count
ENDFUNCTION

' Main program
Dim dummy As Integer
Let dummy = Console.WriteLine("=== Text Analyzer ===")
Let dummy = Console.WriteLine("")

Dim text As String = "Hello World from JVM BASIC"
Let dummy = Console.WriteLine("Text: " + text)
Let dummy = Console.WriteLine("Length: " + FormatF("%.0f", LEN(text)))
Let dummy = Console.WriteLine("Words: " + FormatF("%.0f", CountWords(text)))
Let dummy = Console.WriteLine("Uppercase: " + UPPER(text))
Let dummy = Console.WriteLine("Lowercase: " + LOWER(text))
Let dummy = Console.WriteLine("")

Let dummy = Console.WriteLine("=== Analysis Complete ===")

