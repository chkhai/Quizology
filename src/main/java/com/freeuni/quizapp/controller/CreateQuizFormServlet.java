package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CreateQuizFormServlet", urlPatterns = "/createQuizForm")
public class CreateQuizFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = (User) (session != null ? session.getAttribute("currentUser") : null);

        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }


        String quizType = request.getParameter("quizType");
        

        if (quizType == null || quizType.trim().isEmpty()) {
            response.sendRedirect("createQuiz.jsp");
            return;
        }


        request.getRequestDispatcher("createQuizForm.jsp").forward(request, response);
    }
} 