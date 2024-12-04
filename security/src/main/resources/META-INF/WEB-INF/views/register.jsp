<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/commons/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký tài khoản</title>
</head>
<body>
    <h1>Đăng ký tài khoản</h1>
    <form action="/api/auth/register" method="POST">
        <label for="username">Tên đăng nhập</label>
        <input type="text" id="username" name="username" required>

        <label for="email">Email</label>
        <input type="email" id="email" name="email" required>

        <label for="password">Mật khẩu</label>
        <input type="password" id="password" name="password" required>

        <button type="submit">Đăng ký</button>
    </form>

    <p><a href="/login">Đã có tài khoản? Đăng nhập ngay!</a></p>
</body>
</html>
