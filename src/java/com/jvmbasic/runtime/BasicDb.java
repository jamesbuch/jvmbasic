package com.jvmbasic.runtime;

import java.sql.*;
import java.util.*;

/**
 * BasicDb - Database functions for JVM BASIC 2.0
 *
 * Provides JDBC database operations accessible via Db.FunctionName() in BASIC code.
 * All methods are static for easy bytecode generation.
 *
 * Example usage in BASIC:
 *   Db.Connect("jdbc:sqlite:mydb.db")
 *   var result as String[][] = Db.Query("SELECT * FROM users")
 *   Db.Execute("INSERT INTO users (name) VALUES ('Alice')")
 *   Db.Close()
 *
 * For parameterized queries (SQL injection safe):
 *   Db.Prepare("SELECT * FROM users WHERE id = ? AND name = ?")
 *   Db.SetInt(1, 42)
 *   Db.SetString(2, "Alice")
 *   var result as String[][] = Db.ExecuteQuery()
 *   Db.CloseStmt()
 */
public final class BasicDb {

    private BasicDb() {} // Prevent instantiation

    // Active connection (thread-local for safety)
    private static final ThreadLocal<Connection> currentConnection = new ThreadLocal<>();
    private static final ThreadLocal<PreparedStatement> currentStatement = new ThreadLocal<>();
    private static final ThreadLocal<Statement> currentQueryStatement = new ThreadLocal<>();
    private static final ThreadLocal<ResultSet> currentResultSet = new ThreadLocal<>();

    // ========================================================================
    // Connection Management
    // ========================================================================

