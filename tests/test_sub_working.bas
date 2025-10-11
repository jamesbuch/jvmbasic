SUB printnum(n)
    PRINT "Number:", n
ENDSUB

SUB printmsg(msg)
    PRINT msg
ENDSUB

SUB printpair(a, b)
    PRINT a, "and", b
ENDSUB

PRINT "=== Working SUB Tests ==="

CALL printnum(42)
CALL printmsg("Hello from SUB!")
CALL printpair(10, 20)

LET x = 100
CALL printnum(x)

