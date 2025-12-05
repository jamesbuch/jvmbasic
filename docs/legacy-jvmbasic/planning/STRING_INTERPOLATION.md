# String Interpolation Implementation Plan

**Priority**: HIGH (Phase 10)  
**Status**: Planning  

---

## 🎯 Goal

Implement modern string interpolation similar to Python f-strings or C# interpolated strings.

**Syntax**:
```basic
Dim name As String = "Alice"
Dim age As Integer = 30
Console.WriteLine($"Hello {name}, you are {age} years old!")
' Output: Hello Alice, you are 30 years old!
```

---

## 📋 Requirements

### 1. Variables Only (Phase 10)
Support interpolating variables of all supported types:
```basic
Dim text As String = "world"
Dim count As Integer = 42
Dim price As Single = 19.99
Dim active As Boolean = True

Console.WriteLine($"Text: {text}, Count: {count}, Price: {price}, Active: {active}")
```

### 2. Automatic Type Conversion
Convert all types to strings automatically:
- `Integer` → `Str()`
- `Single` → `Str()`
- `Double` → `Str()`
- `Long` → `Str()`
- `Boolean` → `"True"`/`"False"`
- `String` → as-is

### 3. Escape Sequences
```basic
Console.WriteLine($"Use {{braces}} to print literal braces")
' Output: Use {braces} to print literal braces

Console.WriteLine($"Backslash: \\ Quote: \"")
' Output: Backslash: \ Quote: "
```

---

## 🔧 Implementation

### Lexer Changes (`lexer.cpp`)

**New Token Type**:
```cpp
enum class TokenType {
    // ... existing tokens ...
    INTERPOLATED_STRING,  // $"text {var} more"
};
```

**Recognition**:
```cpp
Token Lexer::readInterpolatedString() {
    // Start with $"
    if (ch == '$' && peek() == '"') {
        read(); // consume $
        read(); // consume "
        
        string result;
        vector<InterpolationPart> parts;
        
        while (ch != '"' && ch != EOF) {
            if (ch == '{') {
                // Found interpolation placeholder
                read(); // consume {
                string varName;
                while (ch != '}' && ch != EOF) {
                    varName += ch;
                    read();
                }
                if (ch == '}') read(); // consume }
                
                parts.push_back({INTERPOLATION, varName});
            } else if (ch == '\\') {
                // Escape sequence
                read();
                if (ch == '{' || ch == '}' || ch == '\\' || ch == '"') {
                    result += ch;
                    read();
                }
            } else {
                result += ch;
                read();
            }
        }
        
        if (ch == '"') read(); // consume closing "
        
        return Token{INTERPOLATED_STRING, result, parts};
    }
}
```

### Parser Changes (`parser.cpp`)

**Parse Interpolated String**:
```cpp
ExprPtr Parser::parsePrimary() {
    // ... existing cases ...
    
    if (currentToken.type == TokenType::INTERPOLATED_STRING) {
        // Build string concatenation expression
        string baseText = currentToken.sval;
        vector<InterpolationPart> parts = currentToken.interpolationParts;
        
        // Create: baseText + Str(var1) + moreText + Str(var2) + ...
        ExprPtr result = make_shared<Expr>(/* build concatenation */);
        
        advance();
        return result;
    }
}
```

**Transform to String Concatenation**:
```basic
' Source:
Console.WriteLine($"Hello {name}, age {age}")

' Transformed AST:
Console.WriteLine("Hello " + name + ", age " + Str(age))
```

### Code Generation (No Changes Needed)

String concatenation already works, so no codegen changes required!

---

## 📝 Examples

### Basic Interpolation
```basic
Dim name As String = "Alice"
Dim greeting As String = $"Hello, {name}!"
Console.WriteLine(greeting)
' Output: Hello, Alice!
```

### Multiple Variables
```basic
Dim first As String = "John"
Dim last As String = "Doe"
Dim age As Integer = 25

Console.WriteLine($"Name: {first} {last}, Age: {age}")
' Output: Name: John Doe, Age: 25
```

### All Types
```basic
Dim name As String = "Product"
Dim count As Integer = 42
Dim price As Single = 19.99
Dim available As Boolean = True
Dim total As Double = 839.58

Console.WriteLine($"{name}: {count} items at ${price} each")
Console.WriteLine($"Total: ${total}, Available: {available}")
' Output:
' Product: 42 items at $19.99 each
' Total: $839.58, Available: True
```

### Escape Braces
```basic
Console.WriteLine($"Use {{variable}} syntax for interpolation")
' Output: Use {variable} syntax for interpolation
```

### Multi-line (Future)
```basic
Dim text As String = $"Line 1: {var1}
Line 2: {var2}
Line 3: {var3}"
```

---

## 🚀 Future Enhancements (Phase 11+)

### 1. Format Specifiers
```basic
Dim pi As Single = 3.14159
Console.WriteLine($"Pi: {pi:F2}")  ' Output: Pi: 3.14

Dim now As Long = Now()
Console.WriteLine($"Date: {now:yyyy-MM-dd}")  ' Output: Date: 2025-10-22
```

### 2. Expressions in Braces
```basic
Dim x As Integer = 10
Dim y As Integer = 20
Console.WriteLine($"Sum: {x + y}, Product: {x * y}")
' Output: Sum: 30, Product: 200
```

