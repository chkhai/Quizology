<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.freeuni.quizapp.model.Quiz" %>
<%@ page import="com.freeuni.quizapp.model.Question" %>
<%@ page import="com.freeuni.quizapp.model.Answer" %>
<%@ page import="com.freeuni.quizapp.model.User" %>
<%@ page import="com.freeuni.quizapp.enums.QuestionType" %>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    Quiz quiz = (Quiz) session.getAttribute("currentQuiz");
    if (quiz == null) {
        response.sendRedirect("quizzes.jsp");
        return;
    }

    List<Question> questions = quiz.getQuestions();
    if (questions == null || questions.isEmpty()) {
        response.sendRedirect("quizzes.jsp");
        return;
    }

    String questionIndexParam = request.getParameter("questionIndex");
    int currentQuestionIndex = 0;
    if (questionIndexParam != null) {
        try {
            currentQuestionIndex = Integer.parseInt(questionIndexParam);
        } catch (NumberFormatException e) {
            currentQuestionIndex = 0;
        }
    }
    
    if (currentQuestionIndex < 0 || currentQuestionIndex >= questions.size()) {
        currentQuestionIndex = 0;
    }

    // Get stored answers from session
    java.util.Map<String, String> userAnswers = (java.util.Map<String, String>) session.getAttribute("quizAnswers");
    if (userAnswers == null) {
        userAnswers = new java.util.HashMap<String, String>();
        session.setAttribute("quizAnswers", userAnswers);
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= quiz.getTitle() %> - Quiz</title>
    <style>
        :root {
            --gradient-accent: linear-gradient(135deg, #E85A4F 0%, #E9704F 100%);
            --text-secondary: #8E8D8A;
            --card-shadow: 0 16px 32px rgba(0, 0, 0, 0.06);
        }
        
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Ubuntu", "Roboto", "Noto Sans", "Droid Sans", "Helvetica Neue", Arial, sans-serif;
        }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Ubuntu", "Roboto", "Noto Sans", "Droid Sans", "Helvetica Neue", Arial, sans-serif;
            color: var(--text-secondary);
            background: #fafafa;
            line-height: 1.6;
        }
        
        .navbar {
            position: sticky;
            top: 0;
            background: #ffffff;
            display: flex;
            align-items: center;
            padding: 1rem 4.5%;
            box-shadow: 0 2px 8px rgba(0,0,0,.05);
            z-index: 100;
        }
        
        .brand {
            font-size: 1.6rem;
            font-weight: 700;
            background: var(--gradient-accent);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            text-decoration: none;
        }
        
        .nav-info {
            margin-left: auto;
            display: flex;
            align-items: center;
            gap: 2rem;
        }
        
        .quiz-header {
            text-align: center;
            padding: 2rem 4.5%;
            background: #ffffff;
            margin-bottom: 2rem;
        }
        
        .quiz-header h1 {
            font-size: 2.2rem;
            font-weight: 800;
            background: var(--gradient-accent);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 0.5rem;
        }
        
        .quiz-info {
            display: flex;
            justify-content: center;
            gap: 2rem;
            font-size: 0.9rem;
            color: var(--text-secondary);
        }
        
        .quiz-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 0 2rem 4rem;
        }
        
        .question-card {
            background: #ffffff;
            border-radius: 12px;
            box-shadow: var(--card-shadow);
            padding: 2rem;
            margin-bottom: 2rem;
        }
        
        .question-number {
            font-size: 0.85rem;
            font-weight: 600;
            color: #E85A4F;
            margin-bottom: 0.5rem;
        }
        
        .question-text {
            font-size: 1.1rem;
            font-weight: 600;
            color: #333333;
            margin-bottom: 1.5rem;
        }
        
        .question-image {
            width: 100%;
            max-width: 400px;
            height: auto;
            border-radius: 8px;
            margin: 1rem 0;
            display: block;
            margin-left: auto;
            margin-right: auto;
        }
        
        .answers {
            display: flex;
            flex-direction: column;
            gap: 0.8rem;
        }
        
        .answer-option {
            display: flex;
            align-items: center;
            padding: 0.8rem 1rem;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        
        .answer-option:hover {
            border-color: #E85A4F;
            background: #fef7f6;
        }
        
        .answer-option input[type="radio"],
        .answer-option input[type="checkbox"] {
            margin-right: 0.8rem;
            transform: scale(1.2);
        }
        
        .answer-text {
            font-size: 0.95rem;
            color: #333333;
        }
        
        .fill-blank-input {
            width: 100%;
            padding: 0.8rem;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 0.95rem;
            transition: border-color 0.2s ease;
        }
        
        .fill-blank-input:focus {
            outline: none;
            border-color: #E85A4F;
        }
        
        .navigation {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 2rem;
            padding: 0 2rem;
        }
        
        .btn {
            padding: 0.8rem 1.5rem;
            border: none;
            border-radius: 50px;
            font-weight: 600;
            text-decoration: none;
            cursor: pointer;
            transition: all 0.2s ease;
            display: inline-block;
        }
        
        .btn-primary {
            background: var(--gradient-accent);
            color: #ffffff;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(232,90,79,.3);
        }
        
        .btn-secondary {
            background: #6c757d;
            color: #ffffff;
        }
        
        .btn-secondary:hover {
            background: #5a6268;
        }
        
        .progress-bar {
            width: 100%;
            height: 6px;
            background: #e0e0e0;
            border-radius: 3px;
            margin: 1rem 0;
            overflow: hidden;
        }
        
        .progress-fill {
            height: 100%;
            background: var(--gradient-accent);
            border-radius: 3px;
            transition: width 0.3s ease;
        }
        
        .submit-quiz {
            text-align: center;
            margin-top: 2rem;
        }
        
        .quiz-form {
            margin: 0;
        }
        
        .immediate-correction-info {
            background: #f0f9ff;
            border: 1px solid #0ea5e9;
            border-radius: 8px;
            padding: 0.8rem 1rem;
            margin-bottom: 1.5rem;
            font-size: 0.85rem;
            color: #0369a1;
        }
        
        /* Show all answer feedback when any answer is selected - ONLY for immediate correction */
        .answers.immediate-correction:has(input[type="radio"]:checked) .answer-option.correct-answer {
            background: #f0fdf4;
            border-color: #22c55e;
            position: relative;
        }
        
        .answers.immediate-correction:has(input[type="radio"]:checked) .answer-option.correct-answer .answer-text {
            color: #16a34a;
            font-weight: 600;
        }
        
        .answers.immediate-correction:has(input[type="radio"]:checked) .answer-option.correct-answer::after {
            content: "Correct Answer";
            position: absolute;
            right: 1rem;
            color: #16a34a;
            font-weight: 600;
            font-size: 0.85rem;
        }
        
        /* Highlight the selected incorrect answer - ONLY for immediate correction */
        .answers.immediate-correction .answer-option.incorrect-answer input[type="radio"]:checked ~ .answer-text {
            color: #dc2626;
            font-weight: 600;
        }
        
        .answers.immediate-correction .answer-option.incorrect-answer input[type="radio"]:checked {
            accent-color: #ef4444;
        }
        
        .answers.immediate-correction .answer-option.incorrect-answer:has(input[type="radio"]:checked) {
            background: #fef2f2;
            border-color: #ef4444;
            position: relative;
        }
        
        .answers.immediate-correction .answer-option.incorrect-answer:has(input[type="radio"]:checked)::after {
            content: "Your Answer";
            position: absolute;
            right: 1rem;
            color: #dc2626;
            font-weight: 600;
            font-size: 0.85rem;
        }
        
        /* Disable interaction after selection ONLY when immediate correction is enabled */
        .answers.immediate-correction:has(input[type="radio"]:checked) .answer-option {
            pointer-events: none;
            cursor: default;
        }
        
        .answers.immediate-correction:has(input[type="radio"]:checked) .answer-option input[type="radio"] {
            pointer-events: none;
        }
        
        @media (max-width: 768px) {
            .quiz-container {
                padding: 0 1rem 4rem;
            }
            
            .question-card {
                padding: 1.5rem;
            }
            
            .navigation {
                flex-direction: column;
                gap: 1rem;
                align-items: stretch;
            }
            
            .btn {
                text-align: center;
            }
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <a href="/home" class="brand">Quizology</a>
        <div class="nav-info">
            <span>Welcome, <%= currentUser.getUsername() %></span>
        </div>
    </nav>

    <div class="quiz-header">
        <h1><%= quiz.getTitle() %></h1>
        <div class="quiz-info">
            <span>Total Questions: <%= questions.size() %></span>
            <% if (quiz.isPracticeModeEnabled()) { %>
                <span>Practice Mode</span>
            <% } %>
            <% if (quiz.isOnePage()) { %>
                <span>One Page Mode</span>
            <% } %>
            <% if (quiz.isImmediateCorrection()) { %>
                <span>Immediate Correction</span>
            <% } %>
        </div>
        <% if (!quiz.isOnePage()) { 
            int progressPercentage = (currentQuestionIndex + 1) * 100 / questions.size();
        %>
            <div class="progress-bar">
                <div class="progress-fill" style="width: <%= progressPercentage %>%;"></div>
            </div>
        <% } %>
    </div>

    <div class="quiz-container">
        <form action="<%= quiz.isOnePage() ? "submitQuiz" : "quizNavigate" %>" method="post" class="quiz-form" id="quiz-form">
            <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
            
            <% if (quiz.isOnePage()) { 
                for (int i = 0; i < questions.size(); i++) {
                    Question question = questions.get(i);
                    List<Answer> answers = question.getAnswers();
            %>
                <div class="question-card">
                    <div class="question-number">Question <%= i + 1 %> of <%= questions.size() %></div>
                    <div class="question-text"><%= question.getText() %></div>
                    
                    <% if (question.getImageUrl() != null && !question.getImageUrl().trim().isEmpty()) { %>
                        <img src="<%= question.getImageUrl() %>" alt="Question Image" class="question-image">
                    <% } %>
                    
                    <% if (question.getType() == QuestionType.multiple_choice && answers != null && !answers.isEmpty()) { %>
                        <div class="answers<%= quiz.isImmediateCorrection() ? " immediate-correction" : "" %>">
                            <% 
                                String storedAnswerMC = userAnswers.get("question_" + question.getId());
                                for (Answer answer : answers) { 
                                    String answerClass = quiz.isImmediateCorrection() ? (answer.isCorrect() ? "correct-answer" : "incorrect-answer") : "";
                                    boolean isChecked = storedAnswerMC != null && storedAnswerMC.equals(answer.getAnswerText());
                            %>
                                <label class="answer-option <%= answerClass %>">
                                    <input type="radio" name="question_<%= question.getId() %>" value="<%= answer.getAnswerText() %>" <%= isChecked ? "checked" : "" %>>
                                    <span class="answer-text"><%= answer.getAnswerText() %></span>
                                </label>
                            <% } %>
                        </div>
                    <% } else if (question.getType() == QuestionType.fill_in_blank) { %>
                        <% String storedAnswer = userAnswers.get("question_" + question.getId()); %>
                        <input type="text" name="question_<%= question.getId() %>" class="fill-blank-input" placeholder="Enter your answer here..." value="<%= storedAnswer != null ? storedAnswer : "" %>">
                    <% } else if (question.getType() == QuestionType.question_response) { %>
                        <% String storedAnswer = userAnswers.get("question_" + question.getId()); %>
                        <input type="text" name="question_<%= question.getId() %>" class="fill-blank-input" placeholder="Type your answer here..." value="<%= storedAnswer != null ? storedAnswer : "" %>">
                    <% } %>
                </div>
            <% } %>
                
                <div class="submit-quiz">
                    <button type="submit" class="btn btn-primary">Submit Quiz</button>
                </div>
                
            <% } else { 
                Question currentQuestion = questions.get(currentQuestionIndex);
                List<Answer> answers = currentQuestion.getAnswers();
            %>
                <div class="question-card">
                    <div class="question-number">Question <%= currentQuestionIndex + 1 %> of <%= questions.size() %></div>
                    <div class="question-text"><%= currentQuestion.getText() %></div>
                    
                    <% if (currentQuestion.getImageUrl() != null && !currentQuestion.getImageUrl().trim().isEmpty()) { %>
                        <img src="<%= currentQuestion.getImageUrl() %>" alt="Question Image" class="question-image">
                    <% } %>
                    
                    <% if (currentQuestion.getType() == QuestionType.multiple_choice && answers != null && !answers.isEmpty()) { %>
                        <div class="answers<%= quiz.isImmediateCorrection() ? " immediate-correction" : "" %>">
                                                    <% 
                            String storedAnswerMC = userAnswers.get("question_" + currentQuestion.getId());
                            for (Answer answer : answers) { 
                                String answerClass = quiz.isImmediateCorrection() ? (answer.isCorrect() ? "correct-answer" : "incorrect-answer") : "";
                                boolean isChecked = storedAnswerMC != null && storedAnswerMC.equals(answer.getAnswerText());
                        %>
                            <label class="answer-option <%= answerClass %>">
                                <input type="radio" name="question_<%= currentQuestion.getId() %>" value="<%= answer.getAnswerText() %>" <%= isChecked ? "checked" : "" %>>
                                <span class="answer-text"><%= answer.getAnswerText() %></span>
                            </label>
                        <% } %>
                        </div>
                    <% } else if (currentQuestion.getType() == QuestionType.fill_in_blank) { %>
                        <% String storedAnswer = userAnswers.get("question_" + currentQuestion.getId()); %>
                        <input type="text" name="question_<%= currentQuestion.getId() %>" class="fill-blank-input" placeholder="Enter your answer here..." value="<%= storedAnswer != null ? storedAnswer : "" %>">
                    <% } else if (currentQuestion.getType() == QuestionType.question_response) { %>
                        <% String storedAnswer = userAnswers.get("question_" + currentQuestion.getId()); %>
                        <input type="text" name="question_<%= currentQuestion.getId() %>" class="fill-blank-input" placeholder="Type your answer here..." value="<%= storedAnswer != null ? storedAnswer : "" %>">
                    <% } %>
                </div>
                
                <input type="hidden" name="currentQuestionId" value="<%= currentQuestion.getId() %>">
                <input type="hidden" name="currentIndex" value="<%= currentQuestionIndex %>">
            <% } %>
        </form>
        
        <% if (!quiz.isOnePage()) { %>
            <div class="navigation">
                <% if (currentQuestionIndex > 0) { %>
                    <button type="submit" name="direction" value="previous" form="quiz-form" class="btn btn-secondary">Previous</button>
                <% } else { %>
                    <span></span>
                <% } %>
                
                <% if (currentQuestionIndex < questions.size() - 1) { %>
                    <button type="submit" name="direction" value="next" form="quiz-form" class="btn btn-primary">Next</button>
                <% } else { %>
                    <button type="submit" name="direction" value="submit" form="quiz-form" class="btn btn-primary">Submit Quiz</button>
                <% } %>
            </div>
        <% } %>
    </div>
</body>
</html> 