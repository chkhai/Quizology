package com.freeuni.quizapp.dao.interfaces;

import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.QuizResult;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface QuizResultDao {

    List<QuizResult> getUsersQuizResults(int user_id) throws SQLException;

    List<QuizResult> getUserQuizResults(int user_id, int quiz_id) throws SQLException;

    List<QuizResult> getQuizResults(int quiz_id) throws SQLException;

    void addQuizResult(int user_id, int quiz_id, int score, int totalQuestions, int timeTakenSeconds, boolean isPracticeMode) throws SQLException;

    void removeUsersAllQuizResults(int user_id) throws SQLException;

    void removeAllQuizResults(int quiz_id) throws SQLException;

    List<Quiz> listPopularQuizzes(int num) throws SQLException;

    int countTimesTaken(int quiz_id) throws SQLException;

    double getAverageScore(int quizId) throws SQLException;
}
