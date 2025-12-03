' Test Phase 8: Timing Functions
Console.WriteLine("=== Phase 8 Timing Functions Test ===")

' Test TIMER
t = TIMER()
Console.WriteLine("Timer (seconds since midnight): " + t)
IF t < 0 THEN
    Console.WriteLine("ERROR: TIMER should be 0-86400")
ENDIF
IF t > 86400 THEN
    Console.WriteLine("ERROR: TIMER should be 0-86400")
ENDIF

' Test NANOSECONDS
start = NANOSECONDS()
Console.WriteLine("Nanosecond timer start: " + start)

' Do some work
sum = 0
FOR i = 1 TO 1000
    sum = sum + i
NEXT i

elapsed = NANOSECONDS() - start
Console.WriteLine("Loop took (nanoseconds): " + elapsed)
Console.WriteLine("Loop sum: " + sum)

IF elapsed < 0 THEN
    Console.WriteLine("ERROR: Elapsed time should be positive")
ENDIF

' Test SLEEP (sleep for 100ms)
Console.WriteLine("Sleeping for 100 milliseconds...")
before = NANOSECONDS()
dummy = SLEEP(100)
after = NANOSECONDS()
slept = (after - before) / 1000000
Console.WriteLine("Actually slept (ms): " + slept)

' Sleep should be at least 80ms (allowing some variance)
IF slept < 80 THEN
    Console.WriteLine("WARNING: SLEEP may not have worked correctly")
ENDIF

Console.WriteLine("=== All Timing Tests Complete ===")

