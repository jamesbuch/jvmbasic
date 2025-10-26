REM ====================================================
REM Modern JVM BASIC Syntax Demo
REM Phase 10: Modern VB-style syntax showcase
REM ====================================================

FUNCTION Add(a As Integer, b As Integer) As Integer
    RETURN a + b
ENDFUNCTION

FUNCTION Multiply(x As Float, y As Float) As Float
    RETURN x * y
ENDFUNCTION

REM ===== Main Program =====

REM Modern variable declarations
x = 10
y = 20
sum = Add(x, y)

REM Console namespace
dummy = Console.WriteLine("=== Modern JVM BASIC Demo ===")
dummy = Console.WriteLine("")

REM Math operations
dummy = Console.WriteLine("Addition: 10 + 20 = " + sum)

a = 3.5
b = 2.0
product = Multiply(a, b)
dummy = Console.WriteLine("Multiplication: 3.5 * 2.0 = " + product)

REM Math namespace
angle = 1.5708
sine = Math.Sin(angle)
dummy = Console.WriteLine("Math.Sin(1.5708) = " + sine)

pi = Math.PI()
dummy = Console.WriteLine("Math.PI = " + pi)

REM Bitwise operations
dummy = Console.WriteLine("")
dummy = Console.WriteLine("Bitwise operations:")
num = 5
leftShift = num << 2
dummy = Console.WriteLine("5 << 2 = " + leftShift)

rightShift = 20 >> 1
dummy = Console.WriteLine("20 >> 1 = " + rightShift)

REM JSON operations
dummy = Console.WriteLine("")
dummy = Console.WriteLine("JSON operations:")
obj = Json.NewObject()
r = Json.Put(obj, "language", "JVM BASIC")
r = Json.PutInt(obj, "version", 9)
jsonStr = Json.ToString(obj)
dummy = Console.WriteLine(jsonStr)

REM HTTP URL encoding
dummy = Console.WriteLine("")
dummy = Console.WriteLine("HTTP operations:")
text = "Hello World"
encoded = Http.UrlEncode(text)
dummy = Console.WriteLine("Encoded: " + encoded)

REM File operations  
dummy = Console.WriteLine("")
dummy = Console.WriteLine("File operations:")
fileContent = "Modern JVM BASIC rocks!"
r = File.WriteAllText("demo.txt", fileContent)
readBack = File.ReadAllText("demo.txt")
dummy = Console.WriteLine("File content: " + readBack)
r = File.Delete("demo.txt")

dummy = Console.WriteLine("")
dummy = Console.WriteLine("=== Demo Complete ===")
Console.WriteLine("JVM BASIC is now a modern, professional language!")

