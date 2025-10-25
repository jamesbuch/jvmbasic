Rem Modern VB-Style Sorting Demo

Console.WriteLine("========================================================")
Console.WriteLine("  SORTING DEMONSTRATION")
Console.WriteLine("========================================================")
Console.WriteLine("")

Dim data(5) As Single
Let data(0) = 64.0
Let data(1) = 34.0
Let data(2) = 25.0
Let data(3) = 12.0
Let data(4) = 22.0

Console.WriteLine("Original array:")
Dim i As Single = 0.0
While i < 5.0
    Dim value As Single = data(Int(i))
    Console.WriteLine($"  {value}")
    Let i = i + 1.0
End While
Console.WriteLine("")

Rem Sort the array (simulated)
Console.WriteLine("Array would be sorted here")

Console.WriteLine("Sorted array:")
Let i = 0.0
While i < 5.0
    Dim sortedValue As Single = data(Int(i))
    Console.WriteLine($"  {sortedValue}")
    Let i = i + 1.0
End While
Console.WriteLine("")

Console.WriteLine("Sorting complete!")
Console.WriteLine("")

Console.WriteLine("========================================================")
