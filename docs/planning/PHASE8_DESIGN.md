# Phase 8 Design: Comprehensive Standard Library Expansion

**Date**: October 18, 2025  
**Branch**: phase8-stdlib  
**Goal**: Build a massive, professional-grade standard library for JVM BASIC

---

## 🎯 Vision

By the end of Phase 8, JVM BASIC should have a standard library extensive enough to **write a JVM BASIC compiler in JVM BASIC itself**. This requires:

1. **Extensive string manipulation** - parsing, tokenization
2. **Collections** - dynamic lists, maps, stacks, queues, sets
3. **File I/O** - character-by-character reading, binary I/O
4. **Date/Time** - full calendar and timing support
5. **Networking** - HTTP requests, socket I/O
6. **Data formats** - JSON, XML parsing and generation
7. **System utilities** - environment variables, command execution

---

## 📋 Current State Analysis

### What We Have (93 Functions)

**Math (36 functions)**:
- Trigonometry: SIN, COS, TAN, ASIN, ACOS, ATAN, ATAN2
- Powers/logs: POW, EXP, LOG, LOG10, SQR/SQRT
- Rounding: ROUND, CEIL, FLOOR, INT
- Utility: ABS, SGN, MIN, MAX
- Random: RND, RNDI, RNDINT
- Constants: PI, E

**String (24 functions)**:
- Basic: LEN, LEFT, RIGHT, MID/SUBSTR
- Case: UPPER/UCASE, LOWER/LCASE
- Trim: TRIM, LTRIM, RTRIM
- Search: INSTR, CONTAINS
- Transform: REVERSE, SPACE, STRING
- Character: ASC, CHR
- Conversion: VAL, STR
- Type check: ISNUM, ISINT
- Format: FORMAT, FORMATF, FORMATI

**Array (8 functions)**:
- Aggregate: MINARRAY, MAXARRAY, SUMARRAY
- Utility: UBOUND
- In-place: SORT (not in builtin registry), REVERSE, FILL

**File I/O (8 functions)**:
- Open: OPENINPUT, OPENOUTPUT
- Read/Write: READLINE, WRITELINE, WRITETEXT
- Control: CLOSEFILE
- Management: FILEEXISTS, DELETEFILE

**Regex (4 functions)**:
- REGEXMATCH, REGEXFIND, REGEXREPLACE, REGEXGROUP

**Enhanced (13 functions via BasicRuntime.java)**:
- split, join (not in registry)
- Array operations (sort_ia, reverse_ia, fill_ia, etc.)

---

## 🚨 Critical Issues to Fix First

### 1. File I/O Type Confusion ⚠️

**Problem**: Documentation shows comparisons like `IF handle >= 0.0` but handles are INT!

**Files to fix**:
- `docs/USER_GUIDE.md` - All file I/O examples use `>= 0.0`
- `docs/planning/PHASE7_DESIGN.md` - Examples use Float comparisons
- Any other documentation

**Fix**: Change all to `IF handle >= 0 THEN`

### 2. Void Functions (SUB calls) ⚠️

**Problem**: 
```basic
LET dummy = CLOSEFILE(handle)  ' Wasteful!
CALL WRITELINE(handle, text)   ' Should this be SUB or FUNCTION?
```

**Current behavior**:
- CLOSEFILE, WRITELINE, WRITETEXT return INT (always 0)
- Users do `LET dummy = ...` which is ugly
- No true "void" functions

**Solution Options**:

**Option A**: Make them true SUBs (procedural, no return)
- Pro: Clean syntax `CALL CLOSEFILE(handle)`
- Con: Breaks existing code if anyone uses return value
- Con: Need to distinguish FUNCTION vs SUB in builtin registry

**Option B**: Keep as-is, document as best practice
- Pro: No breaking changes
- Con: Ugly `LET dummy = ...` pattern continues

**Option C**: Allow ignoring return values
- Pro: Can call as `CALL CLOSEFILE(handle)` OR `LET x = CLOSEFILE(handle)`
- Con: Need parser/semantic changes

**Recommendation**: Option C - Allow CALL statement to invoke FUNCTIONs and ignore returns

### 3. Missing Array Functions in Registry

BasicRuntime.java has these but they're not in builtin_functions.cpp:
- SPLIT (returns String[])
- JOIN (takes String[])
- Array SORT, REVERSE, FILL are void (in-place)

**Fix**: Add to registry or keep as future enhancement

---

## 🎯 Phase 8 Standard Library Plan

### Priority 1: String Functions (HIGH IMPACT)

