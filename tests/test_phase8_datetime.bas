' Test Phase 8: Date/Time Functions
Console.WriteLine("=== Phase 8 Date/Time Functions Test ===")

' Test basic date/time functions
Console.WriteLine("Current date: " + DATE())
Console.WriteLine("Current time: " + TIME())
Console.WriteLine("Current datetime: " + DATETIME())

Dim timestamp As Single
timestamp = NOW()
Console.WriteLine("Current timestamp: " + timestamp)

' Test date component extraction
Dim yr As Integer
Dim mo As Integer
Dim dy As Integer
Dim hr As Integer
Dim mn As Integer
Dim sc As Integer
Dim dw As Integer
yr = YEAR(timestamp)
mo = MONTH(timestamp)
dy = DAY(timestamp)
hr = HOUR(timestamp)
mn = MINUTE(timestamp)
sc = SECOND(timestamp)
dw = DAYOFWEEK(timestamp)

Console.WriteLine("Year: " + yr)
Console.WriteLine("Month: " + mo)
Console.WriteLine("Day: " + dy)
Console.WriteLine("Hour: " + hr)
Console.WriteLine("Minute: " + mn)
Console.WriteLine("Second: " + sc)
Console.WriteLine("Day of week: " + dw)

' Verify year is reasonable
IF yr < 2025 THEN
    Console.WriteLine("ERROR: Year seems wrong: " + yr)
ENDIF
IF yr > 2100 THEN
    Console.WriteLine("ERROR: Year seems wrong: " + yr)
ENDIF

' Verify month is 1-12
IF mo < 1 THEN
Console.WriteLine("ERROR: Month should be 1-12, got: " + mo)
ENDIF
IF mo > 12 THEN
Console.WriteLine("ERROR: Month should be 1-12, got: " + mo)
ENDIF

' Test date arithmetic
Dim future As Single
future = ADDDAYS(timestamp, 7)
Console.WriteLine("One week from now: " + FORMATDATE(future, "yyyy-MM-dd" ))

Dim past As Single
past = ADDDAYS(timestamp, -7)
Console.WriteLine("One week ago: " + FORMATDATE(past, "yyyy-MM-dd" ))

Dim diff As Integer
diff = DATEDIFF(past, future)
Console.WriteLine("Difference in days: " + diff)
IF diff <> 14 THEN
Console.WriteLine("ERROR: Date diff should be 14, got: " + diff)
ENDIF

' Test other date arithmetic
Dim fut2 As Single
Dim fut3 As Single
Dim fut4 As Single
Dim fut5 As Single
Dim fut6 As Single
fut2 = ADDHOURS(timestamp, 24)
fut3 = ADDMINUTES(timestamp, 60)
fut4 = ADDSECONDS(timestamp, 3600)
fut5 = ADDMONTHS(timestamp, 1)
fut6 = ADDYEARS(timestamp, 1)

Console.WriteLine("Add 24 hours: " + FORMATDATE(fut2, "yyyy-MM-dd HH:mm" ))
Console.WriteLine("Add 1 month: " + FORMATDATE(fut5, "yyyy-MM-dd" ))
Console.WriteLine("Add 1 year: " + FORMATDATE(fut6, "yyyy-MM-dd" ))

' Test format patterns
Console.WriteLine("Custom format 1: " + FORMATDATE(timestamp, "MM/dd/yyyy" ))
Console.WriteLine("Custom format 2: " + FORMATDATE(timestamp, "HH:mm:ss" ))
Console.WriteLine("Custom format 3: " + FORMATDATE(timestamp, "EEEE, MMMM d, yyyy" ))

Console.WriteLine("=== All Date/Time Tests Complete ===")

