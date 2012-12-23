<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<table width="760px" border="0">
    <tr>
        <th width="360px" align="right"><fmt:message key="grow.vegetation" /></th>
        <th width="20px" align="left"></th>
        <th width="20px" align="right"></th>
        <th width="360px" align="left"><fmt:message key="grow.flower" /></th>
    </tr>
    <tr>
        <td width="360px" align="right"><c:if test="${grow.vegetation != null}">
            <fmt:formatDate value="${grow.vegetation}" type="date"  dateStyle="long"/>
        </c:if></td>
        <td width="40px" colspan="2" align="center"><img src="${pageContext.request.contextPath}/images/calendar.png"/></td>
        <td width="360px" align="left"><c:if test="${grow.flower != null}">
            <fmt:formatDate value="${grow.flower}" type="date"  dateStyle="long"/>
        </c:if></td>
    </tr>
    <tr>
        <td width="360px" align="right"><c:if test="${grow.vegetationDays ne 0}">
        (<c:out value="${grow.vegetationDays}" />&nbsp;<fmt:message key="grow.days" />)
        </c:if></td>
        <td colspan="2" width="40px" text-align="left"></td>
        <td width="360px" align="left"><c:if test="${grow.flowerDays ne 0}">
        (<c:out value="${grow.flowerDays}" />&nbsp;<fmt:message key="grow.days" />)
        </c:if></td>
    </tr>
    <tr>
        <td width="360px" align="right"><span id="temperature"><c:out value="${calibre.temperature}" />&nbsp;°C</span></td>
        <td width="20px" align="left"><img src="${pageContext.request.contextPath}/images/temperature.png"/></td>
        <td width="20px" align="right"><img src="${pageContext.request.contextPath}/images/humidity.png"/></td>
        <td width="360px" align="left"><span id="humidity"><c:out value="${calibre.humidity}" />&nbsp;%</span></td>
    </tr>
    <tr>
        <td width="360px" align="right"><span id="current"><c:out value="${calibre.current}" />&nbsp;A</span></td>
        <td width="20px" align="left"><img src="${pageContext.request.contextPath}/images/electricity.gif"/></td>
        <td width="20px" align="right"><img src="${pageContext.request.contextPath}/images/moisture.png"/></td>
        <td width="360px" align="left"><span id="moisture"><c:out value="${calibre.moisture}"/></span></td>
    </tr>
</table>
