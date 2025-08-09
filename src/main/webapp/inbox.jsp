<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quizapp.model.User" %>
<%@ page import="com.freeuni.quizapp.model.Message" %>
<%@ page import="com.freeuni.quizapp.enums.MessageType" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
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
    <title>Inbox - Quizology</title>
    <style>
        :root {
            --primary-gradient: linear-gradient(135deg, #EAE7DC 0%, #D8C3A5 100%);
            --accent-gradient: linear-gradient(135deg, #E85A4F 0%, #E9704F 100%);
            --gradient-accent: linear-gradient(135deg, #E85A4F 0%, #E9704F 100%);
            --card-bg: rgba(255, 255, 255, 0.9);
            --card-border: rgba(0, 0, 0, 0.05);
            --text-primary: #8E8D8A;
            --text-secondary: #8E8D8A;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Ubuntu", "Roboto", "Noto Sans", "Droid Sans", "Helvetica Neue", Arial, sans-serif;
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

        .inbox-header {
            text-align: center;
            padding: 4.5% 4.5% 2%;
            background: #ffffff;
        }
        
        .inbox-header h1 {
            font-size: 2.8rem;
            font-weight: 800;
            background: var(--gradient-accent);
            background-clip: text;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            animation: fadeInUp 0.8s ease-out 0.3s both;
        }
        
        .inbox-header p {
            margin-top: 0.8rem;
            font-size: 1.05rem;
            animation: fadeIn 0.8s ease-out 0.6s both;
        }

        .inbox-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 2rem;
            height: calc(100vh - 150px);
        }

        .inbox-layout {
            display: grid;
            grid-template-columns: 1fr 2fr;
            gap: 2rem;
            height: 100%;
            background: white;
            border-radius: 16px;
            box-shadow: 0 8px 24px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .conversations-panel {
            border-right: 1px solid #e0e0e0;
            padding: 1.5rem;
            overflow-y: auto;
        }

        .conversations-header {
            font-size: 1.2rem;
            font-weight: 600;
            color: #E85A4F;
            margin-bottom: 1rem;
            border-bottom: 2px solid #E85A4F;
            padding-bottom: 0.5rem;
        }

        .conversation-item {
            display: flex;
            align-items: center;
            padding: 1rem;
            margin-bottom: 0.5rem;
            border-radius: 8px;
            cursor: pointer;
            transition: background-color 0.2s ease;
            text-decoration: none;
            color: inherit;
        }

        .conversation-item:hover {
            background: #f8f9fa;
        }

        .conversation-item.active {
            background: #E85A4F;
            color: white;
        }

        .conversation-info {
            flex-grow: 1;
        }

        .conversation-name {
            font-weight: 600;
            margin-bottom: 0.25rem;
        }

        .conversation-preview {
            font-size: 0.85rem;
            color: #666;
            opacity: 0.8;
        }

        .conversation-item.active .conversation-preview {
            color: rgba(255, 255, 255, 0.8);
        }

        .chat-panel {
            display: flex;
            flex-direction: column;
            height: 100%;
        }

        .chat-header {
            padding: 1.5rem;
            border-bottom: 1px solid #e0e0e0;
            background: #f8f9fa;
        }

        .chat-header h3 {
            color: #E85A4F;
            margin: 0;
        }

        .messages-container {
            flex-grow: 1;
            padding: 1.5rem;
            overflow-y: auto;
            background: #fafafa;
            height: calc(100vh - 300px);
            scroll-behavior: smooth;
        }

        .message {
            margin-bottom: 1rem;
            display: flex;
            flex-direction: column;
        }

        .message.sent {
            align-items: flex-end;
        }

        .message.received {
            align-items: flex-start;
        }

        .message-bubble {
            max-width: 70%;
            padding: 0.75rem 1rem;
            border-radius: 18px;
            word-wrap: break-word;
        }

        .message.sent .message-bubble {
            background: var(--accent-gradient);
            color: white;
            border-bottom-right-radius: 4px;
        }

        .message.received .message-bubble {
            background: white;
            color: #333;
            border: 1px solid #e0e0e0;
            border-bottom-left-radius: 4px;
        }

        .message-time {
            font-size: 0.75rem;
            color: #666;
            margin-top: 0.25rem;
            opacity: 0.7;
        }

        .challenge-message {
            border: 2px solid #E85A4F;
            border-radius: 12px;
            background: linear-gradient(135deg, #fff5f5, #fef2f2);
            padding: 1.5rem;
            margin: 1rem 0;
        }

        .challenge-header {
            text-align: center;
            margin-bottom: 1rem;
        }

        .challenge-title {
            font-weight: 600;
            color: #E85A4F;
        }

        .quiz-info {
            background: white;
            padding: 1rem;
            border-radius: 8px;
            margin: 1rem 0;
            border-left: 4px solid #E85A4F;
            text-align: center;
        }

        .quiz-name {
            font-weight: 600;
            color: #333;
            margin-bottom: 0.5rem;
        }

        .challenge-actions {
            display: flex;
            gap: 0.75rem;
            margin-top: 1rem;
            justify-content: center;
        }

        .btn {
            padding: 0.6rem 1.2rem;
            border: none;
            border-radius: 6px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s ease;
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }

        .btn-accept {
            background: linear-gradient(135deg, #28a745, #20c997);
            color: white;
        }

        .btn-accept:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 8px rgba(40, 167, 69, 0.3);
        }

        .btn-decline {
            background: linear-gradient(135deg, #dc3545, #c82333);
            color: white;
        }

        .btn-decline:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
        }

        .empty-state {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100%;
            color: #666;
            text-align: center;
        }

        .empty-state h3 {
            margin-bottom: 0.5rem;
            color: #E85A4F;
        }

        .success-message {
            background: linear-gradient(135deg, #28a745, #20c997);
            color: white;
            padding: 1rem 1.5rem;
            border-radius: 8px;
            margin-bottom: 1rem;
            text-align: center;
            font-weight: 500;
        }

        .message-input-area {
            padding: 1.5rem;
            border-top: 1px solid #e0e0e0;
            background: white;
            margin-top: auto;
        }

        .message-form {
            width: 100%;
        }

        .message-input-container {
            display: flex;
            gap: 0.75rem;
            align-items: center;
        }

        .message-input {
            flex-grow: 1;
            padding: 0.75rem 1rem;
            border: 2px solid #e0e0e0;
            border-radius: 25px;
            font-size: 0.95rem;
            outline: none;
            transition: border-color 0.2s ease;
        }

        .message-input:focus {
            border-color: #E85A4F;
        }

        .send-button {
            padding: 0.75rem 1.5rem;
            background: var(--accent-gradient);
            color: white;
            border: none;
            border-radius: 25px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s ease;
            min-width: 80px;
        }

        .send-button:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 8px rgba(232, 90, 79, 0.3);
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
            .inbox-layout {
                grid-template-columns: 1fr;
                height: auto;
            }
            
            .conversations-panel {
                border-right: none;
                border-bottom: 1px solid #e0e0e0;
                max-height: 300px;
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

<section class="inbox-header">
    <h1>Inbox</h1>
    <p>Chat with friends and accept quiz challenges!</p>
</section>

<div class="inbox-container">
    <% String challengeSent = request.getParameter("challengeSent");
       String challengeDeclined = request.getParameter("challengeDeclined");
       if ("true".equals(challengeSent)) { %>
        <div class="success-message">
            Quiz challenge sent successfully!
        </div>
    <% } else if ("true".equals(challengeDeclined)) { %>
        <div class="success-message">
            Challenge declined.
        </div>
    <% } %>

    <div class="inbox-layout">
        <div class="conversations-panel">
            <div class="conversations-header">Conversations</div>
            <%
                @SuppressWarnings("unchecked")
                List<User> friendConversations = (List<User>) request.getAttribute("friendConversations");
                @SuppressWarnings("unchecked")
                Map<Integer, Message> latestMessages = (Map<Integer, Message>) request.getAttribute("latestMessages");
                User conversationPartner = (User) request.getAttribute("conversationPartner");
                
                if (friendConversations != null && !friendConversations.isEmpty()) {
                    for (User friend : friendConversations) {
                        Message latestMsg = latestMessages.get(friend.getId());
                        String preview = "";
                        if (latestMsg != null) {
                            if (latestMsg.getType() == MessageType.challenge) {
                                preview = "Quiz Challenge";
                            } else {
                                preview = latestMsg.getContent();
                                if (preview.length() > 50) {
                                    preview = preview.substring(0, 50) + "...";
                                }
                            }
                        } else {
                            preview = "Start a conversation...";
                        }
                        
                        boolean isActive = conversationPartner != null && conversationPartner.getId() == friend.getId();
            %>
                <a href="inbox?with=<%= friend.getId() %>" class="conversation-item <%= isActive ? "active" : "" %>">
                    <div class="conversation-info">
                        <div class="conversation-name"><%= friend.getUsername() %></div>
                        <div class="conversation-preview"><%= preview %></div>
                    </div>
                </a>
            <%
                    }
                } else {
            %>
                <div class="empty-state" style="height: auto; padding: 2rem;">
                    <h3>No Friends</h3>
                    <p>Add some friends to start chatting and send quiz challenges!</p>
                </div>
            <% } %>
        </div>

        <div class="chat-panel">
            <%
                @SuppressWarnings("unchecked")
                List<Message> conversationMessages = (List<Message>) request.getAttribute("conversationMessages");
                
                if (conversationPartner != null && conversationMessages != null) {
            %>
                <div class="chat-header">
                    <h3>Chat with <%= conversationPartner.getUsername() %></h3>
                </div>
                
                <div class="messages-container">
                    <%
                        for (Message message : conversationMessages) {
                            boolean isSent = message.getSenderId() == currentUser.getId();
                            
                            if (message.getType() == MessageType.challenge) {
                                // Parse challenge content
                                String content = message.getContent();
                                String quizTitle = "Quiz Challenge";
                                int quizId = 0;
                                
                                if (content.startsWith("QUIZ_CHALLENGE:")) {
                                    String[] parts = content.split(":", 3);
                                    if (parts.length >= 3) {
                                        try {
                                            quizId = Integer.parseInt(parts[1]);
                                            quizTitle = parts[2];
                                        } catch (NumberFormatException e) {
                                        }
                                    }
                                }
                    %>
                        <div class="challenge-message">
                            <div class="challenge-header">
                                <span class="challenge-title">
                                    <%= isSent ? "You sent a quiz challenge" : conversationPartner.getUsername() + " challenged you" %>
                                </span>
                            </div>
                            <div class="quiz-info">
                                <div class="quiz-name"><%= quizTitle %></div>
                            </div>
                            <% if (!isSent) { %>
                                <div class="challenge-actions">
                                    <form method="post" action="challenge" style="display: inline;">
                                        <input type="hidden" name="action" value="accept">
                                        <input type="hidden" name="messageId" value="<%= message.getId() %>">
                                        <button type="submit" class="btn btn-accept">Accept Challenge</button>
                                    </form>
                                    <form method="post" action="challenge" style="display: inline;">
                                        <input type="hidden" name="action" value="decline">
                                        <input type="hidden" name="messageId" value="<%= message.getId() %>">
                                        <button type="submit" class="btn btn-decline">Decline</button>
                                    </form>
                                </div>
                            <% } %>
                            <div class="message-time">
                                <%= message.getSentAt().toString().substring(0, 16) %>
                            </div>
                        </div>
                    <%
                            } else {
                    %>
                        <div class="message <%= isSent ? "sent" : "received" %>">
                            <div class="message-bubble">
                                <%= message.getContent() %>
                            </div>
                            <div class="message-time">
                                <%= message.getSentAt().toString().substring(0, 16) %>
                            </div>
                        </div>
                    <%
                            }
                        }
                    %>
                </div>
                
                <div class="message-input-area">
                    <form method="post" action="sendMessage" class="message-form">
                        <input type="hidden" name="friendId" value="<%= conversationPartner.getId() %>">
                        <div class="message-input-container">
                            <input type="text" name="messageText" class="message-input" placeholder="Type a message..." required maxlength="500">
                            <button type="submit" class="send-button">Send</button>
                        </div>
                    </form>
                </div>
            <%
                } else {
            %>
                <div class="empty-state">
                    <h3>Select a Conversation</h3>
                    <p>Choose a friend from the left to start chatting or view challenges.</p>
                </div>
            <% } %>
        </div>
    </div>
</div>

<script>
    function scrollToBottom() {
        const messagesContainer = document.querySelector('.messages-container');
        if (messagesContainer) {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        }
    }
    
    function saveScrollPosition() {
        const messagesContainer = document.querySelector('.messages-container');
        if (messagesContainer) {
            sessionStorage.setItem('chatScrollPosition', messagesContainer.scrollTop);
            const isAtBottom = Math.abs(messagesContainer.scrollHeight - messagesContainer.scrollTop - messagesContainer.clientHeight) < 10;
            sessionStorage.setItem('chatWasAtBottom', isAtBottom ? 'true' : 'false');
        }
    }
    
    function restoreScrollPosition() {
        const messagesContainer = document.querySelector('.messages-container');
        const savedPosition = sessionStorage.getItem('chatScrollPosition');
        if (messagesContainer && savedPosition !== null) {
            messagesContainer.scrollTop = parseInt(savedPosition, 10);
            sessionStorage.removeItem('chatScrollPosition');
            return true;
        }
        return false; 
    }
    
    function wasUserAtBottom() {
        return sessionStorage.getItem('chatWasAtBottom') === 'true';
    }
    
    function isMessageSendRedirect() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.has('sent') && urlParams.get('sent') === 'true';
    }
    
    document.addEventListener('DOMContentLoaded', function() {
        const messagesContainer = document.querySelector('.messages-container');
        const isSendRedirect = isMessageSendRedirect();
        
        if (messagesContainer && messagesContainer.children.length > 0) {
            if (isSendRedirect) {
                scrollToBottom();
                const url = new URL(window.location);
                url.searchParams.delete('sent');
                window.history.replaceState({}, '', url);
            } else {
                const restored = restoreScrollPosition();
                if (!restored || wasUserAtBottom()) {
                    setTimeout(scrollToBottom, 50);
                }
            }
        }
    });
    
    document.addEventListener('DOMContentLoaded', function() {
        const messageInput = document.querySelector('.message-input');
        if (messageInput) {
            messageInput.focus();
        }
    });
    
    document.addEventListener('DOMContentLoaded', function() {
        const messageForm = document.querySelector('.message-form');
        if (messageForm) {
            messageForm.addEventListener('submit', function(e) {
                saveScrollPosition();
                setTimeout(() => {
                    const messageInput = document.querySelector('.message-input');
                    if (messageInput) {
                        messageInput.value = '';
                    }
                }, 50);
            });
        }
    });
    window.addEventListener('beforeunload', saveScrollPosition);
</script>
    
</body>
</html> 