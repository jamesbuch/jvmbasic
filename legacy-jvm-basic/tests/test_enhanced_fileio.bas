' Test Enhanced File I/O - Phase 10 Priority 1
' File.OpenReader, File.ReadLine, File.HasLine, File.CloseReader
' File.ReadAllLines, File.GetLine, File.GetLineCount, File.FreeLines
' File.ReadAllBytes, File.GetByte, File.GetByteCount, File.FreeBytes, File.WriteAllBytes

Console.WriteLine("=== Enhanced File I/O Test ===")

' Create a test file first
Dim content As String
content = "Line 1: Hello World"
Dim result As Integer
result = File.WriteAllText("test_enhanced_io.txt", content + Chr(10) + "Line 2: JVM BASIC" + Chr(10) + "Line 3: Enhanced I/O")
Console.WriteLine("Created test file: " + result)

' Test File.OpenReader and File.ReadLine (streaming)
Console.WriteLine("")
Console.WriteLine("--- Streaming Read Test ---")
Dim reader As Integer
reader = File.OpenReader("test_enhanced_io.txt")
Console.WriteLine("Opened reader handle: " + reader)

IF reader >= 0 THEN
    Dim ltext As String
    Dim lineNum As Integer
    Dim moreLines As Integer
    lineNum = 1
    moreLines = File.HasLine(reader)
    WHILE moreLines > 0
        ltext = File.ReadLine(reader)
        Console.WriteLine("Line " + lineNum + ": " + ltext)
        lineNum = lineNum + 1
        moreLines = File.HasLine(reader)
    ENDWHILE
    Dim closeResult As Integer
    closeResult = File.CloseReader(reader)
    Console.WriteLine("Closed reader: " + closeResult)
ELSE
    Console.WriteLine("ERROR: Could not open reader")
ENDIF

' Test File.ReadAllLines (array-based)
Console.WriteLine("")
Console.WriteLine("--- ReadAllLines Test ---")
Dim linesHandle As Integer
linesHandle = File.ReadAllLines("test_enhanced_io.txt")
Console.WriteLine("ReadAllLines handle: " + linesHandle)

IF linesHandle >= 0 THEN
    Dim count As Integer
    count = File.GetLineCount(linesHandle)
    Console.WriteLine("Line count: " + count)

    Dim i As Integer
    FOR i = 0 TO count - 1
        Dim ln As String
        ln = File.GetLine(linesHandle, i)
        Console.WriteLine("  [" + i + "]: " + ln)
    NEXT i

    Dim freeResult As Integer
    freeResult = File.FreeLines(linesHandle)
    Console.WriteLine("Freed lines: " + freeResult)
ELSE
    Console.WriteLine("ERROR: Could not read all lines")
ENDIF

' Test binary file I/O
Console.WriteLine("")
Console.WriteLine("--- Binary I/O Test ---")

' Create binary data using IntList
Dim byteList As Integer
byteList = IntList.Create()
Dim dummy As Integer
dummy = IntList.Add(byteList, 72)   ' H
dummy = IntList.Add(byteList, 101)  ' e
dummy = IntList.Add(byteList, 108)  ' l
dummy = IntList.Add(byteList, 108)  ' l
dummy = IntList.Add(byteList, 111)  ' o

' Write binary file
Dim writeResult As Integer
writeResult = File.WriteAllBytes("test_binary.dat", byteList)
Console.WriteLine("WriteAllBytes result: " + writeResult)

' Read binary file back
Dim bytesHandle As Integer
bytesHandle = File.ReadAllBytes("test_binary.dat")
Console.WriteLine("ReadAllBytes handle: " + bytesHandle)

IF bytesHandle >= 0 THEN
    Dim byteCount As Integer
    byteCount = File.GetByteCount(bytesHandle)
    Console.WriteLine("Byte count: " + byteCount)

    Dim j As Integer
    Dim byteStr As String
    byteStr = ""
    FOR j = 0 TO byteCount - 1
        Dim b As Integer
        b = File.GetByte(bytesHandle, j)
        byteStr = byteStr + CHR(b)
    NEXT j
    Console.WriteLine("Binary content as string: " + byteStr)

    Dim freeBytesResult As Integer
    freeBytesResult = File.FreeBytes(bytesHandle)
    Console.WriteLine("Freed bytes: " + freeBytesResult)
ELSE
    Console.WriteLine("ERROR: Could not read binary file")
ENDIF

' Clean up
Dim delResult1 As Integer
Dim delResult2 As Integer
delResult1 = File.Delete("test_enhanced_io.txt")
delResult2 = File.Delete("test_binary.dat")
Console.WriteLine("")
Console.WriteLine("Cleanup: " + delResult1 + ", " + delResult2)

Console.WriteLine("=== Enhanced File I/O Test Complete ===")
