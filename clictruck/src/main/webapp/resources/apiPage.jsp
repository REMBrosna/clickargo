<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Enumeration" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Print HTTP Information</title>
</head>
<body>

<h1>HTTP Information</h1>

<h2>Request Method:</h2>
<% 
    String method = request.getMethod();
    out.println("<p>Request Method: " + method + "</p>");
%>

<h2>Request Headers:</h2>
<%
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        String headerValue = request.getHeader(headerName);
        out.println("<p>" + headerName + ": " + headerValue + "</p>");
    }
%>

<h2>Request Body:</h2>
<% 
    java.util.Scanner s = new java.util.Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
    String requestBody = s.hasNext() ? s.next() : "";
    out.println("<p>" + requestBody + "</p>");
%>

</body>
</html>
