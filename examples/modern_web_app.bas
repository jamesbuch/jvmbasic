' ====================================================
' Modern Web Application Demo - Phase 10
' Showcasing modern VB-style syntax with expression statements
' Features: Namespaces, typed variables, JSON, HTTP, File I/O, Bitwise
' ====================================================

FUNCTION CalculateTotal(price As Float, taxRate As Float) As Float
    RETURN price * (1.0 + taxRate)
ENDFUNCTION

FUNCTION FormatCurrency(amount As Float) As String
    RETURN "$" + amount
ENDFUNCTION

SUB DisplayBanner()
    Console.WriteLine("=====================================")
    Console.WriteLine("  Modern JVM BASIC Web Application  ")
    Console.WriteLine("  Phase 10 - VB-Style Syntax Demo    ")
    Console.WriteLine("=====================================")
ENDSUB

' ===== Main Program =====

CALL DisplayBanner()

' Modern variable declarations
appName = "WebApp Demo"
version = 1.0
active = true

Console.WriteLine("Application: " + appName)
Console.WriteLine("Version: " + version)
Console.WriteLine("Active: " + active)
Console.WriteLine("")

' Math operations
price = 99.99
taxRate = 0.08
total = CalculateTotal(price, taxRate)

Console.WriteLine("Price: " + FormatCurrency(price))
Console.WriteLine("Tax Rate: " + (taxRate * 100) + "%")
Console.WriteLine("Total: " + FormatCurrency(total))
Console.WriteLine("")

' JSON operations
obj = Json.NewObject()
r = Json.Put(obj, "app", appName)
r = Json.PutInt(obj, "version", 10)
r = Json.Put(obj, "status", "active")
jsonStr = Json.ToString(obj)
Console.WriteLine("JSON Object: " + jsonStr)
Console.WriteLine("")

' HTTP operations
text = "Hello World"
encoded = Http.UrlEncode(text)
Console.WriteLine("Original: " + text)
Console.WriteLine("URL Encoded: " + encoded)
Console.WriteLine("")

' File operations
fileContent = "Web application data"
r = File.WriteAllText("webapp.txt", fileContent)
readBack = File.ReadAllText("webapp.txt")
Console.WriteLine("File content: " + readBack)
r = File.Delete("webapp.txt")
Console.WriteLine("")

' Bitwise operations
flags = 5
mask = 3
result = flags & mask
Console.WriteLine("Bitwise AND: " + flags + " & " + mask + " = " + result)

shifted = flags << 2
Console.WriteLine("Left shift: " + flags + " << 2 = " + shifted)
Console.WriteLine("")

' Math namespace
angle = 1.5708
sine = Math.Sin(angle)
Console.WriteLine("Math.Sin(π/2) = " + sine)

pi = Math.PI()
Console.WriteLine("Math.PI = " + pi)
Console.WriteLine("")

Console.WriteLine("=====================================")
Console.WriteLine("  Web Application Demo Complete!    ")
Console.WriteLine("=====================================")