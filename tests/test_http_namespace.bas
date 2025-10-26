REM Test Http namespace
REM Phase 9: Http.Get, Http.UrlEncode, Http.UrlDecode

REM Test URL encoding
text = "Hello World!"
encoded = Http.UrlEncode(text)
dummy = Console.WriteLine("Encoded: " + encoded)

decoded = Http.UrlDecode(encoded)
dummy = Console.WriteLine("Decoded: " + decoded)

REM Note: Http.Get requires internet connection
REM Uncomment to test:
REM response = Http.Get("https://api.github.com")
REM Console.WriteLine(response)

Console.WriteLine("Http namespace test complete!")

