# Collections Design for JVM BASIC

**Date**: October 19, 2025  
**Phase**: 8.2  
**Goal**: Add dynamic collections (Lists, Maps, Stacks, Queues)

---

## Design Philosophy

### Approach: Java Collection Wrappers
- Use Java's built-in collections (ArrayList, HashMap, Stack, LinkedList)
- Wrap them with BASIC-friendly function interfaces
- Store collections as Object references
- Type-specific variants for common types (Int, String, Float)

### Why Not Generic Syntax?
JVM BASIC doesn't have:
- Generic type parameters (`List<Int>`)
- Object-oriented method calls on return values
- Type inference for variables

Therefore, we use **function-based API** with type-specific names:
- `INTLIST_*` for integer lists
- `STRINGLIST_*` for string lists  
- `MAP_*` for string-to-string maps
- etc.

---

## Collections API

### IntList - Dynamic Integer Array

```basic
REM Create and use an integer list
LET list = INTLIST_NEW()
CALL INTLIST_ADD(list, 10)
CALL INTLIST_ADD(list, 20)
CALL INTLIST_ADD(list, 30)

LET size = INTLIST_SIZE(list)
PRINT "List size: "; size

LET value = INTLIST_GET(list, 1)
PRINT "Value at index 1: "; value

CALL INTLIST_SET(list, 1, 99)
CALL INTLIST_REMOVE(list, 0)

IF INTLIST_CONTAINS(list, 30) THEN
    PRINT "List contains 30"
ENDIF

LET arr = INTLIST_TOARRAY(list)
REM Now arr is a regular Int array
```

**Functions**:
- `INTLIST_NEW()` → Object
- `INTLIST_ADD(list, value)` → Int (returns size)
- `INTLIST_GET(list, index)` → Int
- `INTLIST_SET(list, index, value)` → Int (returns old value)
- `INTLIST_SIZE(list)` → Int
- `INTLIST_REMOVE(list, index)` → Int (returns removed value)
- `INTLIST_CONTAINS(list, value)` → Bool
- `INTLIST_INDEXOF(list, value)` → Int (-1 if not found)
- `INTLIST_CLEAR(list)` → Int (returns 0)
- `INTLIST_TOARRAY(list)` → Int[]

### StringList - Dynamic String Array

```basic
LET names = STRINGLIST_NEW()
CALL STRINGLIST_ADD(names, "Alice")
CALL STRINGLIST_ADD(names, "Bob")
CALL STRINGLIST_ADD(names, "Charlie")

LET name = STRINGLIST_GET(names, 0)
PRINT "First name: "; name

FOR i = 0 TO STRINGLIST_SIZE(names) - 1
    PRINT STRINGLIST_GET(names, i)
NEXT i
```

**Functions**: Same as IntList but with String types

### FloatList - Dynamic Float Array

**Functions**: Same as IntList but with Float types

### Map - String Key-Value Pairs

```basic
REM Create a map (dictionary)
LET map = MAP_NEW()
CALL MAP_PUT(map, "name", "John")
CALL MAP_PUT(map, "age", "30")
CALL MAP_PUT(map, "city", "Boston")

LET name = MAP_GET(map, "name")
PRINT "Name: "; name

IF MAP_CONTAINSKEY(map, "age") THEN
    PRINT "Age is stored"
ENDIF

LET size = MAP_SIZE(map)
PRINT "Map has "; size; " entries"

LET keys = MAP_KEYS(map)
REM keys is a String array
```

**Functions**:
- `MAP_NEW()` → Object
- `MAP_PUT(map, key, value)` → String (returns old value or "")
- `MAP_GET(map, key)` → String (returns "" if not found)
- `MAP_CONTAINSKEY(map, key)` → Bool
- `MAP_REMOVE(map, key)` → String (returns removed value)
- `MAP_SIZE(map)` → Int
- `MAP_CLEAR(map)` → Int
- `MAP_KEYS(map)` → String[]
- `MAP_VALUES(map)` → String[]

### Stack - LIFO (Last In First Out)

```basic
LET stack = STACK_NEW()
CALL STACK_PUSH(stack, "First")
CALL STACK_PUSH(stack, "Second")
CALL STACK_PUSH(stack, "Third")

LET top = STACK_PEEK(stack)
PRINT "Top: "; top

LET popped = STACK_POP(stack)
PRINT "Popped: "; popped

IF STACK_ISEMPTY(stack) THEN
    PRINT "Stack is empty"
ELSE
    PRINT "Stack has "; STACK_SIZE(stack); " items"
ENDIF
```

**Functions**:
- `STACK_NEW()` → Object
- `STACK_PUSH(stack, value)` → Int (returns new size)
- `STACK_POP(stack)` → String (returns "" if empty)
- `STACK_PEEK(stack)` → String (returns "" if empty)
- `STACK_ISEMPTY(stack)` → Bool
- `STACK_SIZE(stack)` → Int
- `STACK_CLEAR(stack)` → Int

### Queue - FIFO (First In First Out)

