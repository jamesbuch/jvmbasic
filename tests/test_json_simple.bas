REM Test Json namespace - simple version
REM Phase 9: Json.NewObject, Json.Put, Json.ToString

REM Create a JSON object
Dim obj As Integer = Json.NewObject()
Dim r1 As Integer = Json.Put(obj, "name", "Alice")
Dim r2 As Integer = Json.PutInt(obj, "age", 30)
Dim jsonStr As String = Json.ToString(obj)
Dim dummy As Integer = Console.WriteLine(jsonStr)

Print "Json namespace test complete!"

