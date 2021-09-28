import org.pjsip.pjsua2.*;
import org.pjsip.pjsua2.Endpoint;

class MyAccount extends Account {
  @Override
  public void onRegState(OnRegStateParam prm){
    System.out.println("*** One registration state: " + prm.getCode() + prm.getReason());
  }

}

class MyCall extends Call {
    public MyCall(Account acc) {
        super(acc);
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

public class caller{
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
        ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
        ep.libStart();

        AccountConfig acfg = new AccountConfig();
        acfg.setIdUri("sip:testclient@192.168.56.8");
        acfg.getRegConfig().setRegistrarUri("sip:192.168.56.8");
        AuthCredInfo cred = new AuthCredInfo("digest", "asterisk", "testclient", 0, "password");
        acfg.getSipConfig().getAuthCreds().add(cred);
        MyAccount acc = new MyAccount();
        acc.create(acfg);
        Thread.sleep(2000);
        MyCall call = new MyCall(acc);
        CallOpParam callOp = new CallOpParam(true);

        SipHeader sipDirectionHeader = new SipHeader();
        sipDirectionHeader.setHName("X-IPSI-Direction");
        sipDirectionHeader.setHValue("inbound");

        SipHeader sipIdHeader = new SipHeader();
        sipIdHeader.setHName("X-Correlation-ID");
        sipIdHeader.setHValue("test-123");

        SipHeaderVector sipHeaderVector = new SipHeaderVector();
        sipHeaderVector.add(sipDirectionHeader);
        sipHeaderVector.add(sipIdHeader);

        SipTxOption sipTxOption = new SipTxOption();
        sipTxOption.setHeaders(sipHeaderVector);
        callOp.setTxOption(sipTxOption);

        call.makeCall("sip:9999@192.168.56.8", callOp);
        // Thread.sleep(2000);
        Thread.sleep(4000);
        System.out.println("sending dtmf");
        //call.dialDtmf("7");
        call.dialDtmf("1");
        Thread.sleep(500);
        call.dialDtmf("2");
        Thread.sleep(500);
        call.dialDtmf("3");
        Thread.sleep(500);
        call.dialDtmf("4");
        Thread.sleep(500);
        call.dialDtmf("5");
        //call.dialDtmf("12345");
        Thread.sleep(300000);
        call.hangup(callOp);
        acc.delete();
        ep.libDestroy();
        ep.delete();
        System.out.println("haaaai");

    } catch (Exception e){
        System.out.println(e);
        return;
    }
  }
}
