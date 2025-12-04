' Test Http namespace
' Phase 9: Http.Get, Http.UrlEncode, Http.UrlDecode

' Test URL encoding
Dim text As String = "Hello World!"
Dim encoded As String = Http.UrlEncode(text)
Console.WriteLine("Encoded: " + encoded)

Dim decoded As String = Http.UrlDecode(encoded)
Console.WriteLine("Decoded: " + decoded)

' Note: Http.Get requires internet connection
' Uncomment to test:
' response = Http.Get("https://api.github.com")
' Console.WriteLine(response)

Console.WriteLine("Http namespace test complete!")
