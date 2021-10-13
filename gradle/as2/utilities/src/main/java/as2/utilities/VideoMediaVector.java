/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class VideoMediaVector {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected VideoMediaVector(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(VideoMediaVector obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsua2JNI.delete_VideoMediaVector(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public VideoMediaVector() {
    this(pjsua2JNI.new_VideoMediaVector__SWIG_0(), true);
  }

  public VideoMediaVector(long n) {
    this(pjsua2JNI.new_VideoMediaVector__SWIG_1(n), true);
  }

  public long size() {
    return pjsua2JNI.VideoMediaVector_size(swigCPtr, this);
  }

  public long capacity() {
    return pjsua2JNI.VideoMediaVector_capacity(swigCPtr, this);
  }

  public void reserve(long n) {
    pjsua2JNI.VideoMediaVector_reserve(swigCPtr, this, n);
  }

  public boolean isEmpty() {
    return pjsua2JNI.VideoMediaVector_isEmpty(swigCPtr, this);
  }

  public void clear() {
    pjsua2JNI.VideoMediaVector_clear(swigCPtr, this);
  }

  public void add(VideoMedia x) {
    pjsua2JNI.VideoMediaVector_add(swigCPtr, this, VideoMedia.getCPtr(x), x);
  }

  public VideoMedia get(int i) {
    return new VideoMedia(pjsua2JNI.VideoMediaVector_get(swigCPtr, this, i), false);
  }

  public void set(int i, VideoMedia val) {
    pjsua2JNI.VideoMediaVector_set(swigCPtr, this, i, VideoMedia.getCPtr(val), val);
  }

}
