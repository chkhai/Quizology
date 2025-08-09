package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.dao.impl.FriendSystemDaoImpl;
import com.freeuni.quizapp.dao.interfaces.FriendSystemDao;
import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.util.DBConnector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/friends")
public class FriendsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) (session != null ? session.getAttribute("currentUser") : null);

        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            FriendSystemDao friendSystemDao = new FriendSystemDaoImpl(conn);
            
            List<User> friends = friendSystemDao.getUsersFriends(currentUser.getId());
            
            List<User> sentRequests = friendSystemDao.getUsersSentFriendRequests(currentUser.getId());
            
            List<User> receivedRequests = friendSystemDao.getUsersReceivedRequests(currentUser.getId());
            
            request.setAttribute("friends", friends);
            request.setAttribute("sentRequests", sentRequests);
            request.setAttribute("receivedRequests", receivedRequests);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Could not load friends data.");
        }
        
        request.getRequestDispatcher("friends.jsp").forward(request, response);
    }
} 