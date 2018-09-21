package kr.keumyoung.mukin.util;

/**
 * on 25/04/18.
 * Project: KyGroup
 */

public class PlayerJNI {

    // JAVA wrapper for the native library sample given by KY for .mid file player.
    // Source code of the native project is not included, instead compiled .so files are included in the project

    static {
        System.loadLibrary("sample");
    }

    public native int Initialize(String libraryPath);

    public void FinalizePlayer() {
        try {
            Finalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public native void Finalize();

    public native int SetFile(String filePath);

    public native int Start();

    public native int Stop();

    public native int Seek(int clocks);

    public native int IsPlaying();

    public native int Bounce(String path, int type);

    public native void SetPortSelectionMethod(int value);

    public native int GetPortSelectionMethod();

    public native int GetCurrentClocks();

    public native int GetTotalClocks();

    public native void SetKeyControl(int value);

    public native int GetKeyControl();

    public native void SetSpeedControl(int value);

    public native int GetSpeedControl();

    public native long GetTimePerClock();
}
