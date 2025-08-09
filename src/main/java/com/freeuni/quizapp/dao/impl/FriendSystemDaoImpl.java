package com.freeuni.quizapp.dao.impl;

import com.freeuni.quizapp.dao.interfaces.FriendSystemDao;
import com.freeuni.quizapp.dao.interfaces.UserDao;
import com.freeuni.quizapp.enums.FriendshipStatus;
import com.freeuni.quizapp.model.User;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class FriendSystemDaoImpl implements FriendSystemDao {
    private final Connection con;
    private final String table_name_friends = "friends";
    private  final String table_name_requests = "friend_requests";
    private final UserDao userDao;

    public FriendSystemDaoImpl(Connection con) {
        this.con = con;
        userDao = new UserDaoImpl(con);
    }

    @Override
    public List<User> getUsersFriends(int user_id) throws SQLException {
        List<User> res = new ArrayList<>();
        String query = "SELECT friend1_user_id, friend2_user_id FROM " + table_name_friends + " WHERE friend1_user_id = ? OR friend2_user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, user_id);
            stmt.setInt(2, user_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int friend1_id = rs.getInt("friend1_user_id");
                    int friend2_id = rs.getInt("friend2_user_id");

                    int friend_id = (friend1_id == user_id) ? friend2_id : friend1_id;

                    User friend = userDao.getUser(friend_id);
                    if (friend != null) res.add(friend);
                }
            }
        }
        return res;
    }

    @Override
    public List<User> getUsersSentFriendRequests(int user_id) throws SQLException {
        List<User> res = new ArrayList<>();
        String query = "SELECT to_user FROM " + table_name_requests + " WHERE from_user = ? AND status = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, user_id);
            stmt.setString(2, FriendshipStatus.pending.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int to_user = rs.getInt("to_user");
                    User user = userDao.getUser(to_user);
                    if(user != null) res.add(user);
                }
            }
        }
        return res;
    }

    @Override
    public List<User> getUsersReceivedRequests(int user_id) throws SQLException {
        List<User> res = new ArrayList<>();
        String query = "SELECT from_user FROM " + table_name_requests + " WHERE to_user = ? AND status = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, user_id);
            stmt.setString(2, FriendshipStatus.pending.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int from_user = rs.getInt("from_user");
                    User user = userDao.getUser(from_user);
                    if(user != null) res.add(user);
                }
            }
        }
        return res;
    }

    @Override
    public FriendshipStatus getFriendshipStatus(int user_id, int friend_id) throws SQLException {
        if(areFriends(user_id, friend_id)) return FriendshipStatus.accepted;
        String query = "SELECT status FROM " + table_name_requests + " WHERE (to_user = ? AND from_user = ?) OR (to_user = ? AND from_user = ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, user_id);
            stmt.setInt(2, friend_id);
            stmt.setInt(3, friend_id);
            stmt.setInt(4, user_id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    FriendshipStatus friendshipStatus = FriendshipStatus.valueOf(status);
                    if (friendshipStatus == FriendshipStatus.rejected) {
                        return null;
                    }
                    return friendshipStatus;
                }
            }
        }
        return null;
    }

    @Override
    public void sendFriendRequest(int from_id, int to_id) throws SQLException {
        String insertQuery = "INSERT INTO " + table_name_requests + " (from_user, to_user, status) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(insertQuery)) {
            stmt.setInt(1, from_id);
            stmt.setInt(2, to_id);
            stmt.setString(3, FriendshipStatus.pending.name());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateFriendshipStatus(int user_id, int friend_id, FriendshipStatus friendship_status) throws SQLException {
        if (friendship_status == FriendshipStatus.accepted) {
            int mn = Math.min(user_id, friend_id);
            int mx = Math.max(user_id, friend_id);
            // Check if friendship already exists
            String checkQuery = "SELECT COUNT(*) FROM " + table_name_friends + " WHERE friend1_user_id = ? AND friend2_user_id = ?";
            try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, mn);
                checkStmt.setInt(2, mx);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    rs.next();
                    int count = rs.getInt(1);
                    if (count == 0) {
                        String insertFriend = "INSERT INTO " + table_name_friends + " (friend1_user_id, friend2_user_id) VALUES (?, ?)";
                        try (PreparedStatement insertStmt = con.prepareStatement(insertFriend)) {
                            insertStmt.setInt(1, mn);
                            insertStmt.setInt(2, mx);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        }

        // Update the friendship request status
        String updateRequest = "UPDATE " + table_name_requests + " SET status = ? WHERE from_user = ? AND to_user = ?";
        try (PreparedStatement ps = con.prepareStatement(updateRequest)) {
            ps.setString(1, friendship_status.name());
            ps.setInt(2, user_id);
            ps.setInt(3, friend_id);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteFriend(int user_id, int friend_id) throws SQLException {
        int mn = min(user_id, friend_id);
        int mx = max(user_id, friend_id);
        String command = "DELETE FROM " + table_name_friends +
                " WHERE (friend1_user_id = ? AND friend2_user_id = ?)";
        try (PreparedStatement ps = con.prepareStatement(command)) {
            ps.setInt(1, mn);
            ps.setInt(2, mx);
            ps.executeUpdate();
        }
    }

    @Override
    public Timestamp getSentRequestTime(int from_id, int to_id) throws SQLException {
        String query = "SELECT sent_at FROM " + table_name_requests +
                " WHERE (from_user = ? AND to_user = ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, from_id);
            stmt.setInt(2, to_id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getTimestamp("sent_at");
            }
        }
        return null;
    }

    private boolean areFriends(int userId1, int userId2) throws SQLException {
        int mn = min(userId1, userId2);
        int mx = max(userId1, userId2);
        String query = "SELECT * FROM " + table_name_friends + " WHERE friend1_user_id = ? AND friend2_user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, mn);
            stmt.setInt(2, mx);

            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) return true;
            }
        }
        return false;
    }
}
