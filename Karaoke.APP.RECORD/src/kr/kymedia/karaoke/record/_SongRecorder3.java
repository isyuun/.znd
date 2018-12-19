/**
 * 
 */
package kr.kymedia.karaoke.record;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import kr.kymedia.karaoke._IKaraoke;
import kr.kymedia.karaoke.util._Log;

import net.sourceforge.lame.Lame;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;

/**
 * TODO :개발보류중<br>
 * 
 * <pre>
 * 	AudioRecorder사용
 * 	MP3저장
 *  스레드모델사용
 * 	3차개발모델
 * 	스레드효율이떨어진다.
 * </pre>
 * 
 * <br>
 * 변경자: isyoon<br>
 * 변경일: 2012. 1. 28.<br>
 * 변경명: 최초작성<br>
 * 
 */
public class _SongRecorder3 extends SongRecorder implements ISongRecorder {
	final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	private Thread mThread = null;
	private AudioRecord mAudioRecord = null;
	private static boolean mCompressed = true;
	private boolean mPaused = false;
	private int mPayloadSize = 0;

	short pcmLBuf[] = null;
	short pcmRBuf[] = null;
	byte mp3Buf[] = null;

	public _SongRecorder3(String songNumber) throws Exception {
		_Log.e(__CLASSNAME__, "SongRecorder3(...) " + songNumber);

		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return;
		}

		mCompressed = true;

		String path = setFilePath(songNumber);

