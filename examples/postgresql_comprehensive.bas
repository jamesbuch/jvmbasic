' postgresql_comprehensive.bas - Comprehensive PostgreSQL Database Example
' =====================================================================
' Demonstrates all database operations: CREATE, INSERT, UPDATE, SELECT,
' DELETE, DROP, JOINs, transactions, and PostgreSQL-specific features.
'
' PREREQUISITES:
' 1. PostgreSQL server running on localhost:5432
' 2. User 'developer' with password 'test' (or modify connection string)
' 3. PostgreSQL JDBC driver in lib/ directory
'
' TO TEST:
' 1. Start PostgreSQL: sudo systemctl start postgresql
' 2. Create user: CREATE USER developer WITH PASSWORD 'test' CREATEDB;
' 3. Or: sudo -u postgres createuser -P developer
' 4. Compile and run this program
'
' NOTE: PostgreSQL syntax differences from MySQL/MariaDB:
' - Uses SERIAL instead of AUTO_INCREMENT
' - Uses || for string concatenation
' - Uses BOOLEAN type (not INT for booleans)
' - Uses RETURNING clause for getting inserted IDs

Console.WriteLine("======================================================")
Console.WriteLine("  PostgreSQL Comprehensive Database Example")
Console.WriteLine("======================================================")
Console.WriteLine("")

' Connection parameters
Dim dbUrl As String = "jdbc:postgresql://localhost:5432/postgres"
Dim dbUser As String = "developer"
Dim dbPass As String = "test"
Dim testDb As String = "jvmbasic_test"

' Connect to PostgreSQL server
Console.WriteLine("--- Connecting to PostgreSQL Server ---")
Dim conn As Integer = Db.Connect(dbUrl, dbUser, dbPass)
Dim connected As Boolean = False

If conn < 0 Then
    Console.WriteLine("ERROR: Could not connect to PostgreSQL")
    Console.WriteLine("Make sure PostgreSQL is running and credentials are correct")
Else
    Console.WriteLine("Connected! Connection ID: " + conn)
    Console.WriteLine("")

    ' ============================================
    ' SECTION 1: Database Creation
    ' ============================================
    Console.WriteLine("--- Section 1: Create Test Database ---")

    ' Drop database if exists (need to disconnect existing connections)
    Dim dropResult As Integer = Db.Execute(conn, "DROP DATABASE IF EXISTS " + testDb)
    Console.WriteLine("Dropped existing database (if any)")

    ' Create new database
    Dim createDbResult As Integer = Db.Execute(conn, "CREATE DATABASE " + testDb)
    If createDbResult >= 0 Then
        Console.WriteLine("Created database: " + testDb)
    Else
        Console.WriteLine("Note: Database may already exist or user lacks permissions")
    End If

    ' Close connection and reconnect to new database
    Db.Close(conn)
    Console.WriteLine("Reconnecting to " + testDb + "...")

    Dim newUrl As String = "jdbc:postgresql://localhost:5432/" + testDb
    conn = Db.Connect(newUrl, dbUser, dbPass)
    If conn < 0 Then
        Console.WriteLine("ERROR: Could not connect to " + testDb)
    Else
        Console.WriteLine("Connected to " + testDb)
        connected = True
    End If
End If

If connected Then
Console.WriteLine("")

' ============================================
' SECTION 2: Table Creation (PostgreSQL Syntax)
' ============================================
Console.WriteLine("--- Section 2: Create Tables ---")

' Create customers table (uses SERIAL for auto-increment)
Dim createCustomers As String = "CREATE TABLE customers (id SERIAL PRIMARY KEY, name VARCHAR(100) NOT NULL, email VARCHAR(100) UNIQUE, city VARCHAR(50), credit_limit NUMERIC(10,2) DEFAULT 1000.00, is_active BOOLEAN DEFAULT TRUE, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"

Dim r1 As Integer = Db.Execute(conn, createCustomers)
Console.WriteLine("Created 'customers' table")

' Create products table
Dim createProducts As String = "CREATE TABLE products (id SERIAL PRIMARY KEY, name VARCHAR(100) NOT NULL, price NUMERIC(10,2) NOT NULL, stock INTEGER DEFAULT 0, category VARCHAR(50), in_stock BOOLEAN DEFAULT TRUE)"

Dim r2 As Integer = Db.Execute(conn, createProducts)
Console.WriteLine("Created 'products' table")

