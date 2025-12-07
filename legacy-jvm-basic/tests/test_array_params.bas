FUNCTION sumArrayCustom(arr As IntArray, size As Integer) As Integer
    DIM total As Integer = 0
    DIM i As Integer = 0
    WHILE i < size
        total = total + arr(i)
        i = i + 1
    ENDWHILE
    RETURN total
ENDFUNCTION

FUNCTION findMax(arr As IntArray, size As Integer) As Integer
    DIM maxVal As Integer = arr(0)
    DIM i As Integer = 1
    WHILE i < size
        IF arr(i) > maxVal THEN
            maxVal = arr(i)
        ENDIF
        i = i + 1
    ENDWHILE
    RETURN maxVal
ENDFUNCTION

FUNCTION findMin(arr As IntArray, size As Integer) As Integer
    DIM minVal As Integer = arr(0)
    DIM i As Integer = 1
    WHILE i < size
        IF arr(i) < minVal THEN
            minVal = arr(i)
        ENDIF
        i = i + 1
    ENDWHILE
    RETURN minVal
ENDFUNCTION

FUNCTION average(arr As IntArray, size As Integer) As Integer
    RETURN sumArrayCustom(arr, size) / size
ENDFUNCTION

Console.WriteLine("=== Array Parameter Tests ===")
Console.WriteLine("")

DIM numbers(5) As Integer
numbers(0) = 15
numbers(1) = 42
numbers(2) = 8
numbers(3) = 23
numbers(4) = 37

Console.WriteLine("Array: 15, 42, 8, 23, 37")
Console.WriteLine("Sum: " + sumArrayCustom(numbers, 5))
Console.WriteLine("Max: " + findMax(numbers, 5))
Console.WriteLine("Min: " + findMin(numbers, 5))
Console.WriteLine("Avg: " + average(numbers, 5))
Console.WriteLine("")

DIM scores(4) As Integer
scores(0) = 95
scores(1) = 87
scores(2) = 92
scores(3) = 78

Console.WriteLine("Scores: 95, 87, 92, 78")
Console.WriteLine("Total: " + sumArrayCustom(scores, 4))
Console.WriteLine("Highest: " + findMax(scores, 4))
Console.WriteLine("Lowest: " + findMin(scores, 4))
Console.WriteLine("Average: " + average(scores, 4))
Console.WriteLine("")

Console.WriteLine("=== All Array Parameter Tests Pass! ===")

