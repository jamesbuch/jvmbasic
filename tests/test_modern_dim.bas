REM Test modern VB-style DIM syntax
REM Phase 9: Modern variable declarations

DIM x AS INTEGER = 10
DIM y AS SINGLE = 3.14
DIM name AS STRING = "John"
DIM flag AS BOOLEAN = TRUE

PRINT "x = "; x
PRINT "y = "; y
PRINT "name = "; name
PRINT "flag = "; flag

REM Test without initialization
DIM a AS INTEGER
DIM b AS SINGLE
DIM s AS STRING

LET a = 42
LET b = 2.718
LET s = "Hello"

PRINT "a = "; a
PRINT "b = "; b
PRINT "s = "; s

