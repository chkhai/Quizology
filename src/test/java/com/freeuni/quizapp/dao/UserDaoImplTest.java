package com.freeuni.quizapp.dao;

import com.freeuni.quizapp.dao.impl.UserDaoImpl;
import com.freeuni.quizapp.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoImplTest {

    private static Connection connection;
    private UserDaoImpl userDaoImpl;

    private static final Timestamp TIMESTAMP = Timestamp.valueOf("2025-07-01 03:50:38");

    @BeforeAll
    public static void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement statement = connection.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS announcements");
        statement.executeUpdate("DROP TABLE IF EXISTS achievements");
        statement.executeUpdate("DROP TABLE IF EXISTS messages");
        statement.executeUpdate("DROP TABLE IF EXISTS friends");
        statement.executeUpdate("DROP TABLE IF EXISTS friend_requests");
        statement.executeUpdate("DROP TABLE IF EXISTS quiz_results");
        statement.executeUpdate("DROP TABLE IF EXISTS user_answers");
        statement.executeUpdate("DROP TABLE IF EXISTS answers");
        statement.executeUpdate("DROP TABLE IF EXISTS questions");
        statement.executeUpdate("DROP TABLE IF EXISTS quizzes");
        statement.executeUpdate("DROP TABLE IF EXISTS users");
        statement.executeUpdate("CREATE TABLE users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(100) NOT NULL UNIQUE," +
                "hashed_password VARCHAR(255) NOT NULL," +
                "is_admin BOOLEAN DEFAULT FALSE," +
                "bio TEXT," +
                "profile_picture_url VARCHAR(255)," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");
        statement.close();
    }

    @BeforeEach
    public void setUpEach() throws SQLException {
        userDaoImpl = new UserDaoImpl(connection);
        Statement statement = connection.createStatement();
        statement.executeUpdate("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        statement.executeUpdate("INSERT INTO users (username, hashed_password, is_admin) VALUES " +
                "('lkhiz23', 'pwd', TRUE)," +
                "('lchkh23', 'pwd2', TRUE)");
    }

    @AfterEach
    public void deleteFromTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM users");
        statement.close();
    }

    @Test
    public void getUserTest() throws SQLException {
        User user1 = userDaoImpl.getUser(1);
        User user2 = userDaoImpl.getUser(2);
        assertNotNull(user1);
        assertNotNull(user2);
        assertEquals("lkhiz23", user1.getUsername());
        assertEquals("lchkh23", user2.getUsername());
        assertTrue(user1.isAdmin());
        assertTrue(user2.isAdmin());
    }

    @Test
    public void getByUsernameTest() throws SQLException {
        User user1 = userDaoImpl.getByUsername("lkhiz23", true);
        assertNotNull(user1);
        assertEquals("lkhiz23", user1.getUsername());
        assertEquals("pwd", user1.getHashedPassword());
        assertTrue(user1.isAdmin());
        User user2 = userDaoImpl.getByUsername("chkh", false);
        assertNotNull(user2);
        assertEquals("lchkh23", user2.getUsername());
        assertEquals("pwd2", user2.getHashedPassword());
        assertTrue(user2.isAdmin());
    }

    @Test
    public void listAllUsersTest() throws SQLException {
        List<User> users = userDaoImpl.listAllUsers();
        assertEquals(2,  users.size());
        assertEquals("lkhiz23", users.get(0).getUsername());
        assertEquals("lchkh23", users.get(1).getUsername());
    }

    @Test
    public void getUsernameTest() throws SQLException {
        assertEquals("lkhiz23", userDaoImpl.getUsername(1));
        assertEquals("lchkh23", userDaoImpl.getUsername(2));
        assertNull(userDaoImpl.getUsername(3));
    }

    @Test
    public void isAdminTest() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO users (username, hashed_password) VALUES" +
                "('random', 'rand1')");
        assertTrue(userDaoImpl.isAdmin(1));
        assertTrue(userDaoImpl.isAdmin(2));
        assertFalse(userDaoImpl.isAdmin(3));
        assertNull(userDaoImpl.isAdmin(4));
    }

    @Test
    public void getDateTest() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO users (username, hashed_password, created_at) VALUES" +
                "('random', 'rand1', TIMESTAMP '"+ TIMESTAMP +"')");
        Timestamp timestamp = userDaoImpl.getDate(3);
        assertEquals(TIMESTAMP.toString().substring(0, 19), timestamp.toString().substring(0, 19));
        assertNull(userDaoImpl.getDate(4));
    }

    @Test
    public void createUserTest() throws SQLException {
        User user = getTestUser();
        userDaoImpl.createUser(user);
        assertEquals(user, userDaoImpl.getUser(3));
    }

    @Test
    public void changePasswordTest() throws SQLException {
        User user = getTestUser();
        userDaoImpl.createUser(user);
        user.setHashedPassword("rand2");
        userDaoImpl.changePassword(3, "rand2");
        assertEquals(user, userDaoImpl.getUser(3));
    }

    @Test
    public void changePfp() throws SQLException {
        User user = getTestUser();
        userDaoImpl.createUser(user);
        user.setProfilePictureUrl("pic.jpg");
        userDaoImpl.changePfp(3, "pic.jpg");
        assertEquals(user, userDaoImpl.getUser(3));
    }

    @Test
    public void changeBio() throws SQLException {
        User user = getTestUser();
        userDaoImpl.createUser(user);
        user.setBio("Testing is cool");
        userDaoImpl.changeBio(3, "Testing is cool");
        assertEquals(user, userDaoImpl.getUser(3));
    }

    @Test
    public void deleteUserTest() throws SQLException {
        userDaoImpl.deleteUser(-1);
        assertEquals(2, userDaoImpl.listAllUsers().size());
        userDaoImpl.deleteUser(2);
        assertEquals(1, userDaoImpl.listAllUsers().size());
        userDaoImpl.deleteUser(1);
        assertEquals(0, userDaoImpl.listAllUsers().size());
    }

    @Test
    public void setAsAdminTest() throws SQLException {
        userDaoImpl.setAsAdmin(1, false);
        assertFalse(userDaoImpl.isAdmin(1));
        userDaoImpl.setAsAdmin(1, true);
        assertTrue(userDaoImpl.isAdmin(1));
        assertTrue(userDaoImpl.isAdmin(2));
    }

    @Test
    public void isUsernameOccupiedTest() throws SQLException {
        assertTrue(userDaoImpl.isUsernameOccupied("lkhiz23"));
        assertTrue(userDaoImpl.isUsernameOccupied("lchkh23"));
        assertFalse(userDaoImpl.isUsernameOccupied("random"));
    }

    private User getTestUser() {
        return new User(3, "random", "rand1", false, UserDaoImplTest.TIMESTAMP, null, null);
    }

}
