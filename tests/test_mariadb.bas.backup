Rem Test MariaDB database connectivity

Print "=== MariaDB Database Test ==="
Print ""

' Connect to MariaDB
Dim conn As Integer = Db.Connect("jdbc:mariadb://localhost/testdb", "developer", "test")
Print "Connection ID: "; conn

If conn > 0 Then
    Print "✓ Connected to MariaDB"
    Print ""
    
    ' Query database
    Dim result As Integer = Db.Query(conn, "SELECT DATABASE()")
    Print "Query result ID: "; result
    
    If result > 0 Then
        Print "✓ Query executed"
        Print "Database connected and queryable"
    Else
        Print "✗ Query failed"
    End If
    
    ' Close connection
    Dim closeResult As Integer = Db.Close(conn)
    Print "Connection closed: "; closeResult
Else
    Print "✗ Connection failed"
End If

Print ""
Print "MariaDB test complete!"

