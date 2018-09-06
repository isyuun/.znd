package kr.kymedia.karaoke.play3;

import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

@Deprecated
public class AudioTrackPlay extends Thread implements SongPlay, SongPlayListener
{
	private final static int AVCODEC_MAX_AUDIO_FRAME_SIZE = 192 * 1000; // 512 * 1000;
	private final byte[] mAudioFrameBuffer = new byte[AVCODEC_MAX_AUDIO_FRAME_SIZE];
	private final int[] mAudioFrameBufferDataLength = new int[1];
	private AudioDevice mDevice;
	private boolean mDestroy;
	private boolean mRun;
	private boolean mAudioOpened;
	private boolean mType;
	private boolean mPaused;
	private SongPlayListener mOnListener;
	private int mPrevTime;
	private float mTempo;
	private int mPitch;
	private int mDuration;

	static {
		try {
			System.loadLibrary("karaoke");
		} catch (UnsatisfiedLinkError e) {
			Log.e("AudioTrackPlay", "UnsatisfiedLinkError");
		}
	}

	public AudioTrackPlay()
	{
		mPaused = true;
		mOnListener = null;
		mDevice = null;
		mDestroy = false;
		mPrevTime = 0;
		mTempo = 0;
		mPitch = 0;
		mDuration = 0;
		mDevice = new AndroidAudioDevice();
		try {
			this.start();
		} catch (IllegalThreadStateException e) {
		}
	}

	@Override
	public void run()
	{
		while (!mDestroy) {
			while (mRun) {
				if (mPaused == false) {
					if (DecodeFrameFromFile() == -1) {
						if (mOnListener != null)
							mOnListener.onCompletion();
						break;
					}

					int len = mAudioFrameBufferDataLength[0];
					if (mAudioFrameBuffer != null)
						mDevice.writeSamples(mAudioFrameBuffer, 0, len);
				} else {
					try {
						sleep(200);
					} catch (java.lang.InterruptedException e) {
					}
					;
				}
			}

			try {
				sleep(200);
			} catch (java.lang.InterruptedException e) {
			}
			;
		}
	}

	@Override
	public void setOnListener(SongPlayListener listener)
	{
		mOnListener = listener;
	}

	@Override
	public void destroy()
	{
		mRun = false;
		mDestroy = true;

		mDevice.stop();
		mDevice.close();

		close();

		try {
			this.join();
		} catch (InterruptedException e) {
		}
		;

		if (mOnListener != null)
			mOnListener.onRelease();
	}

	@Override
	public boolean mid_open(String path, String cfg, String patch)
	{
		int ret = -1;

		try {
			ret = OpenFromMidFile(path, cfg, patch);
		} catch (UnsatisfiedLinkError e) {
			return false;
		}

		if (ret != 0) {
			try {
				Close();
			} catch (UnsatisfiedLinkError e) {
			}

			return false;
		}

		if (mOnListener != null)
			mOnListener.onPrepared();

		return true;
	}

	@Override
	public boolean load(String path)
	{
		int ret = -1;
		try {
			ret = OpenFromFile(path);
		} catch (UnsatisfiedLinkError e) {
			return false;
		}

		if (ret != 0) {
			try {
				Close();
			} catch (UnsatisfiedLinkError e) {
			}

			return false;
		}

		if (mOnListener != null)
			mOnListener.onPrepared();

		return true;
	}

	@Override
	public boolean play()
	{
		try {
			mAudioOpened = OpenAudio(mAudioFrameBuffer, mAudioFrameBufferDataLength) == 0;
		} catch (UnsatisfiedLinkError e) {
			return false;
		}

		if (!mAudioOpened) {
			try {
				CloseAudio();
			} catch (UnsatisfiedLinkError e) {
			}
			return false;
		}

		mPrevTime = 0;
		mPaused = false;
		mRun = true;
		mTempo = 0;
		mPitch = 0;

		return true;
	}

