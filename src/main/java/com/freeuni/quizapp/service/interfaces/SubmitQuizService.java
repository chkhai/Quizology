package com.freeuni.quizapp.service.interfaces;

import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.User;

import java.sql.SQLException;
import java.util.Map;

public interface SubmitQuizService {

    SubmissionResult processQuizSubmission(User user, Quiz quiz, Map<String, String> userAnswers,
                                           Object startTimeObj, Map<String, String> requestParams) throws SQLException;

    class SubmissionResult {
        private final int score;
        private final int totalQuestions;
        private final int timeTakenSeconds;
        private final double percentage;
        private final Quiz quiz;

        public SubmissionResult(int score, int totalQuestions, int timeTakenSeconds, Quiz quiz) {
            this.score = score;
            this.totalQuestions = totalQuestions;
            this.timeTakenSeconds = timeTakenSeconds;
            this.percentage = totalQuestions > 0 ? (double) score / totalQuestions * 100 : 0.0;
            this.quiz = quiz;
        }

        public int getScore() { return score; }
        public int getTotalQuestions() { return totalQuestions; }
        public int getTimeTakenSeconds() { return timeTakenSeconds; }
        public double getPercentage() { return percentage; }
        public Quiz getQuiz() { return quiz; }
    }
}