REM Test Phase 8: Advanced File I/O and Character I/O
PRINT "=== Phase 8 Advanced File I/O Test ==="

REM Create a test file
LET handle = OPENOUTPUT("test_phase8_temp.txt")
IF handle >= 0 THEN
    LET dummy = WRITELINE(handle, "Line 1: Hello")
    LET dummy = WRITELINE(handle, "Line 2: World")
    LET dummy = WRITELINE(handle, "Line 3: JVM BASIC")
    LET dummy = CLOSEFILE(handle)
    PRINT "Created test file"
ELSE
    PRINT "ERROR: Could not create file"
ENDIF

REM Test FILESIZE
LET size = FILESIZE("test_phase8_temp.txt")
PRINT "File size: "; size; " bytes"
IF size <= 0 THEN
    PRINT "ERROR: File size should be positive"
ENDIF

REM Test ISFILE and ISDIR
IF ISFILE("test_phase8_temp.txt") THEN
    PRINT "ISFILE: OK"
ELSE
    PRINT "ERROR: ISFILE failed"
ENDIF

IF ISDIR("test_phase8_temp.txt") THEN
    PRINT "ERROR: ISDIR should be false for files"
ELSE
    PRINT "ISDIR (should be false): OK"
ENDIF

REM Test character I/O
LET handle = OPENINPUT("test_phase8_temp.txt")
IF handle >= 0 THEN
    PRINT "Reading characters..."
    LET count = 0
    LET ch = READCHAR(handle)
    WHILE ch >= 0
        IF count < 10 THEN
            PRINT "Char "; count; ": "; CHR(ch); " (ASCII "; ch; ")"
        ENDIF
        LET count = count + 1
        LET ch = READCHAR(handle)
    ENDWHILE
    LET dummy = CLOSEFILE(handle)
ELSE
    PRINT "ERROR: Could not open file for reading"
ENDIF

REM Test ISEOF and HASMORE
LET handle = OPENINPUT("test_phase8_temp.txt")
IF handle >= 0 THEN
    IF HASMORE(handle) THEN
        PRINT "HASMORE: OK (file has data)"
    ELSE
        PRINT "ERROR: HASMORE should be true"
    ENDIF
    
    REM Read all data
    LET ch = READCHAR(handle)
    WHILE ch >= 0
        LET ch = READCHAR(handle)
    ENDWHILE
    
    IF ISEOF(handle) THEN
        PRINT "ISEOF: OK (reached end)"
    ELSE
        PRINT "ERROR: ISEOF should be true"
    ENDIF
    
    LET dummy = CLOSEFILE(handle)
ELSE
    PRINT "ERROR: Could not open file"
ENDIF

REM Test COPY
IF COPY("test_phase8_temp.txt", "test_phase8_copy.txt") THEN
    PRINT "COPY: OK"
    IF FILEEXISTS("test_phase8_copy.txt") THEN
        PRINT "Copy file exists: OK"
    ELSE
        PRINT "ERROR: Copy file should exist"
    ENDIF
ELSE
    PRINT "ERROR: COPY failed"
ENDIF

REM Test RENAME  
IF RENAME("test_phase8_copy.txt", "test_phase8_renamed.txt") THEN
    PRINT "RENAME: OK"
    IF FILEEXISTS("test_phase8_renamed.txt") THEN
        PRINT "Renamed file exists: OK"
    ELSE
        PRINT "ERROR: Renamed file should exist"
    ENDIF
ELSE
    PRINT "ERROR: RENAME failed"
ENDIF

REM Test MOVE
IF MOVE("test_phase8_renamed.txt", "test_phase8_moved.txt") THEN
    PRINT "MOVE: OK"
ELSE
    PRINT "ERROR: MOVE failed"
ENDIF

REM Test directory functions
PRINT "Current directory: "; CURRENTDIR()
PRINT "Absolute path: "; ABSOLUTEPATH(".")

REM Test MKDIR
IF MKDIR("test_phase8_dir") THEN
    PRINT "MKDIR: OK"
    IF ISDIR("test_phase8_dir") THEN
        PRINT "Directory exists: OK"
    ELSE
        PRINT "ERROR: Directory should exist"
    ENDIF
ELSE
    REM May already exist
    PRINT "MKDIR: (directory may already exist)"
ENDIF

REM Clean up test files
LET deleted1 = DELETEFILE("test_phase8_temp.txt")
LET deleted2 = DELETEFILE("test_phase8_moved.txt")

REM Remove test directory (only if empty)
LET removed = RMDIR("test_phase8_dir")

PRINT "=== All File I/O Tests Complete ==="

