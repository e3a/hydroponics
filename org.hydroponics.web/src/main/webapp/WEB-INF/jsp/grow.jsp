<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<!--[if lt IE 7 ]> <html class="no-js ie6" lang="en"> <![endif]-->
<!--[if IE 7 ]>    <html class="no-js ie7" lang="en"> <![endif]-->
<!--[if IE 8 ]>    <html class="no-js ie8" lang="en"> <![endif]-->
<!--[if (gte IE 9)|!(IE)]><!--><html class="no-js" lang="en"><!--<![endif]-->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css" media="screen" />

    <title><fmt:message key="hydroponics.title" /></title>
</head>
<body>
<section id="page" class="wrapper">
<article id="home">
    <jsp:include page="includes/header.jsp" flush="true" />
    <jsp:include page="includes/menu.jsp" flush="true" />
</header>

<div id="main" role="main">
<p>
 <fmt:message key="grow.name" /><c:out value="${grow.name}" /><br/>
 <fmt:message key="grow.vegetation" /><c:out value="${grow.vegetation}" /><br/>
 <fmt:message key="grow.flower" /><c:out value="${grow.flower}" /><br/>
 <fmt:message key="grow.end" /><c:out value="${grow.end}" /><br/>
 <fmt:message key="vegetation.days" /><c:out value="${grow.vegetationDays}" /><br/>
 <fmt:message key="flower.days" /><c:out value="${grow.flowerDays}" /><br/>
</p>
</div>
<div id="graph_temp" style="width: 100%; height: 400px"></div>
<div id="graph_humidity" style="width: 100%; height: 400px"></div>
</body>
