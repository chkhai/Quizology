package com.freeuni.quizapp.dao;

import com.freeuni.quizapp.dao.impl.MessageDaoImpl;
import com.freeuni.quizapp.enums.MessageType;
import com.freeuni.quizapp.model.Message;
import com.freeuni.quizapp.model.User;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessageDaoImplTest {
    private static Connection connection;
    private static MessageDaoImpl messageDaoImpl;

    private static final Timestamp TIMESTAMP = Timestamp.valueOf("2025-12-12 03:50:38");

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

        statement.executeUpdate("CREATE TABLE messages (" +
                "message_id INT AUTO_INCREMENT PRIMARY KEY," +
                "from_user_id INT NOT NULL," +
                "to_user_id INT NOT NULL," +
                "type VARCHAR(100) DEFAULT 'text'," +
                "text TEXT NOT NULL," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (from_user_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY (to_user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")");

        statement.close();
    }

    @BeforeEach
    public void setUpEach() throws SQLException {
        messageDaoImpl = new MessageDaoImpl(connection);
        Statement statement = connection.createStatement();
        statement.executeUpdate("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE messages ALTER COLUMN message_id RESTART WITH 1");
        statement.executeUpdate("INSERT INTO users (username, hashed_password, is_admin) VALUES " +
                "('lkhiz23', 'pwd', TRUE), " +
                "('lchkh23', 'pwd2', TRUE), " +
                "('sansi23', 'pwd3', TRUE)");
        statement.executeUpdate("INSERT INTO messages (from_user_id, to_user_id, text) VALUES " +
                "(1, 2, 'Hey lchkh23!'), " +
                "(2, 1, 'Hey lkhiz23!'), " +
                "(3, 1, 'Yo, whats up?')");
        statement.close();
    }

    @AfterEach
    public void deleteFromTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM messages");
        statement.executeUpdate("DELETE FROM users");
        statement.close();
    }

    @Test
    public void getMessageTest() throws SQLException {
        assertEquals("Hey lchkh23!", messageDaoImpl.getMessage(1).getContent());
        assertEquals("Hey lkhiz23!", messageDaoImpl.getMessage(2).getContent());
        assertEquals("Yo, whats up?", messageDaoImpl.getMessage(3).getContent());
        assertNull(messageDaoImpl.getMessage(7));
    }

    @Test
    public void getLastMessageTest() throws SQLException {
        assertEquals("Hey lkhiz23!", messageDaoImpl.getLastMessage(2, 1).getContent());
        assertEquals("Hey lchkh23!", messageDaoImpl.getLastMessage(1, 2).getContent());
        assertNull(messageDaoImpl.getLastMessage(1, 3));
    }

    @Test
    public void addMessageTest() throws SQLException {
        messageDaoImpl.addMessage(1, 3, MessageType.text, "Nothing, wbu?");
        assertEquals("Nothing, wbu?", messageDaoImpl.getLastMessage(1, 3).getContent());
    }

    @Test
    public void removeMessageTest() throws SQLException {
        messageDaoImpl.addMessage(1, 2, MessageType.text, "I said hi");
        assertEquals("I said hi", messageDaoImpl.getLastMessage(1, 2).getContent());
        messageDaoImpl.removeMessage(4);
        assertEquals("Hey lchkh23!", messageDaoImpl.getLastMessage(1, 2).getContent());
    }

    @Test
    public void getMessagesTest() throws SQLException {
        messageDaoImpl.addMessage(1, 2, MessageType.text, "I said hi");
        messageDaoImpl.addMessage(2, 1, MessageType.text, "hi as well");
        List<Message> list = messageDaoImpl.getMessages(2, 1);
        assertEquals(4, list.size());
    }

    @Test
    public void getInboxPeopleListTest() throws SQLException {
        List<User> list =  messageDaoImpl.getInboxPeopleList(1);
        assertEquals(2, list.size());
    }

    @Test
    public void getSentMessageTimeTest() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO messages (from_user_id, to_user_id, text, timestamp) VALUES" +
                "(1, 2, 'Dummy', TIMESTAMP '"+ TIMESTAMP +"')");
        assertEquals(TIMESTAMP.toString().substring(0, 19), messageDaoImpl.getSentMessageTime(1, 2, "Dummy").toString().substring(0, 19));
        assertNull(messageDaoImpl.getSentMessageTime(1, 2, "No such message was ever sent"));
    }

}
