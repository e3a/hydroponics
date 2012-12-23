<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<header>
    <a href="main.htm"><img src="${pageContext.request.contextPath}/images/plant.jpg" id="logo"/></a>
    <p id="header_text">
        <span id="header_title" class="header_title"><fmt:message key="hydroponics.title" /></span><br/>
    <c:if test="${fn:endsWith(pageContext.request.requestURI, 'main.jsp')}">
        <span id="header_name" class="header_title"><c:out value="${grow.name}" /></span>
    </c:if>
    <c:if test="${fn:endsWith(pageContext.request.requestURI, 'list.jsp')}">
        <span id="header_name" class="header_title"><fmt:message key="hydroponics.list.title" /></span>
    </c:if>
    <c:if test="${fn:endsWith(pageContext.request.requestURI, 'switches.jsp')}">
        <span id="header_name" class="header_title"><fmt:message key="hydroponics.switches.title" /></span>
    </c:if>
    <c:if test="${fn:endsWith(pageContext.request.requestURI, 'addFertilizer.jsp')}">
        <span id="header_name" class="header_title"><fmt:message key="hydroponics.fertilizer.title" /></span>
    </c:if>
    <c:if test="${fn:endsWith(pageContext.request.requestURI, 'addImage.jsp')}">
         <span id="header_name" class="header_title"><fmt:message key="hydroponics.image.title" /></span>
    </c:if>
    <c:if test="${fn:endsWith(pageContext.request.requestURI, 'controller.jsp')}">
        <span id="header_name" class="header_title"><fmt:message key="hydroponics.controller.title" /></span>
    </c:if>
    </p>
</header>


