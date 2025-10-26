REM Test real JSON with Gson

REM Test creating and serializing JSON
newObj = Json.NewObject()
Json.Put(newObj, "app", "TestApp")
Json.Put(newObj, "name", "Bob")
Json.PutInt(newObj, "age", 25)
Json.PutInt(newObj, "version", 1)
output = Json.ToString(newObj)
Console.WriteLine("Created JSON: " + output)

REM Test parsing the JSON we just created
parsed = Json.Parse(output)
Console.WriteLine("Parsed back: " + parsed)

appName = Json.GetString(parsed, "app")
userName = Json.GetString(parsed, "name")
userAge = Json.GetInt(parsed, "age")

Console.WriteLine("App: " + appName)
Console.WriteLine("User: " + userName)
Console.WriteLine("Age: " + userAge)

Console.WriteLine("JSON test complete!")

