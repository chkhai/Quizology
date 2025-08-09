package com.freeuni.quizapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for establishing connection to the database;
 *
 * Example usage:
 * <pre>
 *     Connection connection = DBConnector.getConnection();
 * </pre>
 */
public class DBConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/quizapp_db"; // your db url
    private static final String USER = "root"; // your user
    private static final String PASSWORD = "root"; // your password

    /**
     * Returns a new {@link java.sql.Connection} object to the database.
     *
     * @return a valid database connection
     * @throws SQLException if a database access error occurs or the connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}