package com.freeuni.quizapp.service.impl;

import com.freeuni.quizapp.dao.impl.AnswerDaoImpl;
import com.freeuni.quizapp.dao.impl.QuizResultDaoImpl;
import com.freeuni.quizapp.dao.impl.UserAnswerDaoImpl;
import com.freeuni.quizapp.model.Answer;
import com.freeuni.quizapp.model.Question;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.service.interfaces.AchievementService;
import com.freeuni.quizapp.service.interfaces.SubmitQuizService;
import com.freeuni.quizapp.util.DBConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class SubmitQuizServiceImpl implements SubmitQuizService {

    private final AchievementService achievementService = new AchievementServiceImpl();

    @Override
    public SubmissionResult processQuizSubmission(User user, Quiz quiz, Map<String, String> userAnswers,
                                                  Object startTimeObj, Map<String, String> requestParams) throws SQLException {

        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(true);

            AnswerDaoImpl answerDao = new AnswerDaoImpl(conn);
            UserAnswerDaoImpl userAnswerDao = new UserAnswerDaoImpl(conn);
            QuizResultDaoImpl quizResultDao = new QuizResultDaoImpl(conn);

            int score = 0;
            int totalQuestions = quiz.getQuestions().size();

            for (Question question  : quiz.getQuestions()) {
                String userAnswerKey = "question_" + question.getId();
                String userAnswer = userAnswers != null ? userAnswers.get(userAnswerKey) : "";

                if ((userAnswer == null || userAnswer.trim().isEmpty()) && requestParams.get(userAnswerKey) != null) {
                    userAnswer = requestParams.get(userAnswerKey);
                }

                boolean isCorrect = false;
                if (userAnswer != null && !userAnswer.trim().isEmpty()) {
                    List<Answer> correctAnswers = answerDao.getAnswersByQuestionId(question.getId());

                    for (Answer answer : correctAnswers) {
                        if (answer.isCorrect() &&
                                answer.getAnswerText().trim().equalsIgnoreCase(userAnswer.trim())) {
                            isCorrect = true;
                            break;
                        }
                    }

                    if (isCorrect) {
                        score++;
                    }

                    userAnswerDao.addUserAnswer(
                            user.getId(),
                            question.getId(),
                            userAnswer.trim(),
                            isCorrect
                    );
                }
            }

            int timeTakenSeconds = calculateTimeTaken(startTimeObj);

            quizResultDao.addQuizResult(
                    user.getId(),
                    quiz.getId(),
                    score,
                    totalQuestions,
                    timeTakenSeconds,
                    quiz.isPracticeModeEnabled()
            );

            try {
                achievementService.checkQuizCompletionAchievements(
                    user.getId(),
                    quiz.getId(),
                    score,
                    totalQuestions,
                    quiz.isPracticeModeEnabled()
                );
            } catch (SQLException e) {
                System.err.println("Error checking achievements: " + e.getMessage());
            }

            return new SubmissionResult(score, totalQuestions, timeTakenSeconds, quiz);
        }
    }

    private int calculateTimeTaken(Object startTimeObj) {
        int timeTakenSeconds = 0;
        if (startTimeObj != null) {
            long startTimeMillis = 0;
            if (startTimeObj instanceof Timestamp) {
                startTimeMillis = ((Timestamp) startTimeObj).getTime();
            } else if (startTimeObj instanceof Long) {
                startTimeMillis = (Long) startTimeObj;
            }
            if (startTimeMillis > 0) {
                long timeDiff = System.currentTimeMillis() - startTimeMillis;
                timeTakenSeconds = (int) (timeDiff / 1000);
            }
        }
        return timeTakenSeconds;
    }
}