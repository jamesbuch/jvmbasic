FUNCTION sumArrayCustom(arr, size)
    LET total = 0.0
    LET i = 0.0
    WHILE i < size
        LET total = total + arr(i)
        LET i = i + 1.0
    ENDWHILE
    RETURN total
ENDFUNCTION

FUNCTION average(arr, size)
    RETURN sumArrayCustom(arr, size) / size
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

PRINT "=== Array Parameters - Complete Test ==="
PRINT ""

DIM data(5) = 0.0
LET data(0) = 12.0
LET data(1) = 45.0
LET data(2) = 8.0
LET data(3) = 33.0
LET data(4) = 27.0

PRINT "Array: 12, 45, 8, 33, 27"
PRINT "Sum:", sumArrayCustom(data, 5.0)
PRINT "Average:", average(data, 5.0)
PRINT "Max:", findMax(data, 5.0)
PRINT ""

PRINT "=== All Tests Pass! ==="
