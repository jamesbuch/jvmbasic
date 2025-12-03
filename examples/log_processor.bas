' Log File Processor - Demonstrates Advanced String & I/O Functions
' Parses and analyzes log files

Console.WriteLine("=== Log File Processor ===")
Console.WriteLine("Showcases: String parsing, Pattern matching, Statistics")
Console.WriteLine("")

' Create sample log file
logFile = "application.log"
handle = OPENOUTPUT(logFile)
IF handle >= 0 THEN
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
ENDIF

Console.WriteLine("")
Console.WriteLine("Log File Statistics:")

' Count log levels
handle = OPENINPUT(logFile)
IF handle >= 0 THEN
    totalLines = 0
    infoCount = 0
    debugCount = 0
    warningCount = 0
    errorCount = 0
    
    line = READLINE(handle)
    WHILE LEN(line) > 0
        totalLines = totalLines + 1
        
        REM Count by level
        IF CONTAINS(line, "INFO:") THEN
            infoCount = infoCount + 1
        ENDIF
        IF CONTAINS(line, "DEBUG:") THEN
            debugCount = debugCount + 1
        ENDIF
        IF CONTAINS(line, "WARNING:") THEN
            warningCount = warningCount + 1
        ENDIF
        IF CONTAINS(line, "ERROR:") THEN
            errorCount = errorCount + 1
        ENDIF
        
        line = READLINE(handle)
    ENDWHILE
    dummy = CLOSEFILE(handle)
    
    Console.WriteLine("Total log entries: " + totalLines)
    Console.WriteLine("INFO: " + infoCount)
    Console.WriteLine("DEBUG: " + debugCount)
    Console.WriteLine("WARNING: " + warningCount)
    Console.WriteLine("ERROR: " + errorCount)
ENDIF

Console.WriteLine("")
Console.WriteLine("Error Messages:")

' Extract error messages
handle = OPENINPUT(logFile)
IF handle >= 0 THEN
    errorNum = 0
    line = READLINE(handle)
    WHILE LEN(line) > 0
        IF CONTAINS(line, "ERROR:") THEN
            errorNum = errorNum + 1
            
            REM Extract timestamp
            tsStart = INDEXOF(line, "[")
            tsEnd = INDEXOF(line, "]")
            IF tsStart >= 0 THEN
                IF tsEnd > tsStart THEN
                    timestamp = SUBSTRINGLEN(line, tsStart + 1, tsEnd - tsStart - 1)
                    
                    REM Extract message
                    msgStart = INDEXOF(line, "ERROR:")
                    IF msgStart >= 0 THEN
                        message = SUBSTRING(line, msgStart + 7)
                        Console.WriteLine(errorNum + ". [" + timestamp + "] " + message)
                    ENDIF
                ENDIF
            ENDIF
        ENDIF
        
        line = READLINE(handle)
    ENDWHILE
    dummy = CLOSEFILE(handle)
ENDIF

Console.WriteLine("")
Console.WriteLine("String Function Demonstrations:")

sample = "[2025-10-18 10:15:30] INFO: Application started"
Console.WriteLine("Sample log line:")
Console.WriteLine(sample)
Console.WriteLine("")

' Extract parts using string functions
bracketPos = INDEXOF(sample, "]")
IF bracketPos > 0 THEN
    datePart = SUBSTRINGLEN(sample, 1, bracketPos - 1)
    Console.WriteLine("Timestamp: " + datePart)
ENDIF

colonPos = INDEXOF(sample, ":")
IF colonPos > 0 THEN
    afterBracket = SUBSTRING(sample, bracketPos + 2)
    spacePos = INDEXOF(afterBracket, ":")
    IF spacePos > 0 THEN
        level = SUBSTRINGLEN(afterBracket, 0, spacePos)
        Console.WriteLine("Level: " + level)
    ENDIF
ENDIF

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
IF cmp < 0 THEN
    Console.WriteLine(level1 + " comes before " + level2)
ELSE
    Console.WriteLine(level1 + " comes after " + level2)
ENDIF

Console.WriteLine("")
Console.WriteLine("=== Log Processing Complete ===")
Console.WriteLine("Processed at: " + DATETIME())

' Cleanup
deleted = DELETEFILE(logFile)
deleted2 = DELETEFILE("application.log")

