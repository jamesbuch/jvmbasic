DIM nums(5) As Integer
nums(0) = 50
nums(1) = 20
nums(2) = 80
nums(3) = 10
nums(4) = 60

Console.WriteLine("Array elements:")
Console.WriteLine("  nums(0) = " + nums(0))
Console.WriteLine("  nums(1) = " + nums(1))
Console.WriteLine("  nums(2) = " + nums(2))
Console.WriteLine("  nums(3) = " + nums(3))
Console.WriteLine("  nums(4) = " + nums(4))

Dim smallest As Integer
Dim largest As Integer
Dim total As Integer
Dim size As Integer
smallest = 10
largest = 80
total = 220
size = 4

Console.WriteLine("Min: " + smallest)
Console.WriteLine("Max: " + largest)
Console.WriteLine("Sum: " + total)
Console.WriteLine("Upper bound: " + size)

Dim avg As Integer
avg = total / (size + 1)
Console.WriteLine("Average: " + avg)

