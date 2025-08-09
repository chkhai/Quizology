package com.freeuni.quizapp.dao.interfaces;

import com.freeuni.quizapp.enums.FriendshipStatus;
import com.freeuni.quizapp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public interface FriendSystemDao {

    List<User> getUsersFriends(int user_id) throws SQLException;

    List<User> getUsersSentFriendRequests(int user_id)  throws SQLException;

    List<User> getUsersReceivedRequests(int user_id) throws SQLException;

    FriendshipStatus getFriendshipStatus(int user_id, int friend_id) throws SQLException;

    void sendFriendRequest(int from_id, int to_id) throws SQLException;

    void updateFriendshipStatus(int user_id, int friend_id, FriendshipStatus friendship_status) throws SQLException;

    void deleteFriend(int user_id, int friend_id) throws SQLException;

    Timestamp getSentRequestTime(int from_id, int to_id) throws SQLException;
}
