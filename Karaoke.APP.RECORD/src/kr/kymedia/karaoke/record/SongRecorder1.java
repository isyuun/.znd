/**
 * 2011 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * 프로젝트:	Karaoke.new
 * 파일명:	SongRecorder.java	
 * 작성자:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke2.record 
 *    |_ SongRecorder.java
 * </pre>
 * 
 */
/**
 * 
 */
package kr.kymedia.karaoke.record;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import kr.kymedia.karaoke._IKaraoke;
import kr.kymedia.karaoke.util._Log;
import kr.kymedia.karaoke.util.Util;

import net.sourceforge.lame.Lame;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.CamcorderProfile;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;

/**
 * TODO :개발보류중<br>
 * 
 * <pre>
 * 	AudioRecorder사용
 * 	MP3저장
 * 	1차개발모델
 * 	저사양모델에서 프레임드랍문제(녹음이빨라진다.)
 * </pre>
 * 
 * 변경자: isyoon<br>
 * 변경일: 2011. 9. 26.<br>
 * 변경명: 최초작성<br>
 */
// @Deprecated
public class SongRecorder1 extends SongRecorder implements ISongRecorder, AudioRecord.OnRecordPositionUpdateListener {
	final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	// Number of bytes written to file after header
	// after stop() is called, this size is written to the header/data chunk in
	// the wave file
	int mPayloadSize;
	AudioRecord mAudioRecord;
	boolean mPaused = false;

	boolean mCompressed = true;

	// static final int WAVE_CHUNK_SIZE = 8192;
	// static final int OUTPUT_STREAM_BUFFER = 8192;

	byte[] mPCMBuf;
	short[] pcmLBuf;
	short[] pcmRBuf;
	byte[] mp3Buf;

	private String mWAVPath;
	RandomAccessFile mWAVFile;

	private String mMP3Path;
	private File mMP3File;
	BufferedOutputStream mMP3OutputStream;

	/**
	 * 생성자
	 * 
	 * @param songNumber
	 * @throws Exception
	 * @see
	 * 
	 */
	public SongRecorder1(String songNumber) throws Exception {
		_Log.e(__CLASSNAME__, "SongRecorder(...) " + songNumber);

		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return;
		}

		mCompressed = true;

		String path = setFilePath(songNumber);

