' Test namespace/OO-style syntax
' Console.WriteLine, Math.Sin, etc.

' Test Console namespace
Dim dummy As Integer
dummy = Console.WriteLine("Testing Console.WriteLine!")
dummy = Console.Write("No newline test... ")
dummy = Console.WriteLine("done!")

' Test Math namespace
Dim angle As Single
Dim result As Single
angle = 1.5708
result = Math.Sin(angle)
Console.WriteLine("Math.Sin(π/2) ≈ " + FormatF("%.4f", result))

Dim sqrtResult As Single
sqrtResult = Math.Sqrt(16.0)
Console.WriteLine("Math.Sqrt(16) = " + FormatF("%.1f", sqrtResult))

Dim pi As Single
pi = Math.PI()
Console.WriteLine("Math.PI = " + FormatF("%.5f", pi))

Console.WriteLine("Namespace syntax test complete!")

