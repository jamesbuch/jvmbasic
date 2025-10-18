FUNCTION linearSearch(arr, size, target)
    LET idx = 0.0
    WHILE idx < size
        IF arr(idx) == target THEN
            RETURN idx
        ENDIF
        LET idx = idx + 1.0
    ENDWHILE
    RETURN -1.0
ENDFUNCTION

FUNCTION sumArrayCustom(arr, size)
    LET total = 0.0
    LET i = 0.0
    WHILE i < size
        LET total = total + arr(i)
        LET i = i + 1.0
    ENDWHILE
    RETURN total
ENDFUNCTION

FUNCTION average(arr, size)
    RETURN sumArrayCustom(arr, size) / size
ENDFUNCTION

SUB printFibonacci(n)
    LET a = 0.0
    LET b = 1.0
    LET count = 0.0
    PRINT "Fibonacci first", n, "terms:"
    WHILE count < n
        PRINT a;
        PRINT ",";
        LET temp = a + b
        LET a = b
        LET b = temp
        LET count = count + 1.0
    ENDWHILE
    PRINT ""
ENDSUB

FUNCTION isPrime(num)
    IF num <= 1.0 THEN
        RETURN false
    ENDIF
    IF num == 2.0 THEN
        RETURN true
    ENDIF
    LET divisor = 2.0
    WHILE divisor * divisor <= num
        IF num MOD divisor == 0.0 THEN
            RETURN false
        ENDIF
        LET divisor = divisor + 1.0
    ENDWHILE
    RETURN true
ENDFUNCTION

FUNCTION factorialIter(n)
    LET result = 1.0
    LET i = 1.0
    WHILE i <= n
        LET result = result * i
        LET i = i + 1.0
    ENDWHILE
    RETURN result
ENDFUNCTION

FUNCTION digitSum(num)
    LET sum = 0.0
    LET n = num
    WHILE n >= 1.0
        LET digit = n MOD 10.0
        LET sum = sum + digit
        LET n = FLOOR(n / 10.0)
    ENDWHILE
    RETURN sum
ENDFUNCTION

PRINT "========================================="
PRINT "     ALGORITHM SHOWCASE"
PRINT "========================================="
PRINT ""

PRINT "--- 1. BUBBLE SORT ---"
DIM arr(5) = 0.0
LET arr(0) = 64.0
LET arr(1) = 34.0
LET arr(2) = 25.0
LET arr(3) = 12.0
LET arr(4) = 22.0

PRINT "Before sort:", arr(0), arr(1), arr(2), arr(3), arr(4)

LET n = 5.0
LET i = 0.0
WHILE i < n - 1.0
    LET j = 0.0
    WHILE j < n - i - 1.0
        IF arr(j) > arr(j + 1.0) THEN
            LET temp = arr(j)
            LET arr(j) = arr(j + 1.0)
            LET arr(j + 1.0) = temp
        ENDIF
        LET j = j + 1.0
    ENDWHILE
    LET i = i + 1.0
ENDWHILE

PRINT "After sort:", arr(0), arr(1), arr(2), arr(3), arr(4)
PRINT ""

PRINT "--- 2. LINEAR SEARCH ---"
DIM search(7) = 0.0
LET search(0) = 10.0
LET search(1) = 23.0
LET search(2) = 45.0
LET search(3) = 70.0
LET search(4) = 11.0
LET search(5) = 15.0
LET search(6) = 30.0

PRINT "Array:", search(0), search(1), search(2), search(3), search(4), search(5), search(6)
PRINT "Search for 70:", linearSearch(search, 7.0, 70.0)
PRINT "Search for 11:", linearSearch(search, 7.0, 11.0)
PRINT "Search for 99:", linearSearch(search, 7.0, 99.0)
PRINT ""

PRINT "--- 3. SUM AND AVERAGE ---"
DIM values(5) = 0.0
LET values(0) = 10.0
LET values(1) = 20.0
LET values(2) = 30.0
LET values(3) = 40.0
LET values(4) = 50.0

PRINT "Values:", values(0), values(1), values(2), values(3), values(4)
PRINT "Sum:", sumArrayCustom(values, 5.0)
PRINT "Average:", average(values, 5.0)
PRINT ""

PRINT "--- 4. FIBONACCI SEQUENCE ---"
CALL printFibonacci(10.0)
PRINT ""

PRINT "--- 5. PRIME NUMBER CHECK ---"
PRINT "Prime number tests:"
PRINT "  2 is prime:", isPrime(2.0)
PRINT "  17 is prime:", isPrime(17.0)
PRINT "  20 is prime:", isPrime(20.0)
PRINT "  29 is prime:", isPrime(29.0)
PRINT ""

PRINT "--- 6. FACTORIAL (ITERATIVE) ---"
PRINT "Factorials (iterative):"
PRINT "  5! =", factorialIter(5.0)
PRINT "  7! =", factorialIter(7.0)
PRINT "  10! =", factorialIter(10.0)
PRINT ""

PRINT "--- 7. DIGIT SUM ---"
PRINT "Digit sums:"
PRINT "  123 ->", digitSum(123.0)
PRINT "  9876 ->", digitSum(9876.0)
PRINT "  2024 ->", digitSum(2024.0)
PRINT ""

PRINT "========================================="
PRINT "     ALL ALGORITHMS COMPLETE!"
PRINT "========================================="

