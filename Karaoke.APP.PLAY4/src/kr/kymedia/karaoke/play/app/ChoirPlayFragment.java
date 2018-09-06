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
 * 2014 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.PLAY4
 * filename	:	ChurusPlayFragment.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2.app
 *    |_ ChurusPlayFragment.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app;

import java.util.ArrayList;

import kr.kymedia.karaoke.os.AsycTaskExcuter;
import kr.kymedia.karaoke.os.PriorityAsyncTask;
import kr.kymedia.karaoke.play._SoundTouchPlay;
import kr.kymedia.karaoke.play.app.view.SongPlayView;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

/**
 * 
 * TODO<br>
 * 
 * <pre></pre>
 * 
 * @author isyoon
 * @since 2014. 7. 18.
 * @version 1.0
 */
public class ChoirPlayFragment extends SongPlayFragment {

	private ViewGroup choir;
	private final ArrayList<SongPlayView> choirs = new ArrayList<SongPlayView>();

	public ArrayList<SongPlayView> getChoirs() {
		return choirs;
	}

	public ViewGroup getChoir() {
		return choir;
	}

	protected void setChoir(int id) {
		choir = (ViewGroup) findViewById(id);
	}

	protected void setChoirs() {
		Log.e(toString(), getMethodName() + choirs);
	}

	public void addChoir(int id, SongPlayView view) {
		if (choir == null) {
			setChoir(id);
		}

		if (choirs.size() < _SoundTouchPlay.MAX_CHOIRS) {
			choir.addView(view);
			choirs.add(view);
		} else {
			removeChoir(id, view);
		}

		setChoirs();
	}

	public void removeChoir(int id, SongPlayView view) {
		if (choir == null) {
			setChoir(id);
		}

		choir.removeView(view);
		choirs.remove(view);

		setChoirs();
	}

