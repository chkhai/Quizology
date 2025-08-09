package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.service.impl.LeaderboardServiceImpl;
import com.freeuni.quizapp.service.interfaces.LeaderboardService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/leaderboard")
public class LeaderboardServlet extends HttpServlet {
    
    private final LeaderboardService leaderboardService = new LeaderboardServiceImpl();

    public static class LeaderboardEntry {
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

    public static class QuizLeaderboard {
        private final com.freeuni.quizapp.model.Quiz quiz;
        private final java.util.List<LeaderboardEntry> entries;

        public QuizLeaderboard(com.freeuni.quizapp.model.Quiz quiz, java.util.List<LeaderboardEntry> entries) {
            this.quiz = quiz;
            this.entries = entries;
        }

        public com.freeuni.quizapp.model.Quiz getQuiz() { return quiz; }
        public java.util.List<LeaderboardEntry> getEntries() { return entries; }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            List<LeaderboardService.QuizLeaderboard> serviceLeaderboards = leaderboardService.getAllQuizLeaderboards();
            
            List<QuizLeaderboard> servletLeaderboards = new java.util.ArrayList<>();
            for (LeaderboardService.QuizLeaderboard serviceBoard : serviceLeaderboards) {
                List<LeaderboardEntry> servletEntries = new java.util.ArrayList<>();
                for (LeaderboardService.LeaderboardEntry serviceEntry : serviceBoard.getEntries()) {
                    servletEntries.add(new LeaderboardEntry(
                        serviceEntry.getUsername(),
                        serviceEntry.getScore(),
                        serviceEntry.getTotalQuestions(),
                        serviceEntry.getTimeTakenSeconds(),
                        serviceEntry.getCompletedAt()
                    ));
                }
                servletLeaderboards.add(new QuizLeaderboard(serviceBoard.getQuiz(), servletEntries));
            }
            
            request.setAttribute("quizLeaderboards", servletLeaderboards);
            request.getRequestDispatcher("leaderboard.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Database error while loading leaderboard");
        }
    }
} 