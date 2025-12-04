' financial_calculator.bas - Financial calculations with BigDecimal
' Demonstrates precise decimal arithmetic for financial applications

Console.WriteLine("=== Financial Calculator with BigDecimal ===")
Console.WriteLine("")

' Rounding mode constants
' 0=UP, 1=DOWN, 2=CEILING, 3=FLOOR, 4=HALF_UP, 5=HALF_DOWN, 6=HALF_EVEN
Dim roundUp As Integer = 4

' Simple purchase calculation
Console.WriteLine("--- Shopping Cart Calculation ---")
Dim itemPrice As Decimal = Decimal.FromString("19.99")
Dim quantity As Decimal = Decimal.FromString("3")
Dim taxRate As Decimal = Decimal.FromString("0.0825")  ' 8.25% tax

Console.WriteLine("Item Price: $" + Decimal.ToString(itemPrice))
Console.WriteLine("Quantity: " + Decimal.ToString(quantity))
Console.WriteLine("Tax Rate: " + Decimal.ToString(Decimal.Multiply(taxRate, Decimal.FromString("100"))) + "%")

Dim subtotal As Decimal = Decimal.Multiply(itemPrice, quantity)
Console.WriteLine("Subtotal: $" + Decimal.ToString(subtotal))

Dim tax As Decimal = Decimal.Multiply(subtotal, taxRate)
Dim roundedTax As Decimal = Decimal.SetScale(tax, 2, roundUp)
Console.WriteLine("Tax: $" + Decimal.ToString(roundedTax))

Dim total As Decimal = Decimal.Add(subtotal, roundedTax)
Console.WriteLine("Total: $" + Decimal.ToString(total))
Console.WriteLine("")

' Loan interest calculation
Console.WriteLine("--- Loan Interest Calculation ---")
Dim principal As Decimal = Decimal.FromString("10000.00")
Dim annualRate As Decimal = Decimal.FromString("0.05")  ' 5% annual
Dim years As Decimal = Decimal.FromString("3")

Console.WriteLine("Principal: $" + Decimal.ToString(principal))
Console.WriteLine("Annual Rate: 5%")
Console.WriteLine("Term: 3 years")

' Simple interest: I = P * r * t
Dim interest As Decimal = Decimal.Multiply(principal, Decimal.Multiply(annualRate, years))
Console.WriteLine("Simple Interest: $" + Decimal.ToString(interest))

Dim finalAmount As Decimal = Decimal.Add(principal, interest)
Console.WriteLine("Final Amount: $" + Decimal.ToString(finalAmount))
Console.WriteLine("")

' Currency exchange
Console.WriteLine("--- Currency Exchange ---")
Dim usdAmount As Decimal = Decimal.FromString("1000.00")
Dim eurRate As Decimal = Decimal.FromString("0.92")
Dim gbpRate As Decimal = Decimal.FromString("0.79")
Dim jpyRate As Decimal = Decimal.FromString("149.50")

Console.WriteLine("USD Amount: $" + Decimal.ToString(usdAmount))

Dim eurAmount As Decimal = Decimal.Multiply(usdAmount, eurRate)
Console.WriteLine("EUR: " + Decimal.ToString(Decimal.SetScale(eurAmount, 2, roundUp)))

Dim gbpAmount As Decimal = Decimal.Multiply(usdAmount, gbpRate)
Console.WriteLine("GBP: " + Decimal.ToString(Decimal.SetScale(gbpAmount, 2, roundUp)))

Dim jpyAmount As Decimal = Decimal.Multiply(usdAmount, jpyRate)
Console.WriteLine("JPY: " + Decimal.ToString(Decimal.SetScale(jpyAmount, 0, roundUp)))
Console.WriteLine("")

' Percentage calculations
Console.WriteLine("--- Percentage Calculations ---")
Dim originalPrice As Decimal = Decimal.FromString("79.99")
Dim discountPercent As Decimal = Decimal.FromString("25")

Console.WriteLine("Original Price: $" + Decimal.ToString(originalPrice))
Console.WriteLine("Discount: " + Decimal.ToString(discountPercent) + "%")

