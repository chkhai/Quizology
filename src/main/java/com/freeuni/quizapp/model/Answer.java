package com.freeuni.quizapp.model;

import java.util.Objects;

public class Answer {
    private int id;
    private int questionId;
    private String answerText;
    private boolean isCorrect;

    public Answer(int id, int questionId, String answerText, boolean isCorrect) {
        this.id = id;
        this.questionId = questionId;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public int getId() {
        return id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return id == answer.id && questionId == answer.questionId && isCorrect == answer.isCorrect && Objects.equals(answerText, answer.answerText);
    }

}