	@Override
	public boolean play(int time)
	{
		setPrevTime(time);

		try {
			mAudioOpened = OpenAudio(mAudioFrameBuffer, mAudioFrameBufferDataLength) == 0;
		} catch (UnsatisfiedLinkError e) {
			return false;
		}

		if (!mAudioOpened) {
			try {
				CloseAudio();
			} catch (UnsatisfiedLinkError e) {
			}
			return false;
		}

		mPrevTime = 0;
		mPaused = false;
		mRun = true;
		mTempo = 0;
		mPitch = 0;

		return true;
	}

	@Override
	public void close()
	{
		mPrevTime = 0;
		mRun = false;
		mAudioOpened = false;
		mTempo = 0;
		mPitch = 0;

		try {
			Close();
		} catch (UnsatisfiedLinkError e) {
		}
	}

	@Override
	public void setPrevTime(int time)
	{
		if (time > 0)
			mDuration = time;
		else
			mDuration = 0;
	}

	@Override
	public void setPause()
	{
		mPaused = true;
	}

	@Override
	public void setResume()
	{
		mPaused = false;
	}

	@Override
	public void setTempo(float value)
	{
		mTempo = value;

		try {
			SetTempo(value);
		} catch (UnsatisfiedLinkError e) {
		}
	}

	@Override
	public void setPitch(int value)
	{
		mPitch = value;

		try {
			SetPitch(value);
		} catch (UnsatisfiedLinkError e) {
		}
	}

	@Override
	public float getTempo()
	{
		return mTempo;
	}

	@Override
	public int getPitch()
	{
		return mPitch;
	}

	@Override
	public void seek(int sec)
	{
		setPause();
		SetCurrent(sec);
		setResume();
	}

	@Override
	public int getTotalTime()
	{
		int t = GetDuration();

		if (mDuration > 0) {
			if (t > mDuration)
				return mDuration * 1000;
		}

		return t;
	}

	@Override
	public int getCurrentTime()
	{
		if (mAudioOpened) {
			int t = GetCurrent();
			if (mOnListener != null) {
				int time = t / 1000;
				if (mPrevTime != time && time > 0)
					mOnListener.onTime(time);
				mPrevTime = time;
			}

			if (mDuration > 0 && t > mDuration * 1000) {
				if (mOnListener != null)
					mOnListener.onCompletion();
			}

			return t;
		}

		return 0;
	}

	@Override
	public boolean isPlaying()
	{
		return mAudioOpened;
	}

	@Override
	public boolean isPause()
	{
		return mPaused;
	}

	@Override
	public boolean isSeek()
	{
		return false;
	}

	@Override
	public boolean getType()
	{
		return mType;
	}

	@Override
	public void setType(boolean b)
	{
		mType = b;
	}

	@Override
	public void onTime(int t)
	{
	}

	@Override
	public void onPrepared()
	{
	}

	@Override
	public void onCompletion()
	{
	}

	@Override
	public void onError()
	{
	}

	@Override
	public void onBufferingUpdate(int percent)
	{
	}

	@Override
	public void onSeekComplete()
	{
	}

	@Override
	public void onRelease()
	{
	}

	@Override
	public void onReady(int count)
	{
	}

	@Override
	public int getAudioSessionID()
	{
		return mDevice.getTrack().getAudioSessionId();
	}

	@Override
	public void event(OnCompletionListener c, OnErrorListener e, OnPreparedListener p)
	{
	}

	private native int OpenFromFile(String aMediaFile);

	private native int OpenFromMidFile(String aMediaFile, String aCfgFile, String aPatchDir);

	private native void Close();

	private native int OpenAudio(byte[] aAudioFrameBufferRef, int[] aAudioFrameBufferCountRef);

	private native void CloseAudio();

	private native int DecodeFrameFromFile();

	private native void SetTempo(float value);

	private native void SetPitch(float value);

	private native int GetDuration();

	private native int GetCurrent();

	private native void SetCurrent(int value);
}
