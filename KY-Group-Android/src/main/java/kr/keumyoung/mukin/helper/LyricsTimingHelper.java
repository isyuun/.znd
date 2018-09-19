package kr.keumyoung.mukin.helper;

import android.content.Context;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.activity.BaseActivity;
import kr.keumyoung.mukin.activity.PlayerActivity;
import kr.keumyoung.mukin.activity.PreviewActivity;
import kr.keumyoung.mukin.data.lyrics.LyricsTime;
import kr.keumyoung.mukin.data.lyrics.LyricsTimes;
import kr.keumyoung.mukin.elements.TwoLineLyricsView;
import kr.keumyoung.mukin.util.Constants;

/**
 * on 02/04/18.
 * Project: KyGroup
 */

public class LyricsTimingHelper {

    @Inject
    Context context;

    @Inject
    FileHelper fileHelper;

//    LyricsView lyricsView;

    TwoLineLyricsView lyricsView;

    File timingFile;

    ScheduledExecutorService service;

    boolean isPlaying = false;
    long currentMillis = 0;

    BaseActivity activity;

    LyricsTimes lyricsTimes = new LyricsTimes();
    long timeElapsed;
    long startMillis;
    long clockTick;

    public static final int INITIAL_BUFFER = 2000;

    @Inject
    public LyricsTimingHelper() {
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    public Observable<Integer> initiateWithLyricsView(BaseActivity activity, TwoLineLyricsView lyricsView, File timingFile) {
        return Observable.create(subscriber -> {
            this.lyricsView = lyricsView;
            this.timingFile = timingFile;
            this.activity = activity;

            try {
                subscriber.onNext(R.string.reading_lyrics);
                String jsonString = fileHelper.getStringFromFile(timingFile.getAbsolutePath());
                JSONArray timingArray = new JSONArray(jsonString);

                subscriber.onNext(R.string.processing_lyrics);
                parseTimeArray(timingArray);

                subscriber.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

    private void parseTimeArray(JSONArray timingArray) throws JSONException {
        int length = timingArray.length();
        lyricsTimes.clear();
        for (int index = 0; index < length; index++) {
            JSONObject timeObject = timingArray.getJSONObject(index);

            int lineNumber = Integer.parseInt(timeObject.getString(Constants.LINE_NUMBER));
            String word = timeObject.getString(Constants.WORD);
            String nextLine = timeObject.getString(Constants.NEXT_LINE);
            long startTime = Long.parseLong(timeObject.getString(Constants.START_TIME));
            String previousLine = timeObject.getString(Constants.PREVIOUS_LINE);
            long wordLength = Long.parseLong(timeObject.getString(Constants.LENGTH));
            long endTime = Long.parseLong(timeObject.getString(Constants.END_TIME));
            String currentLine = timeObject.getString(Constants.CURRENT_LINE);
            int wordIndex = Integer.parseInt(timeObject.getString(Constants.WORD_INDEX));

            JSONArray words = timeObject.getJSONArray(Constants.WORDS);
            ArrayList<String> wordList = new ArrayList<>();
            for (int wIndex = 0; wIndex < words.length(); wIndex++)
                wordList.add(words.getString(wIndex));
            int type = 1; //Integer.parseInt(timeObject.getString(Constants.TYPE));

            LyricsTime lyricsTime = new LyricsTime(previousLine, nextLine, currentLine, word, startTime, endTime, wordLength, lineNumber, wordIndex, type, wordList);

            lyricsTimes.add(lyricsTime);
        }

        // countdown logic
        LyricsTime firstItem = lyricsTimes.get(0);

        long bufferTick = (INITIAL_BUFFER * 2) / 4;

        long countDownStart = firstItem.getStartTick() - bufferTick;

        LyricsTime lyricsTime = new LyricsTime();
        lyricsTime.setCurrentLine(firstItem.getCurrentLine());
        lyricsTime.setCurrentWord(firstItem.getCurrentWord());
        lyricsTime.setStartTick(countDownStart);
        lyricsTime.setEndMillis(countDownStart + bufferTick);
        lyricsTime.setNextLine(firstItem.getNextLine());
        lyricsTime.setPreviousLine("");
        lyricsTime.setLineNumber(-1);
        lyricsTime.setLength(bufferTick);
        lyricsTime.setWordIndex(0);
        ArrayList<String> words = new ArrayList<>();
        words.add("");
        lyricsTime.setWords(words);
        lyricsTime.setCountDownItem(true);

        lyricsTimes.add(0, lyricsTime);
    }

    public void start() {
        timeElapsed = 0;
        clockTick = 0;
        lyricsView.setVisibility(View.VISIBLE);
        lyricsView.clear();
        if (service == null) {
            service = Executors.newScheduledThreadPool(1);
            service.scheduleWithFixedDelay(getRunnable(), 0, 1, TimeUnit.MILLISECONDS);
            resume();
            startMillis = Calendar.getInstance().getTimeInMillis();
        }
        for (int index = 0; index < lyricsTimes.size(); index++)
            lyricsTimes.get(index).setShown(false);
    }

    private Runnable getRunnable() {
        return () -> {
            try {
                clockTick += 1;

                if (isPlaying) {
                    activity.runOnUiThread(() -> {
                        int currentTick = 0;
                        if (activity instanceof PlayerActivity && ((PlayerActivity) activity).getPlayerJNI() != null) {
                            currentTick = ((PlayerActivity) activity).getPlayerJNI().GetCurrentClocks();
                        } else if (activity instanceof PreviewActivity && ((PreviewActivity) activity).getPlayerJNI() != null) {
                            currentTick = ((PreviewActivity) activity).getPlayerJNI().GetCurrentClocks();
                        }
                        updateView(currentTick);
                    });
                    timeElapsed += 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public void updateView(int currentTick) {
//        System.out.println("currentMillis: " + currentMillis);
//        long currentTick = activity.GetCurrentClocks();

        // skip tick

        int originalTick = currentTick;
        int skipTick = 120;

        // initialization to sync tick as per their code
        currentTick = currentTick - skipTick;

//        System.out.println("syncTick: " + currentTick);

        int currentIndex = lyricsTimes.getWithMillis(currentTick);

        if (currentIndex != -1) {
            LyricsTime currentLyricsTime = lyricsTimes.get(currentIndex);
            if (!currentLyricsTime.isShown()) {
                long endMillis = Calendar.getInstance().getTimeInMillis();
                System.out.println("matched CHECK: currenttick" + originalTick + " | synctick: " + currentTick + " | lyricstick: " + currentLyricsTime.getStartTick() + " | word: " + currentLyricsTime.getCurrentWord() + " | time: " + (endMillis - startMillis));
                lyricsView.update(currentLyricsTime);
                lyricsTimes.remove(currentIndex);
                currentLyricsTime.setShown();
                lyricsTimes.add(currentIndex, currentLyricsTime);
            }

        }
    }

    public void stop() {
        if (service != null) service.shutdown();
        isPlaying = false;
        currentMillis = 0;
        service = null;
    }

    public void pause() {
        isPlaying = false;
    }

    public void resume() {
        isPlaying = true;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void remove() {
        lyricsView.setVisibility(View.INVISIBLE);
        if (service != null) service.shutdownNow();
        service = null;
    }
}
