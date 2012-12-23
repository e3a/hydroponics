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
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/style.css" media="screen" />
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/mootools-core-1.4.5-full-nocompat.js"></script>
<script src="${pageContext.request.contextPath}/js/highcharts/adapters/mootools-adapter.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/highcharts/highcharts.js" type="text/javascript"></script>

<title><fmt:message key="hydroponics.list.title" /></title>
</head>
<body>
<section id="page" class="wrapper">
    <jsp:include page="includes/header.jsp" flush="true" />
    <jsp:include page="includes/menu.jsp" flush="true" />

<div id="main" role="main">
<div class="tableFilter">
    <form id="tableFilter" onsubmit="listTable.filter(this.id); return false;">Filter:
        <select id="column">
            <option value="1">Firstname</option>
            <option value="2">Lastname</option>
            <option value="3">Department</option>
            <option value="4">Start Date</option>
        </select>
        <input type="text" id="keyword" />
        <input type="submit" value="Submit" />
        <input type="reset" value="Clear" />
    </form>
</div>

<table id="listTable">
    <thead><th>Name</th><th>start</th><th>Flower</th><th>End</th></thead>
	<tbody>
	<c:forEach items="${growList}" var="grow">
	    <tr><td><a href="grow.htm?id=${grow.id}"><c:out value="${grow.name}" /></a></td>
	    <td><c:out value="${grow.vegetation}" /></td>
	    <td><c:out value="${grow.flower}" /></td>
	    <td><c:out value="${grow.end}" /></td></tr>
    </c:forEach>
    </tbody>
</table>
</div>
</section>
<script type="text/javascript">
    var myTable = {};
    window.addEvent('domready', function(){
        myTable = new sortableTable('listTable', {overCls: 'over', onClick: function(){alert(this.id)}});
    });
</script>
</body>