	SongPlayView findChoir(int index) {
		try {
			return (SongPlayView) choir.getChildAt(index);
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	public void putLead(SongPlayView view) {
		if (view == null) {
			return;
		}

		choirs.remove(view);
		choirs.add(0, view);
		setChoirs();
	}

	public void delLead(SongPlayView view) {
		if (view == null) {
			return;
		}

		choirs.remove(view);
		setChoirs();
	}

	protected SongPlayView getLead() {
		if (choirs.size() > 0) {
			return choirs.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {

		super.onResume();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

		super.onServiceConnected(name, service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {

		super.onServiceDisconnected(name);
	}

	@Override
	public boolean load(String path) throws Exception {

		// setPath(path);
		super.load(path);

		for (SongPlayView choir : choirs) {
			AsycTaskExcuter.executePriorityAsyncTask(new open(), choir);
		}

		return true;
	}

	protected class open extends PriorityAsyncTask<SongPlayView, Void, Void> {

		@Override
		protected Void doInBackground(SongPlayView... params) {

			for (SongPlayView param : params) {
				try {
					if (TextUtils.isEmpty(param.getPath())) {
						param.load(getPath());
					} else {
						param.load(param.getPath());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

	}

	@Override
	public boolean play() {

		super.play();

		for (SongPlayView choir : choirs) {
			play play = new play();
			AsycTaskExcuter.executePriorityAsyncTask(play, choir);
		}

		return true;
	}

	protected class play extends PriorityAsyncTask<SongPlayView, Void, Void> {

		@Override
		protected Void doInBackground(SongPlayView... params) {

			for (SongPlayView param : params) {
				param.play();
			}
			return null;
		}

	}

	@Override
	public void stop() {

		super.stop();

		for (SongPlayView choir : choirs) {
			AsycTaskExcuter.executePriorityAsyncTask(new stop(), choir);
		}
	}

	protected class stop extends PriorityAsyncTask<SongPlayView, Void, Void> {

		@Override
		protected Void doInBackground(SongPlayView... params) {

			for (SongPlayView param : params) {
				param.stop();
			}
			return null;
		}

	}

	public void seek() {
		seek(this.seek);
	}

	@Override
	public void seek(int msec) {

		this.seek = msec;

		super.seek(msec);

		for (SongPlayView choir : choirs) {
			seek seek = new seek();
			AsycTaskExcuter.executePriorityAsyncTask(seek, choir);
			// choir.seek(seek);
		}
	}

	protected int seek = 0;

	protected class seek extends PriorityAsyncTask<SongPlayView, Void, Void> {

		@Override
		protected Void doInBackground(SongPlayView... params) {

			for (SongPlayView param : params) {
				param.seek(seek);
			}
			return null;
		}

	}

	@Override
	public boolean isPlaying() {

		return super.isPlaying();
	}

	@Override
	public boolean isPause() {

		return super.isPause();
	}

	@Override
	public void pause() {

		super.pause();

		for (SongPlayView choir : choirs) {
			AsycTaskExcuter.executePriorityAsyncTask(new pause(), choir);
			// choir.pause();
		}

		seek(getLead().getCurrentTime());
	}

	protected class pause extends PriorityAsyncTask<SongPlayView, Void, Void> {

		@Override
		protected Void doInBackground(SongPlayView... params) {

			for (SongPlayView param : params) {
				param.pause();
			}
			return null;
		}

	}

	/**
	 * <pre>
	 * 각자따로간다.
	 * </pre>
	 */
	@Override
	public void setPitch(int pitch) {

		super.setPitch(pitch);
	}

	public void setPitch(int index, int pitch) {

		try {
			findChoir(index).setPitch(pitch);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void tempo() {
		setTempoPercent(this.percent);
	}

	@Override
	public void setTempo(float tempo) {
		//super.setTempo(tempo);
		int percent = (int) (tempo * 100.0f - 100.0f);
		setTempoPercent(percent);
	}

	@Override
	public void setTempoPercent(int percent) {

		this.percent = percent;

		super.setTempoPercent(percent);

		for (SongPlayView choir : choirs) {
			AsycTaskExcuter.executePriorityAsyncTask(new tempo(), choir);
		}
	}

	int percent = 0;

	protected class tempo extends PriorityAsyncTask<SongPlayView, Void, Void> {

		@Override
		protected Void doInBackground(SongPlayView... params) {

			for (SongPlayView param : params) {
				param.setTempoPercent(percent);
			}
			return null;
		}

	}

	// public void setTempoPercent(int index, int percent) {
	//
	// try {
	// findChoir(index).setPitch(percent);
	// } catch (Exception e) {
	//
	// e.printStackTrace();
	// }
	// }

	@Override
	public int getTotalTime() {

		if (getLead() != null) {
			return getLead().getTotalTime();
		} else {
			return super.getTotalTime();
		}
	}

	@Override
	public int getCurrentTime() {

		if (getLead() != null) {
			return getLead().getCurrentTime();
		} else {
			return super.getCurrentTime();
		}
	}

	@Override
	public void restart() {

		super.restart();

		boolean open = false;
		for (SongPlayView choir : choirs) {
			if (TextUtils.isEmpty(choir.getPath())) {
				open = true;
				break;
			}
		}

		for (SongPlayView choir : choirs) {
			if (open) {
				AsycTaskExcuter.executePriorityAsyncTask(new open(), choir);
			} else {
				AsycTaskExcuter.executePriorityAsyncTask(new restart(), choir);
			}
		}
	}

	protected class restart extends PriorityAsyncTask<SongPlayView, Void, Void> {

		@Override
		protected Void doInBackground(SongPlayView... params) {

			for (SongPlayView param : params) {
				param.restart();
			}
			return null;
		}

	}

	@Override
	public void repeat() {

		super.repeat();

		boolean open = false;
		for (SongPlayView choir : choirs) {
			if (TextUtils.isEmpty(choir.getPath())) {
				open = true;
				break;
			}
		}

		for (SongPlayView choir : choirs) {
			if (open) {
				AsycTaskExcuter.executePriorityAsyncTask(new open(), choir);
			} else {
				AsycTaskExcuter.executePriorityAsyncTask(new repeat(), choir);
			}
		}
	}

	protected class repeat extends PriorityAsyncTask<SongPlayView, Void, Void> {

		@Override
		protected Void doInBackground(SongPlayView... params) {

			for (SongPlayView param : params) {
				param.repeat();
			}
			return null;
		}

	}

	@Override
	public void release() {

		super.release();

		for (int i = 0; i < choir.getChildCount(); i++) {
			SongPlayView song = findChoir(i);
			if (song != null) {
				song.release();
			}
		}
	}

	@Override
	public void close() {

		super.close();

		for (int i = 0; i < choir.getChildCount(); i++) {
			SongPlayView song = findChoir(i);
			if (song != null) {
				song.close();
			}
		}
	}

	@Override
	public void setHandler(Handler handler) {

		super.setHandler(handler);

	}

	@Override
	public void setOnListener(Listener listener) {

		super.setOnListener(listener);

	}

	@Override
	public void onTime(int t) {

		super.onTime(t);

	}

	@Override
	public void onSeekComplete() {

		super.onSeekComplete();

	}

	@Override
	public void onRelease() {

		super.onRelease();

	}

	@Override
	public void onReady(int count) {

		super.onReady(count);

	}

	@Override
	public void onPrepared() {

		super.onPrepared();

	}

	@Override
	public void onError() {

		super.onError();

	}

	@Override
	public void onError(final ERROR t, final Exception e) {

		super.onError(t, e);

	}

	@Override
	public void onRetry(final int count) {

		super.onRetry(count);

	}

	@Override
	public void onCompletion() {

		super.onCompletion();

	}

	@Override
	public void onBufferingUpdate(int percent) {

		super.onBufferingUpdate(percent);

	}

}
