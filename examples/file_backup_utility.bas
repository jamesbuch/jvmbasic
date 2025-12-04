' File Backup Utility - Demonstrates Phase 8 File I/O & Date/Time
' Creates timestamped backups of files

Console.WriteLine("=== File Backup Utility ===")
Console.WriteLine("Showcases: File operations, Date/Time formatting")
Console.WriteLine("")

' Create a sample file to backup
Dim original As String
Dim handle As Integer
Dim dummy As Integer
Dim size As Single
Dim backupSize As Single
Dim origSize As Single
Dim now As Single
Dim timestamp As String
Dim backupName As String
Dim yr As Integer
Dim mo As Integer
Dim dy As Integer
Dim oneWeek As Single
Dim oneMonth As Single
Dim oneYear As Single
Dim deleted As Boolean

original = "important_data.txt"
handle = OPENOUTPUT(original)
IF handle >= 0 THEN
    dummy = WRITELINE(handle, "This is important data")
    dummy = WRITELINE(handle, "Created: " + DATETIME())
    dummy = WRITELINE(handle, "Version: 1.0")
    dummy = CLOSEFILE(handle)
    Console.WriteLine("Created " + original)
ENDIF

' Check if file exists
IF FILEEXISTS(original) THEN
    Console.WriteLine("File exists: " + original)
    size = FILESIZE(original)
    Console.WriteLine("File size: " + size + " bytes")
ELSE
    Console.WriteLine("ERROR: File not found")
ENDIF

Console.WriteLine("")

' Create timestamped backup
now = NOW()
timestamp = FORMATDATE(now, "yyyyMMdd_HHmmss")
backupName = CONCAT3("backup_", timestamp, ".txt")

Console.WriteLine("Creating backup: " + backupName)
IF COPY(original, backupName) THEN
    Console.WriteLine("Backup created successfully")

    ' Verify backup
    IF FILEEXISTS(backupName) THEN
        backupSize = FILESIZE(backupName)
        Console.WriteLine("Backup size: " + backupSize + " bytes")

        origSize = FILESIZE(original)
        IF backupSize == origSize THEN
            Console.WriteLine("Backup verified: sizes match")
        ELSE
            Console.WriteLine("WARNING: Backup size mismatch")
        ENDIF
    ENDIF
ELSE
    Console.WriteLine("ERROR: Backup failed")
ENDIF

Console.WriteLine("")
Console.WriteLine("Directory Information:")
Console.WriteLine("Current directory: " + CURRENTDIR())
Console.WriteLine("Absolute path: " + ABSOLUTEPATH("."))

Console.WriteLine("")
Console.WriteLine("File Type Checks:")
IF ISFILE(original) THEN
    Console.WriteLine(original + " is a file: YES")
ENDIF

IF ISDIR(".") THEN
    Console.WriteLine("Current directory is a dir: YES")
ENDIF

Console.WriteLine("")
Console.WriteLine("Date/Time Demonstrations:")
Console.WriteLine("Current date: " + DATE())
Console.WriteLine("Current time: " + TIME())
Console.WriteLine("Full datetime: " + DATETIME())
Console.WriteLine("Timestamp: " + NOW())

yr = YEAR(now)
mo = MONTH(now)
dy = DAY(now)
Console.WriteLine("Parsed date: " + yr + "-" + mo + "-" + dy)

' Calculate future dates
oneWeek = ADDDAYS(now, 7)
oneMonth = ADDMONTHS(now, 1)
oneYear = ADDYEARS(now, 1)

Console.WriteLine("")
Console.WriteLine("Future Dates:")
Console.WriteLine("One week from now: " + FORMATDATE(oneWeek, "yyyy-MM-dd"))
Console.WriteLine("One month from now: " + FORMATDATE(oneMonth, "yyyy-MM-dd"))
Console.WriteLine("One year from now: " + FORMATDATE(oneYear, "yyyy-MM-dd"))

Console.WriteLine("")
Console.WriteLine("Cleanup: Deleting backup and original...")
deleted = DELETEFILE(backupName)
IF deleted THEN
    Console.WriteLine("Backup deleted")
ELSE
    Console.WriteLine("Could not delete backup")
ENDIF

deleted = DELETEFILE(original)
IF deleted THEN
    Console.WriteLine("Original deleted")
ENDIF

Console.WriteLine("")
Console.WriteLine("=== Backup Utility Complete ===")
