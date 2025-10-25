' Modern VB-Style Prime Number Generator

Function IsPrime(num As Single) As Boolean
    If num <= 1.0 Then
        Return false
    End If
    If num == 2.0 Then
        Return true
    End If
    If num Mod 2.0 == 0.0 Then
        Return false
    End If
    
    Dim divisor As Single = 3.0
    While divisor * divisor <= num
        If num Mod divisor == 0.0 Then
            Return false
        End If
        divisor = divisor + 2.0
    End While
    Return true
End Function

Function CountPrimes(limit As Single) As Single
    Dim count As Single = 0.0
    Dim num As Single = 2.0
    While num <= limit
        If IsPrime(num) Then
            count = count + 1.0
        End If
        num = num + 1.0
    End While
    Return count
End Function

' Main program
Console.WriteLine("===========================================")
Console.WriteLine("  PRIME NUMBERS")
Console.WriteLine("===========================================")
Console.WriteLine("")

Console.WriteLine("Testing individual numbers:")
Dim isPrime2 As Boolean = IsPrime(2.0)
Dim isPrime17 As Boolean = IsPrime(17.0)
Dim isPrime97 As Boolean = IsPrime(97.0)
Console.WriteLine($"  2 is prime: {isPrime2}")
Console.WriteLine($"  17 is prime: {isPrime17}")
Console.WriteLine($"  97 is prime: {isPrime97}")
Console.WriteLine("")

Console.WriteLine("Counting primes:")
Dim count50 As Single = CountPrimes(50.0)
Dim count100 As Single = CountPrimes(100.0)
Console.WriteLine($"  Primes up to 50: {count50}")
Console.WriteLine($"  Primes up to 100: {count100}")
Console.WriteLine("")

Console.WriteLine("Primes up to 50:")
Dim num As Single = 2.0
While num <= 50.0
    If IsPrime(num) Then
        Console.WriteLine($"  {num}")
    End If
    num = num + 1.0
End While

Console.WriteLine("")
Console.WriteLine("===========================================")
