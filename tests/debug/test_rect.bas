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

DIM rect1 AS NEW Rectangle(10.0, 5.0, "red")
Console.WriteLine("Rectangle: width=" + rect1.width)


