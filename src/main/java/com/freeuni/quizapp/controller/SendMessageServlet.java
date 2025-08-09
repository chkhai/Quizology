package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.dao.impl.FriendSystemDaoImpl;
import com.freeuni.quizapp.dao.impl.MessageDaoImpl;
import com.freeuni.quizapp.dao.impl.UserDaoImpl;
import com.freeuni.quizapp.dao.interfaces.FriendSystemDao;
import com.freeuni.quizapp.dao.interfaces.MessageDao;
import com.freeuni.quizapp.dao.interfaces.UserDao;
import com.freeuni.quizapp.enums.FriendshipStatus;
import com.freeuni.quizapp.enums.MessageType;
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

@WebServlet("/sendMessage")
public class SendMessageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) (session != null ? session.getAttribute("currentUser") : null);

        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String friendIdStr = request.getParameter("friendId");
        String messageText = request.getParameter("messageText");

        if (friendIdStr == null || messageText == null || messageText.trim().isEmpty()) {
            response.sendRedirect("inbox");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            UserDao userDao = new UserDaoImpl(conn);
            FriendSystemDao friendSystemDao = new FriendSystemDaoImpl(conn);
            MessageDao messageDao = new MessageDaoImpl(conn);

            int friendId = Integer.parseInt(friendIdStr);
            User friend = userDao.getUser(friendId);
            
            if (friend == null) {
                response.sendRedirect("inbox");
                return;
            }

            FriendshipStatus friendshipStatus = friendSystemDao.getFriendshipStatus(currentUser.getId(), friend.getId());
            if (friendshipStatus != FriendshipStatus.accepted) {
                response.sendRedirect("inbox");
                return;
            }

            messageDao.addMessage(currentUser.getId(), friend.getId(), MessageType.text, messageText.trim());
            
            response.sendRedirect("inbox?with=" + friendId + "&sent=true");

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("inbox");
        }
    }
} 