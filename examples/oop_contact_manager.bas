' Contact Manager - Demonstrates Real-World OOP Usage
' Shows: Multiple object types, data organization

' Define Contact class
CLASS Contact
    PUBLIC firstName As String
    PUBLIC lastName As String
    PUBLIC email As String
    PUBLIC phone As String
END CLASS

' Define Address class
CLASS Address
    PUBLIC street As String
    PUBLIC city As String
    PUBLIC zip As String
END CLASS

Console.WriteLine("=== Contact Manager System ===")
Console.WriteLine("")

' Create contacts
DIM contact1 AS NEW Contact()
contact1.firstName = "John"
contact1.lastName = "Doe"
contact1.email = "john.doe@email.com"
contact1.phone = "555-1234"

DIM contact2 AS NEW Contact()
contact2.firstName = "Jane"
contact2.lastName = "Smith"
contact2.email = "jane.smith@email.com"
contact2.phone = "555-5678"

DIM contact3 AS NEW Contact()
contact3.firstName = "Bob"
contact3.lastName = "Johnson"
contact3.email = "bob.johnson@email.com"
contact3.phone = "555-9012"

' Create addresses
DIM address1 AS NEW Address()
address1.street = "123 Main St"
address1.city = "Anytown"
address1.zip = "12345"

DIM address2 AS NEW Address()
address2.street = "456 Oak Ave"
address2.city = "Somewhere"
address2.zip = "67890"

DIM address3 AS NEW Address()
address3.street = "789 Pine Rd"
address3.city = "Elsewhere"
address3.zip = "54321"

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
Console.WriteLine("- Multiple CLASS declarations")
Console.WriteLine("- Object instantiation")
Console.WriteLine("- Field access and organization")
