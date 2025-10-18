REM Test Phase 8: Timing Functions
PRINT "=== Phase 8 Timing Functions Test ==="

REM Test TIMER
LET t = TIMER()
PRINT "Timer (seconds since midnight): "; t
IF t < 0 THEN
    PRINT "ERROR: TIMER should be 0-86400"
ENDIF
IF t > 86400 THEN
    PRINT "ERROR: TIMER should be 0-86400"
ENDIF

REM Test NANOSECONDS
LET start = NANOSECONDS()
PRINT "Nanosecond timer start: "; start

REM Do some work
LET sum = 0
FOR i = 1 TO 1000
    LET sum = sum + i
NEXT i

LET elapsed = NANOSECONDS() - start
PRINT "Loop took (nanoseconds): "; elapsed
PRINT "Loop sum: "; sum

IF elapsed < 0 THEN
    PRINT "ERROR: Elapsed time should be positive"
ENDIF

REM Test SLEEP (sleep for 100ms)
PRINT "Sleeping for 100 milliseconds..."
LET before = NANOSECONDS()
LET dummy = SLEEP(100)
LET after = NANOSECONDS()
LET slept = (after - before) / 1000000
PRINT "Actually slept (ms): "; slept

REM Sleep should be at least 80ms (allowing some variance)
IF slept < 80 THEN
    PRINT "WARNING: SLEEP may not have worked correctly"
ENDIF

PRINT "=== All Timing Tests Complete ==="

