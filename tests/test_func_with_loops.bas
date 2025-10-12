FUNCTION factorial(n)
    LET result = 1.0
    LET i = 1.0
    WHILE i <= n
        LET result = result * i
        LET i = i + 1.0
    ENDWHILE
    RETURN result
ENDFUNCTION

FUNCTION sumrange(start, finish)
    LET total = 0.0
    FOR i = start TO finish
        LET total = total + i
    NEXT
    RETURN total
ENDFUNCTION

PRINT "Testing functions with loops"

LET fact5 = factorial(5)
PRINT "factorial(5) =", fact5

LET sum = sumrange(1, 10)
PRINT "sum(1 to 10) =", sum

