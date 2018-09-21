package kr.keumyoung.mukin.data.lyrics;

import java.util.ArrayList;

/**
 *  on 02/04/18.
 * Project: KyGroup
 */

public class LyricsTimes extends ArrayList<LyricsTime> {
    public int getWithMillis(long currentTick) {
		long startTick = 0;
        for (int index = 0; index < size(); index++) {
            LyricsTime lyricsTime = get(index);

			startTick = lyricsTime.getStartTick();
            if ((startTick >= currentTick - 5) && (startTick <= currentTick + 5)) {
                return index;
            }
        }
        return -1;
    }
}
