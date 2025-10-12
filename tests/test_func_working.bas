FUNCTION double(x)
    RETURN x * 2.0
ENDFUNCTION
FUNCTION add3(a, b, c)
    LET temp = a + b
    RETURN temp + c
ENDFUNCTION
FUNCTION max2(a, b)
    IF a > b THEN
        RETURN a
    ELSE
        RETURN b
    ENDIF
ENDFUNCTION
PRINT "=== Working Function Tests ==="
LET val = double(5.0)
PRINT "double(5) =", val
LET sum = add3(10.0, 20.0, 30.0)
PRINT "add3(10, 20, 30) =", sum
LET bigger = max2(15.0, 23.0)
PRINT "max2(15, 23) =", bigger
PRINT "Nested double(double(3)) =", double(double(3.0))
