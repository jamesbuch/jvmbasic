' Test Command-Line Arguments - Phase 10 Priority 2
' Args.GetCount, Args.Get, Args.GetAll, Args.Contains, Args.IndexOf

Console.WriteLine("=== Command-Line Arguments Test ===")

Dim argCount As Integer
argCount = Args.GetCount()
Console.WriteLine("Argument count: " + argCount)

IF argCount > 0 THEN
    Dim i As Integer
    FOR i = 0 TO argCount - 1
        Dim arg As String
        arg = Args.Get(i)
        Console.WriteLine("  Arg[" + i + "]: " + arg)
    NEXT i

    Dim allArgs As String
    allArgs = Args.GetAll()
    Console.WriteLine("All arguments: " + allArgs)
ELSE
    Console.WriteLine("No arguments provided")
    Console.WriteLine("Usage: java -cp .:lib/* BasicProgram arg1 arg2 ...")
ENDIF

Console.WriteLine("=== Command-Line Arguments Test Complete ===")
