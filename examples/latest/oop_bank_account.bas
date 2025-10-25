Rem Modern VB-Style Bank Account OOP

Class BankAccount
    Public balance As Single
    Public owner As String
    Public accountNumber As Single
    
    Public Sub New(name As String, accountNum As Single, initialBalance As Single)
        owner = name
        accountNumber = accountNum
        balance = initialBalance
    End Sub
End Class

Rem Main program
Console.WriteLine("=== Bank Account Management System ===")
Console.WriteLine("")

Dim account1 As New BankAccount("Alice Johnson", 1001.0, 5000.0)
Dim account2 As New BankAccount("Bob Smith", 1002.0, 3500.0)

Console.WriteLine("Account 1:")
Console.WriteLine($"  Holder: {account1.owner}")
Console.WriteLine($"  Number: {account1.accountNumber}")
Console.WriteLine($"  Balance: ${account1.balance}")
Console.WriteLine("")

Console.WriteLine("Account 2:")
Console.WriteLine($"  Holder: {account2.owner}")
Console.WriteLine($"  Number: {account2.accountNumber}")
Console.WriteLine($"  Balance: ${account2.balance}")
Console.WriteLine("")

Rem Perform transactions
Dim bal1 As Single = account1.balance
Let bal1 = bal1 + 500.0
Let account1.balance = bal1

Dim bal2 As Single = account2.balance
Let bal2 = bal2 - 200.0
Let account2.balance = bal2

Console.WriteLine("=== After Transactions ===")
Console.WriteLine($"{account1.owner} new balance: ${account1.balance}")
Console.WriteLine($"{account2.owner} new balance: ${account2.balance}")
