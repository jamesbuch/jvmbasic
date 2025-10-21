REM Comprehensive test for ALL Phase 9 namespaces
REM Tests Console, Math, File, Http, Json, Xml, Db

Print "=== Testing All Namespaces ==="
Print ""

' Math namespace
Print "Math Namespace:"
Print "  PI =", Math.PI()
Print "  Sqrt(16) =", Math.Sqrt(16.0)
Print ""

' File namespace  
Print "File Namespace:"
Let dummy = File.WriteAllText("test_all.txt", "Test content")
Print "  File created"
Print ""

' Http namespace
Print "Http Namespace:"
Print "  UrlEncode result:", Http.UrlEncode("Hello World")
Print ""

' Json namespace
Print "Json Namespace:"
Dim jsonObj As Integer = Json.NewObject()
Let dummy = Json.Put(jsonObj, "name", "Test")
Print "  JSON created"
Print ""

' Xml namespace (placeholder)
Print "Xml Namespace:"
Print "  XML parsed:", Xml.Parse("<test>content</test>")
Print ""

' Db namespace (placeholder)
Print "Db Namespace:"
Print "  DB connected:", Db.Connect("jdbc:test", "user", "pass")
Print ""

Print "=== All Namespace Tests Complete ==="

