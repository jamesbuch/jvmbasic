REM Test PostgreSQL database connectivity

Console.WriteLine("=== PostgreSQL Database Test ===")
Console.WriteLine("")

REM Connect to PostgreSQL
conn = Db.Connect("jdbc:postgresql://localhost/postgres", "developer", "test")
Console.WriteLine("Connection ID: " + conn)

IF conn > 0 THEN
    Console.WriteLine("✓ Connected to PostgreSQL")
    Console.WriteLine("")
    
    REM Query database version
    result = Db.Query(conn, "SELECT version()")
    Console.WriteLine("Query result ID: " + result)
    
    IF result > 0 THEN
        Console.WriteLine("✓ Query executed")
        Console.WriteLine("")
        
        REM Note: Db.Next() method conflicts with NEXT keyword
        REM Using direct query instead
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
Console.WriteLine("PostgreSQL test complete!")

