# Stack Map Frame Analysis

## What Are Stack Map Frames?

Stack map frames are metadata that tells the JVM verifier the state of the operand stack and local variables at specific points in bytecode (branch targets, exception handlers).

**Required since**: Java 7+ (class file version 50+)

## Example: Simple IF Statement

### Java Code:
```java
int x = 10;
if (x > 5) {
    System.out.println("large");
} else {
    System.out.println("small");
}
System.out.println("done");
```

### Bytecode with Stack Map:
```
0: bipush 10              // push 10
2: istore_1               // x = 10
3: iload_1                // load x
4: iconst_5               // push 5
5: if_icmple 19           // if x <= 5 goto 19 (else branch)
8: getstatic System.out   // THEN branch
11: ldc "large"
13: invokevirtual println
16: goto 27               // skip else, goto done
19: getstatic System.out  // ELSE branch (TARGET!)
22: ldc "small"
24: invokevirtual println
27: getstatic System.out  // done (TARGET!)
30: ldc "done"
32: invokevirtual println
35: return

StackMapTable: number_of_entries = 2
  frame_type = 252 /* append */
    offset_delta = 19      // at bytecode offset 19
    locals = [ int ]       // local var 1 is int
  frame_type = 7 /* same */
    offset_delta = 27      // at bytecode offset 27 (relative)
```

## Key Insights

### 1. When Stack Maps Are Needed
- At **branch targets** (where jumps land)
- At **exception handlers**
- NOT at fall-through code

### 2. Frame Types
- **252 (append)**: Add local variables since last frame
- **7 (same)**: Same locals and stack as previous frame
- **255 (full_frame)**: Complete frame specification

### 3. Our Current Code
**Problem**: We generate jumps but NO StackMapTable attribute

**Why it works for small programs**: JVM verifier is lenient for simple control flow

**Why it fails for large programs**: Complex branches require explicit stack maps

## Solution Approaches

### Option A: Add StackMapTable Attribute ✅ **CORRECT**
Generate stack map frames during codegen:
1. Track branch targets
2. Compute stack/locals state at each target
3. Emit StackMapTable attribute

**Effort**: 16-20 hours
**Complexity**: High (need to track data flow)

### Option B: Use Java 6 Compatibility ⚠️ **WORKAROUND**
Set class file version to 49 (Java 5/6):
- No stack maps required
- Works with modern JVM
- **Drawback**: Considered legacy

**Effort**: 5 minutes
**Complexity**: Trivial (change one number)

### Option C: Use ASM or Similar ❌ **OVERKILL**
Use bytecode library that computes frames automatically

**Effort**: Days (rewrite codegen)
**Complexity**: Very high

## Recommendation

**Phase 1** (Now): Use Option B (Java 6 compat) to unblock large programs  
**Phase 2** (Later): Implement Option A properly for modern JVM

This gets users working immediately while we architect the proper solution.

## Implementation (Option B)

Change class file version from 52 (Java 8) to 49 (Java 5):

```cpp
// In ClassFile::write()
// OLD:
write_u2(52);  // minor version
write_u2(0);   // major version 52 = Java 8

// NEW:
write_u2(0);   // minor version  
write_u2(49);  // major version 49 = Java 5/6 (no stack maps required)
```

**That's it!** One line change.

## Testing Plan

1. Change version to 49
2. Rebuild
3. Test comprehensive program
4. Verify it runs without VerifyError
5. Confirm all existing tests still pass

**Expected result**: Large programs now work!

