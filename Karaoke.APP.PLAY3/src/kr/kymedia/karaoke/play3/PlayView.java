package kr.kymedia.karaoke.play3;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.PorterDuff;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import kr.kymedia.karaoke.data.Ruby;
import kr.kymedia.karaoke.data.SongData;
import kr.kymedia.karaoke.data.SongUtil;

public class PlayView extends SurfaceView implements SongView, SurfaceHolder.Callback, PlayViewListener {
	public ScreenThread m_screenThread;
	private SurfaceHolder m_holder;
	private boolean m_redraw;
	private SongData m_data;
	private ArrayList<AnimatedSprite> m_animatedSprite;
	private ArrayList<Sprite> m_backgroundSprite;
	private Sprite m_sprStatus;
	private Sprite m_sprReady;
	private SongPlay songPlay;
	private int m_status;
	private int m_viewType;
	private int m_currtime;
	private boolean m_ruby;
	private int m_lang;
	private boolean m_system;
	private int m_updatetime;
	private Thread m_thread;
	private boolean m_listen;
	private boolean m_transparent;
	private int m_timing;

	private int m_align;
	private int m_margin;
	private int m_delay;
	private Context m_context;

	private static final int SYNC_TEXT = 0;
	private static final int SYNC_READY = 2;
	private static final int SYNC_ENDDIVISION = 7;

	private static final int SYNC_STATUS_NO = 0;
	private static final int SYNC_STATUS_READY = 1;
	private static final int SYNC_STATUS_NEXT = 2;
	private static final int SYNC_STATUS_NUM = 3;
	private static final int SYNC_STATUS_TEXT = 4;
	private static final int SYNC_STATUS_DEL = 5;
	private static final int SYNC_STATUS_START = 6;
	private static final int SYNC_STATUS_DIVISION = 7;

	public static final int SONG_VIEW = 0;
	public static final int RECORD_VIEW = 1;

	public static final int STOP = 0;
	public static final int PLAY = 1;
	public static final int REC = 2;

	public static final int KOR = -1;
	public static final int ENG = 0;
	public static final int JAP = 1;

	public static final int TOP = Gravity.TOP;
	public static final int BOTTOM = Gravity.BOTTOM;

	private PlayViewListener onPlayViewListener = null;

	public PlayView(Context context) {
		super(context);
		m_context = context;
	}

