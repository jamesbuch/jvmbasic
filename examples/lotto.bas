' Simple Lotto Number Generator
' Generates lottery numbers for multiple games

Dim games As Integer
Dim i As Integer
Dim j As Integer

Console.WriteLine("Welcome to the Lotto Simulator!")
Console.WriteLine("Generating 3 games automatically...")
Console.WriteLine("")

games = 3

FOR i = 1 TO games
    Console.Write("Game " + i + ": ")
    FOR j = 1 TO 6
        Console.Write(RNDINT(1, 45) + " ")
    NEXT j
    Console.WriteLine("")
NEXT i

Console.WriteLine("")
Console.WriteLine("Lotto generation complete!")
