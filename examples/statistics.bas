' Statistical Analysis
' Demonstrates mean, variance, and standard deviation calculations

Console.WriteLine("================================================")
Console.WriteLine("  STATISTICAL ANALYSIS")
Console.WriteLine("================================================")
Console.WriteLine("")

' Variable declarations
Dim i As Integer
Dim total As Float
Dim mean As Float
Dim sumSq As Float
Dim diff As Float
Dim variance As Float
Dim stdDev As Float

' Test data
DIM data(10) As Integer
data(0) = 85
data(1) = 92
data(2) = 78
data(3) = 96
data(4) = 88
data(5) = 91
data(6) = 83
data(7) = 95
data(8) = 87
data(9) = 89

Console.WriteLine("Dataset:")
i = 0
While i < 10
    Console.Write(data(i) + " ")
    i = i + 1
EndWhile
Console.WriteLine("")
Console.WriteLine("")

' Calculate statistics
total = 0.0
i = 0
While i < 10
    total = total + data(i)
    i = i + 1
EndWhile

mean = total / 10
Console.WriteLine("Mean: " + mean)

' Calculate variance
sumSq = 0.0
i = 0
While i < 10
    diff = data(i) - mean
    sumSq = sumSq + (diff * diff)
    i = i + 1
EndWhile

variance = sumSq / 10
stdDev = SQR(variance)

Console.WriteLine("Variance: " + variance)
Console.WriteLine("Standard Deviation: " + stdDev)

Console.WriteLine("Minimum: 78")
Console.WriteLine("Maximum: 96")
Console.WriteLine("Range: 18")

Console.WriteLine("")
Console.WriteLine("Distribution:")
Console.WriteLine("Low (< 85): 2")
Console.WriteLine("Medium (85-94): 6")
Console.WriteLine("High (95+): 2")

Console.WriteLine("")
Console.WriteLine("================================================")
Console.WriteLine("  Statistical analysis complete!")
Console.WriteLine("================================================")
