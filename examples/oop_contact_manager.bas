' Contact Manager - Demonstrates Real-World OOP Usage
' Shows: Multiple object types, data organization

' Define Contact class with constructor
Class Contact
    Public firstName As String
    Public lastName As String
    Public email As String
    Public phone As String

    Public Sub New(first As String, last As String, mail As String, ph As String)
        Me.firstName = first
        Me.lastName = last
        Me.email = mail
        Me.phone = ph
    End Sub
End Class

' Define Address class with constructor
Class Address
    Public street As String
    Public city As String
    Public zip As String

    Public Sub New(st As String, ct As String, zp As String)
        Me.street = st
        Me.city = ct
        Me.zip = zp
    End Sub
End Class

Console.WriteLine("=== Contact Manager System ===")
Console.WriteLine("")

' Create contacts using constructor
Dim contact1 As New Contact("John", "Doe", "john.doe@email.com", "555-1234")
Dim contact2 As New Contact("Jane", "Smith", "jane.smith@email.com", "555-5678")
Dim contact3 As New Contact("Bob", "Johnson", "bob.johnson@email.com", "555-9012")

' Create addresses using constructor
Dim address1 As New Address("123 Main St", "Anytown", "12345")
Dim address2 As New Address("456 Oak Ave", "Somewhere", "67890")
Dim address3 As New Address("789 Pine Rd", "Elsewhere", "54321")

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
Console.WriteLine("- Multiple Class declarations")
Console.WriteLine("- Constructor with parameters")
Console.WriteLine("- Object instantiation")
Console.WriteLine("- Field access and organization")
