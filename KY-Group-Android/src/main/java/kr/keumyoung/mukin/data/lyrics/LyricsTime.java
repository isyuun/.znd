package kr.keumyoung.mukin.data.lyrics;

import java.util.ArrayList;

/**
 *  on 02/04/18.
 * Project: KyGroup
 */

public class LyricsTime {
    private String previousLine, nextLine, currentLine, currentWord;
    private long startTick, endMillis, length;
    private int lineNumber, wordIndex;
	private int countDelayTime;
    private ArrayList<String> words;
    private int type = 1;
	private int bpm = 0;
	private int timeOffset = 0;

    private boolean isShown = false, isCountDownItem = false;

    public LyricsTime() {
    }

    public LyricsTime(String previousLine, String nextLine, String currentLine, String currentWord,
    				long startMillis, long endMillis, long length, int lineNumber, int wordIndex, int type, int bpm, int time_offset, ArrayList<String> words) {
        this.previousLine = previousLine;
        this.nextLine = nextLine;
        this.currentLine = currentLine;
        this.currentWord = currentWord;
        this.startTick = startMillis;
        this.endMillis = endMillis;
        this.length = length;
        this.lineNumber = lineNumber;
        this.words = words;
        this.type = type;
        this.wordIndex = wordIndex;
		this.bpm = bpm;
        this.isCountDownItem = false;
		this.countDelayTime = 0;
		this.timeOffset = time_offset;
    }

    public String getPreviousLine() {
        return previousLine;
    }

    public String getNextLine() {
        return nextLine;
    }

    public String getCurrentLine() {
        return currentLine;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public long getStartTick() {
        return startTick;
    }

    public long getEndMillis() {
        return endMillis;
    }

    public long getLength() {
        return length;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setShown() {
        isShown = true;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    public int getType() {
        return type;
    }

    public void setPreviousLine(String previousLine) {
        this.previousLine = previousLine;
    }

    public void setNextLine(String nextLine) {
        this.nextLine = nextLine;
    }

    public void setCurrentLine(String currentLine) {
        this.currentLine = currentLine;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public void setStartTick(long startTick) {
        this.startTick = startTick;
    }

    public void setEndMillis(long endMillis) {
        this.endMillis = endMillis;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setShown(boolean shown) {
        isShown = shown;
    }

    public boolean isCountDownItem() {
        return isCountDownItem;
    }

    public void setCountDownItem(boolean countDownItem) {
        isCountDownItem = countDownItem;
    }

	public void setCountDelayTime(int time) {
        this.countDelayTime = time;
    }

	public int getCountDelayTime() {
        return countDelayTime;
    }

	public void setBpm(int bpm_value) {
        this.bpm = bpm_value;
    }

	public int getBpm() {
        return bpm;
    }

	public void setTimeOffset(int offset_value) {
        this.timeOffset = offset_value;
    }

	public int getTimeOffset() {
        return timeOffset;
    }
}
