# Jetty Web Application Integration for JVM BASIC

## Overview

This document outlines a plan for integrating Jetty as an embedded web server into JVM BASIC, enabling developers to write full-featured web applications in BASIC.

**Current Assets:**
- Jetty 11.0.19 jars already in `lib/` directory:
  - `jetty-server-11.0.19.jar`
  - `jetty-servlet-11.0.19.jar`
  - `jetty-util-11.0.19.jar`
- Existing JSON support (Gson)
- Existing database support (PostgreSQL, MariaDB JDBC)
- Existing HTTP client support (Http.Get, Http.Post)

## Jetty Background

Eclipse Jetty is a lightweight, highly scalable, Java-based web server and Servlet engine. Key characteristics:

1. **Embeddable**: "Don't deploy your application in Jetty, deploy Jetty in your application!"
2. **Protocol Support**: HTTP/1, HTTP/2, HTTP/3, WebSocket
3. **Lightweight**: Small footprint, suitable for embedded scenarios
4. **Standard Compliant**: Jakarta Servlet specification

### Basic Embedded Jetty Pattern (Java)

```java
Server server = new Server(8080);
ServletContextHandler context = new ServletContextHandler("/");
context.addServlet(MyServlet.class, "/*");
server.setHandler(context);
server.start();
server.join();
```

## Proposed JVM BASIC Web API

### Option 1: Callback-Based (Simple)

```basic
' Define a request handler
Sub HandleRequest(request As WebRequest, response As WebResponse)
    Dim name As String = request.GetParameter("name")
    response.SetContentType("application/json")
    response.Write("{""greeting"": ""Hello, " + name + """}")
End Sub

' Start web server
Dim server As New WebServer(8080)
server.AddRoute("/hello", AddressOf HandleRequest)
server.Start()
Console.WriteLine("Server running on port 8080")
server.Join()  ' Block until stopped
```

### Option 2: Class-Based (More Flexible)

```basic
' Define a controller class
Class HelloController
    Public Sub HandleGet(request As WebRequest, response As WebResponse)
        Dim name As String = request.GetParameter("name")
        If name = "" Then name = "World"

        Dim result As Integer = Json.NewObject()
        Json.Put(result, "greeting", "Hello, " + name)
        Json.Put(result, "timestamp", DateTime.Now())

        response.SetContentType("application/json")
        response.Write(Json.ToString(result))
    End Sub

    Public Sub HandlePost(request As WebRequest, response As WebResponse)
        Dim body As String = request.GetBody()
        Dim data As Integer = Json.Parse(body)
        ' Process the data...
        response.SetStatus(201)
        response.Write("{""status"": ""created""}")
    End Sub
End Class

' Main program
Dim server As New WebServer(8080)
Dim controller As New HelloController()
server.Get("/hello", controller.HandleGet)
server.Post("/hello", controller.HandlePost)
server.ServeStatic("/static", "./public")
server.Start()
```

### Option 3: Annotation-Like (Most Modern)

```basic
' Route annotations via naming convention
Class ApiController
    ' GET /api/users
    Public Function Api_Users_Get(request As WebRequest) As String
        Dim users As String = Db.Query(conn, "SELECT * FROM users")
        Return Json.ToString(users)
    End Function

    ' POST /api/users
    Public Sub Api_Users_Post(request As WebRequest, response As WebResponse)
        Dim body As String = request.GetBody()
        ' Create user...
        response.SetStatus(201)
    End Sub

    ' GET /api/users/{id}
    Public Function Api_Users_Id_Get(request As WebRequest) As String
        Dim id As String = request.GetPathParam("id")
        Return Db.QuerySingle(conn, "SELECT * FROM users WHERE id = " + id)
    End Function
End Class
```

## WebRequest Object API

```basic
' Request properties
request.Method          ' GET, POST, PUT, DELETE, etc.
request.Path            ' /api/users/123
request.QueryString     ' name=John&age=30
request.ContentType     ' application/json
request.Body            ' Raw request body

' Request methods
request.GetParameter("name")     ' Query or form parameter
request.GetHeader("Authorization")
request.GetPathParam("id")       ' From route pattern
request.GetBody()                ' Full body as string
request.GetJsonBody()            ' Parsed JSON object
```

