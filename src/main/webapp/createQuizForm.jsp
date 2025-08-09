<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quizapp.model.User" %>
<%@ page import="com.freeuni.quizapp.enums.QuestionType" %>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    String quizType = request.getParameter("quizType");
    if (quizType == null || quizType.trim().isEmpty()) {
        response.sendRedirect("createQuiz.jsp");
        return;
    }
    
    String numQuestionsParam = request.getParameter("numQuestions");
    int numQuestions = 3;
    if (numQuestionsParam != null && !numQuestionsParam.trim().isEmpty()) {
        try {
            numQuestions = Integer.parseInt(numQuestionsParam);
            if (numQuestions < 3) numQuestions = 3;
            if (numQuestions > 10) numQuestions = 10;
        } catch (NumberFormatException e) {
            numQuestions = 3;
        }
    }
    
    QuestionType selectedType;
    try {
        selectedType = QuestionType.valueOf(quizType);
    } catch (IllegalArgumentException e) {
        response.sendRedirect("createQuiz.jsp");
        return;
    }
    
    String quizTypeDisplayName = "";
    String quizTypeDescription = "";
    switch (selectedType) {
        case multiple_choice:
            quizTypeDisplayName = "Multiple Choice";
            quizTypeDescription = "Questions with multiple answer options where users select the correct one";
            break;
        case fill_in_blank:
            quizTypeDisplayName = "Fill in the Blank";
            quizTypeDescription = "Questions where users need to fill in missing words or phrases";
            break;
        case question_response:
            quizTypeDisplayName = "Question Response";
            quizTypeDescription = "Open-ended questions where users provide their own answers";
            break;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create <%= quizTypeDisplayName %> Quiz - Quizology</title>
    <style>
        :root {
            --primary-gradient: linear-gradient(135deg, #EAE7DC 0%, #D8C3A5 100%);
            --accent-gradient: linear-gradient(135deg, #E85A4F 0%, #E9704F 100%);
            --card-bg: rgba(255, 255, 255, 0.9);
            --card-border: rgba(0, 0, 0, 0.05);
            --text-primary: #8E8D8A;
        }
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Ubuntu", "Roboto", "Noto Sans", "Droid Sans", "Helvetica Neue", Arial, sans-serif;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Ubuntu", "Roboto", "Noto Sans", "Droid Sans", "Helvetica Neue", Arial, sans-serif;
            color: var(--text-primary);
            background: var(--primary-gradient);
            min-height: 100vh;
        }
        .navbar {
            position: sticky;
            top: 0;
            width: 100%;
            background: #ffffff;
            display: flex;
            align-items: center;
            padding: 1rem 4.5%;
            z-index: 100;
            box-shadow: 0 2px 8px rgba(0,0,0,.05);
            animation: slideDown 0.8s ease-out both;
            position: relative;
        }
        .brand {
            font-size: 1.6rem;
            font-weight: 700;
            background: var(--accent-gradient);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            text-decoration: none;
        }
        .brand:visited {
            background: var(--accent-gradient);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .nav-links {
            list-style: none;
            display: flex;
            gap: 2rem;
            margin-left: auto;
        }
        .nav-links a {
            text-decoration: none;
            color: var(--text-primary);
            font-weight: 500;
            transition: color .2s ease;
        }
        .nav-links a:hover {
            color: #E85A4F;
        }
        .profile {
            position: relative;
        }
        .profile .dropdown {
            display: none;
            position: absolute;
            left: 50%;
            top: 100%;
            transform: translateX(-50%);
            background: #ffffff;
            list-style: none;
            margin: 0;
            padding: 0.4rem 0;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,.08);
            min-width: 160px;
        }
        .profile:hover .dropdown {
            display: block;
        }
        .profile .dropdown li a {
            display: block;
            padding: 0.6rem 1rem;
            color: var(--text-primary);
            white-space: nowrap;
        }
        .profile .dropdown li a:hover {
            background: #f7f7f7;
            color: #E85A4F;
        }
        .profile a {
            display: flex;
            align-items: center;
            gap: 0.35rem;
        }
        .profile a i {
            color: #B0B0B0;
            font-size: 1rem;
        }
        .create-quiz-container {
            max-width: 900px;
            margin: 4.5% auto 0 auto;
            padding: 0 1.5rem;
        }
        .create-quiz-header {
            text-align: center;
            margin-bottom: 3rem;
        }
        .create-quiz-header h1 {
            font-size: 2.5rem;
            font-weight: 700;
            background: var(--accent-gradient);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 0.5rem;
        }
        .create-quiz-header p {
            font-size: 1.1rem;
            color: var(--text-primary);
            opacity: 0.8;
        }
        .quiz-type-badge {
            display: inline-block;
            background: var(--accent-gradient);
            color: white;
            padding: 0.4rem 1rem;
            border-radius: 20px;
            font-size: 0.9rem;
            font-weight: 500;
            margin-bottom: 1rem;
        }
        .quiz-form {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 18px;
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.13);
            padding: 3rem 2.5rem;
        }
        .form-section {
            margin-bottom: 2.5rem;
        }
        .form-section h3 {
            font-size: 1.4rem;
            font-weight: 600;
            color: #E85A4F;
            margin-bottom: 1rem;
        }
        .form-group {
            margin-bottom: 1.5rem;
        }
        .form-group label {
            display: block;
            font-weight: 500;
            margin-bottom: 0.5rem;
            color: var(--text-primary);
        }
        .form-group input[type="text"],
        .form-group textarea {
            width: 100%;
            padding: 0.8rem 1rem;
            border: 2px solid rgba(0, 0, 0, 0.1);
            border-radius: 8px;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Ubuntu", "Roboto", "Noto Sans", "Droid Sans", "Helvetica Neue", Arial, sans-serif;
            font-size: 1rem;
            background: rgba(255, 255, 255, 0.9);
            transition: border-color 0.2s ease;
        }
        .form-group input[type="text"]:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #E85A4F;
            background: white;
        }
        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }
        .questions-section {
            border-top: 2px solid rgba(0, 0, 0, 0.05);
            padding-top: 2rem;
        }
        .question-item {
            background: rgba(255, 255, 255, 0.7);
            border: 1px solid rgba(0, 0, 0, 0.05);
            border-radius: 12px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
        }
        .question-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
        }
        .question-number {
            font-weight: 600;
            color: #E85A4F;
            font-size: 1.1rem;
        }
        .answer-options {
            margin-top: 1rem;
        }
        .answer-option {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin-bottom: 0.8rem;
        }
        .answer-option input[type="text"] {
            flex: 1;
        }
        .answer-option input[type="radio"] {
            margin-right: 0.5rem;
        }
        .correct-answer-label {
            font-size: 0.9rem;
            color: #28a745;
            font-weight: 500;
        }
        .btn {
            padding: 0.8rem 2rem;
            border: none;
            border-radius: 50px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.2s ease;
            display: inline-block;
            font-size: 1rem;
        }
        .btn-primary {
            background: var(--accent-gradient);
            color: white;
        }
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(232, 90, 79, 0.3);
        }
        .btn-secondary {
            background: transparent;
            border: 2px solid #6c757d;
            color: #6c757d;
        }
        .btn-secondary:hover {
            background: #6c757d;
            color: white;
            text-decoration: none;
        }
        .btn-add {
            background: transparent;
            border: 2px solid #E85A4F;
            color: #E85A4F;
            padding: 0.6rem 1.5rem;
            font-size: 0.9rem;
        }
        .btn-add:hover {
            background: #E85A4F;
            color: white;
        }
        
        .error-message {
            background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
            color: white;
            padding: 1rem 1.5rem;
            border-radius: 12px;
            margin-bottom: 2rem;
            text-align: center;
            font-weight: 500;
            box-shadow: 0 4px 15px rgba(220, 53, 69, 0.2);
            animation: slideDown 0.5s ease-out both;
        }
        .form-actions {
            display: flex;
            gap: 1rem;
            justify-content: center;
            margin-top: 3rem;
        }
        @keyframes slideDown {
            from {
                transform: translateY(-100%);
                opacity: 0;
            }
            to {
                transform: translateY(0);
                opacity: 1;
            }
        }
        @media (max-width: 768px) {
            .create-quiz-container {
                margin: 2rem auto 0;
                padding: 0 1rem;
            }
            .quiz-form {
                padding: 2rem 1.5rem;
            }
            .answer-option {
                flex-direction: column;
                align-items: stretch;
                gap: 0.3rem;
            }
        }
    </style>
