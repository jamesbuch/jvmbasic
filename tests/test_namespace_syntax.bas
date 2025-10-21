REM Test namespace/OO-style syntax
REM Phase 9: Console.WriteLine, Math.Sin, etc.

REM Test Console namespace
Dim dummy As Integer
Let dummy = Console.WriteLine("Testing Console.WriteLine!")
Let dummy = Console.Write("No newline test... ")
Let dummy = Console.WriteLine("done!")

REM Test Math namespace  
Dim angle As Single = 1.5708
Dim result As Single = Math.Sin(angle)
Let dummy = Console.WriteLine("Math.Sin(π/2) ≈ " + FormatF("%.4f", result))

Dim sqrtResult As Single = Math.Sqrt(16.0)
Let dummy = Console.WriteLine("Math.Sqrt(16) = " + FormatF("%.1f", sqrtResult))

Dim pi As Single = Math.PI()
Let dummy = Console.WriteLine("Math.PI = " + FormatF("%.5f", pi))

Print "Namespace syntax test complete!"

