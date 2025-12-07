' webserver_hello.bas - Simple Hello World Web Server
' =====================================================================
' Demonstrates the embedded Jetty web server with basic routing.
'
' PREREQUISITES:
' 1. Jetty jars in lib/ directory (jetty-server, jetty-servlet, etc.)
' 2. Jakarta Servlet API jar
'
' TO RUN:
' 1. Compile: ./jvmbasic examples/webserver_hello.bas -o HelloServer
' 2. Run: java -cp '.:basicrt:lib/*' HelloServer
' 3. Visit: http://localhost:8080/hello

' Handler subroutine for GET /hello
Sub HandleHello()
    Dim userName As String = Request.GetParameter("name")
    If Len(userName) < 1 Then
        userName = "World"
    End If

    ' Capture request info to variables for interpolation
    Dim method As String = Request.GetMethod()
    Dim path As String = Request.GetPath()
    Dim clientIp As String = Request.GetRemoteAddr()

    Response.SetContentType("text/html")
    Response.Write("<html><body>")
    Response.Write($"<h1>Hello, {userName}!</h1>")
    Response.Write($"<p>Request method: {method}</p>")
    Response.Write($"<p>Request path: {path}</p>")
    Response.Write($"<p>Client IP: {clientIp}</p>")
    Response.Write("</body></html>")
End Sub

' Handler for GET /api/status (JSON response)
Sub HandleStatus()
    Response.SetContentType("application/json")
    Response.Write("{\"status\": \"ok\"}")
End Sub

' Main program
Console.WriteLine("Starting JVM BASIC Web Server...")

Dim server As Integer = WebServer.Create(8080)
If server < 0 Then
    Console.WriteLine("Failed to create server!")
Else
    ' Register routes - use class name and method name
    WebServer.AddRoute(server, "GET", "/hello", "HelloServer", "HandleHello")
    WebServer.AddRoute(server, "GET", "/api/status", "HelloServer", "HandleStatus")

    Console.WriteLine("Routes registered:")
    Console.WriteLine("  GET /hello - HTML greeting")
    Console.WriteLine("  GET /api/status - JSON status")

    WebServer.Start(server)
    Console.WriteLine("")
    Console.WriteLine("Server running on http://localhost:8080")
    Console.WriteLine("Press Ctrl+C to stop")

    WebServer.Join(server)
End If