</head>
<body>
<nav class="navbar">
    <a href="/home" class="brand">Quizology</a>
    <ul class="nav-links">
        <li><a href="leaderboard">Leaderboard</a></li>
        <li><a href="friends">Friends</a></li>
        <li><a href="inbox">Inbox</a></li>
        <li class="profile">
            <a href="#"><%= currentUser.getUsername() %></a>
            <ul class="dropdown">
                <li><a href="profile">View Profile</a></li>
                <li><a href="logout">Sign Out</a></li>
            </ul>
        </li>
    </ul>
</nav>

<div class="create-quiz-container">
    <% 
        String errorParam = request.getParameter("error");
        if (errorParam != null) {
            String errorMessage = "";
            if ("missing_required".equals(errorParam)) {
                errorMessage = "Please fill in all required fields. For fill-in-the-blank questions, make sure to include at least 6 underscores (______).";
            } else if ("creation_failed".equals(errorParam)) {
                errorMessage = "Failed to create quiz. Please check your input and try again.";
            } else if ("database_error".equals(errorParam)) {
                errorMessage = "Database error occurred. Please try again later.";
            } else {
                errorMessage = "An error occurred. Please try again.";
            }
    %>
        <div class="error-message">
            <%= errorMessage %>
        </div>
    <% } %>
    
    <div class="create-quiz-header">
        <div class="quiz-type-badge"><%= quizTypeDisplayName %></div>
        <h1>Create Your Quiz</h1>
        <p><%= quizTypeDescription %></p>
    </div>

    <form class="quiz-form" action="submitQuizCreation" method="post">
        <input type="hidden" name="quizType" value="<%= quizType %>">
        <input type="hidden" name="numQuestions" value="<%= numQuestions %>">
        
        <div class="form-section">
            <h3>Quiz Details</h3>
            <div class="form-group">
                <label for="quizTitle">Quiz Title *</label>
                <input type="text" id="quizTitle" name="quizTitle" required maxlength="255">
            </div>
            <div class="form-group">
                <label for="quizDescription">Quiz Description</label>
                <textarea id="quizDescription" name="quizDescription" placeholder="Optional description of your quiz" maxlength="1000"></textarea>
            </div>
        </div>

        <div class="form-section questions-section">
            <h3>Questions</h3>
            
            <% for (int i = 1; i <= numQuestions; i++) { %>
                <div class="question-item">
                    <div class="question-header">
                        <span class="question-number">Question <%= i %></span>
                    </div>
                    
                    <div class="form-group">
                        <label for="question<%= i %>_text">Question Text <%= i == 1 ? "*" : "" %></label>
                        <% if (selectedType == QuestionType.fill_in_blank) { %>
                            <div style="font-size: 0.9rem; color: #666; margin-bottom: 0.5rem;">
                                Use at least 6 underscores (______) to indicate where the blank should be filled.
                            </div>
                        <% } %>
                        <textarea id="question<%= i %>_text" name="question<%= i %>_text" <%= i == 1 ? "required" : "" %> 
                                  placeholder="<%= selectedType == QuestionType.fill_in_blank ? 
                                      (i == 1 ? "Example: The capital of France is ______." : "Example: The capital of France is ______. (optional)") :
                                      (i == 1 ? "Enter your question here" : "Enter your question here (optional)") %>"></textarea>
                    </div>
                    
                    <% if (selectedType == QuestionType.multiple_choice) { %>
                        <div class="answer-options">
                            <label>Answer Options <%= i == 1 ? "*" : "" %></label>
                            <div class="answer-option">
                                <input type="radio" name="question<%= i %>_correct" value="0" <%= i == 1 ? "required" : "" %>>
                                <input type="text" name="question<%= i %>_option0" placeholder="Option A" <%= i == 1 ? "required" : "" %>>
                                <span class="correct-answer-label">Mark as correct</span>
                            </div>
                            <div class="answer-option">
                                <input type="radio" name="question<%= i %>_correct" value="1" <%= i == 1 ? "required" : "" %>>
                                <input type="text" name="question<%= i %>_option1" placeholder="Option B" <%= i == 1 ? "required" : "" %>>
                                <span class="correct-answer-label">Mark as correct</span>
                            </div>
                            <div class="answer-option">
                                <input type="radio" name="question<%= i %>_correct" value="2" <%= i == 1 ? "required" : "" %>>
                                <input type="text" name="question<%= i %>_option2" placeholder="Option C" <%= i == 1 ? "required" : "" %>>
                                <span class="correct-answer-label">Mark as correct</span>
                            </div>
                            <div class="answer-option">
                                <input type="radio" name="question<%= i %>_correct" value="3" <%= i == 1 ? "required" : "" %>>
                                <input type="text" name="question<%= i %>_option3" placeholder="Option D" <%= i == 1 ? "required" : "" %>>
                                <span class="correct-answer-label">Mark as correct</span>
                            </div>
                        </div>
                    <% } else { %>
                        <div class="form-group">
                            <label for="question<%= i %>_answer">Expected Answer <%= i == 1 ? "*" : "" %></label>
                            <% if (selectedType == QuestionType.fill_in_blank) { %>
                                <div style="font-size: 0.9rem; color: #666; margin-bottom: 0.5rem;">
                                    Enter the word or phrase that should replace the underscores (______) in your question.
                                </div>
                            <% } %>
                            <input type="text" id="question<%= i %>_answer" name="question<%= i %>_answer" <%= i == 1 ? "required" : "" %>
                                   placeholder="<%= selectedType == QuestionType.fill_in_blank ? "e.g., Paris" : "Enter a sample correct answer" %>">
                        </div>
                    <% } %>
                </div>
            <% } %>
            
            <% if (numQuestions < 10) { %>
                <div style="text-align: center; margin-top: 2rem;">
                    <a href="createQuizForm.jsp?quizType=<%= quizType %>&numQuestions=<%= numQuestions + 1 %>" class="btn btn-add">+ Add Question</a>
                </div>
            <% } %>
        </div>

        <div class="form-actions">
            <a href="createQuiz.jsp" class="btn btn-secondary">Back</a>
            <button type="submit" class="btn btn-primary">Create Quiz</button>
        </div>
    </form>
</div>

</body>
</html> 