' Test Thread Namespace - Phase 10 Priority 5
' Thread.Sleep, Thread.CurrentId, Thread.SetInt, Thread.GetInt
' Thread.AtomicAdd, Thread.Lock, Thread.Unlock, Thread.Yield, Thread.AvailableProcessors

Console.WriteLine("=== Thread Namespace Test ===")

' Test current thread ID
Dim threadId As Integer
threadId = Thread.CurrentId()
Console.WriteLine("Current thread ID: " + threadId)

' Test available processors
Dim cpuCount As Integer
cpuCount = Thread.AvailableProcessors()
Console.WriteLine("Available processors: " + cpuCount)

' Test shared variables
Console.WriteLine("")
Console.WriteLine("--- Shared Variables Test ---")

Dim setResult As Integer
setResult = Thread.SetInt("counter", 0)
Console.WriteLine("Initial counter: " + Thread.GetInt("counter"))

Dim newVal As Integer
newVal = Thread.AtomicAdd("counter", 5)
Console.WriteLine("After AtomicAdd(5): " + newVal)

newVal = Thread.AtomicAdd("counter", 3)
Console.WriteLine("After AtomicAdd(3): " + newVal)

Dim finalVal As Integer
finalVal = Thread.GetInt("counter")
Console.WriteLine("Final counter value: " + finalVal)

IF finalVal == 8 THEN
    Console.WriteLine("Atomic operations: SUCCESS")
ELSE
    Console.WriteLine("Atomic operations: FAILED")
ENDIF

' Test string shared variables
Dim strResult As Integer
strResult = Thread.SetString("message", "Hello from thread!")
Dim msg As String
msg = Thread.GetString("message")
Console.WriteLine("Shared string: " + msg)

' Test sleep
Console.WriteLine("")
Console.WriteLine("--- Sleep Test ---")
Console.WriteLine("Sleeping for 100ms...")
Dim sleepResult As Integer
sleepResult = Thread.Sleep(100)
Console.WriteLine("Sleep completed: " + sleepResult)

' Test yield
Dim yieldResult As Integer
yieldResult = Thread.Yield()
Console.WriteLine("Yield called: " + yieldResult)

' Test lock/unlock
Console.WriteLine("")
Console.WriteLine("--- Lock Test ---")
Dim lockResult As Integer
lockResult = Thread.Lock("myLock")
Console.WriteLine("Lock acquired: " + lockResult)

Dim unlockResult As Integer
unlockResult = Thread.Unlock("myLock")
Console.WriteLine("Lock released: " + unlockResult)

Console.WriteLine("=== Thread Namespace Test Complete ===")