	public PlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		m_context = context;
	}

	public PlayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		m_context = context;
	}

	public PlayView(Context context, SongPlay songPlay, int viewType) {
		super(context);
		m_context = context;

		this.m_viewType = viewType;
		this.songPlay = songPlay;
		m_screenThread = null;
		m_holder = getHolder();
		m_holder.addCallback(this);
		m_redraw = false;
		m_status = STOP;
		m_currtime = 0;
		m_updatetime = 0;
		m_timing = 0;
		m_ruby = false;
		m_lang = KOR;
		m_system = false;
		m_listen = false;
		m_animatedSprite = new ArrayList<AnimatedSprite>();
		m_backgroundSprite = new ArrayList<Sprite>();
		m_data = new SongData();
		m_sprStatus = null;
		m_sprReady = null;
		m_thread = null;
		m_transparent = false;

		m_align = BOTTOM;
		m_margin = 0;
		m_delay = 0;

		setFocusable(true);
		setFocusableInTouchMode(true);

		m_screenThread = new ScreenThread();
	}

	public void setOnPlayViewListener(PlayViewListener listener) {
		m_listen = true;
		onPlayViewListener = listener;
	}

	public void onDestroy() {

	}

	public void onError() {

	}

	public void onReady(int count) {

	}

	public void destroy() {
		m_redraw = true;

		if (m_screenThread != null) {
			if (m_screenThread.m_audioVisualizer != null) {
				m_screenThread.m_audioVisualizer.release();
				m_screenThread.m_audioVisualizer = null;
			}

			m_screenThread.requestExitAndWait();
			m_screenThread = null;
		}

		m_data.release();

		m_backgroundSprite.clear();
		m_animatedSprite.clear();
	}

	/*
	 * public boolean open(String url, boolean isRuby, int rubyLang, boolean isSystem) {
	 * m_ruby = isRuby;
	 * m_lang = rubyLang;
	 * m_system = isSystem;
	 * 
	 * return open(url, "");
	 * }
	 * 
	 * public boolean open(String mp3, String lyc, boolean isRuby, int rubyLang, boolean isSystem) {
	 * m_ruby = isRuby;
	 * m_lang = rubyLang;
	 * m_system = isSystem;
	 * 
	 * return open(mp3, lyc);
	 * }
	 */

	public boolean open(String mp3, String lyc) {
		boolean b = m_data.load(lyc);
		if (b == true) {
			b = songPlay.open(mp3);
		}

		return b;
	}

	public boolean open(String mp3, InputStream is) {
		boolean b = m_data.load(is);
		if (b == true) {
			b = songPlay.open(mp3);
		}

		return b;
	}

	public boolean stop() {
		if (m_thread != null && m_thread.isAlive()) {
			try {
				m_thread.join();
			} catch (InterruptedException e) {
			}
		}

		m_currtime = 0;
		m_status = STOP;

		m_data.release();

		if (m_screenThread != null)
			m_screenThread.init();

		return true;
	}

	public int getMp3Position() {
		return m_data.getControlTag().nMpPos;
	}

	public int getHeaderTotalTime() {
		int time = m_data.getLastSyncTime();
		return time > 0 ? time + 3000 : time;
	}

	public void setRuby(boolean b) {
		m_ruby = b;
	}

	public boolean getRuby() {
		return m_ruby;
	}

	public void setLang(int v) {
		m_lang = v;
	}

	public int getLang() {
		return m_lang;
	}

	public void setSystem(boolean b) {
		m_system = b;
	}

	public boolean getSystem() {
		return m_system;
	}

	public void setViewType(int v) {
		m_viewType = v;
	}

	public int getViewType() {
		return m_viewType;
	}

	public void setRedraw(boolean redraw) {
		m_redraw = redraw;
	}

	public void setStatus(int status) {
		m_status = status;
	}

	public int getStatus() {
		return m_status;
	}

	public void setSyncTime(int msec) {
		m_timing = msec;
	}

	public int getSyncTime() {
		return m_timing;
	}

	public boolean jump() {
		boolean ret = false;

		if (m_screenThread != null) {
			int time = m_screenThread.jump();

			if (time > 0) {
				updateTime(time);
			}
		}

		return ret;
	}

	public boolean seek(int time) {
		if (m_thread != null && m_thread.isAlive())
			return false;

		boolean ret = false;
		updateTime(time);

		return ret;
	}

	public void redraw() {
		if (m_thread != null && m_thread.isAlive())
			return;

		RedrawThread redraw = new RedrawThread();
		m_thread = new Thread(redraw);
		if (!m_thread.isAlive()) {
			m_thread.start();
		}
	}

	public void setShow(boolean show) {
		if (m_screenThread != null)
			m_screenThread.setShow(show);
	}

	public void setBackgroundImage(Bitmap b, int x, int y, int width, int height, int count) {
		Sprite sprite = new Sprite();
		sprite.initialize(b, x, y, width, height, count);

		m_backgroundSprite.add(sprite);
	}

	public void setAnimatedSpriteImage(Bitmap b, int x, int y, int width, int height, float fps, int count, boolean loop) {
		AnimatedSprite sprite = new AnimatedSprite();
		sprite.initialize(b, x, y, width, height, fps, count, loop);

		m_animatedSprite.add(sprite);
	}

	public void setStatusImage(Bitmap b, int x, int y, int width, int height, int count) {
		m_sprStatus = new Sprite();
		m_sprStatus.initialize(b, x, y, width, height, count);
	}

	public void setReadyImage(Bitmap b, int x, int y, int width, int height, int count) {
		m_sprReady = new Sprite();
		m_sprReady.initialize(b, x, y, width, height, count);
	}

	public void setBackgroundColor(int r, int g, int b) {
		if (m_screenThread != null)
			m_screenThread.setBackgourndColor(r, g, b);
	}

	public void setBackgroundColor(int a, int r, int g, int b) {
		if (m_screenThread != null)
			m_screenThread.setBackgourndColor(a, r, g, b);
	}

	public void setBackgroundTransparent(boolean type) {
		if (type == true) {
			m_transparent = true;
			setZOrderOnTop(true);
			m_holder.setFormat(PixelFormat.TRANSPARENT);
		} else {
			m_transparent = false;
		}
	}

	public void setFont(String path) {
		if (m_screenThread != null)
			m_screenThread.setFont(path);
	}

	public void setLyrics(int posHeight, int fontHeight, int fontBackColor, int fontPaintColor, int alpha) {
		if (m_screenThread != null) {
			m_screenThread.setEnvLyrics(posHeight, fontHeight, fontBackColor, fontPaintColor, alpha);
		}
	}

	public void setReady(boolean view, int posHeight, int fontHeight, int fontBackColor, int alpha) {
		if (m_screenThread != null) {
			m_screenThread.setEnvReady(view, posHeight, fontHeight, fontBackColor, alpha);
		}
	}

	public void setTitle(boolean view, int posHeight, int fontHeight, int fontBackColor, int alpha) {
		if (m_screenThread != null) {
			m_screenThread.setEnvTitle(view, posHeight, fontHeight, fontBackColor, alpha);
		}
	}

	public void setSinger(boolean view, int posWidth, int posHeight, int fontHeight, int fontBackColor, int alpha) {
		if (m_screenThread != null) {
			m_screenThread.setEnvSinger(view, posWidth, posHeight, fontHeight, fontBackColor, alpha);
		}
	}

	public void setVision(int type) {
		m_screenThread.setVision(type);

		if (m_screenThread.m_audioVisualizer != null) {
			m_screenThread.m_audioVisualizer.open(songPlay);
		}
	}

	public void updateTime(int t) {
		m_updatetime = t;
		songPlay.seek(m_updatetime);
		RedrawThread redraw = new RedrawThread();
		m_thread = new Thread(redraw);
		if (!m_thread.isAlive()) {
			m_thread.start();
		}
	}

	public void setLyricAlign(int align) {
		m_align = align;
	}

	public void setLyricMargin(int margin) {
		m_margin = margin;
	}

	public void setLyricDelay(int delay) {
		m_delay = delay;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (m_screenThread == null) {
			m_screenThread = new ScreenThread();
		}

		if (!m_screenThread.isAlive()) {
			m_screenThread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (m_screenThread != null) {
			if (m_screenThread.m_audioVisualizer != null) {
				m_screenThread.m_audioVisualizer.release();
				m_screenThread.m_audioVisualizer = null;
			}

			m_screenThread.requestExitAndWait();
			m_screenThread = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (m_screenThread != null)
			m_screenThread.onWindowResize(w, h);
	}

	public class RedrawThread extends Thread {
		public void run() {
			m_redraw = true;

			if (m_screenThread != null)
				m_screenThread.rePaint(songPlay.getCurrentTime() + m_timing + m_delay);

			m_redraw = false;
		}
	}

	public class ScreenThread extends Thread {
		private AudioVisualizer m_audioVisualizer;
		private boolean m_done;
		private Paint[] m_paintLyrics = new Paint[2];
		private Paint[] m_paintLyricsRuby = new Paint[2];
		private Paint[] m_paintJpRuby1 = new Paint[2];
		private Paint[] m_paintSinger = new Paint[2];
		private Paint[] m_paintTitle = new Paint[2];
		private Paint[] m_paintReady = new Paint[2];
		private int m_backgroundColor = 0;
		private boolean m_bReady;
		private int m_nInterval;
		private int m_nTickTime;
		private int m_nFirstTick;
		private int m_nEndTime;
		private int m_nDisplay;
		private int m_nLinePos;
		private int m_nSpanTime;
		private int m_itor;
		private String m_strReady;
		private String m_strCount;
		private String[] m_strLine = new String[2];
		private String[] m_strLyrics = new String[2];
		private String[] m_strRuby1 = new String[2];
		private String[] m_strRuby2 = new String[2];
		private Point[] m_ptDrawPos = new Point[2];
		private Point[] m_ptDrawPosRuby = new Point[2];
		private Point[] m_ptDrawPosJpRuby1 = new Point[2];
		private String m_strPrev;
		private String m_strGo;
		private int m_width;
		private int m_height;
		private int[] m_nInColor = new int[2];
		private int[] m_nOutColor = new int[2];
		private int m_line;
		private int m_playpos = 0;
		private boolean m_readyinit;
		private float m_xRate = 1;
		private float m_yRate = 1;
		private int m_slide = 0;

		private int m_sizeLyrics;
		private int m_sizeReady;
		private int m_sizeTitle;
		private int m_sizeSinger;
		private int m_colorReady;
		private int m_colorTitle;
		private int m_colorSinger;
		private int[] m_colorLyrics = new int[2];
		private int[] m_colorLyricsBack = new int[2];
		private int m_posHeightLyrics;
		private int m_posHeightReady;
		private int m_posHeightTitle;
		private int m_posWidthSinger;
		private int m_posHeightSinger;
		private boolean m_viewReady;
		private boolean m_viewTitle;
		private boolean m_viewSinger;
		private int m_alphaLyrics;
		private int m_alphaReady;
		private int m_alphaTitle;
		private int m_alphaSinger;
		private boolean m_show;

		private final int CANVAS_WIDTH = 480;
		private final int CANVAS_HEIGHT = 577;
		private final int INNER_COLOR = 0xff15d9e3;
		private final int READY_COLOR = 0xfffc9700;
		// private final int PLAY_POS = 135;
		private final int RUBY_POS = 100;
		private final int SINGER_POS = 135;
		private final int TITLE_POS = 50;
		private final int JP_POS = 27;
		private final int TEXT_SIZE_LYRICS = 30;
		private final int TEXT_SIZE_READY = 37;
		private final int TEXT_SIZE_TITLE = 37;
		private final int TEXT_SIZE_SINGER = 27;

		ScreenThread() {
			super();

			m_sizeLyrics = TEXT_SIZE_LYRICS;
			m_sizeReady = TEXT_SIZE_READY;
			m_sizeTitle = TEXT_SIZE_TITLE;
			m_sizeSinger = TEXT_SIZE_SINGER;
			m_colorReady = READY_COLOR;
			m_colorTitle = INNER_COLOR;
			m_colorSinger = Color.WHITE;
			m_colorLyrics[0] = Color.WHITE;
			m_colorLyrics[1] = INNER_COLOR;
			m_colorLyricsBack[0] = Color.BLACK;
			m_colorLyricsBack[1] = Color.BLACK;
			m_alphaLyrics = 0;
			m_alphaReady = 0;
			m_alphaTitle = 0;
			m_alphaSinger = 0;

			m_posHeightLyrics = -1;
			m_posHeightReady = -1;
			m_posHeightTitle = -1;
			m_posWidthSinger = -1;
			m_posHeightSinger = -1;
			m_viewReady = true;
			m_viewTitle = true;
			m_viewSinger = true;

			m_audioVisualizer = null;
			m_show = true;

			init();

			m_done = false;
			m_width = CANVAS_WIDTH;
			m_height = CANVAS_HEIGHT;

			m_ptDrawPos[0] = new Point();
			m_ptDrawPos[1] = new Point();
			m_ptDrawPosRuby[0] = new Point();
			m_ptDrawPosRuby[1] = new Point();
			m_ptDrawPosJpRuby1[0] = new Point();
			m_ptDrawPosJpRuby1[1] = new Point();

			m_paintLyrics[0] = new Paint();
			m_paintLyrics[0].setAntiAlias(true);
			m_paintLyrics[0].setStyle(Style.STROKE);
			m_paintLyrics[0].setStrokeWidth(2.0f);
			m_paintLyrics[0].setTextSize(m_sizeLyrics);

			m_paintLyrics[1] = new Paint();
			m_paintLyrics[1].setAntiAlias(true);
			m_paintLyrics[1].setStyle(Style.FILL);
			m_paintLyrics[1].setStrokeWidth(2.0f);
			m_paintLyrics[1].setTextSize(m_sizeLyrics);

			m_paintLyricsRuby[0] = new Paint();
			m_paintLyricsRuby[0].setAntiAlias(true);
			m_paintLyricsRuby[0].setStyle(Style.STROKE);
			m_paintLyricsRuby[0].setStrokeWidth(2.0f);
			m_paintLyricsRuby[0].setTextSize(m_sizeLyrics - 3);

			m_paintLyricsRuby[1] = new Paint();
			m_paintLyricsRuby[1].setAntiAlias(true);
			m_paintLyricsRuby[1].setStyle(Style.FILL);
			m_paintLyricsRuby[1].setStrokeWidth(2.0f);
			m_paintLyricsRuby[1].setTextSize(m_sizeLyrics - 3);

			m_paintJpRuby1[0] = new Paint();
			m_paintJpRuby1[0].setAntiAlias(true);
			m_paintJpRuby1[0].setStyle(Style.STROKE);
			m_paintJpRuby1[0].setStrokeWidth(2.0f);
			m_paintJpRuby1[0].setTextSize(m_sizeLyrics / 2);

			m_paintJpRuby1[1] = new Paint();
			m_paintJpRuby1[1].setAntiAlias(true);
			m_paintJpRuby1[1].setStyle(Style.FILL);
			m_paintJpRuby1[1].setStrokeWidth(2.0f);
			m_paintJpRuby1[1].setTextSize(m_sizeLyrics / 2);

			m_paintTitle[0] = new Paint();
			m_paintTitle[0].setAntiAlias(true);
			m_paintTitle[0].setStyle(Style.STROKE);
			m_paintTitle[0].setStrokeWidth(2.0f);
			m_paintTitle[0].setTextSize(m_sizeTitle);

			m_paintTitle[1] = new Paint();
			m_paintTitle[1].setAntiAlias(true);
			m_paintTitle[1].setStyle(Style.FILL);
			m_paintTitle[1].setStrokeWidth(2.0f);
			m_paintTitle[1].setTextSize(m_sizeTitle);

			m_paintSinger[0] = new Paint();
			m_paintSinger[0].setAntiAlias(true);
			m_paintSinger[0].setStyle(Style.STROKE);
			m_paintSinger[0].setStrokeWidth(2.0f);
			m_paintSinger[0].setTextSize(m_sizeSinger);

			m_paintSinger[1] = new Paint();
			m_paintSinger[1].setAntiAlias(true);
			m_paintSinger[1].setStyle(Style.FILL);
			m_paintSinger[1].setStrokeWidth(2.0f);
			m_paintSinger[1].setTextSize(m_sizeSinger);

			m_paintReady[0] = new Paint();
			m_paintReady[0].setAntiAlias(true);
			m_paintReady[0].setStyle(Style.STROKE);
			m_paintReady[0].setStrokeWidth(2.0f);
			m_paintReady[0].setTextSize(m_sizeReady);

			m_paintReady[1] = new Paint();
			m_paintReady[1].setAntiAlias(true);
			m_paintReady[1].setStyle(Style.FILL);
			m_paintReady[1].setStrokeWidth(2.0f);
			m_paintReady[1].setTextSize(m_sizeReady);
		}

		private void init() {
			m_itor = 0;
			m_bReady = false;
			m_nInterval = 0;
			m_nTickTime = 0;
			m_nFirstTick = 0;
			m_nEndTime = 0;
			m_nDisplay = 0;
			m_nLinePos = 0;
			m_nSpanTime = 0;
			m_line = 0;
			m_readyinit = false;
			m_slide = 0;

			m_nInColor[0] = m_colorLyrics[0];
			m_nOutColor[0] = m_colorLyricsBack[0];
			m_nInColor[1] = m_colorLyrics[0];
			m_nOutColor[1] = m_colorLyricsBack[0];

			m_playpos = 0;
			/*
			 * if (m_viewType == SONG_VIEW) {
			 * m_playpos = PLAY_POS;
			 * } else {
			 * m_playpos = 0;
			 * }
			 */
		}

		public void setFont(String path) {
			// style : NORMAL, BOLD, ITALIC, BOLD_ITALIC

			Typeface typeface = null;
			if (path.equals("default"))
				typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
			else if (path.equals("default_bold"))
				typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL);
			else if (path.equals("monospace"))
				typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
			else if (path.equals("sans_serif"))
				typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
			else if (path.equals("serif"))
				typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL);
			else {
				try {
					typeface = Typeface.createFromFile(path);
				} catch (Exception e) {
					try {
						typeface = Typeface.createFromAsset(m_context.getAssets(), path);
					} catch (Exception ex) {
						typeface = null;
					}
				}
			}

			if (typeface != null) {
				m_paintLyrics[0].setTypeface(typeface);
				m_paintLyrics[1].setTypeface(typeface);
				m_paintLyricsRuby[0].setTypeface(typeface);
				m_paintLyricsRuby[1].setTypeface(typeface);
				m_paintJpRuby1[0].setTypeface(typeface);
				m_paintJpRuby1[1].setTypeface(typeface);
				m_paintTitle[0].setTypeface(typeface);
				m_paintTitle[1].setTypeface(typeface);
				m_paintSinger[0].setTypeface(typeface);
				m_paintSinger[1].setTypeface(typeface);
				m_paintReady[0].setTypeface(typeface);
				m_paintReady[1].setTypeface(typeface);
			}
		}

		public void setShow(boolean show) {
			m_show = show;
		}

		@Override
		public void run() {
			SurfaceHolder surfaceHolder = m_holder;
			Canvas canvas = null;

			while (!m_done) {
				if (m_redraw == false) {
					m_currtime = songPlay.getCurrentTime() + m_timing + m_delay;

					try {
						canvas = surfaceHolder.lockCanvas();
						synchronized (surfaceHolder) {
							if (canvas != null) {
								if (m_transparent == true) {
									canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
								} else {
									canvas.drawColor(m_backgroundColor);
								}

								// isyoon_20131014 : 재생시에만업데이트
								if (songPlay.isPlaying()) {
									doUpdate();
								}
								doDraw(canvas);
							}
						}
					} finally {
						try {
							if (canvas != null)
								surfaceHolder.unlockCanvasAndPost(canvas);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		public void requestExitAndWait() {
			m_done = true;

			try {
				join();
			} catch (InterruptedException ex) {
			}
		}

		public void onWindowResize(int w, int h) {
			m_xRate = (float) w / m_width;
			m_yRate = (float) h / m_height;
		}

		public void setBackgourndColor(int r, int g, int b) {
			m_backgroundColor = Color.rgb(r, g, b);
		}

		public void setBackgourndColor(int a, int r, int g, int b) {
			m_backgroundColor = Color.argb(a, r, g, b);
		}

		public void setEnvLyrics(int posHeight, int fontHeight, int fontBackColor, int fontPaintColor, int alpha) {
			m_posHeightLyrics = posHeight;
			m_sizeLyrics = fontHeight;
			m_paintLyrics[0].setTextSize(m_sizeLyrics);
			m_paintLyrics[1].setTextSize(m_sizeLyrics);
			m_paintLyricsRuby[0].setTextSize(m_sizeLyrics - 3);
			m_paintLyricsRuby[1].setTextSize(m_sizeLyrics - 3);
			m_paintJpRuby1[0].setTextSize(m_sizeLyrics / 2);
			m_paintJpRuby1[1].setTextSize(m_sizeLyrics / 2);
			m_colorLyrics[0] = fontBackColor;
			m_colorLyrics[1] = fontPaintColor;

			m_nInColor[0] = m_colorLyrics[0];
			m_nInColor[1] = m_colorLyrics[0];

			m_alphaLyrics = alpha;
			if (m_alphaLyrics < 0)
				m_alphaLyrics = 0;
			else if (m_alphaLyrics > 255)
				m_alphaLyrics = 255;
		}

		public void setEnvReady(boolean view, int posHeight, int fontHeight, int fontBackColor, int alpha) {
			m_viewReady = view;
			m_posHeightReady = posHeight;
			m_sizeReady = fontHeight;
			m_paintReady[0].setTextSize(m_sizeReady);
			m_paintReady[1].setTextSize(m_sizeReady);
			m_colorReady = fontBackColor;

			m_alphaReady = alpha;
			if (m_alphaReady < 0)
				m_alphaReady = 0;
			else if (m_alphaReady > 255)
				m_alphaReady = 255;
		}

		public void setEnvTitle(boolean view, int posHeight, int fontHeight, int fontBackColor, int alpha) {
			m_viewTitle = view;
			m_posHeightTitle = posHeight;
			m_sizeTitle = fontHeight;
			m_paintTitle[0].setTextSize(m_sizeTitle);
			m_paintTitle[1].setTextSize(m_sizeTitle);
			m_colorTitle = fontBackColor;

			m_alphaTitle = alpha;
			if (m_alphaTitle < 0)
				m_alphaTitle = 0;
			else if (m_alphaTitle > 255)
				m_alphaTitle = 255;
		}

		public void setEnvSinger(boolean view, int posWidth, int posHeight, int fontHeight, int fontBackColor, int alpha) {
			m_viewSinger = view;
			m_posWidthSinger = posWidth;
			m_posHeightSinger = posHeight;
			m_sizeSinger = fontHeight;
			m_paintSinger[0].setTextSize(m_sizeSinger);
			m_paintSinger[1].setTextSize(m_sizeSinger);
			m_colorSinger = fontBackColor;

			m_alphaSinger = alpha;
			if (m_alphaSinger < 0)
				m_alphaSinger = 0;
			else if (m_alphaSinger > 255)
				m_alphaSinger = 255;
		}

		public void setVision(int type) {
			if (m_audioVisualizer != null) {
				m_audioVisualizer.release();
				m_audioVisualizer = null;
			}

			if (type > -1)
				m_audioVisualizer = new AudioVisualizer(type);
		}

		public int jump() {
			long ret = -1;

			int attr = m_data.getListSyncTag().get(m_itor).nAttribute;

			if (m_itor == 0) {
				int itor = m_itor;
				ret = m_data.getListSyncTag().get(itor).lTimeSyncStart;
			} else if (attr == SYNC_ENDDIVISION) {
				int itor = m_itor + 1;

				if (itor < m_data.getListSyncTag().size())
					ret = m_data.getListSyncTag().get(itor).lTimeSyncStart;
			}

			return (int) ret;
		}

		private void outText(Canvas canvas, Paint paint, String str, int x, int y, int inColor,
				int outColor, int alpha) {
			if (alpha == 0) {
				paint.setColor(outColor);
				canvas.drawText(str, x, y - 1, paint);
				canvas.drawText(str, x, y + 1, paint);
				canvas.drawText(str, x - 1, y, paint);
				canvas.drawText(str, x + 1, y, paint);

				paint.setColor(inColor);
				canvas.drawText(str, x, y, paint);
			} else {
				paint.setColor(inColor);
				paint.setAlpha(alpha);
				canvas.drawText(str, x, y, paint);
			}
		}

		private void outText(Canvas canvas, Paint[] paint, String str, int x, int y, int inColor,
				int outColor, int alpha) {
			if (alpha == 0) {
				paint[0].setColor(outColor);
				canvas.drawText(str, x, y, paint[0]);

				paint[1].setColor(inColor);
				canvas.drawText(str, x, y, paint[1]);
			} else {
				paint[1].setColor(inColor);
				paint[1].setAlpha(alpha);
				canvas.drawText(str, x, y, paint[1]);
			}
		}

		private Rect outSize(Paint paint, String str) {
			Rect rect = new Rect();

			if (str.length() == 0) {
				rect.set(0, 0, 0, 0);
				return rect;
			}

			paint.getTextBounds(str, 0, str.length(), rect);

			if (rect.width() > 0) {
				rect.right += 2;
				rect.bottom += 2;
			}

			return rect;
		}

		protected void doUpdate() {
			long now = System.currentTimeMillis();

			for (AnimatedSprite spite : m_animatedSprite) {
				spite.update(now);
			}

			if (m_backgroundSprite.size() > 0) {
				if (m_backgroundSprite.get(m_slide).update(now) == true) {
					m_slide++;

					if (m_slide >= m_backgroundSprite.size())
						m_slide = 0;
				}
			}
		}

		protected void doDraw(Canvas canvas) {
			if (m_show == false)
				return;

			Matrix matrix = new Matrix();
			matrix.setScale(m_xRate, m_yRate);
			canvas.setMatrix(matrix);

			drawBackground(canvas);

			if (m_audioVisualizer != null) {
				Rect drawRect = new Rect();
				drawRect.set(0, 0, m_width, m_height);
				m_audioVisualizer.onRender(canvas, drawRect);
			}

			drawLyrics(canvas);
		}

		protected void drawBackground(Canvas canvas) {
			if (m_backgroundSprite.size() > 0) {
				m_backgroundSprite.get(m_slide).draw(canvas);
			} else {
				if (m_animatedSprite.size() > 0) {
					for (AnimatedSprite sprite : m_animatedSprite) {
						sprite.draw(canvas);
					}
				}

				if (m_viewType == SONG_VIEW) {
					if (m_sprStatus != null) {
						if (m_status == PLAY) {
							m_sprStatus.update(1);
						} else if (m_status == REC) {
							m_sprStatus.update(2);
						} else {
							m_sprStatus.update(0);
						}

						m_sprStatus.draw(canvas);
					}
				}
			}
		}

		protected void drawLyrics(Canvas canvas) {
			if (m_status == STOP)
				return;

			if (m_currtime < 0)
				m_currtime = 0;

			if (m_data.isLoad() == false)
				return;

			int currtime = m_currtime;

			int nState = SYNC_STATUS_NO;
			int nType = timeOut(currtime);
			int nLinePos = getLinePos();

			if (m_bReady == false) {
				nState = 0;
				if (m_viewSinger == true) {
					// setSinger(canvas, m_paintSinger, m_data.getLyricsInfoTag().strSinger,
					// m_data.getLyricsInfoTag().strAuthor, m_data.getLyricsInfoTag().strLyricsAuthor);
					String str1 = Ruby.hanToRuby(m_data.getLyricsInfoTag().strSinger, m_lang);
					String str2 = Ruby.hanToRuby(m_data.getLyricsInfoTag().strAuthor, m_lang);
					String str3 = Ruby.hanToRuby(m_data.getLyricsInfoTag().strLyricsAuthor, m_lang);
					setSinger(canvas, m_paintSinger, str1, str2, str3);
				}

				if (m_viewTitle == true) {
					// setTitle(canvas, m_paintTitle, m_data.getLyricsInfoTag().strTitle1, m_data.getLyricsInfoTag().strTitle2);
					String str1 = Ruby.hanToRuby(m_data.getLyricsInfoTag().strTitle1, m_lang);
					String str2 = Ruby.hanToRuby(m_data.getLyricsInfoTag().strTitle2, m_lang);
					// 곡제목 - 다국어데이터처리
					if (m_data.getTitles() != null && m_data.getTitles().length > 1) {
						setTitle(canvas, m_paintTitle, m_data.getTitles()[0], m_data.getTitles()[1]);
						str1 = m_data.getTitles()[0];
						str2 = m_data.getTitles()[1];
					}
					setTitle(canvas, m_paintTitle, str1, str2);
				}
			} else {
				nState = 1;

				setLyrics(canvas, 0);
				setLyrics(canvas, 1);

				if (nType == SYNC_STATUS_NUM) {
					if (m_viewReady == true)
						setReady(canvas, m_paintReady, m_strReady);
				}
			}

			if (nState == 1) {
				Rect curRect = outSize(m_paintLyrics[0], m_strGo);
				Rect preRect = outSize(m_paintLyrics[0], m_strPrev);

				int nPixel = (int) (curRect.width() * ((float) m_nTickTime / (float) m_nSpanTime)) + preRect.width();

				redrawLyrics(canvas, m_paintLyrics, m_strLine[nLinePos], m_ptDrawPos[nLinePos].x,
						m_ptDrawPos[nLinePos].y, nPixel, nLinePos);

				if (m_ruby == true) {
					String[] result = Ruby.hanToRuby(m_strLine[nLinePos], m_strPrev.length(), m_strGo.length(), m_lang);

					Rect curRectRuby = outSize(m_paintLyricsRuby[0], result[2]);
					Rect preRectRuby = outSize(m_paintLyricsRuby[0], result[1]);

					int nPixelRuby = (int) (curRectRuby.width() * ((float) m_nTickTime / (float) m_nSpanTime)) + preRectRuby.width();

					redrawLyrics(canvas, m_paintLyricsRuby, result[0],
							m_ptDrawPosRuby[nLinePos].x, m_ptDrawPosRuby[nLinePos].y,
							nPixelRuby, nLinePos);
				}
			}

		}

		void redrawLyrics(Canvas canvas, Paint paint, String str, int x, int y, int end, int type) {
			if (m_data.getLyricsInfoTag().strLang.equals("EUC-JP")) {
				Rect rect = outSize(paint, str);
				if (end > rect.width())
					end = rect.width();

				canvas.save();

				canvas.translate(x, y - JP_POS);
				canvas.clipRect(-10, -JP_POS, end, rect.height() + JP_POS);

				outText(canvas, paint, str, 0, y - m_ptDrawPosJpRuby1[type].y, m_colorLyrics[1], m_colorLyricsBack[1], 0);

				int pos = 0;
				int cnt = 0;
				while (pos != -1) {
					int nextPos = m_strRuby1[type].indexOf("/", pos);
					if (nextPos == -1)
						break;

					String strRuby = m_strRuby1[type].substring(pos, nextPos);
					if (strRuby.equals("") == false) {
						int size = 0;
						Rect prev = outSize(m_paintLyrics[0], str.substring(0, cnt + 1));
						Rect curr = outSize(m_paintLyrics[0], str.substring(cnt, cnt + 1));
						Rect rcRuby = outSize(m_paintJpRuby1[0], strRuby);

						if (curr.width() > rcRuby.width())
							size = (curr.width() - rcRuby.width()) / 2;
						else
							size = -(rcRuby.width() - curr.width()) / 2;

						outText(canvas, m_paintJpRuby1, strRuby, prev.width() - curr.width() + size, 0, m_colorLyrics[1], m_colorLyricsBack[1], 0);
					}

					pos = nextPos + 1;
					cnt++;

					if (cnt >= str.length())
						break;
				}

				canvas.restore();
			} else {
				Rect rect = outSize(paint, str);
				if (end > rect.width())
					end = rect.width();

				canvas.save();

				canvas.translate(x, y);
				canvas.clipRect(0, -25, end, rect.height() - 18);

				outText(canvas, paint, str, 0, 0, m_colorLyrics[1], m_colorLyricsBack[1], 0);

				canvas.restore();
			}
		}

		void redrawLyrics(Canvas canvas, Paint[] paint, String str, int x, int y, int end, int type) {
			if (m_data.getLyricsInfoTag().strLang.equals("EUC-JP")) {
				Rect rect = outSize(paint[0], str);
				if (end > rect.width())
					end = rect.width();

				canvas.save();

				canvas.translate(x, y - JP_POS);
				canvas.clipRect(-10, -JP_POS, end, rect.height() + JP_POS);

				outText(canvas, paint, str, 0, y - m_ptDrawPosJpRuby1[type].y, m_colorLyrics[1], m_colorLyricsBack[1], 0);

				int pos = 0;
				int cnt = 0;
				while (pos != -1) {
					int nextPos = m_strRuby1[type].indexOf("/", pos);
					if (nextPos == -1)
						break;

					String strRuby = m_strRuby1[type].substring(pos, nextPos);
					if (strRuby.equals("") == false) {
						int size = 0;
						Rect prev = outSize(m_paintLyrics[0], str.substring(0, cnt + 1));
						Rect curr = outSize(m_paintLyrics[0], str.substring(cnt, cnt + 1));
						Rect rcRuby = outSize(m_paintJpRuby1[0], strRuby);

						if (curr.width() > rcRuby.width())
							size = (curr.width() - rcRuby.width()) / 2;
						else
							size = -(rcRuby.width() - curr.width()) / 2;

						outText(canvas, m_paintJpRuby1, strRuby, prev.width() - curr.width() + size, 0, m_colorLyrics[1], m_colorLyricsBack[1], 0);
					}

					pos = nextPos + 1;
					cnt++;

					if (cnt >= str.length())
						break;
				}

				canvas.restore();
			} else {
				Rect rect = outSize(paint[0], str);
				if (end > rect.width())
					end = rect.width();

				canvas.save();

				canvas.translate(x, y);
				canvas.clipRect(0, -25, end, rect.height() - 18);

				outText(canvas, paint, str, 0, 0, m_colorLyrics[1], m_colorLyricsBack[1], 0);

				canvas.restore();
			}
		}

		void setReady(Canvas canvas, Paint[] paint, String str) {
			int margin = m_align == TOP ? m_margin : m_height - m_margin - TEXT_SIZE_LYRICS * 2 + 5;
			int rubypos = m_align == TOP ? -READY_MARGIN - TEXT_SIZE_LYRICS * 4 : READY_MARGIN + TEXT_SIZE_LYRICS * 4;

			int y = margin - rubypos - m_playpos;
			// int y = 333 - m_playpos;

			if (m_posHeightReady >= 0) {
				y = m_posHeightReady;
			}

			if (m_sprReady != null) {
				m_sprReady.draw(canvas, m_sprReady.getXPos(), y - (m_sprReady.getHeight() + m_sizeReady) / 2);
				/*
				 * if ( m_posHeightReady >= 0 ) {
				 * m_sprReady.draw(canvas, m_sprReady.getXPos(), y - (m_sprReady.getHeight() + m_sizeReady)/2);
				 * } else {
				 * m_sprReady.draw(canvas, m_sprReady.getXPos(), y - (m_sprReady.getHeight() + m_sizeReady)/2);
				 * //m_sprReady.draw(canvas, m_sprReady.getXPos(), m_sprReady.getYPos() - m_playpos);
				 * }
				 */
			}

			Rect rect = outSize(paint[0], str);
			outText(canvas, paint, str, (m_width - rect.width()) / 2, y, m_colorReady, Color.BLACK, m_alphaReady);
		}

		void setTitle(Canvas canvas, Paint[] paint, String str1, String str2) {
			Rect rect1 = outSize(paint[0], str1);
			Rect rect2 = outSize(paint[0], str2);

			if (str2 != null && !str2.equals("")) {
				int x1 = (m_width - rect1.width()) / 2;
				int y1 = (m_height - rect1.height() - rect2.height()) / 2 - 20 - m_playpos - TITLE_POS;
				int x2 = (m_width - rect2.width()) / 2;
				int y2 = (m_height - rect2.height()) / 2 + 20 - m_playpos - TITLE_POS;

				if (m_posHeightTitle >= 0) {
					y1 = m_posHeightTitle - m_sizeTitle / 2;
					y2 = m_posHeightTitle + m_sizeTitle / 2;
				}

				outText(canvas, paint, str1, x1, y1, m_colorTitle, Color.BLACK, m_alphaTitle);

				outText(canvas, paint, str2, x2, y2, m_colorTitle, Color.BLACK, m_alphaTitle);
			} else {
				int x = (m_width - rect1.width()) / 2;
				int y = (m_height - rect1.height()) / 2 + 20 - m_playpos - TITLE_POS;

				if (m_posHeightTitle >= 0) {
					y = m_posHeightTitle;
				}

				outText(canvas, paint, str1, x, y, m_colorTitle, Color.BLACK, m_alphaTitle);
			}
		}

		void setSinger(Canvas canvas, Paint[] paint, String str1, String str2, String str3) {
			String strSinger, strAuthor, strLyricsAuthor;

			// if ( m_data.getLyricsInfoTag().strLang.equals("EUC-JP") ) {
			if (m_lang == 0) {
				strSinger = SongData.SINGER_EN + ":" + str1;
				strAuthor = SongData.AUTHOR_EN + ":" + str2;
				strLyricsAuthor = SongData.LYRICSAUTHOR_EN + ":" + str3;
			} else if (m_lang == 1) {
				strSinger = SongData.SINGER_JP + ":" + str1;
				strAuthor = SongData.AUTHOR_JP + ":" + str2;
				strLyricsAuthor = SongData.LYRICSAUTHOR_JP + ":" + str3;
			} else {
				strSinger = SongData.SINGER + ":" + str1;
				strAuthor = SongData.AUTHOR + ":" + str2;
				strLyricsAuthor = SongData.LYRICSAUTHOR + ":" + str3;
			}

			if (!TextUtils.isEmpty(m_data.getSinger())) {
				strSinger = m_data.getSinger();
			}

			if (!TextUtils.isEmpty(m_data.getComposer())) {
				strAuthor = m_data.getComposer();
			}

			if (!TextUtils.isEmpty(m_data.getWriter())) {
				strLyricsAuthor = m_data.getWriter();
			}

			Rect rect1 = outSize(paint[0], strSinger);

			int margin = m_height - (rect1.height() * 2 + 10) - SINGER_POS;

			int ySinger = margin - m_playpos;
			int yAuthor = margin + rect1.height() + 5 - m_playpos;
			int yLAuthor = margin + rect1.height() * 2 + 10 - m_playpos;

			if (m_posHeightSinger >= 0) {
				ySinger = m_posHeightSinger;
				yAuthor = m_posHeightSinger + rect1.height() + 5;
				yLAuthor = m_posHeightSinger + rect1.height() * 2 + 10;
			}

			int x = 20;
			if (m_posWidthSinger >= 0) {
				x = m_posWidthSinger;
			}

			outText(canvas, paint, strSinger, x, ySinger, m_colorSinger, Color.BLACK, m_alphaSinger);
			outText(canvas, paint, strAuthor, x, yAuthor, m_colorSinger, Color.BLACK, m_alphaSinger);
			outText(canvas, paint, strLyricsAuthor, x, yLAuthor, m_colorSinger, Color.BLACK, m_alphaSinger);
		}

		void setLyrics(Canvas canvas, int type) {
			String str = m_strLine[type];

			int margin = m_align == TOP ? m_margin : m_height - m_margin - TEXT_SIZE_LYRICS * 2 + 5;
			int rubypos = m_align == TOP ? -RUBY_POS : RUBY_POS;

			if (m_ruby == true) {
				String rubyStr = Ruby.hanToRuby(str, m_lang);

				if (m_strLyrics[type] != str) {
					Rect rect = outSize(m_paintLyrics[0], str);
					Rect rcRuby = outSize(m_paintLyricsRuby[0], rubyStr);

					if (type == 0) {
						m_ptDrawPos[type].y = margin - m_playpos;
						m_ptDrawPosRuby[type].y = margin - m_playpos - rubypos;

						if (m_posHeightLyrics >= 0) {
							m_ptDrawPos[type].y = m_posHeightLyrics;
							m_ptDrawPosRuby[type].y = m_posHeightLyrics - rubypos;
						}
					} else {
						m_ptDrawPos[type].y = margin + rect.height() + 15 - m_playpos;
						m_ptDrawPosRuby[type].y = margin + rcRuby.height() + 15 - m_playpos - rubypos;

						if (m_posHeightLyrics >= 0) {
							m_ptDrawPos[type].y = m_posHeightLyrics + rcRuby.height() + 15;
							m_ptDrawPosRuby[type].y = m_posHeightLyrics + rcRuby.height() + 15 - rubypos;
						}
					}

					m_ptDrawPos[type].x = (m_width - rect.width()) / 2;
					m_ptDrawPosRuby[type].x = (m_width - rcRuby.width()) / 2;
					m_strLyrics[type] = str;
				}

				if (m_nInColor[type] == m_colorLyrics[1]) {
					outText(canvas, m_paintLyrics, str, m_ptDrawPos[type].x, m_ptDrawPos[type].y, m_nInColor[type], m_nOutColor[type], 0);
					outText(canvas, m_paintLyricsRuby, rubyStr, m_ptDrawPosRuby[type].x, m_ptDrawPosRuby[type].y, m_nInColor[type], m_nOutColor[type], 0);
				} else {
					outText(canvas, m_paintLyrics, str, m_ptDrawPos[type].x, m_ptDrawPos[type].y, m_nInColor[type], m_nOutColor[type], m_alphaLyrics);
					outText(canvas, m_paintLyricsRuby, rubyStr, m_ptDrawPosRuby[type].x, m_ptDrawPosRuby[type].y, m_nInColor[type], m_nOutColor[type], m_alphaLyrics);
				}
			} else {
				if (m_data.getLyricsInfoTag().strLang.equals("EUC-JP")) {
					if (m_strLyrics[type] != str) {
						Rect rect = outSize(m_paintLyrics[0], str);
						Rect rcRuby = outSize(m_paintLyricsRuby[0], m_strRuby2[type]);

						if (type == 0) {
							m_ptDrawPos[type].y = margin - m_playpos;
							m_ptDrawPosRuby[type].y = margin - m_playpos - rubypos;

							if (m_posHeightLyrics >= 0) {
								m_ptDrawPos[type].y = m_posHeightLyrics;
								m_ptDrawPosRuby[type].y = m_posHeightLyrics - rubypos;
							}
						} else {
							m_ptDrawPos[type].y = margin + rect.height() + 15 - m_playpos;
							m_ptDrawPosRuby[type].y = margin + rcRuby.height() + 15 - m_playpos - rubypos;

							if (m_posHeightLyrics >= 0) {
								m_ptDrawPos[type].y = m_posHeightLyrics + rcRuby.height() + 15;
								m_ptDrawPosRuby[type].y = m_posHeightLyrics + rcRuby.height() + 15 - rubypos;
							}
						}

						m_ptDrawPos[type].x = (m_width - rect.width()) / 2;
						m_ptDrawPosRuby[type].x = (m_width - rcRuby.width()) / 2;
						m_ptDrawPosJpRuby1[type].y = m_ptDrawPos[type].y - JP_POS;

						m_strLyrics[type] = str;
					}

					if (m_nInColor[type] == m_colorLyrics[1]) {
						outText(canvas, m_paintLyricsRuby, m_strRuby2[type], m_ptDrawPosRuby[type].x, m_ptDrawPosRuby[type].y, m_colorLyrics[0], m_colorLyricsBack[0], 0);
						outText(canvas, m_paintLyrics, str, m_ptDrawPos[type].x, m_ptDrawPos[type].y, m_nInColor[type], m_nOutColor[type], 0);
					} else {
						outText(canvas, m_paintLyricsRuby, m_strRuby2[type], m_ptDrawPosRuby[type].x, m_ptDrawPosRuby[type].y, m_colorLyrics[0], m_colorLyricsBack[0], m_alphaLyrics);
						outText(canvas, m_paintLyrics, str, m_ptDrawPos[type].x, m_ptDrawPos[type].y, m_nInColor[type], m_nOutColor[type], m_alphaLyrics);
					}

					int pos = 0;
					int cnt = 0;
					while (pos != -1) {
						int nextPos = m_strRuby1[type].indexOf("/", pos);
						if (nextPos == -1)
							break;

						String strRuby = "";
						try {
							strRuby = m_strRuby1[type].substring(pos, nextPos);
						} catch (IndexOutOfBoundsException e) {
						}

						if (strRuby.equals("") == false) {
							int size = 0;

							String prevStr = "";
							try {
								prevStr = str.substring(0, cnt + 1);
							} catch (IndexOutOfBoundsException e) {
							}

							String currStr = "";
							try {
								currStr = str.substring(cnt, cnt + 1);
							} catch (IndexOutOfBoundsException e) {
							}

							Rect prev = outSize(m_paintLyrics[0], prevStr);
							Rect curr = outSize(m_paintLyrics[0], currStr);
							Rect rcRuby = outSize(m_paintJpRuby1[0], strRuby);

							if (curr.width() > rcRuby.width())
								size = (curr.width() - rcRuby.width()) / 2;
							else
								size = -(rcRuby.width() - curr.width()) / 2;

							if (m_nInColor[type] == m_colorLyrics[1]) {
								outText(canvas, m_paintJpRuby1, strRuby, m_ptDrawPos[type].x + prev.width() - curr.width() + size, m_ptDrawPosJpRuby1[type].y, m_nInColor[type], m_nOutColor[type], 0);
							} else {
								outText(canvas, m_paintJpRuby1, strRuby, m_ptDrawPos[type].x + prev.width() - curr.width() + size, m_ptDrawPosJpRuby1[type].y, m_nInColor[type], m_nOutColor[type], m_alphaLyrics);
							}
						}

						pos = nextPos + 1;
						cnt++;
					}
				} else {
					if (m_strLyrics[type] != str) {
						Rect rect = outSize(m_paintLyrics[0], str);

						if (type == 0) {
							m_ptDrawPos[type].y = margin - m_playpos;

							if (m_posHeightLyrics >= 0) {
								m_ptDrawPos[type].y = m_posHeightLyrics;
							}
						} else {
							m_ptDrawPos[type].y = margin + rect.height() + 15 - m_playpos;

							if (m_posHeightLyrics >= 0) {
								m_ptDrawPos[type].y = m_posHeightLyrics + rect.height() + 15;
							}
						}

						m_ptDrawPos[type].x = (m_width - rect.width()) / 2;
						m_strLyrics[type] = str;
					}

					if (m_nInColor[type] == m_colorLyrics[1]) {
						outText(canvas, m_paintLyrics, str, m_ptDrawPos[type].x, m_ptDrawPos[type].y, m_nInColor[type], m_nOutColor[type], 0);
					} else {
						outText(canvas, m_paintLyrics, str, m_ptDrawPos[type].x, m_ptDrawPos[type].y, m_nInColor[type], m_nOutColor[type], m_alphaLyrics);
					}
				}
			}
		}

		int getLinePos() {
			return (m_nLinePos + m_nDisplay) % 2;
		}

		int timeOut(int nTime) {
			int nCodeFlag = -1;
			int nInterval = 0;
			int nState = SYNC_STATUS_NO;

			if (nTime < 0)
				nTime = 0;

			nInterval = nTime - m_nInterval;

			m_nInterval = nTime;

			if (nInterval < 0)
				nInterval = 0;

			m_nTickTime += nInterval;

			if (m_itor < m_data.getListSyncTag().size()) {
				if (m_nFirstTick != 0) {
					nState = SYNC_STATUS_NUM;

					int nPercent = (int) (100 * ((float) m_nTickTime / (float) m_nFirstTick));

					if (nPercent > 80)
						m_strReady = "GO";
					else if (nPercent > 64) // 1
						m_strReady = "1";
					else if (nPercent > 48) // 2
						m_strReady = "2";
					else if (nPercent > 32) // 3
						m_strReady = "3";
					else if (nPercent > 16) // 4
						m_strReady = "4";
					else if (nPercent >= 0) // 5
						m_strReady = "5";

					if (m_strReady.equals(m_strCount) == false) {
						if (m_listen == true) {
							int count = 0;
							try {
								count = Integer.valueOf(m_strReady);
							} catch (NumberFormatException e) {
							}
							onPlayViewListener.onReady(count);
						}

						m_strCount = m_strReady;
					}
				}

				if (m_data.getListSyncTag().get(m_itor).lTimeSyncStart <= nTime) {
					nCodeFlag = m_data.getListSyncTag().get(m_itor).nAttribute;
				}

				if (m_nEndTime > 0 && nTime > m_nEndTime + 2000) {
					if (m_data.getListSyncTag().get(m_itor).nLineDisplay % 2 == 1)
						m_nDisplay++;

					// if ( m_data.getLyricsInfoTag().strLang.equals("EUC-JP") ) {
					if (m_lang == 0) {
						m_strLine[0] = SongData.READY_EN;
					} else if (m_lang == 1) {
						m_strLine[0] = SongData.READY_JP;
					} else {
						m_strLine[0] = SongData.READY;
					}

					m_strLine[1] = "";

					m_strRuby1[0] = "";
					m_strRuby1[1] = "";
					m_strRuby2[0] = "";
					m_strRuby2[1] = "";

					m_readyinit = true;

					m_nEndTime = 0;
					nState = SYNC_STATUS_DIVISION;
				}

				switch (nCodeFlag) {
				case SYNC_ENDDIVISION:
				case SYNC_TEXT:
					if (!m_bReady) {
						for (int i = 0; i < 2; i++) {
							m_strLine[i] = SongUtil.byteToString(
									m_data.getListLyricsTag().get(
											m_data.getListSyncTag().get(m_itor).nLineDisplay + i)
											.strLineLyrics,
									m_data.getLyricsInfoTag().strLang);
							if (m_data.getLyricsInfoTag().strLang.equals("EUC-JP")) {
								m_strRuby1[i] = m_data.getListRuby1Tag().get(m_data.getListSyncTag().get(m_itor).nLineDisplay + i);
								m_strRuby2[i] = m_data.getListRuby2Tag().get(m_data.getListSyncTag().get(m_itor).nLineDisplay + i);
							}
						}

						m_nFirstTick = 1;
						m_bReady = true;
					}

					if (m_nFirstTick == 0 && m_data.getListSyncTag().get(m_itor).nPosLyrics == 0) {
						nState = SYNC_STATUS_START;
						m_nLinePos += 1;
					} else if (m_nFirstTick != 0) {
						nState = SYNC_STATUS_DEL;
						m_nLinePos = m_data.getListSyncTag().get(m_itor).nPosOneLine;
					} else {
						nState = SYNC_STATUS_TEXT;
						m_nLinePos = m_data.getListSyncTag().get(m_itor).nPosOneLine;
					}

					m_nFirstTick = 0;
					m_strReady = "";
					m_strCount = "";

					m_nSpanTime = (int) (m_data.getListSyncTag().get(m_itor).lTimeSyncEnd - m_data
							.getListSyncTag().get(m_itor).lTimeSyncStart);
					m_nTickTime = 1;

					int bufSize = m_data.getListSyncTag().get(m_itor).nPosLyrics;
					byte[] buff = new byte[bufSize + 1];
					for (int i = 0; i < bufSize; i++)
						buff[i] = m_data.getListLyricsTag()
								.get(m_data.getListSyncTag().get(m_itor).nPosOneLine).strLineLyrics[i];

					buff[bufSize] = '\0';

					int bufSize2 = m_data.getListSyncTag().get(m_itor).nPosLen;

					byte[] buff2 = new byte[bufSize2 + 1];
					for (int i = 0; i < bufSize2; i++) {
						buff2[i] = m_data.getListLyricsTag()
								.get(m_data.getListSyncTag().get(m_itor).nPosOneLine).strLineLyrics[bufSize + i];
					}

					buff2[bufSize2] = '\0';

					m_strPrev = SongUtil.byteToString(buff, m_data.getLyricsInfoTag().strLang);
					m_strGo = SongUtil.byteToString(buff2, m_data.getLyricsInfoTag().strLang);

					if ((m_data.getListSyncTag().get(m_itor).nNextDisplay == 1)
							&& (nState != SYNC_STATUS_DEL && m_data.getListSyncTag().get(m_itor).nLineDisplay < m_data
									.getListLyricsTag().size() - 1)) {
						m_strLine[(m_data.getListSyncTag().get(m_itor).nLineDisplay + m_nDisplay + 1) % 2] = SongUtil
								.byteToString(m_data.getListLyricsTag().get(
										m_data.getListSyncTag().get(m_itor).nLineDisplay + 1).strLineLyrics, m_data.getLyricsInfoTag().strLang);
						if (m_data.getLyricsInfoTag().strLang.equals("EUC-JP")) {
							m_strRuby1[(m_data.getListSyncTag().get(m_itor).nLineDisplay + m_nDisplay + 1) % 2] =
									m_data.getListRuby1Tag().get(m_data.getListSyncTag().get(m_itor).nLineDisplay + 1);
							m_strRuby2[(m_data.getListSyncTag().get(m_itor).nLineDisplay + m_nDisplay + 1) % 2] =
									m_data.getListRuby2Tag().get(m_data.getListSyncTag().get(m_itor).nLineDisplay + 1);
						}

						nState = SYNC_STATUS_NEXT;

						int pos = 1 - getLinePos();
						m_nInColor[pos] = m_colorLyrics[0];
						m_nOutColor[pos] = m_colorLyricsBack[0];

						m_line = getLinePos();
					}

					if (nCodeFlag == SYNC_ENDDIVISION) {
						m_nEndTime = (int) (m_data.getListSyncTag().get(m_itor).lTimeSyncEnd);
					}

					m_itor++;
					break;
				case SYNC_READY:
					for (int i = 0; i < 2; i++) {
						m_strLine[i] = SongUtil.byteToString(m_data.getListLyricsTag().get(
								m_data.getListSyncTag().get(m_itor).nLineDisplay + i).strLineLyrics, m_data.getLyricsInfoTag().strLang);

						if (m_data.getLyricsInfoTag().strLang.equals("EUC-JP")) {
							m_strRuby1[i] = m_data.getListRuby1Tag().get(m_data.getListSyncTag().get(m_itor).nLineDisplay + i);
							m_strRuby2[i] = m_data.getListRuby2Tag().get(m_data.getListSyncTag().get(m_itor).nLineDisplay + i);
						}
					}

					nState = SYNC_STATUS_READY;
					m_strPrev = "";
					m_strGo = "";
					m_nSpanTime = 0;
					m_nTickTime = 1;
					m_nFirstTick = (int) (m_data.getListSyncTag().get(m_itor).lTimeSyncEnd - m_data
							.getListSyncTag().get(m_itor).lTimeSyncStart);
					m_bReady = true;

					m_itor++;
					break;
				}
			}

			if (getLinePos() != m_line) {
				if (m_readyinit == false) {
					m_nInColor[m_line] = m_colorLyrics[1];
					m_nOutColor[m_line] = m_colorLyricsBack[1];
				}

				m_line = getLinePos();
				m_readyinit = false;
			}

			return nState;
		}

		void rePaint(int nTime) {
			init();

			int nCodeFlag = -1;
			int nState = SYNC_STATUS_NO;

			m_nInterval = nTime - 1;

			for (int itor = 0; itor < m_data.getListSyncTag().size(); itor++) {
				nCodeFlag = -1;
				nState = SYNC_STATUS_NO;

				if (m_nFirstTick != 0) {
					nState = SYNC_STATUS_NUM;
					int nPercent = (int) (100 * ((float) m_nTickTime / (float) m_nFirstTick));

					if (nPercent > 95)
						m_strReady = "GO";
					else if (nPercent > 80) // 1
						m_strReady = "1";
					else if (nPercent > 60) // 2
						m_strReady = "2";
					else if (nPercent > 40) // 3
						m_strReady = "3";
					else if (nPercent > 20) // 4
						m_strReady = "4";
					else if (nPercent >= 0) // 5
						m_strReady = "5";

					if (m_strReady.equals(m_strCount) == false) {
						if (m_listen == true) {
							try {
								Integer.valueOf(m_strReady);
							} catch (NumberFormatException e) {
							}

							// onPlayViewListener.onReady(count);
						}

						m_strCount = m_strReady;
					}
				}

				if (m_data.getListSyncTag().get(itor).lTimeSyncStart <= nTime) {
					nCodeFlag = m_data.getListSyncTag().get(itor).nAttribute;
				}

				if (m_nEndTime > 0 && nTime > m_nEndTime + 2000) {
					if (m_data.getListSyncTag().get(itor).nLineDisplay % 2 == 1)
						m_nDisplay++;

					// if ( m_data.getLyricsInfoTag().strLang.equals("EUC-JP") ) {
					if (m_lang == 0) {
						m_strLine[0] = SongData.READY_EN;
					} else if (m_lang == 1) {
						m_strLine[0] = SongData.READY_JP;
					} else {
						m_strLine[0] = SongData.READY;
					}

					m_strLine[1] = "";

					m_strRuby1[0] = "";
					m_strRuby1[1] = "";
					m_strRuby2[0] = "";
					m_strRuby2[1] = "";

					m_readyinit = true;

					m_nEndTime = 0;
					nState = SYNC_STATUS_DIVISION;
				}

				switch (nCodeFlag) {
				case SYNC_ENDDIVISION:
				case SYNC_TEXT:
					if (!m_bReady) {
						for (int i = 0; i < 2; i++) {
							m_strLine[i] = SongUtil.byteToString(m_data.getListLyricsTag().get(
									m_data.getListSyncTag().get(itor).nLineDisplay + i).strLineLyrics, m_data.getLyricsInfoTag().strLang);
							if (m_data.getLyricsInfoTag().strLang.equals("EUC-JP")) {
								m_strRuby1[i] = m_data.getListRuby1Tag().get(m_data.getListSyncTag().get(itor).nLineDisplay + i);
								m_strRuby2[i] = m_data.getListRuby2Tag().get(m_data.getListSyncTag().get(itor).nLineDisplay + i);

							}
						}

						m_nFirstTick = 1;
						m_bReady = true;
					}

					if (m_nFirstTick == 0 && m_data.getListSyncTag().get(itor).nPosLyrics == 0) {
						nState = SYNC_STATUS_START;
						m_nLinePos += 1;
					} else if (m_nFirstTick != 0) {
						nState = SYNC_STATUS_DEL;
						m_nLinePos = m_data.getListSyncTag().get(itor).nPosOneLine;
					} else {
						nState = SYNC_STATUS_TEXT;
						m_nLinePos = m_data.getListSyncTag().get(itor).nPosOneLine;
					}

					m_nFirstTick = 0;
					m_strReady = "";
					m_strCount = "";

					m_nSpanTime = (int) (m_data.getListSyncTag().get(itor).lTimeSyncEnd - m_data
							.getListSyncTag().get(itor).lTimeSyncStart);
					m_nTickTime = 1;

					if ((m_data.getListSyncTag().get(itor).nNextDisplay == 1)
							&& (nState != SYNC_STATUS_DEL && m_data.getListSyncTag().get(itor).nLineDisplay < m_data
									.getListLyricsTag().size() - 1)) {
						m_strLine[(m_data.getListSyncTag().get(itor).nLineDisplay + m_nDisplay + 1) % 2] = SongUtil
								.byteToString(m_data.getListLyricsTag().get(
										m_data.getListSyncTag().get(itor).nLineDisplay + 1).strLineLyrics, m_data.getLyricsInfoTag().strLang);

						if (m_data.getLyricsInfoTag().strLang.equals("EUC-JP")) {
							m_strRuby1[(m_data.getListSyncTag().get(itor).nLineDisplay + m_nDisplay + 1) % 2] =
									m_data.getListRuby1Tag().get(m_data.getListSyncTag().get(itor).nLineDisplay + 1);
							m_strRuby2[(m_data.getListSyncTag().get(itor).nLineDisplay + m_nDisplay + 1) % 2] =
									m_data.getListRuby2Tag().get(m_data.getListSyncTag().get(itor).nLineDisplay + 1);
						}

						nState = SYNC_STATUS_NEXT;

						int pos = 1 - getLinePos();
						m_nInColor[pos] = m_colorLyrics[0];
						m_nOutColor[pos] = m_colorLyricsBack[0];

						m_line = getLinePos();
					}

					if (nCodeFlag == SYNC_ENDDIVISION) {
						m_nEndTime = (int) (m_data.getListSyncTag().get(itor).lTimeSyncEnd);
					}

					break;
				case SYNC_READY:
					for (int i = 0; i < 2; i++) {
						m_strLine[i] = SongUtil.byteToString(m_data.getListLyricsTag().get(
								m_data.getListSyncTag().get(itor).nLineDisplay + i).strLineLyrics, m_data.getLyricsInfoTag().strLang);

						if (m_data.getLyricsInfoTag().strLang.equals("EUC-JP")) {
							m_strRuby1[i] = m_data.getListRuby1Tag().get(m_data.getListSyncTag().get(itor).nLineDisplay + i);
							m_strRuby2[i] = m_data.getListRuby2Tag().get(m_data.getListSyncTag().get(itor).nLineDisplay + i);
						}
					}

					nState = SYNC_STATUS_READY;
					m_strPrev = "";
					m_strGo = "";
					m_nSpanTime = 0;
					m_nTickTime = 1;
					m_nFirstTick = (int) (m_data.getListSyncTag().get(itor).lTimeSyncEnd - m_data
							.getListSyncTag().get(itor).lTimeSyncStart);
					m_bReady = true;

					break;
				}

				if (getLinePos() != m_line) {
					if (m_readyinit == false) {
						m_nInColor[m_line] = m_colorLyrics[1];
						m_nOutColor[m_line] = m_colorLyricsBack[1];
					}

					m_line = getLinePos();
					m_readyinit = false;
				}

				m_itor = itor;

				if (m_data.getListSyncTag().get(itor).lTimeSyncStart >= nTime) {
					break;
				}
			}
		}
	}

	@Override
	public SongData getSongData() {

		return m_data;
	}
}
