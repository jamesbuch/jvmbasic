DIM scores(5) As Integer

scores(0) = 85
scores(1) = 92
scores(2) = 78
scores(3) = 95
scores(4) = 88

Console.WriteLine("Test Scores:")
Dim i As Integer
i = 0
Console.WriteLine("Score " + i + "=" + scores(i))
i = 1
Console.WriteLine("Score " + i + "=" + scores(i))

Dim total As Integer
total = scores(0) + scores(1) + scores(2)
Console.WriteLine("Total of first 3 scores: " + total)

IF scores(3) > 90 THEN
    Console.WriteLine("Student 3 got an A!")
ENDIF

DIM flags(3) As Boolean
flags(0) = true
flags(1) = false

Console.WriteLine("Flags:")
Console.WriteLine(flags(0) + " " + flags(1))

