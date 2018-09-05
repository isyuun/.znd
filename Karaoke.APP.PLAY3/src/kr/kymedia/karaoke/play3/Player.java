package kr.kymedia.karaoke.play3;

import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;

/**
 * 노래방 플레이어
 * 
 * @author hownam
 *
 */
public class Player {
	private SongView playView;
	private SongPlay songPlay;
	// private SongRecord songRecord;
	private Context context;
	private SoundMeter soundMeter;
	private SongPlayListener listener;
	private Handler m_handler;
	private int m_precurrtime = 0;

	/**
	 * 재생 타입
	 * SONG(반주곡), RECORD(녹음곡), FEEL(필)
	 */
	public static enum TYPE {
		SING(0),
		LISTEN(1),
		FEEL(3);

		private final int index;

		TYPE(int index) {
			this.index = index;
		}

		public int index() {
			return index;
		}
	}

	/**
	 * 플레이어 상태
	 * STOP(중지), PLAY(재생), REC(녹음)
	 */
	public static enum STATUS {
		STOP(0),
		PLAY(1),
		REC(2);

		private final int index;

		STATUS(int index) {
			this.index = index;
		}

		public int index() {
			return index;
		}
	}

	/**
	 * 루비 언어 타입
	 * KOR(한국어), ENG(영어), JAP(일본어)
	 */
	public static enum RUBY {
		KOR(-1),
		ENG(0),
		JAP(1);

		private final int index;

		RUBY(int index) {
			this.index = index;
		}

		public int index() {
			return index;
		}
	}

	/**
	 * 가사 타입
	 * NORMAL(2줄가사), EXT(6줄가사)
	 */
	public static enum LYC {
		NORMAL(0),
		EXT(1);

		private final int index;

		LYC(int index) {
			this.index = index;
		}

		public int index() {
			return index;
		}
	}

	/**
	 * 가사자막 위치정보
	 * TOP(상단 위치), BOTTOM(하단 위치)
	 */
	public static enum ALIGN {
		TOP(Gravity.TOP),
		BOTTOM(Gravity.BOTTOM);

		private final int index;

		ALIGN(int index) {
			this.index = index;
		}

		public int index() {
			return index;
		}
	}

	private boolean textureViewSupport() {
		boolean exist = true;
		try {
			Class.forName("android.view.TextureView");
		} catch (ClassNotFoundException e) {
			exist = false;
		}
		return exist;
	}

	/**
	 * 생성자
	 * 
	 * @param context
	 */
	public Player(Context context) {
		this.context = context;
		songPlay = new MediaPlayerPlay();

		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
		if (textureViewSupport() == true) {
			playView = new PlayView2(context, songPlay, 0);
		} else {
			playView = new PlayView(context, songPlay, 0);
		}

		soundMeter = null;
		listener = null;
	}

	/**
	 * 생성자
	 * 
	 * @param context
	 * @param viewType
	 *          : 0(곡재생화면), 1(녹음재생화면)
	 * @param isAudioType
	 *          : true(MediaPlayer 이용 재생), false(AudioTrack 이용 재생 (현재미구현))
	 */
	public Player(Context context, int viewType, boolean isAudioType) {
		this.context = context;

		/*
		 * if ( isAudioType == false )
		 * songPlay = new MediaPlayerPlay(); // MediaPlayer 이용 재생
		 * else
		 * songPlay = new AudioTrackPlay(); // AudioTrack 이용 재생
		 */

		songPlay = new MediaPlayerPlay();
		if (textureViewSupport() == true) {
			playView = new PlayView2(context, songPlay, viewType);
		} else {
			playView = new PlayView(context, songPlay, viewType);
		}

		soundMeter = null;
		listener = null;
	}

