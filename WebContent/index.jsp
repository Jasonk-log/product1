<%@ page import = "common.CallService" %>
<%@ page import = "org.json.simple.JSONArray" %>
<%@ page import = "org.json.simple.JSONObject" %>
<%@ page import = "org.json.simple.parser.JSONParser" %>
<%@ page import = "org.json.simple.parser.ParseException" %>
<%
	//String product1 = null;

	CallService cs = new CallService();
	String part1 = cs.getJSON("http://part1:8080");
	String part2 = cs.getJSON("http://part2:8080");
	String part3 = cs.getJSON("http://part3:8080");

	
	JSONParser parser = new JSONParser();
	Object obj1 = parser.parse(part1);
	Object obj2 = parser.parse(part2);
	Object obj3 = parser.parse(part3);
	
	JSONObject jsonObj1 = (JSONObject) obj1;
	String nm_part1 = (String) jsonObj1.get("name");
	Integer up_part1 = (Integer) jsonObj1.get("unitprice");
	
	JSONObject jsonObj2 = (JSONObject) obj2;
	String nm_part2 = (String) jsonObj2.get("name");
	Integer up_part2 = (Integer) jsonObj2.get("unitprice");
	
	JSONObject jsonObj3 = (JSONObject) obj3;
	String nm_part3 = (String) jsonObj3.get("name");
	Integer up_part3 = (Integer) jsonObj3.get("unitprice");
	

	Integer quantity = 10;
	Integer tuprice = up_part1 + up_part2 + up_part3;
	Integer expense = 20;
	Integer price = tuprice + expense;	
	
	JSONObject product1 = new JSONObject();
	product1.put("name", "product1");
	product1.put("quantity", quantity);
	product1.put("unitprice", tuprice);
	product1.put("expense", expense);
	product1.put("price", price);
	
				
	out.print(product1.toString());
	
	
%>
