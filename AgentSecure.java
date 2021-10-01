package agentsecure;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.*;
import java.net.http.HttpClient.Version;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;

public class AgentSecure {
 String startCaptureURI = "http://localhost:8080/startCapture";
 String stopCaptureURI = "http://localhost:8080/stopCapture";
 String showCaptureURI = "http://localhost:8080/showCapture"; 
 Call call; 

 public static void main(String[] args){
  HttpClient client = HttpClient.newHttpClient();
  HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create("https://jsonplaceholder.typicode.com/todos/1"))
  .build();
  HttpResponse<String> response;
  try { 
    response = client.send(request, HttpResponse.BodyHandlers.ofString());
    Response r = new Gson().fromJson(response.body(), Response.class);
    System.out.println(r);
  } catch(Exception e){
        System.out.println(e);
  }
 }

 public CaptureResponse startCapture(CaptureRequest captureRequest) throws IOException, InterruptedException{
  String requestBody = new Gson().toJson(captureRequest);
  HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(startCaptureURI))
      .header("Content-Type", "application/json")
      .POST(BodyPublishers.ofString(requestBody))
      .build();
  HttpClient client = HttpClient.newHttpClient();
  HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
  System.out.println(response.statusCode());
  return new Gson().fromJson(response.body(), CaptureResponse.class);  

 }

 public CaptureResponse stopCapture(ShowCaptureRequest captureRequest) throws IOException, InterruptedException{
  String requestBody = new Gson().toJson(captureRequest);
  HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(stopCaptureURI))
      .header("Content-Type", "application/json")
      .POST(BodyPublishers.ofString(requestBody))
      .build();
  HttpClient client = HttpClient.newHttpClient();
  HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
  System.out.println(response.statusCode());
  return new Gson().fromJson(response.body(), CaptureResponse.class);  


 }

 public CaptureResponse showCapture(ShowCaptureRequest captureRequest) throws IOException, InterruptedException{
  String requestBody = new Gson().toJson(captureRequest);
  HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(showCaptureURI))
      .header("Content-Type", "application/json")
      .POST(BodyPublishers.ofString(requestBody))
      .build();
  HttpClient client = HttpClient.newHttpClient();
  HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
  System.out.println(response.statusCode());
  return new Gson().fromJson(response.body(), CaptureResponse.class);  

 }

}

class Call{
  String correlationId;

}

class CapturedField{
  String name;
  Boolean valid;
  String value;
}

class Cursor{
  String field;
  int position;  
}

class CaptureResponse{
  String capture_id;
  ArrayList<CapturedField> captured_fields;
  Cursor cursor;

}

class CaptureRequest{
  String correlation_id;
  String[] capture_order;

  public CaptureRequest(String correlationId, String[] capture_order){
    this.correlation_id = correlationId;
    this.capture_order = capture_order;
  }
}

class ShowCaptureRequest{
  String correlation_id;
  String username;
  String password;
}

class Response{
  int userId;
  int id;
  String title;
  boolean completed;

  @Override
  public String toString(){
    return "User Id: " + userId + ", id: " + id + ", title: " + title + ", completed: " + completed; 
  }
}
