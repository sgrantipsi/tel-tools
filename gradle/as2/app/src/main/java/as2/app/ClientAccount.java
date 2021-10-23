package as2.app;
import org.pjsip.pjsua2.*;


class ClientAccount extends Account {
  private SignalLock lock;

  @Override
  public void onRegState(OnRegStateParam prm){
    System.out.println("*** One registration state: " + prm.getCode() + prm.getReason());
  }

  @Override
  public void onInstantMessage(OnInstantMessageParam msg){
    System.out.println("CorrelationId: "   + msg.getMsgBody());
    synchronized(lock){
	    lock.message = msg.getMsgBody();
	    System.out.println("Correlation ID is " + lock.message);
	    lock.notify();
            System.out.println("notified");
    }
  }

  public void setLock(SignalLock lock){
      this.lock = lock;
  }

  public String getCorrelationId(){
     return this.lock.message;
  }
}
