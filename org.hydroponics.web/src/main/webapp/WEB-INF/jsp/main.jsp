<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<jsp:include page="includes/head.jsp" flush="true" />
<body>
<section>
    <jsp:include page="includes/header.jsp" flush="true" />
    <jsp:include page="includes/menu.jsp" flush="true" />

    <div id="main" role="main">
        <div id="switches" style="align:center; valign=center;">
            <c:forEach items="${switches}" var="s">
                <c:choose>
                    <c:when test="${s.status eq 0}">
                        <img id="switch${s.number}" src="${pageContext.request.contextPath}/images/redled.png"/>
                    </c:when>
                    <c:otherwise>
                        <img id="switch${s.number}" src="${pageContext.request.contextPath}/images/greenled.png"/>
                    </c:otherwise>
                </c:choose>
                <a href="schedules.htm?id=${s.number}"><c:out value="${s.name}" /></a>&nbsp;&nbsp;
            </c:forEach>
        </div>

        <jsp:include page="includes/data.jsp" flush="true" />

        <ul id="images" class="jcarousel-skin-tango">
            <c:forEach items="${images}" var="image">
                <li><img src="image/${image}?type=thumb" /></li>
            </c:forEach>
        </ul>

        <a id="anchor_temp" rel="#graph_overlay">Temperature/Humidity »</a>
        <a id="anchor_current" rel="#graph_current">Current »</a>
        <a id="anchor_moisture" rel="#graph_moisture">Moisture »</a>
        <a id="anchor_fertilizer" rel="#graph_fertilizer">Fertilizer »</a>

    <!-- start graph -->
    <div id="graph_overlay" class="graph_overlay">
        <h1>Temperature/Humidity</h1>
        <div id="graph_calibre" style="width:470px; height:300px; align=center;"></div>
    </div>
    <div id="graph_current" class="graph_overlay">
        <h1>Current</h1>
        <div id="graph_current" style="width:470px; height:300px; align=center;"></div>
    </div>
    <div id="graph_moisture" class="graph_overlay">
        <h1>Moisture</h1>
        <div id="graph_moisture" style="width:470px; height:300px; align=center;"></div>
    </div>
    <div id="graph_fertilizer" class="graph_overlay">
        <h1>Fertilizer</h1>
        <div id="graph_fertilizer" style="width:470px; height:300px; align=center;"></div>
    </div>

<script type="text/javascript">
    var temperature = $("#temperature");
    var humidity = $("#humidity");
    var current = $("#current");
    var moisture = $("#moisture");


    $(document).ready(function() {
        $("#anchor_temp").overlay({
            color: '#ccc',
            top: 50,
            onLoad: function(event) {
                 graphCalibre = new Dygraph(
                    document.getElementById("graph_calibre"),
                    "calibre.csv?growId=<c:out value="${grow.id}"/>", {
                        rollPeriod: 2
                    }
                );
            }
        });
        $("#anchor_current").overlay({
            color: '#ccc',
            top: 50,
            onLoad: function(event) {
                 graphCalibre = new Dygraph(
                    document.getElementById("graph_current"),
                    "current.csv?growId=<c:out value="${grow.id}"/>", {
                        rollPeriod: 2
                    }
                );
            }
        });
        $("#anchor_moisture").overlay({
            color: '#ccc',
            top: 50,
            onLoad: function(event) {
                 graphCalibre = new Dygraph(
                    document.getElementById("graph_moisture"),
                    "moisture.csv?growId=<c:out value="${grow.id}"/>", {
                        rollPeriod: 2
                    }
                );
            }
        });
        $("#anchor_fertilizer").overlay({
            color: '#ccc',
            top: 50,
            onLoad: function(event) {
                 graphCalibre = new Dygraph(
                    document.getElementById("graph_fertilizer"),
                    "fertilizer.csv?growId=<c:out value="${grow.id}"/>", {
                        rollPeriod: 2
                    }
                );
            }
        });

        jQuery('#images').jcarousel();
    });

    (function pollCalibre(){
        $.ajax({ url: "update", success: function(data){
            if(data.event == "calibre") {
                temperature.text(new String(data.temperature) + " °C");
                humidity.text(new String(data.humidity) + " %");
                current.text(new String(data.current) + " A");
                moisture.text(new String(data.moisture));
            } else if(data.event == "switches") {
                if(data.status == 0) {
                    $("#switch"+data.number).attr('src','<c:out value="${pageContext.request.contextPath}"/>/images/redled.png');
                } else {
                    $("#switch"+data.number).attr('src','<c:out value="${pageContext.request.contextPath}"/>/images/greenled.png');
                }
            } else {
             alert(data.event);
            }
        }, dataType: "json", complete: pollCalibre, timeout: 30000 });
    })();
</script>
</div>
</section>
</body>
