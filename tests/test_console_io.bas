' Test modern Console I/O functions
' Console.WriteLine, Console.Write style functions

' Test Console.WriteLine
Console.WriteLine("Testing Console.WriteLine functionality")
Console.WriteLine("This should print on a new line")

' Test Console.Write (no newline)
Console.Write("This prints ")
Console.Write("on the ")
Console.WriteLine("same line!")

' Test with variables
message = "Hello from JVM BASIC!"
Console.WriteLine(message)

x = 42
xmsg = Str.FormatInt("{0}", x)
Console.WriteLine("x = " + xmsg)

y = 3.14
ymsg = Str.FormatFloat("{0}", y)
Console.WriteLine("y = " + ymsg)

' Modern style works!
Console.WriteLine("Console I/O test complete!")