	/**
	 * 생성자
	 * 
	 * @param context
	 * @param viewType
	 *          : 0(곡재생화면), 1(녹음재생화면)
	 * @param handler
	 */
	public Player(Context context, TYPE viewType, Handler handler) {
		this.context = context;
		songPlay = new MediaPlayerPlay();
		if (textureViewSupport() == true) {
			playView = new PlayView2(context, songPlay, viewType == TYPE.SING ? 0 : 1);
		} else {
			playView = new PlayView(context, songPlay, viewType == TYPE.SING ? 0 : 1);
		}

		m_handler = handler;
		soundMeter = null;
		listener = null;
		playView.setOnPlayViewListener(onPlayViewListener);
		songPlay.setOnListener(onSongPlayListener);
		setBackground();
	}

	/**
	 * 재생뷰 화면 객체
	 * 
	 * @return PlayView 객체 리턴
	 */
	public SongView getPlayView() {
		return playView;
	}

	/**
	 * 재생뷰, 오디오 객체 열기 (녹음곡)
	 * 
	 * @param strMp3
	 *          : mp3경로
	 * @return true(재생성공), false(재생실패)
	 */
	/*
	 * public boolean open(String strMp3) {
	 * boolean ret = playView.open(strMp3, null, false, 0, true);
	 * 
	 * return ret;
	 * }
	 */

	/**
	 * 재생뷰, 오디오 객체 열기
	 * 
	 * @param strMp3
	 *          : mp3경로
	 * @param strLyc
	 *          : 가사경로 ("", null 녹음곡)
	 * @return true(재생성공), false(재생실패)
	 */
	public boolean open(String strMp3, String strLyc) {
		boolean ret = playView.open(strMp3, strLyc);

		return ret;
	}

	/**
	 * 재생뷰, 오디오 객체 열기
	 * 
	 * @param strMp3
	 *          : mp3경로
	 * @param strLyc
	 *          : 가사경로 ("", null 녹음곡)
	 * @return true(재생성공), false(재생실패)
	 */
	public boolean open(String strMp3, InputStream strLyc) {
		boolean ret = playView.open(strMp3, strLyc);

		return ret;
	}

	/**
	 * 재생뷰, 오디오 객체 열기
	 * 
	 * @param strMp3
	 *          : mp3경로
	 * @param strLyc
	 *          : 가사경로 ("", null 녹음곡)
	 * @return true(재생성공), false(재생실패)
	 */
	/*
	 * public boolean open(String strMp3, String strLyc, boolean type) {
	 * boolean ret = playView.open(strMp3, strLyc, false, 0, true);
	 * 
	 * return ret;
	 * }
	 */

	/**
	 * 재생뷰, 오디오 객체 열기
	 * 
	 * @param path
	 *          : 음원경로
	 * @param isRuby
	 *          : true(루비노출), false(루비비노출)
	 * @param rubyLang
	 *          : 0(영어), 1(일어)
	 * @param isSystem
	 *          : true(시스템타임 사용), false(MediaPlayer 사용)
	 * @return true(재생성공), false(재생실패)
	 */
	/*
	 * public boolean open(String path, boolean isRuby, int rubyLang, boolean isSystem) {
	 * boolean ret = playView.open(path, isRuby, rubyLang, isSystem);
	 * 
	 * return ret;
	 * }
	 */

	/**
	 * 재생뷰, 오디오 객체 열기
	 * 
	 * @param strMp3
	 *          : mp3경로
	 * @param strLyc
	 *          : 가사경로
	 * @param isRuby
	 *          : true(루비노출), false(루비비노출)
	 * @param rubyLang
	 *          : 0(영어), 1(일어)
	 * @param isSystem
	 *          : true(시스템타임), false(Media타임)
	 * @return true(재생성공), false(재생실패)
	 */
	/*
	 * public boolean open(String strMp3, String strLyc, boolean isRuby, int rubyLang, boolean isSystem) {
	 * boolean ret = playView.open(strMp3, strLyc, isRuby, rubyLang, isSystem);
	 * 
	 * return ret;
	 * }
	 */

	/**
	 * Media 객체 종료
	 * 
	 * @return
	 */
	public void close() {
		/*
		 * if ( songRecord != null ) {
		 * songRecord.close();
		 * }
		 */
	}

