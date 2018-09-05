package kr.kymedia.karaoke.play3;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.util.Log;

public class AudioVisualizer
{
	private byte[] mBytes;
	private byte[] mFFTBytes;
	private Visualizer mVisualizer;

	private float[] mPoints;
	private Paint mPaint;
	private Paint mFlashPaint;
	private boolean mCycleColor;
	private boolean mTop;
	private int mDivisions;
	private float amplitude = 0;
	private float colorCounter = 0;
	private float modulation = 0;
	private float aggresive = 0.4f;
	private float modulationStrength = 0.4f; // 0-1
	private float angleModulation = 0;

	private int type;

	public AudioVisualizer(int type)
	{
		mBytes = null;
		mFFTBytes = null;
		mVisualizer = null;

		this.type = type;

		if (type == 0) {
			mPaint = new Paint();
			mPaint.setStrokeWidth(1f);
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.argb(200, 0, 128, 255));

			mFlashPaint = new Paint();
			mFlashPaint.setStrokeWidth(5f);
			mFlashPaint.setAntiAlias(true);
			mFlashPaint.setColor(Color.argb(188, 255, 255, 255));

			mCycleColor = true;
		} else if (type == 1) {
			mPaint = new Paint();
			// mPaint.setStrokeWidth(50f);
			// mPaint.setAntiAlias(true);
			// mPaint.setColor(Color.argb(200, 56, 138, 252));

			mPaint.setStrokeWidth(12f);
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.argb(200, 181, 111, 233));

