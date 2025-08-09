package com.freeuni.quizapp.dao.impl;

import com.freeuni.quizapp.dao.interfaces.AchievementDao;
import com.freeuni.quizapp.enums.AchievementType;
import com.freeuni.quizapp.model.Achievement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AchievementDaoImpl implements AchievementDao {
    private final String table_name = "achievements";
    private Connection con;

    public AchievementDaoImpl(Connection con){
        this.con = con;
    }

    @Override
    public void addAchievement(int user_id, AchievementType type, int quiz_id) throws SQLException {
        String query = "INSERT INTO " + table_name + "(user_id, achievement_name, quiz_id) VALUES (?, ?, ?)";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, user_id);
            ps.setString(2, type.name());
            ps.setInt(3, quiz_id);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteAchievement(int user_id, AchievementType type) throws SQLException {
        String query = "DELETE FROM " + table_name +
                " WHERE (user_id = ? AND achievement_name = ?)";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, user_id);
            ps.setString(2, type.name());
            ps.executeUpdate();
        }
    }

    @Override
    public Achievement getAchievement(int user_id, AchievementType type) throws SQLException {
        String query = "SELECT * FROM " + table_name +
                " WHERE (user_id = ? AND achievement_name = ?)";
        try(PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, user_id);
            ps.setString(2, type.name());
            try(ResultSet rs = ps.executeQuery()){
                if(!rs.next()) return null;
                Achievement curr = new Achievement(rs.getInt("achievement_id"),
                        rs.getInt("user_id"),
                        AchievementType.valueOf(rs.getString("achievement_name")),
                        rs.getInt("quiz_id"),
                        rs.getTimestamp("achieved_at")
                );
                return curr;
            }
        }
    }

    @Override
    public List<Achievement> getAchievements(int user_id) throws SQLException {
        List<Achievement> res = new ArrayList<>();
        String query = "SELECT * FROM " + table_name + " WHERE user_id = ?";
        try(PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, user_id);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Achievement curr = new Achievement(rs.getInt("achievement_id"),
                            rs.getInt("user_id"),
                            AchievementType.valueOf(rs.getString("achievement_name")),
                            rs.getInt("quiz_id"),
                            rs.getTimestamp("achieved_at")
                    );
                    res.add(curr);
                }
            }
        }
        return res;
    }
}
