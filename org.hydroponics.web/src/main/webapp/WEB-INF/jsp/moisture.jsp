<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>Date,Moisture
<%
org.springframework.jdbc.support.rowset.SqlRowSet rowSet =
    (org.springframework.jdbc.support.rowset.SqlRowSet) request.getAttribute("rowSet");
java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
while (rowSet.next()) {
    java.sql.Timestamp stamp = rowSet.getTimestamp("timestamp");
    out.print(df.format(new java.util.Date(stamp.getTime())));
    out.print(",");
    out.print(rowSet.getString("moisture"));
    out.println();
}
%>