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
Print "==========================================="
Print "  PRIME NUMBERS"
Print "==========================================="
Print ""

Print "Testing individual numbers:"
Print "  2 is prime: "; IsPrime(2.0)
Print "  17 is prime: "; IsPrime(17.0)
Print "  97 is prime: "; IsPrime(97.0)
Print ""

Print "Counting primes:"
Print "  Primes up to 50: "; CountPrimes(50.0)
Print "  Primes up to 100: "; CountPrimes(100.0)
Print ""

Print "Primes up to 50:"
Dim num As Single = 2.0
While num <= 50.0
    If IsPrime(num) Then
        Print "  "; num
    End If
    num = num + 1.0
End While

Print ""
Print "==========================================="
