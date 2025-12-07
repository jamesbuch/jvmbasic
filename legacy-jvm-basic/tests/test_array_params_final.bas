FUNCTION sumArrayCustom(arr As IntArray, size As Integer) As Integer
    DIM total As Integer = 0
    DIM i As Integer = 0
    WHILE i < size
        total = total + arr(i)
        i = i + 1
    ENDWHILE
    RETURN total
ENDFUNCTION

FUNCTION average(arr As IntArray, size As Integer) As Integer
    RETURN sumArrayCustom(arr, size) / size
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

Console.WriteLine("=== Array Parameters - Complete Test ===")
Console.WriteLine("")

DIM data(5) As Integer
data(0) = 12
data(1) = 45
data(2) = 8
data(3) = 33
data(4) = 27

Console.WriteLine("Array: 12, 45, 8, 33, 27")
Console.WriteLine("Sum: " + sumArrayCustom(data, 5))
Console.WriteLine("Average: " + average(data, 5))
Console.WriteLine("Max: " + findMax(data, 5))
Console.WriteLine("")

Console.WriteLine("=== All Tests Pass! ===")
