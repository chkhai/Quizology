package com.freeuni.quizapp.model;

import java.util.Objects;

public class UserAnswer {

    private int id;
    private int userId;
    private int questionId;
    private String givenAnswer;
    private boolean isCorrect;

    public UserAnswer(int id, int userId, int questionId, String givenAnswer, boolean isCorrect) {
        this.id = id;
        this.userId = userId;
        this.questionId = questionId;
        this.givenAnswer = givenAnswer;
        this.isCorrect = isCorrect;
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

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getGivenAnswer() {
        return givenAnswer;
    }

    public void setGivenAnswer(String givenAnswer) {
        this.givenAnswer = givenAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserAnswer that = (UserAnswer) o;
        return id == that.id && userId == that.userId && questionId == that.questionId && isCorrect == that.isCorrect && Objects.equals(givenAnswer, that.givenAnswer);
    }

}
