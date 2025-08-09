package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.service.impl.QuizServiceImpl;
import com.freeuni.quizapp.service.interfaces.QuizService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "QuizServlet", urlPatterns = "/startQuiz")
public class QuizServlet extends HttpServlet {

    private final QuizService quizService = new QuizServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String quizIdParam = request.getParameter("quizId");
        if (quizIdParam == null) {
            response.sendRedirect("quizzes.jsp");
            return;
        }

        try {
            int quizId = Integer.parseInt(quizIdParam);
            
            boolean isRandom = "true".equals(request.getParameter("random"));
            boolean isOnePage = "true".equals(request.getParameter("onePage"));
            boolean isImmediateCorrection = "true".equals(request.getParameter("immediate"));
            boolean isPracticeMode = "true".equals(request.getParameter("practice"));

            try {
                Quiz quiz = quizService.loadQuizWithQuestions(quizId, isRandom, isOnePage, isImmediateCorrection, isPracticeMode);
                
                if (quiz == null) {
                    response.sendRedirect("quizzes.jsp");
                    return;
                }
                
                // Always clear stored answers when starting any quiz
                session.removeAttribute("quizAnswers");
                session.removeAttribute("isActiveQuizSession");
                session.removeAttribute("lastQuizId");
                
                session.setAttribute("currentQuiz", quiz);
                session.setAttribute("quizStartTime", System.currentTimeMillis());
                
                request.getRequestDispatcher("takeQuiz.jsp").forward(request, response);
                
            } catch (SQLException e) {
                throw new ServletException("Database error while loading quiz", e);
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("quizzes.jsp");
        }
    }
} 