	/**
	 * PlayView 객체 종료
	 * 
	 * @return
	 */
	public void destroy() {
		close();

		m_handler = null;

		songPlay.destroy();
		playView.destroy();
	}

	/**
	 * 재생 중지
	 * 
	 * @return true(재생중지성공), false(재생중지실패)
	 */
	public boolean stop() {
		playView.setStatus(PlayView.STOP);
		songPlay.close();
		return playView.stop();
	}

	/**
	 * 재생 시작
	 * 
	 * @return true(재생성공), false(재생실패)
	 */
	public boolean play() {
		if (songPlay.play() == true) {
			playView.setStatus(PlayView.PLAY);
		} else {
			return false;
		}

		return true;
	}

	/**
	 * 미리듣기 시작
	 * 
	 * @param time
	 *          : 재생시간(0=무제한, 0>미리듣기(msec))
	 * @return true(재생성공), false(재생실패)
	 */
	public boolean play(int time) {
		if (songPlay.play(time) == true) {
			playView.setStatus(PlayView.PLAY);
		} else {
			return false;
		}

		return true;
	}

	/**
	 * 녹음 재생 시작
	 * 
	 * @return true(녹음곡재생성공), false(녹음곡재생실패)
	 */
	public boolean record(String path) {
		if (songPlay.play() == true) {
			playView.setStatus(PlayView.REC);

			/*
			 * if ( songRecord != null ) {
			 * //songRecord.setPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
			 * return songRecord.open(path);
			 * } else {
			 * return true;
			 * }
			 */
		}

		return false;
	}

	/**
	 * 녹음 재생 시작
	 * 
	 * @return true(녹음곡재생성공), false(녹음곡재생실패)
	 */
	public boolean record() {
		if (songPlay.play() == true) {
			playView.setStatus(PlayView.REC);
		}

		return false;
	}

	/**
	 * 일시정지
	 * 
	 * @return
	 */
	public boolean pause() {
		playView.setRedraw(true);
		songPlay.setPause();

		return true;
	}

	/**
	 * 다시재생
	 * 
	 * @return
	 */
	public void resume() {
		playView.setRedraw(false);
		songPlay.setResume();
	}

	/**
	 * 재생 위치 변경
	 * 
	 * @param m
	 *          : sec(초)단위 탐색
	 * @return true(탐색성공), false(탐색실패)
	 */
	public boolean seek(int sec) {
		return playView.seek(sec / 1000);
	}

	/**
	 * 다시 그리기
	 * 
	 */
	public void redraw() {
		playView.redraw();
	}

	/**
	 * 간주점프
	 * 
	 * @return true(간주이동성공), false(간주이동실패)
	 */
	public boolean jump() {
		return playView.jump();
	}

	/**
	 * 전체재생시간
	 * 
	 * @return 전체 재생시간 리턴 msec
	 */
	public int totalTime() {
		return songPlay.getTotalTime();
	}

	/**
	 * 헤더 계산 전체재생시간
	 * 
	 * @return 전체 재생시간 리턴 msec
	 */
	public int headerTotalTime() {
		return playView.getHeaderTotalTime();
	}

	/**
	 * 현재재생시간
	 * 
	 * @return 현재 재생시간 리턴 msec
	 */
	public int currentTime() {
		return songPlay.getCurrentTime();
	}

	/**
	 * 재생여부
	 * 
	 * @return true(재생중), false(중지중)
	 */
	public boolean isPlaying() {
		return songPlay.isPlaying();
	}

	/**
	 * 멈춤여부
	 * 
	 * @return true(일시정지중), false(재생중)
	 */
	public boolean isPaused() {
		return songPlay.isPause();
	}

	/**
	 * 플레이어 상태
	 * 
	 * @param m
	 *          : STATUS (STATUS.STOP, STATUS.PLAY, STATUS.REC)
	 * @return
	 */
	public void status(STATUS st) {
		int state = 0;
		if (st == STATUS.STOP)
			state = 0;
		else if (st == STATUS.PLAY)
			state = 1;
		if (st == STATUS.REC)
			state = 2;

		playView.setStatus(state);
	}

