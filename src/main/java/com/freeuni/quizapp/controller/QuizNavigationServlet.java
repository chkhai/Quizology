package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.service.impl.QuizServiceImpl;
import com.freeuni.quizapp.service.interfaces.QuizService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "QuizNavigationServlet", urlPatterns = "/quizNavigate")
public class QuizNavigationServlet extends HttpServlet {

    private final QuizService quizService = new QuizServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Save current answer using QuizService
        String currentQuestionId = request.getParameter("currentQuestionId");
        if (currentQuestionId != null) {
            String answer = request.getParameter("question_" + currentQuestionId);
            quizService.saveUserAnswer(session, currentQuestionId, answer);
        }

        // Get navigation direction and current index
        String direction = request.getParameter("direction");
        String currentIndexParam = request.getParameter("currentIndex");
        
        int currentIndex = 0;
        if (currentIndexParam != null) {
            try {
                currentIndex = Integer.parseInt(currentIndexParam);
            } catch (NumberFormatException e) {
                currentIndex = 0;
            }
        }

        if ("submit".equals(direction)) {
            request.getRequestDispatcher("/submitQuiz").forward(request, response);
            return;
        }

        int newIndex = quizService.calculateNewQuestionIndex(direction, currentIndex);

        response.sendRedirect("takeQuiz.jsp?questionIndex=" + newIndex);
    }
} 