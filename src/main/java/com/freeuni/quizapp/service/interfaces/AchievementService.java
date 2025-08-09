package com.freeuni.quizapp.service.interfaces;

import com.freeuni.quizapp.enums.AchievementType;
import com.freeuni.quizapp.model.Achievement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface AchievementService {

    void checkQuizCompletionAchievements(int userId, int quizId, int score, int totalQuestions, boolean isPracticeMode) throws SQLException;

    void checkAndAwardQuizCreationAchievements(int userId, int quizId) throws SQLException;
    
    /**
     * Check and award quiz creation achievements using an existing database connection
     * @param conn the database connection to use
     * @param userId the user ID
     * @param quizId the quiz ID that was just created
     * @throws SQLException if database error occurs
     */
    void checkAndAwardQuizCreationAchievements(Connection conn, int userId, int quizId) throws SQLException;

    List<Achievement> getUserAchievements(int userId) throws SQLException;

    boolean hasAchievement(int userId, com.freeuni.quizapp.enums.AchievementType achievementType) throws SQLException;
    

    String getAchievementDescription(AchievementType achievementType);
} 