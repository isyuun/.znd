package kr.kymedia.karaoke.play3;

import android.media.audiofx.BassBoost;
import android.media.audiofx.EnvironmentalReverb;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.os.Build;

public class AudioEffect
{
	private BassBoost mBassBoost;
	private PresetReverb mPresetReverb;
	private EnvironmentalReverb mEnvironmentalReverb;
	private Virtualizer mVirtualizer;

	public AudioEffect()
	{
		mBassBoost = null;
		mPresetReverb = null;
		mEnvironmentalReverb = null;
		mVirtualizer = null;
	}

	public void open(SongPlay player, int type)
	{
		if (player == null)
		{
			return;
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			return;
		}

		if (type == 0) {
			BassBoost(player);
		} else if (type == 1) {
			PresetReverb(player);
		} else if (type == 2) {
			EnvironmentalReverb(player);
		} else if (type == 3) {
			Virtualizer(player);
		}
	}

	private void BassBoost(SongPlay player)
	{
		try {
			mBassBoost = new BassBoost(0, player.getAudioSessionID());
		} catch (Exception e) {
		}
	}

	private void PresetReverb(SongPlay player)
	{
		try {
			mPresetReverb = new PresetReverb(0, player.getAudioSessionID());
		} catch (Exception e) {
		}
	}

	private void EnvironmentalReverb(SongPlay player)
	{
		try {
			mEnvironmentalReverb = new EnvironmentalReverb(0, player.getAudioSessionID());
		} catch (Exception e) {
		}
	}

	private void Virtualizer(SongPlay player)
	{
		try {
			mVirtualizer = new Virtualizer(0, player.getAudioSessionID());
		} catch (Exception e) {
		}
	}

	public void enable(boolean view, int type)
	{
		try {
			if (type == 0) {
				if (mBassBoost != null)
					mBassBoost.setEnabled(view);
			} else if (type == 1) {
				if (mPresetReverb != null)
					mPresetReverb.setEnabled(view);
			} else if (type == 2) {
				if (mEnvironmentalReverb != null)
					mEnvironmentalReverb.setEnabled(view);
			} else if (type == 3) {
				if (mVirtualizer != null)
					mVirtualizer.setEnabled(view);
			}
		} catch (IllegalStateException e) {
		}
	}

	public void release(int type)
	{
		if (type == 0) {
			if (mBassBoost != null)
				mBassBoost.release();
		} else if (type == 1) {
			if (mPresetReverb != null)
				mPresetReverb.release();
		} else if (type == 2) {
			if (mEnvironmentalReverb != null)
				mEnvironmentalReverb.release();
		} else if (type == 3) {
			if (mVirtualizer != null)
				mVirtualizer.release();
		}
	}

	public boolean getBassBoostStrengthSupported()
	{
		return mBassBoost.getStrengthSupported();
	}

	public short getBassBoostRoundedStrength()
	{
		try {
			return mBassBoost.getRoundedStrength();
		} catch (Exception e) {
		}

		return -1;
	}

	public void setBassBoostStrength(short value)
	{
		try {
			mBassBoost.setStrength(value); // off:0, on(1000)
		} catch (Exception e) {
		}
	}

	public boolean getVirtualizerStrengthSupported()
	{
		return mVirtualizer.getStrengthSupported();
	}

	public short getVirtualizerRoundedStrength()
	{
		try {
			return mVirtualizer.getRoundedStrength();
		} catch (Exception e) {
		}

		return -1;
	}

	public void setVirtualizerStrength(short value)
	{
		try {
			mVirtualizer.setStrength(value); // off:0, on(1000)
		} catch (Exception e) {
		}
	}

	public void setEnvironmentalReverb()
	{
		try {
			mEnvironmentalReverb.setReverbLevel((short) -1000);
			mEnvironmentalReverb.setReverbDelay(50);
			mEnvironmentalReverb.setRoomLevel((short) -1000);
		} catch (Exception e) {
		}
	}

	public void setPresetReverb(short type)
	{
		try {
			mPresetReverb.setPreset(type);
		} catch (Exception e) {
		}
	}
}
