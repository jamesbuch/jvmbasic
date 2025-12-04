# Berkeley Sockets API Support for JVM BASIC

## Overview

This document outlines a plan for adding socket support to JVM BASIC, enabling low-level TCP/UDP network programming. Java's Socket API closely mirrors the Berkeley sockets API, making it straightforward to implement.

## Java Socket API vs Berkeley Sockets

Java's networking classes provide similar functionality to BSD sockets:

| Berkeley Sockets | Java Equivalent | Description |
|-----------------|-----------------|-------------|
| `socket()` | `new Socket()` / `new ServerSocket()` | Create socket |
| `bind()` | `ServerSocket(port)` | Bind to port |
| `listen()` | `ServerSocket.accept()` | Listen for connections |
| `accept()` | `ServerSocket.accept()` | Accept connection |
| `connect()` | `Socket(host, port)` | Connect to server |
| `send()` / `write()` | `OutputStream.write()` | Send data |
| `recv()` / `read()` | `InputStream.read()` | Receive data |
| `close()` | `Socket.close()` | Close socket |

## Proposed JVM BASIC Socket API

### TCP Client Socket

```basic
' Create and connect to a server
Dim sock As Integer = Socket.Connect("example.com", 80)

' Send data
Socket.Write(sock, "GET / HTTP/1.0\r\nHost: example.com\r\n\r\n")

' Read response
Dim response As String = Socket.ReadAll(sock)
Console.WriteLine(response)

' Close connection
Socket.Close(sock)
```

### TCP Server Socket

```basic
' Create server socket
Dim server As Integer = Socket.Listen(8080)
Console.WriteLine("Server listening on port 8080")

' Accept connections in a loop
While True
    Dim client As Integer = Socket.Accept(server)
    Dim clientAddr As String = Socket.GetRemoteAddress(client)
    Console.WriteLine("Connection from: " + clientAddr)

    ' Read request
    Dim request As String = Socket.ReadLine(client)
    Console.WriteLine("Request: " + request)

    ' Send response
    Socket.Write(client, "HTTP/1.0 200 OK\r\n")
    Socket.Write(client, "Content-Type: text/html\r\n\r\n")
    Socket.Write(client, "<html><body><h1>Hello from JVM BASIC!</h1></body></html>")

    Socket.Close(client)
Wend
```

### UDP Socket

```basic
' Create UDP socket
Dim udpSock As Integer = Socket.CreateUDP()

' Bind to port for receiving
Socket.BindUDP(udpSock, 5000)

' Send datagram
Socket.SendTo(udpSock, "Hello UDP", "192.168.1.100", 5001)

' Receive datagram
Dim data As String = Socket.ReceiveFrom(udpSock)
Dim senderAddr As String = Socket.GetLastSender(udpSock)
Console.WriteLine("Received from " + senderAddr + ": " + data)

Socket.Close(udpSock)
```

## Proposed Runtime Methods

### socket_ Namespace Methods

```java
// TCP Client
public static int socket_Connect(String host, int port)
public static int socket_Write(int sockId, String data)
public static int socket_WriteBytes(int sockId, byte[] data)
public static String socket_ReadLine(int sockId)
public static String socket_ReadAll(int sockId)
public static byte[] socket_ReadBytes(int sockId, int length)
public static int socket_Available(int sockId)  // Bytes available to read
public static int socket_Close(int sockId)

// TCP Server
public static int socket_Listen(int port)
public static int socket_Accept(int serverSockId)
public static String socket_GetRemoteAddress(int sockId)
public static int socket_GetRemotePort(int sockId)

// UDP
public static int socket_CreateUDP()
public static int socket_BindUDP(int sockId, int port)
public static int socket_SendTo(int sockId, String data, String host, int port)
public static String socket_ReceiveFrom(int sockId)
public static String socket_GetLastSender(int sockId)

// Options
public static int socket_SetTimeout(int sockId, int milliseconds)
public static int socket_SetOption(int sockId, String option, String value)
```

## Implementation Strategy

### Phase 1: TCP Client Sockets
1. Implement `socket_Connect`, `socket_Write`, `socket_ReadLine`, `socket_ReadAll`, `socket_Close`
2. Use internal `Map<Integer, Socket>` to track open sockets
3. Add proper resource cleanup and timeout handling

