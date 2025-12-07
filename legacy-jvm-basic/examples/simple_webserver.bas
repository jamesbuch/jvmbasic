' simple_webserver.bas - Simple HTTP/1.0 Web Server
' ================================================
' NOTE: This example requires Socket namespace support (not yet implemented)
' See docs/planning/SOCKETS_SUPPORT.md for implementation plan
'
' SETUP INSTRUCTIONS:
' 1. Create a wwwroot directory in the same folder as the compiled program:
'    mkdir wwwroot
'
' 2. Create sample files:
'    wwwroot/index.html    - Main HTML page
'    wwwroot/style.css     - CSS stylesheet
'    wwwroot/script.js     - JavaScript file
'    wwwroot/images/       - Image folder
'
' 3. Example index.html:
'    <!DOCTYPE html>
'    <html>
'    <head>
'        <title>JVM BASIC Web Server</title>
'        <link rel="stylesheet" href="style.css">
'    </head>
'    <body>
'        <h1>Welcome to JVM BASIC Web Server!</h1>
'        <p>This page is served by a BASIC program.</p>
'        <script src="script.js"></script>
'    </body>
'    </html>
'
' 4. Run the server and visit http://localhost:8080/
'
' TO START: Run this program
' TO STOP: Press Ctrl+C

Dim PORT As Integer = 8080
Dim WWWROOT As String = "./wwwroot"
Dim running As Boolean = True

Console.WriteLine("========================================")
Console.WriteLine("  JVM BASIC Simple Web Server v1.0")
Console.WriteLine("========================================")
Console.WriteLine("")
Console.WriteLine("Document root: " + WWWROOT)
Console.WriteLine("Starting server on port " + PORT + "...")

' Check if wwwroot exists
Dim wwwExists As Integer = File.Exists(WWWROOT)
If wwwExists < 1 Then
    Console.WriteLine("ERROR: wwwroot directory not found!")
    Console.WriteLine("Please create: " + WWWROOT)
    Console.WriteLine("See comments in source for setup instructions.")
    System.exit(1)
End If

' NOTE: Socket namespace not yet implemented
' The following code shows the intended API
Console.WriteLine("")
Console.WriteLine("NOTE: Socket support not yet implemented.")
Console.WriteLine("This is a demonstration of the planned API.")
Console.WriteLine("")
Console.WriteLine("When implemented, server would be available at:")
Console.WriteLine("  http://localhost:" + PORT + "/")
Console.WriteLine("")

' Placeholder - when sockets are implemented:
'
' Dim server As Integer = Socket.Listen(PORT)
' Console.WriteLine("Server listening on http://localhost:" + PORT + "/")
'
' While running
'     Dim client As Integer = Socket.Accept(server)
'     Dim clientIP As String = Socket.GetRemoteAddress(client)
'
'     ' Read HTTP request
'     Dim request As String = Socket.ReadLine(client)
'     Console.WriteLine(DateTime.Now() + " - " + clientIP + " - " + request)
'
'     ' Parse request
'     Dim method As String = Regex.Group("^(\\w+)", request, 1)
'     Dim path As String = Regex.Group("\\s(/[^\\s]*)", request, 1)
'
'     ' Default to index.html
'     If path = "/" Then path = "/index.html"
'
'     ' Security: prevent path traversal
'     If Regex.Match("\\.\\.", path) Then
'         SendError(client, 403, "Forbidden")
'     Else
'         ServeFile(client, path)
'     End If
'
'     Socket.Close(client)
' Wend
'
' Socket.Close(server)

' For now, just demonstrate the concept
Console.WriteLine("Press Enter to exit...")
Dim dummy As String = Console.ReadLine()
Console.WriteLine("Server stopped.")

' Sub ServeFile(client As Integer, path As String)
'     Dim filePath As String = WWWROOT + path
'
'     If File.Exists(filePath) Then
'         Dim content As String = File.ReadAll(filePath)
'         Dim contentType As String = GetMimeType(path)
'
'         Socket.Write(client, "HTTP/1.0 200 OK\r\n")
'         Socket.Write(client, "Content-Type: " + contentType + "\r\n")
'         Socket.Write(client, "Content-Length: " + Len(content) + "\r\n")
'         Socket.Write(client, "Connection: close\r\n")
'         Socket.Write(client, "\r\n")
'         Socket.Write(client, content)
'     Else
'         SendError(client, 404, "Not Found")
'     End If
' End Sub
'
' Sub SendError(client As Integer, code As Integer, message As String)
'     Dim body As String = "<html><body><h1>" + code + " " + message + "</h1></body></html>"
'     Socket.Write(client, "HTTP/1.0 " + code + " " + message + "\r\n")
'     Socket.Write(client, "Content-Type: text/html\r\n")
'     Socket.Write(client, "Content-Length: " + Len(body) + "\r\n")
'     Socket.Write(client, "\r\n")
'     Socket.Write(client, body)
' End Sub
'
' Function GetMimeType(path As String) As String
'     If Regex.Match("\\.html?$", path) Then Return "text/html"
'     If Regex.Match("\\.css$", path) Then Return "text/css"
'     If Regex.Match("\\.js$", path) Then Return "application/javascript"
'     If Regex.Match("\\.json$", path) Then Return "application/json"
'     If Regex.Match("\\.png$", path) Then Return "image/png"
'     If Regex.Match("\\.jpe?g$", path) Then Return "image/jpeg"
'     If Regex.Match("\\.gif$", path) Then Return "image/gif"
'     If Regex.Match("\\.svg$", path) Then Return "image/svg+xml"
'     If Regex.Match("\\.ico$", path) Then Return "image/x-icon"
'     If Regex.Match("\\.txt$", path) Then Return "text/plain"
'     If Regex.Match("\\.xml$", path) Then Return "application/xml"
'     Return "application/octet-stream"
' End Function
