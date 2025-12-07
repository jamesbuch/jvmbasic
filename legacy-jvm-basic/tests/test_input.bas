' Test Console.ReadLine for user input
Dim name As String
Dim ageStr As String
Dim heightStr As String
Dim studentStr As String

Console.Write("Enter your name: ")
name = Console.ReadLine()

Console.Write("Enter your age: ")
ageStr = Console.ReadLine()

Console.Write("Enter your height in meters: ")
heightStr = Console.ReadLine()

Console.Write("Are you a student? (true/false): ")
studentStr = Console.ReadLine()

Console.WriteLine("Hello " + name)
Console.WriteLine("You entered age: " + ageStr)
Console.WriteLine("You entered height: " + heightStr)
Console.WriteLine("Student: " + studentStr)
