<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt lại mật khẩu</title>
</head>
<body>
    <h1>Đặt lại mật khẩu</h1>
    <form action="/api/auth/reset-password" method="POST">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" required>

        <label for="otp">Mã OTP</label>
        <input type="text" id="otp" name="otp" required>

        <label for="newPassword">Mật khẩu mới</label>
        <input type="password" id="newPassword" name="newPassword" required>

        <button type="submit">Đặt lại mật khẩu</button>
    </form>

    <p><a href="/login">Quay lại đăng nhập</a></p>
</body>
</html>

