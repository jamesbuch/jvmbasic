# JVM BASIC 2.0 - Jetty Web Server Integration Plan

## Overview

This document outlines the plan for integrating Jetty as an embedded web server into JVM BASIC 2.0, enabling developers to write full-featured web applications with modern annotation-based routing similar to PHP 8/Python Flask.

## Goals

1. **Namespace-based API**: `WebServer.Create()`, `Request.GetMethod()`, `Response.Write()`
2. **Class-based Controllers**: Route handlers organized in controller classes
3. **Annotation-based Routing**: `#[Route("/path")]` syntax for declarative routing (Phase 2)
4. **Request/Response API**: Clean access to HTTP request/response data
5. **Static File Serving**: Serve CSS, JS, images from directories
6. **JSON API Support**: Seamless integration with existing Json namespace

## Key Implementation Pattern

The legacy JVM BASIC uses a simple, effective pattern:

1. **Namespace calls** map to `BasicRuntime` static methods:
   - `WebServer.Create(8080)` → `INVOKESTATIC BasicRuntime.webserver_Create(I)I`
   - `Request.GetMethod()` → `INVOKESTATIC BasicRuntime.request_GetMethod()Ljava/lang/String;`
   - `Response.Write(text)` → `INVOKESTATIC BasicRuntime.response_Write(Ljava/lang/String;)V`

2. **Thread-local storage** for request/response context in handlers

3. **Reflection-based dispatch** to call BASIC handlers from Jetty servlet

## Available Libraries

The following Jetty JARs are already in `lib/`:

- `jetty-server-11.0.19.jar` - Core HTTP server
- `jetty-servlet-11.0.19.jar` - Servlet support
- `jetty-util-11.0.19.jar` - Utilities
- `jetty-http-11.0.19.jar` - HTTP utilities
- `jetty-io-11.0.19.jar` - I/O utilities
- `jetty-security-11.0.19.jar` - Security support
- `jakarta.servlet-api-5.0.0.jar` - Servlet API
- `slf4j-api-2.0.9.jar` - Logging facade
- `slf4j-simple-2.0.9.jar` - Simple logging

## Legacy Implementation Reference

The legacy JVM BASIC (C++ based) has a working Jetty implementation in `BasicRuntime.java`:

### Key Components from Legacy:

```java
// WebServer management
webserver_Create(int port)            // Create server, returns ID
webserver_AddRoute(id, method, path, className, handlerMethod)
webserver_ServeStatic(id, prefix, directory)
webserver_Start(id)
webserver_Stop(id)
webserver_Join(id)

// Request access (thread-local)
request_GetMethod()
request_GetPath()
request_GetParameter(name)
request_GetHeader(name)
request_GetBody()
request_GetJsonBody()
request_GetPathParam(name)

// Response building
response_SetStatus(code)
response_SetContentType(type)
response_SetHeader(name, value)
response_Write(content)
response_Redirect(url)
```

---

## Proposed JVM BASIC 2.0 API

### Phase 1: Basic Controller Pattern (No Annotations)

```basic
' AboutController.jvmb - Controller class for about pages

class AboutController extends AbstractController

    ' Handler for GET /about
    public function AboutPage(req as WebRequest, res as WebResponse) as String
        res.SetContentType("text/html")
        return "<h1>About Us</h1><p>Welcome to JVM BASIC 2.0</p>"
    end function

    ' Handler for GET /about/team
    public function TeamPage(req as WebRequest, res as WebResponse) as String
        res.SetContentType("text/html")
        return "<h1>Our Team</h1>"
    end function
end class

' Main program
var server as WebServer = new WebServer(8080)
var controller as AboutController = new AboutController()

server.Get("/about", controller.AboutPage)
server.Get("/about/team", controller.TeamPage)

server.Start()
Console.WriteLine("Server running on http://localhost:8080")
server.Join()
```

### Phase 2: Annotation-Based Routing

