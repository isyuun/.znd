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
 * project	:	Karaoke
 * filename	:	CPULoad.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.util
 *    |_ CPULoad.java
 * </pre>
 * 
 */

package kr.keumyoung.karaoke.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.util.Log;

/**
 *
 * TODO<br>
 * 
 * <pre></pre>
 *
 * @author isyoon
 * @since 2015. 2. 5.
 * @version 1.0
 */
public class CPUMEM {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	protected static String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	private int pid = 0;

	// private long total = 0;
	// private long idle = 0;

	private float usage = 0;

	private RandomAccessFile reader;

	public CPUMEM(int pid, long period) {
		Log.i(__CLASSNAME__, getMethodName());

		this.pid = pid;
		this.period = period;
		info = "";
		try {
			reader = new RandomAccessFile("/proc/stat", "r");
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();
		stop();
	}

	Handler handler;

	public CPUMEM(int pid, long period, CPUMEMListener listener, Handler handler) {
		this(pid, period);
		this.listener = listener;
		this.handler = handler;
	}

	CPUMEMListener listener;

	public interface CPUMEMListener {
		public void onCPUMEMUpdate(CPUMEM cpumem);
	}

	public void setCPUMEMListener(CPUMEMListener listener) {
		this.listener = listener;
	}

	private TimerTask task;
	private Timer timer;
	private long period = 2000;

	public void start() {
		Log.i(__CLASSNAME__, getMethodName());

		try {
			task = new TimerTask() {

				@Override
				public void run() {

					readUsage();
				}
			};

			timer = new Timer();
			timer.schedule(task, period / 2, period);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void stop() {
		Log.i(__CLASSNAME__, getMethodName());

		try {
			handler = null;

			listener = null;

			if (timer != null) {
				timer.purge();
				timer.cancel();
				timer = null;
			}

			if (task != null) {
				task.cancel();
				task = null;
			}

			if (reader != null) {
				reader.close();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public float getUsage() {
		return usage;
	}

	private String info = "";

	public String getInfo() {
		return info;
	}

	Runnable update = new Runnable() {

		@Override
		public void run() {

			if (listener != null) {
				listener.onCPUMEMUpdate(CPUMEM.this);
			}
		}
	};

	private void readUsage() {
		// Log.i(__CLASSNAME__, getMethodName());
		readUsageTop();

		if (handler != null) {
			handler.post(update);
		}
	}

	private void readUsageTop() {
		// Log.i(__CLASSNAME__, getMethodName());

		Runtime runtime = Runtime.getRuntime();
		Process process;
		String res = "-0-";

		try {
			String cmd = "top -n 1";
			process = runtime.exec(cmd);
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			// info = "";
			while ((line = br.readLine()) != null) {
				// Log.w(__CLASSNAME__, line);
				String segs[] = line.trim().split("[ ]+");
				if (segs[0].equalsIgnoreCase("" + pid)) {
					info = line;
					res = segs[2];
					break;
				}
			}

			// Log.i(__CLASSNAME__, info);
		} catch (Exception e) {
			e.fillInStackTrace();
			Log.e(__CLASSNAME__, "Unable to execute top command - " + res);
		}

	}

	private void readUsageFile() {
		// Log.i(__CLASSNAME__, getMethodName());

		info = "";

		try {
			// RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();

			info += load + "\n";

			String[] toks = load.split(" ");

			long idle1 = Long.parseLong(toks[5]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			try {
				Thread.sleep(360);
			} catch (Exception e) {
			}

			reader.seek(0);
			load = reader.readLine();
			// reader.close();

			info += load;

			toks = load.split(" ");

			long idle2 = Long.parseLong(toks[5]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			// return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
			usage = (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

			// Log.i(__CLASSNAME__, info);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// return 0;
	}

	public long getUsedMemorySize() {
		long freeSize = 0L;
		long totalSize = 0L;
		long usedSize = -1L;

		try {
			Runtime info = Runtime.getRuntime();
			freeSize = info.freeMemory();
			totalSize = info.totalMemory();
			usedSize = totalSize - freeSize;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return usedSize;
	}

	public long getTotalMemorySize() {
		long freeSize = 0L;
		long totalSize = 0L;
		long usedSize = -1L;

		try {
			Runtime info = Runtime.getRuntime();
			freeSize = info.freeMemory();
			totalSize = info.totalMemory();
			usedSize = totalSize - freeSize;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return totalSize;
	}

}
