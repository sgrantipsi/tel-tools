package as2.app;
import java.util.ArrayList;
import org.pjsip.pjsua2.*;


class OutboundCall extends Call {
    private transient String server;
    protected transient int startDelay;
    protected transient int endDelay;
    protected transient int dtmfDelay;
    protected transient String[] dtmfs;
    protected transient String[] captureOrder;
    private transient CallOpParam callOp;
    private transient SipHeader sipDirectionHeader;
    private transient String directionHeader = "inbound";
    private transient String correlationName = "X-Correlation-ID";
    private transient SignalLock mediaLock = new SignalLock();
    private transient String extension;
    public transient AgentSecure as2; 
    private transient SignalLock lock;

    public transient String correlationIdOutbound; 
    public transient String correlationIdInbound; 
    public ArrayList<CaptureResponse> captures = new ArrayList<CaptureResponse>();
    //jitter total 
    public long meanJitter, maxJitter;
    //jitter sent
    public long meanJitterTx, maxJitterTx;
    //jitter recieved
    public long meanJitterRx; 
    public long maxJitterRx;
    public long maxRawJitterRx, meanRawJitterRx;
    //number of packets and bytes sent/received
    public long packetsTx, packetsRx;
    public long bytesTx, bytesRx;
    public long meanRoundTripTime, maxRoundTripTime; //seconds 
    public long lossTx, lossRx;
    public long reorderTx, reorderRx;
    public long discardTx, discardRx;
    public long dupTx, dupRx;
    public long meanLossPeriodRx, meanLossPeriodTx; 
    public long maxLossPeriodRx, maxLossPeriodTx;


    private String destination(){
        return "sip:" + extension + "@" + server;
    }

    public void doWait(){
        synchronized(mediaLock){
         try {
            mediaLock.wait();
         } catch(InterruptedException e){
            System.out.println(e);
         }
        }
    }

    private void doNotify(){
        System.out.println("notifying...");
        synchronized(mediaLock){
          mediaLock.notify();
        }
    }

    public void setLock(SignalLock lock){
        this.lock = lock;
    }
    
    public OutboundCall(Account acc, String server, String extension) {
        super(acc);
        this.server = server;
        this.extension = extension;
        correlationIdOutbound = "test-123";
        callOp = new CallOpParam(true);

        SipHeader sipDirectionHeader = new SipHeader();
        sipDirectionHeader.setHName("X-IPSI-Direction");
        sipDirectionHeader.setHValue(directionHeader);

        SipHeader sipIdHeader = new SipHeader();
        sipIdHeader.setHName(correlationName);
        sipIdHeader.setHValue(correlationIdOutbound);

        SipHeaderVector sipHeaderVector = new SipHeaderVector();
        sipHeaderVector.add(sipDirectionHeader);
        sipHeaderVector.add(sipIdHeader);

        SipTxOption sipTxOption = new SipTxOption();
        sipTxOption.setHeaders(sipHeaderVector);
        callOp.setTxOption(sipTxOption);

    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam prm) {
        pjsip_inv_state state;
        int mediaSize;

        synchronized(lock){
	  //Thread.sleep(startDelay);
	  System.out.println("Waiting for correlation id");
          while(lock.message == null){
	        try{
	          lock.wait();
		} catch (InterruptedException e){}
          }
       	  correlationIdInbound = lock.message;
	  System.out.println("received correlation ID !: " + correlationIdInbound);
        }

        try{
          CallInfo callInfo = getInfo();
          CallMediaInfoVector medias = callInfo.getMedia();
          mediaSize = (int) medias.size(); 
          System.out.println("--> Sending DTMF");
          for(int i = 0; i < mediaSize; i++){
            if(medias.get(i).getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
              medias.get(i).getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE
            ){
              sendDTMF();
            }
          }
          Thread.sleep(endDelay);
          hangup(callOp);
          collectStats();
          doNotify();
        } catch (Exception e){
          System.out.println("!!error on media state 2: " + e);
        }
    }

