# Runtime Library Design

## Overview

The runtime library provides:
1. Built-in functions and types
2. Standard library (Console, File, Http, etc.)
3. Java interoperability utilities
4. Web server framework

## Package Structure

```
com.jvmbasic.runtime
├── core/              # Core types and utilities
│   ├── BasicString    # String extensions
│   ├── BasicArray     # Array utilities
│   ├── BasicMath      # Math functions
│   └── Convert        # Type conversions
│
├── io/                # Input/Output
│   ├── Console        # Console I/O
│   ├── File           # File operations
│   └── Stream         # Stream utilities
│
├── collections/       # Collections
│   ├── BasicList      # List<T> wrapper
│   ├── BasicMap       # Map<K,V> wrapper
│   └── BasicSet       # Set<T> wrapper
│
├── net/               # Networking
│   ├── Http           # HTTP client
│   └── Socket         # TCP/UDP sockets
│
├── data/              # Data handling
│   ├── Json           # JSON parsing
│   ├── Xml            # XML parsing
│   └── Db             # Database access
│
├── web/               # Web framework
│   ├── WebServer      # Jetty wrapper
│   ├── Request        # HTTP request
│   ├── Response       # HTTP response
│   └── Route          # Route handling
│
└── interop/           # Java interop
    ├── JavaClass      # Class reflection
    └── JavaMethod     # Method invocation
```

## Core Types

### String Extensions

```java
package com.jvmbasic.runtime.core;

public final class BasicString {

    // String functions (static methods callable from BASIC)

    public static int len(String s) {
        return s == null ? 0 : s.length();
    }

    public static String left(String s, int n) {
        if (s == null || n <= 0) return "";
        return s.substring(0, Math.min(n, s.length()));
    }

    public static String right(String s, int n) {
        if (s == null || n <= 0) return "";
        int start = Math.max(0, s.length() - n);
        return s.substring(start);
    }

    public static String mid(String s, int start, int length) {
        if (s == null || start < 1) return "";
        int idx = start - 1;  // BASIC is 1-indexed
        if (idx >= s.length()) return "";
        int end = Math.min(idx + length, s.length());
        return s.substring(idx, end);
    }

    public static int instr(String haystack, String needle) {
        if (haystack == null || needle == null) return 0;
        int idx = haystack.indexOf(needle);
        return idx < 0 ? 0 : idx + 1;  // 1-indexed
    }

    public static int instrRev(String haystack, String needle) {
        if (haystack == null || needle == null) return 0;
        int idx = haystack.lastIndexOf(needle);
        return idx < 0 ? 0 : idx + 1;
    }

    public static String replace(String s, String find, String replacement) {
        if (s == null) return "";
        return s.replace(find, replacement);
    }

    public static String[] split(String s, String delimiter) {
        if (s == null) return new String[0];
        return s.split(java.util.regex.Pattern.quote(delimiter));
    }

    public static String join(String[] arr, String delimiter) {
        return String.join(delimiter, arr);
    }

    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    public static String upper(String s) {
        return s == null ? "" : s.toUpperCase();
    }

    public static String lower(String s) {
        return s == null ? "" : s.toLowerCase();
    }

    public static String format(String pattern, Object... args) {
        return String.format(pattern, args);
    }
}
```

### Type Conversions

```java
package com.jvmbasic.runtime.core;

public final class Convert {

    // String to numeric
    public static int toInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static long toLong(String s) {
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public static float toFloat(String s) {
        try {
            return Float.parseFloat(s.trim());
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    public static double toDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // Numeric to string
    public static String str(int n) {
        return Integer.toString(n);
    }

    public static String str(long n) {
        return Long.toString(n);
    }

    public static String str(float n) {
        return Float.toString(n);
    }

    public static String str(double n) {
        return Double.toString(n);
    }

    // Formatting
    public static String formatInt(String pattern, int n) {
        return String.format(pattern, n);
    }

    public static String formatFloat(String pattern, double n) {
        return String.format(pattern, n);
    }

    // Character conversions
    public static int asc(String s) {
        return s.isEmpty() ? 0 : s.charAt(0);
    }

    public static String chr(int code) {
        return String.valueOf((char) code);
    }

    // Hex/Binary
    public static String hex(int n) {
        return Integer.toHexString(n).toUpperCase();
    }

    public static String bin(int n) {
        return Integer.toBinaryString(n);
    }

    public static int parseHex(String s) {
        return Integer.parseInt(s, 16);
    }

    public static int parseBin(String s) {
        return Integer.parseInt(s, 2);
    }
}
```

