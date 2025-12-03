' Test Http namespace
' Phase 9: Http.Get, Http.UrlEncode, Http.UrlDecode

' Test URL encoding
text = "Hello World!"
encoded = Http.UrlEncode(text)
Console.WriteLine("Encoded: " + encoded)

decoded = Http.UrlDecode(encoded)
Console.WriteLine("Decoded: " + decoded)

' Note: Http.Get requires internet connection
' Uncomment to test:
' response = Http.Get("https://api.github.com")
' Console.WriteLine(response)

Console.WriteLine("Http namespace test complete!")
