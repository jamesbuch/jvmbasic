' test_stdin_numbers.bas - Test numeric input from stdin
' =====================================================
' Run with: java -cp .:basicrt:lib/* TestStdinNumbers < tests/test_stdin_numbers_data.txt
'
' This test reads numbers from stdin and performs calculations

Console.WriteLine("=== Numeric Input Test ===")
Console.WriteLine("")

' Read first number
Console.Write("Enter first number: ")
Dim num1Str As String = Console.ReadLine()
Dim num1 As Integer = Val(num1Str)
Console.WriteLine("Read: " + num1)

' Read second number
Console.Write("Enter second number: ")
Dim num2Str As String = Console.ReadLine()
Dim num2 As Integer = Val(num2Str)
Console.WriteLine("Read: " + num2)

' Read decimal number
Console.Write("Enter decimal: ")
Dim decStr As String = Console.ReadLine()
Dim dec As Double = Val(decStr)
Console.WriteLine("Read: " + dec)

' Perform calculations
Console.WriteLine("")
Console.WriteLine("--- Results ---")
Console.WriteLine("Sum: " + (num1 + num2))
Console.WriteLine("Difference: " + (num1 - num2))
Console.WriteLine("Product: " + (num1 * num2))

If num2 <> 0 Then
    Console.WriteLine("Quotient: " + (num1 / num2))
End If

Console.WriteLine("Decimal * 2: " + (dec * 2))
Console.WriteLine("")
Console.WriteLine("=== Test Complete ===")
