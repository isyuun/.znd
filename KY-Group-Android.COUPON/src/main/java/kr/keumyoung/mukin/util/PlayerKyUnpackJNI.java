package kr.keumyoung.mukin.util;

import java.util.ArrayList;

public class PlayerKyUnpackJNI {

    static {
        System.loadLibrary("kyunpack");
    }

	public native void Init(String kyPath, String savePath, int songNum);

	public native void LyricSokParse(ArrayList<String> list);

}