	/**
	 * 이벤트 설정
	 * 
	 * @param m
	 *          : sec(초)단위 탐색
	 * @return true(탐색성공), false(탐색실패)
	 */
	public void event(OnCompletionListener c, OnErrorListener e, OnPreparedListener p) {
		songPlay.event(c, e, p);
	}

	/**
	 * 기본 스프라이트 (재생화면, 녹음화면)
	 * 
	 * @param bitmap
	 *          : 비트맵 객체
	 * @param x
	 *          : x 좌표값
	 * @param y
	 *          : y 좌표값
	 * @param width
	 *          : 너비
	 * @param height
	 *          : 높이
	 * @param fps
	 *          : frame per second
	 * @param count
	 *          : 애니메이션 프레임 개수
	 * @param loop
	 *          : 애니메이션 여부 (true:애니메이션, false:애니메이션중지)
	 */
	public void setAnimatedSpriteImage(Bitmap bitmap, int x, int y, int width, int height, float fps, int count, boolean loop) {
		playView.setAnimatedSpriteImage(bitmap, x, y, width, height, fps, count, loop);
	}

	/**
	 * 상태 스프라이트 (재생, 녹음, 정지)
	 * 
	 * @param str
	 *          : 이미지 이름
	 * @param x
	 *          : x 좌표값
	 * @param y
	 *          : y 좌표값
	 * @param width
	 *          : 너비
	 * @param height
	 *          : 높이
	 * @param count
	 *          : 애니메이션 프레임 개수
	 */
	public void setStatusImage(String str, int x, int y, int width, int height, int count) {
		InputStream is = null;
		Resources res = this.context.getResources();
		int type = this.context.getResources().getIdentifier(str, "drawable", this.context.getPackageName());

		try {
			is = res.openRawResource(type);
		} catch (Exception e) {
			return;
		}

		Bitmap b = BitmapFactory.decodeStream(is);
		playView.setStatusImage(b, x, y, width, height, count);
	}

	/**
	 * 준비시간 스프라이트 (준비 배경)
	 * 
	 * @param str
	 *          : 이미지 이름
	 * @param x
	 *          : x 좌표값
	 * @param y
	 *          : y 좌표값
	 * @param width
	 *          : 너비
	 * @param height
	 *          : 높이
	 * @param count
	 *          : 애니메이션 프레임 개수
	 */
	public void setReadyImage(String str, int x, int y, int width, int height, int count) {
		InputStream is = null;
		Resources res = this.context.getResources();
		int type = this.context.getResources().getIdentifier(str, "drawable", this.context.getPackageName());

		try {
			is = res.openRawResource(type);
		} catch (Exception e) {
			return;
		}

		Bitmap b = BitmapFactory.decodeStream(is);
		playView.setReadyImage(b, x, y, width, height, count);
	}

	/**
	 * 상태 스프라이트 (재생, 녹음, 정지)
	 * 
	 * @param bitmap
	 *          : 비트맵 객체
	 * @param x
	 *          : x 좌표값
	 * @param y
	 *          : y 좌표값
	 * @param width
	 *          : 너비
	 * @param height
	 *          : 높이
	 * @param count
	 *          : 애니메이션 프레임 개수
	 */
	public void setStatusImage(Bitmap bitmap, int x, int y, int width, int height, int count) {
		playView.setStatusImage(bitmap, x, y, width, height, count);
	}

	/**
	 * 준비시간 스프라이트 (준비 배경)
	 * 
	 * @param bitmap
	 *          : 비트맵 객체
	 * @param x
	 *          : x 좌표값
	 * @param y
	 *          : y 좌표값
	 * @param width
	 *          : 너비
	 * @param height
	 *          : 높이
	 * @param count
	 *          : 애니메이션 프레임 개수
	 */
	public void setReadyImage(Bitmap bitmap, int x, int y, int width, int height, int count) {
		playView.setReadyImage(bitmap, x, y, width, height, count);
	}

