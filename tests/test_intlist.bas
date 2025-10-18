REM Test IntList Collection (no underscores - lexer limitation)
PRINT "=== IntList Test ==="

REM Create a new list
LET list = INTLISTNEW()
PRINT "Created IntList with ID: "; list

REM Add some values
LET size1 = INTLISTADD(list, 10)
LET size2 = INTLISTADD(list, 20)
LET size3 = INTLISTADD(list, 30)
PRINT "Added 3 items, size: "; size3

REM Get list size
LET size = INTLISTSIZE(list)
PRINT "List size: "; size

REM Get values
LET val0 = INTLISTGET(list, 0)
LET val1 = INTLISTGET(list, 1)
LET val2 = INTLISTGET(list, 2)
PRINT "Values: "; val0; ", "; val1; ", "; val2

REM Set a value
LET oldVal = INTLISTSET(list, 1, 99)
PRINT "Changed index 1 from "; oldVal; " to 99"

LET newVal = INTLISTGET(list, 1)
PRINT "New value at index 1: "; newVal

REM Check contains
IF INTLISTCONTAINS(list, 30) THEN
    PRINT "List contains 30: YES"
ENDIF

IF INTLISTCONTAINS(list, 999) THEN
    PRINT "List contains 999: YES"
ELSE
    PRINT "List contains 999: NO"
ENDIF

REM Find index
LET idx = INTLISTINDEXOF(list, 30)
PRINT "Index of 30: "; idx

REM Remove an item
LET removed = INTLISTREMOVE(list, 0)
PRINT "Removed item at index 0: "; removed

LET newSize = INTLISTSIZE(list)
PRINT "New size after remove: "; newSize

REM Print remaining items
PRINT "Remaining items:"
LET finalSize = INTLISTSIZE(list)
IF finalSize > 0 THEN
    LET vala = INTLISTGET(list, 0)
    PRINT "  [0] = "; vala
ENDIF
IF finalSize > 1 THEN
    LET valb = INTLISTGET(list, 1)
    PRINT "  [1] = "; valb
ENDIF

PRINT "=== IntList Test Complete ==="

