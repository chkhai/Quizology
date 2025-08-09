package com.freeuni.quizapp.model;

import com.freeuni.quizapp.enums.ActionType;

import java.sql.Timestamp;
import java.util.Objects;

public class HistoryItem {
        private int id;
        private int userId;
        private int quizId;
        private ActionType actionType;
        private Timestamp timestamp;

    public HistoryItem(int id, int userId, int quizId, ActionType at, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.actionType = at;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType at) {
        this.actionType = at;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HistoryItem that = (HistoryItem) o;
        return id == that.id && userId == that.userId && quizId == that.quizId && actionType == that.actionType && Objects.equals(timestamp, that.timestamp);
    }

}