	/**
	 * 배경 이미지 슬라이드 스프라이트 (사용자 이미지 처리용)
	 * 
	 * @param bitmap
	 *          : 비트맵 객체
	 * @param x
	 *          : x 좌표값
	 * @param y
	 *          : y 좌표값
	 * @param width
	 *          : 너비
	 * @param height
	 *          : 높이
	 * @param count
	 *          : 애니메이션 프레임 개수
	 */
	public void setBackgroundImage(Bitmap bitmap, int x, int y, int width, int height, int count) {
		playView.setBackgroundImage(bitmap, x, y, width, height, count);
	}

	/**
	 * 전체 이미지 처리
	 */
	public void setBackground() {
		InputStream is = null;
		Resources res = this.context.getResources();

		int type = this.context.getResources().getIdentifier("bg_player_countdown", "drawable", this.context.getPackageName());

		try {
			is = res.openRawResource(type);
		} catch (Exception e) {
			return;
		}

		Bitmap b = BitmapFactory.decodeStream(is);
		setReadyImage(b, 204, 276, 96, 86, 1);
	}

	/**
	 * 배경 스프라이트 이미지 처리
	 * 
	 * @param str
	 *          : 이미지 이름
	 * @param x
	 *          : x 좌표값
	 * @param y
	 *          : y 좌표값
	 * @param width
	 *          : 너비
	 * @param height
	 *          : 높이
	 * @param fps
	 *          : frame per second
	 * @param count
	 *          : 애니메이션 프레임 개수
	 * @param loop
	 *          : 애니메이션 여부 (true:애니메이션, false:애니메이션중지)
	 */
	public void setImage(String str, int x, int y, int width, int height, float fps, int count, boolean loop) {
		InputStream is = null;
		Resources res = this.context.getResources();
		int type = this.context.getResources().getIdentifier(str, "drawable", this.context.getPackageName());

		try {
			is = res.openRawResource(type);
		} catch (Exception e) {
			return;
		}

		Bitmap b = BitmapFactory.decodeStream(is);

		setAnimatedSpriteImage(b, x, y, width, height, fps, count, loop);
	}

	/**
	 * 이미지 없을 경우 배경색 설정
	 * 
	 * @param r
	 *          : red color
	 * @param g
	 *          : green color
	 * @param b
	 *          : blue color
	 */
	public void setBackgroundColor(int r, int g, int b) {
		playView.setBackgroundColor(r, g, b);
	}

	/**
	 * 이미지 없을 경우 배경색 설정
	 * 
	 * @param a
	 *          : alpha
	 * @param r
	 *          : red color
	 * @param g
	 *          : green color
	 * @param b
	 *          : blue color
	 */
	public void setBackgroundColor(int a, int r, int g, int b) {
		playView.setBackgroundColor(a, r, g, b);
	}

	/**
	 * 이미지 배경 투명 처리 (투명 테마 설정시 사용 가능)
	 * 
	 * @param type
	 *          : true: 투명, false: 불투명
	 */
	public void setBackgroundTransparent(boolean type) {
		playView.setBackgroundTransparent(type);
	}

	/**
	 * 루비 여부 설정 (영어/일어 독음 노출 여부)
	 * 
	 * @param type
	 *          : true(노출), false(비노출)
	 */
	public void setRuby(boolean type) {
		playView.setRuby(type);
	}

	/**
	 * 루비 노출 여부 현재 상태
	 * 
	 * @return true(노출), false(비노출)
	 */
	public boolean getRuby() {
		return playView.getRuby();
	}

	/**
	 * 루비언어 설정 (염어, 일어 구분)
	 * 
	 * @param type
	 *          : 0(영어), 1(일어)
	 */
	public void setLang(int type) {
		playView.setLang(type);
	}

	/**
	 * 루비언어 설정 (염어, 일어 구분)
	 * 
	 * @param type
	 *          : 0(영어), 1(일어)
	 */
	public void setLang(RUBY type) {
		playView.setLang(type == RUBY.ENG ? 0 : 1);
	}

