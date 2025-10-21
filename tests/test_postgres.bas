Rem Test PostgreSQL database connectivity

Print "=== PostgreSQL Database Test ==="
Print ""

' Connect to PostgreSQL
Dim conn As Integer = Db.Connect("jdbc:postgresql://localhost/postgres", "developer", "test")
Print "Connection ID: "; conn

If conn > 0 Then
    Print "✓ Connected to PostgreSQL"
    Print ""
    
    ' Query database version
    Dim result As Integer = Db.Query(conn, "SELECT version()")
    Print "Query result ID: "; result
    
    If result > 0 Then
        Print "✓ Query executed"
        Print ""
        
        ' Note: Db.Next() method conflicts with NEXT keyword
        ' Using direct query instead
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
Print "PostgreSQL test complete!"

