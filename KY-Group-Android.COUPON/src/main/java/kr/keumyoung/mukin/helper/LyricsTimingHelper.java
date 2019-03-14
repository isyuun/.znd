package kr.keumyoung.mukin.helper;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.activity.BaseActivity;
import kr.keumyoung.mukin.activity.PlayerActivity;
import kr.keumyoung.mukin.activity.PreviewActivity;
import kr.keumyoung.mukin.activity._PlayerActivity;
import kr.keumyoung.mukin.data.lyrics.LyricsTime;
import kr.keumyoung.mukin.data.lyrics.LyricsTimes;
import kr.keumyoung.mukin.elements.TwoLineLyricsView;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.MicChecker;

/**
 * on 02/04/18.
 * Project: KyGroup
 */

public class LyricsTimingHelper {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s()", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    @Inject
    Context context;

    @Inject
    FileHelper fileHelper;

    //LyricsView lyricsView;

    TwoLineLyricsView lyricsView;

    File timingFile;

    ScheduledExecutorService service;

    boolean isPlaying = false;
    long currentMillis = 0;

    BaseActivity activity;

    static LyricsTimes lyricsTimes = new LyricsTimes();
    long timeElapsed;
    long startMillis;
    long clockTick;
    public ArrayList<String> lyricLineString = new ArrayList<>();

    public static final int INITIAL_BUFFER = 2000;

    @Inject
    public LyricsTimingHelper() {
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    public ArrayList<String> GetLyricsLineString() {
        return lyricLineString;
    }

    //public Observable<Integer> initiateWithLyricsView(BaseActivity activity, TwoLineLyricsView lyricsView, File timingFile) {
    //    if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
    //    return Observable.create(subscriber -> {
    //        this.lyricsView = lyricsView;
    //        this.timingFile = timingFile;
    //        this.activity = activity;
    //
    //        try {
    //            //dsjung
    //            //subscriber.onNext(R.string.reading_lyrics);
    //            String jsonString = fileHelper.getStringFromFile(timingFile.getAbsolutePath());
    //            JSONArray timingArray = new JSONArray(jsonString);
    //
    //            //dsjung
    //            //subscriber.onNext(R.string.processing_lyrics);
    //            parseTimeArray(timingArray);
    //
    //            subscriber.onComplete();
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            subscriber.onError(e);
    //        }
    //    });
    //}
    //
    //private void parseTimeArray(JSONArray timingArray) throws JSONException {
    //    if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName());
    //    int length = timingArray.length();
    //    lyricsTimes.clear();
    //    for (int index = 0; index < length; index++) {
    //        JSONObject timeObject = timingArray.getJSONObject(index);
    //
    //        int lineNumber = Integer.parseInt(timeObject.getString(Constants.LINE_NUMBER));
    //        String word = timeObject.getString(Constants.WORD);
    //        String nextLine = timeObject.getString(Constants.NEXT_LINE);
    //        long startTime = Long.parseLong(timeObject.getString(Constants.START_TIME));
    //        String previousLine = timeObject.getString(Constants.PREVIOUS_LINE);
    //        long wordLength = Long.parseLong(timeObject.getString(Constants.LENGTH));
    //        long endTime = Long.parseLong(timeObject.getString(Constants.END_TIME));
    //        String currentLine = timeObject.getString(Constants.CURRENT_LINE);
    //        int wordIndex = Integer.parseInt(timeObject.getString(Constants.WORD_INDEX));
    //
    //        JSONArray words = timeObject.getJSONArray(Constants.WORDS);
    //        ArrayList<String> wordList = new ArrayList<>();
    //        for (int wIndex = 0; wIndex < words.length(); wIndex++)
    //            wordList.add(words.getString(wIndex));
    //        int type = 1; //Integer.parseInt(timeObject.getString(Constants.TYPE));
    //
    //        LyricsTime lyricsTime = new LyricsTime(previousLine, nextLine, currentLine, word, startTime, endTime, wordLength, lineNumber, wordIndex, type, 0, 0, wordList);
    //
    //        lyricsTimes.add(lyricsTime);
    //    }
    //
    //    // countdown logic
    //    LyricsTime firstItem = lyricsTimes.get(0);
    //
    //    long bufferTick = (INITIAL_BUFFER * 2) / 4;
    //
    //    long countDownStart = firstItem.getStartTick() - bufferTick;
    //
    //    LyricsTime lyricsTime = new LyricsTime();
    //    lyricsTime.setCurrentLine(firstItem.getCurrentLine());
    //    lyricsTime.setCurrentWord(firstItem.getCurrentWord());
    //    lyricsTime.setStartTick(countDownStart);
    //    lyricsTime.setEndMillis(countDownStart + bufferTick);
    //    lyricsTime.setNextLine(firstItem.getNextLine());
    //    lyricsTime.setPreviousLine("");
    //    lyricsTime.setLineNumber(-1);
    //    lyricsTime.setLength(bufferTick);
    //    lyricsTime.setWordIndex(0);
    //    ArrayList<String> words = new ArrayList<>();
    //    words.add("");
    //    lyricsTime.setWords(words);
    //    lyricsTime.setCountDownItem(true);
    //
    //    lyricsTimes.add(0, lyricsTime);
    //}

    public void initiateWithLyrics(BaseActivity activity, TwoLineLyricsView lyricsView, String filePath) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + filePath);
        this.lyricsView = lyricsView;
        this.activity = activity;

