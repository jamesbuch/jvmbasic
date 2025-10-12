FUNCTION bubbleSort(arr, size)
    LET n = size
    LET i = 0.0
    WHILE i < n - 1.0
        LET j = 0.0
        WHILE j < n - i - 1.0
            IF arr(j) > arr(j + 1.0) THEN
                LET temp = arr(j)
                LET arr(j) = arr(j + 1.0)
                LET arr(j + 1.0) = temp
            ENDIF
            LET j = j + 1.0
        ENDWHILE
        LET i = i + 1.0
    ENDWHILE
    RETURN 0.0
ENDFUNCTION

FUNCTION hasDuplicate(arr, size, value)
    LET i = 0.0
    WHILE i < size
        IF arr(i) == value THEN
            RETURN 1.0
        ENDIF
        LET i = i + 1.0
    ENDWHILE
    RETURN 0.0
ENDFUNCTION

SUB generateUniqueNumbers(arr, count, minVal, maxVal)
    LET i = 0.0
    WHILE i < count
        LET num = RNDINT(minVal, maxVal)
        
        IF hasDuplicate(arr, i, num) == 0.0 THEN
            LET arr(i) = num
            LET i = i + 1.0
        ENDIF
    ENDWHILE
ENDSUB

SUB printNumbers(arr, size)
    LET i = 0.0
    WHILE i < size
        IF i > 0.0 THEN
            PRINT " -";
        ENDIF
        IF arr(i) < 10.0 THEN
            PRINT " ";
        ENDIF
        PRINT arr(i);
        LET i = i + 1.0
    ENDWHILE
ENDSUB

FUNCTION sumArray(arr, size)
    LET total = 0.0
    LET i = 0.0
    WHILE i < size
        LET total = total + arr(i)
        LET i = i + 1.0
    ENDWHILE
    RETURN total
ENDFUNCTION

FUNCTION hasConsecutive(arr, size)
    LET i = 0.0
    WHILE i < size - 1.0
        IF arr(i + 1.0) == arr(i) + 1.0 THEN
            RETURN 1.0
        ENDIF
        LET i = i + 1.0
    ENDWHILE
    RETURN 0.0
ENDFUNCTION

PRINT "================================================"
PRINT "  ADVANCED LOTTO NUMBER GENERATOR"
PRINT "  (Numbers 1-45, Pick 6)"
PRINT "================================================"
PRINT ""

DIM numbers(6) = 0.0

PRINT "How many games would you like to generate?"
INPUT games

PRINT ""
PRINT "Generating", games, "unique lotto games..."
PRINT ""

LET gameNum = 1.0
WHILE gameNum <= games
    PRINT "Game", gameNum, ": ";
    
    CALL generateUniqueNumbers(numbers, 6.0, 1, 45)
    
    LET dummy = bubbleSort(numbers, 6.0)
    
    CALL printNumbers(numbers, 6.0)
    
    LET sum = sumArray(numbers, 6.0)
    LET avg = sum / 6.0
    LET hasConsec = hasConsecutive(numbers, 6.0)
    
    PRINT "  (Sum:", sum, "Avg:", avg, "Consec:", hasConsec, ")"
    
    LET gameNum = gameNum + 1.0
ENDWHILE

PRINT ""
PRINT "================================================"
PRINT "  STATISTICS SUMMARY"
PRINT "================================================"
PRINT ""

CALL generateUniqueNumbers(numbers, 6.0, 1, 45)
LET dummy = bubbleSort(numbers, 6.0)

PRINT "Sample game analysis:"
PRINT "  Numbers: ";
CALL printNumbers(numbers, 6.0)
PRINT ""
PRINT "  Sum:", sumArray(numbers, 6.0)
PRINT "  Average:", sumArray(numbers, 6.0) / 6.0
PRINT "  Min:", numbers(0)
PRINT "  Max:", numbers(5)
PRINT "  Range:", numbers(5) - numbers(0)
PRINT "  Has consecutive:", hasConsecutive(numbers, 6.0)

PRINT ""
PRINT "================================================"
PRINT "  Good luck with your lotto tickets!"
PRINT "================================================"

