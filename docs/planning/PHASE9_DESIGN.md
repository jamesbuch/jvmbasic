# Phase 9 Design: Modern Syntax + JSON + Networking

**Date**: October 19, 2025  
**Branch**: phase9-modern-syntax  
**Goal**: Modernize syntax (Visual Basic-like) + Add JSON/XML/Networking

---

## 🎯 Phase 9 Vision

Phase 9 has **two major goals**:

### Goal 1: Modernize Syntax (Visual Basic-like)
Transform JVM BASIC from classic BASIC to modern Visual Basic-style syntax while maintaining backward compatibility for one version.

### Goal 2: Add Internet Capabilities
Add JSON, XML, and HTTP networking to make JVM BASIC capable of web applications.

**Phase 10**: Remove old BASIC syntax entirely, complete modernization.

---

## 🔄 Syntax Modernization Plan

### Current (Old BASIC) → Modern (VB-like)

#### 1. Variable Declaration
```basic
' OLD (Phase 1-8)
LET x = 10
LET name = "John"
DIM arr(10) = 0

' NEW (Phase 9) - VB-style
Dim x As Integer = 10
Dim name As String = "John"
Dim arr(10) As Integer
```

**Key Changes**:
- `LET` becomes optional (assignment without LET)
- `DIM` becomes `Dim` (proper casing)
- Type annotations: `As Integer`, `As String`, `As Single` (Float)
- Explicit initialization

#### 2. Type Names
```basic
' OLD
INT, FLOAT, STRING, BOOL

' NEW  
Integer, Single, String, Boolean
Long, Double (add new types)
```

#### 3. Literals
```basic
' OLD
LET x = 10
LET y = 3.14

' NEW - Type suffixes
Dim x = 10       ' Integer (default for whole numbers)
Dim y = 3.14     ' Single (default for decimals)
Dim z = 100L     ' Long literal
Dim w = 3.14159D ' Double literal
Dim f = 2.5F     ' Single literal (explicit)
```

#### 4. String Concatenation
```basic
' OLD
LET fullName = firstName + " " + lastName
PRINT "Hello "; name

' NEW - VB-style & operator
Dim fullName = firstName & " " & lastName
Console.WriteLine("Hello " & name)
```

#### 5. Function Declarations
```basic
' OLD
FUNCTION Add(a, b)
    RETURN a + b
ENDFUNCTION

' NEW - VB-style with types
Function Add(a As Integer, b As Integer) As Integer
    Return a + b
End Function

' Or even more modern
Public Function Add(a As Integer, b As Integer) As Integer
    Return a + b
End Function
```

#### 6. Control Flow
```basic
' OLD
IF x > 0 THEN
    PRINT "Positive"
ENDIF

' NEW - VB-style
If x > 0 Then
    Console.WriteLine("Positive")
End If

' OLD
FOR i = 1 TO 10
    PRINT i
NEXT i

' NEW
For i = 1 To 10
    Console.WriteLine(i)
Next i
```

#### 7. Console I/O
```basic
' OLD
PRINT "Hello World"
INPUT x

' NEW - VB-style
Console.WriteLine("Hello World")
Console.Write("Enter value: ")
Dim x = Console.ReadLine()
```

#### 8. Comments
```basic
' OLD
REM This is a comment

' NEW - Both supported
' This is a comment (apostrophe - already supported!)
REM This still works (legacy)
```

---

## 📋 Migration Strategy

### Phase 9: Add Modern Syntax (Dual Mode)
- Support **both** old and new syntax
- All examples use new syntax
- Documentation shows both
- Tests cover both syntaxes

### Phase 10: Deprecate Old Syntax
- Remove LET keyword requirement
- Remove old keywords (ENDFUNCTION → End Function)
- Update all tests to modern syntax
- Complete modernization

---

## 🌐 Internet Capabilities

### JSON Support (~15 functions)

```basic
' Parse JSON
Dim json = JsonParse("{""name"":""John"",""age"":30}")
Dim name = JsonGet(json, "name")
Dim age = JsonGetInt(json, "age")

' Generate JSON
Dim obj = JsonNew()
Call JsonPut(obj, "name", "John")
Call JsonPutInt(obj, "age", 30)
Dim jsonStr = JsonToString(obj)
Console.WriteLine(jsonStr)
```

**Functions**:
- JSONPARSE - Parse JSON string → Object
- JSONGET - Get string value by path
- JSONGETINT - Get integer value
- JSONGETFLOAT - Get float value
- JSONGETBOOL - Get boolean value
- JSONGETARRAY - Get array value
- JSONNEW - Create new JSON object
- JSONPUT - Set string value
- JSONPUTINT - Set integer value
- JSONPUTFLOAT - Set float value
- JSONPUTBOOL - Set boolean value
- JSONTOSTRING - Convert to JSON string
- JSONPRETTY - Pretty-print JSON
- JSONPARSE ARRAY - Parse JSON array
- JSONISVALID - Check if valid JSON

### HTTP Client (~12 functions)

```basic
' Simple HTTP GET
Dim response = HttpGet("https://api.example.com/users")
Dim json = JsonParse(response)

' HTTP POST with data
Dim postData = JsonNew()
Call JsonPut(postData, "name", "John")
Dim response = HttpPost("https://api.example.com/users", JsonToString(postData))

' URL utilities
Dim encoded = UrlEncode("Hello World!")
Dim decoded = UrlDecode("Hello%20World%21")
```

