FUNCTION double(x)
    RETURN x * 2.0
ENDFUNCTION

FUNCTION add3(a, b, c)
    RETURN a + b + c
ENDFUNCTION

PRINT "Testing functions"
LET val = double(5.0)
PRINT "double(5) =", val
LET sum = add3(10.0, 20.0, 30.0)
PRINT "add3(10, 20, 30) =", sum
