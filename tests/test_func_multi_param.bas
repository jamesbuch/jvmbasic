FUNCTION add(a, b)
    RETURN a + b
ENDFUNCTION

FUNCTION multiply(x, y)
    RETURN x * y
ENDFUNCTION

FUNCTION average(a, b, c)
    RETURN (a + b + c) / 3
ENDFUNCTION

PRINT "Testing multi-parameter functions"

LET sum = add(10, 20)
PRINT "add(10, 20) =", sum

LET product = multiply(6, 7)
PRINT "multiply(6, 7) =", product

LET avg = average(10, 20, 30)
PRINT "average(10, 20, 30) =", avg

