' Test Phase 8: Advanced File I/O and Character I/O
Console.WriteLine("=== Phase 8 Advanced File I/O Test ===")

' Create a test file
handle = OPENOUTPUT("test_phase8_temp.txt")
IF handle >= 0 THEN
    dummy = WRITELINE(handle, "Line 1: Hello")
    dummy = WRITELINE(handle, "Line 2: World")
    dummy = WRITELINE(handle, "Line 3: JVM BASIC")
    dummy = CLOSEFILE(handle)
    Console.WriteLine("Created test file")
ELSE
    Console.WriteLine("ERROR: Could not create file")
ENDIF

' Test FILESIZE
size = FILESIZE("test_phase8_temp.txt")
Console.WriteLine("File size: " + size + " bytes")
IF size <= 0 THEN
    Console.WriteLine("ERROR: File size should be positive")
ENDIF

' Test ISFILE and ISDIR
IF ISFILE("test_phase8_temp.txt") THEN
    Console.WriteLine("ISFILE: OK")
ELSE
    Console.WriteLine("ERROR: ISFILE failed")
ENDIF

IF ISDIR("test_phase8_temp.txt") THEN
    Console.WriteLine("ERROR: ISDIR should be false for files")
ELSE
    Console.WriteLine("ISDIR (should be false): OK")
ENDIF

' Test character I/O
handle = OPENINPUT("test_phase8_temp.txt")
IF handle >= 0 THEN
    Console.WriteLine("Reading characters...")
    count = 0
    ch = READCHAR(handle)
    WHILE ch >= 0
        IF count < 10 THEN
            Console.WriteLine("Char " + count + ": " + CHR(ch) + " (ASCII " + ch + ")")
        ENDIF
        count = count + 1
        ch = READCHAR(handle)
    ENDWHILE
    dummy = CLOSEFILE(handle)
ELSE
    Console.WriteLine("ERROR: Could not open file for reading")
ENDIF

' Test ISEOF and HASMORE
handle = OPENINPUT("test_phase8_temp.txt")
IF handle >= 0 THEN
    IF HASMORE(handle) THEN
        Console.WriteLine("HASMORE: OK (file has data)")
    ELSE
        Console.WriteLine("ERROR: HASMORE should be true")
    ENDIF
    
    ' Read all data
    ch = READCHAR(handle)
    WHILE ch >= 0
        ch = READCHAR(handle)
    ENDWHILE
    
    IF ISEOF(handle) THEN
        Console.WriteLine("ISEOF: OK (reached end)")
    ELSE
        Console.WriteLine("ERROR: ISEOF should be true")
    ENDIF
    
    dummy = CLOSEFILE(handle)
ELSE
    Console.WriteLine("ERROR: Could not open file")
ENDIF

' Test COPY
IF COPY("test_phase8_temp.txt", "test_phase8_copy.txt") THEN
    Console.WriteLine("COPY: OK")
    IF FILEEXISTS("test_phase8_copy.txt") THEN
        Console.WriteLine("Copy file exists: OK")
    ELSE
        Console.WriteLine("ERROR: Copy file should exist")
    ENDIF
ELSE
    Console.WriteLine("ERROR: COPY failed")
ENDIF

' Test RENAME  
IF RENAME("test_phase8_copy.txt", "test_phase8_renamed.txt") THEN
    Console.WriteLine("RENAME: OK")
    IF FILEEXISTS("test_phase8_renamed.txt") THEN
        Console.WriteLine("Renamed file exists: OK")
    ELSE
        Console.WriteLine("ERROR: Renamed file should exist")
    ENDIF
ELSE
    Console.WriteLine("ERROR: RENAME failed")
ENDIF

' Test MOVE
IF MOVE("test_phase8_renamed.txt", "test_phase8_moved.txt") THEN
    Console.WriteLine("MOVE: OK")
ELSE
    Console.WriteLine("ERROR: MOVE failed")
ENDIF

' Test directory functions
Console.WriteLine("Current directory: " + CURRENTDIR())
Console.WriteLine("Absolute path: " + ABSOLUTEPATH("."))

' Test MKDIR
IF MKDIR("test_phase8_dir") THEN
    Console.WriteLine("MKDIR: OK")
    IF ISDIR("test_phase8_dir") THEN
        Console.WriteLine("Directory exists: OK")
    ELSE
        Console.WriteLine("ERROR: Directory should exist")
    ENDIF
ELSE
    ' May already exist
    Console.WriteLine("MKDIR: (directory may already exist)")
ENDIF

' Clean up test files
deleted1 = DELETEFILE("test_phase8_temp.txt")
deleted2 = DELETEFILE("test_phase8_moved.txt")

' Remove test directory (only if empty)
removed = RMDIR("test_phase8_dir")

Console.WriteLine("=== All File I/O Tests Complete ===")

