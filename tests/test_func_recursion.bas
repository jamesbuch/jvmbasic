FUNCTION fib(n)
    IF n <= 1 THEN
        RETURN n
    ELSE
        RETURN fib(n - 1) + fib(n - 2)
    ENDIF
ENDFUNCTION

PRINT "Testing recursive functions"

FOR i = 0 TO 8
    PRINT "fib("; i; ") =", fib(i)
NEXT i

