package as2.app;
import org.pjsip.pjsua2.*;


class ClientAccount extends Account {
  private SignalLock lock;
  private OutboundCall call;

  @Override
  public void onRegState(OnRegStateParam prm){
    System.out.println("*** registration state: " + prm.getCode() + prm.getReason());
  }

  @Override
  public void onInstantMessage(OnInstantMessageParam msg){
    String message = msg.getMsgBody();
    if(message.equals("xxx")){
      call.close();
    } else { 
     call.setInboundCorrelationId(message);
     call.sendDTMF();
    }
    
  }

  public void setLock(SignalLock lock){
      this.lock = lock;
  }

  public void setCall(OutboundCall call){
      this.call = call;
  }

  public String getCorrelationId(){
     return this.lock.message;
  }
}
