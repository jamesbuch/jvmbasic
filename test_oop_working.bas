REM Test OOP features that are working

CLASS Point
    PUBLIC x AS FLOAT
    PUBLIC y AS FLOAT
END CLASS

CLASS Rectangle
    PUBLIC width AS FLOAT
    PUBLIC height AS FLOAT
END CLASS

DIM p AS NEW Point()
LET p.x = 5.0
LET p.y = 10.0

DIM r AS NEW Rectangle()
LET r.width = 20.0
LET r.height = 15.0

PRINT "Point: ("; p.x; ", "; p.y; ")"
PRINT "Rectangle: "; r.width; " x "; r.height
PRINT ""
PRINT "Phase 7 OOP Features Working:"
PRINT "✓ CLASS declarations"
PRINT "✓ PUBLIC fields"
PRINT "✓ NEW operator"
PRINT "✓ Field access (getfield)"
PRINT "✓ Field assignment (putfield)"

