' webapp_tasks.bas - Full-Featured Task Management Web Application
' ================================================================
' A complete web application demonstrating:
' - Tailwind CSS for styling
' - Alpine.js for reactive UI
' - HTMX for dynamic updates without full page reloads
' - MariaDB/MySQL database with parameterized queries
' - Cookies for session management
' - File uploads
' - Full CRUD operations
'
' DATABASE SETUP:
' CREATE DATABASE tasks_db;
' USE tasks_db;
' CREATE TABLE tasks (
'     id INT AUTO_INCREMENT PRIMARY KEY,
'     title VARCHAR(255) NOT NULL,
'     description TEXT,
'     status ENUM('pending', 'in_progress', 'completed') DEFAULT 'pending',
'     priority INT DEFAULT 1,
'     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
'     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
' );
' CREATE TABLE users (
'     id INT AUTO_INCREMENT PRIMARY KEY,
'     username VARCHAR(50) UNIQUE NOT NULL,
'     email VARCHAR(255),
'     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
' );
'
' TO RUN:
' ./jvmbasic examples/webapp_tasks.bas -o TaskApp
' java -cp '.:basicrt:lib/*' TaskApp
' Visit: http://localhost:8080/

' Database connection string constants
' Note: Each request handler gets its own connection

' Test database connection at startup
Sub InitDatabase()
    Dim testConn As Integer = Db.Connect("jdbc:mariadb://localhost:3306/tasks_db", "developer", "test")
    If testConn < 0 Then
        Console.WriteLine("ERROR: Database connection failed!")
        Console.WriteLine("Make sure MariaDB is running and tasks_db exists.")
    Else
        Console.WriteLine("Database connected successfully.")
        Db.Close(testConn)
    End If
End Sub

' Helper function to get a database connection
Function GetDbConnection() As Integer
    Dim conn As Integer = Db.Connect("jdbc:mariadb://localhost:3306/tasks_db", "developer", "test")
    Return conn
End Function

' ==========================================
' HTML Templates
' ==========================================

Sub WriteHtmlHead(title As String)
    Response.Write("<!DOCTYPE html>")
    Response.Write("<html lang=\"en\">")
    Response.Write("<head>")
    Response.Write("<meta charset=\"UTF-8\">")
    Response.Write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
    Response.Write("<title>" + title + "</title>")
    ' Tailwind CSS from CDN
    Response.Write("<script src=\"https://cdn.tailwindcss.com\"></script>")
    ' Alpine.js from CDN
    Response.Write("<script defer src=\"https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js\"></script>")
    ' HTMX from CDN
    Response.Write("<script src=\"https://unpkg.com/htmx.org@1.9.10\"></script>")
    Response.Write("<style>")
    Response.Write("[x-cloak] { display: none !important; }")
    Response.Write("</style>")
    Response.Write("</head>")
End Sub

Sub WriteNavbar()
    Response.Write("<nav class=\"bg-indigo-600 shadow-lg\">")
    Response.Write("<div class=\"max-w-7xl mx-auto px-4 sm:px-6 lg:px-8\">")
    Response.Write("<div class=\"flex items-center justify-between h-16\">")
    Response.Write("<div class=\"flex items-center\">")
    Response.Write("<span class=\"text-white text-xl font-bold\">JVM BASIC Task Manager</span>")
    Response.Write("</div>")
    Response.Write("<div class=\"flex items-center space-x-4\">")
    Response.Write("<a href=\"/\" class=\"text-white hover:bg-indigo-500 px-3 py-2 rounded-md\">Tasks</a>")
    Response.Write("<a href=\"/upload\" class=\"text-white hover:bg-indigo-500 px-3 py-2 rounded-md\">Upload</a>")
    Response.Write("<a href=\"/about\" class=\"text-white hover:bg-indigo-500 px-3 py-2 rounded-md\">About</a>")
    Response.Write("</div>")
    Response.Write("</div>")
    Response.Write("</div>")
    Response.Write("</nav>")
End Sub

' ==========================================
' Page Handlers
' ==========================================

' GET / - Main task list page
Sub HandleIndex()
    Response.SetContentType("text/html")

    ' Check for session cookie
    Dim sessionId As String = Request.GetCookie("session_id")
    Dim visitCountF As Float = 0

    ' Track visit count
    Dim countStr As String = Request.GetCookie("visit_count")
    If Len(countStr) > 0 Then
        visitCountF = Val(countStr)
    End If
    visitCountF = visitCountF + 1
    Response.SetCookie("visit_count", Str(visitCountF))

    WriteHtmlHead("Task Manager - JVM BASIC")
    Response.Write("<body class=\"bg-gray-100 min-h-screen\">")
    WriteNavbar()

    Response.Write("<main class=\"max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8\">")

    ' Welcome banner
    Response.Write("<div class=\"bg-white rounded-lg shadow-md p-6 mb-6\">")
    Response.Write("<h1 class=\"text-2xl font-bold text-gray-800\">Welcome to Task Manager</h1>")
    Response.Write("<p class=\"text-gray-600\">Visit #" + Str(visitCountF) + " - Built with JVM BASIC, Tailwind CSS, Alpine.js, and HTMX</p>")
    Response.Write("</div>")

    ' Add Task Form with Alpine.js
    Response.Write("<div class=\"bg-white rounded-lg shadow-md p-6 mb-6\" x-data=\"{ open: false }\">")
    Response.Write("<div class=\"flex justify-between items-center mb-4\">")
    Response.Write("<h2 class=\"text-xl font-semibold\">Add New Task</h2>")
    Response.Write("<button @click=\"open = !open\" class=\"bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700 transition\">")
    Response.Write("<span x-text=\"open ? 'Hide Form' : 'Show Form'\"></span>")
    Response.Write("</button>")
    Response.Write("</div>")

    Response.Write("<form x-show=\"open\" x-cloak hx-post=\"/api/tasks\" hx-target=\"#task-list\" hx-swap=\"innerHTML\" class=\"space-y-4\">")
    Response.Write("<div>")
    Response.Write("<label class=\"block text-sm font-medium text-gray-700\">Title</label>")
    Response.Write("<input type=\"text\" name=\"title\" required class=\"mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2 border\">")
    Response.Write("</div>")
    Response.Write("<div>")
    Response.Write("<label class=\"block text-sm font-medium text-gray-700\">Description</label>")
    Response.Write("<textarea name=\"description\" rows=\"3\" class=\"mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2 border\"></textarea>")
    Response.Write("</div>")
    Response.Write("<div>")
    Response.Write("<label class=\"block text-sm font-medium text-gray-700\">Priority</label>")
    Response.Write("<select name=\"priority\" class=\"mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2 border\">")
    Response.Write("<option value=\"1\">Low</option>")
    Response.Write("<option value=\"2\">Medium</option>")
    Response.Write("<option value=\"3\">High</option>")
    Response.Write("</select>")
    Response.Write("</div>")
    Response.Write("<button type=\"submit\" class=\"bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 transition\">Create Task</button>")
    Response.Write("</form>")
    Response.Write("</div>")

    ' Task List - loads via HTMX
    Response.Write("<div class=\"bg-white rounded-lg shadow-md p-6\">")
    Response.Write("<h2 class=\"text-xl font-semibold mb-4\">Tasks</h2>")
    Response.Write("<div id=\"task-list\" hx-get=\"/api/tasks\" hx-trigger=\"load\" hx-swap=\"innerHTML\">")
    Response.Write("<p class=\"text-gray-500\">Loading tasks...</p>")
    Response.Write("</div>")
    Response.Write("</div>")

    Response.Write("</main>")
    Response.Write("</body></html>")
End Sub

' Helper to render task card given DB values
Sub RenderTaskCardFromDB(taskId As Integer, title As String, desc As String, status As String, priority As Integer)
    Dim badgeClass As String = ""
    Dim badgeText As String = ""
    Dim cmpResult As Integer = 0

    ' Check status using StrCmp (returns 0 if equal)
    cmpResult = StrCmp(status, "completed")
    If cmpResult < 1 Then
        badgeClass = "bg-green-100 text-green-800"
        badgeText = "Completed"
    Else
        cmpResult = StrCmp(status, "in_progress")
        If cmpResult < 1 Then
            badgeClass = "bg-yellow-100 text-yellow-800"
            badgeText = "In Progress"
        Else
            badgeClass = "bg-gray-100 text-gray-800"
            badgeText = "Pending"
        End If
    End If

    Response.Write("<div class=\"border rounded-lg p-4 hover:shadow-md transition\" x-data=\"{ expanded: false }\">")
    Response.Write("<div class=\"flex justify-between items-start\">")
    Response.Write("<div>")
    Response.Write("<h3 class=\"font-semibold text-lg\">")
    Response.Write(title)
    Response.Write("</h3>")
    Response.Write("<span class=\"inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ")
    Response.Write(badgeClass)
    Response.Write("\">")
    Response.Write(badgeText)
    Response.Write("</span>")
    Response.Write("</div>")
    Response.Write("<div class=\"flex space-x-2\">")
    Response.Write("<button @click=\"expanded = !expanded\" class=\"text-gray-500 hover:text-gray-700\">")
    Response.Write("<svg class=\"w-5 h-5\" fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M19 9l-7 7-7-7\"></path></svg>")
    Response.Write("</button>")
    Response.Write("<button hx-delete=\"/api/tasks/")
    Response.Write(Str(taskId))
    Response.Write("\" hx-target=\"#task-list\" hx-swap=\"innerHTML\" class=\"text-red-500 hover:text-red-700\">")
    Response.Write("<svg class=\"w-5 h-5\" fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16\"></path></svg>")
    Response.Write("</button>")
    Response.Write("</div>")
    Response.Write("</div>")
    Response.Write("<p x-show=\"expanded\" x-cloak class=\"mt-2 text-gray-600\">")
    Response.Write(desc)
    Response.Write("</p>")
    Response.Write("</div>")
End Sub

' GET /api/tasks - Get task list as HTML fragment (for HTMX)
Sub HandleGetTasks()
    Response.SetContentType("text/html")

    Dim conn As Integer = GetDbConnection()
    Dim hasRows As Integer = 0
    Dim taskId As Integer = 0
    Dim title As String = ""
    Dim desc As String = ""
    Dim status As String = ""
    Dim priorityVal As Integer = 0
    Dim rowCount As Integer = 0

    If conn < 0 Then
        Response.Write("<p class=\"text-red-500\">Database connection failed.</p>")
    Else
        ' Query all tasks from database
        Dim result As Integer = Db.Query(conn, "SELECT id, title, description, status, priority FROM tasks ORDER BY priority DESC, created_at DESC")

        If result < 0 Then
            Response.Write("<p class=\"text-red-500\">Error loading tasks from database.</p>")
        Else
            Response.Write("<div class=\"space-y-4\">")
            hasRows = Db.NextRow(result)
            While hasRows > 0
                taskId = Db.GetInt(result, "id")
                title = Db.GetString(result, "title")
                desc = Db.GetString(result, "description")
                status = Db.GetString(result, "status")
                priorityVal = Db.GetInt(result, "priority")
                Call RenderTaskCardFromDB(taskId, title, desc, status, priorityVal)
                rowCount = rowCount + 1
                hasRows = Db.NextRow(result)
            Wend
            Response.Write("</div>")
            Db.CloseResult(result)

            If rowCount < 1 Then
                Response.Write("<p class=\"text-gray-500\">No tasks yet. Add one above!</p>")
            End If
        End If
        Db.Close(conn)
    End If
End Sub

' POST /api/tasks - Create new task
Sub HandleCreateTask()
    Dim title As String = Request.GetParameter("title")
    Dim description As String = Request.GetParameter("description")
    Dim priorityStr As String = Request.GetParameter("priority")
    Dim priorityVal As Integer = 1
    Dim stmt As Integer = -1
    Dim rows As Integer = 0
    Dim conn As Integer = GetDbConnection()

    If Len(priorityStr) > 0 Then
        priorityVal = Int(Val(priorityStr))
    End If

    If conn > -1 Then
        ' Insert into database using parameterized query
        stmt = Db.Prepare(conn, "INSERT INTO tasks (title, description, priority, status) VALUES (?, ?, ?, 'pending')")
        If stmt > -1 Then
            Db.SetString(stmt, 1, title)
            Db.SetString(stmt, 2, description)
            Db.SetInt(stmt, 3, priorityVal)
            rows = Db.ExecuteUpdate(stmt)
            Db.CloseStmt(stmt)
        End If
        Db.Close(conn)
    End If

    ' Return updated task list
    HandleGetTasks()
End Sub

' DELETE /api/tasks/{id} - Delete task
Sub HandleDeleteTask()
    Dim taskIdStr As String = Request.GetPathParam("id")
    Dim taskIdVal As Integer = 0
    Dim stmt As Integer = -1
    Dim rows As Integer = 0
    Dim conn As Integer = GetDbConnection()

    If Len(taskIdStr) > 0 Then
        taskIdVal = Int(Val(taskIdStr))
    End If

    If conn > -1 Then
        ' Delete from database using parameterized query
        stmt = Db.Prepare(conn, "DELETE FROM tasks WHERE id = ?")
        If stmt > -1 Then
            Db.SetInt(stmt, 1, taskIdVal)
            rows = Db.ExecuteUpdate(stmt)
            Db.CloseStmt(stmt)
        End If
        Db.Close(conn)
    End If

    ' Return updated task list
    HandleGetTasks()
End Sub

' GET /upload - File upload page
Sub HandleUploadPage()
    Response.SetContentType("text/html")
    WriteHtmlHead("File Upload - JVM BASIC")
    Response.Write("<body class=\"bg-gray-100 min-h-screen\">")
    WriteNavbar()

    Response.Write("<main class=\"max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8\">")
    Response.Write("<div class=\"bg-white rounded-lg shadow-md p-6\">")
    Response.Write("<h1 class=\"text-2xl font-bold text-gray-800 mb-4\">File Upload Demo</h1>")

    Response.Write("<form action=\"/api/upload\" method=\"POST\" enctype=\"multipart/form-data\" class=\"space-y-4\">")
    Response.Write("<div>")
    Response.Write("<label class=\"block text-sm font-medium text-gray-700\">Select File</label>")
    Response.Write("<input type=\"file\" name=\"file\" class=\"mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100\">")
    Response.Write("</div>")
    Response.Write("<div>")
    Response.Write("<label class=\"block text-sm font-medium text-gray-700\">Description</label>")
    Response.Write("<input type=\"text\" name=\"description\" class=\"mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2 border\">")
    Response.Write("</div>")
    Response.Write("<button type=\"submit\" class=\"bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700 transition\">Upload</button>")
    Response.Write("</form>")

    Response.Write("</div>")
    Response.Write("</main>")
    Response.Write("</body></html>")
End Sub

' POST /api/upload - Handle file upload
Sub HandleFileUpload()
    Response.SetContentType("text/html")

    ' Declare all variables at the start
    Dim isMultipart As Integer = Request.IsMultipart()
    Dim fileCount As Integer = 0
    Dim hasFile As Integer = 0
    Dim fileName As String = ""
    Dim fileType As String = ""
    Dim fileSize As Integer = 0
    Dim saved As Integer = -1
    Dim errorMsg As String = ""
    Dim proceed As Integer = 1

    ' Check if multipart request
    If isMultipart < 1 Then
        errorMsg = "Invalid request - not a multipart form"
        proceed = 0
    End If

    ' Parse multipart and check for file
    If proceed > 0 Then
        fileCount = Request.ParseMultipart()
        hasFile = Request.HasUpload("file")
        If hasFile < 1 Then
            errorMsg = "No file was uploaded"
            proceed = 0
        End If
    End If

    ' Get file info and save
    If proceed > 0 Then
        fileName = Request.GetUploadFileName("file")
        fileType = Request.GetUploadContentType("file")
        fileSize = Request.GetUploadSize("file")
        saved = Request.SaveUpload("file", "./uploads/" + fileName)
    End If

    ' Render response
    WriteHtmlHead("Upload Result - JVM BASIC")
    Response.Write("<body class=\"bg-gray-100 min-h-screen\">")
    WriteNavbar()
    Response.Write("<main class=\"max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8\">")
    Response.Write("<div class=\"bg-white rounded-lg shadow-md p-6\">")

    If proceed < 1 Then
        Response.Write("<div class=\"bg-red-50 border border-red-200 rounded-md p-4\">")
        Response.Write("<h2 class=\"text-xl font-semibold text-red-800\">Error</h2>")
        Response.Write("<p class=\"text-red-700\">" + errorMsg + "</p>")
        Response.Write("</div>")
    ElseIf saved > -1 Then
        Response.Write("<div class=\"bg-green-50 border border-green-200 rounded-md p-4\">")
        Response.Write("<h2 class=\"text-xl font-semibold text-green-800\">Upload Successful!</h2>")
        Response.Write("<p class=\"text-green-700\">File: ")
        Response.Write(fileName)
        Response.Write(" (")
        Response.Write(fileType)
        Response.Write(")</p>")
        Response.Write("</div>")
    Else
        Response.Write("<div class=\"bg-red-50 border border-red-200 rounded-md p-4\">")
        Response.Write("<h2 class=\"text-xl font-semibold text-red-800\">Upload Failed</h2>")
        Response.Write("<p class=\"text-red-700\">Could not save file to disk</p>")
        Response.Write("</div>")
    End If

    Response.Write("<a href=\"/upload\" class=\"mt-4 inline-block text-indigo-600 hover:text-indigo-800\">Upload Another</a>")
    Response.Write("</div></main></body></html>")
End Sub

' GET /about - About page
Sub HandleAbout()
    Response.SetContentType("text/html")
    WriteHtmlHead("About - JVM BASIC")
    Response.Write("<body class=\"bg-gray-100 min-h-screen\">")
    WriteNavbar()

    Response.Write("<main class=\"max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8\">")
    Response.Write("<div class=\"bg-white rounded-lg shadow-md p-6\">")
    Response.Write("<h1 class=\"text-2xl font-bold text-gray-800 mb-4\">About This Application</h1>")

    Response.Write("<div class=\"prose max-w-none\">")
    Response.Write("<p class=\"text-gray-600 mb-4\">This task management application is built entirely in JVM BASIC, demonstrating the power of the embedded Jetty web server integration.</p>")

    Response.Write("<h2 class=\"text-xl font-semibold mt-6 mb-2\">Technologies Used</h2>")
    Response.Write("<ul class=\"list-disc list-inside text-gray-600 space-y-1\">")
    Response.Write("<li><strong>JVM BASIC</strong> - The application logic and server</li>")
    Response.Write("<li><strong>Jetty 11</strong> - Embedded web server</li>")
    Response.Write("<li><strong>Tailwind CSS</strong> - Utility-first CSS framework</li>")
    Response.Write("<li><strong>Alpine.js</strong> - Lightweight JavaScript framework</li>")
    Response.Write("<li><strong>HTMX</strong> - HTML extensions for AJAX</li>")
    Response.Write("<li><strong>MariaDB</strong> - Database (optional)</li>")
    Response.Write("</ul>")

    Response.Write("<h2 class=\"text-xl font-semibold mt-6 mb-2\">Features Demonstrated</h2>")
    Response.Write("<ul class=\"list-disc list-inside text-gray-600 space-y-1\">")
    Response.Write("<li>Route handling with path parameters</li>")
    Response.Write("<li>Cookie-based session management</li>")
    Response.Write("<li>File uploads with multipart form data</li>")
    Response.Write("<li>JSON API endpoints</li>")
    Response.Write("<li>Static file serving</li>")
    Response.Write("<li>Parameterized database queries</li>")
    Response.Write("</ul>")
    Response.Write("</div>")

    Response.Write("</div>")
    Response.Write("</main>")
    Response.Write("</body></html>")
End Sub

' GET /api/health - Health check
Sub HandleHealth()
    Response.SetContentType("application/json")
    Response.Write("{\"status\": \"healthy\", \"service\": \"JVM BASIC Task Manager\"}")
End Sub

' ==========================================
' Main Application
' ==========================================

' Initialize database
InitDatabase()

Console.WriteLine("Starting JVM BASIC Task Manager...")

Dim server As Integer = WebServer.Create(8080)
If server < 0 Then
    Console.WriteLine("Failed to create server!")
Else
    ' Static files
    WebServer.ServeStatic(server, "/static", "./public")

    ' Page routes
    WebServer.AddRoute(server, "GET", "/", "TaskApp", "HandleIndex")
    WebServer.AddRoute(server, "GET", "/upload", "TaskApp", "HandleUploadPage")
    WebServer.AddRoute(server, "GET", "/about", "TaskApp", "HandleAbout")

    ' API routes
    WebServer.AddRoute(server, "GET", "/api/health", "TaskApp", "HandleHealth")
    WebServer.AddRoute(server, "GET", "/api/tasks", "TaskApp", "HandleGetTasks")
    WebServer.AddRoute(server, "POST", "/api/tasks", "TaskApp", "HandleCreateTask")
    WebServer.AddRoute(server, "DELETE", "/api/tasks/{id}", "TaskApp", "HandleDeleteTask")
    WebServer.AddRoute(server, "POST", "/api/upload", "TaskApp", "HandleFileUpload")

    Console.WriteLine("Routes registered:")
    Console.WriteLine("  GET  /                  - Main task list")
    Console.WriteLine("  GET  /upload            - File upload page")
    Console.WriteLine("  GET  /about             - About page")
    Console.WriteLine("  GET  /api/health        - Health check")
    Console.WriteLine("  GET  /api/tasks         - List tasks (HTMX)")
    Console.WriteLine("  POST /api/tasks         - Create task")
    Console.WriteLine("  DELETE /api/tasks/{id}  - Delete task")
    Console.WriteLine("  POST /api/upload        - File upload")

    WebServer.Start(server)
    Console.WriteLine("")
    Console.WriteLine("Server running on http://localhost:8080")
    Console.WriteLine("Press Ctrl+C to stop")

    WebServer.Join(server)
End If
