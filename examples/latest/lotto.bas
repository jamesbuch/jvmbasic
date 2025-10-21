' Modern VB-Style Lotto Number Generator
' Demonstrates modern syntax with random numbers

' Main program
Dim dummy As Integer
Let dummy = Console.WriteLine("=== Lotto Number Generator ===")
Let dummy = Console.WriteLine("")

Dim games As Single = 5.0
Let dummy = Console.WriteLine("Generating " + FormatF("%.0f", games) + " lotto games:")
Let dummy = Console.WriteLine("")

Dim i As Single = 1.0
While i <= games
    Let dummy = Console.Write("Game " + FormatF("%.0f", i) + ": ")
    Dim j As Single = 1.0
    While j <= 6.0
        Let dummy = Console.Write(FormatF("%.0f", RNDINT(1, 45)) + " ")
        j = j + 1.0
    EndWhile
    Let dummy = Console.WriteLine("")
    i = i + 1.0
EndWhile

Let dummy = Console.WriteLine("")
Let dummy = Console.WriteLine("=== Good Luck! ===")

