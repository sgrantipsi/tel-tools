package as2.app;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.*;
import java.net.http.HttpClient.Version;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm; 
import com.auth0.jwt.exceptions.JWTCreationException;

public class AgentSecure {
 private static String server = "http://192.168.56.8:8080";
 private static String startCaptureURI = server + "/startCapture";
 private static String stopCaptureURI = server + "/stopCapture";
 private static String showCaptureURI = server + "/showCapture"; 
 private static String updateCaptureURI = server + "/updateCapture"; 
 private static String healthURI = server + "/health"; 
 HttpClient client;
 HttpRequest healthRequest;

 public static void main(String[] args){
  AgentSecure as2 = new AgentSecure();
  try{ 
    // System.out.println(as2.healthCheck());
  } catch(Exception e){
    System.out.println(e);
  }
 }

 public AgentSecure(){
  client = HttpClient.newHttpClient();
  healthRequest = HttpRequest.newBuilder()
      .uri(URI.create(healthURI))
      .header("Content-Type", "application/json")
      .build();
 }

 public int healthCheck() throws IOException, InterruptedException{
  HttpResponse<String> response = client.send(healthRequest, 
                                  HttpResponse.BodyHandlers.ofString());
  return response.statusCode();
 }
 
 private String createToken(String secret){
  try { 
    Algorithm algorithm = Algorithm.HMAC256(secret);
    String token = JWT.create().withIssuer("test").sign(algorithm);
    return token;
  } catch(JWTCreationException exception){
    return "";
  }
 }

 public CaptureResponse startCapture(String correlationId, String[] captureOrder, 
                                     String username, String password, String token) 
                                     throws IOException, InterruptedException{
  CaptureRequest captureRequest = new CaptureRequest(correlationId, captureOrder, 
                                  username, password);
  String requestBody = new Gson().toJson(captureRequest);
  String generatedToken = createToken(token);
  HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(startCaptureURI))
      .header("Content-Type", "application/json")
      .header("TOKEN", generatedToken)
      .POST(BodyPublishers.ofString(requestBody))
      .build();

  HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
  System.out.println("the start capture response body is..");
  System.out.println(response.body());
  return new Gson().fromJson(response.body(), CaptureResponse.class);  
 }

 public CaptureResponse stopCapture(String captureId, String username, 
                                    String password, String token) 
                                    throws IOException, InterruptedException{
  StopCaptureRequest stopCaptureRequest = new StopCaptureRequest(captureId, username, password);
  String requestBody = new Gson().toJson(stopCaptureRequest);
  String generatedToken = createToken(token);
  HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(stopCaptureURI))
      .header("Content-Type", "application/json")
      .header("TOKEN", generatedToken)
      .POST(BodyPublishers.ofString(requestBody))
      .build();
  HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
  return new Gson().fromJson(response.body(), CaptureResponse.class);  
 }

 public CaptureResponse showCapture(String correlationId, String username, 
                                    String password, String  token) 
                                    throws IOException, InterruptedException{
  StopCaptureRequest captureRequest = new StopCaptureRequest(correlationId, username, password);
  String requestBody = new Gson().toJson(captureRequest);
  String generatedToken = createToken(token);
  HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(showCaptureURI))
      .header("Content-Type", "application/json")
      .header("TOKEN", generatedToken)
      .POST(BodyPublishers.ofString(requestBody))
      .build();
  HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
  return new Gson().fromJson(response.body(), CaptureResponse.class);  
 }

 public CaptureResponse UpdateCapture(String correlationId, String[] field, 
                                      String username, String password, String token) 
                                      throws IOException, InterruptedException{
  UpdateCaptureRequest captureRequest = new UpdateCaptureRequest(
                                        correlationId, field, username, password);
  String requestBody = new Gson().toJson(captureRequest);
  String generatedToken = createToken(token);
  HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(updateCaptureURI))
      .header("Content-Type", "application/json")
      .header("TOKEN", generatedToken)
      .POST(BodyPublishers.ofString(requestBody))
      .build();
  HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
  return new Gson().fromJson(response.body(), CaptureResponse.class);  
 }
}

class CapturedField{
  String name;
  Boolean valid;
  String value;
  
  @Override
  public String toString(){
    return String.format("%s: %s, valid?%s",name, value, valid);
  } 
}

class Cursor{
  String field;
  int position;  
}

class CaptureResponse{
  String captureId;
  CapturedField[] capturedFields;
  Cursor cursor;
  String responseCode;
  String[] errors;
  
  @Override
  public String toString(){
    if(errors != null){
      return responseCode; 
    } else {
      String fields = "";
      for(CapturedField field: capturedFields){
        fields += field.toString();
      }
      return String.format("captureId: %s, responsecode: %s, errors %s, capturedFields: %s", 
      captureId, responseCode, errors, fields); 
    }
  }
}

class StopCaptureRequest{
  String captureId;
  String username;
  String password;

  public StopCaptureRequest(String captureId, String username, String password){
    this.captureId = captureId;
    this.username = username;
    this.password = password; 
  }
}

class UpdateCaptureRequest extends StopCaptureRequest{
  String[] field;
  public UpdateCaptureRequest(String correlationId, String[] field, 
                              String username, String password){
    super(correlationId, username, password);
    this.field = field;
  }
}

class CaptureRequest{
  String correlationId;
  String[] captureOrder;
  String username;
  String password;

  public CaptureRequest(String correlationId, String[] captureOrder, 
                        String username, String password){
    this.correlationId = correlationId;
    this.captureOrder = captureOrder;
    this.username = username;
    this.password = password; 
  }
}


class Response{
  int userId;
  int id;
  String title;
  boolean completed;

  @Override
  public String toString(){
    return "User Id: " + userId + ", id: " + id + ", title: " + 
            title + ", completed: " + completed; 
  }
}
