# Namespace/OO Syntax Implementation Plan

**Goal**: Support `Console.WriteLine()`, `Math.Sin()`, `str.ToUpper()` syntax

---

## Strategy

### Two Types of Dot Syntax

1. **Namespace Calls** - Static methods on namespaces
   ```basic
   Console.WriteLine("text")
   Math.Sin(angle)
   File.Exists("file.txt")
   ```

2. **Instance Methods** - Methods on variable instances  
   ```basic
   Dim text As String = "hello"
   Dim upper = text.ToUpper()
   Dim len = text.Length()
   ```

---

## Parser Implementation

### Detection Logic

When we see: `IDENTIFIER DOT IDENTIFIER LPAREN`

Check if first identifier is:
- A known namespace? → Namespace call
- A variable? → Instance method call
- Unknown? → Error

### Known Namespaces
- Console
- Math  
- File
- Http
- Json
- Xml
- Db

---

## Implementation Approach

### Phase 1: Parser Support
1. Modify `parsePrimary()` to detect namespace syntax
2. Create new AST node type: `NamespaceCall`
3. Store namespace name + method name + args

### Phase 2: Code Generation
1. For namespace calls: `invokestatic basicrt/BasicRuntime.method`
2. For instance calls: Call appropriate runtime helper

### Phase 3: Runtime Functions
1. Add namespace methods to BasicRuntime.java
2. Implement all Console, Math, File, Http, Json, Xml, Db methods

---

## Start Implementation

Let's begin!

