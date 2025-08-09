package com.freeuni.quizapp.dao.interfaces;

import com.freeuni.quizapp.model.Announcement;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface AnnouncementDao {

    void addAnnouncement(int user_id, String title, String text, String url) throws SQLException;

    boolean deleteAnnouncement(int user_id, String title)  throws SQLException;

    Announcement getAnnouncement(int an_id) throws SQLException;

    List<Announcement> getAllAnnouncements()  throws SQLException;

    List<Announcement> getUsersAnnouncements(int user_id)  throws SQLException;

    public void updateAnnouncement(int announcement_id, String title, String text, String url) throws SQLException;
}
