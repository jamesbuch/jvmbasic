# Loops Implementation Plan

## Overview
Add FOR, WHILE, and DO-WHILE loop support to JVM BASIC.

---

## 1. FOR Loop

### Syntax
```basic
FOR var = start TO end
    <statements>
NEXT var

FOR var = start TO end STEP increment
    <statements>
NEXT var
```

### Examples
```basic
FOR i = 1 TO 10
    PRINT i
NEXT i

FOR x = 0 TO 100 STEP 5
    PRINT x
NEXT x

FOR i = 10 TO 1 STEP -1
    PRINT "Countdown:", i
NEXT i
```

### Semantics
- Loop variable incremented by STEP after each iteration
- Default STEP is 1
- Loop continues while `var <= end` (for positive step) or `var >= end` (for negative step)
- NEXT variable name is optional but recommended for clarity

### Bytecode Pattern
```
<init var = start>
L_top:
<load var>, <load end>, if_icmpgt L_end    # For positive step
<body>
<load var>, <load step>, iadd, <store var>
goto L_top
L_end:
```

### Implementation
- Add tokens: FOR, TO, STEP, NEXT
- Add ForStmt: var, start, end, step (optional), body
- Parse in parseStmt
- Generate proper labels and branches
- Support Float loops too (use fcmpg instead of if_icmpgt)

---

## 2. WHILE Loop

### Syntax
```basic
WHILE condition
    <statements>
ENDWHILE

WHILE condition
    <statements>
WEND
```

### Examples
```basic
LET x = 0
WHILE x < 10
    PRINT x
    LET x = x + 1
ENDWHILE

LET flag = true
WHILE flag
    PRINT "Enter 0 to exit:"
    INPUT x
    IF x == 0 THEN
        LET flag = false
    ENDIF
ENDWHILE
```

### Bytecode Pattern
```
L_top:
<load condition>
ifeq L_end
<body>
goto L_top
L_end:
```

### Implementation
- Add tokens: WHILE, ENDWHILE, WEND
- Add WhileStmt: condition, body
- Very straightforward codegen

---

## 3. DO-WHILE Loop

### Syntax
```basic
DO
    <statements>
WHILE condition

DO
    <statements>
UNTIL condition
```

### Examples
```basic
LET x = 0
DO
    PRINT x
    LET x = x + 1
WHILE x < 10

DO
    PRINT "Enter positive number:"
    INPUT n
UNTIL n > 0
```

### Bytecode Pattern
```
L_top:
<body>
<load condition>
ifne L_top      # WHILE: jump if true
# or
ifeq L_top      # UNTIL: jump if false
```

### Implementation
- Add tokens: DO, UNTIL
- Add DoWhileStmt: condition, body, isUntil (bool)
- Body executes at least once

---

## 4. Loop Control (Future)

### BREAK (EXIT FOR / EXIT WHILE)
```basic
FOR i = 1 TO 100
    IF i == 50 THEN
        EXIT FOR
    ENDIF
NEXT i
```

### CONTINUE
```basic
FOR i = 1 TO 10
    IF i == 5 THEN
        CONTINUE
    ENDIF
    PRINT i
NEXT i
```

Not implementing this session, but planned for future.

---

## Implementation Steps

### FOR Loop
1. Add tokens: FOR, TO, STEP, NEXT
2. Add ForStmt AST node
3. Parse FOR statements
4. Generate bytecode with proper step handling
5. Test positive/negative/float steps

### WHILE Loop
1. Add tokens: WHILE, ENDWHILE, WEND
2. Add WhileStmt AST node
3. Parse WHILE statements  
4. Generate bytecode
5. Test various conditions

### DO-WHILE Loop
1. Add tokens: DO, UNTIL
2. Add DoWhileStmt AST node
3. Parse DO...WHILE/UNTIL statements
4. Generate bytecode
5. Test both WHILE and UNTIL variants

---

## Testing Strategy

### Test 1: Basic FOR loop
```basic
FOR i = 1 TO 5
    PRINT i
NEXT i
```
Expected: 1 2 3 4 5

### Test 2: FOR with STEP
```basic
FOR i = 0 TO 10 STEP 2
    PRINT i
NEXT i
```
Expected: 0 2 4 6 8 10

### Test 3: Countdown
```basic
FOR i = 5 TO 1 STEP -1
    PRINT i
NEXT i
```
Expected: 5 4 3 2 1

### Test 4: FOR with arrays
```basic
DIM arr(5) = 0
FOR i = 0 TO 4
    LET arr(i) = i * 10
NEXT i

FOR i = 0 TO 4
    PRINT arr(i)
NEXT i
```

### Test 5: WHILE loop
```basic
LET x = 0
WHILE x < 5
    PRINT x
    LET x = x + 1
ENDWHILE
```

### Test 6: DO-WHILE
```basic
LET x = 0
DO
    PRINT x
    LET x = x + 1
WHILE x < 5
```

### Test 7: Nested loops
```basic
FOR i = 1 TO 3
    FOR j = 1 TO 3
        PRINT i, "*", j, "=", i * j
    NEXT j
NEXT i
```

### Test 8: Loops with functions
```basic
DIM nums(10) = 0
FOR i = 0 TO 9
    LET nums(i) = RND * 100
NEXT i

FOR i = 0 TO 9
    PRINT INT(nums(i))
NEXT i
```

---

## Edge Cases to Handle

1. **STEP of 0**: Infinite loop (document as undefined behavior)
2. **Negative STEP with TO**: Should count down
3. **Start > End with positive STEP**: Loop doesn't execute
4. **Float loop variables**: Support for numeric loops
5. **Nested loops**: Same variable name (use different locals)
6. **NEXT variable mismatch**: Optional validation

---

## Design Decisions

### FOR Loop Behavior
**Option A**: Always check at loop start (may not execute at all)
```
if var > end goto L_end
L_top:
<body>
var += step
if var <= end goto L_top
L_end:
```

**Option B**: Check at loop end (executes at least once)
```
L_top:
<body>
var += step
if var <= end goto L_top
```

**Recommended: Option A** - Standard behavior, loop may skip entirely

### NEXT Variable Name
**Option A**: Required and must match
**Option B**: Optional
**Option C**: Ignored (parsed but not checked)

**Recommended: Option C** - Simple, flexible, BASIC tradition varies

---

## Implementation Order

1. FOR loop (most common)
2. WHILE loop (straightforward)
3. DO-WHILE loop (variant of WHILE)
4. Tests for all three
5. Nested loop tests
6. Commit and merge

---

## After Loops

Once loops are done, we'll add:
- **Sorting algorithms**: SORT() function for arrays
- **Search**: FIND(), BSEARCH() for arrays
- **Array utilities**: REVERSE(), FILL(), COPY()
- Add to wishlist: User-defined types (STRUCT/TYPE)
- Add to wishlist: User-defined functions


