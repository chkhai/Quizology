package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.dao.impl.QuizDaoImpl;
import com.freeuni.quizapp.dao.impl.UserDaoImpl;
import com.freeuni.quizapp.dao.impl.FriendSystemDaoImpl;
import com.freeuni.quizapp.enums.FriendshipStatus;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.util.DBConnector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = (User) (session != null ? session.getAttribute("currentUser") : null);
        
        String query = request.getParameter("q");
        String type = request.getParameter("type");
        
        if (query == null || query.trim().isEmpty()) {
            response.sendRedirect("quizzes.jsp");
            return;
        }
        
        query = query.trim();
        if (type == null) type = "all";
        
        List<User> foundUsers = new ArrayList<>();
        List<Quiz> foundQuizzes = new ArrayList<>();
        Map<Integer, FriendshipStatus> friendshipStatuses = new HashMap<>();
        
        try (Connection conn = DBConnector.getConnection()) {
            UserDaoImpl userDao = new UserDaoImpl(conn);
            QuizDaoImpl quizDao = new QuizDaoImpl(conn);
            FriendSystemDaoImpl friendSystemDao = new FriendSystemDaoImpl(conn);
            
            if ("users".equals(type) || "all".equals(type)) {
                foundUsers = searchUsers(userDao, query);
                
                if (currentUser != null) {
                    for (User user : foundUsers) {
                        if (user.getId() != currentUser.getId()) {
                            try {
                                FriendshipStatus status = friendSystemDao.getFriendshipStatus(currentUser.getId(), user.getId());
                                friendshipStatuses.put(user.getId(), status);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                friendshipStatuses.put(user.getId(), null);
                            }
                        }
                    }
                }
            }
            
            if ("quizzes".equals(type) || "all".equals(type)) {
                foundQuizzes = searchQuizzes(quizDao, query);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Database error occurred during search");
            return;
        }
        
        request.setAttribute("searchQuery", query);
        request.setAttribute("searchType", type);
        request.setAttribute("foundUsers", foundUsers);
        request.setAttribute("foundQuizzes", foundQuizzes);
        request.setAttribute("friendshipStatuses", friendshipStatuses);
        
        request.getRequestDispatcher("searchResults.jsp").forward(request, response);
    }
    
    private List<User> searchUsers(UserDaoImpl userDao, String query) throws SQLException {
        List<User> results = new ArrayList<>();
        
        List<User> allUsers = userDao.listAllUsers();
        for (User user : allUsers) {
            if (user.getUsername() != null && 
                user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                results.add(user);
            }
        }
        
        return results;
    }
    
    private List<Quiz> searchQuizzes(QuizDaoImpl quizDao, String query) throws SQLException {
        List<Quiz> results = new ArrayList<>();
        
        List<Quiz> allQuizzes = quizDao.listRecentQuizzes(1000);
        
        for (Quiz quiz : allQuizzes) {
            boolean titleMatch = quiz.getTitle() != null && 
                                quiz.getTitle().toLowerCase().contains(query.toLowerCase());
            boolean descMatch = quiz.getDescription() != null && 
                               quiz.getDescription().toLowerCase().contains(query.toLowerCase());
            
            if (titleMatch || descMatch) {
                results.add(quiz);
            }
        }
        
        return results;
    }
} 