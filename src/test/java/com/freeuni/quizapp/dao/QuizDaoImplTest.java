package com.freeuni.quizapp.dao;

import com.freeuni.quizapp.dao.impl.QuizDaoImpl;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuizDaoImplTest {
    private static Connection connection;
    private static QuizDaoImpl quizDaoImpl;

    private static final Timestamp TIMESTAMP1 = Timestamp.valueOf("2025-10-10 03:50:38");
    private static final Timestamp TIMESTAMP2 = Timestamp.valueOf("2025-11-12 03:50:38");

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
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")");

        statement.close();
    }

    @BeforeEach
    public void setUpEach() throws SQLException {
        quizDaoImpl = new QuizDaoImpl(connection);
        Statement statement = connection.createStatement();
        statement.executeUpdate("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE quizzes ALTER COLUMN quiz_id RESTART WITH 1");
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

        statement.close();
    }


    @AfterEach
    public void deleteFromTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM quizzes");
        statement.executeUpdate("DELETE FROM users");
        statement.close();
    }

    @Test
    public void getQuizByIdTest() throws SQLException {
        Quiz q1 =  quizDaoImpl.getQuizById(1);
        Quiz q2 =  quizDaoImpl.getQuizById(2);
        Quiz q3 =  quizDaoImpl.getQuizById(3);
        assertEquals("GEOGRAPHY", q1.getTitle());
        assertEquals("MATHS", q2.getTitle());
        assertEquals("PHYSICS", q3.getTitle());
    }

    @Test
    public void getQuizCreatorTest() throws SQLException {
        User u1 = quizDaoImpl.getQuizCreator("TC");
        User u2 = quizDaoImpl.getQuizCreator("OOP");
        User u3 = quizDaoImpl.getQuizCreator("MATHS");
        User u4 = quizDaoImpl.getQuizCreator("PHYSICS");
        assertEquals("lkhiz23", u1.getUsername());
        assertEquals("lchkh23", u2.getUsername());
        assertEquals("lchkh23", u3.getUsername());
        assertEquals("sansi23", u4.getUsername());
        assertEquals(u2, u3);
    }

    @Test
    public void findUsersCreatedQuizzesTest() throws SQLException {
        List<Quiz> quizzes1 = quizDaoImpl.findUsersCreatedQuizzes(1);
        List<Quiz> quizzes2 = quizDaoImpl.findUsersCreatedQuizzes(2);
        assertEquals(2, quizzes1.size());
        assertEquals(2, quizzes2.size());
        List<String> quizTitles1 = quizzes1.stream().map(Quiz::getTitle).toList();
        assertTrue(quizTitles1.contains("TC"));
        assertTrue(quizTitles1.contains("GEOGRAPHY"));
        List<String> quizTitles2 = quizzes2.stream().map(Quiz::getTitle).toList();
        assertTrue(quizTitles2.contains("MATHS"));
        assertTrue(quizTitles2.contains("OOP"));
    }

    @Test
    public void addQuizTest() throws SQLException {
        List<Quiz> quizzes = quizDaoImpl.findUsersCreatedQuizzes(1);
        assertEquals(2, quizzes.size());
        quizDaoImpl.addQuiz("TENNIS", "Quiz about tennis", 1);
        quizzes = quizDaoImpl.findUsersCreatedQuizzes(1);
        assertEquals(3, quizzes.size());
        quizDaoImpl.addQuiz("LOL", "Quiz about League of Legends", 1);
        quizzes = quizDaoImpl.findUsersCreatedQuizzes(1);
        assertEquals(4, quizzes.size());
        List<String> quizTitles =  quizzes.stream().map(Quiz::getTitle).toList();
        assertTrue(quizTitles.contains("LOL"));
        assertTrue(quizTitles.contains("TENNIS"));
        assertTrue(quizTitles.contains("TC"));
        assertTrue(quizTitles.contains("GEOGRAPHY"));
    }

    @Test
    public void deleteQuizTest() throws SQLException {
        List<Quiz> quizzes = quizDaoImpl.findUsersCreatedQuizzes(1);
        assertEquals(2, quizzes.size());
        quizDaoImpl.deleteQuiz("TC");
        quizzes = quizDaoImpl.findUsersCreatedQuizzes(1);
        assertEquals(1, quizzes.size());
        quizDaoImpl.deleteQuiz("GEOGRAPHY");
        quizzes = quizDaoImpl.findUsersCreatedQuizzes(1);
        assertEquals(0, quizzes.size());
        quizDaoImpl.addQuiz("LOL", "Quiz about League of Legends", 1);
        quizDaoImpl.addQuiz("TENNIS", "Quiz about League of Legends", 1);
        quizzes = quizDaoImpl.findUsersCreatedQuizzes(1);
        assertEquals(2, quizzes.size());
        quizDaoImpl.deleteQuiz("TENNIS");
        quizzes = quizDaoImpl.findUsersCreatedQuizzes(1);
        assertEquals(1, quizzes.size());
    }

    @Test
    public void getCreationTimeTest() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO quizzes (user_id, title, description, created_at) VALUES" +
                "(1, 'C#', 'Quiz about C# Programming Language', TIMESTAMP '"+ TIMESTAMP1 +"')");
        Timestamp timestamp = quizDaoImpl.getCreationTime("C#");
        assertEquals(TIMESTAMP1.toString().substring(0, 19), timestamp.toString().substring(0, 19));
        assertNull(quizDaoImpl.getCreationTime("INVALID QUIZ"));
        assertNotNull(quizDaoImpl.getCreationTime("OOP"));
    }

    @Test
    public void listRecentQuizzesTest() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO quizzes (user_id, title, description, created_at) VALUES" +
                "(1, 'C#', 'Quiz about C# Programming Language', TIMESTAMP '"+ TIMESTAMP1 +"')");
        statement.executeUpdate("INSERT INTO quizzes (user_id, title, description, created_at) VALUES" +
                "(1, 'UNIT TEST QUIZ', 'Quiz about unit testing', TIMESTAMP '"+ TIMESTAMP2 +"')");
        List<Quiz> quizzes = quizDaoImpl.listRecentQuizzes(2);
        assertEquals(2, quizzes.size());
        assertEquals("UNIT TEST QUIZ", quizzes.get(0).getTitle());
        assertEquals("C#", quizzes.get(1).getTitle());
    }

    @Test
    public void addQuizAndReturnIdTest() throws SQLException {
        assertEquals(6, quizDaoImpl.addQuizAndReturnId("new quiz", "description_1", 3));
        assertEquals(7, quizDaoImpl.addQuizAndReturnId("new quiz_2", "description_2", 1));
    }

}
