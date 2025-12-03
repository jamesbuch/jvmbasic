Class BankAccount
    Public owner As String
    Private balance As Single
End Class

Dim account As New BankAccount()
account.owner = "Alice"
Console.WriteLine(account.owner)
