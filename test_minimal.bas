CLASS Point
    PUBLIC x As Float
    PUBLIC y As Float
    
SUB New(px As Float, py As Float)
        x = px
        y = py
    END SUB
END CLASS

DIM point1 AS NEW Point(5.0, 10.0)
Console.WriteLine("Point: x=" + point1.x)