**Functions**:
- HTTPGET - HTTP GET request → String
- HTTPPOST - HTTP POST request → String
- HTTPPUT - HTTP PUT request
- HTTPDELETE - HTTP DELETE request
- HTTPHEAD - HTTP HEAD request
- HTTPSTATUS - Get last HTTP status code
- HTTPHEADERS - Get response headers
- HTTPSETHEAD ER - Set request header
- URLENCODE - URL encode string
- URLDECODE - URL decode string
- URLPARSE - Parse URL into components
- HTTPDOWNLOAD - Download file from URL

### XML Support (~10 functions)

```basic
' Parse XML
Dim xml = XmlParse("<root><name>John</name></root>")
Dim name = XmlGet(xml, "/root/name")

' Generate XML
Dim doc = XmlNew("root")
Dim child = XmlAddChild(doc, "name")
Call XmlSetText(child, "John")
Dim xmlStr = XmlToString(doc)
```

**Functions**:
- XMLPARSE - Parse XML string
- XMLGET - Get node value by XPath
- XMLGETALL - Get all matching nodes
- XMLGETATTR - Get attribute value
- XMLNEW - Create XML document
- XMLADDCHILD - Add child node
- XMLSETTEXT - Set node text
- XMLSETATTR - Set attribute
- XMLTOSTRING - Convert to XML string
- XMLISVALID - Check if valid XML

---

## 📦 Phase 9 Implementation Plan

### Step 1: Add Modern Type Keywords (Week 1)
- Add tokens: Integer, Single, Double, Long, Boolean
- Update lexer to recognize proper casing
- Maintain backward compatibility (INT still works)

### Step 2: Optional LET (Week 1)
- Make LET keyword optional in parser
- `x = 10` works same as `LET x = 10`
- Update documentation

### Step 3: Modern Function Syntax (Week 2)
- Support `Function Name() As Type`
- Support `End Function` (space)
- Maintain `ENDFUNCTION` for compatibility

### Step 4: Type Suffixes (Week 2)
- Parse: 100L, 100S, 3.14F, 3.14D
- Map to appropriate types
- Update lexer number parsing

### Step 5: Console Class (Week 3)
- Add Console.WriteLine(), Console.ReadLine()
- Keep PRINT/INPUT for compatibility
- New examples use Console

### Step 6: JSON Implementation (Week 3-4)
- Choose JSON library (org.json or Gson)
- Implement all JSON functions
- Write comprehensive tests
- Create web scraper example

### Step 7: HTTP Client (Week 4)
- Use java.net.HttpURLConnection
- Implement GET/POST/PUT/DELETE
- Add URL utilities
- Write HTTP test examples

### Step 8: XML Implementation (Week 5)
- Use Java's built-in XML APIs
- Implement parsing and generation
- Write XML tests

---

## 🎯 Function Target for Phase 9

**Current**: 199 functions  
**Add**: ~50 functions (JSON 15 + HTTP 12 + XML 10 + Utilities 13)  
**Target**: ~250 functions

---

## 📝 Backward Compatibility Strategy

### Phase 9 (This Phase)
- **Support BOTH syntaxes**
- All old code still works
- New code uses modern syntax
- Examples show both

### Phase 10 (Next Phase)
- **Remove old syntax**
- Only modern syntax supported
- Convert all tests
- Clean break

---

## 🚀 Example: Modern VB Syntax

```basic
' Modern JVM BASIC (Phase 9+)

Imports System

' Class with modern syntax
Public Class Person
    Private name As String
    Private age As Integer
    
    Public Sub New(n As String, a As Integer)
        Me.name = n
        Me.age = a
    End Sub
    
    Public Function GetInfo() As String
        Return name & " (age " & age & ")"
    End Function
End Class

' Main program
Sub Main()
    ' Modern variable declarations
    Dim p As New Person("John", 30)
    Console.WriteLine(p.GetInfo())
    
    ' HTTP request
    Dim response = HttpGet("https://api.github.com/users/jamesbuch")
    Dim json = JsonParse(response)
    Dim username = JsonGet(json, "login")
    Console.WriteLine("Username: " & username)
    
    ' Collections
    Dim numbers As New IntList()
    numbers.Add(10)
    numbers.Add(20)
    numbers.Add(30)
    
    For i = 0 To numbers.Size() - 1
        Console.WriteLine(numbers.Get(i))
    Next i
End Sub
```

---

## 🎯 Success Criteria

Phase 9 complete when:
1. ✅ Modern VB syntax fully supported
2. ✅ Old syntax still works (backward compatible)
3. ✅ JSON parsing and generation working
4. ✅ HTTP client fully functional
5. ✅ XML support complete
6. ✅ ~250 built-in functions
7. ✅ All tests passing
8. ✅ Examples using modern syntax

---

## 📚 Documentation Plan

### New Documents
- MODERN_SYNTAX_GUIDE.md - Migration guide
- JSON_GUIDE.md - JSON usage examples
- HTTP_GUIDE.md - Networking examples
- PHASE9_COMPLETE.md - Session summary

### Updated Documents
- USER_GUIDE.md - Add modern syntax sections
- README.md - Update with Phase 9 features

---

**Phase 9 Focus**: Modernization + Internet Capabilities  
**Phase 10 Focus**: Complete old syntax removal

**Next Chat**: Begin Phase 9 implementation!

