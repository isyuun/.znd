package kr.keumyoung.mukin.helper;

import android.content.Context;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.elements.LyricsView;
import kr.keumyoung.mukin.util.Constants;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 *  on 08/03/18.
 * Project: KyGroup
 */
// unused
public class LyricsHelper {

    @Inject
    FileHelper fileHelper;

    @Inject
    Context context;

    private File sokFile, timingJson;
    private JSONArray timingArray;

    private int totalIndex, totalLineIndex;

    private int currentIndex, currentLineIndex, prevLineIndex;

    private long microTimePerClock, totalClock;

    private LyricsView lyricsView;

    private ArrayList<String> lyricsLines = new ArrayList<>();

    private long microTimePerWord;

    private String language;

    private Map<Integer, Long> lineTimeMap = new HashMap<>();
    private Map<Integer, Integer> lineWordCount = new HashMap<>();
    private Map<Integer, Map<Integer, Long>> characterTimeMap = new HashMap<>(); // line index => <character index , micro time>

    @Inject
    public LyricsHelper() {
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    public void initiateWithSokFile(File sokFile, File timingJson, String language) {
        try {
            this.sokFile = sokFile;
            this.language = language;
            this.timingJson = timingJson;

            lyricsLines = fileHelper.getStringArray(sokFile.getAbsolutePath());
            timingArray = new JSONArray(fileHelper.getStringFromFile(timingJson.getAbsolutePath()));

            totalLineIndex = lyricsLines.size();
            currentLineIndex = 0;

            totalIndex = 0;
            currentIndex = 0;

            if (language.equalsIgnoreCase(Constants.EN)) {
                for (int lineIndex = 0; lineIndex < lyricsLines.size(); lineIndex++) {
                    String lyricsLine = lyricsLines.get(lineIndex);
                    int count = getWordsFromLine(lyricsLine);
                    totalIndex += count;
                    lineWordCount.put(lineIndex, count);
                }
            } else if (language.equalsIgnoreCase(Constants.KR)) {
                for (int lineIndex = 0; lineIndex < lyricsLines.size(); lineIndex++) {
                    String lyricsLine = lyricsLines.get(lineIndex);
                    int count = getWordsFromLine(lyricsLine);
                    totalIndex += count;
                    lineWordCount.put(lineIndex, count);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getWordsFromLine(String lyricsLine) {
        if (language.equalsIgnoreCase(Constants.EN)) {
            String[] words = lyricsLine.split(" ");
            int count = 0;
            for (String word : words) {
                if (!word.equalsIgnoreCase("")) {
                    count += 1;
                }
            }

            for (int index = 0; index < lyricsLine.length(); index++) {
                int charAscii = (int) lyricsLine.toLowerCase().charAt(index);
                if ((charAscii < 97 || charAscii >= 122) && charAscii != 32) {
                    count += 1;
//                    System.out.println("charAscii: " + charAscii);
                }
            }

            return count;
        } else if (language.equalsIgnoreCase(Constants.KR)) {
            int count = 0;

            for (int index = 0; index < lyricsLine.length(); index++) {
                if (((int) lyricsLine.charAt(index)) != 32) count += 1;
            }

            return count;
        }

        return 0;
    }

    public int getTotalIndex() {
        return totalIndex;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void initiateScroller(long totalClock, long microTimePerClock) {
        this.microTimePerClock = microTimePerClock;
        this.totalClock = totalClock;

        long totalTime = totalClock * microTimePerClock;
        microTimePerWord = totalTime / totalIndex;

//        System.out.println("microTimePerWord: " + microTimePerWord);

        for (int lineIndex = 0; lineIndex < totalLineIndex; lineIndex++) {
            long currentMicroTime = lineWordCount.get(lineIndex) * microTimePerWord;
            String lyricsLine = lyricsLines.get(lineIndex);

            if (lineIndex == 0)
                lineTimeMap.put(lineIndex, currentMicroTime);
            else
                lineTimeMap.put(lineIndex, lineTimeMap.get(lineIndex - 1) + currentMicroTime);

            Map<Integer, Long> characterMap = new HashMap<>();
            long characterTimeOffset = lineTimeMap.get(lineIndex);
            String line = lyricsLines.get(lineIndex);
            int characterCount = getEffectiveCharacterCount(line);
            long timePerCharacter = currentMicroTime / characterCount;

            for (int characterIndex = 0; characterIndex < line.length(); characterIndex++) {
                if (((int) lyricsLine.charAt(characterIndex)) != 32)
                    characterTimeOffset += timePerCharacter;
                characterMap.put(characterIndex, characterTimeOffset);
            }

            characterTimeMap.put(lineIndex, characterMap);
        }
    }

    private int getEffectiveCharacterCount(String lyricsLine) {
        int count = 0;

        for (int index = 0; index < lyricsLine.length(); index++) {
            if (((int) lyricsLine.charAt(index)) != 32) count += 1;
        }

        return count;
    }

    public void initiateView(LyricsView songLyricsView) {
        this.lyricsView = songLyricsView;
    }

    public void updateView(int currentPosition) {
        long currentMicroSecond = currentPosition * microTimePerClock;

        long currentLineMicroSec, nextLineMicroSec;

        for (int lineIndex = currentLineIndex; lineIndex < totalLineIndex; lineIndex++) {
            currentLineMicroSec = lineTimeMap.get(lineIndex);
            nextLineMicroSec = lineIndex == totalLineIndex - 1 ? lineTimeMap.get(lineIndex) : lineTimeMap.get(lineIndex + 1);

            if (currentMicroSecond >= currentLineMicroSec && currentMicroSecond <= nextLineMicroSec) {
                currentLineIndex = lineIndex;
                break;
            }
        }

//        long animTime = microTimePerWord * lineWordCount.get(currentLineIndex);

        prevLineIndex = currentLineIndex;
    }

    public void pause() {
        lyricsView.pause();
    }

    public void resume() {
        lyricsView.resume();
    }
}
