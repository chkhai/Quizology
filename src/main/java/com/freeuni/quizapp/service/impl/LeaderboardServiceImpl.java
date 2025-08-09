package com.freeuni.quizapp.service.impl;

import com.freeuni.quizapp.dao.impl.QuizDaoImpl;
import com.freeuni.quizapp.dao.impl.QuizResultDaoImpl;
import com.freeuni.quizapp.dao.impl.UserDaoImpl;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.QuizResult;
import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.service.interfaces.LeaderboardService;
import com.freeuni.quizapp.util.DBConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class LeaderboardServiceImpl implements LeaderboardService {

    @Override
    public List<QuizLeaderboard> getAllQuizLeaderboards() throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            QuizDaoImpl quizDao = new QuizDaoImpl(conn);
            QuizResultDaoImpl quizResultDao = new QuizResultDaoImpl(conn);
            UserDaoImpl userDao = new UserDaoImpl(conn);
            
            List<Quiz> allQuizzes = quizDao.listRecentQuizzes(1000);
            List<QuizLeaderboard> quizLeaderboards = new ArrayList<>();
            
            for (Quiz quiz : allQuizzes) {
                List<QuizResult> quizResults = quizResultDao.getQuizResults(quiz.getId());
                
                if (quizResults.isEmpty()) {
                    continue;
                }
                
                Map<Integer, QuizResult> bestScoresByUser = getBestScoresByUser(quizResults);
                List<LeaderboardEntry> entries = createLeaderboardEntries(bestScoresByUser, userDao);
                
                if (!entries.isEmpty()) {
                    quizLeaderboards.add(new QuizLeaderboard(quiz, entries));
                }
            }
            
            quizLeaderboards.sort((a, b) -> Integer.compare(b.getEntries().size(), a.getEntries().size()));
            
            return quizLeaderboards;
        }
    }
    
    private Map<Integer, QuizResult> getBestScoresByUser(List<QuizResult> quizResults) {
        Map<Integer, QuizResult> bestScoresByUser = new HashMap<>();
        
        for (QuizResult result : quizResults) {
            if (!result.isPracticeMode()) {
                Integer userId = result.getUserId();
                QuizResult existingBest = bestScoresByUser.get(userId);
                
                if (existingBest == null || 
                    result.getScore() > existingBest.getScore() ||
                    (result.getScore() == existingBest.getScore() && 
                     result.getTimeTakenSeconds() < existingBest.getTimeTakenSeconds())) {
                    bestScoresByUser.put(userId, result);
                }
            }
        }
        
        return bestScoresByUser;
    }
    
    private List<LeaderboardEntry> createLeaderboardEntries(Map<Integer, QuizResult> bestScoresByUser, UserDaoImpl userDao) {
        List<LeaderboardEntry> entries = new ArrayList<>();
        
        for (QuizResult result : bestScoresByUser.values()) {
            try {
                User user = userDao.getUser(result.getUserId());
                if (user != null) {
                    String completedAt = result.getCompletedAt() != null ? 
                        result.getCompletedAt().toString().substring(0, 16) : "Unknown";
                    
                    entries.add(new LeaderboardEntry(
                        user.getUsername(),
                        result.getScore(),
                        result.getTotalQuestions(),
                        result.getTimeTakenSeconds(),
                        completedAt
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        entries.sort((a, b) -> {
            int scoreCompare = Integer.compare(b.getScore(), a.getScore());
            if (scoreCompare != 0) return scoreCompare;
            return Integer.compare(a.getTimeTakenSeconds(), b.getTimeTakenSeconds());
        });
        
        if (entries.size() > 10) {
            entries = entries.subList(0, 10);
        }
        
        return entries;
    }
} 