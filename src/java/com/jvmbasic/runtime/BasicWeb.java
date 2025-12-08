package com.jvmbasic.runtime;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;

/**
 * BasicWeb - Jetty web server integration for JVM BASIC 2.0
 *
 * Provides three namespaces:
 * - WebServer: Create and manage embedded Jetty servers
 * - Request: Access HTTP request data in handlers
 * - Response: Build HTTP responses in handlers
 *
 * Usage in BASIC:
 *   var server as Integer = WebServer.Create(8080)
 *   WebServer.AddRoute(server, "GET", "/hello", "MyApp", "HandleHello")
 *   WebServer.Start(server)
 *   WebServer.Join(server)
 */
public class BasicWeb {

    // Server storage
    private static Map<Integer, Server> servers = new ConcurrentHashMap<>();
    private static Map<Integer, ServletContextHandler> contexts = new ConcurrentHashMap<>();
    private static Map<Integer, List<RouteInfo>> serverRoutes = new ConcurrentHashMap<>();
    private static Map<Integer, List<String[]>> serverStaticDirs = new ConcurrentHashMap<>();
    private static int nextServerId = 1;

    // Thread-local request/response context for handlers
    private static ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<>();
    private static ThreadLocal<HttpServletResponse> currentResponse = new ThreadLocal<>();
    private static ThreadLocal<Map<String, String>> currentPathParams = new ThreadLocal<>();

    // MIME types for static file serving
    private static final Map<String, String> MIME_TYPES = new HashMap<>();
    static {
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("json", "application/json");
        MIME_TYPES.put("xml", "application/xml");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("ico", "image/x-icon");
        MIME_TYPES.put("woff", "font/woff");
        MIME_TYPES.put("woff2", "font/woff2");
        MIME_TYPES.put("ttf", "font/ttf");
        MIME_TYPES.put("pdf", "application/pdf");
        MIME_TYPES.put("zip", "application/zip");
    }

    // ========================================================================
    // WebServer namespace methods
    // ========================================================================