## WebResponse Object API

```basic
' Response methods
response.SetStatus(200)
response.SetContentType("application/json")
response.SetHeader("X-Custom", "value")
response.Write("Hello World")
response.WriteJson(jsonObject)
response.Redirect("/login")
response.SendFile("./files/report.pdf")
```

## WebServer Object API

```basic
' Server configuration
Dim server As New WebServer(port)
server.SetThreadPoolSize(10, 200)   ' min, max threads
server.SetIdleTimeout(30000)        ' 30 seconds

' Route registration
server.Get("/path", handler)
server.Post("/path", handler)
server.Put("/path", handler)
server.Delete("/path", handler)
server.Route("GET", "/path", handler)

' Static files
server.ServeStatic("/static", "./public")

' Middleware (future)
server.Use(loggingMiddleware)
server.Use(authMiddleware)

' Lifecycle
server.Start()
server.Stop()
server.Join()   ' Block until shutdown
server.IsRunning()
```

## Implementation Architecture

### Phase 1: BasicRuntime Extension

Add new runtime methods in `BasicRuntime.java`:

```java
// WebServer management
public static int webserver_Create(int port);
public static void webserver_AddRoute(int serverId, String method, String path, String handlerClass, String handlerMethod);
public static void webserver_Start(int serverId);
public static void webserver_Stop(int serverId);
public static void webserver_Join(int serverId);

// Request/Response access (via thread-local or request ID)
public static String request_GetMethod(int requestId);
public static String request_GetPath(int requestId);
public static String request_GetParameter(int requestId, String name);
public static String request_GetHeader(int requestId, String name);
public static String request_GetBody(int requestId);

public static void response_SetStatus(int requestId, int status);
public static void response_SetContentType(int requestId, String type);
public static void response_SetHeader(int requestId, String name, String value);
public static void response_Write(int requestId, String content);
```

### Phase 2: Code Generation Updates

1. Add `WebServer` as a known class type
2. Generate code to:
   - Create Jetty Server instance
   - Register route handlers (as Java method references or class instances)
   - Start/stop server

### Phase 3: Handler Invocation

The tricky part: How does Jetty call back into BASIC code?

**Option A: Generated Servlet Classes**
- For each handler Sub/Function, generate a separate servlet class
- Register with Jetty's ServletHandler

**Option B: Reflection-Based**
- Generate a single dispatcher servlet
- Use reflection to call the appropriate BASIC method
- Store method mappings in BasicRuntime

**Option C: Interface Implementation**
- Define a `BasicHandler` interface in Java
- Generate classes implementing this interface
- Each class wraps a BASIC Sub/Function

### Recommended: Option B (Reflection-Based)

```java
// In BasicRuntime.java
public class BasicWebDispatcher extends HttpServlet {
    private static Map<String, MethodInfo> routes = new HashMap<>();

    public static void registerRoute(String method, String path,
                                     String className, String methodName) {
        routes.put(method + ":" + path, new MethodInfo(className, methodName));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        String key = req.getMethod() + ":" + req.getPathInfo();
        MethodInfo info = routes.get(key);
        if (info != null) {
            // Create request/response wrapper IDs
            int reqId = registerRequest(req);
            int respId = registerResponse(resp);

            // Call BASIC method via reflection
            Class<?> clazz = Class.forName(info.className);
            Method method = clazz.getMethod(info.methodName, int.class, int.class);
            method.invoke(null, reqId, respId);

            // Cleanup
            unregisterRequest(reqId);
            unregisterResponse(respId);
        }
    }
}
```

## Example: Full Web Application

