package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.model.Announcement;
import com.freeuni.quizapp.service.impl.HomeServiceImpl;
import com.freeuni.quizapp.service.interfaces.AnnouncementService;
import com.freeuni.quizapp.service.impl.AnnouncementServiceImpl;
import com.freeuni.quizapp.service.interfaces.HomeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private final AnnouncementService announcementService = new AnnouncementServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HomeService homeService = new HomeServiceImpl(request);

        try {
            List<Announcement> announcements = announcementService.getRecentAnnouncements(5);
            request.setAttribute("announcements", announcements);
        } catch (Exception e) {
            request.setAttribute("error", "Could not load announcements.");
            e.printStackTrace();
        }

        try {
            homeService.storeRecentQuizzes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            homeService.storePopularQuizzes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            homeService.storeFriendsActivities();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
