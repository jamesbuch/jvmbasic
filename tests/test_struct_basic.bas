REM Test basic struct operations
TYPE Person
    name AS STRING
    age AS FLOAT
ENDTYPE

DIM p AS Person
LET p.name = "Alice"
LET p.age = 30.0

PRINT "Person: "; p.name; ", age "; p.age

