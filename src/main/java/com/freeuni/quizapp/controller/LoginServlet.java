package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private final UserServiceImpl userService = new UserServiceImpl(); // could be injected later

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            request.setAttribute("errorMessage", "Username and password are required");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        try {
            User user = userService.authenticate(username, password);
            if (user == null) {
                request.setAttribute("errorMessage", "Invalid username or password");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            response.sendRedirect(request.getContextPath() + "/home");

        } catch (SQLException | NoSuchAlgorithmException e) {
            throw new ServletException("Login failed", e);
        }
    }
}
