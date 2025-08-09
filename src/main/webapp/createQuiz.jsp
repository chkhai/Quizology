<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quizapp.model.User" %>
<%

    session.removeAttribute("currentQuiz");
    session.removeAttribute("quizAnswers");
    session.removeAttribute("quizStartTime");
    
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Quiz - Quizology</title>
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
            max-width: 800px;
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
        .quiz-type-form {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 18px;
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.13);
            padding: 3rem 2.5rem;
            text-align: center;
        }
        .quiz-type-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 2rem;
            margin-bottom: 3rem;
        }
        .quiz-type-option {
            position: relative;
        }
        .quiz-type-option input[type="radio"] {
            position: absolute;
            opacity: 0;
            cursor: pointer;
        }
        .quiz-type-card {
            background: rgba(255, 255, 255, 0.8);
            border: 2px solid rgba(0, 0, 0, 0.05);
            border-radius: 15px;
            padding: 2rem 1.5rem;
            cursor: pointer;
            transition: all 0.3s ease;
            min-height: 200px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
        }
        .quiz-type-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(232, 90, 79, 0.15);
            border-color: #E85A4F;
        }
        .quiz-type-option input[type="radio"]:checked + .quiz-type-card {
            border-color: #E85A4F;
            background: rgba(232, 90, 79, 0.05);
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(232, 90, 79, 0.2);
        }
        .quiz-type-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
            color: #E85A4F;
        }
        .quiz-type-title {
            font-size: 1.3rem;
            font-weight: 600;
            color: var(--text-primary);
            margin-bottom: 0.8rem;
        }
        .quiz-type-description {
            font-size: 0.95rem;
            color: var(--text-primary);
            opacity: 0.7;
            line-height: 1.4;
        }
        .form-actions {
            display: flex;
            gap: 1rem;
            justify-content: center;
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
            .quiz-type-form {
                padding: 2rem 1.5rem;
            }
            .quiz-type-grid {
                grid-template-columns: 1fr;
                gap: 1.5rem;
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
                <% if (currentUser.isAdmin()) { %>
                    <li><a href="admin">Admin Panel</a></li>
                <% } %>
                <li><a href="logout">Sign Out</a></li>
            </ul>
        </li>
    </ul>
</nav>

<div class="create-quiz-container">
    <div class="create-quiz-header">
        <h1>Create a New Quiz</h1>
        <p>Choose the type of quiz you want to create</p>
    </div>

    <form class="quiz-type-form" action="createQuizForm" method="get">
        <div class="quiz-type-grid">
            <div class="quiz-type-option">
                <input type="radio" id="multiple_choice" name="quizType" value="multiple_choice" required>
                <label for="multiple_choice" class="quiz-type-card">
                    <div class="quiz-type-icon">📝</div>
                    <div class="quiz-type-title">Multiple Choice</div>
                    <div class="quiz-type-description">Create questions with multiple answer options where users select the correct one</div>
                </label>
            </div>
            
            <div class="quiz-type-option">
                <input type="radio" id="fill_in_blank" name="quizType" value="fill_in_blank" required>
                <label for="fill_in_blank" class="quiz-type-card">
                    <div class="quiz-type-icon">✏️</div>
                    <div class="quiz-type-title">Fill in the Blank</div>
                    <div class="quiz-type-description">Create questions where users need to fill in missing words or phrases</div>
                </label>
            </div>
            
            <div class="quiz-type-option">
                <input type="radio" id="question_response" name="quizType" value="question_response" required>
                <label for="question_response" class="quiz-type-card">
                    <div class="quiz-type-icon">💭</div>
                    <div class="quiz-type-title">Question Response</div>
                    <div class="quiz-type-description">Create open-ended questions where users provide their own answers</div>
                </label>
            </div>
        </div>
        
        <div class="form-actions">
            <a href="profile" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary">Continue</button>
        </div>
    </form>
</div>

</body>
</html> 