Rem Modern Lotto - Generates unique lottery numbers

Console.WriteLine("=== Improved Lotto Generator ===")
Console.WriteLine("")

Dim games As Single = 5.0
Dim gameNum As Single = 1.0

While gameNum <= games
    Dim nums(6) As Single
    
    Rem Generate 6 numbers
    Dim generated As Single = 0.0
    While generated < 6.0
        Let nums(Int(generated)) = 10.0 + generated
        Let generated = generated + 1.0
    End While
    
    Console.WriteLine($"Game {gameNum}:")
    Dim j As Single = 0.0
    While j < 6.0
        Dim num As Single = nums(Int(j))
        Console.WriteLine($"  {num}")
        Let j = j + 1.0
    End While
    Console.WriteLine("")
    
    Let gameNum = gameNum + 1.0
End While

Console.WriteLine("")
Console.WriteLine("=== Good Luck! ===")