```basic
' UserController.jvmb - REST API with annotations

#[Controller]
#[Route("/api/users")]
class UserController extends AbstractController

    private var db as DbConnection

    public sub New()
        this.db = Db.Connect("jdbc:postgresql://localhost/myapp", "user", "pass")
    end sub

    ' GET /api/users
    #[Get]
    #[Produces("application/json")]
    public function ListUsers() as String
        var result as String = Db.Query(this.db, "SELECT * FROM users")
        return result
    end function

    ' GET /api/users/{id}
    #[Get("/{id}")]
    public function GetUser(#[PathParam] id as Integer) as String
        var user as String = Db.QuerySingle(this.db, $"SELECT * FROM users WHERE id = {id}")
        if user = "" then
            Response.Status(404)
            return Json.Set("{}", "error", "User not found")
        end if
        return user
    end function

    ' POST /api/users
    #[Post]
    #[Consumes("application/json")]
    public function CreateUser(#[Body] userData as String) as String
        var name as String = Json.Get(userData, "name")
        var email as String = Json.Get(userData, "email")

        Db.Execute(this.db, $"INSERT INTO users (name, email) VALUES ('{name}', '{email}')")

        Response.Status(201)
        return Json.Set("{}", "status", "created")
    end function

    ' PUT /api/users/{id}
    #[Put("/{id}")]
    public function UpdateUser(#[PathParam] id as Integer, #[Body] userData as String) as String
        var name as String = Json.Get(userData, "name")
        Db.Execute(this.db, $"UPDATE users SET name = '{name}' WHERE id = {id}")
        return Json.Set("{}", "status", "updated")
    end function

    ' DELETE /api/users/{id}
    #[Delete("/{id}")]
    public function DeleteUser(#[PathParam] id as Integer) as String
        Db.Execute(this.db, $"DELETE FROM users WHERE id = {id}")
        Response.Status(204)
        return ""
    end function
end class

' Main - auto-register routes from annotations
var app as WebApplication = new WebApplication(8080)
app.RegisterController(new UserController())
app.Start()
```

---

## Implementation Architecture

### File Changes Required

```
src/java/com/jvmbasic/
├── runtime/
│   └── BasicWeb.java         # NEW: WebServer, Request, Response runtime
├── visitor/
│   └── CompilerVisitor.java  # MODIFY: Add WebServer, Request, Response handlers
```

### Step 1: Create BasicWeb.java Runtime

**File: `src/java/com/jvmbasic/runtime/BasicWeb.java`**

This is the runtime class that provides the actual Jetty integration. It follows the same pattern as `BasicHttp.java`, `BasicJson.java`, etc.

