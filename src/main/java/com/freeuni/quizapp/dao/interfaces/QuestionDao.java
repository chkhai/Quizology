package com.freeuni.quizapp.dao.interfaces;

import com.freeuni.quizapp.enums.QuestionType;
import com.freeuni.quizapp.model.Question;

import java.sql.SQLException;
import java.util.List;

public interface QuestionDao {

    void addQuestion(int quiz_id, String text, QuestionType type, String image_url) throws SQLException;

    int addQuestionAndReturnId(int quiz_id, String text, QuestionType type, String image_url) throws SQLException;

    void removeQuestion(String text) throws SQLException;

    List<Question> getAllQuestions() throws SQLException;

    List<Question> getQuizAllQuestions(int quiz_id) throws SQLException;

    void updateQuestion(int update_q_id, String text) throws SQLException;
}
