package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.dao.impl.QuizDaoImpl;
import com.freeuni.quizapp.dao.impl.QuizResultDaoImpl;
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

@WebServlet("/adminAction")
public class AdminActionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

        String action = request.getParameter("action");
        String redirectUrl = "admin";

        try {
            switch (action) {
                case "deleteUser":
                    handleDeleteUser(request, response);
                    redirectUrl += "?message=User deleted successfully";
                    break;
                case "promoteUser":
                    handlePromoteUser(request, response);
                    redirectUrl += "?message=User promoted to admin successfully";
                    break;
                case "deleteQuiz":
                    handleDeleteQuiz(request, response);
                    redirectUrl += "?message=Quiz deleted successfully";
                    break;
                case "clearQuizHistory":
                    handleClearQuizHistory(request, response);
                    redirectUrl += "?message=Quiz history cleared successfully";
                    break;
                default:
                    redirectUrl += "?error=Invalid action";
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectUrl += "?error=" + java.net.URLEncoder.encode("Error: " + e.getMessage(), "UTF-8");
        }

        response.sendRedirect(redirectUrl);
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        String userIdParam = request.getParameter("userId");
        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }

        int userId = Integer.parseInt(userIdParam);
        
        // Prevent admin from deleting themselves
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getId() == userId) {
            throw new IllegalArgumentException("Cannot delete your own account");
        }

        try (Connection conn = DBConnector.getConnection()) {
            UserDaoImpl userDao = new UserDaoImpl(conn);
            
            // Check if user exists
            User userToDelete = userDao.getUser(userId);
            if (userToDelete == null) {
                throw new IllegalArgumentException("User not found");
            }
            
            userDao.deleteUser(userId);
        }
    }

    private void handlePromoteUser(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        String userIdParam = request.getParameter("userId");
        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }

        int userId = Integer.parseInt(userIdParam);

        try (Connection conn = DBConnector.getConnection()) {
            UserDaoImpl userDao = new UserDaoImpl(conn);
            
            // Check if user exists
            User userToPromote = userDao.getUser(userId);
            if (userToPromote == null) {
                throw new IllegalArgumentException("User not found");
            }
            
            // Check if user is already admin
            if (userToPromote.isAdmin()) {
                throw new IllegalArgumentException("User is already an admin");
            }
            
            userDao.setAsAdmin(userId, true);
        }
    }

    private void handleDeleteQuiz(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        String quizIdParam = request.getParameter("quizId");
        if (quizIdParam == null || quizIdParam.trim().isEmpty()) {
            throw new IllegalArgumentException("Quiz ID is required");
        }

        int quizId = Integer.parseInt(quizIdParam);

        try (Connection conn = DBConnector.getConnection()) {
            QuizDaoImpl quizDao = new QuizDaoImpl(conn);
            
            // Check if quiz exists and get its title
            Quiz quizToDelete = quizDao.getQuizById(quizId);
            if (quizToDelete == null) {
                throw new IllegalArgumentException("Quiz not found");
            }
            
            // Delete quiz by title (as per the existing DAO method)
            quizDao.deleteQuiz(quizToDelete.getTitle());
        }
    }

    private void handleClearQuizHistory(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        String quizIdParam = request.getParameter("quizId");
        if (quizIdParam == null || quizIdParam.trim().isEmpty()) {
            throw new IllegalArgumentException("Quiz ID is required");
        }

        int quizId = Integer.parseInt(quizIdParam);

        try (Connection conn = DBConnector.getConnection()) {
            QuizDaoImpl quizDao = new QuizDaoImpl(conn);
            QuizResultDaoImpl quizResultDao = new QuizResultDaoImpl(conn);
            
            // Check if quiz exists
            Quiz quiz = quizDao.getQuizById(quizId);
            if (quiz == null) {
                throw new IllegalArgumentException("Quiz not found");
            }
            
            // Clear all history information for this quiz
            quizResultDao.removeAllQuizResults(quizId);
        }
    }
} 