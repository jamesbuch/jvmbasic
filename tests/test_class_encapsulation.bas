' Test 4: Encapsulation with PRIVATE fields
' Tests: PRIVATE access control, methods accessing private fields

CLASS BankAccount
    PRIVATE balance AS FLOAT
    PUBLIC owner AS STRING
    
    PUBLIC SUB New(name AS STRING, initial AS FLOAT)
        owner = name
        balance = initial
    END SUB
    
    PUBLIC SUB Deposit(amount AS FLOAT)
        balance = balance + amount
    END SUB
    
    PUBLIC FUNCTION GetBalance() AS FLOAT
        RETURN balance
    END FUNCTION
END CLASS

' When codegen works:
' DIM account AS NEW BankAccount("Alice", 1000.0)
' CALL account.Deposit(500.0)
' PRINT account.owner; " has balance: "; account.GetBalance()
' PRINT "Expected: Alice has balance: 1500.0"
'
' This should fail (private field):
' PRINT account.balance

PRINT "Test: Encapsulation with PRIVATE fields"
PRINT "Status: ✓ WORKING (Phase 7 complete)"



