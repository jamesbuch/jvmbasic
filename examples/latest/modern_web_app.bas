REM ====================================================
REM Modern Web Application Demo
REM Phase 9: Showcasing modern VB-style syntax
REM Features: Namespaces, typed variables, JSON, HTTP, File I/O
REM ====================================================

Function CalculateTotal(price As Single, taxRate As Single) As Single
    Return price * (1.0 + taxRate)
End Function

Function FormatCurrency(amount As Single) As String
    Return "$" + FormatF("%.2f", amount)
End Function

Sub DisplayBanner()
    Dim dummy As Integer
    Let dummy = Console.WriteLine("=====================================")
    Let dummy = Console.WriteLine("  Modern JVM BASIC Web Application  ")
    Let dummy = Console.WriteLine("  Phase 9 - VB-Style Syntax Demo    ")
    Let dummy = Console.WriteLine("=====================================")
End Sub

REM ===== Main Program =====

Call DisplayBanner()

REM Typed variable declarations
Dim appName As String = "WebApp Demo"
Dim version As Single = 1.0
Dim active As Boolean = True

Dim dummy As Integer = Console.WriteLine("")
Let dummy = Console.WriteLine("Application: " + appName)
Let dummy = Console.WriteLine("Version: " + FormatF("%.1f", version))

REM File operations
Dim dataFile As String = "app_data.txt"
Dim appData As String = "AppName: " + appName + ", Version: " + FormatF("%.1f", version)
Dim writeResult As Integer = File.WriteAllText(dataFile, appData)

If File.Exists(dataFile) == 1 Then
    Let dummy = Console.WriteLine("Data file created successfully")
    Dim content As String = File.ReadAllText(dataFile)
    Let dummy = Console.WriteLine("Content: " + content)
Else
    Let dummy = Console.WriteLine("Failed to create data file")
End If

REM JSON operations
Let dummy = Console.WriteLine("")
Let dummy = Console.WriteLine("Creating JSON data...")
Dim jsonObj As Integer = Json.NewObject()
Let writeResult = Json.Put(jsonObj, "app", appName)
Let writeResult = Json.PutInt(jsonObj, "users", 42)
Dim jsonString As String = Json.ToString(jsonObj)
Let dummy = Console.WriteLine("JSON: " + jsonString)

REM Math calculations
Let dummy = Console.WriteLine("")
Let dummy = Console.WriteLine("Math calculations...")
Dim price As Single = 99.99
Dim tax As Single = 0.08
Dim total As Single = CalculateTotal(price, tax)
Let dummy = Console.WriteLine("Price: " + FormatCurrency(price))
Let dummy = Console.WriteLine("Tax Rate: " + FormatF("%.0f", tax * 100.0) + "%")
Let dummy = Console.WriteLine("Total: " + FormatCurrency(total))

REM Bitwise operations
Let dummy = Console.WriteLine("")
Let dummy = Console.WriteLine("Bitwise operations...")
Dim flags As Integer = 5
Dim shifted As Integer = flags << 2
Let dummy = Console.WriteLine("Flags: " + FormatI("%d", flags) + " << 2 = " + FormatI("%d", shifted))

REM URL encoding (for web requests)
Let dummy = Console.WriteLine("")
Let dummy = Console.WriteLine("URL encoding...")
Dim query As String = "Hello World"
Dim encoded As String = Http.UrlEncode(query)
Let dummy = Console.WriteLine("Encoded: " + encoded)

REM Clean up
Let writeResult = File.Delete(dataFile)

Let dummy = Console.WriteLine("")
Let dummy = Console.WriteLine("Demo complete!")
Let dummy = Console.WriteLine("JVM BASIC is now a modern, professional language!")

