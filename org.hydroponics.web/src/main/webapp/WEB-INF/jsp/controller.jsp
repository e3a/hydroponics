<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<jsp:include page="includes/head.jsp" flush="true" />
<body>
<section>
    <jsp:include page="includes/header.jsp" flush="true" />
    <jsp:include page="includes/menu.jsp" flush="true" />

<div id="main" role="main">
	<form:form name="controllerEdit" modelAttribute="controllerEditBean" method="post">

	<label for="timeserver"><fmt:message key="edit.timeserver"/> <span class="required">*</span></label> <br/>
	<form:input id="timeserver" size="20" path="timeserver"    />
    <form:errors path="timeserver" cssClass="errors"/><br/>

	<label for="timezone"><fmt:message key="edit.timezone"/> <span class="required">*</span></label> <br/>
	<form:input id="timezone" size="2" path="timezone"    />
    <form:errors path="timezone" cssClass="errors"/><br/><br/>

    <button id="cancel" type="submit"  name="formAction" value="cancel"><fmt:message key="edit.cancel"/></button>
    <button id="submit" type="submit"  name="formAction" value="submit"><fmt:message key="edit.submit"/></button>
	</form:form>
  </div>
</body>
</html>