```java
package com.jvmbasic.runtime;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.*;

public class BasicWeb {

    // Server storage
    private static Map<Integer, Server> servers = new ConcurrentHashMap<>();
    private static Map<Integer, List<RouteInfo>> serverRoutes = new ConcurrentHashMap<>();
    private static Map<Integer, List<String[]>> serverStaticDirs = new ConcurrentHashMap<>();
    private static int nextServerId = 1;

    // Thread-local request/response context
    private static ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<>();
    private static ThreadLocal<HttpServletResponse> currentResponse = new ThreadLocal<>();
    private static ThreadLocal<Map<String, String>> currentPathParams = new ThreadLocal<>();

    // ========================================================================
    // WebServer namespace methods
    // ========================================================================

    public static int webserver_Create(int port) {
        try {
            Server server = new Server(port);
            ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
            context.setContextPath("/");

            int id = nextServerId++;
            servers.put(id, server);
            serverRoutes.put(id, new CopyOnWriteArrayList<>());
            serverStaticDirs.put(id, new CopyOnWriteArrayList<>());

            // Add dispatcher servlet
            context.addServlet(new ServletHolder(new BasicWebDispatcher(id)), "/*");
            server.setHandler(context);

            return id;
        } catch (Exception e) {
            System.err.println("WebServer.Create error: " + e.getMessage());
            return -1;
        }
    }

    public static int webserver_AddRoute(int serverId, String method, String path,
                                          String className, String handlerMethod) {
        List<RouteInfo> routes = serverRoutes.get(serverId);
        if (routes != null) {
            routes.add(new RouteInfo(method.toUpperCase(), path, className, handlerMethod));
            return 0;
        }
        return -1;
    }

    public static int webserver_ServeStatic(int serverId, String urlPrefix, String directory) {
        List<String[]> staticDirs = serverStaticDirs.get(serverId);
        if (staticDirs != null) {
            staticDirs.add(new String[] { urlPrefix, directory });
            return 0;
        }
        return -1;
    }

    public static int webserver_Start(int serverId) {
        try {
            Server server = servers.get(serverId);
            if (server != null) {
                server.start();
                return 0;
            }
            return -1;
        } catch (Exception e) {
            System.err.println("WebServer.Start error: " + e.getMessage());
            return -1;
        }
    }

    public static void webserver_Join(int serverId) {
        try {
            Server server = servers.get(serverId);
            if (server != null) {
                server.join();
            }
        } catch (Exception e) {
            // Ignore interruption
        }
    }

    public static int webserver_Stop(int serverId) {
        try {
            Server server = servers.get(serverId);
            if (server != null) {
                server.stop();
                return 0;
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    public static int webserver_IsRunning(int serverId) {
        Server server = servers.get(serverId);
        return (server != null && server.isRunning()) ? 1 : 0;
    }

    // ========================================================================
    // Request namespace methods
    // ========================================================================

    public static String request_GetMethod() {
        HttpServletRequest req = currentRequest.get();
        return req != null ? req.getMethod() : "";
    }

    public static String request_GetPath() {
        HttpServletRequest req = currentRequest.get();
        String path = req != null ? req.getPathInfo() : "";
        return path != null ? path : "/";
    }

    public static String request_GetQueryString() {
        HttpServletRequest req = currentRequest.get();
        String qs = req != null ? req.getQueryString() : "";
        return qs != null ? qs : "";
    }

    public static String request_GetParameter(String name) {
        HttpServletRequest req = currentRequest.get();
        String val = req != null ? req.getParameter(name) : "";
        return val != null ? val : "";
    }

    public static String request_GetHeader(String name) {
        HttpServletRequest req = currentRequest.get();
        String val = req != null ? req.getHeader(name) : "";
        return val != null ? val : "";
    }

    public static String request_GetBody() {
        HttpServletRequest req = currentRequest.get();
        if (req == null) return "";
        try {
            return new String(req.getInputStream().readAllBytes());
        } catch (Exception e) {
            return "";
        }
    }

    public static int request_GetJsonBody() {
        String body = request_GetBody();
        if (body.isEmpty()) return -1;
        try {
            return BasicJson.parse(body);
        } catch (Exception e) {
            return -1;
        }
    }

    public static String request_GetPathParam(String name) {
        Map<String, String> params = currentPathParams.get();
        if (params != null && params.containsKey(name)) {
            return params.get(name);
        }
        return "";
    }

    public static String request_GetContentType() {
        HttpServletRequest req = currentRequest.get();
        String ct = req != null ? req.getContentType() : "";
        return ct != null ? ct : "";
    }

    public static String request_GetRemoteAddr() {
        HttpServletRequest req = currentRequest.get();
        return req != null ? req.getRemoteAddr() : "";
    }

    // ========================================================================
    // Response namespace methods
    // ========================================================================

    public static void response_SetStatus(int status) {
        HttpServletResponse resp = currentResponse.get();
        if (resp != null) {
            resp.setStatus(status);
        }
    }

    public static void response_SetContentType(String contentType) {
        HttpServletResponse resp = currentResponse.get();
        if (resp != null) {
            resp.setContentType(contentType);
        }
    }

    public static void response_SetHeader(String name, String value) {
        HttpServletResponse resp = currentResponse.get();
        if (resp != null) {
            resp.setHeader(name, value);
        }
    }

    public static void response_Write(String content) {
        HttpServletResponse resp = currentResponse.get();
        if (resp != null) {
            try {
                resp.getWriter().write(content);
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    public static void response_WriteLine(String content) {
        HttpServletResponse resp = currentResponse.get();
        if (resp != null) {
            try {
                resp.getWriter().println(content);
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    public static void response_Redirect(String url) {
        HttpServletResponse resp = currentResponse.get();
        if (resp != null) {
            try {
                resp.sendRedirect(url);
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    // ========================================================================
    // Internal classes
    // ========================================================================

    static class RouteInfo {
        String method;
        String path;
        String className;
        String handlerMethod;
        java.util.regex.Pattern pattern;
        List<String> paramNames = new ArrayList<>();

        RouteInfo(String method, String path, String className, String handlerMethod) {
            this.method = method;
            this.path = path;
            this.className = className;
            this.handlerMethod = handlerMethod;

            // Convert path params like {id} to regex
            String regex = path.replaceAll("\\{([^}]+)\\}", "([^/]+)");
            this.pattern = java.util.regex.Pattern.compile("^" + regex + "$");

            // Extract param names
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\{([^}]+)\\}").matcher(path);
            while (m.find()) {
                paramNames.add(m.group(1));
            }
        }

        Map<String, String> match(String requestPath) {
            java.util.regex.Matcher m = pattern.matcher(requestPath);
            if (m.matches()) {
                Map<String, String> params = new HashMap<>();
                for (int i = 0; i < paramNames.size(); i++) {
                    params.put(paramNames.get(i), m.group(i + 1));
                }
                return params;
            }
            return null;
        }
    }

    static class BasicWebDispatcher extends HttpServlet {
        private final int serverId;

        BasicWebDispatcher(int serverId) {
            this.serverId = serverId;
        }

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp)
                throws jakarta.servlet.ServletException, java.io.IOException {

            String method = req.getMethod();
            String path = req.getPathInfo();
            if (path == null) path = "/";

            // Find matching route
            List<RouteInfo> routes = serverRoutes.get(serverId);
            RouteInfo matchedRoute = null;
            Map<String, String> pathParams = null;

            if (routes != null) {
                for (RouteInfo route : routes) {
                    if (route.method.equals(method)) {
                        // Try exact match first
                        if (route.path.equals(path)) {
                            matchedRoute = route;
                            pathParams = new HashMap<>();
                            break;
                        }
                        // Try pattern match
                        Map<String, String> params = route.match(path);
                        if (params != null) {
                            matchedRoute = route;
                            pathParams = params;
                            break;
                        }
                    }
                }
            }

            if (matchedRoute == null) {
                resp.sendError(404, "Not Found: " + path);
                return;
            }

            // Set thread-local context
            currentRequest.set(req);
            currentResponse.set(resp);
            currentPathParams.set(pathParams);

            try {
                // Call BASIC handler via reflection
                Class<?> clazz = Class.forName(matchedRoute.className);
                java.lang.reflect.Method handler = clazz.getMethod(matchedRoute.handlerMethod);
                handler.invoke(null);  // Static method call
            } catch (Exception e) {
                resp.sendError(500, "Handler error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                currentRequest.remove();
                currentResponse.remove();
                currentPathParams.remove();
            }
        }
    }
}
```

