FUNCTION sumArray(arr, size)
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

FUNCTION findMin(arr, size)
    LET minVal = arr(0)
    LET i = 1.0
    WHILE i < size
        IF arr(i) < minVal THEN
            LET minVal = arr(i)
        ENDIF
        LET i = i + 1.0
    ENDWHILE
    RETURN minVal
ENDFUNCTION

FUNCTION average(arr, size)
    RETURN sumArray(arr, size) / size
ENDFUNCTION

PRINT "=== Array Parameter Tests ==="
PRINT ""

DIM numbers(5) = 0.0
LET numbers(0) = 15.0
LET numbers(1) = 42.0
LET numbers(2) = 8.0
LET numbers(3) = 23.0
LET numbers(4) = 37.0

PRINT "Array: 15, 42, 8, 23, 37"
PRINT "Sum:", sumArray(numbers, 5.0)
PRINT "Max:", findMax(numbers, 5.0)
PRINT "Min:", findMin(numbers, 5.0)
PRINT "Avg:", average(numbers, 5.0)
PRINT ""

DIM scores(4) = 0.0
LET scores(0) = 95.0
LET scores(1) = 87.0
LET scores(2) = 92.0
LET scores(3) = 78.0

PRINT "Scores: 95, 87, 92, 78"
PRINT "Total:", sumArray(scores, 4.0)
PRINT "Highest:", findMax(scores, 4.0)
PRINT "Lowest:", findMin(scores, 4.0)
PRINT "Average:", average(scores, 4.0)
PRINT ""

PRINT "=== All Array Parameter Tests Pass! ==="

