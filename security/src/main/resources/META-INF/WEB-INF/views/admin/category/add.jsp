<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/commons/taglib.jsp"%>

<form action="/admin/categories/save" method="post"
	enctype="multipart/form-data">
	<input type="hidden" value="${category.isEdit}" name="isEdit">
	<input type="hidden" value="${category.id}" name="id"> <label
		for="categoryname">Category name:</label><br> <input type="text"
		id="categoryname" name="name" value="${category.name}"><br>
	<label for="images">Images:</label><br>
    <input type="text" id="images" name="images" value="${category.images}"><br>
	<br> 
	<label for="status">Status</label><br>
	<input type="text" name="status" value="${category.status}"><br>
	<c:if test="${category.isEdit}">
		<input type="submit" value="Update">
	</c:if>
	<c:if test="${!category.isEdit}">
		<input type="submit" value="Insert">
	</c:if>
</form>