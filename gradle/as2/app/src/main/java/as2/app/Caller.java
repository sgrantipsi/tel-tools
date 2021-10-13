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

    public OutboundCall(Account acc, String destination) {
        super(acc);
        this.destination = destination;
    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam prm){
        pjsip_inv_state state;
        int mediaSize;
        try{
            CallInfo callInfo = getInfo();
            CallMediaInfoVector medias = callInfo.getMedia();
            mediaSize = (int) medias.size(); 
            for(int i = 0; i < mediaSize; i++){
              if(medias.get(i).getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
                medias.get(i).getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE
              ){

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

    public String execute(String dtmfs, int startDelay, int endDelay, int dtmfDelay) 
        throws Exception{
        String correlationId = "test-123";
        CallOpParam callOp = new CallOpParam(true);

        SipHeader sipDirectionHeader = new SipHeader();
        sipDirectionHeader.setHName("X-IPSI-Direction");
        sipDirectionHeader.setHValue("inbound");

        SipHeader sipIdHeader = new SipHeader();
        sipIdHeader.setHName("X-Correlation-ID");
        sipIdHeader.setHValue(correlationId);

        SipHeaderVector sipHeaderVector = new SipHeaderVector();
        sipHeaderVector.add(sipDirectionHeader);
        sipHeaderVector.add(sipIdHeader);

        SipTxOption sipTxOption = new SipTxOption();
        sipTxOption.setHeaders(sipHeaderVector);
        callOp.setTxOption(sipTxOption);

        makeCall(destination, callOp);
        Thread.sleep(startDelay);
        System.out.println("sending dtmf..");
        for(char dtmf : dtmfs.toCharArray()){
            dialDtmf(String.valueOf(dtmf));
            Thread.sleep(dtmfDelay);
        }
        dialDtmf("#");
        Thread.sleep(endDelay);
        hangup(callOp);
        return correlationId;

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
  private static String destination = "sip:9999@192.168.56.8";
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
        call.execute(dtmfs, startDelay, endDelay, dtmfDelay);
        caller.close();

    } catch (Exception e){
        System.out.println(e);
        return;
    }
  }
}
