package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.dao.impl.FriendSystemDaoImpl;
import com.freeuni.quizapp.dao.impl.MessageDaoImpl;
import com.freeuni.quizapp.dao.impl.QuizDaoImpl;
import com.freeuni.quizapp.dao.impl.UserDaoImpl;
import com.freeuni.quizapp.dao.interfaces.FriendSystemDao;
import com.freeuni.quizapp.dao.interfaces.MessageDao;
import com.freeuni.quizapp.dao.interfaces.QuizDao;
import com.freeuni.quizapp.dao.interfaces.UserDao;
import com.freeuni.quizapp.enums.FriendshipStatus;
import com.freeuni.quizapp.enums.MessageType;
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

@WebServlet("/challenge")
public class ChallengeServlet extends HttpServlet {

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
        
        if ("send".equals(action)) {
            handleSendChallenge(request, response, currentUser);
        } else if ("accept".equals(action)) {
            handleAcceptChallenge(request, response, currentUser);
        } else if ("decline".equals(action)) {
            handleDeclineChallenge(request, response, currentUser);
        } else {
            response.sendRedirect("inbox");
        }
    }

    private void handleSendChallenge(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        
        String friendUsername = request.getParameter("friendUsername");
        String quizIdStr = request.getParameter("quizId");
        String redirectUrl = request.getParameter("redirectUrl");
        
        if (friendUsername == null || quizIdStr == null) {
            response.sendRedirect(redirectUrl != null ? redirectUrl : "quizzes.jsp");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            UserDao userDao = new UserDaoImpl(conn);
            FriendSystemDao friendSystemDao = new FriendSystemDaoImpl(conn);
            QuizDao quizDao = new QuizDaoImpl(conn);
            MessageDao messageDao = new MessageDaoImpl(conn);

            User friend = userDao.getByUsername(friendUsername, false);
            if (friend == null) {
                response.sendRedirect(redirectUrl != null ? redirectUrl : "quizzes.jsp");
                return;
            }

            FriendshipStatus friendshipStatus = friendSystemDao.getFriendshipStatus(currentUser.getId(), friend.getId());
            if (friendshipStatus != FriendshipStatus.accepted) {
                response.sendRedirect(redirectUrl != null ? redirectUrl : "quizzes.jsp");
                return;
            }

            int quizId = Integer.parseInt(quizIdStr);
            Quiz quiz = quizDao.getQuizById(quizId);
            if (quiz == null) {
                response.sendRedirect(redirectUrl != null ? redirectUrl : "quizzes.jsp");
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("QUIZ_CHALLENGE:");
            sb.append(quizId);
            sb.append(":");
            sb.append(quiz.getTitle());
            String challengeText = sb.toString();
            messageDao.addMessage(currentUser.getId(), friend.getId(), MessageType.challenge, challengeText);
            if (redirectUrl != null && !redirectUrl.trim().isEmpty()) {
                response.sendRedirect(redirectUrl + "?challengeSent=true");
            } else {
                response.sendRedirect("inbox?challengeSent=true");
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(redirectUrl != null ? redirectUrl : "quizzes.jsp");
        }
    }

    private void handleAcceptChallenge(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        
        String messageIdStr = request.getParameter("messageId");
        
        if (messageIdStr == null) {
            response.sendRedirect("inbox");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            MessageDao messageDao = new MessageDaoImpl(conn);
            FriendSystemDao friendSystemDao = new FriendSystemDaoImpl(conn);

            int messageId = Integer.parseInt(messageIdStr);
            var message = messageDao.getMessage(messageId);
            
            if (message == null || message.getReceiverId() != currentUser.getId()) {
                response.sendRedirect("inbox");
                return;
            }

            FriendshipStatus friendshipStatus = friendSystemDao.getFriendshipStatus(currentUser.getId(), message.getSenderId());
            if (friendshipStatus != FriendshipStatus.accepted) {
                response.sendRedirect("inbox");
                return;
            }
            String content = message.getContent();
            if (content.startsWith("QUIZ_CHALLENGE:")) {
                String[] parts = content.split(":", 3);
                if (parts.length >= 2) {
                    try {
                        int quizId = Integer.parseInt(parts[1]);
                        String quizTitle = parts.length >= 3 ? parts[2] : "Quiz";
                        
                        HttpSession session = request.getSession();
                        session.setAttribute("challengerId", message.getSenderId());
                        session.setAttribute("challengeQuizId", quizId);
                        session.setAttribute("challengeQuizTitle", quizTitle);
                        
                        UserDao userDao = new UserDaoImpl(conn);
                        User challenger = userDao.getUser(message.getSenderId());
                        if (challenger != null) {
                            String acceptanceMessage = currentUser.getUsername() + " accepted your quiz challenge: " + quizTitle;
                            messageDao.addMessage(currentUser.getId(), challenger.getId(), MessageType.text, acceptanceMessage);
                        }
                        
                        messageDao.removeMessage(messageId);
                        
                        String url = "quizzes.jsp#settings_" + String.valueOf(quizId);
                        response.sendRedirect(url);
                        return;
                    } catch (NumberFormatException e) {
                    }
                }
            }
            
            response.sendRedirect("inbox");

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("inbox");
        }
    }

    private void handleDeclineChallenge(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        
        String messageIdStr = request.getParameter("messageId");
        
        if (messageIdStr == null) {
            response.sendRedirect("inbox");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            MessageDao messageDao = new MessageDaoImpl(conn);

            int messageId = Integer.parseInt(messageIdStr);
            var message = messageDao.getMessage(messageId);
            
            if (message != null && message.getReceiverId() == currentUser.getId()) {
                messageDao.removeMessage(messageId);
            }
            
            response.sendRedirect("inbox?challengeDeclined=true");

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("inbox");
        }
    }
} 