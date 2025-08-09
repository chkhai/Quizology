package com.freeuni.quizapp.service.interfaces;

import com.freeuni.quizapp.model.Achievement;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.QuizResult;
import com.freeuni.quizapp.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ProfileService {
    List<Quiz> getUserCreatedQuizzes(int userId) throws SQLException;

    Map<Integer, Integer> getQuestionCounts(List<Quiz> quizzes) throws SQLException;

    List<QuizResult> getUserQuizResults(int userId) throws SQLException;

    List<String> buildActivityHistory(List<QuizResult> quizResults, int maxItems) throws SQLException;
    
    List<Achievement> getUserAchievements(int userId) throws SQLException;
}