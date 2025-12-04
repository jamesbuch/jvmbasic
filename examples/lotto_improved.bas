' Advanced Lotto Number Generator
' Generates lottery numbers with statistics

Console.WriteLine("================================================")
Console.WriteLine("  ADVANCED LOTTO NUMBER GENERATOR")
Console.WriteLine("  (Numbers 1-45, Pick 6)")
Console.WriteLine("================================================")
Console.WriteLine("")

Dim games As Integer
Dim gameNum As Integer
Dim num1 As Integer
Dim num2 As Integer
Dim num3 As Integer
Dim num4 As Integer
Dim num5 As Integer
Dim num6 As Integer
Dim sum As Integer

games = 5
Console.WriteLine("Generating " + games + " unique lotto games...")
Console.WriteLine("")

For gameNum = 1 To games
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
Next gameNum

Console.WriteLine("================================================")
Console.WriteLine("  All games generated successfully!")
Console.WriteLine("================================================")
