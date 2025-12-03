Class BankAccount
    Public owner As String
    Private balance As Single
End Class

Dim account As New BankAccount("Alice", 1000.0)
Console.WriteLine(account.owner)
