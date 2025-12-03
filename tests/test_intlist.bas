' Test IntList Collection using namespace syntax
Console.WriteLine("=== IntList Test ===")

' Create a new list
list = IntList.Create()
Console.WriteLine("Created IntList with ID: " + list)

' Add some values
size1 = IntList.Add(list, 10)
size2 = IntList.Add(list, 20)
size3 = IntList.Add(list, 30)
Console.WriteLine("Added 3 items, size: " + size3)

' Get list size
size = IntList.Size(list)
Console.WriteLine("List size: " + size)

' Get values
val0 = IntList.Get(list, 0)
val1 = IntList.Get(list, 1)
val2 = IntList.Get(list, 2)
Console.WriteLine("Values: " + val0 + ", " + val1 + ", " + val2)

' Set a value
oldVal = IntList.Set(list, 1, 99)
Console.WriteLine("Changed index 1 from " + oldVal + " to 99")

newVal = IntList.Get(list, 1)
Console.WriteLine("New value at index 1: " + newVal)

' Check contains
IF IntList.Contains(list, 30) THEN
    Console.WriteLine("List contains 30: YES")
ENDIF

IF IntList.Contains(list, 999) THEN
    Console.WriteLine("List contains 999: YES")
ELSE
    Console.WriteLine("List contains 999: NO")
ENDIF

' Find index
idx = IntList.IndexOf(list, 30)
Console.WriteLine("Index of 30: " + idx)

' Remove an item
removed = IntList.Remove(list, 0)
Console.WriteLine("Removed item at index 0: " + removed)

newSize = IntList.Size(list)
Console.WriteLine("New size after remove: " + newSize)

' Print remaining items
Console.WriteLine("Remaining items:")
finalSize = IntList.Size(list)
IF finalSize > 0 THEN
    vala = IntList.Get(list, 0)
    Console.WriteLine("  [0] = " + vala)
ENDIF
IF finalSize > 1 THEN
    valb = IntList.Get(list, 1)
    Console.WriteLine("  [1] = " + valb)
ENDIF

Console.WriteLine("=== IntList Test Complete ===")

