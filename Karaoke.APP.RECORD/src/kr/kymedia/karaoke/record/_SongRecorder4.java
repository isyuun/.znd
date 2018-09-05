/**
 * 2011 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * 프로젝트:	Karaoke.new
 * 파일명:	SongRecorder2.java	
 * 작성자:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke2.record 
 *    |_ SongRecorder2.java
 * </pre>
 * 
 */
/**
 * 
 */
package kr.kymedia.karaoke.record;

import java.io.File;
import java.nio.ByteBuffer;

import kr.kymedia.karaoke.util.Log;
import android.media.AudioRecord;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Handler;
import android.os.Message;

/**
 * TODO :개발보류중<br>
 * 
 * <pre>
 * 	AudioRecorder사용
 * 	MP3저장
 *  스레드모델사용
 * 	4차개발모델
 * 	아직테스트도못해봤다.T_T
 * </pre>
 * 
 * <br>
 * 변경자: isyoon<br>
 * 변경일: 2011. 9. 26.<br>
 * 변경명: 최초작성<br>
 */
@Deprecated
class _SongRecorder4 extends SongRecorder implements ISongRecorder {
	final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	RecorderThread mRecorderThread;
	Handler mHandler = new Handler() {

		// /**
		// * @see android.os.Handler#dispatchMessage(android.os.Message)
		// */
		// @Override
		// public void dispatchMessage(Message msg) {
		//
		// super.dispatchMessage(msg);
		// }

		/**
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
		}

		/**
		 * @see android.os.Handler#sendMessageAtTime(android.os.Message, long)
		 */
		@Override
		public boolean sendMessageAtTime(Message msg, long uptimeMillis) {

			return super.sendMessageAtTime(msg, uptimeMillis);
		}

	};

	public _SongRecorder4(String path, boolean mCompressed) throws Exception {
		mRecorderThread = new RecorderThread();
		File file = new File(path);
		mRecorderThread.setFileName(file);
	}

	Runnable run = new Runnable() {

		@Override
		public void run() {

			mRecorderThread.run();
		}
	};

	@Override
	public boolean start() throws Exception {

		try {
			mRecorderThread.setRecording(true);
			mHandler.post(run);
		} catch (Exception e) {

			Log.e(__CLASSNAME__, "" + Log.getStackTraceString(e));
			throw e;
		}
		return true;
	}

	@Override
	public boolean stop() throws Exception {

		try {
			mRecorderThread.setRecording(false);
		} catch (Exception e) {


			Log.e(__CLASSNAME__, "" + Log.getStackTraceString(e));
			throw e;
		}
		return true;
	}

	@Override
	public void pause() {

		mRecorderThread.setPaused(true);

	}

	@Override
	public void resume() {

		mRecorderThread.setPaused(false);
	}

	@Override
	public int getAudioFormat() {

		return 0;
	}

	@Override
	public int getAudioSource() {

		return 0;
	}

	@Override
	public int getChannelCount() {

		return 0;
	}

	@Override
	public int getNotificationMarkerPosition() {

		return 0;
	}

	@Override
	public int getPositionNotificationPeriod() {

		return 0;
	}

	@Override
	public int getRecordingState() {

		if (mRecorderThread.isRecording()) {
			return AudioRecord.RECORDSTATE_RECORDING;
		}
		return 0;
	}

	@Override
	public int getSampleRate() {

		return 0;
	}

	@Override
	public int getSampleRateBit() {

		return 0;
	}

	@Override
	public int getState() {

		if (mRecorderThread != null) {
			return AudioRecord.STATE_INITIALIZED;
		}
		return 0;
	}

	@Override
	public int read(byte[] audioData, int offsetInBytes, int sizeInBytes) {

		return 0;
	}

	@Override
	public int read(ByteBuffer audioBuffer, int sizeInBytes) {

		return 0;
	}

	@Override
	public int read(short[] audioData, int offsetInShorts, int sizeInShorts) {

		return 0;
	}

	@Override
	public void release() {


	}

	@Override
	public int setNotificationMarkerPosition(int markerInFrames) {

		return 0;
	}

	@Override
	public int setPositionNotificationPeriod(int periodInFrames) {

		return 0;
	}

	@Override
	public int getChannelConfiguration() {

		return 0;
	}

	@Override
	public void setOnErrorListener(OnErrorListener l) {


	}

	@Override
	public void setOnInfoListener(OnInfoListener listener) {


	}

	/**
	 * 녹음중확인
	 */
	@Override
	public boolean isRecording() {

		return mRecorderThread.isRecording();
	}

}
