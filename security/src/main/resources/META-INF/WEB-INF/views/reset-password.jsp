<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Password Reset Request</title>
</head>
<body>
    <h2>Reset Your Password</h2>
    <c:if test="${not empty alert}">
        <h3 class="alert alert-danger">${alert}</h3>
    </c:if>
    <form action="${pageContext.request.contextPath}/reset-password-request" method="POST">
        <label for="username">Username:</label>
        <input type="text" name="username" id="username" required>
        <button type="submit">Reset Password</button>
    </form>
</body>
</html>
