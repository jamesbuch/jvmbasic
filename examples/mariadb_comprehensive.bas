' mariadb_comprehensive.bas - Comprehensive MariaDB Database Example
' ===================================================================
' Demonstrates all database operations: CREATE, INSERT, UPDATE, SELECT,
' DELETE, DROP, JOINs, transactions, and more.
'
' PREREQUISITES:
' 1. MariaDB server running on localhost:3306
' 2. User 'developer' with password 'test' (or modify connection string)
' 3. MariaDB JDBC driver in lib/ directory
'
' TO TEST:
' 1. Start MariaDB: sudo systemctl start mariadb
' 2. Create user: CREATE USER 'developer'@'localhost' IDENTIFIED BY 'test';
' 3. Grant privileges: GRANT ALL PRIVILEGES ON *.* TO 'developer'@'localhost';
' 4. Compile and run this program

Console.WriteLine("======================================================")
Console.WriteLine("  MariaDB Comprehensive Database Example")
Console.WriteLine("======================================================")
Console.WriteLine("")

' Connection parameters
Dim dbUrl As String = "jdbc:mariadb://localhost:3306/"
Dim dbUser As String = "developer"
Dim dbPass As String = "test"
Dim testDb As String = "jvmbasic_test"

' Connect to MariaDB server (without database specified)
Console.WriteLine("--- Connecting to MariaDB Server ---")
Dim conn As Integer = Db.Connect(dbUrl, dbUser, dbPass)
Dim connected As Boolean = False

If conn < 0 Then
    Console.WriteLine("ERROR: Could not connect to MariaDB")
    Console.WriteLine("Make sure MariaDB is running and credentials are correct")
Else
    Console.WriteLine("Connected! Connection ID: " + conn)
    Console.WriteLine("")
    connected = True
End If

If connected Then

' ============================================
' SECTION 1: Database Creation
' ============================================
Console.WriteLine("--- Section 1: Create Test Database ---")

' Drop database if exists (clean start)
Dim dropResult As Integer = Db.Execute(conn, "DROP DATABASE IF EXISTS " + testDb)
Console.WriteLine("Dropped existing database (if any)")

' Create new database
Dim createDbResult As Integer = Db.Execute(conn, "CREATE DATABASE " + testDb)
If createDbResult >= 0 Then
    Console.WriteLine("Created database: " + testDb)
Else
    Console.WriteLine("ERROR: Could not create database")
End If

' Use the database
Dim useResult As Integer = Db.Execute(conn, "USE " + testDb)
Console.WriteLine("Using database: " + testDb)
Console.WriteLine("")

' ============================================
' SECTION 2: Table Creation
' ============================================
Console.WriteLine("--- Section 2: Create Tables ---")

' Create customers table
Dim createCustomers As String = "CREATE TABLE customers (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, email VARCHAR(100) UNIQUE, city VARCHAR(50), credit_limit DECIMAL(10,2) DEFAULT 1000.00, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"

Dim r1 As Integer = Db.Execute(conn, createCustomers)
Console.WriteLine("Created 'customers' table")

' Create products table
Dim createProducts As String = "CREATE TABLE products (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, price DECIMAL(10,2) NOT NULL, stock INT DEFAULT 0, category VARCHAR(50))"

Dim r2 As Integer = Db.Execute(conn, createProducts)
Console.WriteLine("Created 'products' table")

' Create orders table (foreign keys)
Dim createOrders As String = "CREATE TABLE orders (id INT AUTO_INCREMENT PRIMARY KEY, customer_id INT NOT NULL, order_date DATE NOT NULL, total DECIMAL(10,2), status VARCHAR(20) DEFAULT 'pending', FOREIGN KEY (customer_id) REFERENCES customers(id))"

Dim r3 As Integer = Db.Execute(conn, createOrders)
Console.WriteLine("Created 'orders' table")

' Create order_items table (junction table)
Dim createOrderItems As String = "CREATE TABLE order_items (id INT AUTO_INCREMENT PRIMARY KEY, order_id INT NOT NULL, product_id INT NOT NULL, quantity INT NOT NULL, unit_price DECIMAL(10,2) NOT NULL, FOREIGN KEY (order_id) REFERENCES orders(id), FOREIGN KEY (product_id) REFERENCES products(id))"

