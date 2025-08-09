package com.freeuni.quizapp.service.impl;

import com.freeuni.quizapp.dao.impl.AnswerDaoImpl;
import com.freeuni.quizapp.dao.impl.QuestionDaoImpl;
import com.freeuni.quizapp.dao.impl.QuizDaoImpl;
import com.freeuni.quizapp.model.Question;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.service.interfaces.QuizService;
import com.freeuni.quizapp.util.DBConnector;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizServiceImpl implements QuizService {
    @Override
    public Quiz loadQuizWithQuestions(int quizId, boolean random, boolean onePage, boolean immediate, boolean practiceMode)
            throws SQLException {
        try (Connection con = DBConnector.getConnection()) {
            QuizDaoImpl quizDao = new QuizDaoImpl(con);
            Quiz quiz = quizDao.getQuizById(quizId);
            if (quiz == null) return null;

            QuestionDaoImpl questionDao = new QuestionDaoImpl(con);
            List<Question> questions = questionDao.getQuizAllQuestions(quizId);

            AnswerDaoImpl answerDao = new AnswerDaoImpl(con);
            for (Question q : questions) {
                q.setAnswers(answerDao.getAnswersByQuestionId(q.getId()));
            }

            if (random) Collections.shuffle(questions);

            quiz.setQuestions(questions);
            quiz.setRandom(random);
            quiz.setOnePage(onePage);
            quiz.setImmediateCorrection(immediate);
            quiz.setPracticeModeEnabled(practiceMode);

            return quiz;
        }
    }

        @Override
        public void saveUserAnswer(HttpSession session, String questionId, String answer) {
            if (questionId == null || answer == null || answer.trim().isEmpty()) return;

            Map<String, String> userAnswers = (Map<String, String>) session.getAttribute("quizAnswers");
            if (userAnswers == null) {
                userAnswers = new HashMap<>();
            }

            userAnswers.put("question_" + questionId, answer.trim());
            session.setAttribute("quizAnswers", userAnswers);
        }

        @Override
        public int calculateNewQuestionIndex(String direction, int currentIndex) {
            return switch (direction) {
                case "next" -> currentIndex + 1;
                case "previous" -> currentIndex - 1;
                default -> currentIndex;
            };
        }

}
