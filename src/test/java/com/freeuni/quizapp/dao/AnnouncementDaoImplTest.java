package com.freeuni.quizapp.dao;

import com.freeuni.quizapp.dao.impl.AnnouncementDaoImpl;
import com.freeuni.quizapp.dao.impl.FriendSystemDaoImpl;
import com.freeuni.quizapp.model.Announcement;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnnouncementDaoImplTest {

    private static Connection connection;
    private static AnnouncementDaoImpl announcementDaoImpl;

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

        statement.executeUpdate("CREATE TABLE announcements (" +
                "announcement_id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "title VARCHAR(255)," +
                "announcement_text TEXT NOT NULL," +
                "url VARCHAR(255)," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")");

        statement.close();
    }

    @BeforeEach
    public void setUpEach() throws SQLException {
        announcementDaoImpl = new AnnouncementDaoImpl(connection);
        Statement statement = connection.createStatement();
        statement.executeUpdate("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE announcements ALTER COLUMN announcement_id RESTART WITH 1");
        statement.executeUpdate("INSERT INTO users (username, hashed_password, is_admin) VALUES " +
                "('lkhiz23', 'pwd', TRUE), " +
                "('lchkh23', 'pwd2', TRUE), " +
                "('sansi23', 'pwd3', TRUE), " +
                "('akave23', 'pwd4', TRUE), " +
                "('lbegi23', 'pwd5', TRUE)");
        statement.executeUpdate("INSERT INTO announcements (user_id, title, announcement_text) VALUES " +
                "(1, 'Website Launched', 'Hey, we have launched a new website. Check it out.'), " +
                "(1, 'First ever quiz', 'Challenge your mind with first ever quiz'), " +
                "(2, 'Quiz Marathon', 'We will have quiz marathon starting in 2 days'), " +
                "(3, 'Another quiz', 'This quiz is so hard I guarantee you can''t get half of the points')");
        statement.close();
    }

    @AfterEach
    public void deleteFromTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM users");
        statement.executeUpdate("DELETE FROM announcements");
        statement.close();
    }

    @Test
    public void getAnnouncementTest() throws SQLException {
        Announcement a =  announcementDaoImpl.getAnnouncement(1);
        assertEquals("Website Launched", a.getTitle());
        assertEquals("Hey, we have launched a new website. Check it out.", a.getText());
        Announcement b =  announcementDaoImpl.getAnnouncement(2);
        assertEquals("First ever quiz", b.getTitle());
        assertEquals("Challenge your mind with first ever quiz", b.getText());
        Announcement c =  announcementDaoImpl.getAnnouncement(3);
        assertEquals("Quiz Marathon", c.getTitle());
        assertEquals("We will have quiz marathon starting in 2 days", c.getText());
        Announcement d =  announcementDaoImpl.getAnnouncement(4);
        assertEquals("Another quiz", d.getTitle());
        assertEquals("This quiz is so hard I guarantee you can't get half of the points", d.getText());
        assertNull(announcementDaoImpl.getAnnouncement(7));
    }

    @Test
    public void getAllAnnouncementTest() throws SQLException {
        List<Announcement> list = announcementDaoImpl.getAllAnnouncements();
        assertEquals(4, list.size());
    }

    @Test
    public void getUsersAnnouncementsTest() throws SQLException {
        List<Announcement> l1 = announcementDaoImpl.getUsersAnnouncements(1);
        List<Announcement> l2  = announcementDaoImpl.getUsersAnnouncements(2);
        List<Announcement> l3 = announcementDaoImpl.getUsersAnnouncements(3);
        assertEquals(2, l1.size());
        assertEquals(1, l2.size());
        assertEquals(1, l3.size());
    }

    @Test
    public void addAnnouncementTest() throws SQLException {
        List<Announcement> list = announcementDaoImpl.getAllAnnouncements();
        assertEquals(4, list.size());
        announcementDaoImpl.addAnnouncement(1, "dummy announcement", "This is a dummy announcement for testing purposes", null);
        list = announcementDaoImpl.getAllAnnouncements();
        assertEquals(5, list.size());
    }

    @Test
    public void deleteAnnouncementTest() throws SQLException {
        List<Announcement> list = announcementDaoImpl.getAllAnnouncements();
        assertEquals(4, list.size());
        assertTrue(announcementDaoImpl.deleteAnnouncement(3, "Another quiz"));
        list = announcementDaoImpl.getAllAnnouncements();
        assertEquals(3, list.size());
    }

    @Test
    public void updateAnnouncementTest() throws SQLException {
        assertEquals("Website Launched",  announcementDaoImpl.getAnnouncement(1).getTitle());
        announcementDaoImpl.updateAnnouncement(1, "New Website Launched", "text", null);
        assertEquals("New Website Launched",  announcementDaoImpl.getAnnouncement(1).getTitle());
    }
}