## Console I/O

```java
package com.jvmbasic.runtime.io;

import java.io.*;
import java.util.Scanner;

public final class Console {
    private static final Scanner scanner = new Scanner(System.in);
    private static final PrintStream out = System.out;

    public static void write(String s) {
        out.print(s);
    }

    public static void writeLine(String s) {
        out.println(s);
    }

    public static void writeLine() {
        out.println();
    }

    // Overloads for all types
    public static void writeLine(int n) { out.println(n); }
    public static void writeLine(long n) { out.println(n); }
    public static void writeLine(float n) { out.println(n); }
    public static void writeLine(double n) { out.println(n); }
    public static void writeLine(boolean b) { out.println(b); }

    public static String readLine() {
        return scanner.nextLine();
    }

    public static String readLine(String prompt) {
        out.print(prompt);
        return scanner.nextLine();
    }

    public static int readInt() {
        return scanner.nextInt();
    }

    public static int readInt(String prompt) {
        out.print(prompt);
        return scanner.nextInt();
    }

    public static void clear() {
        // ANSI escape for clear screen
        out.print("\033[H\033[2J");
        out.flush();
    }

    public static void setColor(int foreground, int background) {
        out.printf("\033[%d;%dm", foreground + 30, background + 40);
    }

    public static void resetColor() {
        out.print("\033[0m");
    }
}
```

## Database Access

```java
package com.jvmbasic.runtime.data;

import java.sql.*;
import java.util.*;

public final class Db {
    private static final Map<Integer, Connection> connections = new HashMap<>();
    private static final Map<Integer, PreparedStatement> statements = new HashMap<>();
    private static final Map<Integer, ResultSet> resultSets = new HashMap<>();
    private static int nextId = 1;

    public static int connect(String url, String user, String password) {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            int id = nextId++;
            connections.put(id, conn);
            return id;
        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
            return -1;
        }
    }

    public static int prepare(int connId, String sql) {
        try {
            Connection conn = connections.get(connId);
            PreparedStatement stmt = conn.prepareStatement(sql);
            int id = nextId++;
            statements.put(id, stmt);
            return id;
        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
            return -1;
        }
    }

    public static void setString(int stmtId, int index, String value) {
        try {
            statements.get(stmtId).setString(index, value);
        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
        }
    }

    public static void setInt(int stmtId, int index, int value) {
        try {
            statements.get(stmtId).setInt(index, value);
        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
        }
    }

    public static void setDouble(int stmtId, int index, double value) {
        try {
            statements.get(stmtId).setDouble(index, value);
        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
        }
    }

    public static int executeQuery(int stmtId) {
        try {
            ResultSet rs = statements.get(stmtId).executeQuery();
            int id = nextId++;
            resultSets.put(id, rs);
            return id;
        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
            return -1;
        }
    }

    public static int executeUpdate(int stmtId) {
        try {
            return statements.get(stmtId).executeUpdate();
        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
            return -1;
        }
    }

    public static boolean next(int rsId) {
        try {
            return resultSets.get(rsId).next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getString(int rsId, String column) {
        try {
            return resultSets.get(rsId).getString(column);
        } catch (SQLException e) {
            return "";
        }
    }

    public static int getInt(int rsId, String column) {
        try {
            return resultSets.get(rsId).getInt(column);
        } catch (SQLException e) {
            return 0;
        }
    }

    public static double getDouble(int rsId, String column) {
        try {
            return resultSets.get(rsId).getDouble(column);
        } catch (SQLException e) {
            return 0.0;
        }
    }

    public static void close(int id) {
        try {
            if (connections.containsKey(id)) {
                connections.remove(id).close();
            } else if (statements.containsKey(id)) {
                statements.remove(id).close();
            } else if (resultSets.containsKey(id)) {
                resultSets.remove(id).close();
            }
        } catch (SQLException e) {
            // Ignore close errors
        }
    }
}
```

