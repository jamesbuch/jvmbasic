REM Log File Processor - Demonstrates Advanced String & I/O Functions
REM Parses and analyzes log files

PRINT "=== Log File Processor ==="
PRINT "Showcases: String parsing, Pattern matching, Statistics"
PRINT ""

REM Create sample log file
LET logFile = "application.log"
LET handle = OPENOUTPUT(logFile)
IF handle >= 0 THEN
    LET dummy = WRITELINE(handle, "[2025-10-18 10:15:30] INFO: Application started")
    LET dummy = WRITELINE(handle, "[2025-10-18 10:15:31] DEBUG: Loading configuration")
    LET dummy = WRITELINE(handle, "[2025-10-18 10:15:32] INFO: Server listening on port 8080")
    LET dummy = WRITELINE(handle, "[2025-10-18 10:16:45] WARNING: High memory usage detected")
    LET dummy = WRITELINE(handle, "[2025-10-18 10:17:00] ERROR: Connection timeout to database")
    LET dummy = WRITELINE(handle, "[2025-10-18 10:17:01] INFO: Retrying connection")
    LET dummy = WRITELINE(handle, "[2025-10-18 10:17:02] INFO: Connection established")
    LET dummy = WRITELINE(handle, "[2025-10-18 10:18:30] WARNING: Slow query detected")
    LET dummy = WRITELINE(handle, "[2025-10-18 10:19:00] INFO: Request processed successfully")
    LET dummy = WRITELINE(handle, "[2025-10-18 10:20:00] ERROR: Invalid user credentials")
    LET dummy = CLOSEFILE(handle)
    PRINT "Created "; logFile
ENDIF

PRINT ""
PRINT "Log File Statistics:"

REM Count log levels
LET handle = OPENINPUT(logFile)
IF handle >= 0 THEN
    LET totalLines = 0
    LET infoCount = 0
    LET debugCount = 0
    LET warningCount = 0
    LET errorCount = 0
    
    LET line = READLINE(handle)
    WHILE LEN(line) > 0
        LET totalLines = totalLines + 1
        
        REM Count by level
        IF CONTAINS(line, "INFO:") THEN
            LET infoCount = infoCount + 1
        ENDIF
        IF CONTAINS(line, "DEBUG:") THEN
            LET debugCount = debugCount + 1
        ENDIF
        IF CONTAINS(line, "WARNING:") THEN
            LET warningCount = warningCount + 1
        ENDIF
        IF CONTAINS(line, "ERROR:") THEN
            LET errorCount = errorCount + 1
        ENDIF
        
        LET line = READLINE(handle)
    ENDWHILE
    LET dummy = CLOSEFILE(handle)
    
    PRINT "Total log entries: "; totalLines
    PRINT "INFO: "; infoCount
    PRINT "DEBUG: "; debugCount
    PRINT "WARNING: "; warningCount
    PRINT "ERROR: "; errorCount
ENDIF

PRINT ""
PRINT "Error Messages:"

REM Extract error messages
LET handle = OPENINPUT(logFile)
IF handle >= 0 THEN
    LET errorNum = 0
    LET line = READLINE(handle)
    WHILE LEN(line) > 0
        IF CONTAINS(line, "ERROR:") THEN
            LET errorNum = errorNum + 1
            
            REM Extract timestamp
            LET tsStart = INDEXOF(line, "[")
            LET tsEnd = INDEXOF(line, "]")
            IF tsStart >= 0 THEN
                IF tsEnd > tsStart THEN
                    LET timestamp = SUBSTRINGLEN(line, tsStart + 1, tsEnd - tsStart - 1)
                    
                    REM Extract message
                    LET msgStart = INDEXOF(line, "ERROR:")
                    IF msgStart >= 0 THEN
                        LET message = SUBSTRING(line, msgStart + 7)
                        PRINT errorNum; ". ["; timestamp; "] "; message
                    ENDIF
                ENDIF
            ENDIF
        ENDIF
        
        LET line = READLINE(handle)
    ENDWHILE
    LET dummy = CLOSEFILE(handle)
ENDIF

PRINT ""
PRINT "String Function Demonstrations:"

LET sample = "[2025-10-18 10:15:30] INFO: Application started"
PRINT "Sample log line:"
PRINT sample
PRINT ""

REM Extract parts using string functions
LET bracketPos = INDEXOF(sample, "]")
IF bracketPos > 0 THEN
    LET datePart = SUBSTRINGLEN(sample, 1, bracketPos - 1)
    PRINT "Timestamp: "; datePart
ENDIF

LET colonPos = INDEXOF(sample, ":")
IF colonPos > 0 THEN
    LET afterBracket = SUBSTRING(sample, bracketPos + 2)
    LET spacePos = INDEXOF(afterBracket, ":")
    IF spacePos > 0 THEN
        LET level = SUBSTRINGLEN(afterBracket, 0, spacePos)
        PRINT "Level: "; level
    ENDIF
ENDIF

PRINT ""
PRINT "Padding Demonstration:"
PRINT PADLEFT("INFO", 10); " | Message 1"
PRINT PADLEFT("WARNING", 10); " | Message 2"
PRINT PADLEFT("ERROR", 10); " | Message 3"

PRINT ""
PRINT "String Comparison:"
LET level1 = "ERROR"
LET level2 = "WARNING"
LET cmp = STRCMP(level1, level2)
IF cmp < 0 THEN
    PRINT level1; " comes before "; level2
ELSE
    PRINT level1; " comes after "; level2
ENDIF

PRINT ""
PRINT "=== Log Processing Complete ==="
PRINT "Processed at: "; DATETIME()

REM Cleanup
LET deleted = DELETEFILE(logFile)
LET deleted2 = DELETEFILE("application.log")

