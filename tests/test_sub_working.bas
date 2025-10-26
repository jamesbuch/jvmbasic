SUB printnum(n As Integer)
    Console.WriteLine("Number: " + n)
ENDSUB

SUB printmsg(msg As String)
    Console.WriteLine(msg)
ENDSUB

SUB printpair(a As Integer, b As Integer)
    Console.WriteLine("Pair: " + a + " and " + b)
ENDSUB

Console.WriteLine("=== Working SUB Tests ===")

CALL printnum(42)
CALL printmsg("Hello from SUB!")
CALL printpair(10, 20)

x = 100
CALL printnum(x)

