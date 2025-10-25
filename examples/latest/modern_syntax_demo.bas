REM ====================================================
REM Modern JVM BASIC Syntax Demo
REM Phase 9: Modern VB-style syntax showcase
REM ====================================================

Function Add(a As Integer, b As Integer) As Integer
    Return a + b
End Function

Function Multiply(x As Single, y As Single) As Single
    Return x * y
End Function

REM ===== Main Program =====

REM Modern variable declarations
Dim x As Integer = 10
Dim y As Integer = 20
Dim sum As Integer = Add(x, y)

REM Console namespace
Dim dummy As Integer = Console.WriteLine("=== Modern JVM BASIC Demo ===")
Let dummy = Console.WriteLine("")

REM Math operations
Let dummy = Console.WriteLine("Addition: 10 + 20 = " + FormatI("%d", sum))

Dim a As Single = 3.5
Dim b As Single = 2.0
Dim product As Single = Multiply(a, b)
Let dummy = Console.WriteLine("Multiplication: 3.5 * 2.0 = " + FormatF("%.1f", product))

REM Math namespace
Dim angle As Single = 1.5708
Dim sine As Single = Math.Sin(angle)
Let dummy = Console.WriteLine("Math.Sin(1.5708) = " + FormatF("%.4f", sine))

Dim pi As Single = Math.PI()
Let dummy = Console.WriteLine("Math.PI = " + FormatF("%.5f", pi))

REM Bitwise operations
Let dummy = Console.WriteLine("")
Let dummy = Console.WriteLine("Bitwise operations:")
Dim num As Integer = 5
Dim leftShift As Integer = num << 2
Let dummy = Console.WriteLine("5 << 2 = " + FormatI("%d", leftShift))

Dim rightShift As Integer = 20 >> 1
Let dummy = Console.WriteLine("20 >> 1 = " + FormatI("%d", rightShift))

REM JSON operations
Let dummy = Console.WriteLine("")
Let dummy = Console.WriteLine("JSON operations:")
Dim obj As Integer = Json.NewObject()
Dim r As Integer = Json.Put(obj, "language", "JVM BASIC")
Let r = Json.PutInt(obj, "version", 9)
Dim jsonStr As String = Json.ToString(obj)
Let dummy = Console.WriteLine(jsonStr)

REM HTTP URL encoding
Let dummy = Console.WriteLine("")
Let dummy = Console.WriteLine("HTTP operations:")
Dim text As String = "Hello World"
Dim encoded As String = Http.UrlEncode(text)
Let dummy = Console.WriteLine("Encoded: " + encoded)

REM File operations  
Let dummy = Console.WriteLine("")
Let dummy = Console.WriteLine("File operations:")
Dim fileContent As String = "Modern JVM BASIC rocks!"
Let r = File.WriteAllText("demo.txt", fileContent)
Dim readBack As String = File.ReadAllText("demo.txt")
Let dummy = Console.WriteLine("File content: " + readBack)
Let r = File.Delete("demo.txt")

Let dummy = Console.WriteLine("")
Console.WriteLine("=== Demo Complete ===")
Console.WriteLine("JVM BASIC is now a modern, professional language!")

