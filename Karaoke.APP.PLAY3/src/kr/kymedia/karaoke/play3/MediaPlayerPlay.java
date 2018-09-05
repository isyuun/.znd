package kr.kymedia.karaoke.play3;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

public class MediaPlayerPlay implements SongPlay, SongPlayListener
{
	private SongPlayListener mOnListener;
	private MediaPlayer mMediaPlayer;
	private long mRemainTime;
	private boolean mType;
	private boolean mPause;
	private int mPTime;
	private boolean mSeek;
	private boolean mPlaying;
	private int mDuration;
	private int mCPosition;
	private boolean mDestroy;
	private int mPercent;

	public MediaPlayerPlay()
	{
		mType = false;
		mPause = false;
		mSeek = false;
		mOnListener = null;
		mPTime = 0;
		mRemainTime = 0;
		mDuration = 0;
		mCPosition = 0;
		mDestroy = false;
		mPlaying = false;
		mPercent = 0;
		mMediaPlayer = new MediaPlayer();
	}

	@Override
	public void setOnListener(SongPlayListener listener)
	{
		// Log.d("MediaPlayerPlay", "setOnListener");
		mOnListener = listener;
	}

	@Override
	public void destroy()
	{
		if (mDestroy == false)
		{
			mDestroy = true;
			close();

			// Log.d("MediaPlayerPlay", "destroy");
			mMediaPlayer.release();

			if (mOnListener != null)
				mOnListener.onRelease();
		}
		// Log.d("MediaPlayerPlay", "destroy end");
	}

	@Override
	public boolean mid_open(String path, String cfg, String patch)
	{
		return open(path);
	}

