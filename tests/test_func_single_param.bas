FUNCTION square(x)
    RETURN x * x
ENDFUNCTION

FUNCTION increment(n)
    RETURN n + 1
ENDFUNCTION

PRINT "Testing single-parameter functions"

LET x = 5
LET sq = square(x)
PRINT "square(5) =", sq

LET num = increment(10)
PRINT "increment(10) =", num

PRINT "Nested:", square(increment(3))

