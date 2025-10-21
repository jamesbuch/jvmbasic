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
Print "=== Bank Account Management System ==="
Print ""

Dim account1 As New BankAccount("Alice Johnson", 1001.0, 5000.0)
Dim account2 As New BankAccount("Bob Smith", 1002.0, 3500.0)

Print "Account 1:"
Print "  Holder: "; account1.owner
Print "  Number: "; account1.accountNumber
Print "  Balance: $"; account1.balance
Print ""

Print "Account 2:"
Print "  Holder: "; account2.owner
Print "  Number: "; account2.accountNumber
Print "  Balance: $"; account2.balance
Print ""

Rem Perform transactions
Dim bal1 As Single = account1.balance
Let bal1 = bal1 + 500.0
Let account1.balance = bal1

Dim bal2 As Single = account2.balance
Let bal2 = bal2 - 200.0
Let account2.balance = bal2

Print "=== After Transactions ==="
Print account1.owner; " new balance: $"; account1.balance
Print account2.owner; " new balance: $"; account2.balance
