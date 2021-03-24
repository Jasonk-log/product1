package application.rest;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/")
public class Product1RestEndpoint extends Application {
  private static final Boolean ratings_enabled = Boolean.valueOf(System.getenv("ENABLE_RATINGS"));
  
  private static final String star_color = (System.getenv("STAR_COLOR") == null) ? "black" : System.getenv("STAR_COLOR");
  
  private static final String services_domain = (System.getenv("SERVICES_DOMAIN") == null) ? "" : ("." + System.getenv("SERVICES_DOMAIN"));
  
  private static final String ratings_hostname = (System.getenv("RATINGS_HOSTNAME") == null) ? "ratings" : System.getenv("RATINGS_HOSTNAME");
  
  private static final String ratings_service = "http://" + ratings_hostname + services_domain + ":9080/ratings";
  
  private static final String[] headers_to_propagate = new String[] { 
      "x-request-id", "x-b3-traceid", "x-b3-spanid", "x-b3-sampled", "x-b3-flags", "x-ot-span-context", "x-datadog-trace-id", "x-datadog-parent-id", "x-datadog-sampled", "end-user", 
      "user-agent" };
  
  private String getJsonResponse(String productId, int starsReviewer1, int starsReviewer2) {
    String result = "{";
    result = result + "\"id\": \"" + productId + "\",";
    result = result + "\"product1\": [";
    result = result + "{";
    result = result + "  \"reviewer\": \"Reviewer1\",";
    result = result + "  \"text\": \"An extremely entertaining play by Shakespeare. The slapstick humour is refreshing!\"";
    if (ratings_enabled.booleanValue())
      if (starsReviewer1 != -1) {
        result = result + ", \"rating\": {\"stars\": " + starsReviewer1 + ", \"color\": \"" + star_color + "\"}";
      } else {
        result = result + ", \"rating\": {\"error\": \"Ratings service is currently unavailable\"}";
      }  
    result = result + "},";
    result = result + "{";
    result = result + "  \"reviewer\": \"Reviewer2\",";
    result = result + "  \"text\": \"Absolutely fun and entertaining. The play lacks thematic depth when compared to other plays by Shakespeare.\"";
    if (ratings_enabled.booleanValue())
      if (starsReviewer2 != -1) {
        result = result + ", \"rating\": {\"stars\": " + starsReviewer2 + ", \"color\": \"" + star_color + "\"}";
      } else {
        result = result + ", \"rating\": {\"error\": \"Ratings service is currently unavailable\"}";
      }  
    result = result + "}";
    result = result + "]";
    result = result + "}";
    return result;
  }
  
  private JsonObject getRatings(String productId, HttpHeaders requestHeaders) {
    ClientBuilder cb = ClientBuilder.newBuilder();
    Integer timeout = Integer.valueOf(star_color.equals("black") ? 10000 : 2500);
    cb.property("com.ibm.ws.jaxrs.client.connection.timeout", timeout);
    cb.property("com.ibm.ws.jaxrs.client.receive.timeout", timeout);
    Client client = cb.build();
    WebTarget ratingsTarget = client.target(ratings_service + "/" + productId);
    Invocation.Builder builder = ratingsTarget.request(new String[] { "application/json" });
    for (String header : headers_to_propagate) {
      String value = requestHeaders.getHeaderString(header);
      if (value != null)
        builder.header(header, value); 
    } 
    try {
      Response r = builder.get();
      int statusCode = r.getStatusInfo().getStatusCode();
      if (statusCode == Response.Status.OK.getStatusCode())
        try(StringReader stringReader = new StringReader((String)r.readEntity(String.class)); 
            JsonReader jsonReader = Json.createReader(stringReader)) {
          return jsonReader.readObject();
        }  
      System.out.println("Error: unable to contact " + ratings_service + " got status of " + statusCode);
      return null;
    } catch (ProcessingException e) {
      System.err.println("Error: unable to contact " + ratings_service + " got exception " + e);
      return null;
    } 
  }
  
  @GET
  @Path("/health")
  public Response health() {
    return Response.ok().type("application/json").entity("{\"status\": \"Product1 is healthy\"}").build();
  }
  
  @GET
  @Path("/product1/{productId}")
  public Response bookReviewsById(@PathParam("productId") int productId, @Context HttpHeaders requestHeaders) {
    int starsReviewer1 = -1;
    int starsReviewer2 = -1;
    if (ratings_enabled.booleanValue()) {
      JsonObject ratingsResponse = getRatings(Integer.toString(productId), requestHeaders);
      if (ratingsResponse != null && 
        ratingsResponse.containsKey("ratings")) {
        JsonObject ratings = ratingsResponse.getJsonObject("ratings");
        if (ratings.containsKey("Reviewer1"))
          starsReviewer1 = ratings.getInt("Reviewer1"); 
        if (ratings.containsKey("Reviewer2"))
          starsReviewer2 = ratings.getInt("Reviewer2"); 
      } 
    } 
    String jsonResStr = getJsonResponse(Integer.toString(productId), starsReviewer1, starsReviewer2);
    return Response.ok().type("application/json").entity(jsonResStr).build();
  }
}
