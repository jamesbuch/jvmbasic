' Test IntList Collection using namespace syntax
Console.WriteLine("=== IntList Test ===")

' Create a new list
Dim list As Integer = IntList.Create()
Console.WriteLine("Created IntList with ID: " + list)

' Add some values
Dim size1 As Integer = IntList.Add(list, 10)
Dim size2 As Integer = IntList.Add(list, 20)
Dim size3 As Integer = IntList.Add(list, 30)
Console.WriteLine("Added 3 items, size: " + size3)

' Get list size
Dim size As Integer = IntList.Size(list)
Console.WriteLine("List size: " + size)

' Get values
Dim val0 As Integer = IntList.Get(list, 0)
Dim val1 As Integer = IntList.Get(list, 1)
Dim val2 As Integer = IntList.Get(list, 2)
Console.WriteLine("Values: " + val0 + ", " + val1 + ", " + val2)

' Set a value
Dim oldVal As Integer = IntList.Set(list, 1, 99)
Console.WriteLine("Changed index 1 from " + oldVal + " to 99")

Dim newVal As Integer = IntList.Get(list, 1)
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
Dim idx As Integer = IntList.IndexOf(list, 30)
Console.WriteLine("Index of 30: " + idx)

' Remove an item
Dim removed As Integer = IntList.Remove(list, 0)
Console.WriteLine("Removed item at index 0: " + removed)

Dim newSize As Integer = IntList.Size(list)
Console.WriteLine("New size after remove: " + newSize)

' Print remaining items
Console.WriteLine("Remaining items:")
Dim finalSize As Integer = IntList.Size(list)
IF finalSize > 0 THEN
    Dim vala As Integer = IntList.Get(list, 0)
    Console.WriteLine("  [0] = " + vala)
ENDIF
IF finalSize > 1 THEN
    Dim valb As Integer = IntList.Get(list, 1)
    Console.WriteLine("  [1] = " + valb)
ENDIF

Console.WriteLine("=== IntList Test Complete ===")

