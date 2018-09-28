/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 2015 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.TV
 * filename	:	_SongPlayerView2.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.kykaraoke.play
 *    |_ _SongPlayerView2.java
 * </pre>
 * 
 */

package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;

import kr.keumyoung.karaoke.widget.KaraokePath;
import kr.kymedia.karaoke.util.TextUtil;

/**
 * <pre>
 * 반주곡/녹음곡 스트리밍처리
 * </pre>
 *
 * @author isyoon
 * @since 2015. 2. 3.
 * @version 1.0
 */
class PlayView3 extends PlayView2X {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public PlayView3(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public PlayView3(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PlayView3(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayView3(Context context) {
		super(context);
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();
		mOnCompletionListener = null;
		mOnErrorListener = null;
		mOnInfoListener = null;
		mOnPreparedListener = null;
	}

	/**
	 * <pre>
	 * 211.236.190.103
	 * 0:금영서버: http://211.236.190.103:8080/svc_media/mmp3/08888.mp3
	 * 1:KYM서버: http://resource.kymedia.kr/ky/mp/88/08888.mp3
	 * 2:싸이월드: http://cyms.chorus.co.kr/cykara_dl2.asp?song_id=08888
	 * </pre>
	 */
	int server = 0;

	public String setKaraokeMP3Server() {

		server++;
		if (server > 2) {
			server = 0;
		}

		String ret = "[server:" + server + "]" + getKaraokeMP3(8888);
		return ret;
	}

	public String getKaraokeMP3(int number) {
		String ret;

		// 금영서버: http://211.236.190.103:8080/svc_media/mmp3/08888.mp3
		// KYM서버: http://resource.kymedia.kr/ky/mp/88/08888.mp3
		// 싸이월드: http://cyms.chorus.co.kr/cykara_dl2.asp?song_id=08888
		String host = "http://211.236.190.103:8080/svc_media/";
		String path = "mmp3";
		String form = "/%05d.mp3";

		// KYM서버: http://resource.kymedia.kr/ky/mp/88/08888.mp3
		// host = "http://resource.kymedia.kr/ky/mp/";
		// load = String.format(getResources().getConfiguration().locale, "%05d", number).substring(3);
		// form = "/%05d.mp3";

		// if (server == R.id.radioServerKYS) {
		// //금영서버: http://211.236.190.103:8080/svc_media/mmp3/08888.mp3
		// host = "http://" + KaraokePath.getHosts()[0] + ":8080/svc_media/";
		// load = "mmp3";
		// form = "/%05d.mp3";
		// } else if (server == R.id.radioServerKYM) {
		// //KYM서버: http://resource.kymedia.kr/ky/mp/88/08888.mp3
		// host = "http://" + KaraokePath.getHosts()[1] + "/ky/mp/";
		// load = String.format(getResources().getConfiguration().locale, "%05d", number).substring(3);
		// form = "/%05d.mp3";
		// } else if (server == R.id.radioServerCYW) {
		// //싸이월드: http://cyms.chorus.co.kr/cykara_dl2.asp?song_id=08888
		// host = "http://" + KaraokePath.getHosts()[2] + "/";
		// load = "cykara_dl2.asp?song_id=";
		// form = "%05d";
		// }
		switch (server) {
		case 0:
			// 금영서버: http://211.236.190.103:8080/svc_media/mmp3/08888.mp3
			host = "http://" + KaraokePath.getHosts()[0] + ":8080/svc_media/";
			path = "mmp3";
			form = "/%05d.mp3";
			break;

		case 1:
			// KYM서버: http://resource.kymedia.kr/ky/mp/88/08888.mp3
			host = "http://" + KaraokePath.getHosts()[1] + "/ky/mp/";
			path = String.format(getResources().getConfiguration().locale, "%05d", number).substring(3);
			form = "/%05d.mp3";
			break;

		case 2:
			// 싸이월드: http://cyms.chorus.co.kr/cykara_dl2.asp?song_id=08888
			host = "http://" + KaraokePath.getHosts()[2] + "/";
			//load = "cykara_dl2.asp?song_id=";
			path = ".skym.asp?song_id=";
			form = "%05d";
			break;

		default:
			break;
		}

		ret = String.format(host + path + form, number);

		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + server + "-" + number + "-" + ret);

		return ret;
	}

	String song_id = "99999";

	public void setSongId(String song_id) {

		this.song_id = song_id;
	}

	String path;

	public String getPath() {
		return path;
	}

	String mp3;

	public void setMp3(String mp3) {
		this.mp3 = mp3;
	}

	public void open() throws Exception {

		// String load = getApplicationContext().getExternalFilesDir(null) + "/test.skym";
		// if (getLyricList() == null || getLyricList().size() == 0) {
		// load = getApplicationContext().getExternalFilesDir(null) + "/test.skym";
		// } else {
		// load = getLyricList().get(getLyricIdx()).toString();
		// }
		String path = getLyric();
		if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + path);
		load(path);
	}

	/**
	 * skym가사로드
	 * mp3스트리밍
	 * @throws Exception
	 * @see kr.keumyoung.karaoke.play.PlayView2#load(java.lang.String)
	 */
	@Override
	protected boolean load(String path) throws Exception {

		Log.w(__CLASSNAME__, "load() " + "[ST]");

		if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + path);
		this.path = path;

		try {
			Log.i(__CLASSNAME__, "load() " + "[getSongData]");
			getSongData().release();
			getSongData().load(path);

			Log.i(__CLASSNAME__, "load() " + "[load]");
			// (new load()).execute();
			if (TextUtil.isNetworkUrl(mp3)) {
				stream();
			} else {
				local();
			}

		} catch (Exception e) {
			Log.e(__CLASSNAME__, "load() " + "[NG]" + "\n" + Log.getStackTraceString(e));
			e.printStackTrace();
			stop();
			throw (e);
		}

		// if (BuildConfig.DEBUG) _LOG.e(__CLASSNAME__, getMethodName() + "[ED]" + load);
		Log.w(__CLASSNAME__, "load() " + "[ED]");

		return true;
	}

