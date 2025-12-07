' Bank Account Example - Demonstrating OOP in JVM BASIC
' Shows: Classes, constructors, Public fields, object instantiation

' Define BankAccount class with constructor
Class BankAccount
    Public balance As Float
    Public owner As String
    Public accountNumber As Float

    Public Sub New(name As String, acctNum As Float, initial As Float)
        Me.owner = name
        Me.accountNumber = acctNum
        Me.balance = initial
    End Sub
End Class

' Create bank accounts using constructor syntax
Console.WriteLine("=== Bank Account Management System ===")
Console.WriteLine("")

Dim account1 As New BankAccount("Alice Johnson", 1001.0, 5000.0)
Dim account2 As New BankAccount("Bob Smith", 1002.0, 3500.0)
Dim account3 As New BankAccount("Carol White", 1003.0, 10000.0)

Console.WriteLine("Account Holder: " + account1.owner)
Console.WriteLine("Account Number: " + account1.accountNumber)
Console.WriteLine("Initial Balance: $" + account1.balance)
Console.WriteLine("")

Console.WriteLine("Account Holder: " + account2.owner)
Console.WriteLine("Account Number: " + account2.accountNumber)
Console.WriteLine("Initial Balance: $" + account2.balance)
Console.WriteLine("")

Console.WriteLine("Account Holder: " + account3.owner)
Console.WriteLine("Account Number: " + account3.accountNumber)
Console.WriteLine("Initial Balance: $" + account3.balance)
Console.WriteLine("")

Console.WriteLine("=== Bank Account Demo Complete ===")
Console.WriteLine("OOP features demonstrated:")
Console.WriteLine("- Class declarations")
Console.WriteLine("- Constructor with parameters")
Console.WriteLine("- Object instantiation (New)")
Console.WriteLine("- Field access and assignment")
