' Geometry Example - Multiple Classes Working Together
' Shows: Multiple classes, field access, calculations

' Define Point class with constructor
Class Point
    Public x As Float
    Public y As Float

    Public Sub New(px As Float, py As Float)
        Me.x = px
        Me.y = py
    End Sub
End Class

' Define Rectangle class with constructor
Class Rectangle
    Public width As Float
    Public height As Float
    Public color As String

    Public Sub New(w As Float, h As Float, c As String)
        Me.width = w
        Me.height = h
        Me.color = c
    End Sub
End Class

' Define Circle class with constructor
Class Circle
    Public radius As Float
    Public color As String

    Public Sub New(r As Float, c As String)
        Me.radius = r
        Me.color = c
    End Sub
End Class

Console.WriteLine("=== Geometry Calculator ===")
Console.WriteLine("")

' Create geometric objects using constructors
Dim point1 As New Point(5.0, 10.0)
Dim point2 As New Point(15.0, 20.0)

Dim rect1 As New Rectangle(10.0, 5.0, "red")
Dim rect2 As New Rectangle(20.0, 15.0, "blue")

Dim circle1 As New Circle(7.5, "green")
Dim circle2 As New Circle(12.0, "yellow")

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
Dim area1 As Float
Dim area2 As Float
Dim area3 As Float
Dim area4 As Float

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
Console.WriteLine("- Multiple Class declarations")
Console.WriteLine("- Constructor with parameters")
Console.WriteLine("- Field access and calculations")
Console.WriteLine("- Object instantiation")
