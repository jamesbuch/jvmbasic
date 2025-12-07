DIM names(3) As String

names(0) = "Alice"
names(1) = "Bob"
names(2) = "Charlie"

Console.WriteLine("Names in array:")
Console.WriteLine(names(0))
Console.WriteLine(names(1))
Console.WriteLine(names(2))

IF names(0) == "Alice" THEN
    Console.WriteLine("First name is Alice")
ENDIF

