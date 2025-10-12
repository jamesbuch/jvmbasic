FUNCTION square(x)
    RETURN x * x
ENDFUNCTION

FUNCTION add(a, b)
    RETURN a + b
ENDFUNCTION

SUB printResult(msg, value)
    PRINT msg, value
ENDSUB

PRINT "Testing multiple functions"

LET x = 5
LET sq = square(x)
CALL printResult("Square of 5:", sq)

LET sum = add(10, 20)
CALL printResult("10 + 20 =", sum)

PRINT "Square of sum:", square(sum)