    @Override
    public void onCallState(OnCallStateParam prm){
        pjsip_inv_state state;

        try{
            CallInfo info = getInfo();
            state = info.getState();
        } catch (Exception e) {
            System.out.println("!!! failure getting call state: " + e);
            return;
	}
        if(state == pjsip_inv_state.PJSIP_INV_STATE_CALLING){
           System.out.println("--> calling");
        }
        else if(state == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED){
           System.out.println("--> call confirmed");
        }
        else if(state == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING){
           System.out.println("--> call connecting");
        }
        else if(state == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED){
            System.out.println("--> call disconnected");
            //System.exit(0);
        }
        else if(state == pjsip_inv_state.PJSIP_INV_STATE_EARLY){
            System.out.println("--> early call");
        }
        else if(state == pjsip_inv_state.PJSIP_INV_STATE_INCOMING){
            System.out.println("--> incoming call");
        }
        else if(state == pjsip_inv_state.PJSIP_INV_STATE_NULL){
            System.out.println("-->null call");
        }
    }

    public void start() 
        throws Exception{
        makeCall(destination(), callOp);
    } 

    private void collectStats() throws Exception{
        //transmitted and received stats
        StreamStat streamStat = getStreamStat(0);
        RtcpStat rtcpStat = streamStat.getRtcp();
        RtcpStreamStat txStats = rtcpStat.getTxStat();
        RtcpStreamStat rxStats = rtcpStat.getRxStat(); 
       
        //jitter
        MathStat txJitter = txStats.getJitterUsec();
        MathStat rxJitter = rxStats.getJitterUsec();
        meanJitterRx = rxJitter.getMean();
        meanJitterTx = txJitter.getMean();
        maxJitterRx = rxJitter.getMax();
        maxJitterTx = txJitter.getMax();

        //lossPeriod
        MathStat txLossPeriod = txStats.getLossPeriodUsec();
        MathStat rxLossPeriod = rxStats.getLossPeriodUsec();
        meanLossPeriodRx = rxLossPeriod.getMean();
        meanLossPeriodTx = txLossPeriod.getMean();
        maxLossPeriodRx = rxLossPeriod.getMax();
        maxLossPeriodTx = txLossPeriod.getMax();

        //loss
        lossTx = txStats.getLoss(); 
        lossRx = rxStats.getLoss();
        //packets
        packetsTx = txStats.getPkt();
        packetsRx = rxStats.getPkt();
        //bytes
        bytesTx = txStats.getBytes();
        bytesRx = rxStats.getBytes();
        //reorder
        reorderTx = txStats.getReorder();
        reorderRx = txStats.getReorder();
        //discard
        discardTx = txStats.getDiscard();
        discardRx = txStats.getDiscard();
        //dup
        dupTx = txStats.getDup();
        dupRx = txStats.getDup();
        //round trip 
        MathStat roundTripTime = rtcpStat.getRttUsec();
        meanRoundTripTime = roundTripTime.getMean();
        maxRoundTripTime = roundTripTime.getMax(); 
        //raw Jitter
        MathStat rawJitterStat = rtcpStat.getRxRawJitterUsec();
        maxRawJitterRx = rawJitterStat.getMax(); 
        meanRawJitterRx = rawJitterStat.getMean(); 
    }

    private void sendDTMFString(String dtmfString) throws Exception{
	   CaptureResponse captureResponse = as2.startCapture(correlationIdInbound, 
				  captureOrder);
	   System.out.println("start capture response" + captureResponse);
	   for(char dtmf : dtmfString.toCharArray()){
              System.out.println("Sending " + String.valueOf(dtmf));
	      dialDtmf(String.valueOf(dtmf));
	      Thread.sleep(dtmfDelay);
	   }
	   String captureId = captureResponse.captureId;
	   /*
	   try{
	     captureResponse = as2.stopCapture(captureId);
	     System.out.println("stop capture response " + captureResponse);
	   } catch(Exception e){
	     System.out.println("Stop capture error " + e);
	     continue;
	   }
	   */
	   try{
	     captureResponse = as2.showCapture(captureId);
             System.out.println("Correlation Id: " + correlationIdInbound);
             System.out.println("Capture Id: " + captureId);
	     System.out.println("Show capture: " + captureResponse);
	     captures.add(captureResponse);
	   } catch(Exception e){
	     System.out.println("Show capture error: " + e);
	   }
    }

    private void sendDTMF() throws Exception{
        try{
            System.out.println("--> Sending DTMF");
            for(String dtmfString : dtmfs){
                sendDTMFString(dtmfString);
            }
            dialDtmf("#");
        } catch (Exception e) {
            System.out.println("!! Error: " + e);
	}
    }
}

