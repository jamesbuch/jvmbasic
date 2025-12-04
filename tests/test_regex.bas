Dim text As String
Dim email As String
Dim phone As String
Dim number As String
Dim word As String
Dim username As String
Dim domain As String
Dim masked As String
Dim clean As String
Dim msg1 As String
Dim msg2 As String
Dim msg3 As String

Console.WriteLine("=== Regular Expression Test ===")
Console.WriteLine("")

text = "The answer is 42 and 100"
email = "user@example.com"
phone = "Phone: 555-1234"

Console.WriteLine("1. Pattern matching...")
Console.WriteLine("   Text: " + text)
Console.WriteLine("   Matches '\\d+' (digits): " + Regex.Match("\\d+", text))
Console.WriteLine("   Matches 'xyz': " + Regex.Match("xyz", text))
Console.WriteLine("")

Console.WriteLine("2. Finding patterns...")
number = Regex.Find("\\d+", text)
Console.WriteLine("   First number found: " + number)
word = Regex.Find("answer", text)
Console.WriteLine("   Word 'answer' found: " + word)
Console.WriteLine("")

Console.WriteLine("3. Capture groups...")
username = Regex.Group("(.+)@", email, 1)
Console.WriteLine("   Email: " + email)
Console.WriteLine("   Username (group 1): " + username)
domain = Regex.Group("@(.+)", email, 1)
Console.WriteLine("   Domain (group 1): " + domain)
Console.WriteLine("")

Console.WriteLine("4. Replace...")
masked = Regex.Replace("\\d", phone, "X")
Console.WriteLine("   Original: " + phone)
Console.WriteLine("   Masked: " + masked)
clean = Regex.Replace("[^0-9]", phone, "")
Console.WriteLine("   Digits only: " + clean)
Console.WriteLine("")

Console.WriteLine("5. Format strings...")
msg1 = Str.Format("Hello, {0}!", "World")
Console.WriteLine("    " + msg1)
msg2 = Str.FormatFloat("Pi is approximately {0}", PI)
Console.WriteLine("    " + msg2)
msg3 = Str.FormatInt("The answer is {0}", 42)
Console.WriteLine("    " + msg3)
Console.WriteLine("")

Console.WriteLine("=== Regex Tests Complete ===")

