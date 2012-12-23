<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
        <form:form name="schedulesEdit" modelAttribute="schedulesEditBean" method="post">

        <label for="mode"><fmt:message key="switch.edit.mode"/> <span class="required">*</span></label>
        <form:radiobutton path="mode" value="0" label="Off" />
        <form:radiobutton path="mode" value="1" label="On" />
        <form:radiobutton path="mode" value="2" label="Auto" />
        <br/>

        <label for="status"><fmt:message key="switch.edit.status"/> <span class="required">*</span></label>
        <c:choose>
          <c:when test="${schedulesEditBean.status eq 0}">
            <img src="${pageContext.request.contextPath}/images/redled.png">
          </c:when>
          <c:otherwise>
            <img src="${pageContext.request.contextPath}/images/greenled.png">
          </c:otherwise>
        </c:choose>
        <br/>

        <label for="name"><fmt:message key="switch.edit.name"/></label>
        <form:input id="name" size="40" path="name"/>
        <form:errors path="name" cssClass="errors"/>
        <br/>

        <spring:bind path="schedules">
            <label for="schedules"><fmt:message key="switch.edit.start1"/> <span class="required">*</span></label>
            <c:forEach var="schedule" items="${schedulesEditBean.schedules}" varStatus="rowCounter">

                <input name="schedules" type="text" value="${schedule}" size="2" maxlength="2"/>

                  <c:choose>
                    <c:when test="${rowCounter.count % 6 == 0}">
                        <br/>
                    </c:when>
                 </c:choose>
            </c:forEach>
        </spring:bind>

        <button id="cancel" type="submit"  name="formAction" value="cancel">Cancel</button>
        <button id="submit" type="submit"  name="formAction" value="submit">Submit</button>
        </form:form>
        </binder>
      </div>
</body>
</html>
