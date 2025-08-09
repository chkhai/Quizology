<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Clear quiz session data when user navigates to signup
    session.removeAttribute("currentQuiz");
    session.removeAttribute("quizAnswers");
    session.removeAttribute("quizStartTime");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Sign Up - Quizology</title>
    <style>
        :root {
            --primary-gradient: linear-gradient(135deg, #EAE7DC 0%, #D8C3A5 100%);

            --card-bg: rgba(255, 255, 255, 0.9);
            --card-border: rgba(0, 0, 0, 0.05);
            --input-bg: rgba(255, 255, 255, 0.95);

            --accent-gradient: linear-gradient(135deg, #E85A4F 0%, #E9704F 100%);
            --text-primary: #8E8D8A;
        }

        * {
            box-sizing: border-box;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Ubuntu", "Roboto", "Noto Sans", "Droid Sans", "Helvetica Neue", Arial, sans-serif;
        }

        body {
            margin: 0;
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: var(--primary-gradient);
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Ubuntu", "Roboto", "Noto Sans", "Droid Sans", "Helvetica Neue", Arial, sans-serif;
            color: var(--text-primary);
        }

        .auth-card {
            width: 380px;
            padding: 2.75rem 2.25rem;
            border-radius: 18px;
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
            backdrop-filter: blur(12px);
        }

        .auth-card h2 {
            text-align: center;
            margin: 0 0 1.8rem;
            font-weight: 600;
            letter-spacing: 0.5px;
        }

        .form-group {
            margin-bottom: 1.4rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.4rem;
            font-size: 0.95rem;
            font-weight: 500;
            color: var(--text-primary);
        }

        .form-group input {
            width: 100%;
            padding: 0.75rem 0.9rem;
            border: 1px solid var(--card-border);
            border-radius: 10px;
            font-size: 1rem;
            background: var(--input-bg);
            color: var(--text-primary);
            outline: transparent;
            transition: background 0.25s ease, box-shadow 0.25s ease;
        }

        .form-group input::placeholder {
            color: var(--text-primary);
        }

        .form-group input:focus {
            background: #ffffff;
            box-shadow: 0 0 0 2px rgba(232, 90, 79, 0.3);
        }

        .error-message {
            color: #E85A4F;
            font-size: 0.9rem;
            text-align: center;
            margin-bottom: 1.2rem;
        }

        .action-btn {
            width: 100%;
            padding: 0.85rem;
            font-size: 1.05rem;
            font-weight: 600;
            border: none;
            border-radius: 50px;
            cursor: pointer;
            background-image: var(--accent-gradient);
            color: #ffffff;
            transition: transform 0.25s ease, box-shadow 0.25s ease;
        }

        .action-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 10px rgba(232, 90, 79, 0.3);
        }

        .link {
            display: block;
            text-align: center;
            margin-top: 1.2rem;
            font-size: 0.9rem;
            color: var(--text-primary);
            text-decoration: none;
            transition: color 0.2s ease;
        }

        .link:hover {
            color: #E85A4F;
        }
    </style>
</head>
<body>
<div class="auth-card">
    <h2>Create Account</h2>
    <form action="register" method="post" autocomplete="off">
        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" placeholder="Choose a username" required>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" placeholder="Create a password" required>
        </div>
        <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirm your password" required>
        </div>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error-message"><%= request.getAttribute("errorMessage") %></div>
        <% } %>
        <button type="submit" class="action-btn">Sign Up</button>
    </form>
    <a class="link" href="login.jsp">Already have an account? Log in</a>
</div>
</body>
</html> 