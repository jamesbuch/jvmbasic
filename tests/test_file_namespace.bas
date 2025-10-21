REM Test File namespace
REM Phase 9: File.ReadAllText, File.WriteAllText, File.Exists

REM Create a test file
Dim content As String = "Hello from JVM BASIC File namespace!"
Dim result As Integer = File.WriteAllText("test_output.txt", content)
Dim dummy As Integer = Console.WriteLine("File written: " + FormatI("%d", result))

REM Check if file exists
Dim exists As Integer = File.Exists("test_output.txt")
Let dummy = Console.WriteLine("File exists: " + FormatI("%d", exists))

REM Read it back
Dim readContent As String = File.ReadAllText("test_output.txt")
Let dummy = Console.WriteLine("File content: " + readContent)

REM Clean up
Let result = File.Delete("test_output.txt")
Let dummy = Console.WriteLine("File deleted: " + FormatI("%d", result))

Print "File namespace test complete!"

