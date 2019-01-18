//
// Created by hideo on 7/13/16.
//

#include <assert.h>
#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <android/log.h>

#include "bsmp.h"
#include "bssynth_key.h"
#include "bsmp_ky.h"

typedef struct
{
    JNIEnv* env;
    jobject thiz;
} JNI_INFOMATION;

static BSMP_HANDLE handle = NULL;
static BSMP_FUNC *api = NULL;

static int clockCounter;
static int clocks;
static int beats;
static int measures;
static int numerator;
static int denominator;
static int metronomeClick;
static float tempo;
static float timePerClock;
static float currentTime;
static float totalTime;

static void callback (BSMP_HANDLE handle, BSMP_CALLBACK_TYPE type, void *data, void *user)
{
    switch (type) {
        case BSMP_CALLBACK_TYPE_START:
            __android_log_print (ANDROID_LOG_INFO, "JNI", "Started");
            break;
        case BSMP_CALLBACK_TYPE_STOP:
            __android_log_print (ANDROID_LOG_INFO, "JNI", "Stopped");
            break;
        case BSMP_CALLBACK_TYPE_SEEK:
            __android_log_print (ANDROID_LOG_INFO, "JNI", "Seeked");
            clocks = clockCounter = beats = measures = 0;
            numerator = 4;
            denominator = 4;
            metronomeClick = 24;
            tempo = 120.f;
            timePerClock = 500000.f / (24.f * 1000000.f);
            currentTime = 0.f;
            break;

        case BSMP_CALLBACK_TYPE_CLOCK:
            clocks++;
            if ((++clockCounter % metronomeClick) == 0) {
                clockCounter = 0;
                if (numerator <= ++beats) {
                    beats = 0;
                    measures++;
                }
         //       __android_log_print (ANDROID_LOG_INFO, "JNI", "beat = %03d|%02d (%d clocks)", measures + 1, beats + 1, clocks);
            }
            currentTime += timePerClock;
            break;
        case BSMP_CALLBACK_TYPE_TEMPO:
            __android_log_print (ANDROID_LOG_INFO, "JNI", "tempo = %lu", *(unsigned long *) data);
            tempo = 60 * 1000 * 1000 / (float) *(unsigned long *) data;
            timePerClock = (float) (*(unsigned long *) data) / (24.f * 1000000.f);
            break;
        case BSMP_CALLBACK_TYPE_TIME_SIGNATURE:
        {
            unsigned long params = *(unsigned long *) data;
            __android_log_print (ANDROID_LOG_INFO, "JNI", "time signature = 0x%08lX", params);
            numerator = (int) ((params >> 24) & 0x000000FF);
            denominator = 1 << (int) ((params >> 16) & 0x000000FF);
            metronomeClick = (int) ((params >> 8) & 0x000000FF);
            clockCounter = 0;
        }
            break;
        default:
            break;
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_Initialize (JNIEnv* env, jobject thiz, jstring libraryPath)
{
     BSMP_ERR err = BSMP_OK;

    api = bsmpLoad ();

    if (err == BSMP_OK) {
        // initialize
        __android_log_print (ANDROID_LOG_INFO, "JNI", "Initialize");
        const char *library = env->GetStringUTFChars (libraryPath, NULL);
        JNI_INFOMATION JNIInfo = {env, thiz};
        err = api->initializeWithSoundLib (&handle, callback, NULL, library, &JNIInfo, bssynth_key);
        env->ReleaseStringUTFChars (libraryPath, library);
    }

    if (err == BSMP_OK) {
        // reverb on
        __android_log_print (ANDROID_LOG_INFO, "JNI", "Set reverb = on");
        int value = 1;
        err = api->ctrl (handle, BSMP_CTRL_SET_REVERB, &value, sizeof (value));
    }

    if (err == BSMP_OK) {
        // chorus on
        __android_log_print (ANDROID_LOG_INFO, "JNI", "Set chorus = on");
        int value = 1;
        err = api->ctrl (handle, BSMP_CTRL_SET_CHORUS, &value, sizeof (value));
    }

    if (err == BSMP_OK) {
        // sample rate
        __android_log_print (ANDROID_LOG_INFO, "JNI", "Set sample rate to 44100");
        int value = 44100;
        err = api->ctrl (handle, BSMP_CTRL_SET_SAMPLE_RATE, &value, sizeof (value));
    }

#if 0
	if (err == BSMP_OK) {
		int value = 127;
		__android_log_print(ANDROID_LOG_INFO, "JNI", "poly = 64");
		err = api->ctrl(handle, BSMP_CTRL_SET_POLY, &value, sizeof(value));
	}
#endif

	if (err == BSMP_OK) {
		// set port selection method (SC-88 port A / B)
		int value = BSMP_PORT_SELECTION_METHOD_K;
		err = api->ctrl(handle, BSMP_CTRL_SET_PORT_SELECTION_METHOD, &value, sizeof(value));
	}

#if 0
	if (err == BSMP_OK) {
		float gain = 6.0; // default is 4.0
		err = api->ctrl (handle, BSSE_CTRL_SET_TOTAL_GAIN, &gain, sizeof (gain));
		__android_log_print(ANDROID_LOG_INFO, "JNI", "BSSE_CTRL_SET_TOTAL_GAIN %d", err);
	}
#endif

    if (err == BSMP_OK) {
        // open wave output device
        __android_log_print (ANDROID_LOG_INFO, "JNI", "Open");
        err = api->open (handle, NULL, NULL);
    }

#if 0
	if (err == BSMP_OK) {
		int value = 1;
	    err = api->ctrl (handle, BSMP_CTRL_KY_SET_RHYTHM_CHANGE_TYPE, &value, sizeof (value));
		__android_log_print(ANDROID_LOG_INFO, "JNI", "BSMP_CTRL_KY_SET_RHYTHM_CHANGE_TYPE (%d), err %d", value, err);
	}


	if (err == BSMP_OK) {
		__android_log_print(ANDROID_LOG_INFO, "cocos2d-x debug info", "Master Volume = 127");
		unsigned char value = 127;
		err = api->ctrl(handle, BSSE_CTRL_SET_MASTER_VOLUME, &value, sizeof(value));
	}
#endif
	
    return (jint) err;
}

extern "C" JNIEXPORT void JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_Finalize (JNIEnv* env, jobject thiz)
{
    if (api && handle) {
        api->close (handle);
        api->exit (handle);
    }
    api = NULL;
    handle = NULL;
}

extern "C" JNIEXPORT jint JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_SetFile (JNIEnv* env, jobject thiz, jstring filePath)
{
    __android_log_print (ANDROID_LOG_INFO, "JNI", "SetFile");

    if (api && handle) {
        BSMP_ERR err = BSMP_OK;

        if (err == BSMP_OK) {
            const char *file = env->GetStringUTFChars (filePath, 0);
            __android_log_print (ANDROID_LOG_INFO, "JNI", "SetFile %s", file);
            err = api->setFile (handle, file);
            env->ReleaseStringUTFChars (filePath, file);
        }

        if (err == BSMP_OK) {
            clocks = clockCounter = beats = measures = 0;
            numerator = 4;
            denominator = 4;
            metronomeClick = 24;
            tempo = 120.f;
            timePerClock = 500000.f / (24.f * 1000000.f);
            currentTime = 0.f;

            unsigned long time;
            if (api->getFileInfo (handle, NULL, NULL, NULL, &time) == BSMP_OK) {
                totalTime = (float) time;
            }
        }

        return err;
    }

    return -1;
}

extern "C" JNIEXPORT jint JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_IsPlaying (JNIEnv* env, jobject thiz)
{
    if (api && handle) {
        return api->isPlaying (handle);
    }

    return 0;
}

extern "C" JNIEXPORT jint JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_Start (JNIEnv* env, jobject thiz)
{
    __android_log_print (ANDROID_LOG_INFO, "JNI", "Start");

    if (api && handle) {
        BSMP_ERR err = BSMP_OK;

        if (err == BSMP_OK) {
            if (api->isPlaying (handle) == 0) {
                __android_log_print (ANDROID_LOG_INFO, "JNI", "Start");
                err = api->start (handle);
            }
            else {
                __android_log_print (ANDROID_LOG_INFO, "JNI", "Already Started");
            }
        }

        return err;
    }

    return -1;
}

extern "C" JNIEXPORT jint JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_Stop (JNIEnv* env, jobject thiz)
{
    __android_log_print (ANDROID_LOG_INFO, "JNI", "Stop");

    if (api && handle) {
        BSMP_ERR err = BSMP_OK;

        if (err == BSMP_OK) {
            if (api->isPlaying (handle)) {
                __android_log_print (ANDROID_LOG_INFO, "JNI", "Stop");
                err = api->stop (handle);
            }
            else {
                __android_log_print (ANDROID_LOG_INFO, "JNI", "Already Stopped");
            }
        }

        return err;
    }

    return -1;
}

extern "C" JNIEXPORT jint JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_Seek (JNIEnv* env, jobject thiz, int clock)
{
    __android_log_print (ANDROID_LOG_INFO, "JNI", "Seek (%d[clocks]", clock);

    unsigned long value = 0UL;
    unsigned short division;
    api->getFileInfo (handle, NULL, &division, NULL, NULL);
    if (division > 0) value = clock * division / 24;

    clocks = clockCounter = beats = measures = 0;
    numerator = 4;
    denominator = 4;
    metronomeClick = 24;
    tempo = 120.f;
    timePerClock = 500000.f / (24.f * 1000000.f);
    currentTime = 0.f;

    api->seek (handle, value);

    return 1;
}

static int callback_bounce (int percent, void *user) {
    __android_log_print (ANDROID_LOG_INFO, "JNI", "Bounce (%d%%)", percent);
    return 0;
}

extern "C" JNIEXPORT jint JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_Bounce (JNIEnv* env, jobject thiz, jstring path, jint type)
{
    const char *file = env->GetStringUTFChars (path, 0);
    __android_log_print (ANDROID_LOG_INFO, "JNI", "Bounce (%s)", file);

    if (api && handle) {
        BSMP_ERR err = BSMP_OK;

        if (err == BSMP_OK) {
            err = api->bounce (handle, file, (BSMP_WAVE_FILE) type, callback_bounce, NULL);
        }

        return err;
    }

    return -1;
}

extern "C" JNIEXPORT jint JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_GetCurrentClocks (JNIEnv* env, jobject thiz)
{
	int value = 0;
	if (api && handle)
	{
		BSMP_ERR err;
		err = api->ctrl(handle, BSMP_CTRL_GET_TICK, &value, sizeof(value));
		if (err == BSMP_OK)
		{
	//		__android_log_print(ANDROID_LOG_INFO, "JNI", "BSMP_CTRL_GET_TICK %d", value);
		}
		else
		{
	//		__android_log_print(ANDROID_LOG_INFO, "JNI", "BSMP_CTRL_GET_TICK Err %d", value);
		}
	}

	return value;
//	return clocks;
}

extern "C" JNIEXPORT jfloat JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_GetCurrentTime(JNIEnv* env, jobject thiz)
{
	return currentTime;
}

extern "C" JNIEXPORT jfloat JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_GetTotalTime(JNIEnv* env, jobject thiz)
{
	return totalTime;
}

extern "C" JNIEXPORT jint JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_GetTotalClocks (JNIEnv* env, jobject thiz)
{
    unsigned short division;
    unsigned long value = 0UL;

    api->getFileInfo (handle, NULL, &division, &value, NULL);
//    if (division > 0) value = value * 24 / division;

    return (int) value;
}

extern "C" JNIEXPORT void JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_SetKeyControl (JNIEnv* env, jobject thiz, jint value)
{
    if (api && handle) {
        int v = value;
        api->ctrl (handle, BSMP_CTRL_SET_MASTER_KEY, &v, sizeof (int));
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_GetKeyControl (JNIEnv* env, jobject thiz)
{
    if (api && handle) {
        int v;
        api->ctrl (handle, BSMP_CTRL_GET_MASTER_KEY, &v, sizeof (int));
        return (jint) v;
    }

    return 0;
}

extern "C" JNIEXPORT void JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_SetSpeedControl (JNIEnv* env, jobject thiz, jint value)
{
    if (api && handle) {
        int v = value;
        api->ctrl (handle, BSMP_CTRL_SET_SPEED, &v, sizeof (int));
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_GetSpeedControl (JNIEnv* env, jobject thiz)
{
    if (api && handle) {
        int v;
        api->ctrl (handle, BSMP_CTRL_GET_SPEED, &v, sizeof (int));
        return (jint) v;
    }

    return 0;
}

extern "C" JNIEXPORT jint JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_AddRhythmChange (JNIEnv* env, jobject thiz, jstring source, jstring destination, jstring patternsFolder)
{
    const char *src = env->GetStringUTFChars (source, 0);
    const char *dst = env->GetStringUTFChars (destination, 0);
    const char *ptn = env->GetStringUTFChars (patternsFolder, 0);

    BSMP_ERR err = bsmpKYAddRhythmChange (src, dst, ptn);

    env->ReleaseStringUTFChars (source, src);
    env->ReleaseStringUTFChars (destination, dst);
    env->ReleaseStringUTFChars (patternsFolder, ptn);
    return (jint) err;
}

extern "C" JNIEXPORT void JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_SetRhythmChangeType (JNIEnv* env, jobject thiz, jint value)
{
    if (api && handle) {
        int v = value;
        api->ctrl (handle, BSMP_CTRL_KY_SET_RHYTHM_CHANGE_TYPE, &v, sizeof (int));
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_GetRhythmChangeType (JNIEnv* env, jobject thiz)
{
    if (api && handle) {
        int v;
        api->ctrl (handle, BSMP_CTRL_KY_GET_RHYTHM_CHANGE_TYPE, &v, sizeof (int));
        return (jint) v;
    }

    return 0;
}

extern "C" JNIEXPORT void JNICALL
Java_kr_keumyoung_mukin_util_PlayerJNI_SetPortSelectionMethod (JNIEnv* env, jobject thiz, int value)
{
  	if (api && handle) {
        int v = value, err;
		err = api->ctrl (handle, BSMP_CTRL_SET_PORT_SELECTION_METHOD, &v, sizeof (int));
    }
}