		init(path);
	}

	public _SongRecorder3(String songNumber, boolean compressed) throws Exception {
		_Log.e(__CLASSNAME__, "SongRecorder3(...) " + songNumber + "," + compressed);

		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return;
		}

		mCompressed = compressed;

		String path = setFilePath(songNumber);

		init(path);
	}

	protected void init(String path) throws Exception {
		mPath = path;
		_Log.e(__CLASSNAME__, "RECORDING.QUALITY >> " + "fileFormat : " + getFileFormat(mOutputFormat));
		_Log.e(__CLASSNAME__, "RECORDING.QUALITY >> " + "audioCodec : " + getAudioCodec(mAudioEncoder));
		_Log.e(__CLASSNAME__, "RECORDING.QUALITY >> " + "audioBitRate : " + mSampleBitRate);
		_Log.e(__CLASSNAME__, "RECORDING.QUALITY >> " + "audioSampleRate : " + mSampleRate);

		int channelConfig = AudioFormat.CHANNEL_IN_MONO;
		if (mNumChannels > 1) {
			channelConfig = AudioFormat.CHANNEL_IN_STEREO;
		}

		mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRate, channelConfig, mAaudioFormat);

		mAudioRecord = new AudioRecord(AudioSource.MIC, mSampleRate, channelConfig, mAaudioFormat,
				mBufferSizeInBytes);

		if (mAaudioFormat == AudioFormat.ENCODING_PCM_16BIT) {
			mSampleRateInBit = 16;
		} else {
			mSampleRateInBit = 8;
		}

		if (mCompressed) {
			initLame();
		}

		putRecInfo();

	}

	private void initLame() throws Exception {
		int sampleRate = getSampleRate();
		// Lame.LAME_PRESET_MEDIUM/Lame.LAME_PRESET_STANDARD/LAME_PRESET_EXTREME
		int preset = Lame.LAME_PRESET_STANDARD;
		_Log.d(__CLASSNAME__, "initLame()" + ", " + sampleRate + ", " + getChannelCount() + ", "
				+ preset);

		try {
			// 변경명 : 1. 녹음mp3 품질개선
			Lame.initializeEncoder(sampleRate, getChannelCount());
			Lame.setEncoderPreset(preset);
		} catch (ExceptionInInitializerError e) {

			_Log.e(__CLASSNAME__, "initLame() ... NG!!!" + " --> " + _Log.getStackTraceString(e));
			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
			throw e;
		} catch (NoClassDefFoundError e) {

			_Log.e(__CLASSNAME__, "initLame() ... NG!!!" + " --> " + _Log.getStackTraceString(e));
			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
			throw e;
		} catch (Exception e) {
			// TODO: handle exception
			_Log.e(__CLASSNAME__, "initLame() ... NG!!!" + " --> " + _Log.getStackTraceString(e));
			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
			throw e;
		}
	}

	public String setFilePath(String songNumber) {
		String ret = "";

		// _Log.d(__CLASSNAME__, "setFilePath(...) " + songNumber);

		String path = "";

		File audio = new File(_IKaraoke.RECORD_PATH);
		audio.mkdirs();

		String mp3Path = audio.getAbsolutePath() + "/" + songNumber + ".mp3";
		String wavPath = audio.getAbsolutePath() + "/" + songNumber + ".wav";

		// File output = new File(wavPath);
		// if (output.exists()) {
		// output.delete();
		// }

		if (mCompressed) {
			path = mp3Path;
		} else {
			path = wavPath;
		}

		ret = path;
		mPath = path;

		_Log.d(__CLASSNAME__, "setFilePath(...) " + mPath);
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
	public boolean start() throws Exception {

		_Log.e(__CLASSNAME__, "start()");

		try {
			if (mAudioRecord != null) {
				mAudioRecord.startRecording();

				isRecording = true;

				if (!mCompressed) {
					writeWAVFileHeader();
				}

				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				mThread = new Thread(writeAudioDataToFile, "writeAudioDataToFile Thread");

				mThread.start();
				return true;
			}
		} catch (Exception e) {

			_Log.e(__CLASSNAME__, "" + _Log.getStackTraceString(e));
			throw e;
		}
		return true;

	}

	private final Runnable writeAudioDataToFile = new Runnable() {

		@Override
		public void run() {

			mPayloadSize = 0;
			writeAudioDataToFile();
		}
	};

	@Override
	public boolean stop() throws Exception {

		_Log.e(__CLASSNAME__, "stop()");

		try {
			if (null != mAudioRecord) {
				isRecording = false;

				mAudioRecord.stop();
				mAudioRecord.release();

				mAudioRecord = null;
				mThread = null;

				if (mCompressed) {
					Lame.closeEncoder();
				} else {
					closeWAVFile();
				}
			}
		} catch (Exception e) {

			_Log.e(__CLASSNAME__, "" + _Log.getStackTraceString(e));
			throw e;
		}
		return true;
	}

	private void writeWAVFileHeader() {
		_Log.d(__CLASSNAME__, "writeWAVFileHeader()");
		// FileOutputStream out = null;
		//
		// try {
		// out = new FileOutputStream(mPath);
		// long totalAudioLen = out.getChannel().size();
		// long totalDataLen = totalAudioLen + 36;
		// long byteRate = mSampleRateInBit * mSampleRate * mNumChannels / 8;
		// writeWaveFileHeader(out, totalAudioLen, totalDataLen, mSampleRate,
		// mNumChannels, byteRate);
		// out.close();
		// } catch (FileNotFoundException e) {
		//
		// //_Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		// } catch (IOException e) {
		//
		// //_Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		// }
		// Prepare WAV File.
		RandomAccessFile out;
		try {
			out = new RandomAccessFile(mPath, "rw");
			out.setLength(0); // Set file length to 0, to prevent
			// unexpected behavior in case the file
			// already existed
			out.writeBytes("RIFF");
			out.writeInt(0); // Final file size not known yet, write 0
			out.writeBytes("WAVE");
			out.writeBytes("fmt ");
			out.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
			out.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
			out.writeShort(Short.reverseBytes((short) getChannelCount()));// Number of channels, 1 for mono, 2 for stereo
			out.writeInt(Integer.reverseBytes(getSampleRate())); // Sample rate
			out.writeInt(Integer.reverseBytes(getSampleRate() * mSampleRateInBit * getChannelCount() / 8)); // Byte rate, SampleRate*NumberOfChannels*BitsPerSample/8
			out.writeShort(Short.reverseBytes((short) (getChannelCount() * mSampleRateInBit / 8))); // Block align, NumberOfChannels*BitsPerSample/8
			out.writeShort(Short.reverseBytes((short) mSampleRateInBit)); // Bits per sample
			out.writeBytes("data");
			out.writeInt(0); // Data chunk size not known yet, write 0
			out.close();
		} catch (FileNotFoundException e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		} catch (IOException e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		}
	}

	@SuppressWarnings("unused")
	private void writeWAVFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen,
			long longSampleRate, int channels, long byteRate) throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = (byte) mSampleRateInBit; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}

	private void writeAudioDataToFile() {
		_Log.d(__CLASSNAME__, "writeAudioDataToFile()");

		int bytesRead = 0;
		byte pcmBuf[] = new byte[mBufferSizeInBytes];
		pcmLBuf = new short[mBufferSizeInBytes];
		pcmRBuf = new short[mBufferSizeInBytes];
		mp3Buf = new byte[mBufferSizeInBytes];
		// if (getChannelCount() == 2) {
		// // stereo
		// pcmLBuf = new short[mBufferSizeInBytes/4];
		// pcmRBuf = new short[mBufferSizeInBytes/4];
		// mp3Buf = new byte[mBufferSizeInBytes/4];
		// } else {
		// // default (mono)
		// pcmLBuf = new short[mBufferSizeInBytes/2];
		// pcmRBuf = new short[mBufferSizeInBytes/2];
		// mp3Buf = new byte[mBufferSizeInBytes/2];
		// }

		int samplesRead = 0;
		int bytesEncoded = 0;

		RandomAccessFile out = null;
		// BufferedOutputStream out = null;
		try {
			out = new RandomAccessFile(mPath, "rw");
			// out = new BufferedOutputStream(new FileOutputStream(new File(mPath)), mBufferSizeInBytes);
			if (null != out) {
				if (mCompressed) {
					out.setLength(0);
				} else {
					// WAV File Header
					out.seek(44);
				}

				int count = 0;

				while (isRecording) {
					bytesRead = mAudioRecord.read(pcmBuf, 0, mBufferSizeInBytes);
					count++;

					if (mPaused) {
						_Log.d(__CLASSNAME__, "RECORD() ... PAUSE!!!");
						continue;
					}

					if (AudioRecord.ERROR_INVALID_OPERATION != bytesRead) {
						if (mCompressed) {
							if (getChannelCount() == 2) {
								// stereo
								samplesRead = read(pcmBuf, pcmLBuf, pcmRBuf, bytesRead);
								if (samplesRead > 0) {
									bytesEncoded = Lame.encode(pcmLBuf, pcmRBuf, samplesRead, mp3Buf, mp3Buf.length);
								} else {
									_Log.e(__CLASSNAME__, "read(...) BREAK !!!" + "," + samplesRead);
									break;
								}
							} else {
								// default (mono)
								samplesRead = read(pcmBuf, pcmLBuf, bytesRead);
								if (samplesRead > 0) {
									bytesEncoded = Lame.encode(pcmLBuf, pcmLBuf, samplesRead, mp3Buf, mp3Buf.length);
								} else {
									_Log.e(__CLASSNAME__, "read(...) BREAK !!!" + "," + samplesRead);
									break;
								}
							}
							if (samplesRead > 0) {
								out.write(mp3Buf, 0, bytesEncoded);
								mPayloadSize += bytesEncoded;
							}
							_Log.d(__CLASSNAME__, "write(" + count + ")" + "," + bytesRead + "," + samplesRead
									+ ", " + bytesEncoded + "," + mPayloadSize);
						} else {
							out.write(pcmBuf);
							mPayloadSize += bytesRead;
							_Log.d(__CLASSNAME__, "write(" + count + ")" + "," + bytesRead + "," + mPayloadSize);
						} // if (mCompressed) {
					} // if(AudioRecord.ERROR_INVALID_OPERATION != byteRead){
				} // while(isRecording){

				if (mCompressed) {
					bytesEncoded = Lame.flushEncoder(mp3Buf, mp3Buf.length);
					out.write(mp3Buf, 0, bytesEncoded);
				}

				// out.flush();
				out.close();
			}
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		} catch (IOException e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		} catch (Exception e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		}

	}

	private static short byteToShortLE(byte b1, byte b2) {
		return (short) (b1 & 0xFF | ((b2 & 0xFF) << 8));
	}

	/**
	 * Read audio data from input file (mono)
	 * 
	 * @param dst
	 *          mono audio data output buffer
	 * @param numSamples
	 *          number of samples to read
	 * 
	 * @return number of samples read
	 * 
	 * @throws IOException
	 *           if file I/O error occurs
	 */
	public int read(byte[] src, short[] dst, int bytesRead) throws IOException {
		if (getChannelCount() != 1) {
			return -1;
		}

		int index = 0;

		for (int i = 0; i < bytesRead; i += 2) {
			dst[index] = byteToShortLE(src[i], src[i + 1]);
			index++;
		}

		// _Log.d(__CLASSNAME__, "read(...)" + "(" + bytesRead + ", " + index + ") " + src.length + "," + dst.length);

		return index;
	}

	/**
	 * Read audio data from input file (stereo)
	 * 
	 * @param left
	 *          left channel audio output buffer
	 * @param right
	 *          right channel audio output buffer
	 * @param numSamples
	 *          number of samples to read
	 * 
	 * @return number of samples read
	 * 
	 * @throws IOException
	 *           if file I/O error occurs
	 */
	public int read(byte[] src, short[] left, short[] right, int bytesRead) throws IOException {
		if (getChannelCount() != 2) {
			return -1;
		}

		int index = 0;

		for (int i = 0; i < bytesRead; i += 2) {
			short val = byteToShortLE(src[0], src[i + 1]);
			if (i % 4 == 0) {
				left[index] = val;
			} else {
				right[index] = val;
				index++;
			}
		}

		// _Log.d(__CLASSNAME__, "read(...)" + "(" + bytesRead + ", " + index + ") " + src.length + "," + left.length + "," + right.length);

		return index;
	}

	/**
	 * Close WAV File.
	 */
	private void closeWAVFile() {
		_Log.d(__CLASSNAME__, "closeWAVFile()");

		RandomAccessFile out = null;
		try {
			out = new RandomAccessFile(mPath, "rw");
			out.seek(4); // Write size to RIFF header
			out.writeInt(Integer.reverseBytes(36 + mPayloadSize));
			out.seek(40); // Write size to Subchunk2Size field
			out.writeInt(Integer.reverseBytes(mPayloadSize));
			out.close();
		} catch (FileNotFoundException e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		} catch (IOException e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		} catch (Exception e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		}
	}

	/**
	 * Close MP3 File.
	 */
	@SuppressWarnings("unused")
	private void closeMP3File() {
		_Log.d(__CLASSNAME__, "closeMP3File()");

		int bytesEncoded = 0;

		RandomAccessFile out = null;
		try {
			out = new RandomAccessFile(mPath, "rw");
			bytesEncoded = Lame.flushEncoder(mp3Buf, mp3Buf.length);
			out.write(mp3Buf, 0, bytesEncoded);
			out.close();
			_Log.d(__CLASSNAME__, "flush(...)" + "," + mp3Buf.length + "," + bytesEncoded);
			// TODO: write Xing VBR/INFO tag to mp3 file here
		} catch (FileNotFoundException e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		} catch (IOException e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		} catch (Exception e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		}
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
		return mAudioRecord.read(audioData, offsetInBytes, sizeInBytes);
	}

	@Override
	public int read(ByteBuffer audioBuffer, int sizeInBytes) {

		if (mAudioRecord == null) {
			return 0;
		}
		return mAudioRecord.read(audioBuffer, sizeInBytes);
	}

	@Override
	public int read(short[] audioData, int offsetInShorts, int sizeInShorts) {

		if (mAudioRecord == null) {
			return 0;
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
