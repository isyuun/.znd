package kr.keumyoung.mukin.data.lyrics;

import java.util.ArrayList;

import kr.keumyoung.mukin.util.MicChecker;

/**
 *  on 02/04/18.
 * Project: KyGroup
 */

public class LyricsTimes extends ArrayList<LyricsTime> {
    public int getWithMillis(long currentTick, MicChecker.MIC_CONNECTION_STATES states) {
		long startTick = 0;
        for (int index = 0; index < size(); index++) {
            LyricsTime lyricsTime = get(index);

			startTick = lyricsTime.getStartTick();
         /*   if(MicChecker.getInstance().getStates() == MicChecker.MIC_CONNECTION_STATES.BLUETOOTH_MUKIN)
                startTick += ((lyricsTime.getBpm() * 2)) - 30;
            else  if(MicChecker.getInstance().getStates() == MicChecker.MIC_CONNECTION_STATES.BLUETOOTH_OTHERS)
                startTick += lyricsTime.getBpm() - 30;*/
            if(states == MicChecker.MIC_CONNECTION_STATES.BLUETOOTH_MUKIN)
                startTick += ((lyricsTime.getBpm() * 2)) + 60; // + 올릴수록록 빨라짐
            else if(states == MicChecker.MIC_CONNECTION_STATES.BLUETOOTH_OTHERS)
                startTick += lyricsTime.getBpm() + 70;
            else
                startTick += 100;

            if ((startTick >= currentTick - 5) && (startTick <= currentTick + 5)) {
                return index;
            }
        }
        return -1;
    }
}
