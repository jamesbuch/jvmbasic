' ====================================================
' Modern JVM BASIC Syntax Demo
' Phase 10: Modern VB-style syntax showcase
' ====================================================

FUNCTION Add(a As Integer, b As Integer) As Integer
    RETURN a + b
ENDFUNCTION

FUNCTION Multiply(x As Single, y As Single) As Single
    RETURN x * y
ENDFUNCTION

' ===== Main Program =====

' Modern variable declarations
Dim x As Integer
Dim y As Integer
Dim sum As Integer
Dim a As Single
Dim b As Single
Dim product As Single
Dim angle As Single
Dim sine As Single
Dim pi As Single
Dim num As Integer
Dim leftShift As Integer
Dim rightShift As Integer
Dim obj As Integer
Dim r As Integer
Dim jsonStr As String
Dim text As String
Dim encoded As String
Dim fileContent As String
Dim readBack As String

x = 10
y = 20
sum = Add(x, y)

' Console namespace
Console.WriteLine("=== Modern JVM BASIC Demo ===")
Console.WriteLine("")

' Math operations
Console.WriteLine("Addition: 10 + 20 = " + sum)

a = 3.5
b = 2.0
product = Multiply(a, b)
Console.WriteLine("Multiplication: 3.5 * 2.0 = " + product)

' Math namespace
angle = 1.5708
sine = Math.Sin(angle)
Console.WriteLine("Math.Sin(1.5708) = " + sine)

pi = Math.PI()
Console.WriteLine("Math.PI = " + pi)

' Bitwise operations
Console.WriteLine("")
Console.WriteLine("Bitwise operations:")
num = 5
leftShift = num << 2
Console.WriteLine("5 << 2 = " + leftShift)

rightShift = 20 >> 1
Console.WriteLine("20 >> 1 = " + rightShift)

' JSON operations
Console.WriteLine("")
Console.WriteLine("JSON operations:")
obj = Json.NewObject()
r = Json.Put(obj, "language", "JVM BASIC")
r = Json.PutInt(obj, "version", 9)
jsonStr = Json.ToString(obj)
Console.WriteLine(jsonStr)

' HTTP URL encoding
Console.WriteLine("")
Console.WriteLine("HTTP operations:")
text = "Hello World"
encoded = Http.UrlEncode(text)
Console.WriteLine("Encoded: " + encoded)

' File operations
Console.WriteLine("")
Console.WriteLine("File operations:")
fileContent = "Modern JVM BASIC rocks!"
r = File.WriteAllText("demo.txt", fileContent)
readBack = File.ReadAllText("demo.txt")
Console.WriteLine("File content: " + readBack)
r = File.Delete("demo.txt")

Console.WriteLine("")
Console.WriteLine("=== Demo Complete ===")
Console.WriteLine("JVM BASIC is now a modern, professional language!")
