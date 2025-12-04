' Simple Lotto Number Generator
' Generates lottery numbers for multiple games

Dim games As Integer
Dim i As Integer
Dim j As Integer

Console.WriteLine("Welcome to the Lotto Simulator!")
Console.WriteLine("Generating 3 games automatically...")
Console.WriteLine("")

games = 3

For i = 1 To games
    Console.Write("Game " + i + ": ")
    For j = 1 To 6
        Console.Write(RNDINT(1, 45) + " ")
    Next j
    Console.WriteLine("")
Next i

Console.WriteLine("")
Console.WriteLine("Lotto generation complete!")
