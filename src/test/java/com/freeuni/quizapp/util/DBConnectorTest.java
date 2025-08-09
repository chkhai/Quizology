package com.freeuni.quizapp.util;

import com.freeuni.quizapp.util.DBConnector;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled("Disabled in CI environment – requires real DB connection")
public class DBConnectorTest {
    @Test
    public void getConnectionTest() throws SQLException {
        Connection connection = DBConnector.getConnection();
        assertNotNull(connection);
    }
}