### Step 2: Add CompilerVisitor Handlers

**File: `src/java/com/jvmbasic/visitor/CompilerVisitor.java`**

Add these namespace handlers after the existing `Crypto` handler (around line 2352):

```java
// Handle WebServer namespace - calls com.jvmbasic.runtime.BasicWeb
if ("WebServer".equalsIgnoreCase(pendingNamespace)) {
    pendingNamespace = null;
    return handleWebServerCall(methodName, ctx.argumentList());
}

// Handle Request namespace - calls com.jvmbasic.runtime.BasicWeb
if ("Request".equalsIgnoreCase(pendingNamespace)) {
    pendingNamespace = null;
    return handleRequestCall(methodName, ctx.argumentList());
}

// Handle Response namespace - calls com.jvmbasic.runtime.BasicWeb
if ("Response".equalsIgnoreCase(pendingNamespace)) {
    pendingNamespace = null;
    return handleResponseCall(methodName, ctx.argumentList());
}
```

Then add the handler methods:

```java
private Object handleWebServerCall(String methodName, JvmBasicParser.ArgumentListContext args) {
    String runtimeClass = "com/jvmbasic/runtime/BasicWeb";

    switch (methodName.toLowerCase()) {
        case "create":
            // WebServer.Create(port) -> int
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "webserver_Create", "(I)I", false);
            lastExprType = "Integer";
            break;

        case "addroute":
            // WebServer.AddRoute(serverId, method, path, className, methodName)
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "webserver_AddRoute",
                "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I", false);
            lastExprType = "Integer";
            break;

        case "servestatic":
            // WebServer.ServeStatic(serverId, urlPrefix, directory)
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "webserver_ServeStatic",
                "(ILjava/lang/String;Ljava/lang/String;)I", false);
            lastExprType = "Integer";
            break;

        case "start":
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "webserver_Start", "(I)I", false);
            lastExprType = "Integer";
            break;

        case "stop":
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "webserver_Stop", "(I)I", false);
            lastExprType = "Integer";
            break;

        case "join":
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "webserver_Join", "(I)V", false);
            lastExprType = "Void";
            break;

        case "isrunning":
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "webserver_IsRunning", "(I)I", false);
            lastExprType = "Integer";
            break;

        default:
            throw new RuntimeException("Unknown WebServer method: " + methodName);
    }
    return null;
}

private Object handleRequestCall(String methodName, JvmBasicParser.ArgumentListContext args) {
    String runtimeClass = "com/jvmbasic/runtime/BasicWeb";

    switch (methodName.toLowerCase()) {
        case "getmethod":
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "request_GetMethod",
                "()Ljava/lang/String;", false);
            lastExprType = "String";
            break;

        case "getpath":
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "request_GetPath",
                "()Ljava/lang/String;", false);
            lastExprType = "String";
            break;

        case "getquerystring":
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "request_GetQueryString",
                "()Ljava/lang/String;", false);
            lastExprType = "String";
            break;

        case "getparameter":
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "request_GetParameter",
                "(Ljava/lang/String;)Ljava/lang/String;", false);
            lastExprType = "String";
            break;

        case "getheader":
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "request_GetHeader",
                "(Ljava/lang/String;)Ljava/lang/String;", false);
            lastExprType = "String";
            break;

        case "getbody":
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "request_GetBody",
                "()Ljava/lang/String;", false);
            lastExprType = "String";
            break;

        case "getjsonbody":
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "request_GetJsonBody",
                "()I", false);
            lastExprType = "Integer";
            break;

        case "getpathparam":
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "request_GetPathParam",
                "(Ljava/lang/String;)Ljava/lang/String;", false);
            lastExprType = "String";
            break;

        case "getcontenttype":
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "request_GetContentType",
                "()Ljava/lang/String;", false);
            lastExprType = "String";
            break;

        case "getremoteaddr":
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "request_GetRemoteAddr",
                "()Ljava/lang/String;", false);
            lastExprType = "String";
            break;

        default:
            throw new RuntimeException("Unknown Request method: " + methodName);
    }
    return null;
}

private Object handleResponseCall(String methodName, JvmBasicParser.ArgumentListContext args) {
    String runtimeClass = "com/jvmbasic/runtime/BasicWeb";

    switch (methodName.toLowerCase()) {
        case "setstatus":
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "response_SetStatus", "(I)V", false);
            lastExprType = "Void";
            break;

        case "setcontenttype":
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "response_SetContentType",
                "(Ljava/lang/String;)V", false);
            lastExprType = "Void";
            break;

        case "setheader":
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "response_SetHeader",
                "(Ljava/lang/String;Ljava/lang/String;)V", false);
            lastExprType = "Void";
            break;

        case "write":
            // Handle Response.Write with string conversion (like Console.WriteLine)
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            if (args != null && !args.argument().isEmpty()) {
                visit(args.argument(0).expression());
                ensureString();
            } else {
                mv.visitLdcInsn("");
            }
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "response_Write",
                "(Ljava/lang/String;)V", false);
            lastExprType = "Void";
            break;

        case "writeline":
            if (args != null && !args.argument().isEmpty()) {
                visit(args.argument(0).expression());
                ensureString();
            } else {
                mv.visitLdcInsn("");
            }
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "response_WriteLine",
                "(Ljava/lang/String;)V", false);
            lastExprType = "Void";
            break;

        case "redirect":
            visitArguments(args);
            mv.visitMethodInsn(INVOKESTATIC, runtimeClass, "response_Redirect",
                "(Ljava/lang/String;)V", false);
            lastExprType = "Void";
            break;

        default:
            throw new RuntimeException("Unknown Response method: " + methodName);
    }
    return null;
}
```

