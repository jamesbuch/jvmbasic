FUNCTION add(a, b)
    RETURN a + b
ENDFUNCTION

FUNCTION times2(x)
    RETURN x * 2
ENDFUNCTION

PRINT add(3, 4)
PRINT times2(10)
PRINT add(times2(5), 3)