			mDivisions = 4;
			mTop = false;
		} else if (type == 2) {
			mPaint = new Paint();
			mPaint.setStrokeWidth(3f);
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.argb(255, 222, 92, 143));

			mCycleColor = true;
		} else if (type == 3) {
			mPaint = new Paint();
			mPaint.setStrokeWidth(8f);
			mPaint.setAntiAlias(true);
			mPaint.setXfermode(new PorterDuffXfermode(Mode.LIGHTEN));
			mPaint.setColor(Color.argb(255, 222, 92, 143));

			mDivisions = 16;
			mCycleColor = true;
		} else if (type == 4) {
			mPaint = new Paint();
			mPaint.setStrokeWidth(1f);
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.rgb(0, 128, 255));
		}
	}

	/*
	 * private int[] getAmplitude(byte[] fft) {
	 * int[] amplitude = new int[fft.length/2-2];
	 * for (int i = 2; i <= fft.length/2-2;i+= 2) {
	 * for(int j=0;j<fft.length/2-2;j++){
	 * amplitude[j]=(fft[i] * fft[i] + fft[i + 1] * fft[i + 1]);
	 * }
	 * }
	 * 
	 * return amplitude;
	 * }
	 */

	public int[] getDbLevel(AudioData data, int div) {
		if (data.bytes == null)
			return null;

		int[] db = new int[data.bytes.length / div];
		for (int i = 0; i < data.bytes.length / div; i++) {
			byte rfk = data.bytes[div * i];
			byte ifk = data.bytes[div * i + 1];
			float magnitude = (rfk * rfk + ifk * ifk);
			db[i] = (int) (10 * Math.log10(magnitude));
			if (db[i] < 0)
				db[i] = 0;
			Log.e("test", "i:" + i + ",db[i]:" + db[i]);
		}

		return db;
	}

	public void open(SongPlay player)
	{
		if (player == null)
		{
			return;
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			return;
		}

		try {
			mVisualizer = new Visualizer(player.getAudioSessionID());
		} catch (Exception e) {
		}

		try {
			mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
		} catch (IllegalStateException e) {
		}

		Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener()
		{
			@Override
			public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
					int samplingRate)
			{
				mBytes = bytes;
			}

			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
					int samplingRate)
			{
				mFFTBytes = bytes;
			}
		};

		mVisualizer.setDataCaptureListener(captureListener,
				Visualizer.getMaxCaptureRate() / 2, true, true);

		enable(true);

		/*
		 * AudioEqualizer equalizer = new AudioEqualizer();
		 * equalizer.open(player);
		 * equalizer.enable(true);
		 * int band = equalizer.getNumberOfBands();
		 * int min = equalizer.getBandLevelRange()[0];
		 * int max = equalizer.getBandLevelRange()[1];
		 * 
		 * Log.d("AudioVisualizer", "getNumberOfBands:" + band);
		 * 
		 * for ( int i = 0; i < band; i++ ) {
		 * Log.d("AudioVisualizer", (equalizer.getCenterFreq((short)i) / 1000) + " Hz");
		 * Log.d("AudioVisualizer", "minEQLevel:" + (min / 100) + " dB");
		 * Log.d("AudioVisualizer", "maxEQLevel:" + (max / 100) + " dB");
		 * Log.d("AudioVisualizer", "getBandLevel:" + (equalizer.getBandLevel((short)i)));
		 * }
		 */
	}

	public void enable(boolean view)
	{
		try {
			if (mVisualizer != null)
				mVisualizer.setEnabled(view);
		} catch (IllegalStateException e) {
		}
	}

	public void release()
	{
		if (mVisualizer != null)
			mVisualizer.release();
	}

	public AudioData getData()
	{
		AudioData data = new AudioData(mBytes);
		return data;
	}

	public AudioData getFFTData()
	{
		AudioData data = new AudioData(mFFTBytes);
		return data;
	}

	public void onRender(Canvas canvas, Rect rect)
	{
		if (type == 0) {
			onLineRender(canvas, getData(), rect);
		} else if (type == 1) {
			onBarGraphRender(canvas, getFFTData(), rect);
		} else if (type == 2) {
			onCircleRender(canvas, getData(), rect);
		} else if (type == 3) {
			onCircleBarRender(canvas, getFFTData(), rect);
		} else if (type == 4) {
			onSingleLineRener(canvas, getData(), rect);
		}
	}

	public void onBarGraphRender(Canvas canvas, AudioData data, Rect rect)
	{
		if (data.bytes == null)
			return;

		// getDbLevel(data, 256);

		if (mPoints == null || mPoints.length < data.bytes.length * 4) {
			mPoints = new float[data.bytes.length * 4];
		}

		for (int i = 0; i < data.bytes.length / mDivisions; i++) {
			mPoints[i * 4] = i * 4 * mDivisions;
			mPoints[i * 4 + 2] = i * 4 * mDivisions;
			byte rfk = data.bytes[mDivisions * i];
			byte ifk = data.bytes[mDivisions * i + 1];
			float magnitude = (rfk * rfk + ifk * ifk);
			int dbValue = (int) (10 * Math.log10(magnitude));
			if (dbValue < 0)
				dbValue = 0;

			if (mTop)
			{
				mPoints[i * 4 + 1] = 0;
				mPoints[i * 4 + 3] = (dbValue * 2 - 10);
			}
			else
			{
				mPoints[i * 4 + 1] = rect.height();
				mPoints[i * 4 + 3] = rect.height() - (dbValue * 2 - 10);
			}
		}

		canvas.drawLines(mPoints, mPaint);
	}

	public void onSingleLineRener(Canvas canvas, AudioData data, Rect rect)
	{
		if (data.bytes == null)
			return;

		if (mPoints == null || mPoints.length < data.bytes.length * 4) {
			mPoints = new float[data.bytes.length * 4];
		}

		for (int i = 0; i < data.bytes.length - 1; i++) {
			mPoints[i * 4] = rect.width() * i / (mBytes.length - 1);
			mPoints[i * 4 + 1] = rect.height() / 2
					+ ((byte) (data.bytes[i] + 128)) * (rect.height() / 2) / 128;
			mPoints[i * 4 + 2] = rect.width() * (i + 1) / (data.bytes.length - 1);
			mPoints[i * 4 + 3] = rect.height() / 2
					+ ((byte) (data.bytes[i + 1] + 128)) * (rect.height() / 2) / 128;
		}

		canvas.drawLines(mPoints, mPaint);
	}

	public void onLineRender(Canvas canvas, AudioData data, Rect rect)
	{
		if (data.bytes == null)
			return;

		if (mPoints == null || mPoints.length < data.bytes.length * 4) {
			mPoints = new float[data.bytes.length * 4];
		}

		if (mCycleColor)
		{
			cycleColor();
		}

		for (int i = 0; i < data.bytes.length - 1; i++) {
			mPoints[i * 4] = rect.width() * i / (data.bytes.length - 1);
			mPoints[i * 4 + 1] = rect.height() / 2
					+ ((byte) (data.bytes[i] + 128)) * (rect.height() / 3) / 128;
			mPoints[i * 4 + 2] = rect.width() * (i + 1) / (data.bytes.length - 1);
			mPoints[i * 4 + 3] = rect.height() / 2
					+ ((byte) (data.bytes[i + 1] + 128)) * (rect.height() / 3) / 128;
		}

		float accumulator = 0;
		for (int i = 0; i < data.bytes.length - 1; i++) {
			accumulator += Math.abs(data.bytes[i]);
		}

		float amp = accumulator / (128 * data.bytes.length);
		if (amp > amplitude)
		{
			amplitude = amp;
			canvas.drawLines(mPoints, mFlashPaint);
		}
		else
		{
			amplitude *= 0.99;
			canvas.drawLines(mPoints, mPaint);
		}
	}

	public void onCircleRender(Canvas canvas, AudioData data, Rect rect)
	{
		if (data.bytes == null)
			return;

		if (mPoints == null || mPoints.length < data.bytes.length * 4) {
			mPoints = new float[data.bytes.length * 4];
		}

		if (mCycleColor)
		{
			cycleColor();
		}

		for (int i = 0; i < data.bytes.length - 1; i++) {
			float[] cartPoint = {
					(float) i / (data.bytes.length - 1),
					rect.height() / 2 + ((byte) (data.bytes[i] + 128)) * (rect.height() / 2) / 128
			};

			float[] polarPoint = toPolar(cartPoint, rect);
			mPoints[i * 4] = polarPoint[0];
			mPoints[i * 4 + 1] = polarPoint[1];

			float[] cartPoint2 = {
					(float) (i + 1) / (data.bytes.length - 1),
					rect.height() / 2 + ((byte) (data.bytes[i + 1] + 128)) * (rect.height() / 2) / 128
			};

			float[] polarPoint2 = toPolar(cartPoint2, rect);
			mPoints[i * 4 + 2] = polarPoint2[0];
			mPoints[i * 4 + 3] = polarPoint2[1];
		}

		canvas.drawLines(mPoints, mPaint);

		modulation += 0.04;
	}

	public void onCircleBarRender(Canvas canvas, AudioData data, Rect rect)
	{
		if (data.bytes == null)
			return;

		if (mPoints == null || mPoints.length < data.bytes.length * 4) {
			mPoints = new float[data.bytes.length * 4];
		}

		if (mCycleColor)
		{
			cycleColor();
		}

		for (int i = 0; i < data.bytes.length / mDivisions; i++) {
			byte rfk = data.bytes[mDivisions * i];
			byte ifk = data.bytes[mDivisions * i + 1];
			float magnitude = (rfk * rfk + ifk * ifk);
			float dbValue = 75 * (float) Math.log10(magnitude);

			float[] cartPoint = {
					(float) (i * mDivisions) / (data.bytes.length - 1),
					rect.height() / 2 - dbValue / 4
			};

			float[] polarPoint = toPolar2(cartPoint, rect);
			mPoints[i * 4] = polarPoint[0];
			mPoints[i * 4 + 1] = polarPoint[1];

			float[] cartPoint2 = {
					(float) (i * mDivisions) / (data.bytes.length - 1),
					rect.height() / 2 + dbValue
			};

			float[] polarPoint2 = toPolar2(cartPoint2, rect);
			mPoints[i * 4 + 2] = polarPoint2[0];
			mPoints[i * 4 + 3] = polarPoint2[1];
		}

		canvas.drawLines(mPoints, mPaint);

		modulation += 0.13;
		angleModulation += 0.28;
	}

	private float[] toPolar2(float[] cartesian, Rect rect)
	{
		double cX = rect.width() / 2;
		double cY = rect.height() / 2;
		double angle = (cartesian[0]) * 2 * Math.PI;
		double radius = ((rect.width() / 2) * (1 - aggresive) + aggresive * cartesian[1] / 2) * ((1 - modulationStrength) + modulationStrength * (1 + Math.sin(modulation)) / 2);
		float[] out = {
				(float) (cX + radius * Math.sin(angle + angleModulation)),
				(float) (cY + radius * Math.cos(angle + angleModulation))
		};
		return out;
	}

	private float[] toPolar(float[] cartesian, Rect rect)
	{
		double cX = rect.width() / 2;
		double cY = rect.height() / 2;
		double angle = (cartesian[0]) * 2 * Math.PI;
		double radius = ((rect.width() / 2) * (1 - aggresive) + aggresive * cartesian[1] / 2) * (1.2 + Math.sin(modulation)) / 2.2;
		float[] out = {
				(float) (cX + radius * Math.sin(angle)),
				(float) (cY + radius * Math.cos(angle))
		};

		return out;
	}

	private void cycleColor()
	{
		int r = (int) Math.floor(128 * (Math.sin(colorCounter) + 3));
		int g = (int) Math.floor(128 * (Math.sin(colorCounter + 1) + 1));
		int b = (int) Math.floor(128 * (Math.sin(colorCounter + 7) + 1));
		mPaint.setColor(Color.argb(128, r, g, b));
		colorCounter += 0.03;
	}
}
