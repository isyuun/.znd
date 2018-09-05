package kr.kymedia.karaoke.play3;

import android.media.audiofx.Equalizer;
import android.os.Build;

public class AudioEqualizer
{
	private Equalizer mEqualizer;

	public AudioEqualizer()
	{
		mEqualizer = null;
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
			mEqualizer = new Equalizer(0, player.getAudioSessionID());
		} catch (Exception e) {
		}
	}

	public void enable(boolean view)
	{
		try {
			if (mEqualizer != null)
				mEqualizer.setEnabled(view);
		} catch (IllegalStateException e) {
		}
	}

	public void release()
	{
		if (mEqualizer != null)
			mEqualizer.release();
	}

	public int getNumberOfBands() {
		try {
			if (mEqualizer != null)
				return mEqualizer.getNumberOfBands();
		} catch (Exception e) {
		}

		return -1;
	}

	public short[] getBandLevelRange() {
		try {
			if (mEqualizer != null)
				return mEqualizer.getBandLevelRange();
		} catch (Exception e) {
		}

		return null;
	}

	public int getCenterFreq(short number) {
		try {
			if (mEqualizer != null)
				return mEqualizer.getCenterFreq(number);
		} catch (Exception e) {
		}

		return -1;
	}

	public short getBandLevel(short number) {
		try {
			if (mEqualizer != null)
				return mEqualizer.getBandLevel(number);
		} catch (Exception e) {
		}

		return -1;
	}
}
