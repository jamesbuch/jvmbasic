' Test File namespace
' Phase 9: File.ReadAllText, File.WriteAllText, File.Exists

' Create a test file
Dim content As String
content = "Hello from JVM BASIC File namespace!"
Dim result As Integer
result = File.WriteAllText("test_output.txt", content)
Console.WriteLine("File written: " + FormatI("%d", result))

' Check if file exists
Dim exists As Integer
exists = File.Exists("test_output.txt")
Console.WriteLine("File exists: " + FormatI("%d", exists))

' Read it back
Dim readContent As String
readContent = File.ReadAllText("test_output.txt")
Dim dummy As Integer
dummy = Console.WriteLine("File content: " + readContent)

' Clean up
result = File.Delete("test_output.txt")
Console.WriteLine("File deleted: " + FormatI("%d", result))

Console.WriteLine("File namespace test complete!")

