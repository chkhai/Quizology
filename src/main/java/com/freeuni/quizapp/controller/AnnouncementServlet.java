package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.dao.impl.AnnouncementDaoImpl;
import com.freeuni.quizapp.dao.interfaces.AnnouncementDao;
import com.freeuni.quizapp.model.Announcement;
import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.service.impl.AnnouncementServiceImpl;
import com.freeuni.quizapp.service.interfaces.AnnouncementService;
import com.freeuni.quizapp.util.DBConnector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/announcement")
public class AnnouncementServlet extends HttpServlet {

    private AnnouncementService announcementService;

    @Override
    public void init() throws ServletException {
        announcementService = new AnnouncementServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing announcement ID");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Announcement announcement = announcementService.getAnnouncementById(id);
            if (announcement == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Announcement not found");
                return;
            }

            request.setAttribute("announcement", announcement);
            request.getRequestDispatcher("/announcement.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid announcement ID format");
        } catch (SQLException e) {
            throw new ServletException("Database error retrieving announcement", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to make announcements.");
            return;
        }

        String title = request.getParameter("title");
        String text = request.getParameter("text");
        String url = request.getParameter("url");

        if (title == null || text == null || title.trim().isEmpty() || text.trim().isEmpty()) {
            request.setAttribute("error", "Title and text are required.");
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
            return;
        }

        try {
            announcementService.addAnnouncement(
                    currentUser.getId(),
                    title.trim(),
                    text.trim(),
                    url != null ? url.trim() : ""
            );
            response.sendRedirect("/home");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
