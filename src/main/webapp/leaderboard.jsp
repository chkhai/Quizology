<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quizapp.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="com.freeuni.quizapp.controller.LeaderboardServlet" %>
<%
    // Clear quiz session data when user navigates to leaderboard
    session.removeAttribute("currentQuiz");
    session.removeAttribute("quizAnswers");
    session.removeAttribute("quizStartTime");
    User currentUser = (User) session.getAttribute("currentUser");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Leaderboard - Quizology</title>
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
            background: var(--gradient-accent);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            text-decoration: none;
        }
        
        .brand:visited {
            background: var(--gradient-accent);
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
            color: var(--text-secondary);
            font-weight: 500;
            transition: color .2s ease;
        }
        
        .nav-links a:hover {
            color: #E85A4F;
        }
        
        .nav-links a.friends-link {
            color: #E85A4F;
            font-weight: 600;
        }

        .nav-links a.friends-link:hover {
            color: #D32F2F;
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
            color: var(--text-secondary);
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

        .search-bar {
            position: absolute;
            left: 50%;
            transform: translateX(-50%);
            display: flex;
            justify-content: center;
            width: 42%;
            min-width: 260px;
        }
        
        .search-bar input[type="text"] {
            padding: 0.4rem 1rem 0.4rem 2rem;
            border: 1px solid #e0e0e0;
            border-radius: 50px;
            font-size: 0.9rem;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Ubuntu", "Roboto", "Noto Sans", "Droid Sans", "Helvetica Neue", Arial, sans-serif;
            width: 60%;
            max-width: 500px;
            transition: border-color .2s ease;
            background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%238E8D8A"><path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C8.01 14 6 11.99 6 9.5S8.01 5 10.5 5 15 7.01 15 9.5 12.99 14 10.5 14z"/></svg>');
            background-repeat: no-repeat;
            background-position: 10px center;
            background-size: 16px 16px;
        }
        
        .search-bar input[type="text"]:focus {
            outline: none;
            border-color: #E85A4F;
        }
        
        .leaderboard-header {
            text-align: center;
            padding: 4.5% 4.5% 2%;
            background: #ffffff;
        }
        
        .leaderboard-header h1 {
            font-size: 2.8rem;
            font-weight: 800;
            background: var(--gradient-accent);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            animation: fadeInUp 0.8s ease-out 0.3s both;
        }
        
        .leaderboard-header p {
            margin-top: 0.8rem;
            font-size: 1.05rem;
            animation: fadeIn 0.8s ease-out 0.6s both;
        }
        
        .leaderboard-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 2rem 2rem 4rem;
        }
        
        .quiz-leaderboard {
            background: #ffffff;
            border-radius: 14px;
            box-shadow: var(--card-shadow);
            margin-bottom: 2rem;
            overflow: hidden;
        }
        
        .quiz-header {
            background: var(--gradient-accent);
            color: #ffffff;
            padding: 1.5rem 2rem;
            text-align: center;
        }
        
        .quiz-title {
            font-size: 1.5rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }
        
        .quiz-description {
            font-size: 0.95rem;
            opacity: 0.9;
        }
        
        .table-container {
            max-height: 400px;
            overflow-y: auto;
            overflow-x: auto;
        }
        
        .leaderboard-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.9rem;
        }
        
        .leaderboard-table thead {
            background: #f8f9fa;
            position: sticky;
            top: 0;
            z-index: 10;
        }
        
        .leaderboard-table th {
            padding: 1rem 0.8rem;
            text-align: left;
            font-weight: 600;
            color: #333333;
            border-bottom: 2px solid #e0e0e0;
        }
        
        .leaderboard-table th.rank {
            width: 60px;
            text-align: center;
        }
        
        .leaderboard-table th.username {
            width: 25%;
        }
        
        .leaderboard-table th.score {
            width: 15%;
            text-align: center;
        }
        
        .leaderboard-table th.percentage {
            width: 15%;
            text-align: center;
        }
        
        .leaderboard-table th.time {
            width: 15%;
            text-align: center;
        }
        
        .leaderboard-table th.date {
            width: 20%;
        }
        
        .leaderboard-table tbody tr {
            transition: background-color 0.2s ease;
        }
        
        .leaderboard-table tbody tr:hover {
            background: #f8f9fa;
        }
        
        .leaderboard-table tbody tr:nth-child(even) {
            background: rgba(0, 0, 0, 0.02);
        }
        
        .leaderboard-table tbody tr:nth-child(even):hover {
            background: #f8f9fa;
        }
        
        .leaderboard-table td {
            padding: 0.8rem;
            border-bottom: 1px solid #e8e9ea;
        }
        
        .rank-cell {
            text-align: center;
            font-weight: 700;
            color: #E85A4F;
        }
        
        .rank-cell.first {
            color: #FFD700;
            font-size: 1.1rem;
        }
        
        .rank-cell.second {
            color: #C0C0C0;
            font-size: 1.05rem;
        }
        
        .rank-cell.third {
            color: #CD7F32;
            font-size: 1.05rem;
        }
        
        .username-cell {
            font-weight: 600;
            color: #333333;
        }
        
        .username-link {
            color: #333333;
            text-decoration: none;
            font-weight: 600;
            transition: color 0.2s ease;
        }
        
        .username-link:hover {
            color: #E85A4F;
            text-decoration: underline;
        }
        
        .score-cell {
            text-align: center;
            font-weight: 600;
        }
        
        .percentage-cell {
            text-align: center;
        }
        
        .percentage-excellent {
            color: #22c55e;
            font-weight: 600;
        }
        
        .percentage-good {
            color: #f59e0b;
            font-weight: 600;
        }
        
        .percentage-poor {
            color: #ef4444;
            font-weight: 600;
        }
        
        .time-cell {
            text-align: center;
            font-size: 0.85rem;
            color: var(--text-secondary);
        }
        
        .date-cell {
            font-size: 0.85rem;
            color: var(--text-secondary);
        }
        
        .no-data {
            text-align: center;
            padding: 4rem 2rem;
            color: var(--text-secondary);
            font-style: italic;
        }
        
        .no-data h3 {
            margin-bottom: 1rem;
            color: #E85A4F;
        }
        
        @keyframes slideDown {
            from { transform: translateY(-100%); opacity: 0; }
            to   { transform: translateY(0); opacity: 1; }
        }

        @keyframes fadeInUp {
            from { transform: translateY(20px); opacity: 0; }
            to   { transform: translateY(0); opacity: 1; }
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to   { opacity: 1; }
        }
        
        @media (max-width: 768px) {
            .leaderboard-container {
                padding: 1rem;
            }
            
            .quiz-header {
                padding: 1rem;
            }
            
            .quiz-title {
                font-size: 1.2rem;
            }
            
            .leaderboard-table th,
            .leaderboard-table td {
                padding: 0.6rem 0.4rem;
                font-size: 0.8rem;
            }
            
            .leaderboard-table th.time,
            .leaderboard-table th.date,
            .time-cell,
            .date-cell {
                display: none;
            }
        }
    </style>
