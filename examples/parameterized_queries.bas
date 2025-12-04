' parameterized_queries.bas - Test parameterized/prepared statements
' =====================================================================
' Demonstrates safe parameterized queries to prevent SQL injection.
' Uses both PostgreSQL and MariaDB to show cross-database compatibility.
'
' PREREQUISITES:
' 1. PostgreSQL and/or MariaDB server running
' 2. User 'developer' with password 'test'
'
' PARAMETERIZED QUERY API:
'   Db.Prepare(conn, sql)         - Create prepared statement with ? placeholders
'   Db.SetString(stmt, idx, val)  - Set string parameter (1-indexed)
'   Db.SetInt(stmt, idx, val)     - Set integer parameter
'   Db.SetFloat(stmt, idx, val)   - Set float parameter
'   Db.SetDouble(stmt, idx, val)  - Set double parameter
'   Db.SetNull(stmt, idx, type)   - Set NULL parameter
'   Db.ExecuteQuery(stmt)         - Execute SELECT, returns result set ID
'   Db.ExecuteUpdate(stmt)        - Execute INSERT/UPDATE/DELETE, returns rows affected
'   Db.ClearParameters(stmt)      - Clear params for reuse
'   Db.CloseStmt(stmt)            - Close prepared statement

Console.WriteLine("======================================================")
Console.WriteLine("  Parameterized Query Test")
Console.WriteLine("======================================================")
Console.WriteLine("")

' ============================================
' Test with PostgreSQL
' ============================================
Console.WriteLine("--- Testing with PostgreSQL ---")
Dim pgUrl As String = "jdbc:postgresql://localhost:5432/postgres"
Dim pgConn As Integer = Db.Connect(pgUrl, "developer", "test")

If pgConn < 0 Then
    Console.WriteLine("PostgreSQL not available, skipping...")
Else
    Console.WriteLine("Connected to PostgreSQL")

    ' Create test table
    Db.Execute(pgConn, "DROP TABLE IF EXISTS param_test")
    Db.Execute(pgConn, "CREATE TABLE param_test (id SERIAL PRIMARY KEY, name VARCHAR(100), age INTEGER, salary NUMERIC(10,2))")
    Console.WriteLine("Created test table")

    ' Test 1: Prepared INSERT
    Console.WriteLine("")
    Console.WriteLine("Test 1: Prepared INSERT statements")
    Dim insertStmt As Integer = Db.Prepare(pgConn, "INSERT INTO param_test (name, age, salary) VALUES (?, ?, ?)")

    ' Insert first record
    Db.SetString(insertStmt, 1, "Alice O'Brien")  ' Note: apostrophe handled safely!
    Db.SetInt(insertStmt, 2, 30)
    Db.SetDouble(insertStmt, 3, 75000.50)
    Dim rows As Integer = Db.ExecuteUpdate(insertStmt)
    Console.WriteLine("  Inserted Alice: " + rows + " row(s)")

    ' Reuse statement for second record
    Db.ClearParameters(insertStmt)
    Db.SetString(insertStmt, 1, "Bob \"Bobby\" Smith")  ' Quotes handled safely!
    Db.SetInt(insertStmt, 2, 25)
    Db.SetDouble(insertStmt, 3, 55000.00)
    rows = Db.ExecuteUpdate(insertStmt)
    Console.WriteLine("  Inserted Bob: " + rows + " row(s)")

    ' Third record with different values
    Db.ClearParameters(insertStmt)
    Db.SetString(insertStmt, 1, "Carol; DROP TABLE users;--")  ' SQL injection attempt - safely escaped!
    Db.SetInt(insertStmt, 2, 35)
    Db.SetDouble(insertStmt, 3, 95000.75)
    rows = Db.ExecuteUpdate(insertStmt)
    Console.WriteLine("  Inserted Carol (SQL injection attempt safely handled): " + rows + " row(s)")

    Db.CloseStmt(insertStmt)

    ' Test 2: Prepared SELECT
    Console.WriteLine("")
    Console.WriteLine("Test 2: Prepared SELECT with parameters")
    Dim selectStmt As Integer = Db.Prepare(pgConn, "SELECT id, name, age, salary FROM param_test WHERE age > ? ORDER BY age")
    Db.SetInt(selectStmt, 1, 28)
    Dim result As Integer = Db.ExecuteQuery(selectStmt)

    Console.WriteLine("  Employees over 28:")
    While Db.NextRow(result) > 0
        Dim id As Integer = Db.GetInt(result, "id")
        Dim name As String = Db.GetString(result, "name")
        Dim age As Integer = Db.GetInt(result, "age")
        Dim salary As Double = Db.GetDouble(result, "salary")
        Console.WriteLine("    #" + id + ": " + name + ", age " + age + ", $" + salary)
    Wend
    Db.CloseResult(result)
    Db.CloseStmt(selectStmt)

    ' Test 3: Prepared UPDATE
    Console.WriteLine("")
    Console.WriteLine("Test 3: Prepared UPDATE")
    Dim updateStmt As Integer = Db.Prepare(pgConn, "UPDATE param_test SET salary = salary * ? WHERE age > ?")
    Db.SetDouble(updateStmt, 1, 1.10)  ' 10% raise
    Db.SetInt(updateStmt, 2, 30)
    rows = Db.ExecuteUpdate(updateStmt)
    Console.WriteLine("  Gave 10% raise to " + rows + " employee(s) over 30")
    Db.CloseStmt(updateStmt)

    ' Verify update
    result = Db.Query(pgConn, "SELECT name, salary FROM param_test WHERE age > 30 ORDER BY name")
    While Db.NextRow(result) > 0
        Console.WriteLine("    " + Db.GetString(result, "name") + ": $" + Db.GetDouble(result, "salary"))
    Wend
    Db.CloseResult(result)

    ' Test 4: Prepared DELETE
    Console.WriteLine("")
    Console.WriteLine("Test 4: Prepared DELETE")
    Dim deleteStmt As Integer = Db.Prepare(pgConn, "DELETE FROM param_test WHERE name = ?")
    Db.SetString(deleteStmt, 1, "Carol; DROP TABLE users;--")
    rows = Db.ExecuteUpdate(deleteStmt)
    Console.WriteLine("  Deleted Carol: " + rows + " row(s)")
    Db.CloseStmt(deleteStmt)

    ' Show remaining records
    Console.WriteLine("  Remaining records:")
    result = Db.Query(pgConn, "SELECT name FROM param_test ORDER BY name")
    While Db.NextRow(result) > 0
        Console.WriteLine("    - " + Db.GetString(result, "name"))
    Wend
    Db.CloseResult(result)

    ' Cleanup
    Db.Execute(pgConn, "DROP TABLE param_test")
    Db.Close(pgConn)
    Console.WriteLine("")
    Console.WriteLine("PostgreSQL tests complete!")
