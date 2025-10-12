FUNCTION isPrime(num)
    IF num <= 1.0 THEN
        RETURN false
    ENDIF
    IF num == 2.0 THEN
        RETURN true
    ENDIF
    IF num MOD 2.0 == 0.0 THEN
        RETURN false
    ENDIF
    
    LET divisor = 3.0
    WHILE divisor * divisor <= num
        IF num MOD divisor == 0.0 THEN
            RETURN false
        ENDIF
        LET divisor = divisor + 2.0
    ENDWHILE
    RETURN true
ENDFUNCTION

FUNCTION countPrimes(limit)
    LET count = 0.0
    LET num = 2.0
    WHILE num <= limit
        IF isPrime(num) THEN
            LET count = count + 1.0
        ENDIF
        LET num = num + 1.0
    ENDWHILE
    RETURN count
ENDFUNCTION

SUB printPrimes(limit)
    PRINT "Primes up to", limit, ":"
    LET num = 2.0
    LET count = 0.0
    WHILE num <= limit
        IF isPrime(num) THEN
            PRINT num;
            LET count = count + 1.0
            LET remainder = count MOD 10.0
            IF remainder == 0.0 THEN
                PRINT ""
            ELSE
                PRINT ", ";
            ENDIF
        ENDIF
        LET num = num + 1.0
    ENDWHILE
    PRINT ""
ENDSUB

PRINT "==========================================="
PRINT "  PRIME NUMBERS - Sieve & Generation"
PRINT "==========================================="
PRINT ""

PRINT "Testing individual numbers:"
PRINT "  2 is prime:", isPrime(2.0)
PRINT "  17 is prime:", isPrime(17.0)
PRINT "  100 is prime:", isPrime(100.0)
PRINT "  97 is prime:", isPrime(97.0)
PRINT ""

PRINT "Counting primes:"
PRINT "  Primes up to 50:", countPrimes(50.0)
PRINT "  Primes up to 100:", countPrimes(100.0)
PRINT ""

CALL printPrimes(50.0)

PRINT ""
PRINT "==========================================="
PRINT "  Prime Number Tests Complete!"
PRINT "==========================================="

