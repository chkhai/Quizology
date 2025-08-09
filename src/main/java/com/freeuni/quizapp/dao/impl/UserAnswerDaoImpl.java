package com.freeuni.quizapp.dao.impl;

import com.freeuni.quizapp.dao.interfaces.UserAnswerDao;
import com.freeuni.quizapp.model.UserAnswer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserAnswerDaoImpl implements UserAnswerDao {
    private final Connection con;
    private final String table_name = "user_answers";

    public UserAnswerDaoImpl(Connection con) {
        this.con = con;
    }

    @Override
    public List<UserAnswer> getUserAnswers(int user_id) throws SQLException {
        String query = "SELECT * FROM " + table_name + " WHERE user_id = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, user_id);
            ResultSet rs = ps.executeQuery();
            return getUserAnswersFromRs(rs);
        }
    }

    @Override
    public void addUserAnswer(int user_id, int question_id, String given_answer, boolean is_correct) throws SQLException {
        String query = "INSERT INTO " + table_name + " (user_id, question_id, given_answer, is_correct) VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, user_id );
            ps.setInt(2, question_id);
            ps.setString(3, given_answer);
            ps.setBoolean(4, is_correct);
            ps.executeUpdate();
        }
    }

    @Override
    public List<UserAnswer> findByUserAndQuestion(int user_id, int question_id) throws SQLException {
        String  query = "SELECT * FROM " + table_name + " WHERE user_id = ? AND question_id = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, user_id);
            ps.setInt(2, question_id);
            ResultSet rs = ps.executeQuery();
            return getUserAnswersFromRs(rs);
        }
    }


    private List<UserAnswer> getUserAnswersFromRs(ResultSet rs) throws SQLException {
        List<UserAnswer> res = new ArrayList<>();
        while(rs.next()){
            UserAnswer ua = new UserAnswer(rs.getInt("user_answer_id"),
                    rs.getInt("user_id"),
                    rs.getInt("question_id"),
                    rs.getString("given_answer"),
                    rs.getBoolean("is_correct")
            );
            res.add(ua);
        }
        return res;
    }

}
