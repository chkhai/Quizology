package com.freeuni.quizapp.dao;

import com.freeuni.quizapp.dao.impl.AnswerDaoImpl;
import com.freeuni.quizapp.model.Answer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnswerDaoImplTest {
    private static Connection connection;
    private static AnswerDaoImpl answerDaoImpl;

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

        statement.executeUpdate("CREATE TABLE questions (" +
                "question_id INT AUTO_INCREMENT PRIMARY KEY," +
                "quiz_id INT NOT NULL," +
                "text TEXT NOT NULL," +
                "type VARCHAR(50)," +
                "image_url VARCHAR(255)," +
                "FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE" +
                ")");

        statement.executeUpdate("CREATE TABLE answers (" +
                "answer_id INT AUTO_INCREMENT PRIMARY KEY," +
                "question_id INT NOT NULL," +
                "answer_text TEXT NOT NULL," +
                "is_correct BOOLEAN NOT NULL," +
                "FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE" +
                ")");

        statement.close();
    }

    @BeforeEach
    public void setUpEach() throws SQLException {
        answerDaoImpl = new AnswerDaoImpl(connection);
        Statement statement = connection.createStatement();
        statement.executeUpdate("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE quizzes ALTER COLUMN quiz_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE questions ALTER COLUMN question_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE answers ALTER COLUMN answer_id RESTART WITH 1");
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
        statement.executeUpdate("INSERT INTO questions (quiz_id, text, type, image_url) VALUES " +
                "(1, 'Capital of Georgia', 'multiple_choice', null), " +
                "(1, 'Capital of France:', 'multiple_choice', null), " +
                "(2, 'What is 2+2?', 'fill_in_blank', null)");

        statement.executeUpdate("INSERT INTO answers (question_id, answer_text, is_correct) VALUES " +
                "(1, 'Tbilisi', true), " +
                "(1, 'Kutaisi', false), " +
                "(1, 'Batumi', false), " +
                "(2, 'Paris', true), " +
                "(2, 'Lyon', false), " +
                "(2, 'Marseille', false), " +
                "(3, '4', true)"
        );

        statement.close();
    }


    @AfterEach
    public void deleteFromTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM answers");
        statement.executeUpdate("DELETE FROM questions");
        statement.executeUpdate("DELETE FROM quizzes");
        statement.executeUpdate("DELETE FROM users");
        statement.close();
    }

    @Test
    public void getAnswersByQuestionIdTest() throws SQLException {
        List<Answer> answers1 = answerDaoImpl.getAnswersByQuestionId(1);
        List<Answer> answers2 = answerDaoImpl.getAnswersByQuestionId(2);
        List<Answer> answers3 = answerDaoImpl.getAnswersByQuestionId(3);
        assertEquals(3, answers1.size());
        assertEquals(3, answers2.size());
        assertEquals(1, answers3.size());
    }

    @Test
    public void addAnswerTest() throws SQLException {
        List<Answer> answers1 = answerDaoImpl.getAnswersByQuestionId(1);
        assertEquals(3, answers1.size());
        answerDaoImpl.addAnswer(1,  "Gori", false);
        answers1 = answerDaoImpl.getAnswersByQuestionId(1);
        assertEquals(4, answers1.size());
    }

    @Test
    public void updateAnswerTest() throws SQLException {
        List<Answer> answers1 = answerDaoImpl.getAnswersByQuestionId(1);
        boolean b = false;
        for (Answer a : answers1) {
            b |= a.isCorrect();
        }
        assertTrue(b);
        answerDaoImpl.updateAnswer(1, "Tbilisi", false);
        b = false;
        answers1 = answerDaoImpl.getAnswersByQuestionId(1);
        for (Answer a : answers1) {
            b |= a.isCorrect();
        }
        assertFalse(b);
        answerDaoImpl.updateAnswer(1, "Tbilisi", true);
        answers1 = answerDaoImpl.getAnswersByQuestionId(1);
        for (Answer a : answers1) {
            b |= a.isCorrect();
        }
        assertTrue(b);
    }

    @Test
    public void deleteAnswerTest() throws SQLException {
        List<Answer> answers2 = answerDaoImpl.getAnswersByQuestionId(2);
        boolean b = false;
        for (Answer a : answers2) {
            b |= a.isCorrect();
        }
        assertEquals(3, answers2.size());
        assertTrue(b);
        answerDaoImpl.deleteAnswer(2, "Paris");
        b = false;
        answers2 = answerDaoImpl.getAnswersByQuestionId(2);
        for (Answer a : answers2) {
            b |= a.isCorrect();
        }
        assertEquals(2, answers2.size());
        assertFalse(b);
    }

    @Test
    public void deleteAnswersByQuestionIdTest() throws SQLException {
        List<Answer> answers1 = answerDaoImpl.getAnswersByQuestionId(1);
        assertEquals(3, answers1.size());
        answerDaoImpl.deleteAnswersByQuestionId(1);
        answers1 = answerDaoImpl.getAnswersByQuestionId(1);
        assertEquals(0, answers1.size());
    }
}
