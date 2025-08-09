package com.freeuni.quizapp.dao.interfaces;

import com.freeuni.quizapp.enums.AchievementType;
import com.freeuni.quizapp.model.Achievement;
import com.freeuni.quizapp.model.User;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface AchievementDao {

    void addAchievement(int user_id, AchievementType type, int quiz_id) throws SQLException;

    void deleteAchievement(int user_id, AchievementType type)  throws SQLException;

    Achievement getAchievement(int user_id, AchievementType type)  throws SQLException;

    List<Achievement> getAchievements(int user_id)  throws SQLException;
}
