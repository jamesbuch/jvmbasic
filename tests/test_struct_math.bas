' Test struct with math operations
v1x = 3.0
v1y = 4.0
v2x = 5.0
v2y = 12.0

' Calculate magnitudes
mag1 = SQRT(v1x * v1x + v1y * v1y)
mag2 = SQRT(v2x * v2x + v2y * v2y)

Console.WriteLine("Vector 1: (" + v1x + ", " + v1y + ") magnitude: " + mag1)
Console.WriteLine("Vector 2: (" + v2x + ", " + v2y + ") magnitude: " + mag2)

