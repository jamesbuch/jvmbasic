DIM nums(5) = 0
LET nums(0) = 50
LET nums(1) = 20
LET nums(2) = 80
LET nums(3) = 10
LET nums(4) = 60

PRINT "Array:", nums(0), nums(1), nums(2), nums(3), nums(4)

LET smallest = MINARRAY(nums)
LET largest = MAXARRAY(nums)
LET total = SUMARRAY(nums)
LET size = UBOUND(nums)

PRINT "Min:", smallest
PRINT "Max:", largest
PRINT "Sum:", total
PRINT "Upper bound:", size

LET avg = total / (size + 1)
PRINT "Average:", avg

