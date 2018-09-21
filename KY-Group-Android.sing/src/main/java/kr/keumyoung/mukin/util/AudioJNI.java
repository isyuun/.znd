package kr.keumyoung.mukin.util;

public class AudioJNI {

    // JAVA wrapper for the native library AudioEffect.
    // Source code of the native project is not included, instead compiled .so files are included in the project

    static {
        System.loadLibrary("AudioEffect");
    }

    boolean isFinalized = false;

    public native void StartAudio(int samplerate, int buffersize, String tempPath, String destPath);

    public native void StopAudio();

    public native void onForeground();

    public native void onBackground();

    public native void setReverb(int isReverb);

    public native void setEcho(int isEcho);

    public native void setDelay(int isDelay);

    public void FinalizeAudio() {
        try {
            Finalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public native void Finalize();

    public native void pause();

    public native void restart();
}
