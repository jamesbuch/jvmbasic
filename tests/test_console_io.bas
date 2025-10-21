REM Test modern Console I/O functions
REM Phase 9: Console.WriteLine, Console.Write style functions

REM Test ConsoleWriteLine
LET dummy = CONSOLEWRITELINE("Testing Console.WriteLine functionality")
LET dummy = CONSOLEWRITELINE("This should print on a new line")

REM Test ConsoleWrite (no newline)
LET dummy = CONSOLEWRITE("This prints ")
LET dummy = CONSOLEWRITE("on the ")
LET dummy = CONSOLEWRITELINE("same line!")

REM Test with variables
DIM message AS STRING = "Hello from JVM BASIC!"
LET dummy = CONSOLEWRITELINE(message)

DIM x AS INTEGER = 42
DIM xmsg AS STRING = FORMATI("%d", x)
LET dummy = CONSOLEWRITELINE("x = " + xmsg)

DIM y AS SINGLE = 3.14
DIM ymsg AS STRING = FORMATF("%.2f", y)
LET dummy = CONSOLEWRITELINE("y = " + ymsg)

REM Modern style works!
PRINT "Console I/O test complete!"

