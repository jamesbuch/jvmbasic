REM Test modern Console I/O functions
REM Console.WriteLine, Console.Write style functions

REM Test ConsoleWriteLine
dummy = CONSOLEWRITELINE("Testing Console.WriteLine functionality")
dummy = CONSOLEWRITELINE("This should print on a new line")

REM Test ConsoleWrite (no newline)
dummy = CONSOLEWRITE("This prints ")
dummy = CONSOLEWRITE("on the ")
dummy = CONSOLEWRITELINE("same line!")

REM Test with variables
message = "Hello from JVM BASIC!"
dummy = CONSOLEWRITELINE(message)

x = 42
xmsg = FORMATI("%d", x)
dummy = CONSOLEWRITELINE("x = " + xmsg)

y = 3.14
ymsg = FORMATF("%.2f", y)
dummy = CONSOLEWRITELINE("y = " + ymsg)

REM Modern style works!
Console.WriteLine("Console I/O test complete!")

