' Mathematical Algorithms Demonstration
' Shows GCD, LCM, Factorial, Fibonacci, Prime checking

Function gcd(a As Integer, b As Integer) As Integer
    Dim temp As Integer
    While b > 0
        temp = b
        b = a Mod b
        a = temp
    EndWhile
    Return a
EndFunction

Function lcm(a As Integer, b As Integer) As Integer
    Return (a * b) / gcd(a, b)
EndFunction

Function factorial(n As Integer) As Integer
    If n <= 1 Then
        Return 1
    Else
        Return n * factorial(n - 1)
    EndIf
EndFunction

Function fibonacci(n As Integer) As Integer
    If n <= 1 Then
        Return n
    Else
        Return fibonacci(n - 1) + fibonacci(n - 2)
    EndIf
EndFunction

Function fibonacciIterative(n As Integer) As Integer
    Dim a As Integer
    Dim b As Integer
    Dim i As Integer
    Dim temp As Integer
    If n <= 1 Then
        Return n
    EndIf

    a = 0
    b = 1
    i = 2
    While i <= n
        temp = a + b
        a = b
        b = temp
        i = i + 1
    EndWhile
    Return b
EndFunction

Function isPrime(n As Integer) As Integer
    Dim i As Integer
    If n < 2 Then
        Return 0
    EndIf

    i = 2
    While i * i <= n
        If n Mod i == 0 Then
            Return 0
        EndIf
        i = i + 1
    EndWhile
    Return 1
EndFunction

Console.WriteLine("================================================")
Console.WriteLine("  MATHEMATICAL ALGORITHMS DEMONSTRATION")
Console.WriteLine("================================================")
Console.WriteLine("")

Console.WriteLine("1. Greatest Common Divisor (GCD):")
Console.WriteLine("   GCD(48, 18) = " + gcd(48, 18))
Console.WriteLine("   GCD(1071, 462) = " + gcd(1071, 462))
Console.WriteLine("")

Console.WriteLine("2. Least Common Multiple (LCM):")
Console.WriteLine("   LCM(12, 18) = " + lcm(12, 18))
Console.WriteLine("   LCM(15, 25) = " + lcm(15, 25))
Console.WriteLine("")

Console.WriteLine("3. Factorial:")
Console.WriteLine("   5! = " + factorial(5))
Console.WriteLine("   7! = " + factorial(7))
Console.WriteLine("")

Console.WriteLine("4. Fibonacci Sequence:")
Console.WriteLine("   fib(10) = " + fibonacci(10))
Console.WriteLine("   fib(20) = " + fibonacciIterative(20))
Console.WriteLine("")

Console.WriteLine("5. Prime Number Check:")
Console.WriteLine("   Is 17 prime? " + isPrime(17))
Console.WriteLine("   Is 25 prime? " + isPrime(25))
Console.WriteLine("   Is 29 prime? " + isPrime(29))
Console.WriteLine("")

Console.WriteLine("6. Built-in Math Functions:")
Console.WriteLine("   SQR(144) = " + SQR(144))
Console.WriteLine("   POW(2, 10) = " + POW(2, 10))
Console.WriteLine("   ABS(-15) = " + ABS(-15))
Console.WriteLine("   MIN(5, 3) = " + MIN(5, 3))
Console.WriteLine("   MAX(5, 3) = " + MAX(5, 3))
Console.WriteLine("")

Console.WriteLine("================================================")
Console.WriteLine("  All mathematical algorithms working!")
Console.WriteLine("================================================")
