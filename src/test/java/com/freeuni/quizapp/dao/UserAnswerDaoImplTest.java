package com.freeuni.quizapp.dao;

import com.freeuni.quizapp.dao.impl.AnswerDaoImpl;
import com.freeuni.quizapp.dao.impl.UserAnswerDaoImpl;
import com.freeuni.quizapp.model.UserAnswer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserAnswerDaoImplTest {
    private static Connection connection;
    private static UserAnswerDaoImpl userAnswerDaoImpl;

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

        statement.executeUpdate("CREATE TABLE user_answers (" +
                "user_answer_id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "question_id INT NOT NULL," +
                "given_answer TEXT NOT NULL," +
                "is_correct BOOLEAN NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE" +
                ")");

        statement.close();
    }

    @BeforeEach
    public void setUpEach() throws SQLException {
        userAnswerDaoImpl = new UserAnswerDaoImpl(connection);
        Statement statement = connection.createStatement();
        statement.executeUpdate("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE quizzes ALTER COLUMN quiz_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE questions ALTER COLUMN question_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE user_answers ALTER COLUMN user_answer_id RESTART WITH 1");
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

        statement.executeUpdate("INSERT INTO user_answers (user_id, question_id, given_answer, is_correct) VALUES " +
                "(1, 1, 'Tbilisi', true), " +
                "(2, 1, 'Kutaisi', false), " +
                "(1, 1, 'Batumi', false), " +
                "(3, 2, 'Paris', true), " +
                "(4, 2, 'Lyon', false), " +
                "(3, 2, 'Marseille', false), " +
                "(5, 3, '4', true)"
        );

        statement.close();
    }


    @AfterEach
    public void deleteFromTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM user_answers");
        statement.executeUpdate("DELETE FROM questions");
        statement.executeUpdate("DELETE FROM quizzes");
        statement.executeUpdate("DELETE FROM users");
        statement.close();
    }

    @Test
    public void testGetUserAnswers() throws SQLException {
        List<UserAnswer> list = userAnswerDaoImpl.getUserAnswers(1);
        assertEquals(2, list.size());
        UserAnswer ua1 = list.get(0);
        UserAnswer ua2 = list.get(1);
        assertEquals("Tbilisi", ua1.getGivenAnswer());
        assertTrue(ua1.isCorrect());
        assertEquals("Batumi", ua2.getGivenAnswer());
        assertFalse(ua2.isCorrect());
    }

    @Test
    public void findByUserAndQuestionTest() throws SQLException {
        List<UserAnswer> list = userAnswerDaoImpl.findByUserAndQuestion(1, 1);
        assertEquals(2, list.size());
        List<Boolean> answers = new ArrayList<>();
        answers.add(list.get(0).isCorrect());
        answers.add(list.get(1).isCorrect());
        assertTrue(answers.contains(true));
        assertTrue(answers.contains(false));
    }

    @Test
    public void findByUserAndQuestion2Test() throws SQLException {
        List<UserAnswer> list = userAnswerDaoImpl.findByUserAndQuestion(3, 2);
        assertEquals(2, list.size());
        List<Boolean> answers = new ArrayList<>();
        answers.add(list.get(0).isCorrect());
        answers.add(list.get(1).isCorrect());
        assertTrue(answers.contains(true));
        assertTrue(answers.contains(false));
    }

    @Test
    public void addUserAnswerTest() throws SQLException {
        List<UserAnswer> list = userAnswerDaoImpl.findByUserAndQuestion(1, 1);
        assertEquals(2, list.size());
        userAnswerDaoImpl.addUserAnswer(1, 1, "whatever", false);
        list = userAnswerDaoImpl.findByUserAndQuestion(1, 1);
        assertEquals(3, list.size());
        int trueFrequency = (int) list.stream().filter(UserAnswer::isCorrect).count();
        int falseFrequency = list.size() - trueFrequency;
        assertEquals(2, falseFrequency);
        assertEquals(1, trueFrequency);
    }

    @Test
    public void addUserAnswer2Test() throws SQLException {
        List<UserAnswer> list = userAnswerDaoImpl.findByUserAndQuestion(3, 2);
        assertEquals(2, list.size());
        userAnswerDaoImpl.addUserAnswer(3, 2, "whatever", false);
        userAnswerDaoImpl.addUserAnswer(3, 2, "Paris", true);
        userAnswerDaoImpl.addUserAnswer(3, 2, "Paris", true);
        list = userAnswerDaoImpl.findByUserAndQuestion(3, 2);
        assertEquals(5, list.size());
        int trueFrequency = (int) list.stream().filter(UserAnswer::isCorrect).count();
        int falseFrequency = list.size() - trueFrequency;
        assertEquals(2, falseFrequency);
        assertEquals(3, trueFrequency);
    }
}
