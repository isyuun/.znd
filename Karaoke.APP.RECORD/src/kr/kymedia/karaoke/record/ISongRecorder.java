package kr.kymedia.karaoke.record;

import java.nio.ByteBuffer;

import android.media.MediaRecorder;

public interface ISongRecorder {
	boolean isRecording();

	boolean start() throws Exception;

	boolean stop() throws Exception;

	void pause();

	void resume();

	int getAudioFormat();

	int getAudioSource();

	int getChannelConfiguration();

	int getChannelCount();

	int getNotificationMarkerPosition();

	int getPositionNotificationPeriod();

	int getRecordingState();

	int getSampleRate();

	int getSampleRateBit();

	int getState();

	int read(byte[] audioData, int offsetInBytes, int sizeInBytes);

	int read(ByteBuffer audioBuffer, int sizeInBytes);

	int read(short[] audioData, int offsetInShorts, int sizeInShorts);

	void release();

	int setNotificationMarkerPosition(int markerInFrames);

	int setPositionNotificationPeriod(int periodInFrames);

	void setOnErrorListener(MediaRecorder.OnErrorListener l);

	void setOnInfoListener(MediaRecorder.OnInfoListener listener);

	void setSongRecorderListener(SongRecorderListener listener);
}