Dim r4 As Integer = Db.Execute(conn, createOrderItems)
Console.WriteLine("Created 'order_items' table")
Console.WriteLine("")

' ============================================
' SECTION 3: Insert Data
' ============================================
Console.WriteLine("--- Section 3: Insert Data ---")

' Insert customers
Dim insC1 As Integer = Db.Execute(conn, "INSERT INTO customers (name, email, city, credit_limit) VALUES ('Alice Smith', 'alice@email.com', 'New York', 5000.00)")
Dim insC2 As Integer = Db.Execute(conn, "INSERT INTO customers (name, email, city, credit_limit) VALUES ('Bob Johnson', 'bob@email.com', 'Los Angeles', 3000.00)")
Dim insC3 As Integer = Db.Execute(conn, "INSERT INTO customers (name, email, city, credit_limit) VALUES ('Carol White', 'carol@email.com', 'Chicago', 7500.00)")
Dim insC4 As Integer = Db.Execute(conn, "INSERT INTO customers (name, email, city) VALUES ('David Brown', 'david@email.com', 'Houston')")
Console.WriteLine("Inserted 4 customers")

' Insert products
Dim insP1 As Integer = Db.Execute(conn, "INSERT INTO products (name, price, stock, category) VALUES ('Laptop', 999.99, 50, 'Electronics')")
Dim insP2 As Integer = Db.Execute(conn, "INSERT INTO products (name, price, stock, category) VALUES ('Mouse', 29.99, 200, 'Electronics')")
Dim insP3 As Integer = Db.Execute(conn, "INSERT INTO products (name, price, stock, category) VALUES ('Keyboard', 79.99, 150, 'Electronics')")
Dim insP4 As Integer = Db.Execute(conn, "INSERT INTO products (name, price, stock, category) VALUES ('Monitor', 299.99, 75, 'Electronics')")
Dim insP5 As Integer = Db.Execute(conn, "INSERT INTO products (name, price, stock, category) VALUES ('Desk Chair', 199.99, 30, 'Furniture')")
Console.WriteLine("Inserted 5 products")

' Insert orders
Dim insO1 As Integer = Db.Execute(conn, "INSERT INTO orders (customer_id, order_date, total, status) VALUES (1, '2024-01-15', 1029.98, 'completed')")
Dim insO2 As Integer = Db.Execute(conn, "INSERT INTO orders (customer_id, order_date, total, status) VALUES (2, '2024-01-16', 379.97, 'shipped')")
Dim insO3 As Integer = Db.Execute(conn, "INSERT INTO orders (customer_id, order_date, total, status) VALUES (1, '2024-01-17', 299.99, 'pending')")
Dim insO4 As Integer = Db.Execute(conn, "INSERT INTO orders (customer_id, order_date, total, status) VALUES (3, '2024-01-18', 199.99, 'completed')")
Console.WriteLine("Inserted 4 orders")

' Insert order items
Dim insOI1 As Integer = Db.Execute(conn, "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (1, 1, 1, 999.99)")
Dim insOI2 As Integer = Db.Execute(conn, "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (1, 2, 1, 29.99)")
Dim insOI3 As Integer = Db.Execute(conn, "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (2, 4, 1, 299.99)")
Dim insOI4 As Integer = Db.Execute(conn, "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (2, 3, 1, 79.99)")
Dim insOI5 As Integer = Db.Execute(conn, "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (3, 4, 1, 299.99)")
Dim insOI6 As Integer = Db.Execute(conn, "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (4, 5, 1, 199.99)")
Console.WriteLine("Inserted 6 order items")
Console.WriteLine("")

' ============================================
' SECTION 4: Simple SELECT Queries
' ============================================
Console.WriteLine("--- Section 4: Simple SELECT Queries ---")

' Select all customers
Console.WriteLine("All customers:")
Dim result As Integer = Db.Query(conn, "SELECT id, name, email, city FROM customers ORDER BY id")
While Db.NextRow(result) > 0
    Dim id As Integer = Db.GetInt(result, "id")
    Dim name As String = Db.GetString(result, "name")
    Dim city As String = Db.GetString(result, "city")
    Console.WriteLine("  #" + id + ": " + name + " (" + city + ")")
