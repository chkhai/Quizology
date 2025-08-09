package com.freeuni.quizapp.dao.impl;

import com.freeuni.quizapp.dao.interfaces.QuizDao;
import com.freeuni.quizapp.dao.interfaces.QuizResultDao;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.QuizResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizResultDaoImpl implements QuizResultDao {
    private final String table_name = "quiz_results";
    private final Connection con;

    public QuizResultDaoImpl(Connection connection) {
        con = connection;
    }


    @Override
    public List<QuizResult> getUsersQuizResults(int user_id) throws SQLException {
        String query = "SELECT * FROM " + table_name + " WHERE user_id = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, user_id);
            ResultSet rs = ps.executeQuery();
            List<QuizResult> res = getQuizResultsFromRs(rs);
            return res;
        }
    }

    @Override
    public List<QuizResult> getUserQuizResults(int user_id, int quiz_id) throws SQLException {
        String query = "SELECT * FROM " + table_name + " WHERE user_id = ? AND quiz_id = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, user_id);
            ps.setInt(2, quiz_id);
            ResultSet rs = ps.executeQuery();
            return getQuizResultsFromRs(rs);
        }
    }

    @Override
    public List<QuizResult> getQuizResults(int quiz_id) throws SQLException {
        String query = "SELECT * FROM " + table_name + " WHERE quiz_id = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, quiz_id);
            ResultSet rs = ps.executeQuery();
            List<QuizResult> res = getQuizResultsFromRs(rs);
            return res;
        }
    }

    @Override
    public void addQuizResult(int user_id, int quiz_id, int score, int totalQuestions, int timeTakenSeconds, boolean isPracticeMode) throws SQLException {
        String query = "INSERT INTO " + table_name + " (user_id, quiz_id, total_score, total_questions, time_taken, is_practice) VALUES (?, ?, ?, ?, ?, ?)";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, user_id);
            ps.setInt(2, quiz_id);
            ps.setInt(3, score);
            ps.setInt(4, totalQuestions);
            ps.setInt(5, timeTakenSeconds);
            ps.setBoolean(6, isPracticeMode);
            ps.executeUpdate();
        }
    }

    @Override
    public void removeUsersAllQuizResults(int user_id) throws SQLException {
        String query = "DELETE FROM " + table_name + " WHERE user_id = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, user_id);
            ps.executeUpdate();
        }
    }

    @Override
    public void removeAllQuizResults(int quiz_id) throws SQLException {
        String query = "DELETE FROM " + table_name + " WHERE quiz_id = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, quiz_id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Quiz> listPopularQuizzes(int num) throws SQLException {
        List<Quiz> res = new ArrayList<>();
        String query = "SELECT quiz_id FROM "+ table_name + " GROUP BY quiz_id " +
                "ORDER BY COUNT(DISTINCT user_id) DESC";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ResultSet rs = ps.executeQuery();
            int cnt = 0;
            QuizDao quizDao = new QuizDaoImpl(con);
            while(rs.next()){
                if(cnt == num) break;
                cnt++;
                int quiz_id = rs.getInt("quiz_id");
                Quiz q = quizDao.getQuizById(quiz_id);
                res.add(q);
            }
            return res;
        }
    }

    @Override
    public int countTimesTaken(int quiz_id) throws SQLException {
        String query = "SELECT COUNT(*) AS cnt FROM "+ table_name + " WHERE quiz_id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, quiz_id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return 0;
                return rs.getInt("cnt");
            }
        }
    }

    @Override
    public double getAverageScore(int quizId) throws SQLException {
        String query = "SELECT AVG(total_score * 100.0 / total_questions) AS avg_score FROM " +
                table_name + " WHERE quiz_id = ? AND total_questions > 0";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return 0.0;
                double res = rs.getDouble("avg_score");
                return rs.wasNull() ? 0.0 : res;
            }
        }
    }

    private List<QuizResult> getQuizResultsFromRs(ResultSet rs) throws SQLException {
        List<QuizResult> res = new ArrayList<>();
        while(rs.next()){
            QuizResult curr = new QuizResult(rs.getInt("quiz_result_id"),
                    rs.getInt("user_id"),
                    rs.getInt("quiz_id"),
                    rs.getInt("total_score"),
                    rs.getInt("total_questions"),
                    rs.getInt("time_taken"),
                    rs.getBoolean("is_practice"),
                    rs.getTimestamp("completed_at")
            );
            res.add(curr);
        }
        return res;
    }

}
