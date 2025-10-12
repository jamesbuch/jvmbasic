FUNCTION add(a, b)
    RETURN a + b
ENDFUNCTION

FUNCTION mul(x, y)
    RETURN x * y
ENDFUNCTION

FUNCTION div2(n)
    RETURN n / 2
ENDFUNCTION

PRINT "Testing expression-only functions (no local vars)"
PRINT "add(5, 3) =", add(5, 3)
PRINT "mul(4, 7) =", mul(4, 7)
PRINT "div2(20) =", div2(20)
PRINT "Nested: add(mul(2, 3), div2(10)) =", add(mul(2, 3), div2(10))

