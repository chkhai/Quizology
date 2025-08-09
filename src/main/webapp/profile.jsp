<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quizapp.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    // Clear quiz session data when user navigates to profile
    session.removeAttribute("currentQuiz");
    session.removeAttribute("quizAnswers");
    session.removeAttribute("quizStartTime");
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    User profileUser = (User) request.getAttribute("profileUser");
    Boolean isViewingOwnProfile = (Boolean) request.getAttribute("isViewingOwnProfile");
    if (profileUser == null) {
        profileUser = currentUser;
        isViewingOwnProfile = true;
    }
    boolean isAdmin = currentUser != null && currentUser.isAdmin();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Profile</title>
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
        .profile-container {
            max-width: 800px;
            margin: 4.5% auto 0 auto;
            padding: 0 1.5rem;
        }
        .profile-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 18px;
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.13);
            padding: 2.5rem 2.25rem;
            text-align: center;
            margin-bottom: 2rem;
        }
        .profile-card h2 {
            margin-bottom: 0.5rem;
            font-weight: 700;
            font-size: 2rem;
            background: var(--accent-gradient);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .profile-card .username {
            font-size: 1.2rem;
            font-weight: 500;
            margin-bottom: 1.2rem;
            color: #E85A4F;
        }
        .profile-card .info {
            margin-bottom: 1.2rem;
            font-size: 1rem;
        }
        .profile-card .stats {
            display: flex;
            justify-content: space-around;
            margin: 1.5rem 0 1.2rem 0;
        }
        .profile-card .stat {
            text-align: center;
        }
        .profile-card .stat .number {
            font-size: 1.3rem;
            font-weight: 700;
            background: var(--accent-gradient);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .profile-card .stat .label {
            font-size: 0.85rem;
            color: var(--text-primary);
        }
        .profile-card .btn {
            margin-top: 1.2rem;
            padding: 0.7rem 1.5rem;
            border: none;
            border-radius: 50px;
            background: var(--accent-gradient);
            color: #fff;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            transition: transform 0.2s, box-shadow 0.2s;
            display: inline-block;
        }
        .profile-card .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 10px rgba(232, 90, 79, 0.2);
        }
        .history-section {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 18px;
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.13);
            padding: 2rem;
            margin-bottom: 2rem;
        }
        .history-section h3 {
            font-size: 1.4rem;
            margin-bottom: 1rem;
            color: #E85A4F;
            text-align: center;
        }
        .history-list {
            list-style: none;
            padding: 0;
        }
        .history-item {
            padding: 1rem;
            margin-bottom: 0.8rem;
            background: rgba(255, 255, 255, 0.6);
            border-radius: 10px;
            border: 1px solid rgba(0, 0, 0, 0.05);
            font-size: 0.9rem;
            line-height: 1.4;
        }
        .no-history {
            text-align: center;
            color: var(--text-primary);
            font-style: italic;
            padding: 2rem;
        }

        .achievements-section {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 18px;
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.13);
            padding: 2rem;
            margin-bottom: 2rem;
        }

        .achievements-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 1rem;
            margin-top: 1rem;
        }

        .achievement-card {
            background: rgba(255, 255, 255, 0.7);
            border: 1px solid rgba(232, 90, 79, 0.2);
            border-radius: 12px;
            padding: 1.5rem;
            text-align: center;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }

        .achievement-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(232, 90, 79, 0.1);
        }

        .achievement-icon {
            font-size: 2.5rem;
            margin-bottom: 0.8rem;
        }

        .achievement-name {
            font-weight: 600;
            color: #E85A4F;
            margin-bottom: 0.5rem;
            font-size: 1.1rem;
        }

        .achievement-description {
            color: var(--text-primary);
            font-size: 0.9rem;
            line-height: 1.4;
            margin-bottom: 0.8rem;
        }

        .achievement-description strong {
            color: #E85A4F;
            font-weight: 600;
        }

        .achievement-date {
            color: #888;
            font-size: 0.8rem;
            font-style: italic;
        }

        .no-achievements {
            text-align: center;
            color: var(--text-primary);
            font-style: italic;
            padding: 2rem;
        }

        .bio-section {
            margin-bottom: 1rem;
        }
        
        .bio-display {
            background: rgba(255, 255, 255, 0.7);
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 0.8rem;
            line-height: 1.5;
            min-height: 3rem;
            border: 1px solid rgba(0, 0, 0, 0.05);
        }
        
        .bio-display em {
            color: var(--text-primary);
            opacity: 0.7;
        }
        
        .btn-edit-bio {
            background: transparent;
            border: 2px solid #E85A4F;
            color: #E85A4F;
            padding: 0.5rem 1rem;
            border-radius: 25px;
            font-size: 0.85rem;
            cursor: pointer;
            transition: all 0.2s ease;
            font-weight: 500;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-edit-bio:hover {
            background: #E85A4F;
            color: white;
            transform: translateY(-1px);
            text-decoration: none;
        }
        
        .bio-edit-form {
            margin-top: 1rem;
            animation: fadeIn 0.3s ease-in-out;
        }
        
        .bio-textarea {
            width: 100%;
            min-height: 100px;
            padding: 1rem;
            border: 2px solid rgba(0, 0, 0, 0.1);
            border-radius: 8px;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Ubuntu", "Roboto", "Noto Sans", "Droid Sans", "Helvetica Neue", Arial, sans-serif;
            font-size: 0.95rem;
            resize: vertical;
            background: rgba(255, 255, 255, 0.9);
            margin-bottom: 1rem;
            transition: border-color 0.2s ease;
        }
        
        .bio-textarea:focus {
            outline: none;
            border-color: #E85A4F;
            background: white;
        }
        
        .bio-form-actions {
            display: flex;
            gap: 0.8rem;
            justify-content: center;
        }
        
        .btn-cancel {
            padding: 0.6rem 1.2rem;
            border: 2px solid #6c757d;
            background: transparent;
            color: #6c757d;
            border-radius: 25px;
            cursor: pointer;
            font-weight: 500;
            transition: all 0.2s ease;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-cancel:hover {
            background: #6c757d;
            color: white;
            text-decoration: none;
        }
        
        .btn-save {
            padding: 0.6rem 1.2rem;
            border: none;
            background: var(--accent-gradient);
            color: white;
            border-radius: 25px;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.2s ease;
        }
        
        .btn-save:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 10px rgba(232, 90, 79, 0.3);
        }
        
        .success-message {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 1rem 1.5rem;
            border-radius: 12px;
            margin-bottom: 2rem;
            text-align: center;
            font-weight: 500;
            box-shadow: 0 4px 15px rgba(40, 167, 69, 0.2);
            animation: slideDown 0.5s ease-out both;
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        @keyframes slideDown {
            from { transform: translateY(-100%); opacity: 0; }
            to   { transform: translateY(0); opacity: 1; }
        }
        
        @media (max-width: 768px) {
            .profile-container {
                margin: 2rem auto 0;
                padding: 0 1rem;
            }
            .profile-card {
                padding: 2rem 1.5rem;
            }
            .profile-card .stats {
                flex-direction: column;
                gap: 1rem;
            }
        }

        .admin-announcement-section {
            margin-bottom: 2rem;
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 18px;
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.13);
            padding: 2rem;
        }

        .admin-announcement-section h3 {
            color: #E85A4F;
            font-size: 1.4rem;
            margin-bottom: 1.2rem;
            text-align: center;
        }

        .admin-announcement-section form {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }

        .admin-announcement-section input[type="text"],
        .admin-announcement-section textarea {
            padding: 0.75rem 1rem;
            font-size: 0.95rem;
            border: 1px solid rgba(0, 0, 0, 0.1);
            border-radius: 8px;
            background-color: #fff;
            font-family: inherit;
            resize: vertical;
        }

        .admin-announcement-section textarea {
            min-height: 100px;
        }

        .admin-announcement-section button {
            align-self: flex-end;
            padding: 0.6rem 1.2rem;
            font-size: 0.95rem;
            background: var(--accent-gradient);
            color: #fff;
            border: none;
            border-radius: 25px;
            cursor: pointer;
            font-weight: 600;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .admin-announcement-section button:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 10px rgba(232, 90, 79, 0.2);
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

<div class="profile-container">
    <%
        String successMessage = request.getParameter("success");
        if ("quiz_created".equals(successMessage)) {
    %>
        <div class="success-message">
            Quiz created successfully! Your quiz is now available for others to take.
        </div>
    <% } %>

    <div class="profile-card">
        <h2><%= isViewingOwnProfile ? "My Profile" : profileUser.getUsername() + "'s Profile" %></h2>
        <div class="username"><%= profileUser.getUsername() %></div>
        <div class="info">
            <div class="bio-section">
                <% 
                    String editMode = request.getParameter("edit");
                    boolean isEditingBio = "bio".equals(editMode) && isViewingOwnProfile;
                %>
                
                <% if (!isEditingBio) { %>
                    <% if (profileUser.getBio() != null && !profileUser.getBio().trim().isEmpty()) { %>
                        <div class="bio-display">
                            <%= profileUser.getBio() %>
                        </div>
                    <% } else { %>
                        <div class="bio-display">
                            <em><%= isViewingOwnProfile ? "No bio added yet. Click edit to add one!" : "No bio available." %></em>
                        </div>
                    <% } %>
                    <% if (isViewingOwnProfile) { %>
                        <a href="profile?edit=bio" class="btn-edit-bio">Edit Bio</a>
                    <% } %>
                <% } else { %>
                    <div class="bio-edit-form">
                        <form action="updateBio" method="post">
                            <textarea 
                                name="bio" 
                                class="bio-textarea" 
                                placeholder="Tell us about yourself..." 
                                maxlength="500"><%= profileUser.getBio() != null ? profileUser.getBio() : "" %></textarea>
                            <div class="bio-form-actions">
                                <a href="profile" class="btn-cancel">Cancel</a>
                                <button type="submit" class="btn-save">Save Bio</button>
                            </div>
                        </form>
                    </div>
                <% } %>
            </div>
            <br>
            Member since: <%= profileUser.getCreatedAt() != null ? profileUser.getCreatedAt().toString().substring(0, 10) : "Unknown" %>
        </div>
        <div class="stats">
            <div class="stat">
                <div class="number"><%= request.getAttribute("quizzesCreated") != null ? request.getAttribute("quizzesCreated") : "0" %></div>
                <div class="label">Created</div>
            </div>
            <div class="stat">
                <div class="number"><%= request.getAttribute("quizzesTaken") != null ? request.getAttribute("quizzesTaken") : "0" %></div>
                <div class="label">Taken</div>
            </div>
        </div>
        <% if (!isViewingOwnProfile) { %>
            <%
                com.freeuni.quizapp.enums.FriendshipStatus friendshipStatus = 
                    (com.freeuni.quizapp.enums.FriendshipStatus) request.getAttribute("friendshipStatus");
            %>
            <% if (friendshipStatus == null) { %>
                <!-- No existing relationship, show Add Friend button -->
                <form method="post" action="friendRequest" style="display: inline; margin-right: 1rem;">
                    <input type="hidden" name="action" value="send">
                    <input type="hidden" name="targetUserId" value="<%= profileUser.getId() %>">
                    <button type="submit" class="btn" style="background: linear-gradient(135deg, #28a745, #20c997); margin-top: 1.2rem; padding: 0.7rem 1.5rem; border: none; border-radius: 50px; color: #fff; font-weight: 600; cursor: pointer; transition: transform 0.2s, box-shadow 0.2s; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Ubuntu', 'Roboto', 'Noto Sans', 'Droid Sans', 'Helvetica Neue', Arial, sans-serif;">
                        Add Friend
                    </button>
                </form>
            <% } else if (friendshipStatus == com.freeuni.quizapp.enums.FriendshipStatus.pending) { %>
                <span style="background: linear-gradient(135deg, #6c757d, #5a6268); margin-top: 1.2rem; padding: 0.7rem 1.5rem; border-radius: 50px; color: #fff; font-weight: 600; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Ubuntu', 'Roboto', 'Noto Sans', 'Droid Sans', 'Helvetica Neue', Arial, sans-serif; display: inline-block; margin-right: 1rem; cursor: default;">
                    Request Sent
                </span>
            <% } else if (friendshipStatus == com.freeuni.quizapp.enums.FriendshipStatus.accepted) { %>
                <form method="post" action="friendRequest" style="display: inline; margin-right: 1rem;">
                    <input type="hidden" name="action" value="remove">
                    <input type="hidden" name="targetUserId" value="<%= profileUser.getId() %>">
                    <button type="submit" class="btn" style="background: linear-gradient(135deg, #dc3545, #c82333); margin-top: 1.2rem; padding: 0.7rem 1.5rem; border: none; border-radius: 50px; color: #fff; font-weight: 600; cursor: pointer; transition: transform 0.2s, box-shadow 0.2s; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Ubuntu', 'Roboto', 'Noto Sans', 'Droid Sans', 'Helvetica Neue', Arial, sans-serif;">
                        Remove Friend
                    </button>
                </form>
            <% } %>
            <a class="btn" href="profile" style="margin-right: 1rem;">View My Profile</a>
        <% } %>
        <% if (isViewingOwnProfile) { %>
            <a class="btn" href="createQuiz" style="margin-right: 1rem;">Create Quiz</a>
        <% } %>
        <a class="btn" href="quizzes.jsp">Browse Quizzes</a>
    </div>

    <% if (isAdmin && isViewingOwnProfile) { %>
    <div class="admin-announcement-section">
        <h3>Post a New Announcement</h3>
        <form action="announcement" method="post">
            <input type="text" name="title" placeholder="Title" required />
            <textarea name="text" placeholder="Announcement text" required></textarea>
            <button type="submit">Publish</button>
        </form>
    </div>
    <% } %>


    <% if (profileUser.isAdmin()) { %>
    <div class="history-section">
        <h3><%= isViewingOwnProfile ? "My Created Quizzes" : profileUser.getUsername() + "'s Created Quizzes" %></h3>
        <%
            @SuppressWarnings("unchecked")
            List<com.freeuni.quizapp.model.Quiz> createdQuizzes = (List<com.freeuni.quizapp.model.Quiz>) request.getAttribute("createdQuizzes");
            @SuppressWarnings("unchecked")
            java.util.Map<Integer, Integer> questionCounts = (java.util.Map<Integer, Integer>) request.getAttribute("questionCounts");

            if (createdQuizzes != null && !createdQuizzes.isEmpty()) {
        %>
            <ul class="history-list">
                <% for (com.freeuni.quizapp.model.Quiz quiz : createdQuizzes) { %>
                    <li class="history-item">
                        <strong><%= quiz.getTitle() %></strong><br>
                        <% if (quiz.getDescription() != null && !quiz.getDescription().trim().isEmpty()) { %>
                            Description: <%= quiz.getDescription() %><br>
                        <% } %>
                        Questions: <%= questionCounts != null && questionCounts.get(quiz.getId()) != null ? questionCounts.get(quiz.getId()) : "0" %><br>
                        Created: <%= quiz.getCreatedAt() != null ? quiz.getCreatedAt().toString().substring(0, 16) : "Unknown" %>
                    </li>
                <% } %>
            </ul>
        <% } else { %>
            <div class="no-history">
                <% if (isViewingOwnProfile) { %>
                    You haven't created any quizzes yet.
                    <a href="quizzes.jsp" style="color: #E85A4F;">Create your first quiz!</a>
                <% } else { %>
                    <%= profileUser.getUsername() %> hasn't created any quizzes yet.
                <% } %>
            </div>
        <% } %>
    </div>
    <% } %>

    <div class="achievements-section">
        <h3><%= isViewingOwnProfile ? "My Achievements" : profileUser.getUsername() + "'s Achievements" %></h3>
        <%
            @SuppressWarnings("unchecked")
            List<com.freeuni.quizapp.model.Achievement> achievements = (List<com.freeuni.quizapp.model.Achievement>) request.getAttribute("achievements");
            @SuppressWarnings("unchecked")
            List<String> greatestQuizNames = (List<String>) request.getAttribute("greatestQuizNames");
            if (achievements != null && !achievements.isEmpty()) {
        %>
            <div class="achievements-grid">
                <% for (com.freeuni.quizapp.model.Achievement achievement : achievements) {
                    String icon = "";
                    String displayName = "";
                    String description = "";

                    switch (achievement.getType()) {
                        case Amateur_Author:
                            icon = "📝";
                            displayName = "Amateur Author";
                            description = "Created your first quiz";
                            break;
                        case Prolific_Author:
                            icon = "✍️";
                            displayName = "Prolific Author";
                            description = "Created 5 quizzes";
                            break;
                        case Prodigious_Author:
                            icon = "📚";
                            displayName = "Prodigious Author";
                            description = "Created 10 quizzes";
                            break;
                        case Quiz_Machine:
                            icon = "🎯";
                            displayName = "Quiz Machine";
                            description = "Completed 10 quizzes";
                            break;
                        case I_am_the_Greatest:
                            icon = "🏆";
                            displayName = "I am the Greatest";
                            description = "Achieved the highest score on quiz";

                            // Show all quizzes where user has the highest score
                            if (greatestQuizNames != null && !greatestQuizNames.isEmpty()) {
                                if (greatestQuizNames.size() == 1) {
                                    description = "Achieved the highest score on: <strong>" + greatestQuizNames.get(0) + "</strong>";
                                } else {
                                    StringBuilder sb = new StringBuilder("Achieved the highest score on:<br/>");
                                    for (int i = 0; i < greatestQuizNames.size(); i++) {
                                        sb.append("<strong>• ").append(greatestQuizNames.get(i)).append("</strong>");
                                        if (i < greatestQuizNames.size() - 1) {
                                            sb.append("<br/>");
                                        }
                                    }
                                    description = sb.toString();
                                }
                            }
                            break;
                        case Practice_Makes_Perfect:
                            icon = "⚡";
                            displayName = "Practice Makes Perfect";
                            description = "Completed a quiz in practice mode";
                            break;
                    }
                %>
                    <div class="achievement-card">
                        <div class="achievement-icon"><%= icon %></div>
                        <div class="achievement-name"><%= displayName %></div>
                        <div class="achievement-description"><% out.print(description); %></div>
                        <div class="achievement-date">
                            Earned: <%= achievement.getAchievedAt() != null ? achievement.getAchievedAt().toString().substring(0, 16) : "Unknown" %>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } else { %>
            <div class="no-achievements">
                <%= isViewingOwnProfile ? "No achievements yet. Start taking quizzes to earn your first achievement!" : profileUser.getUsername() + " has no achievements yet." %>
            </div>
        <% } %>
    </div>
    <div class="history-section">
        <h3><%= isViewingOwnProfile ? "My Recent Activity" : profileUser.getUsername() + "'s Recent Activity" %></h3>
        <%
            @SuppressWarnings("unchecked")
            List<String> history = (List<String>) request.getAttribute("history");
            if (history != null && !history.isEmpty()) {
        %>
            <ul class="history-list">
                <% for (String item : history) { %>
                    <li class="history-item"><%= item %></li>
                <% } %>
            </ul>
        <% } else { %>
            <div class="no-history">
                <%= isViewingOwnProfile ? "No recent activity to display." : profileUser.getUsername() + " has no recent activity to display." %>
            </div>
        <% } %>
    </div>
</div>

</body>
</html>