Wend
Db.CloseResult(result)
Console.WriteLine("")

' Select products with filtering
Console.WriteLine("Products priced over $100:")
result = Db.Query(conn, "SELECT name, price, stock FROM products WHERE price > 100 ORDER BY price DESC")
While Db.NextRow(result) > 0
    Dim pname As String = Db.GetString(result, "name")
    Dim price As String = Db.GetString(result, "price")
    Dim stock As Integer = Db.GetInt(result, "stock")
    Console.WriteLine("  " + pname + ": $" + price + " (stock: " + stock + ")")
Wend
Db.CloseResult(result)
Console.WriteLine("")

' ============================================
' SECTION 5: JOIN Queries
' ============================================
Console.WriteLine("--- Section 5: JOIN Queries ---")

' Inner join - orders with customer names
Console.WriteLine("Orders with customer names (INNER JOIN):")
Dim joinQuery As String = "SELECT o.id, c.name, o.order_date, o.total, o.status FROM orders o INNER JOIN customers c ON o.customer_id = c.id ORDER BY o.id"
result = Db.Query(conn, joinQuery)
While Db.NextRow(result) > 0
    Dim oid As Integer = Db.GetInt(result, "id")
    Dim cname As String = Db.GetString(result, "name")
    Dim odate As String = Db.GetString(result, "order_date")
    Dim total As String = Db.GetString(result, "total")
    Dim status As String = Db.GetString(result, "status")
    Console.WriteLine("  Order #" + oid + ": " + cname + " - $" + total + " [" + status + "]")
Wend
Db.CloseResult(result)
Console.WriteLine("")

' Three-way join - order details
Console.WriteLine("Order #1 details (3-way JOIN):")
Dim detailQuery As String = "SELECT p.name, oi.quantity, oi.unit_price, (oi.quantity * oi.unit_price) AS subtotal FROM order_items oi JOIN orders o ON oi.order_id = o.id JOIN products p ON oi.product_id = p.id WHERE o.id = 1"
result = Db.Query(conn, detailQuery)
While Db.NextRow(result) > 0
    Dim prodName As String = Db.GetString(result, "name")
    Dim qty As Integer = Db.GetInt(result, "quantity")
    Dim unitPrice As String = Db.GetString(result, "unit_price")
    Dim subtotal As String = Db.GetString(result, "subtotal")
    Console.WriteLine("  " + prodName + " x" + qty + " @ $" + unitPrice + " = $" + subtotal)
Wend
Db.CloseResult(result)
Console.WriteLine("")

' ============================================
' SECTION 6: Aggregate Functions
' ============================================
Console.WriteLine("--- Section 6: Aggregate Functions ---")

' Count, sum, avg
result = Db.Query(conn, "SELECT COUNT(*) AS cnt, SUM(total) AS total_sales, AVG(total) AS avg_order FROM orders")
If Db.NextRow(result) > 0 Then
    Console.WriteLine("Order statistics:")
    Console.WriteLine("  Total orders: " + Db.GetString(result, "cnt"))
    Console.WriteLine("  Total sales: $" + Db.GetString(result, "total_sales"))
    Console.WriteLine("  Average order: $" + Db.GetString(result, "avg_order"))
End If
Db.CloseResult(result)
Console.WriteLine("")

' Group by with having
Console.WriteLine("Customers with multiple orders:")
result = Db.Query(conn, "SELECT c.name, COUNT(o.id) AS order_count FROM customers c LEFT JOIN orders o ON c.id = o.customer_id GROUP BY c.id, c.name HAVING COUNT(o.id) > 1")
While Db.NextRow(result) > 0
    Console.WriteLine("  " + Db.GetString(result, "name") + ": " + Db.GetString(result, "order_count") + " orders")
Wend
Db.CloseResult(result)
Console.WriteLine("")

' ============================================
' SECTION 7: UPDATE Operations
' ============================================
Console.WriteLine("--- Section 7: UPDATE Operations ---")

' Update single record
Dim updateResult As Integer = Db.Execute(conn, "UPDATE customers SET credit_limit = 10000.00 WHERE name = 'Carol White'")
Console.WriteLine("Updated Carol's credit limit: " + updateResult + " row(s) affected")

