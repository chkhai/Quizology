package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.dao.impl.FriendSystemDaoImpl;
import com.freeuni.quizapp.dao.impl.MessageDaoImpl;
import com.freeuni.quizapp.dao.impl.UserDaoImpl;
import com.freeuni.quizapp.dao.interfaces.FriendSystemDao;
import com.freeuni.quizapp.dao.interfaces.MessageDao;
import com.freeuni.quizapp.dao.interfaces.UserDao;
import com.freeuni.quizapp.model.Message;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/inbox")
public class InboxServlet extends HttpServlet {

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
            MessageDao messageDao = new MessageDaoImpl(conn);
            FriendSystemDao friendSystemDao = new FriendSystemDaoImpl(conn);
            UserDao userDao = new UserDaoImpl(conn);

            List<User> friends = friendSystemDao.getUsersFriends(currentUser.getId());
            List<User> conversationPartners = messageDao.getInboxPeopleList(currentUser.getId());
            
            List<User> friendConversations = new ArrayList<>(friends);

            Map<Integer, Message> latestMessages = new HashMap<>();
            for (User friend : friendConversations) {
                Message latestMessage = messageDao.getLastMessage(currentUser.getId(), friend.getId());
                if (latestMessage == null) {
                    latestMessage = messageDao.getLastMessage(friend.getId(), currentUser.getId());
                }
                if (latestMessage != null) {
                    latestMessages.put(friend.getId(), latestMessage);
                }
            }

            String conversationPartnerId = request.getParameter("with");
            List<Message> conversationMessages = null;
            User conversationPartner = null;
            
            if (conversationPartnerId != null && !conversationPartnerId.trim().isEmpty()) {
                try {
                    int partnerId = Integer.parseInt(conversationPartnerId);
                    boolean isFriend = false;
                    for (User friend : friends) {
                        if (friend.getId() == partnerId) {
                            isFriend = true;
                            conversationPartner = friend;
                            break;
                        }
                    }
                    
                    if (isFriend) {
                        conversationMessages = messageDao.getMessages(currentUser.getId(), partnerId);
                    }
                } catch (NumberFormatException e) {
                }
            }

            request.setAttribute("friends", friends);
            request.setAttribute("friendConversations", friendConversations);
            request.setAttribute("latestMessages", latestMessages);
            request.setAttribute("conversationMessages", conversationMessages);
            request.setAttribute("conversationPartner", conversationPartner);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading inbox. Please try again.");
        }

        request.getRequestDispatcher("inbox.jsp").forward(request, response);
    }
} 