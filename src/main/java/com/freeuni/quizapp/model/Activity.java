package com.freeuni.quizapp.model;

import com.freeuni.quizapp.enums.AchievementType;
import com.freeuni.quizapp.enums.ActionType;

import java.sql.Timestamp;

public class Activity {
    private User user;
    private ActionType type;
    private AchievementType achievementType;
    private Quiz quiz;
    private Timestamp timestamp;

    public Activity(User user, ActionType type, AchievementType achievementType, Timestamp timestamp) {
        this.user = user;
        this.type = type;
        this.achievementType = achievementType;
        this.timestamp = timestamp;
    }

    public Activity(User user, ActionType type, Quiz quiz,  Timestamp timestamp) {
        this.user = user;
        this.type = type;
        this.quiz = quiz;
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public ActionType getType() {
        return type;
    }

    public AchievementType getAchievementType() {
        return achievementType;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
