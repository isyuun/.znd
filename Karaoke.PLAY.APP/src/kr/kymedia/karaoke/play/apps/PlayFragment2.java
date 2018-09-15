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
 * project	:	Karaoke.PLAY.TEST
 * filename	:	PlayFragmentChoir.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play2.app
 *    |_ PlayFragmentChoir.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.apps;

import kr.kymedia.karaoke.play._SoundTouchPlay;
import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.play.app.view.SongPlayView;
import kr.kymedia.karaoke.play.app.view._PlayView;
import kr.kymedia.karaoke.util.Log;
import kr.kymedia.karaoke.widget.KaraokeTextEdit;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * 
 * TODO<br>
 * 
 * <pre>
 * 코러스기능실행
 * </pre>
 * 
 * @author isyoon
 * @since 2014. 7. 18.
 * @version 1.0
 */
public class PlayFragment2 extends PlayFragment {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		// return super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	@Override
	public void start() {

		super.start();

		choir(true);
	}

	@Override
	public void prepare() {

		super.prepare();

		choir(false);
	}

	protected _PlayView addChoir() {
		Log.w(__CLASSNAME__, getMethodName() + getChoirs().size());

		_PlayView view = null;

		if (getChoirs().size() < _SoundTouchPlay.MAX_CHOIRS) {
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);

			view = (_PlayView) inflater.inflate(R.layout.layout_play, mRootView, false);
			view.setId(R.id.include_play_choir);
			view.setChoir(true);
			view.setHandler(handler);
			addChoir(R.id.choir, view);

			setChoirs();

			String path = ((KaraokeTextEdit) findViewById(R.id.editPath)).getText().toString();
			view.setPath(path);

			Log.e(__CLASSNAME__, getMethodName() + getPath());
			Log.e(__CLASSNAME__, getMethodName() + view.getPath());
		}

		return view;
	}

	protected void removeChoir() {
		if (getChoirs().size() > 1) {
			SongPlayView view = getChoirs().get(getChoirs().size() - 1);
			removeChoir(view.getID(), view);
		}
	}

	protected void removeChoirs() {
		while (getChoirs().size() > 1) {
			removeChoir();
		}
	}

	private void choir(boolean init) {
		Log.e(__CLASSNAME__, getMethodName());

		if (init) {
			setChoir(R.id.choir);
			putLead(getPlayView());
		}

		if (findViewById(R.id.buttonAdd) != null) {
			((ImageButton) findViewById(R.id.buttonAdd)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					addChoir();
				}

			});
		}
	}

	@Override
	protected void setChoirs() {
		// Log.i(__CLASSNAME__, getMethodName() + getChoirs());
		super.setChoirs();

		for (SongPlayView song : getChoirs()) {
			song.setChoir();
		}
	}

	@Override
	public void onPrepared() {
		Log.d(__CLASSNAME__, getMethodName());


		if (getChoirs().size() > 0) {
			(new Prepare()).execute();
		} else {
			super.onPrepared();
		}
	}

	class Prepare extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean isPrepared = false;

			if (getChoirs().size() > 0) {
				while (!isPrepared) {
					for (SongPlayView choir : getChoirs()) {
						isPrepared = choir.isPrepared();
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			}

			postDelayed(new Runnable() {

				@Override
				public void run() {

					prepare();
					play();
				}

			}, _SoundTouchPlay.TIME_RESTART);

			return null;
		}

	}

	@Override
	public void stop() {

		super.stop();

	}

	@Override
	public boolean load(String path) throws Exception {

		return super.load(path);
	}

	@Override
	public void seek(int msec) {

		super.seek(msec);
	}

}
