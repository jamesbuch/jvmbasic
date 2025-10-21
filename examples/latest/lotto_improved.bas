Rem Modern Lotto - Generates unique lottery numbers

Print "=== Improved Lotto Generator ==="
Print ""

Dim games As Single = 5.0
Dim gameNum As Single = 1.0

While gameNum <= games
    Dim nums(6) = 0.0
    
    Rem Generate 6 numbers
    Dim generated As Single = 0.0
    While generated < 6.0
        Let nums(Int(generated)) = RndInt(1, 45)
        Let generated = generated + 1.0
    End While
    
    Rem Sort the numbers
    Call ARRAYSORT(nums)
    
    Print "Game "; gameNum; ":";
    Dim j As Single = 0.0
    While j < 6.0
        Print " "; nums(Int(j));
        Let j = j + 1.0
    End While
    Print ""
    
    Let gameNum = gameNum + 1.0
End While

Print ""
Print "=== Good Luck! ==="
