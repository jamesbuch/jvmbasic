' Test multi-argument PRINT with comma and semicolon
x = 42
y = 3.14
name = "Alice"

Console.WriteLine("Test 1: Basic values without semicolons")
Console.WriteLine(x)
Console.WriteLine(y)
Console.WriteLine(name)

Console.WriteLine("Test 2: Comma separator (with spaces)")
Console.WriteLine("x is " + x + "and y is" + y)

Console.WriteLine("Test 3: Semicolon separator (no spaces)")
Console.WriteLine("x=" + x + " y=" + y)

Console.WriteLine("Test 4: Mixed separators")
Console.WriteLine("Value: " + x + "Pi:" + y)

Console.WriteLine("Test 5: Trailing comma (no newline)")
Console.WriteLine("Loading")
Console.WriteLine("done")

Console.WriteLine("Test 6: Trailing semicolon (no newline)")
Console.Write("Count: ")
Console.WriteLine(x)

Console.WriteLine("Test 7: Boolean values")
flag = true
Console.WriteLine("Flag is " + flag)

Console.WriteLine("Test 8: Multiple types")
Console.WriteLine("Values: " + x + " " + y + " " + name + " " + flag)

