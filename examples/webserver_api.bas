' webserver_api.bas - REST API Web Server Example
' =====================================================================
' Demonstrates path parameters, JSON parsing, and static file serving.
'
' PREREQUISITES:
' 1. Jetty jars in lib/ directory
' 2. Jakarta Servlet API jar
'
' TO RUN:
' 1. Create static files: mkdir -p public && echo "<h1>Welcome</h1>" > public/index.html
' 2. Compile: ./jvmbasic examples/webserver_api.bas -o ApiServer
' 3. Run: java -cp '.:basicrt:lib/*' ApiServer
' 4. Test:
'    - curl http://localhost:8080/static/index.html (static file)
'    - curl http://localhost:8080/api/users (list users)
'    - curl http://localhost:8080/api/users/42 (get user by ID)
'    - curl -X POST -H "Content-Type: application/json" \
'           -d '{"name":"Alice","email":"alice@example.com"}' \
'           http://localhost:8080/api/users (create user)

' Simulated user database (using static data)
Dim userCount As Integer = 2

' GET /api/users - List all users
Sub HandleListUsers()
    Response.SetContentType("application/json")
    Response.Write("[")
    Response.Write("{\"id\": 1, \"name\": \"John\", \"email\": \"john@example.com\"},")
    Response.Write("{\"id\": 2, \"name\": \"Jane\", \"email\": \"jane@example.com\"}")
    Response.Write("]")
End Sub

' GET /api/users/{id} - Get user by ID
Sub HandleGetUser()
    Dim userId As String = Request.GetPathParam("id")
    Dim found As Integer = 0
    Dim cmp As Integer = 0

    Response.SetContentType("application/json")

    ' Use strcmp for string equality (returns 0 if equal)
    cmp = Strcmp(userId, "1")
    If cmp < 1 Then
        If cmp > -1 Then
            Response.Write("{\"id\": 1, \"name\": \"John\", \"email\": \"john@example.com\"}")
            found = 1
        End If
    End If

    If found < 1 Then
        cmp = Strcmp(userId, "2")
        If cmp < 1 Then
            If cmp > -1 Then
                Response.Write("{\"id\": 2, \"name\": \"Jane\", \"email\": \"jane@example.com\"}")
                found = 1
            End If
        End If
    End If

    If found < 1 Then
        Response.SetStatus(404)
        Response.Write("{\"error\": \"User not found\", \"id\": \"" + userId + "\"}")
    End If
End Sub

' POST /api/users - Create new user
Sub HandleCreateUser()
    Dim jsonHandle As Integer = Request.GetJsonBody()

    Response.SetContentType("application/json")

    If jsonHandle < 0 Then
        Response.SetStatus(400)
        Response.Write("{\"error\": \"Invalid JSON body\"}")
    Else
        Dim name As String = Json.GetString(jsonHandle, "name")
        Dim email As String = Json.GetString(jsonHandle, "email")

        If Len(name) < 1 Then
            Response.SetStatus(400)
            Response.Write("{\"error\": \"Name is required\"}")
        Else
            Response.SetStatus(201)
            Response.Write("{\"id\": 3, \"name\": \"" + name + "\", \"email\": \"" + email + "\", \"message\": \"User created\"}")
        End If
    End If
End Sub

' GET /api/health - Health check endpoint
Sub HandleHealth()
    Response.SetContentType("application/json")
    Response.Write("{\"status\": \"healthy\", \"service\": \"JVM BASIC API\"}")
End Sub

' Main program
Console.WriteLine("Starting JVM BASIC REST API Server...")

Dim server As Integer = WebServer.Create(8080)
If server < 0 Then
    Console.WriteLine("Failed to create server!")
Else
    ' Serve static files from ./public directory at /static URL prefix
    WebServer.ServeStatic(server, "/static", "./public")

    ' API routes
    WebServer.AddRoute(server, "GET", "/api/health", "ApiServer", "HandleHealth")
    WebServer.AddRoute(server, "GET", "/api/users", "ApiServer", "HandleListUsers")
    WebServer.AddRoute(server, "GET", "/api/users/{id}", "ApiServer", "HandleGetUser")
    WebServer.AddRoute(server, "POST", "/api/users", "ApiServer", "HandleCreateUser")

    Console.WriteLine("Routes registered:")
    Console.WriteLine("  GET  /static/*         - Static files from ./public")
    Console.WriteLine("  GET  /api/health       - Health check")
    Console.WriteLine("  GET  /api/users        - List users")
    Console.WriteLine("  GET  /api/users/{id}   - Get user by ID")
    Console.WriteLine("  POST /api/users        - Create user (JSON body)")

    WebServer.Start(server)
    Console.WriteLine("")
    Console.WriteLine("Server running on http://localhost:8080")
    Console.WriteLine("Press Ctrl+C to stop")

    WebServer.Join(server)
End If
