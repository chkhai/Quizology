package com.freeuni.quizapp.dao;

import com.freeuni.quizapp.dao.impl.QuizResultDaoImpl;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.QuizResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QuizResultDaoImplTest {
    private static Connection connection;
    private static QuizResultDaoImpl quizresultDaoImpl;

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

        statement.executeUpdate("CREATE TABLE quiz_results (" +
                "quiz_result_id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "quiz_id INT NOT NULL," +
                "total_score INT NOT NULL," +
                "total_questions INT NOT NULL," +
                "time_taken INT," +
                "is_practice BOOLEAN DEFAULT FALSE," +
                "completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE" +
                ")");

        statement.close();
    }

    @BeforeEach
    public void setUpEach() throws SQLException {
        quizresultDaoImpl = new QuizResultDaoImpl(connection);
        Statement statement = connection.createStatement();
        statement.executeUpdate("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE quizzes ALTER COLUMN quiz_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE quiz_results ALTER COLUMN quiz_result_id RESTART WITH 1");
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
        statement.executeUpdate("INSERT INTO quiz_results (user_id, quiz_id, total_score, total_questions, time_taken, is_practice) VALUES " +
                "(1, 1, 8, 10, 120, false), " +
                "(2, 1, 10, 10, 90, false), " +
                "(3, 2, 6, 10, 150, true), " +
                "(1, 2, 9, 10, 80, false), " +
                "(2, 3, 7, 10, 110, true)");

        statement.close();
    }

    @AfterEach
    public void deleteFromTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM users");
        statement.executeUpdate("DELETE FROM quizzes");
        statement.executeUpdate("DELETE FROM quiz_results");
        statement.close();
    }

    @Test
    public void getUsersQuizResultsTest() throws SQLException {
        List<QuizResult> results = quizresultDaoImpl.getUsersQuizResults(1);
        assertEquals(2, results.size());
        int trueFrequency = (int) results.stream().filter(QuizResult::isPracticeMode).count();
        int falseFrequency = results.size() - trueFrequency;
        List<Integer> scores = results.stream().map(QuizResult::getScore).toList();
        assertEquals(2, falseFrequency);
        assertEquals(0, trueFrequency);
        assertTrue(scores.contains(8));
        assertTrue(scores.contains(9));
    }

    @Test
    public void getQuizResults() throws SQLException {
        List<QuizResult> results = quizresultDaoImpl.getQuizResults(1);
        assertEquals(2, results.size());
        int trueFrequency = (int) results.stream().filter(QuizResult::isPracticeMode).count();
        int falseFrequency = results.size() - trueFrequency;
        List<Integer> scores = results.stream().map(QuizResult::getScore).toList();
        assertEquals(2, falseFrequency);
        assertEquals(0, trueFrequency);
        assertTrue(scores.contains(8));
        assertTrue(scores.contains(10));
    }

    @Test
    public void addQuizResultTest() throws SQLException {
        List<QuizResult> results = quizresultDaoImpl.getQuizResults(1);
        assertEquals(2, results.size());
        quizresultDaoImpl.addQuizResult(3, 1, 7, 10, 40, true);
        results = quizresultDaoImpl.getQuizResults(1);
        assertEquals(3, results.size());
        quizresultDaoImpl.addQuizResult(5, 1, 9, 10, 70, false);
        results = quizresultDaoImpl.getQuizResults(1);
        assertEquals(4, results.size());
        int trueFrequency = (int) results.stream().filter(QuizResult::isPracticeMode).count();
        int falseFrequency = results.size() - trueFrequency;
        List<Integer> scores = results.stream().map(QuizResult::getScore).toList();
        assertEquals(3, falseFrequency);
        assertEquals(1, trueFrequency);
        assertTrue(scores.contains(7));
        assertTrue(scores.contains(8));
        assertTrue(scores.contains(9));
        assertTrue(scores.contains(10));
    }

    @Test
    public void removeAllQuizResultsTest() throws SQLException {
        List<QuizResult> results = quizresultDaoImpl.getQuizResults(1);
        assertEquals(2, results.size());
        quizresultDaoImpl.addQuizResult(3, 1, 7, 10, 40, true);
        results = quizresultDaoImpl.getQuizResults(1);
        assertEquals(3, results.size());
        quizresultDaoImpl.addQuizResult(5, 1, 9, 10, 70, false);
        results = quizresultDaoImpl.getQuizResults(1);
        assertEquals(4, results.size());
        quizresultDaoImpl.removeAllQuizResults(1);
        results = quizresultDaoImpl.getQuizResults(1);
        assertEquals(0, results.size());
        assertEquals(2,  quizresultDaoImpl.getQuizResults(2).size());
        quizresultDaoImpl.removeAllQuizResults(2);
        assertEquals(0, quizresultDaoImpl.getQuizResults(2).size());
    }

    @Test
    public void getUserQuizResults() throws SQLException {
        quizresultDaoImpl.addQuizResult(1, 1, 7, 10, 40, true);
        quizresultDaoImpl.addQuizResult(1, 1, 9, 10, 35, false);
        quizresultDaoImpl.addQuizResult(1, 1, 10, 10, 30, false);
        List<QuizResult> results = quizresultDaoImpl.getUserQuizResults(1, 1);
        assertEquals(4, results.size());
        int trueFrequency = (int) results.stream().filter(QuizResult::isPracticeMode).count();
        int falseFrequency = results.size() - trueFrequency;
        List<Integer> scores = results.stream().map(QuizResult::getScore).toList();
        assertEquals(3, falseFrequency);
        assertEquals(1, trueFrequency);
        assertTrue(scores.contains(7));
        assertTrue(scores.contains(8));
        assertTrue(scores.contains(9));
        assertTrue(scores.contains(10));
    }

    @Test
    public void removeUsersAllQuizResultsTest() throws SQLException {
        List<QuizResult> results = quizresultDaoImpl.getUsersQuizResults(1);
        assertEquals(2, results.size());
        quizresultDaoImpl.removeUsersAllQuizResults(1);
        results = quizresultDaoImpl.getUsersQuizResults(1);
        assertEquals(0, results.size());
        quizresultDaoImpl.addQuizResult(1, 1, 7, 10, 40, true);
        results = quizresultDaoImpl.getUsersQuizResults(1);
        assertEquals(1, results.size());
        results = quizresultDaoImpl.getUsersQuizResults(2);
        assertEquals(2, results.size());
        quizresultDaoImpl.removeUsersAllQuizResults(2);
        results = quizresultDaoImpl.getUsersQuizResults(2);
        assertEquals(0, results.size());
    }

    @Test
    public void countTimesTakenTest() throws SQLException {
        int q1 = quizresultDaoImpl.countTimesTaken(1);
        int q2 = quizresultDaoImpl.countTimesTaken(2);
        int q3 = quizresultDaoImpl.countTimesTaken(3);
        assertEquals(2, q1);
        assertEquals(2, q2);
        assertEquals(1, q3);
    }

    @Test
    public void getAverageScoreTest() throws SQLException {
        double avgScore1 = quizresultDaoImpl.getAverageScore(1);
        double avgScore2 = quizresultDaoImpl.getAverageScore(2);
        double avgScore3 = quizresultDaoImpl.getAverageScore(3);
        assertEquals(90.0, avgScore1);
        assertEquals(75.0, avgScore2);
        assertEquals(70.0, avgScore3);
    }

    @Test
    public void listPopularQuizzesTest() throws SQLException {
        quizresultDaoImpl.addQuizResult(1, 1, 9, 10, 40, false);
        List<Quiz> popularQuizzes = quizresultDaoImpl.listPopularQuizzes(4);
        assertEquals(3, popularQuizzes.size());
        List<Integer> ids = popularQuizzes.stream().map(Quiz::getId).toList();
        assertEquals(1, ids.get(0));
        assertEquals(2, ids.get(1));
        assertEquals(3, ids.get(2));

    }

}
