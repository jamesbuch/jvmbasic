' Geometry Example - Multiple Classes Working Together
' Shows: Multiple classes, field access, calculations

' Define Point class
CLASS Point
    PUBLIC x As Float
    PUBLIC y As Float
END CLASS

' Define Rectangle class
CLASS Rectangle
    PUBLIC width As Float
    PUBLIC height As Float
    PUBLIC color As String
END CLASS

' Define Circle class
CLASS Circle
    PUBLIC radius As Float
    PUBLIC color As String
END CLASS

Console.WriteLine("=== Geometry Calculator ===")
Console.WriteLine("")

' Create geometric objects
DIM point1 AS NEW Point()
point1.x = 5.0
point1.y = 10.0

DIM point2 AS NEW Point()
point2.x = 15.0
point2.y = 20.0

DIM rect1 AS NEW Rectangle()
rect1.width = 10.0
rect1.height = 5.0
rect1.color = "red"

DIM rect2 AS NEW Rectangle()
rect2.width = 20.0
rect2.height = 15.0
rect2.color = "blue"

DIM circle1 AS NEW Circle()
circle1.radius = 7.5
circle1.color = "green"

DIM circle2 AS NEW Circle()
circle2.radius = 12.0
circle2.color = "yellow"

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

' Calculate areas
Dim area1 As Single
Dim area2 As Single
Dim area3 As Single
Dim area4 As Single

area1 = rect1.width * rect1.height
area2 = rect2.width * rect2.height
area3 = PI() * circle1.radius * circle1.radius
area4 = PI() * circle2.radius * circle2.radius

Console.WriteLine("Areas:")
Console.WriteLine("  Rectangle 1 area: " + area1)
Console.WriteLine("  Rectangle 2 area: " + area2)
Console.WriteLine("  Circle 1 area: " + area3)
Console.WriteLine("  Circle 2 area: " + area4)
Console.WriteLine("")

Console.WriteLine("=== Geometry Demo Complete ===")
Console.WriteLine("OOP features demonstrated:")
Console.WriteLine("- Multiple CLASS declarations")
Console.WriteLine("- Field access and calculations")
Console.WriteLine("- Object instantiation")
