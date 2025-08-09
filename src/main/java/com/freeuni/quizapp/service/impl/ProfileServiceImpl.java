package com.freeuni.quizapp.service.impl;

import com.freeuni.quizapp.dao.impl.AchievementDaoImpl;
import com.freeuni.quizapp.dao.impl.QuestionDaoImpl;
import com.freeuni.quizapp.dao.impl.QuizDaoImpl;
import com.freeuni.quizapp.dao.impl.QuizResultDaoImpl;
import com.freeuni.quizapp.model.Achievement;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.QuizResult;
import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.service.interfaces.ProfileService;
import com.freeuni.quizapp.util.DBConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class ProfileServiceImpl implements ProfileService {

    @Override
    public List<Quiz> getUserCreatedQuizzes(int userId) throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(true);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            QuizDaoImpl quizDao = new QuizDaoImpl(conn);
            return quizDao.findUsersCreatedQuizzes(userId);
        }
    }

    @Override
    public Map<Integer, Integer> getQuestionCounts(List<Quiz> quizzes) throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(true);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            QuestionDaoImpl questionDao = new QuestionDaoImpl(conn);
            Map<Integer, Integer> questionCounts = new HashMap<>();

            if (quizzes != null) {
                for (Quiz quiz : quizzes) {
                    int questionCount = questionDao.getQuizAllQuestions(quiz.getId()).size();
                    questionCounts.put(quiz.getId(), questionCount);
                }
            }

            return questionCounts;
        }
    }

    @Override
    public List<QuizResult> getUserQuizResults(int userId) throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(true);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            QuizResultDaoImpl quizResultDao = new QuizResultDaoImpl(conn);
            return quizResultDao.getUsersQuizResults(userId);
        }
    }

    @Override
    public List<String> buildActivityHistory(List<QuizResult> quizResults, int maxItems) throws SQLException {
        List<String> history = new ArrayList<>();

        if (quizResults != null && !quizResults.isEmpty()) {
            try (Connection conn = DBConnector.getConnection()) {
                conn.setAutoCommit(true);
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

                QuizDaoImpl quizDao = new QuizDaoImpl(conn);

                quizResults.sort(Comparator.comparing(QuizResult::getCompletedAt).reversed());

                for (int i = 0; i < Math.min(maxItems, quizResults.size()); i++) {
                    QuizResult qr = quizResults.get(i);
                    Quiz quiz = quizDao.getQuizById(qr.getQuizId());

                    if (quiz != null) {
                        String activity = String.format(
                                "Took quiz: %s (Score: %d/%d, Time: %ds, %s, Date: %s)",
                                quiz.getTitle(),
                                qr.getScore(),
                                qr.getTotalQuestions(),
                                qr.getTimeTakenSeconds(),
                                qr.isPracticeMode() ? "Practice" : "Normal",
                                qr.getCompletedAt() != null ? qr.getCompletedAt().toString() : "Unknown"
                        );
                        history.add(activity);
                    }
                }
            }
        }
        return history;
    }

    @Override
    public List<Achievement> getUserAchievements(int userId) throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(true);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            AchievementDaoImpl achievementDao = new AchievementDaoImpl(conn);
            return achievementDao.getAchievements(userId);
        }
    }

}