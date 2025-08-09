package com.freeuni.quizapp.dao;

import com.freeuni.quizapp.dao.impl.AnnouncementDaoImpl;
import com.freeuni.quizapp.dao.impl.QuestionDaoImpl;
import com.freeuni.quizapp.enums.QuestionType;
import com.freeuni.quizapp.model.Question;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QuestionDaoImplTest {
    private static Connection connection;
    private static QuestionDaoImpl questionDaoImpl;

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

        statement.executeUpdate("CREATE TABLE questions (" +
                "question_id INT AUTO_INCREMENT PRIMARY KEY," +
                "quiz_id INT NOT NULL," +
                "text TEXT NOT NULL," +
                "type VARCHAR(50)," +
                "image_url VARCHAR(255)," +
                "FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE" +
                ")");

        statement.close();
    }

    @BeforeEach
    public void setUpEach() throws SQLException {
        questionDaoImpl = new QuestionDaoImpl(connection);
        Statement statement = connection.createStatement();
        statement.executeUpdate("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE quizzes ALTER COLUMN quiz_id RESTART WITH 1");
        statement.executeUpdate("ALTER TABLE questions ALTER COLUMN question_id RESTART WITH 1");
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
        statement.close();
    }

    @AfterEach
    public void deleteFromTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM users");
        statement.executeUpdate("DELETE FROM quizzes");
        statement.executeUpdate("DELETE FROM questions");
        statement.close();
    }

    @Test
    public void getAllQuestionsTest() throws SQLException {
        List<Question> questions = questionDaoImpl.getAllQuestions();
        assertEquals(3, questions.size());
    }

    @Test
    public void getQuizAllQuestionsTest() throws SQLException {
        List<Question> questions1 = questionDaoImpl.getQuizAllQuestions(1);
        assertEquals(2, questions1.size());
        Question q1 = questions1.get(0);
        Question q2 = questions1.get(1);
        assertEquals("Capital of Georgia", q1.getText());
        assertEquals("Capital of France:", q2.getText());
        List<Question> questions2 = questionDaoImpl.getQuizAllQuestions(2);
        assertEquals(1, questions2.size());
        Question q3 = questions2.get(0);
        assertEquals("What is 2+2?", q3.getText());
    }

    @Test
    public void addQuestionTest() throws SQLException {
        List<Question> questions = questionDaoImpl.getAllQuestions();
        assertEquals(3, questions.size());
        questionDaoImpl.addQuestion(2, "Bit harder, what is 8 squared?", QuestionType.fill_in_blank, null);
        questions = questionDaoImpl.getAllQuestions();
        assertEquals(4, questions.size());
        questionDaoImpl.addQuestion(2, "The hardest, what is 12 x (2 + 3) - 0 x 3?", QuestionType.fill_in_blank, null);
        questions = questionDaoImpl.getAllQuestions();
        assertEquals(5, questions.size());
    }

    @Test
    public void removeQuestionTest() throws SQLException {
        List<Question> questions = questionDaoImpl.getAllQuestions();
        assertEquals(3, questions.size());
        questionDaoImpl.removeQuestion("What is 2+2?");
        questions = questionDaoImpl.getAllQuestions();
        assertEquals(2, questions.size());
        List<Question> quizQuestions = questionDaoImpl.getQuizAllQuestions(2);
        assertEquals(0, quizQuestions.size());
        questionDaoImpl.removeQuestion("Capital of France:");
        questions = questionDaoImpl.getAllQuestions();
        assertEquals(1, questions.size());
    }

    @Test
    public void updateQuestionTest() throws SQLException {
        List<Question> questions = questionDaoImpl.getAllQuestions();
        questionDaoImpl.updateQuestion(1, "Capital of Georgia:"); // add : at the end
        questions =  questionDaoImpl.getAllQuestions();
        Question q1 = questions.get(0);
        System.out.println(q1.getId());
        assertEquals("Capital of Georgia:", q1.getText());
    }

    @Test
    public void addQuestionAndGetIdTest() throws SQLException {
        assertEquals(4, questionDaoImpl.addQuestionAndReturnId(4, "what is a DFA?",  QuestionType.question_response, null));
        assertEquals(5, questionDaoImpl.addQuestionAndReturnId(4, "what is 18 * 923?",  QuestionType.question_response, null));
    }

}
