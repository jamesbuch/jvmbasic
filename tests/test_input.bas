' Test Console.ReadLine for user input (requires interactive stdin)
name = ""
age = 0
height = 0.0
student = false

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
