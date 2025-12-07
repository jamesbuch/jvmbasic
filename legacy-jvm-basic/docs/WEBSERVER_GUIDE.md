# JVM BASIC Web Server Guide

JVM BASIC includes an embedded Jetty web server that allows you to build web applications entirely in BASIC. This guide covers all the features available for creating web servers, REST APIs, and serving static files.

## Prerequisites

The following JAR files are required in your `lib/` directory:

- `jetty-server-11.0.19.jar`
- `jetty-servlet-11.0.19.jar`
- `jetty-util-11.0.19.jar`
- `jetty-http-11.0.19.jar`
- `jetty-io-11.0.19.jar`
- `jetty-security-11.0.19.jar`
- `jakarta.servlet-api-5.0.0.jar`
- `slf4j-api-2.0.9.jar`
- `slf4j-simple-2.0.9.jar` (optional, for logging)

## Quick Start

Here's a minimal web server that responds to HTTP requests:

```basic
' hello_web.bas
Sub HandleHello()
    Response.SetContentType("text/html")
    Response.Write("<h1>Hello, World!</h1>")
End Sub

Dim server As Integer = WebServer.Create(8080)
WebServer.AddRoute(server, "GET", "/hello", "HelloWeb", "HandleHello")
WebServer.Start(server)
Console.WriteLine("Server running on http://localhost:8080/hello")
WebServer.Join(server)
```

Compile and run:
```bash
./jvmbasic -o HelloWeb < hello_web.bas
java -cp '.:basicrt:lib/*' HelloWeb
```

## WebServer Namespace

### Creating a Server

```basic
Dim server As Integer = WebServer.Create(8080)
```

Creates a new web server on the specified port. Returns a server ID (positive integer) on success, or -1 on error.

### Registering Routes

```basic
WebServer.AddRoute(server, "GET", "/path", "ClassName", "MethodName")
WebServer.AddRoute(server, "POST", "/api/users", "ApiServer", "CreateUser")
WebServer.AddRoute(server, "GET", "/users/{id}", "ApiServer", "GetUser")
```

Parameters:
- `server` - Server ID from `WebServer.Create()`
- HTTP method: "GET", "POST", "PUT", "DELETE", "PATCH"
- URL path pattern (supports `{param}` placeholders)
- Class name (the compiled .class file name)
- Handler method name (must be a `Sub` with no parameters)

### Path Parameters

Routes can include path parameters using `{name}` syntax:

```basic
WebServer.AddRoute(server, "GET", "/users/{id}", "MyApp", "GetUser")
WebServer.AddRoute(server, "GET", "/posts/{postId}/comments/{commentId}", "MyApp", "GetComment")
```

Access path parameters in your handler:
```basic
Sub GetUser()
    Dim userId As String = Request.GetPathParam("id")
    Response.Write("User ID: " + userId)
End Sub
```

### Serving Static Files

```basic
WebServer.ServeStatic(server, "/static", "./public")
```

Parameters:
- `server` - Server ID
- URL prefix (e.g., "/static")
- File system directory path

Files are served with appropriate MIME types for common extensions (html, css, js, json, images, fonts, etc.).

Example:
```basic
' Serve files from ./public at /static/*
WebServer.ServeStatic(server, "/static", "./public")
' Access: http://localhost:8080/static/style.css -> ./public/style.css
```

### Server Lifecycle

```basic
WebServer.Start(server)    ' Start the server (non-blocking)
WebServer.Stop(server)     ' Stop the server
WebServer.Join(server)     ' Block until server stops
Dim running As Integer = WebServer.IsRunning(server)  ' 1 if running, 0 if not
```

## Request Namespace

Access information about the current HTTP request within a handler.

### Basic Request Information

```basic
Dim method As String = Request.GetMethod()        ' "GET", "POST", etc.
Dim path As String = Request.GetPath()            ' "/api/users"
Dim queryString As String = Request.GetQueryString()  ' "name=John&age=30"
Dim contentType As String = Request.GetContentType()  ' "application/json"
Dim clientIp As String = Request.GetRemoteAddr()      ' "127.0.0.1"
```

### Query/Form Parameters

```basic
Dim name As String = Request.GetParameter("name")
Dim age As String = Request.GetParameter("age")
```

Works for both URL query parameters (`?name=John`) and form POST data.

### HTTP Headers

```basic
Dim auth As String = Request.GetHeader("Authorization")
Dim userAgent As String = Request.GetHeader("User-Agent")
```

### Request Body

```basic
' Get raw body as string
Dim body As String = Request.GetBody()

' Parse JSON body and get handle for Json.* methods
Dim jsonHandle As Integer = Request.GetJsonBody()
If jsonHandle > -1 Then
    Dim name As String = Json.GetString(jsonHandle, "name")
    Dim age As Integer = Json.GetInt(jsonHandle, "age")
End If
```

### Path Parameters

```basic
' For route "/users/{id}/posts/{postId}"
Dim userId As String = Request.GetPathParam("id")
Dim postId As String = Request.GetPathParam("postId")
```

## Response Namespace

Build and send HTTP responses.

