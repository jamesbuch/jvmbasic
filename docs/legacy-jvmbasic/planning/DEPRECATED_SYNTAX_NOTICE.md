# ⚠️ Deprecated Syntax Notice

**Date**: October 22, 2025  
**Version**: Phase 9 Complete  
**Effective**: Phase 10 (Next Release)

---

## 🚨 IMPORTANT: Old BASIC Syntax Being Removed

Starting with **Phase 10**, the classic all-uppercase BASIC syntax will be **deprecated and then removed**.

---

## What's Changing

### ❌ DEPRECATED (Phase 10 Warnings → Phase 11 Errors)

| Old Syntax | Modern Replacement | Status |
|------------|-------------------|--------|
| `LET x = 10` | `Dim x As Integer = 10` | ⚠️ Deprecated |
| `FUNCTION Add(a, b)` | `Function Add(a As Integer, b As Integer) As Integer` | ⚠️ Deprecated |
| `ENDFUNCTION` | `End Function` | ⚠️ Deprecated |
| `ENDSUB` | `End Sub` | ⚠️ Deprecated |
| `ENDIF` | `End If` | ⚠️ Deprecated |
| `ENDWHILE` | `End While` | ⚠️ Deprecated |
| `ENDTYPE` | `End Type` | ⚠️ Deprecated |
| `ENDCLASS` | `End Class` | ⚠️ Deprecated |

### ✅ KEEPING (Forever)

| Syntax | Reason | Example |
|--------|--------|---------|
| `Print` | Too useful, familiar | `Print "Hello"` |
| Mixed-case | Readability | `Dim`/`DIM`/`dim` all work |
| Case-insensitive | Accessibility | `Function`/`FUNCTION`/`function` |

---

## Migration Timeline

### Phase 9 (Current) ✅
**Status**: Both syntaxes fully supported  
**Action**: None required - all code works

```basic
' Both styles work:
LET x = 10                    ' Old style
Dim y As Integer = 20         ' Modern style
```

### Phase 10 (Next Release) ⚠️
**Status**: Deprecation warnings  
**Action**: Update code to modern syntax

```bash
$ ./jvmbasic < old_program.bas
Warning: Line 5: 'LET' is deprecated, use 'Dim x As Type = value' instead
Warning: Line 12: 'ENDFUNCTION' is deprecated, use 'End Function'
Generated BasicProgram.class
```

### Phase 11 (Future) ❌
**Status**: Old syntax removed  
**Action**: Must use modern syntax

```bash
$ ./jvmbasic < old_program.bas
Error: Line 5: 'LET' keyword not supported (use Dim)
Error: Line 12: Expected 'End Function' but got 'ENDFUNCTION'
```

---

## How to Migrate Your Code

### Step 1: Variable Declarations
**Old**:
```basic
LET x = 10
LET name = "Alice"
LET active = TRUE
```

**Modern**:
```basic
Dim x As Integer = 10
Dim name As String = "Alice"
Dim active As Boolean = True
```

### Step 2: Function Declarations
**Old**:
```basic
FUNCTION Add(a, b)
    RETURN a + b
ENDFUNCTION
```

**Modern**:
```basic
Function Add(a As Single, b As Single) As Single
    Return a + b
End Function
```

### Step 3: Control Flow
**Old**:
```basic
IF x > 0 THEN
    PRINT "Positive"
ENDIF

WHILE x < 10
    LET x = x + 1
ENDWHILE
```

**Modern**:
```basic
If x > 0 Then
    Print "Positive"
End If

While x < 10
    x = x + 1
End While
```

### Step 4: Classes
**Old**:
```basic
CLASS Person
    PUBLIC name AS STRING
END CLASS
```

**Modern** (same, but recommended mixed-case):
```basic
Class Person
    Public name As String
End Class
```

---

## Automated Migration Tool (Planned)

### Phase 10 Will Include: `jvmbasic-modernize`

