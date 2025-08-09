package com.freeuni.quizapp.dao.impl;

import com.freeuni.quizapp.dao.interfaces.UserDao;
import com.freeuni.quizapp.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    private final Connection con;
    private final String table_name = "users";
    private final String useridColumn = "user_id";
    private final String usernameColumn = "username";
    private final String passwordColumn = "hashed_password";
    private final String isAdminColumn = "is_admin";
    private final String createdAtColumn = "created_at";
    private final String bioColumn = "bio";
    private final String pfpUrlColumn = "profile_picture_url";

    public UserDaoImpl(Connection connection) {
        this.con = connection;
    }

    @Override
    public User getUser(int user_id) throws SQLException {
        final String command = "SELECT * FROM " + table_name + " WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(command)) {
            ps.setInt(1, user_id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return createUserFromResultSet(rs);
            }
        }
    }


    @Override
    public User getByUsername(String username, boolean exact) throws SQLException {
        String command;
        if (exact) command = "SELECT * FROM " + table_name + " WHERE " + usernameColumn + " = ?";
        else command = "SELECT * FROM " + table_name + " WHERE " + usernameColumn + " LIKE ?";

        try (PreparedStatement ps = con.prepareStatement(command)) {
            if(exact) ps.setString(1, username);
            else  ps.setString(1, "%"+username+"%");
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return createUserFromResultSet(rs);
            }
        }
    }

    @Override
    public List<User> listAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String command = "SELECT * FROM " + table_name;
        try(PreparedStatement ps = con.prepareStatement(command);
            ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User curr = createUserFromResultSet(rs);
                    users.add(curr);
                }
        }
        return users;
    }

    @Override
    public String getUsername(int user_id) throws SQLException {
        User user = getUser(user_id);
        if(user == null) return null;
        return user.getUsername();
    }

    @Override
    public Boolean isAdmin(int user_id) throws SQLException {
        User user = getUser(user_id);
        if(user == null) return null;
        return user.isAdmin();
    }

    @Override
    public Timestamp getDate(int user_id) throws SQLException {
        User user = getUser(user_id);
        if(user == null) return null;
        return user.getCreatedAt();
    }

    @Override
    public void createUser(User user) throws SQLException {
        String command = "INSERT INTO " + table_name +
                " (" + useridColumn + ", " + usernameColumn + ", " +
                passwordColumn + ", " + isAdminColumn + ", " +
                createdAtColumn + ", " + bioColumn + ", " + pfpUrlColumn + ")" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(command)) {
            ps.setInt(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getHashedPassword());
            ps.setBoolean(4, user.isAdmin());
            ps.setTimestamp(5, user.getCreatedAt());
            ps.setString(6, user.getBio());
            ps.setString(7, user.getProfilePictureUrl());
            ps.executeUpdate();
        }
    }

    @Override
    public void changePassword(int user_id, String newPassword) throws SQLException {
        String command = "UPDATE " + table_name + " SET " + passwordColumn + " = ? WHERE "+ useridColumn + " = ?";
        try(PreparedStatement ps = con.prepareStatement(command)) {
            ps.setString(1, newPassword);
            ps.setInt(2, user_id);
            ps.executeUpdate();
        }
    }

    @Override
    public void changePfp(int user_id, String newPfp) throws SQLException {
        String command = "UPDATE " + table_name + " SET " + pfpUrlColumn + " = ? WHERE "+ useridColumn + " = ?";
        try(PreparedStatement ps = con.prepareStatement(command)) {
            ps.setString(1, newPfp);
            ps.setInt(2, user_id);
            ps.executeUpdate();
        }
    }

    @Override
    public void changeBio(int user_id, String newBio) throws SQLException {
        String command = "UPDATE " + table_name + " SET " + bioColumn + " = ? WHERE "+ useridColumn + " = ?";
        try(PreparedStatement ps = con.prepareStatement(command)) {
            ps.setString(1, newBio);
            ps.setInt(2, user_id);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteUser(int user_id) throws SQLException{
        String command = "DELETE FROM " + table_name + " WHERE " + useridColumn + " = ?";
        try(PreparedStatement ps = con.prepareStatement(command)) {
            ps.setInt(1, user_id);
            ps.executeUpdate();
        }
    }

    @Override
    public void setAsAdmin(int user_id, boolean admin) throws SQLException {
        String command = "UPDATE " + table_name + " SET " + isAdminColumn + " = ? WHERE "+ useridColumn + " = ?";
        try(PreparedStatement ps = con.prepareStatement(command)) {
            ps.setBoolean(1, admin);
            ps.setInt(2, user_id);
            ps.executeUpdate();
        }
    }

    @Override
    public Boolean isUsernameOccupied(String username) throws SQLException {
        User user = getByUsername(username, true);
        return user != null;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt(useridColumn),
                rs.getString(usernameColumn),
                rs.getString(passwordColumn),
                rs.getBoolean(isAdminColumn),
                rs.getTimestamp(createdAtColumn),
                rs.getString(bioColumn),
                rs.getString(pfpUrlColumn)
        );
    }
}
