package com.freeuni.quizapp.service.interfaces;

import com.freeuni.quizapp.model.Announcement;

import java.sql.SQLException;
import java.util.List;

public interface AnnouncementService {

    public List<Announcement> getRecentAnnouncements(int num) throws SQLException;

    public Announcement getAnnouncementById(int id) throws SQLException;

    void addAnnouncement(int userId, String title, String text, String url);
}
