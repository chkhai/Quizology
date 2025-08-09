package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.dao.impl.FriendSystemDaoImpl;
import com.freeuni.quizapp.dao.impl.UserDaoImpl;
import com.freeuni.quizapp.dao.interfaces.FriendSystemDao;
import com.freeuni.quizapp.dao.interfaces.UserDao;
import com.freeuni.quizapp.enums.FriendshipStatus;
import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.util.DBConnector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/friendRequest")
public class FriendRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) (session != null ? session.getAttribute("currentUser") : null);

        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String targetUsernameParam = request.getParameter("targetUsername");
        String targetUserIdParam = request.getParameter("targetUserId");
        
        if (action == null) {
            response.sendRedirect("friends");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            FriendSystemDao friendSystemDao = new FriendSystemDaoImpl(conn);
            UserDao userDao = new UserDaoImpl(conn);
            
            int targetUserId = -1;
            
            if (targetUserIdParam != null) {
                try {
                    targetUserId = Integer.parseInt(targetUserIdParam);
                } catch (NumberFormatException e) {
                    response.sendRedirect("friends");
                    return;
                }
            } else if (targetUsernameParam != null) {
                User targetUser = userDao.getByUsername(targetUsernameParam, true);
                if (targetUser != null) {
                    targetUserId = targetUser.getId();
                }
            }
            
            if (targetUserId == -1 || targetUserId == currentUser.getId()) {
                response.sendRedirect("friends");
                return;
            }

            switch (action) {
                case "send":
                    try (PreparedStatement cleanupStmt = conn.prepareStatement(
                        "DELETE t1 FROM friend_requests t1 " +
                        "INNER JOIN friend_requests t2 " +
                        "WHERE t1.friend_request_id < t2.friend_request_id " +
                        "AND t1.from_user = ? AND t1.to_user = ? " +
                        "AND t2.from_user = ? AND t2.to_user = ?")) {
                        cleanupStmt.setInt(1, currentUser.getId());
                        cleanupStmt.setInt(2, targetUserId);
                        cleanupStmt.setInt(3, currentUser.getId());
                        cleanupStmt.setInt(4, targetUserId);
                        cleanupStmt.executeUpdate();
                    }
                    
                    try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM friend_requests WHERE from_user = ? AND to_user = ? AND status = 'pending'")) {
                        checkStmt.setInt(1, currentUser.getId());
                        checkStmt.setInt(2, targetUserId);
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            rs.next();
                            int existingCount = rs.getInt(1);
                            if (existingCount == 0) {
                                friendSystemDao.sendFriendRequest(currentUser.getId(), targetUserId);
                            }
                        }
                    }
                    break;
                    
                case "accept":
                    friendSystemDao.updateFriendshipStatus(targetUserId, currentUser.getId(), FriendshipStatus.accepted);
                    break;
                    
                case "reject":
                    friendSystemDao.updateFriendshipStatus(targetUserId, currentUser.getId(), FriendshipStatus.rejected);
                    break;
                    
                case "remove":
                    friendSystemDao.deleteFriend(currentUser.getId(), targetUserId);
                    try (PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM friend_requests WHERE from_user = ? AND to_user = ?");
                         PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM friend_requests WHERE from_user = ? AND to_user = ?")) {
                        stmt1.setInt(1, currentUser.getId());
                        stmt1.setInt(2, targetUserId);
                        stmt1.executeUpdate(); 
                        stmt2.setInt(1, targetUserId);
                        stmt2.setInt(2, currentUser.getId());
                        stmt2.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String referer = request.getHeader("Referer");
        if (referer != null && (referer.contains("profile") || referer.contains("search"))) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect("friends");
        }
    }
} 