```bash
# Convert old syntax to modern
./jvmbasic-modernize < old_program.bas > modern_program.bas

# Preview changes
./jvmbasic-modernize --preview old_program.bas

# Batch conversion
./jvmbasic-modernize --directory ./programs/
```

**Features**:
- Automatic keyword conversion
- Type annotation inference
- Whitespace/formatting preservation
- Comment preservation
- Safe transformation (validates before/after)

---

## Why Are We Doing This?

### Problems with Dual Syntax
1. **Confusing**: Two ways to do everything
2. **Maintenance**: Double the parser complexity
3. **Documentation**: Have to show both forms
4. **Learning Curve**: Which style to use?

### Benefits of Modern-Only
1. **Clarity**: One clear way to write code
2. **Professional**: Looks like VB.NET, C#, TypeScript
3. **Tooling**: Better IDE support with typed syntax
4. **Maintainability**: Simpler parser, easier to extend

---

## Backward Compatibility

### Phase 9 Code (Forever Supported)
```basic
' This code will ALWAYS work (modern syntax)
Function Calculate(x As Single) As Single
    Return Math.Sqrt(x)
End Function
```

### Old Branches Preserved
- `phase8-stdlib` - Has old syntax
- `phase7-oop` - Has old syntax
- `main` - Has old syntax

**Recommendation**: Archive old code in Git branches, not in main development

---

## FAQ

### Q: Can I still use PRINT instead of Console.WriteLine?
**A**: Yes! `Print` is staying - it's too useful and familiar.

### Q: Will my Phase 9 code break in Phase 10?
**A**: No! Modern syntax code is safe. Only old uppercase keywords affected.

### Q: When should I migrate?
**A**: Start now! Phase 10 will add warnings, Phase 11 will remove support entirely.

### Q: Can I mix old and modern syntax?
**A**: In Phase 9 (current), yes. In Phase 10+, only modern syntax supported.

### Q: What about case sensitivity?
**A**: All keywords remain case-insensitive! `Dim`/`DIM`/`dim` all work.

---

## Support

### Migration Help
- Read: `docs/USER_GUIDE.md` for modern syntax reference
- Examples: `examples/latest/` for modern syntax examples
- Tool: `jvmbasic-modernize` (coming in Phase 10)
- Forum: GitHub issues for questions

### Breaking Change Policy
- **Major versions** (1.0 → 2.0): Breaking changes allowed
- **Minor versions** (1.0 → 1.1): Deprecations only
- **Patches** (1.0.0 → 1.0.1): Fully backward compatible

---

## Timeline Summary

| Version | Status | Old Syntax | Modern Syntax |
|---------|--------|------------|---------------|
| Phase 9 (current) | ✅ Stable | ✅ Supported | ✅ Supported |
| Phase 10 (next) | 🔄 Planning | ⚠️ Warnings | ✅ Supported |
| Phase 11 (future) | 📋 Planned | ❌ Removed | ✅ Supported |
| v1.0 (release) | 🎯 Goal | ❌ Removed | ✅ Only Option |

---

## Action Required

### For Users
1. **Review** your code for old syntax patterns
2. **Migrate** to modern syntax before Phase 10
3. **Test** with Phase 9 (both syntaxes work)
4. **Update** documentation and examples

### For Developers
1. Remove old syntax examples from `examples/` directory
2. Keep only `examples/latest/` with modern syntax
3. Update all documentation to show modern syntax only
4. Archive old code in Git branches

---

**Notice Date**: October 22, 2025  
**Deprecation**: Phase 10 (estimated December 2025)  
**Removal**: Phase 11 (estimated February 2026)  
**Final**: v1.0 Release (Q2 2026)

**🎯 Conclusion**: JVM BASIC is evolving into a modern professional language. The transition is gradual, well-supported, and will result in cleaner, more maintainable code. Let's embrace the future together! 🚀**

