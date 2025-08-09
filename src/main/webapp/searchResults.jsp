<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.freeuni.quizapp.model.User" %>
<%@ page import="com.freeuni.quizapp.model.Quiz" %>
<%@ page import="com.freeuni.quizapp.enums.FriendshipStatus" %>
<%
    String searchQuery = (String) request.getAttribute("searchQuery");
    String searchType = (String) request.getAttribute("searchType");
    List<User> foundUsers = (List<User>) request.getAttribute("foundUsers");
    List<Quiz> foundQuizzes = (List<Quiz>) request.getAttribute("foundQuizzes");
    Map<Integer, FriendshipStatus> friendshipStatuses = (Map<Integer, FriendshipStatus>) request.getAttribute("friendshipStatuses");
    
    User currentUser = (User) session.getAttribute("currentUser");
    boolean isLoggedIn = currentUser != null;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Results - Quizology</title>
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
            position: relative;
            animation: slideDown 0.8s ease-out both;
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
            padding: 0.45rem 1rem 0.45rem 2rem;
            border: 1px solid #e0e0e0;
            border-radius: 50px;
            font-size: 0.9rem;
            width: 60%;
            max-width: 420px;
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
            color: var(--text-secondary);
            white-space: nowrap;
        }
        
        .profile .dropdown li a:hover {
            background: #f7f7f7;
            color: #E85A4F;
        }
        
        .search-header {
            text-align: center;
            padding: 4.5% 4.5% 2%;
        }
        
        .search-header h1 {
            font-size: 2.8rem;
            font-weight: 800;
            background: var(--gradient-accent);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            animation: fadeInUp 0.8s ease-out 0.3s both;
        }
        
        .search-info {
            margin-top: 0.8rem;
            font-size: 1.05rem;
            animation: fadeIn 0.8s ease-out 0.6s both;
        }
        
        .results-section {
            padding: 0 4.5% 3%;
        }
        
        .section-title {
            font-size: 1.8rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 1.5rem;
            border-bottom: 2px solid #E85A4F;
            padding-bottom: 0.5rem;
        }
        
        .results-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 1.5rem;
            margin-bottom: 3rem;
        }
        
        .result-card {
            background: #ffffff;
            border-radius: 14px;
            box-shadow: var(--card-shadow);
            padding: 1.8rem;
            transition: transform .28s cubic-bezier(.2,.8,.4,1), box-shadow .28s ease;
        }
        
        .result-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 20px 36px rgba(0,0,0,.12);
        }
        
        .user-card h3 {
            font-size: 1.25rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 0.5rem;
        }
        
        .user-bio {
            font-size: 0.95rem;
            color: var(--text-secondary);
            margin-bottom: 1rem;
            min-height: 2.5rem;
        }
        
        .quiz-card h3 {
            font-size: 1.25rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 0.5rem;
        }
        
        .quiz-description {
            font-size: 0.95rem;
            color: var(--text-secondary);
            margin-bottom: 1rem;
            min-height: 2.5rem;
        }
        
        .btn-view {
            background: var(--gradient-accent);
            color: white;
            padding: 0.6rem 1.4rem;
            border-radius: 50px;
            text-decoration: none;
            font-weight: 600;
            font-size: 0.9rem;
            transition: transform .2s ease;
            display: inline-block;
        }
        
        .btn-view:hover {
            transform: translateY(-2px);
            text-decoration: none;
            color: white;
        }
        
        .no-results {
            text-align: center;
            padding: 3rem;
            color: var(--text-secondary);
            font-size: 1.1rem;
        }
        
        .back-link {
            display: inline-block;
            margin: 2rem 4.5%;
            color: #E85A4F;
            text-decoration: none;
            font-weight: 600;
        }
        
        .back-link:hover {
            text-decoration: underline;
        }

        .overlay { 
            position: fixed;
            inset: 0;
            background: rgba(0, 0, 0, .35);
            display: none;
            align-items: center;
            justify-content: center;
            z-index: 200;
        }
        .overlay:target { 
            display: flex;
            animation: fadeOverlay 0.3s ease-out forwards;
        }
        .settings-modal {
            background: #ffffff;
            border-radius: 14px;
            width: min(480px, 90%);
            padding: 2rem 1.8rem 3rem;
            box-shadow: 0 12px 32px rgba(0, 0, 0, .12);
            opacity: 0;
            transform: translateY(40px) scale(0.95);
            animation: none; 
        }
        .modal-header { 
            display: flex; 
            justify-content: space-between; 
            align-items: center; 
            margin-bottom: 1.4rem; 
        }

        .modal-header h2 { 
            font-size: 1.4rem; 
            font-weight: 700; 
            color: #333333; 
        }
        
        .setting {
            margin-bottom: 1rem;
        }

        .setting-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .setting label {
            font-size: 0.95rem;
        }

        .setting input {
            width: 40px;
            height: 20px;
        }

        .setting-desc {
            font-size: 0.78rem;
            color: var(--text-secondary);
            margin-top: 0.25rem;
        }

        .quiz-info {
            background: #f7f7f7;
            padding: 1rem 1.2rem;
            border-radius: 10px;
            margin-bottom: 1.4rem;
        }

        .quiz-info h3 {
            margin-bottom: 0.35rem;
            font-size: 1.1rem;
            color: #333333;
        }

        .quiz-info p {
            font-size: 0.85rem;
            color: var(--text-secondary);
        }

        .btn-cancel,
        .btn-start-confirm {
            flex: 1 1 50%;
            text-align: center;
            padding: 0.85rem 0;
            border-radius: 50px;
            font-weight: 600;
            text-decoration: none;
        }

        .btn-cancel {
            background: #6c757d;
            color: #ffffff;
        }

        .btn-start-confirm {
            background: var(--gradient-accent);
            color: #ffffff;
        }

        .toggle {
            appearance: none;
            width: 42px;
            height: 22px;
            background: #dcdcdc;
            border-radius: 50px;
            position: relative;
            cursor: pointer;
            outline: none;
            transition: background 0.3s;
        }

        .toggle::before {
            content: "";
            position: absolute;
            left: 2px;
            top: 50%;
            transform: translateY(-50%);
            width: 18px;
            height: 18px;
            background: #ffffff;
            border-radius: 50%;
            transition: transform 0.3s;
        }

        .toggle:checked {
            background: #E85A4F;
        }

        .toggle:checked::before {
            transform: translate(20px, -50%);
        }

        .toggle:hover::before {
            box-shadow: 0 0 4px rgba(0, 0, 0, 0.25);
        }
        
        .overlay:target .settings-modal {
            animation: modalIn 0.4s cubic-bezier(.25,.8,.25,1) forwards;
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

        @keyframes fadeOverlay {
            from { opacity: 0; }
            to   { opacity: 1; }
        }

        @keyframes modalIn {
            from { opacity: 0; transform: translateY(40px) scale(0.95); }
            to   { opacity: 1; transform: translateY(0) scale(1); }
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <a href="/home" class="brand">Quizology</a>
        
        <form class="search-bar" action="search" method="get">
            <input type="text" name="q" value="<%= searchQuery != null ? searchQuery : "" %>" placeholder="Search" required/>
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

    <div class="search-header">
        <h1>Search Results</h1>
        <% if (searchQuery != null) { %>
            <p class="search-info">
                Showing results for "<%= searchQuery %>" 
                <% if (!"all".equals(searchType)) { %>
                    in <%= searchType %>
                <% } %>
            </p>
        <% } %>
    </div>

    <% if (("all".equals(searchType) || "users".equals(searchType)) && foundUsers != null && !foundUsers.isEmpty()) { %>
        <div class="results-section">
            <h2 class="section-title">Users (<%= foundUsers.size() %>)</h2>
            <div class="results-grid">
                <% for (User user : foundUsers) { %>
                    <div class="result-card user-card">
                        <h3>
                            <%= user.getUsername() %>
                            <% if (currentUser != null && !user.getUsername().equals(currentUser.getUsername())) { 
                                FriendshipStatus status = friendshipStatuses != null ? friendshipStatuses.get(user.getId()) : null;
                                if (status == FriendshipStatus.accepted) { %>
                                    <span style="font-size: 0.7rem; background: linear-gradient(135deg, #E85A4F, #D32F2F); color: white; padding: 0.2rem 0.5rem; border-radius: 12px; margin-left: 0.5rem; font-weight: 500;">Friends</span>
                            <% } } %>
                        </h3>
                        <div class="user-bio">
                            <%= user.getBio() != null && !user.getBio().trim().isEmpty() ? user.getBio() : "No bio available" %>
                        </div>
                        <div style="display: flex; gap: 0.5rem; margin-top: 1rem; justify-content: center; align-items: center;">
                            <a href="profile?username=<%= user.getUsername() %>" class="btn-view" style="flex: 1; text-align: center; padding: 0.6rem 1.4rem; font-weight: 600; background: linear-gradient(135deg, #E85A4F, #D32F2F); max-width: 120px; display: flex; align-items: center; justify-content: center;">View Profile</a>
                            <% if (currentUser != null && !user.getUsername().equals(currentUser.getUsername())) { 
                                FriendshipStatus status = friendshipStatuses != null ? friendshipStatuses.get(user.getId()) : null;
                                if (status == null) { %>
                                    <form method="post" action="friendRequest" style="flex: 1; max-width: 120px;">
                                        <input type="hidden" name="action" value="send">
                                        <input type="hidden" name="targetUsername" value="<%= user.getUsername() %>">
                                        <button type="submit" class="btn-view" 
                                                style="width: 100%; background: linear-gradient(135deg, #28a745, #20c997); border: none; cursor: pointer; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; font-weight: 600; padding: 0.6rem 1.4rem; border-radius: 50px; color: white; transition: transform .2s ease; display: flex; align-items: center; justify-content: center; text-align: center;">
                                            Add Friend
                                        </button>
                                    </form>
                                <% } else if (status == FriendshipStatus.pending) { %>
                                    <div style="flex: 1; display: flex; align-items: center; justify-content: center; padding: 0.6rem 1.4rem; background: linear-gradient(135deg, #6c757d, #5a6268); border-radius: 50px; color: white; font-weight: 600; font-size: 0.9rem; max-width: 120px; text-align: center;">
                                        Request Sent
                                    </div>
                                <% } 
                            } %>
                        </div>
                    </div>
                <% } %>
            </div>
        </div>
    <% } %>

    <% if (("all".equals(searchType) || "quizzes".equals(searchType)) && foundQuizzes != null && !foundQuizzes.isEmpty()) { %>
        <div class="results-section">
            <h2 class="section-title">Quizzes (<%= foundQuizzes.size() %>)</h2>
            <div class="results-grid">
                <% for (Quiz quiz : foundQuizzes) { %>
                    <div class="result-card quiz-card">
                        <h3><%= quiz.getTitle() %></h3>
                        <div class="quiz-description">
                            <%= quiz.getDescription() != null && !quiz.getDescription().trim().isEmpty() ? quiz.getDescription() : "No description available" %>
                        </div>
                        <a href="#settings_<%= quiz.getId() %>" class="btn-view">Take Quiz</a>
                    </div>

                    <div id="settings_<%= quiz.getId() %>" class="overlay">
                        <div class="settings-modal">
                            <form action="startQuiz" method="get">
                                <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
                                <div class="modal-header">
                                    <h2>Quiz Settings</h2>
                                </div>
                                <div class="quiz-info">
                                    <h3><%= quiz.getTitle() %></h3>
                                    <p><%= quiz.getDescription() != null && !quiz.getDescription().trim().isEmpty() ? quiz.getDescription() : "No description available" %></p> 
                                </div>
                                <div class="setting">
                                    <div class="setting-row">
                                        <label for="randToggle_<%= quiz.getId() %>">Randomize Question Order</label>
                                        <input type="checkbox" class="toggle" id="randToggle_<%= quiz.getId() %>" name="random" value="true">
                                    </div>
                                    <p class="setting-desc">Shuffle questions for a different experience each time</p>
                                </div>
                                <div class="setting">
                                    <div class="setting-row">
                                        <label for="onePageToggle_<%= quiz.getId() %>">One Page Mode</label>
                                        <input type="checkbox" class="toggle" id="onePageToggle_<%= quiz.getId() %>" name="onePage" value="true">
                                    </div>
                                    <p class="setting-desc">Show all questions on a single page</p>
                                </div>
                                <div class="setting">
                                    <div class="setting-row">
                                        <label for="immToggle_<%= quiz.getId() %>">Immediate Correction</label>
                                        <input type="checkbox" class="toggle" id="immToggle_<%= quiz.getId() %>" name="immediate" value="true">
                                    </div>
                                    <p class="setting-desc">Show correct answers immediately after selecting (multiple choice only)</p>
                                </div>
                                <div class="setting">
                                    <div class="setting-row">
                                        <label for="practiceToggle_<%= quiz.getId() %>">Practice Mode</label>
                                        <input type="checkbox" class="toggle" id="practiceToggle_<%= quiz.getId() %>" name="practice" value="true">
                                    </div>
                                    <p class="setting-desc">Take quiz without time limits or score tracking</p>
                                </div>
                                <div style="display:flex; gap:1rem; margin-top:1.3rem;">
                                    <a href="#" class="btn-cancel">Cancel</a>
                                    <button type="submit" class="btn-start-confirm" style="border:none; cursor:pointer;">Start Quiz</button>
                                </div>
                            </form>
                        </div>
                    </div>
                <% } %>
            </div>
        </div>
    <% } %>

    <% if ((foundUsers == null || foundUsers.isEmpty()) && (foundQuizzes == null || foundQuizzes.isEmpty())) { %>
        <div class="no-results">
            <% if (searchQuery != null) { %>
                <h3>No results found for "<%= searchQuery %>"</h3>
                <p>Try different keywords or browse all content.</p>
            <% } else { %>
                <h3>Search for users and quizzes</h3>
                <p>Enter a search term to find content.</p>
            <% } %>
        </div>
    <% } %>

    <a href="quizzes.jsp" class="back-link">← Back to Browse</a>
</body>
</html> 