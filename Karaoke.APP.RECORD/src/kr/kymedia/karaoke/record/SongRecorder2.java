/**
 * 2012 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * 프로젝트:	Karaoke.v2
 * 파일명:	SongRecorder2.java	
 * 작성자:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.record 
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

import kr.kymedia.karaoke._IKaraoke;
import kr.kymedia.karaoke.util.Log;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Build;

/**
 * TODO :<br>
 * 
 * <pre>
 * 	MediaRecorder사용
 * 	3gp/m4a저장
 * 	2차개발모델(톡송녹음모듈)
 * 	일부모델에서 디바이스문제
 * </pre>
 * 
 * 변경자: isyoon<br>
 * 변경일: 2012. 1. 6.<br>
 * 변경명: 최초작성<br>
 * <b> 옵티머스원(android 2.2(8)) : 녹음시 채널설정1로 반드시 할것<br>
 * mNumChannels = 1 오디오 비트레이트/샘플레이트 조절시 폰별 오류발생 - 보류<br>
 * setAudioEncodingBitRate : <br>
 * setAudioSamplingRate :<br>
 * LG옵티머스2X<br>
 */
public class SongRecorder2 extends SongRecorder implements ISongRecorder, MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener {
	final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected MediaRecorder mMediaRecorder = new MediaRecorder();
	private OnErrorListener elistener = null;
	private OnInfoListener ilistener = null;

	@Override
	public void setOnErrorListener(OnErrorListener listener) {

		if (mMediaRecorder != null) {
			elistener = listener;
		}
	}

	@Override
	public void setOnInfoListener(OnInfoListener listener) {

		if (mMediaRecorder != null) {
			ilistener = listener;
		}
	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {

		Log.e(__CLASSNAME__, getMethodName() + mr.toString() + "," + what + "," + extra);
		isRecording = false;
		if (elistener != null) {
			elistener.onError(mr, what, extra);
		}
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {

		// Log.e(__CLASSNAME__, getMethodName() + mr.toString() + "," + what + "," + extra);
		// isRecording = false;
		if (ilistener != null) {
			ilistener.onInfo(mr, what, extra);
		}
	}

	public SongRecorder2() throws Exception {

	}

	public SongRecorder2(String name) throws Exception {

		isRecording = false;

		String path = setFilePath(name);

		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return;
		}

		Log.d(__CLASSNAME__, getMethodName() + name + "," + mOutputFormat + ","
				+ mAudioEncoder + "," + mSampleBitRate + "," + mSampleRate);

		init(path);

	}

	public SongRecorder2(String songNumber, boolean compressed) throws Exception {

		isRecording = false;

		String path = setFilePath(songNumber);

		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return;
		}

		Log.d(__CLASSNAME__, getMethodName() + songNumber + "," + mOutputFormat + ","
				+ mAudioEncoder + "," + mSampleBitRate + "," + mSampleRate);

		init(path);

	}

	protected void init(String path) throws Exception {
		Log.e(__CLASSNAME__, getMethodName() + path);

		mPath = path;

		// try {
		// prepare(CamcorderProfile.QUALITY_HIGH);
		// Log.e(__CLASSNAME__, getMethodName() + "OK!!!" + path);
		// return;
		// } catch (Exception e) {
		//
		// Log.e(__CLASSNAME__, "prepare(...) CamcorderProfile.QUALITY_HIGH - NG!!!" + path);
		// Log.e(__CLASSNAME__, "" + Log.getStackTraceString(e));
		// e.printStackTrace();
		// release();
		// }
		//
		// if (mMediaReorder != null) {
		// return;
		// }
		//
		// try {
		// prepare(CamcorderProfile.QUALITY_LOW);
		// Log.e(__CLASSNAME__, getMethodName() + "OK!!!" + path);
		// return;
		// } catch (Exception e) {
		//
		// Log.e(__CLASSNAME__, "prepare(...) CamcorderProfile.QUALITY_LOW - NG!!!" + path);
		// Log.e(__CLASSNAME__, "" + Log.getStackTraceString(e));
		// e.printStackTrace();
		// release();
		// //throw e;
		// }
		//
		// release();

		String err = "";

		int i = 0;
		do {
			try {
				prepare(CAMCORDER_QUALITY[i]);
				Log.e(__CLASSNAME__, getMethodName() + "OK!!!" + mPath);
				return;
			} catch (Exception e) {

				err = Log.getStackTraceString(e);
				e.printStackTrace();
				release();
				Log.e(__CLASSNAME__, getMethodName() + "NG!!!" + mPath);
			}
		} while ((++i < CAMCORDER_QUALITY.length) && mMediaRecorder == null);

		if (mMediaRecorder == null) {
			addRecInfo(err);
			Log.e(__CLASSNAME__, getRecInfo());
			throw new Exception(err);
		}

	}

