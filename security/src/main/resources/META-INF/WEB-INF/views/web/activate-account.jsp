<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kích hoạt tài khoản</title>
</head>
<body>
    <h1>Kích hoạt tài khoản</h1>
    <form action="/api/auth/activate" method="POST">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" required>

        <label for="otp">Mã OTP</label>
        <input type="text" id="otp" name="otp" required>

        <button type="submit">Kích hoạt tài khoản</button>
    </form>

    <p><a href="/login">Quay lại đăng nhập</a></p>
</body>
</html>
    