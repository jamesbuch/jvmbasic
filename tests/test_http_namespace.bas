REM Test Http namespace
REM Phase 9: Http.Get, Http.UrlEncode, Http.UrlDecode

REM Test URL encoding
Dim text As String = "Hello World!"
Dim encoded As String = Http.UrlEncode(text)
Dim dummy As Integer = Console.WriteLine("Encoded: " + encoded)

Dim decoded As String = Http.UrlDecode(encoded)
Let dummy = Console.WriteLine("Decoded: " + decoded)

REM Note: Http.Get requires internet connection
REM Uncomment to test:
REM Dim response As String = Http.Get("https://api.github.com")
REM Console.WriteLine(response)

Print "Http namespace test complete!"