	// /**
	// * <pre>
	// * 쓸데없이시발~~~
	// * </pre>
	// *
	// * @author isyoon
	// *
	// */
	// private class load extends AsyncTask<Void, Integer, String> {
	//
	// @Override
	// protected String doInBackground(Void... params) {
	//
	// _LOG.i(__CLASSNAME__, "load() " + "[doInBackground]");
	// try {
	// if (TextUtil.isNetworkUrl(mp3)) {
	// stream();
	// } else {
	// local();
	// }
	// } catch (Exception e) {
	//
	// _LOG.e(__CLASSNAME__, "load() " + "[doInBackground]" + "[NG]" + "\n" + _LOG.getStackTraceString(e));
	// e.printStackTrace();
	// }
	// return null;
	// }
	// }

	protected void stream() throws Exception {
		Log.w(__CLASSNAME__, "stream() " + "[ST]");

		if (!TextUtil.isNetworkUrl(mp3)) {
			Log.i(__CLASSNAME__, "stream() " + "[getKaraokeMP3]");
			int number = Integer.parseInt(song_id);
			mp3 = getKaraokeMP3(number);
		}

		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + mp3);

		Log.i(__CLASSNAME__, "stream() " + "[MediaPlayer]");
		init();

		Log.i(__CLASSNAME__, "stream() " + "[setAudioStreamType]");
		getMediaPlayer().setAudioStreamType(AudioManager.STREAM_MUSIC);

		Log.i(__CLASSNAME__, "stream() " + "[setDataSource]");
		getMediaPlayer().setDataSource(mp3);

		Log.i(__CLASSNAME__, "stream() " + "[prepareAsync]");
		getMediaPlayer().prepareAsync();

		Log.w(__CLASSNAME__, "stream() " + "[ED]");
	}

	protected void local() throws Exception {
		Log.w(__CLASSNAME__, "local() " + "[ST]");

		if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[ST]" + path);
		File sourceFile = new File(path);

		if (sourceFile.exists()) {
			FileInputStream fs = new FileInputStream(sourceFile);
			FileDescriptor fd = fs.getFD();
			getMediaPlayer().setDataSource(fd);
			fs.close(); // 데이터 소스를 설정한 후 스트림을 닫았다.
			getMediaPlayer().prepare();
		}

		Log.e(__CLASSNAME__, "local() " + "[ED]");
	}

	public void playLyrics() throws Exception {
		if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[ST]");
		try {
			getLyricsPlay().play();
		} catch (Exception e) {

			if (BuildConfig.DEBUG) Log.w(__CLASSNAME__ + TAG_ERR,  "[NG]" + getMethodName());
			// e.printStackTrace();
			throw (e);
		}
		if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[ED]");
	}

	/**
	 * @see kr.keumyoung.karaoke.play.PlayView2#play()
	 */
	@Override
	public boolean play() throws Exception {
		boolean ret = super.play();
		return ret;
	}

	@Override
	public void setVisibility(int visibility) {

		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + visibility);
		super.setVisibility(visibility);
		if (getLyricsPlay() != null) {
			getLyricsPlay().setVisibility(visibility);
		}
	}

	/**
	 * @see kr.keumyoung.karaoke.play.PlayView2#stop()
	 */
	@Override
	public void stop() {
		super.stop();
		if (BuildConfig.DEBUG) Log.w(__CLASSNAME__, getMethodName() + "[RESET]" + isPlaying() + ":" + getPlayState());
		reset();
	}

	/**
	 * @see kr.keumyoung.karaoke.play.PlayView2#pause()
	 */
	@Override
	protected void pause() {
		super.pause();
	}

	@Override
	protected void resume() {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName());
		super.resume();
	}

	/**
	 * @see kr.keumyoung.karaoke.play.PlayView2#close()
	 */
	@Override
	protected void close() {
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName());
		super.close();
		stop();
	}

}
