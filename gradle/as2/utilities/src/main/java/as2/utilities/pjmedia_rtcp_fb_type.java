/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public final class pjmedia_rtcp_fb_type {
  public final static pjmedia_rtcp_fb_type PJMEDIA_RTCP_FB_ACK = new pjmedia_rtcp_fb_type("PJMEDIA_RTCP_FB_ACK");
  public final static pjmedia_rtcp_fb_type PJMEDIA_RTCP_FB_NACK = new pjmedia_rtcp_fb_type("PJMEDIA_RTCP_FB_NACK");
  public final static pjmedia_rtcp_fb_type PJMEDIA_RTCP_FB_TRR_INT = new pjmedia_rtcp_fb_type("PJMEDIA_RTCP_FB_TRR_INT");
  public final static pjmedia_rtcp_fb_type PJMEDIA_RTCP_FB_OTHER = new pjmedia_rtcp_fb_type("PJMEDIA_RTCP_FB_OTHER");

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static pjmedia_rtcp_fb_type swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + pjmedia_rtcp_fb_type.class + " with value " + swigValue);
  }

  private pjmedia_rtcp_fb_type(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private pjmedia_rtcp_fb_type(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private pjmedia_rtcp_fb_type(String swigName, pjmedia_rtcp_fb_type swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static pjmedia_rtcp_fb_type[] swigValues = { PJMEDIA_RTCP_FB_ACK, PJMEDIA_RTCP_FB_NACK, PJMEDIA_RTCP_FB_TRR_INT, PJMEDIA_RTCP_FB_OTHER };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}

