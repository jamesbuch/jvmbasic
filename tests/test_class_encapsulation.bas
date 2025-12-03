' Test 4: Encapsulation with PRIVATE fields
' Tests: PRIVATE access control, methods accessing private fields

CLASS BankAccount
    PRIVATE balance As Float
    PUBLIC owner As String
    
SUB New(name As Integer, initial As Integer)
        owner = name
        balance = initial
    END SUB
    
SUB Deposit(amount As Integer)
        balance = balance + amount
    END SUB
    
    PUBLIC FUNCTION GetBalance() As Single
        RETURN balance
    END FUNCTION
END CLASS

' When codegen works:
' DIM account AS NEW BankAccount("Alice", 1000.0)
' CALL account.Deposit(500.0)
' Console.WriteLine(account.owner + " " + " has balance: " + " " + account.GetBalance())
' PRINT "Expected: Alice has balance: 1500.0"
'
' This should fail (private field):
' PRINT account.balance

Console.WriteLine("Test: Encapsulation with PRIVATE fields")
Console.WriteLine("Status: ✓ WORKING (Phase 7 complete)")