**Missing critical functions**:
```basic
' String manipulation
REPLACE(text, oldStr, newStr) -> String
REPLACEALL(text, pattern, newStr) -> String  ' Regex version
STARTSWITH(text, prefix) -> Bool
ENDSWITH(text, suffix) -> Bool
INDEXOF(text, search) -> Int  ' Alias for INSTR
LASTINDEXOF(text, search) -> Int
SUBSTRING(text, start) -> String  ' To end
SUBSTRING(text, start, length) -> String  ' Overload

' String building
CONCAT(str1, str2) -> String
CONCAT3(str1, str2, str3) -> String
REPEAT(text, count) -> String
PADLEFT(text, width) -> String
PADLEFT(text, width, char) -> String
PADRIGHT(text, width) -> String
PADRIGHT(text, width, char) -> String

' String parsing (for writing compiler!)
SPLIT(text, delimiter) -> String[]  ' Already in BasicRuntime!
JOIN(arr, delimiter) -> String      ' Already in BasicRuntime!
SPLITLINES(text) -> String[]
CHAR(text, index) -> String  ' Get char at position
CHARAT(text, index) -> Int   ' Get ASCII at position

' String comparison
STRCMP(s1, s2) -> Int  ' -1, 0, 1
STRICMP(s1, s2) -> Int  ' Case insensitive
EQUALS(s1, s2) -> Bool
EQUALSIGNORECASE(s1, s2) -> Bool
```

### Priority 2: Date/Time Support (ESSENTIAL)

```basic
' Current date/time
NOW() -> Long  ' Milliseconds since epoch
DATE() -> String  ' "2025-10-18"
TIME() -> String  ' "14:30:45"
DATETIME() -> String  ' "2025-10-18 14:30:45"

' Date components
YEAR(millis) -> Int
MONTH(millis) -> Int  ' 1-12
DAY(millis) -> Int
DAYOFWEEK(millis) -> Int  ' 0-6, 0=Sunday
DAYOFYEAR(millis) -> Int  ' 1-366
HOUR(millis) -> Int
MINUTE(millis) -> Int
SECOND(millis) -> Int
MILLISECOND(millis) -> Int

' Date arithmetic
DATEADD(millis, days) -> Long
DATEDIFF(millis1, millis2) -> Int  ' Days difference
ADDYEARS(millis, years) -> Long
ADDMONTHS(millis, months) -> Long
ADDDAYS(millis, days) -> Long
ADDHOURS(millis, hours) -> Long
ADDMINUTES(millis, minutes) -> Long
ADDSECONDS(millis, seconds) -> Long

' Date parsing/formatting
PARSEDATE(dateStr) -> Long
FORMATDATE(millis, format) -> String  ' e.g. "yyyy-MM-dd"
FORMATTIME(millis, format) -> String
```

### Priority 3: Timing/Clock Functions (PERFORMANCE)

```basic
' Timing
TIMER() -> Float  ' Seconds since midnight
MILLISECONDS() -> Long  ' System.currentTimeMillis()
NANOSECONDS() -> Long   ' System.nanoTime()
SLEEP(milliseconds) -> Void  ' Thread.sleep()

' Stopwatch pattern
LET start = NANOSECONDS()
' ... do work ...
LET elapsed = NANOSECONDS() - start
PRINT "Took "; elapsed; " nanoseconds"
```

### Priority 4: Character I/O (COMPILER NEEDS)

```basic
' Single character operations
READCHAR(handle) -> Int  ' Read one char as ASCII, -1 on EOF
WRITECHAR(handle, charCode) -> Void
PEEKCHAR(handle) -> Int  ' Look ahead without consuming
HASMORE(handle) -> Bool  ' Check if more data available

' Console character I/O
GETCH() -> Int  ' Read char from console (no echo)
GETCHE() -> Int  ' Read char from console (with echo)
KEYPRESSED() -> Bool  ' Check if key waiting
```

### Priority 5: Advanced File I/O

```basic
' File operations
FILESIZE(filename) -> Long
RENAME(oldName, newName) -> Bool
COPY(source, dest) -> Bool
MOVE(source, dest) -> Bool
ISFILE(path) -> Bool
ISDIR(path) -> Bool

' Directory operations
MKDIR(path) -> Bool
RMDIR(path) -> Bool
LISTDIR(path) -> String[]  ' List directory contents
CURRENTDIR() -> String
CHANGEDIR(path) -> Bool

' Binary I/O
OPENBINARY(filename) -> Int
READBYTE(handle) -> Int
WRITEBYTE(handle, value) -> Void
READBYTES(handle, count) -> Int[]
WRITEBYTES(handle, bytes) -> Void

' Stream operations
FLUSH(handle) -> Void
SEEK(handle, position) -> Bool
TELL(handle) -> Long  ' Current position
ISEOF(handle) -> Bool
```

### Priority 6: Collections (GAME CHANGER!)

This is the big one - need design for generic types.

