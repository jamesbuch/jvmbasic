FUNCTION isPrime(num As Integer) As Integer
    IF num <= 1 THEN
        RETURN 0
    ENDIF
    IF num == 2 THEN
        RETURN 1
    ENDIF
    IF num MOD 2 == 0 THEN
        RETURN 0
    ENDIF
    
    divisor = 3
    WHILE divisor * divisor <= num
        IF num MOD divisor == 0 THEN
            RETURN 0
        ENDIF
        divisor = divisor + 2
    ENDWHILE
    RETURN 1
ENDFUNCTION

FUNCTION countPrimes(limit As Integer) As Integer
    count = 0
    num = 2
    WHILE num <= limit
        IF isPrime(num) THEN
            count = count + 1
        ENDIF
        num = num + 1
    ENDWHILE
    RETURN count
ENDFUNCTION

FUNCTION findNthPrime(n As Integer) As Integer
    count = 0
    num = 2
    WHILE count < n
        IF isPrime(num) THEN
            count = count + 1
        ENDIF
        num = num + 1
    ENDWHILE
    RETURN num - 1
ENDFUNCTION

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
count = 0
num = 2
WHILE count < 20
    IF isPrime(num) THEN
        count = count + 1
        Console.WriteLine("Prime " + count + ": " + num)
    ENDIF
    num = num + 1
ENDWHILE

Console.WriteLine("")
Console.WriteLine("================================================")
Console.WriteLine("  Prime number calculations complete!")
Console.WriteLine("================================================")