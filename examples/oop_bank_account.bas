' Bank Account Example - Demonstrating OOP in JVM BASIC
' Shows: Classes, constructors, PUBLIC fields, object instantiation

' Define BankAccount class
CLASS BankAccount
    PUBLIC balance As Float
    PUBLIC owner As String
    PUBLIC accountNumber As Float
END CLASS

' Create bank accounts
Console.WriteLine("=== Bank Account Management System ===")
Console.WriteLine("")

DIM account1 AS NEW BankAccount()
account1.owner = "Alice Johnson"
account1.accountNumber = 1001.0
account1.balance = 5000.0

DIM account2 AS NEW BankAccount()
account2.owner = "Bob Smith"
account2.accountNumber = 1002.0
account2.balance = 3500.0

DIM account3 AS NEW BankAccount()
account3.owner = "Carol White"
account3.accountNumber = 1003.0
account3.balance = 10000.0

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
Console.WriteLine("- CLASS declarations")
Console.WriteLine("- PUBLIC fields")
Console.WriteLine("- Object instantiation (NEW)")
Console.WriteLine("- Field access and assignment")
