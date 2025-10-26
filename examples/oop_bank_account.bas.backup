REM Bank Account Example - Demonstrating OOP in JVM BASIC
REM Shows: Classes, constructors, PUBLIC/PRIVATE fields, object instantiation

' Define BankAccount class
CLASS BankAccount
    PRIVATE balance AS FLOAT
    PUBLIC owner AS STRING
    PUBLIC accountNumber AS FLOAT
    
    PUBLIC SUB New(name AS STRING, accountNum AS FLOAT, initialBalance AS FLOAT)
        owner = name
        accountNumber = accountNum
        balance = initialBalance
    END SUB
END CLASS

' Create bank accounts
PRINT "=== Bank Account Management System ==="
PRINT ""

DIM account1 AS NEW BankAccount("Alice Johnson", 1001.0, 5000.0)
DIM account2 AS NEW BankAccount("Bob Smith", 1002.0, 3500.0)
DIM account3 AS NEW BankAccount("Carol White", 1003.0, 10000.0)

PRINT "Account Holder: "; account1.owner
PRINT "Account Number: "; account1.accountNumber
PRINT "Initial Balance: $"; account1.balance
PRINT ""

PRINT "Account Holder: "; account2.owner
PRINT "Account Number: "; account2.accountNumber
PRINT "Initial Balance: $"; account2.balance
PRINT ""

PRINT "Account Holder: "; account3.owner
PRINT "Account Number: "; account3.accountNumber
PRINT "Initial Balance: $"; account3.balance
PRINT ""

' Demonstrate field access and modification
LET account1.balance = account1.balance + 500.0
LET account2.balance = account2.balance - 200.0

PRINT "=== After Transactions ==="
PRINT account1.owner; " new balance: $"; account1.balance
PRINT account2.owner; " new balance: $"; account2.balance
PRINT account3.owner; " balance: $"; account3.balance

PRINT ""
PRINT "✓ OOP Features Demonstrated:"
PRINT "  - Class declarations with fields"
PRINT "  - PUBLIC and PRIVATE modifiers"
PRINT "  - Constructors with multiple parameters"
PRINT "  - Object instantiation with NEW"
PRINT "  - Field access and modification"
PRINT "  - Multiple objects of same class"