	/**
	 * 루비언어 상태
	 * 
	 * @return 0(영어), 1(일어)
	 */
	public int getLang() {
		return playView.getLang();
	}

	/**
	 * 화면 뷰 설정 (일반 재생화면, 녹음 재생화면)
	 * 
	 * @param type
	 *          : 0(일반 재생화면), 1(녹음곡 재생화면)
	 */
	public void setViewType(int type) {
		playView.setViewType(type);
	}

	/**
	 * 화면 뷰 상태
	 * 
	 * @return 0(일반 재생화면), 1(녹음곡 재생화면)
	 */
	public int getViewType() {
		return playView.getViewType();
	}

	/**
	 * 시스템 시간 사용 여부 (특정폰에서 MediaPlayer.getCurrentPosition() 시간을 몇초단위로 넘겨받아 시스템 시간을 활용하여 대체여부 결정)
	 * 
	 * @param type
	 *          : true(시스템시간 활용), false(MediaPlayer.getCurrentPosition() 사용)
	 */
	public void setUseSystem(boolean type) {
		playView.setSystem(type);
		songPlay.setType(type);
	}

	/**
	 * 시스템 시간 상태
	 * 
	 * @return true(시스템시간 활용), false(MediaPlayer.getCurrentPosition() 사용)
	 */
	public boolean getUseSystem() {
		return songPlay.getType();
	}

	/**
	 * 미디어플레이어 리스너
	 * 
	 * @param listener
	 */
	public void setSongPlayListener(SongPlayListener listener) {
		playView.setOnPlayViewListener(onPlayViewListener);
		songPlay.setOnListener(listener);
		this.listener = listener;
	}

	/**
	 * 미디어레코더 리스너
	 * 
	 * @param listener
	 */
	/*
	 * public void setSongRecordListener(SongScoreListener listener) {
	 * songRecord.setOnListener(listener);
	 * }
	 */

	/**
	 * 템포설정 (AudioTrackPlay 이용시 사용 가능)
	 * 
	 * @param value
	 *          : 템포값 범위(-25 ~ 25) 5단위 설정 (-25, -20, -15, -10, -5, 0, 5, 10, 15, 20, 25)
	 */
	public void setTempo(int value)
	{
		songPlay.setTempo(value);
	}

	/**
	 * 템포값 리턴 (AudioTrackPlay 이용시 사용 가능)
	 * 
	 * @return 템포값 범위(-25 ~ 25) 5단위 설정 (-25, -20, -15, -10, -5, 0, 5, 10, 15, 20, 25)
	 */
	public float getTempo()
	{
		return songPlay.getTempo();
	}

	/**
	 * 키설정 (AudioTrackPlay 이용시 사용 가능)
	 * 
	 * @param value
	 *          : 키값 범위(-5 ~ 5) 1단위 설정 (-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5)
	 */
	public void setPitch(int value)
	{
		songPlay.setPitch(value);
	}

	/**
	 * 키값 리턴 (AudioTrackPlay 이용시 사용 가능)
	 * 
	 * @return 키값 범위(-5 ~ 5) 1단위 설정 (-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5)
	 */
	public int getPitch()
	{
		return songPlay.getPitch();
	}

	/**
	 * 글꼴 설정
	 * 
	 * @param path
	 *          : 폰트 경로 (외부경로: full path)
	 * @return
	 */
	public void setFont(String path)
	{
		playView.setFont(path);
	}

	/**
	 * 가사 설정
	 * 
	 * @param posHeight
	 *          : y좌표
	 * @param fontHeight
	 *          : 폰트 크기
	 * @param fontBackColor
	 *          : 배경색(default:white)
	 * @param fontPaintColor
	 *          : 전경색(default:0xff15d9e3)
	 * @param alpha
	 *          : 투명도 (0~255)
	 * @return
	 */
	public void setLyrics(int posHeight, int fontHeight, int fontBackColor, int fontPaintColor, int alpha) {
		playView.setLyrics(posHeight, fontHeight, fontBackColor, fontPaintColor, alpha);
	}

