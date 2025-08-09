package com.freeuni.quizapp.dao.impl;

import com.freeuni.quizapp.dao.interfaces.QuestionDao;
import com.freeuni.quizapp.enums.QuestionType;
import com.freeuni.quizapp.model.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuestionDaoImpl implements QuestionDao {
    private final Connection con;
    private final String table_name = "questions";

    public QuestionDaoImpl(Connection con) {
        this.con = con;
    }

    @Override
    public void addQuestion(int quiz_id, String text, QuestionType type, String image_url) throws SQLException {
        String query = "INSERT INTO " + table_name + " (quiz_id, text, type, image_url) VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, quiz_id);
            ps.setString(2, text);
            ps.setString(3, type.name());
            ps.setString(4, image_url);
            ps.executeUpdate();
        }
    }

    @Override
    public int addQuestionAndReturnId(int quiz_id, String text, QuestionType type, String image_url) throws SQLException {
        String query = "INSERT INTO " + table_name + " (quiz_id, text, type, image_url) VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, quiz_id);
            ps.setString(2, text);
            ps.setString(3, type.name());
            ps.setString(4, image_url);
            ps.executeUpdate();
            
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating question failed, no ID obtained.");
            }
        }
    }

    @Override
    public void removeQuestion(String text) throws SQLException {
        String query = "DELETE FROM " + table_name + " WHERE text = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setString(1, text);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Question> getAllQuestions() throws SQLException {
        String query = "SELECT * FROM " + table_name + " ORDER BY question_id";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ResultSet rs = ps.executeQuery();
            List<Question> lst = getQuestionsFromRs(rs);
            return lst;
        }
    }

    @Override
    public List<Question> getQuizAllQuestions(int quiz_id) throws SQLException {
        String query = "SELECT * FROM " + table_name + " WHERE quiz_id = ? ORDER BY question_id";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, quiz_id);
            ResultSet rs = ps.executeQuery();
            List<Question> res = getQuestionsFromRs(rs);
            return res;
        }
    }

    @Override
    public void updateQuestion(int update_q_id, String text) throws SQLException {
        String updateQuery = "UPDATE " + table_name +" SET text = ? WHERE question_id = ?";

        try (PreparedStatement ps = con.prepareStatement(updateQuery)) {
            ps.setString(1, text);
            ps.setInt(2, update_q_id);
            ps.executeUpdate();
        }
    }

    private List<Question> getQuestionsFromRs(ResultSet rs) throws SQLException {
        List<Question> lst = new ArrayList<>();
        while(rs.next()){
            String q_type = rs.getString("type");
            QuestionType qt = QuestionType.valueOf(q_type);
            Question q = new Question(rs.getInt("question_id"),
                    rs.getInt("quiz_id"),
                    rs.getString("text"),
                    qt,
                    rs.getString("image_url")
            );
            lst.add(q);
        }
        return lst;
    }
}
