<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quizapp.model.Quiz" %>
<%@ page import="com.freeuni.quizapp.model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quiz Results - Quizology</title>
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
            background: var(--primary-gradient);
            color: var(--text-primary);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .results-container {
            width: 90%;
            max-width: 600px;
            background: var(--card-bg);
            border-radius: 18px;
            padding: 3rem 2.5rem;
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
            backdrop-filter: blur(12px);
            border: 1px solid var(--card-border);
            text-align: center;
            animation: slideUp 0.6s ease-out;
        }
        
        .results-header {
            margin-bottom: 2rem;
        }
        
        .results-title {
            font-size: 2.5rem;
            font-weight: 700;
            background: var(--accent-gradient);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 0.5rem;
        }
        
        .quiz-title {
            font-size: 1.2rem;
            color: var(--text-primary);
            opacity: 0.8;
        }
        
        .score-display {
            margin: 2rem 0;
            padding: 2rem;
            background: rgba(232, 90, 79, 0.1);
            border-radius: 14px;
            border: 2px solid rgba(232, 90, 79, 0.2);
        }
        
        .score-number {
            font-size: 4rem;
            font-weight: 800;
            background: var(--accent-gradient);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            line-height: 1;
        }
        
        .score-label {
            font-size: 1.1rem;
            margin-top: 0.5rem;
            color: var(--text-primary);
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 1.5rem;
            margin: 2rem 0;
        }
        
        .stat-card {
            background: rgba(255, 255, 255, 0.6);
            padding: 1.5rem 1rem;
            border-radius: 12px;
            border: 1px solid rgba(0, 0, 0, 0.05);
        }
        
        .stat-value {
            font-size: 1.8rem;
            font-weight: 700;
            color: #E85A4F;
            margin-bottom: 0.5rem;
        }
        
        .stat-label {
            font-size: 0.9rem;
            color: var(--text-primary);
        }
        
        .actions {
            margin-top: 2.5rem;
            display: flex;
            gap: 1rem;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .btn {
            padding: 0.8rem 1.8rem;
            border: none;
            border-radius: 50px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.25s ease;
            display: inline-block;
        }
        
        .btn-primary {
            background: var(--accent-gradient);
            color: #ffffff;
            box-shadow: 0 4px 12px rgba(232, 90, 79, 0.25);
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(232, 90, 79, 0.35);
        }
        
        .btn-secondary {
            background: transparent;
            color: #E85A4F;
            border: 2px solid #E85A4F;
        }
        
        .btn-secondary:hover {
            background: var(--accent-gradient);
            color: #ffffff;
            transform: translateY(-2px);
        }
        
        .performance-message {
            margin: 1.5rem 0;
            padding: 1rem;
            border-radius: 10px;
            font-weight: 500;
        }
        
        .excellent { background: rgba(76, 175, 80, 0.1); color: #4CAF50; }
        .good { background: rgba(255, 193, 7, 0.1); color: #FF9800; }
        .needs-improvement { background: rgba(244, 67, 54, 0.1); color: #F44336; }
        
        @keyframes slideUp {
            from { transform: translateY(40px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }
        
        @media (max-width: 600px) {
            .results-container {
                width: 95%;
                padding: 2rem 1.5rem;
            }
            
            .score-number {
                font-size: 3rem;
            }
            
            .actions {
                flex-direction: column;
            }
            
            .btn {
                width: 100%;
            }
        }
    </style>
</head>
<body>
    <%
        Quiz quiz = (Quiz) request.getAttribute("quiz");
        Integer score = (Integer) request.getAttribute("score");
        Integer totalQuestions = (Integer) request.getAttribute("totalQuestions");
        Integer timeTakenSeconds = (Integer) request.getAttribute("timeTakenSeconds");
        Double percentage = (Double) request.getAttribute("percentage");
        
        User currentUser = (User) session.getAttribute("currentUser");

        if (score == null) score = 0;
        if (totalQuestions == null) totalQuestions = 0;
        if (timeTakenSeconds == null) timeTakenSeconds = 0;
        if (percentage == null) percentage = 0.0;
        

        int minutes = timeTakenSeconds / 60;
        int seconds = timeTakenSeconds % 60;
        String timeFormatted = String.format("%d:%02d", minutes, seconds);

        String performanceClass = "";
        String performanceMessage = "";
        if (percentage >= 80) {
            performanceClass = "excellent";
            performanceMessage = "Excellent work! You have a great understanding of the subject.";
        } else if (percentage >= 60) {
            performanceClass = "good";
            performanceMessage = "Good job! You have a solid grasp of the material.";
        } else {
            performanceClass = "needs-improvement";
            performanceMessage = "Keep studying! There's room for improvement.";
        }
    %>
    
    <div class="results-container">
        <div class="results-header">
            <h1 class="results-title">Quiz Complete!</h1>
            <p class="quiz-title"><%= quiz != null ? quiz.getTitle() : "Unknown Quiz" %></p>
        </div>
        
        <div class="score-display">
            <div class="score-number"><%= score %>/<%= totalQuestions %></div>
            <div class="score-label"><%= String.format("%.1f", percentage) %>% Correct</div>
        </div>
        
        <div class="performance-message <%= performanceClass %>">
            <%= performanceMessage %>
        </div>
        
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-value"><%= score %></div>
                <div class="stat-label">Correct Answers</div>
            </div>
            <div class="stat-card">
                <div class="stat-value"><%= totalQuestions %></div>
                <div class="stat-label">Total Questions</div>
            </div>
            <div class="stat-card">
                <div class="stat-value"><%= timeFormatted %></div>
                <div class="stat-label">Time Taken</div>
            </div>
        </div>
        
        <div class="actions">
            <a href="quizzes.jsp" class="btn btn-primary">Browse More Quizzes</a>
            <% if (currentUser != null) { %>
                <a href="profile" class="btn btn-secondary">View Profile</a>
            <% } %>
        </div>
    </div>
</body>
</html> 