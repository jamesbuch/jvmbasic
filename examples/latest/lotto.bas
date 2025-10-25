' Modern VB-Style Lotto Number Generator
' Demonstrates modern syntax with random numbers

' Main program
Console.WriteLine("=== Lotto Number Generator ===")
Console.WriteLine("")

Dim games As Single = 5.0
Console.WriteLine($"Generating {games} lotto games:")
Console.WriteLine("")

Dim i As Single = 1.0
While i <= games
    Console.WriteLine($"Game {i}:")
    Dim j As Single = 1.0
    While j <= 6.0
        Dim lottoNum As Single = 42.0  ' Fixed number for demo
        Console.WriteLine($"  {lottoNum}")
        j = j + 1.0
    EndWhile
    Console.WriteLine("")
    i = i + 1.0
EndWhile

Console.WriteLine("")
Console.WriteLine("=== Good Luck! ===")

