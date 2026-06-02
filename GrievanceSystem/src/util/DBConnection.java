package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton DB Connection Utility
 * Handles JDBC connection to MySQL grievance_db
 */
public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/grievance_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "root"; // Change to your MySQL password

    private static Connection connection = null;

    // Private constructor — no instantiation
    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connected to grievance_db successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Add the JAR to your classpath.", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
