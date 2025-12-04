' File Backup Utility - Demonstrates Phase 8 File I/O & Date/Time
' Creates timestamped backups of files

Console.WriteLine("=== File Backup Utility ===")
Console.WriteLine("Showcases: File operations, Date/Time formatting")
Console.WriteLine("")

' Create a sample file to backup
Dim original As String
Dim handle As Integer
Dim dummy As Integer
Dim size As Float
Dim backupSize As Float
Dim origSize As Float
Dim now As Float
Dim timestamp As String
Dim backupName As String
Dim yr As Integer
Dim mo As Integer
Dim dy As Integer
Dim oneWeek As Float
Dim oneMonth As Float
Dim oneYear As Float
Dim deleted As Boolean

original = "important_data.txt"
handle = OPENOUTPUT(original)
If handle >= 0 Then
    dummy = WRITELINE(handle, "This is important data")
    dummy = WRITELINE(handle, "Created: " + DATETIME())
    dummy = WRITELINE(handle, "Version: 1.0")
    dummy = CLOSEFILE(handle)
    Console.WriteLine("Created " + original)
EndIf

' Check if file exists
If FILEEXISTS(original) Then
    Console.WriteLine("File exists: " + original)
    size = FILESIZE(original)
    Console.WriteLine("File size: " + size + " bytes")
Else
    Console.WriteLine("ERROR: File not found")
EndIf

Console.WriteLine("")

' Create timestamped backup
now = NOW()
timestamp = FORMATDATE(now, "yyyyMMdd_HHmmss")
backupName = CONCAT3("backup_", timestamp, ".txt")

Console.WriteLine("Creating backup: " + backupName)
If COPY(original, backupName) Then
    Console.WriteLine("Backup created successfully")

    ' Verify backup
    If FILEEXISTS(backupName) Then
        backupSize = FILESIZE(backupName)
        Console.WriteLine("Backup size: " + backupSize + " bytes")

        origSize = FILESIZE(original)
        If backupSize == origSize Then
            Console.WriteLine("Backup verified: sizes match")
        Else
            Console.WriteLine("WARNING: Backup size mismatch")
        EndIf
    EndIf
Else
    Console.WriteLine("ERROR: Backup failed")
EndIf

Console.WriteLine("")
Console.WriteLine("Directory Information:")
Console.WriteLine("Current directory: " + CURRENTDIR())
Console.WriteLine("Absolute path: " + ABSOLUTEPATH("."))

Console.WriteLine("")
Console.WriteLine("File Type Checks:")
If ISFILE(original) Then
    Console.WriteLine(original + " is a file: YES")
EndIf

If ISDIR(".") Then
    Console.WriteLine("Current directory is a dir: YES")
EndIf

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
If deleted Then
    Console.WriteLine("Backup deleted")
Else
    Console.WriteLine("Could not delete backup")
EndIf

deleted = DELETEFILE(original)
If deleted Then
    Console.WriteLine("Original deleted")
EndIf

Console.WriteLine("")
Console.WriteLine("=== Backup Utility Complete ===")
