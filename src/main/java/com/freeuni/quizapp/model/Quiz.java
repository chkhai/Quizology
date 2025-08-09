package com.freeuni.quizapp.model;

import com.freeuni.quizapp.dao.impl.QuestionDaoImpl;
import com.freeuni.quizapp.dao.interfaces.QuestionDao;
import com.freeuni.quizapp.util.DBConnector;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class Quiz {
        private int id;
        private String title;
        private String description;
        private int creatorId;
        private boolean isRandom;
        private boolean isOnePage;
        private boolean isImmediateCorrection;
        private boolean isPracticeModeEnabled;
        private Timestamp createdAt;
        private List<Question> questions;

    public Quiz(int id, String title, String description, int creatorId, boolean isRandom,
                boolean isOnePage, boolean isImmediateCorrection, boolean isPracticeModeEnabled,
                Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creatorId = creatorId;
        this.isRandom = isRandom;
        this.isOnePage = isOnePage;
        this.isImmediateCorrection = isImmediateCorrection;
        this.isPracticeModeEnabled = isPracticeModeEnabled;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }

    public boolean isOnePage() {
        return isOnePage;
    }

    public void setOnePage(boolean onePage) {
        isOnePage = onePage;
    }

    public boolean isImmediateCorrection() {
        return isImmediateCorrection;
    }

    public void setImmediateCorrection(boolean immediateCorrection) {
        isImmediateCorrection = immediateCorrection;
    }

    public boolean isPracticeModeEnabled() {
        return isPracticeModeEnabled;
    }

    public void setPracticeModeEnabled(boolean practiceModeEnabled) {
        isPracticeModeEnabled = practiceModeEnabled;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        return id == quiz.id && creatorId == quiz.creatorId && isRandom == quiz.isRandom && isOnePage == quiz.isOnePage && isImmediateCorrection == quiz.isImmediateCorrection && isPracticeModeEnabled == quiz.isPracticeModeEnabled && Objects.equals(title, quiz.title) && Objects.equals(description, quiz.description) && Objects.equals(createdAt, quiz.createdAt) && Objects.equals(questions, quiz.questions);
    }

}
