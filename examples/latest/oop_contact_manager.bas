' Modern VB-Style Contact Manager OOP Example

CLASS Contact
    PUBLIC name AS STRING
    PUBLIC email AS STRING
    PUBLIC phone AS STRING
    
    PUBLIC SUB New(n AS STRING, e AS STRING, p AS STRING)
        name = n
        email = e
        phone = p
    END SUB
END CLASS

' Main program
Dim dummy As Integer
Let dummy = Console.WriteLine("=== Contact Manager ===")
Let dummy = Console.WriteLine("")

Dim contact1 As New Contact("Alice Johnson", "alice@email.com", "555-1001")
Dim contact2 As New Contact("Bob Smith", "bob@email.com", "555-1002")

Let dummy = Console.WriteLine("Contact 1:")
Let dummy = Console.WriteLine("  Name: " + contact1.name)
Let dummy = Console.WriteLine("  Email: " + contact1.email)
Let dummy = Console.WriteLine("  Phone: " + contact1.phone)
Let dummy = Console.WriteLine("")

Let dummy = Console.WriteLine("Contact 2:")
Let dummy = Console.WriteLine("  Name: " + contact2.name)
Let dummy = Console.WriteLine("  Email: " + contact2.email)
Let dummy = Console.WriteLine("  Phone: " + contact2.phone)

