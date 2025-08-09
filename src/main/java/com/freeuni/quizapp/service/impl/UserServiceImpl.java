package com.freeuni.quizapp.service.impl;

import com.freeuni.quizapp.dao.impl.UserDaoImpl;
import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.service.interfaces.UserService;
import com.freeuni.quizapp.util.DBConnector;
import com.freeuni.quizapp.util.PasswordHasher;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.Instant;

public class UserServiceImpl implements UserService {
    @Override
    public User authenticate(String username, String password) throws SQLException, NoSuchAlgorithmException {
        try (Connection con = DBConnector.getConnection()) {
            UserDaoImpl userDao = new UserDaoImpl(con);
            User user = userDao.getByUsername(username, true);

            if (user == null) return null;

            String hashedInput = PasswordHasher.hashPassword(password);
            if (!hashedInput.equals(user.getHashedPassword())) return null;

            return user;
        }
    }

    @Override
    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    @Override
    public User registerUser(String username, String password, String confirmPassword)
            throws SQLException, NoSuchAlgorithmException, IllegalArgumentException {

        if (username == null || password == null || confirmPassword == null) {
            throw new IllegalArgumentException("All fields are required.");
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        try (Connection con = DBConnector.getConnection()) {
            UserDaoImpl userDao = new UserDaoImpl(con);

            if (userDao.isUsernameOccupied(username)) {
                throw new IllegalArgumentException("Username already taken.");
            }

            int nextId = 1;
            try (PreparedStatement ps = con.prepareStatement("SELECT MAX(user_id) FROM users");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nextId = rs.getInt(1) + 1;
                }
            }
            String hashedPassword = PasswordHasher.hashPassword(password);

            User newUser = new User(nextId, username, hashedPassword, false,
                    Timestamp.from(Instant.now()), "", "");

            userDao.createUser(newUser);
            return newUser;
        }
    }


}
