REM Contact Manager - Demonstrates Real-World OOP Usage
REM Shows: Multiple object types, constructors, data organization

' Define Contact class
CLASS Contact
    PUBLIC firstName AS STRING
    PUBLIC lastName AS STRING
    PUBLIC email AS STRING
    PUBLIC phone AS STRING
    
    PUBLIC SUB New(first AS STRING, last AS STRING, mail AS STRING, number AS STRING)
        firstName = first
        lastName = last
        email = mail
        phone = number
    END SUB
END CLASS

' Define Address class
CLASS Address
    PUBLIC street AS STRING
    PUBLIC city AS STRING
    PUBLIC zip AS STRING
    
    PUBLIC SUB New(st AS STRING, ct AS STRING, z AS STRING)
        street = st
        city = ct
        zip = z
    END SUB
END CLASS

PRINT "=== Contact Management System ==="
PRINT ""

' Create contacts
DIM contact1 AS NEW Contact("Alice", "Johnson", "alice@email.com", "555-1234")
DIM contact2 AS NEW Contact("Bob", "Smith", "bob@email.com", "555-5678")
DIM contact3 AS NEW Contact("Carol", "White", "carol@email.com", "555-9012")

' Create addresses
DIM addr1 AS NEW Address("123 Main St", "Springfield", "12345")
DIM addr2 AS NEW Address("456 Oak Ave", "Riverside", "67890")

' Display contact information
PRINT "Contact 1:"
PRINT "  Name: "; contact1.firstName; " "; contact1.lastName
PRINT "  Email: "; contact1.email
PRINT "  Phone: "; contact1.phone
PRINT ""

PRINT "Contact 2:"
PRINT "  Name: "; contact2.firstName; " "; contact2.lastName
PRINT "  Email: "; contact2.email
PRINT "  Phone: "; contact2.phone
PRINT ""

PRINT "Contact 3:"
PRINT "  Name: "; contact3.firstName; " "; contact3.lastName
PRINT "  Email: "; contact3.email
PRINT "  Phone: "; contact3.phone
PRINT ""

' Display addresses
PRINT "Address 1:"
PRINT "  "; addr1.street
PRINT "  "; addr1.city; ", "; addr1.zip
PRINT ""

PRINT "Address 2:"
PRINT "  "; addr2.street
PRINT "  "; addr2.city; ", "; addr2.zip
PRINT ""

PRINT "✓ Contact manager demonstrating:"
PRINT "  - Real-world OOP data modeling"
PRINT "  - Multiple classes with different purposes"
PRINT "  - Multi-parameter constructors"
PRINT "  - Complex data organization"