' Create orders table
Dim createOrders As String = "CREATE TABLE orders (id SERIAL PRIMARY KEY, customer_id INTEGER NOT NULL REFERENCES customers(id), order_date DATE NOT NULL, total NUMERIC(10,2), status VARCHAR(20) DEFAULT 'pending')"

Dim r3 As Integer = Db.Execute(conn, createOrders)
Console.WriteLine("Created 'orders' table")

' Create order_items table
Dim createOrderItems As String = "CREATE TABLE order_items (id SERIAL PRIMARY KEY, order_id INTEGER NOT NULL REFERENCES orders(id), product_id INTEGER NOT NULL REFERENCES products(id), quantity INTEGER NOT NULL, unit_price NUMERIC(10,2) NOT NULL)"

Dim r4 As Integer = Db.Execute(conn, createOrderItems)
Console.WriteLine("Created 'order_items' table")

' Create an index
Dim idx1 As Integer = Db.Execute(conn, "CREATE INDEX idx_orders_customer ON orders(customer_id)")
Console.WriteLine("Created index on orders.customer_id")
Console.WriteLine("")

' ============================================
' SECTION 3: Insert Data
' ============================================
Console.WriteLine("--- Section 3: Insert Data ---")

' Insert customers
Db.Execute(conn, "INSERT INTO customers (name, email, city, credit_limit) VALUES ('Alice Smith', 'alice@email.com', 'New York', 5000.00)")
Db.Execute(conn, "INSERT INTO customers (name, email, city, credit_limit) VALUES ('Bob Johnson', 'bob@email.com', 'Los Angeles', 3000.00)")
Db.Execute(conn, "INSERT INTO customers (name, email, city, credit_limit, is_active) VALUES ('Carol White', 'carol@email.com', 'Chicago', 7500.00, TRUE)")
Db.Execute(conn, "INSERT INTO customers (name, email, city, is_active) VALUES ('David Brown', 'david@email.com', 'Houston', FALSE)")
Console.WriteLine("Inserted 4 customers")

' Insert products
Db.Execute(conn, "INSERT INTO products (name, price, stock, category) VALUES ('Laptop', 999.99, 50, 'Electronics')")
Db.Execute(conn, "INSERT INTO products (name, price, stock, category) VALUES ('Mouse', 29.99, 200, 'Electronics')")
Db.Execute(conn, "INSERT INTO products (name, price, stock, category) VALUES ('Keyboard', 79.99, 150, 'Electronics')")
Db.Execute(conn, "INSERT INTO products (name, price, stock, category) VALUES ('Monitor', 299.99, 75, 'Electronics')")
Db.Execute(conn, "INSERT INTO products (name, price, stock, category, in_stock) VALUES ('Desk Chair', 199.99, 0, 'Furniture', FALSE)")
Console.WriteLine("Inserted 5 products")

' Insert orders
Db.Execute(conn, "INSERT INTO orders (customer_id, order_date, total, status) VALUES (1, '2024-01-15', 1029.98, 'completed')")
Db.Execute(conn, "INSERT INTO orders (customer_id, order_date, total, status) VALUES (2, '2024-01-16', 379.97, 'shipped')")
Db.Execute(conn, "INSERT INTO orders (customer_id, order_date, total, status) VALUES (1, '2024-01-17', 299.99, 'pending')")
Db.Execute(conn, "INSERT INTO orders (customer_id, order_date, total, status) VALUES (3, '2024-01-18', 199.99, 'completed')")
Console.WriteLine("Inserted 4 orders")

' Insert order items
Db.Execute(conn, "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (1, 1, 1, 999.99)")
Db.Execute(conn, "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (1, 2, 1, 29.99)")
Db.Execute(conn, "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (2, 4, 1, 299.99)")
Db.Execute(conn, "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (2, 3, 1, 79.99)")
Db.Execute(conn, "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (3, 4, 1, 299.99)")
Db.Execute(conn, "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (4, 5, 1, 199.99)")
Console.WriteLine("Inserted 6 order items")
Console.WriteLine("")

' ============================================
' SECTION 4: Simple SELECT Queries
' ============================================
Console.WriteLine("--- Section 4: Simple SELECT Queries ---")