        parseSokLineArray(filePath);
    }

    public void parseSokLineArray(String file_path) {
        if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, getMethodName() + ":" + file_path);

        lyricsTimes.clear();
        //	StringBuffer strBuffer = new StringBuffer();

        try {
            File fl = new File(file_path);
            FileInputStream fin = new FileInputStream(fl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin, Constants.ENCODING_EUC_KR));

            //String encodedString = URLEncoder.encode(searchedString, "euc-kr");
            String encodedString;
            lyricLineString = new ArrayList<>();
            String line = null;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                count += 1;
                if (count <= 6) continue;

                line = line.replace("\r", "");
                line = line.replace("\n", "");
                line = line.replace("  ", " ");
                line = line.replace("   ", " ");
                line = line.replace("    ", " ");
                //encodedString = URLEncoder.encode(line, "euc-kr");
                //System.out.println("##### line str : " + line + " | len : " + line.length());
                //if (BuildConfig.DEBUG) Log.wtf(__CLASSNAME__, "##### line str : " + line + " | len : " + line.length());

                if (line.length() != 0)
                    lyricLineString.add(line);
            }

            reader.close();
        } catch (Exception e) {
        }

    }

    static public void parseSokTimeArray(String word, String next_line, String pre_line, String cur_line, int line_num, int word_idx, int word_len, int start_time, int end_time, int bpm) {
        //String info = "";
        //info += "[" + word + "]";
        //info += "[" + next_line + "]";
        //info += "[" + pre_line + "]";
        //info += "[" + cur_line + "]";
        //info += "[" + line_num + "]";
        //info += "[" + word_idx + "]";
        //info += "[" + word_len + "]";
        //info += "[" + start_time + "]";
        //info += "[" + end_time + "]";
        //info += "[" + bpm + "]";
        //if (BuildConfig.DEBUG) Log.wtf("[parseSokTimeArray]", info);
        //	System.out.println("==== parseSokTimeArray word " + word + " next_line " + next_line + " pre_line " + pre_line + " cur_line " + cur_line + " line_num " + line_num);
        //	System.out.println("#### word_idx " + word_idx + " word_len " + word_len + " start_time " + start_time + " end_time " + end_time + " bpm " + bpm);

        ArrayList<String> wordList = new ArrayList<>();
        //StringTokenizer tokens = new StringTokenizer(cur_line, " " );
        //for( int x = 1; tokens.hasMoreElements(); x++ ){
        //	System.out.println( "문자" + x + " = " + tokens.nextToken() );
        //	wordList.add(tokens.nextToken());
        //}

        for (int i = 0; i < cur_line.length(); i++) {
            char ch = cur_line.charAt(i);
            String cur_line_buf = cur_line;
            String word_str = "";

            if (ch >= '가' && ch <= '힣') {
                word_str = String.valueOf(ch);
            } else if ((cur_line.charAt(i) >= 0x41 && cur_line.charAt(i) <= 0x5A) || (cur_line.charAt(i) >= 0x61 && cur_line.charAt(i) <= 0x7A)) {
                for (int j = i; j < cur_line.length(); j++) {
                    if ((cur_line.charAt(j) >= 0x41 && cur_line.charAt(j) <= 0x5A) || (cur_line.charAt(j) >= 0x61 && cur_line.charAt(j) <= 0x7A)) {
                        ch = cur_line.charAt(j);
                        word_str += String.valueOf(ch);
                        i++;
                    } else {
                        i--;
                        break;
                    }
                }
            } else
                word_str += String.valueOf(ch);

            if (i + 1 < cur_line.length()) {
                if (cur_line.charAt(i + 1) == 0x20) {
                    word_str += " ";
                    i++;
                }
            }

            //	System.out.println("~~~~ word_str : " + word_str + "| cur_line_buf : " + cur_line_buf);
            wordList.add(word_str);
        }

        int type = 1; //Integer.parseInt(timeObject.getString(Constants.TYPE));

        int bpm_time = (int) ((60.0f / (bpm * 120.0f)) * 1000);
        //System.out.println("~~~~ getBpm : " + lyricsTime.getBpm() + " / time_offset : " + time_offset);
        int time_offset = (word_len * bpm_time);

        LyricsTime lyricsTime = new LyricsTime(pre_line, next_line, cur_line, word, start_time, end_time, word_len, line_num, word_idx, type, bpm, time_offset, wordList);

        lyricsTimes.add(lyricsTime);
    }

    static public void parseSokCountTimeArray(int delay) {
        //if (BuildConfig.DEBUG) Log.wtf("[parseSokCountTimeArray]", "" + delay);
        LyricsTime firstItem = lyricsTimes.get(0);

        for (int i = 5; i > 0; i--) {
            long bufferTick = 120 * i;
            long countDownStart = firstItem.getStartTick() - bufferTick;
            //	System.out.println("~~~~ delay : " + delay + " / firstItem.getStartTick() : " + firstItem.getStartTick() + " / bpm : " + firstItem.getBpm());

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
            lyricsTime.setCountDelayTime(delay);
            lyricsTime.setBpm(firstItem.getBpm());

            lyricsTimes.add(0, lyricsTime);
        }
    }

    long jumpTime;
    long microTimePerClock = 4000;
    long MSEC2SEC = 1000000;
    float INTERVAL = 2.0f;

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

        jumpTime = 0;
        if (this.activity instanceof _PlayerActivity) {
            microTimePerClock = ((_PlayerActivity) this.activity).GetTimePerClock();
        }


        for (int index = 0; index < lyricsTimes.size(); index++) {
            //lyricsTimes.get(index).setShown(false);
            LyricsTime lyricsTime = lyricsTimes.get(index);
            lyricsTime.setShown(false);
            //String info = "";
            //info += "[" + lyricsTime.getWordIndex() + "]";
            //info += "[" + lyricsTime.isCountDownItem() + "]";
            //info += "[" + lyricsTime.isShown() + "]";
            //info += "[" + ((_PlayerActivity) this.activity).getPlayerJNI().GetTotalClocks() + "]";
            //info += "[" + lyricsTime.getStartTick() + "]";
            //info += "[" + lyricsTime.getEndMillis() + "]";
            //info += "[" + jumpTime + "]";
            //info += "[" + lyricsTime.getCurrentWord() + "]";
            //info += "[" + lyricsTime.getCurrentLine() + "]";
            //if (BuildConfig.DEBUG) Log.wtf("LyricsTimingHelper", "[LyricsTime]" + info);
            if (jumpTime == 0 && lyricsTime.isCountDownItem()) {
                jumpTime = lyricsTime.getStartTick() * microTimePerClock;
            }
        }

        //jump();   //test
        showJump();
    }

    public void updateView(int currentTick) {
        try {
            if (isPlaying) {
                final long checkTime = (long) (currentTick * microTimePerClock + (INTERVAL * MSEC2SEC));
                //String info = "";
                //info += "[curr:" + checkTime + "]";
                //info += "[jump:" + jumpTime + "]";
                //if (BuildConfig.DEBUG) Log.e("LyricsTimingHelper", "[updateView]" + info);
                if (checkTime < jumpTime) {
                    showJump();
                } else {
                    hideInitStateFrame();
                    hideJump();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println("currentMillis: " + currentMillis);
        //long currentTick = activity.GetCurrentClocks();

        //skip tick

        int originalTick = currentTick;
        //int skipTick = 120;
        //int skipTick = 80;
        int skipTick = 0;

        //initialization to sync tick as per their code
        currentTick = currentTick - skipTick;
        if (currentTick < 0)
            currentTick = 0;

        //System.out.println("syncTick: " + currentTick);

        int currentIndex = lyricsTimes.getWithMillis(currentTick, MicChecker.getInstance().getStates());

        if (currentIndex != -1) {
            LyricsTime currentLyricsTime = lyricsTimes.get(currentIndex);
            if (!currentLyricsTime.isShown()) {
                //long endMillis = Calendar.getInstance().getTimeInMillis();
                //System.out.println("matched CHECK: currenttick" + originalTick + " | synctick: " + currentTick + " | lyricstick: " + currentLyricsTime.getStartTick() + " | word: " + currentLyricsTime.getCurrentWord() + " | time: " + (endMillis - startMillis));
                //System.out.println("MicChecker.getInstance().getStates() " + MicChecker.getInstance().getStates());
                lyricsView.update(currentLyricsTime);
                lyricsTimes.remove(currentIndex);
                currentLyricsTime.setShown();
                lyricsTimes.add(currentIndex, currentLyricsTime);
            }
        }
    }

    public void jump() {
        if (BuildConfig.DEBUG) Log.e("LyricsTimingHelper", "jump()");
        if (isPlaying && this.activity instanceof _PlayerActivity) {
            //isyuun:머누
            ((_PlayerActivity) this.activity).getPlayerJNI().Seek((int) (jumpTime / MSEC2SEC - INTERVAL) * 50);
        }
    }

    private void showJump() {
        if (this.activity instanceof _PlayerActivity) {
            ((_PlayerActivity) this.activity).showJump();
        }
    }

    private void hideJump() {
        if (this.activity instanceof _PlayerActivity) {
            ((_PlayerActivity) this.activity).hideJump();
        }
    }

    private void hideInitStateFrame() {
        if (this.activity instanceof _PlayerActivity) {
            ((_PlayerActivity) this.activity).hideInitStateFrame();
        }
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
                Thread.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
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