</head>
<body>

<nav class="navbar">
    <a href="/home" class="brand">Quizology</a>
    <form class="search-bar" action="search" method="get">
        <input type="text" name="q" placeholder="Search" required>
        <input type="hidden" name="type" value="all"/>
    </form>
    <ul class="nav-links">
        <li><a href="leaderboard">Leaderboard</a></li>
        <% if (currentUser == null) { %>
            <li><a href="login.jsp">Login</a></li>
        <% } else { %>
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
        <% } %>
    </ul>
</nav>

<section class="leaderboard-header">
    <h1>Leaderboard</h1>
    <p>See who's leading the pack in each quiz challenge!</p>
</section>

<div class="leaderboard-container">
    <% 
        @SuppressWarnings("unchecked")
                List<LeaderboardServlet.QuizLeaderboard> quizLeaderboards =
            (List<LeaderboardServlet.QuizLeaderboard>) request.getAttribute("quizLeaderboards");

        if (quizLeaderboards != null && !quizLeaderboards.isEmpty()) {
            for (LeaderboardServlet.QuizLeaderboard quizLeaderboard : quizLeaderboards) {
    %>
    
    <div class="quiz-leaderboard">
        <div class="quiz-header">
            <div class="quiz-title"><%= quizLeaderboard.getQuiz().getTitle() %></div>
            <% if (quizLeaderboard.getQuiz().getDescription() != null && !quizLeaderboard.getQuiz().getDescription().trim().isEmpty()) { %>
                <div class="quiz-description"><%= quizLeaderboard.getQuiz().getDescription() %></div>
            <% } %>
        </div>
        
        <div class="table-container">
            <table class="leaderboard-table">
                <thead>
                    <tr>
                        <th class="rank">Rank</th>
                        <th class="username">Player</th>
                        <th class="score">Score</th>
                        <th class="percentage">Accuracy</th>
                        <th class="time">Time</th>
                        <th class="date">Completed</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        int rank = 1;
                        for (LeaderboardServlet.LeaderboardEntry entry : quizLeaderboard.getEntries()) {
                            String rankClass = "";
                            if (rank == 1) rankClass = "first";
                            else if (rank == 2) rankClass = "second";
                            else if (rank == 3) rankClass = "third";
                            
                            String percentageClass = "";
                            if (entry.getPercentage() >= 80) percentageClass = "percentage-excellent";
                            else if (entry.getPercentage() >= 60) percentageClass = "percentage-good";
                            else percentageClass = "percentage-poor";
                            
                            int minutes = entry.getTimeTakenSeconds() / 60;
                            int seconds = entry.getTimeTakenSeconds() % 60;
                            String timeFormatted = String.format("%d:%02d", minutes, seconds);
                    %>
                    <tr>
                        <td class="rank-cell <%= rankClass %>"><%= rank %></td>
                        <td class="username-cell">
                            <a href="profile?username=<%= entry.getUsername() %>" class="username-link">
                                <%= entry.getUsername() %>
                            </a>
                        </td>
                        <td class="score-cell"><%= entry.getScore() %>/<%= entry.getTotalQuestions() %></td>
                        <td class="percentage-cell <%= percentageClass %>"><%= String.format("%.1f", entry.getPercentage()) %>%</td>
                        <td class="time-cell"><%= timeFormatted %></td>
                        <td class="date-cell"><%= entry.getCompletedAt() %></td>
                    </tr>
                    <% 
                            rank++;
                        }
                    %>
                </tbody>
            </table>
        </div>
    </div>
    
    <% 
            }
        } else { 
    %>
    
    <div class="no-data">
        <h3>No Leaderboard Data Available</h3>
        <p>Complete some quizzes to see the leaderboards!</p>
        <br>
        <a href="quizzes.jsp" style="color: #E85A4F; text-decoration: none; font-weight: 600;">Browse Quizzes →</a>
    </div>
    
    <% } %>
</div>

</body>
</html> 