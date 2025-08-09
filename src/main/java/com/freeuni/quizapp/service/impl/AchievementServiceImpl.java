package com.freeuni.quizapp.service.impl;

import com.freeuni.quizapp.dao.impl.AchievementDaoImpl;
import com.freeuni.quizapp.dao.impl.QuizDaoImpl;
import com.freeuni.quizapp.dao.impl.QuizResultDaoImpl;
import com.freeuni.quizapp.enums.AchievementType;
import com.freeuni.quizapp.model.Achievement;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.QuizResult;
import com.freeuni.quizapp.service.interfaces.AchievementService;
import com.freeuni.quizapp.util.DBConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AchievementServiceImpl implements AchievementService {

    @Override
    public void checkQuizCompletionAchievements(int userId, int quizId, int score, int totalQuestions, boolean isPracticeMode) throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(true);
            
            AchievementDaoImpl achievementDao = new AchievementDaoImpl(conn);
            QuizResultDaoImpl quizResultDao = new QuizResultDaoImpl(conn);

            if (isPracticeMode && !hasAchievement(userId, AchievementType.Practice_Makes_Perfect)) {
                achievementDao.addAchievement(userId, AchievementType.Practice_Makes_Perfect, quizId);
            }


            List<QuizResult> userQuizResults = quizResultDao.getUsersQuizResults(userId);
            if (userQuizResults.size() >= 10 && !hasAchievement(userId, AchievementType.Quiz_Machine)) {
                achievementDao.addAchievement(userId, AchievementType.Quiz_Machine, quizId);
            }

            if (score > 0 && !isPracticeMode) {
                List<QuizResult> allQuizResults = quizResultDao.getQuizResults(quizId);
                boolean isHighestScore = true;
                
                for (QuizResult result : allQuizResults) {
                    if (result.getUserId() == userId || result.isPracticeMode()) {
                        continue;
                    }
                    if (result.getScore() > score) {
                        isHighestScore = false;
                        break;
                    }
                }
                if (isHighestScore && !hasAchievement(userId, AchievementType.I_am_the_Greatest)) {
                    achievementDao.addAchievement(userId, AchievementType.I_am_the_Greatest, quizId);
                }
            }
        }
    }

    @Override
    public void checkAndAwardQuizCreationAchievements(int userId, int quizId) throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(true);
            checkAndAwardQuizCreationAchievements(conn, userId, quizId);
        }
    }
    
    @Override
    public void checkAndAwardQuizCreationAchievements(Connection conn, int userId, int quizId) throws SQLException {
        AchievementDaoImpl achievementDao = new AchievementDaoImpl(conn);

        QuizDaoImpl quizDao = new QuizDaoImpl(conn);
        List<Quiz> userQuizzes = quizDao.findUsersCreatedQuizzes(userId);
        int quizCount = userQuizzes.size();

        // Use the connection-based hasAchievement method
        if (hasAchievementWithConnection(conn, userId, AchievementType.Amateur_Author)) {
            achievementDao.deleteAchievement(userId, AchievementType.Amateur_Author);
        }
        if (hasAchievementWithConnection(conn, userId, AchievementType.Prolific_Author)) {
            achievementDao.deleteAchievement(userId, AchievementType.Prolific_Author);
        }
        if (hasAchievementWithConnection(conn, userId, AchievementType.Prodigious_Author)) {
            achievementDao.deleteAchievement(userId, AchievementType.Prodigious_Author);
        }
        if (quizCount >= 10) {
            achievementDao.addAchievement(userId, AchievementType.Prodigious_Author, quizId);
        } else if (quizCount >= 5) {
            achievementDao.addAchievement(userId, AchievementType.Prolific_Author, quizId);
        } else if (quizCount >= 1) {
            achievementDao.addAchievement(userId, AchievementType.Amateur_Author, quizId);
        }
    }

    @Override
    public List<Achievement> getUserAchievements(int userId) throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(true);
            AchievementDaoImpl achievementDao = new AchievementDaoImpl(conn);
            return achievementDao.getAchievements(userId);
        }
    }

    @Override
    public boolean hasAchievement(int userId, AchievementType achievementType) throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(true);
            return hasAchievementWithConnection(conn, userId, achievementType);
        }
    }
    
    /**
     * Helper method to check achievement using an existing connection
     */
    private boolean hasAchievementWithConnection(Connection conn, int userId, AchievementType achievementType) throws SQLException {
        AchievementDaoImpl achievementDao = new AchievementDaoImpl(conn);
        Achievement achievement = achievementDao.getAchievement(userId, achievementType);
        return achievement != null;
    }

    @Override
    public String getAchievementDescription(AchievementType achievementType) {
        return switch (achievementType) {
            case Amateur_Author -> "Created your first quiz";
            case Prolific_Author -> "Created 5 quizzes";
            case Prodigious_Author -> "Created 10 quizzes";
            case Quiz_Machine -> "Completed 10 quizzes";
            case I_am_the_Greatest -> "Achieved the highest score on a quiz";
            case Practice_Makes_Perfect -> "Completed a quiz in practice mode";
        };
    }
} 