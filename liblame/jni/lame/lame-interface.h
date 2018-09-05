/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class net_sourceforge_lame_Lame */

#ifndef _Included_net_sourceforge_lame_Lame
#define _Included_net_sourceforge_lame_Lame
#ifdef __cplusplus
extern "C" {
#endif
#undef net_sourceforge_lame_Lame_MP3_BUFFER_SIZE
#define net_sourceforge_lame_Lame_MP3_BUFFER_SIZE 1024L
#undef net_sourceforge_lame_Lame_LAME_PRESET_DEFAULT
#define net_sourceforge_lame_Lame_LAME_PRESET_DEFAULT 0L
#undef net_sourceforge_lame_Lame_LAME_PRESET_MEDIUM
#define net_sourceforge_lame_Lame_LAME_PRESET_MEDIUM 1L
#undef net_sourceforge_lame_Lame_LAME_PRESET_STANDARD
#define net_sourceforge_lame_Lame_LAME_PRESET_STANDARD 2L
#undef net_sourceforge_lame_Lame_LAME_PRESET_EXTREME
#define net_sourceforge_lame_Lame_LAME_PRESET_EXTREME 3L
/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    initializeEncoder
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_initializeEncoder
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    setEncoderPreset
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_net_sourceforge_lame_Lame_setEncoderPreset
  (JNIEnv *, jclass, jint);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    encode
 * Signature: ([S[SI[BI)I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_encode
  (JNIEnv *, jclass, jshortArray, jshortArray, jint, jbyteArray, jint);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    flushEncoder
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_flushEncoder
  (JNIEnv *, jclass, jbyteArray, jint);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    closeEncoder
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_closeEncoder
  (JNIEnv *, jclass);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    initializeDecoder
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_initializeDecoder
  (JNIEnv *, jclass);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    getDecoderSampleRate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_getDecoderSampleRate
  (JNIEnv *, jclass);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    getDecoderChannels
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_getDecoderChannels
  (JNIEnv *, jclass);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    getDecoderDelay
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_getDecoderDelay
  (JNIEnv *, jclass);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    getDecoderPadding
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_getDecoderPadding
  (JNIEnv *, jclass);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    getDecoderTotalFrames
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_getDecoderTotalFrames
  (JNIEnv *, jclass);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    getDecoderFrameSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_getDecoderFrameSize
  (JNIEnv *, jclass);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    getDecoderBitrate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_getDecoderBitrate
  (JNIEnv *, jclass);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    nativeConfigureDecoder
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_nativeConfigureDecoder
  (JNIEnv *, jclass, jbyteArray, jint);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    nativeDecodeFrame
 * Signature: ([BI[S[S)I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_nativeDecodeFrame
  (JNIEnv *, jclass, jbyteArray, jint, jshortArray, jshortArray);

/*
 * Class:     net_sourceforge_lame_Lame
 * Method:    closeDecoder
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sourceforge_lame_Lame_closeDecoder
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
