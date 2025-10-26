' ====================================================
' Modern Web Application Demo - Phase 9
' Showcasing modern VB-style syntax with expression statements
' Features: Namespaces, typed variables, JSON, HTTP, File I/O, Bitwise
' ====================================================

Function CalculateTotal(price As Single, taxRate As Single) As Single
    Return price * (1.0 + taxRate)
End Function

Function FormatCurrency(amount As Single) As String
    Return "$" + FormatF("%.2f", amount)
End Function

Sub DisplayBanner()
    Print "====================================="
    Print "  Modern JVM BASIC Web Application  "
    Print "  Phase 9 - VB-Style Syntax Demo    "
    Print "====================================="
End Sub

' ===== Main Program =====

Call DisplayBanner()

' Typed variable declarations
Dim appName As String = "WebApp Demo"
Dim version As Single = 1.0
Dim active As Boolean = True

Print ""
Print "Application: "; appName
Print "Version: "; version

' File operations using File namespace
Dim dataFile As String = "app_data.txt"
Dim appData As String = "AppName: " + appName + ", Version: " + FormatF("%.1f", version)
File.WriteAllText(dataFile, appData)

If File.Exists(dataFile) == 1 Then
    Print "Data file created successfully"
    Dim content As String = File.ReadAllText(dataFile)
    Print "Content: "; content
Else
    Print "Failed to create data file"
End If

' JSON operations using Json namespace
Print ""
Print "Creating JSON data..."
Dim jsonObj As Integer = Json.NewObject()
Json.Put(jsonObj, "app", appName)
Json.PutInt(jsonObj, "users", 42)
Dim jsonString As String = Json.ToString(jsonObj)
Print "JSON: "; jsonString

' Math calculations using Math namespace
Print ""
Print "Math calculations..."
Dim price As Single = 99.99
Dim tax As Single = 0.08
Dim total As Single = CalculateTotal(price, tax)
Print "Price: "; FormatCurrency(price)
Print "Tax Rate: "; FormatF("%.0f", tax * 100.0); "%"
Print "Total: "; FormatCurrency(total)

' Bitwise operations demonstration
Print ""
Print "Bitwise operations..."
Dim flags As Integer = 5
Dim shifted As Integer = flags << 2
Dim masked As Integer = flags & 3
Dim combined As Integer = 1 | 2 | 4
Dim toggled As Integer = flags ^ 3
Print "Flags: "; flags; " << 2 = "; shifted
Print "Bitwise AND: "; flags; " & 3 = "; masked
Print "Bitwise OR: 1 | 2 | 4 = "; combined
Print "Bitwise XOR: "; flags; " ^ 3 = "; toggled

' URL encoding for web requests using Http namespace
Print ""
Print "URL encoding..."
Dim query As String = "Hello World"
Dim encoded As String = Http.UrlEncode(query)
Print "Original: "; query
Print "Encoded: "; encoded

' Clean up
File.Delete(dataFile)

Print ""
Print "Demo complete!"
Print "JVM BASIC is now a modern, professional language!"
