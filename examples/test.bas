' Define a class with constructor and methods
Class BankAccount
    Private balance As Float
    Public owner As String
    
    Public Sub New(name As String, initial As Float)
        Me.owner = name
        Me.balance = initial
    End Sub
    
    Public Sub Deposit(amount As Float)
        If amount > 0.0 Then
            Me.balance = Me.balance + amount
            Console.WriteLine("Deposited: " + Str(amount))
        End If
    End Sub
    
    Public Function GetBalance() As Float
        Return Me.balance
    End Function
End Class

' Create and use objects
Dim account As New BankAccount("Alice", 1000.0)
account.Deposit(500.0)
Console.WriteLine(account.owner + " has $" + Str(account.GetBalance()))

Type Person
    name As String
    age As Float
End Type

Dim person As Person
person.name = "Alice"
person.age = 30.0
Console.WriteLine("Person: " + person.name + ", age " + Str(person.age))

Function Factorial(n As Float) As Float
    If n <= 1.0 Then
        Return 1.0
    Else
        Return n * Factorial(n - 1.0)
    End If
End Function

Console.WriteLine("5! = " + Str(Factorial(5.0)))  ' Output: 120.0

Dim numbers(10) As Float = 0.0
For i = 0 To 9
    numbers(i) = RndInt(1, 100)
Next i

ArraySort(numbers)
Console.WriteLine("Min: " + Str(ArrayMin(numbers)))
Console.WriteLine("Max: " + Str(ArrayMax(numbers)))
Console.WriteLine("Average: " + Str(ArrayAvg(numbers)))
