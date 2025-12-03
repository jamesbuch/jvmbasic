Dim account As New BankAccount("Alice", 1000.0)
account.Deposit(500.0)
Console.WriteLine(account.owner + " has $" + Str(account.GetBalance()))