' Select all customers
Console.WriteLine("All customers:")
Dim result As Integer = Db.Query(conn, "SELECT id, name, email, city, is_active FROM customers ORDER BY id")
While Db.NextRow(result) > 0
    Dim id As Integer = Db.GetInt(result, "id")
    Dim name As String = Db.GetString(result, "name")
    Dim city As String = Db.GetString(result, "city")
    Dim active As String = Db.GetString(result, "is_active")
    Console.WriteLine("  #" + id + ": " + name + " (" + city + ") - active: " + active)
Wend
Db.CloseResult(result)
Console.WriteLine("")

' Select with boolean filter
Console.WriteLine("Active customers only:")
result = Db.Query(conn, "SELECT name, city FROM customers WHERE is_active = TRUE ORDER BY name")
While Db.NextRow(result) > 0
    Console.WriteLine("  " + Db.GetString(result, "name") + " - " + Db.GetString(result, "city"))
Wend
Db.CloseResult(result)
Console.WriteLine("")

' ============================================
' SECTION 5: PostgreSQL-Specific Features
' ============================================
Console.WriteLine("--- Section 5: PostgreSQL-Specific Features ---")

' String concatenation with ||
Console.WriteLine("Formatted customer list (using || concat):")
result = Db.Query(conn, "SELECT name || ' from ' || city AS description FROM customers ORDER BY id")
While Db.NextRow(result) > 0
    Console.WriteLine("  " + Db.GetString(result, "description"))
Wend
Db.CloseResult(result)
Console.WriteLine("")

' COALESCE and NULL handling
Console.WriteLine("Credit limits with default (COALESCE):")
result = Db.Query(conn, "SELECT name, COALESCE(credit_limit, 0)::TEXT AS limit_str FROM customers ORDER BY id")
While Db.NextRow(result) > 0
    Console.WriteLine("  " + Db.GetString(result, "name") + ": $" + Db.GetString(result, "limit_str"))
Wend
Db.CloseResult(result)
Console.WriteLine("")

' ============================================
' SECTION 6: JOIN Queries
' ============================================
Console.WriteLine("--- Section 6: JOIN Queries ---")

' Inner join
Console.WriteLine("Orders with customer names:")
Dim joinQuery As String = "SELECT o.id, c.name, o.order_date, o.total, o.status FROM orders o INNER JOIN customers c ON o.customer_id = c.id ORDER BY o.id"
result = Db.Query(conn, joinQuery)
While Db.NextRow(result) > 0
    Dim oid As Integer = Db.GetInt(result, "id")
    Dim cname As String = Db.GetString(result, "name")
    Dim total As String = Db.GetString(result, "total")
    Dim status As String = Db.GetString(result, "status")
    Console.WriteLine("  Order #" + oid + ": " + cname + " - $" + total + " [" + status + "]")
Wend
Db.CloseResult(result)
Console.WriteLine("")

' Left join (show customers with no orders)
Console.WriteLine("Customers with order count (LEFT JOIN):")
result = Db.Query(conn, "SELECT c.name, COUNT(o.id) AS order_count FROM customers c LEFT JOIN orders o ON c.id = o.customer_id GROUP BY c.id, c.name ORDER BY order_count DESC")
While Db.NextRow(result) > 0
    Console.WriteLine("  " + Db.GetString(result, "name") + ": " + Db.GetString(result, "order_count") + " orders")
Wend
Db.CloseResult(result)
Console.WriteLine("")

' ============================================
' SECTION 7: Aggregate Functions
' ============================================
Console.WriteLine("--- Section 7: Aggregate Functions ---")

' Statistics
result = Db.Query(conn, "SELECT COUNT(*) AS cnt, SUM(total)::TEXT AS total_sales, AVG(total)::NUMERIC(10,2)::TEXT AS avg_order, MIN(total)::TEXT AS min_order, MAX(total)::TEXT AS max_order FROM orders")
If Db.NextRow(result) > 0 Then
    Console.WriteLine("Order statistics:")
    Console.WriteLine("  Count: " + Db.GetString(result, "cnt"))
    Console.WriteLine("  Total sales: $" + Db.GetString(result, "total_sales"))
    Console.WriteLine("  Average: $" + Db.GetString(result, "avg_order"))
    Console.WriteLine("  Min: $" + Db.GetString(result, "min_order"))
    Console.WriteLine("  Max: $" + Db.GetString(result, "max_order"))
End If
Db.CloseResult(result)
Console.WriteLine("")

