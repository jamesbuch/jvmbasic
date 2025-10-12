PRINT "=== File I/O Test ==="
PRINT ""

PRINT "1. Writing to file..."
LET outFile = OPENOUTPUT("/tmp/jvmbasic_test.txt")
IF outFile >= 0.0 THEN
    CALL writeLine(outFile, "Hello from JVM BASIC!")
    CALL writeLine(outFile, "Line 2: Numbers and text")
    CALL writeLine(outFile, "Line 3: Array parameters work!")
    CALL writeText(outFile, "No newline here")
    CALL closeFile(outFile)
    PRINT "   File written successfully"
ELSE
    PRINT "   Error opening file for writing"
ENDIF
PRINT ""

PRINT "2. Reading from file..."
LET inFile = OPENINPUT("/tmp/jvmbasic_test.txt")
IF inFile >= 0.0 THEN
    LET line1 = READLINE(inFile)
    LET line2 = READLINE(inFile)
    LET line3 = READLINE(inFile)
    CALL closeFile(inFile)
    PRINT "   Line 1:", line1
    PRINT "   Line 2:", line2
    PRINT "   Line 3:", line3
ELSE
    PRINT "   Error opening file for reading"
ENDIF
PRINT ""

PRINT "3. File operations..."
PRINT "   File exists:", FILEEXISTS("/tmp/jvmbasic_test.txt")
CALL deleteFile("/tmp/jvmbasic_test.txt")
PRINT "   After delete:", FILEEXISTS("/tmp/jvmbasic_test.txt")
PRINT ""

PRINT "=== File I/O Tests Complete ==="