### Status Code

```basic
Response.SetStatus(200)   ' OK
Response.SetStatus(201)   ' Created
Response.SetStatus(400)   ' Bad Request
Response.SetStatus(404)   ' Not Found
Response.SetStatus(500)   ' Internal Server Error
```

### Content Type

```basic
Response.SetContentType("text/html")
Response.SetContentType("application/json")
Response.SetContentType("text/plain")
```

### Custom Headers

```basic
Response.SetHeader("X-Custom-Header", "value")
Response.SetHeader("Cache-Control", "no-cache")
```

### Writing Response Body

```basic
' Write without newline
Response.Write("<h1>Hello</h1>")

' Write with newline
Response.WriteLine("Line 1")
Response.WriteLine("Line 2")

' String interpolation works
Dim name As String = "World"
Response.Write($"<h1>Hello, {name}!</h1>")
```

### Redirects

```basic
Response.Redirect("/login")
Response.Redirect("https://example.com")
```

## Complete Example: REST API

```basic
' api_server.bas - Complete REST API example

' GET /api/users - List all users
Sub HandleListUsers()
    Response.SetContentType("application/json")
    Response.Write("[{\"id\": 1, \"name\": \"John\"}, {\"id\": 2, \"name\": \"Jane\"}]")
End Sub

' GET /api/users/{id} - Get user by ID
Sub HandleGetUser()
    Dim userId As String = Request.GetPathParam("id")
    Response.SetContentType("application/json")
    Response.Write("{\"id\": " + userId + ", \"name\": \"User " + userId + "\"}")
End Sub

' POST /api/users - Create user
Sub HandleCreateUser()
    Dim jsonHandle As Integer = Request.GetJsonBody()

    Response.SetContentType("application/json")

    If jsonHandle < 0 Then
        Response.SetStatus(400)
        Response.Write("{\"error\": \"Invalid JSON\"}")
    Else
        Dim name As String = Json.GetString(jsonHandle, "name")
        Response.SetStatus(201)
        Response.Write("{\"id\": 3, \"name\": \"" + name + "\", \"created\": true}")
    End If
End Sub

' Main
Dim server As Integer = WebServer.Create(8080)

' Static files
WebServer.ServeStatic(server, "/static", "./public")

' API routes
WebServer.AddRoute(server, "GET", "/api/users", "ApiServer", "HandleListUsers")
WebServer.AddRoute(server, "GET", "/api/users/{id}", "ApiServer", "HandleGetUser")
WebServer.AddRoute(server, "POST", "/api/users", "ApiServer", "HandleCreateUser")

WebServer.Start(server)
Console.WriteLine("API server running on http://localhost:8080")
WebServer.Join(server)
```

## Tips and Best Practices

### 1. Always Set Content-Type

```basic
Response.SetContentType("application/json")  ' For JSON APIs
Response.SetContentType("text/html")         ' For HTML pages
```

### 2. Handle Errors Gracefully

```basic
Sub HandleRequest()
    Dim jsonHandle As Integer = Request.GetJsonBody()
    If jsonHandle < 0 Then
        Response.SetStatus(400)
        Response.Write("{\"error\": \"Bad request\"}")
    Else
        ' Process request...
    End If
End Sub
```

### 3. Use String Escapes for JSON

```basic
' Use \" for quotes in JSON strings
Response.Write("{\"status\": \"ok\"}")
```

### 4. Capture Namespace Calls for Interpolation

String interpolation doesn't support namespace calls directly. Capture values first:

```basic
' Don't do this:
' Response.Write($"Method: {Request.GetMethod()}")  ' Won't work

' Do this instead:
Dim method As String = Request.GetMethod()
Response.Write($"Method: {method}")
```

### 5. Security Considerations

- Static file serving includes directory traversal protection
- Always validate user input before database queries
- Consider adding authentication for sensitive endpoints

## API Reference Summary

| Namespace | Method | Description |
|-----------|--------|-------------|
| **WebServer** | `Create(port)` | Create server, returns ID |
| | `AddRoute(id, method, path, class, handler)` | Register route |
| | `ServeStatic(id, prefix, directory)` | Serve static files |
| | `Start(id)` | Start server |
| | `Stop(id)` | Stop server |
| | `Join(id)` | Block until stopped |
| | `IsRunning(id)` | Check if running |
| **Request** | `GetMethod()` | HTTP method |
| | `GetPath()` | Request path |
| | `GetQueryString()` | Query string |
| | `GetParameter(name)` | Query/form param |
| | `GetHeader(name)` | HTTP header |
| | `GetBody()` | Raw body string |
| | `GetJsonBody()` | Parse body as JSON |
| | `GetPathParam(name)` | Path parameter |
| | `GetContentType()` | Content-Type header |
| | `GetRemoteAddr()` | Client IP |
| **Response** | `SetStatus(code)` | HTTP status |
| | `SetContentType(type)` | Content-Type |
| | `SetHeader(name, value)` | Custom header |
| | `Write(content)` | Write body |
| | `WriteLine(content)` | Write with newline |
| | `Redirect(url)` | Send redirect |
