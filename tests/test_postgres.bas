' Test PostgreSQL database connectivity

Console.WriteLine("=== PostgreSQL Database Test ===")
Console.WriteLine("")

' Connect to PostgreSQL
conn = Db.Connect("jdbc:postgresql://localhost/postgres", "developer", "test")
Console.WriteLine("Connection ID: " + conn)

IF conn > 0 THEN
    Console.WriteLine("✓ Connected to PostgreSQL")
    Console.WriteLine("")

    ' Query database version
    result = Db.Query(conn, "SELECT version()")
    Console.WriteLine("Query result ID: " + result)

    IF result > 0 THEN
        Console.WriteLine("✓ Query executed")

        ' Use Db.NextRow to advance to first row
        hasRow = Db.NextRow(result)
        IF hasRow > 0 THEN
            Console.WriteLine("✓ Row available")
            version = Db.GetString(result, "version")
            Console.WriteLine("PostgreSQL version: " + version)
        ENDIF
    ELSE
        Console.WriteLine("✗ Query failed")
    ENDIF

    ' Close connection
    closeResult = Db.Close(conn)
    Console.WriteLine("Connection closed: " + closeResult)
ELSE
    Console.WriteLine("✗ Connection failed (expected if no database running)")
ENDIF

Console.WriteLine("")
Console.WriteLine("PostgreSQL test complete!")