### Step 3: Grammar Changes for Annotations (Phase 2)

**Add to JvmBasicLexer.g4:**
```antlr
HASH_BRACKET : '#[' ;
```

**Add to JvmBasicParser.g4:**
```antlr
annotation
    : HASH_BRACKET annotationName (LPAREN annotationArgs? RPAREN)? RBRACKET
    ;

annotationName
    : IDENTIFIER (DOT IDENTIFIER)*
    ;

annotationArgs
    : annotationArg (COMMA annotationArg)*
    ;

annotationArg
    : (IDENTIFIER EQ)? expression
    ;

// Annotations can precede class, method, field, parameter declarations
classDeclaration
    : annotation* accessModifier? ABSTRACT? CLASS IDENTIFIER ...
    ;

methodDeclaration
    : annotation* accessModifier? SHARED? OVERRIDE? (FUNCTION | SUB) IDENTIFIER ...
    ;

parameter
    : annotation* IDENTIFIER AS typeName (EQ expression)?
    ;
```

### Step 2: Runtime Classes

**Create `src/java/com/jvmbasic/runtime/web/` package:**

```
web/
├── BasicWebServer.java      # Jetty server wrapper
├── BasicWebDispatcher.java  # Request dispatcher servlet
├── BasicWebRequest.java     # Request wrapper
├── BasicWebResponse.java    # Response wrapper
├── RouteInfo.java           # Route metadata
├── ControllerRegistry.java  # Controller registration
└── AnnotationProcessor.java # Process #[Route] etc.
```

### Step 3: BasicWebServer.java

