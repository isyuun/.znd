package kr.keumyoung.mukin.data;

/**
 *  on 28/03/18.
 * Project: KyGroup
 */
// unused
public class LyricsObject {
    private String prevLine, currentLine, nextLine;
    private long animTime;

    public LyricsObject(String prevLine, String currentLine, String nextLine, long animTime) {
        this.prevLine = prevLine;
        this.currentLine = currentLine;
        this.nextLine = nextLine;
        this.animTime = animTime;
    }

    public String getPrevLine() {
        return prevLine;
    }

    public String getCurrentLine() {
        return currentLine;
    }

    public String getNextLine() {
        return nextLine;
    }

    public long getAnimTime() {
        return animTime;
    }
}
