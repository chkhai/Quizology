package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.dao.impl.MessageDaoImpl;
import com.freeuni.quizapp.dao.impl.UserDaoImpl;
import com.freeuni.quizapp.dao.interfaces.MessageDao;
import com.freeuni.quizapp.dao.interfaces.UserDao;
import com.freeuni.quizapp.enums.MessageType;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.service.impl.SubmitQuizServiceImpl;
import com.freeuni.quizapp.service.interfaces.SubmitQuizService;
import com.freeuni.quizapp.util.DBConnector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/submitQuiz")
public class SubmitQuizServlet extends HttpServlet {

    private final SubmitQuizService submitQuizService = new SubmitQuizServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) (session != null ? session.getAttribute("currentUser") : null);

        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Quiz currentQuiz = (Quiz) session.getAttribute("currentQuiz");
        @SuppressWarnings("unchecked")
        Map<String, String> userAnswers = (Map<String, String>) session.getAttribute("quizAnswers");
        Object startTimeObj = session.getAttribute("quizStartTime");

        if (currentQuiz == null) {
            response.sendRedirect("quizzes.jsp");
            return;
        }

        try {
            Map<String, String> requestParams = new HashMap<>();
            for (String paramName : request.getParameterMap().keySet()) {
                requestParams.put(paramName, request.getParameter(paramName));
            }

            SubmitQuizService.SubmissionResult result = submitQuizService.processQuizSubmission(
                    currentUser, currentQuiz, userAnswers, startTimeObj, requestParams
            );

            request.setAttribute("quiz", result.getQuiz());
            request.setAttribute("score", result.getScore());
            request.setAttribute("totalQuestions", result.getTotalQuestions());
            request.setAttribute("timeTakenSeconds", result.getTimeTakenSeconds());
            request.setAttribute("percentage", result.getPercentage());

            Integer challengerId = (Integer) session.getAttribute("challengerId");
            Integer challengeQuizId = (Integer) session.getAttribute("challengeQuizId");
            String challengeQuizTitle = (String) session.getAttribute("challengeQuizTitle");
            
            if (challengerId != null && challengeQuizId != null && 
                challengeQuizId.equals(currentQuiz.getId())) {
                
                try (Connection conn = DBConnector.getConnection()) {
                    UserDao userDao = new UserDaoImpl(conn);
                    MessageDao messageDao = new MessageDaoImpl(conn);
                    
                    User challenger = userDao.getUser(challengerId);
                    if (challenger != null) {
                        String completionMessage = currentUser.getUsername() + " completed your quiz challenge '" + 
                                                 (challengeQuizTitle != null ? challengeQuizTitle : currentQuiz.getTitle()) + 
                                                 "' with a score of " + result.getScore() + "/" + result.getTotalQuestions() + 
                                                 " (" + String.format("%.1f", result.getPercentage()) + "%)";
                        messageDao.addMessage(currentUser.getId(), challenger.getId(), MessageType.text, completionMessage);
                    }
                } catch (SQLException e) {
                    System.err.println("Error sending challenge completion notification: " + e.getMessage());
                }
            }

            session.removeAttribute("currentQuiz");
            session.removeAttribute("quizAnswers");
            session.removeAttribute("quizStartTime");
            session.removeAttribute("isActiveQuizSession");
            session.removeAttribute("challengerId");
            session.removeAttribute("challengeQuizId");
            session.removeAttribute("challengeQuizTitle");

            request.getRequestDispatcher("quizResults.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error processing quiz submission: " + e.getMessage());
        }
    }
} 