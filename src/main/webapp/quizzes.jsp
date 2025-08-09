<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.Connection, java.util.List" %>
<%@ page import="com.freeuni.quizapp.util.DBConnector" %>
<%@ page import="com.freeuni.quizapp.model.Quiz" %>
<%@ page import="com.freeuni.quizapp.model.Question" %>
<%@ page import="com.freeuni.quizapp.dao.impl.QuizDaoImpl" %>
<%@ page import="com.freeuni.quizapp.dao.impl.QuestionDaoImpl" %>
<%@ page import="com.freeuni.quizapp.enums.QuestionType" %>
<%
    session.removeAttribute("currentQuiz");
    session.removeAttribute("quizAnswers");
    session.removeAttribute("quizStartTime");
    session.removeAttribute("isActiveQuizSession");
    session.removeAttribute("lastQuizId");
%>
<%
    com.freeuni.quizapp.model.User currentUser = (com.freeuni.quizapp.model.User) session.getAttribute("currentUser");
    
    Connection conn = null;
    List<Quiz> quizzes = null;
    List<com.freeuni.quizapp.model.User> userFriends = null;
    try {
        conn = DBConnector.getConnection();
        QuizDaoImpl quizDao = new QuizDaoImpl(conn);
        quizzes = quizDao.listRecentQuizzes(1000);
        
        if (currentUser != null) {
            com.freeuni.quizapp.dao.impl.FriendSystemDaoImpl friendSystemDao = new com.freeuni.quizapp.dao.impl.FriendSystemDaoImpl(conn);
            userFriends = friendSystemDao.getUsersFriends(currentUser.getId());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Browse Quizzes - Quizology</title>
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
             background:#fafafa; 
            }
        .navbar { 
            position: sticky; 
            top: 0; 
            background:#ffffff; 
            display:flex; 
            align-items:center; 
            padding:1rem 4.5%; 
            box-shadow:0 2px 8px rgba(0,0,0,.05); 
            z-index:100; 
            position:relative; 
            animation: slideDown 0.8s ease-out both; 
        }
        .brand { 
            font-size:1.6rem; 
            font-weight:700; 
            background: var(--gradient-accent); 
            background-clip:text; 
            -webkit-background-clip:text; 
            -webkit-text-fill-color:transparent; 
            text-decoration:none; 
        }
        .nav-links {
            list-style: none;
            display: flex;
            gap: 2rem;
            margin-left: auto;
        }
        .nav-links a { 
            text-decoration:none; 
            color:var(--text-secondary); 
            font-weight:500; 
        }
        .nav-links a:hover { 
            color:#E85A4F; 
        }

        .search-bar { 
            position:absolute; 
            left:50%; 
            transform:translateX(-50%); 
            display:flex; 
            justify-content:center; 
            width:42%; 
            min-width:260px; 
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

        .browse-header { 
            text-align:center; 
            padding:4.5% 4.5% 2%; 
        }

        .browse-header h1 { 
            font-size:2.8rem; 
            font-weight:800; 
            background: var(--gradient-accent); 
            background-clip:text; 
            -webkit-background-clip:text;
            -webkit-text-fill-color:transparent; 
            animation: fadeInUp 0.8s ease-out 0.3s both; 
        }

        .browse-header p {
            margin-top: 0.8rem;
            font-size: 1.05rem;
            animation: fadeIn 0.8s ease-out 0.6s both;
        }

        .quiz-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            column-gap: 1.2rem;
            row-gap: 2rem;
            padding: 0 4.5% 6%;
        }
        
        @media (max-width: 900px) {
            .quiz-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }

        @media (max-width: 600px) {
            .quiz-grid {
                grid-template-columns: 1fr;
            }
        }

        .quiz-card {
            position: relative;
            background: #ffffff;
            border-radius: 14px;
            box-shadow: var(--card-shadow);
            padding: 1.8rem 1.6rem 2rem;
            display: flex;
            flex-direction: column;
            transition: transform .28s cubic-bezier(.2,.8,.4,1), box-shadow .28s ease;
            overflow: hidden;
            width: 100%;
        }
        .quiz-card-header {
            height: 120px;
            border-radius: 10px;
            margin: -1.8rem -1.6rem 1rem -1.6rem;
            background: linear-gradient(135deg, #E85A4F 0%, #F09B7D 100%);
        }
        .quiz-card::after { 
            content:""; 
            position:absolute; 
            inset:0; 
            background:var(--gradient-accent); 
            opacity:0; 
            transition:opacity .28s ease; 
            pointer-events:none; 
        }
        .quiz-card:hover { 
            transform: translateY(-12px) scale(1.05); 
            box-shadow:0 20px 36px rgba(0,0,0,.12); 
        }
        .quiz-card:hover::after { 
            opacity:0.05; 
        }
        .quiz-card h3 {
            font-size: 1.25rem;
            font-weight: 700;
            margin-bottom: 0.6rem;
            color: #333333;
            text-align: center;
        }
        .quiz-card p {
            flex: 1;
            font-size: 0.95rem;
            margin-bottom: 1.2rem;
            text-align: center;
        }
        .quiz-actions {
            display: flex;
            gap: 0.75rem;
            align-items: center;
            justify-content: center;
            margin-top: auto;
        }

        .btn-start { 
            padding:0.75rem 1.6rem; 
            border:none; 
            border-radius:50px;
            background:var(--gradient-accent); 
            color:#ffffff; 
            font-weight:600; 
            text-decoration:none; 
            cursor:pointer; 
            transition:transform .2s ease, box-shadow .2s ease;
            flex: 1;
            text-align: center;
        }
        .btn-start:hover { 
            transform:translateY(-3px); 
            box-shadow:0 8px 20px rgba(232,90,79,.35); 
        }

        .btn-challenge {
            padding: 0.75rem 1.2rem;
            border: 2px solid #E85A4F;
            border-radius: 50px;
            background: white;
            color: #E85A4F;
            font-weight: 600;
            text-decoration: none;
            cursor: pointer;
            transition: all .2s ease;
            font-size: 0.85rem;
        }

        .btn-challenge:hover {
            background: #E85A4F;
            color: white;
            transform: translateY(-2px);
        }

        .challenge-modal {
            background: #ffffff;
            border-radius: 14px;
            width: min(420px, 90%);
            padding: 2rem 1.8rem;
            box-shadow: 0 12px 32px rgba(0, 0, 0, .12);
            opacity: 0;
            transform: translateY(40px) scale(0.95);
            animation: none;
        }

        .friend-select {
            width: 100%;
            padding: 0.75rem 1rem;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 0.95rem;
            margin-bottom: 1rem;
            background: white;
        }

        .friend-select:focus {
            outline: none;
            border-color: #E85A4F;
        }

        .btn-send-challenge {
            width: 100%;
            padding: 0.85rem;
            border: none;
            border-radius: 50px;
            background: var(--gradient-accent);
            color: white;
            font-weight: 600;
            cursor: pointer;
            margin-top: 1rem;
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
            display:flex; 
            justify-content:space-between; 
            align-items:center; 
            margin-bottom:1.4rem; 
        }

        .modal-header h2 { 
            font-size:1.4rem; 
            font-weight:700; 
            color:#333333; 
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

        #settingsStartBtn {
            margin-top: 1.5rem;
            width: 100%;
            padding: 0.9rem 0;
            border: none;
            border-radius: 50px;
            background: var(--gradient-accent);
            color: #ffffff;
            font-weight: 600;
            cursor: pointer;
        }

        .quiz-info {
            background: #f7f7f7;
            padding: 1rem 1.2rem;
            border-radius: 10px;
            margin-bottom: 1.4rem;
            text-align: center;
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
            box-shadow: 0 4px 12px rgba(0, 0, 0, .08);
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
        
        .overlay:target .settings-modal,
        .overlay:target .challenge-modal {
            animation: modalIn 0.4s cubic-bezier(.25,.8,.25,1) forwards;
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
        <input type="text" name="q" placeholder="Search" required />
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

<section class="browse-header">
    <h1>Browse Quizzes</h1>
    <p>Explore our collection and challenge yourself!</p>
</section>

<section class="quiz-grid">
    <% if (quizzes != null) {
           for (Quiz q : quizzes) { %>
            <div class="quiz-card">
                <div class="quiz-card-header"></div>
                <h3><%= q.getTitle() %></h3>
                <p><%= q.getDescription() == null ? "No description." : q.getDescription() %></p>
                <div class="quiz-actions">
                    <a href="#settings_<%= q.getId() %>" class="btn-start">Start Quiz</a>
                    <% if (currentUser != null && userFriends != null && !userFriends.isEmpty()) { %>
                        <a href="#challenge_<%= q.getId() %>" class="btn-challenge">Challenge</a>
                    <% } %>
                </div>
            </div>

            <div id="settings_<%= q.getId() %>" class="overlay">
                <div class="settings-modal">
                    <form action="startQuiz" method="get">
                        <input type="hidden" name="quizId" value="<%= q.getId() %>">
                        <div class="modal-header">
                            <h2>Quiz Settings</h2>
                        </div>
                        <div class="quiz-info">
                            <h3><%= q.getTitle() %></h3>
                            <p><%= q.getDescription() == null ? "No description." : q.getDescription() %></p> 
                        </div>
                        <div class="setting">
                            <div class="setting-row">
                                <label for="randToggle_<%= q.getId() %>">Randomize Question Order</label>
                                <input type="checkbox" class="toggle" id="randToggle_<%= q.getId() %>" name="random" value="true">
                            </div>
                            <p class="setting-desc">Shuffle questions for a different experience each time</p>
                        </div>
                        <div class="setting">
                            <div class="setting-row">
                                <label for="onePageToggle_<%= q.getId() %>">One Page Mode</label>
                                <input type="checkbox" class="toggle" id="onePageToggle_<%= q.getId() %>" name="onePage" value="true">
                            </div>
                            <p class="setting-desc">Show all questions on a single page</p>
                        </div>
                        <% 
                            boolean hasMultipleChoice = false;
                            try {
                                QuestionDaoImpl questionDao = new QuestionDaoImpl(conn);
                                List<Question> questions = questionDao.getQuizAllQuestions(q.getId());
                                if (!questions.isEmpty()) {
                                    hasMultipleChoice = questions.get(0).getType() == QuestionType.multiple_choice;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (hasMultipleChoice) { 
                        %>
                        <div class="setting">
                            <div class="setting-row">
                                <label for="immToggle_<%= q.getId() %>">Immediate Correction</label>
                                <input type="checkbox" class="toggle" id="immToggle_<%= q.getId() %>" name="immediate" value="true">
                            </div>
                            <p class="setting-desc">Show correct answers immediately after selecting (multiple choice only)</p>
                        </div>
                        <% } %>
                        <div class="setting">
                            <div class="setting-row">
                                <label for="practiceToggle_<%= q.getId() %>">Practice Mode</label>
                                <input type="checkbox" class="toggle" id="practiceToggle_<%= q.getId() %>" name="practice" value="true">
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

            <% if (currentUser != null && userFriends != null && !userFriends.isEmpty()) { %>
            <div id="challenge_<%= q.getId() %>" class="overlay">
                <div class="challenge-modal">
                    <form method="post" action="challenge">
                        <input type="hidden" name="action" value="send">
                        <input type="hidden" name="quizId" value="<%= q.getId() %>">
                        <input type="hidden" name="redirectUrl" value="quizzes.jsp">
                        
                        <div class="modal-header">
                            <h2>Challenge a Friend</h2>
                        </div>
                        
                        <div class="quiz-info">
                            <h3><%= q.getTitle() %></h3>
                            <p>Send this quiz as a challenge to one of your friends!</p>
                        </div>
                        
                        <select name="friendUsername" class="friend-select" required>
                            <option value="">Select a friend to challenge...</option>
                            <% for (com.freeuni.quizapp.model.User friend : userFriends) { %>
                                <option value="<%= friend.getUsername() %>"><%= friend.getUsername() %></option>
                            <% } %>
                        </select>
                        
                        <div style="display:flex; gap:1rem;">
                            <a href="#" class="btn-cancel">Cancel</a>
                            <button type="submit" class="btn-send-challenge" style="flex: 1;">Send Challenge</button>
                        </div>
                    </form>
                </div>
            </div>
            <% } %>
    <%     }
       } else { %>
        <p>No quizzes available.</p>
    <% } %>
</section>

</body>
</html> 