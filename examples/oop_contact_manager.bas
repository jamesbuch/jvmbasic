REM Contact Manager - Demonstrates Real-World OOP Usage
REM Shows: Multiple object types, constructors, data organization

REM Define Contact class
CLASS Contact
    PUBLIC firstName As String
    PUBLIC lastName As String
    PUBLIC email As String
    PUBLIC phone As String
    
SUB New(first As String, last As String, mail As String, number As String)
        firstName = first
        lastName = last
        email = mail
        phone = number
    END SUB
END CLASS

REM Define Address class
CLASS Address
    PUBLIC street As String
    PUBLIC city As String
    PUBLIC zip As String
    
SUB New(st As String, ct As String, z As String)
        street = st
        city = ct
        zip = z
    END SUB
END CLASS

Console.WriteLine("=== Contact Manager System ===")
Console.WriteLine("")

REM Create contacts
DIM contact1 AS NEW Contact("John", "Doe", "john.doe@email.com", "555-1234")
DIM contact2 AS NEW Contact("Jane", "Smith", "jane.smith@email.com", "555-5678")
DIM contact3 AS NEW Contact("Bob", "Johnson", "bob.johnson@email.com", "555-9012")

REM Create addresses
DIM address1 AS NEW Address("123 Main St", "Anytown", "12345")
DIM address2 AS NEW Address("456 Oak Ave", "Somewhere", "67890")
DIM address3 AS NEW Address("789 Pine Rd", "Elsewhere", "54321")

Console.WriteLine("Contact 1:")
Console.WriteLine("  Name: " + contact1.firstName + " " + contact1.lastName)
Console.WriteLine("  Email: " + contact1.email)
Console.WriteLine("  Phone: " + contact1.phone)
Console.WriteLine("  Address: " + address1.street + ", " + address1.city + " " + address1.zip)
Console.WriteLine("")

Console.WriteLine("Contact 2:")
Console.WriteLine("  Name: " + contact2.firstName + " " + contact2.lastName)
Console.WriteLine("  Email: " + contact2.email)
Console.WriteLine("  Phone: " + contact2.phone)
Console.WriteLine("  Address: " + address2.street + ", " + address2.city + " " + address2.zip)
Console.WriteLine("")

Console.WriteLine("Contact 3:")
Console.WriteLine("  Name: " + contact3.firstName + " " + contact3.lastName)
Console.WriteLine("  Email: " + contact3.email)
Console.WriteLine("  Phone: " + contact3.phone)
Console.WriteLine("  Address: " + address3.street + ", " + address3.city + " " + address3.zip)
Console.WriteLine("")

Console.WriteLine("=== Contact Manager Demo Complete ===")
Console.WriteLine("OOP features demonstrated:")
Console.WriteLine("✓ Multiple CLASS declarations")
Console.WriteLine("✓ Constructor parameters")
Console.WriteLine("✓ Object instantiation")
Console.WriteLine("✓ Field access and organization")