' Prime Number Calculator
' Checks primality, counts primes, and finds nth prime

Function isPrime(num As Integer) As Integer
    Dim divisor As Integer
    If num <= 1 Then
        Return 0
    EndIf
    If num == 2 Then
        Return 1
    EndIf
    If num Mod 2 == 0 Then
        Return 0
    EndIf

    divisor = 3
    While divisor * divisor <= num
        If num Mod divisor == 0 Then
            Return 0
        EndIf
        divisor = divisor + 2
    EndWhile
    Return 1
EndFunction

Function countPrimes(limit As Integer) As Integer
    Dim count As Integer
    Dim num As Integer
    count = 0
    num = 2
    While num <= limit
        If isPrime(num) Then
            count = count + 1
        EndIf
        num = num + 1
    EndWhile
    Return count
EndFunction

Function findNthPrime(n As Integer) As Integer
    Dim count As Integer
    Dim num As Integer
    count = 0
    num = 2
    While count < n
        If isPrime(num) Then
            count = count + 1
        EndIf
        num = num + 1
    EndWhile
    Return num - 1
EndFunction

Console.WriteLine("================================================")
Console.WriteLine("  PRIME NUMBER CALCULATOR")
Console.WriteLine("================================================")
Console.WriteLine("")

Console.WriteLine("Checking individual numbers:")
Console.WriteLine("Is 17 prime? " + isPrime(17))
Console.WriteLine("Is 25 prime? " + isPrime(25))
Console.WriteLine("Is 29 prime? " + isPrime(29))
Console.WriteLine("Is 97 prime? " + isPrime(97))
Console.WriteLine("")

Console.WriteLine("Counting primes up to limits:")
Console.WriteLine("Primes up to 10: " + countPrimes(10))
Console.WriteLine("Primes up to 50: " + countPrimes(50))
Console.WriteLine("Primes up to 100: " + countPrimes(100))
Console.WriteLine("")

Console.WriteLine("Finding nth prime:")
Console.WriteLine("5th prime: " + findNthPrime(5))
Console.WriteLine("10th prime: " + findNthPrime(10))
Console.WriteLine("20th prime: " + findNthPrime(20))
Console.WriteLine("")

Console.WriteLine("Prime number list (first 20):")
Dim count As Integer
Dim num As Integer
count = 0
num = 2
While count < 20
    If isPrime(num) Then
        count = count + 1
        Console.WriteLine("Prime " + count + ": " + num)
    EndIf
    num = num + 1
EndWhile

Console.WriteLine("")
Console.WriteLine("================================================")
Console.WriteLine("  Prime number calculations complete!")
Console.WriteLine("================================================")
