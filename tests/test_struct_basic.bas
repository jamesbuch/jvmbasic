' Test basic struct operations
TYPE Person
    name As String
    age As Float
ENDTYPE

DIM p As Person
p.name = "Alice"
p.age = 30.0
Console.WriteLine("Person: " + p.name + ", age " + p.age)