		init(path);
	}

	/**
	 * 생성자 Starts recording. void startRecording
	 * 
	 * Changes state to RECORDING.
	 *
	 */
	public SongRecorder1(String songNumber, boolean compressed) throws Exception {
		_Log.e(__CLASSNAME__, getMethodName() + songNumber + "," + compressed);

		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return;
		}

		mCompressed = compressed;

		String path = setFilePath(songNumber);

		init(path);
	}

	protected void init(String path) throws Exception {
		_Log.e(__CLASSNAME__, "init(...) " + path);
		_Log.e(__CLASSNAME__, "RECORDING.QUALITY >> " + "fileFormat : " + getFileFormat(mOutputFormat));
		_Log.e(__CLASSNAME__, "RECORDING.QUALITY >> " + "audioCodec : " + getAudioCodec(mAudioEncoder));
		_Log.e(__CLASSNAME__, "RECORDING.QUALITY >> " + "audioBitRate : " + mSampleBitRate);
		_Log.e(__CLASSNAME__, "RECORDING.QUALITY >> " + "audioSampleRate : " + mSampleRate);

		int channelConfig = AudioFormat.CHANNEL_IN_MONO;
		if (mNumChannels > 1) {
			channelConfig = AudioFormat.CHANNEL_IN_STEREO;
		}

		// int i = 0;
		// do {
		// init(AudioSource.MIC, SAMPLE_RATE[i], channelConfig, AudioFormat.ENCODING_PCM_16BIT, path);
		// } while ((++i < SAMPLE_RATE.length) & !(getState() == AudioRecord.STATE_INITIALIZED));
		getCamcorderQuality(CamcorderProfile.QUALITY_HIGH);
		if (!init(AudioSource.MIC, mSampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT, path)) {
			getCamcorderQuality(CamcorderProfile.QUALITY_LOW);
			init(AudioSource.MIC, mSampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT, path);
		}

		if (getState() == AudioRecord.STATE_INITIALIZED) {
			prepareFile(path);
		}

		putRecInfo();

	}

	/**
	 * 녹음시작 Prepares the recorder for recording, in case the recorder is not in the INITIALIZING state
	 * and the file path was not set the recorder is set to the ERROR state, which makes a
	 * reconstruction necessary. In case AudioRecord recording is toggled, the header of the wave file
	 * is written. In case of an exception, the state is changed to ERROR
	 * 
	 */
	@Override
	public boolean start() throws Exception {

		// _Log.d(__CLASSNAME__, "start()");
		int ret = 0;
		try {
			if (getState() == AudioRecord.STATE_INITIALIZED) {
				mPayloadSize = 0;
				mAudioRecord.startRecording();
				byte buf[] = new byte[mBufferSizeInBytes];
				ret = mAudioRecord.read(buf, 0, mBufferSizeInBytes);
				if (ret > 0) {
					isRecording = true;
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			_Log.e(__CLASSNAME__, "" + _Log.getStackTraceString(e));
			throw e;
		}
	}

	/**
	 * 녹음중지
	 * 
	 * @return
	 */
	@Override
	public boolean stop() throws Exception {

		_Log.d(__CLASSNAME__, "stop()");
		if (getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
			return false;
		}

		mAudioRecord.stop();

		isRecording = true;

		if (mCompressed) {
			// Close MP3 File.
			try {
				int bytesEncoded = Lame.flushEncoder(mp3Buf, mp3Buf.length);
				Lame.closeEncoder();
				mMP3OutputStream.write(mp3Buf, 0, bytesEncoded);
				mMP3OutputStream.flush();
				mMP3OutputStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				_Log.e(__CLASSNAME__, "" + _Log.getStackTraceString(e));
				throw e;
			}
			// TODO: write Xing VBR/INFO tag to mp3 file here
		} else {
			// Close WAV File.
			try {
				mWAVFile.seek(4); // Write size to RIFF header
				mWAVFile.writeInt(Integer.reverseBytes(36 + mPayloadSize));
				mWAVFile.seek(40); // Write size to Subchunk2Size field
				mWAVFile.writeInt(Integer.reverseBytes(mPayloadSize));
				mWAVFile.close();
			} catch (Exception e) {

				_Log.e(__CLASSNAME__, "" + _Log.getStackTraceString(e));
				throw e;
			}
		}

		return true;

	}

	/**
	 * 초기화
	 * 
	 * @param audioSource
	 * @param sampleRateInHz
	 * @param channelConfig
	 * @param audioFormat
	 * @param path
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * 
	 * @author isyoon
	 *
	 */
	private boolean init(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat,
			String path) throws Exception {


		_Log.d(__CLASSNAME__, "init (...) " + ", " + audioSource + ", " + sampleRateInHz + ", "
				+ channelConfig + ", " + audioFormat + ", " + path);

		if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
			mSampleRateInBit = 16;
		} else {
			mSampleRateInBit = 8;
		}

		int channelCount;
		if (channelConfig == AudioFormat.CHANNEL_IN_STEREO) {
			channelCount = 2;
		} else {
			channelCount = 1;
		}

		// 문제만 더생긴다 ...
		// AudioRecord Initialize Fail 후 카메라 녹음이 안됨 (MOTOROLA ATRIX)
		mPeriodInFrames = sampleRateInHz * TIMER_INTERVAL / 1000;
		mBufferSizeInBytes = mPeriodInFrames * (2 * mSampleRateInBit * channelCount / 8);

		int minBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

		int periodInFrames = minBufferSize / (2 * mSampleRateInBit * channelCount / 8);

		// 최소버퍼사이즈가 8K 이상은 퍼포먼스가 떨어진다.
		if (minBufferSize > 8 * 1024) {
			_Log.e(__CLASSNAME__, "init() ... BUF NG!!!" + " --> " + sampleRateInHz + ", "
					+ mBufferSizeInBytes + ", " + mPeriodInFrames + ", " + minBufferSize + ", "
					+ periodInFrames);
			try {
				throw new Exception(__CLASSNAME__ + "initialization failed");
			} catch (Exception e) {

				// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
			}
			return false;
		}

		_Log.d(__CLASSNAME__, "MinBufferSize CHECK: " + mBufferSizeInBytes + ", " + mPeriodInFrames
				+ " --> " + minBufferSize + ", " + periodInFrames);

		// AudioRecord Initialize시 최소 버퍼사이즈 확인
		// Check to make sure buffer size is not smaller than the smallest allowed one
		// if (mBufferSizeInBytes < minBufferSize)
		{
			// Set frame period and timer interval accordingly
			mPeriodInFrames = periodInFrames;
			mBufferSizeInBytes = minBufferSize;
		}

		// We're important...
		// 재생중간 묵음오류
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		mAudioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat,
				mBufferSizeInBytes);

		if (getState() == AudioRecord.STATE_INITIALIZED) {
			_Log.d(__CLASSNAME__, "init(...) ... OK!!!" + " --> " + sampleRateInHz + ", "
					+ mBufferSizeInBytes + ", " + mPeriodInFrames);
		} else {
			_Log.e(__CLASSNAME__, "init(...) ... NG!!!" + " --> " + sampleRateInHz + ", "
					+ mBufferSizeInBytes + ", " + mPeriodInFrames);
			try {
				throw new Exception(__CLASSNAME__ + "initialization failed");
			} catch (Exception e) {

				// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
			}
			return false;
		}

		mAudioRecord.setRecordPositionUpdateListener(this);

		int sucess = mAudioRecord.setPositionNotificationPeriod(mPeriodInFrames);

		if (sucess == AudioRecord.SUCCESS) {
			_Log.d(__CLASSNAME__, "init setPositionNotificationPeriod(...) ... OK!!!" + " --> "
					+ sampleRateInHz + ", " + mBufferSizeInBytes + ", " + mPeriodInFrames);
		} else {
			_Log.e(__CLASSNAME__, "init setPositionNotificationPeriod(...) ... NG!!!" + " --> "
					+ sampleRateInHz + ", " + mBufferSizeInBytes + ", " + mPeriodInFrames);
			try {
				throw new Exception(__CLASSNAME__ + "initialization failed");
			} catch (Exception e) {

				// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
			}
			return false;
		}

		// Prepare Buffer
		// Don't use mBufferSizeInBytes
		// int periodInFrames = minBufferSize / (2 * mSampleRateInBit * mChannelCount / 8);
		int bufferSize = mPeriodInFrames * mSampleRateInBit * channelCount / 8;
		mPCMBuf = new byte[bufferSize];
		pcmLBuf = new short[mPCMBuf.length];
		pcmRBuf = new short[mPCMBuf.length];
		mp3Buf = new byte[mPCMBuf.length];
		// if (getChannelCount() == 2) {
		// // stereo
		// pcmLBuf = new short[mPCMBuf.length / 4];
		// pcmRBuf = new short[mPCMBuf.length / 4];
		// mp3Buf = new byte[mPCMBuf.length / 4];
		// } else {
		// // mono
		// pcmLBuf = new short[mPCMBuf.length / 2];
		// pcmRBuf = new short[mPCMBuf.length / 2];
		// mp3Buf = new byte[mPCMBuf.length / 2];
		// }

		mPayloadSize = 0;
		if (mCompressed) {
			int sampleRate = getSampleRate();
			// try {
			// //변경명 : 1. 녹음mp3 품질개선
			// Lame.initializeEncoder(sampleRate, getChannelCount());
			// // Lame.LAME_PRESET_MEDIUM/Lame.LAME_PRESET_STANDARD/LAME_PRESET_EXTREME
			// Lame.setEncoderPreset(Lame.LAME_PRESET_EXTREME);
			// } catch (ExceptionInInitializerError e) {
			//
			// _Log.e(__CLASSNAME__, "init() ... NG!!!" + " --> " + _Log.getStackTraceString(e));
			// //_Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
			// } catch (NoClassDefFoundError e) {
			//
			// _Log.e(__CLASSNAME__, "init() ... NG!!!" + " --> " + _Log.getStackTraceString(e));
			// //_Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
			// } catch (Exception e) {
			// // TODO: handle exception
			// _Log.e(__CLASSNAME__, "init() ... NG!!!" + " --> " + _Log.getStackTraceString(e));
			// //_Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
			// }
			// 변경명 : 1. 녹음mp3 품질개선
			Lame.initializeEncoder(sampleRate, getChannelCount());
			// Lame.LAME_PRESET_MEDIUM/Lame.LAME_PRESET_STANDARD/LAME_PRESET_EXTREME
			Lame.setEncoderPreset(Lame.LAME_PRESET_EXTREME);
		}

		_Log.e(__CLASSNAME__, "init() ... FINISH!!! (" + getSampleRate() + "," + mBufferSizeInBytes
				+ "," + mPCMBuf.length + "," + mPeriodInFrames + ")");

		return (getState() == AudioRecord.STATE_INITIALIZED);
	}

	private String setFilePath(String songNumber) {
		String ret = "";

		_Log.d(__CLASSNAME__, "setFilePath(...) " + songNumber);

		String path = "";

		File audio = new File(_IKaraoke.RECORD_PATH);
		audio.mkdirs();
		// _Log.d(LOG_TAG, "writing to directory " + audio.getAbsolutePath());

		// construct file name
		String mp3Path = audio.getAbsolutePath() + "/" + songNumber + "." + "mp3";
		String wavPath = audio.getAbsolutePath() + "/" + songNumber + "." + "wav";

		// File output = new File(wavPath);
		// if (output.exists()) {
		// output.delete();
		// }
		// _Log.d(LOG_TAG, "writing to file " + wavPath);

		// start the recording
		if (mCompressed) {
			path = mp3Path;
		} else {
			path = wavPath;
		}

		// 확장자오류변경 - 보류
		// if (mCompressed) {
		// path = replaceFileExt(path, "wav", "mp3");
		// } else {
		// path = replaceFileExt(path, "mp3", "wav");
		// }

		ret = path;
		return ret;
	}

	private String prepareFile(String path) throws Exception {
		String ret = "";

		_Log.d(__CLASSNAME__, "prepareFile(...) " + path);

		if (mCompressed) {
			// Prepare MP3 File.
			mMP3Path = path;
			mMP3File = new File(mMP3Path);
			// 억세스확인이문제
			// if (!mMP3File.canWrite()) {
			// mAudioRecord.release();
			// mAudioRecord = null;
			// return;
			// }
			mMP3OutputStream = new BufferedOutputStream(new FileOutputStream(mMP3File),
					mPCMBuf.length / 2);

		} else {
			// Prepare WAV File.
			mWAVPath = path;
			mWAVFile = new RandomAccessFile(mWAVPath, "rw");
			mWAVFile.setLength(0); // Set file length to 0, to prevent
			// unexpected behavior in case the file
			// already existed
			mWAVFile.writeBytes("RIFF");
			mWAVFile.writeInt(0); // Final file size not known yet, write 0
			mWAVFile.writeBytes("WAVE");
			mWAVFile.writeBytes("fmt ");
			mWAVFile.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
			mWAVFile.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
			mWAVFile.writeShort(Short.reverseBytes((short) getChannelCount()));// Number of channels, 1 for mono, 2 for stereo
			mWAVFile.writeInt(Integer.reverseBytes(getSampleRate())); // Sample rate
			mWAVFile.writeInt(Integer.reverseBytes(getSampleRate() * mSampleRateInBit
					* getChannelCount() / 8)); // Byte rate, SampleRate*NumberOfChannels*BitsPerSample/8
			mWAVFile.writeShort(Short.reverseBytes((short) (getChannelCount() * mSampleRateInBit / 8))); // Block align, NumberOfChannels*BitsPerSample/8
			mWAVFile.writeShort(Short.reverseBytes((short) mSampleRateInBit)); // Bits per sample
			mWAVFile.writeBytes("data");
			mWAVFile.writeInt(0); // Data chunk size not known yet, write 0
		}

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
	@SuppressWarnings("unused")
	private String replaceFileExt(String path, String src, String dst) {
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
	public void onPeriodicNotification(AudioRecord recorder) {


		int bytesRead = 0;
		try {
			if (mAudioRecord != null) {
				bytesRead = read(mPCMBuf, 0, mPCMBuf.length);
			}
		} catch (Exception e) {

			e.printStackTrace();
			return;
		}

		// _Log.d(__CLASSNAME__, "onPeriodicNotification (...)" + mPCMBuf.toString() + "," + mPCMBuf.length);

		if (mPaused) {
			_Log.d(__CLASSNAME__, "RECORD() ... PAUSE!!!");
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
							bytesEncoded = Lame.encode(pcmLBuf, pcmRBuf, samplesRead, mp3Buf, mp3Buf.length);
						}
					} else {
						// default (mono)
						samplesRead = Util.pcmByteToShort(mPCMBuf, pcmLBuf, bytesRead);
						if (samplesRead > 0) {
							bytesEncoded = Lame.encode(pcmLBuf, pcmLBuf, samplesRead, mp3Buf, mp3Buf.length);
						}
					}
					if (bytesEncoded > 0) {
						mMP3OutputStream.write(mp3Buf, 0, bytesEncoded);
					}
				}
			} catch (Exception e) {

				_Log.e(__CLASSNAME__,
						"RECORD() ... NG!!! (" + mPCMBuf.toString() + ", " + mp3Buf.toString() + ", "
								+ bytesEncoded + ")");
				// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
			}
			_Log.d(__CLASSNAME__, "write(...)" + "," + bytesRead + "," + samplesRead + ", "
					+ bytesEncoded + "," + mPayloadSize);
		} else {
			// put WAV buffer to File
			try {
				if (bytesRead > 0) {
					mWAVFile.write(mPCMBuf, 0, bytesRead);
				}
				_Log.d(__CLASSNAME__, "write(...)" + "," + bytesRead + "," + samplesRead + ", "
						+ bytesEncoded + "," + mPayloadSize);
			} catch (Exception e) {

				_Log.e(__CLASSNAME__,
						"RECORD() ... NG!!! (" + mPCMBuf.toString() + ", " + mp3Buf.toString() + ", "
								+ bytesEncoded + ")");
				// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
			}
			mPayloadSize += bytesRead;
		}

	}

	@Override
	public void onMarkerReached(AudioRecord arg0) {


	}

	@Override
	public void pause() {
		_Log.d(__CLASSNAME__, "pause()");
		mPaused = true;
	}

	@Override
	public void resume() {
		_Log.d(__CLASSNAME__, "resume()");
		mPaused = false;
	}

	@Override
	public int getAudioFormat() {

		if (mAudioRecord == null) {
			return -1;
		}
		return mAudioRecord.getAudioFormat();
	}

	@Override
	public int getAudioSource() {

		if (mAudioRecord == null) {
			return -1;
		}
		return mAudioRecord.getAudioSource();
	}

	@Override
	public int getChannelConfiguration() {

		if (mAudioRecord == null) {
			return -1;
		}
		return mAudioRecord.getChannelConfiguration();
	}

	@Override
	public int getChannelCount() {

		if (mAudioRecord == null) {
			return -1;
		}
		return mAudioRecord.getChannelCount();
	}

	@Override
	public int getNotificationMarkerPosition() {

		if (mAudioRecord == null) {
			return -1;
		}
		return mAudioRecord.getNotificationMarkerPosition();
	}

	@Override
	public int getPositionNotificationPeriod() {

		if (mAudioRecord == null) {
			return -1;
		}
		return mAudioRecord.getPositionNotificationPeriod();
	}

	@Override
	public int getRecordingState() {

		if (mAudioRecord == null) {
			return -1;
		}
		return mAudioRecord.getRecordingState();
	}

	@Override
	public int getSampleRate() {

		if (mAudioRecord == null) {
			return -1;
		}
		return mAudioRecord.getSampleRate();
	}

	@Override
	public int getSampleRateBit() {

		return mSampleRateInBit;
	}

	@Override
	public int getState() {

		if (mAudioRecord == null) {
			return -1;
		}
		return mAudioRecord.getState();
	}

	@Override
	public int read(byte[] audioData, int offsetInBytes, int sizeInBytes) {

		if (mAudioRecord == null) {
			return 0;
		}
		if (listener == null) {
			listener.read(audioData, offsetInBytes, sizeInBytes);
		}
		return mAudioRecord.read(audioData, offsetInBytes, sizeInBytes);
	}

	@Override
	public int read(ByteBuffer audioBuffer, int sizeInBytes) {

		if (mAudioRecord == null) {
			return 0;
		}
		if (listener == null) {
			listener.read(audioBuffer, sizeInBytes);
		}
		return mAudioRecord.read(audioBuffer, sizeInBytes);
	}

	@Override
	public int read(short[] audioData, int offsetInShorts, int sizeInShorts) {

		if (mAudioRecord == null) {
			return 0;
		}
		if (listener == null) {
			listener.read(audioData, offsetInShorts, sizeInShorts);
		}
		return mAudioRecord.read(audioData, offsetInShorts, sizeInShorts);
	}

	@Override
	public void release() {

		if (mAudioRecord == null) {
			return;
		}
		mAudioRecord.release();
		mAudioRecord = null;
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

		return isRecording;
	}

}
