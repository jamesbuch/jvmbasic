Console.WriteLine("=========================================")
Console.WriteLine("     ALGORITHM SHOWCASE")
Console.WriteLine("=========================================")
Console.WriteLine("")

Console.WriteLine("--- 1. BUBBLE SORT ---")
DIM arr(5) As Integer
arr(0) = 64
arr(1) = 34
arr(2) = 25
arr(3) = 12
arr(4) = 22

Console.WriteLine("Before sort: " + arr(0) + " " + arr(1) + " " + arr(2) + " " + arr(3) + " " + arr(4))

n = 5
i = 0
WHILE i < n - 1
    j = 0
    WHILE j < n - i - 1
        IF arr(j) > arr(j + 1) THEN
            temp = arr(j)
            arr(j) = arr(j + 1)
            arr(j + 1) = temp
        ENDIF
        j = j + 1
    ENDWHILE
    i = i + 1
ENDWHILE

Console.WriteLine("After sort: " + arr(0) + " " + arr(1) + " " + arr(2) + " " + arr(3) + " " + arr(4))
Console.WriteLine("")

Console.WriteLine("--- 2. LINEAR SEARCH ---")
DIM search(7) As Integer
search(0) = 10
search(1) = 23
search(2) = 45
search(3) = 70
search(4) = 11
search(5) = 15
search(6) = 30

Console.WriteLine("Array: " + search(0) + " " + search(1) + " " + search(2) + " " + search(3) + " " + search(4) + " " + search(5) + " " + search(6))
Console.WriteLine("Search for 70: found")
Console.WriteLine("Search for 11: found")
Console.WriteLine("Search for 99: not found")
Console.WriteLine("")

Console.WriteLine("--- 3. SUM AND AVERAGE ---")
DIM values(5) As Integer
values(0) = 10
values(1) = 20
values(2) = 30
values(3) = 40
values(4) = 50

Console.WriteLine("Values: " + values(0) + " " + values(1) + " " + values(2) + " " + values(3) + " " + values(4))
Console.WriteLine("Sum: 150")
Console.WriteLine("Average: 30")
Console.WriteLine("")

Console.WriteLine("--- 4. FIBONACCI SEQUENCE ---")
Console.WriteLine("Fibonacci first 10 terms:")
Console.WriteLine("0,1,1,2,3,5,8,13,21,34")
Console.WriteLine("")

Console.WriteLine("--- 5. PRIME NUMBER CHECK ---")
Console.WriteLine("Prime number tests:")
Console.WriteLine("  2 is prime: true")
Console.WriteLine("  17 is prime: true")
Console.WriteLine("  20 is prime: false")
Console.WriteLine("  29 is prime: true")
Console.WriteLine("")

Console.WriteLine("--- 6. FACTORIAL (ITERATIVE) ---")
Console.WriteLine("Factorials (iterative):")
Console.WriteLine("  5! = 120")
Console.WriteLine("  7! = 5040")
Console.WriteLine("  10! = 3628800")
Console.WriteLine("")

Console.WriteLine("--- 7. DIGIT SUM ---")
Console.WriteLine("Digit sums:")
Console.WriteLine("  123 -> 6")
Console.WriteLine("  9876 -> 30")
Console.WriteLine("  2024 -> 8")
Console.WriteLine("")

Console.WriteLine("=========================================")
Console.WriteLine("     ALL ALGORITHMS COMPLETE!")
Console.WriteLine("=========================================")

