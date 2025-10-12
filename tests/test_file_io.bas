PRINT "=== File I/O Test ==="
PRINT ""

PRINT "1. Check if test file exists..."
LET exists = FILEEXISTS("/tmp/jvmbasic_test.txt")
PRINT "   File exists before test:", exists

PRINT ""
PRINT "2. Open file for writing..."
LET outFile = OPENOUTPUT("/tmp/jvmbasic_test.txt")
IF outFile >= 0.0 THEN
    PRINT "   File opened successfully, handle:", outFile
ELSE
    PRINT "   ERROR: Could not open file for writing"
ENDIF

PRINT ""
PRINT "3. Open file for reading..."
LET inFile = OPENINPUT("/tmp/jvmbasic_test_read.txt")
IF inFile >= 0.0 THEN
    PRINT "   File opened, handle:", inFile
    LET line = READLINE(inFile)
    PRINT "   Read line:", line
    LET dummy = CLOSEFILE(inFile)
ELSE
    PRINT "   File doesn't exist (expected if first run)"
ENDIF

PRINT ""
PRINT "4. File existence checks..."
PRINT "   FILEEXISTS(\"/tmp\"):", FILEEXISTS("/tmp")
PRINT "   FILEEXISTS(\"/nonexistent.txt\"):", FILEEXISTS("/nonexistent.txt")

PRINT ""
PRINT "=== File I/O Tests Complete ==="
PRINT "Note: Full write/read tests need manual verification"

