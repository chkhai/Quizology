package com.freeuni.quizapp.service.interfaces;

import com.freeuni.quizapp.model.User;

import java.sql.SQLException;

public interface HomeService {

    void storePopularQuizzes() throws SQLException;

    void storeRecentQuizzes() throws SQLException;

    void storeRecentlyTakenQuizzes() throws SQLException;

    void storeRecentlyCreatedQuizzes() throws SQLException;

    void storeAchievements() throws SQLException;

    void storeMessages() throws SQLException;

    void storeFriendsActivities() throws SQLException;

}
