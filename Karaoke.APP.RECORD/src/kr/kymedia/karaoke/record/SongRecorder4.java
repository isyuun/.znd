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
 * 2012 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.KPOP.LIB
 * filename	:	SongRecorder4.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.record
 *    |_ SongRecorder4.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.record;

import kr.kymedia.karaoke.util.Log;
import kr.kymedia.karaoke.util.Util;
import net.sourceforge.lame.Lame;
import android.media.AudioRecord;
import android.os.AsyncTask;

/**
 *
 * NOTE:<br>
 *
 * @author isyoon
 * @since 2013. 5. 10.
 * @version 1.0
 * @see SongRecorder1.java
 */
public class SongRecorder4 extends SongRecorder1 {
	final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	/**
	 * Lame Encoding Buffer
	 * 
	 * @author isyoon
	 *
	 */
	class LameAsync {
		private final short[] pcmLBuf;
		private final short[] pcmRBuf;
		private int samplesRead = -1;
		private final byte[] mp3Buf;

		public LameAsync(short[] pcmLBuf, short[] pcmRBuf, int samplesRead, byte[] mp3Buf) {
			super();
			this.pcmLBuf = pcmLBuf;
			this.pcmRBuf = pcmRBuf;
			this.samplesRead = samplesRead;
			this.mp3Buf = mp3Buf;
		}

		/**
		 * Lame Encoding AsyncTask
		 * 
		 * @author isyoon
		 *
		 */
		class encode extends AsyncTask<LameAsync, Integer, Integer> {

			@Override
			protected Integer doInBackground(LameAsync... params) {

				return Lame.encode(pcmLBuf, pcmRBuf, samplesRead, mp3Buf, mp3Buf.length);
			}

		}

		public void encode() {
			(new encode()).execute(this);
		}

	};

	/**
	 * Lame Encoding AsyncTask
	 * 
	 * @author isyoon
	 *
	 */
	class encode extends AsyncTask<Object, Integer, Integer> {

		@Override
		protected Integer doInBackground(Object... params) {

			// if (params.length == 4) {
			// ArrayList<Short> pcmLBuf = param[0];
			// ArrayList<Short> pcmRBuf = param[1];;
			// int samplesRead = param[2];
			// ArrayList<Byte> mp3Buf = param[3];
			//
			// return Lame.encode(pcmLBuf., pcmRBuf, samplesRead, mp3Buf, mp3Buf.size());
			// } else {
			// return 0;
			// }
			return 0;
		}

	}

	public SongRecorder4(String songNumber) throws Exception {
		super(songNumber);
	}

	public SongRecorder4(String songNumber, boolean compressed) throws Exception {
		super(songNumber, compressed);
	}

	/**
	 * @see kr.kymedia.karaoke.record.SongRecorder1#onPeriodicNotification(android.media.AudioRecord)
	 */
	@Override
	public void onPeriodicNotification(AudioRecord recorder) {

		super.onPeriodicNotification(recorder);
		int bytesRead = 0;
		try {
			if (mAudioRecord != null) {
				bytesRead = read(mPCMBuf, 0, mPCMBuf.length);
			}
		} catch (Exception e) {

			e.printStackTrace();
			return;
		}

		// Log.d(__CLASSNAME__, "onPeriodicNotification (...)" + mPCMBuf.toString() + "," + mPCMBuf.length);

		if (mPaused) {
			Log.d(__CLASSNAME__, "RECORD() ... PAUSE!!!");
			return;
		}

		int samplesRead = -1;
		int bytesEncoded = -1;

		if (mCompressed) {
			// put MP3 buffer to File
			try {
				if (bytesRead > 0) {

					if (getChannelCount() == 2) {
						// stereo
						samplesRead = Util.pcmByteToShort(mPCMBuf, pcmLBuf, pcmRBuf, bytesRead);
						if (samplesRead > 0) {
							// bytesEncoded = Lame.encode(pcmLBuf, pcmRBuf, samplesRead, mp3Buf, mp3Buf.length);
							(new LameAsync(pcmLBuf, pcmRBuf, samplesRead, mp3Buf)).encode();
						}
					} else {
						// default (mono)
						samplesRead = Util.pcmByteToShort(mPCMBuf, pcmLBuf, bytesRead);
						if (samplesRead > 0) {
							// bytesEncoded = Lame.encode(pcmLBuf, pcmLBuf, samplesRead, mp3Buf, mp3Buf.length);
							(new LameAsync(pcmLBuf, pcmLBuf, samplesRead, mp3Buf)).encode();
						}
					}
					if (bytesEncoded > 0) {
						mMP3OutputStream.write(mp3Buf, 0, bytesEncoded);
					}
				}
			} catch (Exception e) {

				Log.e(__CLASSNAME__,
						"RECORD() ... NG!!! (" + mPCMBuf.toString() + ", " + mp3Buf.toString() + ", "
								+ bytesEncoded + ")");
				// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
			}
			Log.d(__CLASSNAME__, "write(...)" + "," + bytesRead + "," + samplesRead + ", "
					+ bytesEncoded + "," + mPayloadSize);
		} else {
			// put WAV buffer to File
			try {
				if (bytesRead > 0) {
					mWAVFile.write(mPCMBuf, 0, bytesRead);
				}
				Log.d(__CLASSNAME__, "write(...)" + "," + bytesRead + "," + samplesRead + ", "
						+ bytesEncoded + "," + mPayloadSize);
			} catch (Exception e) {

				Log.e(__CLASSNAME__,
						"RECORD() ... NG!!! (" + mPCMBuf.toString() + ", " + mp3Buf.toString() + ", "
								+ bytesEncoded + ")");
				// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
			}
			mPayloadSize += bytesRead;
		}

	}

}
