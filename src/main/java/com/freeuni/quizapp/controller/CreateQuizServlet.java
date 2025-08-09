package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.dao.impl.AnswerDaoImpl;
import com.freeuni.quizapp.dao.impl.QuestionDaoImpl;
import com.freeuni.quizapp.dao.impl.QuizDaoImpl;
import com.freeuni.quizapp.enums.QuestionType;
import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.service.impl.AchievementServiceImpl;
import com.freeuni.quizapp.service.interfaces.AchievementService;
import com.freeuni.quizapp.util.DBConnector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "CreateQuizServlet", urlPatterns = "/submitQuizCreation")
public class CreateQuizServlet extends HttpServlet {

    private final AchievementService achievementService = new AchievementServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = (User) (session != null ? session.getAttribute("currentUser") : null);

        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }


        String quizTitle = request.getParameter("quizTitle");
        String quizDescription = request.getParameter("quizDescription");
        String quizType = request.getParameter("quizType");
        
        String numQuestionsParam = request.getParameter("numQuestions");
        int numQuestions = 3;
        if (numQuestionsParam != null && !numQuestionsParam.trim().isEmpty()) {
            try {
                numQuestions = Integer.parseInt(numQuestionsParam);
                if (numQuestions < 1) numQuestions = 3;
                if (numQuestions > 10) numQuestions = 10;
            } catch (NumberFormatException e) {
                numQuestions = 3;
            }
        }


        if (quizTitle == null || quizTitle.trim().isEmpty() || 
            quizType == null || quizType.trim().isEmpty()) {
            // Redirect back with error
            response.sendRedirect("createQuiz.jsp?error=missing_fields");
            return;
        }


        QuestionType selectedType;
        try {
            selectedType = QuestionType.valueOf(quizType);
        } catch (IllegalArgumentException e) {
            response.sendRedirect("createQuiz.jsp?error=invalid_type");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false);
            
            try {

                QuizDaoImpl quizDao = new QuizDaoImpl(conn);
                int quizId = quizDao.addQuizAndReturnId(
                    quizTitle.trim(), 
                    quizDescription != null ? quizDescription.trim() : null, 
                    currentUser.getId()
                );


                QuestionDaoImpl questionDao = new QuestionDaoImpl(conn);
                AnswerDaoImpl answerDao = new AnswerDaoImpl(conn);


                for (int i = 1; i <= numQuestions; i++) {
                    String questionText = request.getParameter("question" + i + "_text");
                    

                    if (questionText == null || questionText.trim().isEmpty()) {
                        if (i == 1) {
                            throw new IllegalArgumentException("First question is required");
                        }
                        continue; 
                    }


                    if (selectedType == QuestionType.fill_in_blank) {
                        if (!questionText.contains("______")) {
                            throw new IllegalArgumentException("Fill-in-the-blank questions must contain at least 6 underscores (______) to indicate the blank");
                        }
                    }

                int questionId = questionDao.addQuestionAndReturnId(
                    quizId, 
                    questionText.trim(), 
                    selectedType, 
                    null
                );


                    if (selectedType == QuestionType.multiple_choice) {

                        String correctAnswerParam = request.getParameter("question" + i + "_correct");
                        if (correctAnswerParam == null) {
                            if (i == 1) {
                                throw new IllegalArgumentException("Correct answer must be selected for question " + i);
                            }
                            continue;
                        }
                        
                        int correctIndex = Integer.parseInt(correctAnswerParam);
                        

                        for (int j = 0; j < 4; j++) {
                            String optionText = request.getParameter("question" + i + "_option" + j);
                            if (optionText != null && !optionText.trim().isEmpty()) {
                                boolean isCorrect = (j == correctIndex);
                                answerDao.addAnswer(questionId, optionText.trim(), isCorrect);
                            } else if (i == 1) {

                                throw new IllegalArgumentException("All answer options are required for question " + i);
                            }
                        }
                    } else {

                        String answer = request.getParameter("question" + i + "_answer");
                        if (answer != null && !answer.trim().isEmpty()) {
                            answerDao.addAnswer(questionId, answer.trim(), true);
                        } else if (i == 1) {
                            throw new IllegalArgumentException("Answer is required for question " + i);
                        }
                    }
                }

                // *** FIX: Check and award quiz creation achievements ***
                // Use the same database connection to avoid lock conflicts
                try {
                    achievementService.checkAndAwardQuizCreationAchievements(conn, currentUser.getId(), quizId);
                    System.out.println("Successfully checked quiz creation achievements for user " + currentUser.getId() + " and quiz " + quizId);
                } catch (SQLException e) {
                    // Don't fail the quiz creation if achievement checking fails
                    System.err.println("Failed to check achievements after quiz creation: " + e.getMessage());
                    e.printStackTrace();
                }

                conn.commit();
                

                response.sendRedirect("profile?success=quiz_created");
                
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                

                String errorMessage = e.getMessage();
                if (errorMessage != null && (errorMessage.contains("required") || errorMessage.contains("underscores"))) {
                    response.sendRedirect("createQuizForm.jsp?quizType=" + quizType + "&numQuestions=" + numQuestions + "&error=missing_required");
                } else {
                    response.sendRedirect("createQuizForm.jsp?quizType=" + quizType + "&numQuestions=" + numQuestions + "&error=creation_failed");
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("createQuizForm.jsp?quizType=" + quizType + "&error=database_error");
        }
    }
} 