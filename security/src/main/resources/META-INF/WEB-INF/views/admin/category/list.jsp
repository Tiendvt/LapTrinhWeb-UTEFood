<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/commons/taglib.jsp"%>
<a href="/admin/categories/add">Add category</a>
<table>
	<tr>
		<th>STT</th>
		<th>Images</th>
		<th>Categoryname</th>
		<th>Status</th>
		<th>Action</th>
	</tr>
	<c:forEach items="${list}" var = "cate" varStatus="stt">
		<tr>
			<td>${stt.index+1}</td>
			<td>$(cate.images)</td>
			<td>$(cate.name)</td>
			<td>$(status)</td>
			<td>
				<a href="/admin/categories/edit/${cate.id}">Sua</a>
				<a href="/admin/categories/delete/${cate.id}">Xoa</a>
			</td>
		<tr/>
	</c:forEach>	
</table>