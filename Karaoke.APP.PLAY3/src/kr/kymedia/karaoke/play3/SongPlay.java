package kr.kymedia.karaoke.play3;

import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;

public interface SongPlay {
	public void setOnListener(SongPlayListener listener);

	public void destroy();

	public boolean open(String path);

	public boolean mid_open(String path, String cfg, String patch);

	public boolean play();

	public boolean play(int time);

	public void stop();

	public void close();

	public void setPause();

	public void setResume();

	public void setTempo(float tempo);

	public float getTempo();

	public void setPitch(int pitch);

	public int getPitch();

	public void seek(int msec);

	public int getTotalTime();

	public int getCurrentTime();

	public boolean isPlaying();

	public boolean isPause();

	public boolean isSeek();

	public boolean getType();

	public void setType(boolean b);

	public int getAudioSessionID();

	public void setPrevTime(int time);

	public void event(OnCompletionListener c, OnErrorListener e, OnPreparedListener p);
}