	/**
	 * 준비시간 설정
	 * 
	 * @param view
	 *          : 노출여부
	 * @param posHeight
	 *          : y좌표
	 * @param fontHeight
	 *          : 폰트 크기
	 * @param fontBackColor
	 *          : 배경색(default:0xfffc9700)
	 * @param alpha
	 *          : 투명도 (0~255)
	 * @return
	 */
	public void setReady(boolean view, int posHeight, int fontHeight, int fontBackColor, int alpha) {
		playView.setReady(view, posHeight, fontHeight, fontBackColor, alpha);
	}

	/**
	 * 타이틀 설정
	 * 
	 * @param view
	 *          : 노출여부
	 * @param posHeight
	 *          : y좌표
	 * @param fontHeight
	 *          : 폰트 크기
	 * @param fontBackColor
	 *          : 배경색(default:0xff15d9e3)
	 * @param alpha
	 *          : 투명도 (0~255)
	 * @return
	 */
	public void setTitle(boolean view, int posHeight, int fontHeight, int fontBackColor, int alpha) {
		playView.setTitle(view, posHeight, fontHeight, fontBackColor, alpha);
	}

	/**
	 * 가수/작곡/작사 설정
	 * 
	 * @param view
	 *          : 노출여부
	 * @param posHeight
	 *          : y좌표
	 * @param fontHeight
	 *          : 폰트 크기
	 * @param fontBackColor
	 *          : 배경색(default:white)
	 * @param alpha
	 *          : 투명도 (0~255)
	 * @return
	 */
	public void setSinger(boolean view, int posWidth, int posHeight, int fontHeight, int fontBackColor, int alpha) {
		playView.setSinger(view, posWidth, posHeight, fontHeight, fontBackColor, alpha);
	}

	/**
	 * wave/fft visualizer 출력
	 * 
	 * @param type
	 *          : -1(출력없음), 0(선형그래프), 1(막대그래프), 2(원형그래프), 3(원형막대그래프), 4(선형단일)
	 * @return
	 */
	public void setVision(int type) {
		playView.setVision(type);
	}

	/**
	 * 재생뷰 리스너 설정 (내부용)
	 */
	private PlayViewListener onPlayViewListener = new PlayViewListener() {
		// 화면 종료시 처리
		public void onDestroy() {
			close();
		}

		// 다운로드시 에러
		public void onError() {
			close();

			if (listener != null) {
				listener.onError();
			}
		}

		// 전주 카운트
		public void onReady(int count) {
			close();

			if (listener != null) {
				listener.onReady(count);
			}
		}
	};

	SongPlayListener onSongPlayListener = new SongPlayListener() {
		// 현재 재생 시간
		@SuppressLint("DefaultLocale")
		public void onTime(int t) {
			(new Thread(new Runnable() {
				@Override
				public void run() {
					int sec = currentTime();
					if (m_precurrtime != sec / 1000) {
						if (m_handler != null) {
							Message msg = Message.obtain(m_handler, 0, sec, totalTime());
							m_handler.sendMessage(msg);
							m_precurrtime = sec / 1000;
						}
					}
				}
			})).start();
		}

		// 재생 준비 완료
		public void onPrepared() {
			if (m_handler != null) {
				Message msg = Message.obtain(m_handler, 5);
				m_handler.sendMessage(msg);
			}
		}

		// 재생 완료
		public void onCompletion() {
			if (m_handler != null) {
				Message msg = Message.obtain(m_handler, 6);
				m_handler.sendMessage(msg);
			}
		}

		// 재생중 오류
		public void onError() {
			if (m_handler != null) {
				Message msg = Message.obtain(m_handler, 9);
				m_handler.sendMessage(msg);
			}
		}

		// 버퍼링 사이즈
		public void onBufferingUpdate(int percent) {
		}

		public void onSeekComplete()
		{
		}

		public void onRelease()
		{
		}

		public void onReady(int count)
		{
		}
	};

	/**
	 * Sound Meter 측정 오픈
	 * 
	 * @return
	 */
	public void openSoundMeter() {
		soundMeter.start();
	}