' Update multiple records
updateResult = Db.Execute(conn, "UPDATE products SET stock = stock - 1 WHERE id IN (1, 2)")
Console.WriteLine("Decreased stock for products 1 and 2: " + updateResult + " row(s) affected")

' Verify updates
result = Db.Query(conn, "SELECT name, credit_limit FROM customers WHERE name = 'Carol White'")
If Db.NextRow(result) > 0 Then
    Console.WriteLine("Carol's new credit limit: $" + Db.GetString(result, "credit_limit"))
End If
Db.CloseResult(result)
Console.WriteLine("")

' ============================================
' SECTION 8: Transactions
' ============================================
Console.WriteLine("--- Section 8: Transactions ---")

' Start transaction
Console.WriteLine("Starting transaction...")
Db.BeginTransaction(conn)

' Make changes
Dim t1 As Integer = Db.Execute(conn, "INSERT INTO customers (name, email, city) VALUES ('Eve Test', 'eve@test.com', 'Test City')")
Console.WriteLine("  Inserted new customer")

Dim t2 As Integer = Db.Execute(conn, "UPDATE products SET price = price * 1.10 WHERE category = 'Electronics'")
Console.WriteLine("  Increased electronics prices by 10%: " + t2 + " products")

' Rollback transaction
Console.WriteLine("Rolling back transaction...")
Db.Rollback(conn)

' Verify rollback
result = Db.Query(conn, "SELECT COUNT(*) AS cnt FROM customers WHERE name = 'Eve Test'")
If Db.NextRow(result) > 0 Then
    Console.WriteLine("Eve Test exists: " + Db.GetString(result, "cnt") + " (should be 0)")
End If
Db.CloseResult(result)

' Now do a committed transaction
Console.WriteLine("")
Console.WriteLine("Starting another transaction (will commit)...")
Db.BeginTransaction(conn)
Dim t3 As Integer = Db.Execute(conn, "INSERT INTO customers (name, email, city) VALUES ('Frank Final', 'frank@final.com', 'Final City')")
Console.WriteLine("  Inserted Frank")
Db.Commit(conn)
Console.WriteLine("Transaction committed")

' Verify commit
result = Db.Query(conn, "SELECT COUNT(*) AS cnt FROM customers WHERE name = 'Frank Final'")
If Db.NextRow(result) > 0 Then
    Console.WriteLine("Frank Final exists: " + Db.GetString(result, "cnt") + " (should be 1)")
End If
Db.CloseResult(result)
Console.WriteLine("")

' ============================================
' SECTION 9: DELETE Operations
' ============================================
Console.WriteLine("--- Section 9: DELETE Operations ---")

' Delete single record
Dim delResult As Integer = Db.Execute(conn, "DELETE FROM customers WHERE name = 'Frank Final'")
Console.WriteLine("Deleted Frank Final: " + delResult + " row(s)")

' Show remaining customers
Console.WriteLine("Remaining customers:")
result = Db.Query(conn, "SELECT name FROM customers ORDER BY id")
While Db.NextRow(result) > 0
    Console.WriteLine("  - " + Db.GetString(result, "name"))
Wend
Db.CloseResult(result)
Console.WriteLine("")

' ============================================
' SECTION 10: Cleanup - DROP Tables and Database
' ============================================
Console.WriteLine("--- Section 10: Cleanup ---")

' Drop tables (order matters due to foreign keys)
Db.Execute(conn, "DROP TABLE IF EXISTS order_items")
Console.WriteLine("Dropped order_items table")
Db.Execute(conn, "DROP TABLE IF EXISTS orders")
Console.WriteLine("Dropped orders table")
Db.Execute(conn, "DROP TABLE IF EXISTS products")
Console.WriteLine("Dropped products table")
Db.Execute(conn, "DROP TABLE IF EXISTS customers")
Console.WriteLine("Dropped customers table")

' Drop database
Db.Execute(conn, "DROP DATABASE IF EXISTS " + testDb)
Console.WriteLine("Dropped database: " + testDb)
Console.WriteLine("")

' Close connection
Db.Close(conn)
Console.WriteLine("Connection closed")
Console.WriteLine("")

Console.WriteLine("======================================================")
Console.WriteLine("  MariaDB Comprehensive Test Complete!")
Console.WriteLine("======================================================")

End If  ' End of "If connected Then"