```java
package com.jvmbasic.runtime.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import java.util.*;

public class BasicWebServer {
    private static Map<Integer, Server> servers = new HashMap<>();
    private static Map<Integer, List<RouteInfo>> routes = new HashMap<>();
    private static int nextId = 1;

    public static int create(int port) {
        try {
            Server server = new Server(port);
            ServletContextHandler context = new ServletContextHandler();
            context.setContextPath("/");

            int id = nextId++;
            servers.put(id, server);
            routes.put(id, new ArrayList<>());

            // Add dispatcher servlet
            context.addServlet(new ServletHolder(new BasicWebDispatcher(id)), "/*");
            server.setHandler(context);

            return id;
        } catch (Exception e) {
            return -1;
        }
    }

    public static void addRoute(int serverId, String method, String path,
                                 String className, String methodName) {
        List<RouteInfo> serverRoutes = routes.get(serverId);
        if (serverRoutes != null) {
            serverRoutes.add(new RouteInfo(method, path, className, methodName));
        }
    }

    public static void start(int serverId) throws Exception {
        Server server = servers.get(serverId);
        if (server != null) {
            server.start();
        }
    }

    public static void join(int serverId) throws Exception {
        Server server = servers.get(serverId);
        if (server != null) {
            server.join();
        }
    }

    public static void stop(int serverId) throws Exception {
        Server server = servers.get(serverId);
        if (server != null) {
            server.stop();
        }
    }

    public static List<RouteInfo> getRoutes(int serverId) {
        return routes.getOrDefault(serverId, Collections.emptyList());
    }
}
```

### Step 4: BasicWebDispatcher.java

```java
package com.jvmbasic.runtime.web;

import jakarta.servlet.http.*;
import java.lang.reflect.*;

public class BasicWebDispatcher extends HttpServlet {
    private final int serverId;

    // Thread-local storage for request/response
    private static ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<>();
    private static ThreadLocal<HttpServletResponse> currentResponse = new ThreadLocal<>();

    public BasicWebDispatcher(int serverId) {
        this.serverId = serverId;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String method = req.getMethod();
        String path = req.getPathInfo();
        if (path == null) path = "/";

        // Find matching route
        RouteInfo route = findRoute(method, path);

        if (route == null) {
            resp.setStatus(404);
            resp.getWriter().write("{\"error\": \"Not found\"}");
            return;
        }

        try {
            // Set thread-local request/response
            currentRequest.set(req);
            currentResponse.set(resp);

            // Extract path parameters
            Map<String, String> pathParams = route.extractParams(path);

            // Call the BASIC handler via reflection
            Class<?> clazz = Class.forName(route.getClassName());
            Method handler = findHandler(clazz, route.getMethodName());

            Object instance = clazz.getDeclaredConstructor().newInstance();
            Object result = handler.invoke(instance);

            if (result != null) {
                resp.getWriter().write(result.toString());
            }

        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } finally {
            currentRequest.remove();
            currentResponse.remove();
        }
    }

    private RouteInfo findRoute(String method, String path) {
        for (RouteInfo route : BasicWebServer.getRoutes(serverId)) {
            if (route.matches(method, path)) {
                return route;
            }
        }
        return null;
    }

    // Static accessors for Request/Response namespaces
    public static HttpServletRequest getRequest() {
        return currentRequest.get();
    }

    public static HttpServletResponse getResponse() {
        return currentResponse.get();
    }
}
```

### Step 5: Request/Response Runtime Methods

**Add to BasicRuntime.java or create BasicWebRuntime.java:**

```java
// Request namespace methods
public static String request_GetMethod() {
    HttpServletRequest req = BasicWebDispatcher.getRequest();
    return req != null ? req.getMethod() : "";
}

public static String request_GetPath() {
    HttpServletRequest req = BasicWebDispatcher.getRequest();
    return req != null ? req.getPathInfo() : "";
}

public static String request_GetParameter(String name) {
    HttpServletRequest req = BasicWebDispatcher.getRequest();
    return req != null ? req.getParameter(name) : "";
}

public static String request_GetHeader(String name) {
    HttpServletRequest req = BasicWebDispatcher.getRequest();
    return req != null ? req.getHeader(name) : "";
}

public static String request_GetBody() {
    HttpServletRequest req = BasicWebDispatcher.getRequest();
    if (req == null) return "";
    try {
        return new String(req.getInputStream().readAllBytes());
    } catch (Exception e) {
        return "";
    }
}

// Response namespace methods
public static void response_SetStatus(int code) {
    HttpServletResponse resp = BasicWebDispatcher.getResponse();
    if (resp != null) resp.setStatus(code);
}

public static void response_SetContentType(String type) {
    HttpServletResponse resp = BasicWebDispatcher.getResponse();
    if (resp != null) resp.setContentType(type);
}

public static void response_Write(String content) {
    HttpServletResponse resp = BasicWebDispatcher.getResponse();
    if (resp != null) {
        try {
            resp.getWriter().write(content);
        } catch (Exception e) {}
    }
}
```

