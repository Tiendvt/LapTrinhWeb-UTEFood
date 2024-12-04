<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create New Product</title>
</head>
<body>
    <div align="center">
        <h1>Create New Product</h1>
        <br/>
        <form action="${pageContext.request.contextPath}/save" method="post">
            <table border="0" cellpadding="10">
                <tr>
                    <td>Product Name:</td>
                    <td><input type="text" name="name" value="${product.name}" /></td>
                </tr>
                <tr>
                    <td>Brand:</td>
                    <td><input type="text" name="brand" value="${product.brand}" /></td>
                </tr>
                <tr>
                    <td>Made in:</td>
                    <td><input type="text" name="madein" value="${product.madein}" /></td>
                </tr>
                <tr>
                    <td>Price:</td>
                    <td><input type="text" name="price" value="${product.price}" /></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <button type="submit">Save</button>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</body>
</html>
    