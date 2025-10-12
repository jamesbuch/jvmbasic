FUNCTION add(a, b)
    RETURN a + b
ENDFUNCTION

SUB greet(name)
    PRINT "Hello,", name
ENDSUB

PRINT "Testing type inference"

LET x = 5
LET y = 3
LET result = add(x, y)
PRINT "add(5, 3) =", result

CALL greet("World")
CALL greet("Alice")

LET f = add(2.5, 3.5)
PRINT "add(2.5, 3.5) =", f