End If

Console.WriteLine("")

' ============================================
' Test with MariaDB
' ============================================
Console.WriteLine("--- Testing with MariaDB ---")
Dim myUrl As String = "jdbc:mariadb://localhost:3306/mysql"
Dim myConn As Integer = Db.Connect(myUrl, "developer", "test")

If myConn < 0 Then
    Console.WriteLine("MariaDB not available, skipping...")
Else
    Console.WriteLine("Connected to MariaDB")

    ' Create test database and table
    Db.Execute(myConn, "CREATE DATABASE IF NOT EXISTS jvmbasic_test")
    Db.Execute(myConn, "USE jvmbasic_test")
    Db.Execute(myConn, "DROP TABLE IF EXISTS param_test")
    Db.Execute(myConn, "CREATE TABLE param_test (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100), score FLOAT, active BOOLEAN)")
    Console.WriteLine("Created test table")

    ' Test: Batch insert with prepared statement
    Console.WriteLine("")
    Console.WriteLine("Test: Batch INSERT with prepared statement")
    Dim myInsert As Integer = Db.Prepare(myConn, "INSERT INTO param_test (name, score, active) VALUES (?, ?, ?)")

    ' Insert multiple records efficiently
    Db.SetString(myInsert, 1, "Player One")
    Db.SetFloat(myInsert, 2, 95.5)
    Db.SetInt(myInsert, 3, 1)  ' true
    Db.ExecuteUpdate(myInsert)

    Db.ClearParameters(myInsert)
    Db.SetString(myInsert, 1, "Player Two")
    Db.SetFloat(myInsert, 2, 87.3)
    Db.SetInt(myInsert, 3, 1)
    Db.ExecuteUpdate(myInsert)

    Db.ClearParameters(myInsert)
    Db.SetString(myInsert, 1, "Player Three")
    Db.SetFloat(myInsert, 2, 72.1)
    Db.SetInt(myInsert, 3, 0)  ' false
    Db.ExecuteUpdate(myInsert)

    Db.CloseStmt(myInsert)
    Console.WriteLine("  Inserted 3 players")

    ' Test: Parameterized SELECT with float comparison
    Console.WriteLine("")
    Console.WriteLine("Test: SELECT with float parameter")
    Dim mySelect As Integer = Db.Prepare(myConn, "SELECT name, score FROM param_test WHERE score > ? AND active = ?")
    Db.SetFloat(mySelect, 1, 80.0)
    Db.SetInt(mySelect, 2, 1)  ' active only
    Dim myResult As Integer = Db.ExecuteQuery(mySelect)

    Console.WriteLine("  Active players with score > 80:")
    While Db.NextRow(myResult) > 0
        Console.WriteLine("    " + Db.GetString(myResult, "name") + ": " + Db.GetFloat(myResult, "score"))
    Wend
    Db.CloseResult(myResult)
    Db.CloseStmt(mySelect)

    ' Cleanup
    Db.Execute(myConn, "DROP TABLE param_test")
    Db.Execute(myConn, "DROP DATABASE jvmbasic_test")
    Db.Close(myConn)
    Console.WriteLine("")
    Console.WriteLine("MariaDB tests complete!")
End If

Console.WriteLine("")
Console.WriteLine("======================================================")
Console.WriteLine("  Parameterized Query Tests Complete!")
Console.WriteLine("======================================================")