Dim discountMultiplier As Decimal = Decimal.DivideSimple(discountPercent, Decimal.FromString("100"))
Dim discountAmount As Decimal = Decimal.Multiply(originalPrice, discountMultiplier)
discountAmount = Decimal.SetScale(discountAmount, 2, roundUp)
Console.WriteLine("Discount Amount: $" + Decimal.ToString(discountAmount))

Dim salePrice As Decimal = Decimal.Subtract(originalPrice, discountAmount)
Console.WriteLine("Sale Price: $" + Decimal.ToString(salePrice))
Console.WriteLine("")

' Tip calculator
Console.WriteLine("--- Tip Calculator ---")
Dim billAmount As Decimal = Decimal.FromString("85.50")
Dim tipPercent15 As Decimal = Decimal.FromString("0.15")
Dim tipPercent18 As Decimal = Decimal.FromString("0.18")
Dim tipPercent20 As Decimal = Decimal.FromString("0.20")

Console.WriteLine("Bill Amount: $" + Decimal.ToString(billAmount))

Dim tip15 As Decimal = Decimal.SetScale(Decimal.Multiply(billAmount, tipPercent15), 2, roundUp)
Dim tip18 As Decimal = Decimal.SetScale(Decimal.Multiply(billAmount, tipPercent18), 2, roundUp)
Dim tip20 As Decimal = Decimal.SetScale(Decimal.Multiply(billAmount, tipPercent20), 2, roundUp)

Console.WriteLine("15% tip: $" + Decimal.ToString(tip15) + " (Total: $" + Decimal.ToString(Decimal.Add(billAmount, tip15)) + ")")
Console.WriteLine("18% tip: $" + Decimal.ToString(tip18) + " (Total: $" + Decimal.ToString(Decimal.Add(billAmount, tip18)) + ")")
Console.WriteLine("20% tip: $" + Decimal.ToString(tip20) + " (Total: $" + Decimal.ToString(Decimal.Add(billAmount, tip20)) + ")")
Console.WriteLine("")

' Split bill calculator
Console.WriteLine("--- Split Bill ---")
Dim totalBill As Decimal = Decimal.FromString("156.75")
Dim numPeople As Decimal = Decimal.FromString("4")

Console.WriteLine("Total Bill: $" + Decimal.ToString(totalBill))
Console.WriteLine("Number of People: 4")

Dim perPerson As Decimal = Decimal.DivideSimple(totalBill, numPeople)
perPerson = Decimal.SetScale(perPerson, 2, roundUp)
Console.WriteLine("Per Person: $" + Decimal.ToString(perPerson))

' Calculate if rounding caused any discrepancy
Dim calculatedTotal As Decimal = Decimal.Multiply(perPerson, numPeople)
Dim difference As Decimal = Decimal.Subtract(totalBill, calculatedTotal)
If Decimal.CompareTo(difference, Decimal.Zero()) <> 0 Then
    Console.WriteLine("Rounding adjustment: $" + Decimal.ToString(difference))
End If
Console.WriteLine("")

' Compound interest demonstration
Console.WriteLine("--- Compound Interest (Annual) ---")
Dim p As Decimal = Decimal.FromString("1000.00")
Dim r As Decimal = Decimal.FromString("1.05")  ' 5% = multiply by 1.05
Dim n As Integer = 10

Console.WriteLine("Principal: $" + Decimal.ToString(p) + ", Rate: 5%, Years: 10")

Dim amount As Decimal = p
Dim year As Integer
For year = 1 To n
    amount = Decimal.Multiply(amount, r)
Next

Dim finalAmt As Decimal = Decimal.SetScale(amount, 2, roundUp)
Console.WriteLine("Final Amount: $" + Decimal.ToString(finalAmt))
Dim totalInterest As Decimal = Decimal.Subtract(finalAmt, p)
Console.WriteLine("Total Interest Earned: $" + Decimal.ToString(totalInterest))
Console.WriteLine("")

Console.WriteLine("=== Calculator Complete ===")
