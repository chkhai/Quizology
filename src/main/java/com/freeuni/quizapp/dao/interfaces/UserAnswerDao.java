package com.freeuni.quizapp.dao.interfaces;

import com.freeuni.quizapp.model.Achievement;
import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.model.UserAnswer;

import java.sql.SQLException;
import java.util.List;

public interface UserAnswerDao {

    List<UserAnswer> getUserAnswers(int user_id) throws SQLException;

    void addUserAnswer(int user_id, int question_id, String given_answer, boolean is_correct) throws SQLException;

    List<UserAnswer> findByUserAndQuestion(int user_id, int question_id) throws SQLException;
}