### 3. Alignment and Padding
```basic
Dim name As String = "Item"
Console.WriteLine($"{name,-20} $10.00")  ' Left-aligned, 20 chars
' Output: Item                 $10.00
```

---

## 🧪 Testing

### New Tests
- `test_interpolation_basic.bas` - Simple variable interpolation
- `test_interpolation_types.bas` - All type conversions
- `test_interpolation_escape.bas` - Escape sequences
- `test_interpolation_multiple.bas` - Multiple variables
- `test_interpolation_console.bas` - With Console.WriteLine

### Example Test
```basic
' test_interpolation_basic.bas
Dim name As String = "Alice"
Dim age As Integer = 30

Dim result As String = $"Name: {name}, Age: {age}"
Console.WriteLine(result)

' Expected output: Name: Alice, Age: 30
```

---

## 📚 Documentation

### User Guide Update
```markdown
### String Interpolation (Phase 10)

Use `$"..."` syntax to interpolate variables into strings:

\```basic
Dim name As String = "Alice"
Console.WriteLine($"Hello, {name}!")
\```

Supported types: String, Integer, Single, Double, Long, Boolean
```

### Quick Reference
```basic
' Basic interpolation
$"Hello {variable}"

' Multiple variables
$"Name: {first} {last}, Age: {age}"

' Escape braces
$"Use {{braces}} for literal braces"
```

---

## 🎯 Implementation Steps

### Phase 10.1: Basic Support (Week 1-2)
1. Add `INTERPOLATED_STRING` token type to lexer
2. Implement lexer recognition of `$"..."` strings
3. Parse `{variable}` placeholders
4. Transform to string concatenation in parser
5. Test with simple cases

### Phase 10.2: Type Support (Week 3)
6. Ensure automatic type conversion for all types
7. Test Integer, Single, Double, Long, Boolean
8. Handle edge cases (null/empty strings)

### Phase 10.3: Escape Sequences (Week 4)
9. Support `{{` and `}}` for literal braces
10. Support `\\`, `\"`, etc.
11. Comprehensive testing

### Phase 10.4: Documentation (Week 4)
12. Update user guide
13. Add examples to README
14. Create quick reference

---

## 🔧 Technical Details

### Token Structure
```cpp
struct Token {
    TokenType type;
    string sval;
    float fval;
    int line;
    
    // For interpolated strings
    vector<InterpolationPart> interpolationParts;
};

struct InterpolationPart {
    enum Type { TEXT, VARIABLE };
    Type type;
    string value;
};
```

### Transformation Algorithm
```
Input: $"Hello {name}, you are {age} years old"

Parse into parts:
  1. TEXT: "Hello "
  2. VARIABLE: "name"
  3. TEXT: ", you are "
  4. VARIABLE: "age"
  5. TEXT: " years old"

Generate expression:
  "Hello " + name + ", you are " + Str(age) + " years old"
```

### Type Conversion Rules
```cpp
ExprPtr convertToString(ExprPtr expr, Type type) {
    if (type == Type::String) {
        return expr;  // Already string
    } else if (type == Type::Int || type == Type::Float || 
               type == Type::Long || type == Type::Double) {
        // Create Str() call
        return makeBuiltinCall("Str", {expr});
    } else if (type == Type::Bool) {
        // Convert to "True"/"False"
        return makeTernary(expr, 
            makeString("True"), 
            makeString("False"));
    }
}
```

---

## 🎨 Syntax Comparison

### JVM BASIC (Proposed)
```basic
Console.WriteLine($"Hello {name}, age {age}")
```

### Python
```python
print(f"Hello {name}, age {age}")
```

### C#
```csharp
Console.WriteLine($"Hello {name}, age {age}");
```

### JavaScript (ES6)
```javascript
console.log(`Hello ${name}, age ${age}`);
```

**We match modern language conventions!**

---

## ⚠️ Limitations (Phase 10)

1. **Variables only** - No expressions in braces
   - ❌ `$"Result: {x + y}"`  (Phase 11)
   - ✅ `$"Result: {result}"`

2. **No format specifiers** (Phase 11)
   - ❌ `$"Price: {price:C2}"`
   - ✅ `$"Price: {price}"`

3. **No alignment** (Phase 11)
   - ❌ `$"{name,-20}"`
   - ✅ `$"{name}"`

4. **Single-line only** (Phase 10)
   - Multi-line support in Phase 11

---

## 🎯 Success Criteria

String interpolation is complete when:
- ✅ `$"..."` syntax recognized by lexer
- ✅ Variables of all types can be interpolated
- ✅ Automatic type conversion works
- ✅ Escape sequences work (`{{`, `}}`, `\\`, `\"`)
- ✅ 5+ tests passing
- ✅ Documentation complete
- ✅ Examples updated to use interpolation

---

## 📊 Impact

### Before
```basic
Dim name As String = "Alice"
Dim age As Integer = 30
Console.WriteLine("Name: " + name + ", Age: " + Str(age))
```

### After
```basic
Dim name As String = "Alice"
Dim age As Integer = 30
Console.WriteLine($"Name: {name}, Age: {age}")
```

**Much cleaner and more readable!**

---

**Priority**: HIGH  
**Complexity**: MEDIUM  
**Timeline**: 4 weeks for Phase 10 basic support  
**Dependencies**: None (can implement independently)  

**Ready to implement!** 🚀