```basic
LET queue = QUEUE_NEW()
CALL QUEUE_ENQUEUE(queue, "First")
CALL QUEUE_ENQUEUE(queue, "Second")
CALL QUEUE_ENQUEUE(queue, "Third")

LET front = QUEUE_PEEK(queue)
PRINT "Front: "; front

LET dequeued = QUEUE_DEQUEUE(queue)
PRINT "Dequeued: "; dequeued

LET size = QUEUE_SIZE(queue)
PRINT "Queue has "; size; " items"
```

**Functions**:
- `QUEUE_NEW()` → Object
- `QUEUE_ENQUEUE(queue, value)` → Int (returns new size)
- `QUEUE_DEQUEUE(queue)` → String (returns "" if empty)
- `QUEUE_PEEK(queue)` → String (returns "" if empty)
- `QUEUE_ISEMPTY(queue)` → Bool
- `QUEUE_SIZE(queue)` → Int
- `QUEUE_CLEAR(queue)` → Int

---

## Implementation Strategy

### Phase 1: IntList and StringList
Most commonly needed, implement these first.

### Phase 2: Map
Critical for many use cases (configuration, lookups, etc.)

### Phase 3: Stack and Queue
Useful for algorithms, less critical than above

### Phase 4: FloatList
Nice to have, implement if time permits

---

## Technical Implementation

### In BasicRuntime.java

```java
// IntList implementation
private static Map<Integer, java.util.ArrayList<Integer>> intLists = 
    new HashMap<>();
private static int nextIntListId = 1;

public static int intListNew() {
    int id = nextIntListId++;
    intLists.put(id, new java.util.ArrayList<Integer>());
    return id;
}

public static int intListAdd(int listId, int value) {
    java.util.ArrayList<Integer> list = intLists.get(listId);
    if (list == null) return -1;
    list.add(value);
    return list.size();
}

public static int intListGet(int listId, int index) {
    java.util.ArrayList<Integer> list = intLists.get(listId);
    if (list == null || index < 0 || index >= list.size()) return 0;
    return list.get(index);
}

// ... similar for other operations
```

### Type System Considerations

JVM BASIC currently only has:
- Int, Float, String, Bool
- IntArray, FloatArray, StringArray, BoolArray
- UserDefined (structs)

Collections return **Int** handles (IDs) that map to Java objects internally.
- Lists: Int ID → ArrayList
- Maps: Int ID → HashMap
- Stacks/Queues: Int ID → Stack/LinkedList

---

## Function Count

### IntList: 10 functions
### StringList: 10 functions
### FloatList: 10 functions
### Map: 9 functions
### Stack: 7 functions
### Queue: 7 functions

**Total**: 53 new collection functions

**New Grand Total**: 157 + 53 = 210 built-in functions!

---

## Testing Strategy

Create comprehensive tests:
- `test_intlist.bas` - All IntList operations
- `test_stringlist.bas` - All StringList operations
- `test_map.bas` - All Map operations
- `test_stack.bas` - Stack operations
- `test_queue.bas` - Queue operations
- `test_collections_combined.bas` - Real-world usage

---

## Example Use Cases

### 1. Shopping Cart (IntList + StringList)
```basic
LET productIds = INTLIST_NEW()
LET productNames = STRINGLIST_NEW()
LET quantities = INTLIST_NEW()

CALL INTLIST_ADD(productIds, 101)
CALL STRINGLIST_ADD(productNames, "Widget")
CALL INTLIST_ADD(quantities, 5)

FOR i = 0 TO INTLIST_SIZE(productIds) - 1
    LET id = INTLIST_GET(productIds, i)
    LET name = STRINGLIST_GET(productNames, i)
    LET qty = INTLIST_GET(quantities, i)
    PRINT id; ": "; name; " x"; qty
NEXT i
```

### 2. Configuration Manager (Map)
```basic
LET config = MAP_NEW()
CALL MAP_PUT(config, "server", "localhost")
CALL MAP_PUT(config, "port", "8080")
CALL MAP_PUT(config, "debug", "true")

LET server = MAP_GET(config, "server")
LET port = MAP_GET(config, "port")
PRINT "Connecting to "; server; ":"; port
```

### 3. Undo/Redo Stack
```basic
LET undoStack = STACK_NEW()
CALL STACK_PUSH(undoStack, "action1")
CALL STACK_PUSH(undoStack, "action2")
CALL STACK_PUSH(undoStack, "action3")

REM Undo
LET action = STACK_POP(undoStack)
PRINT "Undoing: "; action
```

### 4. Task Queue
```basic
LET tasks = QUEUE_NEW()
CALL QUEUE_ENQUEUE(tasks, "Process order 1")
CALL QUEUE_ENQUEUE(tasks, "Process order 2")
CALL QUEUE_ENQUEUE(tasks, "Process order 3")

WHILE NOT QUEUE_ISEMPTY(tasks)
    LET task = QUEUE_DEQUEUE(tasks)
    PRINT "Processing: "; task
ENDWHILE
```

---

## Next Steps

1. Implement IntList in BasicRuntime.java
2. Register IntList functions in builtin_functions.cpp
3. Test IntList
4. Repeat for StringList, Map, Stack, Queue
5. Write comprehensive tests
6. Update documentation

---

**Status**: Design Complete
**Ready for**: Implementation