### Step 6: CompilerVisitor Updates

Add code generation for:

1. **WebServer class instantiation**
2. **Request/Response namespace calls**
3. **Annotation metadata collection**

```java
// In CompilerVisitor.java

private void visitWebServerCreate(List<JvmBasicParser.ExpressionContext> args) {
    // Generate: BasicWebServer.create(port)
    visit(args.get(0)); // port
    mv.visitMethodInsn(INVOKESTATIC,
        "com/jvmbasic/runtime/web/BasicWebServer",
        "create", "(I)I", false);
}

private void visitRequestMethod(String method, List<JvmBasicParser.ExpressionContext> args) {
    switch (method) {
        case "GetMethod":
            mv.visitMethodInsn(INVOKESTATIC,
                "com/jvmbasic/runtime/web/BasicWebRuntime",
                "request_GetMethod", "()Ljava/lang/String;", false);
            break;
        case "GetParameter":
            visit(args.get(0)); // parameter name
            mv.visitMethodInsn(INVOKESTATIC,
                "com/jvmbasic/runtime/web/BasicWebRuntime",
                "request_GetParameter", "(Ljava/lang/String;)Ljava/lang/String;", false);
            break;
        // ... other Request methods
    }
}
```

---

## Implementation Phases

### Phase 1: Basic WebServer (No Annotations)
**Priority: High**

1. Create runtime web package structure
2. Implement BasicWebServer.java
3. Implement BasicWebDispatcher.java
4. Add Request/Response runtime methods
5. Add CompilerVisitor code generation for WebServer, Request, Response namespaces
6. Test with simple hello world web server

**Deliverable:**
```basic
' Works without annotations
sub HandleHello()
    Response.SetContentType("text/html")
    Response.Write("<h1>Hello World!</h1>")
end sub

var server as Integer = WebServer.Create(8080)
WebServer.AddRoute(server, "GET", "/hello", "HelloWeb", "HandleHello")
WebServer.Start(server)
WebServer.Join(server)
```

### Phase 2: Controller Classes
**Priority: High**

1. Add AbstractController base class
2. Support controller instantiation
3. Method-based route handlers
4. Path parameters support

**Deliverable:**
```basic
class HelloController
    public function Hello(req as WebRequest, res as WebResponse) as String
        var name as String = req.GetParameter("name")
        if name = "" then name = "World"
        res.SetContentType("text/html")
        return $"<h1>Hello, {name}!</h1>"
    end function
end class

var server as WebServer = new WebServer(8080)
var ctrl as HelloController = new HelloController()
server.Get("/hello", ctrl.Hello)
server.Start()
```

### Phase 3: Annotation Grammar
**Priority: Medium**

1. Update lexer for `#[` token
2. Update parser for annotation rules
3. Attach annotations to AST nodes
4. Store annotations in symbol table

### Phase 4: Annotation Processing
**Priority: Medium**

1. Create AnnotationProcessor class
2. Process `#[Route]`, `#[Get]`, `#[Post]`, etc.
3. Auto-register routes from annotations
4. Generate annotation metadata in bytecode

**Deliverable:**
```basic
#[Controller]
#[Route("/api")]
class ApiController

    #[Get("/status")]
    public function Status() as String
        return Json.Set("{}", "status", "ok")
    end function
end class

var app as WebApplication = new WebApplication(8080)
app.Scan("ApiController")  ' Auto-discover routes from annotations
app.Start()
```

### Phase 5: Advanced Features
**Priority: Low**

1. Static file serving
2. Middleware support
3. Session management
4. WebSocket support (future)

---

## Example: Full REST API Application

