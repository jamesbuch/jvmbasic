Rem Modern VB-Style Sorting Demo

Print "========================================================"
Print "  SORTING DEMONSTRATION"
Print "========================================================"
Print ""

Dim data(10) = 0.0
Dim i As Single = 0.0

Rem Fill array with random values
While i < 10.0
    Let data(Int(i)) = RndInt(1, 100)
    Let i = i + 1.0
End While

Print "Original array:"
Let i = 0.0
While i < 10.0
    Print data(Int(i)); " ";
    Let i = i + 1.0
End While
Print ""
Print ""

Rem Sort the array
Call ARRAYSORT(data)

Print "Sorted array:"
Let i = 0.0
While i < 10.0
    Print data(Int(i)); " ";
    Let i = i + 1.0
End While
Print ""
Print ""

Print "Sorting complete!"
Print ""

Print "========================================================"
