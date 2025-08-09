package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.dao.impl.QuizDaoImpl;
import com.freeuni.quizapp.dao.impl.UserDaoImpl;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.util.DBConnector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is logged in and is admin
        HttpSession session = request.getSession(false);
        User currentUser = (User) (session != null ? session.getAttribute("currentUser") : null);

        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if (!currentUser.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin privileges required.");
            return;
        }

        try {
            // Fetch all users and quizzes for admin panel
            try (Connection conn = DBConnector.getConnection()) {
                UserDaoImpl userDao = new UserDaoImpl(conn);
                QuizDaoImpl quizDao = new QuizDaoImpl(conn);

                List<User> allUsers = userDao.listAllUsers();
                List<Quiz> allQuizzes = quizDao.listRecentQuizzes(100); // Get up to 100 recent quizzes

                request.setAttribute("allUsers", allUsers);
                request.setAttribute("allQuizzes", allQuizzes);
                
                // Check for success/error messages
                String message = request.getParameter("message");
                String error = request.getParameter("error");
                if (message != null) {
                    request.setAttribute("successMessage", message);
                }
                if (error != null) {
                    request.setAttribute("errorMessage", error);
                }

                request.getRequestDispatcher("admin.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Database error while loading admin panel");
        }
    }
} 