## Web Server

```java
package com.jvmbasic.runtime.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import jakarta.servlet.http.*;

import java.util.*;
import java.util.regex.*;
import java.lang.reflect.Method;

public final class WebServer {
    private static final Map<Integer, ServerInstance> servers = new HashMap<>();
    private static int nextId = 1;

    public static int create(int port) {
        try {
            Server server = new Server(port);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);

            ServerInstance instance = new ServerInstance(server, context);
            int id = nextId++;
            servers.put(id, instance);
            return id;
        } catch (Exception e) {
            return -1;
        }
    }

    public static void addRoute(int serverId, String method, String path,
                                String className, String handlerName) {
        ServerInstance instance = servers.get(serverId);
        if (instance == null) return;

        RouteInfo route = new RouteInfo(method, path, className, handlerName);
        instance.routes.add(route);
    }

    public static void serveStatic(int serverId, String urlPrefix, String directory) {
        ServerInstance instance = servers.get(serverId);
        if (instance == null) return;

        instance.staticPaths.put(urlPrefix, directory);
    }

    public static void start(int serverId) {
        ServerInstance instance = servers.get(serverId);
        if (instance == null) return;

        try {
            // Add dispatcher servlet
            instance.context.addServlet(
                new ServletHolder(new DispatcherServlet(instance)),
                "/*"
            );
            instance.server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stop(int serverId) {
        ServerInstance instance = servers.get(serverId);
        if (instance != null) {
            try {
                instance.server.stop();
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    public static void join(int serverId) {
        ServerInstance instance = servers.get(serverId);
        if (instance != null) {
            try {
                instance.server.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static boolean isRunning(int serverId) {
        ServerInstance instance = servers.get(serverId);
        return instance != null && instance.server.isRunning();
    }
}

// Internal classes
class ServerInstance {
    final Server server;
    final ServletContextHandler context;
    final List<RouteInfo> routes = new ArrayList<>();
    final Map<String, String> staticPaths = new HashMap<>();

    ServerInstance(Server server, ServletContextHandler context) {
        this.server = server;
        this.context = context;
    }
}

class RouteInfo {
    final String method;
    final Pattern pattern;
    final List<String> paramNames;
    final String className;
    final String handlerName;

    RouteInfo(String method, String path, String className, String handlerName) {
        this.method = method;
        this.className = className;
        this.handlerName = handlerName;
        this.paramNames = new ArrayList<>();

        // Convert path pattern to regex
        String regex = path.replaceAll("\\{([^}]+)\\}", "(?<$1>[^/]+)");
        Matcher m = Pattern.compile("\\{([^}]+)\\}").matcher(path);
        while (m.find()) {
            paramNames.add(m.group(1));
        }
        this.pattern = Pattern.compile("^" + regex + "$");
    }
}
```

## Request/Response Objects

