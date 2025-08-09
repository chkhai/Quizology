package com.freeuni.quizapp.dao;

import com.freeuni.quizapp.dao.impl.FriendSystemDaoImpl;
import com.freeuni.quizapp.dao.impl.UserDaoImpl;
import com.freeuni.quizapp.enums.FriendshipStatus;
import com.freeuni.quizapp.model.User;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FriendSystemDaoImplTest {
    private static Connection connection;
    private static FriendSystemDaoImpl friendSystemDaoImpl;

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

        statement.executeUpdate("CREATE TABLE friend_requests (" +
                "friend_request_id INT AUTO_INCREMENT PRIMARY KEY," +
                "from_user INT NOT NULL," +
                "to_user INT NOT NULL," +
                "status VARCHAR(10) DEFAULT 'pending'," +
                "sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (from_user) REFERENCES users(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY (to_user) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")");

        statement.executeUpdate("CREATE TABLE friends (" +
                "friendship_id INT AUTO_INCREMENT PRIMARY KEY," +
                "friend1_user_id INT NOT NULL," +
                "friend2_user_id INT NOT NULL," +
                "UNIQUE (friend1_user_id, friend2_user_id)," +
                "FOREIGN KEY (friend1_user_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY (friend2_user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")");
        statement.close();
    }

    @BeforeEach
    public void setUpEach() throws SQLException {
        friendSystemDaoImpl = new FriendSystemDaoImpl(connection);
        Statement statement = connection.createStatement();
        statement.executeUpdate("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE friend_requests ALTER COLUMN friend_request_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE friends ALTER COLUMN friendship_id RESTART WITH 1");
        statement.executeUpdate("INSERT INTO users (username, hashed_password, is_admin) VALUES " +
                "('lkhiz23', 'pwd', TRUE), " +
                "('lchkh23', 'pwd2', TRUE), " +
                "('sansi23', 'pwd3', TRUE), " +
                "('akave23', 'pwd4', TRUE), " +
                "('lbegi23', 'pwd5', TRUE)");
        statement.executeUpdate("INSERT INTO friends (friend1_user_id, friend2_user_id) VALUES " +
                "(1, 2), " +
                "(1, 3), " +
                "(2, 3), " +
                "(3, 4), " +
                "(3, 5)");
        statement.executeUpdate("INSERT INTO friend_requests (from_user, to_user, status) VALUES " +
                "(1, 2, 'accepted'), " +
                "(1, 3, 'accepted'), " +
                "(2, 3, 'accepted'), " +
                "(3, 4, 'accepted'), " +
                "(3, 5, 'accepted'), " +
                "(2, 4, 'pending'), " +
                "(2, 5, 'rejected')," +
                "(5, 1, 'pending')," +
                "(4, 1, 'pending')");
        statement.close();
    }

    @AfterEach
    public void deleteFromTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM users");
        statement.executeUpdate("DELETE FROM friends");
        statement.executeUpdate("DELETE FROM friend_requests");
        statement.close();
    }

    @Test
    public void getUsersFriendsTest() throws SQLException {
        List<User> list1 = friendSystemDaoImpl.getUsersFriends(1);
        List<User> list2 = friendSystemDaoImpl.getUsersFriends(2);
        List<User> list3 = friendSystemDaoImpl.getUsersFriends(3);
        List<User> list4 = friendSystemDaoImpl.getUsersFriends(4);
        List<User> list5 = friendSystemDaoImpl.getUsersFriends(5);
        assertEquals(2, list1.size());
        assertEquals(2, list2.size());
        assertEquals(4, list3.size());
        assertEquals(1, list4.size());
        assertEquals(1, list5.size());
    }

    @Test
    public void getUsersSentFriendRequestsTest() throws SQLException {
        List<User> list1 = friendSystemDaoImpl.getUsersSentFriendRequests(1);
        List<User> list2 = friendSystemDaoImpl.getUsersSentFriendRequests(2);
        List<User> list3 = friendSystemDaoImpl.getUsersSentFriendRequests(3);
        List<User> list4 = friendSystemDaoImpl.getUsersSentFriendRequests(4);
        List<User> list5 = friendSystemDaoImpl.getUsersSentFriendRequests(5);
        assertEquals(0, list1.size());
        assertEquals(1, list2.size());
        assertEquals(0, list3.size());
        assertEquals(1, list4.size());
        assertEquals(1, list5.size());
    }

    @Test
    public void getUsersReceivedFriendRequestsTest() throws SQLException {
        List<User> list1 = friendSystemDaoImpl.getUsersReceivedRequests(1);
        List<User> list2 = friendSystemDaoImpl.getUsersReceivedRequests(2);
        List<User> list3 = friendSystemDaoImpl.getUsersReceivedRequests(3);
        List<User> list4 = friendSystemDaoImpl.getUsersReceivedRequests(4);
        List<User> list5 = friendSystemDaoImpl.getUsersReceivedRequests(5);
        assertEquals(2, list1.size());
        assertEquals(0, list2.size());
        assertEquals(0, list3.size());
        assertEquals(1, list4.size());
        assertEquals(0, list5.size());
    }

    @Test
    public void getFriendshipStatusTest() throws SQLException {
        assertEquals(FriendshipStatus.accepted, friendSystemDaoImpl.getFriendshipStatus(1, 2));
        assertEquals(FriendshipStatus.accepted, friendSystemDaoImpl.getFriendshipStatus(3, 1));
        assertEquals(FriendshipStatus.accepted, friendSystemDaoImpl.getFriendshipStatus(4, 3));
        assertEquals(FriendshipStatus.accepted, friendSystemDaoImpl.getFriendshipStatus(3, 2));
        assertEquals(FriendshipStatus.pending, friendSystemDaoImpl.getFriendshipStatus(1, 5));
        assertEquals(FriendshipStatus.pending, friendSystemDaoImpl.getFriendshipStatus(4, 2));
        assertEquals(FriendshipStatus.pending, friendSystemDaoImpl.getFriendshipStatus(1, 4));
        assertNull(friendSystemDaoImpl.getFriendshipStatus(2, 5));
        assertNull(friendSystemDaoImpl.getFriendshipStatus(1, 9));
    }

    @Test
    public void sendFriendRequestTest() throws SQLException {
        List<User> list1 =  friendSystemDaoImpl.getUsersSentFriendRequests(4);
        assertEquals(1, list1.size());
        friendSystemDaoImpl.sendFriendRequest(4, 5);
        list1 =  friendSystemDaoImpl.getUsersSentFriendRequests(4);
        assertEquals(2, list1.size());
        assertEquals(FriendshipStatus.pending, friendSystemDaoImpl.getFriendshipStatus(4, 5));
    }

    @Test
    public void updateFriendshipStatusTest() throws SQLException {
        friendSystemDaoImpl.sendFriendRequest(4, 5);
        friendSystemDaoImpl.updateFriendshipStatus(4, 5, FriendshipStatus.accepted);
        assertEquals(FriendshipStatus.accepted, friendSystemDaoImpl.getFriendshipStatus(4, 5));
    }

    @Test
    public void deleteFriendTest() throws SQLException {
        friendSystemDaoImpl.deleteFriend(3, 1);
        friendSystemDaoImpl.deleteFriend(1, 2);
        friendSystemDaoImpl.deleteFriend(2, 3);
        List<User> list1 = friendSystemDaoImpl.getUsersFriends(1);
        List<User> list2 = friendSystemDaoImpl.getUsersFriends(2);
        List<User> list3 = friendSystemDaoImpl.getUsersFriends(3);
        List<User> list4 = friendSystemDaoImpl.getUsersFriends(4);
        List<User> list5 = friendSystemDaoImpl.getUsersFriends(5);
        assertEquals(0, list1.size());
        assertEquals(0, list2.size());
        assertEquals(2, list3.size());
        assertEquals(1, list4.size());
        assertEquals(1, list5.size());
    }

    @Test
    public void getSentRequestTimeTest() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO friend_requests (from_user, to_user, sent_at) VALUES" +
                "(4, 5, TIMESTAMP '"+ TIMESTAMP +"')");
        Timestamp timestamp = friendSystemDaoImpl.getSentRequestTime(4, 5);
        assertEquals(TIMESTAMP.toString().substring(0, 19), timestamp.toString().substring(0, 19));
        assertNull(friendSystemDaoImpl.getSentRequestTime(2, 8));
    }

}
