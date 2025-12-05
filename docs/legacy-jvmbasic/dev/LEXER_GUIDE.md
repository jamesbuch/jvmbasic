# JVM BASIC - Lexer Guide

**Purpose**: Understanding tokenization and extending the lexer  
**Audience**: Developers adding new syntax

---

## Overview

The lexer (lexical analyzer / scanner) converts source code text into a stream of tokens.

**Files**: `lexer.h`, `lexer.cpp`

**Input**: Character stream (from stdin or file)  
**Output**: Token stream

---

## Token Structure

```cpp
enum class TokenType { 
    END, NUMBER, STRING, ID, 
    PLUS, MINUS, MUL, DIV, MOD, 
    ASSIGN, SEMI, COMMA, LPAREN, RPAREN, DOT,
    PRINT, LET, INPUT, DIM, 
    LT, GT, LE, GE, EQ, NE, 
    TRUE, FALSE, 
    IF, THEN, ELSE, ENDIF, ELSEIF,
    FOR, TO, STEP, NEXT, 
    WHILE, ENDWHILE, WEND, 
    DO, UNTIL,
    FUNCTION, ENDFUNCTION, SUB, ENDSUB, RETURN, CALL,
    TYPE, ENDTYPE, AS
};

struct Token {
    TokenType type;
    string val;        // Original text
    double num = 0.0;  // For NUMBER tokens
    int line = 1;      // Line number (for errors)
};
```

---

## Lexer Class

```cpp
class Lexer {
private:
    istream& in;     // Input stream
    char ch = 0;     // Current character
    bool eof = false;
    int line = 1;    // Current line number

    void read();     // Read next character
    void skipWhite();
    void error(const string& msg);

public:
    Lexer(istream& i);
    Token nextToken();  // Get next token
    int getLine() const;
};
```

---

## How It Works

### 1. Character Reading

```cpp
void Lexer::read() {
    if (!in.get(ch)) {
        eof = true;
        ch = 0;
    } else if (ch == '\n') {
        line++;  // Track line numbers
    }
}
```

### 2. Whitespace Skipping

```cpp
void Lexer::skipWhite() {
    while (!eof && isspace(ch)) {
        read();
    }
}
```

### 3. Token Recognition

The `nextToken()` method uses **lookahead** to determine token type:

```cpp
Token Lexer::nextToken() {
    skipWhite();
    int tokenLine = line;  // Capture line for token
    
    if (eof) return {TokenType::END, "", 0.0, tokenLine};
    
    // Try each token type in order...
}
```

---

## Token Recognition Patterns

### Numbers
```cpp
// Digits followed by optional decimal point
if (isdigit(ch)) {
    string s;
    while (!eof && isdigit(ch)) {
        s += ch;
        read();
    }
    if (!eof && ch == '.') {
        s += ch;
        read();
        while (!eof && isdigit(ch)) {
            s += ch;
            read();
        }
    }
    return {TokenType::NUMBER, s, stod(s), tokenLine};
}
```

**Key Point**: Must start with digit (not `.`) to avoid conflict with member access.

### Strings
```cpp
if (ch == '"') {
    read();  // Skip opening quote
    string s;
    while (!eof && ch != '"') {
        s += ch;
        read();
    }
    if (!eof && ch == '"') read();  // Skip closing quote
    return {TokenType::STRING, s, 0.0, tokenLine};
}
```

### Keywords and Identifiers
```cpp
if (isalpha(ch)) {
    string s;
    while (!eof && isalnum(ch)) {
        s += ch;
        read();
    }
    
    // Convert to uppercase for case-insensitive keywords
    string upper = s;
    for (auto& c : upper) c = toupper(c);
    
    // Check keywords
    if (upper == "PRINT") return {TokenType::PRINT, s, 0.0, tokenLine};
    if (upper == "LET") return {TokenType::LET, s, 0.0, tokenLine};
    // ... more keywords ...
    
    // Not a keyword → identifier
    return {TokenType::ID, s, 0.0, tokenLine};
}
```

**Key Feature**: Case-insensitive keywords (`PRINT`, `Print`, `print` all work).

### Operators
```cpp
// Single-character operators
if (ch == '+') { read(); return {TokenType::PLUS, "+", 0.0, tokenLine}; }
if (ch == '-') { read(); return {TokenType::MINUS, "-", 0.0, tokenLine}; }

// Multi-character operators (with lookahead)
if (ch == '=') { 
    read(); 
    if (!eof && ch == '=') {  // ==
        read(); 
        return {TokenType::EQ, "==", 0.0, tokenLine}; 
    }
    return {TokenType::ASSIGN, "=", 0.0, tokenLine};  // =
}

if (ch == '<') {
    read();
    if (!eof && ch == '=') {  // <=
        read(); 
        return {TokenType::LE, "<=", 0.0, tokenLine}; 
    }
    if (!eof && ch == '>') {  // <>
        read(); 
        return {TokenType::NE, "<>", 0.0, tokenLine}; 
    }
    return {TokenType::LT, "<", 0.0, tokenLine};  // <
}
```

