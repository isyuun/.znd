package kr.kymedia.karaoke.play3;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class KaraokePlay extends Activity {
	// AudioRecordPlay record = null;
	Player player = null;
	Player player2 = null;
	// Score score = null;
	String str;
	SeekBar mSeekBar;
	TextView textView;
	Handler handler;
	Button btn;
	int seekValue = -1;

	// AudioRecordPlay recorder = null;
	// CameraPreview mPreview;

	// private class DummyClass {
	// }

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/*
		 * final boolean SUPPORT_STRICT_MODE = Build.VERSION_CODES.FROYO < Build.VERSION.SDK_INT;
		 * final boolean SUPPORT_HONEYCOMB = Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT;
		 * 
		 * boolean debaggable = (getApplicationInfo().flags
		 * & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		 * 
		 * if (SUPPORT_STRICT_MODE && debaggable) {
		 * StrictMode.ThreadPolicy.Builder threadPolicy = new StrictMode.ThreadPolicy.Builder();
		 * //threadPolicy.detectNetwork();
		 * //threadPolicy.detectDiskReads();
		 * //threadPolicy.detectDiskWrites();
		 * threadPolicy.detectAll();
		 * threadPolicy.penaltyLog();
		 * threadPolicy.penaltyDropBox();
		 * threadPolicy.penaltyDialog();
		 * 
		 * if (SUPPORT_HONEYCOMB) {
		 * threadPolicy.detectCustomSlowCalls();
		 * threadPolicy.penaltyDeathOnNetwork();
		 * threadPolicy.penaltyFlashScreen();
		 * }
		 * 
		 * StrictMode.setThreadPolicy(threadPolicy.build());
		 * 
		 * StrictMode.VmPolicy.Builder vmPolicy = new StrictMode.VmPolicy.Builder();
		 * vmPolicy.detectLeakedSqlLiteObjects();
		 * vmPolicy.penaltyLog();
		 * vmPolicy.penaltyDeath();
		 * vmPolicy.penaltyDropBox();
		 * 
		 * if (SUPPORT_HONEYCOMB) {
		 * vmPolicy.detectActivityLeaks();
		 * vmPolicy.detectLeakedClosableObjects();
		 * vmPolicy.setClassInstanceLimit(DummyClass.class, 1);
		 * }
		 * StrictMode.setVmPolicy(vmPolicy.build());
		 * }
		 */

		super.onCreate(savedInstanceState);

		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		// getWindow().setBackgroundDrawable(null);
		// setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_Transparent);
		// getWindow().setBackgroundDrawableResource(R.style.Theme_Transparent);
		// getWindow().setBackgroundDrawableResource(android.R.style.Theme_Translucent);
		// setTheme(R.style.Theme_Transparent);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 세로 화면으로 고정할 경우
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 가로 화면으로 고정할 경우

		// String url = "aHR0cDsvL3Jnc291cWNlLm95bWVhaWEubXIvcmJjb3JsL2twZnAvMjoxMzA/MDIvOjQvMT4wNDA8Mkc5QVBRVSY0Lm0lYQ==";
		// String url = "XkdNQA0cFkJdQFZFS1BcHlFKVFVfWlgeV0EWQlhQVkJaHFJAUEMWAnACCgB1AwsfdAcWAXADDQB2AX4JC2NoZXAHF11zUg==";
		// String temp = SongUtil.makeDecryption("", new String(Base64.decode(url.replace(" ", "+"), 0)));
		// Log.d("KaraokePlay", "temp:"+temp);

		// 플레이어 뷰 생성
		player = new Player(this);
		player2 = new Player(this);
		// score = new Score();
		// score.setSongScoreListener(onSongScoreListener);
		/*
		 * // 배경 설정
		 * if ( player.getViewType() == 0 )
		 * {
		 * player.setImage("bg_player_screen_play", 0, 0, 480, 577, 1, 2, true);
		 * player.setImage("ani_top_character", 165, 317, 41, 58, 3, 2, true);
		 * player.setImage("ani_bottom_character", 342, 467, 51, 72, 8, 5, true);
		 * 
		 * player.setStatusImage("img_player", 109, 365, 258, 145, 3);
		 * } else {
		 * player.setImage("bg_player_screen_record", 0, 0, 480, 577, 1, 1, false);
		 * player.setImage("ani_playing_record", 84, 168, 307, 302, 8, 8, true);
		 * player.setImage("img_musicnote", 308, 136, 67, 165, 1, 1, false);
		 * player.setImage("img_player_playing", 15, 83, 227, 113, 0.5f, 2, true);
		 * }
		 * player.setReadyImage("bg_player_countdown", 204, 276, 96, 86, 1);
		 */

		player.setBackground();
		player2.setBackgroundColor(128, 128, 128);
		player.setBackgroundTransparent(true);
		// player.setBackgroundColor(128, 128, 128);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int height = metrics.heightPixels;
		int width = metrics.widthPixels;

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// mPreview = new CameraPreview(this);
		// setContentView(mPreview);

		// LinearLayout.LayoutParams playParam = new LinearLayout.LayoutParams(width, height);
		// addContentView(player.getPlayView(), playParam);

		// ((SurfaceView)player.getPlayView()).setBackgroundColor(Color.TRANSPARENT);
		setContentView((View) player2.getPlayView());

		// setContentView(R.layout.main);
		addContentView((View) player.getPlayView(), new LinearLayout.LayoutParams(width, height));

		// player.setTitle(true, 100, 20, Color.CYAN, 156);
		// player.setSinger(true, 100, 200, 15, Color.RED, 156);
		// player.setReady(true, 50, 15, Color.GREEN, 156);
		// player.setLyrics(300, 20, Color.YELLOW, Color.MAGENTA, 156);

		// recorder = AudioRecordPlay.getInstanse();

		// 재생
		play();
		// record = new AudioRecordPlay();
		// record.load("");
		// record.play();

		// 검색바 설정
		mSeekBar = new SeekBar(this);
		mSeekBar.setProgress(0);

		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (seekValue > -1) {
					player.seek(seekValue);
				}

				seekValue = -1;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser == true) {
					seekValue = progress;
				}
			}
		});

		LinearLayout linear = new LinearLayout(this);
		linear.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, height);
		LinearLayout.LayoutParams paramSb = new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		paramSb.setMargins(0, height - 130, 0, 0);
		linear.addView(mSeekBar, paramSb);
		addContentView(linear, param);

		textView = new TextView(this);
		textView.setText("00:00/00:00");
		textView.setTextColor(Color.WHITE);

		LinearLayout linear2 = new LinearLayout(this);
		linear2.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(width, height);
		LinearLayout.LayoutParams paramSb2 = new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		paramSb2.setMargins(0, height - 155, 0, 0);
		linear2.addView(textView, paramSb2);
		addContentView(linear2, param2);

		// -1(출력없음), 0(선형그래프), 1(막대그래프), 2(원형그래프), 3(원형막대그래프), 4(선형단일)
		str = "출력없음";
		btn = new Button(this);
		btn.setText(str);

		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (str.equals("출력없음")) {
					// player.resume();
					str = "선형그래프";
					btn.setText(str);
					player.setVision(0);
				} else if (str.equals("선형그래프")) {
					// player.pause();
					str = "막대그래프";
					btn.setText(str);
					player.setVision(1);
				} else if (str.equals("막대그래프")) {
					str = "원형그래프";
					btn.setText(str);
					player.setVision(2);
				} else if (str.equals("원형그래프")) {
					str = "원형막대그래프";
					btn.setText(str);
					player.setVision(3);
				} else if (str.equals("원형막대그래프")) {
					str = "선형단일";
					btn.setText(str);
					player.setVision(4);
				} else if (str.equals("선형단일")) {
					str = "선형그래프";
					btn.setText(str);
					player.setVision(0);
				}
				// player.setView(false);
				// player.setRedraw(true);
			}
		});

		LinearLayout linear3 = new LinearLayout(this);
		linear3.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(width, height);
		LinearLayout.LayoutParams paramSb3 = new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		paramSb3.setMargins(0, height - 250, 0, 0);
		linear3.addView(btn, paramSb3);
		addContentView(linear3, param3);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				updateUI(msg);
			}
		};
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// recorder.close();
		// Log.e("KaraokePlay", "destroy");
		player.destroy();
	}

	@Override
	public void onStop() {
		super.onStop();

		// recorder.close();
		// Log.e("KaraokePlay", "destroy");
		player.destroy();
	}

	public void updateUI(Message msg) {
		if (msg.what == 1) {
			textView.setText((String) msg.obj);
		} else if (msg.what == 2) {
			mSeekBar.setSecondaryProgress(msg.arg1);
		}
	}

	public void play() {
		// 암호화된 경로
		// String mp3 = "XkdNQA0cFgIJAhcCCgUXAQMDFwELAAMIDAsJH05FWm9TVl1ZXhxUXTAAFgR2BwADbF5JAw==";
		// String lyc = "XkdNQA0cFlNBXkoeWltWQk9AF1NUHVJCE1BAW1xBWG9aXwseXkBJDzNcV1ceWl0NdgQNCXA=";
		// String mp3 = "XkdNQA0cFlNBXkoeWltWQk9AF1NUHVJCE1BAW1xBWG9aXwseXkBJDzNcV1ceWl0NdgYPBXIfUUQwQwMfakRYQGheVlIuX1xfJh1aX2dYSx8nXFtZJ1YWBHkFDAFjXlBU";
		// String lyc = "";
		// mp3 = "XkdNQA0cFkJdQFZFS1BcHlFKVFVfWlgeV0EWQlhQVkJaHFJAUEMWAnACCgB1Ag4fdQcWAXADDQFzenB9DmV8fHEHF11zUg==";

		// mp3 설정
		player.setSongPlayListener(onSongPlayListener);
		// player.setSongRecordListener(onSongScoreListener);
		// player.load(mp3);

		Log.d("KaraokePlay", "play");

		player.setUseSystem(true);

		AssetFileDescriptor afd;
		try {
			afd = getAssets().openFd("03314.skym");
			// 211.236.190.103
			player.open("http://211.236.190.103:8080/svc_media/mmp3/03314.mp3", afd.createInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// player.close();
		// player.load(mp3, lyc, false);
		// player.setSyncTime(-300);
		// if ( ret == true ) {
		// // 미디어 리스너
		// player.setSongPlayListener(onSongPlayListener);
		// }
	}

	SongPlayListener onSongPlayListener = new SongPlayListener() {
		// 현재 재생 시간
		@Override
		@SuppressLint("DefaultLocale")
		public void onTime(int t) {
			mSeekBar.setProgress(t);

			(new Thread(new Runnable() {
				@Override
				public void run() {
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = String.format("%02d:%02d/%02d:%02d", mSeekBar.getProgress() / 60000, mSeekBar.getProgress() % 60, player.totalTime() / 60000, player.totalTime() % 60);
					handler.sendMessage(msg);
				}
			})).start();
		}

		// 재생 준비 완료
		@Override
		public void onPrepared() {
			Log.e("KaraokePlay", "onPrepared");
			if (player != null) {
				// player.play(0);
				player.play();
				// player.record(Environment.getExternalStorageDirectory() + "/record.mp3");
				// recorder.load(Environment.getExternalStorageDirectory() + "/record.mp3");
				// player.setVision(-1);

				// mSeekBar.setMax(player.totalTime());
			}
		}

		// 재생 완료
		@Override
		public void onCompletion() {
			Log.e("KaraokePlay", "onCompletion");
			if (player != null) {
				// Log.d("MainActivity", "score:"+player.getScore());
				player.stop();
				// score.load("", "");
				play();
			}
		}

		// 재생중 오류
		@Override
		public void onError() {
			Log.e("KaraokePlay", "MediaPlayer Error");
		}

		// 버퍼링 사이즈
		@Override
		public void onBufferingUpdate(int percent) {
			Log.e("KaraokePlay", "onBufferingUpdate:" + percent);
			final int secondPercent = percent * mSeekBar.getMax() / 100;

			(new Thread(new Runnable() {
				@Override
				public void run() {
					Message msg = handler.obtainMessage();
					msg.what = 2;
					msg.arg1 = secondPercent;
					handler.sendMessage(msg);
				}
			})).start();
		}

		@Override
		public void onSeekComplete()
		{
			Log.e("KaraokePlay", "onSeekComplete");
		}

		@Override
		public void onRelease()
		{
			Log.e("KaraokePlay", "onRelease");
		}

		@Override
		public void onReady(int count)
		{
			Log.e("KaraokePlay", "onReady:" + count);
		}
	};
}
