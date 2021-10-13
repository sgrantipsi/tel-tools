//package au.com.ipsi.agentsecure;
package as2.app;
import java.util.*;
import org.pjsip.pjsua2.*;
import org.pjsip.pjsua2.Endpoint;
import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm; 


class MyAccount extends Account {
  public static MyCall call; 

  @Override
  public void onRegState(OnRegStateParam prm){
    System.out.println("*** One registration state: " + prm.getCode() + prm.getReason());
  }

  public String getHeader(String data, String name){
    int index = data.indexOf(name);
    if(index == -1){
        return "";
    }
    String sub = data.substring(index).split("\\r?\\n", 2)[0]; 
    int i = sub.indexOf(":");
    String value = sub.substring(i+1);
    return value.trim();
  }

  @Override
  public void onIncomingCall(OnIncomingCallParam incoming){
    String message = incoming.getRdata().getWholeMsg();
    String correlationId = getHeader(message, "X-Correlation-ID");
    
    call = new MyCall(this, incoming.getCallId(), correlationId);
    CallOpParam call_param = new CallOpParam();
    call_param.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
    try{
      call.answer(call_param);
    } catch (Exception e){
       System.out.println(e);
    }
  }

}

class MyCall extends Call {
    public String callId;
    private AgentSecure as2;
    public String correlationId;
    public String captureId;
    private String[] captureOrder;
    private String username;
    private String password;
    private String token;
    private int dtmfCount = 0;
    

    public MyCall(Account acc, int callId, String correlationId){
        super(acc, callId);
        this.correlationId = correlationId;
        as2 = new AgentSecure();
        username = "username";
        password = "password";
        captureOrder = new String[]{"pan", "expirtyDate", "cvv"};
        token = "secret";
    }

    @Override
    public void onDtmfDigit(OnDtmfDigitParam digitParam){
        String digit = digitParam.getDigit();
        System.out.println("DTMF received ==> " + digit);
        dtmfCount++;
        System.out.println("count: " + dtmfCount);
        if(dtmfCount == 5){
          try{
            CaptureResponse stopResponse = as2.stopCapture(captureId, username, password, token);  
            System.out.println(stopResponse);     
            CaptureResponse showResponse = as2.showCapture(captureId, username, password, token);  
            System.out.println(showResponse);     
	  } catch(Exception e){
            System.out.println(e);
          } 
        }
    }

    @Override
    public void onCallState(OnCallStateParam prm){
        pjsip_inv_state state;
        try{
            CallInfo info = getInfo();
            state = info.getState();
        } catch (Exception e) {
            System.out.println("!!!!!!!!! failure getting call state");
            return;
	}
        if( state == pjsip_inv_state.PJSIP_INV_STATE_CALLING){
            System.out.println("------------>calling");
	}
        else if( state == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED){
            System.out.println("------------>call confirmed");
	    try { 
              CallInfo info = getInfo();
              String callId = info.getCallIdString();
	      System.out.println("****correlation id***************" + correlationId);
              
              CaptureResponse captureResponse =  as2.startCapture(correlationId, captureOrder, username, password, token);
              captureId = captureResponse.captureId;
              System.out.println("capture id is..");
	      System.out.println("******capture id : " + captureResponse.captureId);
	      System.out.println("******name : " + captureResponse.capturedFields[0].name);
	      System.out.println("******valid: " + captureResponse.capturedFields[0].valid);
	      System.out.println("******value: " + captureResponse.capturedFields[0].value);
              System.out.println("***** response code***" + captureResponse.responseCode);
              System.out.println("***** errors***" + captureResponse.errors);
              System.out.println(correlationId);

	    } catch(Exception e){
              System.out.println("Failed");
	      System.out.println(e);
	    }
	}
        else if( state == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING){
            System.out.println("------------>call connecting");
	}
        else if( state == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED){
            System.out.println("------------>call disconnected");
	}
        else if( state == pjsip_inv_state.PJSIP_INV_STATE_EARLY){
            System.out.println("------------>early call");
	}
        else if( state == pjsip_inv_state.PJSIP_INV_STATE_INCOMING){
            System.out.println("------------>incoming call");
	}
        else if( state == pjsip_inv_state.PJSIP_INV_STATE_NULL){
            System.out.println("------------>null call");
	}
    }
}

public class Ivr {
  static {
    System.loadLibrary("pjsua2");
    System.out.println("Library loaded xx");
  }

  public static void main(String argv[]){
    try {
        
        Endpoint ep = new Endpoint();
        ep.libCreate(); 
        // Initialize endpoint
        EpConfig epConfig = new EpConfig();
        ep.libInit(epConfig);
        // Create SIP transport.
        TransportConfig sipTpConfig = new TransportConfig();
        sipTpConfig.setPort(5060);
        sipTpConfig.setBoundAddress("192.168.56.7");
        sipTpConfig.setPublicAddress("192.168.56.7");
        ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
        ep.libStart();

        TransportConfig mediaTpConfig = new TransportConfig();
        mediaTpConfig.setBoundAddress("192.168.56.7");
        AccountMediaConfig amc = new AccountMediaConfig();
        amc.setTransportConfig(mediaTpConfig);

        AccountConfig acfg = new AccountConfig();
        acfg.setIdUri("sip:sip_trunk@192.168.56.8");
        acfg.getRegConfig().setRegistrarUri("sip:192.168.56.8");
        acfg.setMediaConfig(amc);
        AuthCredInfo cred = new AuthCredInfo("digest", "asterisk", "sip_trunk", 0, "password");
        acfg.getSipConfig().getAuthCreds().add(cred);
        MyAccount acc = new MyAccount();
        acc.create(acfg);
        //Scanner userInput = new Scanner(System.in);
        System.out.println("sleeping..");
        //String input = userInput.nextLine();
        Thread.sleep(300000);
        acc.delete();
        ep.libDestroy();
        ep.delete();

    } catch (Exception e){
        System.out.println(e);
        return;
    }
  }
}
