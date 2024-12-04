<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/commons/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
    <div>
        <form action="${pageContext.request.contextPath}/login" method="post" style="max-width: 400px; margin: 0 auto;">
            <p>
                E-mail: <input type="text" name="username" required />
            </p>
            <p>
                Password: <input type="password" name="password" required />
            </p>
            <p>
                <input type="submit" value="Login" />
            </p>
        </form>
    </div>
</body>
</html>


