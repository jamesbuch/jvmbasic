' Test PostgreSQL database connectivity

Console.WriteLine("=== PostgreSQL Database Test ===")
Console.WriteLine("")

' Connect to PostgreSQL
Dim conn As Integer
conn = Db.Connect("jdbc:postgresql://localhost/postgres", "developer", "test")
Console.WriteLine("Connection ID: " + conn)

IF conn > 0 THEN
    Console.WriteLine("✓ Connected to PostgreSQL")
    Console.WriteLine("")

    ' Query database version
    Dim result As Integer
    result = Db.Query(conn, "SELECT version()")
    Console.WriteLine("Query result ID: " + result)

    IF result > 0 THEN
        Console.WriteLine("✓ Query executed")

        ' Use Db.NextRow to advance to first row
        Dim hasRow As Integer
        hasRow = Db.NextRow(result)
        IF hasRow > 0 THEN
            Console.WriteLine("✓ Row available")
            Dim version As String
            version = Db.GetString(result, "version")
            Console.WriteLine("PostgreSQL version: " + version)
        ENDIF
    ELSE
        Console.WriteLine("✗ Query failed")
    ENDIF

    ' Close connection
    Dim closeResult As Integer
    closeResult = Db.Close(conn)
    Console.WriteLine("Connection closed: " + closeResult)
ELSE
    Console.WriteLine("✗ Connection failed (expected if no database running)")
ENDIF

Console.WriteLine("")
Console.WriteLine("PostgreSQL test complete!")
