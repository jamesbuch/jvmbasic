' Modern VB-Style File Backup Utility
' Demonstrates File namespace operations

' Main program
Dim dummy As Integer
Let dummy = Console.WriteLine("=== File Backup Utility ===")
Let dummy = Console.WriteLine("")

' Create a test file
Let dummy = File.WriteAllText("original.txt", "This is the original file content.")
Let dummy = Console.WriteLine("Created original file")

' Read and display content
Dim content As String = File.ReadAllText("original.txt")
Let dummy = Console.WriteLine("Content: " + content)
Let dummy = Console.WriteLine("")

' Create backup
Let dummy = File.Copy("original.txt", "backup.txt")
Let dummy = Console.WriteLine("Created backup")

' Verify files exist
Dim origExists As Single = File.Exists("original.txt")
Dim backupExists As Single = File.Exists("backup.txt")
Let dummy = Console.WriteLine("Original exists: " + FormatF("%.0f", origExists))
Let dummy = Console.WriteLine("Backup exists: " + FormatF("%.0f", backupExists))
Let dummy = Console.WriteLine("")

Let dummy = Console.WriteLine("=== Backup Complete ===")

