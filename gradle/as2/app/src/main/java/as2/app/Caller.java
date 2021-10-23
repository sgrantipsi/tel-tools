package as2.app;
import org.pjsip.pjsua2.*;
import org.pjsip.pjsua2.Endpoint;
import com.google.gson.Gson;


class SignalLock{
  public String message;
}



class Request {
  public String correlationName = "X-Correlation-Id";
  public boolean capture = true;
  public String[] captureOrder = new String[]{"pan", "expiryDate", "cvv"};
  public String direction = "inbound"; 
  public String[] dtmf = new String[]{"4111111111111111"};
  public float dtmfDelaySeconds = 0.5f; 
  public float startDelaySeconds = 0;
  public float endDelaySeconds = 0;
  public String apiUser = "username";
  public String apiPassword = "password";
  public boolean includeVoice = true;
  public String extension = "9999";
  public String secret = "secret";
  public String server = "192.168.56.8";
  public String sipUser = "testclient";
  public String sipPassword = "password";
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

  public static void main(String argv[]){
    try {
        Request request = new Request();
        SignalLock messageLock = new SignalLock();
        Caller caller = new Caller(request.server, 
                                   request.sipUser, request.sipPassword);
        caller.account.setLock(messageLock);
        OutboundCall call = new OutboundCall(caller.account, 
                                             request.server, request.extension);
        call.dtmfs = request.dtmf;
        call.captureOrder = request.captureOrder;
        call.dtmfDelay = Math.round(request.dtmfDelaySeconds) * 1000;
        call.startDelay = Math.round(request.startDelaySeconds) * 1000;
        call.endDelay = Math.round(request.endDelaySeconds) * 1000;
        call.as2 = new AgentSecure(request.server, request.apiUser, 
                                   request.apiPassword, request.secret);
        call.setLock(messageLock);
        call.start(); //dial
        call.doWait(); //wait for call media to establish and proceed with call plan 
        caller.close();
        String response = new Gson().toJson(call);
        System.out.println(response);
        Thread.sleep(15);

    } catch (Exception e){
        System.out.println(e);
        return;
    }
  }
}