' ============================================
' SECTION 8: UPDATE Operations
' ============================================
Console.WriteLine("--- Section 8: UPDATE Operations ---")

' Update single record
Dim updateResult As Integer = Db.Execute(conn, "UPDATE customers SET credit_limit = 10000.00 WHERE name = 'Carol White'")
Console.WriteLine("Updated Carol's credit limit: " + updateResult + " row(s)")

' Update with calculation
updateResult = Db.Execute(conn, "UPDATE products SET price = price * 0.90 WHERE category = 'Electronics'")
Console.WriteLine("Applied 10% discount to electronics: " + updateResult + " row(s)")

' Verify update
Console.WriteLine("Updated electronics prices:")
result = Db.Query(conn, "SELECT name, price::TEXT AS price FROM products WHERE category = 'Electronics' ORDER BY price")
While Db.NextRow(result) > 0
    Console.WriteLine("  " + Db.GetString(result, "name") + ": $" + Db.GetString(result, "price"))
Wend
Db.CloseResult(result)
Console.WriteLine("")

' ============================================
' SECTION 9: Transactions
' ============================================
Console.WriteLine("--- Section 9: Transactions ---")

' Transaction with rollback
Console.WriteLine("Testing rollback...")
Db.BeginTransaction(conn)
Db.Execute(conn, "INSERT INTO customers (name, email, city) VALUES ('Test User', 'test@test.com', 'Test City')")
Console.WriteLine("  Inserted test user")
Db.Rollback(conn)
Console.WriteLine("  Rolled back")

result = Db.Query(conn, "SELECT COUNT(*) AS cnt FROM customers WHERE name = 'Test User'")
If Db.NextRow(result) > 0 Then
    Console.WriteLine("  Test user count: " + Db.GetString(result, "cnt") + " (should be 0)")
End If
Db.CloseResult(result)

' Transaction with commit
Console.WriteLine("")
Console.WriteLine("Testing commit...")
Db.BeginTransaction(conn)
Db.Execute(conn, "INSERT INTO customers (name, email, city) VALUES ('Real User', 'real@real.com', 'Real City')")
Console.WriteLine("  Inserted real user")
Db.Commit(conn)
Console.WriteLine("  Committed")

result = Db.Query(conn, "SELECT COUNT(*) AS cnt FROM customers WHERE name = 'Real User'")
If Db.NextRow(result) > 0 Then
    Console.WriteLine("  Real user count: " + Db.GetString(result, "cnt") + " (should be 1)")
End If
Db.CloseResult(result)
Console.WriteLine("")

' ============================================
' SECTION 10: DELETE Operations
' ============================================
Console.WriteLine("--- Section 10: DELETE Operations ---")

Dim delResult As Integer = Db.Execute(conn, "DELETE FROM customers WHERE name = 'Real User'")
Console.WriteLine("Deleted 'Real User': " + delResult + " row(s)")

Console.WriteLine("Remaining customer count:")
result = Db.Query(conn, "SELECT COUNT(*) AS cnt FROM customers")
If Db.NextRow(result) > 0 Then
    Console.WriteLine("  " + Db.GetString(result, "cnt") + " customers")
End If
Db.CloseResult(result)
Console.WriteLine("")

' ============================================
' SECTION 11: Cleanup
' ============================================
Console.WriteLine("--- Section 11: Cleanup ---")

' Drop tables (order matters due to foreign keys)
Db.Execute(conn, "DROP TABLE IF EXISTS order_items")
Console.WriteLine("Dropped order_items")
Db.Execute(conn, "DROP TABLE IF EXISTS orders")
Console.WriteLine("Dropped orders")
Db.Execute(conn, "DROP TABLE IF EXISTS products")
Console.WriteLine("Dropped products")
Db.Execute(conn, "DROP TABLE IF EXISTS customers")
Console.WriteLine("Dropped customers")

' Close connection
Db.Close(conn)

' Reconnect to postgres database to drop test database
conn = Db.Connect(dbUrl, dbUser, dbPass)
If conn > 0 Then
    Db.Execute(conn, "DROP DATABASE IF EXISTS " + testDb)
    Console.WriteLine("Dropped database: " + testDb)
    Db.Close(conn)
End If

Console.WriteLine("")
Console.WriteLine("======================================================")
Console.WriteLine("  PostgreSQL Comprehensive Test Complete!")
Console.WriteLine("======================================================")

End If  ' End of "If connected Then"