```basic
' app.jvmb - Complete REST API example

#[Controller]
#[Route("/api/tasks")]
class TaskController extends AbstractController

    private var tasks as String[]
    private var nextId as Integer

    public sub New()
        this.tasks = new String[100]
        this.nextId = 1
    end sub

    ' GET /api/tasks - List all tasks
    #[Get]
    public function ListTasks() as String
        var result as String = Json.CreateArray()
        for i = 0 to this.nextId - 1
            if this.tasks[i] <> "" then
                Json.Push(result, this.tasks[i])
            end if
        next i
        return result
    end function

    ' GET /api/tasks/{id} - Get single task
    #[Get("/{id}")]
    public function GetTask(#[PathParam] id as Integer) as String
        if id < 0 or id >= this.nextId or this.tasks[id] = "" then
            Response.Status(404)
            return Json.Set("{}", "error", "Task not found")
        end if
        return this.tasks[id]
    end function

    ' POST /api/tasks - Create task
    #[Post]
    public function CreateTask(#[Body] data as String) as String
        var title as String = Json.Get(data, "title")
        var task as String = Json.Create()
        task = Json.SetInt(task, "id", this.nextId)
        task = Json.Set(task, "title", title)
        task = Json.SetBool(task, "completed", false)

        this.tasks[this.nextId] = task
        this.nextId = this.nextId + 1

        Response.Status(201)
        return task
    end function

    ' PUT /api/tasks/{id} - Update task
    #[Put("/{id}")]
    public function UpdateTask(#[PathParam] id as Integer, #[Body] data as String) as String
        if id < 0 or id >= this.nextId or this.tasks[id] = "" then
            Response.Status(404)
            return Json.Set("{}", "error", "Task not found")
        end if

        var task as String = this.tasks[id]
        if Json.Has(data, "title") then
            task = Json.Set(task, "title", Json.Get(data, "title"))
        end if
        if Json.Has(data, "completed") then
            task = Json.SetBool(task, "completed", Json.GetBool(data, "completed"))
        end if

        this.tasks[id] = task
        return task
    end function

    ' DELETE /api/tasks/{id} - Delete task
    #[Delete("/{id}")]
    public function DeleteTask(#[PathParam] id as Integer) as String
        if id < 0 or id >= this.nextId or this.tasks[id] = "" then
            Response.Status(404)
            return Json.Set("{}", "error", "Task not found")
        end if

        this.tasks[id] = ""
        Response.Status(204)
        return ""
    end function
end class

' Main application
Console.WriteLine("Starting Task API Server...")

var app as WebApplication = new WebApplication(8080)
app.RegisterController(new TaskController())
app.ServeStatic("/", "./public")

Console.WriteLine("Server running on http://localhost:8080")
Console.WriteLine("API endpoints:")
Console.WriteLine("  GET    /api/tasks      - List all tasks")
Console.WriteLine("  GET    /api/tasks/{id} - Get task by ID")
Console.WriteLine("  POST   /api/tasks      - Create task")
Console.WriteLine("  PUT    /api/tasks/{id} - Update task")
Console.WriteLine("  DELETE /api/tasks/{id} - Delete task")

app.Start()
```

---

## Annotation Reference

### Routing Annotations

| Annotation | Description | Example |
|------------|-------------|---------|
| `#[Controller]` | Marks class as web controller | `#[Controller]` |
| `#[Route("/path")]` | Base path for controller | `#[Route("/api")]` |
| `#[Get]` | HTTP GET handler | `#[Get("/users")]` |
| `#[Post]` | HTTP POST handler | `#[Post]` |
| `#[Put]` | HTTP PUT handler | `#[Put("/{id}")]` |
| `#[Delete]` | HTTP DELETE handler | `#[Delete("/{id}")]` |
| `#[Patch]` | HTTP PATCH handler | `#[Patch("/{id}")]` |

### Parameter Annotations

| Annotation | Description | Example |
|------------|-------------|---------|
| `#[PathParam]` | Extract from URL path | `#[PathParam] id as Integer` |
| `#[QueryParam]` | Extract from query string | `#[QueryParam] page as Integer` |
| `#[Body]` | Request body | `#[Body] data as String` |
| `#[Header]` | HTTP header value | `#[Header("Authorization")]` |

### Content Annotations

| Annotation | Description | Example |
|------------|-------------|---------|
| `#[Produces("type")]` | Response content type | `#[Produces("application/json")]` |
| `#[Consumes("type")]` | Expected request type | `#[Consumes("application/json")]` |

---

## Testing Plan

1. **Unit Tests**: Test individual runtime components
2. **Integration Tests**: Test full request/response cycle
3. **Example Programs**:
   - `examples/jetty_hello.jvmb` - Simple hello world
   - `examples/jetty_api.jvmb` - REST API demo
   - `examples/jetty_static.jvmb` - Static file serving

---

## Dependencies

Required for compilation and runtime:
- Java 21+
- Jetty 11.0.19
- Jakarta Servlet API 5.0.0
- SLF4J 2.0.9

---

## Timeline Summary

| Phase | Description | Dependencies |
|-------|-------------|--------------|
| 1 | Basic WebServer | None |
| 2 | Controller Classes | Phase 1 |
| 3 | Annotation Grammar | None |
| 4 | Annotation Processing | Phase 2, 3 |
| 5 | Advanced Features | Phase 4 |

**Recommended order**: Phase 1 -> Phase 2 -> Phase 3 -> Phase 4 -> Phase 5
