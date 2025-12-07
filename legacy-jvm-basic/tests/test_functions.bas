Console.WriteLine("=== Math Functions ===")
Dim neg As Integer
neg = 0 - 5
Console.WriteLine("ABS of negative: " + ABS(neg))
Console.WriteLine("SQR(16): " + SQR(16))
Console.WriteLine("INT(3.7): " + INT(3.7))
Console.WriteLine("POW(2, 3): " + POW(2, 3))

Console.WriteLine("SIN(0): " + SIN(0))
Console.WriteLine("COS(0): " + COS(0))

Console.WriteLine("PI: " + PI)
Console.WriteLine("E: " + E)

Console.WriteLine("MIN(10, 5): " + MIN(10, 5))
Console.WriteLine("MAX(10, 5): " + MAX(10, 5))

Console.WriteLine("=== String Functions ===")
Dim s As String
s = "Hello World"
Console.WriteLine("LEN: " + LEN(s))
Console.WriteLine("UPPER: " + UPPER(s))
Console.WriteLine("LOWER: " + LOWER(s))
Console.WriteLine("LEFT(s, 5): " + LEFT(s, 5))
Console.WriteLine("RIGHT(s, 5): " + RIGHT(s, 5))
Console.WriteLine("MID(3, 5): " + MID(s, 3, 5))

Dim name As String
name = "  Alice  "
Console.WriteLine("Before trim: [" + name + "]")
Console.WriteLine("After trim: [" + TRIM(name) + "]")

Console.WriteLine("CHR(65): " + CHR(65))
Console.WriteLine("ASC(A): " + ASC("A"))

Console.WriteLine("=== Functions in Expressions ===")
Dim a As Integer
a = 3
Dim b As Integer
b = 4
Dim hyp As Single
hyp = SQR(POW(a, 2) + POW(b, 2))
Console.WriteLine("Hypotenuse of 3,4: " + hyp)

Console.WriteLine("=== Done ===")

