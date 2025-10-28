REM Geometry Example - Multiple Classes Working Together
REM Shows: Multiple classes, constructors, field access, calculations

REM Define Point class
CLASS Point
    PUBLIC x As Float
    PUBLIC y As Float
    
SUB New(px As Float, py As Float)
        x = px
        y = py
    END SUB
END CLASS

REM Define Rectangle class
CLASS Rectangle
    PUBLIC width As Float
    PUBLIC height As Float
    PUBLIC color As String
    
SUB New(w As Float, h As Float, c As String)
        width = w
        height = h
        color = c
    END SUB
END CLASS

REM Define Circle class
CLASS Circle
    PUBLIC radius As Float
    PUBLIC color As String
    
SUB New(r As Float, c As String)
        radius = r
        color = c
    END SUB
END CLASS

Console.WriteLine("=== Geometry Calculator ===")
Console.WriteLine("")

REM Create geometric objects
DIM point1 AS NEW Point(5.0, 10.0)
DIM point2 AS NEW Point(15.0, 20.0)

DIM rect1 AS NEW Rectangle(10.0, 5.0, "red")
DIM rect2 AS NEW Rectangle(20.0, 15.0, "blue")

DIM circle1 AS NEW Circle(7.5, "green")
DIM circle2 AS NEW Circle(12.0, "yellow")

Console.WriteLine("Points:")
Console.WriteLine("  Point 1: x=" + point1.x)
Console.WriteLine("  Point 1: y=" + point1.y)
Console.WriteLine("  Point 2: x=" + point2.x)
Console.WriteLine("  Point 2: y=" + point2.y)
Console.WriteLine("")

Console.WriteLine("Rectangles:")
Console.WriteLine("  Rectangle 1: width=" + rect1.width)
Console.WriteLine("  Rectangle 1: height=" + rect1.height)
Console.WriteLine("  Rectangle 1: color=" + rect1.color)
Console.WriteLine("  Rectangle 2: width=" + rect2.width)
Console.WriteLine("  Rectangle 2: height=" + rect2.height)
Console.WriteLine("  Rectangle 2: color=" + rect2.color)
Console.WriteLine("")

Console.WriteLine("Circles:")
Console.WriteLine("  Circle 1: radius=" + circle1.radius)
Console.WriteLine("  Circle 1: color=" + circle1.color)
Console.WriteLine("  Circle 2: radius=" + circle2.radius)
Console.WriteLine("  Circle 2: color=" + circle2.color)
Console.WriteLine("")

REM Calculate areas
area1 = rect1.width * rect1.height
area2 = rect2.width * rect2.height
area3 = PI * circle1.radius * circle1.radius
area4 = PI * circle2.radius * circle2.radius

Console.WriteLine("Areas:")
Console.WriteLine("  Rectangle 1 area: " + area1)
Console.WriteLine("  Rectangle 2 area: " + area2)
Console.WriteLine("  Circle 1 area: " + area3)
Console.WriteLine("  Circle 2 area: " + area4)
Console.WriteLine("")

Console.WriteLine("=== Geometry Demo Complete ===")
Console.WriteLine("OOP features demonstrated:")
Console.WriteLine("✓ Multiple CLASS declarations")
Console.WriteLine("✓ Constructor parameters")
Console.WriteLine("✓ Field access and calculations")
Console.WriteLine("✓ Object instantiation")