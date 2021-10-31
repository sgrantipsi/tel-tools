package as2.app;
import org.pjsip.pjsua2.*;
import org.pjsip.pjsua2.Endpoint;
import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Handler;
import io.javalin.core.validation.ValidationException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Properties;

class SignalLock{
  public String message;
  public boolean hangup = false;
}


class Request {
  public String correlationName = "X-Correlation-Id";
  public boolean capture = true;
  public String[] captureOrder = new String[]{"pan", "expiryDate", "cvv"};
  public String direction = "inbound"; 
  public String dtmf = "4111"; //"4111111111111111";
  public float dtmfDelaySeconds = 0.5f; 
  public float startDelaySeconds = 0;
  public float endDelaySeconds = 0;
  public boolean includeVoice = true;
  public String apiUser = "username";
  public String apiPassword = "password";
  public String extension = "9999";
  public String secret = "secret";
  public String server = "192.168.56.8";
  public String sipUser = "testclient";
  public String sipPassword = "password";
  public String environment = "dev";


  void setConnectionParams() throws FileNotFoundException, IOException{
     //get env settings from config file from this.environment
    Properties properties = new Properties();
    String configFile = this.environment + ".config";
    InputStream inputStream = new FileInputStream(new File(configFile));
    properties.load(inputStream);
    this.apiUser = properties.getProperty("apiUser");
    this.apiPassword = properties.getProperty("apiPassword");
    this.apiPassword = properties.getProperty("apiPassword");
    this.server = properties.getProperty("server");
    this.secret = properties.getProperty("secret");
    this.sipUser = properties.getProperty("sipUser");
    this.sipPassword = properties.getProperty("sipPassword");
  }
}


public class Caller{
  static {
    System.loadLibrary("pjsua2");
  }
  private static int port = 5060;
  private Endpoint endpoint;
  private EpConfig endpointConfig;
  private TransportConfig transportConfig;
  public ClientAccount account;
  private AccountConfig config;

  private void createEndpoint() throws Exception{
    endpoint = new Endpoint();
    endpoint.libCreate(); 
    // Initialize endpoint
    EpConfig endpointConfig = new EpConfig();
    endpoint.libInit(endpointConfig);
    // Create SIP transport.
    TransportConfig transportConfig = new TransportConfig();
    transportConfig.setPort(port);
    endpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, transportConfig);
    endpoint.libStart();
  }

  private void createAccount(String server, String user, String password) throws Exception{
    config = new AccountConfig();
    config.setIdUri("sip:" + user + "@" + server);
    config.getRegConfig().setRegistrarUri("sip:"+server);
    AuthCredInfo cred = new AuthCredInfo("digest", "asterisk", user, 0, password);
    config.getSipConfig().getAuthCreds().add(cred);
    account = new ClientAccount();
    account.create(config);
  }
 
  public void close() throws Exception{
    account.delete();
    endpoint.libDestroy();
    endpoint.delete();
  }

  public Caller(String server, String user, String password) throws Exception{
     createEndpoint();
     createAccount(server, user, password);
  }

  public static void main(String[] args){
     Javalin app = Javalin.create().start(7000);
     app.post("/call", Caller.testCall);
     app.exception(ValidationException.class, (e, ctx) -> {
       ctx.json(e.getErrors()).status(400);
     });
  }

  public static Handler testCall = ctx -> {
    try {
      
        //Request request = new Request();
        Request request = ctx.bodyAsClass(Request.class);
        request.setConnectionParams();
        Caller caller = new Caller(request.server, 
                                   request.sipUser, request.sipPassword);
        OutboundCall call = new OutboundCall(caller.account, 
                                             request.server, request.extension);
        caller.account.setCall(call);
        call.dtmf = request.dtmf;
        call.captureOrder = request.captureOrder;
        call.dtmfDelay = Math.round(request.dtmfDelaySeconds) * 1000;
        call.startDelay = Math.round(request.startDelaySeconds) * 1000;
        call.endDelay = Math.round(request.endDelaySeconds) * 1000;
        call.as2 = new AgentSecure(request.server, request.apiUser, 
                                   request.apiPassword, request.secret);
        call.start(); //dial
        System.out.println("do await..");
        call.doWait(); //wait for call media to establish and proceed with call plan 
        System.out.println("done");
        caller.close();
        String response = new Gson().toJson(call);
        ctx.json(response);

    } catch (Exception e){
        System.out.println(e);
        ctx.json("{\"error\": " + e + "}");
    }
  };
}
