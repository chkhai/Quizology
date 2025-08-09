<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.freeuni.quizapp.model.User" %>
<%
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
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Friends - Quizology</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Ubuntu", "Roboto", "Noto Sans", "Droid Sans", "Helvetica Neue", Arial, sans-serif;
        }

        :root {
            --primary-gradient: linear-gradient(135deg, #EAE7DC 0%, #D8C3A5 100%);
            --accent-gradient: linear-gradient(135deg, #E85A4F 0%, #E9704F 100%);
            --gradient-accent: linear-gradient(135deg, #E85A4F 0%, #E9704F 100%);
            --card-bg: rgba(255, 255, 255, 0.9);
            --card-border: rgba(0, 0, 0, 0.05);
            --text-primary: #8E8D8A;
            --text-secondary: #8E8D8A;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Ubuntu", "Roboto", "Noto Sans", "Droid Sans", "Helvetica Neue", Arial, sans-serif;
            background: var(--primary-gradient);
            min-height: 100vh;
            color: #333;
            line-height: 1.6;
        }

        .navbar {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            padding: 1rem 4.5%;
            position: sticky;
            top: 0;
            z-index: 100;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            animation: slideDown 0.8s ease-out both;
        }

        .brand {
            font-size: 1.5rem;
            font-weight: 700;
            background: linear-gradient(135deg, #E85A4F, #D32F2F);
            background-clip: text;
            -webkit-background-clip: text;
            color: transparent;
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

        .nav-links {
            display: flex;
            list-style: none;
            gap: 2rem;
            align-items: center;
        }

        .nav-links a {
            text-decoration: none;
            color: var(--text-secondary);
            font-weight: 500;
            transition: color 0.3s ease;
        }

        .nav-links a:hover {
            color: #E85A4F;
        }
        


        .profile {
            position: relative;
        }

        .dropdown {
            position: absolute;
            top: 100%;
            left: 50%;
            transform: translateX(-50%) translateY(-10px);
            background: white;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            list-style: none;
            padding: 0.5rem 0;
            min-width: 160px;
            opacity: 0;
            visibility: hidden;
            transition: all 0.3s ease;
        }

        .profile:hover .dropdown {
            opacity: 1;
            visibility: visible;
            transform: translateX(-50%) translateY(0);
        }

        .dropdown li {
            padding: 0.5rem 1rem;
        }

        .dropdown a {
            color: var(--text-secondary);
            text-decoration: none;
            display: block;
        }

        .dropdown a:hover {
            color: #E85A4F;
        }

        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 2rem;
        }

        .friends-header {
            text-align: center;
            padding: 4.5% 4.5% 2%;
            background: #ffffff;
        }
        
        .friends-header h1 {
            font-size: 2.8rem;
            font-weight: 800;
            background: var(--gradient-accent);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            animation: fadeInUp 0.8s ease-out 0.3s both;
        }
        
        .friends-header p {
            margin-top: 0.8rem;
            font-size: 1.05rem;
            animation: fadeIn 0.8s ease-out 0.6s both;
        }

        .friends-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 2rem;
            margin-bottom: 2rem;
        }

        .friends-section {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 16px;
            padding: 2rem;
            box-shadow: 0 8px 24px rgba(0,0,0,0.1);
            backdrop-filter: blur(10px);
        }

        .section-title {
            font-size: 1.5rem;
            font-weight: 700;
            color: #E85A4F;
            margin-bottom: 1.5rem;
            text-align: center;
            border-bottom: 2px solid #E85A4F;
            padding-bottom: 0.5rem;
        }

        .user-list {
            list-style: none;
        }

        .user-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1rem;
            margin-bottom: 0.75rem;
            background: #f8f9fa;
            border-radius: 8px;
            border-left: 4px solid #E85A4F;
            transition: transform 0.2s ease;
        }

        .user-item:hover {
            transform: translateX(5px);
        }

        .user-info {
            flex-grow: 1;
        }

        .username {
            font-weight: 600;
            color: #333;
            text-decoration: none;
            font-size: 1.1rem;
        }

        .username:hover {
            color: #E85A4F;
        }

        .user-actions {
            display: flex;
            gap: 0.5rem;
        }

        .btn {
            padding: 0.5rem 1rem;
            border: none;
            border-radius: 6px;
            font-size: 0.9rem;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
        }

        .btn-accept {
            background: linear-gradient(135deg, #28a745, #20c997);
            color: white;
        }

        .btn-accept:hover {
            background: linear-gradient(135deg, #218838, #1abc9c);
            transform: translateY(-2px);
        }

        .btn-reject {
            background: linear-gradient(135deg, #dc3545, #c82333);
            color: white;
        }

        .btn-reject:hover {
            background: linear-gradient(135deg, #c82333, #a71e2a);
            transform: translateY(-2px);
        }

        .btn-remove {
            background: linear-gradient(135deg, #6c757d, #5a6268);
            color: white;
        }

        .btn-remove:hover {
            background: linear-gradient(135deg, #5a6268, #495057);
            transform: translateY(-2px);
        }

        .btn-view {
            background: linear-gradient(135deg, #E85A4F, #D32F2F);
            color: white;
        }

        .btn-view:hover {
            background: linear-gradient(135deg, #D32F2F, #B71C1C);
            transform: translateY(-2px);
        }

        .btn-message {
            background: linear-gradient(135deg, #007bff, #0056b3);
            color: white;
        }

        .btn-message:hover {
            background: linear-gradient(135deg, #0056b3, #004085);
            transform: translateY(-2px);
        }
        .empty-message {
            text-align: center;
            color: #666;
            font-style: italic;
            padding: 2rem;
            background: #f8f9fa;
            border-radius: 8px;
            border: 2px dashed #dee2e6;
        }

        .error-message {
            background: #f8d7da;
            color: #721c24;
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1rem;
            text-align: center;
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
            .friends-grid {
                grid-template-columns: 1fr;
            }
            
            .user-item {
                flex-direction: column;
                align-items: flex-start;
                gap: 1rem;
            }
            
            .user-actions {
                align-self: stretch;
                justify-content: space-between;
            }
        }
    </style>
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

<section class="friends-header">
    <h1>Friends</h1>
    <p>Connect with other quiz enthusiasts!</p>
</section>

<div class="container">
    <% String error = (String) request.getAttribute("error");
       if (error != null) { %>
        <div class="error-message"><%= error %></div>
    <% } %>

    <div class="friends-grid">
        <div class="friends-section">
            <h2 class="section-title">My Friends</h2>
            <%
                @SuppressWarnings("unchecked")
                List<User> friends = (List<User>) request.getAttribute("friends");
                if (friends != null && !friends.isEmpty()) {
            %>
                <ul class="user-list">
                    <% for (User friend : friends) { %>
                        <li class="user-item">
                            <div class="user-info">
                                <a href="profile?username=<%= friend.getUsername() %>" class="username">
                                    <%= friend.getUsername() %>
                                </a>
                            </div>
                            <div class="user-actions">
                                <a href="inbox?with=<%= friend.getId() %>" class="btn btn-message">
                                    Send Message
                                </a>
                                <form method="post" action="friendRequest" style="display: inline;">
                                    <input type="hidden" name="action" value="remove">
                                    <input type="hidden" name="targetUserId" value="<%= friend.getId() %>">
                                    <button type="submit" class="btn btn-remove">
                                        Remove
                                    </button>
                                </form>
                            </div>
                        </li>
                    <% } %>
                </ul>
            <% } else { %>
                <div class="empty-message">
                    You don't have any friends yet. Start by searching for people to connect with!
                </div>
            <% } %>
        </div>

        <div class="friends-section">
            <h2 class="section-title">Friend Requests</h2>
            <%
                @SuppressWarnings("unchecked")
                List<User> receivedRequests = (List<User>) request.getAttribute("receivedRequests");
                if (receivedRequests != null && !receivedRequests.isEmpty()) {
            %>
                <ul class="user-list">
                    <% for (User requester : receivedRequests) { %>
                        <li class="user-item">
                            <div class="user-info">
                                <a href="profile?username=<%= requester.getUsername() %>" class="username">
                                    <%= requester.getUsername() %>
                                </a>
                            </div>
                            <div class="user-actions">
                                <form method="post" action="friendRequest" style="display: inline;">
                                    <input type="hidden" name="action" value="accept">
                                    <input type="hidden" name="targetUserId" value="<%= requester.getId() %>">
                                    <button type="submit" class="btn btn-accept">Accept</button>
                                </form>
                                <form method="post" action="friendRequest" style="display: inline;">
                                    <input type="hidden" name="action" value="reject">
                                    <input type="hidden" name="targetUserId" value="<%= requester.getId() %>">
                                    <button type="submit" class="btn btn-reject">Reject</button>
                                </form>
                            </div>
                        </li>
                    <% } %>
                </ul>
            <% } else { %>
                <div class="empty-message">
                    No pending friend requests.
                </div>
            <% } %>
        </div>

        <div class="friends-section">
            <h2 class="section-title">Sent Requests</h2>
            <%
                @SuppressWarnings("unchecked")
                List<User> sentRequests = (List<User>) request.getAttribute("sentRequests");
                if (sentRequests != null && !sentRequests.isEmpty()) {
            %>
                <ul class="user-list">
                    <% for (User recipient : sentRequests) { %>
                        <li class="user-item">
                            <div class="user-info">
                                <a href="profile?username=<%= recipient.getUsername() %>" class="username">
                                    <%= recipient.getUsername() %>
                                </a>
                                <div style="font-size: 0.9rem; color: #666; margin-top: 0.25rem;">
                                    Request sent
                                </div>
                            </div>
                        </li>
                    <% } %>
                </ul>
            <% } else { %>
                <div class="empty-message">
                    No pending sent requests.
                </div>
            <% } %>
        </div>
    </div>
</div>

</body>
</html> 