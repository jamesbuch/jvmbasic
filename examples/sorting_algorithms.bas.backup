FUNCTION bubbleSort(arr, size)
    LET swaps = 0.0
    LET n = size
    LET i = 0.0
    WHILE i < n - 1.0
        LET j = 0.0
        WHILE j < n - i - 1.0
            IF arr(j) > arr(j + 1.0) THEN
                LET temp = arr(j)
                LET arr(j) = arr(j + 1.0)
                LET arr(j + 1.0) = temp
                LET swaps = swaps + 1.0
            ENDIF
            LET j = j + 1.0
        ENDWHILE
        LET i = i + 1.0
    ENDWHILE
    RETURN swaps
ENDFUNCTION

FUNCTION binarySearch(arr, size, target)
    LET left = 0.0
    LET right = size - 1.0
    
    WHILE left <= right
        LET mid = INT((left + right) / 2.0)
        
        IF arr(mid) == target THEN
            RETURN mid
        ELSEIF arr(mid) < target THEN
            LET left = mid + 1.0
        ELSE
            LET right = mid - 1.0
        ENDIF
    ENDWHILE
    
    RETURN -1.0
ENDFUNCTION

FUNCTION linearSearch(arr, size, target)
    LET i = 0.0
    WHILE i < size
        IF arr(i) == target THEN
            RETURN i
        ENDIF
        LET i = i + 1.0
    ENDWHILE
    RETURN -1.0
ENDFUNCTION

FUNCTION findMin(arr, size)
    LET minVal = arr(0)
    LET minIdx = 0.0
    LET i = 1.0
    WHILE i < size
        IF arr(i) < minVal THEN
            LET minVal = arr(i)
            LET minIdx = i
        ENDIF
        LET i = i + 1.0
    ENDWHILE
    RETURN minIdx
ENDFUNCTION

FUNCTION findMax(arr, size)
    LET maxVal = arr(0)
    LET maxIdx = 0.0
    LET i = 1.0
    WHILE i < size
        IF arr(i) > maxVal THEN
            LET maxVal = arr(i)
            LET maxIdx = i
        ENDIF
        LET i = i + 1.0
    ENDWHILE
    RETURN maxIdx
ENDFUNCTION

SUB printArray(arr, size, label)
    PRINT label;
    LET i = 0.0
    WHILE i < size
        PRINT arr(i);
        IF i < size - 1.0 THEN
            PRINT ",";
        ENDIF
        LET i = i + 1.0
    ENDWHILE
    PRINT ""
ENDSUB

SUB fillRandom(arr, size, minVal, maxVal)
    LET i = 0.0
    WHILE i < size
        LET arr(i) = RNDINT(minVal, maxVal)
        LET i = i + 1.0
    ENDWHILE
ENDSUB

FUNCTION isSorted(arr, size)
    LET i = 0.0
    WHILE i < size - 1.0
        IF arr(i) > arr(i + 1.0) THEN
            RETURN 0.0
        ENDIF
        LET i = i + 1.0
    ENDWHILE
    RETURN 1.0
ENDFUNCTION

PRINT "========================================================"
PRINT "  SORTING AND SEARCHING ALGORITHMS DEMONSTRATION"
PRINT "========================================================"
PRINT ""

DIM data(10) = 0.0

PRINT "Initializing array with random values..."
CALL fillRandom(data, 10.0, 1, 100)
CALL printArray(data, 10.0, "Original:  ")
PRINT ""

PRINT "1. BUBBLE SORT"
PRINT "   Sorting array..."
LET swaps = bubbleSort(data, 10.0)
CALL printArray(data, 10.0, "Sorted:    ")
PRINT "   Swaps performed:", swaps
PRINT "   Is sorted:", isSorted(data, 10.0)
PRINT ""

PRINT "2. BINARY SEARCH (on sorted array)"
LET searchFor = data(5)
PRINT "   Searching for:", searchFor
LET idx = binarySearch(data, 10.0, searchFor)
IF idx >= 0.0 THEN
    PRINT "   Found at index:", idx
ELSE
    PRINT "   Not found"
ENDIF

LET searchFor = 999.0
PRINT "   Searching for:", searchFor
LET idx = binarySearch(data, 10.0, searchFor)
IF idx >= 0.0 THEN
    PRINT "   Found at index:", idx
ELSE
    PRINT "   Not found (expected)"
ENDIF
PRINT ""

PRINT "3. LINEAR SEARCH"
CALL fillRandom(data, 10.0, 1, 50)
CALL printArray(data, 10.0, "Unsorted:  ")
LET searchFor = data(7)
PRINT "   Searching for:", searchFor
LET idx = linearSearch(data, 10.0, searchFor)
IF idx >= 0.0 THEN
    PRINT "   Found at index:", idx
ELSE
    PRINT "   Not found"
ENDIF
PRINT ""

PRINT "4. FIND MIN/MAX"
LET minIdx = findMin(data, 10.0)
LET maxIdx = findMax(data, 10.0)
PRINT "   Minimum value:", data(minIdx), "at index", minIdx
PRINT "   Maximum value:", data(maxIdx), "at index", maxIdx
PRINT ""

PRINT "5. PERFORMANCE TEST"
PRINT "   Creating larger dataset (100 elements)..."
DIM bigData(100) = 0.0
CALL fillRandom(bigData, 100.0, 1, 1000)
PRINT "   Sorting 100 elements..."
LET swaps = bubbleSort(bigData, 100.0)
PRINT "   Swaps performed:", swaps
PRINT "   Verification: sorted =", isSorted(bigData, 100.0)
PRINT ""

PRINT "   Searching for elements..."
LET found = 0.0
LET i = 0.0
WHILE i < 10.0
    LET searchFor = bigData(INT(RND * 100.0))
    LET idx = binarySearch(bigData, 100.0, searchFor)
    IF idx >= 0.0 THEN
        LET found = found + 1.0
    ENDIF
    LET i = i + 1.0
ENDWHILE
PRINT "   Found", found, "out of 10 searches"
PRINT ""

PRINT "========================================================"
PRINT "  All algorithm tests complete!"
PRINT "========================================================"
