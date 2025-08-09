package com.freeuni.quizapp.dao.interfaces;

import com.freeuni.quizapp.model.Answer;

import java.sql.SQLException;
import java.util.List;

public interface AnswerDao {

    void addAnswer(int question_id, String answer_text, boolean is_correct) throws SQLException;

    List<Answer> getAnswersByQuestionId(int questionId) throws SQLException;

    void updateAnswer(int question_id, String answer_text, boolean is_correct) throws SQLException;

    void deleteAnswer(int question_id, String answer_text) throws SQLException;

    void deleteAnswersByQuestionId(int questionId) throws SQLException;
}
