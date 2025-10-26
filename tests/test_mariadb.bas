REM Test MariaDB database connectivity

Console.WriteLine("=== MariaDB Database Test ===")
Console.WriteLine("")

REM Connect to MariaDB
conn = Db.Connect("jdbc:mariadb://localhost/testdb", "developer", "test")
Console.WriteLine("Connection ID: " + conn)

IF conn > 0 THEN
    Console.WriteLine("✓ Connected to MariaDB")
    Console.WriteLine("")
    
    REM Query database
    result = Db.Query(conn, "SELECT DATABASE()")
    Console.WriteLine("Query result ID: " + result)
    
    IF result > 0 THEN
        Console.WriteLine("✓ Query executed")
        Console.WriteLine("Database connected and queryable")
    ELSE
        Console.WriteLine("✗ Query failed")
    ENDIF
    
    REM Close connection
    closeResult = Db.Close(conn)
    Console.WriteLine("Connection closed: " + closeResult)
ELSE
    Console.WriteLine("✗ Connection failed")
ENDIF

Console.WriteLine("")
Console.WriteLine("MariaDB test complete!")

