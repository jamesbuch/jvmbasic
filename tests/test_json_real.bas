Rem Test real JSON with Gson

' Test creating and serializing JSON
Dim newObj As Integer = Json.NewObject()
Json.Put(newObj, "app", "TestApp")
Json.Put(newObj, "name", "Bob")
Json.PutInt(newObj, "age", 25)
Json.PutInt(newObj, "version", 1)
Dim output As String = Json.ToString(newObj)
Print "Created JSON: "; output

' Test parsing the JSON we just created
Dim parsed As Integer = Json.Parse(output)
Print "Parsed back: "; parsed

Dim appName As String = Json.GetString(parsed, "app")
Dim userName As String = Json.GetString(parsed, "name")
Dim userAge As Integer = Json.GetInt(parsed, "age")

Print "App: "; appName
Print "User: "; userName  
Print "Age: "; userAge

Print "JSON test complete!"

