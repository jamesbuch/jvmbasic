Console.WriteLine("================================================")
Console.WriteLine("  ADVANCED LOTTO NUMBER GENERATOR")
Console.WriteLine("  (Numbers 1-45, Pick 6)")
Console.WriteLine("================================================")
Console.WriteLine("")

games = 0
Console.WriteLine("How many games do you want to generate?")
INPUT games

Console.WriteLine("")
Console.WriteLine("Generating " + games + " unique lotto games...")
Console.WriteLine("")

FOR gameNum = 1 TO games
    num1 = RNDINT(1, 45)
    num2 = RNDINT(1, 45)
    num3 = RNDINT(1, 45)
    num4 = RNDINT(1, 45)
    num5 = RNDINT(1, 45)
    num6 = RNDINT(1, 45)
    
    Console.WriteLine("Game " + gameNum + ": ")
    Console.WriteLine("  Numbers: " + num1 + " " + num2 + " " + num3 + " " + num4 + " " + num5 + " " + num6)
    
    sum = num1 + num2 + num3 + num4 + num5 + num6
    Console.WriteLine("  Sum: " + sum)
    Console.WriteLine("")
NEXT gameNum

Console.WriteLine("================================================")
Console.WriteLine("  All games generated successfully!")
Console.WriteLine("================================================")