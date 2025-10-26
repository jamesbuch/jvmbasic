REM File Backup Utility - Demonstrates Phase 8 File I/O & Date/Time
REM Creates timestamped backups of files

PRINT "=== File Backup Utility ==="
PRINT "Showcases: File operations, Date/Time formatting"
PRINT ""

REM Create a sample file to backup
LET original = "important_data.txt"
LET handle = OPENOUTPUT(original)
IF handle >= 0 THEN
    LET dummy = WRITELINE(handle, "This is important data")
    LET dummy = WRITELINE(handle, "Created: " + DATETIME())
    LET dummy = WRITELINE(handle, "Version: 1.0")
    LET dummy = CLOSEFILE(handle)
    PRINT "Created "; original
ENDIF

REM Check if file exists
IF FILEEXISTS(original) THEN
    PRINT "File exists: "; original
    LET size = FILESIZE(original)
    PRINT "File size: "; size; " bytes"
ELSE
    PRINT "ERROR: File not found"
ENDIF

PRINT ""

REM Create timestamped backup
LET now = NOW()
LET timestamp = FORMATDATE(now, "yyyyMMdd_HHmmss")
LET backupName = CONCAT3("backup_", timestamp, ".txt")

PRINT "Creating backup: "; backupName
IF COPY(original, backupName) THEN
    PRINT "Backup created successfully"
    
    REM Verify backup
    IF FILEEXISTS(backupName) THEN
        LET backupSize = FILESIZE(backupName)
        PRINT "Backup size: "; backupSize; " bytes"
        
        LET origSize = FILESIZE(original)
        IF backupSize = origSize THEN
            PRINT "Backup verified: sizes match"
        ELSE
            PRINT "WARNING: Backup size mismatch"
        ENDIF
    ENDIF
ELSE
    PRINT "ERROR: Backup failed"
ENDIF

PRINT ""
PRINT "Directory Information:"
PRINT "Current directory: "; CURRENTDIR()
PRINT "Absolute path: "; ABSOLUTEPATH(".")

PRINT ""
PRINT "File Type Checks:"
IF ISFILE(original) THEN
    PRINT original; " is a file: YES"
ENDIF

IF ISDIR(".") THEN
    PRINT "Current directory is a dir: YES"
ENDIF

PRINT ""
PRINT "Date/Time Demonstrations:"
PRINT "Current date: "; DATE()
PRINT "Current time: "; TIME()
PRINT "Full datetime: "; DATETIME()
PRINT "Timestamp: "; NOW()

LET yr = YEAR(now)
LET mo = MONTH(now)
LET dy = DAY(now)
PRINT "Parsed date: "; yr; "-"; mo; "-"; dy

REM Calculate future dates
LET oneWeek = ADDDAYS(now, 7)
LET oneMonth = ADDMONTHS(now, 1)
LET oneYear = ADDYEARS(now, 1)

PRINT ""
PRINT "Future Dates:"
PRINT "One week from now: "; FORMATDATE(oneWeek, "yyyy-MM-dd")
PRINT "One month from now: "; FORMATDATE(oneMonth, "yyyy-MM-dd")
PRINT "One year from now: "; FORMATDATE(oneYear, "yyyy-MM-dd")

PRINT ""
PRINT "Cleanup: Deleting backup..."
LET deleted = DELETEFILE(backupName)
IF deleted THEN
    PRINT "Backup deleted"
ELSE
    PRINT "Could not delete backup"
ENDIF

PRINT ""
PRINT "=== Backup Utility Complete ==="

