/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class CodecParamInfo {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected CodecParamInfo(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CodecParamInfo obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsua2JNI.delete_CodecParamInfo(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setClockRate(long value) {
    pjsua2JNI.CodecParamInfo_clockRate_set(swigCPtr, this, value);
  }

  public long getClockRate() {
    return pjsua2JNI.CodecParamInfo_clockRate_get(swigCPtr, this);
  }

  public void setChannelCnt(long value) {
    pjsua2JNI.CodecParamInfo_channelCnt_set(swigCPtr, this, value);
  }

  public long getChannelCnt() {
    return pjsua2JNI.CodecParamInfo_channelCnt_get(swigCPtr, this);
  }

  public void setAvgBps(long value) {
    pjsua2JNI.CodecParamInfo_avgBps_set(swigCPtr, this, value);
  }

  public long getAvgBps() {
    return pjsua2JNI.CodecParamInfo_avgBps_get(swigCPtr, this);
  }

  public void setMaxBps(long value) {
    pjsua2JNI.CodecParamInfo_maxBps_set(swigCPtr, this, value);
  }

  public long getMaxBps() {
    return pjsua2JNI.CodecParamInfo_maxBps_get(swigCPtr, this);
  }

  public void setMaxRxFrameSize(long value) {
    pjsua2JNI.CodecParamInfo_maxRxFrameSize_set(swigCPtr, this, value);
  }

  public long getMaxRxFrameSize() {
    return pjsua2JNI.CodecParamInfo_maxRxFrameSize_get(swigCPtr, this);
  }

  public void setFrameLen(long value) {
    pjsua2JNI.CodecParamInfo_frameLen_set(swigCPtr, this, value);
  }

  public long getFrameLen() {
    return pjsua2JNI.CodecParamInfo_frameLen_get(swigCPtr, this);
  }

  public void setPcmBitsPerSample(long value) {
    pjsua2JNI.CodecParamInfo_pcmBitsPerSample_set(swigCPtr, this, value);
  }

  public long getPcmBitsPerSample() {
    return pjsua2JNI.CodecParamInfo_pcmBitsPerSample_get(swigCPtr, this);
  }

  public void setPt(long value) {
    pjsua2JNI.CodecParamInfo_pt_set(swigCPtr, this, value);
  }

  public long getPt() {
    return pjsua2JNI.CodecParamInfo_pt_get(swigCPtr, this);
  }

  public void setFmtId(pjmedia_format_id value) {
    pjsua2JNI.CodecParamInfo_fmtId_set(swigCPtr, this, value.swigValue());
  }

  public pjmedia_format_id getFmtId() {
    return pjmedia_format_id.swigToEnum(pjsua2JNI.CodecParamInfo_fmtId_get(swigCPtr, this));
  }

  public CodecParamInfo() {
    this(pjsua2JNI.new_CodecParamInfo(), true);
  }

}
