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
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	Karaoke.PLAY4
 * filename	:	PlayService.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.karaoke.play2
 *    |_ PlayService.java
 * </pre>
 */

package kr.kymedia.karaoke.play;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

/**
 *
 *
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 7. 14.
 * @version 1.0
 */
public class SongService extends IntentService {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected static String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	/**
	 * <pre>
	 * TODO
	 * </pre>
	 *
	 * @param name
	 */
	public SongService(String name) {
		super(name);
	}

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// // Normally we would do some work here, like download a file.
			// // For our sample, we just sleep for 5 seconds.
			// long endTime = System.currentTimeMillis() + 5 * 1000;
			// while (System.currentTimeMillis() < endTime) {
			// synchronized (this) {
			// try {
			// wait(endTime - System.currentTimeMillis());
			// } catch (Exception e) {
			// }
			// }
			// }
			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			stopSelf(msg.arg1);
		}
	}

	@Override
	public void onCreate() {
		Log.i(__CLASSNAME__, getMethodName());

		super.onCreate();

		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(__CLASSNAME__, getMethodName() + intent + "," + flags + "," + startId);

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		// return super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	/**
	 * The IntentService calls this method from the default worker thread with the intent that started
	 * the service. When this method returns, IntentService stops the service, as appropriate.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(__CLASSNAME__, getMethodName() + intent);



	}

	public final IBinder localBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		public SongService getService() {
			return SongService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(__CLASSNAME__, getMethodName() + intent);


		return localBinder;
	}

	@Override
	public void onDestroy() {
		Log.i(__CLASSNAME__, getMethodName());


		super.onDestroy();
	}

}