    /**
     * Connect to a database using JDBC URL
     * Examples:
     *   "jdbc:sqlite:mydb.db"
     *   "jdbc:mysql://localhost:3306/mydb"
     *   "jdbc:postgresql://localhost:5432/mydb"
     */
    public static boolean Connect(String jdbcUrl) {
        try {
            Close(); // Close any existing connection
            Connection conn = DriverManager.getConnection(jdbcUrl);
            currentConnection.set(conn);
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Connect with username and password
     */
    public static boolean Connect(String jdbcUrl, String username, String password) {
        try {
            Close();
            Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
            currentConnection.set(conn);
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close the current connection
     */
    public static void Close() {
        CloseStmt();
        Connection conn = currentConnection.get();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close errors
            }
            currentConnection.remove();
        }
    }

    /**
     * Check if connected
     */
    public static boolean IsConnected() {
        Connection conn = currentConnection.get();
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // ========================================================================
    // Simple Query Execution (String-based, not parameterized)
    // ========================================================================

    /**
     * Execute a SELECT query and return results as 2D string array
     * First row contains column names
     */
    public static String[][] Query(String sql) {
        Connection conn = currentConnection.get();
        if (conn == null) {
            System.err.println("No database connection");
            return new String[0][];
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return resultSetToArray(rs);
        } catch (SQLException e) {
            System.err.println("Query error: " + e.getMessage());
            return new String[0][];
        }
    }

    /**
     * Execute a SELECT query for cursor-based iteration
     * Use with NextRow() and GetString/GetInt/GetDouble
     */
    public static void QueryCursor(String sql) {
        Connection conn = currentConnection.get();
        if (conn == null) {
            System.err.println("No database connection");
            return;
        }

        try {
            CloseResults(); // Close any existing result set
            Statement stmt = conn.createStatement();
            currentQueryStatement.set(stmt);
            ResultSet rs = stmt.executeQuery(sql);
            currentResultSet.set(rs);
        } catch (SQLException e) {
            System.err.println("Query error: " + e.getMessage());
        }
    }

    /**
     * Advance to the next row in the result set
     * Returns true if there is a next row, false if no more rows
     * Note: Called "NextRow" to avoid conflict with BASIC "NEXT" keyword
     */
    public static boolean NextRow() {
        ResultSet rs = currentResultSet.get();
        if (rs == null) {
            return false;
        }
        try {
            return rs.next();
        } catch (SQLException e) {
            System.err.println("NextRow error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get a string value from current row by column name
     */
    public static String GetString(String columnName) {
        ResultSet rs = currentResultSet.get();
        if (rs == null) return "";
        try {
            String value = rs.getString(columnName);
            return value != null ? value : "";
        } catch (SQLException e) {
            System.err.println("GetString error: " + e.getMessage());
            return "";
        }
    }

    /**
     * Get an integer value from current row by column name
     */
    public static int GetInt(String columnName) {
        ResultSet rs = currentResultSet.get();
        if (rs == null) return 0;
        try {
            return rs.getInt(columnName);
        } catch (SQLException e) {
            System.err.println("GetInt error: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get a long value from current row by column name
     */
    public static long GetLong(String columnName) {
        ResultSet rs = currentResultSet.get();
        if (rs == null) return 0L;
        try {
            return rs.getLong(columnName);
        } catch (SQLException e) {
            System.err.println("GetLong error: " + e.getMessage());
            return 0L;
        }
    }

    /**
     * Get a double value from current row by column name
     */
    public static double GetDouble(String columnName) {
        ResultSet rs = currentResultSet.get();
        if (rs == null) return 0.0;
        try {
            return rs.getDouble(columnName);
        } catch (SQLException e) {
            System.err.println("GetDouble error: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Get a float value from current row by column name
     */
    public static float GetFloat(String columnName) {
        ResultSet rs = currentResultSet.get();
        if (rs == null) return 0.0f;
        try {
            return rs.getFloat(columnName);
        } catch (SQLException e) {
            System.err.println("GetFloat error: " + e.getMessage());
            return 0.0f;
        }
    }

    /**
     * Get a boolean value from current row by column name
     */
    public static boolean GetBool(String columnName) {
        ResultSet rs = currentResultSet.get();
        if (rs == null) return false;
        try {
            return rs.getBoolean(columnName);
        } catch (SQLException e) {
            System.err.println("GetBool error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Execute prepared SELECT query for cursor-based iteration
     * Use with NextRow() and GetString/GetInt/GetDouble
     */
    public static void ExecuteQueryCursor() {
        PreparedStatement pstmt = currentStatement.get();
        if (pstmt == null) {
            System.err.println("No prepared statement");
            return;
        }

        try {
            CloseResults(); // Close any existing result set
            ResultSet rs = pstmt.executeQuery();
            currentResultSet.set(rs);
        } catch (SQLException e) {
            System.err.println("ExecuteQueryCursor error: " + e.getMessage());
        }
    }

    /**
     * Close the current result set and query statement
     */
    public static void CloseResults() {
        ResultSet rs = currentResultSet.get();
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // Ignore close errors
            }
            currentResultSet.remove();
        }
        Statement stmt = currentQueryStatement.get();
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                // Ignore close errors
            }
            currentQueryStatement.remove();
        }
    }

    /**
     * Execute an INSERT, UPDATE, or DELETE statement
     * Returns number of affected rows
     */
    public static int Execute(String sql) {
        Connection conn = currentConnection.get();
        if (conn == null) {
            System.err.println("No database connection");
            return -1;
        }

        try (Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Execute error: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Execute a statement that may return a result (for DDL like CREATE TABLE)
     */
    public static boolean ExecuteAny(String sql) {
        Connection conn = currentConnection.get();
        if (conn == null) {
            System.err.println("No database connection");
            return false;
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("Execute error: " + e.getMessage());
            return false;
        }
    }

    // ========================================================================
    // Parameterized Queries (SQL Injection Safe)
    // ========================================================================

    /**
     * Prepare a parameterized SQL statement
     * Use ? placeholders for parameters
     */
    public static boolean Prepare(String sql) {
        Connection conn = currentConnection.get();
        if (conn == null) {
            System.err.println("No database connection");
            return false;
        }

        try {
            CloseStmt(); // Close any existing prepared statement
            PreparedStatement pstmt = conn.prepareStatement(sql);
            currentStatement.set(pstmt);
            return true;
        } catch (SQLException e) {
            System.err.println("Prepare error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Set a string parameter (1-indexed)
     */
    public static boolean SetString(int index, String value) {
        PreparedStatement pstmt = currentStatement.get();
        if (pstmt == null) {
            System.err.println("No prepared statement");
            return false;
        }

        try {
            pstmt.setString(index, value);
            return true;
        } catch (SQLException e) {
            System.err.println("SetString error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Set an integer parameter (1-indexed)
     */
    public static boolean SetInt(int index, int value) {
        PreparedStatement pstmt = currentStatement.get();
        if (pstmt == null) {
            System.err.println("No prepared statement");
            return false;
        }

        try {
            pstmt.setInt(index, value);
            return true;
        } catch (SQLException e) {
            System.err.println("SetInt error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Set a long parameter (1-indexed)
     */
    public static boolean SetLong(int index, long value) {
        PreparedStatement pstmt = currentStatement.get();
        if (pstmt == null) {
            System.err.println("No prepared statement");
            return false;
        }

        try {
            pstmt.setLong(index, value);
            return true;
        } catch (SQLException e) {
            System.err.println("SetLong error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Set a float parameter (1-indexed)
     */
    public static boolean SetFloat(int index, float value) {
        PreparedStatement pstmt = currentStatement.get();
        if (pstmt == null) {
            System.err.println("No prepared statement");
            return false;
        }

        try {
            pstmt.setFloat(index, value);
            return true;
        } catch (SQLException e) {
            System.err.println("SetFloat error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Set a double parameter (1-indexed)
     */
    public static boolean SetDouble(int index, double value) {
        PreparedStatement pstmt = currentStatement.get();
        if (pstmt == null) {
            System.err.println("No prepared statement");
            return false;
        }

        try {
            pstmt.setDouble(index, value);
            return true;
        } catch (SQLException e) {
            System.err.println("SetDouble error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Set a null parameter (1-indexed)
     */
    public static boolean SetNull(int index) {
        PreparedStatement pstmt = currentStatement.get();
        if (pstmt == null) {
            System.err.println("No prepared statement");
            return false;
        }

        try {
            pstmt.setNull(index, Types.NULL);
            return true;
        } catch (SQLException e) {
            System.err.println("SetNull error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clear all parameters from the prepared statement
     */
    public static boolean ClearParameters() {
        PreparedStatement pstmt = currentStatement.get();
        if (pstmt == null) {
            System.err.println("No prepared statement");
            return false;
        }

        try {
            pstmt.clearParameters();
            return true;
        } catch (SQLException e) {
            System.err.println("ClearParameters error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Execute prepared SELECT query
     */
    public static String[][] ExecuteQuery() {
        PreparedStatement pstmt = currentStatement.get();
        if (pstmt == null) {
            System.err.println("No prepared statement");
            return new String[0][];
        }

        try (ResultSet rs = pstmt.executeQuery()) {
            return resultSetToArray(rs);
        } catch (SQLException e) {
            System.err.println("ExecuteQuery error: " + e.getMessage());
            return new String[0][];
        }
    }

    /**
     * Execute prepared INSERT/UPDATE/DELETE
     */
    public static int ExecuteUpdate() {
        PreparedStatement pstmt = currentStatement.get();
        if (pstmt == null) {
            System.err.println("No prepared statement");
            return -1;
        }

        try {
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ExecuteUpdate error: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Close the prepared statement
     */
    public static void CloseStmt() {
        PreparedStatement pstmt = currentStatement.get();
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                // Ignore close errors
            }
            currentStatement.remove();
        }
    }

    // ========================================================================
    // Transaction Management
    // ========================================================================

    /**
     * Begin a transaction (disable auto-commit)
     */
    public static boolean BeginTransaction() {
        Connection conn = currentConnection.get();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);
            return true;
        } catch (SQLException e) {
            System.err.println("BeginTransaction error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Commit the current transaction
     */
    public static boolean Commit() {
        Connection conn = currentConnection.get();
        if (conn == null) return false;

        try {
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("Commit error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Rollback the current transaction
     */
    public static boolean Rollback() {
        Connection conn = currentConnection.get();
        if (conn == null) return false;

        try {
            conn.rollback();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("Rollback error: " + e.getMessage());
            return false;
        }
    }

    // ========================================================================
    // Utility Functions
    // ========================================================================

    /**
     * Get the last inserted ID (for auto-increment columns)
     * Works with MySQL, SQLite, PostgreSQL
     */
    public static long GetLastInsertId() {
        Connection conn = currentConnection.get();
        if (conn == null) return -1;

        try (Statement stmt = conn.createStatement()) {
            // Try different methods for different databases
            try (ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()")) {
                if (rs.next()) return rs.getLong(1);
            } catch (SQLException e1) {
                try (ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) return rs.getLong(1);
                } catch (SQLException e2) {
                    try (ResultSet rs = stmt.executeQuery("SELECT lastval()")) {
                        if (rs.next()) return rs.getLong(1);
                    } catch (SQLException e3) {
                        // All methods failed
                    }
                }
            }
        } catch (SQLException e) {
            // Ignore
        }
        return -1;
    }

    /**
     * Escape a string for use in SQL (basic escaping)
     * Note: Prefer parameterized queries instead!
     */
    public static String Escape(String value) {
        if (value == null) return "NULL";
        return value.replace("'", "''");
    }

    /**
     * Get table names in the database
     */
    public static String[] GetTables() {
        Connection conn = currentConnection.get();
        if (conn == null) return new String[0];

        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"})) {
                List<String> tables = new ArrayList<>();
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
                return tables.toArray(new String[0]);
            }
        } catch (SQLException e) {
            System.err.println("GetTables error: " + e.getMessage());
            return new String[0];
        }
    }

    /**
     * Get column names for a table
     */
    public static String[] GetColumns(String tableName) {
        Connection conn = currentConnection.get();
        if (conn == null) return new String[0];

        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, tableName, "%")) {
                List<String> columns = new ArrayList<>();
                while (rs.next()) {
                    columns.add(rs.getString("COLUMN_NAME"));
                }
                return columns.toArray(new String[0]);
            }
        } catch (SQLException e) {
            System.err.println("GetColumns error: " + e.getMessage());
            return new String[0];
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private static String[][] resultSetToArray(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        List<String[]> rows = new ArrayList<>();

        // First row: column names
        String[] header = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            header[i - 1] = meta.getColumnName(i);
        }
        rows.add(header);

        // Data rows
        while (rs.next()) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                Object value = rs.getObject(i);
                row[i - 1] = value != null ? value.toString() : null;
            }
            rows.add(row);
        }

        return rows.toArray(new String[0][]);
    }
}
