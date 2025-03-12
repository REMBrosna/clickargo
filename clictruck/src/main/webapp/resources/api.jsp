<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Enumeration" %>
<% 
    String method = request.getMethod();
    System.out.println("<p>Request Method: " + method + "</p>");
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        String headerValue = request.getHeader(headerName);
        System.out.println("<p>" + headerName + ": " + headerValue + "</p>");
    }
    java.util.Scanner s = new java.util.Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
    String requestBody = s.hasNext() ? s.next() : "";
    System.out.println("<p>" + requestBody + "</p>");
%>
<%
    response.setContentType("application/json");
	out.println("{ \"err_code\":\"506\", \"err_msg\":\"Trucking operator not found\" } 	");
%>