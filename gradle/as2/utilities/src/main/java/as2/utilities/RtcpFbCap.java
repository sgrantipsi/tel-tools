/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class RtcpFbCap {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected RtcpFbCap(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(RtcpFbCap obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsua2JNI.delete_RtcpFbCap(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setCodecId(String value) {
    pjsua2JNI.RtcpFbCap_codecId_set(swigCPtr, this, value);
  }

  public String getCodecId() {
    return pjsua2JNI.RtcpFbCap_codecId_get(swigCPtr, this);
  }

  public void setType(pjmedia_rtcp_fb_type value) {
    pjsua2JNI.RtcpFbCap_type_set(swigCPtr, this, value.swigValue());
  }

  public pjmedia_rtcp_fb_type getType() {
    return pjmedia_rtcp_fb_type.swigToEnum(pjsua2JNI.RtcpFbCap_type_get(swigCPtr, this));
  }

  public void setTypeName(String value) {
    pjsua2JNI.RtcpFbCap_typeName_set(swigCPtr, this, value);
  }

  public String getTypeName() {
    return pjsua2JNI.RtcpFbCap_typeName_get(swigCPtr, this);
  }

  public void setParam(String value) {
    pjsua2JNI.RtcpFbCap_param_set(swigCPtr, this, value);
  }

  public String getParam() {
    return pjsua2JNI.RtcpFbCap_param_get(swigCPtr, this);
  }

  public RtcpFbCap() {
    this(pjsua2JNI.new_RtcpFbCap(), true);
  }

}
