<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quizapp.model.User" %>
<%
    // Clear quiz session data when user navigates away from quiz
    session.removeAttribute("currentQuiz");
    session.removeAttribute("quizAnswers");
    session.removeAttribute("quizStartTime");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quizology – Challenge Your Mind</title>
    <style>
        :root {
            --gradient-accent: linear-gradient(135deg, #E85A4F 0%, #E9704F 100%);
            --beige-gradient: linear-gradient(135deg, #EAE7DC 0%, #D8C3A5 100%);
            --text-secondary: #8E8D8A;
            --card-shadow: 0 18px 40px rgba(0, 0, 0, 0.08);
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
            background-color: #ffffff;
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
        }

        .brand:visited {
            background: var(--gradient-accent);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .nav-links {
            display: flex;
            gap: 2rem;
            list-style: none;
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

        .hero {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            padding: 4.5% 4.5% 7%;
            background: var(--beige-gradient);
        }

        .hero-content {
            flex: 1 1 420px;
        }

        .hero-title {
            font-size: 3.6rem;
            font-weight: 800;
            line-height: 1.15;
            background: var(--gradient-accent);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            animation: fadeInUp 1s ease-out 0.3s both;
        }

        .hero-title span {
            color: #8E8D8A;
        }

        .hero-description {
            margin-top: 1.2rem;
            max-width: 480px;
            font-size: 1.05rem;
            animation: fadeIn 1s ease-out 0.6s both;
        }

        .hero-btn-group {
            margin-top: 2.2rem;
            display: flex;
            gap: 1.2rem;
        }

        .btn-primary,
        .btn-outline {
            padding: 0.95rem 1.85rem;
            border-radius: 50px;
            font-size: 1.05rem;
            font-weight: 600;
            cursor: pointer;
            border: none;
            transition: all .25s ease;
        }

        .btn-primary,
        .btn-primary:visited {
            text-decoration: none;
            background-image: var(--gradient-accent);
            color: #ffffff;
            box-shadow: 0 4px 12px rgba(232, 90, 79, 0.25);
        }

        .btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 20px rgba(232, 90, 79, 0.35);
        }

        .btn-outline {
            background: transparent;
            color: #E85A4F;
            border: 2px solid #E85A4F;
        }

        .btn-outline:hover {
            background-image: var(--gradient-accent);
            color: #ffffff;
            border-color: transparent;
            transform: translateY(-3px);
            box-shadow: 0 8px 20px rgba(232, 90, 79, 0.35);
        }

        .stats {
            display: flex;
            gap: 2.5rem;
            margin-top: 3.5rem;
            flex-wrap: wrap;
        }

        .stat {
            text-align: center;
        }

        .stat .number {
            font-size: 2.1rem;
            font-weight: 700;
            background: var(--gradient-accent);
            background-clip: text;
            -webkit-background-clip: text;
        }

        .stat .label {
            font-size: 0.8rem;
            letter-spacing: 1.2px;
            text-transform: uppercase;
        }

        .hero-card-wrapper {
            flex: 1 1 360px;
            perspective: 1000px;
            display: flex;
            justify-content: center;
            margin-top: 2rem;
        }

        .quiz-card {
            background: #ffffff;
            border-radius: 14px;
            width: 340px;
            padding: 2rem 1.75rem 2.4rem;
            box-shadow: var(--card-shadow);
            transform: rotate(6deg);
            animation: rotateIn 1s ease-out 1s both;
        }

        .quiz-label {
            display: inline-block;
            padding: 0.35rem 0.85rem;
            font-size: 0.85rem;
            font-weight: 600;
            border-radius: 8px;
            background-image: var(--gradient-accent);
            color: #ffffff;
            margin-bottom: 1.1rem;
        }

        .quiz-question {
            font-size: 1.2rem;
            font-weight: 600;
            margin-bottom: 1.4rem;
            color: var(--text-secondary);
        }

        .answer {
            background: #f7f7f7;
            border-radius: 8px;
            padding: 0.65rem 0.85rem;
            margin-bottom: 0.75rem;
            border: 1px solid #e5e5e5;
            font-size: 0.95rem;
            color: var(--text-secondary);
        }

        .answer.correct {
            background-image: var(--gradient-accent);
            color: #ffffff;
            border: none;
        }

        .categories-section {
            padding: 6% 4.5% 7%;
        }

        .categories-title {
            text-align: center;
            font-size: 2.4rem;
            font-weight: 700;
            margin-bottom: 3rem;
            background: var(--gradient-accent);
            background-clip: text;
            -webkit-background-clip: text;
        }

        .categories-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 2rem;
        }

        .category-card {
            background: #ffffff;
            border-radius: 14px;
            padding: 2rem 1.6rem;
            box-shadow: var(--card-shadow);
            text-align: center;
            transition: transform .25s ease, box-shadow .25s ease;
        }

        .category-card:hover {
            transform: translateY(-6px);
            box-shadow: 0 12px 28px rgba(0,0,0,.1);
        }

        .category-icon {
            font-size: 2.4rem;
            margin-bottom: 1rem;
            color: #E85A4F;
        }

        .category-name {
            font-size: 1.1rem;
            font-weight: 600;
        }

        footer {
            margin-top: 8%;
            padding: 2.5rem 4.5%;
            text-align: center;
            background: #f7f7f7;
            font-size: 0.9rem;
        }

        @media (max-width: 900px) {
            .hero {
                flex-direction: column;
                text-align: center;
            }
            .hero-card-wrapper {
                margin-top: 3.5rem;
            }
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

        @keyframes rotateIn {
            from { transform: rotate(-12deg) scale(0.8); opacity: 0; }
            to   { transform: rotate(6deg) scale(1); opacity: 1; }
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
    </style>

    <link rel="stylesheet" type="text/css" href="css/index.css">

</head>
<body>

<nav class="navbar">
    <a href="/home" class="brand">Quizology</a>
    <form class="search-bar" action="search" method="get">
        <input type="text" name="q" placeholder="Search" required/>
        <input type="hidden" name="type" value="all"/>
    </form>
    <ul class="nav-links">
        <li><a href="leaderboard">Leaderboard</a></li>
        <% com.freeuni.quizapp.model.User currentUser = (com.freeuni.quizapp.model.User) session.getAttribute("currentUser");
           if (currentUser == null) { %>
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

<section class="hero">
    <div class="hero-content">
        <h1 class="hero-title">Challenge <br/>Your <span>Mind</span></h1>
        <p class="hero-description">Discover engaging quizzes. Test your knowledge, learn something new, and compete with friends!</p>
        <div class="hero-btn-group">
            <a href="quizzes.jsp" class="btn-primary">Browse Quizzes</a>
        </div>

        <div class="stats">
            <div class="stat">
                <div class="number">10+</div>
                <div class="label">Quizzes</div>
            </div>
        </div>
    </div>

    <div class="hero-card-wrapper">
        <div class="quiz-card">
            <span class="quiz-label">Geography Quiz</span>
            <p class="quiz-question">What is the capital of Japan?</p>
            <div class="answer">Seoul</div>
            <div class="answer correct">Tokyo</div>
            <div class="answer">Beijing</div>
            <div class="answer">Bangkok</div>
        </div>
    </div>
</section>

<%@ page import="java.util.List" %>
<%@ page import="com.freeuni.quizapp.model.*" %>
<%@ page import="com.freeuni.quizapp.dao.interfaces.UserDao" %>
<%@ page import="com.freeuni.quizapp.dao.impl.UserDaoImpl" %>
<%@ page import="com.freeuni.quizapp.util.DBConnector" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.freeuni.quizapp.enums.ActionType" %>
<%@ page import="com.freeuni.quizapp.enums.AchievementType" %>

<div class="panel-board">

    <div class="announcements">
        <h2>Announcements</h2>
        <%
            List<Announcement> announcements = (List<Announcement>) request.getAttribute("announcements");
        %>
        <%
            if (announcements != null && !announcements.isEmpty()) {
                for (Announcement ann : announcements) {
        %>
        <div class="announcement">
            <h3 class="truncate"><%= ann.getTitle() %></h3>
            <p class="truncate"><%= ann.getText() %></p>
            <p><a href="announcement?id=<%= ann.getId() %>">Show more</a></p>
            <small>Posted on: <%= ann.getCreatedAt() %></small>
        </div>

        <hr/>
        <%
            }
        } else {
        %>
        <p>No announcements available.</p>
        <%
            }
        %>
    </div>

    <div class="popular-quizzes">
        <h2>Popular Quizzes</h2>
        <%
            List<Quiz> popularQuizzes = (List<Quiz>) request.getAttribute("popularQuizzes");
            if (popularQuizzes != null && !popularQuizzes.isEmpty()) {
                for (Quiz quiz : popularQuizzes) {
        %>
        <div class="popular-quiz">
            <a href="quizzes.jsp#settings_<%= quiz.getId() %>">
                <h3><%= quiz.getTitle() %></h3>
            </a>
            <p><%= quiz.getDescription() %></p>
            <small>Created on: <%= quiz.getCreatedAt() %></small>
        </div>
        <%
            }
        } else {
        %>
        <p>No popular quizzes available.</p>
        <%
            }
        %>
    </div>

    <div class="recent-quizzes">
        <h2>Recent Quizzes</h2>
        <%
            List<Quiz> recentQuizzes = (List<Quiz>) request.getAttribute("recentQuizzes");
            if (recentQuizzes != null && !recentQuizzes.isEmpty()) {
                for (Quiz quiz : recentQuizzes) {
        %>
        <div class="recent-quiz">
            <a href="quizzes.jsp#settings_<%= quiz.getId() %>">
                <h3><%= quiz.getTitle() %></h3>
            </a>
            <p><%= quiz.getDescription() %></p>
            <small>Created on: <%= quiz.getCreatedAt() %></small>
        </div>
        <%
            }
        } else {
        %>
        <p>No recent quizzes available.</p>
        <%
            }
        %>
    </div>






    <div class="friends-activities">
        <h2>Friends' Activities</h2>
        <%
            List<Activity> friendsActivities = (List<Activity>) request.getAttribute("friendsActivities");
        %>

        <% if (friendsActivities != null && !friendsActivities.isEmpty()) { %>
        <ul>
            <% for (Activity activity : friendsActivities) {
                User user = activity.getUser();
                String username = user.getUsername();
                ActionType actionType = activity.getType();
                String content = "";

                switch (actionType) {
                    case achievement_earned:
                        AchievementType achievement = activity.getAchievementType();
                        content = "<a href='profile?username=" + username + "'>" + username + "</a> earned the achievement: <strong>" + achievement.name().replace("_", " ") + "</strong>";
                        break;
                    case quiz_taken:
                        Quiz takenQuiz = activity.getQuiz();
                        content = "<a href='profile?username=" + username + "'>" + username + "</a> took the quiz: <a href='quizzes.jsp#settings_" + takenQuiz.getId() + "'>\"" + takenQuiz.getTitle() + "\"</a>";
                        break;
                    case quiz_created:
                        Quiz createdQuiz = activity.getQuiz();
                        content = "<a href='profile?username=" + username + "'>" + username + "</a> created a new quiz: <a href='quizzes.jsp#settings_" + createdQuiz.getId() + "'>\"" + createdQuiz.getTitle() + "\"</a>";
                        break;
                }
            %>
            <li>
                <%= content %><br/>
                <small><%= activity.getTimestamp() %></small>
            </li>
            <% } %>
        </ul>
        <% } else { %>
        <p>No recent activities from friends.</p>
        <% } %>
    </div>


</div>

</body>
</html>
