DIM scores(5) As Integer
Dim i As Integer
FOR i = 0 TO 4
    scores(i) = (i + 1) * 10
NEXT i

FOR i = 0 TO 4
    Console.WriteLine(scores(i))
NEXT i

