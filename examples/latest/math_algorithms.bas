' Modern VB-Style Mathematical Algorithms

Function Gcd(a As Single, b As Single) As Single
    While b > 0.0
        Dim temp As Single = b
        b = a Mod b
        a = temp
    End While
    Return a
End Function

Function Lcm(a As Single, b As Single) As Single
    Return (a * b) / Gcd(a, b)
End Function

Function Factorial(n As Single) AS SINGLE
    If n <= 1.0 Then
        Return 1.0
    Else
        Return n * Factorial(n - 1.0)
    End If
End Function

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

Function Power(base As Single, exponent As Single) As Single
    Return Math.Pow(base, exponent)
End Function

' Main program
Print "========================================================"
Print "  MATHEMATICAL ALGORITHMS"
Print "========================================================"
Print ""

Print "1. GCD & LCM:"
Print "   GCD(48, 18) = "; Gcd(48.0, 18.0)
Print "   LCM(12, 18) = "; Lcm(12.0, 18.0)
Print ""

Print "2. FACTORIAL:"
Print "   5! = "; Factorial(5.0)
Print "   10! = "; Factorial(10.0)
Print ""

Print "3. PRIME TESTING:"
Print "   17 is prime: "; IsPrime(17.0)
Print "   100 is prime: "; IsPrime(100.0)
Print ""

Print "4. PRIMES UP TO 50:"
Dim num As Single = 2.0
While num <= 50.0
    If IsPrime(num) Then
        Print "  "; num
    End If
    num = num + 1.0
End While
Print ""

Print "5. POWERS (using Math.Pow):"
Print "   2^8 = "; Power(2.0, 8.0)
Print "   3^4 = "; Power(3.0, 4.0)
Print ""

Print "========================================================"