	protected void prepare(int quality) throws Exception {
		Log.e(__CLASSNAME__, getMethodName() + "[START] RECORDING.QUALITY - " + quality);
		Log.e(__CLASSNAME__, "RECORDING.QUALITY >> " + "path : " + mPath);
		Log.e(__CLASSNAME__, "RECORDING.QUALITY >> " + "fileFormat : " + getFileFormat(mOutputFormat));
		Log.e(__CLASSNAME__, "RECORDING.QUALITY >> " + "audioCodec : " + getAudioCodec(mAudioEncoder));
		Log.e(__CLASSNAME__, "RECORDING.QUALITY >> " + "audioBitRate : " + mSampleBitRate);
		Log.e(__CLASSNAME__, "RECORDING.QUALITY >> " + "audioSampleRate : " + mSampleRate);

		mQuality = quality;

		if (mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder();
		}

		mMediaRecorder.setOnErrorListener(this);
		mMediaRecorder.setOnInfoListener(this);

		// * 옵티머스원(android 2.2(8)) : 녹음시 채널설정1로 반드시 할것
		// * mNumChannels = 1
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecorder.setAudioChannels(mNumChannels);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
			// 진저브레드(android 2.3.3(10)이상)
			mSampleRate = 44100;
			mSampleBitRate = 128000;
			mOutputFormat = MediaRecorder.OutputFormat.MPEG_4;
			mAudioEncoder = MediaRecorder.AudioEncoder.AAC;
		} else {
			// 프로요(android 2.3.3(10)이하)
			// older version of Android, use crappy sounding voice codec
			mSampleRate = 8000;
			mSampleBitRate = 12200;
			mOutputFormat = MediaRecorder.OutputFormat.THREE_GPP;
			mAudioEncoder = MediaRecorder.AudioEncoder.AMR_NB;
		}

		// 단말에 설정된 프로파일확인
		// try {
		// getCamcorderQuality(quality);
		// } catch (Exception e) {
		//
		// e.printStackTrace();
		// }
		getCamcorderQuality(quality);

		// 포맷설정
		mMediaRecorder.setOutputFormat(mOutputFormat);

		// 오디오포맷설정
		mMediaRecorder.setAudioEncoder(mAudioEncoder);

		// * 오디오 비트레이트/샘플레이트 조절시 폰별 오류발생 - 보류
		// * setAudioEncodingBitRate :
		// * setAudioSamplingRate : LG옵티머스2X : LG-SU660
		// if (("LG-SU660").compareToIgnoreCase(android.os.Build.MODEL) == 0
		// || ("LG-P990").compareToIgnoreCase(android.os.Build.MODEL) == 0) {
		// Log.e("폰별예외처리:", Build.MODEL.toString());
		// //이런씨댕
		// try {
		// getCamcorderQuality(quality);
		// } catch (Exception e) {
		//
		// e.printStackTrace();
		// }
		// }

		mMediaRecorder.setAudioEncodingBitRate(mSampleBitRate);
		mMediaRecorder.setAudioSamplingRate(mSampleRate);

		// 파일확장자m4a통일
		mPath = replaceFileExt(mPath, "3gp", "m4a");
		mMediaRecorder.setOutputFile(mPath);

		mMediaRecorder.prepare();

		putRecInfo();
		Log.i(__CLASSNAME__, getMethodName() + "\n" + getRecInfo());

