package as2.app;
import org.pjsip.pjsua2.*;


class ClientAccount extends Account {

  @Override
  public void onRegState(OnRegStateParam prm){
    System.out.println("*** registration state: " + prm.getCode() + prm.getReason());
  }

  @Override
  public void onInstantMessage(OnInstantMessageParam msg){
    String message = msg.getMsgBody();
  }

}
