package com.freeuni.quizapp.service.interfaces;

import com.freeuni.quizapp.model.Quiz;

import java.sql.SQLException;
import java.util.List;

public interface LeaderboardService {
    
    List<QuizLeaderboard> getAllQuizLeaderboards() throws SQLException;
    
    class LeaderboardEntry {
        private final String username;
        private final int score;
        private final int totalQuestions;
        private final double percentage;
        private final int timeTakenSeconds;
        private final String completedAt;

        public LeaderboardEntry(String username, int score, int totalQuestions, int timeTakenSeconds, String completedAt) {
            this.username = username;
            this.score = score;
            this.totalQuestions = totalQuestions;
            this.percentage = totalQuestions > 0 ? (double) score / totalQuestions * 100 : 0;
            this.timeTakenSeconds = timeTakenSeconds;
            this.completedAt = completedAt;
        }

        public String getUsername() { return username; }
        public int getScore() { return score; }
        public int getTotalQuestions() { return totalQuestions; }
        public double getPercentage() { return percentage; }
        public int getTimeTakenSeconds() { return timeTakenSeconds; }
        public String getCompletedAt() { return completedAt; }
    }

    class QuizLeaderboard {
        private final Quiz quiz;
        private final List<LeaderboardEntry> entries;

        public QuizLeaderboard(Quiz quiz, List<LeaderboardEntry> entries) {
            this.quiz = quiz;
            this.entries = entries;
        }

        public Quiz getQuiz() { return quiz; }
        public List<LeaderboardEntry> getEntries() { return entries; }
    }
} 