REM Bank Account Example - Demonstrating OOP in JVM BASIC
REM Shows: Classes, constructors, PUBLIC/PRIVATE fields, object instantiation

REM Define BankAccount class
CLASS BankAccount
    PRIVATE balance As Float
    PUBLIC owner As String
    PUBLIC accountNumber As Float
    
SUB New(name As String, accountNum As Float, initialBalance As Float)
        owner = name
        accountNumber = accountNum
        balance = initialBalance
    END SUB
END CLASS

REM Create bank accounts
Console.WriteLine("=== Bank Account Management System ===")
Console.WriteLine("")

DIM account1 AS NEW BankAccount("Alice Johnson", 1001.0, 5000.0)
DIM account2 AS NEW BankAccount("Bob Smith", 1002.0, 3500.0)
DIM account3 AS NEW BankAccount("Carol White", 1003.0, 10000.0)

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
Console.WriteLine("✓ CLASS declarations")
Console.WriteLine("✓ PUBLIC/PRIVATE fields")
Console.WriteLine("✓ Constructor (SUB New)")
Console.WriteLine("✓ Object instantiation (NEW)")
Console.WriteLine("✓ Field access")