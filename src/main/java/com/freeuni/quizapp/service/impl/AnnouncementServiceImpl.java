package com.freeuni.quizapp.service.impl;

import com.freeuni.quizapp.dao.interfaces.AnnouncementDao;
import com.freeuni.quizapp.dao.impl.AnnouncementDaoImpl;
import com.freeuni.quizapp.model.Announcement;
import com.freeuni.quizapp.service.interfaces.AnnouncementService;
import com.freeuni.quizapp.util.DBConnector;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementDao announcementDao;

    public AnnouncementServiceImpl() {
        try {
            announcementDao = new AnnouncementDaoImpl(DBConnector.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Announcement> getRecentAnnouncements(int num) throws SQLException {
        List<Announcement> res = announcementDao.getAllAnnouncements();
        res.sort(Comparator.comparing(Announcement::getCreatedAt).reversed());
        if (res.size() > num) {
            res.subList(num, res.size()).clear();
        }
        return res;
    }

    public Announcement getAnnouncementById(int id) throws SQLException {
        return announcementDao.getAnnouncement(id);
    }

    public void addAnnouncement(int userId, String title, String text, String url) {
        try {
            announcementDao.addAnnouncement(userId, title, text, url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
