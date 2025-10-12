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

FUNCTION partition(arr, low, high)
    LET pivot = arr(high)
    LET i = low - 1.0
    
    LET j = low
    WHILE j < high
        IF arr(j) <= pivot THEN
            LET i = i + 1.0
            LET temp = arr(i)
            LET arr(i) = arr(j)
            LET arr(j) = temp
        ENDIF
        LET j = j + 1.0
    ENDWHILE
    
    LET temp = arr(i + 1.0)
    LET arr(i + 1.0) = arr(high)
    LET arr(high) = temp
    RETURN i + 1.0
ENDFUNCTION

SUB printArray(arr, size, label)
    PRINT label;
    LET i = 0.0
    WHILE i < size
        PRINT arr(i);
        IF i < size - 1.0 THEN
            PRINT ", ";
        ENDIF
        LET i = i + 1.0
    ENDWHILE
    PRINT ""
ENDSUB

PRINT "================================================"
PRINT "  SORTING ALGORITHMS - JVM BASIC"
PRINT "================================================"
PRINT ""

DIM data(8) = 0.0
LET data(0) = 64.0
LET data(1) = 34.0
LET data(2) = 25.0
LET data(3) = 12.0
LET data(4) = 22.0
LET data(5) = 11.0
LET data(6) = 90.0
LET data(7) = 5.0

CALL printArray(data, 8.0, "Original: ")

PRINT ""
PRINT "Sorting with Bubble Sort..."
LET result = bubbleSort(data, 8.0)
CALL printArray(data, 8.0, "Sorted:   ")

PRINT ""
PRINT "Binary Search Tests:"
PRINT "  Search for 22:", binarySearch(data, 8.0, 22.0)
PRINT "  Search for 90:", binarySearch(data, 8.0, 90.0)
PRINT "  Search for 5:", binarySearch(data, 8.0, 5.0)
PRINT "  Search for 99:", binarySearch(data, 8.0, 99.0)

PRINT ""
PRINT "================================================"
PRINT "  All Sorting Tests Complete!"
PRINT "================================================"

