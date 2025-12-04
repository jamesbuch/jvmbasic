' Log File Processor - Demonstrates Advanced String & I/O Functions
' Parses and analyzes log files

Console.WriteLine("=== Log File Processor ===")
Console.WriteLine("Showcases: String parsing, Pattern matching, Statistics")
Console.WriteLine("")

' Variable declarations
Dim logFile As String
Dim handle As Integer
Dim dummy As Integer
Dim totalLines As Integer
Dim infoCount As Integer
Dim debugCount As Integer
Dim warningCount As Integer
Dim errorCount As Integer
Dim line As String
Dim errorNum As Integer
Dim tsStart As Integer
Dim tsEnd As Integer
Dim timestamp As String
Dim msgStart As Integer
Dim message As String
Dim sample As String
Dim bracketPos As Integer
Dim datePart As String
Dim colonPos As Integer
Dim afterBracket As String
Dim spacePos As Integer
Dim level As String
Dim level1 As String
Dim level2 As String
Dim cmp As Integer
Dim deleted As Boolean
Dim deleted2 As Boolean

' Create sample log file
logFile = "application.log"
handle = OPENOUTPUT(logFile)
If handle >= 0 Then
    dummy = WRITELINE(handle, "[2025-10-18 10:15:30] INFO: Application started")
    dummy = WRITELINE(handle, "[2025-10-18 10:15:31] DEBUG: Loading configuration")
    dummy = WRITELINE(handle, "[2025-10-18 10:15:32] INFO: Server listening on port 8080")
    dummy = WRITELINE(handle, "[2025-10-18 10:16:45] WARNING: High memory usage detected")
    dummy = WRITELINE(handle, "[2025-10-18 10:17:00] ERROR: Connection timeout to database")
    dummy = WRITELINE(handle, "[2025-10-18 10:17:01] INFO: Retrying connection")
    dummy = WRITELINE(handle, "[2025-10-18 10:17:02] INFO: Connection established")
    dummy = WRITELINE(handle, "[2025-10-18 10:18:30] WARNING: Slow query detected")
    dummy = WRITELINE(handle, "[2025-10-18 10:19:00] INFO: Request processed successfully")
    dummy = WRITELINE(handle, "[2025-10-18 10:20:00] ERROR: Invalid user credentials")
    dummy = CLOSEFILE(handle)
    Console.WriteLine("Created " + logFile)
EndIf

Console.WriteLine("")
Console.WriteLine("Log File Statistics:")

' Count log levels
handle = OPENINPUT(logFile)
If handle >= 0 Then
    totalLines = 0
    infoCount = 0
    debugCount = 0
    warningCount = 0
    errorCount = 0

    line = READLINE(handle)
    While LEN(line) > 0
        totalLines = totalLines + 1

        ' Count by level
        If CONTAINS(line, "INFO:") Then
            infoCount = infoCount + 1
        EndIf
        If CONTAINS(line, "DEBUG:") Then
            debugCount = debugCount + 1
        EndIf
        If CONTAINS(line, "WARNING:") Then
            warningCount = warningCount + 1
        EndIf
        If CONTAINS(line, "ERROR:") Then
            errorCount = errorCount + 1
        EndIf

        line = READLINE(handle)
    EndWhile
    dummy = CLOSEFILE(handle)

    Console.WriteLine("Total log entries: " + totalLines)
    Console.WriteLine("INFO: " + infoCount)
    Console.WriteLine("DEBUG: " + debugCount)
    Console.WriteLine("WARNING: " + warningCount)
    Console.WriteLine("ERROR: " + errorCount)
EndIf

Console.WriteLine("")
Console.WriteLine("Error Messages:")

' Extract error messages
handle = OPENINPUT(logFile)
If handle >= 0 Then
    errorNum = 0
    line = READLINE(handle)
    While LEN(line) > 0
        If CONTAINS(line, "ERROR:") Then
            errorNum = errorNum + 1

            ' Extract timestamp
            tsStart = INDEXOF(line, "[")
            tsEnd = INDEXOF(line, "]")
            If tsStart >= 0 Then
                If tsEnd > tsStart Then
                    timestamp = SUBSTRINGLEN(line, tsStart + 1, tsEnd - tsStart - 1)

                    ' Extract message
                    msgStart = INDEXOF(line, "ERROR:")
                    If msgStart >= 0 Then
                        message = SUBSTRING(line, msgStart + 7)
                        Console.WriteLine(errorNum + ". [" + timestamp + "] " + message)
                    EndIf
                EndIf
            EndIf
        EndIf

        line = READLINE(handle)
    EndWhile
    dummy = CLOSEFILE(handle)
EndIf

Console.WriteLine("")
Console.WriteLine("String Function Demonstrations:")

sample = "[2025-10-18 10:15:30] INFO: Application started"
Console.WriteLine("Sample log line:")
Console.WriteLine(sample)
Console.WriteLine("")

' Extract parts using string functions
bracketPos = INDEXOF(sample, "]")
If bracketPos > 0 Then
    datePart = SUBSTRINGLEN(sample, 1, bracketPos - 1)
    Console.WriteLine("Timestamp: " + datePart)
EndIf

colonPos = INDEXOF(sample, ":")
If colonPos > 0 Then
    afterBracket = SUBSTRING(sample, bracketPos + 2)
    spacePos = INDEXOF(afterBracket, ":")
    If spacePos > 0 Then
        level = SUBSTRINGLEN(afterBracket, 0, spacePos)
        Console.WriteLine("Level: " + level)
    EndIf
EndIf

Console.WriteLine("")
Console.WriteLine("Padding Demonstration:")
Console.WriteLine(PADLEFT("INFO", 10) + " | Message 1")
Console.WriteLine(PADLEFT("WARNING", 10) + " | Message 2")
Console.WriteLine(PADLEFT("ERROR", 10) + " | Message 3")

Console.WriteLine("")
Console.WriteLine("String Comparison:")
level1 = "ERROR"
level2 = "WARNING"
cmp = STRCMP(level1, level2)
If cmp < 0 Then
    Console.WriteLine(level1 + " comes before " + level2)
Else
    Console.WriteLine(level1 + " comes after " + level2)
EndIf

Console.WriteLine("")
Console.WriteLine("=== Log Processing Complete ===")
Console.WriteLine("Processed at: " + DATETIME())

' Cleanup
deleted = DELETEFILE(logFile)
