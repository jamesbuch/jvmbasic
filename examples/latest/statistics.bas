' Modern VB-Style Statistical Analysis

Function Mean(arr As Single, size As Single) As Single
    Dim total As Single = 0.0
    Dim i As Single = 0.0
    While i < size
        total = total + arr(Int(i))
        i = i + 1.0
    End While
    Return total / size
End Function

Function StdDev(arr As Single, size As Single) As Single
    Dim avg As Single = Mean(arr, size)
    Dim sumSq As Single = 0.0
    Dim i As Single = 0.0
    While i < size
        Dim diff As Single = arr(Int(i)) - avg
        sumSq = sumSq + (diff * diff)
        i = i + 1.0
    End While
    Return Math.Sqrt(sumSq / size)
End Function

' Main
Print "==========================================="
Print "  STATISTICAL ANALYSIS"
Print "==========================================="

Dim scores(10) = 0.0
scores(0) = 85.5
scores(1) = 92.3
scores(2) = 78.9
scores(3) = 95.0
scores(4) = 88.7

Print "Mean: "; Mean(scores, 5.0)
Print "Std Dev: "; StdDev(scores, 5.0)
