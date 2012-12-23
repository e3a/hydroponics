<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div id="navigation">
    <c:if test="${fn:endsWith(pageContext.request.requestURI, 'main.jsp') == false}">
        <a href="main.htm">Home</a>
    </c:if>
    <c:if test="${fn:endsWith(pageContext.request.requestURI, 'list.jsp') == false}">
        <a href="list.htm">List</a>
    </c:if>
    <c:if test="${fn:endsWith(pageContext.request.requestURI, 'edit.jsp') == false}">
        <a href="edit.htm">New</a>
    </c:if>
    <c:if test="${fn:endsWith(pageContext.request.requestURI, 'grow.jsp') == true}">
        <a href="edit.htm?growId=${grow.id}">Edit</a>
    </c:if>
    <c:if test="${fn:endsWith(pageContext.request.requestURI, 'controller.jsp') == false}">
        <a href="controller.htm">Controller</a>
    </c:if>
    <c:if test="${fn:endsWith(pageContext.request.requestURI, 'main.jsp') == true}">
        <a href="addImage.htm">Image</a>
        <a href="addFertilizer.htm">Fertilizer</a>
    </c:if>
</div>