	@Override
	public boolean open(String path)
	{
		// Log.d("MediaPlayerPlay", "open");
		Log.d("player", "open()");
		if (mDestroy == true)
			return false;

		mMediaPlayer.reset();
		// try {
		// mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		// } catch (IllegalStateException e) {
		// return false;
		// }
		try {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepareAsync();
		} catch (Exception e) {
			// Log.d("MediaPlayerPlay", "open error");
			// e.printStackTrace();
			Log.e("player", "open() - ERROR!!!\n" + Log.getStackTraceString(e));
			return false;
		}

		MediaPlayer.OnCompletionListener onMediaPlayerComplete = new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer arg) {
				if (mOnListener != null)
					mOnListener.onCompletion();
				// Log.d("MediaPlayerPlay", "onCompletion");
			}
		};
		mMediaPlayer.setOnCompletionListener(onMediaPlayerComplete);

		MediaPlayer.OnErrorListener onMediaPlayerError = new MediaPlayer.OnErrorListener() {
			public boolean onError(MediaPlayer mp, int what, int extra) {
				if (mOnListener != null)
					mOnListener.onError();
				// Log.d("MediaPlayerPlay", "onError");
				return true;
			}
		};
		mMediaPlayer.setOnErrorListener(onMediaPlayerError);

		MediaPlayer.OnPreparedListener onMediaPlayerPrepared = new MediaPlayer.OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				if (mOnListener != null)
					mOnListener.onPrepared();
				// Log.d("MediaPlayerPlay", "onPrepared");
			}
		};
		mMediaPlayer.setOnPreparedListener(onMediaPlayerPrepared);

		MediaPlayer.OnBufferingUpdateListener onMediaPlayerBufferingUpdate = new MediaPlayer.OnBufferingUpdateListener() {
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				if (mPercent != percent) {
					if (mOnListener != null)
						mOnListener.onBufferingUpdate(percent);

					mPercent = percent;
				}
				// Log.d("MediaPlayerPlay", "onBufferingUpdate");
			}
		};
		mMediaPlayer.setOnBufferingUpdateListener(onMediaPlayerBufferingUpdate);

		MediaPlayer.OnSeekCompleteListener onMediaPlayerSeekComplete = new MediaPlayer.OnSeekCompleteListener() {
			public void onSeekComplete(MediaPlayer mp) {
				mSeek = false;
				if (mOnListener != null)
					mOnListener.onSeekComplete();
				// Log.d("MediaPlayerPlay", "onSeekComplete");
			}
		};
		mMediaPlayer.setOnSeekCompleteListener(onMediaPlayerSeekComplete);

		return true;
	}

	@Override
	public boolean play()
	{
		// Log.d("MediaPlayerPlay", "play");
		if (mDestroy == true)
			return false;

		mPTime = 0;
		mRemainTime = 0;
		mCPosition = 0;
		mPause = false;
		mSeek = false;
		mPlaying = true;

		try {
			mMediaPlayer.start();
		} catch (IllegalStateException e) {
			return false;
		}
		// Log.d("MediaPlayerPlay", "play end");

		return true;
	}

	@Override
	public boolean play(int time)
	{
		// Log.d("MediaPlayerPlay", "play");
		if (mDestroy == true)
			return false;

		setPrevTime(time);

		mPTime = 0;
		mRemainTime = 0;
		mCPosition = 0;
		mPause = false;
		mSeek = false;
		mPlaying = true;

		try {
			mMediaPlayer.start();
		} catch (IllegalStateException e) {
			return false;
		}
		// Log.d("MediaPlayerPlay", "play end");

		return true;
	}

	@Override
	public void stop() {

		try {
			mMediaPlayer.stop();
		} catch (IllegalStateException e) {
		}
	}

	@Override
	public void close()
	{
		// Log.d("MediaPlayerPlay", "close");
		if (mDestroy == true)
			return;

		/*
		 * if ( mPlaying == true ) {
		 * mPlaying = false;
		 * if ( mOnListener != null )
		 * mOnListener.onError();
		 * }
		 */

		try {
			mMediaPlayer.stop();
		} catch (IllegalStateException e) {
			return;
		}

		mMediaPlayer.reset();
		mPause = false;
		mSeek = false;
		mPTime = 0;
		mRemainTime = 0;
		mCPosition = 0;
		mPlaying = false;
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
		if (mDestroy == true)
			return;

		mPause = true;

		try {
			mMediaPlayer.pause();
		} catch (IllegalStateException e) {
		}
	}

	@Override
	public void setResume()
	{
		if (mDestroy == true)
			return;

		mRemainTime = 0;
		mPause = false;

		try {
			mMediaPlayer.start();
		} catch (IllegalStateException e) {
		}
	}

	@Override
	public void setTempo(float tempo)
	{
	}

	@Override
	public void setPitch(int value)
	{
	}

	@Override
	public float getTempo()
	{
		return 0;
	}

	@Override
	public int getPitch()
	{
		return 0;
	}

	@Override
	public void seek(int sec)
	{
		if (mDestroy == true)
			return;

		mRemainTime = 0;
		try {
			mSeek = true;
			mMediaPlayer.seekTo(sec * 1000);
		} catch (IllegalStateException e) {
		}
	}

	@Override
	public int getCurrentTime()
	{
		if (mDestroy == true)
			return 0;

		if (mPlaying == false)
			return 0;

		if (mPause) {
			int t = 0;
			try {
				t = mMediaPlayer.getCurrentPosition();
			} catch (IllegalStateException e) {
			}
			if (mOnListener != null) {
				int time = t / 1000;
				if (mPTime != time)
					mOnListener.onTime(time);
				mPTime = time;
			}
			return t;
		}

		if (isPlaying()) {
			if (mType == false) {
				int t = 0;
				try {
					t = mMediaPlayer.getCurrentPosition();
				} catch (IllegalStateException e) {
				}

				if (mOnListener != null) {
					int time = t / 1000;
					if (mPTime != time)
						mOnListener.onTime(time);
					mPTime = time;
				}

				if (mDuration > 0 && t > mDuration) {
					if (mOnListener != null)
						mOnListener.onCompletion();
				}

				return t;
			} else {
				int cposition = 0;
				try {
					cposition = mMediaPlayer.getCurrentPosition();
				} catch (IllegalStateException e) {
				}

				long currtime = System.currentTimeMillis();
				if (mRemainTime == 0 && cposition > 0) {
					mRemainTime = currtime - cposition;
				}

				int t = 0;
				if (mRemainTime > 0)
					t = (int) (currtime - mRemainTime);

				if ((t > cposition + 300 || t < cposition - 300) && mCPosition != cposition)
					mRemainTime = currtime - cposition;

				if (mOnListener != null) {
					int time = t / 1000;
					if (mPTime != time)
						mOnListener.onTime(time);
					mPTime = time;
				}

				if (mDuration > 0 && t > mDuration) {
					if (mOnListener != null)
						mOnListener.onCompletion();
				}

				mCPosition = cposition;

				return t;
			}
		} else {
			return 0;
		}
	}

	@Override
	public int getTotalTime()
	{
		if (mDestroy == true)
			return 0;

		if (mPlaying == false)
			return 0;

		if (mDuration > 0) {
			int t = 0;
			if (isPlaying()) {
				t = mMediaPlayer.getDuration();
			}

			if (t > mDuration) {
				return mDuration;
			}

			return t;
		} else {
			if (isPlaying())
				return mMediaPlayer.getDuration();
		}

		return 0;
	}

	@Override
	public boolean isPlaying()
	{
		if (mDestroy == true)
			return false;

		boolean playing = mPlaying;
		/*
		 * boolean playing = false;
		 * try {
		 * playing = mMediaPlayer.isPlaying();
		 * } catch (IllegalStateException e) {
		 * return false;
		 * }
		 */

		return playing;
	}

	@Override
	public boolean isPause()
	{
		return mPause;
	}

	@Override
	public boolean isSeek()
	{
		return mSeek;
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
		if (mDestroy == true)
			return mMediaPlayer.getAudioSessionId();

		return 0;
	}

	@Override
	public void event(OnCompletionListener c, OnErrorListener e, OnPreparedListener p)
	{
		mMediaPlayer.setOnCompletionListener(c);
		mMediaPlayer.setOnErrorListener(e);
		mMediaPlayer.setOnPreparedListener(p);
	}

}
