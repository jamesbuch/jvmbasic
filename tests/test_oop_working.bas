REM Test OOP features that are working

CLASS Point
    PUBLIC x As Float
    PUBLIC y As Float
END CLASS

CLASS Rectangle
    PUBLIC width As Float
    PUBLIC height As Float
END CLASS

DIM p AS NEW Point()
p.x = 5.0
p.y = 10.0

DIM r AS NEW Rectangle()
r.width = 20.0
r.height = 15.0

Console.WriteLine("Point: (" + p.x + ", " + p.y + ")")
Console.WriteLine("Rectangle: " + r.width + " x " + r.height)
Console.WriteLine("")
Console.WriteLine("Phase 7 OOP Features Working:")
Console.WriteLine("✓ CLASS declarations")
Console.WriteLine("✓ PUBLIC fields")
Console.WriteLine("✓ NEW operator")
Console.WriteLine("✓ Field access (getfield)")
Console.WriteLine("✓ Field assignment (putfield)")

