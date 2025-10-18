FUNCTION sumArrayCustom(arr, size)
    LET total = 0.0
    LET i = 0.0
    WHILE i < size
        LET total = total + arr(i)
        LET i = i + 1.0
    ENDWHILE
    RETURN total
ENDFUNCTION

FUNCTION findMax(arr, size)
    LET maxVal = arr(0)
    LET i = 1.0
    WHILE i < size
        IF arr(i) > maxVal THEN
            LET maxVal = arr(i)
        ENDIF
        LET i = i + 1.0
    ENDWHILE
    RETURN maxVal
ENDFUNCTION

FUNCTION getElement(arr, index)
    RETURN arr(index)
ENDFUNCTION

PRINT "=== Array Functions Test ==="

DIM numbers(5) = 0.0
LET numbers(0) = 15.0
LET numbers(1) = 42.0
LET numbers(2) = 8.0
LET numbers(3) = 23.0
LET numbers(4) = 37.0

PRINT "Sum:", sumArrayCustom(numbers, 5.0)
PRINT "Max:", findMax(numbers, 5.0)
PRINT "Element 2:", getElement(numbers, 2.0)

PRINT "=== Tests Complete ==="

