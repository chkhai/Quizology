package com.freeuni.quizapp.model;

import com.freeuni.quizapp.dao.impl.AnswerDaoImpl;
import com.freeuni.quizapp.dao.interfaces.AnswerDao;
import com.freeuni.quizapp.enums.QuestionType;
import com.freeuni.quizapp.util.DBConnector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Question {

    private int id;
    private int quizId;
    private String text;
    private QuestionType type;
    private String imageUrl;
    private List<Answer> answers;

    public Question(int id, int quizId, String text, QuestionType type,  String imageUrl) {
        this.id = id;
        this.quizId = quizId;
        this.text = text;
        this.type = type;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return id == question.id && quizId == question.quizId && Objects.equals(text, question.text) && type == question.type && Objects.equals(imageUrl, question.imageUrl) && Objects.equals(answers, question.answers);
    }

}
