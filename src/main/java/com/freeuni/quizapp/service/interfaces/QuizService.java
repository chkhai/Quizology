package com.freeuni.quizapp.service.interfaces;

import com.freeuni.quizapp.model.Quiz;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

public interface QuizService {
        Quiz loadQuizWithQuestions(int quizId, boolean random, boolean onePage, boolean immediate, boolean practiceMode)
                throws SQLException;
        void saveUserAnswer(HttpSession session, String questionId, String answer);
        int calculateNewQuestionIndex(String direction, int currentIndex);
}
