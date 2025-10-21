REM Test modern VB-style function syntax
REM Phase 9: Typed parameters and return types

FUNCTION Add(a AS INTEGER, b AS INTEGER) AS INTEGER
    RETURN a + b
ENDFUNCTION

FUNCTION Multiply(x AS SINGLE, y AS SINGLE) AS SINGLE
    RETURN x * y
ENDFUNCTION

FUNCTION Greet(name AS STRING) AS STRING
    RETURN "Hello, " + name + "!"
ENDFUNCTION

SUB PrintSum(a AS INTEGER, b AS INTEGER)
    DIM result AS INTEGER = Add(a, b)
    PRINT "Sum: "; result
ENDSUB

REM Test the functions
DIM x AS INTEGER = 10
DIM y AS INTEGER = 20
DIM sum AS INTEGER = Add(x, y)
PRINT "Add(10, 20) = "; sum

DIM f1 AS SINGLE = 3.5
DIM f2 AS SINGLE = 2.0
DIM product AS SINGLE = Multiply(f1, f2)
PRINT "Multiply(3.5, 2.0) = "; product

DIM greeting AS STRING = Greet("World")
PRINT greeting

CALL PrintSum(15, 25)

