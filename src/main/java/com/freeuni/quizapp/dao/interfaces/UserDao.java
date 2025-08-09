package com.freeuni.quizapp.dao.interfaces;

import com.freeuni.quizapp.model.User;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface UserDao {

    User getUser(int user_id) throws SQLException;

    User getByUsername(String username, boolean exact) throws SQLException;

    List<User> listAllUsers() throws SQLException;

    String getUsername(int user_id) throws SQLException;

    Boolean isAdmin(int user_id) throws SQLException;

    Timestamp getDate(int user_id) throws SQLException;

    void createUser(User user) throws SQLException;

    void changePassword(int user_id, String newPassword)  throws SQLException;

    void changePfp(int user_id, String newPfp)  throws SQLException;

    void changeBio(int user_id, String newBio)  throws SQLException;

    void deleteUser(int user_id)  throws SQLException;

    void setAsAdmin(int user_id, boolean admin)  throws SQLException;

    Boolean isUsernameOccupied(String username) throws SQLException;
}