    /**
     * Create a new web server on the specified port.
     * @param port The port number to listen on
     * @return Server ID (positive) on success, -1 on error
     */
    public static int webserver_Create(int port) {
        try {
            Server server = new Server(port);
            ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
            context.setContextPath("/");

            int id = nextServerId++;
            servers.put(id, server);
            contexts.put(id, context);
            serverRoutes.put(id, new CopyOnWriteArrayList<>());
            serverStaticDirs.put(id, new CopyOnWriteArrayList<>());

            // Add dispatcher servlet to handle all requests
            context.addServlet(new ServletHolder(new BasicWebDispatcher(id)), "/*");
            server.setHandler(context);

            return id;
        } catch (Exception e) {
            System.err.println("WebServer.Create error: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Register a route handler.
     * @param serverId Server ID from Create()
     * @param method HTTP method (GET, POST, PUT, DELETE, etc.)
     * @param path URL path pattern (supports {param} placeholders)
     * @param className Name of the compiled BASIC class
     * @param handlerMethod Name of the handler method (must be static Sub)
     * @return 0 on success, -1 on error
     */
    public static int webserver_AddRoute(int serverId, String method, String path,
                                          String className, String handlerMethod) {
        List<RouteInfo> routes = serverRoutes.get(serverId);
        if (routes != null) {
            routes.add(new RouteInfo(method.toUpperCase(), path, className, handlerMethod));
            return 0;
        }
        return -1;
    }

    /**
     * Register a directory for serving static files.
     * @param serverId Server ID
     * @param urlPrefix URL path prefix (e.g., "/static")
     * @param directory File system directory path
     * @return 0 on success, -1 on error
     */
    public static int webserver_ServeStatic(int serverId, String urlPrefix, String directory) {
        List<String[]> staticDirs = serverStaticDirs.get(serverId);
        if (staticDirs != null) {
            // Normalize URL prefix
            if (!urlPrefix.startsWith("/")) urlPrefix = "/" + urlPrefix;
            if (urlPrefix.endsWith("/") && urlPrefix.length() > 1) {
                urlPrefix = urlPrefix.substring(0, urlPrefix.length() - 1);
            }
            // Resolve directory to absolute path
            File dir = new File(directory);
            if (!dir.isAbsolute()) {
                dir = new File(System.getProperty("user.dir"), directory);
            }
            staticDirs.add(new String[] { urlPrefix, dir.getAbsolutePath() });
            return 0;
        }
        return -1;
    }

    /**
     * Start the web server (non-blocking).
     * @param serverId Server ID
     * @return 0 on success, -1 on error
     */
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
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Block until the server stops.
     * @param serverId Server ID
     */
    public static void webserver_Join(int serverId) {
        try {
            Server server = servers.get(serverId);
            if (server != null) {
                server.join();
            }
        } catch (InterruptedException e) {
            // Ignore interruption
        }
    }

    /**
     * Stop the web server.
     * @param serverId Server ID
     * @return 0 on success, -1 on error
     */
    public static int webserver_Stop(int serverId) {
        try {
            Server server = servers.get(serverId);
            if (server != null) {
                server.stop();
                return 0;
            }
            return -1;
        } catch (Exception e) {
            System.err.println("WebServer.Stop error: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Check if server is running.
     * @param serverId Server ID
     * @return 1 if running, 0 if not
     */
    public static int webserver_IsRunning(int serverId) {
        Server server = servers.get(serverId);
        return (server != null && server.isRunning()) ? 1 : 0;
    }

    // ========================================================================
    // Request namespace methods
    // ========================================================================

    /** Get the HTTP method (GET, POST, etc.) */
    public static String request_GetMethod() {
        HttpServletRequest req = currentRequest.get();
        return req != null ? req.getMethod() : "";
    }

    /** Get the request path */
    public static String request_GetPath() {
        HttpServletRequest req = currentRequest.get();
        String path = req != null ? req.getPathInfo() : "";
        return path != null ? path : "/";
    }

    /** Get the query string */
    public static String request_GetQueryString() {
        HttpServletRequest req = currentRequest.get();
        String qs = req != null ? req.getQueryString() : "";
        return qs != null ? qs : "";
    }

    /** Get a query/form parameter */
    public static String request_GetParameter(String name) {
        HttpServletRequest req = currentRequest.get();
        String val = req != null ? req.getParameter(name) : "";
        return val != null ? val : "";
    }

    /** Get an HTTP header */
    public static String request_GetHeader(String name) {
        HttpServletRequest req = currentRequest.get();
        String val = req != null ? req.getHeader(name) : "";
        return val != null ? val : "";
    }

    /** Get the raw request body */
    public static String request_GetBody() {
        HttpServletRequest req = currentRequest.get();
        if (req == null) return "";
        try {
            // Check if body was already read
            String cachedBody = (String) req.getAttribute("__jvmbasic_body");
            if (cachedBody != null) return cachedBody;

            // Read and cache the body
            String body = new String(req.getInputStream().readAllBytes());
            req.setAttribute("__jvmbasic_body", body);
            return body;
        } catch (Exception e) {
            return "";
        }
    }

    /** Get body as JSON string (validates it's valid JSON) */
    public static String request_GetJsonBody() {
        String body = request_GetBody();
        if (body.isEmpty()) return "{}";
        // Validate it's valid JSON using BasicJson.IsValid
        if (BasicJson.IsValid(body)) {
            return body;
        }
        return "{}";
    }

    /** Get a path parameter from route pattern */
    public static String request_GetPathParam(String name) {
        Map<String, String> params = currentPathParams.get();
        if (params != null && params.containsKey(name)) {
            return params.get(name);
        }
        return "";
    }

    /** Get the Content-Type header */
    public static String request_GetContentType() {
        HttpServletRequest req = currentRequest.get();
        String ct = req != null ? req.getContentType() : "";
        return ct != null ? ct : "";
    }

    /** Get the client IP address */
    public static String request_GetRemoteAddr() {
        HttpServletRequest req = currentRequest.get();
        return req != null ? req.getRemoteAddr() : "";
    }

    // ========================================================================
    // Response namespace methods
    // ========================================================================

    /** Set the HTTP status code */
    public static void response_SetStatus(int status) {
        HttpServletResponse resp = currentResponse.get();
        if (resp != null) {
            resp.setStatus(status);
        }
    }

    /** Set the Content-Type header */
    public static void response_SetContentType(String contentType) {
        HttpServletResponse resp = currentResponse.get();
        if (resp != null) {
            resp.setContentType(contentType);
        }
    }

    /** Set a response header */
    public static void response_SetHeader(String name, String value) {
        HttpServletResponse resp = currentResponse.get();
        if (resp != null) {
            resp.setHeader(name, value);
        }
    }

    /** Write content to response body */
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

    /** Write content with newline to response body */
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

    /** Send a redirect response */
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

    /**
     * Route information holder
     */
    static class RouteInfo {
        String method;
        String path;
        String className;
        String handlerMethod;
        java.util.regex.Pattern pattern;
        List<String> paramNames = new ArrayList<>();
        boolean hasParams;

        RouteInfo(String method, String path, String className, String handlerMethod) {
            this.method = method;
            this.path = path;
            this.className = className;
            this.handlerMethod = handlerMethod;
            this.hasParams = path.contains("{");

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

    /**
     * Dispatcher servlet that routes requests to BASIC handlers
     */
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

            // Check for static file serving first
            List<String[]> staticDirs = serverStaticDirs.get(serverId);
            if (staticDirs != null && "GET".equals(method)) {
                for (String[] mapping : staticDirs) {
                    String urlPrefix = mapping[0];
                    String fileDir = mapping[1];
                    if (path.startsWith(urlPrefix + "/") || path.equals(urlPrefix)) {
                        String relativePath = path.substring(urlPrefix.length());
                        if (relativePath.isEmpty()) relativePath = "/index.html";
                        if (serveStaticFile(resp, fileDir, relativePath)) {
                            return;
                        }
                    }
                }
            }

            // Find matching route
            List<RouteInfo> routes = serverRoutes.get(serverId);
            RouteInfo matchedRoute = null;
            Map<String, String> pathParams = null;

            if (routes != null) {
                // Try exact match first
                for (RouteInfo route : routes) {
                    if (route.method.equals(method) && !route.hasParams) {
                        if (route.path.equals(path)) {
                            matchedRoute = route;
                            pathParams = new HashMap<>();
                            break;
                        }
                    }
                }

                // Try pattern match if no exact match
                if (matchedRoute == null) {
                    for (RouteInfo route : routes) {
                        if (route.method.equals(method) && route.hasParams) {
                            Map<String, String> params = route.match(path);
                            if (params != null) {
                                matchedRoute = route;
                                pathParams = params;
                                break;
                            }
                        }
                    }
                }
            }

            if (matchedRoute == null) {
                resp.setStatus(404);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\": \"Not Found: " + path + "\"}");
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
            } catch (ClassNotFoundException e) {
                resp.setStatus(500);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\": \"Handler class not found: " + matchedRoute.className + "\"}");
            } catch (NoSuchMethodException e) {
                resp.setStatus(500);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\": \"Handler method not found: " + matchedRoute.handlerMethod + "\"}");
            } catch (Exception e) {
                resp.setStatus(500);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\": \"Handler error: " + e.getMessage() + "\"}");
                e.printStackTrace();
            } finally {
                currentRequest.remove();
                currentResponse.remove();
                currentPathParams.remove();
            }
        }

        /**
         * Serve a static file from the file system
         * @return true if file was served, false if not found
         */
        private boolean serveStaticFile(HttpServletResponse resp, String baseDir, String relativePath)
                throws java.io.IOException {
            // Security: prevent directory traversal
            if (relativePath.contains("..")) {
                return false;
            }

            File file = new File(baseDir, relativePath);
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                return false;
            }

            // Check that the resolved path is still under baseDir (security)
            try {
                String canonicalBase = new File(baseDir).getCanonicalPath();
                String canonicalFile = file.getCanonicalPath();
                if (!canonicalFile.startsWith(canonicalBase)) {
                    return false;
                }
            } catch (IOException e) {
                return false;
            }

            // Determine MIME type
            String fileName = file.getName();
            String ext = "";
            int dotIdx = fileName.lastIndexOf('.');
            if (dotIdx > 0) {
                ext = fileName.substring(dotIdx + 1).toLowerCase();
            }
            String mimeType = MIME_TYPES.getOrDefault(ext, "application/octet-stream");

            resp.setContentType(mimeType);
            resp.setContentLengthLong(file.length());

            // Stream the file
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream out = resp.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return true;
        }
    }
}