**Simple approach (no generics yet)**:
```basic
' IntList - dynamic integer array
INTLIST_NEW() -> Object
INTLIST_ADD(list, value) -> Void
INTLIST_GET(list, index) -> Int
INTLIST_SET(list, index, value) -> Void
INTLIST_SIZE(list) -> Int
INTLIST_REMOVE(list, index) -> Void
INTLIST_CONTAINS(list, value) -> Bool
INTLIST_INDEXOF(list, value) -> Int
INTLIST_CLEAR(list) -> Void
INTLIST_TOARRAY(list) -> Int[]

' StringList
STRINGLIST_NEW() -> Object
STRINGLIST_ADD(list, value) -> Void
... etc

' Map (String -> String)
MAP_NEW() -> Object
MAP_PUT(map, key, value) -> Void
MAP_GET(map, key) -> String
MAP_CONTAINSKEY(map, key) -> Bool
MAP_REMOVE(map, key) -> Void
MAP_SIZE(map) -> Int
MAP_KEYS(map) -> String[]

' Stack
STACK_NEW() -> Object
STACK_PUSH(stack, value) -> Void
STACK_POP(stack) -> String
STACK_PEEK(stack) -> String
STACK_ISEMPTY(stack) -> Bool
STACK_SIZE(stack) -> Int

' Queue
QUEUE_NEW() -> Object
QUEUE_ENQUEUE(queue, value) -> Void
QUEUE_DEQUEUE(queue) -> String
QUEUE_PEEK(queue) -> String
QUEUE_ISEMPTY(queue) -> Bool
QUEUE_SIZE(queue) -> Int
```

**With generics (future)**:
```basic
DIM list AS List<Int>
LET list = NEW List<Int>()
CALL list.Add(42)
PRINT list.Get(0)
```

### Priority 7: JSON Support (DATA INTERCHANGE)

```basic
' JSON parsing
JSONPARSE(text) -> Object  ' Parse JSON string
JSONGET(obj, path) -> String  ' Get value by path "data.items[0].name"
JSONGETINT(obj, path) -> Int
JSONGETFLOAT(obj, path) -> Float
JSONGETBOOL(obj, path) -> Bool
JSONGETARRAY(obj, path) -> Object
JSONGETOBJECT(obj, path) -> Object

' JSON generation
JSONNEW() -> Object
JSONPUT(obj, key, value) -> Void
JSONPUTINT(obj, key, value) -> Void
JSONPUTFLOAT(obj, key, value) -> Void
JSONPUTBOOL(obj, key, value) -> Void
JSONPUTARRAY(obj, key, arr) -> Void
JSONPUTOBJECT(obj, key, obj2) -> Void
JSONTOSTRING(obj) -> String
JSONTOSTRING(obj, pretty) -> String  ' Pretty print
```

### Priority 8: XML Support (LEGACY COMPATIBILITY)

```basic
' XML parsing
XMLPARSE(text) -> Object
XMLGET(doc, xpath) -> String
XMLGETALL(doc, xpath) -> String[]
XMLGETATTR(node, attrName) -> String

' XML generation
XMLNEW(rootTag) -> Object
XMLADDCHILD(parent, tagName) -> Object
XMLSETTEXT(node, text) -> Void
XMLSETATTR(node, attrName, value) -> Void
XMLTOSTRING(doc) -> String
XMLTOSTRING(doc, pretty) -> String
```

### Priority 9: Networking (INTERNET ACCESS)

```basic
' HTTP client
HTTPGET(url) -> String  ' Simple GET
HTTPPOST(url, data) -> String
HTTPHEAD(url) -> String
HTTPSTATUS(url) -> Int  ' Get status code

' URL utilities
URLENCODE(text) -> String
URLDECODE(text) -> String
URLPARSE(url) -> Object  ' Parse URL into components

' Socket support (advanced)
SOCKCONNECT(host, port) -> Int  ' Returns handle
SOCKSEND(handle, data) -> Bool
SOCKRECV(handle) -> String
SOCKRECVLINE(handle) -> String
SOCKCLOSE(handle) -> Void
```

### Priority 10: System Utilities

```basic
' Environment
GETENV(name) -> String  ' Get environment variable
SETENV(name, value) -> Bool  ' Set environment variable
ENVLIST() -> String[]  ' List all env vars

' Process execution
EXEC(command) -> Int  ' Execute and return exit code
EXECOUTPUT(command) -> String  ' Execute and capture output
SYSTEM(command) -> Int  ' Alias for EXEC

' System info
USERNAME() -> String
HOSTNAME() -> String
OSNAME() -> String
OSVERSION() -> String
JAVAVERSION() -> String
WORKINGDIR() -> String
HOMEDIR() -> String
TEMPDIR() -> String

' Memory/performance
FREEMEMORY() -> Long
TOTALMEMORY() -> Long
MAXMEMORY() -> Long
RUNGC() -> Void  ' Force garbage collection
```

