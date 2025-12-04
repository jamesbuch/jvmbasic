' ====================================================
' Modern Web Application Demo - Phase 10
' Showcasing modern VB-style syntax with expression statements
' Features: Namespaces, typed variables, JSON, HTTP, File I/O, Bitwise
' ====================================================

Function CalculateTotal(price As Float, taxRate As Float) As Float
    Return price * (1.0 + taxRate)
EndFunction

Function FormatCurrency(amount As Float) As String
    Return "$" + amount
EndFunction

Sub DisplayBanner()
    Console.WriteLine("=====================================")
    Console.WriteLine("  Modern JVM BASIC Web Application  ")
    Console.WriteLine("  Phase 10 - VB-Style Syntax Demo    ")
    Console.WriteLine("=====================================")
EndSub

' ===== Main Program =====

Call DisplayBanner()

' Modern variable declarations
Dim appName As String
Dim version As Float
Dim active As Boolean
Dim price As Float
Dim taxRate As Float
Dim total As Float
Dim obj As Integer
Dim r As Integer
Dim jsonStr As String
Dim text As String
Dim encoded As String
Dim fileContent As String
Dim readBack As String
Dim flags As Integer
Dim mask As Integer
Dim result As Integer
Dim shifted As Integer
Dim angle As Float
Dim sine As Float
Dim pi As Float

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
Console.WriteLine("Math.Sin(pi/2) = " + sine)

pi = Math.PI()
Console.WriteLine("Math.PI = " + pi)
Console.WriteLine("")

Console.WriteLine("=====================================")
Console.WriteLine("  Web Application Demo Complete!    ")
Console.WriteLine("=====================================")
