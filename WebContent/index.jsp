<%@ page import = "common.CallService" %>
<%
	CallService cs = new CallService();
	String json_part1 = cs.getJSON("http://part1:8080");
	String json_part2 = cs.getJSON("http://part2:8080");
	String json_part3 = cs.getJSON("http://part3:8080");

	out.println(json_part1); out.println("<br>");
	out.println(json_part2); out.println("<br>");
	out.println(json_part3); out.println("<br>");
	
%>
