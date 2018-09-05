/**
 * 2012 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * 프로젝트:	Karaoke.v3
 * 파일명:	SongRecorder3.java	
 * 작성자:	hownam
 *
 * <pre>
 * kr.kymedia.karaoke.record 
 *    |_ SongRecorder3.java
 * </pre>
 * 
 */
/**
 * 
 */
package kr.kymedia.karaoke.record;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class SongRecorder3 extends SongRecorder implements ISongRecorder, SongScoreListener
{
	final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	private SongScoreListener mOnListener;
	private short[] mAudioRecordBuffer;
	private int mAudioRecordBufferLength;
	private AudioRecord audioRecord;

	private short nChannels;
	private int sRate;
	private short mBitsPersample;
	private int mBufferSize;
	private int mAudioSource;
	private int aFormat;

	private int totalTick;
	private int scoreTick;
	private boolean useScore = true;
	private boolean useRecord = true;

	private int mPeriodInFrames;
	private static final int TIMER_INTERVAL = 120;

	private RandomAccessFile randomAccessWriter = null;
	private int payloadSize = 0;
	private boolean bWave = false;
	private final static int[] sampleRates = { 44100, 22050, 11025, 8000 };

	private enum State {
		INITIALIZING, READY, RECORDING, ERROR, STOPPED
	};

	private State mState;

	private int mSampleIndex = 0;
	private int mRecordingTime = 0;
	private int mRecordingIndex = 0;
	private int mScore = 0;

	private final int EASY = -1;
	private final int NORMAL = 0;
	private final int HARD = 1;
	private int mLevel = NORMAL;

	private List<MelodyInfo> melodyInfo = null;

	protected class MelodyInfo {
		public int note;
		public int onset;
		public int offset;
	}

	private static final int fftChunkSize = 0x1000; 	// 4096
	private final static int MIN_FREQUENCY = 49; 	// 49.0 HZ of G1 - lowest note
	// for crazy Russian choir.
	private final static int MAX_FREQUENCY = 1568;	// 1567.98 HZ of G6 - highest
	// demanded note in the
	// classical repertoire

	private final static int SPECTRUM_HZ = MAX_FREQUENCY - MIN_FREQUENCY;

	private static double MAX_AMPLITUDE = 1.2e5;
	private static double noiseLevel = 4e4;
	private static boolean mIsNoiseInitialized = false;
	private static double FFTPerSecond = 0;

	private final static String[] notes = { "a", "a#", "b", "c", "c#", "d", "d#", "e", "f", "f#", "g", "g#" };
	private static Set<Integer> sBlackNotes = new HashSet<Integer>(Arrays.asList(new Integer[] { 1, 4, 6, 9, 11 }));

	private int filled;
	private short[] mAudioData;

	private TreeMap<Double, Integer> NotePitchesMap = new TreeMap<Double, Integer>();
	private double[] NotePitches =
	{
			16.35, 17.32, 18.35, 19.45, 20.60,
			21.83, 23.12, 24.50, 25.96, 27.50,    	// 10
			29.14, 30.87, 32.70, 34.65, 36.71,
			38.89, 41.20, 43.65, 46.25, 49.00,    	// 20
			51.91, 55.00, 58.27, 61.74, 65.41,
			69.30, 73.42, 77.78, 82.41, 87.31,		// 30
			92.50, 98.00, 103.83, 110.00, 116.54,
			123.47, 130.81, 138.59, 146.83, 155.56, 	// 40
			164.81, 174.61, 185.00, 196.00, 207.65,
			220.00, 233.08, 246.94, 261.63, 277.18,		// 50
			293.66, 311.13, 329.63, 349.23, 369.99,
			392.00, 415.30, 440.00, 466.16, 493.88, 	// 60
			523.25, 554.37, 587.33, 622.25, 659.26,
			698.46, 739.99, 783.99, 830.61, 880.00,		// 70
			932.33, 987.77, 1046.50, 1108.73, 1174.66,
			1244.51, 1318.51, 1396.91, 1479.98, 1567.98, 	// 80
			1661.22, 1760.00, 1864.66, 1975.53, 2093.00,
			2217.46, 2349.32, 2489.02, 2637.02, 2793.83,		// 90
			2959.96, 3135.96, 3322.44, 3520.00, 3729.31,
			3951.07, 4186.01, 4434.92, 4698.64, 4978.03,		// 100
			5274.04, 5587.65, 5919.91, 6271.92, 6644.87,
			7040.00, 7458.62, 7902.13, 8372.01, 8869.84,		// 110
			9397.27, 9956.06, 10548.08, 11175.30, 11839.82,
			12543.85, 13289.75, 14080.00, 14917.24, 15804.26		// 120
	};

	/**
	 * 녹음중확인
	 */
	private boolean isRecording = false;

	static {
		System.loadLibrary("karaoke");
	}

	private static double distanceFromA4(double frequency) {
		return Math.log(frequency / 440) * 12 / Math.log(2);
	}

	/*
	 * Modulus without negative results
	 */
	private static long mod(long x, long y)
	{
		long result = x % y;
		if (result < 0)
		{
			result += y;
		}
		return result;
	}

	private static int closestNoteIndex(double distanceFromA4)
	{
		return (int) mod(Math.round(distanceFromA4), 12);
	}

	private static String noteName(double distanceFromA4) {
		// 440 Hz is A4 and there are 9 half-steps from C4 to A4
		long octaveNumber = 4 + (long) Math.floor((9.0 + Math.round(distanceFromA4)) / 12.0);
		return notes[closestNoteIndex(distanceFromA4)] + octaveNumber;
	}

	private static String hzToNoteName(double frequency) {
		// distance in half-steps
		double distanceFromA4 = distanceFromA4(frequency);
		return noteName(distanceFromA4);
	}

	private int getPitchCoord(double pitch) {
		final SortedMap<Double, Integer> tail_map = NotePitchesMap.tailMap(pitch);
		final SortedMap<Double, Integer> head_map = NotePitchesMap.headMap(pitch);
		final double closest_right = tail_map == null || tail_map.isEmpty() ? NotePitchesMap.lastKey() : tail_map.firstKey();
		final double closest_left = head_map == null || head_map.isEmpty() ? NotePitchesMap.firstKey() : head_map.lastKey();
		if (closest_right - pitch < pitch - closest_left) {
			return NotePitchesMap.get(closest_right);
		} else {
			return NotePitchesMap.get(closest_left);
		}
	}

	private static class FreqResult {
		// public HashMap<Double, Double> frequencies;
		public double bestFrequency;
		// public double maxAmp;
		// public boolean isPitchDetected;
		public double noiseLevel;
		public int note;

		// public String noteName;

		public FreqResult() {
			// this.frequencies = null;
			this.bestFrequency = 0.0;
			// this.isPitchDetected = false;
			this.noiseLevel = 0.0;
			this.note = 0;
		}

		@Override
		public String toString() {
			return "<FreqResult: " + bestFrequency + " Hz" + ", note:" + note + ", noiseLevel:" + noiseLevel + ">";
		}

		// public void destroy() {
		// frequencies.clear();
		// }
	}

	private static class FrequencyCluster {
		public double average_frequency = 0;
		public double total_amplitude = 0;

		public void add(double freq, double amplitude) {
			double new_total_amp = total_amplitude + amplitude;
			average_frequency = (total_amplitude * average_frequency + freq * amplitude) / new_total_amp;
			total_amplitude = new_total_amp;
		}

		/*
		 * public boolean isNear(double freq) {
		 * if (Math.abs(1 - (average_frequency / freq)) < 0.05) {
		 * // only 5% difference
		 * return true;
		 * } else {
		 * return false;
		 * }
		 * }
		 */

		public boolean isHarmonic(double freq) {
			double harmonic_factor = freq / average_frequency;
			double distance_from_int = Math.abs(Math.round(harmonic_factor) - harmonic_factor);
			if (distance_from_int < 0.05) {
				// only 5% distance
				return true;
			} else {
				return false;
			}
		}

		public void addHarmony(double freq, double amp) {
			double exactHarmonicFactor = Math.round(freq / average_frequency);
			double newFrequency = freq / exactHarmonicFactor;
			this.add(newFrequency, amp);
		}

		@Override
		public String toString() {
			return "(" + average_frequency + ", " + total_amplitude + ")";
		}
	}

	private FreqResult AnalyzeFrequencies(short[] audio_data) {
		double[] frequencyData = new double[audio_data.length * 2];

		for (int i = 0; i < audio_data.length; i++) {
			frequencyData[i * 2] = audio_data[i];
			frequencyData[i * 2 + 1] = 0;
		}

		DoFFT(frequencyData, audio_data.length);

		return AnalyzeFFT(audio_data.length, frequencyData);
	}

	private FreqResult AnalyzeFFT(int audioDataLength, double[] frequencyData) {
		// boolean pitchDetected = false;
		double best_frequency = 0;
		double bestAmplitude = 0;
		// HashMap<Double, Double> frequencies = new HashMap<Double, Double>();
		List<Double> bestFrequencies = new ArrayList<Double>();
		List<Double> bestAmps = new ArrayList<Double>();

		FreqResult fr = new FreqResult();

		final int min_frequency_fft = (int) Math.round(1.0 * MIN_FREQUENCY * audioDataLength / sampleRates[mSampleIndex]);
		final int max_frequency_fft = (int) Math.round(1.0 * MAX_FREQUENCY * audioDataLength / sampleRates[mSampleIndex]);

		for (int i = min_frequency_fft; i <= max_frequency_fft; i++) {

			final double currentFrequency = i * 1.0 * sampleRates[mSampleIndex]
					/ audioDataLength;

			// round to nearest DRAW_FREQUENCY_STEP (eg 63/64/65 -> 65 hz)
			// final double draw_frequency = Math
			// .round(current_frequency
			// / DRAW_FREQUENCY_STEP)
			// * DRAW_FREQUENCY_STEP;
			final double current_amplitude = Math.pow(frequencyData[i * 2], 2)
					+ Math.pow(frequencyData[i * 2 + 1], 2);

			// final double normalized_amplitude = current_amplitude * normalFreqAmp / current_frequency;
			final double normalizedAmplitude = Math.pow(current_amplitude, 0.5) / currentFrequency;

			// frequencies.put(draw_frequency, Math.pow(current_amplitude, 0.5) / draw_frequency_step + current_sum_for_this_slot);
			// frequencies.put(draw_frequency, normalized_amplitude / DRAW_FREQUENCY_STEP + current_sum_for_this_slot);
			// frequencies.put(currentFrequency, normalizedAmplitude);

			// find peaks
			// NOTE: this finds all the relevant peaks because their
			// amplitude usually keeps rising with the frequency.
			if (normalizedAmplitude > bestAmplitude) {

				// it's important to note the best_amplitude also for noise level measurement.
				best_frequency = currentFrequency;
				bestAmplitude = normalizedAmplitude;

				// make sure this isn't the 48.44970703125 FFT artifact
				// and that this isn't some background noise
				if ((currentFrequency > MIN_FREQUENCY) && (normalizedAmplitude > noiseLevel)) {
					bestFrequencies.add(currentFrequency);
					bestAmps.add(bestAmplitude);
				}
			}
		}

		best_frequency = clusterFrequencies(bestFrequencies, bestAmps);

		// if ( (bestAmplitude > noiseLevel) && (best_frequency > 0)) {
		// pitchDetected = true;
		// }

		if (!mIsNoiseInitialized) {
			// the first sample + 50% means we catch some jitter in the noise amplitude too.
			noiseLevel = bestAmplitude * 1.5;
			mIsNoiseInitialized = true;
			if (noiseLevel > MAX_AMPLITUDE / 2) {
				noiseLevel = MAX_AMPLITUDE / 2;
				// Log.e("AudioRecordPlay", "Noise levels are too high.");
			}
		}

		fr.bestFrequency = best_frequency;
		// fr.frequencies = frequencies;
		// fr.isPitchDetected = pitchDetected;
		fr.noiseLevel = noiseLevel;
		// fr.maxAmp = bestAmplitude;
		fr.note = getPitchCoord(best_frequency * 10);
		// fr.noteName = hzToNoteName(best_frequency);

		return fr;
	}

	private static double clusterFrequencies(List<Double> bestFrequencies, List<Double> bestAmps) {
		List<FrequencyCluster> clusters = new ArrayList<FrequencyCluster>();
		FrequencyCluster currentCluster = new FrequencyCluster();
		clusters.add(currentCluster);

		if (bestFrequencies.size() > 0)
		{
			currentCluster.add(bestFrequencies.get(0), bestAmps.get(0));
		}

		// init clusters and
		// join near clusters
		for (int i = 1; i < bestFrequencies.size(); i++)
		{
			double freq = bestFrequencies.get(i);
			double amp = bestAmps.get(i);

			// this isn't near
			// NOTE: assuming harmonies are consecutive (no unharmonics in between harmonies)
			currentCluster = new FrequencyCluster();
			clusters.add(currentCluster);
			currentCluster.add(freq, amp);
		}

		// join harmonies
		// there should be only 6-10 peaks, so this isn't too bad that it's O
		FrequencyCluster nextCluster;
		for (int i = 0; i < clusters.size(); i++) {
			currentCluster = clusters.get(i);
			for (int j = i + 1; j < clusters.size(); j++) {
				nextCluster = clusters.get(j);
				if (currentCluster.isHarmonic(nextCluster.average_frequency)) {
					// Give harmonies a x10 bonus point because I see there are strange stuff like
					// 12.2222 and 12.444 harmonies which are the strongest but obviously wrong.
					currentCluster.addHarmony(nextCluster.average_frequency, nextCluster.total_amplitude);
				}
			}
		}

		// find best cluster
		double bestClusterAmplitude = 0;
		double bestFrequency = 0;
		for (int i = 0; i < clusters.size(); i++) {
			FrequencyCluster clu = clusters.get(i);
			if (bestClusterAmplitude < clu.total_amplitude) {
				bestClusterAmplitude = clu.total_amplitude;
				bestFrequency = clu.average_frequency;
			}
		}

		return bestFrequency;
	}

	/** * Convert midi notes to floating point pitches - based on sc_midicps in the SC C++ code */
	private float sc_midicps(float note) {
		return (float) (440.0 * Math.pow((float) 2., (note - 69.0) * (float) 0.083333333333));
	}

	private int swap(int i) {
		int byte0 = i & 0xff;
		int byte1 = (i >> 8) & 0xff;
		int byte2 = (i >> 16) & 0xff;
		int byte3 = (i >> 24) & 0xff;

		return (byte0 << 24) | (byte1 << 16) | (byte2 << 8) | byte3;
	}

	private byte[] ShortToByte(short[] input)
	{
		int short_index, byte_index;
		int iterations = input.length;

		byte[] buffer = new byte[input.length * 2];

		short_index = byte_index = 0;

		for (/* NOP */; short_index != iterations; /* NOP */)
		{
			buffer[byte_index] = (byte) (input[short_index] & 0x00FF);
			buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);

			++short_index;
			byte_index += 2;
		}

		return buffer;
	}

	private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener() {
		public void onPeriodicNotification(AudioRecord recorder) {
			if (State.RECORDING != mState) {
				return;
			}

			audioRecord.read(mAudioRecordBuffer, 0, mAudioRecordBufferLength); // read audio data to buffer

			if (useScore == true) {
				runFFT();
			}

			if (useRecord == true) {
				if (bWave == true) {
					try {
						randomAccessWriter.write(ShortToByte(mAudioRecordBuffer));
						payloadSize += mAudioRecordBuffer.length;
					} catch (IOException e) {
					}
				} else {
					WriteRecord();
					DoRecord();
				}
			}
		}

		public void onMarkerReached(AudioRecord recorder) {
		}
	};

	private void runFFT() {
		if (mRecordingTime / TIMER_INTERVAL % 3 == 0) {
			updateFFT();
		} else {
			mRecordingTime += TIMER_INTERVAL;
		}
	}

	private void updateFFT() {
		short[] audioBuffer = new short[fftChunkSize];
		double chunk = mAudioRecordBufferLength / fftChunkSize;
		int rectime = (int) (TIMER_INTERVAL / chunk);
		if (chunk < 1) {
			mRecordingTime += TIMER_INTERVAL;

			System.arraycopy(mAudioRecordBuffer, 0, audioBuffer, 0, mAudioRecordBufferLength);
			FreqResult fr = AnalyzeFrequencies(audioBuffer);

			for (int j = mRecordingIndex; j < melodyInfo.size(); j++) {
				if (mRecordingTime >= melodyInfo.get(j).onset && mRecordingTime <= melodyInfo.get(j).offset) {
					if (fr.note >= melodyInfo.get(j).note - 5 + 5 * mLevel && fr.note <= melodyInfo.get(j).note + 20 - 5 * mLevel) {
						scoreTick++;
					}

					mRecordingIndex = j;

					if (j + 1 < melodyInfo.size() && (melodyInfo.get(j + 1).onset - melodyInfo.get(j).onset < 4000)) {
						totalTick++;
					}

					break;
				}
			}
		} else {
			for (int i = 0; i < (int) chunk; i++) {
				mRecordingTime += rectime;

				System.arraycopy(mAudioRecordBuffer, i * fftChunkSize, audioBuffer, 0, fftChunkSize);
				FreqResult fr = AnalyzeFrequencies(audioBuffer);

				for (int j = mRecordingIndex; j < melodyInfo.size(); j++) {
					if (mRecordingTime >= melodyInfo.get(j).onset && mRecordingTime <= melodyInfo.get(j).offset) {
						if (fr.note >= melodyInfo.get(j).note - 5 + 5 * mLevel && fr.note <= melodyInfo.get(j).note + 20 - 5 * mLevel) {
							scoreTick++;
						}

						mRecordingIndex = j;

						if (j + 1 < melodyInfo.size() && (melodyInfo.get(j + 1).onset - melodyInfo.get(j).onset < 4000)) {
							totalTick++;
						}

						break;
					}
				}
			}
		}
	}

	/**
	 * 생성자 (yyyyMMddHHmmss형식의 파일이름 생성)
	 * 
	 * @param songNumber
	 *          : 곡번호
	 * @param path
	 *          : 파일 저장위치(디렉토리 경로)
	 * @return
	 */
	public SongRecorder3(String songNumber, String path) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String currentDateandTime = sdf.format(new Date());

		mPath = path + "/" + currentDateandTime + ".mp3";

		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return;
		}

		info(77, 0, 1000 * 420);

		if (useScore == true)
			download(songNumber);

		init(path);
	}

	/**
	 * 생성자 (yyyyMMddHHmmss형식의 파일이름 생성)
	 * 
	 * @param songNumber
	 *          : 곡번호
	 * @param path
	 *          : 파일 저장위치(디렉토리 경로)
	 * @param level
	 *          : 난이도 설정("easy", "normal", "hard")
	 * @return
	 */
	public SongRecorder3(String songNumber, String path, String level) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String currentDateandTime = sdf.format(new Date());

		mPath = path + "/" + currentDateandTime + ".mp3";

		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return;
		}

		info(77, 0, 1000 * 420);

		if (useScore == true)
			download(songNumber);

		init(path);

		setLevel(level);
	}

	/*
	 * public SongRecorder3(String songNumber) throws Exception {
	 * 
	 * String path = setFilePath(songNumber);
	 * 
	 * if (!android.os.Environment.getExternalStorageState().equals(
	 * android.os.Environment.MEDIA_MOUNTED)) {
	 * return;
	 * }
	 * 
	 * mPath = path;
	 * info(77, 0, 1000 * 420);
	 * 
	 * if ( useScore == true )
	 * download(songNumber);
	 * 
	 * init(path);
	 * }
	 * 
	 * public SongRecorder3(String songNumber, boolean compressed) throws Exception {
	 * 
	 * String path = setFilePath(songNumber);
	 * 
	 * if (!android.os.Environment.getExternalStorageState().equals(
	 * android.os.Environment.MEDIA_MOUNTED)) {
	 * return;
	 * }
	 * 
	 * mPath = path;
	 * info(77, 0, 1000 * 420);
	 * 
	 * if ( useScore == true )
	 * download(songNumber);
	 * 
	 * init(path);
	 * }
	 */

	protected void download(String number) {

		AsyncTask<String, Integer, Void> task = new AsyncTask<String, Integer, Void>()
		{
			final int IO_BUFFER_SIZE = 4096;

			@Override
			protected void onPreExecute()
			{
			}

			@Override
			protected Void doInBackground(String... urls)
			{
				String songId = urls[0];

				byte[] rok = null;

				try {
					rok = download("http://cyms.chorus.co.kr/mlkara_dl.asp?song_id=" + songId);
				} catch (IOException e) {
					if (mOnListener != null)
						mOnListener.onError();

					info(77, 0, 1000 * 420);
					return null;
				}

				if (rok == null) {
					if (mOnListener != null)
						mOnListener.onError();

					info(77, 0, 1000 * 420);
					return null;
				}

				InputStream is = new ByteArrayInputStream(rok);

				info(is);
				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... progress)
			{
			}

			@Override
			protected void onPostExecute(Void result)
			{
			}

			@Override
			protected void onCancelled()
			{
			}

			private byte[] download(String path) throws IOException {
				URL url = null;
				try {
					url = new URL(path);
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return null;
				}

				InputStream in = null;
				try {
					URLConnection conn = url.openConnection();
					conn.setConnectTimeout(7000);
					conn.connect();
					in = new BufferedInputStream(conn.getInputStream(), IO_BUFFER_SIZE);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}

				ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				BufferedOutputStream out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);

				try {
					copy(in, out);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}

				return dataStream.toByteArray();
			}

			private void copy(InputStream in, OutputStream out) throws IOException {
				byte[] b = new byte[IO_BUFFER_SIZE];
				int read;
				int totalDownloaded = 0;
				boolean first = false;
				int pos = -1;
				while ((read = in.read(b)) != -1) {
					totalDownloaded += read;
					out.write(b, 0, read);

					if (first == false) {
						if (read > 41) {
							if (b[0] == 0x52 && b[1] == 0x4f && b[2] == 0x4b) {
								pos = ((b[32] << 24) + ((b[31] & 0xff) << 16) + ((b[30] & 0xff) << 8) + (b[29] & 0xff));
							} else if (b[0] == 0x4c && b[1] == 0x59 && b[2] == 0x52) {
								pos = ((b[40] << 24) + ((b[39] & 0xff) << 16) + ((b[38] & 0xff) << 8) + (b[37] & 0xff));
							}
						}
						first = true;
					}

					if (pos > 0 && totalDownloaded > pos)
						break;
				}
			}
		};

		task.execute(number);
	}

	protected boolean init(String path) throws Exception {
		// Log.e(__CLASSNAME__, getMethodName() + path);

		int number = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
			number = 0;
		} else {
			number = sampleRates.length - 1;
		}

		mOnListener = null;

		boolean result = false;
		int i = 0;
		do {
			result = prepare(AudioSource.MIC,
					sampleRates[Math.abs(number - i)],
					// AudioFormat.CHANNEL_IN_STEREO,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT);
		} while ((++i < sampleRates.length) & !(mState == State.INITIALIZING));

		/*
		 * if ( !(mState == State.INITIALIZING) ) {
		 * result = prepare(AudioSource.MIC,
		 * sampleRates[i-1],
		 * AudioFormat.CHANNEL_IN_MONO,
		 * AudioFormat.ENCODING_PCM_16BIT);
		 * }
		 */

		return result;
	}

	/**
	 * 점수 기능 사용 유무
	 * 
	 * @return true:점수체크, false:점수체크빠짐(0점)
	 */
	public boolean getUseScore() {
		return useScore;
	}

	/**
	 * 점수 기능 사용 유무 설정
	 * 
	 * @param use
	 *          : true:점수체크, false:점수체크빠짐(0점)
	 */
	public void setUseScore(boolean use) {
		useScore = use;
	}

	/**
	 * 녹음 기능 사용 유무
	 * 
	 * @return true:녹음, false:비녹음
	 */
	public boolean getUseRecord() {
		return useRecord;
	}

	/**
	 * 녹음 기능 사용 유무 설정
	 * 
	 * @param use
	 *          : true:녹음, false:비녹음
	 */
	public void setUseRecord(boolean use) {
		useRecord = use;
	}

	/**
	 * 녹음파일의 전체 디렉토리 경로
	 * 
	 * @return 녹음 파일 디렉토리 경로
	 */
	public String getPath() {
		return mPath;
	}

	/*
	 * public static String setFilePath(String songNumber) {
	 * String ret = "";
	 * 
	 * //Log.d(__CLASSNAME__, "setFilePath(...) " + songNumber);
	 * 
	 * String path = "";
	 * 
	 * File audio = new File(IKaraoke.RECORD_PATH);
	 * audio.mkdirs();
	 * 
	 * // 파일확장자mp3
	 * String mp4Path = audio.getAbsolutePath() + "/" + songNumber + "." + "mp3";
	 * 
	 * path = mp4Path;
	 * 
	 * ret = path;
	 * 
	 * //Log.d(__CLASSNAME__, "writing to file " + mPath);
	 * return ret;
	 * }
	 */

	protected boolean prepare(int audioSource, int sampleRate, int channelConfig, int audioFormat) throws Exception {
		if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
			mBitsPersample = 16;
		} else {
			mBitsPersample = 8;
		}

		if (channelConfig == AudioFormat.CHANNEL_IN_MONO) {
			nChannels = 1;
		} else {
			nChannels = 2;
		}

		for (int i = 0; i < sampleRates.length; i++) {
			if (sampleRates[i] == sampleRate) {
				mSampleIndex = i;
				break;
			}
		}

		mAudioSource = audioSource;
		sRate = sampleRate;
		aFormat = audioFormat;

		mSampleRate = sampleRate;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
			mSampleBitRate = 128000;
		} else {
			mSampleBitRate = 12200;
		}

		try {
			mPeriodInFrames = sRate * TIMER_INTERVAL / 1000;
			mBufferSize = mPeriodInFrames * 2 * nChannels * mBitsPersample / 8;
			if (mBufferSize < AudioRecord.getMinBufferSize(sRate, channelConfig, aFormat)) {
				mBufferSize = AudioRecord.getMinBufferSize(sRate, channelConfig, aFormat);
				mPeriodInFrames = mBufferSize / (2 * mBitsPersample * nChannels / 8);
			}

			audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, mBufferSize);

			audioRecord.setRecordPositionUpdateListener(updateListener);
			audioRecord.setPositionNotificationPeriod(mPeriodInFrames);
			mState = State.INITIALIZING;
		} catch (Exception e) {
			e.printStackTrace();
			mState = State.ERROR;
			return false;
		}

		return true;
	}

	/**
	 * 녹음 시작
	 * 
	 * @return 녹음 시작 성공 유무
	 */
	@Override
	public boolean start() throws Exception {
		// Log.d(__CLASSNAME__, "start()");
		try {
			open(mPath);
			isRecording = true;
			return true;
		} catch (Exception e) {
			Log.e(__CLASSNAME__, "" + Log.getStackTraceString(e));
			throw e;
		}
	}

	/**
	 * 녹음 중지
	 * 
	 * @return 녹음 중지 성공 유무
	 */
	@Override
	public boolean stop() throws Exception {
		// Log.d(__CLASSNAME__, "stop()");
		try {
			close();
			isRecording = false;
			return true;
		} catch (Exception e) {
			Log.e(__CLASSNAME__, "" + Log.getStackTraceString(e));
			throw e;
		}
	}

	/**
	 * 녹음 일시정지
	 */
	@Override
	public void pause() {
	}

	/**
	 * 녹음 다시시작
	 */
	@Override
	public void resume() {
	}

	/**
	 * 오디오 포맷 : 사용하지 않음
	 */
	@Override
	public int getAudioFormat() {
		return 0;
	}

	/**
	 * 오디오 소스 : 사용하지 않음
	 */
	@Override
	public int getAudioSource() {
		return 0;
	}

	/**
	 * 채컬 설정 : 사용하지 않음
	 */
	@Override
	public int getChannelConfiguration() {
		return 0;
	}

	/**
	 * 채컬 개수 : 사용하지 않음
	 */
	@Override
	public int getChannelCount() {
		return 0;
	}

	/**
	 * NotificationMarkerPosition : 사용하지 않음
	 */
	@Override
	public int getNotificationMarkerPosition() {
		return 0;
	}

	/**
	 * PositionNotificationPeriod : 사용하지 않음
	 */
	@Override
	public int getPositionNotificationPeriod() {
		return 0;
	}

	/**
	 * 녹음 상태 : 사용하지 않음
	 */
	@Override
	public int getRecordingState() {
		return 0;
	}

	/**
	 * 샘플링 레이트
	 * 
	 * @return 샘플링 레이트 (44100, 22050, 11025, 8000)
	 */
	@Override
	public int getSampleRate() {
		return mSampleRate;
	}

	/**
	 * 샘플 비트
	 * 
	 * @return 샘플 비트 (16, 8) : 16고정
	 */
	@Override
	public int getSampleRateBit() {
		return mSampleBitRate;
	}

	/**
	 * 상태 : 사용하지 않음
	 */
	@Override
	public int getState() {
		return 0;
	}

	/**
	 * 녹음 버퍼 : 사용하지 않음
	 */
	@Override
	public int read(byte[] audioData, int offsetInBytes, int sizeInBytes) {
		return 0;
	}

	/**
	 * 녹음 버퍼 : 사용하지 않음
	 */
	@Override
	public int read(ByteBuffer audioBuffer, int sizeInBytes) {
		return 0;
	}

	/**
	 * 녹음 버퍼 : 사용하지 않음
	 */
	@Override
	public int read(short[] audioData, int offsetInShorts, int sizeInShorts) {
		return 0;
	}

	/**
	 * 레코딩 객체 릴리즈
	 */
	@Override
	public void release() {
		// Log.d(__CLASSNAME__, "release()");
		if (audioRecord != null) {
			audioRecord.release();
		}
	}

	/**
	 * NotificationMarkerPosition : 사용하지 않음
	 */
	@Override
	public int setNotificationMarkerPosition(int markerInFrames) {
		return 0;
	}

	/**
	 * PositionNotificationPeriod : 사용하지 않음
	 */
	@Override
	public int setPositionNotificationPeriod(int periodInFrames) {
		return 0;
	}

	/**
	 * 생성되는 파일 포맷
	 * 
	 * @return mp3 고정
	 */
	public String getFileFormat() {
		String ret = "mp3";

		return ret;
	}

	/**
	 * 인코딩 포맷
	 * 
	 * @return mp3 고정
	 */
	public String getEncodingFormat() {
		String ret = "mp3";

		return ret;
	}

	/**
	 * 녹음 여부 확인
	 * param : true:레코딩중, false:중지
	 */
	@Override
	public boolean isRecording() {
		return isRecording;
	}

	/**
	 * 녹음 우선순위 설정
	 * 
	 * @param level
	 *          : THREAD_PRIORITY_AUDIO, THREAD_PRIORITY_URGENT_AUDIO
	 */
	public void setPriority(int level) {
		android.os.Process.setThreadPriority(level); // android.os.Process.THREAD_PRIORITY_AUDIO //THREAD_PRIORITY_URGENT_AUDIO);
	}

	/**
	 * 점수
	 * 
	 * @return 점수값
	 */
	public int getScore() {
		return mScore;
	}

	private boolean info(int note, int start, int end) {
		if (melodyInfo != null)
			melodyInfo.clear();

		melodyInfo = new ArrayList<MelodyInfo>();
		MelodyInfo info = new MelodyInfo();
		info.note = note;
		info.onset = start;
		info.offset = end;
		melodyInfo.add(info);

		return true;
	}

	private boolean info(InputStream is) {
		if (melodyInfo != null)
			melodyInfo.clear();

		melodyInfo = new ArrayList<MelodyInfo>();

		try {
			DataInputStream dis = new DataInputStream(is);

			dis.skipBytes(29);
			int music = swap(dis.readInt());

			dis.skipBytes(4);
			int melody = swap(dis.readInt());

			dis.skipBytes(melody - 41);

			int size = music - melody;
			int time = 0;
			for (int i = 0; i < size; i += 12) {
				MelodyInfo info = new MelodyInfo();
				info.note = swap(dis.readInt());
				info.onset = swap(dis.readInt());
				info.offset = swap(dis.readInt());

				if (info.onset > time) {
					melodyInfo.add(info);
					time = info.onset;
				}
			}
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	private boolean open(String path) {
		mScore = 0;
		filled = 0;
		totalTick = 1;
		scoreTick = 0;
		mRecordingIndex = 0;
		mRecordingTime = 0;

		payloadSize = 0;
		mAudioRecordBufferLength = mBufferSize / 4; // (mPeriodInFrames*mBitsPersample/8*nChannels);
		mAudioRecordBuffer = new short[mAudioRecordBufferLength];
		mAudioData = new short[mAudioRecordBufferLength * 10];

		for (int i = 0; i < NotePitches.length; i++) {
			NotePitchesMap.put(NotePitches[i], i);  // encode coordinates
		}

		if (useRecord == true) {
			String ext = path.substring(path.length() - 3, path.length());
			if (ext.equals("wav") == true) {
				try {
					randomAccessWriter = new RandomAccessFile(path, "rw");
					randomAccessWriter.setLength(0); // Set file length to 0, to prevent unexpected behavior in case the file already existed
					randomAccessWriter.writeBytes("RIFF");
					randomAccessWriter.writeInt(0); // Final file size not known yet, write 0
					randomAccessWriter.writeBytes("WAVE");
					randomAccessWriter.writeBytes("fmt ");
					randomAccessWriter.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
					randomAccessWriter.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
					randomAccessWriter.writeShort(Short.reverseBytes(nChannels));// Number of channels, 1 for mono, 2 for stereo
					randomAccessWriter.writeInt(Integer.reverseBytes(sRate)); // Sample rate
					randomAccessWriter.writeInt(Integer.reverseBytes(sRate * nChannels * mBitsPersample / 8)); // Byte rate, SampleRate*NumberOfChannels*mBitsPersample/8
					randomAccessWriter.writeShort(Short.reverseBytes((short) (nChannels * mBitsPersample / 8))); // Block align, NumberOfChannels*mBitsPersample/8
					randomAccessWriter.writeShort(Short.reverseBytes(mBitsPersample)); // Bits per sample
					randomAccessWriter.writeBytes("data");
					randomAccessWriter.writeInt(0); // Data chunk size not known yet, write 0
					mState = State.READY;
				} catch (Exception e) {
					mState = State.ERROR;
					if (mOnListener != null)
						mOnListener.onError();
					return false;
				}

				bWave = true;
			} else {
				int ret = -1;

				try {
					ret = OpenRecord(path, mBitsPersample, sRate, nChannels, sRate, nChannels, mAudioRecordBuffer, mAudioRecordBufferLength);
				} catch (UnsatisfiedLinkError e) {
					return false;
				}

				if (ret < 0) {
					if (mOnListener != null)
						mOnListener.onError();
					return false;
				}

				mState = State.READY;
				bWave = false;

				/*
				 * (new Thread(new Runnable() {
				 * 
				 * @Override
				 * public void run() {
				 * while(mState != State.STOPPED) {
				 * Log.e(__CLASSNAME__, "DoRecord()");
				 * DoRecord();
				 * try {
				 * Thread.sleep(100);
				 * } catch (InterruptedException e) {}
				 * }
				 * }
				 * })).start();
				 */
			}
		} else {
			mState = State.READY;
		}

		if (mState == State.READY) {
			try {
				audioRecord.startRecording();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				mState = State.ERROR;
				if (mOnListener != null)
					mOnListener.onError();
				return false;
			}
			audioRecord.read(mAudioRecordBuffer, 0, mAudioRecordBufferLength);
			mState = State.RECORDING;
		} else {
			mState = State.ERROR;
			if (mOnListener != null)
				mOnListener.onError();
			return false;
		}

		return true;
	}

	private int score() {
		int score = (int) (scoreTick * 100 / totalTick * 1.1);

		if (score > 100)
			score = 100;
		else if (score < 30) {
			score = 0;
		}

		if (score > 70 && score < 90) {
			Random rand = new Random();
			score = Math.abs(rand.nextInt(30) + 70);
		} else if (score >= 95) {
			score = 100;
		}

		return score;
	}

	/**
	 * 녹음 중지
	 */
	public void close() {
		if (mState == State.RECORDING) {
			mState = State.STOPPED;

			if (mOnListener != null) {
				mScore = score();
				mOnListener.onCompletion(mScore);
				// Log.e("SongRecorder3", "score:"+mScore);
			}

			try {
				audioRecord.stop();
			} catch (IllegalStateException e) {
			}

			if (useRecord == true) {
				if (bWave == true) {
					try {
						randomAccessWriter.seek(4); // Write size to RIFF header
						randomAccessWriter.writeInt(Integer.reverseBytes(36 + payloadSize));

						randomAccessWriter.seek(40); // Write size to Subchunk2Size field
						randomAccessWriter.writeInt(Integer.reverseBytes(payloadSize));

						randomAccessWriter.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						CloseRecord();
					} catch (UnsatisfiedLinkError e) {
					}
				}
			}
		} else {
			mState = State.ERROR;
		}
	}

	private void setLevel(String level)
	{
		if (level == null || level.equals("") == true) {
			mLevel = NORMAL;
		}

		String str = level.toLowerCase();
		if (str.equals("easy") == true) {
			mLevel = EASY;
		} else if (str.equals("normal") == true) {
			mLevel = NORMAL;
		} else if (str.equals("hard") == true) {
			mLevel = HARD;
		} else {
			mLevel = NORMAL;
		}
	}

	/**
	 * 점수 리스너 설정
	 */
	public void setOnListener(SongScoreListener listener)
	{
		mOnListener = listener;
	}

	/**
	 * 점수 리스너 완료
	 * param : 점수
	 */
	@Override
	public void onCompletion(int score)
	{
	}

	/**
	 * 점수 처리중 에러
	 */
	@Override
	public void onError()
	{
	}

	private native int OpenRecord(String filename, int bit_rate, int sample_rate, int channels, int output_samplerate, int output_channels, short[] audioBuffer, int length);

	private native void CloseRecord();

	private native void WriteRecord();

	private native void DoRecord();

	private native void DoFFT(double[] data, int size);
}
