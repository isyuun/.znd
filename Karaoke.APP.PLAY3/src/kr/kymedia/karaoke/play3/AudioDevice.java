package kr.kymedia.karaoke.play3;

import android.media.AudioTrack;

public interface AudioDevice extends Disposable {
	/** @return whether this AudioDevice is in mono or stereo mode. */
	public boolean isMono();

	/**
	 * Writes the array of 16-bit signed PCM samples to the audio device and blocks until they have been processed.
	 * 
	 * @param samples
	 *          The samples.
	 * @param offset
	 *          The offset into the samples array
	 * @param numSamples
	 *          the number of samples to write to the device
	 */
	public void writeSamples(short[] samples, int offset, int numSamples);

	/**
	 * Writes the array of float PCM samples to the audio device and blocks until they have been processed.
	 * 
	 * @param samples
	 *          The samples.
	 * @param offset
	 *          The offset into the samples array
	 * @param numSamples
	 *          the number of samples to write to the device
	 */
	public void writeSamples(float[] samples, int offset, int numSamples);

	/**
	 * Writes the array of float PCM samples to the audio device and blocks until they have been processed.
	 * 
	 * @param samples
	 *          The samples.
	 */
	public void writeSamples(float[] samples);

	/**
	 * Writes the array of float PCM samples to the audio device and blocks until they have been processed.
	 * 
	 * @param samples
	 *          The samples.
	 */
	public void writeSamples(byte[] samples);

	/**
	 * Writes the array of float PCM samples to the audio device and blocks until they have been processed.
	 * 
	 * @param samples
	 *          The samples.
	 * @param offset
	 *          The offset into the samples array
	 * @param numSamples
	 *          the number of samples to write to the device
	 */
	public int writeSamples(byte[] samples, int offset, int numSamples);

	/** @return the latency in samples. */
	public int getLatency();

	/** Frees all resources associated with this AudioDevice. Needs to be called when the device is no longer needed. */
	public void dispose();

	/** Sets the volume in the range [0,1]. */
	public void setVolume(float volume);

	public void stop();

	public void close();

	public AudioTrack getTrack();
}
