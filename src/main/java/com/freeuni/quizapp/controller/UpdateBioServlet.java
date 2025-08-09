package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.dao.impl.UserDaoImpl;
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

@WebServlet("/updateBio")
public class UpdateBioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String newBio = request.getParameter("bio");
        if (newBio == null) {
            newBio = "";
        }
        
        if (newBio.length() > 500) {
            newBio = newBio.substring(0, 500);
        }
        
        try (Connection conn = DBConnector.getConnection()) {
            UserDaoImpl userDao = new UserDaoImpl(conn);
            userDao.changeBio(currentUser.getId(), newBio);
            
            currentUser.setBio(newBio);
            session.setAttribute("currentUser", currentUser);
            
            response.sendRedirect("profile");
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Database error while updating bio");
        }
    }
} 