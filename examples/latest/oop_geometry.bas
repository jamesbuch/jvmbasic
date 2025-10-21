' Modern VB-Style Geometry OOP Example
' Demonstrates classes with Math namespace

CLASS Circle
    PUBLIC radius AS SINGLE
    
    PUBLIC SUB New(r AS SINGLE)
        radius = r
    END SUB
END CLASS

CLASS Rectangle
    PUBLIC width AS SINGLE
    PUBLIC height AS SINGLE
    
    PUBLIC SUB New(w AS SINGLE, h AS SINGLE)
        width = w
        height = h
    END SUB
END CLASS

' Main program
Dim dummy As Integer
Let dummy = Console.WriteLine("=== Geometry Demonstrations ===")
Let dummy = Console.WriteLine("")

Dim circle As New Circle(5.0)
Dim area As Single = Math.PI() * circle.radius * circle.radius
Let dummy = Console.WriteLine("Circle radius: " + FormatF("%.1f", circle.radius))
Let dummy = Console.WriteLine("Circle area: " + FormatF("%.2f", area))
Let dummy = Console.WriteLine("")

Dim rect As New Rectangle(10.0, 5.0)
Dim rectArea As Single = rect.width * rect.height
Let dummy = Console.WriteLine("Rectangle: " + FormatF("%.1f", rect.width) + " x " + FormatF("%.1f", rect.height))
Let dummy = Console.WriteLine("Rectangle area: " + FormatF("%.2f", rectArea))

