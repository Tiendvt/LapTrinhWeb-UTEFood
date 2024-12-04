<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ include file="/commons/taglib.jsp"%>
<form action="${pageContext.request.contextPath }/admin/category/update" method="post" enctype="multipart/form-data">
	<input type="text"id="categoryid" name="categoryid" value="${cate.categoryid}" hidden="hidden"><br>
	<label for="categoryname">Category name:</label><br> 
	<input type="text"id="categoryname" name="categoryname" value="${cate.categoryname}"><br> 
	<label for="image">Images:</label><br>
		<c:if test="${cate.images.substring(0,5)!='https'}">
					<c:url value="/image?fname=${cate.images }" var="imgUrl"></c:url>
				</c:if> <c:if test="${cate.images.substring(0,5)=='https'}">
					<c:url value="${cate.images }" var="imgUrl"></c:url>
				</c:if> <img height="150" width="200" src="${imgUrl}" id="imagess" /> 
	<input type="file" onchange=" chooseFile(this)" id="images"name="images" value="${cate.images}" ><br>
	<label for="categoryname">Status</label><br>
	<input type="radio" id="ston" name="status" value="1" checked>
	<label for="1">Đang hoạt động</label><br>
	<input type="radio" id="stof" name="status" value="0" checked>
	<label for="0">Khóa</label><br>
	<br> <input type="submit" value="update">
</form>