**Pattern**: Always consume characters as you recognize them.

---

## Adding a New Token

### Example: Adding `REM` Comments

**Step 1**: Add token type
```cpp
enum class TokenType { 
    // ... existing ...
    REM,  // NEW
};
```

**Step 2**: Add recognition logic
```cpp
// In nextToken(), after checking for keywords:
if (upper == "REM") {
    // Consume rest of line
    while (!eof && ch != '\n') {
        read();
    }
    // Return next token (skip comment)
    return nextToken();
}
```

**Step 3**: Test
```basic
REM This is a comment
PRINT "Hello"
```

Should tokenize as: `PRINT`, `STRING`, `END`

---

## Special Cases

### Case 1: Multi-Word Keywords

```cpp
// Handle "END IF" → ENDIF
if (upper == "END") {
    skipWhite();
    if (!eof && isalpha(ch)) {
        string next;
        while (!eof && isalnum(ch)) {
            next += ch;
            read();
        }
        string nextUpper = next;
        for (auto& c : nextUpper) c = toupper(c);
        
        if (nextUpper == "IF") 
            return {TokenType::ENDIF, "ENDIF", 0.0, tokenLine};
        // Could add END WHILE, END SUB, etc.
    }
}
```

### Case 2: Context-Sensitive Tokens

The `.` can be:
- Part of a number: `3.14`
- Member access: `obj.field`

**Solution**: Only recognize `.` as number start if followed by digit:
```cpp
// Numbers start with digit (NOT '.')
if (isdigit(ch)) {
    // ... parse number ...
}

// Later, as standalone:
if (ch == '.') { 
    read(); 
    return {TokenType::DOT, ".", 0.0, tokenLine}; 
}
```

### Case 3: String Escapes (Not Implemented Yet)

To add escape sequences like `\"`:
```cpp
if (ch == '"') {
    read();
    string s;
    while (!eof && ch != '"') {
        if (ch == '\\') {  // Escape
            read();
            if (ch == 'n') s += '\n';
            else if (ch == 't') s += '\t';
            else if (ch == '"') s += '"';
            else s += ch;  // Unknown escape
            read();
        } else {
            s += ch;
            read();
        }
    }
    // ...
}
```

---

## Error Handling

### Invalid Characters
```cpp
char c = ch;
read();
error("Invalid character '" + string(1, c) + 
      "' at line " + to_string(tokenLine));
```

### Unterminated Strings
```cpp
if (eof) error("Unterminated string at line " + to_string(tokenLine));
```

**Best Practice**: Always include line numbers in errors.

---

## Testing the Lexer

### Manual Test
```bash
echo 'PRINT "Hello", 42' | ./jvmbasic-new
```

### Debugging
Add prints to see tokens:
```cpp
Token Lexer::nextToken() {
    Token t = /* ... recognition logic ... */;
    
    // DEBUG
    cerr << "Token: " << (int)t.type << " '" << t.val 
         << "' line " << t.line << "\n";
    
    return t;
}
```

---

## Common Pitfalls

### 1. **Forgetting to read()**
```cpp
// WRONG
if (ch == '+') return {TokenType::PLUS, "+", 0.0, tokenLine};

// CORRECT
if (ch == '+') { read(); return {TokenType::PLUS, "+", 0.0, tokenLine}; }
```

### 2. **Not Checking EOF**
```cpp
// WRONG
if (ch == '=') {
    read();
    if (ch == '=') { /* ... */ }  // Might be EOF!
}

// CORRECT
if (ch == '=') {
    read();
    if (!eof && ch == '=') { /* ... */ }
}
```

### 3. **Case Sensitivity**
Always convert to uppercase for keyword matching:
```cpp
string upper = s;
for (auto& c : upper) c = toupper(c);
if (upper == "PRINT") /* ... */
```

### 4. **Losing Line Numbers**
```cpp
// Capture line BEFORE reading token
int tokenLine = line;
// ... read token ...
return {TokenType::X, ..., tokenLine};  // Use captured line
```

---

## Performance Notes

- Lexer is **single-pass** (no backtracking)
- **O(n)** where n = source code length
- Character-by-character reading is fast for typical programs
- No need for sophisticated optimization here

---

## Extension Checklist

When adding a new token:
- [ ] Add to `TokenType` enum
- [ ] Add recognition logic in `nextToken()`
- [ ] Handle in parser
- [ ] Add test case
- [ ] Update documentation

---

## Summary

The lexer is simple but critical:
- Converts text → tokens
- Handles keywords (case-insensitive)
- Tracks line numbers for errors
- Single-pass, no backtracking
- Easy to extend

**Next**: Read PARSER_GUIDE.md to see how tokens become AST.

