SUB greet(name As String)
Console.WriteLine("Hello, " + name)
ENDSUB

Console.WriteLine("Testing SUB")
CALL greet("Alice")
CALL greet("Bob")

