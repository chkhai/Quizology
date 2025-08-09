package com.freeuni.quizapp.dao.impl;

import com.freeuni.quizapp.dao.interfaces.QuizDao;
import com.freeuni.quizapp.dao.interfaces.UserDao;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDaoImpl implements QuizDao {
    private final Connection con;
    private final String table_name = "quizzes";

    public QuizDaoImpl(Connection con) {
        this.con = con;
    }

    @Override
    public Quiz getQuizById(int id) throws SQLException{
        String query = "SELECT * FROM " + table_name + " WHERE quiz_id = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()) return null;
            return new Quiz(
                    rs.getInt("quiz_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("user_id"),
                    false,
                    false,
                    false,
                    false,
                    rs.getTimestamp("created_at"));
        }
    }

    @Override
    public void addQuiz(String title, String description, int creator_id) throws SQLException {
        String query = "INSERT INTO " +  table_name + " (title, description, user_id) VALUES (?, ?, ?)";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setInt(3, creator_id);
            ps.executeUpdate();
        }
    }

    @Override
    public int addQuizAndReturnId(String title, String description, int creator_id) throws SQLException {
        String query = "INSERT INTO " + table_name + " (title, description, user_id) VALUES (?, ?, ?)";
        try(PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setInt(3, creator_id);
            ps.executeUpdate();
            
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating quiz failed, no ID obtained.");
            }
        }
    }

    @Override
    public void deleteQuiz(String title) throws SQLException {
        String query = "DELETE FROM " + table_name + " WHERE title = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setString(1, title);
            ps.executeUpdate();
        }
    }

    @Override
    public User getQuizCreator(String title)  throws SQLException{
        String query = "SELECT user_id FROM " + table_name + " WHERE title = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()) return null;
            int user_id = rs.getInt("user_id");
            UserDao userDao = new UserDaoImpl(con);
            return userDao.getUser(user_id);
        }
    }

    @Override
    public List<Quiz> findUsersCreatedQuizzes(int creator_id)  throws SQLException{
        String query = "SELECT * FROM " + table_name + " WHERE user_id = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, creator_id);
            ResultSet rs = ps.executeQuery();
            return getQuizzesFromRs(rs);
        }
    }

    @Override
    public List<Quiz> listRecentQuizzes(int num)  throws SQLException{
        String query = "SELECT * FROM " + table_name + " ORDER BY created_at DESC";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ResultSet rs = ps.executeQuery();
            int cnt = 0;
            List<Quiz> res = new ArrayList<>();
            while(rs.next()){
                if(cnt == num) break;
                Quiz q = new Quiz(
                        rs.getInt("quiz_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("user_id"),
                        false,
                        false,
                        false,
                        false,
                        rs.getTimestamp("created_at"));
                res.add(q);
                cnt++;
            }
            return res;
        }
    }

    @Override
    public Timestamp getCreationTime(String title)  throws SQLException{
        String query = "SELECT created_at FROM " + table_name + " WHERE title = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()) return null;
            return rs.getTimestamp("created_at");
        }
    }

    private List<Quiz> getQuizzesFromRs(ResultSet rs) throws SQLException {
        List<Quiz> res = new ArrayList<>();
        while(rs.next()){
            Quiz q = new Quiz(
                    rs.getInt("quiz_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("user_id"),
                    false,
                    false,
                    false,
                    false,
                    rs.getTimestamp("created_at"));
            res.add(q);
        }
        return res;
    }

}
