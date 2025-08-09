package com.freeuni.quizapp.service.interfaces;

import com.freeuni.quizapp.model.User;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public interface UserService {

    User authenticate(String username, String password) throws SQLException, NoSuchAlgorithmException;

    void logout(HttpSession session);

    User registerUser(String username, String password, String confirmPassword)
            throws SQLException, NoSuchAlgorithmException, IllegalArgumentException;

}