		Log.e(__CLASSNAME__, getMethodName() + "[END] RECORDING.QUALITY - " + quality);
	}

	public String getPath() {
		return mPath;
	}

	public String setFilePath(String name) {
		String ret = "";

		Log.d(__CLASSNAME__, "setFilePath(...) " + name);

		String path = "";

		File audio = new File(_IKaraoke.RECORD_PATH);
		audio.mkdirs();
		// Log.d(LOG_TAG, "writing to directory " + audio.getAbsolutePath());

		// 파일확장자m4a통일
		// construct file name
		// String gp3Path = audio.getAbsolutePath() + "/" + songNumber + "."+"3gp";
		String mp4Path = audio.getAbsolutePath() + "/" + name + "." + "m4a";

		// File output = new File(wavPath);
		// if (output.exists()) {
		// output.delete();
		// }

		// 파일확장자m4a통일
		// start the recording
		// if (mOutputFormat == MediaRecorder.OutputFormat.MPEG_4) {
		// path = mp4Path;
		// } else {
		// path = gp3Path;
		// }
		path = mp4Path;

		ret = path;
		mPath = path;

		Log.d(__CLASSNAME__, "writing to file " + mPath);
		return ret;
	}

	/**
	 * 파일경로 확장자를 변경해준다.
	 * 
	 * @param path
	 * @param src
	 * @param dst
	 * @return
	 */
	protected String replaceFileExt(String path, String src, String dst) {
		path = path.toLowerCase();
		src = src.toLowerCase();
		dst = dst.toLowerCase();

		int idx = path.indexOf("." + dst, path.length() - 4);
		if (idx == path.length() - 4) {
			return path;
		}

		idx = path.indexOf("." + src, path.length() - 4);
		if (idx == path.length() - 4) {

			path = path.substring(0, idx) + "." + dst;

		} else if (idx < 0) {
			// '.'확장자확인
			idx = path.indexOf('.', path.length() - 1);

			if (idx == path.length() - 1) {
				path = path.substring(0, idx) + "." + dst;
			} else {
				path = path + "." + dst;
			}
		}

		return path;
	}

	@Override
	public boolean start() throws Exception {

		Log.d(__CLASSNAME__, "start()");
		try {
			mMediaRecorder.start();
			isRecording = true;
			return true;
		} catch (Exception e) {

			Log.e(__CLASSNAME__, getMethodName() + Log.getStackTraceString(e));
			throw new Exception(Log.getStackTraceString(e));
		}
	}

	@Override
	public boolean stop() throws Exception {

		Log.d(__CLASSNAME__, "stop()");
		try {
			if (mMediaRecorder != null) {
				if (isRecording) {
					mMediaRecorder.stop();
				}
				mMediaRecorder.reset();
			}
			isRecording = false;
			return true;
		} catch (Exception e) {

			Log.e(__CLASSNAME__, "" + Log.getStackTraceString(e));
			throw e;
		}
	}

	@Override
	public void pause() {


	}

	@Override
	public void resume() {


	}

	// /**
	// * 녹음오디오샘플링레이트(hz)설정
	// *
	// * @param samplingRate
	// * @see
	// * 녹음시 오디오 샘플링레이트(hz)설정 - 사이즈관련
	// * hz단위 예) 44.1khz -> 44100
	// */
	// public void setAudioSamplingRate(int samplingRate) {
	// Log.d(__CLASSNAME__, "setAudioSamplingRate()" + samplingRate);
	// mSamplingRate = samplingRate;
	// if (mMediaReorder != null) {
	// reset();
	// prepare();
	// }
	// }
	//
	// public int getAudioSamplingRate() {
	// return mSamplingRate;
	// }
	//
	// /**
	// * 녹음오디오비트레이트(bps)설정
	// *
	// * @param bitRate
	// * @see
	// * 녹음시 오디오 비트레이트(bps) 설정 - 사이즈변경관련
	// * bps단위 예)128kbps -> 128000
	// * 오디오 비트레이트/샘플레이트 조절시 폰별 오류발생
	// * setAudioEncodingBitRate : LG옵티머스2X/모토로라 아트릭스
	// */
	// public void setAudioEncodingBitRate(int bitRate) {
	// Log.d(__CLASSNAME__, "setAudioEncodingBitRate()" + bitRate);
	// mBitRate = bitRate;
	// if (mMediaReorder != null) {
	// reset();
	// prepare();
	// }
	// }
	//
	// public int getAudioEncodingBitRate() {
	// return mBitRate;
	// }
	//
	// /**
	// * 녹음오디오인코딩방식설정(AMR_NB/AMR_WB/AAC)
	// *
	// * @param audioEncoder
	// * @see
	// * 녹음시 오디오 인코딩방식 설정
	// *
	// */
	// public void setAudioEncoder(int audioEncoder) {
	// Log.d(__CLASSNAME__, "setAudioEncoder()" + audioEncoder);
	// mAudioEncoder = audioEncoder;
	// if (mMediaReorder != null) {
	// reset();
	// prepare();
	// }
	// }
	//
	// public int getAudioEncoder() {
	// return mAudioEncoder;
	// }

	@Override
	public int getAudioFormat() {

		return 0;
	}

	@Override
	public int getAudioSource() {

		return 0;
	}

	@Override
	public int getChannelConfiguration() {

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

		return 0;
	}

	@Override
	public int getSampleRate() {

		return mSampleRate;
	}

	@Override
	public int getSampleRateBit() {

		return mSampleBitRate;
	}

	@Override
	public int getState() {

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

		Log.d(__CLASSNAME__, "release()");
		if (mMediaRecorder != null) {
			mMediaRecorder.release();
			mMediaRecorder = null;
		}

	}

	@Override
	public int setNotificationMarkerPosition(int markerInFrames) {

		return 0;
	}

	@Override
	public int setPositionNotificationPeriod(int periodInFrames) {

		return 0;
	}

	public String getFileFormat() {
		String ret = "";

		switch (mOutputFormat) {
		case MediaRecorder.OutputFormat.MPEG_4:
			ret = "MP4";
			break;

		case MediaRecorder.OutputFormat.THREE_GPP:
		case MediaRecorder.OutputFormat.AMR_NB:
		case MediaRecorder.OutputFormat.AMR_WB:
			ret = "3GP";
			break;

		case MediaRecorder.OutputFormat.DEFAULT:
		default:
			ret = "DEF";
			break;
		}

		return ret;
	}

	public String getEncodingFormat() {
		String ret = "";

		switch (mAudioEncoder) {
		case MediaRecorder.AudioEncoder.AAC:
			ret = "AAC";
			break;

		case MediaRecorder.AudioEncoder.AMR_NB:
		case MediaRecorder.AudioEncoder.AMR_WB:
			ret = "AMR";
			break;

		case MediaRecorder.AudioEncoder.DEFAULT:
		default:
			ret = "DEF";
			break;
		}

		return ret;
	}

	/**
	 * 녹음중확인
	 */
	@Override
	public boolean isRecording() {

		return isRecording;
	}

}
