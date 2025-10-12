FUNCTION sum(arr, size)
    LET total = 0.0
    LET i = 0.0
    WHILE i < size
        LET total = total + arr(i)
        LET i = i + 1.0
    ENDWHILE
    RETURN total
ENDFUNCTION

FUNCTION mean(arr, size)
    RETURN sum(arr, size) / size
ENDFUNCTION

FUNCTION variance(arr, size)
    LET avg = mean(arr, size)
    LET sumSq = 0.0
    LET i = 0.0
    WHILE i < size
        LET diff = arr(i) - avg
        LET sumSq = sumSq + (diff * diff)
        LET i = i + 1.0
    ENDWHILE
    RETURN sumSq / size
ENDFUNCTION

FUNCTION stdDev(arr, size)
    RETURN SQR(variance(arr, size))
ENDFUNCTION

FUNCTION min(arr, size)
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

FUNCTION max(arr, size)
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

PRINT "==========================================="
PRINT "  STATISTICAL ANALYSIS"
PRINT "==========================================="
PRINT ""

DIM scores(10) = 0.0
LET scores(0) = 85.5
LET scores(1) = 92.3
LET scores(2) = 78.9
LET scores(3) = 95.0
LET scores(4) = 88.7
LET scores(5) = 76.4
LET scores(6) = 91.2
LET scores(7) = 83.6
LET scores(8) = 89.1
LET scores(9) = 94.8

PRINT "Test Scores Analysis"
PRINT "Sample size:", 10
PRINT ""

PRINT "Descriptive Statistics:"
PRINT "  Mean:", mean(scores, 10.0)
PRINT "  Std Dev:", stdDev(scores, 10.0)
PRINT "  Variance:", variance(scores, 10.0)
PRINT "  Minimum:", min(scores, 10.0)
PRINT "  Maximum:", max(scores, 10.0)
PRINT "  Range:", max(scores, 10.0) - min(scores, 10.0)
PRINT ""

PRINT "==========================================="
PRINT "  Analysis Complete!"
PRINT "==========================================="

