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
	<form:form id="addFertilizer" name="addFertilizer" modelAttribute="fertilizerEditBean" method="post">

	<label for="timestamp"><fmt:message key="edit.timestamp"/> <span class="required">*</span></label><br/>
	<form:input id="timestamp" size="20" path="timestamp" type="date" />
    <form:errors path="timestamp" cssClass="errors"/><br/>

	<label for="fertilizer"><fmt:message key="edit.fertilizer"/> <span class="required">*</span></label><br/>
	<form:input id="fertilizer" size="20" path="fertilizer" maxlength="4" min="1" max="9999"/>
    <form:errors path="fertilizer" cssClass="errors"/><br/><br/>

	<form:hidden path="id" />
	<form:hidden path="grow" />
    <button id="cancel" type="submit"  name="formAction" value="cancel"><fmt:message key="edit.cancel"/></button>
    <button id="submit" type="submit"  name="formAction" value="submit"><fmt:message key="edit.submit"/></button>
	</form:form>
</div>
<script>
  $(function() {
  $("#addFertilizer").validator();
  $(":date").dateinput({
          format: 'mm/dd/yyyy',
          selectors: true,
          min: -100,
          max: 100,
          speed: 'fast'
      });
  });
</script>
</body>
</html>
