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
 * filename	:	PlayFragment.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play.apps.test
 *    |_ PlayFragment.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app;

import kr.kymedia.karaoke.play.impl.ISongService;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.fragment.app.Fragment;
import android.util.Log;

/**
 * 
 *
 * <pre>
 * 재생용백그라운드서비스
 * </pre>
 * 
 * @author isyoon
 * @since 2014. 7. 8.
 * @version 1.0
 */
class SongServiceFragment extends Fragment implements ISongService {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		// return super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	public SongServiceFragment() {
		super();
		Log.i(__CLASSNAME__, getMethodName());
	}

	/**
	 * 서비스여부결정 - 다중트랙재생 - 보류
	 */
	public boolean isService = false;

	protected ServiceConnection SongServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			SongServiceFragment.this.onServiceConnected(name, service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

			SongServiceFragment.this.onServiceDisconnected(name);

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// _Log.i(toString(), getMethodName());


		super.onCreate(savedInstanceState);

		if (isService) {
			connectSongService();
		}
	}

	@Override
	public void onDestroy() {
		Log.i(toString(), getMethodName());


		super.onDestroy();

		if (isService) {
			disconnectSongService();
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {


	}

	@Override
	public void onServiceDisconnected(ComponentName name) {


	}

	@Override
	public void connectSongService() {


	}

	@Override
	public void disconnectSongService() {


	}

}