### Priority 11: Advanced Math

```basic
' More math functions
SINH(x) -> Float  ' Hyperbolic functions
COSH(x) -> Float
TANH(x) -> Float
CBRT(x) -> Float  ' Cube root
HYPOT(x, y) -> Float  ' sqrt(x^2 + y^2)
DEGREES(radians) -> Float
RADIANS(degrees) -> Float

' Statistical
MEAN(arr) -> Float
MEDIAN(arr) -> Float
MODE(arr) -> Float
STDEV(arr) -> Float  ' Standard deviation
VARIANCE(arr) -> Float
CORRELATION(arr1, arr2) -> Float

' Bitwise operations
BITAND(a, b) -> Int
BITOR(a, b) -> Int
BITXOR(a, b) -> Int
BITNOT(a) -> Int
BITSHIFT(value, positions) -> Int  ' Positive = left, negative = right
```

---

## 🏗️ Implementation Strategy

### Phase 8.1: Foundation (Week 1)
1. ✅ Fix file I/O documentation (handle >= 0, not >= 0.0)
2. ✅ Add CALL support for ignoring function returns
3. ✅ Add 20+ essential string functions
4. ✅ Add complete date/time support
5. ✅ Add timing/clock functions
6. ✅ Add character I/O

### Phase 8.2: Collections (Week 2)
1. Design collections API (IntList, StringList, Map, Stack, Queue)
2. Implement in BasicRuntime.java
3. Add to builtin_functions registry
4. Write comprehensive tests
5. Document usage patterns

### Phase 8.3: Data Formats (Week 3)
1. Add JSON support (parsing + generation)
2. Add XML support (parsing + generation)
3. Add CSV support
4. Write parser tests with real data

### Phase 8.4: Networking (Week 4)
1. Add HTTP client functions
2. Add socket support
3. Add URL utilities
4. Write network tests

### Phase 8.5: System Integration (Week 5)
1. Add environment variable access
2. Add process execution
3. Add system info functions
4. Add advanced file operations

### Phase 8.6: Polish (Week 6)
1. Add advanced math/statistics
2. Add bitwise operations
3. Complete documentation
4. Write showcase programs

---

## 📊 Target: 300+ Standard Library Functions

Current: 93 functions  
Target: 300+ functions  
To add: 207+ functions

### Breakdown:
- String: +30 functions (24 → 54)
- Date/Time: +25 functions (0 → 25)
- Timing: +4 functions (0 → 4)
- Character I/O: +6 functions (0 → 6)
- File I/O: +20 functions (8 → 28)
- Collections: +45 functions (0 → 45)
- JSON: +15 functions (0 → 15)
- XML: +10 functions (0 → 10)
- Networking: +12 functions (0 → 12)
- System: +20 functions (0 → 20)
- Math: +20 functions (36 → 56)

**Total: ~207 new functions → ~300 total**

---

## 🧪 Testing Strategy

Each function category needs:
1. **Unit tests** - Individual function tests
2. **Integration tests** - Combined usage
3. **Real-world examples** - Practical programs
4. **Performance tests** - Benchmark critical operations

Example test files:
- `test_string_advanced.bas` - All new string functions
- `test_datetime.bas` - Date/time operations
- `test_collections.bas` - List, Map, Stack, Queue
- `test_json.bas` - JSON parsing and generation
- `test_networking.bas` - HTTP requests
- `examples/web_scraper.bas` - HTTP + JSON + regex
- `examples/csv_processor.bas` - File I/O + string parsing
- `examples/mini_compiler.bas` - Collections + file I/O + string parsing

---

## 🎯 Success Criteria

Phase 8 is complete when:

1. ✅ 300+ built-in functions available
2. ✅ All tests passing (aim for 100+ test files)
3. ✅ Complete documentation with examples
4. ✅ Can write non-trivial programs:
   - Web scraper (HTTP + JSON + regex)
   - CSV processor (file I/O + string manipulation)
   - Simple compiler/interpreter (collections + parsing)
   - Data analyzer (statistics + file I/O)
5. ✅ Performance acceptable (no major regressions)
6. ✅ All examples run correctly

---

## 🚀 Let's Build This!

JVM BASIC is about to become a **serious, production-ready language** with a standard library that rivals Python, Ruby, or any modern scripting language.

By Phase 8 completion, you'll be able to:
- Write web applications
- Parse and generate JSON/XML
- Process large data files
- Build command-line tools
- Create network clients/servers
- And yes... **write a compiler in JVM BASIC!**

---

**Next Steps**: Start with Phase 8.1 - Foundation functions.

