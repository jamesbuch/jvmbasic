Console.WriteLine("Test 1: Basic FOR loop")
FOR i = 1 TO 5
    Console.WriteLine(i)
NEXT i

Console.WriteLine("Test 2: FOR with STEP")
FOR i = 0 TO 10 STEP 2
    Console.WriteLine(i)
NEXT i

Console.WriteLine("Test 3: Countdown")
FOR i = 5 TO 1 STEP -1
    Console.WriteLine(i)
NEXT i

Console.WriteLine("Test 4: FOR with arrays")
DIM nums(5) As Integer
FOR i = 0 TO 4
    nums(i) = i * 10
NEXT i

Console.WriteLine("Array contents:")
FOR i = 0 TO 4
    Console.WriteLine(nums(i))
NEXT i

