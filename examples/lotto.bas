games = 0

Console.WriteLine("Welcome to the Lotto Simulator!")
Console.WriteLine("Enter the number of games you want to generate:")
INPUT games

FOR i = 1 TO games
    Console.WriteLine("Game " + i + ": ")
    FOR j = 1 TO 6
        Console.Write(RNDINT(1, 45) + " ")
    NEXT j
    Console.WriteLine("")
NEXT i
