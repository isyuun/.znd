package kr.keumyoung.mukin.data.lyrics;

import java.util.ArrayList;

/**
 *  on 02/04/18.
 * Project: KyGroup
 */

public class LyricsTimes extends ArrayList<LyricsTime> {
    public int getWithMillis(long currentTick) {
        for (int index = 0; index < size(); index++) {
            LyricsTime lyricsTime = get(index);

            if ((lyricsTime.getStartTick() >= currentTick - 5) && (lyricsTime.getStartTick() <= currentTick + 5)) {
                return index;
            }
        }
        return -1;
    }
}
