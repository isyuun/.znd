package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import java.io.File;

import kr.keumyoung.karaoke.api._Const;
import kr.keumyoung.karaoke.api._Download;
import kr.kymedia.karaoke.util.TextUtil;

public class PlayView6 extends PlayView5 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    private String _toString() {
        return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
    }

    public PlayView6(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PlayView6(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PlayView6(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayView6(Context context) {
        super(context);
    }

    public void open(String song_id) {
        path_sd = getApplicationContext().getExternalFilesDir(null) + "";
        start();
        down(song_id);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public boolean load(String path) {
        return super.load(path);
    }

    /**
     * SD카드경로
     */
    String path_sd;
    /**
     * 반주용MP3주소
     */
    protected String url_skym;
    /**
     * 가사용skym주소
     */
    protected String url_lyric;
    /***
     * 반주중곡번호
     */
    public String song_id;
    /**
     * <pre>
     * 가사파일만받는거다~~~
     * </pre>
     */
    protected _Download download;

    /**
     * <pre>
     (구)KYMedia 서버
     * mp3 : http://resource.kymedia.kr/ky/mp/01/00101.mp3
     * 가사 : http://resource.kymedia.kr/ky/md/01/00101.mid
     * skym : http://resource.kymedia.kr/ky/skym/01/00101.skym
     (현) 금영그룹서버
     * 211.236.190.103
     * 금영서버: http://211.236.190.103:8080/svc_media/mmp3/08888.mp3
     * 사이월드: http://cyms.chorus.co.kr/cykara_dl2.asp?song_id=08888
     * 신규서버: http://cyms.chorus.co.kr/.skym.asp?song_id=08888
     * </pre>
     */
    private void down(String song_id) {
        this.song_id = song_id;
        this.url_lyric = "http://cyms.chorus.co.kr/.skym.asp?song_id=" + song_id;
        this.url_skym = "http://211.236.190.103:8080/svc_media/mmp3/" + song_id + ".mp3";
        if (BuildConfig.DEBUG) Log.e(_toString(), getMethodName() + "[ST]" + this.song_id + ":" + this.url_lyric + ":" + this.url_skym);

        //if (_KP_1016 == null) {
        //    ShowMessageNotResponse(getString(kr.kymedia.kykaraoke.tv.R.string.common_info), getString(kr.kymedia.kykaraoke.tv.R.string.message_error_network_timeout));
        //    return;
        //}
        //
        //HideMenu(getMethodName());
        //
        //if (!("00000").equals(_KP_1016.result_code)) {
        //    stopLoading(getMethodName());
        //    if (!TextUtil.isEmpty(_KP_1016.result_message)) {
        //        ShowMessageOk(CLOSE_OK, getString(kr.kymedia.kykaraoke.tv.R.string.common_info), _KP_1016.result_message);
        //    }
        //    return;
        //}

        //this.url_lyric = _KP_1016.url_lyric;
        //this.url_skym = "_KP_1016.url_skym;
        //this.video_url = _KP_1016.video_url;
        //this.type = _KP_1016.type;

        download = new _Download(handlerKP);
        download.setFileName("sing.skym");
        download.setType(REQUEST_FILE_SONG);
        download.setMp3(url_skym);
        download.setLyc(url_lyric);
        download.setNewPath(path_sd);
        download.start();
        //download.setListener(this);
    }

    final Handler handlerKP = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (BuildConfig.DEBUG) Log.e(_toString(), "handleMessage()" + msg);
            KP(msg);
        }
    };

    protected void KP(Message msg) {
        if (BuildConfig.DEBUG) Log.e(_toString(), getMethodName() + _Const.COMPLETE_KP.get(msg.getData().getInt("state")) + ":" + msg);

        int state = msg.getData().getInt("state");

        // 반주곡스트리밍처리
        switch (state) {
            case COMPLETE_SONG_PLAY: // 반주곡 파일 다운로드
                //stopTaskShowMessageNotResponse();
                start();
                break;
            case COMPLETE_DOWN_SONG: // 반주곡 시작
                //stopTaskShowMessageNotResponse();
                open();
                break;
            case COMPLETE_LISTEN_SONG: // 녹음곡 재생 정보
            case COMPLETE_LISTEN_OTHER_SONG:
                //stopTaskShowMessageNotResponse();
                //downListen(state);
                break;
            case COMPLETE_DOWN_LISTEN: // 녹음곡 파일 다운로드
            case COMPLETE_DOWN_LISTEN_OTHER:
                //stopTaskShowMessageNotResponse();
                ///startListen(state);
                break;
            default:
                //try {
                //    super.KP(msg);
                //} catch (Exception e) {
                //    if (BuildConfig.DEBUG) _LOG(getMethodName(), e);
                //}
                break;
        }
    }

    @Override
    public void open() {
        this.url_lyric = "http://cyms.chorus.co.kr/.skym.asp?song_id=" + this.song_id;
        this.url_skym = "http://211.236.190.103:8080/svc_media/mmp3/" + this.song_id + ".mp3";
        if (BuildConfig.DEBUG) Log.e(_toString(), getMethodName() + "[ST]" + this.song_id + ":" + this.url_lyric + ":" + this.url_skym);
        if (TextUtil.isEmpty(url_lyric)) {
            return;
        }

        setMp3(url_skym);
        setLyric(path_sd + File.separator + "sing.skym");
        setSongId(song_id);

        try {
            super.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG) Log.w(_toString(), getMethodName() + "[ED]" + song_id + ":" + url_lyric);
    }

    protected String getString(int resId) {
        return getApplicationContext().getString(resId);
    }

    @Override
    public boolean play() {
        boolean ret = super.play();
        try {
            playLyrics();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void stop() {
        super.stop();
    }
}
