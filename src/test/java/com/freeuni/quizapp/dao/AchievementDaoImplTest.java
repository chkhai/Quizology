package com.freeuni.quizapp.dao;

import com.freeuni.quizapp.dao.impl.AchievementDaoImpl;
import com.freeuni.quizapp.dao.impl.FriendSystemDaoImpl;
import com.freeuni.quizapp.dao.interfaces.AchievementDao;
import com.freeuni.quizapp.enums.AchievementType;
import com.freeuni.quizapp.model.Achievement;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AchievementDaoImplTest {

    private static Connection connection;
    private static AchievementDaoImpl achievementDaoImpl;

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

        statement.executeUpdate("CREATE TABLE quizzes (" +
                "quiz_id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "title VARCHAR(255) NOT NULL," +
                "description TEXT," +
                "bio TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")");

        statement.executeUpdate("CREATE TABLE achievements (" +
                "achievement_id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "achievement_name VARCHAR(255)," +
                "quiz_id INT," +
                "achieved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE" +
                ")");

        statement.close();
    }


    @BeforeEach
    public void setUpEach() throws SQLException {
        achievementDaoImpl = new AchievementDaoImpl(connection);
        Statement statement = connection.createStatement();
        statement.executeUpdate("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE quizzes ALTER COLUMN quiz_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE achievements ALTER COLUMN achievement_id RESTART WITH 1");
        statement.executeUpdate("INSERT INTO users (username, hashed_password, is_admin) VALUES " +
                "('lkhiz23', 'pwd', TRUE), " +
                "('lchkh23', 'pwd2', TRUE), " +
                "('sansi23', 'pwd3', TRUE), " +
                "('akave23', 'pwd4', TRUE), " +
                "('lbegi23', 'pwd5', TRUE)");
        statement.executeUpdate("INSERT INTO quizzes (user_id, title, description) VALUES " +
                "(1, 'GEOGRAPHY', 'Geography Quiz'), " +
                "(2, 'MATHS', 'Math Quiz'), " +
                "(3, 'PHYSICS', 'Physics Quiz'), " +
                "(1, 'TC', 'Theoretical Computing Quiz'), " +
                "(2, 'OOP', 'OOP Quiz')");
        statement.executeUpdate("INSERT INTO achievements (user_id, achievement_name, quiz_id) VALUES " +
                "(1, 'Prolific_Author', 4), " +
                "(1, 'Quiz_Machine', 2), " +
                "(1, 'Practice_Makes_Perfect', 5), " +
                "(2, 'Amateur_Author', 2), " +
                "(3, 'Amateur_Author', 3), " +
                "(4, 'I_am_the_Greatest', 1), " +
                "(5, 'I_am_the_Greatest', 4)");
        statement.close();
    }

    @AfterEach
    public void deleteFromTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM users");
        statement.executeUpdate("DELETE FROM quizzes");
        statement.executeUpdate("DELETE FROM achievements");
        statement.close();
    }

    @Test
    public void getAchievementTest() throws SQLException {
        assertEquals("Prolific_Author", achievementDaoImpl.getAchievement(1, AchievementType.Prolific_Author).getType().toString());
        assertEquals("Quiz_Machine", achievementDaoImpl.getAchievement(1, AchievementType.Quiz_Machine).getType().toString());
        assertEquals("Amateur_Author", achievementDaoImpl.getAchievement(2, AchievementType.Amateur_Author).getType().toString());
        assertEquals("I_am_the_Greatest", achievementDaoImpl.getAchievement(5, AchievementType.I_am_the_Greatest).getType().toString());
        assertNull(achievementDaoImpl.getAchievement(5, AchievementType.Quiz_Machine));
    }

    @Test
    public void getAchievementsTest() throws SQLException {
        List<Achievement> l1 = achievementDaoImpl.getAchievements(1);
        List<Achievement> l2 = achievementDaoImpl.getAchievements(2);
        List<Achievement> l3 = achievementDaoImpl.getAchievements(3);
        List<Achievement> l4 = achievementDaoImpl.getAchievements(4);
        List<Achievement> l5 = achievementDaoImpl.getAchievements(5);
        assertEquals(3, l1.size());
        assertEquals(1, l2.size());
        assertEquals(1, l3.size());
        assertEquals(1, l4.size());
        assertEquals(1, l5.size());
    }

    @Test
    public void addAchievementTest() throws SQLException {
        List<Achievement> l2 = achievementDaoImpl.getAchievements(2);
        assertEquals(1, l2.size());
        achievementDaoImpl.addAchievement(2, AchievementType.Prodigious_Author, 5);
        l2 = achievementDaoImpl.getAchievements(2);
        assertEquals(2, l2.size());
        achievementDaoImpl.addAchievement(2,  AchievementType.Practice_Makes_Perfect, 4);
        l2 = achievementDaoImpl.getAchievements(2);
        assertEquals(3, l2.size());
        List<Achievement> l3 = achievementDaoImpl.getAchievements(3);
        assertEquals(1, l3.size());
        achievementDaoImpl.addAchievement(3,  AchievementType.Practice_Makes_Perfect, 2);
        l3 = achievementDaoImpl.getAchievements(3);
        assertEquals(2, l3.size());
    }

    @Test
    public void deleteAchievementTest() throws SQLException {
        List<Achievement> l1 = achievementDaoImpl.getAchievements(1);
        assertEquals(3, l1.size());
        achievementDaoImpl.deleteAchievement(1, AchievementType.Prolific_Author);
        l1 = achievementDaoImpl.getAchievements(1);
        assertEquals(2, l1.size());
        achievementDaoImpl.deleteAchievement(1, AchievementType.Quiz_Machine);
        l1 = achievementDaoImpl.getAchievements(1);
        assertEquals(1, l1.size());
    }
}
