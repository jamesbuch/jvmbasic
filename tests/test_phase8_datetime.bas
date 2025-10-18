REM Test Phase 8: Date/Time Functions
PRINT "=== Phase 8 Date/Time Functions Test ==="

REM Test basic date/time functions
PRINT "Current date: "; DATE()
PRINT "Current time: "; TIME()
PRINT "Current datetime: "; DATETIME()

LET timestamp = NOW()
PRINT "Current timestamp: "; timestamp

REM Test date component extraction
LET yr = YEAR(timestamp)
LET mo = MONTH(timestamp)
LET dy = DAY(timestamp)
LET hr = HOUR(timestamp)
LET mn = MINUTE(timestamp)
LET sc = SECOND(timestamp)
LET dw = DAYOFWEEK(timestamp)

PRINT "Year: "; yr
PRINT "Month: "; mo
PRINT "Day: "; dy
PRINT "Hour: "; hr
PRINT "Minute: "; mn
PRINT "Second: "; sc
PRINT "Day of week: "; dw

REM Verify year is reasonable
IF yr < 2025 THEN
    PRINT "ERROR: Year seems wrong: "; yr
ENDIF
IF yr > 2100 THEN
    PRINT "ERROR: Year seems wrong: "; yr
ENDIF

REM Verify month is 1-12
IF mo < 1 THEN
    PRINT "ERROR: Month should be 1-12, got: "; mo
ENDIF
IF mo > 12 THEN
    PRINT "ERROR: Month should be 1-12, got: "; mo
ENDIF

REM Test date arithmetic
LET future = ADDDAYS(timestamp, 7)
PRINT "One week from now: "; FORMATDATE(future, "yyyy-MM-dd")

LET past = ADDDAYS(timestamp, -7)
PRINT "One week ago: "; FORMATDATE(past, "yyyy-MM-dd")

LET diff = DATEDIFF(past, future)
PRINT "Difference in days: "; diff
IF diff <> 14 THEN
    PRINT "ERROR: Date diff should be 14, got: "; diff
ENDIF

REM Test other date arithmetic
LET fut2 = ADDHOURS(timestamp, 24)
LET fut3 = ADDMINUTES(timestamp, 60)
LET fut4 = ADDSECONDS(timestamp, 3600)
LET fut5 = ADDMONTHS(timestamp, 1)
LET fut6 = ADDYEARS(timestamp, 1)

PRINT "Add 24 hours: "; FORMATDATE(fut2, "yyyy-MM-dd HH:mm")
PRINT "Add 1 month: "; FORMATDATE(fut5, "yyyy-MM-dd")
PRINT "Add 1 year: "; FORMATDATE(fut6, "yyyy-MM-dd")

REM Test format patterns
PRINT "Custom format 1: "; FORMATDATE(timestamp, "MM/dd/yyyy")
PRINT "Custom format 2: "; FORMATDATE(timestamp, "HH:mm:ss")
PRINT "Custom format 3: "; FORMATDATE(timestamp, "EEEE, MMMM d, yyyy")

PRINT "=== All Date/Time Tests Complete ==="

