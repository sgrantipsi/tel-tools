package as2.app;
import org.pjsip.pjsua2.*;
import org.pjsip.pjsua2.Endpoint;

class ClientAccount extends Account {
  @Override
  public void onRegState(OnRegStateParam prm){
    System.out.println("*** One registration state: " + prm.getCode() + prm.getReason());
  }
}

class OutboundCall extends Call {
    private String destination;
    protected int startDelay;
    protected int endDelay;
    protected int dtmfDelay;
    protected String dtmfs;
    private CallOpParam callOp;
    private SipHeader sipDirectionHeader;
    private String directionHeader = "outbound";
    private String correlationName = "X-Correlation-ID";
    private String correlationId = "test-123";
    private String signalMonitor = "";

    public void doWait(){
        synchronized(signalMonitor){
         try {
            signalMonitor.wait();
         } catch(InterruptedException e){
            System.out.println(e);
         }
        }
    }

    private void doNotify(){
        synchronized(signalMonitor){
          signalMonitor.notify();
        }
    }
    
    public OutboundCall(Account acc, String destination) {
        super(acc);
        this.destination = destination;
        callOp = new CallOpParam(true);

        SipHeader sipDirectionHeader = new SipHeader();
        sipDirectionHeader.setHName("X-IPSI-Direction");
        sipDirectionHeader.setHValue(directionHeader);

        SipHeader sipIdHeader = new SipHeader();
        sipIdHeader.setHName(correlationName);
        sipIdHeader.setHValue(correlationId);

        SipHeaderVector sipHeaderVector = new SipHeaderVector();
        sipHeaderVector.add(sipDirectionHeader);
        sipHeaderVector.add(sipIdHeader);

        SipTxOption sipTxOption = new SipTxOption();
        sipTxOption.setHeaders(sipHeaderVector);
        callOp.setTxOption(sipTxOption);
    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam prm){
        pjsip_inv_state state;
        int mediaSize;
        try{
            CallInfo callInfo = getInfo();
            CallMediaInfoVector medias = callInfo.getMedia();
            mediaSize = (int) medias.size(); 
            System.out.println("....on call media state -----------------< ");
            for(int i = 0; i < mediaSize; i++){
              if(medias.get(i).getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
                medias.get(i).getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE
              ){
               sendDTMF();
              }

            }
        } catch (Exception e) {
            System.out.println("!! failure getting call state");
            return;
	}
    }

    @Override
    public void onCallState(OnCallStateParam prm){
        pjsip_inv_state state;
        try{
            CallInfo info = getInfo();
            state = info.getState();
        } catch (Exception e) {
            System.out.println("!! failure getting call state");
            return;
	}
        if( state == pjsip_inv_state.PJSIP_INV_STATE_CALLING){
            System.out.println("--> calling");
	}
        else if( state == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED){
            System.out.println("--> call confirmed");
	}
        else if( state == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING){
            System.out.println("--> call connecting");
	}
        else if( state == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED){
            System.out.println("--> call disconnected");
	}
        else if( state == pjsip_inv_state.PJSIP_INV_STATE_EARLY){
            System.out.println("--> early call");
	}
        else if( state == pjsip_inv_state.PJSIP_INV_STATE_INCOMING){
            System.out.println("--> incoming call");
	}
        else if( state == pjsip_inv_state.PJSIP_INV_STATE_NULL){
            System.out.println("-->null call");
	}
    }

    public String execute() 
        throws Exception{

        makeCall(destination, callOp);
        return correlationId;

    } 

    private void sendDTMF() throws Exception{
        System.out.println("sending dtmf..");
        for(char dtmf : dtmfs.toCharArray()){
            dialDtmf(String.valueOf(dtmf));
            Thread.sleep(dtmfDelay);
        }
        dialDtmf("#");
        Thread.sleep(endDelay);
        hangup(callOp);
        doNotify();
    }
 
}

public class Caller{
  static {
    System.loadLibrary("pjsua2");
    System.out.println("Library loaded xx");
  }
  private static String dtmfs = "12345";
  private static int dtmfDelay = 500; //milliseconds
  private static int startDelay = 3000;
  private static int endDelay = 3000;
  private static String secret = "secret";
  private static String username = "username";
  private static String password = "password";
  private static String correlationId; 
  private static String destination = "sip:9999@192.168.56.8";
  private Endpoint endpoint;
  private EpConfig endpointConfig;
  private TransportConfig transportConfig;
  public ClientAccount account;
  private AccountConfig config;
  public static String[] captureOrder = new String[]{"pan", "expiryDate", "cvv"};

  private void createEndpoint() throws Exception{
    endpoint = new Endpoint();
    endpoint.libCreate(); 
    // Initialize endpoint
    EpConfig endpointConfig = new EpConfig();
    endpoint.libInit(endpointConfig);
    // Create SIP transport.
    TransportConfig transportConfig = new TransportConfig();
    transportConfig.setPort(5060);
    endpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, transportConfig);
    endpoint.libStart();
  }

  private void createAccount() throws Exception{
    config = new AccountConfig();
    config.setIdUri("sip:testclient@192.168.56.8");
    config.getRegConfig().setRegistrarUri("sip:192.168.56.8");
    AuthCredInfo cred = new AuthCredInfo("digest", "asterisk", "testclient", 0, "password");
    config.getSipConfig().getAuthCreds().add(cred);
    account = new ClientAccount();
    account.create(config);
  }
 
  public void close() throws Exception{
    account.delete();
    endpoint.libDestroy();
    endpoint.delete();
  }

  public Caller() throws Exception{
     createEndpoint();
     createAccount();
  }

  public static void main(String argv[]){
    try {
        Caller caller = new Caller();
        OutboundCall call = new OutboundCall(caller.account, destination);
        call.dtmfs = dtmfs;
        call.dtmfDelay = dtmfDelay;
        call.startDelay = startDelay;
        call.endDelay = endDelay;
        correlationId = call.execute(); //dial
        AgentSecure as2 = new AgentSecure();
        CaptureResponse captureResponse = as2.startCapture(correlationId, 
                                          captureOrder, username, password, secret);
        //Thread.sleep(startDelay);
        call.doWait(); //wait for call to establish and send dtmf
        String captureId = captureResponse.captureId;
        CaptureResponse stopResonse = as2.stopCapture(captureId, username, password, secret);
        CaptureResponse showResponse = as2.showCapture(captureId, username, password, secret);
        System.out.println("#########################");
        System.out.println(showResponse);
        Thread.sleep(15000);
        caller.close();

    } catch (Exception e){
        System.out.println(e);
        return;
    }
  }
}
