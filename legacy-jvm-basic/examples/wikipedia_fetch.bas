' wikipedia_fetch.bas - Fetch Wikipedia articles with known content
' Uses Wikipedia API for reliable, structured responses
' Demonstrates HTTP GET with regex parsing

Console.WriteLine("=== Wikipedia Article Fetcher ===")
Console.WriteLine("")

' Wikipedia API returns JSON summaries - more reliable than full HTML
' Format: https://en.wikipedia.org/api/rest_v1/page/summary/ARTICLE_NAME

' Test 1: Fetch Albert Einstein summary (well-known facts)
Console.WriteLine("--- Article: Albert Einstein ---")
Dim einsteinUrl As String = "https://en.wikipedia.org/api/rest_v1/page/summary/Albert_Einstein"
Dim einstein As String = Http.Get(einsteinUrl)

If Len(einstein) > 0 Then
    ' Extract the extract (summary text) using Json namespace
    Dim einsteinObj As Integer = Json.Parse(einstein)
    Dim summary As String = Json.GetString(einsteinObj, "extract")
    summary = Mid(summary, 1, 200)
    Console.WriteLine("Summary: " + summary + "...")

    ' Check for expected facts
    If Regex.Match("physicist", einstein) Then
        Console.WriteLine("Verified: Contains 'physicist' - PASS")
    End If

    If Regex.Match("1879", einstein) Then
        Console.WriteLine("Verified: Contains birth year 1879 - PASS")
    End If

    If Regex.Match("relativity", einstein) Then
        Console.WriteLine("Verified: Mentions relativity - PASS")
    End If
Else
    Console.WriteLine("Failed to fetch article")
End If
Console.WriteLine("")

' Test 2: Fetch Python programming language
Console.WriteLine("--- Article: Python (programming language) ---")
Dim pythonUrl As String = "https://en.wikipedia.org/api/rest_v1/page/summary/Python_(programming_language)"
Dim python As String = Http.Get(pythonUrl)

If Len(python) > 0 Then
    Dim pythonObj As Integer = Json.Parse(python)
    Dim pythonSummary As String = Json.GetString(pythonObj, "extract")
    pythonSummary = Mid(pythonSummary, 1, 200)
    Console.WriteLine("Summary: " + pythonSummary + "...")

    If Regex.Match("programming language", python) Then
        Console.WriteLine("Verified: Is a programming language - PASS")
    End If

    If Regex.Match("Guido", python) Then
        Console.WriteLine("Verified: Mentions Guido van Rossum - PASS")
    End If
Else
    Console.WriteLine("Failed to fetch article")
End If
Console.WriteLine("")

' Test 3: Fetch a simple well-known topic - Moon
Console.WriteLine("--- Article: Moon ---")
Dim moonUrl As String = "https://en.wikipedia.org/api/rest_v1/page/summary/Moon"
Dim moon As String = Http.Get(moonUrl)

If Len(moon) > 0 Then
    Dim moonObj As Integer = Json.Parse(moon)
    Dim moonSummary As String = Json.GetString(moonObj, "extract")
    moonSummary = Mid(moonSummary, 1, 200)
    Console.WriteLine("Summary: " + moonSummary + "...")

    If Regex.Match("Earth", moon) Then
        Console.WriteLine("Verified: Mentions Earth - PASS")
    End If

    If Regex.Match("satellite", moon) Then
        Console.WriteLine("Verified: Described as satellite - PASS")
    End If
Else
    Console.WriteLine("Failed to fetch article")
End If
Console.WriteLine("")

' Test 4: Extract structured data
Console.WriteLine("--- Extracting Structured Data ---")
Dim mathUrl As String = "https://en.wikipedia.org/api/rest_v1/page/summary/Mathematics"
Dim math As String = Http.Get(mathUrl)

If Len(math) > 0 Then
    Dim mathObj As Integer = Json.Parse(math)

    ' Extract title
    Dim pageTitle As String = Json.GetString(mathObj, "title")
    Console.WriteLine("Title: " + pageTitle)

    ' Extract description
    Dim desc As String = Json.GetString(mathObj, "description")
    Console.WriteLine("Description: " + desc)

    ' Check content type
    Dim contentType As String = Json.GetString(mathObj, "type")
    Console.WriteLine("Content type: " + contentType)
End If
Console.WriteLine("")

Console.WriteLine("=== Wikipedia Fetcher Complete ===")