```basic
' blog.bas - A simple blog API

Class BlogController
    Private conn As Integer

    Public Sub New()
        Me.conn = Db.Connect("jdbc:postgresql://localhost/blog", "user", "pass")
    End Sub

    ' GET /posts - List all posts
    Public Sub ListPosts(request As WebRequest, response As WebResponse)
        Dim result As Integer = Db.Query(Me.conn, "SELECT * FROM posts ORDER BY created_at DESC")
        Dim posts As Integer = Json.NewArray()

        While Db.Next(result)
            Dim post As Integer = Json.NewObject()
            Json.PutInt(post, "id", Db.GetInt(result, "id"))
            Json.Put(post, "title", Db.GetString(result, "title"))
            Json.Put(post, "content", Db.GetString(result, "content"))
            Json.ArrayPush(posts, post)
        End While

        response.SetContentType("application/json")
        response.Write(Json.ToString(posts))
    End Sub

    ' POST /posts - Create a post
    Public Sub CreatePost(request As WebRequest, response As WebResponse)
        Dim body As Integer = request.GetJsonBody()
        Dim title As String = Json.GetString(body, "title")
        Dim content As String = Json.GetString(body, "content")

        Dim sql As String = "INSERT INTO posts (title, content) VALUES ('" + title + "', '" + content + "')"
        Db.Execute(Me.conn, sql)

        response.SetStatus(201)
        response.Write("{""status"": ""created""}")
    End Sub

    ' GET /posts/{id} - Get single post
    Public Sub GetPost(request As WebRequest, response As WebResponse)
        Dim id As String = request.GetPathParam("id")
        Dim result As Integer = Db.Query(Me.conn, "SELECT * FROM posts WHERE id = " + id)

        If Db.Next(result) Then
            Dim post As Integer = Json.NewObject()
            Json.PutInt(post, "id", Db.GetInt(result, "id"))
            Json.Put(post, "title", Db.GetString(result, "title"))
            Json.Put(post, "content", Db.GetString(result, "content"))
            response.SetContentType("application/json")
            response.Write(Json.ToString(post))
        Else
            response.SetStatus(404)
            response.Write("{""error"": ""Post not found""}")
        End If
    End Sub
End Class

' Main program
Dim controller As New BlogController()
Dim server As New WebServer(8080)

server.Get("/posts", controller.ListPosts)
server.Post("/posts", controller.CreatePost)
server.Get("/posts/:id", controller.GetPost)

Console.WriteLine("Blog API running on http://localhost:8080")
server.Start()
server.Join()
```

## Integration with Existing Features

### JSON Support
Already have `Json.Parse()`, `Json.NewObject()`, `Json.Put()`, etc. - perfect for REST APIs.

### Database Support
Already have `Db.Connect()`, `Db.Query()`, `Db.GetString()`, etc. - ready for data-driven web apps.

### File I/O
Already have `File.ReadAllText()`, `File.WriteAllText()` - can serve static files.

### HTTP Client
Already have `Http.Get()`, `Http.Post()` - can make outbound API calls.

## Implementation Phases

### Phase 1: Minimal Web Server
- `WebServer.Create(port)` - returns server ID
- `WebServer.Start(id)`, `WebServer.Stop(id)`
- Simple handler registration with static methods
- Basic request/response access

### Phase 2: Full Request/Response API
- Complete WebRequest object API
- Complete WebResponse object API
- Path parameters support
- JSON body parsing

### Phase 3: Route Patterns
- Support for `/users/:id` style routes
- Wildcard routes `/static/*`
- Method-specific routing (GET, POST, etc.)

### Phase 4: Static File Serving
- `WebServer.ServeStatic("/path", "./directory")`
- MIME type detection
- Caching headers

### Phase 5: WebSocket Support (Future)
- Upgrade handling
- Message send/receive
- Connection management

## Technical Challenges

1. **Callback Invocation**: How Jetty calls back into BASIC code
   - Solution: Reflection-based dispatcher servlet

2. **Thread Safety**: Web requests are concurrent
   - Solution: Thread-local storage for request/response context

3. **Handler Registration**: Mapping BASIC Subs to Jetty handlers
   - Solution: Generate wrapper classes or use reflection

4. **Session Management**: Stateful web apps
   - Solution: Leverage Jetty's built-in session support

5. **Type Mapping**: BASIC types to HTTP concepts
   - Solution: WebRequest/WebResponse wrapper classes in runtime

## References

- [Jetty GitHub Repository](https://github.com/jetty/jetty.project)
- [Embedded Jetty Tutorial - Baeldung](https://www.baeldung.com/jetty-embedded)
- [Jetty Servlet Examples](https://github.com/jetty-project/embedded-servlet-server)
- [ZetCode Jetty Tutorial](https://zetcode.com/java/jetty/embedded/)
- [Vogella Jetty Tutorial](https://www.vogella.com/tutorials/Jetty/article.html)
