' http_fetch_urls.bas - HTTP fetch and URL extraction example
' Demonstrates Http.Get and Regex to extract URLs from web pages
' Uses httpbin.org for reliable, repeatable test results

Console.WriteLine("=== HTTP Fetch and URL Extraction Demo ===")
Console.WriteLine("")

' Test 1: Fetch from httpbin.org (reliable test service)
Console.WriteLine("--- Test 1: HTTP GET Request ---")
Dim response As String
response = Http.Get("https://httpbin.org/html")

If Len(response) > 0 Then
    Console.WriteLine("Response length: " + Len(response) + " characters")

    ' Check for expected content in the Herman Melville story
    If Regex.Match("Moby Dick", response) Then
        Console.WriteLine("Found 'Moby Dick' in response - SUCCESS")
    Else
        Console.WriteLine("Did not find expected content")
    End If

    ' Extract the title
    Dim title As String = Regex.Group("<title>([^<]+)</title>", response, 1)
    Console.WriteLine("Page title: " + title)
Else
    Console.WriteLine("Failed to fetch response")
End If
Console.WriteLine("")

' Test 2: Fetch JSON and parse
Console.WriteLine("--- Test 2: JSON Response ---")
Dim jsonResponse As String
jsonResponse = Http.Get("https://httpbin.org/json")

If Len(jsonResponse) > 0 Then
    Console.WriteLine("JSON response received")
    ' Find the slideshow title
    Dim slideTitle As String = Regex.Find("\"title\":\\s*\"([^\"]+)\"", jsonResponse)
    Console.WriteLine("Found title pattern: " + slideTitle)
End If
Console.WriteLine("")

' Test 3: Fetch headers info
Console.WriteLine("--- Test 3: Headers Echo ---")
Dim headersResponse As String
headersResponse = Http.Get("https://httpbin.org/headers")

If Len(headersResponse) > 0 Then
    Console.WriteLine("Headers response received")
    ' Extract User-Agent
    Dim userAgent As String = Regex.Group("\"User-Agent\":\\s*\"([^\"]+)\"", headersResponse, 1)
    Console.WriteLine("User-Agent: " + userAgent)
End If
Console.WriteLine("")

' Test 4: IP address lookup
Console.WriteLine("--- Test 4: IP Address ---")
Dim ipResponse As String
ipResponse = Http.Get("https://httpbin.org/ip")

If Len(ipResponse) > 0 Then
    Dim ip As String = Regex.Group("\"origin\":\\s*\"([^\"]+)\"", ipResponse, 1)
    Console.WriteLine("Your IP: " + ip)
End If
Console.WriteLine("")

' Test 5: URL encoding/decoding
Console.WriteLine("--- Test 5: URL Encoding ---")
Dim original As String = "Hello World! Special chars: &=?"
Dim encoded As String = Http.UrlEncode(original)
Dim decoded As String = Http.UrlDecode(encoded)

Console.WriteLine("Original: " + original)
Console.WriteLine("Encoded:  " + encoded)
Console.WriteLine("Decoded:  " + decoded)

If original = decoded Then
    Console.WriteLine("Round-trip encoding: SUCCESS")
Else
    Console.WriteLine("Round-trip encoding: FAILED")
End If
Console.WriteLine("")

Console.WriteLine("=== HTTP Demo Complete ===")
