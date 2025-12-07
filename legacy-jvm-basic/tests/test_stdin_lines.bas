' test_stdin_lines.bas - Test reading multiple lines from stdin
' ===========================================================
' Run with: java -cp .:basicrt:lib/* TestStdinLines < tests/test_stdin_lines_data.txt
'
' This test demonstrates reading and processing multiple text lines

Console.WriteLine("=== Multi-line Input Test ===")
Console.WriteLine("")

' Read a title line
Console.Write("Title: ")
Dim title As String = Console.ReadLine()
Console.WriteLine("Got title: " + title)

' Read count of items
Console.Write("Number of items: ")
Dim countStr As String = Console.ReadLine()
Dim count As Integer = Val(countStr)
Console.WriteLine("Will read " + count + " items")
Console.WriteLine("")

' Read each item
Dim total As Integer = 0
Dim i As Integer
For i = 1 To count
    Console.Write("Item " + i + ": ")
    Dim item As String = Console.ReadLine()
    Dim value As Integer = Val(item)
    Console.WriteLine("  Read: " + item + " (as int: " + value + ")")
    total = total + value
Next

Console.WriteLine("")
Console.WriteLine("--- Summary ---")
Console.WriteLine("Title: " + title)
Console.WriteLine("Items read: " + count)
Console.WriteLine("Total sum: " + total)
Console.WriteLine("Average: " + (total / count))
Console.WriteLine("")
Console.WriteLine("=== Test Complete ===")
