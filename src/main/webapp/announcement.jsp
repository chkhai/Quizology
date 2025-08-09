<%--
  Created by IntelliJ IDEA.
  User: lbegi
  Date: 11.07.2025
  Time: 01:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.freeuni.quizapp.model.Announcement" %>
<html>
<head>
    <title>Announcement</title>
    <link rel="stylesheet" type="text/css" href="css/announcement.css">
</head>
<body>
    <%
        Announcement announcement = (Announcement) request.getAttribute("announcement");
        if (announcement == null) {
    %>
    <h2>Announcement not found</h2>
    <%
    } else {
    %>
    <div class="announcement-detail">
        <h2><%= announcement.getTitle() %>
        </h2>
        <p><%= announcement.getText() %>
        </p>
        <small>Posted on: <%= announcement.getCreatedAt() %>
        </small>
    </div>
    <%
        }
    %>

</body>
</html>
