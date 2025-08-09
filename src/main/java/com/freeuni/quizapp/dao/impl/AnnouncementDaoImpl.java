package com.freeuni.quizapp.dao.impl;

import com.freeuni.quizapp.dao.interfaces.AnnouncementDao;
import com.freeuni.quizapp.model.Announcement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDaoImpl implements AnnouncementDao {
    private final String table_name = "announcements";
    private Connection con;

    public AnnouncementDaoImpl(Connection con) {
        this.con = con;
    }

    @Override
    public void addAnnouncement(int user_id, String title, String text, String url) throws SQLException {
        String query = "INSERT INTO " + table_name + "(user_id, title, announcement_text, url) VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, user_id);
            ps.setString(2, title);
            ps.setString(3, text);
            ps.setString(4, url);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean deleteAnnouncement(int user_id, String title) throws SQLException {
        String query = "DELETE FROM " + table_name + " WHERE user_id = ? AND title = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, user_id);
            ps.setString(2, title);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }


    @Override
    public Announcement getAnnouncement(int an_id) throws SQLException {
        String query = "SELECT * FROM " + table_name + " WHERE announcement_id = ?";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, an_id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Announcement a = new Announcement(rs.getInt("announcement_id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("announcement_text"),
                            rs.getString("url"),
                            rs.getTimestamp("created_at")
                    );
                    return a;
                }
            }
        }
        return null;
    }

    @Override
    public List<Announcement> getAllAnnouncements() throws SQLException {
        List<Announcement> res = new ArrayList<>();
        String query = "SELECT * FROM " + table_name + " ORDER BY created_at DESC";
        try(PreparedStatement ps = con.prepareStatement(query)){
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Announcement a = new Announcement(rs.getInt("announcement_id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("announcement_text"),
                            rs.getString("url"),
                            rs.getTimestamp("created_at")
                    );
                    res.add(a);
                }
            }
        }
        return res;
    }

    @Override
    public List<Announcement> getUsersAnnouncements(int user_id) throws SQLException {
        List<Announcement> res = new ArrayList<>();
        String query = "SELECT * FROM " + table_name + " WHERE user_id = ? ORDER BY created_at DESC";
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, user_id);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Announcement a = new Announcement(rs.getInt("announcement_id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("announcement_text"),
                            rs.getString("url"),
                            rs.getTimestamp("created_at")
                    );
                    res.add(a);
                }
            }
        }
        return res;
    }

    @Override
    public void updateAnnouncement(int announcement_id, String title, String text, String url) throws SQLException {
        String query = "UPDATE " + table_name + " SET title = ?, announcement_text = ?, url = ? WHERE announcement_id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, title);
            ps.setString(2, text);
            ps.setString(3, url);
            ps.setInt(4, announcement_id);
            ps.executeUpdate();
        }
    }
}
