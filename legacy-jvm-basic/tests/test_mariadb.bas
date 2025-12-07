' Test MariaDB database connectivity

Console.WriteLine("=== MariaDB Database Test ===")
Console.WriteLine("")

' Connect to MariaDB
Dim conn As Integer
conn = Db.Connect("jdbc:mariadb://localhost/testdb", "developer", "test")
Console.WriteLine("Connection ID: " + conn)

IF conn > 0 THEN
    Console.WriteLine("✓ Connected to MariaDB")
    Console.WriteLine("")

    ' Query current database
    Dim result As Integer
    result = Db.Query(conn, "SELECT DATABASE() as dbname")
    Console.WriteLine("Query result ID: " + result)

    IF result > 0 THEN
        Console.WriteLine("✓ Query executed")

        ' Use Db.NextRow to advance to first row
        Dim hasRow As Integer
        hasRow = Db.NextRow(result)
        IF hasRow > 0 THEN
            Console.WriteLine("✓ Row available")
            Dim dbname As String
            dbname = Db.GetString(result, "dbname")
            Console.WriteLine("Current database: " + dbname)
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
Console.WriteLine("MariaDB test complete!")