```java
package com.jvmbasic.runtime.web;

import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

public final class Request {
    private static final ThreadLocal<HttpServletRequest> current = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, String>> pathParams = new ThreadLocal<>();

    static void setCurrent(HttpServletRequest req) {
        current.set(req);
        pathParams.set(new HashMap<>());
    }

    static void setPathParam(String name, String value) {
        pathParams.get().put(name, value);
    }

    public static String getMethod() {
        return current.get().getMethod();
    }

    public static String getPath() {
        return current.get().getPathInfo();
    }

    public static String getParameter(String name) {
        String value = current.get().getParameter(name);
        return value != null ? value : "";
    }

    public static String getHeader(String name) {
        String value = current.get().getHeader(name);
        return value != null ? value : "";
    }

    public static String getPathParam(String name) {
        String value = pathParams.get().get(name);
        return value != null ? value : "";
    }

    public static String getBody() {
        try {
            BufferedReader reader = current.get().getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static String getCookie(String name) {
        Cookie[] cookies = current.get().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    public static String getRemoteAddr() {
        return current.get().getRemoteAddr();
    }
}

public final class Response {
    private static final ThreadLocal<HttpServletResponse> current = new ThreadLocal<>();

    static void setCurrent(HttpServletResponse resp) {
        current.set(resp);
    }

    public static void setStatus(int code) {
        current.get().setStatus(code);
    }

    public static void setContentType(String type) {
        current.get().setContentType(type);
    }

    public static void setHeader(String name, String value) {
        current.get().setHeader(name, value);
    }

    public static void write(String content) {
        try {
            current.get().getWriter().print(content);
        } catch (IOException e) {
            // Ignore
        }
    }

    public static void writeLine(String content) {
        try {
            current.get().getWriter().println(content);
        } catch (IOException e) {
            // Ignore
        }
    }

    public static void redirect(String url) {
        try {
            current.get().sendRedirect(url);
        } catch (IOException e) {
            // Ignore
        }
    }

    public static void setCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        current.get().addCookie(cookie);
    }

    public static void setCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        current.get().addCookie(cookie);
    }
}
```

## Built-in Function Registry

```java
package com.jvmbasic.runtime;

import java.util.*;

public final class BuiltinRegistry {
    private static final Map<String, MethodHandle> functions = new HashMap<>();

    static {
        // String functions
        register("LEN", BasicString.class, "len", String.class);
        register("LEFT", BasicString.class, "left", String.class, int.class);
        register("RIGHT", BasicString.class, "right", String.class, int.class);
        register("MID", BasicString.class, "mid", String.class, int.class, int.class);
        register("INSTR", BasicString.class, "instr", String.class, String.class);
        register("REPLACE", BasicString.class, "replace", String.class, String.class, String.class);
        register("TRIM", BasicString.class, "trim", String.class);
        register("UPPER", BasicString.class, "upper", String.class);
        register("LOWER", BasicString.class, "lower", String.class);

        // Conversion functions
        register("STR", Convert.class, "str", int.class);
        register("STR", Convert.class, "str", long.class);
        register("STR", Convert.class, "str", float.class);
        register("STR", Convert.class, "str", double.class);
        register("VAL", Convert.class, "toDouble", String.class);
        register("INT", Convert.class, "toInt", String.class);
        register("ASC", Convert.class, "asc", String.class);
        register("CHR", Convert.class, "chr", int.class);

        // Math functions
        register("ABS", Math.class, "abs", int.class);
        register("ABS", Math.class, "abs", double.class);
        register("SQR", Math.class, "sqrt", double.class);
        register("SIN", Math.class, "sin", double.class);
        register("COS", Math.class, "cos", double.class);
        register("TAN", Math.class, "tan", double.class);
        register("LOG", Math.class, "log", double.class);
        register("EXP", Math.class, "exp", double.class);
        register("POW", Math.class, "pow", double.class, double.class);
        register("RND", BasicMath.class, "rnd");
        register("ROUND", Math.class, "round", double.class);
        register("FLOOR", Math.class, "floor", double.class);
        register("CEIL", Math.class, "ceil", double.class);
    }

    private static void register(String name, Class<?> clazz, String method, Class<?>... params) {
        try {
            functions.put(name, MethodHandles.lookup().findStatic(clazz, method,
                MethodType.methodType(getReturnType(clazz, method, params), params)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to register " + name, e);
        }
    }

    public static MethodHandle lookup(String name) {
        return functions.get(name.toUpperCase());
    }
}
```
