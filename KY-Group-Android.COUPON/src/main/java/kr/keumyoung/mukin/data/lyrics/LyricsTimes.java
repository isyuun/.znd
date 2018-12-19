package kr.keumyoung.mukin.data.lyrics;

import android.util.Log;

import java.util.ArrayList;

import kr.keumyoung.mukin.util.MicChecker;

/**
 * on 02/04/18.
 * Project: KyGroup
 */

public class LyricsTimes extends ArrayList<LyricsTime> {
    public int getWithMillis(long currentTick, MicChecker.MIC_CONNECTION_STATES states) {
        //_Log.e("MicChecker", "LyricsTimes.getWithMillis()" + ":" + currentTick + ":" + states);
        long startTick = 0;
        for (int index = 0; index < size(); index++) {
            LyricsTime lyricsTime = get(index);

            startTick = lyricsTime.getStartTick();
            /*if(MicChecker.getInstance().getStates() == MicChecker.MIC_CONNECTION_STATES.BLUETOOTH_MUKIN_K200)
                startTick += ((lyricsTime.getBpm() * 2)) - 30;
            else  if(MicChecker.getInstance().getStates() == MicChecker.MIC_CONNECTION_STATES.BLUETOOTH_OTHERS)
                startTick += lyricsTime.getBpm() - 30;*/
            //if (states == MicChecker.MIC_CONNECTION_STATES.BLUETOOTH_MUKIN)
            //    startTick += ((lyricsTime.getBpm() * 2)) + 60; // + 올릴수록록 빨라짐
            //else if (states == MicChecker.MIC_CONNECTION_STATES.BLUETOOTH_OTHERS)
            //    startTick += lyricsTime.getBpm() + 70;
            //else
            //    startTick += 110;
            float tick = 0;
            switch (states) {
                case BLUETOOTH_MUKIN_K200:
                    tick = ((lyricsTime.getBpm() * 2));
                    break;
                case BLUETOOTH_MUKIN_K200S:
                    tick = ((lyricsTime.getBpm() * 2));
                    break;
                case BLUETOOTH_OTHERS:
                    tick = lyricsTime.getBpm() + 70;
                    break;
                case HEADSET:
                case NONE:
                    tick = 110;
                    break;
            }
            //_Log.e("MicChecker", "LyricsTimes.getWithMillis()" + ":" + currentTick + ":" + states + "-" + lyricsTime.getBpm() + ":" + tick + "-" + startTick + ":" + (startTick + tick));
            startTick += tick;

            if ((startTick >= currentTick - 15) && (startTick <= currentTick + 15)) {
                return index;
            }
        }
        return -1;
    }
}
