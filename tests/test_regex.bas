PRINT "=== Regular Expression Test ==="
PRINT ""

LET text = "The answer is 42 and 100"
LET email = "user@example.com"
LET phone = "Phone: 555-1234"

PRINT "1. Pattern matching..."
PRINT "   Text:", text
PRINT "   Matches '\\d+' (digits):", REGEXMATCH("\\d+", text)
PRINT "   Matches 'xyz':", REGEXMATCH("xyz", text)
PRINT ""

PRINT "2. Finding patterns..."
LET number = REGEXFIND("\\d+", text)
PRINT "   First number found:", number
LET word = REGEXFIND("answer", text)
PRINT "   Word 'answer' found:", word
PRINT ""

PRINT "3. Capture groups..."
LET username = REGEXGROUP("(.+)@", email, 1)
PRINT "   Email:", email
PRINT "   Username (group 1):", username
LET domain = REGEXGROUP("@(.+)", email, 1)
PRINT "   Domain (group 1):", domain
PRINT ""

PRINT "4. Replace..."
LET masked = REGEXREPLACE("\\d", phone, "X")
PRINT "   Original:", phone
PRINT "   Masked:", masked
LET clean = REGEXREPLACE("[^0-9]", phone, "")
PRINT "   Digits only:", clean
PRINT ""

PRINT "5. Format strings..."
LET msg1 = FORMAT("Hello, {0}!", "World")
PRINT "   ", msg1
LET msg2 = FORMATF("Pi is approximately {0}", PI)
PRINT "   ", msg2
LET msg3 = FORMATI("The answer is {0}", 42)
PRINT "   ", msg3
PRINT ""

PRINT "=== Regex Tests Complete ==="

