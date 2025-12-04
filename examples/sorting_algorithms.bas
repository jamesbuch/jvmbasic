' Sorting and Searching Algorithms
' Demonstrates bubble sort and linear search

Console.WriteLine("================================================")
Console.WriteLine("  SORTING And SEARCHING ALGORITHMS")
Console.WriteLine("================================================")
Console.WriteLine("")

' Variable declarations
Dim i As Integer
Dim j As Integer
Dim n As Integer
Dim swaps As Integer
Dim temp As Integer
Dim target As Integer
Dim found As Integer

' Test data
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
While i < 10
    Console.Write(numbers(i) + " ")
    i = i + 1
EndWhile
Console.WriteLine("")
Console.WriteLine("")

' Simple bubble sort demonstration
Console.WriteLine("Performing bubble sort...")
swaps = 0
n = 10
i = 0
While i < n - 1
    j = 0
    While j < n - i - 1
        If numbers(j) > numbers(j + 1) Then
            temp = numbers(j)
            numbers(j) = numbers(j + 1)
            numbers(j + 1) = temp
            swaps = swaps + 1
        EndIf
        j = j + 1
    EndWhile
    i = i + 1
EndWhile

Console.WriteLine("After bubble sort:")
i = 0
While i < 10
    Console.Write(numbers(i) + " ")
    i = i + 1
EndWhile
Console.WriteLine("")
Console.WriteLine("Swaps performed: " + swaps)
Console.WriteLine("")

' Search demonstrations
target = 25
found = -1
i = 0
While i < 10
    If numbers(i) == target Then
        found = i
        i = 10
    EndIf
    i = i + 1
EndWhile

If found >= 0 Then
    Console.WriteLine("Search for " + target + ": Found at index " + found)
Else
    Console.WriteLine("Search for " + target + ": Not found")
EndIf

target = 99
found = -1
i = 0
While i < 10
    If numbers(i) == target Then
        found = i
        i = 10
    EndIf
    i = i + 1
EndWhile

If found >= 0 Then
    Console.WriteLine("Search for " + target + ": Found at index " + found)
Else
    Console.WriteLine("Search for " + target + ": Not found")
EndIf

Console.WriteLine("")
Console.WriteLine("================================================")
Console.WriteLine("  Sorting and searching complete!")
Console.WriteLine("================================================")
