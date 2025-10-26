Console.WriteLine("================================================")
Console.WriteLine("  SORTING AND SEARCHING ALGORITHMS")
Console.WriteLine("================================================")
Console.WriteLine("")

REM Test data
DIM numbers(10) As Integer
numbers(0) = 64
numbers(1) = 34
numbers(2) = 25
numbers(3) = 12
numbers(4) = 22
numbers(5) = 11
numbers(6) = 90
numbers(7) = 5
numbers(8) = 77
numbers(9) = 30

Console.WriteLine("Original array:")
i = 0
WHILE i < 10
    Console.Write(numbers(i) + " ")
    i = i + 1
ENDWHILE
Console.WriteLine("")
Console.WriteLine("")

REM Simple bubble sort demonstration
Console.WriteLine("Performing bubble sort...")
swaps = 0
n = 10
i = 0
WHILE i < n - 1
    j = 0
    WHILE j < n - i - 1
        IF numbers(j) > numbers(j + 1) THEN
            temp = numbers(j)
            numbers(j) = numbers(j + 1)
            numbers(j + 1) = temp
            swaps = swaps + 1
        ENDIF
        j = j + 1
    ENDWHILE
    i = i + 1
ENDWHILE

Console.WriteLine("After bubble sort:")
i = 0
WHILE i < 10
    Console.Write(numbers(i) + " ")
    i = i + 1
ENDWHILE
Console.WriteLine("")
Console.WriteLine("Swaps performed: " + swaps)
Console.WriteLine("")

REM Search demonstrations
target = 25
found = -1
i = 0
WHILE i < 10
    IF numbers(i) == target THEN
        found = i
        i = 10
    ENDIF
    i = i + 1
ENDWHILE

IF found >= 0 THEN
    Console.WriteLine("Search for " + target + ": Found at index " + found)
ELSE
    Console.WriteLine("Search for " + target + ": Not found")
ENDIF

target = 99
found = -1
i = 0
WHILE i < 10
    IF numbers(i) == target THEN
        found = i
        i = 10
    ENDIF
    i = i + 1
ENDWHILE

IF found >= 0 THEN
    Console.WriteLine("Search for " + target + ": Found at index " + found)
ELSE
    Console.WriteLine("Search for " + target + ": Not found")
ENDIF

Console.WriteLine("")
Console.WriteLine("================================================")
Console.WriteLine("  Sorting and searching complete!")
Console.WriteLine("================================================")