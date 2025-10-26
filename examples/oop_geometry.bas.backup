REM Geometry Example - Multiple Classes Working Together
REM Shows: Multiple classes, constructors, field access, calculations

' Define Point class
CLASS Point
    PUBLIC x AS FLOAT
    PUBLIC y AS FLOAT
    
    PUBLIC SUB New(px AS FLOAT, py AS FLOAT)
        x = px
        y = py
    END SUB
END CLASS

' Define Rectangle class
CLASS Rectangle
    PUBLIC width AS FLOAT
    PUBLIC height AS FLOAT
    PUBLIC color AS STRING
    
    PUBLIC SUB New(w AS FLOAT, h AS FLOAT, c AS STRING)
        width = w
        height = h
        color = c
    END SUB
END CLASS

' Define Circle class
CLASS Circle
    PUBLIC radius AS FLOAT
    PUBLIC centerX AS FLOAT
    PUBLIC centerY AS FLOAT
    
    PUBLIC SUB New(r AS FLOAT, cx AS FLOAT, cy AS FLOAT)
        radius = r
        centerX = cx
        centerY = cy
    END SUB
END CLASS

PRINT "=== Geometry System - OOP Demo ==="
PRINT ""

' Create geometric shapes
DIM p1 AS NEW Point(10.0, 20.0)
DIM p2 AS NEW Point(50.0, 75.0)

DIM rect AS NEW Rectangle(100.0, 50.0, "Blue")
DIM square AS NEW Rectangle(80.0, 80.0, "Red")

DIM circle AS NEW Circle(25.0, 100.0, 100.0)

' Display points
PRINT "Point 1: ("; p1.x; ", "; p1.y; ")"
PRINT "Point 2: ("; p2.x; ", "; p2.y; ")"
PRINT ""

' Display rectangles
PRINT rect.color; " Rectangle: "; rect.width; " x "; rect.height
LET rectArea = rect.width * rect.height
PRINT "  Area: "; rectArea
PRINT ""

PRINT square.color; " Square: "; square.width; " x "; square.height
LET squareArea = square.width * square.height
PRINT "  Area: "; squareArea
PRINT ""

' Display circle
PRINT "Circle at ("; circle.centerX; ", "; circle.centerY; ")"
PRINT "  Radius: "; circle.radius
LET circleArea = PI * circle.radius * circle.radius
PRINT "  Area: "; circleArea
PRINT ""

' Calculate distance between points
LET dx = p2.x - p1.x
LET dy = p2.y - p1.y
LET distance = SQR(dx * dx + dy * dy)
PRINT "Distance between points: "; distance
PRINT ""

PRINT "✓ Multiple classes working together!"

