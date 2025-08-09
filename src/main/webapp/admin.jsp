<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.freeuni.quizapp.model.User" %>
<%@ page import="com.freeuni.quizapp.model.Quiz" %>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null || !currentUser.isAdmin()) {
        response.sendRedirect("login.jsp");
        return;
    }

    List<User> allUsers = (List<User>) request.getAttribute("allUsers");
    List<Quiz> allQuizzes = (List<Quiz>) request.getAttribute("allQuizzes");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Panel - Quizology</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Ubuntu", "Roboto", "Noto Sans", "Droid Sans", "Helvetica Neue", Arial, sans-serif;
            background: linear-gradient(135deg, #EAE7DC 0%, #D8C3A5 100%);
            min-height: 100vh;
            color: #333;
        }

        .navbar {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            padding: 1rem 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
            position: sticky;
            top: 0;
            z-index: 100;
        }

        .brand {
            font-size: 1.5rem;
            font-weight: 700;
            color: #E85A4F;
            text-decoration: none;
        }

        .nav-links {
            display: flex;
            list-style: none;
            gap: 2rem;
            align-items: center;
        }

        .nav-links a {
            text-decoration: none;
            color: #555;
            font-weight: 500;
            transition: color 0.3s ease;
        }

        .nav-links a:hover {
            color: #E85A4F;
        }

        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 2rem;
        }

        .admin-header {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            text-align: center;
        }

        .admin-header h1 {
            color: #333;
            font-size: 2.5rem;
            margin-bottom: 0.5rem;
        }

        .admin-header p {
            color: #666;
            font-size: 1.1rem;
        }

        .admin-section {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        }

        .section-title {
            font-size: 1.5rem;
            color: #333;
            margin-bottom: 1.5rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid #E85A4F;
        }

        .alert {
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
            font-weight: 500;
        }

        .alert-success {
            background: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
        }

        .alert-error {
            background: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
        }

        .admin-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }

        .admin-table th,
        .admin-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        .admin-table th {
            background: #f8f9fa;
            font-weight: 600;
            color: #333;
        }

        .admin-table tr:hover {
            background: #f8f9fa;
        }

        .btn {
            padding: 0.5rem 1rem;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 500;
            text-decoration: none;
            display: inline-block;
            margin: 0.25rem;
            transition: all 0.3s ease;
        }

        .btn-danger {
            background: #dc3545;
            color: white;
        }

        .btn-danger:hover {
            background: #c82333;
        }

        .btn-warning {
            background: #ffc107;
            color: #212529;
        }

        .btn-warning:hover {
            background: #e0a800;
        }

        .status-badge {
            padding: 0.25rem 0.5rem;
            border-radius: 12px;
            font-size: 0.8rem;
            font-weight: 500;
        }

        .status-admin {
            background: #28a745;
            color: white;
        }

        .status-user {
            background: #6c757d;
            color: white;
        }

        .empty-state {
            text-align: center;
            padding: 3rem;
            color: #666;
        }

        @media (max-width: 768px) {
            .navbar {
                padding: 1rem;
            }

            .container {
                padding: 0 1rem;
            }

            .admin-table {
                font-size: 0.9rem;
            }

            .admin-table th,
            .admin-table td {
                padding: 0.5rem;
            }

            .btn {
                padding: 0.4rem 0.8rem;
                font-size: 0.85rem;
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
        <li><a href="profile">Profile</a></li>
        <li><a href="logout">Sign Out</a></li>
    </ul>
</nav>

<div class="container">
    <div class="admin-header">
        <h1>🛠️ Admin Panel</h1>
        <p>Manage users, quizzes, and system data</p>
    </div>

    <% if (successMessage != null) { %>
        <div class="alert alert-success">
            ✅ <%= successMessage %>
        </div>
    <% } %>

    <% if (errorMessage != null) { %>
        <div class="alert alert-error">
            ❌ <%= errorMessage %>
        </div>
    <% } %>

    <!-- User Management Section -->
    <div class="admin-section">
        <h2 class="section-title">👥 User Management</h2>
        
        <% if (allUsers != null && !allUsers.isEmpty()) { %>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Username</th>
                        <th>Status</th>
                        <th>Created At</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (User user : allUsers) { %>
                        <tr>
                            <td><%= user.getId() %></td>
                            <td><%= user.getUsername() %></td>
                            <td>
                                <span class="status-badge <%= user.isAdmin() ? "status-admin" : "status-user" %>">
                                    <%= user.isAdmin() ? "Admin" : "User" %>
                                </span>
                            </td>
                            <td><%= user.getCreatedAt() != null ? user.getCreatedAt().toString() : "N/A" %></td>
                            <td>
                                <% if (!user.isAdmin()) { %>
                                    <form style="display: inline;" method="post" action="adminAction" 
                                          onsubmit="return confirm('Are you sure you want to promote this user to admin?')">
                                        <input type="hidden" name="action" value="promoteUser">
                                        <input type="hidden" name="userId" value="<%= user.getId() %>">
                                        <button type="submit" class="btn btn-warning">Promote to Admin</button>
                                    </form>
                                <% } %>
                                
                                <% if (user.getId() != currentUser.getId()) { %>
                                    <form style="display: inline;" method="post" action="adminAction" 
                                          onsubmit="return confirm('Are you sure you want to delete this user? This action cannot be undone.')">
                                        <input type="hidden" name="action" value="deleteUser">
                                        <input type="hidden" name="userId" value="<%= user.getId() %>">
                                        <button type="submit" class="btn btn-danger">Delete User</button>
                                    </form>
                                <% } else { %>
                                    <span style="color: #666; font-style: italic;">Cannot delete self</span>
                                <% } %>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } else { %>
            <div class="empty-state">
                <div>👤</div>
                <p>No users found in the system.</p>
            </div>
        <% } %>
    </div>

    <!-- Quiz Management Section -->
    <div class="admin-section">
        <h2 class="section-title">📚 Quiz Management</h2>
        
        <% if (allQuizzes != null && !allQuizzes.isEmpty()) { %>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Quiz ID</th>
                        <th>Title</th>
                        <th>Description</th>
                        <th>Creator ID</th>
                        <th>Created At</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Quiz quiz : allQuizzes) { %>
                        <tr>
                            <td><%= quiz.getId() %></td>
                            <td><%= quiz.getTitle() %></td>
                            <td><%= quiz.getDescription() != null ? 
                                   (quiz.getDescription().length() > 50 ? 
                                    quiz.getDescription().substring(0, 50) + "..." : 
                                    quiz.getDescription()) : "No description" %>
                            </td>
                            <td><%= quiz.getCreatorId() %></td>
                            <td><%= quiz.getCreatedAt() != null ? quiz.getCreatedAt().toString() : "N/A" %></td>
                            <td>
                                <form style="display: inline;" method="post" action="adminAction" 
                                      onsubmit="return confirm('Are you sure you want to clear all history for this quiz?')">
                                    <input type="hidden" name="action" value="clearQuizHistory">
                                    <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
                                    <button type="submit" class="btn btn-warning">Clear History</button>
                                </form>
                                
                                <form style="display: inline;" method="post" action="adminAction" 
                                      onsubmit="return confirm('Are you sure you want to delete this quiz? This action cannot be undone.')">
                                    <input type="hidden" name="action" value="deleteQuiz">
                                    <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
                                    <button type="submit" class="btn btn-danger">Delete Quiz</button>
                                </form>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } else { %>
            <div class="empty-state">
                <div>📝</div>
                <p>No quizzes found in the system.</p>
            </div>
        <% } %>
    </div>
</div>

</body>
</html> 