	/**
	 * Sound Meter 정지
	 * 
	 * @return
	 */
	public void closeSoundMeter() {
		soundMeter.stop();
	}

	/**
	 * Sound Meter Amplitude
	 * 
	 * @return
	 */
	public double SoundMeter() {
		return soundMeter.getAmplitudeEMA();
	}

	/**
	 * 싱크 시간 조절 설정
	 * 
	 * @param msec
	 *          : 음수(싱크 느리게), 양수(싱크 빠르게)
	 */
	public void setSyncTime(int msec) {
		playView.setSyncTime(msec);
	}

	/**
	 * 싱크 시간 조절값
	 * 
	 * @return millisecond 시간 리턴 : 음수(싱크 느리게), 양수(싱크 빠르게)
	 */
	public int getSyncTime() {
		return playView.getSyncTime();
	}

	/**
	 * 텍스트 출력 여부 결정
	 * 
	 * @param show
	 *          : true(화면 텍스트 노출), false(화면텍스트 비노출)
	 * @return
	 */
	public void setView(boolean show) {
		playView.setShow(show);
	}

	/**
	 * 드로잉 여부 결정
	 * 
	 * @param b
	 *          : true(자막 정지), false(자막 재생)
	 * @return
	 */
	public void setRedraw(boolean b) {
		playView.setRedraw(b);
	}

	/**
	 * 가사자막위치(Player.ALIGN)
	 * 
	 * @param align
	 *          TOP/BOTTOM
	 * @see ALIGN
	 */
	public void setLyricAlign(ALIGN align) {
		playView.setLyricAlign(align.index());
	}

	/**
	 * 가사자막마진(pixel)
	 * 
	 */
	public void setLyricMargin(int margin) {
		playView.setLyricMargin(margin);
	}

	/**
	 * 가사자막싱크(.msec)
	 * 
	 * @param sync
	 *          +-
	 */
	public void setLyricSync(int sync) {
		playView.setLyricDelay(sync);
	}

	/**
	 * 곡제목 - 다국어처리
	 * 
	 * @param title
	 */
	public void setTitle(String title) {

		if (TextUtils.isEmpty(title)) {
			return;
		}
		if (playView != null) {
			playView.getSongData().setTitle(title);
		}
	}

	/**
	 * 곡제목 - 다국어처리
	 */
	public String getTitle() {
		if (playView != null && playView.getSongData() != null) {
			return playView.getSongData().getTitle();
		} else {
			return null;
		}
	}

	/**
	 * 가수명 - 다국어데이터처리
	 * 
	 * @param singer
	 */
	public void setSinger(String singer) {

		if (TextUtils.isEmpty(singer)) {
			return;
		}
		if (playView != null) {
			playView.getSongData().setSinger(singer);
		}
	}

	/**
	 * 가수명 - 다국어데이터처리
	 */
	public String getSinger() {
		if (playView != null && playView.getSongData() != null) {
			return playView.getSongData().getSinger();
		} else {
			return null;
		}
	}

	/**
	 * 작곡가 - 다국어데이터처리
	 * 
	 * @param composer
	 */
	public void setComposer(String composer) {

		if (TextUtils.isEmpty(composer)) {
			return;
		}
		if (playView != null) {
			playView.getSongData().setComposer(composer);
		}
	}

	/**
	 * 작곡가 - 다국어데이터처리
	 */
	public String getComposer() {
		if (playView != null && playView.getSongData() != null) {
			return playView.getSongData().getComposer();
		} else {
			return null;
		}
	}

	/**
	 * 작사가 - 다국어데이터처리
	 * 
	 * @param writer
	 */
	public void setWriter(String writer) {

		if (TextUtils.isEmpty(writer)) {
			return;
		}
		if (playView != null) {
			playView.getSongData().setWriter(writer);
		}
	}

	/**
	 * 작사가 - 다국어데이터처리
	 */
	public String getWriter() {
		if (playView != null && playView.getSongData() != null) {
			return playView.getSongData().getWriter();
		} else {
			return null;
		}
	}
};
