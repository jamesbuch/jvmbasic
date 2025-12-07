' Test Json namespace - simple version
' Phase 9: Json.NewObject, Json.Put, Json.ToString

' Create a JSON object
Dim obj As Integer = Json.NewObject()
Dim r1 As Integer = Json.Put(obj, "name", "Alice")
Dim r2 As Integer = Json.PutInt(obj, "age", 30)
Dim jsonStr As String = Json.ToString(obj)
Console.WriteLine(jsonStr)

Console.WriteLine("Json namespace test complete!")
