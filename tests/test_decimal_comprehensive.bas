' Test Decimal (BigDecimal) comprehensive operations
' Tests all BigDecimal runtime functions

Console.WriteLine("=== Decimal Operations Test ===")
Console.WriteLine("")

' Test creation from string
Dim price As Decimal
Dim taxRate As Decimal
Dim quantity As Decimal

price = Decimal.FromString("19.99")
taxRate = Decimal.FromString("0.0825")
quantity = Decimal.FromString("3")

Console.WriteLine("price = 19.99")
Console.WriteLine("taxRate = 0.0825")
Console.WriteLine("quantity = 3")
Console.WriteLine("")

' Test arithmetic operations
Console.WriteLine("--- Arithmetic Operations ---")
Dim subtotal As Decimal
Dim tax As Decimal
Dim total As Decimal

subtotal = Decimal.Multiply(price, quantity)
Console.WriteLine("subtotal (price * qty) = " + Decimal.ToString(subtotal))

tax = Decimal.Multiply(subtotal, taxRate)
Console.WriteLine("tax (subtotal * rate) = " + Decimal.ToString(tax))

total = Decimal.Add(subtotal, tax)
Console.WriteLine("total (subtotal + tax) = " + Decimal.ToString(total))

Dim change As Decimal
change = Decimal.Subtract(Decimal.FromString("100.00"), total)
Console.WriteLine("change from $100 = " + Decimal.ToString(change))
Console.WriteLine("")

' Test division with scale
Console.WriteLine("--- Division Operations ---")
Dim a As Decimal
Dim b As Decimal
a = Decimal.FromString("10")
b = Decimal.FromString("3")

Dim divResult As Decimal
divResult = Decimal.DivideSimple(a, b)
Console.WriteLine("10 / 3 (simple) = " + Decimal.ToString(divResult))

Dim remainder As Decimal
remainder = Decimal.Remainder(a, b)
Console.WriteLine("10 mod 3 = " + Decimal.ToString(remainder))
Console.WriteLine("")

' Test power
Console.WriteLine("--- Power Operations ---")
Dim base As Decimal
Dim squared As Decimal
base = Decimal.FromString("1.5")
squared = Decimal.Pow(base, 2)
Console.WriteLine("1.5^2 = " + Decimal.ToString(squared))

Dim cubed As Decimal
cubed = Decimal.Pow(base, 3)
Console.WriteLine("1.5^3 = " + Decimal.ToString(cubed))
Console.WriteLine("")

' Test comparison
Console.WriteLine("--- Comparison Operations ---")
Dim cmp As Integer
cmp = Decimal.CompareTo(price, taxRate)
Console.WriteLine("CompareTo(19.99, 0.0825) = " + cmp)

Dim eq As Boolean
eq = Decimal.Equals(price, price)
Console.WriteLine("Equals(price, price) = " + eq)

eq = Decimal.Equals(price, taxRate)
Console.WriteLine("Equals(price, taxRate) = " + eq)
Console.WriteLine("")

' Test unary operations
Console.WriteLine("--- Unary Operations ---")
Dim neg As Decimal
neg = Decimal.FromString("-123.456")
Dim absVal As Decimal
absVal = Decimal.Abs(neg)
Console.WriteLine("Abs(-123.456) = " + Decimal.ToString(absVal))

Dim negated As Decimal
negated = Decimal.Negate(price)
Console.WriteLine("Negate(19.99) = " + Decimal.ToString(negated))

Dim sign As Integer
sign = Decimal.Signum(neg)
Console.WriteLine("Signum(-123.456) = " + sign)

sign = Decimal.Signum(price)
Console.WriteLine("Signum(19.99) = " + sign)
Console.WriteLine("")

' Test scale and precision
Console.WriteLine("--- Scale and Precision ---")
Dim piVal As Decimal
piVal = Decimal.FromString("3.14159265358979323846")
Console.WriteLine("piVal = " + Decimal.ToString(piVal))

Dim sc As Integer
sc = Decimal.Scale(piVal)
Console.WriteLine("Scale(piVal) = " + sc)

Dim prec As Integer
prec = Decimal.Precision(piVal)
Console.WriteLine("Precision(piVal) = " + prec)
Console.WriteLine("")

' Test rounding
Console.WriteLine("--- Rounding ---")
Dim rounded As Decimal
rounded = Decimal.Round(piVal, 5)
Console.WriteLine("Round(piVal, 5) = " + Decimal.ToString(rounded))

rounded = Decimal.Round(piVal, 3)
Console.WriteLine("Round(piVal, 3) = " + Decimal.ToString(rounded))
Console.WriteLine("")

' Test utility functions
Console.WriteLine("--- Utility Functions ---")
Dim maxVal As Decimal
maxVal = Decimal.Max(price, taxRate)
Console.WriteLine("Max(19.99, 0.0825) = " + Decimal.ToString(maxVal))

Dim minVal As Decimal
minVal = Decimal.Min(price, taxRate)
Console.WriteLine("Min(19.99, 0.0825) = " + Decimal.ToString(minVal))
Console.WriteLine("")

' Test constants
Console.WriteLine("--- Constants ---")
Console.WriteLine("Zero = " + Decimal.ToString(Decimal.Zero()))
Console.WriteLine("One = " + Decimal.ToString(Decimal.One()))
Console.WriteLine("Ten = " + Decimal.ToString(Decimal.Ten()))
Console.WriteLine("")

' Test conversion
Console.WriteLine("--- Conversion ---")
Dim fromDouble As Decimal
fromDouble = Decimal.FromDouble(3.14159)
Console.WriteLine("FromDouble(3.14159) = " + Decimal.ToString(fromDouble))

Dim asDouble As Double
asDouble = Decimal.ToDouble(piVal)
Console.WriteLine("ToDouble(piVal) = " + asDouble)
Console.WriteLine("")

Console.WriteLine("=== Decimal Test Complete ===")
