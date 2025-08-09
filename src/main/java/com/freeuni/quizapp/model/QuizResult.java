package com.freeuni.quizapp.model;

import java.sql.Timestamp;
import java.util.Objects;

public class QuizResult {
    private int id;
    private int userId;
    private int quizId;
    private int score;
    private int totalQuestions;
    private int timeTakenSeconds;
    private boolean isPracticeMode;
    private Timestamp completedAt;

    public QuizResult(int id, int userId, int quizId, int score,
                      int totalQuestions, int timeTakenSeconds, boolean isPracticeMode,
                      Timestamp completedAt) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timeTakenSeconds = timeTakenSeconds;
        this.isPracticeMode = isPracticeMode;
        this.completedAt = completedAt;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public void setTimeTakenSeconds(int timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }

    public boolean isPracticeMode() {
        return isPracticeMode;
    }

    public void setPracticeMode(boolean practiceMode) {
        isPracticeMode = practiceMode;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuizResult that = (QuizResult) o;
        return id == that.id && userId == that.userId && quizId == that.quizId && score == that.score && totalQuestions == that.totalQuestions && timeTakenSeconds == that.timeTakenSeconds && isPracticeMode == that.isPracticeMode && Objects.equals(completedAt, that.completedAt);
    }

}
