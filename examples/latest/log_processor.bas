' Modern VB-Style Log Processor
' Demonstrates file and string operations

' Main program
Dim dummy As Integer
Let dummy = Console.WriteLine("=== Log Processor ===")
Let dummy = Console.WriteLine("")

' Create sample log file
Dim logContent As String = "INFO: Application started"
logContent = logContent + CHR(10) + "ERROR: Connection failed"
logContent = logContent + CHR(10) + "INFO: Retrying connection"
logContent = logContent + CHR(10) + "INFO: Connection successful"

Let dummy = File.WriteAllText("app.log", logContent)
Let dummy = Console.WriteLine("Created log file")

' Read and process
Dim content As String = File.ReadAllText("app.log")
Let dummy = Console.WriteLine("Log content:")
Let dummy = Console.WriteLine(content)
Let dummy = Console.WriteLine("")

' Count errors
Dim errorCount As Single = 0.0
If CONTAINS(content, "ERROR") Then
    errorCount = 1.0
EndIf
Let dummy = Console.WriteLine("Error count: " + FormatF("%.0f", errorCount))
Let dummy = Console.WriteLine("")

Let dummy = Console.WriteLine("=== Processing Complete ===")