### Phase 2: TCP Server Sockets
1. Implement `socket_Listen`, `socket_Accept`, `socket_GetRemoteAddress`
2. Use `Map<Integer, ServerSocket>` for server sockets
3. Handle multi-client scenarios

### Phase 3: UDP Sockets
1. Implement UDP-specific methods using `DatagramSocket`
2. Add `Map<Integer, DatagramSocket>` for UDP sockets

### Phase 4: Non-blocking I/O (Optional)
1. Consider adding NIO-based non-blocking sockets
2. Implement selector-based multiplexing for high-performance servers

## Comparison: Sockets vs Jetty for Web Servers

| Feature | Raw Sockets | Jetty |
|---------|-------------|-------|
| HTTP Parsing | Manual | Built-in |
| HTTPS/TLS | Manual SSL setup | Built-in |
| Performance | Varies | Optimized |
| Complexity | High | Low |
| Learning Value | High | Low |
| Protocol Flexibility | Any protocol | HTTP only |
| Use Case | Education, custom protocols | Web apps |

### Recommendation

- **Use Jetty** for web applications needing standard HTTP features
- **Use Sockets** for:
  - Custom protocols (game servers, chat, IoT)
  - Educational purposes (understanding networking)
  - Simple HTTP servers without framework overhead
  - Non-HTTP services (SMTP, FTP, custom protocols)

## Example: Simple HTTP Server with Sockets

```basic
' simple_http_server.bas - Minimal HTTP/1.0 server using sockets
' Serves static files from wwwroot directory

Dim PORT As Integer = 8080
Dim WWWROOT As String = "./wwwroot"

Console.WriteLine("Starting HTTP server on port " + PORT)
Dim server As Integer = Socket.Listen(PORT)

While True
    Dim client As Integer = Socket.Accept(server)

    ' Read HTTP request line
    Dim requestLine As String = Socket.ReadLine(client)
    Console.WriteLine("Request: " + requestLine)

    ' Parse: GET /path HTTP/1.0
    Dim method As String = Regex.Group("^(\\w+)", requestLine, 1)
    Dim path As String = Regex.Group("\\s(/[^\\s]*)", requestLine, 1)

    If path = "/" Then path = "/index.html"

    Dim filePath As String = WWWROOT + path

    If File.Exists(filePath) Then
        Dim content As String = File.ReadAll(filePath)
        Dim contentType As String = GetContentType(path)

        Socket.Write(client, "HTTP/1.0 200 OK\r\n")
        Socket.Write(client, "Content-Type: " + contentType + "\r\n")
        Socket.Write(client, "Content-Length: " + Len(content) + "\r\n")
        Socket.Write(client, "\r\n")
        Socket.Write(client, content)
    Else
        Socket.Write(client, "HTTP/1.0 404 Not Found\r\n\r\n")
        Socket.Write(client, "<h1>404 Not Found</h1>")
    End If

    Socket.Close(client)
Wend

Function GetContentType(path As String) As String
    If Regex.Match("\\.html$", path) Then Return "text/html"
    If Regex.Match("\\.css$", path) Then Return "text/css"
    If Regex.Match("\\.js$", path) Then Return "application/javascript"
    If Regex.Match("\\.png$", path) Then Return "image/png"
    If Regex.Match("\\.jpg$", path) Then Return "image/jpeg"
    If Regex.Match("\\.gif$", path) Then Return "image/gif"
    Return "application/octet-stream"
End Function
```

## wwwroot Directory Structure

```
wwwroot/
├── index.html
├── css/
│   └── style.css
├── js/
│   └── app.js
└── images/
    └── logo.png
```

## Security Considerations

1. **Path Traversal**: Validate paths to prevent `../` attacks
2. **Buffer Limits**: Set maximum read sizes to prevent memory exhaustion
3. **Timeout**: Always set socket timeouts to prevent hanging connections
4. **Resource Cleanup**: Ensure sockets are closed in error handlers

## Integration with Existing Features

Socket support would complement existing JVM BASIC features:

- **Threads**: Multi-threaded server handling
- **File I/O**: Serving static files
- **Regex**: HTTP request parsing
- **JSON**: Building/parsing JSON APIs
- **Database**: Building data-driven services

## Timeline Estimate

- Phase 1 (TCP Client): 2-3 days
- Phase 2 (TCP Server): 2-3 days
- Phase 3 (UDP): 1-2 days
- Phase 4 (NIO): 3-5 days (optional)

Total: ~1-2 weeks for full socket support
