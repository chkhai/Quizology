package com.freeuni.quizapp.dao.interfaces;

import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.User;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface QuizDao {

    Quiz getQuizById(int id) throws SQLException;

    void addQuiz(String title, String  description, int creator_id) throws SQLException;

    int addQuizAndReturnId(String title, String description, int creator_id) throws SQLException;

    void deleteQuiz(String title) throws SQLException;

    User getQuizCreator(String title) throws SQLException;

    List<Quiz> findUsersCreatedQuizzes(int creator_id) throws SQLException;

    List<Quiz> listRecentQuizzes(int num) throws SQLException;

    Timestamp getCreationTime(String title) throws SQLException;
}
