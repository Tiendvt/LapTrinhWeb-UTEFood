<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<form action="${pageContext.request.contextPath }/admin/category/insert" method="post" enctype="multipart/form-data">
	<label for="categoryname">Category name:</label><br> 
	<input type="text"id="categoryname" name="categoryname"><br> 
	<label for="image">Images:</label><br>
	 <img height="150" width="200" id="imagess" src=""/>
	<input type="file" onchange=" chooseFile(this)"id="images"name="images"><br>
	<label for="categoryname">Status</label><br>
	<input type="radio" id="ston" name="status" value="1" checked>
	<label for="1">Đang hoạt động</label><br>
	<input type="radio" id="stof" name="status" value="0" checked>
	<label for="0">Khóa</label><br>
	<br> <input type="submit" value="insert">
</form>
