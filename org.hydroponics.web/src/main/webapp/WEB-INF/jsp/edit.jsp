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
       <form:form id="growEdit" name="growEdit" modelAttribute="growEditBean" method="post">

        <label for="name"><fmt:message key="edit.name"/> <span class="required">*</span></label><br/>
        <form:input id="name" size="40" path="name" required="required" maxlength="40"/>
        <form:errors path="name" cssClass="errors"/><br/>

        <label for="vegetation"><fmt:message key="edit.vegetation"/> <span class="required">*</span></label><br/>
        <form:input id="vegetation" size="20" path="vegetation" type="date" />
        <form:errors path="vegetation" cssClass="errors"/>
        <br/>

        <label for="flower"><fmt:message key="edit.flower"/> <span class="required">*</span></label><br/>
        <form:input id="flower" size="20" path="flower" type="date" />
        <form:errors path="flower" cssClass="errors"/>
        <br/>

        <label for="end"><fmt:message key="edit.end"/> <span class="required">*</span></label><br/>
        <form:input id="end" size="20" path="end" type="date" />
        <form:errors path="end" cssClass="errors"/>
        <br/>

        <label for="plants"><fmt:message key="edit.plants"/> <span class="required">*</span></label><br/>
        <form:input id="plants" size="4" path="plants"    />
        <form:errors path="plants" cssClass="errors"/>
        <br/>

        <label for="result"><fmt:message key="edit.result"/> <span class="required">*</span></label><br/>
        <form:input id="result" size="4" path="result"    />
        <form:errors path="result" cssClass="errors"/>
        <br/><br/>

        <form:hidden path="id" />
    <button id="cancel" type="submit"  name="formAction" value="cancel"><fmt:message key="edit.cancel"/></button>
    <button id="submit" type="submit"  name="formAction" value="submit"><fmt:message key="edit.submit"/></button>
            <c:if test="${growEditBean.id ne 0}">
                <button id="delete" type="submit" name="formAction" value="delete"><fmt:message key="edit.delete"/></button>
            </c:if>
        </form:form>
    </div>
    <script>
        $(function() {
        $("#growEdit").validator();
        $(":date").dateinput({
                format: 'mm/dd/yyyy',
                selectors: true,
                min: -100,
                max: 100,
                speed: 'fast'
            });
        });
    </script>
</section>
</body>
</html>
